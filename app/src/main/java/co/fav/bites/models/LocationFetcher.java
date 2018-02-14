package co.fav.bites.models;

/*
 * Created by rishav on 2/9/2018.
 */

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;

import timber.log.Timber;

import static co.fav.bites.models.Constants.SETTINGS_REQUEST_CODE;

public class LocationFetcher {

    private static final int TIME_INTERVAL = 10 * 1000;
    private static final int FASTEST_TIME_INTERVAL = 5 * 1000;
    private final int PERMISSION_REQUEST_CODE = 1001;
    private Location mLastLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private OnLocationFetchListener onLocationFetchListener;

    private void settingRequest(Activity mActivity) {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(TIME_INTERVAL);    // 10 seconds, in milliseconds
        mLocationRequest.setFastestInterval(FASTEST_TIME_INTERVAL);   // 5 second, in milliseconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(mActivity);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(mActivity, locationSettingsResponse -> {
            // All location settings are satisfied. The client can initialize
            // location requests here.
            getLocation(mActivity);
        });

        task.addOnFailureListener(mActivity, (Exception e) -> {
            int statusCode = ((ApiException) e).getStatusCode();
            switch (statusCode) {
                case CommonStatusCodes.RESOLUTION_REQUIRED:
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(mActivity, SETTINGS_REQUEST_CODE);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    // Location settings are not satisfied. However, we have no way
                    // to fix the settings so we won't show the dialog.
                    break;
            }
        });
    }

    public void getLocation(Activity mActivity) {
        LocationManager manager = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mActivity);

        if (manager == null) {
            if (onLocationFetchListener != null) {
                onLocationFetchListener.onLocationFetch(null);
            }
            return;
        }

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            settingRequest(mActivity);
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(mActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(mActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                mActivity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
                return;
            }
        }

        /*Getting the location after acquiring location service*/
        mFusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            // Got last known location. In some rare situations this can be null.
            if (location != null) {
                mLastLocation = location;
                if (onLocationFetchListener != null) {
                    onLocationFetchListener.onLocationFetch(mLastLocation);
                }
            } else {
                Timber.e("No data for current location");
                if (onLocationFetchListener != null) {
                    onLocationFetchListener.onLocationFetch(null);
                }
                //mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
            }
        });

        mFusedLocationClient.getLastLocation().addOnFailureListener(e -> requestLocationUpdates(mActivity));
    }

    public void requestLocationUpdates(Activity mActivity) {
        LocationManager manager = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mActivity);

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(TIME_INTERVAL);    // 10 seconds, in milliseconds
        mLocationRequest.setFastestInterval(FASTEST_TIME_INTERVAL);   // 5 second, in milliseconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        Timber.e("Location address %s",
                                Utils.getCompleteAddressString(mActivity, location.getLatitude(), location.getLongitude()));
                        if (onLocationFetchListener != null) {
                            onLocationFetchListener.onLocationFetch(location);
                            removeLocationUpdates();
                        }
                    }
                }
            };
        };

        if (manager != null && !manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            settingRequest(mActivity);
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(mActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(mActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                mActivity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
                return;
            }
        }

        if (ActivityCompat.checkSelfPermission(mActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Timber.e("Permissions not granted");
            return;
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
    }

    public interface OnLocationFetchListener {
        void onLocationFetch(Location mLocation);
    }

    public void setOnLocationFetchListener(OnLocationFetchListener onLocationFetchListener) {
        this.onLocationFetchListener = onLocationFetchListener;
    }

    public void removeLocationUpdates() {
        if (mFusedLocationClient != null && mLocationCallback != null)
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }
}
