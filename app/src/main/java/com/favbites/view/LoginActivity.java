package com.favbites.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
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
import com.kaopiz.kprogresshud.KProgressHUD;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class LoginActivity extends BaseActivity implements View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener{

    private final String TAG = LoginActivity.class.getSimpleName();
    TextView tvSignIn, tvForgotPassword, tvSkipLogin, tvNewUser;
    EditText editEmail, editPassword;
    private final int RC_SIGN_IN = 1001;
    KProgressHUD dialog;
    Activity activity = this;

    GoogleApiClient mGoogleApiClient;
    GoogleSignInOptions gso;
    ImageView imgGoogleLogin, imgFbLogin;
    CallbackManager callbackManager;
    String deviceToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initGoogleLogin();
        initViews();
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
        deviceToken = FirebaseInstanceId.getInstance().getToken();
        callbackManager = CallbackManager.Factory.create();

        dialog = Utils.showDialog(this);
        tvSignIn = (TextView) findViewById(R.id.tvSignIn);
        tvForgotPassword = (TextView) findViewById(R.id.tvForgotPassword);
        tvNewUser = (TextView) findViewById(R.id.tvNewUser);
        tvSkipLogin = (TextView) findViewById(R.id.tvSkipLogin);
        editEmail = (EditText) findViewById(R.id.editEmail);
        editPassword = (EditText) findViewById(R.id.editPassword);
        imgGoogleLogin = (ImageView) findViewById(R.id.imgGoogleLogin);
        imgFbLogin = (ImageView) findViewById(R.id.imgFbLogin);

        tvForgotPassword.setPaintFlags(tvForgotPassword.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tvSkipLogin.setPaintFlags(tvSkipLogin.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        tvSignIn.setOnClickListener(this);
        tvSkipLogin.setOnClickListener(this);
        tvForgotPassword.setOnClickListener(this);
        tvNewUser.setOnClickListener(this);
        imgGoogleLogin.setOnClickListener(this);
        imgFbLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.tvSignIn:
                loginUser();
                break;

            case R.id.tvSkipLogin:
                dialog.show();
                ModelManager.getInstance().getGuestLoginManager()
                        .loginUser(Operations.getGuestUserParams("token", "A"));
                break;

            case R.id.tvForgotPassword:
                startActivity(new Intent(this, ForgotPasswordActivity.class));
                break;

            case R.id.tvNewUser:
                startActivity(new Intent(this, RegistrationActivity.class));
                break;

            case R.id.imgGoogleLogin:
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
                break;

            case R.id.imgFbLogin:
                ModelManager.getInstance().getFacebookLoginManager().doFacebookLogin(activity, callbackManager);
                break;
        }
    }

    public void loginUser() {
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all the details", Toast.LENGTH_SHORT).show();
        } else if(!Utils.emailValidator(email)){
            Toast.makeText(this, "Please enter the valid email address.", Toast.LENGTH_SHORT).show();
        } else {
            dialog.show();
            ModelManager.getInstance().getLoginManager()
                    .loginUser(this, Operations.getLoginParams(email, password, deviceToken, "A"));
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
            case Constants.LOGIN_SUCCESS:
                dialog.dismiss();
                Toast.makeText(this, "" + event.getValue(), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, RestaurantsActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
                break;

            case Constants.LOGIN_FAILED:
                dialog.dismiss();
                Toast.makeText(this, "" + event.getValue(), Toast.LENGTH_SHORT).show();
                break;

            case Constants.GUEST_LOGIN_SUCCESS:
                dialog.dismiss();
                startActivity(new Intent(activity, RestaurantsActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
                break;

            case Constants.GUEST_LOGIN_FAILED:
                dialog.dismiss();
                Toast.makeText(activity, ""+event.getValue(), Toast.LENGTH_SHORT).show();
                break;

            case Constants.NO_RESPONSE:
                dialog.dismiss();
                Toast.makeText(this, "" + event.getValue(), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        } else if (resultCode == RESULT_OK) {
            dialog.show();
            ModelManager.getInstance().getFacebookLoginManager().getFacebookData(activity);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.i(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            dialog.show();
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
