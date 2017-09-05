package com.favbites.controller;

/*
 * Created by rishav on 8/28/2017.
 */

import android.util.Log;

import com.favbites.model.APIClient;
import com.favbites.model.APIInterface;
import com.favbites.model.Constants;
import com.favbites.model.Event;
import com.favbites.model.beans.RestaurantDetailsData;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantDetailsManager {
    private final static String TAG = RestaurantDetailsManager.class.getSimpleName();
 //   public static List<RestaurantDetailsData.Data> dataList = new ArrayList<>();
    public static RestaurantDetailsData.Data data;

    public void getRestaurantDetails(final String params) {
        final APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<RestaurantDetailsData> call = apiInterface.restaurantDetailsData(params);
        call.enqueue(new Callback<RestaurantDetailsData>() {
            @Override
            public void onResponse(Call<RestaurantDetailsData> call, Response<RestaurantDetailsData> response) {
                try {
                    Log.e(TAG, "response code: "+response.code());
                    RestaurantDetailsData restaurantDetailsData = response.body();
                    //String status = restaurantData.response;
                    data = restaurantDetailsData.data;


                    EventBus.getDefault().post(new Event(Constants.RESTAURANT_DETAILS_SUCCESS, ""));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<RestaurantDetailsData> call, Throwable t) {
                EventBus.getDefault().post(new Event(Constants.NO_RESPONSE, Constants.SERVER_ERROR));
            }
        });
    }

}
