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
import co.fav.bites.controller.UserPostManager;
import co.fav.bites.models.Constants;
import co.fav.bites.models.Event;
import co.fav.bites.models.FBPreferences;
import co.fav.bites.models.Operations;
import co.fav.bites.models.Utils;
import co.fav.bites.models.beans.UserPostsData;
import co.fav.bites.views.adapters.FragmentPostsAdapter;

public class PostsFragment extends BaseFragment {

    RecyclerView recyclerPosts;
    FragmentPostsAdapter fragmentPostsAdapter;
    private List<UserPostsData.Data> postsList;
    boolean isLoaded = true;
    ProgressBar progressBar, progressPosts;
    RecyclerView.OnScrollListener onScrollListener;
    int page = 1;
    String to_user_id;
    Dialog dialog;

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
        dialog = Utils.showDialog(getActivity());
        progressBar = view.findViewById(R.id.progressBar);
        progressPosts = view.findViewById(R.id.progressPosts);
        progressBar.setVisibility(View.VISIBLE);

        to_user_id = FBPreferences.readString(getActivity(), "to_user_id");


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

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(Event event) {
        dialog.dismiss();
        EventBus.getDefault().removeStickyEvent(Event.class);
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
                    Toast.makeText(getActivity(), "No more posts", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getActivity(), "Sorry, there are no posts.", Toast.LENGTH_SHORT).show();
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
    }*/

    public void loadMore() {
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerPosts
                .getLayoutManager();

        onScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView,
                                   int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (linearLayoutManager.findLastCompletelyVisibleItemPosition() == postsList.size() - 1)
                    if (isAtBottom(recyclerPosts)) {
                        if (UserPostManager.postsList.size() > 1) {
                            dialog.show();
                            UserPostManager.postsList.clear();
                            ModelManager.getInstance().getUserPostManager().getUserPosts(
                                    Operations.userPostsParams(to_user_id, ++page));
                        }
                    }
            }
        };

        recyclerPosts.addOnScrollListener(onScrollListener);
    }

    public static boolean isAtBottom(RecyclerView recyclerView) {
        return !ViewCompat.canScrollVertically(recyclerView, 1);
    }

}