package org.cloudbus.foggatewaylib;

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

public abstract class ForegroundService extends Service {

    private static final String TAG = "ForegroundService";

    private final ForegroundServiceBinder binder = new ForegroundServiceBinder();

    public static final int KEEP_ALIVE = 1;
    public static final int SHOULD_STOP = 2;

    public static final String ACTION_START = "START";
    public static final String ACTION_STOP = "STOP";

    protected abstract int execute();

    protected boolean init(Bundle extras){
        return true;
    }

    public static void sendIntentToForegroundService(Context context, String action, Bundle extras, Class<? extends ForegroundService> cls){
        Intent intent = new Intent(context, cls);
        intent.setAction(action);
        if (extras != null)
            intent.putExtras(extras);
        context.startService(intent);
        Log.d(TAG, "Intent sent");
    }

    public static void startForegroundService(Context context, Bundle extras, Class<? extends ForegroundService> serviceClass, Class<? extends Activity> activityClass){
        if (extras == null)
            extras = new Bundle();
        extras.putString("activity", activityClass.getName());
        sendIntentToForegroundService(context, ACTION_START, extras, serviceClass);
    }

    public static void startForegroundService(Context context, Class<? extends ForegroundService> serviceClass, Class<? extends Activity> activityClass){
        Bundle extras = new Bundle();
        extras.putString("activity", activityClass.getName());
        sendIntentToForegroundService(context, ACTION_START, extras, serviceClass);
    }

    public static void stopForegroundService(Context context, Class<? extends ForegroundService> cls){
        sendIntentToForegroundService(context, ACTION_STOP, null, cls);
    }

    public static void bind(Context context, ServiceConnection connection, Class<? extends ForegroundService> cls){
        Intent intent = new Intent(context, cls);
        context.bindService(intent, connection, 0);
    }

    private void stopMe(){
        onStop();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(true);
        }
        stopSelf();
    }

    public void onStop(){

    }

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

    protected boolean customAction(String action){
        return false;
    }

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

    public class ForegroundServiceBinder extends Binder {
        ForegroundService getService() {
            // Return this instance of LocalService so clients can call public methods
            return ForegroundService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    @Override
    public final void onDestroy() {
        super.onDestroy();
        onStop();
    }
}
