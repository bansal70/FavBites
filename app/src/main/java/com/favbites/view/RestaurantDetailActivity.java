package com.favbites.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.favbites.R;
import com.favbites.view.adapters.RestaurantDetailAdapter;

import java.util.ArrayList;

public class RestaurantDetailActivity extends BaseActivity implements View.OnClickListener {

    private Context context = this;
    ArrayList<String> list;
    ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);

        initViews();
    }

    public void initViews() {
        list = new ArrayList<>();
        imgBack = (ImageView) findViewById(R.id.imgBack);
        imgBack.setOnClickListener(this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerMenus);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        list.add("a");
        list.add("b");
        list.add("c");
        list.add("a");
        list.add("b");
        list.add("c");

        RestaurantDetailAdapter restaurantDetailAdapter = new RestaurantDetailAdapter(this, list);
        recyclerView.setAdapter(restaurantDetailAdapter);
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
