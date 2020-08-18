package com.slothychemdoksloth.attendancetracker;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.core.app.NotificationCompat;

public class Notification_Receiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
       /* NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent repeating_intent = new Intent(context, Main_Page_Activity.class);
        repeating_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 100, repeating_intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentIntent(pendingIntent)
                .setSmallIcon(android.R.drawable.arrow_up_float)
                .setContentTitle("Attendance Tracker")
                .setContentText("Reminder to Update Attendance, Ignore if Already Done")
                .setAutoCancel(true);

        notificationManager.notify(100,builder.build());*/
                Intent service1 = new Intent(context, Notification_Helper.class);
                service1.setData((Uri.parse("custom://"+System.currentTimeMillis())));
                context.startService(service1);

    }
}
