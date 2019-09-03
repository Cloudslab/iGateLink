package org.cloudbus.foggatewaylib.core;

import android.content.Context;

/**
 * Execution Manager with a {@link Context} property.
 *
 * @author Riccardo Mancini
 */
public class AndroidExecutionManager extends ExecutionManager {
    /**
     * Context in which the {@link ExecutionManager} is running.
     *
     * @see #ExecutionManager(Context)
     * @see #getContext()
     */
    private Context context;

    /**
     * Default constructor.
     * It sets the {@link #context}, initializes all the maps and creates the {@link ProgressData}
     * {@link Store}.
     *
     * @param context the {@link Context} in which {@link ExecutionManager} is running.
     *
     * @see #context
     * @see #stores
     * @see #providers
     * @see #providersOfData
     * @see #triggers
     * @see #choosers
     * @see #UITriggers
     * @see #KEY_DATA_PROGRESS
     */
    public AndroidExecutionManager(Context context){
        super();

        this.context = context;
    }

    /**
     * @return the {@link Context} in which {@link ExecutionManager} is running.
     */
    public Context getContext() {
        return context;
    }
}
