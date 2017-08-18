package com.favbites.view.adapters;

/*
 * Created by rishav on 8/18/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.favbites.R;
import com.favbites.view.RestaurantDetailActivity;

import java.util.ArrayList;

public class RestaurantsAdapter extends RecyclerView.Adapter<RestaurantsAdapter.ViewHolder>{
    private Context context;
    private ArrayList<String> list;

    public RestaurantsAdapter(Context context, ArrayList<String> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public RestaurantsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_restaurants_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RestaurantsAdapter.ViewHolder holder, int position) {
        Glide.with(context)
                .load("https://eatstreet-static.s3.amazonaws.com/assets/images/restaurant_logos/capriottis-sandwich-shop-regent-st-6186_1414086898208.png")
                .into(holder.imgRestaurant);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView imgRestaurant;

        ViewHolder(View itemView) {
            super(itemView);

            imgRestaurant = itemView.findViewById(R.id.imgRestaurant);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            context.startActivity(new Intent(context, RestaurantDetailActivity.class));
        }
    }
}
