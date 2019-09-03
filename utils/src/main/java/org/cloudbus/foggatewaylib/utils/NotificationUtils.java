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

/**
 * Utility functions for managing notifications.
 *
 * @author Riccardo Mancini
 */
public class NotificationUtils {

    public static final String DEFAULT_CHANNEL_ID = "default";
    public static final String DEFAULT_CHANNEL_NAME = "Default";
    public static final String DEFAULT_CHANNEL_DESCRIPTION = "Default channel.";
    public static final int DEFAULT_NOTIFICATION_ID = 1;
    public static final String DEFAULT_NOTIFICATION_TITLE = "App is running";
    public static final String DEFAULT_NOTIFICATION_DESCRIPTION = "Service running in background";

    /**
     * Creates a {@link NotificationChannel} with the given {@code channel_id}, {@code name},
     * {@code description} and {@code importance}.
     * No action will be taken if {@link Build.VERSION#SDK_INT} is lower than 26.
     *
     * @param context the {@link Context} in which {@link Context#getSystemService(String)}
     *                will be called.
     * @param channel_id the id of the channel
     * @param name the name of the channel
     * @param description the description of the channel
     * @param importance the importance of the channel
     * @see NotificationChannel#NotificationChannel(String, CharSequence, int)
     */
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

    /**
     * Creates a channel with default values.
     *
     * @param context the {@link Context} in which {@link Context#getSystemService(String)}
     *                will be called.
     * @see #DEFAULT_CHANNEL_ID
     * @see #DEFAULT_CHANNEL_NAME
     * @see #DEFAULT_CHANNEL_DESCRIPTION
     * @see NotificationManager#IMPORTANCE_DEFAULT
     */
    public static void initDefaultChannel(Context context) {
        initChannel(context, DEFAULT_CHANNEL_ID, DEFAULT_CHANNEL_NAME, DEFAULT_CHANNEL_DESCRIPTION,
                NotificationManager.IMPORTANCE_DEFAULT);
    }

    /**
     * Builds a notification with the given parameters.
     *
     * @param context the context used to create the notification.
     * @param intent the intent of the notification.
     * @param title the title of the notification.
     * @param text the text of the notification.
     * @param icon the icon of the notification.
     * @param channelId the id of the channel in which to publish the notification.
     * @return the built {@link Notification}
     *
     * @see NotificationCompat.Builder#Builder(Context, String)
     * @see NotificationCompat.Builder#setContentTitle(CharSequence)
     * @see NotificationCompat.Builder#setContentText(CharSequence)
     * @see NotificationCompat.Builder#setContentIntent(PendingIntent)
     */
    public static Notification buildNotification(Context context, PendingIntent intent,
                                                 String title, String text, int icon,
                                                 String channelId){
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, channelId)
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(intent);

        if (icon != 0)
            notificationBuilder.setSmallIcon(icon);

        return notificationBuilder.build();
    }

    /**
     * Builds a notification with default parameters.
     *
     * @param context the context used to create the notification.
     * @param activity the class of the activity to start when the notification is clicked.
     * @return the built {@link Notification}
     * @see #buildNotification(Context, PendingIntent, String, String, int, String)
     * @see #DEFAULT_NOTIFICATION_TITLE
     * @see #DEFAULT_NOTIFICATION_DESCRIPTION
     * @see #DEFAULT_CHANNEL_ID
     */
    public static Notification buildDefaultNotification(Context context,
                                                        Class<? extends Activity> activity) {
        Intent notificationIntent = new Intent(context, activity);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                0, notificationIntent, 0);

        return buildNotification(context, pendingIntent, DEFAULT_NOTIFICATION_TITLE,
                DEFAULT_NOTIFICATION_DESCRIPTION, 0, DEFAULT_CHANNEL_ID);
    }
}
