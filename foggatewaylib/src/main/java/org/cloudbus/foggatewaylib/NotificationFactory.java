package org.cloudbus.foggatewaylib;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationFactory {

    public static final String CHANNEL_ID = "default";
    public static final int NOTIFICATION_ID = 1;

    public static void initChannels(Context context) {
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                "Default",
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("Default notification channel");
        notificationManager.createNotificationChannel(channel);
    }

    public static Notification build(Context context, Class<?> activity, String title, String text,
                                     int icon){
        initChannels(context);

        Intent notificationIntent = new Intent(context, activity);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                0, notificationIntent, 0);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(pendingIntent);

        if (icon != 0)
            notificationBuilder.setSmallIcon(icon);

        return notificationBuilder.build();
    }
}
