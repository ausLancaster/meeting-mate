package com.team33.meetingmate.ui.files;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
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

import com.team33.meetingmate.AcceptThread;
import com.team33.meetingmate.AppActivity;
import com.team33.meetingmate.R;

import static com.team33.meetingmate.MainActivity.TAG;

public class FilesFragment extends Fragment {

    private FilesViewModel filesViewModel;
    private View view;
    private AppActivity activity;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        filesViewModel =
                ViewModelProviders.of(this).get(FilesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_files, container, false);
        final TextView textView = root.findViewById(R.id.text_files);
        filesViewModel.getText().observe(this, new Observer<String>() {
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

        Button receive = view.findViewById(R.id.button_receive);
        receive.setOnClickListener(v -> {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                startActivity(discoverableIntent);
            }
            Log.d(TAG, "Bluetooth: Device discoverable");
            AcceptThread acceptThread = new AcceptThread();
            acceptThread.start();
        });
    }
}