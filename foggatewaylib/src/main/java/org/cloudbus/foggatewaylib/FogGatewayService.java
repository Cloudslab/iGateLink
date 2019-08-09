package org.cloudbus.foggatewaylib;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * {@link ForegroundService} that holds an {@link ExecutionManager}.
 *
 * @author Riccardo Mancini
 */
//TODO consider moving to another package
public class FogGatewayService extends ForegroundService implements ExecutionManager.Holder {
    public static final String TAG = "FogGatewayService";

    private ExecutionManager executionManager;

    /**
     * Constructor that initializes the executionManager.
     * Do not call this directly! Use {@link #start(Context, Class)} to start the service.
     *
     * @see #start(Context, Class)
     */
    public FogGatewayService(){
        super();
        executionManager = new ExecutionManager(this);
    }

    /**
     * @see ForegroundService#init()
     */
    @Override
    protected boolean init(Bundle extras) {
        return true;
    }

    /**
     * @see ForegroundService#execute()
     */
    @Override
    protected int execute() {
        return ForegroundService.KEEP_ALIVE;
    }

    /**
     * @return the {@link ExecutionManager} hold in this service.
     */
    @Override
    public ExecutionManager getExecutionManager() {
        return executionManager;
    }

    /**
     * Starts this service.
     *
     * @param context the context in which to call {@link Context#startService(Intent)}.
     * @param cls the class of the activity that will be executed when clicking on the
     *            notification.
     * @see #stop(Context)
     */
    public static void start(Context context, Class<? extends Activity> cls){
        startForegroundService(context, FogGatewayService.class, cls);
    }

    /**
     * Stops this service.
     *
     * @param context the context in which to call {@link Context#stopService(Intent)}.
     * @see #stop(Context)
     */
    public static void stop(Context context){
        stopForegroundService(context, FogGatewayService.class);
    }

    /**
     * Unbinds from the activity, removing all UI {@link Trigger}s.
     *
     * @see android.app.Service#onUnbind(Intent)
     */
    @Override
    public boolean onUnbind(Intent intent) {
        executionManager.removeAllUITriggers();
        return false;
    }
}
