package utdallas.wallhack;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Set;

public class BluetoothListActivity extends AppCompatActivity {
    protected ListView bluetoothListView;
    protected ArrayList<BluetoothDevice> bluetoothDevices;
    protected ArrayList<String> deviceList;
    private BluetoothAdapter BTAdapter;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                recordDeviceFound(device);
            } else {
                Log.d("DeviceFound", "Action Skipped");
            }
        }
    };
    private final BroadcastReceiver dReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Log.d("DeviceFound", "Discovery Started");
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_list);

        Intent intent = getIntent();
        bluetoothListView = findViewById(R.id.bluetoothListView);

        //register bluetooth discovery receiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);

        IntentFilter dFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        registerReceiver(dReceiver, dFilter);

        try {
            start_bluetooth();
        } catch(Exception e) {
            //todo fail gracefully
        }

        /*
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, deviceList);
        bluetoothListView.setAdapter(adapter);
        */
    }

    private void start_bluetooth() throws Exception {
        BTAdapter = BluetoothAdapter.getDefaultAdapter();

        //check for bluetooth capability
        if (BTAdapter == null) {
            throw new Exception("No bluetooth adapters"); //TODO this will not fail gracefully
        } else if (!BTAdapter.isEnabled()) {
            //enable bluetooth
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBtIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivityForResult(enableBtIntent, 1);
            //todo use onActivityResult to check for successful enable
            //throw new Exception("Bluetooth not enabled!");
        }

        //find and record paired devices
        Set<BluetoothDevice> pairedDevices = BTAdapter.getBondedDevices();
        bluetoothDevices = new ArrayList<>(3);
        deviceList = new ArrayList<>(3);
        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                recordDeviceFound(device);
            }
        }

        BTAdapter.startDiscovery();
    }

    private void recordDeviceFound(BluetoothDevice device) {
        String deviceStr = device.getName() + " " + device.getAddress();
        Log.d("DeviceFound", deviceStr);
        bluetoothDevices.add(device);
        deviceList.add(deviceStr);
    }

    protected void onDestroy(){
        super.onDestroy();

        unregisterReceiver(mReceiver);
    }
}
