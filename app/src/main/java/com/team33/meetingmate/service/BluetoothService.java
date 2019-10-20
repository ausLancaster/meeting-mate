package com.team33.meetingmate.service;

import android.bluetooth.BluetoothSocket;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.team33.meetingmate.AppActivity;
import com.team33.meetingmate.ui.files.FileUploadActivity;
import com.team33.meetingmate.ui.files.FilesFragment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BluetoothService {
    private static final String TAG = "MY_APP_DEBUG_TAG";
    private Handler handler; // handler that gets info from Bluetooth service
    private ConnectedThread connectedThread;

    // Defines several constants used when transmitting messages between the service and the UI.
    private interface MessageConstants {
        public static final int MESSAGE_READ = 0;
        public static final int MESSAGE_WRITE = 1;
        public static final int MESSAGE_TOAST = 2;

        // ... (Add other message types here as needed.)
    }

    BluetoothService(BluetoothSocket socket) {
        connectedThread = new ConnectedThread(socket);
        handler = AppActivity.handler;
    }

    public void run() {
        connectedThread.run();
    }

    public void write(byte[] bytes) {
        connectedThread.write(bytes);
    }

    public void cancel() {
        connectedThread.cancel();
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private byte[] mmBuffer; // mmBuffer store for the stream

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams; using temp objects because
            // member streams are final.
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating input stream", e);
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating output stream", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
            Log.d(TAG, "INS: "+mmInStream.toString());
            Log.d(TAG, "OUTS: "+mmOutStream.toString());
        }

        public void run() {
            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
            mmBuffer = new byte[1024];
            int numBytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                try {
                    // Read from the InputStream.
                    numBytes = mmInStream.read(mmBuffer);
                    byteBuffer.write(mmBuffer, 0, numBytes);
                    System.out.println(numBytes);
                    if (numBytes < 0) {
                        break;
                    }
                } catch (IOException e) {
                    Log.d(TAG, "Input stream was disconnected", e);
                    break;
                }
            }
            // Send the obtained bytes to AppActivity.
            Bundle dataBundle = new Bundle();
            dataBundle.putByteArray("BLUETOOTH_RECEIVED_FILE", byteBuffer.toByteArray());
            Message message = handler.obtainMessage();
            message.setData(dataBundle);
            handler.sendMessage(message);
            try {
                byteBuffer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Call this from the main activity to send data to the remote device.
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);

                // Share the sent message with the UI activity.
//                Message writtenMsg = handler.obtainMessage(MessageConstants.MESSAGE_WRITE, -1, -1, mmBuffer);
//                writtenMsg.sendToTarget();
                mmOutStream.flush();
                mmOutStream.close();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when sending data", e);

                // Send a failure message back to the activity.
                Message writeErrorMsg =
                        handler.obtainMessage(MessageConstants.MESSAGE_TOAST);
                Bundle bundle = new Bundle();
                bundle.putString("toast",
                        "Couldn't send data to the other device");
                writeErrorMsg.setData(bundle);
                handler.sendMessage(writeErrorMsg);
            }
        }

        // Call this method from the main activity to shut down the connection.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }
}