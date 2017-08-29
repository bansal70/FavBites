package com.favbites.controller;

/*
 * Created by rishav on 8/29/2017.
 */

import android.util.Log;

import com.favbites.model.APIClient;
import com.favbites.model.APIInterface;
import com.favbites.model.Constants;
import com.favbites.model.Event;
import com.favbites.model.beans.ImageResult;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadPhotoManager {
    private final String TAG = UploadPhotoManager.class.getSimpleName();

    public void uploadPostServer(String params, String filePath) {
        File file = new File(filePath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        final APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);

        MultipartBody.Part body =
                MultipartBody.Part.createFormData("image", file.getName(), requestFile);

        Call<ImageResult> resultCall = apiInterface.uploadImage(params, body);

        resultCall.enqueue(new Callback<ImageResult>() {
            @Override
            public void onResponse(Call<ImageResult> call, Response<ImageResult> response) {
                try {
                    String result = response.body().response;
                    Log.e(TAG, "upload_image response-- "+result);

                    if (result.equals("1"))
                        EventBus.getDefault().post(new Event(Constants.UPLOAD_PHOTO_SUCCESS, ""));
                    else
                        EventBus.getDefault().post(new Event(Constants.UPLOAD_PHOTO_FAILED, ""));

                } catch (Exception e) {
                    e.printStackTrace();
                    EventBus.getDefault().post(new Event(Constants.NO_RESPONSE, Constants.SERVER_ERROR));
                }
            }

            @Override
            public void onFailure(Call<ImageResult> call, Throwable t) {
                //t.printStackTrace();
                EventBus.getDefault().post(new Event(Constants.NO_RESPONSE, Constants.SERVER_ERROR));
            }
        });
    }
}
