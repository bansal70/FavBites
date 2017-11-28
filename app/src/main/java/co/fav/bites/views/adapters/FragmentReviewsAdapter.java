package co.fav.bites.views.adapters;

/*
 * Created by rishav on 9/5/2017.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import co.fav.bites.R;
import co.fav.bites.models.beans.UserReviewsData;

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

        holder.tvRestaurant.setText(restaurant.name + "(" + subItem.name + ")");
        holder.tvAddress.setText(restaurant.streetAddress);

        if (reviews.message.isEmpty())
            holder.tvReview.setVisibility(View.GONE);
        else
            holder.tvReview.setVisibility(View.VISIBLE);

        holder.tvReview.setText(reviews.message);
        float rating = Float.parseFloat(reviews.rating);
        holder.rbRatings.setRating(rating);

        if (!restaurant.logoUrl.isEmpty())
        Glide.with(context)
                .load(restaurant.logoUrl)
                .crossFade()
                .into(holder.imgRestaurant);

        if (!user.image.isEmpty())
        Glide.with(context)
                .load(user.image)
                .crossFade()
                .into(holder.imgUser);

    }

    @Override
    public int getItemCount() {
        return reviewsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tvRestaurant, tvAddress, tvReview;
        private ImageView imgRestaurant, imgUser;
        private RatingBar rbRatings;

        public ViewHolder(View itemView) {
            super(itemView);

            tvRestaurant = itemView.findViewById(R.id.tvRestaurant);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvReview = itemView.findViewById(R.id.tvReview);
            imgRestaurant = itemView.findViewById(R.id.imgRestaurant);
            imgUser = itemView.findViewById(R.id.imgUser);
            rbRatings = itemView.findViewById(R.id.rbRatings);
        }
    }
}
