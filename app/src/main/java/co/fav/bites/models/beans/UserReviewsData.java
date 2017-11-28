package co.fav.bites.models.beans;

/*
 * Created by rishav on 9/5/2017.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserReviewsData {
    @SerializedName("mesg")
    @Expose
    public String mesg;
    @SerializedName("response")
    @Expose
    public String response;
    @SerializedName("data")
    @Expose
    public List<Data> data = null;

    public class Data {
        @SerializedName("Review")
        @Expose
        public Review review;
        @SerializedName("Subitem")
        @Expose
        public Subitem subitem;
        @SerializedName("User")
        @Expose
        public User user;
        @SerializedName("Restaurant")
        @Expose
        public Restaurant restaurant;
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
        @SerializedName("is_admin")
        @Expose
        public String isAdmin;
    }

    public class Restaurant {

        @SerializedName("id")
        @Expose
        public String id;
        @SerializedName("key")
        @Expose
        public String key;
        @SerializedName("delivry_time")
        @Expose
        public String delivryTime;
        @SerializedName("logoUrl")
        @Expose
        public String logoUrl;
        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("city")
        @Expose
        public String city;
        @SerializedName("state")
        @Expose
        public String state;
        @SerializedName("zip")
        @Expose
        public String zip;
        @SerializedName("phone")
        @Expose
        public String phone;
        @SerializedName("acceptsCard")
        @Expose
        public String acceptsCard;
        @SerializedName("acceptsCash")
        @Expose
        public String acceptsCash;
        @SerializedName("isTestRestaurant")
        @Expose
        public String isTestRestaurant;
        @SerializedName("latitude")
        @Expose
        public String latitude;
        @SerializedName("longitude")
        @Expose
        public String longitude;
        @SerializedName("maxWaitTime")
        @Expose
        public String maxWaitTime;
        @SerializedName("minFreeDelivery")
        @Expose
        public String minFreeDelivery;
        @SerializedName("minWaitTime")
        @Expose
        public String minWaitTime;
        @SerializedName("offersDelivery")
        @Expose
        public String offersDelivery;
        @SerializedName("offersPickup")
        @Expose
        public String offersPickup;
        @SerializedName("open")
        @Expose
        public String open;
        @SerializedName("streetAddress")
        @Expose
        public String streetAddress;
        @SerializedName("taxRate")
        @Expose
        public String taxRate;
        @SerializedName("timezone")
        @Expose
        public String timezone;
        @SerializedName("url")
        @Expose
        public String url;
        @SerializedName("created")
        @Expose
        public String created;
        @SerializedName("modified")
        @Expose
        public String modified;
        @SerializedName("is_admin")
        @Expose
        public String isAdmin;
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
        public String phone;
        @SerializedName("userType")
        @Expose
        public String userType;
        @SerializedName("image")
        @Expose
        public String image;
        @SerializedName("status")
        @Expose
        public String status;
        @SerializedName("forgot_key")
        @Expose
        public String forgotKey;
        @SerializedName("activationKey")
        @Expose
        public String activationKey;
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
}
