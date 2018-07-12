package utdallas.wallhack;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.Queue;

public class DrawingActivity extends AppCompatActivity {

    private Canvas canvas;
    private Paint paint;
    private float wallHeight;
    private float wallWidth;
    private int h;
    private int w;
    private ImageView overlay;
    private ImageView image;
    private DisplayMetrics displayMetrics = new DisplayMetrics();
    private int width;
    private int height;
    //private ArrayList<WallData> sampleData = new ArrayList<>();
    private Queue<WallData> dataQueue;
    private TextView wallHeightText;
    private TextView wallWidthText;
    private Handler messageHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);
        Bundle extras = getIntent().getExtras();
        byte[] byteArray = extras.getByteArray("picture");

        messageHandler = new Handler();

        //Set Height and Width Text Fields
        wallHeight = extras.getFloat("height");
        wallWidth = extras.getFloat("width");
        wallHeightText = findViewById(R.id.heightText);
        wallWidthText = findViewById(R.id.widthText);
        wallHeightText.setText(Float.toString(wallHeight));
        wallWidthText.setText(Float.toString(wallWidth));

        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        image = (ImageView) findViewById(R.id.wallPicture);
        image.setImageBitmap(bmp);

        h = bmp.getHeight();
        w = bmp.getWidth();
        Bitmap overlayBmp = Bitmap.createBitmap(w,h,bmp.getConfig());
        overlay = findViewById(R.id.overlay);
        overlay.setImageBitmap(overlayBmp);

        canvas = new Canvas(overlayBmp);
        paint = new Paint();
        //paint.setColor(Color.RED);
        paint.setStrokeWidth(55);

        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;

//        try {
//            File file = new File("testFile.txt");
//            FileReader fileReader = new FileReader(file);
//            BufferedReader bufferedReader = new BufferedReader(fileReader);
//            String line;
//            while ((line = bufferedReader.readLine()) != null) {
//                sampleData.add(new WallData(line.split(" ")));
//            }
//            fileReader.close();
//            }
//            catch (IOException e) {
//            e.printStackTrace();
//        }
        dataQueue = new LinkedList<>();
        makeTestData();
    }

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
        while(!dataQueue.isEmpty()){
            WallData data = dataQueue.remove();
            paint.setColor(data.getColor());
            float x = data.getxPos();
            float scaledX = x/width;
            float y = data.getyPos();
            float scaledY = y/height;
            textureDraw(scaledX,scaledY,data.getTexture());
            //pointDraw(scaledX,scaledY);
            overlay.invalidate();
        }
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        float x = event.getRawX();
//        float y = event.getRawY();
//
//        /*String xCoord = Float.toString(x);
//        String yCoord = Float.toString(y);
//
//        String coords = xCoord + ", " + yCoord;
//
//        int duration = Toast.LENGTH_LONG;
//        Toast toast = Toast.makeText(this, coords, duration);
//        toast.show();*/
//
//        float ratio = x/width;
//
//        if(ratio <= 0.5){
//            paint.setColor(Color.RED);
//        }
//        else{
//            paint.setColor(Color.BLUE);
//        }
//
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                lineDraw(x,ratio);
//                overlay.invalidate();
//                break;
//        }
//        return true;
//    }

    private void pointDraw(float scaledX,float scaledY) {
        float newX = canvas.getWidth()*scaledX;
        float newY = canvas.getHeight()*scaledY;
        canvas.drawPoint(newX,newY,paint);
    }

    private void textureDraw(float scaledX,float scaledY,Bitmap texture){
        float newX = canvas.getWidth()*scaledX;
        float newY = canvas.getHeight()*scaledY;
        RectF rectF = new RectF(newX,newY,newX+100,newY+100);
        canvas.drawBitmap(texture,null,rectF,null);
    }


}
