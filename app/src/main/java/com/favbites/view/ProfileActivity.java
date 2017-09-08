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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.favbites.R;
import com.favbites.controller.AccountManager;
import com.favbites.controller.ModelManager;
import com.favbites.model.Constants;
import com.favbites.model.Event;
import com.favbites.model.FBPreferences;
import com.favbites.model.ImagePicker;
import com.favbites.model.Operations;
import com.favbites.model.Utils;
import com.favbites.model.beans.AccountData;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.List;

import static android.os.Build.VERSION_CODES.M;

public class ProfileActivity extends BaseActivity implements View.OnClickListener{

    EditText editFirstName, editLastName, editEmail;
    TextView tvChangePassword, tvFollowers, tvFollowings;
    ImageView imgBack, imgEdit, imgBackground, imgProfilePic;
    String user_id;
    Dialog pd;
    boolean isEditing = true;
    Dialog dialogEdit, dialogPassword;
    TextView tvCancel, tvConfirm;
    TextView tvConfirmPassword, tvCancelPassword;
    EditText editOldPassword, editNewPassword, editConfirmPassword;
    private int REQUEST_IMAGE = 101;
    private int PERMISSION_REQUEST_CODE = 102;
    String filePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        initViews();
        initDialog();
        initPasswordDialog();
    }

    public void initViews() {
        pd = Utils.showDialog(this);
        pd.show();

        user_id = FBPreferences.readString(this, "user_id");
        ModelManager.getInstance().getAccountManager().userAccount(Operations.profileParams(user_id));

        editFirstName = (EditText) findViewById(R.id.editFirstName);
        editLastName = (EditText) findViewById(R.id.editLastName);
        editEmail = (EditText) findViewById(R.id.editEmail);
        tvChangePassword = (TextView) findViewById(R.id.tvChangePass);
        tvFollowers = (TextView) findViewById(R.id.tvFollowers);
        tvFollowings = (TextView) findViewById(R.id.tvFollowing);

        imgBack = (ImageView) findViewById(R.id.imgBack);
        imgEdit = (ImageView) findViewById(R.id.imgEdit);
        imgProfilePic = (ImageView) findViewById(R.id.imgProfilePic);
        imgBackground = (ImageView) findViewById(R.id.imgBackground);
        editFields(false);

        imgBack.setOnClickListener(this);
        imgEdit.setOnClickListener(this);
        imgProfilePic.setOnClickListener(this);
        tvChangePassword.setOnClickListener(this);
        tvFollowers.setOnClickListener(this);
        tvFollowings.setOnClickListener(this);
    }

    public void initDialog() {
        dialogEdit = Utils.createDialog(this, R.layout.dialog_alert);
        tvCancel = dialogEdit.findViewById(R.id.tvCancel);
        tvConfirm = dialogEdit.findViewById(R.id.tvConfirm);

        tvCancel.setOnClickListener(this);
        tvConfirm.setOnClickListener(this);
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
                filePath = "";
                imgEdit.setImageResource(R.drawable.edit_done);
                break;

            case R.id.tvCancel:
                dialogEdit.dismiss();
                break;

            case R.id.imgProfilePic:
                checkPermissions();
                break;

            case R.id.tvChangePass:
                dialogPassword.show();
                break;

            case R.id.tvConfirmPassword:
                changePassword();
                break;

            case R.id.tvCancelPassword:
                dialogPassword.dismiss();
                break;

            case R.id.tvFollowers:
                startActivity(new Intent(this, FollowersActivity.class)
                        .putExtra("user_id", user_id));
                break;

            case R.id.tvFollowing:
                startActivity(new Intent(this, FollowingActivity.class)
                        .putExtra("user_id", user_id));
                break;
        }
    }

    public void initPasswordDialog() {
        dialogPassword = Utils.createDialog(this, R.layout.dialog_change_password);
        editOldPassword = dialogPassword.findViewById(R.id.editOldPassword);
        editNewPassword = dialogPassword.findViewById(R.id.editNewPassword);
        editConfirmPassword = dialogPassword.findViewById(R.id.editConfirmPassword);

        tvConfirmPassword = dialogPassword.findViewById(R.id.tvConfirmPassword);
        tvCancelPassword = dialogPassword.findViewById(R.id.tvCancelPassword);

        tvConfirmPassword.setOnClickListener(this);
        tvCancelPassword.setOnClickListener(this);
    }

    public void changePassword() {
        String oldPass = editOldPassword.getText().toString();
        String newPass = editNewPassword.getText().toString();
        String confirmPass = editConfirmPassword.getText().toString();
        if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty())
            Toast.makeText(this, "Please fill all the data", Toast.LENGTH_SHORT).show();
        else if (!newPass.equals(confirmPass))
            Toast.makeText(this, "Password didn't match", Toast.LENGTH_SHORT).show();
        else if (newPass.length() < 6)
            Toast.makeText(this, "Password must contains at least 6 letters.", Toast.LENGTH_SHORT).show();
        else {
            pd.show();
            ModelManager.getInstance().getAccountManager().changePassword(
                    Operations.changePasswordParams(user_id, oldPass, newPass));
        }
    }

    private void checkPermissions() {

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
        startActivityForResult(chooseImageIntent, REQUEST_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK) {

            Bitmap photo = ImagePicker.getImageFromResult(this, resultCode, data);
            // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
            Uri tempUri = ImagePicker.getImageUri(this, photo);

            // CALL THIS METHOD TO GET THE ACTUAL PATH
            File finalFile = new File(ImagePicker.getRealPathFromURI(this, tempUri));
            filePath = finalFile.getAbsolutePath();
            imgProfilePic.setImageBitmap(photo);
            imgBackground.setImageBitmap(photo);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE && hasAllPermissionsGranted(grantResults)) {
            chooseImage();
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

    @Subscribe
    public void onEvent(Event event) {
        switch (event.getKey()) {
            case Constants.PROFILE_SUCCESS:
                if (pd.isShowing())
                pd.dismiss();
                setData();
                break;

            case Constants.PROFILE_EMPTY:
                pd.dismiss();
                Toast.makeText(this, Constants.SERVER_ERROR, Toast.LENGTH_SHORT).show();
                break;

            case Constants.UPDATE_PROFILE_SUCCESS:
                pd.dismiss();
                ModelManager.getInstance().getAccountManager().userAccount(Operations.profileParams(user_id));
                imgEdit.setImageResource(R.drawable.edit);
                isEditing = true;
                editFields(false);
                Toast.makeText(this, "Your profile has been updated successfully", Toast.LENGTH_SHORT).show();
                break;

            case Constants.UPDATE_PROFILE_FAILED:
                pd.dismiss();
                Toast.makeText(this, R.string.update_profile_failed, Toast.LENGTH_SHORT).show();
                break;

            case Constants.CHANGE_PASSWORD_SUCCESS:
                dialogPassword.dismiss();
                pd.dismiss();
                Toast.makeText(this, "Password has been updated successfully.", Toast.LENGTH_SHORT).show();
                break;

            case Constants.CHANGE_PASSWORD_FAILED:
                pd.dismiss();
                Toast.makeText(this, "Please enter the correct password", Toast.LENGTH_SHORT).show();
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

        if (!user.image.isEmpty()) {
            decodeImage(user.image);
            FBPreferences.putString(this, "profile_pic", user.image);
        }

        List<AccountData.Follower> followersList = data.follower;
        List<AccountData.Following> followingsList = data.following;

        String followers = followersList.size() + " " + "Followers";
        String followings = followingsList.size() + " " + "Following";

        tvFollowers.setText(followers);
        tvFollowings.setText(followings);
    }

    public void decodeImage(String path) {
        Glide.with(this)
                .load(path)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .placeholder(R.drawable.demo_img)
                .crossFade()
                .centerCrop()
                .into(imgBackground);

        Picasso.with(this)
                .load(path)
                .fit()
                .transform(Utils.imageTransformation())
                .placeholder(R.drawable.demo_img)
                .into(imgProfilePic);
    }

}
