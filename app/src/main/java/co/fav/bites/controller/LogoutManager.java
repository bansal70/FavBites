package co.fav.bites.controller;

/*
 * Created by rishav on 8/30/2017.
 */

import android.content.Context;
import android.util.Log;

import co.fav.bites.models.APIClient;
import co.fav.bites.models.APIInterface;
import co.fav.bites.models.Constants;
import co.fav.bites.models.Event;
import co.fav.bites.models.FBPreferences;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LogoutManager {
    private final String TAG = LogoutManager.class.getSimpleName();

    public void logoutUser(final Context context, String params) {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ResponseBody> call = apiInterface.response(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String output = response.body().string();
                    Log.e(TAG, "logout response-- "+output);
                    JSONObject jsonObject = new JSONObject(output);
                    String status = jsonObject.getString("response");

                    if (status.equals("1")) {
                        EventBus.getDefault().postSticky(new Event(Constants.LOGOUT_SUCCESS, ""));
                        FBPreferences.clearPref(context);
                    } else {
                        EventBus.getDefault().postSticky(new Event(Constants.LOGOUT_FAILED, ""));
                    }

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
