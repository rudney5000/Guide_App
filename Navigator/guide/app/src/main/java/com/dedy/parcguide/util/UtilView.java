package com.dedy.parcguide.util;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dedy.parcguide.MainActivity;
import com.dedy.parcguide.R;
import com.dedy.parcguide.googleplaces.Constants;


public class UtilView {

    public static void getStarsByValue(double value, LinearLayout container, LayoutInflater inflater, Constants.PLACE_TYPES type) {

        double valueAfterDecimalPoint = value - Math.floor(value);

        int roundedValue = (int) (value - valueAfterDecimalPoint);

        for (int i = 0; i < roundedValue; i++) {
            ImageView ratingImageFull = (ImageView) inflater.inflate(R.layout.rating_image, null);
            ratingImageFull.setBackgroundResource(Constants.PLACE_TYPES.getFullRatingImageForType(type));

            container.addView(ratingImageFull);
        }

        if (valueAfterDecimalPoint >= 0 && valueAfterDecimalPoint < 0.25) {
            ImageView ratingImageEmpty = (ImageView) inflater.inflate(R.layout.rating_image, null);

            container.addView(ratingImageEmpty);
        } else if (valueAfterDecimalPoint >= 0.25 && valueAfterDecimalPoint < 0.75) {
            ImageView ratingImageHalf = (ImageView) inflater.inflate(R.layout.rating_image, null);
            ratingImageHalf.setBackgroundResource(Constants.PLACE_TYPES.getHalfRatingImageForType(type));

            container.addView(ratingImageHalf);
        } else {
            ImageView ratingImageFull = (ImageView) inflater.inflate(R.layout.rating_image, null);
            ratingImageFull.setBackgroundResource(Constants.PLACE_TYPES.getFullRatingImageForType(type));

            container.addView(ratingImageFull);
        }

        if (roundedValue < 4) {
            for (int i = 0; i < 4 - roundedValue; i++) {
                ImageView ratingImageEmpty = (ImageView) inflater.inflate(R.layout.rating_image, null);

                container.addView(ratingImageEmpty);
            }
        }
    }

    public static void showSoftKeyboard(Activity activity, EditText edittext) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(edittext, InputMethodManager.SHOW_IMPLICIT);

    }

    public static void hideSoftKeyboard(Activity activity, EditText edittext) {
        Context c = activity.getBaseContext();
        View v = edittext.findFocus();
        if (v == null)
            return;
        InputMethodManager inputManager = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static int getDrawableIdByString(Context c, String name) {
        try {
            int id = c.getResources().getIdentifier("forecast_" + name, "drawable", c.getPackageName());

            return id;
        } catch (Exception e) {

        }

        return 0;
    }

    public static void setNoLollipopPadding(View view, int top, int bottom) {

        if (!isLollipop()) {
            view.setPadding(0, (int) (top * MainActivity.density), 0, (int) (bottom * MainActivity.density));
        }
    }

    public static void setLollipopPadding(View view, int left, int top, int right, int bottom) {

        if (isLollipop()) {
            view.setPadding(0, (int) (top * MainActivity.density), 0, (int) (bottom * MainActivity.density));
        }
    }

    public static void setViewHeight(View view, float height) {
        view.getLayoutParams().height = (int) (MainActivity.density * height);
    }

    public static boolean isLollipop() {
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion < Build.VERSION_CODES.LOLLIPOP) {
            return false;
        }

        return true;
    }
}
