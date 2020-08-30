package com.test.xcamera.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.test.xcamera.R;

import java.text.DecimalFormat;

public class MoFPVRockerView extends View {
    public static final String TAG = "MoFPVRockerView";

    //onTouchEvent的状态
    private static final int TOUCH_DOWN = 0;
    private static final int TOUCH_MOVE = 1;
    private static final int TOUCH_UP = 2;
    private static final int TOUCH_CENTER = 3;

    //缩放系数：球离控件的边距
    private static final float PagerMarginsScale = 0f;

    // 背景
    private Drawable mDrawBg;
    // 球
    private Drawable mDrawRocker;
    private int mPagerMargins = 0;

    private int mBgWight, mBgR;

    //球的宽、半径
    private int mRockerWidth, mRockerR;
    //球的坐标
    private int mRockerX, mRockerY;
    private Rect mRockerRect = new Rect();
    //rocker 默认位置 0-center
    private int mRockerPosition = 0;

    //控制模式
    private int mTouchMode = TOUCH_CENTER;

    private Paint paint = new Paint();
    //球坐标的回掉监听
    private OnLocationListener onLocationListener;

    private float mRockerXPer, mRockerYPer;

    private DecimalFormat mFormat = new DecimalFormat("##0.000");

    private static final float VALUE_SCALE = 0.05f;

    public MoFPVRockerView(Context context) {
        super(context);
        init(null, 0);
    }

    public MoFPVRockerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public MoFPVRockerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    public void init(AttributeSet attrs, int defStyleAttr) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MoFPVRockerView,
                defStyleAttr, 0);
        mDrawBg = a.getDrawable(R.styleable.MoFPVRockerView_control_bg);
        mDrawRocker = a.getDrawable(R.styleable.MoFPVRockerView_control_rocker);
        mRockerPosition = a.getInteger(R.styleable.MoFPVRockerView_rocker_gravide, 0);
        a.recycle();
        //根据模式初始化小球位置
        if (mRockerPosition == 0) {
            //居中
            mRockerXPer = 0;
            mRockerYPer = 0;
        } else if (mRockerPosition == 1) {
            //底部
            mRockerXPer = 0;
            mRockerYPer = -1;
        }
        paint.setColor(0xaaff0000);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        parentHeight = parentWidth = Math.min(parentWidth, parentHeight);
        mBgWight = parentWidth;
        mPagerMargins = (int) (parentWidth * PagerMarginsScale);
        mBgR = parentWidth / 2 - mPagerMargins - mRockerR;
        mRockerWidth = mDrawRocker.getIntrinsicWidth();
        mRockerR = mRockerWidth / 2;
//        Log.d(TAG, "onMeasure: mBgWight = " + mBgWight + ", mPagerMargins = " + mPagerMargins +
//                ", mBgR = " + mBgR + ", mRockerWidth = " + mRockerWidth + ", mRockerR = " + mRockerR);
        setMeasuredDimension(parentWidth, parentHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制背景
//        mDrawBg.setBounds(0, 0, mBgWight, mBgWight);
//        mDrawBg.draw(canvas);
        //计算小球的位置
        mDrawRocker.setBounds(getRockerRect());
        //绘制小球
        mDrawRocker.draw(canvas);
    }

    /**
     * 以摇杆当前的坐标为中心点，计算Rocker绘画的范围
     *
     * @return
     */
    private Rect getRockerRect() {
        int rockerX = (int) (mBgR * mRockerXPer + mBgWight / 2);
        int rockerY = (int) (-mBgR * mRockerYPer + mBgWight / 2);
        mRockerRect.set(rockerX - mRockerR, rockerY - mRockerR, rockerX + mRockerR,
                rockerY + mRockerR);
        return mRockerRect;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isTouchRocker((int) event.getX(), (int) event.getY())) {
                    mTouchMode = TOUCH_DOWN;
                    if (onLocationListener != null)
                        onLocationListener.onStart();
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mTouchMode == TOUCH_DOWN || mTouchMode == TOUCH_MOVE) {
                    moveRocker(event.getX(), event.getY());
                    mTouchMode = TOUCH_MOVE;
                    return true;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (mTouchMode == TOUCH_MOVE) {
                    mTouchMode = TOUCH_UP;
                    moveBack(false);
                }
                if (onLocationListener != null)
                    onLocationListener.onFinish();
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * x,y是否在小球的范围。
     * 判断是否触碰到小球
     *
     * @param x
     * @param y
     * @return
     */
    public boolean isTouchRocker(int x, int y) {
        return mRockerRect.contains(x, y);
    }

    /**
     * 移动小球到 x,y
     *
     * @param x
     * @param y
     */
    private void moveRocker(float x, float y) {
        if (!isRange(x, y)) {
            setCircleXY(x, y);
        } else {
            mRockerX = (int) x;
            mRockerY = (int) y;
        }
        setListenerData();
        postInvalidate();
    }

    /**
     * 判断小球是否在范围
     *
     * @param x
     * @param y
     * @return
     */
    private boolean isRange(float x, float y) {
        float dx = mBgWight / 2 - x; // 当前手指x坐标
        float dy = mBgWight / 2 - y; // 当前手指y坐标
        return mBgR >= Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
    }

    /**
     * 计算球的x,y坐标
     *
     * @param x
     * @param y
     */
    private void setCircleXY(float x, float y) {
        float dx = mBgWight / 2 - x;
        float dy = mBgWight / 2 - y;
        double touchR = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
        double cosAngle = Math.acos(dx / touchR);
        if (dy > 0) {
            cosAngle = -cosAngle;
        }
        mRockerX = (int) (mBgR * Math.cos(Math.PI - cosAngle)) + mBgWight / 2;
        mRockerY = (int) (mBgR * Math.sin(cosAngle)) + mBgWight / 2;
        Log.d(TAG, "setCircleXY: mRockerX = " + mRockerX + ", mRockerY = " + mRockerY);
    }

    /**
     * 回调小球位置
     */
    private void setListenerData() {
        if (mBgR == 0) {
            return;
        }
        mRockerXPer = (mRockerX - mBgWight / 2f) / (float) mBgR;
        mRockerYPer = (mBgWight / 2f - mRockerY) / (float) mBgR;
        float x = (Float.valueOf(mFormat.format(mRockerXPer))) * VALUE_SCALE;
        float y = (Float.valueOf(mFormat.format(mRockerYPer))) * VALUE_SCALE;
        String xValue = String.valueOf(((Float.valueOf(mFormat.format(x))) * 1000));
        String yValue = String.valueOf(((Float.valueOf(mFormat.format(y))) * 1000));
        xValue = xValue.substring(0, xValue.indexOf("."));
        yValue = yValue.substring(0, yValue.indexOf("."));
        Log.d(TAG, "setListenerData: x = " + x + "  y = " + y);
        if (onLocationListener != null) {
            if (mTouchMode != TOUCH_UP) {
                onLocationListener.getLocation(Integer.valueOf(xValue), Integer.valueOf(yValue));
            }
        }
    }

    public interface OnLocationListener {
        /**
         * 开始移动
         */
        void onStart();

        /**
         * @param x 方向偏移百分比
         * @param y 方向偏移百分比
         */
        void getLocation(int x, int y);

        /**
         * 停止移动
         */
        void onFinish();
    }

    public void setOnLocationListener(OnLocationListener onLocationListener) {
        this.onLocationListener = onLocationListener;
    }

    /**
     * 动画类
     * 小球回归时使用的动画
     */
    private class AnimToBack implements Runnable {

        private long startTime;
        private float duration;
        private int startX, startY;
        private int dx, dy;

        public void start(int startX, int startY, int endX, int endY, int duration) {
            this.duration = duration;
            dx = endX - startX;
            dy = endY - startY;
            this.startX = startX;
            this.startY = startY;
            startTime = SystemClock.uptimeMillis();
            post(this);
        }

        @Override
        public void run() {
            float progress = (SystemClock.uptimeMillis() - startTime) / duration;
            if (progress >= 1) {
                progress = 1;
            } else {
                post(this);
            }
            moveRocker(dx * progress + startX, dy * progress + startY);
        }
    }

    /**
     * 将摇杆移动到回归点
     */
    public void moveBack(boolean needSendCmd) {
        int backX = mBgWight / 2;
        int backY = mBgWight / 2;
        if (needSendCmd) {
            mTouchMode = TOUCH_CENTER;
        }
        new AnimToBack().start(mRockerX, mRockerY, backX, backY, 150);
    }
}
