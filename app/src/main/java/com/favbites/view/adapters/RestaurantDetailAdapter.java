package com.favbites.view.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.favbites.R;

import java.util.ArrayList;

/*
 * Created by rishav on 8/18/2017.
 */

public class RestaurantDetailAdapter extends RecyclerView.Adapter<RestaurantDetailAdapter.ViewHolder> {

    private Context context;
    private ArrayList<String> list;

    public RestaurantDetailAdapter(Context context, ArrayList<String> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public RestaurantDetailAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_restaurant_menus, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RestaurantDetailAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
