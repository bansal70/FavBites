package co.fav.bites.controller;

/*
 * Created by rishav on 9/5/2017.
 */

import android.util.Log;

import co.fav.bites.models.APIClient;
import co.fav.bites.models.APIInterface;
import co.fav.bites.models.Constants;
import co.fav.bites.models.Event;
import co.fav.bites.models.beans.UserReviewsData;

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
                        EventBus.getDefault().postSticky(new Event(Constants.USER_REVIEWS_SUCCESS, ""));
                    }
                    else
                        EventBus.getDefault().postSticky(new Event(Constants.USER_REVIEWS_EMPTY, ""));

                } catch (Exception e) {
                    e.printStackTrace();
                    EventBus.getDefault().postSticky(new Event(Constants.NO_RESPONSE, Constants.SERVER_ERROR));
                }
            }

            @Override
            public void onFailure(Call<UserReviewsData> call, Throwable t) {
                EventBus.getDefault().postSticky(new Event(Constants.NO_RESPONSE, Constants.SERVER_ERROR));
            }
        });
    }
}
