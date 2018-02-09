package co.fav.bites.views;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import co.fav.bites.R;
import co.fav.bites.controller.ModelManager;
import co.fav.bites.controller.RestaurantsManager;
import co.fav.bites.models.Constants;
import co.fav.bites.models.Event;
import co.fav.bites.models.FBPreferences;
import co.fav.bites.models.Operations;
import co.fav.bites.models.Utils;
import co.fav.bites.models.beans.RestaurantData;
import co.fav.bites.views.adapters.RestaurantsAdapter;

import static co.fav.bites.models.Utils.restaurantsList;

public class RestaurantsActivity extends BaseActivity implements View.OnTouchListener,
        View.OnClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final int PERMISSION_REQUEST_CODE = 10001;

    GoogleApiClient mGoogleApiClient;
    LocationManager manager;

    private Activity activity = this;
    Dialog pd;
    private int page = 1;
    RecyclerView recyclerView;
    RestaurantsAdapter restaurantsAdapter;
    EditText editSearch;
    // TextView tvResults;
    String search = "";
    boolean isSearch = false;
    // LinearLayout resultLayout;
    Dialog dialogLocation;
    TextView tvSubmit, tvAutoDetect, tvNoRestaurant;
    EditText editLocation;
    ImageView imgLocation, imgHome, imgProfilePic;
    DrawerLayout navigationDrawer;
    String user_id;
    TextView tvRestaurants, tvFavRestaurants, tvCheckInRestaurants;
    boolean isRes, isFav, isCheck;
    RecyclerView.OnScrollListener onScrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurants);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
        }

        if (!mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();

        checkLocation();
        initViews();
    }

    public void checkLocation() {
        pd = Utils.showDialog(this);
        manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        RestaurantsManager.datumList.clear();
        restaurantsList.clear();

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            initDialog();
        } else {
            search = FBPreferences.readString(this, "location");
            if (!search.isEmpty()) {
                ModelManager.getInstance().getRestaurantsManager()
                        .searchRestaurant(Operations.getSearchRestaurantParams(search, page));
                pd.show();
            } else {
                Toast.makeText(activity, "Unable to get your current location. " +
                        "Please select your location", Toast.LENGTH_SHORT).show();
                initDialog();
            }
        }
    }

    public void initViews() {
        // resultLayout = (LinearLayout) findViewById(R.id.resultLayout);
        tvRestaurants = findViewById(R.id.tvRestaurants);
        tvFavRestaurants = findViewById(R.id.tvFavRestaurants);
        tvCheckInRestaurants = findViewById(R.id.tvCheckInRestaurants);

        user_id = FBPreferences.readString(this, "user_id");
        navigationDrawer = findViewById(R.id.drawer_layout);
        imgHome = findViewById(R.id.imgHome);
        editSearch = findViewById(R.id.editSearch);
        editSearch.setOnTouchListener(this);

        if (!search.isEmpty())
            editSearch.setText(search);
        // tvResults = (TextView) findViewById(R.id.tvResults);
        tvNoRestaurant = findViewById(R.id.tvNoRestaurant);
        imgLocation = findViewById(R.id.imgLocation);
        imgProfilePic = findViewById(R.id.imgProfilePic);

        imgHome.setOnClickListener(this);
        imgLocation.setOnClickListener(this);

        recyclerView = findViewById(R.id.recyclerRestaurants);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));

        restaurantsAdapter = new RestaurantsAdapter(this, restaurantsList);
        recyclerView.setAdapter(restaurantsAdapter);

        tvRestaurants.setOnClickListener(this);
        tvFavRestaurants.setOnClickListener(this);
        tvCheckInRestaurants.setOnClickListener(this);

        isLoaded(true, false, false);
        loadMore();
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        final int DRAWABLE_RIGHT = 2;

        if (event.getAction() == MotionEvent.ACTION_UP) {

            if (event.getRawX() >= (editSearch.getRight() - editSearch.getCompoundDrawables()
                    [DRAWABLE_RIGHT].getBounds().width())) {
                search = editSearch.getText().toString().trim();
                if (search.isEmpty()) {
                    return true;
                }
                page = 1;
                isSearch = true;
                restaurantsList.clear();
                pd.show();
                if (isRes)
                    ModelManager.getInstance().getRestaurantsManager()
                            .searchRestaurant(Operations.getSearchRestaurantParams(search, page));
                else if (isFav)
                    ModelManager.getInstance().getRestaurantsManager()
                            .favRestaurants(Operations.getFavRestaurantsParams(search, page));
                else
                    ModelManager.getInstance().getRestaurantsManager()
                            .checkInRestaurants(Operations.getCheckInRestaurantsParams(search, page));

                return true;
            }
        }
        return false;
    }

    public void initDialog() {
        dialogLocation = Utils.createDialog(this, R.layout.dialog_location);
        tvSubmit = dialogLocation.findViewById(R.id.tvSubmit);
        tvAutoDetect = dialogLocation.findViewById(R.id.tvAutoDetect);
        editLocation = dialogLocation.findViewById(R.id.editLocation);

        tvSubmit.setOnClickListener(this);
        tvAutoDetect.setOnClickListener(this);
        dialogLocation.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvSubmit:
                search = editLocation.getText().toString().trim();
                if (search.isEmpty()) {
                    Toast.makeText(activity, "Please enter your location..", Toast.LENGTH_SHORT).show();
                    return;
                }
                search = editLocation.getText().toString().trim();
                pd.show();
                ModelManager.getInstance().getRestaurantsManager()
                        .searchRestaurant(Operations.getSearchRestaurantParams(search, 1));
                break;

            case R.id.tvAutoDetect:
                enableLoc();
                break;

            case R.id.imgLocation:
                enableLoc();
                break;

            case R.id.imgHome:
                navigationDrawer.openDrawer(GravityCompat.START);
                break;

            case R.id.tvAccount:
                navigationDrawer.closeDrawer(GravityCompat.START);
                startActivity(new Intent(this, ProfileActivity.class));
                break;

            case R.id.tvTerms:
                navigationDrawer.closeDrawer(GravityCompat.START);
                startActivity(new Intent(this, TermsConditionsActivity.class));
                break;

            case R.id.tvAbout:
                navigationDrawer.closeDrawer(GravityCompat.START);
                startActivity(new Intent(this, AboutActivity.class));
                break;

            case R.id.tvLogout:
                navigationDrawer.closeDrawer(GravityCompat.START);
                if (user_id.isEmpty()) {
                    startActivity(new Intent(this, LoginActivity.class));
                    return;
                }
                pd.show();
                ModelManager.getInstance().getLogoutManager().logoutUser(this,
                        Operations.logoutParams(user_id));
                break;

            case R.id.tvRestaurants:
                if (!isRes) {
                    pd.show();
                    page = 1;
                    ModelManager.getInstance().getRestaurantsManager()
                            .searchRestaurant(Operations.getSearchRestaurantParams(search, page));

                        restaurantsList.clear();
                        recyclerView.scrollToPosition(0);

                    isLoaded(true, false, false);
                }

                setBackground(R.color.colorPrimary, R.color.colorHome, R.color.colorHome);
                setTextColor(R.color.colorBlack, R.color.colorWhite, R.color.colorWhite);
                break;

            case R.id.tvFavRestaurants:
                if (!isFav) {
                    pd.show();
                    page = 1;

                    restaurantsList.clear();
                    recyclerView.scrollToPosition(0);

                    isLoaded(false, true, false);

                    ModelManager.getInstance().getRestaurantsManager()
                            .favRestaurants(Operations.getFavRestaurantsParams(search, page));
                }

                setBackground(R.color.colorHome, R.color.colorPrimary, R.color.colorHome);
                setTextColor(R.color.colorWhite, R.color.colorBlack, R.color.colorWhite);
                break;

            case R.id.tvCheckInRestaurants:
                if (!isCheck) {
                    pd.show();
                    page = 1;
                    ModelManager.getInstance().getRestaurantsManager()
                            .checkInRestaurants(Operations.getCheckInRestaurantsParams(search, page));

                    restaurantsList.clear();
                    recyclerView.scrollToPosition(0);

                    isLoaded(false, false, true);
                }

                setBackground(R.color.colorHome, R.color.colorHome, R.color.colorPrimary);
                setTextColor(R.color.colorWhite, R.color.colorWhite, R.color.colorBlack);
                break;

            case R.id.tvBookmarks:
                if (user_id.isEmpty()) {
                    Toast.makeText(activity, "Please login to check your bookmark restaurants",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                navigationDrawer.closeDrawer(GravityCompat.START);
                startActivity(new Intent(this, BookmarkRestaurantsActivity.class));
                break;
        }
    }

    public void isLoaded(boolean a, boolean b, boolean c) {
        isRes = a;
        isFav = b;
        isCheck = c;
    }

    public void setBackground(int a, int b, int c) {
        tvRestaurants.setBackgroundResource(a);
        tvFavRestaurants.setBackgroundResource(b);
        tvCheckInRestaurants.setBackgroundResource(c);
    }

    public void setTextColor(int a, int b, int c) {
        tvRestaurants.setTextColor(ContextCompat.getColor(this, a));
        tvFavRestaurants.setTextColor(ContextCompat.getColor(this, b));
        tvCheckInRestaurants.setTextColor(ContextCompat.getColor(this, c));
    }

    public void setDrawer() {
        TextView tvUsername = findViewById(R.id.tvUsername);
        TextView tvLogout = findViewById(R.id.tvLogout);
        TextView tvAccount = findViewById(R.id.tvAccount);
        TextView tvBookmarks = findViewById(R.id.tvBookmarks);
        TextView tvTerms = findViewById(R.id.tvTerms);
        TextView tvAbout = findViewById(R.id.tvAbout);

        String first_name = FBPreferences.readString(this, "first_name");
        String last_name = FBPreferences.readString(this, "last_name");
        String name = first_name + " " + last_name;
        String profile_pic = FBPreferences.readString(this, "profile_pic");

        if (user_id.isEmpty()) {
            tvLogout.setText(R.string.login);
            tvAccount.setVisibility(View.GONE);
        }
        if (!profile_pic.isEmpty())
            Picasso.with(this)
                    .load(profile_pic)
                    .fit()
                    .transform(Utils.imageTransformation())
                    .placeholder(R.drawable.demo_img)
                    .into(imgProfilePic);

        if (!first_name.isEmpty())
            tvUsername.setText(name);
        else
            tvUsername.setText(R.string.guest_user);

        tvLogout.setOnClickListener(this);
        tvAccount.setOnClickListener(this);
        tvBookmarks.setOnClickListener(this);
        tvTerms.setOnClickListener(this);
        tvAbout.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        /*if (Utils.isRestaurantRated) {
           *//* ModelManager.getInstance().getRestaurantDetailsManager()
                    .getRestaurantDetails(this, Operations.getRestaurantDetailsParams(restaurant_id, user_id));*//*

            // RestaurantsManager.datumList.clear();
            // restaurantsList.clear();
            Utils.isRestaurantRated = false;
            // page = 1;
            pd.show();

            restaurantsList.removeAll(RestaurantsManager.datumList);

            if (isRes)
                ModelManager.getInstance().getRestaurantsManager()
                        .searchRestaurant(Operations.getSearchRestaurantParams(search, page));
            else if (isFav)
                ModelManager.getInstance().getRestaurantsManager()
                        .favRestaurants(Operations.getFavRestaurantsParams(search, page));
            else
                ModelManager.getInstance().getRestaurantsManager()
                        .checkInRestaurants(Operations.getCheckInRestaurantsParams(search, page));

        }*/

        setDrawer();
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

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(Event event) {
        EventBus.getDefault().removeStickyEvent(Event.class);
        switch (event.getKey()) {
            case Constants.RESTAURANTS_SEARCH_SUCCESS:
                pd.dismiss();
                recyclerView.addOnScrollListener(onScrollListener);
                /*if (isSearch) {
                    resultLayout.setVisibility(View.VISIBLE);
                    tvResults.setText(editSearch.getText().toString());
                }*/

                tvNoRestaurant.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

                if (dialogLocation != null && dialogLocation.isShowing())
                    dialogLocation.dismiss();

                restaurantsList.addAll(RestaurantsManager.datumList);
                restaurantsAdapter.notifyDataSetChanged();

                break;

            case Constants.RESTAURANTS_SEARCH_FAILED:
                /*if (isSearch) {
                    resultLayout.setVisibility(View.VISIBLE);
                    tvResults.setText(editSearch.getText().toString());
                }*/
                recyclerView.removeOnScrollListener(onScrollListener);
                recyclerView.clearOnScrollListeners();

                pd.dismiss();

                if (restaurantsList.size() < 1) {
                    tvNoRestaurant.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    Toast.makeText(activity, R.string.no_restaurant, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(activity, "No more data.", Toast.LENGTH_SHORT).show();
                }

                if (dialogLocation != null && dialogLocation.isShowing())
                    dialogLocation.dismiss();


                break;

            case Constants.LOCATION_SUCCESS:
                search = FBPreferences.readString(this, "location");
                if (!search.isEmpty())
                    editSearch.setText(search);

                if (!pd.isShowing())
                    pd.show();
                if (dialogLocation != null && dialogLocation.isShowing())
                    dialogLocation.dismiss();
                if (isRes)
                    ModelManager.getInstance().getRestaurantsManager()
                            .searchRestaurant(Operations.getSearchRestaurantParams(event.getValue(), 1));
                else if (isFav)
                    ModelManager.getInstance().getRestaurantsManager()
                            .favRestaurants(Operations.getFavRestaurantsParams(event.getValue(), page));
                else
                    ModelManager.getInstance().getRestaurantsManager()
                            .checkInRestaurants(Operations.getCheckInRestaurantsParams(event.getValue(), page));
                break;

            case Constants.LOCATION_EMPTY:
                Toast.makeText(activity, "Unable to get your current location.", Toast.LENGTH_SHORT).show();
                break;

            case Constants.LOGOUT_SUCCESS:
                recyclerView.removeOnScrollListener(onScrollListener);
                pd.dismiss();
                startActivity(new Intent(this, LoginActivity.class));
                navigationDrawer.closeDrawer(GravityCompat.START);
                finish();
                Toast.makeText(activity, "You have been logged out successfully", Toast.LENGTH_SHORT).show();
                break;

            case Constants.LOGOUT_FAILED:
                pd.dismiss();
                Toast.makeText(activity, Constants.SERVER_ERROR, Toast.LENGTH_SHORT).show();
                break;

            case Constants.NO_RESPONSE:
                pd.dismiss();
                Toast.makeText(activity, "" + event.getValue(), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void loadMore() {
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                .getLayoutManager();

        onScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView,
                                   int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int totalItemCount = linearLayoutManager.getItemCount();
                int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                int visibleThreshold = 1;
                boolean endHasBeenReached = lastVisibleItem + visibleThreshold >= totalItemCount;

                if (totalItemCount > 0 && endHasBeenReached) {
                        if (RestaurantsManager.datumList.size() > 1) {
                            RestaurantsManager.datumList.clear();
                            pd.show();
                            if (isRes)
                                ModelManager.getInstance().getRestaurantsManager()
                                        .searchRestaurant(Operations.getSearchRestaurantParams(search, ++page));
                            else if (isFav)
                                ModelManager.getInstance().getRestaurantsManager()
                                        .favRestaurants(Operations.getFavRestaurantsParams(search, ++page));
                            else
                                ModelManager.getInstance().getRestaurantsManager()
                                        .checkInRestaurants(Operations.getCheckInRestaurantsParams(search, ++page));
                        }
                }
            }
        };

        recyclerView.addOnScrollListener(onScrollListener);
    }

    private void enableLoc() {
        if (!mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();
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
                Toast.makeText(activity, "Please grant all the permissions.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(activity, "Please grant all the permissions.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case co.fav.bites.controller.LocationManager.REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case RESULT_OK:
                        pd.show();
                        getCurrentLocation();
                        break;
                    case RESULT_CANCELED:
                        break;
                }
                break;

            case Constants.DETAILS_REQUEST_CODE:
              //  if (Utils.isReviewed) {
                    if (data != null) {
                        float item_rating = 0.0f;
                        List<String> ratingList = new ArrayList<>();
                        for (RestaurantData.Datum datum : restaurantsList) {
                            List<RestaurantData.Subitem> subItemsList = Parcels.unwrap(data.getParcelableExtra("subItems_list"));
                            String restaurant_id = data.getStringExtra("restaurant_id");

                            RestaurantData.Restaurant restaurant = datum.restaurant;
                            if (restaurant_id.equals(restaurant.id)) {
                                int j = 0;
                                for (RestaurantData.Subitem subItem : subItemsList) {
                                    List<RestaurantData.Subitem> homeItems = datum.subitem;

                                    homeItems.get(j).setRating(subItem.rating);
                                    homeItems.get(j).setReviewCount(subItem.reviewCount);
                                    homeItems.get(j).setBasePrice(subItem.basePrice);
                                    homeItems.get(j).setName(subItem.name);
                                    homeItems.get(j).setKey(subItem.key);

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
                                    restaurant.setItem_rating(String.valueOf(avg));

                                    restaurantsAdapter.notifyDataSetChanged();
                                    j++;
                                }

                                return;
                            }
                        }
                    }
               // }
                break;

            case Constants.MENU_REQUEST_CODE:
                if (Utils.isReviewed) {
                    if (data != null && !data.getStringExtra("dish_key").isEmpty()) {
                        float item_rating = 0.0f;
                        List<String> ratingList = new ArrayList<>();
                        for (RestaurantData.Datum datum : restaurantsList) {
                            List<RestaurantData.Subitem> subItemsList = datum.subitem;
                            for (RestaurantData.Subitem subItem : subItemsList) {

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

                                    RestaurantData.Restaurant restaurant = datum.restaurant;
                                    float avg = item_rating / ratingList.size();
                                    restaurant.setItem_rating(String.valueOf(avg));

                                    restaurantsAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }

                    Utils.isReviewed = false;
                }
                break;

        }
    }

    public void getCurrentLocation() {
        if (!pd.isShowing())
            pd.show();
        ModelManager.getInstance().getLocationManager()
                .startLocationUpdates(this, mGoogleApiClient);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (pd.isShowing()) {
                    pd.dismiss();
                    Toast.makeText(getApplicationContext(), "Unable to get your current location",
                            Toast.LENGTH_SHORT).show();
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

}