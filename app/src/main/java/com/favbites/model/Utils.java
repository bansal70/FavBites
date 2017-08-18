package com.favbites.model;

/*
 * Created by rishav on 8/18/2017.
 */

import android.content.Context;
import android.util.Log;

import com.kaopiz.kprogresshud.KProgressHUD;

public class Utils {

    private static final String TAG = Utils.class.getSimpleName();

    public static KProgressHUD showDialog(Context context) {
        KProgressHUD hud = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);

        Log.i(TAG, "Dialog created");

        return hud;
    }
}
