package com.team33.meetingmate.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.team33.meetingmate.AppActivity;
import com.team33.meetingmate.R;

public class SettingsFragment extends Fragment {

    public static boolean syncCalendar = true;

    private SettingsViewModel settingsViewModel;
    private AppActivity activity;
    private View view;

    private TextView textTime;
    private TextView textLocation;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        settingsViewModel =
                ViewModelProviders.of(this).get(SettingsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        final TextView textView = root.findViewById(R.id.text_settings);
        settingsViewModel.getText().observe(this, s -> textView.setText(s));
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        view = getView();
        activity = ((AppActivity) getActivity());

        Switch syncONOFF = view.findViewById(R.id.switch_calendar);
        syncONOFF.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                syncCalendar = isChecked;
            }
        });

        textTime = view.findViewById(R.id.text_time);
        textTime.setText(activity.getTime() == null ? "" : activity.getTime().toString());

        textLocation = view.findViewById(R.id.text_location);
        textLocation.setText(activity.getLocation() == null ? "" : "Lat: " + Double.toString(activity.getLocation().getLatitude()) + ", Lng: " + Double.toString(activity.getLocation().getLongitude()));
    }

    @Override
    public void onResume() {
        super.onResume();

        textTime.setText(activity.getTime() == null ? "" : activity.getTime().toString());
        textLocation.setText(activity.getLocation() == null ? "" : "Lat: " + Double.toString(activity.getLocation().getLatitude()) + ", Lng: " + Double.toString(activity.getLocation().getLongitude()));
    }

    @Override
    public void onPause() {
        super.onPause();
    }

}