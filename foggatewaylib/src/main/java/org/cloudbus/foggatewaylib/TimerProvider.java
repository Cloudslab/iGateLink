package org.cloudbus.foggatewaylib;

import android.util.Log;

public abstract class TimerProvider<T extends Data, S extends Data>
        extends DataProvider<T,S> {
    private static final String TAG = "TimerProvider";
    private int period;
    private int delay;
    private Timer timer;

    protected abstract T[] retrieveInputData();

    protected long makeRequestID(){
        return FogGatewayService.nextRequestID();
    }

    public TimerProvider(int period, int delay, Class<T> inputType, Class<S> outputType) {
        super(inputType, outputType);
        this.period = period;
        this.delay = delay;

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
