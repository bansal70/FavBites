package com.favbites.view.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.favbites.R;
import com.favbites.model.beans.RestaurantData;
import com.favbites.view.ReviewsActivity;

import java.util.List;

/*
 * Created by rishav on 8/18/2017.
 */

public class RestaurantDetailAdapter extends RecyclerView.Adapter<RestaurantDetailAdapter.ViewHolder> {

    private Context context;
    private List<RestaurantData.Subitem> subItemList;
    private int totalItems;

    public RestaurantDetailAdapter(Context context, List<RestaurantData.Subitem> subItemList, int totalItems) {
        this.context = context;
        this.subItemList = subItemList;
        this.totalItems = totalItems;
    }

    @Override
    public RestaurantDetailAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_restaurant_menus, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RestaurantDetailAdapter.ViewHolder holder, int position) {
        RestaurantData.Subitem subItems = subItemList.get(position);

        holder.tvItemName.setText(subItems.name);
        holder.tvItemPrice.setText(String.format("$%s", subItems.basePrice));
    }

    @Override
    public int getItemCount() {
        if (totalItems == 6)
            return subItemList.size() > 6 ? 6 : subItemList.size();
        else
            return subItemList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvItemName, tvItemPrice;
        private RatingBar rbItemRatings;

        ViewHolder(View itemView) {
            super(itemView);

            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvItemPrice = itemView.findViewById(R.id.tvItemPrice);
            rbItemRatings = itemView.findViewById(R.id.rbItemRatings);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            context.startActivity(new Intent(context, ReviewsActivity.class)
                    .putExtra("dish_key", subItemList.get(getAdapterPosition()).key)
                    .putExtra("dish_name", subItemList.get(getAdapterPosition()).name));
        }
    }
}
