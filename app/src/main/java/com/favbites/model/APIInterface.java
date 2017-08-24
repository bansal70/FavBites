package com.favbites.model;

import com.favbites.model.beans.RestaurantData;
import com.favbites.model.beans.ReviewsData;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/*
 * Created by rishav on 09/01/17.
 */

public interface APIInterface {

    @GET
    Call<ResponseBody> response(@Url String string);

    @GET
    Call<RestaurantData> restaurantData(@Url String string);

    @GET
    Call<ReviewsData> reviewsData(@Url String string);

    @GET
    Call<RestaurantData.Restaurant> restaurantDetails(@Url String string);
}
