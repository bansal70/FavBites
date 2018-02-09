package co.fav.bites.views.adapters;

/*
 * Created by rishav on 8/18/2017.
 */

import android.app.Activity;
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

import java.util.List;

import co.fav.bites.R;
import co.fav.bites.models.Constants;
import co.fav.bites.models.beans.RestaurantData;
import co.fav.bites.views.RestaurantDetailActivity;

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

        String isOpen = "0";
        if (restaurant.isOpen != null)
            isOpen = restaurant.isOpen;

        float rating = 0.0f;
        if (restaurant.item_rating != null && !restaurant.item_rating.equals(""))
            rating = Float.parseFloat(restaurant.item_rating);

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

        List<RestaurantData.Subitem> subItemsList = datum.subitem;

       /* Collections.sort(subItemsList, (item1, item2) -> {
            float menu1 = 0.0f, menu2 = 0.0f;
            if (!item1.getRating().isEmpty())
                menu1 = Float.parseFloat(item1.getRating());
            if (!item2.getRating().isEmpty())
                menu2 = Float.parseFloat(item2.getRating());
            return menu2 < menu1 ? -1 : menu1 == menu2 ? 0 : 1;
        });*/

        ItemsAdapter itemsAdapter = new ItemsAdapter(context, subItemsList);
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
           // recyclerItems.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            ((Activity)context).startActivityForResult(new Intent(context, RestaurantDetailActivity.class)
                    .putExtra("position", String.valueOf(getAdapterPosition()))
                    .putExtra("restaurant_id", restaurantsList.get(getAdapterPosition()).restaurant.id), Constants.DETAILS_REQUEST_CODE);
        }
    }
}