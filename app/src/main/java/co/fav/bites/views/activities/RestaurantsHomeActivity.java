package co.fav.bites.views.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import co.fav.bites.R;
import co.fav.bites.models.ApiParams;
import co.fav.bites.models.Config;
import co.fav.bites.models.Constants;
import co.fav.bites.models.FBPreferences;
import co.fav.bites.models.LocationFetcher;
import co.fav.bites.models.RecyclerPagination;
import co.fav.bites.models.Utils;
import co.fav.bites.models.beans.RestaurantData;
import co.fav.bites.models.beans.RestaurantDetailsData;
import co.fav.bites.views.AboutActivity;
import co.fav.bites.views.BookmarkRestaurantsActivity;
import co.fav.bites.views.ProfileActivity;
import co.fav.bites.views.TermsConditionsActivity;
import co.fav.bites.views.UserProfileActivity;
import co.fav.bites.views.adapters.RestaurantsAdapter;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class RestaurantsHomeActivity extends AppBaseActivity {

    @BindView(R.id.recyclerRestaurants)
    RecyclerView recyclerRestaurants;

    @BindView(R.id.editSearch)
    EditText editSearch;

    @BindView(R.id.tvRestaurants)
    TextView tvRestaurants;

    @BindView(R.id.tvFavRestaurants)
    TextView tvFavRestaurants;

    @BindView(R.id.tvCheckInRestaurants)
    TextView tvCheckInRestaurants;

    @BindView(R.id.tvNoRestaurant)
    TextView tvNoRestaurant;

    @BindView(R.id.imgHome)
    ImageView imgHome;

    @BindView(R.id.imgProfilePic)
    ImageView imgProfilePic;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @BindView(R.id.tvUsername)
    TextView tvUsername;

    @BindView(R.id.tvLogout)
    TextView tvLogout;

    @BindView(R.id.tvAccount)
    TextView tvAccount;

    @BindView(R.id.tvLocation)
    TextView tvLocation;

    @BindView(R.id.tvReviews)
    TextView tvReviews;

    private String search = "", user_id = "", searchUrl = Config.search_restaurant_url, name=  "";
    double lat, lng;

    Dialog dialogLocation;
    private boolean isRes = true, isFav = false, isCheck = false;
    private int page = 1;

    private PublishProcessor<Integer> pagination;
    private CompositeDisposable compositeDisposable;
    public boolean requestOnWay = false;
    public List<RestaurantData.Datum> mRestaurantsList = new ArrayList<>();
    RestaurantsAdapter restaurantsAdapter;

    private LocationFetcher mLocationFetch = new LocationFetcher();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurants);
        ButterKnife.bind(this);

        initViews();
    }

    private void initViews() {
        pagination = PublishProcessor.create();
        compositeDisposable = new CompositeDisposable();
        user_id = FBPreferences.readString(this, "user_id");

        recyclerRestaurants.setLayoutManager(new LinearLayoutManager(mContext));
        restaurantsAdapter = new RestaurantsAdapter(this, mRestaurantsList);
        recyclerRestaurants.setAdapter(restaurantsAdapter);

        recyclerRestaurants.addOnScrollListener(new RecyclerPagination(recyclerRestaurants.getLayoutManager()) {
            @Override
            public void onLoadMore(int currentPage, int totalItemCount, View view) {
                if (!requestOnWay) {
                    if (isInternetActive()) {
                        pagination.onNext(page);
                        showDialog();
                        //mProgressBar.setVisibility(View.VISIBLE);
                    } else {
                        showToast(getString(R.string.error_internet_connection));
                    }
                }
            }
        });


        mLocationFetch.setOnLocationFetchListener(mLocation -> {
            if (mLocation == null) {
                showToast(getString(R.string.error_current_location_unavailable));
                try {
                    Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(this);
                    startActivityForResult(intent, Constants.PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }

            name = "";
            editSearch.setText("");

            lat = mLocation.getLatitude();
            lng = mLocation.getLongitude();

            search = Utils.getCompleteAddressString(mContext, mLocation.getLatitude(), mLocation.getLongitude());
            tvLocation.setText(search);

            FBPreferences.putDouble(mContext, Constants.DEFUALT_LAT, lat);
            FBPreferences.putDouble(mContext, Constants.DEFUALT_LNG, lng);
            FBPreferences.putString(mContext, Constants.DEFAULT_LOCATION, search);

            if (isRes) {
                searchRestaurantsBy(Config.search_restaurant_url);
            } else if (isFav) {
                searchRestaurantsBy(Config.fav_restaurant_url);
            } else {
                searchRestaurantsBy(Config.check_restaurant_url);
            }
        });

        checkLocation();
    }

    private void checkLocation() {
        if (Utils.isGPS(this)) {
            checkPermissions();
            return;
        }

        search = FBPreferences.readString(mContext, Constants.DEFAULT_LOCATION);

        if (!search.isEmpty()) {
            lat = FBPreferences.readDouble(mContext, Constants.DEFUALT_LAT);
            lng = FBPreferences.readDouble(mContext, Constants.DEFUALT_LNG);

            tvLocation.setText(search);
            if (isRes) {
                searchRestaurantsBy(Config.search_restaurant_url);
            } else if (isFav) {
                searchRestaurantsBy(Config.fav_restaurant_url);
            } else {
                searchRestaurantsBy(Config.check_restaurant_url);
            }
            return;
        }

        checkPermissions();
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
            } else {
                mLocationFetch.getLocation(this);
            }
        } else {
            mLocationFetch.getLocation(this);
        }
    }

    @OnTouch(R.id.editSearch)
    public boolean getSearch(MotionEvent event) {
        final int DRAWABLE_RIGHT = 2;

        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (event.getRawX() >= (editSearch.getRight() - editSearch.getCompoundDrawables()
                    [DRAWABLE_RIGHT].getBounds().width())) {

                name = editSearch.getText().toString().trim();
                if (name.isEmpty()) {
                    showToast("Please enter the restaurant name or dish name");
                    return false;
                }

                if (isRes) {
                    searchRestaurantsBy(Config.search_restaurant_url);
                }
                else if (isFav) {
                    searchRestaurantsBy(Config.fav_restaurant_url);
                }
                else {
                    searchRestaurantsBy(Config.check_restaurant_url);
                }

                return true;
            }
        }
        return false;
    }

    private void setRestaurantsData(RestaurantData mRestaurant) {
        dismissDialog();
        if (dialogLocation != null) {
            dialogLocation.dismiss();
        }

        if (mRestaurant.response.equals("0") || mRestaurant.data.size() == 0) {
            compositeDisposable.dispose();
            if (mRestaurantsList.size() == 0) {
                tvNoRestaurant.setVisibility(View.VISIBLE);
                recyclerRestaurants.setVisibility(View.GONE);
                showToast(getString(R.string.no_restaurant));
            }
            return;
        }

        requestOnWay = false;
        mRestaurantsList.addAll(mRestaurant.data);
        restaurantsAdapter.notifyDataSetChanged();
    }

    public void initDialog() {
        dialogLocation = Utils.createDialog(this, R.layout.dialog_location);
        TextView tvSubmit = dialogLocation.findViewById(R.id.tvSubmit);
        TextView tvAutoDetect = dialogLocation.findViewById(R.id.tvAutoDetect);
        EditText editLocation = dialogLocation.findViewById(R.id.editLocation);

        tvSubmit.setOnClickListener(v -> {
            search = editLocation.getText().toString().trim();
            if (search.isEmpty()) {
                showToast(getString(R.string.error_empty_location));
                return;
            }
            getRestaurantsApi();
        });

        tvAutoDetect.setOnClickListener(v -> mLocationFetch.requestLocationUpdates(this));
        dialogLocation.show();
    }

    private void getRestaurantsApi() {
        showDialog();
        tvNoRestaurant.setVisibility(View.GONE);
        recyclerRestaurants.setVisibility(View.VISIBLE);

        Disposable disposable = pagination.onBackpressureDrop()
                .doOnNext(page -> requestOnWay = true)
                .concatMap(integer -> getRestaurants())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(this::setRestaurantsData)
                .doOnError(this::serverError)
                .onErrorReturn(throwable -> {
                    dismissDialog();
                    throwable.printStackTrace();
                    return new RestaurantData();
                })
                .subscribe();

        compositeDisposable.add(disposable);
        pagination.onNext(page);
    }

    private Flowable<RestaurantData> getRestaurants() {
        return apiService.getRestaurants(searchUrl, ApiParams.getSearchRestaurantParams(name, page++, lat, lng))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @OnClick(R.id.tvRestaurants)
    public void searchRestaurants() {
        if (isRes) {
            return;
        }
        isRes = true;
        searchRestaurantsBy(Config.search_restaurant_url);
        isLoaded(true, false, false);
        setBackground(R.color.colorPrimary, R.color.colorHome, R.color.colorHome);
        setTextColor(R.color.colorBlack, R.color.colorWhite, R.color.colorWhite);
    }

    @OnClick(R.id.tvFavRestaurants)
    public void favRestaurants() {
        if (isFav) {
            return;
        }
        isFav = true;
        searchRestaurantsBy(Config.fav_restaurant_url);
        isLoaded(false, true, false);
        setBackground(R.color.colorHome, R.color.colorPrimary, R.color.colorHome);
        setTextColor(R.color.colorWhite, R.color.colorBlack, R.color.colorWhite);
    }

    @OnClick(R.id.tvCheckInRestaurants)
    public void checkInRestaurants() {
        if (isCheck) {
            return;
        }
        isCheck = true;
        searchRestaurantsBy(Config.check_restaurant_url);
        isLoaded(false, false, true);
        setBackground(R.color.colorHome, R.color.colorHome, R.color.colorPrimary);
        setTextColor(R.color.colorWhite, R.color.colorWhite, R.color.colorBlack);
    }

    private void searchRestaurantsBy(String url) {
        page = 1;
        searchUrl = url;
        mRestaurantsList.clear();
        restaurantsAdapter.notifyDataSetChanged();
        pagination = PublishProcessor.create();
        compositeDisposable = new CompositeDisposable();
        getRestaurantsApi();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Constants.MENU_REQUEST_CODE:
                if (data != null) {
                    String restaurant_id = data.getStringExtra("restaurant_id");
                    if (restaurant_id == null)
                        return;
                    updateData(restaurant_id);
                }
                break;

            case Constants.SETTINGS_REQUEST_CODE:
                switch (resultCode) {
                    case RESULT_OK:
                        checkPermissions();
                        break;

                    case Activity.RESULT_CANCELED:
                        if (!search.isEmpty())
                            return;
                        try {
                            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .build(this);
                            startActivityForResult(intent, Constants.PLACE_AUTOCOMPLETE_REQUEST_CODE);
                        } catch (GooglePlayServicesRepairableException e) {
                            e.printStackTrace();
                        } catch (GooglePlayServicesNotAvailableException e) {
                            e.printStackTrace();
                        }
                        break;
                }
                break;

            case Constants.PLACE_AUTOCOMPLETE_REQUEST_CODE:
                switch (resultCode) {
                    case RESULT_OK:
                        Place place;
                        if (data != null) {
                            place = PlaceAutocomplete.getPlace(this, data);
                            search = place.getName().toString();
                            tvLocation.setText(search);
                            lat = place.getLatLng().latitude;
                            lng = place.getLatLng().longitude;

                            FBPreferences.putString(mContext, Constants.DEFAULT_LOCATION, search);
                            FBPreferences.putDouble(mContext, Constants.DEFUALT_LAT, lat);
                            FBPreferences.putDouble(mContext, Constants.DEFUALT_LNG, lng);
                            name = "";
                            editSearch.setText("");

                            if (isRes) {
                                searchRestaurantsBy(Config.search_restaurant_url);
                            } else if (isFav) {
                                searchRestaurantsBy(Config.fav_restaurant_url);
                            } else {
                                searchRestaurantsBy(Config.check_restaurant_url);
                            }
                        }
                        break;
                    case PlaceAutocomplete.RESULT_ERROR:
                        Status status;
                        if (data != null) {
                            status = PlaceAutocomplete.getStatus(this, data);
                            Timber.i(status.getStatusMessage());
                        }

                        break;
                    case RESULT_CANCELED:
                        if (search.isEmpty())
                            checkLocation();
                        break;
                }
                break;
        }
    }

    private void updateData(String restaurant_id) {
        showDialog();
        apiService.getRestaurantDetails(ApiParams.getRestaurantDetailsParams(restaurant_id, user_id))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(restaurantDetailsData -> {
                    saveUpdates(restaurantDetailsData, restaurant_id);
                })
                .doOnError(this::serverError)
                .onErrorReturn(throwable -> {
                    serverError(throwable);
                    return new RestaurantDetailsData();
                })
                .subscribe();
    }

    private void saveUpdates(RestaurantDetailsData restaurantDetailsData, String restaurant_id) {
        List<RestaurantData.Subitem> listSubItems = restaurantDetailsData.data.subitem;
        for (RestaurantData.Datum data1 : mRestaurantsList) {
            if (restaurant_id.equals(data1.restaurant.id)) {
                data1.subitem.clear();
                data1.subitem.addAll(listSubItems);
                data1.restaurant = restaurantDetailsData.data.restaurant;
                restaurantsAdapter.notifyDataSetChanged();
                dismissDialog();
            }
        }
    }

    public void isLoaded(boolean search, boolean fav, boolean checkIn) {
        isRes = search;
        isFav = fav;
        isCheck = checkIn;
    }

    public void setBackground(int search, int fav, int checkIn) {
        tvRestaurants.setBackgroundResource(search);
        tvFavRestaurants.setBackgroundResource(fav);
        tvCheckInRestaurants.setBackgroundResource(checkIn);
    }

    public void setTextColor(int search, int fav, int checkIn) {
        tvRestaurants.setTextColor(ContextCompat.getColor(this, search));
        tvFavRestaurants.setTextColor(ContextCompat.getColor(this, fav));
        tvCheckInRestaurants.setTextColor(ContextCompat.getColor(this, checkIn));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE && Utils.hasAllPermissionsGranted(mContext, grantResults)) {
            showDialog();
            mLocationFetch.getLocation(this);
        } else {
            try {
                Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                        .build(this);
                startActivityForResult(intent, Constants.PLACE_AUTOCOMPLETE_REQUEST_CODE);
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
        }
    }

    @OnClick(R.id.tvLocation)
    public void changeLocation() {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .build(this);
            startActivityForResult(intent, Constants.PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        setDrawer();
    }

    @OnClick(R.id.imgHome)
    public void openDrawer() {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public void setDrawer() {
        String first_name = FBPreferences.readString(this, "first_name");
        String last_name = FBPreferences.readString(this, "last_name");
        String name = first_name + " " + last_name;
        String profile_pic = FBPreferences.readString(this, "profile_pic");
        user_id = FBPreferences.readString(this, "user_id");

        if (first_name.isEmpty())
            name = getString(R.string.guest_user);

        if (user_id.isEmpty()) {
            tvLogout.setText(R.string.login);
            tvAccount.setVisibility(View.GONE);
            tvReviews.setVisibility(View.GONE);
        }

        if (!profile_pic.isEmpty())
            Utils.loadCircularImage(mContext, profile_pic, imgProfilePic, R.drawable.demo_img);

        tvUsername.setText(name);

    }

    @OnClick(R.id.imgLocation)
    public void getNearByRestaurants() {
        mLocationFetch.requestLocationUpdates(this);
    }

    @OnClick(R.id.tvLogout)
    public void logoutUser() {
        Utils.logoutAlert(this);
    }

    @OnClick(R.id.tvBookmarks)
    public void bookmarksPage() {
        if (user_id.isEmpty()) {
            showToast(getString(R.string.prompt_check_login_bookmarks));
            return;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        startActivity(new Intent(this, BookmarkRestaurantsActivity.class));
        Utils.gotoNextActivityAnimation(mContext);
    }

    @OnClick(R.id.tvTerms)
    public void termsPage() {
        drawerLayout.closeDrawer(GravityCompat.START);
        startActivity(new Intent(this, TermsConditionsActivity.class));
        Utils.gotoNextActivityAnimation(mContext);
    }

    @OnClick(R.id.tvAbout)
    public void aboutPage() {
        drawerLayout.closeDrawer(GravityCompat.START);
        startActivity(new Intent(this, AboutActivity.class));
        Utils.gotoNextActivityAnimation(mContext);
    }

    @OnClick(R.id.tvAccount)
    public void accountPage() {
        drawerLayout.closeDrawer(GravityCompat.START);
        startActivity(new Intent(this, ProfileActivity.class));
        Utils.gotoNextActivityAnimation(mContext);
    }

    @OnClick(R.id.tvReviews)
    public void userReviews() {
        drawerLayout.closeDrawer(GravityCompat.START);
        startActivity(new Intent(mContext, UserProfileActivity.class)
            .putExtra("user_id", user_id));
        Utils.gotoNextActivityAnimation(mContext);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }
}
