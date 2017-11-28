package co.fav.bites.controller;

/*
 * Created by Rishav on 8/18/2017.
 */

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

public class GoogleLoginManager implements GoogleApiClient.OnConnectionFailedListener{
    private FragmentActivity context;
    GoogleApiClient mGoogleApiClient;
    private GoogleSignInOptions gso;

    public GoogleLoginManager(FragmentActivity context) {
        this.context = context;
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
    }

    public void googleLogin() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .enableAutoManage(context, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

}
