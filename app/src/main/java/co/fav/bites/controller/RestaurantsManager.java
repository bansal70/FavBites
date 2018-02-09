package co.fav.bites.controller;

/*
 * Created by rishav on 8/22/2017.
 */

import android.support.annotation.NonNull;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import co.fav.bites.models.APIClient;
import co.fav.bites.models.APIInterface;
import co.fav.bites.models.Constants;
import co.fav.bites.models.Event;
import co.fav.bites.models.beans.RestaurantData;
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
            public void onResponse(@NonNull Call<RestaurantData> call, @NonNull Response<RestaurantData> response) {
                try {
                    Log.e(TAG, "response code: "+response.code());
                    RestaurantData restaurantData = response.body();
                    //String status = restaurantData.response;
                    datumList = restaurantData.data;

                    if (datumList.isEmpty()) {
                        EventBus.getDefault().postSticky(new Event(Constants.RESTAURANTS_SEARCH_FAILED, ""));
                        return;
                    }

                    EventBus.getDefault().postSticky(new Event(Constants.RESTAURANTS_SEARCH_SUCCESS, ""));
                } catch (Exception e) {
                    e.printStackTrace();
                    EventBus.getDefault().postSticky(new Event(Constants.NO_RESPONSE, Constants.SERVER_ERROR));
                }
            }

            @Override
            public void onFailure(@NonNull Call<RestaurantData> call, @NonNull Throwable t) {
                Log.e(TAG, "Error in operation");
                EventBus.getDefault().postSticky(new Event(Constants.NO_RESPONSE, Constants.SERVER_ERROR));
            }
        });
    }

    public void favRestaurants(final String params) {
        final APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<RestaurantData> call = apiInterface.restaurantData(params);
        call.enqueue(new Callback<RestaurantData>() {
            @Override
            public void onResponse(Call<RestaurantData> call, Response<RestaurantData> response) {
                try {
                    Log.e(TAG, "response code: "+response.code());
                    datumList = new ArrayList<>();
                    RestaurantData restaurantData = response.body();
                    //String status = restaurantData.response;
                    datumList = restaurantData.data;

                    if (datumList ==null || datumList.isEmpty()) {
                        EventBus.getDefault().postSticky(new Event(Constants.RESTAURANTS_SEARCH_FAILED, ""));
                        return;
                    }

                    EventBus.getDefault().postSticky(new Event(Constants.RESTAURANTS_SEARCH_SUCCESS, ""));

                } catch (Exception e) {
                    e.printStackTrace();
                    EventBus.getDefault().postSticky(new Event(Constants.NO_RESPONSE, Constants.SERVER_ERROR));
                }
            }

            @Override
            public void onFailure(Call<RestaurantData> call, Throwable t) {
                Log.e(TAG, "Error in operation");
                EventBus.getDefault().postSticky(new Event(Constants.NO_RESPONSE, Constants.SERVER_ERROR));
            }
        });
    }

    public void checkInRestaurants(final String params) {
        final APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<RestaurantData> call = apiInterface.restaurantData(params);
        call.enqueue(new Callback<RestaurantData>() {
            @Override
            public void onResponse(Call<RestaurantData> call, Response<RestaurantData> response) {
                try {
                    Log.e(TAG, "response code: "+response.code());
                    datumList = new ArrayList<>();
                    RestaurantData restaurantData = response.body();
                    //String status = restaurantData.response;
                    datumList = restaurantData.data;

                    if (datumList ==null || datumList.isEmpty()) {
                        EventBus.getDefault().postSticky(new Event(Constants.RESTAURANTS_SEARCH_FAILED, ""));
                        return;
                    }

                    EventBus.getDefault().postSticky(new Event(Constants.RESTAURANTS_SEARCH_SUCCESS, ""));
                } catch (Exception e) {
                    e.printStackTrace();
                    EventBus.getDefault().postSticky(new Event(Constants.NO_RESPONSE, Constants.SERVER_ERROR));
                }
            }

            @Override
            public void onFailure(Call<RestaurantData> call, Throwable t) {
                Log.e(TAG, "Error in operation");
                EventBus.getDefault().postSticky(new Event(Constants.NO_RESPONSE, Constants.SERVER_ERROR));
            }
        });
    }

}
