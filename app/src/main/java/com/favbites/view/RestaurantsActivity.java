package com.favbites.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.favbites.R;
import com.favbites.controller.ModelManager;
import com.favbites.controller.RestaurantsManager;
import com.favbites.model.Constants;
import com.favbites.model.Event;
import com.favbites.model.Operations;
import com.favbites.model.Utils;
import com.favbites.view.adapters.RestaurantsAdapter;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class RestaurantsActivity extends BaseActivity implements View.OnTouchListener {

    private Activity activity = this;
    KProgressHUD pd;
    private int page = 1;
    RecyclerView recyclerView;
    RestaurantsAdapter restaurantsAdapter;
    EditText editSearch;
    TextView tvResults;
    String search = "";
    boolean isSearch = false;
    LinearLayout resultLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurants);

        initViews();
    }

    public void initViews() {

        pd = Utils.showMessageDialog(this, "Fetching restaurants...");
        pd.show();
        resultLayout = (LinearLayout) findViewById(R.id.resultLayout);

        ModelManager.getInstance().getRestaurantsManager()
                .searchRestaurant(Operations.getSearchRestaurantParams("", page));

        editSearch = (EditText) findViewById(R.id.editSearch);
        editSearch.setOnTouchListener(this);
        tvResults = (TextView) findViewById(R.id.tvResults);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerRestaurants);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));

        restaurantsAdapter = new RestaurantsAdapter(this);
        recyclerView.setAdapter(restaurantsAdapter);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        final int DRAWABLE_RIGHT = 2;

        if (event.getAction() == MotionEvent.ACTION_UP) {

            if (event.getRawX() >= (editSearch.getRight() - editSearch.getCompoundDrawables()
                    [DRAWABLE_RIGHT].getBounds().width())) {
                search = editSearch.getText().toString().trim();
                if (search.isEmpty()) {
                   return true;
                }
                isSearch = true;
                pd.show();
                ModelManager.getInstance().getRestaurantsManager()
                        .searchRestaurant(Operations.getSearchRestaurantParams(search, 1));
                return true;
            }
        }
        return false;
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
            case Constants.RESTAURANTS_SEARCH_SUCCESS:
                if (isSearch) {
                    resultLayout.setVisibility(View.VISIBLE);
                    tvResults.setText(editSearch.getText().toString());
                    editSearch.setText("");
                }
                pd.dismiss();
                setData();
                break;

            case Constants.RESTAURANTS_SEARCH_FAILED:
                if (isSearch) {
                    resultLayout.setVisibility(View.VISIBLE);
                    tvResults.setText(editSearch.getText().toString());
                    editSearch.setText("");
                }

                pd.dismiss();
              //  Toast.makeText(activity, ""+event.getValue(), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private int visibleThreshold = 1;

    public void setData() {
        restaurantsAdapter.notifyDataSetChanged();

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                .getLayoutManager();

        recyclerView
                .addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView,
                                           int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);

                        totalItemCount = recyclerView.getLayoutManager().getItemCount();
                        lastVisibleItem = linearLayoutManager
                                .findLastVisibleItemPosition();
                        if (!loading
                                && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                            // End has been reached
                            //  if (list.size() != 0) {

                            // linearLayoutManager.scrollToPositionWithOffset(0, lastVisibleItem);
                            if (RestaurantsManager.datumList.size() > 9) {
                                pd.show();
                                ModelManager.getInstance().getRestaurantsManager()
                                        .searchRestaurant(Operations.getSearchRestaurantParams(search, ++page));
                            }
                            //                        restaurantsAdapter.notifyDataSetChanged();
                            loading = true;
                        }
                    }
                });

    }

}
