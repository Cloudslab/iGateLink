package org.cloudbus.foggatewaylib;

import android.os.AsyncTask;

import java.util.concurrent.Executor;

/**
 * Abstract implementation of a {@link Provider} based on {@link AsyncTask}.
 * Whenever {@link #execute(long, Data[])} is called, a new task is submitted to the
 * {@link Executor} backing the {@link AsyncTask}.
 *
 * @param <T> the type of input data.
 * @param <S> the type of output data.
 *
 * @author Riccardo Mancini
 */
public abstract class AsyncProvider<T extends Data, S extends Data> extends Provider<T, S> {
    private static final String TAG = "AsyncProvider";

    /**
     * The {@link Executor} backing the {@link AsyncTask} instances.
     */
    private Executor executor;

    /**
     * Default constructor that sets the {@link Executor} to the default one.
     *
     * @param inputType the type of input data.
     * @param outputType the type of output data.
     */
    public AsyncProvider(Class<T> inputType, Class<S> outputType){
        super(inputType, outputType);
        this.executor = AsyncTask.THREAD_POOL_EXECUTOR;
    }

    /**
     * Provides the data given some input data.
     * It will be called in {@link ProviderAsyncTask#doInBackground(Data[])}
     *
     * @param progressPublisher interface for publishing progress through
*                          {@link Provider#publishProgress(long, int, String)} by using
*                          {@link AsyncTask#publishProgress(Object[])} and
*                          {@link ProviderAsyncTask#onProgressUpdate(ProgressData[])}.
     * @param requestID the id of the request that is being serviced.
     * @param input the input data.
     * @return the output data.
     * @throws Exception any exception thrown will be caught in and passed to
     *                   {@link #onPostExecuteError(long, Throwable)}.
     * @see ProviderAsyncTask#doInBackground(Data[])
     * @see #execute(long, Data[])
     */
    public abstract S[] getData(ProgressPublisher progressPublisher, long requestID, T... input)
            throws Exception;

    /**
     * Callback that will be called in {@link ProviderAsyncTask#onPreExecute()}.
     *
     * @param requestID the id of the request that is being serviced.
     * @see ProviderAsyncTask#onPreExecute()
     */
    public void onPreExecute(long requestID){}

    /**
     * Callback that will be called in {@link ProviderAsyncTask#onPostExecute(Data[])}
     * in case no error occurred, after having stored the output in the {@link Provider#outStore}.
     *
     * @param requestID the id of the request that is being serviced.
     * @param output the output produced by {@link #getData(ProgressPublisher, long, Data[])}
     * @see ProviderAsyncTask#onPostExecute(Data[])
     * @see #onPostExecuteError(long, Throwable)
     * @see Provider#publishResults(long, Data[])
     */
    public void onPostExecuteResult(long requestID, S... output){}

    /**
     * Callback that will be called in {@link ProviderAsyncTask#onPostExecute(Data[])} in case
     * an error occurred, after having published an error progress with {@code progress=-1}.
     *
     * @param requestID the id of the request that is being serviced.
     * @param throwable the throwable that caused the error.
     * @see ProviderAsyncTask#onPostExecute(Data[])
     * @see #onPostExecuteResult(long, Data[])
     * @see Provider#publishProgresses(long, ProgressData...)
     */
    public void onPostExecuteError(long requestID, Throwable throwable){}

    /**
     * Callback that will be called in {@link ProviderAsyncTask#onProgressUpdate(ProgressData...),
     * after having published the progresses.
     *
     * @param requestID the id of the request that is being serviced.
     * @param values the progress updates that have been published
     * @see ProviderAsyncTask#onProgressUpdate(ProgressData...)
     * @see Provider#publishProgresses(long, ProgressData...)
     */
    public void onProgressUpdate(long requestID, ProgressData... values){}

    /**
     * @param executor the {@link Executor} that will be used for upcoming tasks.
     */
    protected void setExecutor(Executor executor) {
        this.executor = executor;
    }

    /**
     * Schedules a new task in the {@link #executor}.
     *
     * @param requestID the request id the given data refers to.
     * @param input the input data.
     */
    @Override
    @SuppressWarnings("unchecked")
    public void execute(long requestID, T... input){
        ProviderAsyncTask task =
                new ProviderAsyncTask(requestID);
        task.executeOnExecutor(executor, input);
    }

    /**
     * {@link AsyncTask} implementation using the callbacks defined in the upper class.
     *
     * @see #onPreExecute(long)
     * @see #getData(ProgressPublisher, long, Data[])
     * @see #onProgressUpdate(long, ProgressData...)
     * @see #onPostExecuteResult(long, Data[])
     * @see #onPostExecuteError(long, Throwable)
     */
    private class ProviderAsyncTask extends AsyncTask<T, ProgressData, S[]>
                                        implements ProgressPublisher {
        /**
         * The throwable caught in {@link #doInBackground(Data[])}, if any.
         */
        private Throwable throwable;

        /**
         * ID of the request that is being serviced.
         */
        private long requestID;

        /**
         * Default constructor that sets the {@link #requestID}.
         */
        public ProviderAsyncTask(long requestID){
            super();
            this.requestID = requestID;
        }

        /**
         * @see AsyncTask#onPreExecute()
         * @see AsyncProvider#onPreExecute(long)
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            AsyncProvider.this.onPreExecute(requestID);
        }

        /**
         * Runs {@link AsyncProvider#getData(ProgressPublisher, long, Data[])} and catches any
         * {@link Throwable} for later error handling in
         * {@link AsyncProvider#onPostExecuteError(long, Throwable)}.
         *
         * @param input input data
         * @return output data
         * @see AsyncTask#doInBackground(Object[])
         * @see AsyncProvider#getData(ProgressPublisher, long, Data[])
         */
        @Override
        protected S[] doInBackground(T... input) {
            try {
                return getData(this, requestID, input);
            } catch (Throwable e){
                throwable = e;
                return null;
            }
        }

        /**
         * Publishes results and calls {@link AsyncProvider#onPostExecuteResult(long, Data[])}
         * in case no error was generated in {@link #doInBackground(Data[])}; publishes an error
         * progress update and calls {@link AsyncProvider#onPostExecuteError(long, Throwable)}
         * otherwise.
         *
         * @param output data
         * @see AsyncTask#onPostExecute(Object)
         * @see AsyncProvider#onPostExecuteError(long, Throwable)
         * @see AsyncProvider#onPostExecuteResult(long, Data[])
         * @see AsyncProvider#getData(ProgressPublisher, long, Data[])
         */
        @Override
        protected void onPostExecute(S[] output) {
            if (output != null && output.length > 0){
                publishResults(requestID, output);
                onPostExecuteResult(requestID, output);
            } else{
                throwable.printStackTrace();
                AsyncProvider.this.publishProgress(requestID, -1,
                        "An error occurred: " + throwable.getMessage());
                onPostExecuteError(requestID, throwable);
            }
        }

        /**
         * Passes a progress update from the worker thread to the main thread using
         * {@link AsyncTask#publishProgress(Object[])}.
         *
         * @param progress refer to {@link Provider#publishProgress(long, int, String)}
         * @param message refer to {@link Provider#publishProgress(long, int, String)}
         * @see AsyncTask#publishProgress(Object[])
         */
        @Override
        public void publish(int progress, String message) {
            publishProgress(new ProgressData(getProviderKey(), requestID, progress, message));
        }

        /**
         * Receives the progress update from the worker thread and publishes it before calling
         * the {@link AsyncProvider#onProgressUpdate(long, ProgressData...)} callback.
         *
         * @param values
         * @see AsyncTask#onProgressUpdate(Object[])
         * @see Provider#publishProgresses(long, ProgressData...)
         * @see AsyncProvider#onProgressUpdate(long, ProgressData...)
         */
        @Override
        protected void onProgressUpdate(ProgressData... values) {
            AsyncProvider.this.publishProgresses(requestID, values);
            AsyncProvider.this.onProgressUpdate(requestID, values);
        }
    }

    /**
     * Interface used for passing the progress updates from the {@link AsyncProvider} context
     * to the {@link AsyncProvider.ProviderAsyncTask} one and finally calling
     * {@link Provider#publishProgresses(long, ProgressData...)}.
     *
     * @see AsyncProvider.ProviderAsyncTask#publish(int, String)
     * @see AsyncProvider.ProviderAsyncTask#onProgressUpdate(ProgressData...)
     * @see Provider#publishProgresses(long, ProgressData...)
     */
    public interface ProgressPublisher{

        /**
         * Publishes a progress update.
         *
         * @param progress refer to {@link Provider#publishProgress(long, int, String)}
         * @param message refer to {@link Provider#publishProgress(long, int, String)}
         */
        void publish(int progress, String message);
    }
}
