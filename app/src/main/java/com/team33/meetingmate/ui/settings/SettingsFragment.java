package com.team33.meetingmate.ui.settings;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.team33.meetingmate.AppActivity;
import com.team33.meetingmate.R;

import static android.content.ContentValues.TAG;

public class SettingsFragment extends Fragment {

    private static final String TAG = "AppActivity";

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
        settingsViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        view = getView();
        activity = ((AppActivity) getActivity());

//        ListView listDevices = (ListView) v.findViewById(R.id.list_devices);
//        listDevices.setAdapter(activity.getBluetoothArrayAdapter());

//        Button btnONOFF = (Button) v.findViewById(R.id.btnONOFF);
//        btnONOFF.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.d(TAG, "onClick: enabling/disabling bluetooth.");
//                scanForDevices();
//            }
//        });

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
//        textLocation.setText(activity.getLocation() == null ? "" : activity.getLocation().toString());

//        IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
//        LocalBroadcastManager.getInstance(activity).registerReceiver(activity.getBluetoothBroadcastReceiver(), BTIntent);
//        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//        LocalBroadcastManager.getInstance(activity).registerReceiver(activity.getBluetoothBroadcastReceiver(), filter);
    }

    @Override
    public void onPause() {
        super.onPause();
//        LocalBroadcastManager.getInstance(activity).unregisterReceiver(activity.getBluetoothBroadcastReceiver());
    }

//    public void scanForDevices() {
//        if (activity.getBluetoothAdapter() == null) {
//            Log.d(TAG, "Does not have BT capabilities.");
//        } else {
//            if (!activity.getBluetoothAdapter().isEnabled()) {
//                Log.d(TAG, "Enabling BT.");
//                Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                startActivity(enableBTIntent);
//
//                IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
//                LocalBroadcastManager.getInstance(activity).registerReceiver(activity.getBluetoothBroadcastReceiver(), BTIntent);
//            }
//            Log.d(TAG, "Scanning for devices");
//
//            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//            LocalBroadcastManager.getInstance(activity).registerReceiver(activity.getBluetoothBroadcastReceiver(), filter);
//        }
//    }

}