package com.favbites.model;

import android.content.Context;
import android.content.SharedPreferences;

/*
 * Created by rishav on 8/17/17.
 */

public class FBPreferences {

    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences("CS_PREF", Context.MODE_PRIVATE);
    }

    public static String readString(Context context, String key) {
        return getPreferences(context).getString(key, "");
    }

    public static void putString(Context context, String key, String value) {
         getPreferences(context).edit().putString(key, value).apply();
    }

    public static void getString(Context context , String key , String value){
        getPreferences(context).getString(key,value);
    }

    public static void putBoolean(Context context, String key, boolean value) {
        getPreferences(context).edit().putBoolean(key, value).apply();
    }

    public static boolean getBoolean(Context context, String key, boolean value) {
        return getPreferences(context).getBoolean(key, value);
    }

    public static boolean readBoolean(Context context, String key) {
        return getPreferences(context).getBoolean(key, false);
    }

    public static void clearPref(Context context) {
        getPreferences(context).edit().clear().apply();
    }
}
