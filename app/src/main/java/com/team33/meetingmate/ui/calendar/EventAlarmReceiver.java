package com.team33.meetingmate.ui.calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.team33.meetingmate.NotificationsDeliver;

public class EventAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Notifications", "alarm");
        String text = "Your " + intent.getStringExtra("Event Name") + " is starting soon. Please read your documents";
        NotificationsDeliver.getInstance().sendNotification(context, "Meeting Mate", text);
    }
}