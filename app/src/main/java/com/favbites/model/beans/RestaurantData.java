package com.favbites.model.beans;

/*
 * Created by rishav on 8/22/2017.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RestaurantData {
    @SerializedName("mesg")
    @Expose
    public String mesg;
    @SerializedName("response")
    @Expose
    public String response;
    @SerializedName("data")
    @Expose
    public List<Datum> data;
    @SerializedName("totalpages")
    @Expose
    public Integer totalPages;

    public class Food {

        @SerializedName("id")
        @Expose
        public String id;
        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("created")
        @Expose
        public String created;
        @SerializedName("modified")
        @Expose
        public String modified;
        @SerializedName("restaurant_id")
        @Expose
        public String restaurantId;

    }

    public class Hour {

        @SerializedName("id")
        @Expose
        public String id;
        @SerializedName("day")
        @Expose
        public String day;
        @SerializedName("timinig")
        @Expose
        public String timinig;
        @SerializedName("created")
        @Expose
        public String created;
        @SerializedName("modified")
        @Expose
        public String modified;
        @SerializedName("restaurant_id")
        @Expose
        public String restaurantId;
    }

    public class Item {

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
        @SerializedName("created")
        @Expose
        public String created;
        @SerializedName("modified")
        @Expose
        public String modified;
        @SerializedName("restaurant_id")
        @Expose
        public String restaurantId;
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
        @SerializedName("rating")
        @Expose
        public String rating;
        @SerializedName("reviewCount")
        @Expose
        public int reviewCount;

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
        public String deliveryTime;
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
        @SerializedName("isOpen")
        @Expose
        public String isOpen;
        @SerializedName("bookmark")
        @Expose
        public String bookmark;
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
        @SerializedName("rating")
        @Expose
        public String rating;
        @SerializedName("user_id")
        @Expose
        public String userId;

    }

    public class Bookmark {
        @SerializedName("restaurant_id")
        @Expose
        public Restaurant restaurant_id;
    }

    public class Datum {
        @SerializedName("Restaurant")
        @Expose
        public Restaurant restaurant;
        @SerializedName("Food")
        @Expose
        public List<Food> food = null;
        @SerializedName("Hour")
        @Expose
        public List<Hour> hour = null;
        @SerializedName("Comment")
        @Expose
        public List<Object> comment = null;
        @SerializedName("Review")
        @Expose
        public List<Object> review = null;
        @SerializedName("Item")
        @Expose
        public List<Item> item = null;
        @SerializedName("Subitem")
        @Expose
        public List<Subitem> subitem = null;
        @SerializedName("Bookmark")
        @Expose
        public List<Object> bookmark = null;
    }

}
