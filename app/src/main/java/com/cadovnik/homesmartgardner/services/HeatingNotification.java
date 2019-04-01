package com.cadovnik.homesmartgardner.services;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import android.widget.RemoteViews;

import com.cadovnik.homesmartgardner.R;
import com.cadovnik.homesmartgardner.view.MainActivity;

/**
 * Helper class for showing and canceling heating
 * notifications.
 * <p>
 * This class makes heavy use of the {@link NotificationCompat.Builder} helper
 * class to create notifications in a backward-compatible way.
 */
public class HeatingNotification {
    private static final String NOTIFICATION_TAG = "Heating";
    public static void sendNotify(final Context context,
                              final String exampleString) {
        final Resources res = context.getResources();

        // This image is used as the notification's large icon (thumbnail).

        final String ticker = exampleString;
        final String title = res.getString(
                R.string.heating_notification_title_template, exampleString);
        final String text = res.getString(
                R.string.heating_notification_placeholder_text_template, exampleString);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context)

                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_smoke_bomb)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setTicker(ticker)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(text)
                        .setBigContentTitle(title)
                        .setSummaryText("Dummy summary text"))
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setAutoCancel(true);
        String channelId = "heating";
        NotificationChannel channel = new NotificationChannel(channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT);

        builder.setChannelId(channelId);
        notify(context, builder.build(), channel);
    }

    public static void HeatingProcessNotify(final Context contex){
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(contex)
                        .setSmallIcon(R.drawable.ic_smoke_bomb)
                        .setContentTitle("Heating process ")
                        .setContentText("Heating in process");
        Intent resultIntent = new Intent(contex, MainActivity.class);


        RemoteViews remoteViewsExtended = new RemoteViews("Heating process ", R.layout.process_notification);
//        remoteViewsExtended.setTextViewText(R.id.textView, "Extended custom notification text");
//        remoteViewsExtended.setOnClickPendingIntent(R.id.root, rootPendingIntent);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(contex);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        builder.setContentIntent(resultPendingIntent)
                .setCustomBigContentView(remoteViewsExtended)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle());

        NotificationManager mNotificationManager =
                (NotificationManager) contex.getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        String channelId = "heating";
        NotificationChannel channel = new NotificationChannel(channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT);

        builder.setChannelId(channelId);
        notify(contex,builder.build(), channel);

    }
    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private static void notify(final Context context, final Notification notification, NotificationChannel channel ) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        nm.createNotificationChannel(channel);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.notify(NOTIFICATION_TAG, 0, notification);
        } else {
            nm.notify(NOTIFICATION_TAG.hashCode(), notification);
        }
    }


    @TargetApi(Build.VERSION_CODES.ECLAIR)
    public static void cancel(final Context context) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.cancel(NOTIFICATION_TAG, 0);
        } else {
            nm.cancel(NOTIFICATION_TAG.hashCode());
        }
    }
}
