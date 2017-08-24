package com.favbites.model;

/*
 * Created by rishav on 8/18/2017.
 */

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Window;
import android.widget.LinearLayout;

import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    private static final String TAG = Utils.class.getSimpleName();

    public static KProgressHUD showDialog(Context context) {
        KProgressHUD hud = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(1)
                .setDimAmount(0.5f);

        Log.i(TAG, "Dialog created");

        return hud;
    }

    public static KProgressHUD showMessageDialog(Context context, String message) {
        KProgressHUD hud = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setLabel(message)
                .setAnimationSpeed(1)
                .setDimAmount(0.5f);

        Log.i(TAG, "Dialog created");

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

}
