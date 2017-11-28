package co.fav.bites.views;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import co.fav.bites.R;
import co.fav.bites.controller.RestaurantDetailsManager;
import co.fav.bites.models.beans.RestaurantDetailsData;
import co.fav.bites.views.adapters.PostsAdapter;

public class PostsActivity extends BaseActivity implements View.OnClickListener{

    RecyclerView recyclerPhotos;
    PostsAdapter postsAdapter;
    List<RestaurantDetailsData.Comment> postsList;
    ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);

        initViews();
    }

    public void initViews() {
        postsList = new ArrayList<>();
        recyclerPhotos = (RecyclerView) findViewById(R.id.recyclerPhotos);
        recyclerPhotos.setHasFixedSize(true);
        recyclerPhotos.setLayoutManager(new GridLayoutManager(this, 3));

        DividerItemDecoration horizontal = new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL);
        DividerItemDecoration vertical = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);

        horizontal.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider));
        vertical.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider));

        recyclerPhotos.addItemDecoration(horizontal);
        recyclerPhotos.addItemDecoration(vertical);

        RestaurantDetailsData.Data data = RestaurantDetailsManager.data;
        postsList.addAll(data.comment);

        postsAdapter = new PostsAdapter(this, postsList, "Posts");
        recyclerPhotos.setAdapter(postsAdapter);

        imgBack = (ImageView) findViewById(R.id.imgBack);
        imgBack.setOnClickListener(this);
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
