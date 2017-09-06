package com.favbites.model;

/*
 * Created by rishav 8/21/2017.
 */

import android.util.Log;

public class Operations {
    private static final String TAG = Operations.class.getSimpleName();

    public static String getRegistrationParams(String firstName, String lastName, String email,
                                               String password, String deviceToken, String deviceType) {
        String params = Config.registration_url + "?fname=" + firstName
                +"&lname=" + lastName
                +"&email=" + email
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

    public static String getFavRestaurantsParams(String search, int page) {
        String params = Config.fav_restaurant_url + "?search=" + search
                +"&page="+page;

        Log.i(TAG, "fav_restaurant params-- "+params);

        return params;
    }

    public static String getCheckInRestaurantsParams(String search, int page) {
        String params = Config.check_restaurant_url + "?search=" + search
                +"&page="+page;

        Log.i(TAG, "check_in_restaurant params-- "+params);

        return params;
    }

    public static String getRestaurantDetailsParams(String restaurant_id, String user_id) {
        String params = Config.restaurant_details_url + "?restaurant_id=" + restaurant_id
                +"&user_id="+user_id;

        Log.e(TAG, "restaurant_details params-- "+params);

        return params;
    }

    public static String getItemReviews(int restaurant_id, int dish_id, String user_id) {
        String params = Config.dish_reviews_url + "?restaurant_id=" + restaurant_id
                +"&dish_id=" + dish_id
                +"&user_id=" + user_id;

        Log.i(TAG, "dish_reviews params-- "+params);

        return params;
    }

    public static String addItemReview(String user_id, int restaurant_id, int dish_id, String rating, String comment) {
        String params = Config.add_dish_reviews_url + "?user_id=" + user_id
                +"&restaurant_id=" + restaurant_id
                +"&dish_id=" + dish_id
                +"&rating=" + rating
                +"&comment=" + comment;

        Log.e(TAG, "add dish_review params-- "+params);

        return params;
    }

    public static String bookmarkRestaurant(String user_id, String restaurant_id, String status) {
        String params = Config.bookmark_url + "?user_id=" + user_id
                +"&restaurant_id=" + restaurant_id
                +"&status=" + status;

        Log.i(TAG, "bookmark_restaurant params-- "+params);

        return params;
    }

    public static String getBookmarkRestaurantsParams(String user_id) {
        String params = Config.bookmark_restaurants_url + "?user_id=" + user_id;

        Log.e(TAG, "bookmark_restaurant params-- "+params);

        return params;
    }

    public static String uploadPhoto(String user_id, String restaurant_id, String comment) {
        String params = Config.upload_photo_url + "?user_id=" + user_id
                +"&restaurant_id=" + restaurant_id
                +"&comment=" + comment;

        Log.i(TAG, "upload_photo params-- "+params);

        return params;
    }

    public static String logoutParams(String user_id) {
        String params = Config.logout_url + "?user_id=" + user_id;

        Log.e(TAG, "logout_photo params-- "+params);
        return params;
    }

    public static String profileParams(String user_id) {
        String params = Config.profile_url + "?id=" + user_id;

        Log.e(TAG, "user_profile params-- "+params);
        return params;
    }

    public static String updateProfileParams(String user_id, String first_name,
                                             String last_name, String email) {
        String params = Config.update_profile_url + "?user_id=" + user_id
                +"&fname=" + first_name
                +"&lname=" + last_name
                +"&email=" + email;

        Log.e(TAG, "update_profile params-- "+params);
        return params;
    }

    public static String changePasswordParams(String user_id, String oldPassword, String newPassword) {
        String params = Config.update_password_url + "?user_id=" + user_id
                +"&oldPass=" + oldPassword
                +"&newPass=" + newPassword;

        Log.e(TAG, "change_password params-- "+params);
        return params;
    }

    //status ==> 1=>follow, 2=>un-follow
    public static String followUserParams(String follow_id, String follower_id, String status) {
        String params = Config.follow_user_url + "?follow_to=" + follow_id
                +"&follower=" + follower_id + "&status=" + status;

        Log.e(TAG, "follow_user params-- "+params);
        return params;
    }

    public static String followersParams(String user_id) {
        String params = Config.follower_url + "?id=" + user_id;

        Log.e(TAG, "followers param-- "+params);
        return params;
    }

    public static String followingParams(String user_id) {
        String params = Config.following_url + "?id=" + user_id;

        Log.e(TAG, "following params-- "+params);
        return params;
    }

    public static String userPostsParams(String user_id, int page) {
        String params = Config.user_posts_url + "?user_id=" + user_id
                +"&page="+page;

        Log.e(TAG, "user_posts params-- "+params);
        return params;
    }

    public static String userReviewsParams(String user_id, int page) {
        String params = Config.user_reviews_url + "?user_id=" + user_id
                +"&page="+page;

        Log.e(TAG, "user_reviews params-- "+params);
        return params;
    }
}
