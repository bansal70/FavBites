package com.favbites.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.favbites.R;
import com.favbites.controller.ModelManager;
import com.favbites.model.Constants;
import com.favbites.model.Event;
import com.favbites.model.FBPreferences;
import com.favbites.model.Operations;
import com.favbites.model.Utils;
import com.favbites.model.beans.RestaurantData;
import com.favbites.view.adapters.RestaurantDetailAdapter;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class RestaurantDetailActivity extends BaseActivity implements View.OnClickListener {

    private Context context = this;
    ImageView imgBack, imgRestaurant, imgBookmark;
    TextView tvRestaurant, tvAddress, tvPhone, tvOpen;
    RatingBar rbRatings;
    int position;
    String isOpen;
    String restaurant_id, user_id, bookmark_status;
    TextView tvShowMore;
    int totalItems = 6;
    private RecyclerView recyclerView;
    private RestaurantDetailAdapter restaurantDetailAdapter;
    private List<RestaurantData.Subitem> subItemList;
    KProgressHUD pd;
    List<Object> bookmark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);

        initViews();
    }

    public void initViews() {
        pd = Utils.showMessageDialog(this, "Please wait...");
        user_id = FBPreferences.readString(this, "user_id");

        imgBack = (ImageView) findViewById(R.id.imgBack);
        imgRestaurant = (ImageView) findViewById(R.id.imgRestaurant);
        imgBookmark = (ImageView) findViewById(R.id.imgBookmark);

        rbRatings = (RatingBar) findViewById(R.id.rbRatings);
        tvRestaurant = (TextView) findViewById(R.id.tvRestaurant);
        tvAddress = (TextView) findViewById(R.id.tvAddress);
        tvPhone = (TextView) findViewById(R.id.tvPhone);
        tvOpen = (TextView) findViewById(R.id.tvOpen);
        tvShowMore = (TextView) findViewById(R.id.tvShowMore);

        imgBack.setOnClickListener(this);
        tvShowMore.setOnClickListener(this);
        imgBookmark.setOnClickListener(this);

        String p = getIntent().getStringExtra("position");
        position = Integer.parseInt(p);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerMenus);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        RestaurantData.Datum datum = Utils.restaurantsList.get(position);

        subItemList = datum.subitem;

        restaurantDetailAdapter = new RestaurantDetailAdapter(this, subItemList, totalItems);
        recyclerView.setAdapter(restaurantDetailAdapter);

        if (subItemList.size() > 6)
            tvShowMore.setVisibility(View.VISIBLE);

        setData();
    }

    public void setData() {
        RestaurantData.Datum datum = Utils.restaurantsList.get(position);
        RestaurantData.Restaurant restaurant = datum.restaurant;

        String name = restaurant.name;
        String streetAddress = "Address: " + restaurant.streetAddress + ", "
                + restaurant.city + ", "
                + restaurant.state + " "
                + restaurant.zip;
        String logoUrl = restaurant.logoUrl;
        String phone = "Phone: " + restaurant.phone;
        isOpen = restaurant.isOpen;
        restaurant_id = restaurant.id;
        FBPreferences.putString(this, "restaurant_id", restaurant_id);

        Glide.with(this)
                .load(logoUrl)
                .into(imgRestaurant);
        tvRestaurant.setText(name);
        tvAddress.setText(streetAddress);
        tvPhone.setText(phone);

        if (isOpen.equals("0")) {
            tvOpen.setText(R.string.closed_now);
            tvOpen.setTextColor(ContextCompat.getColor(context, R.color.red_color));
        } else {
            tvOpen.setText(R.string.open_now);
            tvOpen.setTextColor(ContextCompat.getColor(context, R.color.green_color));
        }

        bookmark = datum.bookmark;
        if (bookmark.isEmpty()) {
            bookmark_status = "0";
            imgBookmark.setImageResource(R.drawable.unbookmark);
        }
        else {
            bookmark_status = "1";
            imgBookmark.setImageResource(R.drawable.bookmark);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBack:
                finish();
                break;

            case R.id.tvShowMore:
                totalItems = Utils.restaurantsList.size();
                restaurantDetailAdapter = new RestaurantDetailAdapter(this, subItemList, totalItems);
                recyclerView.setAdapter(restaurantDetailAdapter);
                recyclerView.scrollToPosition(6);
                tvShowMore.setVisibility(View.GONE);
                break;

            case R.id.imgBookmark:
                if (user_id.isEmpty() || user_id == null) {
                    Toast.makeText(context, "Please login to bookmark any restaurant", Toast.LENGTH_SHORT).show();
                    return;
                }
                pd.show();
                if (bookmark_status.equals("0"))
                    ModelManager.getInstance().getBookmarkManager().bookmarkRestaurant(
                            Operations.bookmarkRestaurant(user_id, restaurant_id, "1"));
                else
                    ModelManager.getInstance().getBookmarkManager().bookmarkRestaurant(
                        Operations.bookmarkRestaurant(user_id, restaurant_id, "0"));

                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(Event event) {
        switch (event.getKey()) {

            case Constants.BOOKMARK_ADDED:
                pd.dismiss();
                if (bookmark_status.equals("0")) {
                    bookmark_status = "1";
                    Toast.makeText(context, "Restaurant has been bookmarked successfully.", Toast.LENGTH_SHORT).show();
                    imgBookmark.setImageResource(R.drawable.bookmark);
                }
                else {
                    bookmark_status = "0";
                    Toast.makeText(context, "Bookmark has been removed.", Toast.LENGTH_SHORT).show();
                    imgBookmark.setImageResource(R.drawable.unbookmark);
                }
                break;

            case Constants.NO_RESPONSE:
                pd.dismiss();
                Toast.makeText(context, ""+event.getValue(), Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
