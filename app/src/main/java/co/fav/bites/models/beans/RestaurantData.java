package co.fav.bites.models.beans;

/*
 * Created by rishav on 8/22/2017.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

public class RestaurantData {
    @SerializedName("mesg")
    public String mesg;
    @SerializedName("response")
    public String response;
    @SerializedName("data")
    public List<Datum> data = new ArrayList<>();
    @SerializedName("totalpages")
    public Integer totalPages;

    public class Food {

        @SerializedName("id")
        public String id;
        @SerializedName("name")
        public String name;
        @SerializedName("created")
        public String created;
        @SerializedName("modified")
        public String modified;
        @SerializedName("restaurant_id")
        public String restaurantId;

    }

    public class Hour {

        @SerializedName("id")
        public String id;
        @SerializedName("day")
        public String day;
        @SerializedName("timinig")
        public String timinig;
        @SerializedName("created")
        public String created;
        @SerializedName("modified")
        public String modified;
        @SerializedName("restaurant_id")
        public String restaurantId;
    }

    public class Item {

        @SerializedName("id")
        public String id;
        @SerializedName("key")
        public String key;
        @SerializedName("name")
        public String name;
        @SerializedName("description")
        public String description;
        @SerializedName("created")
        public String created;
        @SerializedName("modified")
        public String modified;
        @SerializedName("restaurant_id")
        public String restaurantId;
    }

    @Parcel
    public static class Subitem {
        @SerializedName("id")
        public String id;
        @SerializedName("key")
        public String key;
        @SerializedName("name")
        public String name;
        @SerializedName("description")
        public String description;
        @SerializedName("basePrice")
        public String basePrice;
        @SerializedName("created")
        public String created;
        @SerializedName("modified")
        public String modified;
        @SerializedName("item_id")
        public String itemId;
        @SerializedName("restaurant_id")
        public String restaurantId;
        @SerializedName("rating")
        public String rating;
        @SerializedName("reviewCount")
        public int reviewCount;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getRestaurantId() {
            return restaurantId;
        }

        public void setRestaurantId(String restaurantId) {
            this.restaurantId = restaurantId;
        }

        public String getRating() {
            return rating;
        }

        public void setRating(String rating) {
            this.rating = rating;
        }

        public int getReviewCount() {
            return reviewCount;
        }

        public void setReviewCount(int reviewCount) {
            this.reviewCount = reviewCount;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getBasePrice() {
            return basePrice;
        }

        public void setBasePrice(String basePrice) {
            this.basePrice = basePrice;
        }
    }

    public class Restaurant {

        @SerializedName("id")
        public String id;
        @SerializedName("key")
        public String key;
        @SerializedName("delivry_time")
        public String deliveryTime;
        @SerializedName("logoUrl")
        public String logoUrl;
        @SerializedName("name")
        public String name;
        @SerializedName("city")
        public String city;
        @SerializedName("state")
        public String state;
        @SerializedName("zip")
        public String zip;
        @SerializedName("phone")
        public String phone;
        @SerializedName("acceptsCard")
        public String acceptsCard;
        @SerializedName("acceptsCash")
        public String acceptsCash;
        @SerializedName("isTestRestaurant")
        public String isTestRestaurant;
        @SerializedName("latitude")
        public String latitude;
        @SerializedName("longitude")
        public String longitude;
        @SerializedName("maxWaitTime")
        public String maxWaitTime;
        @SerializedName("minFreeDelivery")
        public String minFreeDelivery;
        @SerializedName("minWaitTime")
        public String minWaitTime;
        @SerializedName("offersDelivery")
        public String offersDelivery;
        @SerializedName("offersPickup")
        public String offersPickup;
        @SerializedName("open")
        public String open;
        @SerializedName("streetAddress")
        public String streetAddress;
        @SerializedName("taxRate")
        public String taxRate;
        @SerializedName("timezone")
        public String timezone;
        @SerializedName("url")
        public String url;
        @SerializedName("created")
        public String created;
        @SerializedName("modified")
        public String modified;
        @SerializedName("isOpen")
        public String isOpen;
        @SerializedName("bookmark")
        public String bookmark;
        @SerializedName("item_rating")
        public String item_rating;

        public String getItem_rating() {
            return item_rating;
        }

        public void setItem_rating(String item_rating) {
            this.item_rating = item_rating;
        }
    }

    public class Review {
        @SerializedName("id")
        public String id;
        @SerializedName("restaurant_id")
        public String restaurantId;
        @SerializedName("dish_id")
        public String dishId;
        @SerializedName("message")
        public String message;
        @SerializedName("rating")
        public String rating;
        @SerializedName("user_id")
        public String userId;
    }

    public class Bookmark {
        @SerializedName("restaurant_id")
        public Restaurant restaurant_id;
    }

    public class Datum {
        @SerializedName("Restaurant")
        public Restaurant restaurant;
        @SerializedName("Food")
        public List<Food> food = null;
        @SerializedName("Hour")
        public List<Hour> hour = null;
        @SerializedName("Comment")
        public List<Object> comment = null;
        @SerializedName("Review")
        public List<Object> review = null;
        @SerializedName("Item")
        public List<Item> item = null;
        @SerializedName("Subitem")
        public List<Subitem> subitem = null;
        @SerializedName("Bookmark")
        public List<Object> bookmark = null;
    }

    public static class Comment {

        @SerializedName("id")
        @Expose
        public String id;
        @SerializedName("user_id")
        @Expose
        public String userId;
        @SerializedName("restaurant_id")
        @Expose
        public String restaurantId;
        @SerializedName("comment")
        @Expose
        public String comment;
        @SerializedName("image")
        @Expose
        public String image;
        @SerializedName("created")
        @Expose
        public String created;
        @SerializedName("modified")
        @Expose
        public String modified;
        @SerializedName("User")
        @Expose
        public AccountData.User user;

        public Comment(String image) {
            this.image = image;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }
    }

}
