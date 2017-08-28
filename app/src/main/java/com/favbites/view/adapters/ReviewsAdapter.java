package com.favbites.view.adapters;

/*
 * Created by rishav on 8/21/2017.
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
import com.favbites.R;
import com.favbites.model.beans.ReviewsData;

import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {
    private Context context;
    private List<ReviewsData.Datum> reviewsList;

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
        holder.tvDescription.setText(dish.message);
        holder.tvDate.setText(dish.created);

        ReviewsData.User user = reviews.user;

        if (user.image == null)
            holder.imgCustomer.setImageResource(R.drawable.demo_img);
        else
            Glide.with(context).load(user.image).into(holder.imgCustomer);

        if (user.fname.isEmpty())
            holder.tvCustomer.setText(R.string.anonymous);
        else
            holder.tvCustomer.setText(user.fname + " " + user.lname);
    }

    @Override
    public int getItemCount() {
        return reviewsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tvCustomer, tvDate, tvDescription;
        private ImageView imgCustomer;
        private RatingBar rbRatings;

        ViewHolder(View itemView) {
            super(itemView);

            tvCustomer = itemView.findViewById(R.id.tvCustomerName);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            imgCustomer = itemView.findViewById(R.id.imgCustomer);
            rbRatings = itemView.findViewById(R.id.rbRatings);
        }
    }
}
