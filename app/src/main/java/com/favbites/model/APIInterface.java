package com.favbites.model;

import com.favbites.model.beans.AccountData;
import com.favbites.model.beans.BookmarkData;
import com.favbites.model.beans.FollowersData;
import com.favbites.model.beans.FollowingData;
import com.favbites.model.beans.ImageResult;
import com.favbites.model.beans.RestaurantData;
import com.favbites.model.beans.RestaurantDetailsData;
import com.favbites.model.beans.ReviewsData;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Url;

/*
 * Created by rishav on 09/01/17.
 */

public interface APIInterface {

    @POST
    Call<ResponseBody> response(@Url String string);

    @POST
    Call<RestaurantData> restaurantData(@Url String string);

    @POST
    Call<RestaurantDetailsData> restaurantDetailsData(@Url String string);

    @POST
    Call<ReviewsData> reviewsData(@Url String string);

    @Multipart
    @POST
    Call<ImageResult> uploadImage(@Url String string, @Part MultipartBody.Part file);

    @POST
    Call<AccountData> userProfile(@Url String string);

    @Multipart
    @POST
    Call<ResponseBody> updateProfile(@Url String string, @Part MultipartBody.Part file);

    @POST
    Call<BookmarkData> bookmarkData(@Url String string);

    @POST
    Call<FollowersData> followersData(@Url String string);

    @POST
    Call<FollowingData> followingData(@Url String string);
}
