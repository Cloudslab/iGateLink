package org.cloudbus.foggatewaylib.service;

import android.app.Activity;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import org.cloudbus.foggatewaylib.utils.NotificationUtils;

/**
 * {@link Service} extension that automatically puts itself in the foreground at the start.
 * This class also contains methods for simplifying the management of the service lifecycle.
 *
 * @author Riccardo Mancini
 */
public abstract class ForegroundService extends Service {
    private static final String TAG = "ForegroundService";

    private final ForegroundServiceBinder binder = new ForegroundServiceBinder();

    /**
     * The service should not stop after execution of this intent.
     *
     * @see #execute()
     * @see #onStartCommand(Intent, int, int)
     */
    public static final int KEEP_ALIVE = 1;

    /**
     * The service should stop after execution of this intent.
     *
     * @see #execute()
     * @see #onStartCommand(Intent, int, int)
     */
    public static final int SHOULD_STOP = 2;

    /**
     * Intent action meaning that the service must start.
     *
     * @see #onStartCommand(Intent, int, int)
     */
    public static final String ACTION_START = "START";

    /**
     * Intent action meaning that the service must stop.
     *
     * @see #onStartCommand(Intent, int, int)
     */
    public static final String ACTION_STOP = "STOP";

    /**
     * Custom operations to execute at service start-up.
     * It is executed after initialization, when the service receives the {@link #ACTION_START}
     * action. For defining custom actions refer to {@link #customAction(String)}.
     *
     * @return {@link #KEEP_ALIVE} or {@link #SHOULD_STOP}.
     * @see #onStartCommand(Intent, int, int)
     */
    protected abstract int execute();

    /**
     * Custom operations to execute at service initialization during start-up.
     * This method should contain only the initialization and not hang up the service.
     * For long executions, use {@link #execute()}.
     *
     * @param extras the extras in the {@link Intent} that triggered the service.
     * @return true if everything is alright, false if the service should be aborted.
     */
    protected boolean init(Bundle extras){
        return true;
    }

    /**
     * Sends an {@link Intent} to the service.
     *
     * @param context the {@link Context} in which {@link Context#startService(Intent)} will be
     *                called.
     * @param action the action of the service (it can be one of {@link #ACTION_START} or
     *               {@link #ACTION_STOP}, or it could be a cutom action handled by
     *               {@link #customAction(String)}.
     * @param extras extras to be added to the {@link Intent}.
     * @param cls the class of the service to be started.
     * @see #startForegroundService(Context, Class, Class)
     * @see #startForegroundService(Context, Bundle, Class, Class)
     * @see #stopForegroundService(Context, Class)
     */
    public static void sendIntentToForegroundService(Context context, String action, Bundle extras,
                                                     Class<? extends ForegroundService> cls){
        Intent intent = new Intent(context, cls);
        intent.setAction(action);
        if (extras != null)
            intent.putExtras(extras);
        context.startService(intent);
        Log.d(TAG, "Intent sent");
    }

    /**
     * Starts the {@link ForegroundService} by sending the {@link #ACTION_START} action.
     *
     * @param context the {@link Context} in which {@link Context#startService(Intent)} will be
     *                called.
     * @param extras extras to be added to the {@link Intent}.
     * @param serviceClass the class of the service to be started.
     * @param activityClass the class of the activity to be started when the notification is
     *                      clicked.
     * @see #sendIntentToForegroundService(Context, String, Bundle, Class)
     * @see #startForegroundService(Context, Class, Class)
     * @see #stopForegroundService(Context, Class)
     * @see #bind(Context, ServiceConnection, Class)
     */
    public static void startForegroundService(Context context, Bundle extras,
                                              Class<? extends ForegroundService> serviceClass,
                                              Class<? extends Activity> activityClass){
        if (extras == null)
            extras = new Bundle();
        extras.putString("activity", activityClass.getName());
        sendIntentToForegroundService(context, ACTION_START, extras, serviceClass);
    }

    /**
     * Starts the {@link ForegroundService} by sending the {@link #ACTION_START} action.
     *
     * @param context the {@link Context} in which {@link Context#startService(Intent)} will be
     *                called.
     * @param serviceClass the class of the service to be started.
     * @param activityClass the class of the activity to be started when the notification is
     *                      clicked.
     * @see #sendIntentToForegroundService(Context, String, Bundle, Class)
     * @see #startForegroundService(Context, Bundle, Class, Class)
     * @see #stopForegroundService(Context, Class)
     * @see #bind(Context, ServiceConnection, Class)
     */
    public static void startForegroundService(Context context,
                                              Class<? extends ForegroundService> serviceClass,
                                              Class<? extends Activity> activityClass){
        Bundle extras = new Bundle();
        extras.putString("activity", activityClass.getName());
        sendIntentToForegroundService(context, ACTION_START, extras, serviceClass);
    }

    /**
     * Stops the {@link ForegroundService} by sending the {@link #ACTION_STOP} action.
     *
     * @param context the {@link Context} in which {@link Context#stopService(Intent)} will be
     *                called.
     * @param cls the class of the service to be started.
     * @see #sendIntentToForegroundService(Context, String, Bundle, Class)
     * @see #startForegroundService(Context, Bundle, Class, Class)
     * @see #startForegroundService(Context, Class, Class)
     */
    public static void stopForegroundService(Context context,
                                             Class<? extends ForegroundService> cls){
        sendIntentToForegroundService(context, ACTION_STOP, null, cls);
    }

    /**
     * Binds the {@link Context} to the {@link ForegroundService}.
     *
     * @param context the {@link Context} in which {@link Context#stopService(Intent)} will be
     *                called.
     * @param connection the {@link ServiceConnection} callback to be used.
     * @param cls the class of the service to be started.
     * @see #startForegroundService(Context, Bundle, Class, Class)
     * @see #startForegroundService(Context, Class, Class)
     */
    public static void bind(Context context, ServiceConnection connection,
                            Class<? extends ForegroundService> cls){
        Intent intent = new Intent(context, cls);
        context.bindService(intent, connection, 0);
    }

    /**
     * Stops the service.
     *
     * @see #onStop()
     */
    private void stopMe(){
        onStop();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(true);
        }
        stopSelf();
    }

    /**
     * Does something before the service stops itself using {@link #stopMe()}.
     *
     * @see #stopMe()
     */
    public void onStop(){  }

    /**
     * Starts the service in the foreground.
     *
     * @param extras {@link Intent} extras containing info about the activity to be called once
     *                             the notification is clicked.
     * @return true if everything was alright, false if the service should be aborted.
     */
    @SuppressWarnings("unchecked")
    protected boolean selfStartForeground(Bundle extras){
        Class activity;
        try{
            String activityName = extras.getString("activity");
            if (activityName != null)
                activity = Class.forName(activityName);
            else
                return false;
        } catch (ClassNotFoundException e){
            e.printStackTrace();
            Log.e(TAG, "Activity string is not valid!");
            return false;
        }

        NotificationUtils.initDefaultChannel(this);
        Notification notification = NotificationUtils.buildDefaultNotification(this,
                activity);

        startForeground(NotificationUtils.DEFAULT_NOTIFICATION_ID, notification);
        return true;
    }

    /**
     * Override this method for defining a custom action.
     *
     * @param action the action to be handled.
     * @return true if action was handled, false otherwise.
     */
    protected boolean customAction(String action){
        return false;
    }

    /**
     * Based on the action in the {@link Intent}, it does one of the following:
     * <ul>
     *     <li>{@link #ACTION_START}: starts the service by calling {@link #init(Bundle)} and
     *     {@link #selfStartForeground(Bundle)} and finally {@link #execute()}</li>
     *     <li>{@link #ACTION_STOP}: stops itself by calling {@link #stopMe()}</li>
     *     <li>otherwise calls {@link #customAction(String)} which will handle the action</li>
     * </ul>
     *
     * @see {@link Service#onStartCommand(Intent, int, int)}
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null){
            stopMe();
            return START_NOT_STICKY;
        }
        String action = intent.getAction();

        Log.d("DEBUG", "Received intent " + action);

        switch (action){
            case ACTION_START:
                Log.d(TAG, "Starting... (" + getClass().getSimpleName() + ")");

                Bundle extras = intent.getExtras();

                if (!init(extras) || !selfStartForeground(extras)){
                    stopMe();
                    break;
                }

                switch (execute()){
                    case KEEP_ALIVE:
                        break;

                    case SHOULD_STOP:
                        stopMe();
                        break;
                }
                break;

            case ACTION_STOP:
                stopMe();
                break;

            default:
                if (!customAction(action))
                    Log.w(TAG, "Unrecognized action");
        }

        return START_STICKY;
    }

    /**
     * Binder to the activity.
     */
    public class ForegroundServiceBinder extends Binder {

        /**
         * @return reference to this service.
         */
        ForegroundService getService() {
            // Return this instance of LocalService so clients can call public methods
            return ForegroundService.this;
        }
    }

    /**
     * Return this binder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}
