package com.favbites.controller;

/*
 * Created by rishav on 8/22/2017.
 */

import android.location.Location;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class LocationManager implements com.google.android.gms.location.LocationListener {

    private static final String TAG = LocationManager.class.getSimpleName();
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    public void requestLocation(GoogleApiClient googleApiClient, LocationRequest locationRequest) {
        mGoogleApiClient = googleApiClient;
        mLocationRequest = locationRequest;
        startLocationUpdates();
    }

    @SuppressWarnings("MissingPermission")
    private void startLocationUpdates() {

        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient,
                    mLocationRequest,
                    this).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {

                }
            });
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        getLocation(location);
    }

    @SuppressWarnings("MissingPermission")
    private void getLocation(Location location) {
        if (location != null) {
            stopLocationUpdates();
            String latitude = String.valueOf(location.getLatitude());
            String longitude = String.valueOf(location.getLongitude());
            Log.e(TAG, "latitude: "+ latitude + "\nlongitude: " + longitude);
        } else {
            Log.e(TAG, "Unable to get the current location");
        }
    }

    private void stopLocationUpdates() {
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient,
                    this
            ).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                }
            });
            mGoogleApiClient.disconnect();
        }
    }
}
