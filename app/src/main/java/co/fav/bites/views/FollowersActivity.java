package co.fav.bites.views;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import co.fav.bites.R;
import co.fav.bites.controller.FollowersManager;
import co.fav.bites.controller.ModelManager;
import co.fav.bites.models.Constants;
import co.fav.bites.models.Event;
import co.fav.bites.models.FBPreferences;
import co.fav.bites.models.Operations;
import co.fav.bites.models.Utils;
import co.fav.bites.models.beans.FollowersData;
import co.fav.bites.views.adapters.FollowersAdapter;

public class FollowersActivity extends BaseActivity implements View.OnClickListener{

    RecyclerView recyclerFollowers;
    FollowersAdapter followersAdapter;
    private List<FollowersData.Follower> followerList;
    Dialog pd;
    String user_id, id;
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
        user_id = FBPreferences.readString(this, "user_id");
        id = getIntent().getStringExtra("user_id");
        imgBack.setOnClickListener(this);

        ModelManager.getInstance().getFollowersManager().getFollowers(Operations
                .followersParams(id, user_id));
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

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(Event event) {
        EventBus.getDefault().removeStickyEvent(Event.class);
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
                break;
        }
    }

}
