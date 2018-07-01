package utdallas.wallhack;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class DrawingActivity extends AppCompatActivity {

    private Canvas canvas;
    private Paint paint;
    int h;
    int w;
    ImageView overlay;
    ImageView image;
    DisplayMetrics displayMetrics = new DisplayMetrics();
    int width;
    ArrayList<WallData> sampleData = new ArrayList<>();
    Queue<WallData> dataQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);
        Bundle extras = getIntent().getExtras();
        byte[] byteArray = extras.getByteArray("picture");

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
        dataQueue.add(new WallData(300,500,"wood"));
        dataQueue.add(new WallData(700,500,"wood"));
        dataQueue.add(new WallData(1100,500,"wood"));
        dataQueue.add(new WallData(500,500,"wire/pvc"));
        dataQueue.add(new WallData(800,500,"metal"));
        dataQueue.add(new WallData(900, 500, "ac"));
    }

    public void drawTest(View view) {
        while(!dataQueue.isEmpty()){
            WallData data = dataQueue.remove();
            paint.setColor(data.getColor());
            float x = data.getX();
            float ratio = x/width;
            lineDraw(x,ratio);
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

    private void lineDraw(float x,float ratio) {
        float newX = canvas.getWidth()*ratio;
        canvas.drawLine(newX,overlay.getY(),newX,overlay.getY()+overlay.getMaxHeight(),paint);
    }


}
