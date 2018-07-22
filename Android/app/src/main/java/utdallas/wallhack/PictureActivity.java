package utdallas.wallhack;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Queue;

public class PictureActivity extends AppCompatActivity {

    public static ImageView imageView;
    public static Button continueButton;
    public static EditText heighField;
    public static EditText widthField;
    public TextView continueMessage;
    public TextView dimensionMessage;
    public static final int IMAGE_GALLERY_REQUEST = 20;
    public static final int REQUEST_TAKE_PHOTO = 10;
    public static final int REQUEST_CROP_PHOTO = 30;
    public static final int REQUEST_BLUETOOTH = 40;
    public static final int MY_PERMISSIONS_REQUEST_WRITE = 1;
    public static final int MY_PERMISSIONS_REQUEST_READ = 2;
    private static String photoPath;
    private static String croppedPhotoPath;
    private Queue<WallData> dataQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        imageView = (ImageView)findViewById(R.id.takenPicture);
        continueButton = (Button)findViewById(R.id.continueButton);
        heighField = findViewById(R.id.heightField);
        widthField = findViewById(R.id.widthField);
        continueMessage = findViewById(R.id.continueMessage);
        dimensionMessage = findViewById(R.id.dimensionMessage);
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
                setElementsVisible();
                Uri imageUri = data.getData();

                InputStream inputStream;
                try {
                    inputStream = getContentResolver().openInputStream(imageUri);

                    // get a bitmap from the stream.
                    Bitmap image = BitmapFactory.decodeStream(inputStream);


                    // show the image to the user
                    imageView.setImageBitmap(image);
                    continueButton.setVisibility(View.VISIBLE);
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
                File f = new File(photoPath);
                Uri contentUri = FileProvider.getUriForFile(this,"utdallas.wallhack.provider", f);
                cropPhoto(contentUri);
            }
            else{
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ);
            }
        }

        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                imageView.setImageBitmap(bitmap);
                setElementsVisible();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        }

        if (requestCode == REQUEST_BLUETOOTH){
            Bundle bundle = data.getExtras();
            dataQueue = (Queue<WallData>)bundle.getSerializable("dataQueue");
            //nextActivity();
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
                setElementsVisible();
            }
        }
        // END_INCLUDE(onRequestPermissionsResult)
    }

    private void setElementsVisible(){
        continueButton.setVisibility(View.VISIBLE);
        continueMessage.setVisibility(View.INVISIBLE);
        heighField.setVisibility(View.VISIBLE);
        widthField.setVisibility(View.VISIBLE);
        dimensionMessage.setVisibility(View.VISIBLE);
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

        //Where are we finding the data?
        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String pictureDirectoryPath = pictureDirectory.getPath();
        photoPath = pictureDirectoryPath;

        //Represent it as a URI
        Uri data = Uri.parse(pictureDirectoryPath);

        photoPickerIntent.setDataAndType(data,"image/*");

        photoPickerIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(photoPickerIntent, IMAGE_GALLERY_REQUEST);

    }

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
        String pictureName = getPictureName(false);
        File imageFile = new File(pictureDirectory,pictureName);
        return imageFile;
    }

    private void createCroppedFile()throws IOException{
        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String pictureName = getPictureName(true);
        File croppedFile = new File(pictureDirectory,pictureName);
        croppedPhotoPath = croppedFile.getAbsolutePath();
    }

    private String getPictureName(boolean cropped){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = sdf.format(new Date());
        if(cropped){
            return "wallhack"+timestamp+"CROPPED.jpg";
        }
        else {
            return "wallhack" + timestamp + ".jpg";
        }
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(photoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void galleryAddCroppedPic(){
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(croppedPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void cropPhoto(Uri sUri){
        try {
            File tempFile = new File(getCacheDir(), getPictureName(false));

            if (!tempFile.exists()) {
                File f = new File(photoPath);
                Uri imageUri = FileProvider.getUriForFile(this,"utdallas.wallhack.provider", f);
                InputStream is = getContentResolver().openInputStream(imageUri);
                FileOutputStream fos = new FileOutputStream(tempFile);
                byte[] buffer = new byte[1024];
                int size;
                while ((size = is.read(buffer)) != -1)
                    fos.write(buffer, 0, size);
                fos.close();
            }

            File tempCropped = new File(getCacheDir(), "tempImgCropped.png");
            Uri sourceUri = Uri.fromFile(tempFile);
            Uri destinationUri = Uri.fromFile(tempCropped);
            UCrop.Options options = new UCrop.Options();
            options.setFreeStyleCropEnabled(true);
            UCrop.of(sourceUri, destinationUri)
                    .withOptions(options)
                    .start(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void nextActivity(View view){
        Intent intent = new Intent(this, DrawingActivity.class);
        if(TextUtils.isEmpty(heighField.getText())){
            Toast.makeText(this, "Please enter a heigth", Toast.LENGTH_LONG).show();
            return;
        }
        else if(TextUtils.isEmpty(widthField.getText())){
            Toast.makeText(this, "Please enter a width", Toast.LENGTH_LONG).show();
            return;
        }
        else {
            Bitmap bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 50, stream);
            byte[] byteArray = stream.toByteArray();
            intent.putExtra("picture", byteArray);
            intent.putExtra("height",Float.parseFloat(heighField.getText().toString()));
            intent.putExtra("width",Float.parseFloat(widthField.getText().toString()));
            startActivity(intent);
        }
    }

}
