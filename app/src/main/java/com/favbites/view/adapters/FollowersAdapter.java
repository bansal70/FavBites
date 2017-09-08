package com.favbites.view.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.favbites.R;
import com.favbites.controller.ModelManager;
import com.favbites.model.Constants;
import com.favbites.model.Event;
import com.favbites.model.FBPreferences;
import com.favbites.model.Operations;
import com.favbites.model.Utils;
import com.favbites.model.beans.FollowersData;
import com.favbites.view.UserProfileActivity;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

/*
 * Created by rishav on 9/4/2017.
 */

public class FollowersAdapter extends RecyclerView.Adapter<FollowersAdapter.ViewHolder> {

    private Context context;
    private List<FollowersData.Follower> followerList;
    private String user_name;
    private String user_id;

    public FollowersAdapter(Context context, List<FollowersData.Follower> followerList) {
        this.context = context;
        this.followerList = followerList;
        EventBus.getDefault().register(this);
    }

    @Override
    public FollowersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_followers, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FollowersAdapter.ViewHolder holder, int position) {
        FollowersData.Follower follower = followerList.get(position);

        if (!follower.image.isEmpty())
        Picasso.with(context)
                .load(follower.image)
                .fit()
                .transform(Utils.imageTransformation())
                .placeholder(R.drawable.demo_img)
                .into(holder.imgUser);

        String name = follower.fname + " " + follower.lname;
        if (!name.isEmpty())
            holder.tvUser.setText(name);

        user_id = FBPreferences.readString(context, "user_id");

        if (follower.id.equals(user_id) || user_id.isEmpty())
            holder.tvFollow.setVisibility(View.GONE);
        else
            holder.tvFollow.setVisibility(View.VISIBLE);

        if (follower.isFollow == 1)
            followUser(holder.tvFollow, "1");
        else
            followUser(holder.tvFollow, "2");

    }

    @Override
    public int getItemCount() {
        return followerList.size();
    }


    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);

        EventBus.getDefault().unregister(this);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView tvUser, tvFollow;
        private ImageView imgUser;
        private LinearLayout profileLayout;

        private ViewHolder(View itemView) {
            super(itemView);

            tvUser = itemView.findViewById(R.id.tvUser);
            tvFollow = itemView.findViewById(R.id.tvFollow);
            imgUser = itemView.findViewById(R.id.imgUser);
            profileLayout = itemView.findViewById(R.id.profileLayout);

            tvFollow.setOnClickListener(this);
            profileLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tvFollow:

                    FollowersData.Follower follower = followerList.get(getAdapterPosition());
                    user_name = follower.fname;

                    // follow => 1, un_follow => 2
                    if (tvFollow.getText().toString().equalsIgnoreCase("Follow")) {
                        ModelManager.getInstance().getFollowUserManager().followUser(
                                Operations.followUserParams(follower.id, user_id, "1"));
                        followUser(tvFollow, "1");
                    }
                    else {
                        ModelManager.getInstance().getFollowUserManager().followUser(
                                Operations.followUserParams(follower.id, user_id, "2"));
                        followUser(tvFollow, "2");
                    }
                    break;

                case R.id.profileLayout:
                    FollowersData.Follower follower_data = followerList.get(getAdapterPosition());
                    String follower_id = follower_data.id;
                    FBPreferences.putString(context, "to_user_id", follower_id);
                    context.startActivity(new Intent(context, UserProfileActivity.class)
                                .putExtra("user_id", follower_id));
                    break;
            }
        }
    }

    private void followUser(TextView tvFollow, String status) {
        if (status.equals("1")) {
            tvFollow.setText(R.string.following);
            tvFollow.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));
            tvFollow.setBackgroundResource(R.color.colorHome);
            tvFollow.setCompoundDrawablesWithIntrinsicBounds(R.drawable.followed, 0, 0, 0);
        } else {
            tvFollow.setText(R.string.follow);
            tvFollow.setTextColor(ContextCompat.getColor(context, R.color.colorBlack));
            tvFollow.setBackgroundResource(R.color.colorPrimary);
            tvFollow.setCompoundDrawablesWithIntrinsicBounds(R.drawable.follow, 0, 0, 0);
        }
    }

    @Subscribe
    public void onEvent(Event event) {
        switch (event.getKey()) {
            case Constants.FOLLOW_SUCCESS:
                Toast.makeText(context, "You're now following "+user_name, Toast.LENGTH_SHORT).show();
                break;

            case Constants.UNFOLLOW_SUCCESS:
                Toast.makeText(context, "You're not following "+user_name+ " anymore", Toast.LENGTH_SHORT).show();
                break;

            case Constants.NO_RESPONSE:
                break;
        }
    }
}
