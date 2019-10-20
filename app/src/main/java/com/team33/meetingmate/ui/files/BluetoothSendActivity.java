package com.team33.meetingmate.ui.files;

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
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.team33.meetingmate.Constants;
import com.team33.meetingmate.R;
import com.team33.meetingmate.bluetooth.BluetoothConnectThread;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

public class BluetoothSendActivity extends AppCompatActivity {

    private final static String TAG = "BluetoothSendActivity";
    private final static int REQUEST_ENABLE_BT = 200;

    private Uri fileURI;
    private String fileName;
    private String fileExtension;
    private String fileType;
    private byte[] fileBytes;

    private ArrayAdapter<String> deviceArrayAdapter;
    private Button bluetoothButton;
    private Button shareButton;

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
        setContentView(R.layout.activity_bluetooth_send);

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

        // Register for broadcasts when a device is discovered.
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);

        bluetoothButton = (Button) findViewById(R.id.button_bluetooth);
        bluetoothButton.setOnClickListener(v -> {
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

        // Send via default bluetooth adapter
        shareButton = (Button) findViewById(R.id.button_share);
        shareButton.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_STREAM, fileURI);
            startActivity(intent);
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

                try {
                    InputStream is = getContentResolver().openInputStream(fileURI);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    byte[] b = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = is.read(b)) != -1) {
                        bos.write(b, 0, bytesRead);
                    }
                    byte[] bytes = bos.toByteArray();
                    System.out.println("READ FILE OF BYTE LENGTH " + bytes.length);

                    BluetoothConnectThread bluetoothConnectThread = new BluetoothConnectThread(device, bytes);
                    bluetoothConnectThread.start();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

}
