package com.team33.meetingmate.ui.calendar;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.team33.meetingmate.ui.notifications.NotificationsDeliver;

public class EventAlarmNearReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent nextIntent = new Intent().setClass(context, EventActivity.class);
        nextIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int id = intent.getIntExtra("id", 0);
        nextIntent.putExtra("id", (long) id);
        nextIntent.setAction("nearReceiverAction");
        PendingIntent nextPendingIntent = PendingIntent.getActivity(context, 1022, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        String text = "Your " + intent.getStringExtra("Name") + " is starting soon. Please read your documents.";

        NotificationsDeliver.getInstance().sendNotification(context, "Meeting Mate", text, nextPendingIntent);
    }
}
