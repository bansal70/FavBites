package co.fav.bites.models.beans;

/*
 * Created by rishav on 8/28/2017.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

public class RestaurantDetailsData {

    @SerializedName("mesg")
    @Expose
    public String mesg;
    @SerializedName("response")
    @Expose
    public String response;
    @SerializedName("data")
    @Expose
    public Data data;

    public class Data {

        @SerializedName("Restaurant")
        @Expose
        public Restaurant restaurant;
        @SerializedName("Food")
        @Expose
        public List<Food> food = null;
        @SerializedName("Comment")
        @Expose
        public List<Comment> comment = null;
        /*@SerializedName("Review")
        @Expose
        public List<Review> review = null;*/
        @SerializedName("Item")
        @Expose
        public List<Item> item = null;
        @SerializedName("Subitem")
        @Expose
        public List<Subitem> subitem = null;

    }

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
        @SerializedName("bookmark")
        @Expose
        public String bookmark;
        @SerializedName("isOpen")
        @Expose
        public String isOpen;
        @SerializedName("item_rating")
        @Expose
        public String item_rating;

        public String getItem_rating() {
            return item_rating;
        }

        public void setItem_rating(String item_rating) {
            this.item_rating = item_rating;
        }
    }

    /*public class Review {

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
        @SerializedName("Subitem")
        @Expose
        public Subitem subitem;
    }*/

    @Parcel
    public static class Subitem {

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
        public User user;

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
        public String image;
        @SerializedName("status")
        @Expose
        public String status;
        @SerializedName("forgot_key")
        @Expose
        public Object forgotKey;
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
}
