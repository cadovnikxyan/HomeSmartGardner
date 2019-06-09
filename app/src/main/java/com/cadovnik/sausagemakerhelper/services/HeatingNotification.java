package com.cadovnik.sausagemakerhelper.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.cadovnik.sausagemakerhelper.R;
import com.cadovnik.sausagemakerhelper.view.HeatingView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

/**
 * Helper class for showing and canceling heating
 * notifications.
 * <p>
 * This class makes heavy use of the {@link NotificationCompat.Builder} helper
 * class to create notifications in a backward-compatible way.
 */
public class HeatingNotification {
    private static final String NOTIFICATION_TAG = "Heating";

    public static final int ONGOING_NOTIFICATION_ID = getRandomNumber();
    public static final int SMALL_ICON = R.drawable.ic_smoke_bomb;
    public static final int STOP_ACTION_ICON = R.drawable.ic_action_stat_share;
    private static RemoteViews remoteView;
    private static NotificationCompat.Builder builder;
    private static Notification notification = null;
    private static NotificationChannel notificationChannel = null;
    public static int getRandomNumber() {
        return new Random().nextInt(100000);
    }
    /** PendingIntent to stop the service. */
    private static PendingIntent getStopServicePI(Service context) {
        PendingIntent piStopService;
        {
            Intent iStopService = new IntentBuilder(context).setCommand(Command.STOP).build();
            piStopService = PendingIntent.getService(context, getRandomNumber(), iStopService, 0);
        }
        return piStopService;
    }

    /** Get pending intent to launch the activity. */
    private static PendingIntent getStartIntent(Service context, JSONObject object) {
        PendingIntent piLaunchMainActivity;
        {
            Intent iLaunchMainActivity = new IntentBuilder(context).setCommand(Command.START).setJson(object).build();
            piLaunchMainActivity = PendingIntent.getActivity(context, getRandomNumber(), iLaunchMainActivity, 0);
        }
        return piLaunchMainActivity;
    }

    public static String CHANNEL_ID = String.valueOf(getRandomNumber());
    public static void createHeatingProcessNotify(Service context, JSONObject object){
        // Create Pending Intents.
        PendingIntent piStartIntent = getStartIntent(context, object);
        PendingIntent piStopService = getStopServicePI(context);

        // Action to stop the service.
        NotificationCompat.Action stopAction =
                new NotificationCompat.Action.Builder(
                        STOP_ACTION_ICON,
                        getNotificationStopActionText(context),
                        piStopService)
                        .build();

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        CharSequence channelName = "Heating process";
         notificationManager.getNotificationChannels().forEach(channel ->  {
            if (channel.getName().equals(channelName)){
                notificationChannel = channel;
            }});
        if ( notificationChannel == null ){
            notificationChannel = new NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(notificationChannel);
        }else{
            CHANNEL_ID = notificationChannel.getId();
        }
        notificationChannel.setSound(null, null);
        RemoteViews notificationLayout = new RemoteViews(context.getPackageName(), R.layout.process_notification_small);
        remoteView = new RemoteViews(context.getPackageName(), R.layout.process_notification_big);
        remoteView.setImageViewResource(R.id.probe, R.drawable.inside_product);
        remoteView.setImageViewResource(R.id.out_temp, R.drawable.celsius);
        // Create a notification.
                builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(SMALL_ICON)
                        .setContentIntent(piStartIntent)
                        .addAction(stopAction)
                        .setStyle(new NotificationCompat.BigTextStyle())
                        .setContent(remoteView)
                        .setCustomBigContentView(remoteView)
                        .setOnlyAlertOnce(true)
                        .setSound(null);
        notification = builder.build();
        context.startForeground(ONGOING_NOTIFICATION_ID, notification);
    }

    @NonNull
    private static String getNotificationStopActionText(Service context) {
        return context.getString(R.string.notification_stop_action_text);
    }

    public static void updateNotification(Service context, JSONObject object){
        try {
            remoteView.setTextViewText(R.id.probe_temp_value, String.format("%.2f", object.getDouble("currentProbeTemp")) + " \u2103");
            remoteView.setTextViewText(R.id.out_box_temp, String.format("%.2f", object.getDouble("currentOutTemp")) + " \u2103");
//            notificationChannel.setSound(null, null);
//            builder.setPriority(NotificationManager.IMPORTANCE_LOW).setSound(null);
        } catch (JSONException e) {
            Log.e(Notification.class.toString(), "JSON ESP: " , e);
        }
        context.startForeground(ONGOING_NOTIFICATION_ID, builder.build());
    }

    public static void cancel(Service context){
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

}
