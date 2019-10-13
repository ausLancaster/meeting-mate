package com.team33.meetingmate;

import java.text.SimpleDateFormat;
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
}
