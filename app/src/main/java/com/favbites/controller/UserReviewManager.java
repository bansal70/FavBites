package com.favbites.controller;

/*
 * Created by rishav on 9/5/2017.
 */

import android.util.Log;

import com.favbites.model.APIClient;
import com.favbites.model.APIInterface;
import com.favbites.model.Constants;
import com.favbites.model.Event;
import com.favbites.model.beans.UserReviewsData;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserReviewManager {
    private final static String TAG = UserPostManager.class.getSimpleName();
    public static List<UserReviewsData.Data> reviewsList = new ArrayList<>();

    public void getUserReviews(String params) {
        final APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<UserReviewsData> call = apiInterface.userReviews(params);
        call.enqueue(new Callback<UserReviewsData>() {
            @Override
            public void onResponse(Call<UserReviewsData> call, Response<UserReviewsData> response) {
                try {
                    Log.i(TAG, "response code: "+response.code());
                    String status = response.body().response;
                    if (status.equals("1")) {
                        reviewsList = response.body().data;
                        EventBus.getDefault().post(new Event(Constants.USER_REVIEWS_SUCCESS, ""));
                    }
                    else
                        EventBus.getDefault().post(new Event(Constants.USER_REVIEWS_EMPTY, ""));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<UserReviewsData> call, Throwable t) {
                EventBus.getDefault().post(new Event(Constants.NO_RESPONSE, Constants.SERVER_ERROR));
            }
        });
    }
}
