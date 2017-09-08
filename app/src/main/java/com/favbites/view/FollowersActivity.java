package com.favbites.view;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.favbites.R;
import com.favbites.controller.FollowersManager;
import com.favbites.controller.ModelManager;
import com.favbites.model.Constants;
import com.favbites.model.Event;
import com.favbites.model.Operations;
import com.favbites.model.Utils;
import com.favbites.model.beans.FollowersData;
import com.favbites.view.adapters.FollowersAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class FollowersActivity extends BaseActivity implements View.OnClickListener{

    RecyclerView recyclerFollowers;
    FollowersAdapter followersAdapter;
    private List<FollowersData.Follower> followerList;
    Dialog pd;
    String user_id;
    TextView tvNoFollowers;
    ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);

        initViews();
    }

    public void initViews() {
        pd = Utils.showDialog(this);
        pd.show();

        followerList = new ArrayList<>();
        tvNoFollowers = (TextView) findViewById(R.id.tvNoFollower);
        imgBack = (ImageView) findViewById(R.id.imgBack);

        recyclerFollowers = (RecyclerView) findViewById(R.id.recyclerFollowers);
        recyclerFollowers.setHasFixedSize(true);
        recyclerFollowers.setLayoutManager(new LinearLayoutManager(this));
        recyclerFollowers.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        followersAdapter = new FollowersAdapter(this, followerList);
        recyclerFollowers.setAdapter(followersAdapter);
        user_id = getIntent().getStringExtra("user_id");
        imgBack.setOnClickListener(this);

        ModelManager.getInstance().getFollowersManager().getFollowers(Operations.followersParams(user_id));
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
            case Constants.FOLLOWER_SUCCESS:
                pd.dismiss();
                setData();
                break;

            case Constants.FOLLOWER_EMPTY:
                pd.dismiss();
                tvNoFollowers.setVisibility(View.VISIBLE);
                recyclerFollowers.setVisibility(View.GONE);
                break;

            case Constants.NO_RESPONSE:
                pd.dismiss();
                Toast.makeText(this, ""+event.getValue(), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void setData() {
        followerList.addAll(FollowersManager.followerList);
        followersAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBack:
                finish();
                recyclerFollowers.setAdapter(null);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        recyclerFollowers.setAdapter(null);
    }
}
