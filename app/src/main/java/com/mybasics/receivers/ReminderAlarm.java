package com.mybasics.receivers;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.mybasics.R;
import com.mybasics.models.Reminder;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class ReminderAlarm extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("title");
        LocalDateTime schedule = LocalDateTime.parse(intent.getStringExtra("schedule"));
        int id = intent.getIntExtra("id", -999);

        NotificationChannel channel;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "");
        builder.setSmallIcon(R.drawable.logo_transparent_48)
                .setContentTitle(title)
                .setContentText("You have something coming up.");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel("mybasics", context.getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH);
            builder.setChannelId(channel.getId());
            notificationManager.createNotificationChannel(channel);
        } else {
            builder.setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true);
        }
        notificationManager.notify(id, builder.build());

    }

    public static void setAlarm(Reminder reminder, Context context) {
        Intent intent = new Intent(context, ReminderAlarm.class);
        intent.putExtra("title", reminder.getTitle());
        intent.putExtra("schedule", reminder.getSchedule().toString());
        intent.putExtra("id", reminder.getId());
        PendingIntent pi = PendingIntent.getBroadcast(context, reminder.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
        LocalDateTime schedule = reminder.getSchedule();
        alarmManager.set(AlarmManager.RTC_WAKEUP, schedule.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(), pi);
        Log.d("ALARM SET", "" + true);
    }

    public static void cancelAlarm(Reminder reminder, Context context) {
        Intent intent = new Intent(context, ReminderAlarm.class);
        intent.putExtra("title", reminder.getTitle());
        intent.putExtra("schedule", reminder.getSchedule().toString());
        intent.putExtra("id", reminder.getId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, reminder.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        Log.d("Alarm Cancelled", "" + reminder.getId());
    }
}
