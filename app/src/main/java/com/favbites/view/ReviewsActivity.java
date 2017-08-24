package com.favbites.view;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.favbites.R;
import com.favbites.model.Utils;
import com.favbites.view.adapters.ReviewsAdapter;

import java.util.ArrayList;

public class ReviewsActivity extends BaseActivity implements View.OnClickListener{

    private Activity activity = this;
    ArrayList<String> list;
    TextView tvItem, tvAddReview;
    ImageView imgBack;
    Dialog dialogReview;
    TextView dialogTvSubmit, dialogTvCancel;
    EditText editComment;
    RatingBar rbItemRating;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);

        initViews();
        initDialog();
    }

    public void initViews() {
        list = new ArrayList<>();
        tvItem = (TextView) findViewById(R.id.tvItem);
        tvAddReview = (TextView) findViewById(R.id.tvAddReview);
        imgBack = (ImageView) findViewById(R.id.imgBack);

        RecyclerView recyclerView  = (RecyclerView) findViewById(R.id.recyclerReviews);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.addItemDecoration(new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL));

        list.add("a");
        list.add("a");
        list.add("a");

        ReviewsAdapter reviewsAdapter = new ReviewsAdapter(activity, list);
        recyclerView.setAdapter(reviewsAdapter);
        tvItem.setText("Cheesecake");

        tvAddReview.setOnClickListener(this);
        imgBack.setOnClickListener(this);
        String p = getIntent().getStringExtra("position");
        position = Integer.parseInt(p);
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
                finish();
                break;

            case R.id.tvAddReview:
                dialogReview.show();
                break;

            case R.id.tvSubmit:
                if (rbItemRating.getRating() == 0.0) {
                    Toast.makeText(activity, "Please give the rating...", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(activity, "Review Submitted successfully.", Toast.LENGTH_SHORT).show();
                dialogReview.dismiss();
                break;

            case R.id.tvCancel:
                dialogReview.dismiss();
                break;
        }
    }
}
