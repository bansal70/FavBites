package co.fav.bites.controller;

/*
 * Created by rishav on 9/4/2017.
 */

import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import co.fav.bites.models.APIClient;
import co.fav.bites.models.APIInterface;
import co.fav.bites.models.Constants;
import co.fav.bites.models.Event;
import co.fav.bites.models.beans.FollowersData;
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
                        EventBus.getDefault().postSticky(new Event(Constants.FOLLOWER_SUCCESS, ""));
                    }
                    else
                        EventBus.getDefault().postSticky(new Event(Constants.FOLLOWER_EMPTY, ""));

                } catch (Exception e) {
                    e.printStackTrace();
                    EventBus.getDefault().postSticky(new Event(Constants.NO_RESPONSE, Constants.SERVER_ERROR));
                }
            }

            @Override
            public void onFailure(Call<FollowersData> call, Throwable t) {
                EventBus.getDefault().postSticky(new Event(Constants.NO_RESPONSE, Constants.SERVER_ERROR));
            }
        });
    }
}
