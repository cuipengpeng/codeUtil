package com.test.xcamera.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.test.xcamera.R;

import java.util.Random;

/**
 * zjh  双击点赞
 */
public class LoveLayout extends RelativeLayout {

    private OnCallBack onCallBack;
    private Context context;
    private Drawable icon;
    private int mClickCount = 0;
    private LikeLayoutHandler mHandler;


    public LoveLayout(Context context) {
        this(context, null);
    }

    public LoveLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoveLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        icon = context.getResources().getDrawable(R.mipmap.ic_heart);
        mHandler = new LikeLayoutHandler();
        setClipChildren(false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();
            mClickCount++;
            mHandler.removeCallbacksAndMessages(null);
            if (mClickCount >= 2) {
                addHeartView(x, y);
                if (onCallBack != null && isFastClick()) {
                    onCallBack.onLikeListener();
                }
                mHandler.sendEmptyMessageDelayed(1, 500);
            } else {
                mHandler.sendEmptyMessageDelayed(0, 500);
            }

        }
        return true;
    }
    private void addHeartView(float x, float y) {
        LayoutParams lp = new LayoutParams(icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
        lp.leftMargin = (int) (x - icon.getIntrinsicWidth() / 2);
        lp.topMargin = (int) (y - icon.getIntrinsicHeight());
        ImageView img = new ImageView(context);
        img.setScaleType(ImageView.ScaleType.MATRIX);
        Matrix matrix = new Matrix();
        matrix.postRotate(getRandomRotate());
        img.setImageMatrix(matrix);
        img.setImageDrawable(icon);
        img.setLayoutParams(lp);
        addView(img);
        AnimatorSet animSet = getShowAnimSet(img);
        AnimatorSet hideSet = getHideAnimSet(img);
        animSet.start();
        animSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                hideSet.start();
            }
        });
        hideSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                removeView(img);
            }
        });
    }

    private AnimatorSet getShowAnimSet(ImageView view) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1.2f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1.2f, 1f);
        AnimatorSet animSet = new AnimatorSet();
        animSet.playTogether(scaleX, scaleY);
        animSet.setDuration(100);
        return animSet;
    }

    private AnimatorSet getHideAnimSet(ImageView view) {
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 1f, 0.1f);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 2f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 2f);
        ObjectAnimator translation = ObjectAnimator.ofFloat(view, "translationY", 0f, -150f);
        AnimatorSet animSet = new AnimatorSet();
        animSet.playTogether(alpha, scaleX, scaleY, translation);
        animSet.setDuration(500);
        return animSet;
    }

    private float getRandomRotate() {
        return new Random().nextInt(60) - 30;
    }

    private void pauseClick() {
        if (mClickCount == 1) {
            if (onCallBack != null) {
                onCallBack.onPauseListener();
            }
        }
        mClickCount = 0;
    }

    public void onPause() {
        mClickCount = 0;
        mHandler.removeCallbacksAndMessages(null);
    }

    public class LikeLayoutHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    pauseClick();
                    break;
                case 1:
                    onPause();
                    break;
            }
        }
    }

    public void setCallBack(OnCallBack onCallBack) {
        this.onCallBack = onCallBack;
    }

    public interface OnCallBack {
        void onLikeListener();

        void onPauseListener();
    }
    private static final int MIN_CLICK_DELAY_TIME = 1000;
    private static long lastClickTime;

    public static boolean isFastClick() {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            flag = true;
        }
        lastClickTime = curClickTime;
        return flag;
    }
}