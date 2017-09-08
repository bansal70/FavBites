package com.favbites.view;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.favbites.R;
import com.favbites.controller.ModelManager;
import com.favbites.controller.ReviewsManager;
import com.favbites.model.Constants;
import com.favbites.model.Event;
import com.favbites.model.FBPreferences;
import com.favbites.model.Operations;
import com.favbites.model.Utils;
import com.favbites.model.beans.ReviewsData;
import com.favbites.view.adapters.ReviewsAdapter;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class ReviewsActivity extends BaseActivity implements View.OnClickListener{

    private Activity activity = this;
    TextView tvItem, tvAddReview, tvNoReview;
    ImageView imgBack;
    Dialog dialogReview;
    TextView dialogTvSubmit, dialogTvCancel;
    EditText editComment;
    RatingBar rbItemRating;
    int dish_key, restaurant_id;
    String dish_name, user_id;
    KProgressHUD pd;
    RecyclerView recyclerView;
    ReviewsAdapter reviewsAdapter;
    List<ReviewsData.Datum> reviewsList;
    RatingBar rbRatings;
    String userComment = "", userRating = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);

        initViews();
        initDialog();
    }

    public void initViews() {
        pd = Utils.showDialog(this);
        pd.show();

        String key = getIntent().getStringExtra("dish_key");
        dish_key = Integer.parseInt(key);
        dish_name = getIntent().getStringExtra("dish_name");
        restaurant_id = Integer.parseInt(FBPreferences.readString(this, "restaurant_id"));
        user_id = FBPreferences.readString(this, "user_id");

        ModelManager.getInstance().getReviewsManager().dishReviews(this,
                Operations.getItemReviews(restaurant_id, dish_key, user_id));

        tvItem = (TextView) findViewById(R.id.tvItem);
        tvAddReview = (TextView) findViewById(R.id.tvAddReview);
        imgBack = (ImageView) findViewById(R.id.imgBack);
        rbRatings = (RatingBar) findViewById(R.id.rbRatings);
        tvNoReview = (TextView) findViewById(R.id.tvNoReview);

        tvItem.setText(dish_name);
        reviewsList = new ArrayList<>();
        recyclerView  = (RecyclerView) findViewById(R.id.recyclerReviews);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.addItemDecoration(new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL));

        reviewsAdapter = new ReviewsAdapter(activity, reviewsList);
        recyclerView.setAdapter(reviewsAdapter);

        tvAddReview.setOnClickListener(this);
        imgBack.setOnClickListener(this);
    }

    public void initDialog() {
        dialogReview = Utils.createDialog(this, R.layout.dialog_review);

        editComment = dialogReview.findViewById(R.id.editComment);
        rbItemRating = dialogReview.findViewById(R.id.rbItemRating);
        dialogTvSubmit = dialogReview.findViewById(R.id.tvSubmit);
        dialogTvCancel = dialogReview.findViewById(R.id.tvCancel);

        dialogTvSubmit.setOnClickListener(this);
        dialogTvCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.imgBack:
                recyclerView.setAdapter(null);
                finish();
                break;

            case R.id.tvAddReview:
                if (user_id.isEmpty()) {
                    Toast.makeText(activity, "Please login to add your review", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!userRating.isEmpty()) {
                    rbItemRating.setRating(Float.parseFloat(userRating));
                    editComment.setText(userComment);
                }
                dialogReview.show();
                break;

            case R.id.tvSubmit:
                if (rbItemRating.getRating() == 0.0) {
                    Toast.makeText(activity, "Please give the rating...", Toast.LENGTH_SHORT).show();
                    return;
                }
                pd.show();
                String rating = String.valueOf(rbItemRating.getRating());
                String comment = editComment.getText().toString();

                ModelManager.getInstance().getReviewsManager().addItemReview(this,
                        Operations.addItemReview(user_id, restaurant_id, dish_key, rating, comment));

                break;

            case R.id.tvCancel:
                dialogReview.dismiss();
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(Event event) {
        switch (event.getKey()) {
            case Constants.REVIEWS_SUCCESS:
                pd.dismiss();

                float ratings = Float.parseFloat(FBPreferences.readString(this, "dish_ratings"));
                rbRatings.setRating(ratings);

                reviewsList.addAll(ReviewsManager.reviewsList);
                reviewsAdapter.notifyDataSetChanged();
                checkUserRating();
                break;

            case Constants.REVIEWS_EMPTY:
                pd.dismiss();
                userRating = "";
                userRating = "";
                rbRatings.setRating(0);
                tvNoReview.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                break;

            case Constants.ADD_REVIEWS_SUCCESS:
                Utils.isReviewed = true;
                dialogReview.dismiss();
                reviewsList.clear();
                tvNoReview.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                ModelManager.getInstance().getReviewsManager().dishReviews(this,
                        Operations.getItemReviews(restaurant_id, dish_key, user_id));
                Toast.makeText(activity, ""+event.getValue(), Toast.LENGTH_SHORT).show();
                break;

            case Constants.ADD_REVIEWS_FAILED:
                pd.dismiss();
                Toast.makeText(activity, ""+event.getValue(), Toast.LENGTH_SHORT).show();
                break;

            case Constants.NO_RESPONSE:
                pd.dismiss();
                Toast.makeText(activity, ""+event.getValue(), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void checkUserRating() {
        Log.e("ReviewsActivity", "size list: "+reviewsList.size());
        for (int i=0; i<ReviewsManager.reviewsList.size(); i++) {
            ReviewsData.Datum data = ReviewsManager.reviewsList.get(i);
            ReviewsData.User user = data.user;
            ReviewsData.Review subItem = data.review;
            Log.e("ReviewsActivity", "user_id: "+user.id+ "\nprofile_id: "+user_id);
            if (user.id.equals(user_id)) {
                tvAddReview.setText(R.string.edit_review);
                userComment = subItem.message;
                userRating = subItem.rating;
                return;
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        recyclerView.setAdapter(null);
    }
}
