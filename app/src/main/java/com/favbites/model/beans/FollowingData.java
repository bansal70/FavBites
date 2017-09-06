package com.favbites.model.beans;

/*
 * Created by rishav on 9/4/2017.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FollowingData {
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
        @SerializedName("FollowingTo")
        @Expose
        public List<FollowingTo> followingTo = null;
    }

    public class FollowingTo {

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
        @SerializedName("is_follow")
        @Expose
        public int isFollow;

    }

}
