package org.cloudbus.foggatewaylib.utils;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationUtils {

    public static final String DEFAULT_CHANNEL_ID = "default";
    public static final String DEFAULT_CHANNEL_NAME = "Default";
    public static final String DEFAULT_CHANNEL_DESCRIPTION = "Default channel.";
    public static final int DEFAULT_NOTIFICATION_ID = 1;
    public static final String DEFAULT_NOTIFICATION_TITLE = "App is running";
    public static final String DEFAULT_NOTIFICATION_DESCRIPTION = "App service running in background";

    public static void initChannel(Context context, String channel_id, String name,
                                   String description, int importance) {
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel(channel_id, name,
                importance);
        channel.setDescription(description);
        notificationManager.createNotificationChannel(channel);
    }

    public static void initDefaultChannel(Context context) {
        initChannel(context, DEFAULT_CHANNEL_ID, DEFAULT_CHANNEL_NAME, DEFAULT_CHANNEL_DESCRIPTION,
                NotificationManager.IMPORTANCE_DEFAULT);
    }

    public static Notification buildNotification(Context context, PendingIntent intent, String title,
                                                 String text, int icon, String channelId){
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, channelId)
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(intent);

        if (icon != 0)
            notificationBuilder.setSmallIcon(icon);

        return notificationBuilder.build();
    }

    public static Notification buildDefaultNotification(Context context,
                                                        Class<? extends Activity> activity) {
        Intent notificationIntent = new Intent(context, activity);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                0, notificationIntent, 0);

        return buildNotification(context, pendingIntent, DEFAULT_NOTIFICATION_TITLE,
                DEFAULT_NOTIFICATION_DESCRIPTION, 0, DEFAULT_CHANNEL_ID);
    }
}
