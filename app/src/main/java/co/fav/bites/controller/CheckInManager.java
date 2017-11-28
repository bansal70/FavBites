package co.fav.bites.controller;

/*
 * Created by rishav on 9/7/2017.
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
                        EventBus.getDefault().postSticky(new Event(Constants.CHECK_IN_SUCCESS, ""));
                    else
                        EventBus.getDefault().postSticky(new Event(Constants.CHECK_IN_FAILED, ""));
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    EventBus.getDefault().postSticky(new Event(Constants.NO_RESPONSE, Constants.SERVER_ERROR));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                EventBus.getDefault().postSticky(new Event(Constants.NO_RESPONSE, Constants.SERVER_ERROR));
            }
        });
    }
}
