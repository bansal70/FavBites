package co.fav.bites.models;

/*
 * Created by rishav on 8/18/2017.
 */

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Transformation;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import co.fav.bites.R;
import co.fav.bites.models.beans.RestaurantData;
import co.fav.bites.views.LoginActivity;
import timber.log.Timber;

public class Utils {

    private static final String TAG = Utils.class.getSimpleName();
    public static List<RestaurantData.Datum> restaurantsList = new ArrayList<>();
    public static boolean isPhotoUploaded = false;
    public static boolean isReviewed = false;
    public static boolean isRestaurantRated = false;

    @SuppressWarnings("ConstantConditions")
    public static Dialog showDialog(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.progress_dialog);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        return dialog;
    }

    public static boolean emailValidator(String email) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    @SuppressWarnings("ConstantConditions")
    public static Dialog createDialog(Context context, int layout) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(layout);
        dialog.setCancelable(false);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        return dialog;
    }

    public static File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        Log.e(TAG, "imagePath: "+image.getAbsolutePath());
        return image;
    }

    public static String getCompleteAddressString(Context context, double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);

                if (returnedAddress.getLocality() != null)
                    strAdd = returnedAddress.getLocality(); //city
                else if (returnedAddress.getAdminArea() != null)
                    strAdd = returnedAddress.getAdminArea(); //state
                else if (returnedAddress.getPostalCode() != null)
                    strAdd = returnedAddress.getPostalCode(); //postal code

            } else {
                Timber.e("No address returned");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strAdd;
    }

    public static Transformation imageTransformation() {
        Transformation transformation = new RoundedTransformationBuilder()
                .borderColor(Color.BLACK)
                .cornerRadiusDp(30)
                .oval(false)
                .build();

        return transformation;
    }

    public static boolean isInternetActive(Context ctx) {
        ConnectivityManager conMgr = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        assert conMgr != null;
        NetworkInfo info = conMgr.getActiveNetworkInfo();

        return info != null && info.isConnected() && info.isAvailable();
    }

    public static void gotoPreviousActivityAnimation(Context mContext) {
        ((Activity) mContext).overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }

    public static void gotoNextActivityAnimation(Context mContext) {
        ((Activity) mContext).overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }

    public static void loadCircularImage(Context mContext, String path, ImageView imageView, int placeHolder) {
        Glide.with(mContext)
                .load(path)
                .apply(new RequestOptions().circleCrop().placeholder(placeHolder))
                .into(imageView);
    }

    public static void loadImage(Context mContext, String path, ImageView imageView, int placeHolder) {
        Glide.with(mContext)
                .load(path)
                .apply(new RequestOptions().centerCrop().placeholder(placeHolder))
                .into(imageView);
    }

    public static void showToast(Context mContext, String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

    public static boolean hasAllPermissionsGranted(Context mContext, @NonNull int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                showToast(mContext, mContext.getString(R.string.error_permissions_denied));
                return false;
            }
        }
        return true;
    }

    public static boolean isGPS(Activity mActivity) {
        LocationManager manager = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);
        return manager != null && manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }


    public static void logoutAlert(final Activity mActivity) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mActivity);
        alertBuilder.setMessage(R.string.alert_logout);

        alertBuilder
                .setPositiveButton(android.R.string.yes, (dialogInterface, i) -> {
                    dialogInterface.dismiss();

                    FBPreferences.clearPref(mActivity);
                    mActivity.startActivity(new Intent(mActivity, LoginActivity.class));
                    mActivity.finish();

                    Utils.showToast(mActivity, mActivity.getString(R.string.success_logged_out));
                })
                .setNegativeButton(android.R.string.no, (dialogInterface, i) -> dialogInterface.dismiss());

        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();
    }


}
