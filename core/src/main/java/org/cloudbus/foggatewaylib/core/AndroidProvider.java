package org.cloudbus.foggatewaylib.core;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

/**
 * Provider with Android-specific thread-safe methods for publishing progress and results.
 *
 * @param <T> the type of input data.
 * @param <S> the type of output data.
 *
 * @author Riccardo Mancini
 */
public abstract class AndroidProvider<T extends Data, S extends Data> extends Provider<T, S> {

    /**
     * @param inputType  type of the input data.
     * @param outputType type of the output data.
     */
    public AndroidProvider(Class<T> inputType, Class<S> outputType) {
        super(inputType, outputType);
    }

    /**
     * Notifies about current progress in the execution of the request. The progress update must
     * refer to the whole request, of which this provider could be just an intermediate step.
     * If called from a thread different from the main thread, it will execute in the main thread
     * by using the main thread handler.
     *
     * @param requestID the request id this progress refers to.
     * @param progress refer to {@link ProgressData#progress}
     * @param message refer to {@link ProgressData#message}
     * @see #publishProgresses(long, ProgressData...)
     * @see #publishProgress(long, int, String)
     * @see ProgressData
     */
    @Override
    protected void publishProgress(final long requestID, final int progress,
                                             final String message){
        if (Looper.myLooper() == Looper.getMainLooper())
            super.publishProgress(requestID, progress, message);
        else
            runInMainThread(new Runnable() {
                @Override
                public void run() {
                    AndroidProvider.super.publishProgress(requestID, progress, message);
                }
            });
    }

    /**
     * Notifies about current progress in the execution of the request. The progress update must
     * refer to the whole request, of which this provider could be just an intermediate step.
     * If called from a thread different from the main thread, it will execute in the main thread
     * by using the main thread handler.
     *
     * @param requestID the request id this progress refers to.
     * @param progressData the progress data to be stored.
     * @see #publishProgress(long, int, String)
     * @see #publishProgresses(long, ProgressData...)
     * @see ProgressData
     */
    @Override
    protected void publishProgresses(final long requestID,
                                               final ProgressData... progressData){
        if (Looper.myLooper() == Looper.getMainLooper())
            super.publishProgresses(requestID, progressData);
        else
            runInMainThread(new Runnable() {
                @Override
                public void run() {
                    AndroidProvider.super.publishProgresses(requestID, progressData);
                }
            });
    }

    /**
     * Publishes the result of the execution in the {@link #outStore}.
     * If called from a thread different from the main thread, it will execute in the main thread
     * by using the main thread handler.
     *
     * @param requestID the request id the data refers to.
     * @param data the data to be stored.
     * @see #publishResultsThreadSafe(Data[])
     * @see #publishResults(long, Data[])
     */
    @Override
    protected void publishResults(final long requestID, final S... data){
        if (Looper.myLooper() == Looper.getMainLooper())
            super.publishResults(requestID, data);
        else
            runInMainThread(new Runnable() {
                @Override
                public void run() {
                    AndroidProvider.super.publishResults(requestID, data);
                }
            });
    }

    /**
     * Publishes the result of the execution in the {@link #outStore}.
     * If called from a thread different from the main thread, it will execute in the main thread
     * by using the main thread handler.
     *
     * @param data the data to be stored.
     * @see #publishResultsThreadSafe(long, Data[])
     * @see #publishResults(Data[])
     */
    @Override
    protected void publishResults(final S... data){
        if (Looper.myLooper() == Looper.getMainLooper())
            super.publishResults(data);
        else
            runInMainThread(new Runnable() {
                @Override
                public void run() {
                    AndroidProvider.super.publishResults(data);
                }
            });
    }

    protected void runInMainThread(Runnable runnable){
        if (getContext() != null){
            new Handler(getContext().getMainLooper()).post(runnable);
        }
    }

    /**
     * Returns the {@link AndroidExecutionManager} linked to this {@link Provider}.
     *
     * @throws RuntimeException in case the {@link ExecutionManager} is not an instance
     *         of {@link AndroidExecutionManager}.
     */
    public AndroidExecutionManager getAndroidExecutionManager(){
        if (getExecutionManager() == null)
            return null;

        if (getExecutionManager() instanceof AndroidExecutionManager)
            return (AndroidExecutionManager) getExecutionManager();
        else
            throw new RuntimeException("The ExecutionManager must be an AndroidExecutionManager");
    }

    /**
     * Returns the context of the attached {@link AndroidExecutionManager}.
     *
     * @throws RuntimeException in case the {@link ExecutionManager} is not an instance
     *         of {@link AndroidExecutionManager}.
     */
    protected Context getContext(){
        if (getAndroidExecutionManager() == null)
            return null;

        return getAndroidExecutionManager().getContext();
    }
}
