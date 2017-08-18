package com.favbites.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.favbites.R;
import com.favbites.model.Utils;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.kaopiz.kprogresshud.KProgressHUD;

public class LoginActivity extends BaseActivity implements View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener{

    private final String TAG = LoginActivity.class.getSimpleName();
    TextView tvSignIn, tvForgotPassword, tvSkipLogin;
    ImageView imgGoogleLogin;
    GoogleApiClient mGoogleApiClient;
    GoogleSignInOptions gso;
    private final int RC_SIGN_IN = 1001;
    KProgressHUD dialog;
    Activity activity = this;

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
        dialog = Utils.showDialog(this);
        tvSignIn = (TextView) findViewById(R.id.tvSignIn);
        tvForgotPassword = (TextView) findViewById(R.id.tvForgotPassword);
        tvSkipLogin = (TextView) findViewById(R.id.tvSkipLogin);
        imgGoogleLogin = (ImageView) findViewById(R.id.imgGoogleLogin);

        tvForgotPassword.setPaintFlags(tvForgotPassword.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tvSkipLogin.setPaintFlags(tvSkipLogin.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        tvSkipLogin.setOnClickListener(this);
        imgGoogleLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvSkipLogin:
                startActivity(new Intent(activity, RestaurantsActivity.class));
                break;

            case R.id.imgGoogleLogin:
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.i(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            dialog.show();
            Toast.makeText(this, "Successfully signed-in.", Toast.LENGTH_SHORT).show();
            GoogleSignInAccount acct = result.getSignInAccount();
            if (acct != null) {
                Log.i(TAG, "Account name: "+acct.getDisplayName());
                Log.i(TAG, "Account email: "+acct.getEmail());
                Log.i(TAG, "Account id: "+acct.getId());
                Log.i(TAG, "Account profile pic url: "+acct.getPhotoUrl());
                startActivity(new Intent(activity, RestaurantsActivity.class));
                finish();
            }

        } else {
            Toast.makeText(this, "Google sign-in failed. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
