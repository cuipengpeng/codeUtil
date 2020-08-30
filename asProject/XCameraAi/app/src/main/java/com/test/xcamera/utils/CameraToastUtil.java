package com.test.xcamera.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Author: mz
 * Time:  2019/9/25
 */
public class CameraToastUtil {
    public static void show(String msg, Context context) {
        //android 7.0 版本  Toast  没有做catch  处理
        ToastCompat compat = new ToastCompat();
        compat.showToast(context, msg, Toast.LENGTH_SHORT, 0);

    }

    public static void show(String msg, Context context, float rotation) {
        //android 7.0 版本  Toast  没有做catch  处理
        ToastCompat compat = new ToastCompat();
        compat.showToast(context, msg, Toast.LENGTH_SHORT, rotation);
    }

    public static void showCenter(String msg, Context context, float rotation) {
        //android 7.0 版本  Toast  没有做catch  处理
        ToastCompat compat = new ToastCompat();
        compat.showToast(context, msg, Toast.LENGTH_SHORT);

    }

    public static void show90(String msg, Context context) {
        //android 7.0 版本  Toast  没有做catch  处理
        ToastCompat compat = new ToastCompat();
        compat.showToast90(context, msg, Toast.LENGTH_SHORT);

    }

    public static void show180(String msg, Context context) {
        //android 7.0 版本  Toast  没有做catch  处理
        ToastCompat compat = new ToastCompat();
        compat.showToast180(context, msg, Toast.LENGTH_SHORT);

    }

    /**
     * 显示特定位置的
     *
     * @param msg
     * @param context
     * @param gravent
     */
    public static void show(String msg, Context context, int gravent, int x_offent, int y_offent) {
        //android 7.0 版本  Toast  没有做catch  处理
        ToastCompat compat = new ToastCompat();
        compat.showToast(context, msg, Toast.LENGTH_SHORT, gravent, x_offent, y_offent);

    }
}
