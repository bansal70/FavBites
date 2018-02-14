package co.fav.bites.views;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.fav.bites.R;
import co.fav.bites.models.Constants;
import co.fav.bites.models.FBPreferences;
import co.fav.bites.models.LocationFetcher;
import co.fav.bites.models.Utils;
import co.fav.bites.views.activities.RestaurantsHomeActivity;


public class SplashActivity extends BaseActivity{

    private static final int PERMISSION_REQUEST_CODE = 101;
    private Handler handler;
    private Runnable runnable;
    private LocationFetcher mLocationFetch = new LocationFetcher();
    private String user_id;

    @BindView(R.id.locationLayout)
    LinearLayout locationLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        user_id = FBPreferences.readString(this, "user_id");

        mLocationFetch.setOnLocationFetchListener(mLocation -> {
            handleSleep();
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
            } else {
                mLocationFetch.getLocation(this);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE && Utils.hasAllPermissionsGranted(this, grantResults)) {
            mLocationFetch.getLocation(this);
        } else {
            handleSleep();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.SETTINGS_REQUEST_CODE:
                switch (resultCode) {
                    case Activity.RESULT_OK:
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

        runnable = this::getCurrentLocation;
        handler.postDelayed(runnable, Constants.SPLASH_TIMEOUT);
    }

    public void getCurrentLocation() {
        if (Utils.isGPS(this)) {
            locationLayout.setVisibility(View.VISIBLE);
        }

        Handler handler = new Handler();
        handler.postDelayed(this::openMains, 3000);
    }

    private void openMains() {
        if (handler != null)
            handler.removeCallbacks(runnable);

        if (user_id == null || user_id.isEmpty())
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        else
            startActivity(new Intent(SplashActivity.this, RestaurantsHomeActivity.class));

        finish();
        Utils.gotoNextActivityAnimation(this);
    }

    @Override
    public void onBackPressed() {
        if (handler != null)
            handler.removeCallbacks(runnable);
        finish();
        Utils.gotoPreviousActivityAnimation(this);
    }
}
