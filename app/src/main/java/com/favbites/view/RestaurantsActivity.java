package com.favbites.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.favbites.R;
import com.favbites.view.adapters.RestaurantsAdapter;

import java.util.ArrayList;

public class RestaurantsActivity extends BaseActivity {

    private Activity activity = this;
    ArrayList<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurants);

        initViews();
    }

    public void initViews() {
        list = new ArrayList<>();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerRestaurants);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        list.add("a");
        list.add("b");
        list.add("c");

        RestaurantsAdapter restaurantsAdapter = new RestaurantsAdapter(this, list);
        recyclerView.setAdapter(restaurantsAdapter);
    }
}
