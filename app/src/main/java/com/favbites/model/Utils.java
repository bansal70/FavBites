package com.favbites.model;

/*
 * Created by rishav on 8/18/2017.
 */

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Environment;
import android.util.Log;
import android.view.Window;
import android.widget.LinearLayout;

import com.favbites.model.beans.RestaurantData;
import com.kaopiz.kprogresshud.KProgressHUD;
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

public class Utils {

    private static final String TAG = Utils.class.getSimpleName();
    public static List<RestaurantData.Datum> restaurantsList = new ArrayList<>();

    public static KProgressHUD showDialog(Context context) {
        KProgressHUD hud;
        hud = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(false)
                .setAnimationSpeed(1)
                .setDimAmount(0.5f);

        return hud;
    }

    public static KProgressHUD showMessageDialog(Context context, String message) {
        KProgressHUD hud;
        hud = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(false)
                .setLabel(message)
                .setAnimationSpeed(1)
                .setDimAmount(0.5f);

        return hud;
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
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
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
                Log.e(TAG, "No address returned");
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

        Log.e(TAG, "Circular image transformation");

        return transformation;
    }

}
