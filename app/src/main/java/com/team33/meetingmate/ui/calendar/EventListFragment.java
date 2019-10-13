package com.team33.meetingmate.ui.calendar;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.team33.meetingmate.R;

import java.util.ArrayList;
import java.util.Collections;
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
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().size() == 0) {
                                Log.d("Calendar", "Calendar is empty");
                            }
                            List<Map<String, Object>> calendarList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                calendarList.add(document.getData());
                                Log.d("Calendar", document.getId() + " => " + document.getData());
                            }
                            Collections.sort(
                                    calendarList,
                                    (Map<String, Object> e1, Map<String, Object> e2) ->
                                            Long.compare((long)e1.get("startDate"), (long)e2.get("startDate"))
                            );
                            CalendarAdapter adapter = new CalendarAdapter(getActivity(), R.layout.event_list_item, calendarList);
                            ListView listView = view.findViewById(R.id.calendar_list_view);
                            listView.setAdapter(adapter);
                        } else {
                            Log.d("Calendar", "Error getting documents: ", task.getException());
                        }
                    }
                });

        return view;
    }


}
