package com.team33.meetingmate.firebase.events;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class EventsFetcher {
    private final static String TAG = "EventsFetcher";

    private IEventsFetcherCallback callback;

    public EventsFetcher(IEventsFetcherCallback callback) {
        this.callback = callback;
    }

    public void getAllEvents() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().size() == 0) {
                            Log.d(TAG, "Calendar is empty");
                        }
                        List<Map<String, Object>> calendarList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            calendarList.add(document.getData());
                        }
                        Collections.sort(
                                calendarList,
                                (Map<String, Object> e1, Map<String, Object> e2) ->
                                        Long.compare((long) e1.get("startDate"), (long) e2.get("startDate"))
                        );
                        this.callback.onComplete(calendarList);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                        this.callback.onError(task.getException().getMessage());
                    }
                });
    }
}
