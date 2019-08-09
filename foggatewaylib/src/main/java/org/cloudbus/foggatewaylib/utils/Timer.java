package org.cloudbus.foggatewaylib.utils;

import java.util.TimerTask;

/**
 * Simple timer implementation wrapping {@link java.util.Timer}.
 *
 * @see java.util.Timer
 *
 * @author Riccardo Mancini
 */
public class Timer {
    private java.util.Timer timer;
    private int delay;
    private int period;
    private Runnable runnable;

    /**
     * Constructor that creates a timer that will run the given {@link Runnable} every
     * {@code period} after an initial {@code delay}.
     * This does not start the timer.
     *
     * @param runnable the code to be run.
     * @param delay initial delay in milliseconds.
     * @param period period in milliseconds
     * @see #start()
     * @see java.util.Timer#schedule(TimerTask, long, long)
     */
    public Timer(Runnable runnable, int delay, int period){
        this.runnable = runnable;
        this.delay = delay;
        this.period = period;
    }

    /**
     * Stops the timer.
     *
     * @see java.util.Timer#cancel()
     * @see java.util.Timer#purge()
     */
    public void stop(){
        if(timer != null){
            timer.cancel();
            timer.purge();
        }
    }

    /**
     * Starts the timer.
     *
     * @see java.util.Timer#Timer()
     * @see TimerTask
     * @see java.util.Timer#schedule(TimerTask, long, long)
     */
    public void start(){
        timer = new java.util.Timer();
        TimerTask timerTask = new TimerTask() {
            public void run() {
                runnable.run();
            }
        };
        timer.schedule(timerTask, delay, period);
    }
}
