package com.team33.meetingmate.ui.files;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.team33.meetingmate.AcceptThread;
import com.team33.meetingmate.ConnectThread;
import com.team33.meetingmate.Constants;
import com.team33.meetingmate.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FileUploadActivity extends AppCompatActivity {
    private final static String TAG = "FileUploadActivity";


    private Uri fileURI;
    private String fileName;
    private String fileExtension;
    private String fileType;
    private byte[] fileBytes;

    private RadioGroup radioGroup;
    private Button bluetooth;
    private Button upload;
    private TextView errorMessage;

    private StorageReference mStorage;

    private ArrayAdapter<String> deviceArrayAdapter;
    private final Integer REQUEST_ENABLE_BT = 1;

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                deviceArrayAdapter.add(deviceName + "," + deviceHardwareAddress);
                Log.d(TAG, "Bluetooth: Device {" + deviceName + ", " + deviceHardwareAddress + "}");
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                // Discovery started
                Log.d(TAG, "Bluetooth: Discovery started");
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                // Discovery finished
                Log.d(TAG, "Bluetooth: Discovery finished");
            } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "Bluetooth: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "Bluetooth: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "Bluetooth: STATE ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "Bluetooth: STATE TURNING ON");
                        break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_upload);

        bluetooth = findViewById(R.id.button_bluetooth);
        upload = findViewById(R.id.button_upload);
        errorMessage = findViewById(R.id.error_message);
        radioGroup = findViewById(R.id.events_list);
        fetchEvents();


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            fileURI = (Uri) extras.get(Constants.EXTRAS_FILE_URL);
            fileName = (String) extras.get(Constants.EXTRAS_FILE_NAME);
            fileExtension = (String) extras.get(Constants.EXTRAS_FILE_EXTENSION);

            Object fileTypeObject = extras.get(Constants.ExtrasFileType);
            fileType = fileTypeObject == null ? "" : (String) fileTypeObject;

            Object imageDataObject = extras.get(Constants.EXTRAS_IMAGE_DATA);
            fileBytes = imageDataObject == null ? null : (byte[]) imageDataObject;
        }

        // Firebase storage
        mStorage = FirebaseStorage.getInstance().getReference();

        upload.setOnClickListener(v -> {
            uploadFile(radioGroup.getCheckedRadioButtonId());
            finish();
        });

        // Register for broadcasts when a device is discovered.
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);

//        // Enable discoverability
//        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
//        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
//        startActivity(discoverableIntent);

        bluetooth.setOnClickListener(v -> {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            // Check if device supports Bluetooth
            if (bluetoothAdapter != null) {
                // Enable bluetooth
                if (!bluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
                // Query paired devices
                Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

                if (pairedDevices.size() > 0) {
                    // There are paired devices. Get the name and address of each paired device.
                    for (BluetoothDevice device : pairedDevices) {
                        String deviceName = device.getName();
                        String deviceHardwareAddress = device.getAddress(); // MAC address
                    }
                }

                // Start discovery
                bluetoothAdapter.startDiscovery();
            } else {
                Toast.makeText(getBaseContext(), "Device does not support Bluetooth", Toast.LENGTH_SHORT).show();
            }
        });

        // Bluetooh connectivity
        deviceArrayAdapter = new ArrayAdapter<String>(this, R.layout.simple_list_item_1, android.R.id.text1);
        ListView listDevices = (ListView) findViewById(R.id.list_devices);
        listDevices.setAdapter(deviceArrayAdapter);
        listDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = ((String) parent.getAdapter().getItem(position)).split(",")[0];
                String address = ((String) parent.getAdapter().getItem(position)).split(",")[1];
                Log.d(TAG, "Bluetooth: Attempting to connect to " + name);
                BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address);
                ConnectThread connectThread = new ConnectThread(device, fileBytes);
                connectThread.run();
            }
        });

//        // Send via default bluetooth adapter
//        bluetooth.setOnClickListener(v -> {
//            Intent intent = new Intent();
//            intent.setAction(Intent.ACTION_SEND);
//            intent.setType("text/plain");
//            intent.putExtra(Intent.EXTRA_STREAM, fileURI);
//            startActivity(intent);
//        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private void uploadFile(int eventID) {
        String uploadFileName = fileName;
        if (fileExtension != null && !fileExtension.isEmpty()) {
            uploadFileName = fileName + "." + fileExtension;
        }

        StorageReference mountainsRef = mStorage
                .child(Constants.ATTACHMENT_REF)
                .child(Integer.toString(eventID))
                .child(uploadFileName);

        UploadTask uploadTask;
        if (fileType != null && fileType.equals(Constants.IMAGE_FILE_TYPE) && fileBytes != null) {
            uploadTask = mountainsRef.putBytes(fileBytes);
        } else {
            uploadTask = mountainsRef.putFile(fileURI);
        }

        uploadTask.addOnFailureListener(exception -> {
            Log.d(TAG, "onFailure: " + exception.getMessage());
            Toast.makeText(
                    FileUploadActivity.this,
                    "Failed to attached file.",
                    Toast.LENGTH_LONG).show();
        }).addOnSuccessListener(taskSnapshot ->
                Toast.makeText(
                        FileUploadActivity.this,
                        "Successfully attached file.",
                        Toast.LENGTH_LONG).show());
    }

    @SuppressLint("SetTextI18n")
    private void fetchEvents() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().size() == 0) {
                            Log.d("Calendar", "Calendar is empty");
                            errorMessage.setText("Calendar is empty");
                        }
                        List<Map<String, Object>> eventsList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            eventsList.add(document.getData());
                            Log.d("Calendar", document.getId() + " => " + document.getData());
                        }
                        Collections.sort(
                                eventsList,
                                (Map<String, Object> e1, Map<String, Object> e2) ->
                                        Long.compare((long) e1.get("startDate"), (long) e2.get("startDate"))
                        );

                        radioGroup.setOrientation(LinearLayout.VERTICAL);

                        for (int i = eventsList.size() - 1; i >= 0; i--) {
                            Map<String, Object> event = eventsList.get(i);

                            RadioButton radioButton = new RadioButton(FileUploadActivity.this);
                            long id = (long) event.get("id");
                            radioButton.setText((String) event.get("summary"));
                            radioButton.setId((int) id);
                            radioButton.setChecked(i == eventsList.size() - 1);

                            radioGroup.addView(radioButton);
                            upload.setEnabled(true);
                            bluetooth.setEnabled(true);
                        }
                    } else {
                        Log.d("Calendar", "Error getting documents: ", task.getException());
                        errorMessage.setText("Error while fetching events.");
                    }
                });
    }
}