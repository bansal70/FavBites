package com.favbites.controller;

/*
 * Created by rishav on 9/4/2017.
 */

import android.util.Log;

import com.favbites.model.APIClient;
import com.favbites.model.APIInterface;
import com.favbites.model.Constants;
import com.favbites.model.Event;
import com.favbites.model.beans.FollowersData;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FollowersManager {
    private final static String TAG = FollowersManager.class.getSimpleName();
    public static List<FollowersData.Follower> followerList = new ArrayList<>();

    public void getFollowers(String params) {
        final APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<FollowersData> call = apiInterface.followersData(params);
        call.enqueue(new Callback<FollowersData>() {
            @Override
            public void onResponse(Call<FollowersData> call, Response<FollowersData> response) {
                try {
                    Log.i(TAG, "response code: "+response.code());
                    String status = response.body().response;
                    if (status.equals("1")) {
                        FollowersData.Data data = response.body().data;
                        followerList = data.follower;
                        EventBus.getDefault().post(new Event(Constants.FOLLOWER_SUCCESS, ""));
                    }
                    else
                        EventBus.getDefault().post(new Event(Constants.FOLLOWER_EMPTY, ""));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<FollowersData> call, Throwable t) {
                EventBus.getDefault().post(new Event(Constants.NO_RESPONSE, Constants.SERVER_ERROR));
            }
        });
    }
}
