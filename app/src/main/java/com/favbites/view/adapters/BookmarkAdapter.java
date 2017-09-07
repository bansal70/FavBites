package com.favbites.view.adapters;

/*
 * Created by win 10 on 9/1/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.favbites.R;
import com.favbites.model.beans.BookmarkData;
import com.favbites.view.RestaurantDetailActivity;

import java.util.List;


public class BookmarkAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<BookmarkData.Data> dataList;

    public BookmarkAdapter(Context context, List<BookmarkData.Data> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View view = LayoutInflater.from(context).inflate(R.layout.view_bookmarks_list, parent, false);
        vh = new ItemsViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        BookmarkData.Data data = dataList.get(position);
        BookmarkData.Restaurant restaurant = data.restaurant;

        String name = restaurant.name;
        String streetAddress = restaurant.streetAddress + ", "
                + restaurant.city + ", "
                + restaurant.state + " "
                + restaurant.zip;
        String logoUrl = restaurant.logoUrl;
        String phone = "Phone: " + restaurant.phone;

        ((ItemsViewHolder) holder).tvName.setText(name);
        ((ItemsViewHolder) holder).tvAddress.setText(String.format("Address: %s", streetAddress));
        ((ItemsViewHolder) holder).tvPhone.setText(phone);
        Glide.with(context)
                .load(logoUrl)
                .into(((ItemsViewHolder) holder).imgRestaurant);

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    private class ItemsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imgRestaurant;
        private TextView tvName, tvAddress, tvPhone;

        private ItemsViewHolder(View itemView) {
            super(itemView);

            imgRestaurant = itemView.findViewById(R.id.imgRestaurant);
            tvName = itemView.findViewById(R.id.tvName);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvPhone = itemView.findViewById(R.id.tvPhone);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            context.startActivity(new Intent(context, RestaurantDetailActivity.class)
                    .putExtra("position", String.valueOf(getAdapterPosition()))
                    .putExtra("restaurant_id", dataList.get(getAdapterPosition()).restaurant.id));
        }
    }
}