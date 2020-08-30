package com.test.xcamera.utils;

import android.content.Context;
import android.content.SharedPreferences;


public class SharedPreferencesUtil {

    public static final String STIRAGE_DAME = "meetvr";
    private Context context = null;
    private static SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil();
    private SharedPreferences.Editor spEdit;
    private SharedPreferences sharedPreferences;

    private SharedPreferencesUtil() {

    }

    public static SharedPreferencesUtil instance() {
        return sharedPreferencesUtil;
    }

    public void init(Context context) {
        if (context == null) {
            return;
        }
        sharedPreferences = context.getSharedPreferences(STIRAGE_DAME, 0);
        spEdit = sharedPreferences.edit();
    }

    private String BEAUTY_KEY = "BEAUTY_FlAG";

    public void saveBeautyModle(int value) {
        if (spEdit == null) {
            return;
        }
        spEdit.putInt(BEAUTY_KEY, value);
        spEdit.commit();
    }

    public void saveString(String key, String value) {
        if (spEdit == null) {
            return;
        }
        spEdit.putString(key, value);
        spEdit.commit();
    }

    public String getString(String key) {
        return sharedPreferences.getString(key, null);
    }


    public int getBeautyModle() {
        return sharedPreferences.getInt(BEAUTY_KEY, -1);
    }
}
