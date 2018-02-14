package co.fav.bites.views;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import co.fav.bites.R;
import co.fav.bites.controller.ModelManager;
import co.fav.bites.models.Constants;
import co.fav.bites.models.Event;
import co.fav.bites.models.FBPreferences;
import co.fav.bites.models.ImagePicker;
import co.fav.bites.models.Operations;
import co.fav.bites.models.Utils;

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
        restaurant_id = getIntent().getStringExtra("restaurant_id");

        tvUpload = findViewById(R.id.tvUpload);
        editComment = findViewById(R.id.editComment);

        imgPhoto = findViewById(R.id.imgPhoto);
        imgBack = findViewById(R.id.imgBack);

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

        if (requestCode == PERMISSION_REQUEST_CODE && Utils.hasAllPermissionsGranted(this, grantResults)) {
            chooseImage();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            Bitmap photo = ImagePicker.getImageFromResult(this, resultCode, data);

            Uri tempUri = ImagePicker.getImageUri(this, photo);
            File finalFile = new File(ImagePicker.getRealPathFromURI(this, tempUri));
            filePath = finalFile.getAbsolutePath();

            imgPhoto.setImageBitmap(photo);
        } else {
            finish();
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

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(Event event) {
        EventBus.getDefault().removeStickyEvent(Event.class);
        switch (event.getKey()) {

            case Constants.UPLOAD_PHOTO_SUCCESS:
                pd.dismiss();
                Toast.makeText(this, "Your post has been submitted successfully.", Toast.LENGTH_SHORT).show();

                Intent i = new Intent();
                i.putExtra("file_path", filePath);
                setResult(RESULT_OK, i);

                finish();
                Utils.gotoPreviousActivityAnimation(this);
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
