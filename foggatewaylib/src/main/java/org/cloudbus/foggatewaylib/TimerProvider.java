package org.cloudbus.foggatewaylib;

import android.util.Log;

import org.cloudbus.foggatewaylib.utils.Timer;

/**
 * Abstract {@link AsyncProvider} that will call its @{@link AsyncProvider#execute(long, Data[])}
 * asynchronously, based on a configurable {@link Timer}.
 *
 * @param <T> the type of input data.
 * @param <S> the type of output data.
 *
 * @author Riccardo Mancini
 */
//TODO not tested
public abstract class TimerProvider<T extends Data, S extends Data>
        extends AsyncProvider<T,S> {
    private static final String TAG = "TimerProvider";
    private Timer timer;

    protected abstract T[] retrieveInputData();

    /**
     * Retrieves a request id from the input data or makes a new one.
     *
     * @param input the input data
     * @return the request id
     * @see ExecutionManager#nextRequestID()
     */
    protected long makeRequestID(T... input){
        return ExecutionManager.nextRequestID();
    }

    /**
     * @param period the period of the timer.
     * @param delay the delay before the first trigger of the timer.
     * @param inputType the type of input data.
     * @param outputType the type of output data.
     */
    public TimerProvider(int period, int delay, Class<T> inputType, Class<S> outputType) {
        super(inputType, outputType);

        if (period <= 0) {
            Log.e(TAG, "No period was set");
        } else {
            timer = new Timer(new Runnable() {
                @Override
                public void run() {
                    T[] input = retrieveInputData();
                    execute(makeRequestID(input), input);
                }
            }, delay >= 0 ? delay : 0, period);
        }
    }

    /**
     * When attached to an {@link ExecutionManager}, start the timer.
     */
    @Override
    public void onAttach() {
        if (timer != null)
            timer.start();
    }

    /**
     * When detached from an {@link ExecutionManager}, stop the timer.
     */
    @Override
    public void onDetach() {
        if (timer != null)
            timer.stop();
    }
}
