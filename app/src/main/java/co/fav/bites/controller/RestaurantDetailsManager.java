package co.fav.bites.controller;

/*
 * Created by rishav on 8/28/2017.
 */

import android.content.Context;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import co.fav.bites.models.APIClient;
import co.fav.bites.models.APIInterface;
import co.fav.bites.models.Constants;
import co.fav.bites.models.Event;
import co.fav.bites.models.Utils;
import co.fav.bites.models.beans.RestaurantDetailsData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantDetailsManager {
    private final static String TAG = RestaurantDetailsManager.class.getSimpleName();
 //   public static List<RestaurantDetailsData.Data> dataList = new ArrayList<>();
    public static RestaurantDetailsData.Data data;

    public void getRestaurantDetails(Context mContext, final String params) {

        if (!Utils.isInternetActive(mContext)) {
            EventBus.getDefault().postSticky(new Event(Constants.NO_INTERNET, Constants.INTERNET_ERROR));
            return;
        }

        final APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<RestaurantDetailsData> call = apiInterface.restaurantDetailsData(params);
        call.enqueue(new Callback<RestaurantDetailsData>() {
            @Override
            public void onResponse(Call<RestaurantDetailsData> call, Response<RestaurantDetailsData> response) {
                try {
                    Log.e(TAG, "response code: "+response.code());
                    RestaurantDetailsData restaurantDetailsData = response.body();
                    //String status = restaurantData.response;
                    if (response.body().response.equals("1")) {
                        data = restaurantDetailsData.data;
                        EventBus.getDefault().postSticky(new Event(Constants.RESTAURANT_DETAILS_SUCCESS, ""));
                    } else {
                        EventBus.getDefault().postSticky(new Event(Constants.NO_RESPONSE, Constants.SERVER_ERROR));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    EventBus.getDefault().postSticky(new Event(Constants.NO_RESPONSE, Constants.SERVER_ERROR));
                }
            }

            @Override
            public void onFailure(Call<RestaurantDetailsData> call, Throwable t) {
                t.printStackTrace();
                EventBus.getDefault().postSticky(new Event(Constants.NO_RESPONSE, Constants.SERVER_ERROR));
            }
        });
    }

}
