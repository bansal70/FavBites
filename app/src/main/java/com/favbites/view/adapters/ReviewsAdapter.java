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

import com.favbites.R;

import java.util.ArrayList;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {
    private Context context;
    private ArrayList<String> list;

    public ReviewsAdapter(Context context, ArrayList<String> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ReviewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_reviews, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewsAdapter.ViewHolder holder, int position) {
        holder.tvCustomer.setText("Johoney Scandy");
        holder.tvDate.setText("Aug 21, 2017");
        holder.tvDescription.setText("Amazing taste and would love to recommend this to everyone. " +
                "It's delicious and at affordable price");
        holder.rbRatings.setRating(5.0f);
    }

    @Override
    public int getItemCount() {
        return list.size();
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
