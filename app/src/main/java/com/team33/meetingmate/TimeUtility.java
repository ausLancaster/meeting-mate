package com.team33.meetingmate;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtility {

    private static SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("d/M EEEE");


    public static String formatTime(Date date) {
        return timeFormat.format(date);
    }

    public static String formatDate(Date date) {
        return dateFormat.format(date);
    }

    public static String formatTimePassed(Date date) {
        Calendar eventDate = Calendar.getInstance();
        eventDate.setTime(date);
        java.util.Calendar yesterday = java.util.Calendar.getInstance();
        yesterday.add(java.util.Calendar.DATE, -1);
        Calendar today = Calendar.getInstance();
        if (eventDate.after(yesterday)) {
            // event less than a day old
            return timeFormat.format(date);
        } else {
            // event more than a day old
            int daysAgo = (int)( (today.getTimeInMillis() - eventDate.getTimeInMillis()) / (1000 * 60 * 60 * 24));
            return daysAgo + " days ago";
        }
    }

}
