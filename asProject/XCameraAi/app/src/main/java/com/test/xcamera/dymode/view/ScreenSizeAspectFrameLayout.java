package com.test.xcamera.dymode.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.ss.android.ttve.utils.UIUtils;

public class ScreenSizeAspectFrameLayout extends FrameLayout {
    public ScreenSizeAspectFrameLayout(@NonNull Context context) {
        super(context);
    }

    public ScreenSizeAspectFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ScreenSizeAspectFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 屏幕定高, 适配宽.
        int screenHeight = UIUtils.getScreenHeight(getContext());
        int screenWidth = UIUtils.getScreenWidth(getContext());
        int height = 0, width = 0;
        double screenScale = (double) screenWidth / screenHeight;
//        if (screenScale > 0.5625) {
//            width = screenWidth;
////            height = width * 16 / 9;
//            height = width * 9 / 16;
//        } else {
//            height = screenHeight;
////            width = (int) (height * 9.0f / 16);
//            width = (int) (height * 16.0f / 9);
//        }

//        String content = "screenWidth: " + screenWidth + ", screenHeight: " + screenHeight + ", screenScale：" + screenScale + "";
//        FileUtil.writeFileToSDCard(testPath, content.getBytes(), testName, true, true, false);

        if (screenScale > 0.5625) {
            height = screenHeight;
            width = height * 16 / 9;


//            height = screenHeight;
//            width = height * 16 / 9;
        } else {
            height = screenHeight;
            width = (int) (height * 9.0f / 16);
//            width = (int) (height * 16.0f / 9);
        }

        widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);

//        String content1 = "width: " + width + ", height: " + height + ", widthMeasureSpec：" + widthMeasureSpec + "" + ", heightMeasureSpec: " + heightMeasureSpec;
//        FileUtil.writeFileToSDCard(testPath, content1.getBytes(), testName, true, true, false);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

//    static String testPath = Environment.getExternalStorageDirectory().getPath() + "/MoTest";
//    static String testName = "testu.txt";
}