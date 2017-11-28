package co.fav.bites.views.adapters;

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
import co.fav.bites.models.beans.RestaurantDetailsData;
import co.fav.bites.views.ReviewsActivity;

/*
 * Created by rishav on 8/18/2017.
 */

public class RestaurantDetailAdapter extends RecyclerView.Adapter<RestaurantDetailAdapter.ViewHolder> {

    private Context context;
    private List<RestaurantDetailsData.Subitem> subItemList;
    public RestaurantDetailAdapter(Context context, List<RestaurantDetailsData.Subitem> subItemList) {
        this.context = context;
        this.subItemList = subItemList;
    }

    @Override
    public RestaurantDetailAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_restaurant_menus, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RestaurantDetailAdapter.ViewHolder holder, int position) {
        RestaurantDetailsData.Subitem subItems = subItemList.get(position);

        holder.tvItemName.setText(subItems.name);
        holder.tvItemPrice.setText(String.format("$%s", subItems.basePrice));
        if (subItems.rating.isEmpty())
            holder.rbItemRatings.setRating(0.0f);
        else {
            float ratings = Float.parseFloat(subItems.rating);
            holder.rbItemRatings.setRating(ratings);
        }
        holder.tvReviewCount.setText("(" + subItems.reviewCount + ")");
    }

    @Override
    public int getItemCount() {
        return subItemList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvItemName, tvItemPrice, tvReviewCount;
        private RatingBar rbItemRatings;

        ViewHolder(View itemView) {
            super(itemView);

            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvItemPrice = itemView.findViewById(R.id.tvItemPrice);
            rbItemRatings = itemView.findViewById(R.id.rbItemRatings);
            tvReviewCount = itemView.findViewById(R.id.tvReviewCount);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            context.startActivity(new Intent(context, ReviewsActivity.class)
                    .putExtra("dish_key", subItemList.get(getAdapterPosition()).key)
                    .putExtra("dish_name", subItemList.get(getAdapterPosition()).name)
                    .putExtra("restaurant_id", subItemList.get(getAdapterPosition()).restaurantId));
        }
    }
}
