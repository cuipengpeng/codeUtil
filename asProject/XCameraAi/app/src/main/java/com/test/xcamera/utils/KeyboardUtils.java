package com.test.xcamera.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

public class KeyboardUtils {

    private static final String TAG = KeyboardUtils.class.getSimpleName();

    private static final String EXTRA_DEF_KEYBOARD_HEIGHT = "def_keyboard_height";
    private static final String EXTRA_KEYBOARD_OPEN = "keyboard_height_open";
    private static final int DEF_KEYBOARD_HEIGHT_WITH_DP = 310;
    private static int sDefKeyboardHeight = -1;

    private static DisplayMetrics getDisplayMetrics(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm;
    }

    public static int getDisplayWidthPixels(Context context) {
        return getDisplayMetrics(context).widthPixels;
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int getFontHeight(TextView textView) {
        Paint paint = new Paint();
        paint.setTextSize(textView.getTextSize());
        Paint.FontMetrics fm = paint.getFontMetrics();
        return (int) Math.ceil(fm.bottom - fm.top);
    }

    public static View getRootView(Activity context) {
        return ((ViewGroup) context.findViewById(android.R.id.content)).getChildAt(0);
    }

    public static int getDefKeyboardHeight(Context context) {
        if (sDefKeyboardHeight < 0) {
            sDefKeyboardHeight = dip2px(context, DEF_KEYBOARD_HEIGHT_WITH_DP);
        }
        int height = PreferenceManager.getDefaultSharedPreferences(context).getInt(EXTRA_DEF_KEYBOARD_HEIGHT, 0);
        return sDefKeyboardHeight = height > 0 && sDefKeyboardHeight != height ? height : sDefKeyboardHeight;
    }

    public static void setDefKeyboardHeight(Context context, int height) {
        if (sDefKeyboardHeight != height) {
            PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(EXTRA_DEF_KEYBOARD_HEIGHT, height).commit();
            KeyboardUtils.sDefKeyboardHeight = height;
        }
    }

    public static boolean isFullScreen(final Activity activity) {
        return (activity.getWindow().getAttributes().flags &
                WindowManager.LayoutParams.FLAG_FULLSCREEN) != 0;
    }

    public static int getNavigationBarSize(Resources resources) {
        int resourceId = resources.getIdentifier("navigation_bar_height",
                "dimen", "android");
        return resourceId > 0 ? resources.getDimensionPixelSize(resourceId) : 0;
    }

    public static boolean hasNavigationBar(Resources resources) {
        int hasNavBarId = resources.getIdentifier("config_showNavigationBar",
                "bool", "android");
        return hasNavBarId > 0 && resources.getBoolean(hasNavBarId);
    }

    /**
     * 开启软键盘
     * @param et
     */
    public static void openSoftKeyboard(Context context, EditText et) {
        if (et != null) {
            et.setFocusable(true);
            et.setFocusableInTouchMode(true);
            et.requestFocus();
            InputMethodManager inputManager = (InputMethodManager) et.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(et, 0);
            PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(EXTRA_KEYBOARD_OPEN, true).commit();
        }
    }

    public static void closeSoftKeyboard(Context context, View view) {
        if (view == null || view.getWindowToken() == null) {
            Log.d(TAG, "Window token == null");
            return;
        }
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(EXTRA_KEYBOARD_OPEN, false).commit();
    }

    public static boolean isKeyboardOpen(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(EXTRA_KEYBOARD_OPEN, false);
    }
}
