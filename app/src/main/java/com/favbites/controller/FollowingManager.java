package com.favbites.controller;

/*
 * Created by rishav on 9/4/2017.
 */

import android.util.Log;

import com.favbites.model.APIClient;
import com.favbites.model.APIInterface;
import com.favbites.model.Constants;
import com.favbites.model.Event;
import com.favbites.model.beans.FollowingData;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FollowingManager {
    private final static String TAG = FollowingManager.class.getSimpleName();
    public static List<FollowingData.FollowingTo> followingList = new ArrayList<>();

    public void getFollowing(String params) {
        final APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<FollowingData> call = apiInterface.followingData(params);
        call.enqueue(new Callback<FollowingData>() {
            @Override
            public void onResponse(Call<FollowingData> call, Response<FollowingData> response) {
                try {
                    Log.i(TAG, "response code: "+response.code());
                    String status = response.body().response;
                    if (status.equals("1")) {
                        FollowingData.Data data = response.body().data;
                        followingList = data.followingTo;
                        EventBus.getDefault().post(new Event(Constants.FOLLOWING_SUCCESS, ""));
                    }
                    else
                        EventBus.getDefault().post(new Event(Constants.FOLLOWING_EMPTY, ""));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<FollowingData> call, Throwable t) {
                EventBus.getDefault().post(new Event(Constants.NO_RESPONSE, Constants.SERVER_ERROR));
            }
        });
    }
}
