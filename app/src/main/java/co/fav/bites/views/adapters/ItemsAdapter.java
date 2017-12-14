package co.fav.bites.views.adapters;

/*
 * Created by win 10 on 8/23/2017.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

import co.fav.bites.R;
import co.fav.bites.models.Constants;
import co.fav.bites.models.beans.RestaurantData;
import co.fav.bites.views.ReviewsActivity;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {
    private Context context;
    private List<RestaurantData.Subitem> subItemList;

    public ItemsAdapter() {
    }

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
        holder.tvReviewCount.setText(String.format("(%s)", String.valueOf(subItems.reviewCount)));
    }

    @Override
    public int getItemCount() {
        return subItemList.size() > 3 ? 3 : subItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView tvItemName, tvItemPrice, tvReviewCount;
        private RatingBar rbItemRatings;

        public ViewHolder(View itemView) {
            super(itemView);

            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvItemPrice = itemView.findViewById(R.id.tvItemPrice);
            rbItemRatings = itemView.findViewById(R.id.rbItemRatings);
            tvReviewCount = itemView.findViewById(R.id.tvReviewCount);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            ((Activity)context).startActivityForResult(new Intent(context, ReviewsActivity.class)
                    .putExtra("dish_key", subItemList.get(getAdapterPosition()).key)
                    .putExtra("dish_name", subItemList.get(getAdapterPosition()).name)
                    .putExtra("restaurant_id", subItemList.get(getAdapterPosition()).restaurantId), Constants.MENU_REQUEST_CODE);
        }
    }

    public List<RestaurantData.Subitem> getSubItemList() {
        return subItemList;
    }
}
