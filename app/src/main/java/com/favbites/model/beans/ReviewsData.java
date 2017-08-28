package com.favbites.model.beans;

/*
 * Created by rishav on 8/23/2017.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReviewsData {
    @SerializedName("mesg")
    @Expose
    public String mesg;
    @SerializedName("responce")
    @Expose
    public String responce;
    @SerializedName("data")
    @Expose
    public List<Datum> data = null;
    @SerializedName("rating")
    @Expose
    public String rating;

    public class Datum {
        @SerializedName("Review")
        @Expose
        public Review review;
        @SerializedName("User")
        @Expose
        public User user;
        @SerializedName("Subitem")
        @Expose
        public Subitem subitem;
    }

    public class Review {
        @SerializedName("id")
        @Expose
        public String id;
        @SerializedName("restaurant_id")
        @Expose
        public String restaurantId;
        @SerializedName("dish_id")
        @Expose
        public String dishId;
        @SerializedName("message")
        @Expose
        public String message;
        @SerializedName("created")
        @Expose
        public String created;
        @SerializedName("modified")
        @Expose
        public String modified;
        @SerializedName("rating")
        @Expose
        public String rating;
        @SerializedName("user_id")
        @Expose
        public String userId;
    }

    public class User {
        @SerializedName("id")
        @Expose
        public String id;
        @SerializedName("username")
        @Expose
        public String username;
        @SerializedName("password")
        @Expose
        public String password;
        @SerializedName("email")
        @Expose
        public String email;
        @SerializedName("fname")
        @Expose
        public String fname;
        @SerializedName("lname")
        @Expose
        public String lname;
        @SerializedName("phone")
        @Expose
        public Object phone;
        @SerializedName("userType")
        @Expose
        public String userType;
        @SerializedName("image")
        @Expose
        public Object image;
        @SerializedName("status")
        @Expose
        public String status;
        @SerializedName("forgot_key")
        @Expose
        public String forgotKey;
        @SerializedName("activationKey")
        @Expose
        public Object activationKey;
        @SerializedName("deviceToken")
        @Expose
        public String deviceToken;
        @SerializedName("deviceType")
        @Expose
        public String deviceType;
        @SerializedName("created")
        @Expose
        public String created;
        @SerializedName("modified")
        @Expose
        public String modified;
        @SerializedName("social_id")
        @Expose
        public String socialId;
    }


    public class Subitem {

        @SerializedName("id")
        @Expose
        public String id;
        @SerializedName("key")
        @Expose
        public String key;
        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("description")
        @Expose
        public String description;
        @SerializedName("basePrice")
        @Expose
        public String basePrice;
        @SerializedName("created")
        @Expose
        public String created;
        @SerializedName("modified")
        @Expose
        public String modified;
        @SerializedName("item_id")
        @Expose
        public String itemId;
        @SerializedName("restaurant_id")
        @Expose
        public String restaurantId;

    }
}
