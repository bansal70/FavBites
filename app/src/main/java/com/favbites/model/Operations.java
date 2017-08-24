package com.favbites.model;

/*
 * Created by rishav 8/21/2017.
 */

import android.util.Log;

public class Operations {
    private static final String TAG = Operations.class.getSimpleName();

    public static String getRegistrationParams(String email, String password, String deviceToken, String deviceType) {
        String params = Config.registration_url+"?email=" + email
                +"&password=" + password
                +"&deviceToken=" + deviceToken
                +"&deviceType=" + deviceType;

        Log.i(TAG, "registration params-- "+params);

        return params;
    }

    public static String getLoginParams(String email, String password, String deviceToken, String deviceType) {
        String params = Config.login_url+"?email=" + email
                +"&pass=" + password
                +"&token=" + deviceToken
                +"&deviceType=" + deviceType;

        Log.i(TAG, "login params-- "+params);

        return params;
    }

    public static String getForgotPasswordParams(String email) {
        String params = Config.forgot_password + "?email=" + email;
        Log.i(TAG, "forgot_password params-- "+params);

        return params;
    }

    public static String getGuestUserParams(String deviceToken, String deviceType) {
        String params = Config.guest_login_url + "?deviceToken=" +deviceToken
                + "&deviceType="+deviceType;
        Log.i(TAG, "guest_user params-- "+params);

        return params;
    }

    public static String getSearchRestaurantParams(String search, int page) {
        String params = Config.search_restaurant_url + "?search=" + search
                +"&page="+page;

        Log.i(TAG, "search_restaurant params-- "+params);

        return params;
    }
}