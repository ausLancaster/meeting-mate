package com.team33.meetingmate.ui.calendar;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.team33.meetingmate.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;

public class EventListFragment extends Fragment {

    private int layout = R.layout.fragment_event_list;
    static String LAYOUT_TYPE = "type";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (this.getArguments() != null)
            this.layout = getArguments().getInt(LAYOUT_TYPE);

        View view = inflater.inflate(layout, container, false);

        ButterKnife.bind(this, view);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().size() == 0) {
                            Log.d("Calendar", "Calendar is empty");
                        }
                        List<Map<String, Object>> calendarList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            calendarList.add(document.getData());
                        }
                        Collections.sort(
                                calendarList,
                                (Map<String, Object> e1, Map<String, Object> e2) ->
                                        Long.compare((long)e1.get("startDate"), (long)e2.get("startDate"))
                        );
                        if (calendarList.size() > 0) {
                            Map<String, Object> eventData = calendarList.get(0);
                            eventData.put("showDate", true);
                            Date prevDate = new Date((long) eventData.get("startDate"));
                            for (int i=1; i<calendarList.size(); i++) {
                                eventData = calendarList.get(i);
                                Date eventDate = new Date((long) eventData.get("startDate"));
                                if (eventDate.getYear() == prevDate.getYear() &&
                                        eventDate.getMonth() == prevDate.getMonth() &&
                                        eventDate.getDay() == prevDate.getDay()) {
                                    eventData.put("showDate", false);
                                } else {
                                    eventData.put("showDate", true);
                                }
                            }
                        }
                        CalendarAdapter adapter = new CalendarAdapter(getActivity(), R.layout.event_list_item, calendarList);
                        ListView listView = view.findViewById(R.id.calendar_list_view);
                        listView.setAdapter(adapter);
                    } else {
                        Log.d("Calendar", "Error getting documents: ", task.getException());
                    }
                });

        return view;
    }


}
