package com.team33.meetingmate;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import java.util.Objects;
import java.util.Random;

public class NotificationsDeliver {

    private final static String CHANNEL_ID = "10000";
    private final static String CHANNEL_NAME = "meeting mate notifications channel";
    private final static String CHANNEL_DESCRIPTION = "meeting mate notifications channel";


    private static NotificationsDeliver instance;

    private NotificationsDeliver() {
    }

    public static NotificationsDeliver getInstance() {
        if (instance == null) {
            instance = new NotificationsDeliver();
        }
        return instance;
    }

    public void createNotificationChannel(@NonNull Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            channel.setDescription(CHANNEL_DESCRIPTION);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = ContextCompat.getSystemService(context, NotificationManager.class);
            Objects.requireNonNull(notificationManager).createNotificationChannel(channel);
        }
    }

    public void sendNotification(@NonNull Context context, String title, String text ) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_event_note_white_24dp)
                .setContentTitle(title)
                .setContentText(text)
//                .setStyle(new NotificationCompat.BigTextStyle()
//                        .bigText("Much longer text that cannot fit one line..."))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // notificationId is a unique int for each notification that you must define
        int randomId = new Random().nextInt();
        notificationManager.notify(randomId, builder.build());
    }
}
