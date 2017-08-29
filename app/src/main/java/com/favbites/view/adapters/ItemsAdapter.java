package com.favbites.view.adapters;

/*
 * Created by win 10 on 8/23/2017.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.favbites.R;
import com.favbites.model.beans.RestaurantData;

import java.util.List;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {
    private Context context;
    private List<RestaurantData.Subitem> subItemList;

    public ItemsAdapter(Context context, List<RestaurantData.Subitem> subItemList) {
        this.context = context;
        this.subItemList = subItemList;
    }

    @Override
    public ItemsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_restaurant_menus, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemsAdapter.ViewHolder holder, int position) {
        RestaurantData.Subitem subItems = subItemList.get(position);

        holder.tvItemName.setText(subItems.name);
        holder.tvItemPrice.setText(String.format("$%s", subItems.basePrice));
        if (subItems.rating.isEmpty())
            holder.rbItemRatings.setRating(0.0f);
        else {
            float ratings = Float.parseFloat(subItems.rating);
            holder.rbItemRatings.setRating(ratings);
        }
    }

    @Override
    public int getItemCount() {
        return subItemList.size() > 3 ? 3 : subItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tvItemName, tvItemPrice;
        private RatingBar rbItemRatings;

        public ViewHolder(View itemView) {
            super(itemView);

            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvItemPrice = itemView.findViewById(R.id.tvItemPrice);
            rbItemRatings = itemView.findViewById(R.id.rbItemRatings);
        }
    }
}
