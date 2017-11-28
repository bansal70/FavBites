package co.fav.bites.models.beans;

/*
 * Created by rishav on 9/1/2017.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BookmarkData {
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
        @SerializedName("Bookmark")
        @Expose
        public Bookmark bookmark;
        @SerializedName("Restaurant")
        @Expose
        public Restaurant restaurant;
    }

    public class Bookmark {
        @SerializedName("id")
        @Expose
        public String id;
        @SerializedName("user_id")
        @Expose
        public String userId;
        @SerializedName("restaurant_id")
        @Expose
        public String restaurantId;
        @SerializedName("created")
        @Expose
        public String created;
        @SerializedName("modified")
        @Expose
        public String modified;
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
        @SerializedName("item_rating")
        @Expose
        public String rating;
    }
}
