package co.fav.bites.views;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import co.fav.bites.R;
import co.fav.bites.controller.AccountManager;
import co.fav.bites.controller.ModelManager;
import co.fav.bites.models.Constants;
import co.fav.bites.models.Event;
import co.fav.bites.models.Operations;
import co.fav.bites.models.Utils;
import co.fav.bites.models.beans.AccountData;
import co.fav.bites.views.adapters.ViewPagerAdapter;
import co.fav.bites.views.fragments.PostsFragment;
import co.fav.bites.views.fragments.ReviewsFragment;

public class UserProfileActivity extends BaseActivity implements View.OnClickListener {

    TabLayout tabLayout;
    ViewPager viewPager;
    String user_id;
    Dialog pd;
    TextView tvFollowers,tvFollowing, tvUser;
    ImageView imgUser, imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        initViews();
    }

    public void initViews() {
        EventBus.getDefault().register(this);
        pd = Utils.showDialog(this);
        pd.show();
        user_id = getIntent().getStringExtra("user_id");
        ModelManager.getInstance().getAccountManager().userAccount(this, Operations.profileParams(user_id));

        tvFollowers = (TextView) findViewById(R.id.tvFollowers);
        tvFollowing = (TextView) findViewById(R.id.tvFollowing);
        imgUser = (ImageView) findViewById(R.id.imgUser);
        imgBack = (ImageView) findViewById(R.id.imgBack);
        tvUser = (TextView) findViewById(R.id.tvUser);

        tvFollowers.setOnClickListener(this);
        tvFollowing.setOnClickListener(this);
        imgBack.setOnClickListener(this);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        setUpTexts();
    }

    private void setUpTexts() {
        if (tabLayout != null) {
            TextView tab1 = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
            tab1.setText(R.string.posts);
            tabLayout.getTabAt(0).setCustomView(tab1);


            TextView tab2 = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
            tab2.setText(R.string.reviews);
            tabLayout.getTabAt(1).setCustomView(tab2);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        Bundle args = new Bundle();
        args.putString("user_id", user_id);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        PostsFragment postsFragment = new PostsFragment();
        postsFragment.setArguments(args);
        ReviewsFragment reviewsFragment = new ReviewsFragment();
        reviewsFragment.setArguments(args);

        adapter.addFragment(postsFragment, "POSTS");
        adapter.addFragment(reviewsFragment, "REVIEWS");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvFollowers:
                startActivity(new Intent(this, FollowersActivity.class)
                        .putExtra("user_id", user_id));
                break;

            case R.id.imgBack:
                finish();
                break;

            case R.id.tvFollowing:
                startActivity(new Intent(this, FollowingActivity.class)
                        .putExtra("user_id", user_id));
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!EventBus.getDefault().isRegistered(this))
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
            case Constants.PROFILE_SUCCESS:
                ModelManager.getInstance().getUserPostManager().getUserPosts(
                        Operations.userPostsParams(user_id, 1));
                pd.dismiss();
                setUserData();
                break;

            case Constants.PROFILE_EMPTY:
                pd.dismiss();
                Toast.makeText(this, Constants.SERVER_ERROR, Toast.LENGTH_SHORT).show();
                break;

            case Constants.NO_RESPONSE:
                pd.dismiss();
                Toast.makeText(this, ""+event.getValue(), Toast.LENGTH_SHORT).show();
                break;

            case Constants.NO_INTERNET:
                pd.dismiss();
                Toast.makeText(this, ""+event.getValue(), Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
    }

    public void setUserData() {
        AccountData.Data data = AccountManager.data;
        AccountData.User user = data.user;

        if (!user.image.isEmpty()) {
            Picasso.with(this)
                    .load(user.image)
                    .fit()
                    .transform(Utils.imageTransformation())
                    .placeholder(R.drawable.demo_img)
                    .into(imgUser);
        }
        String name = user.fname + " " + user.lname;
        if (!user.fname.isEmpty())
            tvUser.setText(name);

        List<AccountData.Follower> followersList = data.follower;
        List<AccountData.Following> followingsList = data.following;

        String followers = followersList.size() + "\n" + "Followers";
        String following = followingsList.size() + "\n" + "Following";

        tvFollowers.setText(followers);
        tvFollowing.setText(following);
    }

}
