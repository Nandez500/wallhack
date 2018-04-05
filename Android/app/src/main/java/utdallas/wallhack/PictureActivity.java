package utdallas.wallhack;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class PictureActivity extends AppCompatActivity {

    public static ImageView imageView;
    public static final int IMAGE_GALLERY_REQUEST = 20;
    static final int REQUEST_TAKE_PHOTO = 10;
    public static final int MY_PERMISSIONS_REQUEST_WRITE = 1;
    public static final int MY_PERMISSIONS_REQUEST_READ = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        imageView = (ImageView)findViewById(R.id.takenPicture);
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == RESULT_OK) {
            if (requestCode == IMAGE_GALLERY_REQUEST) {
                Uri imageUri = data.getData();

                InputStream inputStream;
                try {
                    inputStream = getContentResolver().openInputStream(imageUri);

                    // get a bitmap from the stream.
                    Bitmap image = BitmapFactory.decodeStream(inputStream);


                    // show the image to the user
                    imageView.setImageBitmap(image);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    // show a message to the user indictating that the image is unavailable.
                    Toast.makeText(this, "Unable to open image", Toast.LENGTH_LONG).show();
                }
            }
        }
        if (requestCode == REQUEST_TAKE_PHOTO) {
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(this, "REQUEST_TAKE_PHOTO", Toast.LENGTH_LONG).show();
                galleryAddPic();
                Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
                imageView.setImageBitmap(bitmap);
            }
            else{
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // BEGIN_INCLUDE(onRequestPermissionsResult)
        if (requestCode == MY_PERMISSIONS_REQUEST_WRITE) {
            // Request for camera permission.
            // Permission has been granted. Start camera preview Activity.
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                dispatchTakePictureIntent();
            else {
                // Permission request was denied.
            }
        }
        else if(requestCode == MY_PERMISSIONS_REQUEST_READ){
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                galleryAddPic();
                Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
                imageView.setImageBitmap(bitmap);
            }
        }
        // END_INCLUDE(onRequestPermissionsResult)
    }


    public void takePicButton(View view){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
            dispatchTakePictureIntent();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE);
        }
    }

    public void choosePicButton(View view){
        //Invoke the image gallery with an implicit intent
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);

        //Wehere are we finding the data?
        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String pictureDirectoryPath = pictureDirectory.getPath();

        //Represent it as a URI
        Uri data = Uri.parse(pictureDirectoryPath);

        photoPickerIntent.setDataAndType(data,"image/*");

        photoPickerIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(photoPickerIntent, IMAGE_GALLERY_REQUEST);

    }
    private static String photoPath;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File imageFile = null;
        try{
            imageFile = createImageFile();
        }
        catch (IOException e){

        }
        if(imageFile != null) {
            Uri photoURI = FileProvider.getUriForFile(this,"utdallas.wallhack.provider", imageFile);
            photoPath = imageFile.getAbsolutePath();

            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
        }

    }

    private File createImageFile() throws IOException{
        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String pictureName = getPictureName();
        File imageFile = new File(pictureDirectory,pictureName);
        return imageFile;
    }

    private String getPictureName(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = sdf.format(new Date());
        return "wallhack"+timestamp+".jpg";
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(photoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

}
