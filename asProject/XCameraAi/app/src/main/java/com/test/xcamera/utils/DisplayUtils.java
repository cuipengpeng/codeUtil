package com.test.xcamera.utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class DisplayUtils {

    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     */
    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     */
    public static int dpInt2px(Context context, int dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int dp2px(Context context, int dimen_resID) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (context.getResources().getDimension(dimen_resID) * scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

     public   static  int  getwidth(Activity context){
         WindowManager manager = context.getWindowManager();
         DisplayMetrics outMetrics = new DisplayMetrics();
         manager.getDefaultDisplay().getMetrics(outMetrics);
         int width = outMetrics.widthPixels;
//         int height = outMetrics.heightPixels;
         return width;
     }

    public   static  int  getheight(Activity context){
        WindowManager manager = context.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
         int height = outMetrics.heightPixels;
        return height;
    }
}