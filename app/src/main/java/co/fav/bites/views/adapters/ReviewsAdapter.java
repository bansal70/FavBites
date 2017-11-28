package co.fav.bites.views.adapters;

/*
 * Created by rishav on 8/21/2017.
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
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import co.fav.bites.R;
import co.fav.bites.controller.ModelManager;
import co.fav.bites.models.FBPreferences;
import co.fav.bites.models.Operations;
import co.fav.bites.models.Utils;
import co.fav.bites.models.beans.ReviewsData;
import co.fav.bites.views.UserProfileActivity;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {
    private Context context;
    private List<ReviewsData.Datum> reviewsList;
    private String user_id;

    public ReviewsAdapter(Context context, List<ReviewsData.Datum> reviewsList) {
        this.context = context;
        this.reviewsList = reviewsList;
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

        if (dish.message.isEmpty())
            holder.tvDescription.setVisibility(View.GONE);
        else
            holder.tvDescription.setVisibility(View.VISIBLE);

        holder.tvDescription.setText(dish.message);
        holder.tvDate.setText(dish.modified);

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
                    String user_name = user.fname;
                    // follow => 1, un_follow => 2
                    if (tvFollow.getText().toString().equalsIgnoreCase("Follow")) {
                        ModelManager.getInstance().getFollowUserManager().followUser(
                                Operations.followUserParams(user.id, user_id, "1"));
                        followUser(tvFollow, "1");
                        Toast.makeText(context, "You're now following "+ user_name, Toast.LENGTH_SHORT).show();
                    }
                    else {
                        ModelManager.getInstance().getFollowUserManager().followUser(
                                Operations.followUserParams(user.id, user_id, "2"));
                        followUser(tvFollow, "2");
                        Toast.makeText(context, "You're not following "+ user_name + " anymore", Toast.LENGTH_SHORT).show();
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

}
