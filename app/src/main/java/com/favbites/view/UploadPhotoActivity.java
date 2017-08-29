package com.favbites.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.favbites.BuildConfig;
import com.favbites.R;
import com.favbites.controller.ModelManager;
import com.favbites.model.Constants;
import com.favbites.model.Event;
import com.favbites.model.FBPreferences;
import com.favbites.model.Operations;
import com.favbites.model.Utils;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.IOException;

import static android.os.Build.VERSION_CODES.M;


public class UploadPhotoActivity extends BaseActivity implements View.OnClickListener{

    private static final int PERMISSION_REQUEST_CODE = 1001;
    String filePath;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    ImageView imgPhoto, imgBack;
    TextView tvUpload;
    EditText editComment;
    String user_id, restaurant_id;
    KProgressHUD pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        dispatchTakePictureIntent();
        initViews();
    }

    public void initViews() {
        pd = Utils.showMessageDialog(this, "Uploading post...");
        user_id = FBPreferences.readString(this, "user_id");
        restaurant_id = FBPreferences.readString(this, "restaurant_id");

        tvUpload = (TextView) findViewById(R.id.tvUpload);
        editComment = (EditText) findViewById(R.id.editComment);

        imgPhoto = (ImageView) findViewById(R.id.imgPhoto);
        imgBack = (ImageView) findViewById(R.id.imgBack);

        tvUpload.setOnClickListener(this);
        imgBack.setOnClickListener(this);
    }

    private void dispatchTakePictureIntent() {

        if (Build.VERSION.SDK_INT >= M) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);

        } else {
            captureImage();
        }
    }

    public void captureImage() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File photoFile;
        try {
            photoFile = Utils.createImageFile();
            filePath = photoFile.getAbsolutePath();
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            Uri photoURI = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID
                    + ".provider", photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE && hasAllPermissionsGranted(grantResults)) {
            captureImage();
        } else {
            Toast.makeText(this, "Please grant all the permissions...", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean hasAllPermissionsGranted(@NonNull int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Uri imageUri = Uri.parse(filePath);
            File file = new File(imageUri.getPath());
            try {
                //InputStream ims = new FileInputStream(file);
                Bitmap d = new BitmapDrawable(this.getResources() , file.getAbsolutePath()).getBitmap();
                int nh = (int) ( d.getHeight() * (512.0 / d.getWidth()) );
                Bitmap scaled = Bitmap.createScaledBitmap(d, 512, nh, true);
                imgPhoto.setImageBitmap(scaled);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvUpload:
                pd.show();
                String comment = editComment.getText().toString().trim();

                ModelManager.getInstance().getUploadPhotoManager()
                        .uploadPostServer(Operations.uploadPhoto(user_id, restaurant_id, comment), filePath);
                break;

            case R.id.imgBack:
                finish();
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(Event event) {
        switch (event.getKey()) {

            case Constants.UPLOAD_PHOTO_SUCCESS:
                pd.dismiss();
                Toast.makeText(this, "Your post has been submitted successfully.", Toast.LENGTH_SHORT).show();
                finish();
                break;

            case Constants.UPLOAD_PHOTO_FAILED:
                pd.dismiss();
                Toast.makeText(this, "Sorry, upload photo failed. Please try again.", Toast.LENGTH_SHORT).show();
                break;

            case Constants.NO_RESPONSE:
                pd.dismiss();
                Toast.makeText(this, ""+event.getValue(), Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
