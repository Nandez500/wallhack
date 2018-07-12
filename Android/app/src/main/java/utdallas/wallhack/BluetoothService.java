package utdallas.wallhack;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import static android.content.ContentValues.TAG;
import static android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE;

public class BluetoothService {
    private static final String TAG = "BluetoothService";
    private static final String NAME = "BluetoothService";

    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;
    private InputStream inStream = null;
    private Handler mHandler = null;
    private CommunicationThread commThread;

    // Well known SPP UUID
    private static final UUID MY_UUID = UUID.fromString("88e2a5aa-1fb9-474a-a915-87457661899f");

    // Insert your server's MAC address
    private static String address = "B8:27:EB:B3:EF:90";

    public BluetoothService(Context context, Handler handler) {
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        mHandler = handler;
    }

    public synchronized void connect(BluetoothDevice device) {
        Log.d(TAG, "connect to: " + device);

        if(commThread != null) {
            commThread.cancel();
        }
        commThread = new CommunicationThread(device);

        commThread.start();
    }

    public void disconnect() {
        Log.d(TAG, "disconnecting...");

        if(commThread != null) {
            commThread.cancel();
        }
    }

    private class CommunicationThread extends Thread {
        private final BluetoothSocket btSocket;
        private final BluetoothDevice btDevice;
        private InputStream inStream;
        private OutputStream outStream;
        private boolean connected = false;

        public CommunicationThread(BluetoothDevice device) {
                btDevice = device;
                BluetoothSocket tmp = null;
                try {
                    tmp = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
                } catch (IOException e) {
                    Log.e(TAG, "Error creating Bluetooth socket");
                }
            btSocket = tmp;
        }

        public void run() {
            setName("CommunicationThread");
            Log.i(TAG, "Connecting");
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            byte[] buffer = new byte[1024];
            int bytes;

            btAdapter.cancelDiscovery();

            try {
                btSocket.connect();
            } catch (IOException e) {
                try {
                    btSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "Unable to close() socket during connection failure");
                }
                Log.e(TAG, "Connection Failed");
            }

            try {
                tmpIn = btSocket.getInputStream();
                tmpOut = btSocket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Socket In/Out Error" + e.getMessage() + ".");
            }

            inStream = tmpIn;
            outStream = tmpOut;
            connected = true;
            Log.i(TAG, "Connected");

            while(connected) {
                try {
                    bytes = inStream.read(buffer);
                    mHandler.obtainMessage(Constants.MESSAGE_TARGET, bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    Log.e(TAG, "Connection Lost");
                    connected = false;
                }
            }
        }

        public boolean isConnected() {
            return connected;
        }

        public void write(byte[] buffer) {
            if(connected) {
                try {
                    outStream.write(buffer);

                } catch (IOException e) {
                    Log.e(TAG, "Exception during write", e);
                }
            }
        }

        public void cancel() {
            connected = false;
            try {
                btSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of socket failed" + e.getMessage() + ".");
            }
        }
    }
}