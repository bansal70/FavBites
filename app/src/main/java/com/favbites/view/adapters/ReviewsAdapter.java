package com.favbites.view.adapters;

/*
 * Created by rishav on 8/21/2017.
 */

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.favbites.R;
import com.favbites.controller.ModelManager;
import com.favbites.model.Constants;
import com.favbites.model.Event;
import com.favbites.model.FBPreferences;
import com.favbites.model.Operations;
import com.favbites.model.Utils;
import com.favbites.model.beans.ReviewsData;
import com.favbites.view.UserProfileActivity;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {
    private Context context;
    private List<ReviewsData.Datum> reviewsList;
    private String user_id;
    private Dialog pd;
    private String user_name;

    public ReviewsAdapter(Context context, List<ReviewsData.Datum> reviewsList) {
        this.context = context;
        this.reviewsList = reviewsList;
        pd = Utils.showDialog(context);
        EventBus.getDefault().register(this);
    }

    @Override
    public ReviewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_reviews, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewsAdapter.ViewHolder holder, int position) {
        ReviewsData.Datum reviews = reviewsList.get(position);

        ReviewsData.Review dish = reviews.review;
        float ratings = Float.parseFloat(dish.rating);
        holder.rbRatings.setRating(ratings);
        holder.tvDescription.setText(dish.message);
        holder.tvDate.setText(dish.created);

        ReviewsData.User user = reviews.user;

        user_id = FBPreferences.readString(context, "user_id");

        if (user_id.equals(user.id) || user_id.isEmpty())
            holder.tvFollow.setVisibility(View.INVISIBLE);
        else
            holder.tvFollow.setVisibility(View.VISIBLE);

        if (user.image.isEmpty())
            holder.imgCustomer.setImageResource(R.drawable.demo_img);
        else
            Picasso.with(context)
                    .load(user.image)
                    .fit()
                    .transform(Utils.imageTransformation())
                    .placeholder(R.drawable.demo_img)
                    .into(holder.imgCustomer);

        if (user.fname.isEmpty())
            holder.tvCustomer.setText(R.string.anonymous);
        else
            holder.tvCustomer.setText(user.fname + " " + user.lname);

        if (user.isFollow == 1) {
            followUser(holder.tvFollow, "1");
        } else {
            followUser(holder.tvFollow, "2");
        }

    }

    @Override
    public int getItemCount() {
        return reviewsList.size();
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);

        EventBus.getDefault().unregister(this);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView tvCustomer, tvDate, tvDescription, tvFollow;
        private ImageView imgCustomer;
        private RatingBar rbRatings;
        private LinearLayout profileLayout;

        private ViewHolder(View itemView) {
            super(itemView);

            tvCustomer = itemView.findViewById(R.id.tvCustomerName);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            imgCustomer = itemView.findViewById(R.id.imgCustomer);
            rbRatings = itemView.findViewById(R.id.rbRatings);
            tvFollow = itemView.findViewById(R.id.tvFollow);
            profileLayout = itemView.findViewById(R.id.profileLayout);

            tvFollow.setOnClickListener(this);
            profileLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tvFollow:
                    ReviewsData.Datum reviews = reviewsList.get(getAdapterPosition());
                    ReviewsData.User user = reviews.user;
                    user_name = user.fname;
                    pd.show();

                    // follow => 1, un_follow => 2
                    if (tvFollow.getText().toString().equalsIgnoreCase("Follow")) {
                        ModelManager.getInstance().getFollowUserManager().followUser(
                                Operations.followUserParams(user.id, user_id, "1"));
                        followUser(tvFollow, "1");
                    }
                    else {
                        ModelManager.getInstance().getFollowUserManager().followUser(
                                Operations.followUserParams(user.id, user_id, "2"));
                        followUser(tvFollow, "2");
                    }
                    break;

                case R.id.profileLayout:
                    ReviewsData.Datum reviews_data = reviewsList.get(getAdapterPosition());
                    ReviewsData.User user_data = reviews_data.user;

                    String id = user_data.id;
                    FBPreferences.putString(context, "to_user_id", id);
                    context.startActivity(new Intent(context, UserProfileActivity.class)
                            .putExtra("user_id", id));
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
                pd.dismiss();
                Toast.makeText(context, "You're now following "+user_name, Toast.LENGTH_SHORT).show();
                break;

            case Constants.UNFOLLOW_SUCCESS:
                pd.dismiss();
                Toast.makeText(context, "You're not following "+user_name+ " anymore", Toast.LENGTH_SHORT).show();
                break;
            case Constants.NO_RESPONSE:
                pd.dismiss();
                break;
        }
    }

}
