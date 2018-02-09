package co.fav.bites.views;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;

import co.fav.bites.BuildConfig;
import co.fav.bites.R;
import co.fav.bites.controller.AccountManager;
import co.fav.bites.controller.ModelManager;
import co.fav.bites.models.Constants;
import co.fav.bites.models.Event;
import co.fav.bites.models.FBPreferences;
import co.fav.bites.models.FilePath;
import co.fav.bites.models.Operations;
import co.fav.bites.models.Utils;
import co.fav.bites.models.beans.AccountData;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.os.Build.VERSION_CODES.M;

public class MyAccountActivity extends BaseActivity implements View.OnClickListener{

    EditText editFirstName, editLastName, editEmail;
    TextView tvChangePassword;
    ImageView imgBack, imgEdit, imgBackground;
    CircleImageView imgProfilePic;
    String user_id;
    Dialog pd;
    boolean isEditing = true;
    Dialog dialogEdit, dialogImage;
    TextView tvCancel, tvConfirm, tvCamera, tvGallery;
    private int REQUEST_IMAGE = 101;
    private int PERMISSION_REQUEST_CODE = 102;
    String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        initViews();
        initDialog();
        initImageDialog();
    }

    public void initViews() {
        pd = Utils.showDialog(this);

        user_id = FBPreferences.readString(this, "user_id");

        editFirstName = (EditText) findViewById(R.id.editFirstName);
        editLastName = (EditText) findViewById(R.id.editLastName);
        editEmail = (EditText) findViewById(R.id.editEmail);
        tvChangePassword = (TextView) findViewById(R.id.tvChangePass);
        imgBack = (ImageView) findViewById(R.id.imgBack);
        imgEdit = (ImageView) findViewById(R.id.imgEdit);
        imgProfilePic = (CircleImageView) findViewById(R.id.imgProfilePic);
        imgBackground = (ImageView) findViewById(R.id.imgBackground);
        editFields(false);

        imgBack.setOnClickListener(this);
        imgEdit.setOnClickListener(this);
        imgProfilePic.setOnClickListener(this);

        pd.show();
        ModelManager.getInstance().getAccountManager().userAccount(this, Operations.profileParams(user_id));
    }

    public void initDialog() {
        dialogEdit = Utils.createDialog(this, R.layout.dialog_alert);
        tvCancel = dialogEdit.findViewById(R.id.tvCancel);
        tvConfirm = dialogEdit.findViewById(R.id.tvConfirm);

        tvCancel.setOnClickListener(this);
        tvConfirm.setOnClickListener(this);
    }

    public void initImageDialog() {
        dialogImage = Utils.createDialog(this, R.layout.dialog_image_picker);
        dialogImage.setCancelable(true);
        tvCamera = dialogImage.findViewById(R.id.tvCamera);
        tvGallery = dialogImage.findViewById(R.id.tvGallery);

        tvCamera.setOnClickListener(this);
        tvGallery.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBack:
                finish();
                break;

            case R.id.imgEdit:
                if (isEditing) {
                    dialogEdit.show();
                } else {
                    updateProfile();
                }
                break;

            case R.id.tvConfirm:
                dialogEdit.dismiss();
                isEditing = false;
                editFields(true);
                imgEdit.setImageResource(R.drawable.edit_done);
                break;

            case R.id.tvCancel:
                dialogEdit.dismiss();
                break;

            case R.id.imgProfilePic:
                checkPermissions();
                break;

            case R.id.tvCamera:
                chooseImage("Camera");
                break;

            case R.id.tvGallery:
                chooseImage("Gallery");
                break;
        }
    }

    private void checkPermissions() {

        if (Build.VERSION.SDK_INT >= M) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);

        } else {
            dialogImage.show();
        }
    }

    public void chooseImage(String param) {
        Intent takePictureIntent = new Intent();
        if (param.equals("Gallery")) {
            filePath = "";
            takePictureIntent.setType("image/*");
            takePictureIntent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(takePictureIntent,
                    "Select Picture"), REQUEST_IMAGE);
        } else {
            takePictureIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

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
                startActivityForResult(Intent.createChooser(takePictureIntent,
                        "Select Picture..."), REQUEST_IMAGE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK) {
            Uri imageUri;
            dialogImage.dismiss();
            if (filePath.isEmpty()) {
                imageUri = data.getData();
                filePath = FilePath.getRealPathFromURI(imageUri, this);
                decodeImage(filePath);
            }
            else {
                imageUri = Uri.parse(filePath);
                File file = new File(imageUri.getPath());
                decodeImage(file.getAbsolutePath());
            }
        }
    }

    public void decodeImage(String path) {
        try {
            Utils.loadCircularImage(this, path, imgProfilePic, R.drawable.demo_img);
            Utils.loadImage(this, path, imgBackground, R.drawable.demo_img);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE && hasAllPermissionsGranted(grantResults)) {
            dialogImage.show();
        } else {
            Toast.makeText(this, "Please grant all the permissions", Toast.LENGTH_SHORT).show();
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

    public void updateProfile() {
        String firstName = editFirstName.getText().toString().trim();
        String lastName = editLastName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Please fill all the data", Toast.LENGTH_SHORT).show();
        } else if (!Utils.emailValidator(email)) {
            Toast.makeText(this, "Please enter the correct email address", Toast.LENGTH_SHORT).show();
        } else {
            pd.show();
            ModelManager.getInstance().getAccountManager().updateProfile(
                    Operations.updateProfileParams(user_id, firstName, lastName, email), filePath);
        }
    }
    public void editFields(boolean status) {
        editFirstName.setEnabled(status);
        editLastName.setEnabled(status);
        editEmail.setEnabled(status);
        imgProfilePic.setEnabled(status);
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
            case Constants.PROFILE_SUCCESS:
                pd.dismiss();
                setData();
                break;

            case Constants.PROFILE_EMPTY:
                pd.dismiss();
                Toast.makeText(this, Constants.SERVER_ERROR, Toast.LENGTH_SHORT).show();
                break;

            case Constants.UPDATE_PROFILE_SUCCESS:
                pd.dismiss();
                imgEdit.setImageResource(R.drawable.edit);
                isEditing = true;
                editFields(false);
                break;

            case Constants.UPDATE_PROFILE_FAILED:
                Toast.makeText(this, R.string.update_profile_failed, Toast.LENGTH_SHORT).show();
                break;

            case Constants.NO_RESPONSE:
                pd.dismiss();
                Toast.makeText(this, ""+event.getValue(), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void setData() {
        AccountData.Data data = AccountManager.data;
        AccountData.User user = data.user;
        editFirstName.setText(user.fname);
        editLastName.setText(user.lname);

        if (!user.email.isEmpty())
            editEmail.setText(user.email);

        if (user.image != null) {
            decodeImage(user.image.toString());
            /*Glide.with(this).load(user.image)
                    .placeholder(R.drawable.demo_img)
                    .crossFade()
                    .into(imgProfilePic);
            Glide.with(this).load(user.image)
                    .placeholder(R.drawable.demo_img)
                    .crossFade()
                    .into(imgBackground);*/
        }
    }
}
