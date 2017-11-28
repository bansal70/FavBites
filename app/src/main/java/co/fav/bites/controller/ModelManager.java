package co.fav.bites.controller;

/*
 * Created by Rishav on 8/18/2017.
 */

public class ModelManager {

    private static ModelManager modelManager;
    private RegistrationManager registrationManager;
    private LoginManager loginManager;
    private ForgotPasswordManager forgotPasswordManager;
    private LocationManager locationManager;
    private GuestLoginManager guestLoginManager;
    private RestaurantsManager restaurantsManager;
    private RestaurantDetailsManager restaurantDetailsManager;
    private ReviewsManager reviewsManager;
    private BookmarkManager bookmarkManager;
    private UploadPhotoManager uploadPhotoManager;
    private LogoutManager logoutManager;
    private AccountManager accountManager;
    private FollowUserManager followUserManager;
    private FollowersManager followersManager;
    private FollowingManager followingManager;
    private UserPostManager userPostManager;
    private UserReviewManager userReviewManager;
    private FacebookLoginManager facebookLoginManager;
    private CheckInManager checkInManager;

    public static ModelManager getInstance() {
        if (modelManager == null)
            return modelManager = new ModelManager();
        else
            return modelManager;
    }

    private ModelManager() {
        registrationManager = new RegistrationManager();
        loginManager = new LoginManager();
        forgotPasswordManager = new ForgotPasswordManager();
        locationManager = new LocationManager();
        guestLoginManager = new GuestLoginManager();
        restaurantsManager = new RestaurantsManager();
        reviewsManager = new ReviewsManager();
        bookmarkManager = new BookmarkManager();
        restaurantDetailsManager = new RestaurantDetailsManager();
        uploadPhotoManager = new UploadPhotoManager();
        logoutManager = new LogoutManager();
        accountManager = new AccountManager();
        followUserManager = new FollowUserManager();
        followersManager = new FollowersManager();
        followingManager = new FollowingManager();
        userPostManager = new UserPostManager();
        userReviewManager = new UserReviewManager();
        facebookLoginManager = new FacebookLoginManager();
        checkInManager = new CheckInManager();
    }

    public RegistrationManager getRegistrationManager() {
        return registrationManager;
    }

    public LoginManager getLoginManager() {
        return loginManager;
    }

    public ForgotPasswordManager getForgotPasswordManager() {
        return forgotPasswordManager;
    }

    public LocationManager getLocationManager() {
        return locationManager;
    }

    public GuestLoginManager getGuestLoginManager() {
        return guestLoginManager;
    }

    public RestaurantsManager getRestaurantsManager() {
        return restaurantsManager;
    }

    public ReviewsManager getReviewsManager() {
        return reviewsManager;
    }

    public BookmarkManager getBookmarkManager() {
        return bookmarkManager;
    }

    public RestaurantDetailsManager getRestaurantDetailsManager() {
        return restaurantDetailsManager;
    }

    public UploadPhotoManager getUploadPhotoManager() {
        return uploadPhotoManager;
    }

    public LogoutManager getLogoutManager() {
        return logoutManager;
    }

    public AccountManager getAccountManager() {
        return accountManager;
    }

    public FollowUserManager getFollowUserManager() {
        return followUserManager;
    }

    public FollowersManager getFollowersManager() {
        return followersManager;
    }

    public FollowingManager getFollowingManager() {
        return followingManager;
    }

    public UserPostManager getUserPostManager() {
        return userPostManager;
    }

    public UserReviewManager getUserReviewManager() {
        return userReviewManager;
    }

    public FacebookLoginManager getFacebookLoginManager() {
        return facebookLoginManager;
    }

    public CheckInManager getCheckInManager() {
        return checkInManager;
    }
}
