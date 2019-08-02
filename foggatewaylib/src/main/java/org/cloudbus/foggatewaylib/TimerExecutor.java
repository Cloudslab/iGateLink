package org.cloudbus.foggatewaylib;

import android.os.Bundle;
import android.util.Log;

public abstract class TimerExecutor<T extends Data, S extends Data> extends EventExecutor<T,S> {
    private static final String TAG = "TimerExecutor";
    private int period;
    private int delay;
    private Timer timer;

    @Override
    protected void init(Bundle extras) {
        super.init(extras);

        period = extras.getInt("period", 0);
        delay = extras.getInt("delay", 0);

        if (period <= 0){
            Log.e(TAG, "No period was set");
        } else {
            timer = new Timer(new Runnable() {
                @Override
                public void run() {
                    eventHandler();
                }
            }, delay >= 0 ? delay : 0, period);
        }
    }

    @Override
    protected int execute() {
        if (timer == null)
            return SHOULD_STOP;
        else{
            timer.start();
            return KEEP_ALIVE;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (timer != null)
            timer.stop();
    }
}
