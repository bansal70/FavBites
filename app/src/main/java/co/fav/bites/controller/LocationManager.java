package co.fav.bites.controller;

/*
 * Created by rishav on 8/22/2017.
 */

import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import org.greenrobot.eventbus.EventBus;

import co.fav.bites.models.Constants;
import co.fav.bites.models.Event;
import co.fav.bites.models.FBPreferences;
import co.fav.bites.models.Utils;

public class LocationManager implements com.google.android.gms.location.LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = LocationManager.class.getSimpleName();
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Activity context;
    public static final int REQUEST_CHECK_SETTINGS = 1212;
    private boolean gotLocation = true;

    public void requestLocation(Activity context, GoogleApiClient googleApiClient) {
        this.context = context;
        mGoogleApiClient = googleApiClient;

        checkLocationSettings();
    }

    private void checkLocationSettings() {
        if (!mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10 * 1000);
        mLocationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(result1 -> {
            final Status status = result1.getStatus();
            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    try {
                        status.startResolutionForResult(context, REQUEST_CHECK_SETTINGS);
                    } catch (Exception e) {
                        // Ignore the error.
                        e.printStackTrace();
                        Log.e("TAG", "Message: " + e.getMessage());
                    }
                    break;
            }
        });
    }

    @SuppressWarnings("MissingPermission")
    public void startLocationUpdates(Activity context, GoogleApiClient googleApiClient) {
        mGoogleApiClient = googleApiClient;
        this.context = context;
        gotLocation = true;
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10 * 1000);
        mLocationRequest.setFastestInterval(1000);

        if (!mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();
        else {

            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient,
                    mLocationRequest,
                    this).setResultCallback(status -> {

                    });
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        getLocation(location);
    }

    private void getLocation(Location location) {
        if (location != null && gotLocation) {
            stopLocationUpdates();
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            String address = Utils.getCompleteAddressString(context, latitude, longitude);
            FBPreferences.putString(context, "location", address);
            Log.e(TAG, "latitude: "+ latitude + "\nlongitude: " + longitude);

            FBPreferences.putDouble(context, "latitude", latitude);
            FBPreferences.putDouble(context, "longitude", longitude);

            gotLocation = false;
            EventBus.getDefault().postSticky(new Event(Constants.LOCATION_SUCCESS, address));
        }
    }

    private void stopLocationUpdates() {
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient,
                    this);
            mGoogleApiClient.disconnect();
        }
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
}