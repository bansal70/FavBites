package com.favbites.model.beans;

/*
 * Created by rishav on 8/30/2017.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AccountData {
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
        @SerializedName("User")
        @Expose
        public User user;
        @SerializedName("Follower")
        @Expose
        public List<Object> follower = null;
        @SerializedName("Following")
        @Expose
        public List<Object> following = null;
        @SerializedName("Bookmark")
        @Expose
        public List<Object> bookmark = null;
        @SerializedName("Comment")
        @Expose
        public List<Object> comment = null;
        @SerializedName("Check")
        @Expose
        public List<Object> check = null;
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
