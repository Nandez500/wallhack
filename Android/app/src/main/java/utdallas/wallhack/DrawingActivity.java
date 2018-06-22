package utdallas.wallhack;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.icu.util.TimeUnit;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import static utdallas.wallhack.R.id.radio;
import static utdallas.wallhack.R.id.wallPicture;

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
        makeTestData();
    }

    private void makeTestData(){
        sampleData.add(new WallData(300,500,"wood"));
        sampleData.add(new WallData(700,500,"wood"));
        sampleData.add(new WallData(1100,500,"wood"));
        sampleData.add(new WallData(500,500,"wire/pvc"));
        sampleData.add(new WallData(800,500,"metal"));
    }

    public void drawTest(View view) {
        for(WallData wd : sampleData){
            paint.setColor(wd.getColor());
            float x = wd.getX();
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
