package com.favbites.controller;

/*
 * Created by rishav on 8/24/2017.
 */

import android.util.Log;

import com.favbites.model.APIClient;
import com.favbites.model.APIInterface;
import com.favbites.model.Constants;
import com.favbites.model.Event;
import com.favbites.model.beans.BookmarkData;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookmarkManager {
    private final static String TAG = BookmarkManager.class.getSimpleName();
    public static List<BookmarkData.Data> dataList;

    public void bookmarkRestaurant(final String params) {
        final APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ResponseBody> call = apiInterface.response(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Log.i(TAG, "response code: "+response.code());
                    String result = response.body().string();
                    JSONObject jsonObject = new JSONObject(result);
                    String status = jsonObject.getString("responce");
                    if (status.equals("1"))
                        EventBus.getDefault().post(new Event(Constants.BOOKMARK_ADDED, ""));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                EventBus.getDefault().post(new Event(Constants.NO_RESPONSE, Constants.SERVER_ERROR));
            }
        });
    }

    public void getBookmarks(String params) {
        final APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<BookmarkData> call = apiInterface.bookmarkData(params);
        call.enqueue(new Callback<BookmarkData>() {
            @Override
            public void onResponse(Call<BookmarkData> call, Response<BookmarkData> response) {
                try {
                    Log.i(TAG, "response code: "+response.code());
                    String status = response.body().response;
                    if (status.equals("1")) {
                        dataList = response.body().data;
                        EventBus.getDefault().post(new Event(Constants.BOOKMARK_RESTAURANTS_SUCCESS, ""));
                    }
                    else
                        EventBus.getDefault().post(new Event(Constants.BOOKMARK_RESTAURANTS_EMPTY, ""));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<BookmarkData> call, Throwable t) {
                EventBus.getDefault().post(new Event(Constants.NO_RESPONSE, Constants.SERVER_ERROR));
            }
        });
    }
}
