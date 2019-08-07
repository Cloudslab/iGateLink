package org.cloudbus.foggatewaylib;

import android.util.Log;

import java.lang.reflect.Array;
import java.util.Arrays;

public abstract class Provider<T extends Data, S extends Data>{
    private static final String TAG = "Provider";

    private Class<T> inputType;
    private Class<S> outputType;
    private ExecutionManager executionManager;

    protected Store<S> outStore;
    protected Store<ProgressData> progressStore;

    public Provider(Class<T> inputType, Class<S> outputType){
        this.inputType = inputType;
        this.outputType = outputType;
    }

    public Class<T> getInputType(){return inputType;}
    public Class<S> getOutputType(){return outputType;}

    public void attach(ExecutionManager executionManager, Store<S> outStore,
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

    protected ExecutionManager getExecutionManager(){
        if (this.executionManager == null)
            Log.e(TAG, "ExecutionManager is not bound");
        return this.executionManager;
    }

    public void detach(){
        onDetach();
        this.executionManager = null;
    }

    protected void publishProgress(long requestID, int progress, String message){
        progressStore.store(new ProgressData(requestID, progress, message));
    }

    protected void publishResult(long requestID, S... data){
        for (S d:data)
            d.setRequestID(requestID);
        outStore.store(data);
    }

    protected void publishResult(S... data){
        outStore.store(data);
    }


    public void onAttach(){}
    public void onDetach(){}

    public abstract void execute(long requestID, T... input);

    @SuppressWarnings("unchecked")
    public void executeCast(long requestID, Data... input){
        execute(requestID, Arrays.asList(input).toArray(
                (T[]) Array.newInstance(getInputType(), input.length)));
    }
}
