package com.test.xcamera.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.SensorManager;
import android.view.OrientationEventListener;

import com.test.xcamera.enumbean.ScreenOrientationType;

/**
 * Created by zll on 2019/11/13.
 */

public class ScreenOrientationListener extends OrientationEventListener {
    private static final String TAG = "ScreenOrientationListener";
    private static final int SENSOR_ANGLE = 20;
    private ChangeOrientationListener mChangeOrientationListener;
    private int mOrientation = -1;
    private boolean mFirst = true;

    public ScreenOrientationListener(Context context, ChangeOrientationListener listener) {
        super(context, SensorManager.SENSOR_DELAY_NORMAL);
        mChangeOrientationListener = listener;
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onOrientationChanged(int orientation) {
//        Log.d(TAG, "onOrientationChanged orientation=" + orientation);
        if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
            return;  //手机平放时，检测不到有效的角度
        }

        //下面是手机旋转准确角度与四个方向角度（0 90 180 270）的转换
        mFirst = false;
        if (orientation > 360 - SENSOR_ANGLE || orientation < SENSOR_ANGLE) {
            orientation = 0;
            if (mOrientation != orientation || mFirst) {
                mChangeOrientationListener.orientationChanged(ScreenOrientationType.PORTRAIT);
            }
        } else if (orientation > 270 - SENSOR_ANGLE && orientation < 270 + SENSOR_ANGLE) {
            orientation = 270;
            if (mOrientation != orientation) {
                mChangeOrientationListener.orientationChanged(ScreenOrientationType.LANDSCAPE);
            }
        } else if (orientation > 90 - SENSOR_ANGLE && orientation < 90 + SENSOR_ANGLE) {
//            orientation = 90;
//            if (mOrientation != orientation) {
//                mChangeOrientationListener.orientationChanged(ScreenOrientationType.REVERSE_LANDSCAPE);
//            }
        } else if (orientation > 180 - SENSOR_ANGLE && orientation < 180 + SENSOR_ANGLE) {
            orientation = 180;
            if (mOrientation != orientation) {
                mChangeOrientationListener.orientationChanged(ScreenOrientationType.REVERSE_PORTRAIT);
            }
        }
        mOrientation = orientation;
    }

    public interface ChangeOrientationListener {
        void orientationChanged(ScreenOrientationType type);
    }

    public static boolean isLandscape(ScreenOrientationType type) {
        return type == ScreenOrientationType.LANDSCAPE || type == ScreenOrientationType.REVERSE_LANDSCAPE;
    }

    public static boolean isPortrait(ScreenOrientationType type) {
        return type == ScreenOrientationType.PORTRAIT || type == ScreenOrientationType.REVERSE_PORTRAIT;
    }
}
