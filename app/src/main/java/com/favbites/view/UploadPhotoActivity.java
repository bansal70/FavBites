package com.favbites.view;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.favbites.R;
import com.favbites.controller.ModelManager;
import com.favbites.model.Constants;
import com.favbites.model.Event;
import com.favbites.model.FBPreferences;
import com.favbites.model.ImagePicker;
import com.favbites.model.Operations;
import com.favbites.model.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;

import static android.os.Build.VERSION_CODES.M;


public class UploadPhotoActivity extends BaseActivity implements View.OnClickListener{

    private static final int PERMISSION_REQUEST_CODE = 1001;
    String filePath;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    ImageView imgPhoto, imgBack;
    TextView tvUpload;
    EditText editComment;
    String user_id, restaurant_id;
    Dialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        dispatchTakePictureIntent();
        initViews();
    }

    public void initViews() {
        pd = Utils.showDialog(this);
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
            chooseImage();
        }
    }

    public void chooseImage() {
        Intent chooseImageIntent = ImagePicker.getPickImageIntent(this);
        startActivityForResult(chooseImageIntent, REQUEST_IMAGE_CAPTURE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE && hasAllPermissionsGranted(grantResults)) {
            chooseImage();
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

            Bitmap photo = ImagePicker.getImageFromResult(this, resultCode, data);

            Uri tempUri = ImagePicker.getImageUri(this, photo);
            File finalFile = new File(ImagePicker.getRealPathFromURI(this, tempUri));
            filePath = finalFile.getAbsolutePath();

            imgPhoto.setImageBitmap(photo);
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
                Utils.isPhotoUploaded = true;
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
