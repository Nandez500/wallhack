package utdallas.wallhack;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.ArrayList;

import static android.support.v4.app.ActivityCompat.startActivityForResult;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "utdallas.wallhack.MESSAGE";
    private static final int REQUEST_GET_BLUETOOTH_DEVICE = 1;
    private ArrayList<BluetoothDevice> bluetoothDevices;
    ImageView imageView;
    private BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void startBluetooth (View view) {
        Intent bluetoothIntent = new Intent(this, BluetoothListActivity.class);
        startActivityForResult(bluetoothIntent, REQUEST_GET_BLUETOOTH_DEVICE);
    }
    public void takePicture(View view){
        Intent pictureIntent = new Intent(this, PictureActivity.class);
        startActivity(pictureIntent);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_GET_BLUETOOTH_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;
        }
    }

    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
                .getString(BluetoothListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        Toast.makeText(this, "Address:"+address, Toast.LENGTH_SHORT).show();
    }
}
