package com.test.xcamera.utils;

import android.content.Context;
import android.graphics.Point;
import android.view.View;
import android.view.WindowManager;

import com.test.xcamera.application.AiCameraApplication;

/**
 * Created by smz on 2019/12/20.
 */

public class ViewUitls {
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dp2px(Context context, int dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dp(Context context, int pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 设置view是否隐藏
     */
    public static void isViewGone(View view, boolean gone) {
        view.setVisibility(gone ? View.INVISIBLE : View.VISIBLE);
    }

    /**
     * 设置view是否隐藏
     */
    public static void isViewInvis(View view, boolean invisible) {
        view.setVisibility(invisible ? View.INVISIBLE : View.VISIBLE);
    }

    /**
     * 获取屏幕宽高
     *
     * @return width hight
     */
    public static int[] getScreenSize() {
        int size[] = new int[2];
        WindowManager windowManager = (WindowManager) AiCameraApplication.getContext().getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            Point point = new Point();
            windowManager.getDefaultDisplay().getSize(point);
            size[0] = point.x;
            size[1] = point.y;
        }

        return size;
    }
}
