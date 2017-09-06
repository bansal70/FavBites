package com.favbites.view.fragments;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.favbites.R;
import com.favbites.controller.ModelManager;
import com.favbites.controller.UserPostManager;
import com.favbites.model.Constants;
import com.favbites.model.Event;
import com.favbites.model.FBPreferences;
import com.favbites.model.Operations;
import com.favbites.model.beans.UserPostsData;
import com.favbites.view.adapters.FragmentPostsAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class PostsFragment extends BaseFragment {

    RecyclerView recyclerPosts;
    FragmentPostsAdapter fragmentPostsAdapter;
    private List<UserPostsData.Data> postsList;
    boolean isLoaded = true;
    ProgressBar progressBar, progressPosts;
    RecyclerView.OnScrollListener onScrollListener;
    int page = 1;
    String to_user_id;

    public PostsFragment() {

    }

    public static PostsFragment newInstance() {
        Bundle args = new Bundle();
        PostsFragment fragment = new PostsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_posts, container, false);
        initViews(view);
        return view;
    }

    public void initViews(View view) {
        progressBar = view.findViewById(R.id.progressBar);
        progressPosts = view.findViewById(R.id.progressPosts);
        progressBar.setVisibility(View.VISIBLE);

        to_user_id = FBPreferences.readString(getActivity(), "to_user_id");
        ModelManager.getInstance().getUserPostManager().getUserPosts(
                Operations.userPostsParams(to_user_id, page));

        postsList = new ArrayList<>();
        recyclerPosts = view.findViewById(R.id.recyclerPosts);
        recyclerPosts.setHasFixedSize(true);
        recyclerPosts.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerPosts.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));

        fragmentPostsAdapter = new FragmentPostsAdapter(getActivity(), postsList);
        recyclerPosts.setAdapter(fragmentPostsAdapter);
        loadMore();
    }

    @Override
    public void onStart() {
        super.onStart();

        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(Event event) {
        switch (event.getKey()) {
            case Constants.USER_POSTS_SUCCESS:
                progressBar.setVisibility(View.GONE);
                progressPosts.setVisibility(View.GONE);
                recyclerPosts.setVisibility(View.VISIBLE);
                postsList.addAll(UserPostManager.postsList);
                fragmentPostsAdapter.notifyDataSetChanged();
                break;

            case Constants.USER_POSTS_EMPTY:
                progressBar.setVisibility(View.GONE);
                progressPosts.setVisibility(View.GONE);
                recyclerPosts.removeOnScrollListener(onScrollListener);
                if (postsList.size() != 0)
                    Toast.makeText(getActivity(), "No more data", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getActivity(), "Sorry, there are no posts.", Toast.LENGTH_SHORT).show();
                break;

            case Constants.NO_RESPONSE:
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), ""+event.getValue(), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void loadMore() {
        onScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // Load more if we have reach the end to the recyclerView
                if (!recyclerPosts.canScrollVertically(View.FOCUS_DOWN)) {
                    page++;
                    progressPosts.setVisibility(View.VISIBLE);
                    ModelManager.getInstance().getUserPostManager().getUserPosts(
                            Operations.userPostsParams(to_user_id, page));
                } else
                    progressPosts.setVisibility(View.GONE);
            }
        };

        recyclerPosts.addOnScrollListener(onScrollListener);
    }
}