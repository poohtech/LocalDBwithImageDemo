package com.example.localdbdemo.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by user on 1/3/16.
 */
public class Pref {
    private static SharedPreferences sharedPreferences = null;

    public static void openPref(Context context) {
        sharedPreferences = context.getSharedPreferences("PREF_FILE", Context.MODE_PRIVATE);
    }

    public static String getValue(Context context, String key, String value) {
        Pref.openPref(context);
        String result = Pref.sharedPreferences.getString(key, value);
        Pref.sharedPreferences = null;
        return result;
    }

    public static void setValue(Context context, String key, String value) {
        Pref.openPref(context);
        SharedPreferences.Editor editor = Pref.sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
        editor = null;
        Pref.sharedPreferences = null;
    }

    public static long getValue(Context context, String key, long value) {
        Pref.openPref(context);
        long result = Pref.sharedPreferences.getLong(key, value);
        Pref.sharedPreferences = null;
        return result;
    }

    public static void setValue(Context context, String key, long value) {
        Pref.openPref(context);
        SharedPreferences.Editor editor = Pref.sharedPreferences.edit();
        editor.putLong(key, value);
        editor.commit();
        editor = null;
        Pref.sharedPreferences = null;
    }
}
