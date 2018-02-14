package co.fav.bites.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import co.fav.bites.R;
import co.fav.bites.controller.ModelManager;
import co.fav.bites.controller.RestaurantDetailsManager;
import co.fav.bites.models.Constants;
import co.fav.bites.models.Event;
import co.fav.bites.models.FBPreferences;
import co.fav.bites.models.LocationFetcher;
import co.fav.bites.models.MySupportMapFragment;
import co.fav.bites.models.Operations;
import co.fav.bites.models.Utils;
import co.fav.bites.models.beans.RestaurantData;
import co.fav.bites.models.beans.RestaurantDetailsData;
import co.fav.bites.views.activities.AppBaseActivity;
import co.fav.bites.views.adapters.PostsAdapter;
import co.fav.bites.views.adapters.RestaurantDetailAdapter;

import static android.view.View.GONE;

public class RestaurantDetailActivity extends AppBaseActivity implements View.OnClickListener,
        OnMapReadyCallback, MySupportMapFragment.OnTouchListener{

    private Context context = this;
    private static final int PERMISSION_REQUEST_CODE = 10001;

    ImageView imgBack, imgRestaurant, imgBookmark;
    TextView tvRestaurant, tvAddress, tvPhone, tvOpen;
    RatingBar rbRatings;
    String isOpen;
    String restaurant_id, user_id, bookmark_status, restaurant_name, restaurant_phone;
    TextView tvShowMore, tvUploadPhoto, tvCall, tvCheckIn, tvNoPosts, tvNoMenus, tvViewMore;

    private ScrollView scrollView;
    CardView imgDirections;
    GoogleMap googleMap;

    RecyclerView recyclerView, recyclerPosts;
    private RestaurantDetailAdapter restaurantDetailAdapter;
    private PostsAdapter postsAdapter;
    private List<RestaurantData.Subitem> subItemList;
    private List<RestaurantDetailsData.Comment> postsList;
    String bookmark;
    double latitude, longitude;
    ProgressBar progressBar;
    boolean isChecked = false, isReviewed = false;
    double lat, lng;
    String locationAccess = "";
    private LocationFetcher mLocationFetch = new LocationFetcher();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);

        mLocationFetch.setOnLocationFetchListener(mLocation -> {
            if (mLocation == null) {
                return;
            }

            lat = mLocation.getLatitude();
            lng = mLocation.getLongitude();

            if (locationAccess.equalsIgnoreCase("directions")) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(
                        "http://maps.google.com/maps?saddr=" + lat + ", " + lng +
                                "&daddr=" + latitude + ", " + longitude));
                startActivity(i);
            } else if (locationAccess.equalsIgnoreCase("check_in")) {
                setCheckIn();
            }
        });

        initViews();
    }

    public void initViews() {
        EventBus.getDefault().register(this);

        progressBar = findViewById(R.id.progressBar);
        user_id = FBPreferences.readString(this, "user_id");

        showDialog();

        restaurant_id = getIntent().getStringExtra("restaurant_id");

        subItemList = new ArrayList<>();
        postsList = new ArrayList<>();
        imgBack = findViewById(R.id.imgBack);
        imgRestaurant = findViewById(R.id.imgRestaurant);
        imgBookmark = findViewById(R.id.imgBookmark);

        rbRatings = findViewById(R.id.rbRatings);
        tvRestaurant = findViewById(R.id.tvRestaurant);
        tvAddress = findViewById(R.id.tvAddress);
        tvPhone = findViewById(R.id.tvPhone);
        tvOpen = findViewById(R.id.tvOpen);
        tvShowMore = findViewById(R.id.tvShowMore);
        tvUploadPhoto = findViewById(R.id.tvUploadPhoto);
        tvCall = findViewById(R.id.tvCall);
        tvCheckIn = findViewById(R.id.tvCheckIn);
        tvNoPosts = findViewById(R.id.tvNoPosts);
        tvNoMenus = findViewById(R.id.tvNoMenu);
        tvViewMore = findViewById(R.id.tvViewMore);
        scrollView = findViewById(R.id.scrollView);
        imgDirections = findViewById(R.id.imgDirections);

        imgBack.setOnClickListener(this);
        tvShowMore.setOnClickListener(this);
        imgBookmark.setOnClickListener(this);
        tvUploadPhoto.setOnClickListener(this);
        tvCall.setOnClickListener(this);
        tvViewMore.setOnClickListener(this);
        tvCheckIn.setOnClickListener(this);
        imgDirections.setOnClickListener(this);

        ModelManager.getInstance().getRestaurantDetailsManager()
                .getRestaurantDetails(this, Operations.getRestaurantDetailsParams(restaurant_id, user_id));

        initData();
    }

    public void initData() {
        recyclerView = findViewById(R.id.recyclerMenus);
        //recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        restaurantDetailAdapter = new RestaurantDetailAdapter(this, subItemList);
        recyclerView.setAdapter(restaurantDetailAdapter);

        recyclerPosts = findViewById(R.id.recyclerPosts);
        recyclerPosts.setLayoutManager(new GridLayoutManager(this, 4));
        postsAdapter = new PostsAdapter(this, postsList, "Details");
        recyclerPosts.setAdapter(postsAdapter);
    }

    public void setData() {
        RestaurantDetailsData.Data data = RestaurantDetailsManager.data;
        RestaurantData.Restaurant restaurant = data.restaurant;

        restaurant_name = restaurant.name;
        String streetAddress = "Address: " + restaurant.streetAddress + ", "
                + restaurant.city + ", "
                + restaurant.state + " "
                + restaurant.zip;
        String logoUrl = restaurant.logoUrl;
        restaurant_phone = restaurant.phone;
        String phone = "Phone: " + restaurant.phone;
        isOpen = restaurant.isOpen;
        restaurant_id = restaurant.id;
     //   FBPreferences.putString(this, "restaurant_id", restaurant_id);
        float rating = Float.parseFloat(restaurant.item_rating);
        rbRatings.setRating(rating);

        Glide.with(this)
                .load(logoUrl)
                .into(imgRestaurant);
        tvRestaurant.setText(restaurant_name);
        tvAddress.setText(streetAddress);
        tvPhone.setText(phone);

        if (isOpen.equals("0")) {
            tvOpen.setText(R.string.closed_now);
            tvOpen.setTextColor(ContextCompat.getColor(context, R.color.red_color));
        } else {
            tvOpen.setText(R.string.open_now);
            tvOpen.setTextColor(ContextCompat.getColor(context, R.color.green_color));
        }

        bookmark = restaurant.bookmark;
        if (bookmark.isEmpty() || bookmark.equals("0")) {
            bookmark_status = "0";
            imgBookmark.setImageResource(R.drawable.unbookmark);
        }
        else {
            bookmark_status = "1";
            imgBookmark.setImageResource(R.drawable.bookmark);
        }

        latitude = Double.parseDouble(restaurant.latitude);
        longitude = Double.parseDouble(restaurant.longitude);

        MySupportMapFragment mapFragment = (MySupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapFragment.setListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBack:
                ratingUpdate();
                break;

            case R.id.tvShowMore:
                subItemList.clear();
                RestaurantDetailsData.Data data = RestaurantDetailsManager.data;
                subItemList.addAll(data.subitem);
                restaurantDetailAdapter.notifyDataSetChanged();

                tvShowMore.setVisibility(GONE);
                break;

            case R.id.imgBookmark:
                if (user_id.isEmpty() || user_id == null) {
                    showToast(getString(R.string.prompt_login_for_bookmark));
                    return;
                }
                showDialog();
                if (bookmark_status.equals("0") || bookmark_status.isEmpty())
                    ModelManager.getInstance().getBookmarkManager().bookmarkRestaurant(
                            Operations.bookmarkRestaurant(user_id, restaurant_id, "1"));
                else
                    ModelManager.getInstance().getBookmarkManager().bookmarkRestaurant(
                            Operations.bookmarkRestaurant(user_id, restaurant_id, "0"));

                break;

            case R.id.tvUploadPhoto:
                if (user_id.isEmpty()) {
                    showToast(getString(R.string.error_guest_post));
                    return;
                }
                startActivityForResult(new Intent(this, UploadPhotoActivity.class)
                        .putExtra("restaurant_id", restaurant_id), Constants.PHOTO_REQUEST_CODE);
                break;

            case R.id.tvCall:
                if (restaurant_phone.isEmpty()) {
                    showToast(getString(R.string.error_contact_information_empty));
                    return;
                }
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + restaurant_phone));
                startActivity(intent);
                break;

            case R.id.tvViewMore:
                startActivity(new Intent(this, PostsActivity.class));
                break;

            case R.id.tvCheckIn:
                if (user_id.isEmpty()) {
                    showToast(getString(R.string.error_guest_check_in));
                    return;
                }
                if (isOpen.equals("0")) {
                    showToast(getString(R.string.error_restaurant_closed));
                    return;
                }
                if (isChecked) {
                    showToast(getString(R.string.error_already_checked_in));
                    return;
                }
                locationAccess = "check_in";
                mLocationFetch.requestLocationUpdates(this);
                break;

            case R.id.imgDirections:
                if (!Utils.isGPS(this)) {
                    mLocationFetch.getLocation(this);
                    return;
                }

                locationAccess = "directions";
                mLocationFetch.requestLocationUpdates(this);

                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        EventBus.getDefault().unregister(this);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(Event event) {
        EventBus.getDefault().removeStickyEvent(Event.class);
        switch (event.getKey()) {
            case Constants.RESTAURANT_DETAILS_SUCCESS:
                dismissDialog();
                subItemList.clear();
                postsList.clear();
                scrollView.setVisibility(View.VISIBLE);

                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

                RestaurantDetailsData.Data data = RestaurantDetailsManager.data;
                // subItemList.addAll(data.subitem);
                for (int i=0; i<data.subitem.size(); i++) {
                    if (i < 6)
                        subItemList.add(data.subitem.get(i));
                    else
                        break;
                }

                restaurantDetailAdapter.notifyDataSetChanged();
                postsList.addAll(data.comment);
                postsAdapter.notifyDataSetChanged();

                if (data.subitem.size() < 6)
                    tvShowMore.setVisibility(View.GONE);

                if (subItemList.size() == 0) {
                    tvNoMenus.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                }

                if (postsList.size() != 0) {
                    tvNoPosts.setVisibility(View.GONE);
                    recyclerPosts.setVisibility(View.VISIBLE);
                }
                else {
                    tvNoPosts.setVisibility(View.VISIBLE);
                    recyclerPosts.setVisibility(View.GONE);
                }

                if (postsList.size() > 4)
                    tvViewMore.setVisibility(View.VISIBLE);

                setData();

                break;

            case Constants.BOOKMARK_ADDED:
                dismissDialog();
                if (bookmark_status.equals("0")) {
                    bookmark_status = "1";
                    showToast(getString(R.string.success_bookmarked));
                    imgBookmark.setImageResource(R.drawable.bookmark);
                }
                else {
                    bookmark_status = "0";
                    showToast(getString(R.string.success_bookmark_removed));
                    imgBookmark.setImageResource(R.drawable.unbookmark);
                }
                break;

            case Constants.LOCATION_EMPTY:
                dismissDialog();
                break;

            case Constants.CHECK_IN_SUCCESS:
                dismissDialog();
                isChecked = true;
                showToast(getString(R.string.success_checked_in) + " " +restaurant_name);

                tvCheckIn.setText(R.string.checked_in);
                tvCheckIn.setTextColor(ContextCompat.getColor(context, R.color.colorBlack));
                tvCheckIn.setBackgroundResource(R.color.colorPrimary);
                tvCheckIn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.tick_black, 0);
                break;

            case Constants.CHECK_IN_FAILED:
                dismissDialog();
                showToast(getString(R.string.error_check_in_failed));
                break;

            case Constants.NO_RESPONSE:
                dismissDialog();
                showToast(event.getValue());
                break;

            case Constants.NO_INTERNET:
                dismissDialog();
                showToast(event.getValue());
                finish();
                break;

        }
    }

    @Override
    public void onMapReady(GoogleMap mMap) {
        googleMap = mMap;

        LatLng latLng = new LatLng(latitude, longitude);
        googleMap.addMarker(new MarkerOptions().position(latLng).title(restaurant_name));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }

    public void setCheckIn() {
        Location restaurantLoc =new Location("A");
        restaurantLoc.setLatitude(latitude);
        restaurantLoc.setLongitude(longitude);

        Location userLoc = new Location("B");
        userLoc.setLatitude(lat);
        userLoc.setLongitude(lng);

        double distance=restaurantLoc.distanceTo(userLoc);
        if (distance <= Constants.CHECK_IN_DISTANCE) {
            showDialog();

            ModelManager.getInstance().getCheckInManager().checkInUser(Operations.checkInParams(user_id, restaurant_id));
        } else {
            dismissDialog();
            Toast.makeText(context, "You must be in the restaurant to check in", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE && Utils.hasAllPermissionsGranted(this, grantResults)) {
            mLocationFetch.getLocation(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case co.fav.bites.controller.LocationManager.REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        if (locationAccess.equals("check_in"))
                            mLocationFetch.requestLocationUpdates(this);
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                }
                break;

            case Constants.PHOTO_REQUEST_CODE:
                if (data != null) {
                    showDialog();
                    tvNoPosts.setVisibility(View.GONE);
                    ModelManager.getInstance().getRestaurantDetailsManager()
                            .getRestaurantDetails(this, Operations.getRestaurantDetailsParams(restaurant_id, user_id));
                }
                break;

            case Constants.MENU_REQUEST_CODE:
                if (data != null && !data.getStringExtra("restaurant_id").isEmpty()) {
                    isReviewed = true;
                    showDialog();
                    ModelManager.getInstance().getRestaurantDetailsManager().getRestaurantDetails(this,
                            Operations.getRestaurantDetailsParams(restaurant_id, user_id));
                }
                break;
        }
    }

    private void ratingUpdate() {
        Intent i = new Intent();

        if (isReviewed)
            i.putExtra("restaurant_id", restaurant_id);

        setResult(RESULT_OK, i);
        finish();
    }

    @Override
    public void onBackPressed() {
        ratingUpdate();
    }

    @Override
    public void onTouch() {
        scrollView.requestDisallowInterceptTouchEvent(true);
    }
}
