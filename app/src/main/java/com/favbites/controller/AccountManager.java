package com.favbites.controller;

/*
 * Created by rishav on 8/30/2017.
 */

import android.util.Log;

import com.favbites.model.APIClient;
import com.favbites.model.APIInterface;
import com.favbites.model.Constants;
import com.favbites.model.Event;
import com.favbites.model.beans.AccountData;
import com.favbites.model.beans.AccountUpdateData;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountManager {
    private final String TAG = AccountManager.class.getSimpleName();
    public static AccountData.Data data;

    public void userAccount(String params) {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<AccountData> call = apiInterface.userProfile(params);
        call.enqueue(new Callback<AccountData>() {
            @Override
            public void onResponse(Call<AccountData> call, Response<AccountData> response) {
                try {
                    String status = response.body().response;
                    Log.e(TAG, "profile response-- "+status);
                    AccountData accountData = response.body();
                    data = accountData.data;

                    if (status.equals("1")) {
                        EventBus.getDefault().post(new Event(Constants.PROFILE_SUCCESS, ""));
                    } else {
                        EventBus.getDefault().post(new Event(Constants.PROFILE_EMPTY, ""));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<AccountData> call, Throwable t) {
                EventBus.getDefault().post(new Event(Constants.NO_RESPONSE, Constants.SERVER_ERROR));
            }
        });
    }

    public void updateProfile(String params, String filePath) {
        File file = new File(filePath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        final APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);

        MultipartBody.Part body =
                MultipartBody.Part.createFormData("image", file.getName(), requestFile);

        Call<AccountUpdateData> resultCall = apiInterface.updateProfile(params, body);

        resultCall.enqueue(new Callback<AccountUpdateData>() {
            @Override
            public void onResponse(Call<AccountUpdateData> call, Response<AccountUpdateData> response) {
                try {
                    String result = response.body().response;
                    Log.e(TAG, "update_profile response-- "+result);

                    if (result.equals("1"))
                        EventBus.getDefault().post(new Event(Constants.UPDATE_PROFILE_SUCCESS, ""));
                    else
                        EventBus.getDefault().post(new Event(Constants.UPDATE_PROFILE_FAILED, ""));

                } catch (Exception e) {
                    e.printStackTrace();
                    EventBus.getDefault().post(new Event(Constants.NO_RESPONSE, Constants.SERVER_ERROR));
                }
            }

            @Override
            public void onFailure(Call<AccountUpdateData> call, Throwable t) {
                //t.printStackTrace();
                EventBus.getDefault().post(new Event(Constants.NO_RESPONSE, Constants.SERVER_ERROR));
            }
        });
    }
}
