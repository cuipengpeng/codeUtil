package com.test.xcamera.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.test.xcamera.R;
import com.editvideo.ScreenUtils;

/**
 * 对焦框
 * Created by zll on 2019/10/11.
 */

public class MoFocusView extends RelativeLayout {
    public static final String TAG = "MoFocusView";
    private ImageView mRect;
    private View mParentLayout;
    private VerticalSeekBar2 mVerticalSeekBar;
    private GestureDetectorCompat mDetector;
    private int focusHeight, focusWidth;
    private int mScreenWidth, mScreenHeight;
    private Context mContext;
    private boolean isCenter = true;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
        }
    };

    public MoFocusView(Context context) {
        super(context);
        initView(context);
    }

    public MoFocusView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.focus_view_layout, this, true);

        mParentLayout = findViewById(R.id.focus_parent_layout);
        mRect = findViewById(R.id.focus_view_rect);
        mVerticalSeekBar = findViewById(R.id.focus_view_seekbar);
        mVerticalSeekBar.setUnSelectColor(getResources().getColor(R.color.focus_bg));
        mVerticalSeekBar.setSelectColor(getResources().getColor(R.color.focus_bg));
        mVerticalSeekBar.setmInnerProgressWidth(1.5f);
        mVerticalSeekBar.setThumb(R.mipmap.icon_sun);
        mVerticalSeekBar.setThumbSize(28, 28);
        mVerticalSeekBar.setProgress(50);
        mVerticalSeekBar.setOnSlideChangeListener(new VerticalSeekBar2.SlideChangeListener() {
            @Override
            public void onStart(VerticalSeekBar2 slideView, int progress) {
                Log.d(TAG, "onStart: " + progress);
            }

            @Override
            public void onProgress(VerticalSeekBar2 slideView, int progress) {
                Log.d(TAG, "onProgress: " + progress);
            }

            @Override
            public void onStop(VerticalSeekBar2 slideView, int progress) {
                Log.d(TAG, "onStop: " + progress);
            }
        });

        mDetector = new GestureDetectorCompat(mContext, new GestureListener());

        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);//设置对焦区域的宽
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);//设置对焦区域的高

        mParentLayout.measure(w, h);
        focusHeight = mParentLayout.getMeasuredHeight();
        focusWidth = mParentLayout.getMeasuredWidth();

        mScreenWidth = ScreenUtils.getScreenWidth(mContext);
        mScreenHeight = ScreenUtils.getScreenHeight(mContext);

        mHandler.postDelayed(mDismissFocusRunnable, 3 * 1000);
    }

    public void onTouch(MotionEvent event) {
        mDetector.onTouchEvent(event);
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.d(TAG, "onSingleTapUp: ");
            showFocus(e, false);
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return true;
        }
    }

    private void showFocus(MotionEvent event, boolean center) {
        isCenter = center;
        int width = focusWidth;
        int height = focusHeight;
//        int width = Util.dip2px(getActivity(), 70);
//        int height = Util.dip2px(getActivity(), 70);
        int x;
        int y;
        if (center) {
            x = mScreenWidth / 2;
            y = mScreenHeight / 2;
        } else {
            x = (int) event.getRawX();
            y = (int) event.getRawY();
        }
        int l, t, r, b;
        l = x - width / 2;
        t = y - height / 2;
        r = x + width / 2;
        b = y + height / 2;
        if (l < 0) {
            r = r + -l;
            l = 0;
        }
        if (t < 0) {
            b = b + -t;
            t = 0;
        }
        if (r > mScreenWidth) {
            l -= r - mScreenWidth;
        }
        if (b > mScreenHeight) {
            t -= b - mScreenHeight;
        }

        mParentLayout.setX(l);
        mParentLayout.setY(t);
        mParentLayout.setVisibility(View.VISIBLE);
        mHandler.removeCallbacks(mDismissFocusRunnable);
        mHandler.postDelayed(mDismissFocusRunnable, 3 * 1000);
    }

    private Runnable mDismissFocusRunnable = new Runnable() {
        @Override
        public void run() {
            mParentLayout.setVisibility(View.GONE);
            if (!isCenter) {
                showFocus(null, true);
            }
        }
    };
}
