package com.favbites.view;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.favbites.R;
import com.favbites.controller.RestaurantDetailsManager;
import com.favbites.model.beans.RestaurantDetailsData;

import java.util.ArrayList;
import java.util.List;

public class PostsViewActivity extends BaseActivity implements View.OnClickListener{

    int position;
    List<RestaurantDetailsData.Comment> postsList;
    ImageView imgPost, imgBack;
    TextView tvAboutPhoto, tvUser, tvCreated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts_view);

        initViews();
    }

    public void initViews() {
        position = getIntent().getIntExtra("position", 0);
        postsList = new ArrayList<>();
        imgPost = (ImageView) findViewById(R.id.imgPost);

        tvAboutPhoto = (TextView) findViewById(R.id.tvAboutPhoto);
        tvUser = (TextView) findViewById(R.id.tvUser);
        tvCreated = (TextView) findViewById(R.id.tvCreated);

        RestaurantDetailsData.Data data = RestaurantDetailsManager.data;
        postsList.addAll(data.comment);

        RestaurantDetailsData.Comment comment = postsList.get(position);
        tvAboutPhoto.setText(comment.comment);
        tvCreated.setText(comment.created);
        Glide.with(this)
                .load(comment.image)
                .crossFade()
                .into(imgPost);

        RestaurantDetailsData.User user = comment.user;
        tvUser.setText(user.fname + " " + user.lname);

        imgBack = (ImageView) findViewById(R.id.imgBack);
        imgBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBack:
                finish();
                break;
        }
    }
}
