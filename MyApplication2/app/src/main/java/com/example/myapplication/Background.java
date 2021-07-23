package com.example.myapplication;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class Background extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getStringExtra("time") != null) {
            String delay = intent.getStringExtra("time");
            NotificationCompat.Builder b = new NotificationCompat.Builder(context, "n")
                    .setContentTitle("Deadline approaching!")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setOnlyAlertOnce(true)
                    .setAutoCancel(true).setContentText("Your assigned task is expiring in " + delay);

            Intent resultIntent = new Intent(context, MainActivity.class);
            resultIntent.putExtra("notify", "task");
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addNextIntentWithParentStack(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            b.setContentIntent(resultPendingIntent);
            NotificationManagerCompat mgrcompat = NotificationManagerCompat.from(context);
            mgrcompat.notify(0, b.build());
        }
    }
}
