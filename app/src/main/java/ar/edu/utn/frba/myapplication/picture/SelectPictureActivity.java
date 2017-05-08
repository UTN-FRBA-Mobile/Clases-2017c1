package ar.edu.utn.frba.myapplication.picture;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import ar.edu.utn.frba.myapplication.R;

public class SelectPictureActivity extends AppCompatActivity {

    public static final String SELECTED_PICTURE = "selectedImage";
    private static final int REQUEST_TAKE_PHOTO = 6959;
    private static final int REQUEST_PICK_IMAGE = 838;
    private static final int REQUEST_READ = 4313;

    private ImageView selectedImageView;

    String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_picture);
        selectedImageView = (ImageView) findViewById(R.id.selectedImageView);
        View cameraButton = findViewById(R.id.cameraButton);
        cameraButton.setEnabled(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA));
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });
        findViewById(R.id.galleryButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        currentPhotoPath = getIntent().getStringExtra(SELECTED_PICTURE);
        loadImage();
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "ar.edu.utn.frba.myapplication.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private void openGallery() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(this, R.string.readStorageRequired, Toast.LENGTH_LONG).show();
            }
            else {
                ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE }, REQUEST_READ);
            }
            return;
        }
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        gallery.setType("image/*");
        startActivityForResult(gallery, REQUEST_PICK_IMAGE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putString(SELECTED_PICTURE, currentPhotoPath);
    }

    private void saveImage(Uri data) {
        try {
            InputStream input = getContentResolver().openInputStream(data);
            assert input != null;
            File file = createImageFile();
            FileOutputStream output = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int len = input.read(buffer);
            while (len != -1) {
                output.write(buffer, 0, len);
                len = input.read(buffer);
            };
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void loadImage() {
        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, null);
        selectedImageView.setImageBitmap(bitmap);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    Intent resultData = new Intent();
                    resultData.putExtra(SELECTED_PICTURE, currentPhotoPath);
                    setResult(RESULT_OK, resultData);
                    loadImage();
                }
                else {
                    new File(currentPhotoPath).delete();
                }
                break;
            case REQUEST_PICK_IMAGE:
                if (resultCode == RESULT_OK && data != null) {
                    saveImage(data.getData());
                    Intent resultData = new Intent();
                    resultData.putExtra(SELECTED_PICTURE, currentPhotoPath);
                    setResult(RESULT_OK, resultData);
                    loadImage();
                }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_READ: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery();
                }
                else {
                    Toast.makeText(this, R.string.readStorageRequired, Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
