package com.test.xcamera.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.test.xcamera.R;
import com.test.xcamera.utils.BottomSelectorUtil;


/**
 * Created by zll on 2019/5/27.
 */

public class BottomSelectorView extends LinearLayout implements View.OnClickListener {
    public static final int LAPSE = 0;
    public static final int SLOW_MOTION = 1;
    public static final int VIDEO = 2;
    public static final int STORY = 3;
    public static final int PHOTO = 4;
    public static final int LONG_EXPLORE = 5;
    private CameraScroller mCameraScroller;
    private CurrentShotModeListener mCurrentIndexListener;
    private Context mContext;
    public TextView mText0, mText1, mText2, mText3, mText4, mText5;
    private float x1 = 0;
    private float x2 = 0;
    private float y1 = 0;
    private float y2 = 0;
    private long mLastTime = 0;

    public BottomSelectorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.bottom_selector_layout, this, true);
    }

    public void init() {
        mCameraScroller = findViewById(R.id.camera_scroller);
        mCameraScroller.setType(0);
        mText0 = findViewById(R.id.bottom_selector_lapse);
        mText1 = findViewById(R.id.bottom_selector_slow_motion);
        mText2 = findViewById(R.id.bottom_selector_video);
        mText3 = findViewById(R.id.bottom_selector_story);
        mText4 = findViewById(R.id.bottom_selector_photo);
        mText5 = findViewById(R.id.bottom_selector_long_explore);

        mText0.setOnClickListener(this);
        mText1.setOnClickListener(this);
        mText2.setOnClickListener(this);
        mText3.setOnClickListener(this);
        mText4.setOnClickListener(this);
        mText5.setOnClickListener(this);

        setEnabled(true);
    }

    public void setBottomSelectorCurrentIndexListener(CurrentShotModeListener listener) {
        mCurrentIndexListener = listener;
    }

    public void onScroll(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            x1 = event.getX();
            y1 = event.getY();
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            x2 = event.getX();
            y2 = event.getY();
            if (x1 - x2 > 50) {
                if (BottomSelectorUtil.getBottomCurrentSelectedIndex() < BottomSelectorUtil.BOTTOM_MAX_INDEX) {
                    setTextEnable(false);
                    moveRight(1);
                    System.out.println("BottomSelectorView onScroll index = " + BottomSelectorUtil.getBottomCurrentSelectedIndex() + " x1 = " + x1 + "   x2 = " + x2);
                } else {
                    setTextEnable(true);
                }
            } else if (x2 - x1 > 50) {
                if (BottomSelectorUtil.getBottomCurrentSelectedIndex() > BottomSelectorUtil.BOTTOM_MIN_INDEX) {
                    setTextEnable(false);
                    moveLeft(1);
                    System.out.println("BottomSelectorView onScroll index = " + BottomSelectorUtil.getBottomCurrentSelectedIndex() + " x1 = " + x1 + "   x2 = " + x2);
                } else {
                    setTextEnable(true);
                }
            }
        }
    }

    public void onScroll(boolean isRight) {
        if (System.currentTimeMillis() - mLastTime > 300) {
            if (!isRight) {
                if (BottomSelectorUtil.getBottomCurrentSelectedIndex() < BottomSelectorUtil.BOTTOM_MAX_INDEX) {
                    moveRight(1);
                    System.out.println("BottomSelectorView onScroll index = " + BottomSelectorUtil.getBottomCurrentSelectedIndex() + " x1 = " + x1 + "   x2 = " + x2);
                }
            } else {
                if (BottomSelectorUtil.getBottomCurrentSelectedIndex() > BottomSelectorUtil.BOTTOM_MIN_INDEX) {
                    moveLeft(1);
                    System.out.println("BottomSelectorView onScroll index = " + BottomSelectorUtil.getBottomCurrentSelectedIndex() + " x1 = " + x1 + "   x2 = " + x2);
                }
            }
            mLastTime = System.currentTimeMillis();
        }
        System.out.println("BottomSelectorView detect on scroll");
    }

    public void moveLeft(int step) {
        // z 1258  w 930  j 2483.33 660.5  m 1770
        System.out.println("BottomSelectorView onScroll moveLeft index = " + (BottomSelectorUtil.getBottomCurrentSelectedIndex() - 1));
        CameraScroller cameraScroller = mCameraScroller;
        int leftIndex = BottomSelectorUtil.getBottomCurrentSelectedIndex() - 1;

        int k = 0;
        for (int i = 0; i < step; i++) {
            k += cameraScroller.getChildAt(leftIndex).getWidth() + cameraScroller.getChildAt(leftIndex + 1).getWidth();
            leftIndex = leftIndex - 1;
        }
        k = Math.round(k / 2.0f);

        cameraScroller.leftIndex = BottomSelectorUtil.getBottomCurrentSelectedIndex() - step;
        cameraScroller.rightIndex = BottomSelectorUtil.getBottomCurrentSelectedIndex();

        cameraScroller.mScroller.startScroll(cameraScroller.getScrollX(), 0, -k, 0, cameraScroller.duration);
        cameraScroller.scrollToNext(cameraScroller.rightIndex, cameraScroller.leftIndex);
        BottomSelectorUtil.setBottomSelectedIndex(BottomSelectorUtil.getBottomCurrentSelectedIndex() - step);
        cameraScroller.invalidate();
        if (mCurrentIndexListener != null) {
            mCurrentIndexListener.currentShotMode(BottomSelectorUtil.getBottomCurrentSelectedIndex());
        }
    }

    public void moveRight(int step) {
        System.out.println("BottomSelectorView onScroll moveRight index = " + (BottomSelectorUtil.getBottomCurrentSelectedIndex() + 1));
        CameraScroller cameraScroller = mCameraScroller;
        int rightIndex = BottomSelectorUtil.getBottomCurrentSelectedIndex() + 1;

        int k = 0;
        for (int i = 0; i < step; i++) {
            k += cameraScroller.getChildAt(rightIndex - 1).getWidth() + cameraScroller.getChildAt(rightIndex).getWidth();
            rightIndex = rightIndex + 1;
        }
        k = Math.round(k / 2.0f);

        cameraScroller.leftIndex = BottomSelectorUtil.getBottomCurrentSelectedIndex();
        cameraScroller.rightIndex = BottomSelectorUtil.getBottomCurrentSelectedIndex() + step;

        cameraScroller.mScroller.startScroll(cameraScroller.getScrollX(), 0, k, 0, cameraScroller.duration);
        cameraScroller.scrollToNext(cameraScroller.leftIndex, cameraScroller.rightIndex);
        BottomSelectorUtil.setBottomSelectedIndex(BottomSelectorUtil.getBottomCurrentSelectedIndex() + step);
        cameraScroller.invalidate();
        if (mCurrentIndexListener != null) {
            mCurrentIndexListener.currentShotMode(BottomSelectorUtil.getBottomCurrentSelectedIndex());
        }
    }

    @Override
    public void onClick(View v) {
        int tag = BottomSelectorUtil.getBottomCurrentSelectedIndex();
        System.out.println("BottomSelectorView detect on onClick");
        if (System.currentTimeMillis() - mLastTime > 300) {
            switch (v.getId()) {
                case R.id.bottom_selector_lapse:
                    tag = Integer.parseInt((String) mText0.getTag());
                    break;
                case R.id.bottom_selector_slow_motion:
                    tag = Integer.parseInt((String) mText1.getTag());
                    break;
                case R.id.bottom_selector_video:
                    tag = Integer.parseInt((String) mText2.getTag());
                    break;
                case R.id.bottom_selector_story:
                    tag = Integer.parseInt((String) mText3.getTag());
                    break;
                case R.id.bottom_selector_photo:
                    tag = Integer.parseInt((String) mText4.getTag());
                    break;
                case R.id.bottom_selector_long_explore:
                    tag = Integer.parseInt((String) mText5.getTag());
                    break;

            }
            textClick(tag - BottomSelectorUtil.getBottomCurrentSelectedIndex());
        }
    }

    private void textClick(int step) {
        if (step > 0) {
            moveRight(Math.abs(step));
        } else if (step < 0) {
            moveLeft(Math.abs(step));
        }
        mLastTime = System.currentTimeMillis();
    }

    public void setTextEnable(boolean enable) {
        mText0.setEnabled(enable);
        mText1.setEnabled(enable);
        mText2.setEnabled(enable);
        mText3.setEnabled(enable);
        mText4.setEnabled(enable);
        mText5.setEnabled(enable);
    }
}
