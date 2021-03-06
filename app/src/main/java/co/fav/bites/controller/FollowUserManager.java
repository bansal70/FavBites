package co.fav.bites.controller;

/*
 * Created by rishav on 9/1/2017.
 */

import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import co.fav.bites.models.APIClient;
import co.fav.bites.models.APIInterface;
import co.fav.bites.models.Constants;
import co.fav.bites.models.Event;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FollowUserManager {
    private final String TAG = LoginManager.class.getSimpleName();

    public void followUser(String params) {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ResponseBody> call = apiInterface.response(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String output = response.body().string();
                    Log.e(TAG, "follow_user response-- "+output);
                    JSONObject jsonObject = new JSONObject(output);
                    int status = jsonObject.getInt("status");

                    if (status == 1)
                        EventBus.getDefault().post(new Event(Constants.FOLLOW_SUCCESS, ""));
                    else
                        EventBus.getDefault().post(new Event(Constants.UNFOLLOW_SUCCESS, ""));
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    EventBus.getDefault().post(new Event(Constants.NO_RESPONSE, Constants.SERVER_ERROR));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                EventBus.getDefault().post(new Event(Constants.NO_RESPONSE, Constants.SERVER_ERROR));
            }
        });
    }
}
