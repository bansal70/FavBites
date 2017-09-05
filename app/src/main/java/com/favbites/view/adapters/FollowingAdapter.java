package com.favbites.view.adapters;

/*
 * Created by rishav on 9/4/2017.
 */

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.favbites.R;
import com.favbites.controller.ModelManager;
import com.favbites.model.Constants;
import com.favbites.model.Event;
import com.favbites.model.FBPreferences;
import com.favbites.model.Operations;
import com.favbites.model.Utils;
import com.favbites.model.beans.FollowingData;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class FollowingAdapter extends RecyclerView.Adapter<FollowingAdapter.ViewHolder> {

    private Context context;
    private List<FollowingData.FollowingTo> followingList;
    private String user_id;
    private String user_name;

    public FollowingAdapter(Context context, List<FollowingData.FollowingTo> followingList) {
        this.context = context;
        this.followingList = followingList;

        EventBus.getDefault().register(this);
    }

    @Override
    public FollowingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_following, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FollowingAdapter.ViewHolder holder, int position) {
        FollowingData.FollowingTo following = followingList.get(position);

        if (!following.image.isEmpty())
            Picasso.with(context)
                    .load(following.image)
                    .fit()
                    .transform(Utils.imageTransformation())
                    .placeholder(R.drawable.demo_img)
                    .into(holder.imgUser);

        String name = following.fname + " " + following.lname;
        if (!name.isEmpty())
            holder.tvUser.setText(name);

        user_id = FBPreferences.readString(context, "user_id");
    }

    @Override
    public int getItemCount() {
        return followingList.size();
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);

        EventBus.getDefault().unregister(this);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvUser, tvUnFollow;
        ImageView imgUser;

        private ViewHolder(View itemView) {
            super(itemView);

            tvUser = itemView.findViewById(R.id.tvUser);
            tvUnFollow = itemView.findViewById(R.id.tvUnFollow);
            imgUser = itemView.findViewById(R.id.imgUser);

            tvUnFollow.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tvUnFollow:
                    FollowingData.FollowingTo following = followingList.get(getAdapterPosition());
                    user_name = following.fname;
                    Log.e("Adapter", "name: "+user_name);

                    // follow => 1, un_follow => 2
                    if (tvUnFollow.getText().toString().equalsIgnoreCase("Follow")) {
                        ModelManager.getInstance().getFollowUserManager().followUser(
                                Operations.followUserParams(following.id, user_id, "1"));
                        followUser(tvUnFollow, "1");
                    }
                    else {
                        ModelManager.getInstance().getFollowUserManager().followUser(
                                Operations.followUserParams(following.id, user_id, "2"));
                        followUser(tvUnFollow, "2");
                    }
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