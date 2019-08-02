package org.cloudbus.foggatewaylib;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import androidx.annotation.NonNull;

public abstract class EventExecutor<T extends Data, S extends Data> extends Executor<T,S>{
    public static final String ACTION_EXECUTE = "EXECUTE";

    protected abstract T[] onEventGatherInput();
    protected abstract S onEventExecute(T... input) throws Exception;
    protected abstract void onEventResult(@NonNull S output);
    protected abstract void onEventError(Exception e);

    protected void onEventPreExecute() {}

    @Override
    protected int execute() {
        return KEEP_ALIVE;
    }

    @Override
    protected boolean customAction(String action) {
        if (action.equals(ACTION_EXECUTE)){
            eventHandler();
            return true;
        }
        return false;
    }

    class EventHandlerAsyncTask extends AsyncTask<T, Void, S>{
        private Exception exception;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            onEventPreExecute();
        }

        @Override
        protected S doInBackground(T... input) {
            try {
                return onEventExecute(input);
            } catch (Exception e){
                exception = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(S output) {
            super.onPostExecute(output);
            if (output != null)
                onEventResult(output);
            else
                onEventError(exception);
        }
    }

    protected void eventHandler(){
        @SuppressLint("StaticFieldLeak")
        EventHandlerAsyncTask task= new EventHandlerAsyncTask();
        task.execute(onEventGatherInput());
    }
}
