package com.team33.meetingmate;

/*
 * Copyright (c) 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;

/**
 * Asynchronously load the tasks.
 *
 * @author Yaniv Inbar
 */
class AsyncLoadEvents extends CommonAsyncTask {

    AsyncLoadEvents(CreateEventActivity createEventActivity) {
        super(createEventActivity);
    }

    @Override
    protected void doInBackground() throws IOException {
        DateTime now = new DateTime(System.currentTimeMillis());
        Events events = client.events().list("primary")
                .setMaxResults(10)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        activity.items = events.getItems();
    }

    static void run(CreateEventActivity tasksSample) {
        new AsyncLoadEvents(tasksSample).execute();
    }
}