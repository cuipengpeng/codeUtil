package com.test.xcamera.dymode.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.TextureView;

import com.ss.android.ttve.utils.UIUtils;

/**
 * Created by zll on 2020/4/23.
 */

public class AutoFitTextureView extends TextureView {
    private int mRatioWidth = 0;
    private int mRatioHeight = 0;

    private int widthSpec = 0;
    private int heightSpec = 0;

    public AutoFitTextureView(Context context) {
        this(context, null);
    }

    public AutoFitTextureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoFitTextureView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Sets the aspect ratio for this view. The size of the view will be measured based on the ratio
     * calculated from the parameters. Note that the actual sizes of parameters don't matter, that
     * is, calling setAspectRatio(2, 3) and setAspectRatio(4, 6) make the same result.
     *
     * @param width  Relative horizontal size
     * @param height Relative vertical size
     */
    public void setAspectRatio(int width, int height) {
//        if (width < 0 || height < 0) {
//            throw new IllegalArgumentException("Size cannot be negative.");
//        }
//        mRatioWidth = width;
//        mRatioHeight = height;
//        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (widthSpec == 0 || heightSpec == 0) {
            int screenHeight = UIUtils.getScreenHeight(getContext());
            int screenWidth = UIUtils.getScreenWidth(getContext());
            int height = 0, width = 0;
            double screenScale = (double) screenWidth / screenHeight;

            if (screenScale > 0.5625) {
                height = screenHeight;
                width = height * 16 / 9;
            } else {
                height = screenHeight;
                width = (int) (height * 9.0f / 16);
            }

//            widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
//            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);

            widthSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
            heightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthSpec, heightSpec);
    }
}
