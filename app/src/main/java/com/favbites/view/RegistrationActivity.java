package com.favbites.view;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.favbites.R;
import com.favbites.controller.ModelManager;
import com.favbites.model.Constants;
import com.favbites.model.Event;
import com.favbites.model.Operations;
import com.favbites.model.Utils;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.iid.FirebaseInstanceId;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class RegistrationActivity extends BaseActivity implements View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener {

    EditText editFirstName, editLastName, editEmail, editPassword, editConfirmPassword;
    TextView tvSignUp;
    CheckBox cbTerms;
    Dialog pd;
    private CoordinatorLayout coordinatorLayout;
    ImageView imgBack;

    ImageView imgGoogleLogin, imgFbLogin;
    GoogleApiClient mGoogleApiClient;
    GoogleSignInOptions gso;
    private final int RC_SIGN_IN = 1001;
    CallbackManager callbackManager;
    String deviceToken = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        initViews();
        initGoogleLogin();
    }

    public void initGoogleLogin() {
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    public void initViews() {
        callbackManager = CallbackManager.Factory.create();
        deviceToken = FirebaseInstanceId.getInstance().getToken();

        pd = Utils.showDialog(this);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayout);

        editFirstName = (EditText) findViewById(R.id.editFirstName);
        editLastName = (EditText) findViewById(R.id.editLastName);
        editEmail = (EditText) findViewById(R.id.editEmail);
        editPassword = (EditText) findViewById(R.id.editPassword);
        editConfirmPassword = (EditText) findViewById(R.id.editConfirmPassword);
        imgGoogleLogin = (ImageView) findViewById(R.id.imgGoogleLogin);
        imgFbLogin = (ImageView) findViewById(R.id.imgFbLogin);

        tvSignUp = (TextView) findViewById(R.id.tvSignUp);
        cbTerms = (CheckBox) findViewById(R.id.cbTerms);
        imgBack = (ImageView) findViewById(R.id.imgBack);

        tvSignUp.setOnClickListener(this);
        imgBack.setOnClickListener(this);
        imgGoogleLogin.setOnClickListener(this);
        imgFbLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.tvSignUp:
                registerUser();
                break;

            case R.id.imgGoogleLogin:
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
                break;

            case R.id.imgFbLogin:
                ModelManager.getInstance().getFacebookLoginManager().doFacebookLogin(this, callbackManager);
                break;

            case R.id.imgBack:
                finish();
                break;
        }
    }

    public void registerUser() {
        String firstName = editFirstName.getText().toString().trim();
        String lastName = editLastName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        String confirmPassword = editConfirmPassword.getText().toString().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()
                || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all the details", Toast.LENGTH_SHORT).show();
        } else if(!password.equals(confirmPassword)) {
            Toast.makeText(this, "Password didn't match", Toast.LENGTH_SHORT).show();
        } else if (password.length() < 6) {
            Toast.makeText(this, "Password must contains at least 6 letters.", Toast.LENGTH_SHORT).show();
        } else if(!Utils.emailValidator(email)){
            Toast.makeText(this, "Please enter the valid email address.", Toast.LENGTH_SHORT).show();
        }else if (!cbTerms.isChecked()) {
            Toast.makeText(this, "Please accept all the terms and conditions.", Toast.LENGTH_SHORT).show();
        } else {
            pd.show();

            ModelManager.getInstance().getRegistrationManager().registerUser(
                    Operations.getRegistrationParams(firstName, lastName, email, password, deviceToken, "A"));
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
    public void onEvent(Event event){
        switch (event.getKey()) {
            case Constants.REGISTRATION_SUCCESS:
                pd.dismiss();
                Toast.makeText(this, ""+event.getValue(), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
                break;

            case Constants.REGISTRATION_FAILED:
                pd.dismiss();
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, ""+event.getValue(), Snackbar.LENGTH_LONG);
                snackbar.show();
               // Toast.makeText(this, ""+event.getValue(), Toast.LENGTH_SHORT).show();
                break;

            case Constants.LOGIN_SUCCESS:
                pd.dismiss();
                Toast.makeText(this, "" + event.getValue(), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, RestaurantsActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
                break;

            case Constants.LOGIN_FAILED:
                pd.dismiss();
                Toast.makeText(this, "" + event.getValue(), Toast.LENGTH_SHORT).show();
                break;

            case Constants.NO_RESPONSE:
                pd.dismiss();
                Toast.makeText(this, ""+event.getValue(), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        } else if (resultCode == RESULT_OK) {
            pd.show();
            ModelManager.getInstance().getFacebookLoginManager().getFacebookData(this);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            pd.show();
            GoogleSignInAccount acct = result.getSignInAccount();
            if (acct != null) {
                String name = acct.getDisplayName();
                String email = acct.getEmail();
                String id = acct.getId();
                String photo = "";
                String firstName="", lastName="";
                if (name != null) {
                    String[] split = name.split(" ");
                    firstName = split[0];
                    lastName = split[1];
                }
                if (acct.getPhotoUrl() != null)
                    photo = acct.getPhotoUrl().toString();

                ModelManager.getInstance().getLoginManager()
                        .loginUser(this, Operations.getSocialLoginParams(firstName, lastName, email,
                                id, deviceToken, "A", photo));
            }
        } else {
            Toast.makeText(this, "Google sign-in failed. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
