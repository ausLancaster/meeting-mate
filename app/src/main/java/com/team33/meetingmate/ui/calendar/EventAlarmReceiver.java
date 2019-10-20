package com.team33.meetingmate.ui.calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;

import androidx.core.content.ContextCompat;

import com.team33.meetingmate.NotificationsDeliver;

public class EventAlarmReceiver extends BroadcastReceiver {

    private final float MAX_DISTANCE = 0.05f;

    @Override
    public void onReceive(Context context, Intent intent) {
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            setupEventReminder(context, intent);
            return;
        }

        double eventLatitude = intent.getDoubleExtra("Latitude", 0);
        double eventLongitude = intent.getDoubleExtra("Longitude", 0);
        if (eventLatitude == 0 && eventLongitude == 0) {
            setupEventReminder(context, intent);
            return;
        }
        LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();

        // Check whether user is already at location
        if (Math.abs(eventLatitude - latitude) < MAX_DISTANCE && Math.abs(eventLongitude - longitude) < MAX_DISTANCE) {
            setupEventReminder(context, intent);
        } else {
            sendTravelReminder(context, intent);
        }
    }

    private void sendTravelReminder(Context context, Intent intent) {

        Intent nextIntent = new Intent(context, EventActivity.class);
        int id = intent.getIntExtra("id", 0);
        nextIntent.putExtra("id", (long)id);
        nextIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        nextIntent.setAction("travelReminder");
        PendingIntent nextPendingIntent = PendingIntent.getActivity(context, 1011, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        String text = "Time to leave. Your " + intent.getStringExtra("Name") + " is starting soon.";
        NotificationsDeliver.getInstance().sendNotification(context, "Meeting Mate", text, nextPendingIntent);
    }

    private void setupEventReminder(Context context, Intent intent) {
        Intent alarmIntent = new Intent(context.getApplicationContext(), EventAlarmNearReceiver.class);
        alarmIntent.putExtra("Name", intent.getStringExtra("Name"));
        int id = intent.getIntExtra("id", 0);
        alarmIntent.putExtra("id", id);
        alarmIntent.setAction("receiverAction");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 2345, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        java.util.Calendar cal = java.util.Calendar.getInstance();
        //cal.add(java.util.Calendar.MINUTE, 1);
        cal.add(java.util.Calendar.SECOND, 3);

        manager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
    }

}