package co.fav.bites.views;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import co.fav.bites.R;
import co.fav.bites.controller.ModelManager;
import co.fav.bites.controller.RestaurantDetailsManager;
import co.fav.bites.models.Constants;
import co.fav.bites.models.Event;
import co.fav.bites.models.FBPreferences;
import co.fav.bites.models.Operations;
import co.fav.bites.models.Utils;
import co.fav.bites.models.beans.RestaurantDetailsData;
import co.fav.bites.views.adapters.PostsAdapter;
import co.fav.bites.views.adapters.RestaurantDetailAdapter;

import static android.view.View.GONE;

public class RestaurantDetailActivity extends BaseActivity implements View.OnClickListener,
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, View.OnTouchListener {

    private Context context = this;
    private static final int PERMISSION_REQUEST_CODE = 10001;
    GoogleApiClient mGoogleApiClient;

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
    Dialog pd;
    String bookmark;
    GoogleMap googleMap;
    double latitude, longitude;
    ProgressBar progressBar;
    boolean isChecked = false;
    ImageView imgTransparent;
    private ScrollView scrollView;
    CardView imgDirections;
    double lat, lng;
    LocationRequest mLocationRequest;
    LocationCallback mLocationCallback;
    FusedLocationProviderClient mFusedLocationClient;
    LocationManager manager;

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
        EventBus.getDefault().register(this);

        progressBar = findViewById(R.id.progressBar);
        pd = Utils.showDialog(this);
        user_id = FBPreferences.readString(this, "user_id");

        pd.show();
        lat = FBPreferences.readDouble(this, "latitude");
        lng = FBPreferences.readDouble(this, "longitude");

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
        imgTransparent = findViewById(R.id.imgTransparent);
        imgDirections = findViewById(R.id.imgDirections);

        imgBack.setOnClickListener(this);
        tvShowMore.setOnClickListener(this);
        imgBookmark.setOnClickListener(this);
        tvUploadPhoto.setOnClickListener(this);
        tvCall.setOnClickListener(this);
        tvViewMore.setOnClickListener(this);
        tvCheckIn.setOnClickListener(this);
        imgTransparent.setOnTouchListener(this);
        imgDirections.setOnClickListener(this);

        ModelManager.getInstance().getRestaurantDetailsManager()
                .getRestaurantDetails(this, Operations.getRestaurantDetailsParams(restaurant_id, user_id));

        initData();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    lat = location.getLatitude();
                    lng = location.getLongitude();
                }
            }
        };

        currentLocation();
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

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        /*if (Utils.isPhotoUploaded) {
            //subItemList.clear();

            postsList.clear();
            ModelManager.getInstance().getRestaurantDetailsManager()
                    .getRestaurantDetails(this, Operations.getRestaurantDetailsParams(restaurant_id, user_id));
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            tvShowMore.setVisibility(View.GONE);
            Utils.isReviewed = false;
            Utils.isPhotoUploaded = false;
        }*/
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
                startActivityForResult(new Intent(this, UploadPhotoActivity.class)
                        .putExtra("restaurant_id", restaurant_id), Constants.PHOTO_REQUEST_CODE);
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
                if (user_id.isEmpty()) {
                    Toast.makeText(context, "Please login to check in the restaurant", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (isOpen.equals("0")) {
                    Toast.makeText(context, "Sorry, restaurant is closed now.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (isChecked) {
                    Toast.makeText(context, "You have already checked in the restaurant", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!mGoogleApiClient.isConnected())
                    mGoogleApiClient.connect();

                enableLoc();
                break;

            case R.id.imgDirections:
                Intent i = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(
                        "http://maps.google.com/maps?saddr="+lat+ ", "+ lng+
                                "&daddr="+latitude + ", "+longitude));
                startActivity(i);
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
                pd.dismiss();
                subItemList.clear();
                postsList.clear();
                scrollView.setVisibility(View.VISIBLE);

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

            case Constants.NO_INTERNET:
                pd.dismiss();
                Toast.makeText(context, ""+event.getValue(), Toast.LENGTH_SHORT).show();
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
            case co.fav.bites.controller.LocationManager.REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        getCurrentLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                }
                break;

            case Constants.PHOTO_REQUEST_CODE:
                if (data != null && Utils.isPhotoUploaded) {
                    tvNoPosts.setVisibility(View.GONE);
                    recyclerPosts.setVisibility(View.VISIBLE);
                    RestaurantDetailsData.Comment comment = new RestaurantDetailsData.Comment(data.getStringExtra("file_path"));
                    postsList.add(comment);
                    postsAdapter.notifyDataSetChanged();
                    Utils.isPhotoUploaded = false;
                }
                break;

            case Constants.MENU_REQUEST_CODE:
                if (Utils.isReviewed && subItemList.size() != 0) {
                    if (data != null && !data.getStringExtra("dish_key").isEmpty()) {
                        float item_rating = 0.0f;
                        List<String> ratingList = new ArrayList<>();
                        RestaurantDetailsData.Data restaurantData = RestaurantDetailsManager.data;

                        List<RestaurantDetailsData.Subitem> subItemsList = restaurantData.subitem;

                        for (RestaurantDetailsData.Subitem subItem : subItemsList) {

                            if (subItem.key.equals(data.getStringExtra("dish_key"))) {

                                subItem.setRating(data.getStringExtra("rating"));
                                subItem.setReviewCount(data.getIntExtra("reviews_count", 0));

                                for (int i = 0; i < subItemsList.size(); i++) {
                                    if (!subItemsList.get(i).rating.isEmpty()) {
                                        float rating = Float.parseFloat(subItemsList.get(i).rating);
                                        if (rating > 0) {
                                            item_rating += rating;
                                            ratingList.add(String.valueOf(item_rating));
                                        }
                                    }
                                }

                                float avg = item_rating / ratingList.size();
                                rbRatings.setRating(avg);

                                Collections.sort(subItemList, (item1, item2) -> {
                                    float menu1 = 0.0f, menu2 = 0.0f;
                                    if (!item1.getRating().isEmpty())
                                        menu1 = Float.parseFloat(item1.getRating());
                                    if (!item2.getRating().isEmpty())
                                        menu2 = Float.parseFloat(item2.getRating());
                                    return menu2 < menu1 ? -1 : menu1 == menu2 ? 0 : 1;
                                });

                                restaurantDetailAdapter.notifyDataSetChanged();
                                recyclerView.scrollToPosition(0);
                            }
                        }
                    }
                }

                break;
        }
    }

    public void getCurrentLocation() {
        pd.show();
        ModelManager.getInstance().getLocationManager()
                .startLocationUpdates(this, mGoogleApiClient);
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (pd.isShowing()) {
                pd.dismiss();
                Toast.makeText(context, "Unable to check you in this time. Please try again.", Toast.LENGTH_SHORT).show();
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

    public void currentLocation() {
        if (manager != null && !manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                lat = location.getLatitude();
                lng = location.getLongitude();

                mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                        mLocationCallback, null /* Looper */);

            } else {
                mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                        mLocationCallback, null /* Looper */);
            }
        });
    }


    private void ratingUpdate() {
        String json = new Gson().toJson(subItemList);
        Intent i = new Intent();
      /*  i.putExtra("restaurant_id", restaurant_id);
        i.putExtra("dish_key", dish_key);
        i.putExtra("rating", menu_ratings);
        i.putExtra("reviews_count", reviewCount); */
        if (json != null && !json.isEmpty())
            i.putExtra("subItems_list", Parcels.wrap(subItemList));
        i.putExtra("restaurant_id", restaurant_id);

        setResult(RESULT_OK, i);
        finish();
    }

    @Override
    public void onBackPressed() {
        ratingUpdate();
    }

}
