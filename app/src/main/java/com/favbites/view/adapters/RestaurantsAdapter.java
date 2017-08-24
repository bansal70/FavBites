package com.favbites.view.adapters;

/*
 * Created by rishav on 8/18/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.favbites.R;
import com.favbites.controller.RestaurantsManager;
import com.favbites.model.beans.RestaurantData;
import com.favbites.view.RestaurantDetailActivity;

import java.util.List;

public class RestaurantsAdapter extends RecyclerView.Adapter {
    private Context context;

    public RestaurantsAdapter(Context context) {
        this.context = context;
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

            RestaurantData.Datum datum = RestaurantsManager.datumList.get(position);
            RestaurantData.Restaurant restaurant = datum.restaurant;

            String name = restaurant.name;
            String streetAddress = restaurant.streetAddress + ", "
                    + restaurant.city + ", "
                    + restaurant.state + " "
                    + restaurant.zip;
            String logoUrl = restaurant.logoUrl;

            ((ItemsViewHolder)holder).tvName.setText(name);
            ((ItemsViewHolder)holder).tvAddress.setText(String.format("Address: %s", streetAddress));
            Glide.with(context)
                    .load(logoUrl)
                    .into(((ItemsViewHolder)holder).imgRestaurant);

            List<RestaurantData.Subitem> subItemList = datum.subitem;
            ItemsAdapter itemsAdapter = new ItemsAdapter(context, subItemList);
            ((ItemsViewHolder) holder).recyclerItems.setAdapter(itemsAdapter);
    }

    @Override
    public int getItemCount() {
        return RestaurantsManager.datumList.size();
    }

    public class ItemsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView imgRestaurant;
        private TextView tvName, tvAddress, tvOpenToday;
        private RecyclerView recyclerItems;

       public ItemsViewHolder(View itemView) {
            super(itemView);

            imgRestaurant = itemView.findViewById(R.id.imgRestaurant);
            tvName = itemView.findViewById(R.id.tvName);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvOpenToday = itemView.findViewById(R.id.tvOpenToday);

           recyclerItems = itemView.findViewById(R.id.recyclerItems);
           recyclerItems.setHasFixedSize(true);
           recyclerItems.setLayoutManager(new LinearLayoutManager(context));
           recyclerItems.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            context.startActivity(new Intent(context, RestaurantDetailActivity.class)
            .putExtra("position", String.valueOf(getAdapterPosition())));
        }
    }
}
