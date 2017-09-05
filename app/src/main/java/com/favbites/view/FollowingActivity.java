package com.favbites.view;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.favbites.R;
import com.favbites.controller.FollowingManager;
import com.favbites.controller.ModelManager;
import com.favbites.model.Constants;
import com.favbites.model.Event;
import com.favbites.model.FBPreferences;
import com.favbites.model.Operations;
import com.favbites.model.Utils;
import com.favbites.model.beans.FollowingData;
import com.favbites.view.adapters.FollowingAdapter;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class FollowingActivity extends BaseActivity implements View.OnClickListener{

    RecyclerView recyclerFollowing;
    FollowingAdapter followingAdapter;
    private List<FollowingData.FollowingTo> followingList;
    KProgressHUD pd;
    String user_id;
    TextView tvNoFollowing;
    ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following);
        initViews();
    }

    public void initViews() {
        pd = Utils.showDialog(this);
        pd.show();

        followingList = new ArrayList<>();
        tvNoFollowing = (TextView) findViewById(R.id.tvNoFollowing);
        imgBack = (ImageView) findViewById(R.id.imgBack);

        recyclerFollowing = (RecyclerView) findViewById(R.id.recyclerFollowing);
        recyclerFollowing.setHasFixedSize(true);
        recyclerFollowing.setLayoutManager(new LinearLayoutManager(this));
        recyclerFollowing.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        followingAdapter = new FollowingAdapter(this, followingList);
        recyclerFollowing.setAdapter(followingAdapter);
        user_id = FBPreferences.readString(this, "user_id");
        imgBack.setOnClickListener(this);

        ModelManager.getInstance().getFollowingManager().getFollowing(Operations.followingParams(user_id));
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
            case Constants.FOLLOWING_SUCCESS:
                pd.dismiss();
                setData();
                break;

            case Constants.FOLLOWING_EMPTY:
                pd.dismiss();
                tvNoFollowing.setVisibility(View.VISIBLE);
                recyclerFollowing.setVisibility(View.GONE);
                break;

            case Constants.NO_RESPONSE:
                pd.dismiss();
                Toast.makeText(this, ""+event.getValue(), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void setData() {
        followingList.addAll(FollowingManager.followingList);
        followingAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBack:
                finish();
                recyclerFollowing.setAdapter(null);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        recyclerFollowing.setAdapter(null);
    }
}