package com.favbites.controller;

/*
 * Created by Rishav on 8/18/2017.
 */

public class ModelManager {

    private static ModelManager modelManager;
    private RegistrationManager registrationManager;
    private LoginManager loginManager;
    private ForgotPasswordManager forgotPasswordManager;
    private LocationManager locationManager;
    private GuestLoginManager guestLoginManager;
    private RestaurantsManager restaurantsManager;

    public static ModelManager getInstance() {
        if (modelManager == null)
            return modelManager = new ModelManager();
        else
            return modelManager;
    }

    private ModelManager() {
        registrationManager = new RegistrationManager();
        loginManager = new LoginManager();
        forgotPasswordManager = new ForgotPasswordManager();
        locationManager = new LocationManager();
        guestLoginManager = new GuestLoginManager();
        restaurantsManager = new RestaurantsManager();
    }

    public RegistrationManager getRegistrationManager() {
        return registrationManager;
    }

    public LoginManager getLoginManager() {
        return loginManager;
    }

    public ForgotPasswordManager getForgotPasswordManager() {
        return forgotPasswordManager;
    }

    public LocationManager getLocationManager() {
        return locationManager;
    }

    public GuestLoginManager getGuestLoginManager() {
        return guestLoginManager;
    }

    public RestaurantsManager getRestaurantsManager() {
        return restaurantsManager;
    }
}
