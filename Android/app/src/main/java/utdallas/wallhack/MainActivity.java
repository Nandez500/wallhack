package utdallas.wallhack;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;

import static android.support.v4.app.ActivityCompat.startActivityForResult;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "utdallas.wallhack.MESSAGE";
    private ArrayList<BluetoothDevice> bluetoothDevices;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }



    public void sendMessage(View view){
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.editText);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void startBluetooth (View view) {
        Intent intent = new Intent(this, BluetoothListActivity.class);
        startActivity(intent);
    }
}
