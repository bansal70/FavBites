package co.fav.bites.models;

import java.util.HashMap;

import co.fav.bites.models.beans.AccountData;
import co.fav.bites.models.beans.BookmarkData;
import co.fav.bites.models.beans.FollowersData;
import co.fav.bites.models.beans.FollowingData;
import co.fav.bites.models.beans.ImageResult;
import co.fav.bites.models.beans.RestaurantData;
import co.fav.bites.models.beans.RestaurantDetailsData;
import co.fav.bites.models.beans.ReviewsData;
import co.fav.bites.models.beans.UserPostsData;
import co.fav.bites.models.beans.UserReviewsData;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/*
 * Created by rishav on 09/01/17.
 */

public interface APIInterface {

    @POST
    Call<ResponseBody> response(@Url String string);

    @POST
    Call<ResponseBody> facebookLogin(@Url String string, @Query("image") String image);

    @POST
    Call<RestaurantData> restaurantData(@Url String string);

    @POST
    Call<RestaurantDetailsData> restaurantDetailsData(@Url String string);

    @POST
    Call<ReviewsData> reviewsData(@Url String string);

    @Multipart
    @POST
    Call<ImageResult> uploadImage(@Url String string, @Part MultipartBody.Part file);

    @POST
    Call<AccountData> userProfile(@Url String string);

    @Multipart
    @POST
    Call<ResponseBody> updateProfile(@Url String string, @Part MultipartBody.Part file);

    @POST
    Call<BookmarkData> bookmarkData(@Url String string);

    @POST
    Call<FollowersData> followersData(@Url String string);

    @POST
    Call<FollowingData> followingData(@Url String string);

    @POST
    Call<UserPostsData> userPosts(@Url String string);

    @POST
    Call<UserReviewsData> userReviews(@Url String string);

    @POST
    Flowable<RestaurantData> getRestaurants(@Url String string, @QueryMap HashMap<String, String> mapParams);

    @POST("getRestaurantData")
    Observable<RestaurantDetailsData> getRestaurantDetails(@QueryMap HashMap<String, String> mapParams);
}
