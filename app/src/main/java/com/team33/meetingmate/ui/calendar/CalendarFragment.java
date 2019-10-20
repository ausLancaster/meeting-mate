package com.team33.meetingmate.ui.calendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.team33.meetingmate.R;

public class CalendarFragment extends Fragment {

    private CalendarViewModel calendarViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        calendarViewModel =
                ViewModelProviders.of(this).get(CalendarViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_calendar, container, false);

        Button b = root.findViewById(R.id.button);


        return root;
    }
}