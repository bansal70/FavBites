package com.favbites.view;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.favbites.R;
import com.favbites.controller.ModelManager;
import com.favbites.controller.RestaurantDetailsManager;
import com.favbites.model.Constants;
import com.favbites.model.Event;
import com.favbites.model.FBPreferences;
import com.favbites.model.Operations;
import com.favbites.model.Utils;
import com.favbites.model.beans.RestaurantDetailsData;
import com.favbites.view.adapters.PostsAdapter;
import com.favbites.view.adapters.RestaurantDetailAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

public class RestaurantDetailActivity extends BaseActivity implements View.OnClickListener,
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, View.OnTouchListener {

    private Context context = this;
    private static final int PERMISSION_REQUEST_CODE = 10001;
    GoogleApiClient mGoogleApiClient;
    LocationManager manager;

    ImageView imgBack, imgRestaurant, imgBookmark;
    TextView tvRestaurant, tvAddress, tvPhone, tvOpen;
    RatingBar rbRatings;
    String isOpen;
    String restaurant_id, user_id, bookmark_status, restaurant_name, restaurant_phone;
    TextView tvShowMore, tvUploadPhoto, tvCall, tvCheckIn, tvNoPosts, tvNoMenus, tvViewMore;

    RecyclerView recyclerView, recyclerPosts;
    private RestaurantDetailAdapter restaurantDetailAdapter;
    private PostsAdapter postsAdapter;
    private List<RestaurantDetailsData.Subitem> subItemList;
    private List<RestaurantDetailsData.Comment> postsList;
    KProgressHUD pd;
    String bookmark;
    GoogleMap googleMap;
    double latitude, longitude;
    ProgressBar progressBar;
    boolean isChecked = false;
    ImageView imgTransparent;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
        }

        if (!mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();

        initViews();
    }

    public void initViews() {
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        pd = Utils.showDialog(this);
        user_id = FBPreferences.readString(this, "user_id");
        pd.show();

        restaurant_id = getIntent().getStringExtra("restaurant_id");

        subItemList = new ArrayList<>();
        postsList = new ArrayList<>();
        imgBack = (ImageView) findViewById(R.id.imgBack);
        imgRestaurant = (ImageView) findViewById(R.id.imgRestaurant);
        imgBookmark = (ImageView) findViewById(R.id.imgBookmark);

        rbRatings = (RatingBar) findViewById(R.id.rbRatings);
        tvRestaurant = (TextView) findViewById(R.id.tvRestaurant);
        tvAddress = (TextView) findViewById(R.id.tvAddress);
        tvPhone = (TextView) findViewById(R.id.tvPhone);
        tvOpen = (TextView) findViewById(R.id.tvOpen);
        tvShowMore = (TextView) findViewById(R.id.tvShowMore);
        tvUploadPhoto = (TextView) findViewById(R.id.tvUploadPhoto);
        tvCall = (TextView) findViewById(R.id.tvCall);
        tvCheckIn = (TextView) findViewById(R.id.tvCheckIn);
        tvNoPosts = (TextView) findViewById(R.id.tvNoPosts);
        tvNoMenus = (TextView) findViewById(R.id.tvNoMenu);
        tvViewMore = (TextView) findViewById(R.id.tvViewMore);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        imgTransparent = (ImageView) findViewById(R.id.imgTransparent);

        imgBack.setOnClickListener(this);
        tvShowMore.setOnClickListener(this);
        imgBookmark.setOnClickListener(this);
        tvUploadPhoto.setOnClickListener(this);
        tvCall.setOnClickListener(this);
        tvViewMore.setOnClickListener(this);
        tvCheckIn.setOnClickListener(this);
        imgTransparent.setOnTouchListener(this);

        ModelManager.getInstance().getRestaurantDetailsManager()
                .getRestaurantDetails(Operations.getRestaurantDetailsParams(restaurant_id, user_id));

        initData();
    }

    public void initData() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerMenus);
        //recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        restaurantDetailAdapter = new RestaurantDetailAdapter(this, subItemList);
        recyclerView.setAdapter(restaurantDetailAdapter);

        recyclerPosts = (RecyclerView) findViewById(R.id.recyclerPosts);
        recyclerPosts.setLayoutManager(new GridLayoutManager(this, 4));
        postsAdapter = new PostsAdapter(this, postsList, "Details");
        recyclerPosts.setAdapter(postsAdapter);
    }

    public void setData() {
        RestaurantDetailsData.Data data = RestaurantDetailsManager.data;
        RestaurantDetailsData.Restaurant restaurant = data.restaurant;

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
        FBPreferences.putString(this, "restaurant_id", restaurant_id);
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

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if (Utils.isPhotoUploaded) {
            postsList.clear();
            ModelManager.getInstance().getRestaurantDetailsManager()
                    .getRestaurantDetails(Operations.getRestaurantDetailsParams(restaurant_id, user_id));
            Utils.isPhotoUploaded = false;
        }
        if (Utils.isReviewed) {
            subItemList.clear();
            ModelManager.getInstance().getRestaurantDetailsManager()
                    .getRestaurantDetails(Operations.getRestaurantDetailsParams(restaurant_id, user_id));
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            tvShowMore.setVisibility(View.GONE);
            Utils.isReviewed = false;
        }
        /*if (Utils.i) {
            totalItems = 6  ;
            data.subitem.clear();
            subItemList.clear();
            postsList.clear();
            initData();

        }*/
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBack:
                finish();
                break;

            case R.id.tvShowMore:
                subItemList.clear();
                RestaurantDetailsData.Data data = RestaurantDetailsManager.data;
                subItemList.addAll(data.subitem);
                restaurantDetailAdapter.notifyDataSetChanged();

               /* totalItems = subItemList.size();
                restaurantDetailAdapter = new RestaurantDetailAdapter(this, subItemList, subItemList.size());
                recyclerView.setAdapter(restaurantDetailAdapter);*/
                // recyclerView.scrollToPosition(6);
                tvShowMore.setVisibility(GONE);
                break;

            case R.id.imgBookmark:
                if (user_id.isEmpty() || user_id == null) {
                    Toast.makeText(context, "Please login to bookmark any restaurant", Toast.LENGTH_SHORT).show();
                    return;
                }
                pd.show();
                if (bookmark_status.equals("0") || bookmark_status.isEmpty())
                    ModelManager.getInstance().getBookmarkManager().bookmarkRestaurant(
                            Operations.bookmarkRestaurant(user_id, restaurant_id, "1"));
                else
                    ModelManager.getInstance().getBookmarkManager().bookmarkRestaurant(
                            Operations.bookmarkRestaurant(user_id, restaurant_id, "0"));

                break;

            case R.id.tvUploadPhoto:
                if (user_id.isEmpty()) {
                    Toast.makeText(context, "Please login to post something...", Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivity(new Intent(this, UploadPhotoActivity.class));
                break;

            case R.id.tvCall:
                if (restaurant_phone.isEmpty()) {
                    Toast.makeText(context, "Sorry, this restaurant doesn't have any contact information", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+restaurant_phone));
                startActivity(intent);
                break;

            case R.id.tvViewMore:
                startActivity(new Intent(this, PostsActivity.class
                ));
                break;

            case R.id.tvCheckIn:
                if (isChecked) {
                    Toast.makeText(context, "You have already checked in the restaurant", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!mGoogleApiClient.isConnected())
                    mGoogleApiClient.connect();

                enableLoc();
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

            case Constants.RESTAURANT_DETAILS_SUCCESS:
                pd.dismiss();

                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

                setData();

                RestaurantDetailsData.Data data = RestaurantDetailsManager.data;
                // subItemList.addAll(data.subitem);
                for (int i=0; i<6; i++) {
                    subItemList.add(data.subitem.get(i));
                }
                restaurantDetailAdapter.notifyDataSetChanged();
                postsList.addAll(data.comment);
                postsAdapter.notifyDataSetChanged();

                if (data.subitem.size() > 6)
                    tvShowMore.setVisibility(View.VISIBLE);

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

                break;

            case Constants.BOOKMARK_ADDED:
                pd.dismiss();
                if (bookmark_status.equals("0")) {
                    bookmark_status = "1";
                    Toast.makeText(context, "Restaurant has been bookmarked successfully.", Toast.LENGTH_SHORT).show();
                    imgBookmark.setImageResource(R.drawable.bookmark);
                }
                else {
                    bookmark_status = "0";
                    Toast.makeText(context, "Restaurant has been removed from your bookmarks.", Toast.LENGTH_SHORT).show();
                    imgBookmark.setImageResource(R.drawable.unbookmark);
                }
                break;

            case Constants.LOCATION_SUCCESS:
                pd.dismiss();
                setCheckIn();
                break;

            case Constants.LOCATION_EMPTY:
                pd.dismiss();
                break;

            case Constants.CHECK_IN_SUCCESS:
                pd.dismiss();
                isChecked = true;
                Toast.makeText(context, "You have checked in the "+restaurant_name, Toast.LENGTH_SHORT).show();

                tvCheckIn.setText(R.string.checked_in);
                tvCheckIn.setTextColor(ContextCompat.getColor(context, R.color.colorBlack));
                tvCheckIn.setBackgroundResource(R.color.colorPrimary);
                tvCheckIn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.tick_black, 0);
                break;

            case Constants.CHECK_IN_FAILED:
                pd.dismiss();
                Toast.makeText(context, "Failed to check in. Please try again.", Toast.LENGTH_SHORT).show();
                break;

            case Constants.NO_RESPONSE:
                pd.dismiss();
                Toast.makeText(context, ""+event.getValue(), Toast.LENGTH_SHORT).show();
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
        double lat = FBPreferences.readDouble(this, "latitude");
        double lng = FBPreferences.readDouble(this, "longitude");
        Location restaurantLoc =new Location("A");
        restaurantLoc.setLatitude(latitude);
        restaurantLoc.setLongitude(longitude);

        Location userLoc = new Location("B");
        userLoc.setLatitude(lat);
        userLoc.setLongitude(lng);

        double distance=restaurantLoc.distanceTo(userLoc);
        if (distance <= Constants.CHECK_IN_DISTANCE) {
            if (!pd.isShowing())
                pd.show();

            ModelManager.getInstance().getCheckInManager().checkInUser(
                    Operations.checkInParams(user_id, restaurant_id));

        } else {
            if (pd.isShowing())
                pd.dismiss();
            Toast.makeText(context, "You must be in the restaurant to check in", Toast.LENGTH_SHORT).show();
        }

    }
    private void enableLoc() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
            } else {
                if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    getCurrentLocation();
                    return;
                }
                ModelManager.getInstance().getLocationManager()
                        .requestLocation(this, mGoogleApiClient);
            }
        } else {
            if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                getCurrentLocation();
                return;
            }
            ModelManager.getInstance().getLocationManager()
                    .requestLocation(this, mGoogleApiClient);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ModelManager.getInstance().getLocationManager()
                        .requestLocation(this, mGoogleApiClient);
            } else {
                Toast.makeText(this, "Please grant all the permissions.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please grant all the permissions.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case com.favbites.controller.LocationManager.REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        getCurrentLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                }
                break;
        }
    }

    public void getCurrentLocation() {
        pd.show();
        ModelManager.getInstance().getLocationManager()
                .startLocationUpdates(this, mGoogleApiClient);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (pd.isShowing()) {
                    pd.dismiss();
                    Toast.makeText(context, "Unable to check you in this time. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        }, 10000);
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // Disallow ScrollView to intercept touch events.
                scrollView.requestDisallowInterceptTouchEvent(true);
                // Disable touch on transparent view
                return false;

            case MotionEvent.ACTION_UP:
                // Allow ScrollView to intercept touch events.
                scrollView.requestDisallowInterceptTouchEvent(false);
                return true;

            case MotionEvent.ACTION_MOVE:
                scrollView.requestDisallowInterceptTouchEvent(true);
                return false;

            default:
                return true;
        }
    }
}
