package co.fav.bites.controller;

/*
 * Created by rishav on 8/22/2017.
 */

import android.content.Context;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import co.fav.bites.models.APIClient;
import co.fav.bites.models.APIInterface;
import co.fav.bites.models.Constants;
import co.fav.bites.models.Event;
import co.fav.bites.models.FBPreferences;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginManager {
    private final String TAG = LoginManager.class.getSimpleName();

    public void loginUser(final Context context, String params) {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ResponseBody> call = apiInterface.response(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String output = response.body().string();
                    Log.e(TAG, "Login response-- "+output);
                    JSONObject jsonObject = new JSONObject(output);
                    String status = jsonObject.getString("response");
                    String msg = jsonObject.getString("mesg");
                    if (status.equals("1")) {
                        JSONObject data = jsonObject.getJSONObject("data");

                        String user_id = data.getString("id");
                        String first_name = data.getString("fname");
                        String last_name = data.getString("lname");
                        String image = data.getString("image");

                        FBPreferences.putString(context, "user_id", user_id);
                        FBPreferences.putString(context, "first_name", first_name);
                        FBPreferences.putString(context, "last_name", last_name);
                        FBPreferences.putString(context, "profile_pic", image);

                        EventBus.getDefault().postSticky(new Event(Constants.LOGIN_SUCCESS, msg));
                    }
                    else
                        EventBus.getDefault().postSticky(new Event(Constants.LOGIN_FAILED, msg));
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

    public void fbLoginUser(final Context context, String params, String image) {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ResponseBody> call = apiInterface.facebookLogin(params, image);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String output = response.body().string();
                    Log.e(TAG, "Login response-- "+output);
                    JSONObject jsonObject = new JSONObject(output);
                    String status = jsonObject.getString("response");
                    String msg = jsonObject.getString("mesg");
                    if (status.equals("1")) {
                        JSONObject data = jsonObject.getJSONObject("data");

                        String user_id = data.getString("id");
                        String first_name = data.getString("fname");
                        String last_name = data.getString("lname");
                        String image = data.getString("image");

                        FBPreferences.putString(context, "user_id", user_id);
                        FBPreferences.putString(context, "first_name", first_name);
                        FBPreferences.putString(context, "last_name", last_name);
                        FBPreferences.putString(context, "profile_pic", image);

                        EventBus.getDefault().postSticky(new Event(Constants.LOGIN_SUCCESS, msg));
                    }
                    else
                        EventBus.getDefault().postSticky(new Event(Constants.LOGIN_FAILED, msg));
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
