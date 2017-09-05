package com.favbites.view.adapters;

/*
 * Created by rishav on 8/31/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.favbites.R;
import com.favbites.model.beans.RestaurantDetailsData;
import com.favbites.view.PostsViewActivity;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder>{
    private Context context;
    private List<RestaurantDetailsData.Comment> postsList;
    private String state;

    public PostsAdapter(Context context, List<RestaurantDetailsData.Comment> postsList, String state) {
        this.context = context;
        this.postsList = postsList;
        this.state = state;
    }

    @Override
    public PostsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (state.equals("Details"))
            view = LayoutInflater.from(context).inflate(R.layout.view_user_posts, parent, false);
        else
            view = LayoutInflater.from(context).inflate(R.layout.view_photos, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PostsAdapter.ViewHolder holder, int position) {
        RestaurantDetailsData.Comment comment = postsList.get(position);
        Glide.with(context)
                .load(comment.image)
                .crossFade()
                .into(holder.imgPosts);
    }

    @Override
    public int getItemCount() {
        if (state.equals("Details"))
            return postsList.size() > 4 ? 4 : postsList.size();
        else
            return postsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imgPosts;
        public ViewHolder(View itemView) {
            super(itemView);
            imgPosts = itemView.findViewById(R.id.imgPosts);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            context.startActivity(new Intent(context, PostsViewActivity.class)
            .putExtra("position", getAdapterPosition()));
        }
    }
}