package com.favbites.controller;

/*
 * Created by rishav on 9/5/2017.
 */

import android.util.Log;

import com.favbites.model.APIClient;
import com.favbites.model.APIInterface;
import com.favbites.model.Constants;
import com.favbites.model.Event;
import com.favbites.model.beans.UserPostsData;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserPostManager {
    private final static String TAG = UserPostManager.class.getSimpleName();
    public static List<UserPostsData.Data> postsList = new ArrayList<>();

    public void getUserPosts(String params) {
        final APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<UserPostsData> call = apiInterface.userPosts(params);
        call.enqueue(new Callback<UserPostsData>() {
            @Override
            public void onResponse(Call<UserPostsData> call, Response<UserPostsData> response) {
                try {
                    Log.i(TAG, "response code: "+response.code());
                    String status = response.body().response;
                    if (status.equals("1")) {
                        postsList = response.body().data;
                        EventBus.getDefault().post(new Event(Constants.USER_POSTS_SUCCESS, ""));
                    }
                    else
                        EventBus.getDefault().post(new Event(Constants.USER_POSTS_EMPTY, ""));

                } catch (Exception e) {
                    e.printStackTrace();
                    EventBus.getDefault().post(new Event(Constants.NO_RESPONSE, Constants.SERVER_ERROR));
                }
            }

            @Override
            public void onFailure(Call<UserPostsData> call, Throwable t) {
                EventBus.getDefault().post(new Event(Constants.NO_RESPONSE, Constants.SERVER_ERROR));
            }
        });
    }
}
