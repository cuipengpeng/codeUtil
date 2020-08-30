package com.test.xcamera.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by smz on 2020/1/19.
 */

public class DlgUtils {
    /**
     * toast的方向
     * */
    public static int mRotate = 0;
    /**
     * 显示Toast
     */
    public static void toast(Context context, String text, int rotate) {
        CameraToastUtil.show(text, context, mRotate);
    }

    public static void toast(Context context, String text) {
        toast(context, text, 0);
    }

    public static void toastCenterL(Context context, String text) {
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
