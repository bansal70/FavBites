package co.fav.bites.views.adapters;

/*
 * Created by rishav on 9/5/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

import co.fav.bites.R;
import co.fav.bites.models.Utils;
import co.fav.bites.models.beans.UserReviewsData;
import co.fav.bites.views.RestaurantDetailActivity;

public class FragmentReviewsAdapter extends RecyclerView.Adapter<FragmentReviewsAdapter.ViewHolder>{

    private Context context;
    private List<UserReviewsData.Data> reviewsList;

    public FragmentReviewsAdapter(Context context, List<UserReviewsData.Data> reviewsList) {
        this.context = context;
        this.reviewsList = reviewsList;
    }

    @Override
    public FragmentReviewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_fragment_reviews, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FragmentReviewsAdapter.ViewHolder holder, int position) {
        UserReviewsData.Data data = reviewsList.get(position);
        UserReviewsData.Review reviews = data.review;
        UserReviewsData.User user = data.user;
        UserReviewsData.Restaurant restaurant = data.restaurant;
        UserReviewsData.Subitem subItem = data.subitem;

        holder.tvRestaurant.setText(String.format("%s(%s)", restaurant.name, subItem.name));
        holder.tvAddress.setText(restaurant.streetAddress);

        if (reviews.message.isEmpty())
            holder.tvReview.setVisibility(View.GONE);
        else
            holder.tvReview.setVisibility(View.VISIBLE);

        holder.tvReview.setText(reviews.message);
        float rating = Float.parseFloat(reviews.rating);
        holder.rbRatings.setRating(rating);

        Utils.loadImage(context, restaurant.logoUrl, holder.imgRestaurant, R.mipmap.ic_launcher);
        Utils.loadCircularImage(context, user.image, holder.imgUser, R.drawable.demo_img);
    }

    @Override
    public int getItemCount() {
        return reviewsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tvRestaurant, tvAddress, tvReview;
        private ImageView imgRestaurant, imgUser;
        private RatingBar rbRatings;
        private LinearLayout restaurantLL;

        public ViewHolder(View itemView) {
            super(itemView);

            tvRestaurant = itemView.findViewById(R.id.tvRestaurant);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvReview = itemView.findViewById(R.id.tvReview);
            imgRestaurant = itemView.findViewById(R.id.imgRestaurant);
            imgUser = itemView.findViewById(R.id.imgUser);
            rbRatings = itemView.findViewById(R.id.rbRatings);

            restaurantLL = itemView.findViewById(R.id.restaurantLL);

            restaurantLL.setOnClickListener(view -> {
                UserReviewsData.Data data = reviewsList.get(getAdapterPosition());
                context.startActivity(new Intent(context, RestaurantDetailActivity.class)
                        .putExtra("restaurant_id", data.restaurant.id));
            });
        }
    }
}
