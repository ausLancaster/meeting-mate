package com.team33.meetingmate.ui.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.team33.meetingmate.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class NotificationsDeliver {

    private final static String CHANNEL_ID = "10000";
    private final static String CHANNEL_NAME = "meeting mate notifications channel";
    private final static String CHANNEL_DESCRIPTION = "meeting mate notifications channel";

    public static final int MINUTES_NOTIFY_BEFORE_EVENT = 15;

    public static final int MINUTES_NOTIFY_BEFORE_MOVE = 45;

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

    public void sendNotification(@NonNull Context context, String title, String text) {
        sendNotification(context, title, text, null);
    }

        public void sendNotification(@NonNull Context context, String title, String text, PendingIntent pendingIntent ) {

        // Send notification to phone

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_event_note_white_24dp)
                .setContentTitle(title)
                .setContentText(text)
//                .setStyle(new NotificationCompat.BigTextStyle()
//                        .bigText("Much longer text that cannot fit one line..."))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(new long[] { 1000, 1000, 1000 })
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify(999, builder.build());

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> notificationData = new HashMap<>();
            notificationData.put("text", text);
            notificationData.put("date", Calendar.getInstance().getTimeInMillis());
            notificationData.put("seen", false);

        db.collection("notifications")
                .document(Integer.toString(notificationData.hashCode()))
                .set(notificationData);


    }
}
