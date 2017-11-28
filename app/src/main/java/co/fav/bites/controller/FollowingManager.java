package co.fav.bites.controller;

/*
 * Created by rishav on 9/4/2017.
 */

import android.util.Log;

import co.fav.bites.models.APIClient;
import co.fav.bites.models.APIInterface;
import co.fav.bites.models.Constants;
import co.fav.bites.models.Event;
import co.fav.bites.models.beans.FollowingData;

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
                        EventBus.getDefault().postSticky(new Event(Constants.FOLLOWING_SUCCESS, ""));
                    }
                    else
                        EventBus.getDefault().postSticky(new Event(Constants.FOLLOWING_EMPTY, ""));

                } catch (Exception e) {
                    e.printStackTrace();
                    EventBus.getDefault().postSticky(new Event(Constants.NO_RESPONSE, Constants.SERVER_ERROR));
                }
            }

            @Override
            public void onFailure(Call<FollowingData> call, Throwable t) {
                EventBus.getDefault().postSticky(new Event(Constants.NO_RESPONSE, Constants.SERVER_ERROR));
            }
        });
    }
}
