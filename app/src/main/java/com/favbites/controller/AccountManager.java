package com.favbites.controller;

/*
 * Created by rishav on 8/30/2017.
 */

import android.content.Context;
import android.util.Log;

import com.favbites.model.APIClient;
import com.favbites.model.APIInterface;
import com.favbites.model.Constants;
import com.favbites.model.Event;
import com.favbites.model.FBPreferences;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountManager {
    private final String TAG = AccountManager.class.getSimpleName();

    public void userAccount(final Context context, String params) {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ResponseBody> call = apiInterface.response(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String output = response.body().string();
                    Log.e(TAG, "my_account response-- "+output);
                    JSONObject jsonObject = new JSONObject(output);
                    String status = jsonObject.getString("response");

                    if (status.equals("1")) {
                        EventBus.getDefault().post(new Event(Constants.PROFILE_SUCCESS, ""));
                        FBPreferences.clearPref(context);
                    } else {
                        EventBus.getDefault().post(new Event(Constants.PROFILE_EMPTY, ""));
                    }

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
