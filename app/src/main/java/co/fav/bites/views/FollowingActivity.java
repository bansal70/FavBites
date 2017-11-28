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
import co.fav.bites.controller.FollowingManager;
import co.fav.bites.controller.ModelManager;
import co.fav.bites.models.Constants;
import co.fav.bites.models.Event;
import co.fav.bites.models.FBPreferences;
import co.fav.bites.models.Operations;
import co.fav.bites.models.Utils;
import co.fav.bites.models.beans.FollowingData;
import co.fav.bites.views.adapters.FollowingAdapter;

public class FollowingActivity extends BaseActivity implements View.OnClickListener{

    RecyclerView recyclerFollowing;
    FollowingAdapter followingAdapter;
    private List<FollowingData.FollowingTo> followingList;
    Dialog pd;
    String user_id, id;
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
        id = getIntent().getStringExtra("user_id");
        imgBack.setOnClickListener(this);

        ModelManager.getInstance().getFollowingManager().getFollowing(Operations
                .followingParams(id, user_id));
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
                break;
        }
    }

}