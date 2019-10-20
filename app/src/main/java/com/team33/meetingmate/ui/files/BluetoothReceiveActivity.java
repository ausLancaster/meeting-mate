package com.team33.meetingmate.ui.files;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.team33.meetingmate.R;
import com.team33.meetingmate.bluetooth.BluetoothAcceptThread;

import static com.team33.meetingmate.MainActivity.TAG;

public class BluetoothReceiveActivity extends AppCompatActivity {

    private static ArrayAdapter<byte[]> receivedFilesArrayAdapter;

    public static Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_receive);

        handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                Bundle bundle = message.getData();
                byte[] receivedBytes = bundle.getByteArray("BLUETOOTH_RECEIVED_FILE");
                Log.d(TAG, "Got data of length " + receivedBytes.length);
                receivedFilesArrayAdapter.add(receivedBytes);
                receivedFilesArrayAdapter.notifyDataSetChanged();
            }
        };

        receivedFilesArrayAdapter = new ArrayAdapter<byte[]>(this, R.layout.simple_list_item_1, android.R.id.text1);

        ImageView imageView = (ImageView) findViewById(R.id.test_image);

        ListView listReceivedFiles = (ListView) findViewById(R.id.list_received_files);
        listReceivedFiles.setAdapter(receivedFilesArrayAdapter);
        listReceivedFiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                byte[] imageByteArray = (byte[]) parent.getAdapter().getItem(position);
                System.out.println(imageByteArray.length);

//                try {
//                    adapter.add((FileItem) new ObjectInputStream(new ByteArrayInputStream(imageByteArray)).readObject());
//                } catch (IOException e) {
//                    e.printStack
                try {
                    Bitmap bm = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
                    DisplayMetrics dm = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(dm);
                    imageView.setMinimumHeight(dm.heightPixels);
                    imageView.setMinimumWidth(dm.widthPixels);
                    imageView.setImageBitmap(bm);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Button activateReceiveButton = findViewById(R.id.button_activate_receive);
        activateReceiveButton.setOnClickListener(v -> {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                startActivity(discoverableIntent);
            }
            Log.d(TAG, "Bluetooth: Device discoverable");
            BluetoothAcceptThread bluetoothAcceptThread = new BluetoothAcceptThread();
            bluetoothAcceptThread.start();
        });

    }
}
