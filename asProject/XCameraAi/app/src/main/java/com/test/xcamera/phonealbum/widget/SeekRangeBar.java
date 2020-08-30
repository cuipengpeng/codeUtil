package com.test.xcamera.phonealbum.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.test.xcamera.R;
import com.test.xcamera.utils.DensityUtils;


public class SeekRangeBar extends View {
    private Paint mRedMarkPaint;
    private Paint mBlueMarkPaint;
    private Paint mBlueMarkBorderPaint;
    private Paint mSelectMarkBorderPaint;
    private Paint mBackgroundPaint;


    /**
     * 圆角矩形弧度
     */
    private float mRectCorner;
    private float mRectCornerTwo;
    /**
     * 待绘制的笔迹矩形区域
     */
    private RectF mMarkRectF = new RectF();
    private RectF mMarkRectBrodF = new RectF();
    private RectF mBackgroundRectF = new RectF();
    private RectF mTitleAndTimeRectF = new RectF();

    /**
     * 左右手柄编辑 start
     */
    private final int thumbLeft = 0;
    private final int thumbRight = 1;


    private final Bitmap thumbImageLeft = BitmapFactory.decodeResource(getResources(), R.mipmap.edit_ic_move_left_normal);
    private final Bitmap thumbImageRight = BitmapFactory.decodeResource(getResources(), R.mipmap.edit_ic_move_right_normal);
    private final Bitmap thumbImageLeftPressed = BitmapFactory.decodeResource(getResources(), R.mipmap.edit_ic_move_left_normal);
    private final Bitmap thumbImageRightPressed = BitmapFactory.decodeResource(getResources(), R.mipmap.edit_ic_move_right_normal);
    private NinePatch mNinePatchLift, mNinePatchLiftPressed, mNinePatchRight, mNinePatchRightPressed;
    private RectF mRect;
    private boolean is_ACTION_UP = false;
    private float linMargin_width;//边框线宽度
    private float paddingMargin;
    private Thumb pressedThumb = null;
    private boolean mIsDragging = false;
    private int mMaxValue = -1;
    private int mMinValue = -1;
    private float mChangStart = 0;
    private float mChangEnd = 0;

    /**
     * 刻度尺移动偏移量
     */
    private float mScrollOffset;


    public SeekRangeBar(Context context) {
        this(context, null);
    }

    public SeekRangeBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mRectCorner = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                2f,
                getResources().getDisplayMetrics());
        int defaultTextSize = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                10f,
                getResources().getDisplayMetrics());
        int defaultTextColor = Color.parseColor("#4DFFFFFF");


        linMargin_width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                2f,
                getResources().getDisplayMetrics());
        paddingMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                16f,
                getResources().getDisplayMetrics());
        initPainters();
    }

    private void initPainters() {
        mRedMarkPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBlueMarkPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBlueMarkBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSelectMarkBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBlueMarkBorderPaint.setColor(Color.parseColor("#00434343"));//每个时间段颜色 描边
        mSelectMarkBorderPaint.setColor(Color.parseColor("#28ffffff"));//每个时间段颜色 描边
        mBackgroundPaint.setColor(Color.parseColor("#00000000"));//整个背景

        mRect = new RectF();
        mNinePatchLift = new NinePatch(thumbImageLeft, thumbImageLeft.getNinePatchChunk(), null);
        mNinePatchLiftPressed = new NinePatch(thumbImageLeftPressed, thumbImageLeftPressed.getNinePatchChunk(), null);
        mNinePatchRight = new NinePatch(thumbImageRight, thumbImageRight.getNinePatchChunk(), null);
        mNinePatchRightPressed = new NinePatch(thumbImageRightPressed, thumbImageRightPressed.getNinePatchChunk(), null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackground(canvas);//背景绘制

        drawMarks(canvas);//时间条绘制
        drawThumbLeftRight(canvas);

    }

    /**
     * 绘制时间线背景
     */
    private void drawBackground(Canvas canvas) {
        final float halfWidth = getHalfWidth();
        float leftmost = (mChangStart ) + halfWidth + mScrollOffset;
        leftmost = Math.max(getPaddingLeft(), leftmost);
        float rightmost = (mChangEnd ) + halfWidth + mScrollOffset;
        rightmost = Math.min(rightmost, getWidth() - getPaddingRight());
        mBackgroundRectF.set(leftmost, 0, rightmost, getHeight());
        canvas.drawRect(mBackgroundRectF, mBackgroundPaint);
    }

    /**
     * 绘制笔迹
     */
    private void drawMarks(Canvas canvas) {
        updateMarkRectF();
        canvas.drawRoundRect(mMarkRectBrodF, mRectCorner, mRectCorner, mSelectMarkBorderPaint);

        canvas.drawRoundRect(mMarkRectBrodF, mRectCorner, mRectCorner, mSelectMarkBorderPaint);
    }

    private float getHalfWidth() {
        final float halfWidth = (getWidth() - getPaddingLeft() - getPaddingRight()) / 2f;
        return halfWidth;
    }

    private void updateMarkRectF() {
        final float halfWidth = getHalfWidth();
        float border = 0;
//        if(!isBorder){
        border = DensityUtils.dp2px(getContext(), 1);
//        }
        float left = (mChangStart ) +
                halfWidth +
                mScrollOffset;
        float right = (mChangEnd ) +
                halfWidth +
                mScrollOffset;
        mMarkRectF.set(left + border, 0 + border, right - border, getHeight() - border);
        mMarkRectBrodF.set(left, 0, right, getHeight());
        Log.i("club", "club_drawThumbLeftRight:updateMarkRectF_left:" + left + " right：" + right);

    }

    /**
     * 画左右手柄
     *
     * @param canvas
     */
    private void drawThumbLeftRight(Canvas canvas) {

        canvas.save();
//        updateMarkRectF(mEditMark,true);

        drawThumb(normalizedToScreenLeft(mChangStart), canvas, thumbLeft);
        drawThumb(normalizedToScreenRight(mChangEnd), canvas, thumbRight);
      /*  List<Mark> visibleMarks = getVisibleMarks();
        mEditMarkPosition=0;
        for (Mark mark : visibleMarks) {
            if(mark.getMarkStatus()==Mark.EDIT){
                mEditMark=mark;
                mStartTimeValue=mark.getStart();
                mEndTimeValue=mark.getEnd();
                drawThumb(normalizedToScreenLeft(mark.getStart()),  canvas,thumbLeft);
                drawThumb(normalizedToScreenRight(mark.getEnd()),  canvas,thumbRight);
                break;
            }
            mEditMarkPosition++;
        }*/

        canvas.restore();
    }

    /**
     * 🌹画两边滑动块
     *
     * @param screenCoord The mX-coordinate in screen space where to draw the image.
     * @param canvas      The canvas to draw upon.
     */
    private void drawThumb(double screenCoord, Canvas canvas, int thumb) {
        Log.i("club", "club_drawThumbLeftRight:screenCoord:" + screenCoord);

        NinePatch np;
        float buttonToDrawWidth = 0;
        int left;
        int right;
        if (thumb == thumbLeft) {
            np = mNinePatchLift;
            buttonToDrawWidth = paddingMargin;
            left = (int) (screenCoord - buttonToDrawWidth + mRectCorner);
            right = (int) (screenCoord - buttonToDrawWidth + mRectCorner) + (int) paddingMargin;

        } else {
            np = mNinePatchRight;
            left = (int) (screenCoord - buttonToDrawWidth - mRectCorner);
            right = (int) (screenCoord - buttonToDrawWidth - mRectCorner) + (int) paddingMargin;
        }
        mRect.top = 0;
        mRect.bottom = getHeight();
        mRect.left = left;
        mRect.right = right;
        Log.i("club:", "club:drawBitmap:top:" + mRect.top
                + " bottom:" + mRect.bottom
                + " left:" + mRect.left
                + " right:" + mRect.right
        );
        np.draw(canvas, mRect);
//        Rect src=new Rect(0,0,(int)paddingMargin,DensityUtils.dp2px(getContext(),38) );
//        canvas.drawBitmap(np,src, mRect, null);


    }

    float lasTx = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (!isEnabled()) {
            return false;
        }
        final int action = event.getAction();

        switch (action & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
                attemptClaimDrag();
                pressedThumb = evalPressedThumb(event.getX());
                Log.i("tag", "ACTION_DOWN pressedThumb" + pressedThumb);
                if (pressedThumb != null) {//两边滑动块
                    lasTx = event.getX();
                    is_ACTION_UP = true;
                    onStartDivideLineTrackingTouch();
                    invalidate();
                } else if (event.getY() < getHeight()) {//整体滚动
                    return super.onTouchEvent(event);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i("tag", "ACTION_MOVE pressedThumb" + pressedThumb);

                if (pressedThumb != null) {//滑动分割线
                    is_ACTION_UP = true;
                    if (mIsDragging) {
                        if (Math.abs(lasTx - event.getX()) > 5) {
                            trackTouchEvent(event);
                        }
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
                is_ACTION_UP = false;
                onStopDivideLineTrackingTouch();
                if (Math.abs(lasTx - event.getX()) > 5) {
                    trackTouchEvent(event);
                } else {
                    invalidate();
                }

                break;
            case MotionEvent.ACTION_POINTER_DOWN:

                break;
            case MotionEvent.ACTION_POINTER_UP:
                invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
                invalidate(); // see above explanation
                break;
        }

        return mIsDragging || super.onTouchEvent(event);

    }

    private boolean trackTouchEvent(MotionEvent event) {

        final float x = event.getX();
        if (Thumb.MIN.equals(pressedThumb)) {
            float start = calculateDragX(x);
            Log.i("club", "club_trackTouchEvent:start=" + start + " mChangStart=" + mChangStart + " x=" + x);
            if (mChangEnd - start > 5) {
                mChangStart = start;
            }
        } else if (Thumb.MAX.equals(pressedThumb)) {
            float end = calculateDragX(x);
            if (end - mChangStart > 5) {
                mChangEnd = end;
            }

            Log.i("club", "club_trackTouchEvent:end=" + end + " mChangEnd=" + mChangEnd + "  x=" + x);

        } else {
            Log.i("else", "pointerIndex =" + x);
        }
        invalidate();
//        Log.i("club","club_trackTouchEvent:Mark;getStart="+mEditMark.getStart()+"getEnd="+mEditMark.getEnd());
        if (mOnSeekRangeCallBack != null) {
            mOnSeekRangeCallBack.onChangCallBack(is_ACTION_UP, mChangStart, mChangEnd);
        }
        return true;

    }

    /**
     * 计算手柄滑动距离
     *
     * @return
     */
    private float calculateDragX(float x) {
        final float halfWidth = getHalfWidth();
        float leftBound = (x - halfWidth) - mScrollOffset;
        leftBound = (float) Math.max(mMinValue, Math.min(leftBound, mMaxValue));
        return leftBound;
    }

    private Thumb evalPressedThumb(float touchX) {
        Thumb result = null;
        boolean minThumbPressed = isInMinThumbRange(touchX, mChangStart);
        boolean maxThumbPressed = isInMaxThumbRange(touchX, mChangEnd);

        if (minThumbPressed && maxThumbPressed) {
            if (touchX <= normalizedToScreenLeft(mChangStart)) {
                result = Thumb.MIN;
            } else if (touchX >= normalizedToScreenRight(mChangEnd)) {
                result = Thumb.MAX;
            } else {
                result = (touchX / getWidth() > 0.5f) ? Thumb.MIN : Thumb.MAX;

            }
            // if both thumbs are pressed (they lie on top of each other), choose the one with more room to drag. this avoids "stalling" the thumbs in a corner, not being able to drag them apart anymore.
        } else if (minThumbPressed) {
            result = Thumb.MIN;
        } else if (maxThumbPressed) {
            result = Thumb.MAX;
        }
        if (result != null) {
            if (Thumb.MIN.equals(result)) {

            }
        }


        return result;
    }

    /**
     * 触摸开始滑动
     */
    void onStartDivideLineTrackingTouch() {
        mIsDragging = true;
    }

    /**
     * 停止触摸滑动
     */
    void onStopDivideLineTrackingTouch() {
        mIsDragging = false;
    }

    private boolean isInMinThumbRange(float touchX, double normalizedThumbValue) {
        double value = Math.abs(touchX - normalizedToScreenLeft(normalizedThumbValue));
        return value <= 2 * paddingMargin;

    }

    private boolean isInMaxThumbRange(float touchX, double normalizedThumbValue) {
        double value = Math.abs(touchX - normalizedToScreenRight(normalizedThumbValue));

        return value <= 2 * paddingMargin;
    }

    private double normalizedToScreenLeft(double normalizedCoord) {
        final float halfWidth = getHalfWidth();
        float left = (float) ((normalizedCoord ) +
                halfWidth +
                mScrollOffset);
        Log.i("club", "club_drawThumbLeftRight:updateMarkRectF_" + " left：" + left);

        return left;
    }

    private double normalizedToScreenRight(double normalizedCoord) {
        final float halfWidth = getHalfWidth();

        float right = (float) ((Math.min(normalizedCoord, mMaxValue) ) +
                halfWidth +
                mScrollOffset);
        Log.i("club", "club_drawThumbLeftRight:updateMarkRectF_" + " right：" + right);

        return right;
    }

    /**
     * 区间开始
     *
     * @param mChangStart
     */
    public void setChangStart(float mChangStart) {
        this.mChangStart = mChangStart;
    }

    /**
     * 区间结束
     *
     * @param mChangEnd
     */
    public void setChangEnd(float mChangEnd) {
        this.mChangEnd = mChangEnd;
    }

    /**
     * 设置最大值
     *
     * @param max
     */
    public void setScaleList(int max) {
        mMinValue = 0;
        mMaxValue = max;
        invalidate();
    }

    public void selectScale( float offset) {
        mScrollOffset = offset;
        invalidate();
    }

    /**
     * 防止父控件事件拦截
     */
    private void attemptClaimDrag() {
        if (getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
    }

    private enum Thumb {
        MIN, MAX
    }

    private OnSeekRangeCallBack mOnSeekRangeCallBack;

    public void setOnSeekRangeCallBack(OnSeekRangeCallBack mOnSeekRangeCallBack) {
        this.mOnSeekRangeCallBack = mOnSeekRangeCallBack;
    }

    public interface OnSeekRangeCallBack {
        void onChangCallBack(boolean isUp, float start, float end);
    }
}
