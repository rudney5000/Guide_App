package com.dedy.parcguide.util;

import android.app.Activity;
import android.os.Build;

import com.dedy.parcguide.R;
import com.dedy.parcguide.googleplaces.Constants;


public class ThemeUtil {

    public static void setCurrentListTheme(Constants.PLACE_TYPES type, Activity activity) {

        int currentApiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentApiVersion >= Build.VERSION_CODES.LOLLIPOP) {

            if (type == Constants.PLACE_TYPES.GOOGLE_PLACES_SLEEP) {
                activity.setTheme(R.style.AppThemeSleep);
            }
        }
    }

    public static void setTranslucentTheme(Activity activity) {
        int currentApiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentApiVersion >= Build.VERSION_CODES.LOLLIPOP) {

            activity.setTheme(R.style.AppThemeTranslucent);
        }
    }
}
