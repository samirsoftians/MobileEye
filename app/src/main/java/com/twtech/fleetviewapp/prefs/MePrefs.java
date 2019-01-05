package com.twtech.fleetviewapp.prefs;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by root on 29/4/16.
 */
public class MePrefs {
    public static final String KEY_IMAGE_PATH = "IMAGE_PATH";
    public static final String PREFS_STORE_IMAGE = "STORE_IMAGE";

    public static void saveImage(Context context, String selectedImagePath) {
        SharedPreferences pref = context.getSharedPreferences(PREFS_STORE_IMAGE, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(KEY_IMAGE_PATH, selectedImagePath);
        editor.commit();
    }

    public static String getKeyImageName(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_STORE_IMAGE, Context.MODE_PRIVATE);
        String imagePath = sharedPreferences.getString(KEY_IMAGE_PATH, "none");
        sharedPreferences = null;
        return imagePath;
    }


}
