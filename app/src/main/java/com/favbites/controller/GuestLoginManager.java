package com.favbites.controller;

/*
 * Created by rishav on 8/22/2017.
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

public class GuestLoginManager {
    private final String TAG = GuestLoginManager.class.getSimpleName();

    public void loginUser(String params) {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ResponseBody> call = apiInterface.response(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String output = response.body().string();
                    Log.e(TAG, "guest_login response-- "+output);
                    JSONObject jsonObject = new JSONObject(output);
                    String status = jsonObject.getString("response");
                    String msg = jsonObject.getString("mesg");
                    if (status.equals("1"))
                        EventBus.getDefault().post(new Event(Constants.GUEST_LOGIN_SUCCESS, msg));
                    else
                        EventBus.getDefault().post(new Event(Constants.GUEST_LOGIN_FAILED, msg));
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                EventBus.getDefault().post(new Event(Constants.GUEST_LOGIN_FAILED, Constants.SERVER_ERROR));
            }
        });
    }
}