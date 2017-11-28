package co.fav.bites.views.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import co.fav.bites.R;
import co.fav.bites.controller.ModelManager;
import co.fav.bites.controller.UserReviewManager;
import co.fav.bites.models.Constants;
import co.fav.bites.models.Event;
import co.fav.bites.models.FBPreferences;
import co.fav.bites.models.Operations;
import co.fav.bites.models.Utils;
import co.fav.bites.models.beans.UserReviewsData;
import co.fav.bites.views.adapters.FragmentReviewsAdapter;

public class ReviewsFragment extends BaseFragment {

    RecyclerView recyclerReviews;
    FragmentReviewsAdapter fragmentReviewsAdapter;
    private List<UserReviewsData.Data> reviewsList;
    boolean isLoaded = true;
    ProgressBar progressBar, progressReviews;
    RecyclerView.OnScrollListener onScrollListener;
    int page = 1;
    String to_user_id;
    Dialog dialog;

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
        dialog = Utils.showDialog(getActivity());
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

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(Event event) {
        dialog.dismiss();
        EventBus.getDefault().removeStickyEvent(Event.class);
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
                    Toast.makeText(getActivity(), "No more reviews", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getActivity(), "Sorry, there is no data.", Toast.LENGTH_SHORT).show();
                break;

            case Constants.NO_RESPONSE:
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), ""+event.getValue(), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    /*public void loadMore() {
        onScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // Load more if we have reach the end to the recyclerView
                if (!ViewCompat.canScrollVertically(recyclerView, 1)) {
                    page++;
                    progressReviews.setVisibility(View.VISIBLE);
                    ModelManager.getInstance().getUserReviewManager().getUserReviews(
                            Operations.userReviewsParams(to_user_id, page));
                } else
                    progressReviews.setVisibility(View.GONE);
            }
        };

        recyclerReviews.addOnScrollListener(onScrollListener);
    }*/

    public void loadMore() {
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerReviews
                .getLayoutManager();

        onScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView,
                                   int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(linearLayoutManager.findLastCompletelyVisibleItemPosition()== reviewsList.size()-1)
                    if (isAtBottom(recyclerReviews)) {
                        if (UserReviewManager.reviewsList.size() > 1) {
                            dialog.show();
                            UserReviewManager.reviewsList.clear();
                            ModelManager.getInstance().getUserReviewManager().getUserReviews(
                                    Operations.userReviewsParams(to_user_id, ++page));
                        }
                    }
            }
        };

        recyclerReviews.addOnScrollListener(onScrollListener);
    }

    public static boolean isAtBottom(RecyclerView recyclerView) {
        return !ViewCompat.canScrollVertically(recyclerView, 1);

    }

}
