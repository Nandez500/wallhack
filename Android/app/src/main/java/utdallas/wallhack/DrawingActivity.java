package utdallas.wallhack;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Queue;

public class DrawingActivity extends AppCompatActivity {
    private static final String TAG = "DrawingActivity";

    private Canvas canvas;
    private Paint paint;
    private float wallHeight;
    private float wallWidth;
    private int h;
    private int w;
    private int count;
    private boolean scanning;
    private ImageView overlay;
    private ImageView image;
    private Button scanningButton;
    private Button resetButton;
    private DisplayMetrics displayMetrics = new DisplayMetrics();
    private int width;
    private int height;
    private Queue<WallData> dataQueue;
    private TextView messageText;
    private float testX = 0;
    private float testY = 0;
    private float xPos = 0;
    private float yPos = 0;
    private int display = 0;

    private static String address = "B8:27:EB:B3:EF:90";
    private BluetoothAdapter btAdapter;
    private BluetoothDevice device;
    private BluetoothService bluetoothService;

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg){
            switch (display) {
                case 0:
                    messageText.setText("Collecting Data");
                    display++;
                    break;
                case 1:
                    messageText.setText("Collecting Data.");
                    display++;
                    break;
                case 2:
                    messageText.setText("Collecting Data..");
                    display++;
                    break;
                case 3:
                    messageText.setText("Collecting Data...");
                    display = 0;
                    break;
            }

            switch (msg.what) {
                case 0:

                    /**
                     Read a message
                     */
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);

                    /**
                     Split the message
                     */
                    String[] splitMessage = readMessage.split(",");

                    /**
                     If it's walabot data...
                     */
                    Log.i(TAG,"Message: "+readMessage+"/nLength: "+splitMessage.length);
                    if(splitMessage.length == 5) {
                        //Toast.makeText(getApplicationContext(),readMessage, Toast.LENGTH_LONG).show();
                        try {
                            WallData target = new WallData(getApplicationContext(),
                                    splitMessage[0], Float.valueOf(splitMessage[1]),
                                    Float.valueOf(splitMessage[2]), Float.valueOf(splitMessage[3]),
                                    Float.valueOf(splitMessage[4]));
                            //target.setyPos(yPos-yOffset);
                            //target.setxPos(xPos-xOffset);
                            target.setxPos(testX);
                            target.setyPos(testY);
                            incrementTestPos();
                            //dataQueue.add(target);
                            plotPoint(target);
                        }catch (NumberFormatException e){
                            Log.i(TAG,"Error processing input: "+readMessage);
                        }
                        break;
                    }

                    /**
                     If it's locatioin data...
                     */
                    else if(splitMessage.length == 3){
                        try {
                            xPos = Float.valueOf(splitMessage[0]);
                            yPos = Float.valueOf(splitMessage[1]);
                        }catch (NumberFormatException e){
                            Log.i(TAG,"Error processing input: "+ readMessage);
                            break;
                        }
                        count ++;
                    }
                    else
                        break;
            }
        }
    };

    private void incrementTestPos(){
        testX += 100;
        if(testX > 1000){
            testX = 0;
            testY += 100;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);
        Bundle extras = getIntent().getExtras();
        byte[] byteArray = extras.getByteArray("picture");
        messageText = findViewById(R.id.messageText);

        bluetoothService = new BluetoothService(this,mHandler);

        scanning = false;

        //Set Height and Width Text Fields
        wallHeight = extras.getFloat("height");
        wallWidth = extras.getFloat("width");

        scanningButton = findViewById(R.id.scanningButton);
        resetButton = findViewById(R.id.resetButton);

        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        image = (ImageView) findViewById(R.id.wallPicture);
        image.setImageBitmap(bmp);

        h = bmp.getHeight();
        w = bmp.getWidth();
        Bitmap overlayBmp = Bitmap.createBitmap(w,h,bmp.getConfig());
        overlay = findViewById(R.id.overlay);
        overlay.setImageBitmap(overlayBmp);

        canvas = new Canvas(overlayBmp);

        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;
    }

    /*******************************************************************************************
     * Button Methods
     *******************************************************************************************/
    public void startScanningButton(View view){
        if(!scanning) {
            btAdapter = BluetoothAdapter.getDefaultAdapter();
            device = btAdapter.getRemoteDevice(address);
            bluetoothService.connect(device);
            messageText.setText("Move the device in small circles to calibrate");
            messageText.setVisibility(View.VISIBLE);
            scanningButton.setText("Stop Scanning");
            resetButton.setVisibility(View.VISIBLE);
            scanning = true;
        }
        else{
            bluetoothService.disconnect();
            messageText.setVisibility(View.INVISIBLE);
            scanningButton.setVisibility(View.INVISIBLE);
            resetButton.setVisibility(View.INVISIBLE);
            scanning = false;
            plotData();
            //dataQueue = bluetoothService.getDataQueue();
        }
    }

    public void resetButton(View view){
        bluetoothService.reset();
    }

    /*******************************************************************************************
     * Drawing Test Methods
     *******************************************************************************************/
    private void makeTestData(){
        for(int y = 500; y < 1000; y++)
            if(y%50 == 0)
                dataQueue.add(new WallData(this,"wood",0,1100,y,0));
        for(int y = 500; y < 1000; y++)
            if(y%50 ==0)
                dataQueue.add(new WallData(this,"wire/pvc",0,500,y,0));
        for(int y = 500; y < 1000; y++)
            if(y%50 ==0)
                dataQueue.add(new WallData(this,"metal",0,800,y,0));
        for(int y = 500; y < 1000; y++)
            if(y%50 == 0)
                dataQueue.add(new WallData(this,"ac",0,900, y, 0));
    }

    public void drawTest(View view) {
        makeTestData();
        while(!dataQueue.isEmpty()){
            WallData data = dataQueue.remove();
            float x = data.getxPos();
            float scaledX = x/width;
            float y = data.getyPos();
            float scaledY = y/height;
            textureDraw(scaledX,scaledY,data.getTexture());
            overlay.invalidate();
        }
    }


    /*******************************************************************************************
     * Drawing Methods
     *******************************************************************************************/
    private void plotData(){
        //Toast.makeText(this, "Optical counts:" + bluetoothService.getCount(), Toast.LENGTH_LONG).show();
        //int count = bluetoothService.getCount();
        dataQueue = bluetoothService.getDataQueue();

            if(!dataQueue.isEmpty() && dataQueue != null) {
                while(!dataQueue.isEmpty()) {
                    WallData data = dataQueue.remove();
                    float x = data.getxPos();
                    float scaledX = x / width;
                    float y = data.getyPos();
                    float scaledY = y / height;
                    textureDraw(scaledX, scaledY, data.getTexture());
                    overlay.invalidate();
                }
            }

    }

    private void plotPoint(WallData data){
        float x = data.getxPos();
        float scaledX = x / width;
        float y = data.getyPos();
        float scaledY = y / height;
        textureDraw(scaledX, scaledY, data.getTexture());
        overlay.invalidate();
    }

    private void textureDraw(float scaledX,float scaledY,Bitmap texture){
        float newX = canvas.getWidth()*scaledX;
        float newY = canvas.getHeight()*scaledY;
        RectF rectF = new RectF(newX,newY,newX+100,newY+100);
        canvas.drawBitmap(texture,null,rectF,null);
    }
}
