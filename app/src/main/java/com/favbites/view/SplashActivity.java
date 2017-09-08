package com.favbites.view;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.LinearLayout;

import com.favbites.R;
import com.favbites.controller.ModelManager;
import com.favbites.model.Constants;
import com.favbites.model.Event;
import com.favbites.model.FBPreferences;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.iid.FirebaseInstanceId;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class SplashActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private static final int PERMISSION_REQUEST_CODE = 101;
    GoogleApiClient mGoogleApiClient;
    private Handler handler;
    private Runnable runnable;
    private LinearLayout locationLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
        }

        if (!mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();

        enableLoc();
    }

    private void enableLoc() {
        FirebaseInstanceId.getInstance().getToken();
        locationLayout = (LinearLayout) findViewById(R.id.locationLayout);

        FBPreferences.removeKey(this, "location");
        if (!mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
            } else {
                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                    ModelManager.getInstance().getLocationManager()
                            .requestLocation(this, mGoogleApiClient);
                else
                    handleSleep();
            }
        } else {
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                ModelManager.getInstance().getLocationManager()
                    .requestLocation(this, mGoogleApiClient);
            else
                handleSleep();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                    ModelManager.getInstance().getLocationManager()
                            .requestLocation(this, mGoogleApiClient);
                else
                    handleSleep();
            } else {
                handleSleep();
            }
        } else {
            handleSleep();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case com.favbites.controller.LocationManager.REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                 //       ModelManager.getInstance().getLocationManager().startLocationUpdates(activity, mGoogleApiClient);
                        handleSleep();
                        break;
                    case Activity.RESULT_CANCELED:
                        handleSleep();
                        break;
                }
                break;
        }
    }

    public void handleSleep() {
        handler = new Handler();

        runnable = new Runnable() {
            @Override
            public void run() {
                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(SplashActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        ModelManager.getInstance().getLocationManager()
                                .startLocationUpdates(SplashActivity.this, mGoogleApiClient);
                    }
                }*/
                getCurrentLocation();
            }
        };
        handler.postDelayed(runnable, Constants.SPLASH_TIMEOUT);
    }

    public void getCurrentLocation() {
        locationLayout.setVisibility(View.VISIBLE);
        ModelManager.getInstance().getLocationManager()
                .startLocationUpdates(this, mGoogleApiClient);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (FBPreferences.readString(SplashActivity.this, "location").isEmpty())
                                     EventBus.getDefault().post(new Event(Constants.LOCATION_EMPTY, ""));
            }
        }, 3000);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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
            case Constants.LOCATION_SUCCESS:
                if (handler != null)
                    handler.removeCallbacks(runnable);

                if (FBPreferences.readString(SplashActivity.this, "user_id").isEmpty())
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                else
                    startActivity(new Intent(SplashActivity.this, RestaurantsActivity.class));
                finish();
                break;

            case Constants.LOCATION_EMPTY:
                if (handler != null)
                    handler.removeCallbacks(runnable);

                if (FBPreferences.readString(SplashActivity.this, "user_id").isEmpty())
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                else
                    startActivity(new Intent(SplashActivity.this, RestaurantsActivity.class));
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (handler != null)
            handler.removeCallbacks(runnable);
        finish();
    }
}
