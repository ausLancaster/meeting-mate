package com.team33.meetingmate.ui.calendar;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.team33.meetingmate.R;
import com.team33.meetingmate.firebase.events.EventsFetcher;
import com.team33.meetingmate.firebase.events.IEventsFetcherCallback;

import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;

public class EventListFragment extends Fragment implements IEventsFetcherCallback {
    private final static String TAG = "EventListFragment";

    private int layout = R.layout.fragment_event_list;
    static String LAYOUT_TYPE = "type";
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (this.getArguments() != null)
            this.layout = getArguments().getInt(LAYOUT_TYPE);

        view = inflater.inflate(layout, container, false);

        ButterKnife.bind(this, view);

        EventsFetcher eventsFetcher = new EventsFetcher(this);
        eventsFetcher.getAllEvents();

        return view;
    }


    @Override
    public void onComplete(List<Map<String, Object>> calendarList) {
        CalendarAdapter adapter = new CalendarAdapter(getActivity(), R.layout.event_list_item, calendarList);
        ListView listView = view.findViewById(R.id.calendar_list_view);
        listView.setAdapter(adapter);
    }

    @Override
    public void onError(String error) {
        Log.d(TAG, "onError: " + error);
    }
}
