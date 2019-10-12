package com.team33.meetingmate;

import android.util.Log;

import java.io.IOException;

/**
 * Asynchronously load the tasks.
 *
 * @author Yaniv Inbar
 */
class AsyncInsertEvent extends CommonAsyncTask {

    AsyncInsertEvent(CreateEventActivity createEventActivity) {
        super(createEventActivity);
    }

    @Override
    protected void doInBackground() throws IOException {
        String calendarId = "primary";
        activity.event = activity.service.events().insert(calendarId, activity.event).execute();
        Log.d("Calendar", activity.event.toString());
    }

    static void run(CreateEventActivity tasksSample) {
        new AsyncInsertEvent(tasksSample).execute();
    }
}