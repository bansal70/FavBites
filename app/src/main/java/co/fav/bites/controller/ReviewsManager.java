package co.fav.bites.controller;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import co.fav.bites.models.APIClient;
import co.fav.bites.models.APIInterface;
import co.fav.bites.models.Constants;
import co.fav.bites.models.Event;
import co.fav.bites.models.FBPreferences;
import co.fav.bites.models.Utils;
import co.fav.bites.models.beans.ReviewsData;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*
 * Created by rishav on 8/23/2017.
 */

public class ReviewsManager {
    private final static String TAG = ReviewsManager.class.getSimpleName();
    public static List<ReviewsData.Datum> reviewsList = new ArrayList<>();

    public void dishReviews(final Context context, final String params) {

        if (!Utils.isInternetActive(context)) {
            EventBus.getDefault().postSticky(new Event(Constants.NO_INTERNET, Constants.INTERNET_ERROR));
            return;
        }

        final APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ReviewsData> call = apiInterface.reviewsData(params);
        call.enqueue(new Callback<ReviewsData>() {
            @Override
            public void onResponse(Call<ReviewsData> call, Response<ReviewsData> response) {
                try {
                    ReviewsData reviewsData = response.body();
                    String status = reviewsData.response;

                    if (status.equals("0")) {
                        EventBus.getDefault().postSticky(new Event(Constants.REVIEWS_EMPTY, ""));
                        FBPreferences.putString(context, "dish_ratings", "0");
                        return;
                    }

                    reviewsList = reviewsData.data;

                    String ratings = reviewsData.rating;
                    FBPreferences.putString(context, "dish_ratings", ratings);

                    EventBus.getDefault().postSticky(new Event(Constants.REVIEWS_SUCCESS, ""));

                } catch (Exception e) {
                    e.printStackTrace();
                    EventBus.getDefault().postSticky(new Event(Constants.NO_RESPONSE, Constants.SERVER_ERROR));
                }

            }

            @Override
            public void onFailure(Call<ReviewsData> call, Throwable t) {
                EventBus.getDefault().postSticky(new Event(Constants.NO_RESPONSE, Constants.SERVER_ERROR));
            }
        });
    }

    public void addItemReview(final Context context, String params) {
        final APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ResponseBody> call = apiInterface.response(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String output = response.body().string();
                    JSONObject jsonObject = new JSONObject(output);
                    String status = jsonObject.getString("response");
                    String msg = jsonObject.getString("mesg");

                    if (status.equals("1"))
                        EventBus.getDefault().postSticky(new Event(Constants.ADD_REVIEWS_SUCCESS, msg));
                    else
                        EventBus.getDefault().postSticky(new Event(Constants.ADD_REVIEWS_FAILED, msg));

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                EventBus.getDefault().postSticky(new Event(Constants.NO_RESPONSE, Constants.SERVER_ERROR));
            }
        });
    }


}
