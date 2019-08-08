package org.cloudbus.foggatewaylib;

import android.util.Log;

import androidx.annotation.Nullable;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * Provides output data with given input data (if any).
 * A provider may run asynchronously from the main data flow (e.g. {@link TimerProvider}) or may
 * be called from an event from another component, like a {@link Trigger}, or from the UI.
 * <p>
 * Please note that, unless differently specified, the provider will run on the main thread, thus
 * the use of an {@link AsyncProvider} is highly encouraged.
 * <p>
 * Inside the {@link ExecutionManager}, each provider is associated with an <code>outputKey</code>,
 * which identifies in which {@link Store} to store the output from this provider. In case of
 * multiple providers for the same <code>outputKey</code>, a {@link Chooser} must be defined
 * within the {@link ExecutionManager} to choose between them on a per-request basis.
 * Furthermore, every Provider is linked to the same progress {@link Store}, which is used to
 * publish the current progress of the requests, such as errors, intermediate steps, final
 * completion.
 * <p>
 * NB: A single instance of a Provider may only output data on the same {@link Store}.
 *
 * @param <T> the type of input data.
 * @param <S> the type of output data.
 * @see ExecutionManager
 * @see Chooser
 * @see Trigger
 *
 * @author Riccardo Mancini
 */
public abstract class Provider<T extends Data, S extends Data>{
    private static final String TAG = "Provider";

    /**
     * Type of the input data.
     */
    private Class<T> inputType;

    /**
     * Type of the output data.
     */
    private Class<S> outputType;

    /**
     * The {@link ExecutionManager} this provider is associated with.
     */
    private ExecutionManager executionManager;

    /**
     * The {@link Store} where output data is to be stored.
     */
    private Store<S> outStore;

    /**
     * The {@link Store} where progress data is to be stored.
     */
    private Store<ProgressData> progressStore;

    /**
     * @param inputType type of the input data.
     * @param outputType type of the output data.
     */
    public Provider(Class<T> inputType, Class<S> outputType){
        this.inputType = inputType;
        this.outputType = outputType;
    }

    /**
     * @return type of the input data.
     */
    public Class<T> getInputType(){return inputType;}

    /**
     * @return type of the output data.
     */
    public Class<S> getOutputType(){return outputType;}

    /**
     * Attaches this provider to the given {@link ExecutionManager} and @{link Store}s.
     *
     * @param executionManager the {@link ExecutionManager} this provider will be bound to.
     * @param outStore the {@link Store} to be used for storing the output of this provider.
     * @param progressStore the {@link Store} to be used for storing the progress of this provider.
     * @see #onAttach()
     * @see #detach()
     */
    void attach(ExecutionManager executionManager, Store<S> outStore,
                       Store<ProgressData> progressStore){
        this.executionManager = executionManager;
        if (executionManager == null){
            Log.e(TAG, "executionManager is null");
            return;
        }
        this.outStore = outStore;
        this.progressStore = progressStore;
        onAttach();
    }

    /**
     * @return the {@link ExecutionManager} this provider is bound to, or null.
     */
    @Nullable
    protected ExecutionManager getExecutionManager(){
        if (this.executionManager == null)
            Log.e(TAG, "ExecutionManager is not bound");
        return this.executionManager;
    }

    /**
     * Detaches this provider from the {@link ExecutionManager}.
     */
    void detach(){
        onDetach();
        this.executionManager = null;
    }

    /**
     * Notifies about current progress in the execution of the request. The progress update must
     * refer to the whole request, of which this provider could be just an intermediate step.
     *
     * @param requestID the request id this progress refers to.
     * @param progress refer to {@link ProgressData#progress}
     * @param message refer to {@link ProgressData#message}
     * @see #publishProgresses(long, ProgressData...)
     * @see ProgressData
     */
    protected void publishProgress(long requestID, int progress, String message){
        progressStore.store(new ProgressData(requestID, progress, message));
    }

    /**
     * Notifies about current progress in the execution of the request. The progress update must
     * refer to the whole request, of which this provider could be just an intermediate step.
     *
     * @param requestID the request id this progress refers to.
     * @param progressData the progress data to be stored.
     * @see #publishProgress(long, int, String)
     * @see ProgressData
     */
    protected void publishProgresses(long requestID, ProgressData... progressData){
        for (ProgressData d:progressData)
            d.setRequestID(requestID);
        progressStore.store(progressData);
    }

    /**
     * Publishes the result of the execution in the {@link #outStore}.
     *
     * @param requestID the request id the data refers to.
     * @param data the data to be stored.
     * @see #publishResults(Data[])
     */
    protected void publishResults(long requestID, S... data){
        for (S d:data)
            d.setRequestID(requestID);
        outStore.store(data);
    }

    /**
     * Publishes the result of the execution in the {@link #outStore}.
     *
     * @param data the data to be stored.
     * @see #publishResults(Data[])
     */
    protected void publishResults(S... data){
        outStore.store(data);
    }

    /**
     * Performs actions after the {@link ExecutionManager} is bound.
     * It does nothing unless overwritten.
     *
     * @see #attach(ExecutionManager, Store, Store)
     */
    public void onAttach(){}

    /**
     * Performs actions before the {@link ExecutionManager} is unbound.
     * It does nothing unless overwritten.
     *
     * @see #detach()
     */
    public void onDetach(){}

    /**
     * Provides output for the given input.
     * This function must call {@link #publishResults(Data[])} or
     * {@link #publishResults(long, Data[])} before returning.
     *
     * @param requestID the request id the given data refers to.
     * @param input the input data.
     * @see #publishResults(Data[])
     * @see #publishResults(long, Data[])
     * @see #executeCast(long, Data...)
     */
    public abstract void execute(long requestID, T... input);

    /**
     * Provides output for the given {@link Data} input.
     * Input {@link Data} will be cast to <code>T</code> at runtime and passed to
     * {@link #execute(long, Data[])}
     *
     * @param requestID the request id the given data refers to.
     * @param input the input data.
     * @see #execute(long, Data[])
     */
    @SuppressWarnings("unchecked")
    public void executeCast(long requestID, Data... input){
        execute(requestID, Arrays.asList(input).toArray(
                (T[]) Array.newInstance(getInputType(), input.length)));
    }
}
