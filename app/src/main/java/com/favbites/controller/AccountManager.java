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

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
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
        Call<ResponseBody> resultCall;
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);

        if (!filePath.isEmpty()) {
            File file = new File(filePath);
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part body =
                    MultipartBody.Part.createFormData("image", file.getName(), requestFile);
            resultCall = apiInterface.updateProfile(params, body);
        } else {
            resultCall = apiInterface.response(params);
        }

        resultCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result = response.body().string();
                    Log.e(TAG, "update_profile response-- "+result);

                    JSONObject jsonObject = new JSONObject(result);
                    String status = jsonObject.getString("response");

                    if (status.equals("1"))
                        EventBus.getDefault().post(new Event(Constants.UPDATE_PROFILE_SUCCESS, ""));
                    else
                        EventBus.getDefault().post(new Event(Constants.UPDATE_PROFILE_FAILED, ""));

                } catch (Exception e) {
                    e.printStackTrace();
                    EventBus.getDefault().post(new Event(Constants.NO_RESPONSE, Constants.SERVER_ERROR));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //t.printStackTrace();
                EventBus.getDefault().post(new Event(Constants.NO_RESPONSE, Constants.SERVER_ERROR));
            }
        });
    }

    public void changePassword(String params) {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ResponseBody> call = apiInterface.response(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String status = response.body().string();
                    Log.e(TAG, "change_password response-- "+status);
                    JSONObject jsonObject = new JSONObject(status);
                    String output = jsonObject.getString("response");

                    if (output.equals("1")) {
                        EventBus.getDefault().post(new Event(Constants.CHANGE_PASSWORD_SUCCESS, ""));
                    } else {
                        EventBus.getDefault().post(new Event(Constants.CHANGE_PASSWORD_FAILED, ""));
                    }

                } catch (Exception e) {
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
