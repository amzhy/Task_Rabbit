package com.example.myapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyMessagingService extends FirebaseMessagingService {

    public MyMessagingService() { }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("REMOTE", remoteMessage.getFrom());
        sendNotification(remoteMessage.getData().get("message"), remoteMessage.getData().get("title"));
    }

    private void sendNotification(String body, String title) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("title", title != null ? title : "error title");

            NotificationChannel channel = new NotificationChannel("n", "n", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager mgr = getSystemService(NotificationManager.class);
            mgr.createNotificationChannel(channel);

            Intent resultIntent = new Intent(this, MainActivity.class);
            resultIntent.putExtra("notify", title.contains("message") ? "inbox" : "task");

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addNextIntentWithParentStack(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder b = new NotificationCompat.Builder(getApplicationContext(), "n")
                    .setContentTitle(title)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setAutoCancel(true).setContentText(body).setContentIntent(resultPendingIntent);

            NotificationManagerCompat mgrcompat = NotificationManagerCompat.from(getApplicationContext());
            mgrcompat.notify(0, b.build());
        }
    }
}