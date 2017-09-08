package com.favbites.view;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.favbites.R;
import com.favbites.controller.BookmarkManager;
import com.favbites.controller.ModelManager;
import com.favbites.model.Constants;
import com.favbites.model.Event;
import com.favbites.model.FBPreferences;
import com.favbites.model.Operations;
import com.favbites.model.Utils;
import com.favbites.model.beans.BookmarkData;
import com.favbites.view.adapters.BookmarkAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class BookmarkRestaurantsActivity extends BaseActivity implements View.OnClickListener{

    private List<BookmarkData.Data> dataList;
    RecyclerView recyclerView;
    BookmarkAdapter bookmarkAdapter;
    Dialog pd;
    String user_id;
    TextView tvNoBookmarks;
    ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark_restaurants);

        initViews();
    }

    public void initViews() {
        dataList = new ArrayList<>();
        pd = Utils.showDialog(this);
        user_id = FBPreferences.readString(this, "user_id");

        tvNoBookmarks = (TextView) findViewById(R.id.tvNoBookmarks);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerBookmarks);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        imgBack = (ImageView) findViewById(R.id.imgBack);

        bookmarkAdapter = new BookmarkAdapter(this, dataList);
        recyclerView.setAdapter(bookmarkAdapter);

        imgBack.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        pd.show();
        dataList.clear();
        ModelManager.getInstance().getBookmarkManager().getBookmarks(
                Operations.getBookmarkRestaurantsParams(user_id));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBack:
                finish();
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
            case Constants.BOOKMARK_RESTAURANTS_SUCCESS:
                pd.dismiss();
                setData();
                break;

            case Constants.BOOKMARK_RESTAURANTS_EMPTY:
                tvNoBookmarks.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                pd.dismiss();
                Toast.makeText(this, R.string.no_bookmark, Toast.LENGTH_SHORT).show();
                break;

            case Constants.NO_RESPONSE:
                pd.dismiss();
                Toast.makeText(this, ""+event.getValue(), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void setData() {
        tvNoBookmarks.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        dataList.addAll(BookmarkManager.dataList);
        bookmarkAdapter.notifyDataSetChanged();
    }
}
