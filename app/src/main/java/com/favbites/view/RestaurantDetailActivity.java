package com.favbites.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.favbites.R;
import com.favbites.controller.RestaurantsManager;
import com.favbites.model.beans.RestaurantData;
import com.favbites.view.adapters.RestaurantDetailAdapter;

import java.util.List;

public class RestaurantDetailActivity extends BaseActivity implements View.OnClickListener {

    private Context context = this;
    ImageView imgBack, imgRestaurant;
    TextView tvRestaurant, tvAddress, tvPhone, tvOpen;
    RatingBar rbRatings;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);

        initViews();
    }

    public void initViews() {
        imgBack = (ImageView) findViewById(R.id.imgBack);

        imgRestaurant = (ImageView) findViewById(R.id.imgRestaurant);
        rbRatings = (RatingBar) findViewById(R.id.rbRatings);
        tvRestaurant = (TextView) findViewById(R.id.tvRestaurant);
        tvAddress = (TextView) findViewById(R.id.tvAddress);
        tvPhone = (TextView) findViewById(R.id.tvPhone);
        tvOpen = (TextView) findViewById(R.id.tvOpen);

        imgBack.setOnClickListener(this);

        String p = getIntent().getStringExtra("position");
        position = Integer.parseInt(p);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerMenus);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        RestaurantData.Datum datum = RestaurantsManager.datumList.get(position);

        List<RestaurantData.Subitem> subItemList = datum.subitem;
        RestaurantDetailAdapter restaurantDetailAdapter = new RestaurantDetailAdapter(this, subItemList);
        recyclerView.setAdapter(restaurantDetailAdapter);

        setData();
    }

    public void setData() {
        RestaurantData.Datum datum = RestaurantsManager.datumList.get(position);
        RestaurantData.Restaurant restaurant = datum.restaurant;
        String name = restaurant.name;
        String streetAddress = "Address: " + restaurant.streetAddress + ", "
                + restaurant.city + ", "
                + restaurant.state + " "
                + restaurant.zip;
        String logoUrl = restaurant.logoUrl;
        String phone = "Phone: " + restaurant.phone;

        Glide.with(this)
                .load(logoUrl)
                .into(imgRestaurant);
        tvRestaurant.setText(name);
        tvAddress.setText(streetAddress);
        tvPhone.setText(phone);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBack:
                finish();
                break;
        }
    }
}
