package com.favbites.view.adapters;

/*
 * Created by rishav on 8/18/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.favbites.R;
import com.favbites.model.beans.RestaurantData;
import com.favbites.view.RestaurantDetailActivity;

import java.util.List;

public class RestaurantsAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<RestaurantData.Datum> restaurantsList;

    public RestaurantsAdapter(Context context, List<RestaurantData.Datum> restaurantsList) {
        this.context = context;
        this.restaurantsList = restaurantsList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View view = LayoutInflater.from(context).inflate(R.layout.view_restaurants_list, parent, false);
        vh = new ItemsViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        RestaurantData.Datum datum = restaurantsList.get(position);
        RestaurantData.Restaurant restaurant = datum.restaurant;

        String name = restaurant.name;
        String streetAddress = restaurant.streetAddress + ", "
                + restaurant.city + ", "
                + restaurant.state + " "
                + restaurant.zip;
        String logoUrl = restaurant.logoUrl;
        String isOpen = restaurant.isOpen;
        float rating = Float.parseFloat(restaurant.item_rating);

        ((ItemsViewHolder) holder).tvName.setText(name);
        ((ItemsViewHolder) holder).tvAddress.setText(String.format("Address: %s", streetAddress));
        ((ItemsViewHolder) holder).rbRatings.setRating(rating);
        Glide.with(context)
                .load(logoUrl)
                .into(((ItemsViewHolder) holder).imgRestaurant);

        if (isOpen.equals("0")) {
            ((ItemsViewHolder) holder).tvOpenToday.setText(R.string.closed_now);
            ((ItemsViewHolder) holder).tvOpenToday.setTextColor(ContextCompat.getColor(context, R.color.red_color));
        } else {
            ((ItemsViewHolder) holder).tvOpenToday.setText(R.string.open_now);
            ((ItemsViewHolder) holder).tvOpenToday.setTextColor(ContextCompat.getColor(context, R.color.green_color));
        }

        List<RestaurantData.Subitem> subItemList = datum.subitem;
        ItemsAdapter itemsAdapter = new ItemsAdapter(context, subItemList);
        ((ItemsViewHolder) holder).recyclerItems.setAdapter(itemsAdapter);

    }

    @Override
    public int getItemCount() {
        return restaurantsList.size();
    }

    private class ItemsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imgRestaurant;
        private TextView tvName, tvAddress, tvOpenToday;
        private RecyclerView recyclerItems;
        private RatingBar rbRatings;

        private ItemsViewHolder(View itemView) {
            super(itemView);

            imgRestaurant = itemView.findViewById(R.id.imgRestaurant);
            tvName = itemView.findViewById(R.id.tvName);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvOpenToday = itemView.findViewById(R.id.tvOpenToday);
            rbRatings = itemView.findViewById(R.id.rbRatings);

            recyclerItems = itemView.findViewById(R.id.recyclerItems);
            recyclerItems.setHasFixedSize(true);
            recyclerItems.setLayoutManager(new LinearLayoutManager(context));
            recyclerItems.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

            itemView.setOnClickListener(this);
            recyclerItems.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            context.startActivity(new Intent(context, RestaurantDetailActivity.class)
                    .putExtra("position", String.valueOf(getAdapterPosition()))
                    .putExtra("restaurant_id", restaurantsList.get(getAdapterPosition()).restaurant.id));
        }
    }
}