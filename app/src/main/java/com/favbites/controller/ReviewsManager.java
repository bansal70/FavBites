package com.favbites.controller;

import com.favbites.model.APIClient;
import com.favbites.model.APIInterface;
import com.favbites.model.Constants;
import com.favbites.model.Event;
import com.favbites.model.beans.ReviewsData;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*
 * Created by rishav on 8/23/2017.
 */

public class ReviewsManager {
    private final static String TAG = ReviewsManager.class.getSimpleName();
    public static List<ReviewsData.Datum> datumList = new ArrayList<>();

    public void searchRestaurant(final String params) {
        final APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ReviewsData> call = apiInterface.reviewsData(params);
        call.enqueue(new Callback<ReviewsData>() {
            @Override
            public void onResponse(Call<ReviewsData> call, Response<ReviewsData> response) {
                try {
                    ReviewsData reviewsData = response.body();
                    String status = reviewsData.response;

                    datumList = reviewsData.data;

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
            public void onFailure(Call<ReviewsData> call, Throwable t) {
                EventBus.getDefault().post(new Event(Constants.RESTAURANTS_SEARCH_FAILED, Constants.SERVER_ERROR));
            }
        });

    }
}
