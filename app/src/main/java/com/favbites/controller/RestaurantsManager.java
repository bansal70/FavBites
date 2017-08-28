package com.favbites.controller;

/*
 * Created by rishav on 8/22/2017.
 */

import android.util.Log;

import com.favbites.model.APIClient;
import com.favbites.model.APIInterface;
import com.favbites.model.Constants;
import com.favbites.model.Event;
import com.favbites.model.beans.RestaurantData;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantsManager {
    private final static String TAG = RestaurantsManager.class.getSimpleName();
    public static List<RestaurantData.Datum> datumList = new ArrayList<>();

    public void searchRestaurant(final String params) {
        final APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<RestaurantData> call = apiInterface.restaurantData(params);
        call.enqueue(new Callback<RestaurantData>() {
            @Override
            public void onResponse(Call<RestaurantData> call, Response<RestaurantData> response) {
                try {
                    Log.e(TAG, "response code: "+response.code());
                    RestaurantData restaurantData = response.body();
                    //String status = restaurantData.response;

                    datumList = restaurantData.data;

                    if (datumList.isEmpty()) {
                        EventBus.getDefault().post(new Event(Constants.RESTAURANTS_SEARCH_FAILED, ""));
                        return;
                    }

                    EventBus.getDefault().post(new Event(Constants.RESTAURANTS_SEARCH_SUCCESS, ""));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<RestaurantData> call, Throwable t) {
                Log.e(TAG, "Error in operation");
                EventBus.getDefault().post(new Event(Constants.RESTAURANTS_SEARCH_FAILED, Constants.SERVER_ERROR));
            }
        });
    }

}
