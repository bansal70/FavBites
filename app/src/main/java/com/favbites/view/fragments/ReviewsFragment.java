package com.favbites.view.fragments;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.favbites.R;
import com.favbites.controller.ModelManager;
import com.favbites.controller.UserReviewManager;
import com.favbites.model.Constants;
import com.favbites.model.Event;
import com.favbites.model.FBPreferences;
import com.favbites.model.Operations;
import com.favbites.model.beans.UserReviewsData;
import com.favbites.view.adapters.FragmentReviewsAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class ReviewsFragment extends BaseFragment {

    RecyclerView recyclerReviews;
    FragmentReviewsAdapter fragmentReviewsAdapter;
    private List<UserReviewsData.Data> reviewsList;
    boolean isLoaded = true;
    ProgressBar progressBar, progressReviews;
    RecyclerView.OnScrollListener onScrollListener;
    int page = 1;
    String to_user_id;

    public ReviewsFragment() {
    }

    public static ReviewsFragment newInstance() {

        Bundle args = new Bundle();

        ReviewsFragment fragment = new ReviewsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser && isLoaded) {
            if (progressBar != null)
                progressBar.setVisibility(View.VISIBLE);

            to_user_id = FBPreferences.readString(getActivity(), "to_user_id");
            ModelManager.getInstance().getUserReviewManager().getUserReviews(
                    Operations.userReviewsParams(to_user_id, page));
            isLoaded = false;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_reviews, container, false);
        initViews(view);
        return view;
    }

    public void initViews(View view) {
        progressBar = view.findViewById(R.id.progressBar);
        progressReviews = view.findViewById(R.id.progressReviews);

        reviewsList = new ArrayList<>();
        recyclerReviews = view.findViewById(R.id.recyclerReviews);
        recyclerReviews.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerReviews.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        fragmentReviewsAdapter = new FragmentReviewsAdapter(getActivity(), reviewsList);
        recyclerReviews.setAdapter(fragmentReviewsAdapter);
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
            case Constants.USER_REVIEWS_SUCCESS:
                progressBar.setVisibility(View.GONE);
                progressReviews.setVisibility(View.GONE);
                recyclerReviews.setVisibility(View.VISIBLE);
                reviewsList.addAll(UserReviewManager.reviewsList);
                fragmentReviewsAdapter.notifyDataSetChanged();
                break;

            case Constants.USER_REVIEWS_EMPTY:
                progressBar.setVisibility(View.GONE);
                progressReviews.setVisibility(View.GONE);
                recyclerReviews.removeOnScrollListener(onScrollListener);
                if (reviewsList.size() != 0)
                    Toast.makeText(getActivity(), "No more data", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getActivity(), "Sorry, there is no data.", Toast.LENGTH_SHORT).show();
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
                if (!recyclerReviews.canScrollVertically(View.FOCUS_DOWN)) {
                    page++;
                    Log.e("kfndjfbdjbf", "knfbdon");
                    progressReviews.setVisibility(View.VISIBLE);
                    ModelManager.getInstance().getUserReviewManager().getUserReviews(
                            Operations.userReviewsParams(to_user_id, page));
                } else
                    progressReviews.setVisibility(View.GONE);
            }
        };

        recyclerReviews.addOnScrollListener(onScrollListener);
    }

}
