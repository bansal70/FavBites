package co.fav.bites.views.adapters;

/*
 * Created by rishav on 9/4/2017.
 */

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

import com.squareup.picasso.Picasso;

import java.util.List;

import co.fav.bites.R;
import co.fav.bites.controller.ModelManager;
import co.fav.bites.models.FBPreferences;
import co.fav.bites.models.Operations;
import co.fav.bites.models.Utils;
import co.fav.bites.models.beans.FollowingData;
import co.fav.bites.views.UserProfileActivity;

public class FollowingAdapter extends RecyclerView.Adapter<FollowingAdapter.ViewHolder> {

    private Context context;
    private List<FollowingData.FollowingTo> followingList;
    private String user_id;

    public FollowingAdapter(Context context, List<FollowingData.FollowingTo> followingList) {
        this.context = context;
        this.followingList = followingList;
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

        if (following.id.equals(user_id) || user_id.isEmpty())
            holder.tvUnFollow.setVisibility(View.GONE);
        else
            holder.tvUnFollow.setVisibility(View.VISIBLE);

        if (following.isFollow == 1)
            followUser(holder.tvUnFollow, "1");
        else
            followUser(holder.tvUnFollow, "2");
    }

    @Override
    public int getItemCount() {
        return followingList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView tvUser, tvUnFollow;
        private ImageView imgUser;
        private LinearLayout profileLayout;

        private ViewHolder(View itemView) {
            super(itemView);

            tvUser = itemView.findViewById(R.id.tvUser);
            tvUnFollow = itemView.findViewById(R.id.tvUnFollow);
            imgUser = itemView.findViewById(R.id.imgUser);
            profileLayout = itemView.findViewById(R.id.profileLayout);

            tvUnFollow.setOnClickListener(this);
            profileLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tvUnFollow:
                    FollowingData.FollowingTo following = followingList.get(getAdapterPosition());
                    String user_name = following.fname;
                    // follow => 1, un_follow => 2
                    if (tvUnFollow.getText().toString().equalsIgnoreCase("Follow")) {
                        ModelManager.getInstance().getFollowUserManager().followUser(
                                Operations.followUserParams(following.id, user_id, "1"));
                        followUser(tvUnFollow, "1");
                        Toast.makeText(context, "You're now following "+ user_name,
                                Toast.LENGTH_SHORT).show();
                    }
                    else {
                        ModelManager.getInstance().getFollowUserManager().followUser(
                                Operations.followUserParams(following.id, user_id, "2"));
                        followUser(tvUnFollow, "2");
                        Toast.makeText(context, "You're not following "+ user_name + " anymore",
                                Toast.LENGTH_SHORT).show();
                    }
                    break;

                case R.id.profileLayout:
                    FollowingData.FollowingTo following_data = followingList.get(getAdapterPosition());
                    String following_id = following_data.id;
                    FBPreferences.putString(context, "to_user_id", following_id);
                    context.startActivity(new Intent(context, UserProfileActivity.class)
                            .putExtra("user_id", following_id));
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
}