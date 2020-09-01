package com.hospital.checkup.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static com.hospital.checkup.base.BaseApplication.getContext;

/**
 * 密度相关工具类
 */
public class DensityUtil {
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getContext().getResources().getDisplayMetrics());
    }

    /**
     * sp转化为dp
     */
    public static int sp2px(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                getContext().getResources().getDisplayMetrics());
    }


    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static float px2dip(float px) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (px / scale + 0.5f);
    }


    /**
     * px转sp
     *
     * @param px
     * @return
     */
    public static float px2sp(float px) {
        final float scale = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (px / scale + 0.5f);
    }

    /**
     * 根据字体大小获取字的高度
     */
    public static int getFontHeight(int fontSize) {
        Paint paint = new Paint();
        paint.setTextSize(sp2px(fontSize));
        Paint.FontMetrics fm = paint.getFontMetrics();
        return (int) Math.ceil(fm.descent - fm.top) + 2;
    }

    /**
     * 得到屏幕宽度
     *
     * @return 宽度
     */
    public static int getScreenWidth() {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    /**
     * 得到屏幕高度
     */
    public static int getScreenHeight() {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }


    /**
     * 获取手机实际内容高度,即除去状态栏和导航栏的高度
     *
     * @return
     */
    public static int getRealContentHeight() {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels - getStatusBarHeight();
    }

    /**
     * 总和上边两个,获取宽高返回数组
     */
    public static int[] getWidthHeight() {
        int[] screen = new int[2];
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        screen[0] = dm.widthPixels;
        screen[1] = dm.heightPixels;
        return screen;
    }

    /**
     * getRawY  当前位置相对整个屏幕的长度
     * getY    当前位置相对容器(当前点击事件的view)的长度
     * <p>
     * 获取高度,不知道有啥用
     */
    public static float getBaseY(MotionEvent e) {
        return e.getRawY() - e.getY() - getStatusBarHeight();
    }

    /**
     * 同上
     */
    public static float getBaseX(MotionEvent e) {
        return e.getRawX() - e.getX();
    }

    /**
     * 重新测量传入的view的宽高
     * 自定义view常用
     */
    public static void measureView(View v) {
        int w = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        v.measure(w, h);
    }


    /**
     * 获取状态栏高度
     */
    public static int getStatusBarHeight() {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = getContext().getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }


    /**
     * 判断是否存在NavigationBar：
     */
    public static boolean checkDeviceHasNavigationBar() {
        boolean hasNavigationBar = false;
        Resources rs = getContext().getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {
            LogUtils.printLog("checkDeviceHasNavigationBar:"+e.getMessage());
        }
        return hasNavigationBar;
    }


    /**
     * 先判断是否存在
     * 获取NavigationBar的高度：
     */
    public static int getNavigationBarHeight() {
        int navigationBarHeight = 0;
        Resources rs = getContext().getResources();
        int id = rs.getIdentifier("navigation_bar_height", "dimen", "android");
        if (id > 0 && checkDeviceHasNavigationBar()) {
            navigationBarHeight = rs.getDimensionPixelSize(id);
        }
        return navigationBarHeight;
    }

    /**
     * 获取手机真实高度
     */
    public static Point getDisplayRealSize() {
        Point point = new Point();
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wm.getDefaultDisplay().getRealSize(point);
            return point;
        } else {
            point.y = getScreenHeight();
            point.x = getScreenWidth();
            return point;
        }
    }


}
