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

    // Create a BroadcastReceiver for ACTION_FOUND
//    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            // When discovery finds a device
//            if (action.equals(activity.getBluetoothAdapter().ACTION_STATE_CHANGED)) {
//                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, activity.getBluetoothAdapter().ERROR);
//
//                switch(state){
//                    case BluetoothAdapter.STATE_OFF:
//                        Log.d(TAG, "onReceive: STATE OFF");
//                        break;
//                    case BluetoothAdapter.STATE_TURNING_OFF:
//                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING OFF");
//                        break;
//                    case BluetoothAdapter.STATE_ON:
//                        Log.d(TAG, "mBroadcastReceiver1: STATE ON");
//                        break;
//                    case BluetoothAdapter.STATE_TURNING_ON:
//                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING ON");
//                        break;
//                }
//            }
//        }
//    };

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

        View v = getView();
        activity = ((AppActivity) getActivity());

        ListView listDevices = (ListView) v.findViewById(R.id.list_devices);

        Button btnONOFF = (Button) v.findViewById(R.id.btnONOFF);
        btnONOFF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: enabling/disabling bluetooth.");
//                enableDisableBT();
                scanForDevices();
            }
        });

        TextView textTime = (TextView) v.findViewById(R.id.text_time);
        textTime.setText(activity.getTime() == null ? "" : activity.getTime().toString());

        TextView textLocation = (TextView) v.findViewById(R.id.text_location);
        textLocation.setText(activity.getLocation() == null ? "" : activity.getLocation().toString());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        LocalBroadcastManager.getInstance(activity).unregisterReceiver(mBroadcastReceiver1);
        LocalBroadcastManager.getInstance(activity).unregisterReceiver(activity.getBluetoothBroadcastReceiver());
    }

    @Override
    public void onPause() {
        super.onPause();
//        LocalBroadcastManager.getInstance(activity).unregisterReceiver(mBroadcastReceiver1);
        LocalBroadcastManager.getInstance(activity).unregisterReceiver(activity.getBluetoothBroadcastReceiver());
    }

    @Override
    public void onResume() {
        super.onResume();
//        IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
//        LocalBroadcastManager.getInstance(activity).registerReceiver(mBroadcastReceiver1, BTIntent);
    }

//    public void enableDisableBT(){
//        if(activity.getBluetoothAdapter() == null){
//            Log.d(TAG, "enableDisableBT: Does not have BT capabilities.");
//        }
//        if(!activity.getBluetoothAdapter().isEnabled()){
//            Log.d(TAG, "enableDisableBT: enabling BT.");
//            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivity(enableBTIntent);
//
//            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
//            LocalBroadcastManager.getInstance(activity).registerReceiver(mBroadcastReceiver1, BTIntent);
//        }
//        if(activity.getBluetoothAdapter().isEnabled()){
//            Log.d(TAG, "enableDisableBT: disabling BT.");
//            activity.getBluetoothAdapter().disable();
//
//            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
//            activity.registerReceiver(mBroadcastReceiver1, BTIntent);
//        }
//    }

    public void scanForDevices() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        activity.registerReceiver(activity.getBluetoothBroadcastReceiver(), filter);
    }

}