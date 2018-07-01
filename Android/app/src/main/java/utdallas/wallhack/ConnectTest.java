package utdallas.wallhack;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
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

public class ConnectTest extends Activity {
    TextView out;
    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;
    private InputStream inStream = null;
    private CommunicationThread commThread;

    // Well known SPP UUID
    private static final UUID MY_UUID = UUID.fromString("88e2a5aa-1fb9-474a-a915-87457661899f");

    // Insert your server's MAC address
    private static String address = "B8:27:EB:B3:EF:90";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        out = (TextView) findViewById(R.id.out);

        out.append("\n...In onCreate()...");

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        CheckBTState();
    }

    @Override
    public void onStart() {
        super.onStart();
        out.append("\n...In onStart()...");
    }

    @Override
    public void onResume() {
        super.onResume();

        out.append("\n...In onResume...\n...Attempting client connect...");

        // Set up a pointer to the remote node using it's address.
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        commThread = new CommunicationThread(device);

        commThread.start();
    }

    @Override
    public void onPause() {
        super.onPause();

        out.append("\n...In onPause()...");

        /*if (outStream != null) {
            try {
                outStream.flush();
            } catch (IOException e) {
                AlertBox("Fatal Error", "In onPause() and failed to flush output stream: " + e.getMessage() + ".");
            }
        }

        try {
            btSocket.close();
        } catch (IOException e2) {
            AlertBox("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
        }*/
    }

    @Override
    public void onStop() {
        super.onStop();
        out.append("\n...In onStop()...");
        commThread.cancel();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        out.append("\n...In onDestroy()...");
    }

    private void CheckBTState() {
        // Check for Bluetooth support and then check to make sure it is turned on

        // Emulator doesn't support Bluetooth and will return null
        if (btAdapter == null) {
            AlertBox("Fatal Error", "Bluetooth not supported. Aborting.");
        } else {
            if (btAdapter.isEnabled()) {
                out.append("\n...Bluetooth is enabled...");
            } else {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(btAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }

    public void AlertBox(String title, String message) {
        Log.e(TAG, message);
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message + " Press OK to exit.")
                .setPositiveButton("OK", new OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        finish();
                    }
                }).show();
    }

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    //out.append(readMessage);
                    String[] splitMessage = readMessage.split(",");
                    WallData target = new WallData(
                            splitMessage[0], Double.valueOf(splitMessage[1]),
                            Double.valueOf(splitMessage[2]), Double.valueOf(splitMessage[3]),
                            Double.valueOf(splitMessage[4]));
                    out.append(target.toString());
                    Log.i(TAG, "Message Received");
                    break;
            }
        }
    };

    private class CommunicationThread extends Thread {
        private final BluetoothSocket btSocket;
        private final BluetoothDevice btDevice;
        private InputStream inStream;
        private OutputStream outStream;
        private boolean isConnected = false;

        public CommunicationThread(BluetoothDevice device) {
            btDevice = device;
            BluetoothSocket tmp = null;
            try {
                tmp = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                AlertBox("Fatal Error", "In CommunicationThread and failed to create a Bluetooth Socket" + e.getMessage() + ".");
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
                    AlertBox("Fatal Error", "unable to close() socket during connection failure" + e2.getMessage() + ".");
                }
                AlertBox("Fatal Error", "Connection Failed" + e.getMessage() + ".");
            }

            try {
                tmpIn = btSocket.getInputStream();
                tmpOut = btSocket.getOutputStream();
            } catch (IOException e) {
                AlertBox("Fatal Error", "Socket In/Out Error" + e.getMessage() + ".");
            }

            inStream = tmpIn;
            outStream = tmpOut;
            isConnected = true;
            Log.i(TAG, "Connected");

            while(isConnected) {
                try {
                    bytes = inStream.read(buffer);
                    mHandler.obtainMessage(0, bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    Log.e(TAG, "Connection Lost");
                    isConnected = false;
                }
            }
        }

        public void cancel() {
            isConnected = false;
            try {
                btSocket.close();
            } catch (IOException e) {
                AlertBox("Fatal Error", "close() of socket failed" + e.getMessage() + ".");
            }
        }
    }
}