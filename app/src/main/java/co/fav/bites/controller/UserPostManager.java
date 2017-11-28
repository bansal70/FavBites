package co.fav.bites.controller;

/*
 * Created by rishav on 9/5/2017.
 */

import android.util.Log;

import co.fav.bites.models.APIClient;
import co.fav.bites.models.APIInterface;
import co.fav.bites.models.Constants;
import co.fav.bites.models.Event;
import co.fav.bites.models.beans.UserPostsData;

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
                        EventBus.getDefault().postSticky(new Event(Constants.USER_POSTS_SUCCESS, ""));
                    }
                    else
                        EventBus.getDefault().postSticky(new Event(Constants.USER_POSTS_EMPTY, ""));

                } catch (Exception e) {
                    e.printStackTrace();
                    EventBus.getDefault().postSticky(new Event(Constants.NO_RESPONSE, Constants.SERVER_ERROR));
                }
            }

            @Override
            public void onFailure(Call<UserPostsData> call, Throwable t) {
                EventBus.getDefault().postSticky(new Event(Constants.NO_RESPONSE, Constants.SERVER_ERROR));
            }
        });
    }
}
