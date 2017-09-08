package com.favbites.controller;

/*
 * Created by rishav on 9/7/2017.
 */

import android.util.Log;

import com.favbites.model.APIClient;
import com.favbites.model.APIInterface;
import com.favbites.model.Constants;
import com.favbites.model.Event;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckInManager {
    private final String TAG = GuestLoginManager.class.getSimpleName();

    public void checkInUser(String params) {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ResponseBody> call = apiInterface.response(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String output = response.body().string();
                    Log.e(TAG, "checkIn response-- "+output);
                    JSONObject jsonObject = new JSONObject(output);
                    String status = jsonObject.getString("response");
                    if (status.equals("1"))
                        EventBus.getDefault().post(new Event(Constants.CHECK_IN_SUCCESS, ""));
                    else
                        EventBus.getDefault().post(new Event(Constants.CHECK_IN_FAILED, ""));
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                EventBus.getDefault().post(new Event(Constants.NO_RESPONSE, Constants.SERVER_ERROR));
            }
        });
    }
}
