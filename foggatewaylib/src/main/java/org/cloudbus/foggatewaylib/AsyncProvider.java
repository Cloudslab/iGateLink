package org.cloudbus.foggatewaylib;

import android.os.AsyncTask;

import java.util.concurrent.Executor;

public abstract class AsyncProvider<T extends Data, S extends Data> extends DataProvider<T, S>{
    private static final String TAG = "AsyncProvider";

    private Executor executor;

    public AsyncProvider(Class<T> inputType, Class<S> outputType){
        super(inputType, outputType);
        this.executor = AsyncTask.THREAD_POOL_EXECUTOR;
    }

    public abstract S[] getData(ProgressPublisher progressListener, long requestID, T... input)
            throws Exception;

    public void onPreExecute(long requestID){}
    public void onPostExecuteResult(long requestID, S... output){}
    public void onPostExecuteError(long requestID, Throwable throwable){}
    public void onProgressUpdate(long requestID, ProgressData... values){}

    protected void setExecutor(Executor executor) {
        this.executor = executor;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void execute(long requestID, T... input){
        AsyncProvider.DataProviderAsyncTask task =
                new AsyncProvider.DataProviderAsyncTask(requestID);
        task.executeOnExecutor(executor, input);
    }

    private class DataProviderAsyncTask extends AsyncTask<T, ProgressData, S[]>
                                        implements ProgressPublisher {
        private Throwable throwable;
        private long requestID;

        public DataProviderAsyncTask(long requestID){
            super();
            this.requestID = requestID;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            AsyncProvider.this.onPreExecute(requestID);
        }

        @Override
        protected S[] doInBackground(T... input) {
            try {
                return getData(this, requestID, input);
            } catch (Throwable e){
                throwable = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(S[] output) {
            if (output != null && output.length > 0){
                publishResult(requestID, output);
                onPostExecuteResult(requestID, output);
            } else{
                throwable.printStackTrace();
                AsyncProvider.this.publishProgress(requestID, -1,
                        "An error occurred: " + throwable.getMessage());
                onPostExecuteError(requestID, throwable);
            }
        }

        @Override
        public void publish(int progress, String message) {
            publishProgress(new ProgressData(requestID, progress, message));
        }

        @Override
        protected void onProgressUpdate(ProgressData... values) {
            progressStore.store(values);
            AsyncProvider.this.onProgressUpdate(requestID, values);
        }
    }

    public interface ProgressPublisher{
        void publish(int progress, String message);
    }
}
