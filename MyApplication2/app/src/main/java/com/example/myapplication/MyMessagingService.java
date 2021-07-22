package com.example.myapplication;

import android.app.AlarmManager;
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
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

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

            if (body.contains("reminder")) {
                Intent ii = new Intent(this, Background.class);
                ii.putExtra("time", body.contains("10") ? "10 min": body.contains("15") ? "15 min" : "30 min");
                //long time = System.currentTimeMillis();
                int delay = body.contains("10") ? 10 : body.contains("15") ? 15 : 30;
                long schedule = checkTime(title, delay);
                if (schedule != 0) {
                    AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
                    PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), 0, ii, 0);
                    alarm.set(AlarmManager.RTC_WAKEUP, schedule, pi);
                }
            } else {
                mgrcompat.notify(0, b.build());
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private long checkTime(String deadline, int delay) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate localDate = LocalDate.now();
        String now = dtf.format(localDate);

        long sendTime = 0;

        DateTimeFormatter tf = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime localTime = LocalTime.now();

        //String localTimeString = tf.format(localTime).substring(0, 5);
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try {
            //Date current = df.parse(now + " "+localTimeString);
            Date taskDeadline = df.parse(deadline);
            sendTime = taskDeadline.getTime() - delay * 60000;
            } catch (ParseException e) {
            e.printStackTrace();
        }
        return sendTime;
    }
}