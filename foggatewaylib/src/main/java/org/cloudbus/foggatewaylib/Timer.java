package org.cloudbus.foggatewaylib;

import android.os.Handler;

import java.util.TimerTask;

public class Timer {
    private java.util.Timer timer;
    private Handler handler = new Handler();
    private int delay;
    private int period;
    private Runnable runnable;

    public Timer(Runnable runnable, int delay, int period){
        this.runnable = runnable;
        this.delay = delay;
        this.period = period;
    }

    public void stop(){
        if(timer != null){
            timer.cancel();
            timer.purge();
        }
    }

    public void start(){
        timer = new java.util.Timer();
        TimerTask timerTask = new TimerTask() {
            public void run() {
                handler.post(runnable);
            }
        };
        timer.schedule(timerTask, delay, period);
    }
}
