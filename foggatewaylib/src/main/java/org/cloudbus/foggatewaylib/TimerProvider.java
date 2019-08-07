package org.cloudbus.foggatewaylib;

import android.util.Log;

import org.cloudbus.foggatewaylib.utils.Timer;

public abstract class TimerProvider<T extends Data, S extends Data>
        extends AsyncProvider<T,S> {
    private static final String TAG = "TimerProvider";
    private Timer timer;

    protected abstract T[] retrieveInputData();

    protected long makeRequestID(){
        return ExecutionManager.nextRequestID();
    }

    public TimerProvider(int period, int delay, Class<T> inputType, Class<S> outputType) {
        super(inputType, outputType);

        if (period <= 0) {
            Log.e(TAG, "No period was set");
        } else {
            timer = new Timer(new Runnable() {
                @Override
                public void run() {
                    execute(makeRequestID(), retrieveInputData());
                }
            }, delay >= 0 ? delay : 0, period);
        }
    }

    @Override
    public void onAttach() {
        if (timer != null)
            timer.start();
    }

    @Override
    public void onDetach() {
        if (timer != null)
            timer.stop();
    }
}
