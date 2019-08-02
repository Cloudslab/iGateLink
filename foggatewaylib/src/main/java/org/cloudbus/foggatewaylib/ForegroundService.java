package org.cloudbus.foggatewaylib;

import android.app.Activity;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public abstract class ForegroundService extends Service {

    private static final String LOGSTR = "ForegroundService";

    public static final int KEEP_ALIVE = 1;
    public static final int SHOULD_STOP = 2;

    public static final String ACTION_START = "START";
    public static final String ACTION_STOP = "STOP";

    protected abstract int execute();

    protected void init(Bundle extras){ }

    public static void sendIntentToForegroundService(Context context, String action, Bundle extras, Class<? extends ForegroundService> cls){
        Intent intent = new Intent(context, cls);
        intent.setAction(action);
        if (extras != null)
            intent.putExtras(extras);
        context.startService(intent);
        Log.d(LOGSTR, "Intent sent");
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

    private void stopMe(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(0);
        }
        stopSelf();
    }

    public void onStop(){

    }

    protected boolean customAction(String action){
        return false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();

        Log.d("DEBUG", "Received intent " + action);

        switch (action){
            case ACTION_START:
                Log.d(LOGSTR, "Starting... (" + getClass().getSimpleName() + ")");
                Class activity;
                String title;
                String text;
                int icon;

                Bundle extras = intent.getExtras();

                try{
                    activity = Class.forName(intent.getStringExtra("activity"));
                } catch (ClassNotFoundException e){
                    e.printStackTrace();
                    Log.e(LOGSTR, "Activity string is not valid!");
                    stopSelf();
                    return START_NOT_STICKY;
                }

                title = extras.getString("title", "Running");
                text = extras.getString("text", "App is running");
                icon = extras.getInt("icon", 0);

                init(extras);

                Notification notification =  NotificationFactory.build(this, activity, title, text, icon);

                startForeground(NotificationFactory.NOTIFICATION_ID, notification);

                switch (execute()){
                    case KEEP_ALIVE:
                        break;

                    case SHOULD_STOP:
                        stopMe();
                        break;
                }
                break;
            case ACTION_STOP:
                onStop();
                stopMe();
                break;

            default:
                if (!customAction(action))
                    Log.w(LOGSTR, "Unrecognized action");
        }

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public final void onDestroy() {
        super.onDestroy();
        onStop();
    }
}
