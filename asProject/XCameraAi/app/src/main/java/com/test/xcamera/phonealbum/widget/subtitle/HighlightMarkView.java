package com.test.xcamera.phonealbum.widget.subtitle;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;


import com.test.xcamera.R;
import com.test.xcamera.phonealbum.widget.subtitle.bean.Mark;
import com.test.xcamera.utils.DensityUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by club
 * 绘制所有的时间点
 */

public class HighlightMarkView extends View implements OnScrollListener, DataProtocol {
    private static final String TAG = HighlightMarkView.class.getSimpleName();
    private Paint mRedMarkPaint;
    private Paint mBlueMarkPaint;
    private Paint mBlueMarkBorderPaint;
    private Paint mSelectMarkBorderPaint;
    private Paint mBackgroundPaint;
    private TextPaint mTitlePaint;
    private TextPaint mTimePaint;
    private TextPaint mCategoryTextPaint;

    private Bitmap mBitmap;
    private CharSequence mText;
    private int mTextSize;
    private int mTextColor;

    private int mMaxValue = -1;
    private int mMinValue = -1;
    /**
     * 屏幕正中指针值
     */
    private int mCurrentValue;
    /**
     * 刻度尺移动偏移量
     */
    private float mScrollOffset;
    /**
     * 刻度宽度
     */
    private int mLineDividerWidth;
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
     * 有效触摸区域界限（有效指触摸的是笔迹区域）
     */
    private Bounds mBounds = new Bounds();

    private List<Mark> mDataList = new ArrayList<>();
    private List<Mark> mVisibleMarkList = new ArrayList<>();

    private float mTitleMarginTop;
    private float mTimeMarginTop;
    private float mTitleAndTimeHorizontalPadding;
    private DecimalFormat mFormatter = new DecimalFormat("#.#");
    /**
     * 左右手柄编辑 start
     */
    private final int thumbLeft = 0;
    private final int thumbRight = 1;

    private double mStartTimeValue = -1;
    private double mEndTimeValue = -1;
    private final Bitmap thumbImageLeft = BitmapFactory.decodeResource(getResources(), R.mipmap.edit_ic_move_left_normal);
    private final Bitmap thumbImageRight = BitmapFactory.decodeResource(getResources(), R.mipmap.edit_ic_move_right_normal);
    private final Bitmap thumbImageLeftPressed = BitmapFactory.decodeResource(getResources(), R.mipmap.edit_ic_move_left_normal);
    private final Bitmap thumbImageRightPressed = BitmapFactory.decodeResource(getResources(), R.mipmap.edit_ic_move_right_normal);
    private NinePatch mNinePatchLift, mNinePatchLiftPressed, mNinePatchRight, mNinePatchRightPressed;
    private RectF mRect;
    private boolean is_ACTION_UP = false;
    private boolean is_ACTION_Down = true;
    private float linMargin_width;//边框线宽度
    private float paddingMargin;
    private Thumb pressedThumb = null;
    private boolean mIsDragging = false;
    private Mark mEditMark;
    private int mEditMarkPosition = 0;


    /**
     * 左右手柄编辑 end
     */
    public HighlightMarkView(Context context) {
        this(context, null);
    }

    public HighlightMarkView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mLineDividerWidth = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                5f,
                getResources().getDisplayMetrics());
        mRectCorner = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                2f,
                getResources().getDisplayMetrics());
        int defaultTextSize = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                10f,
                getResources().getDisplayMetrics());
        int defaultTextColor = Color.parseColor("#4DFFFFFF");
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.HighlightMarkView);
        mTextSize = array.getDimensionPixelSize(
                R.styleable.HighlightMarkView_android_textSize,
                defaultTextSize);
        mTextColor = array.getColor(
                R.styleable.HighlightMarkView_android_textColor,
                defaultTextColor);
        mText = array.getText(R.styleable.HighlightMarkView_android_text);
        Drawable drawable = array.getDrawable(R.styleable.HighlightMarkView_android_src);
        if (drawable != null) {
            mBitmap = BitmapUtil.drawableToBitmap(drawable);
        }
        array.recycle();
        mTitleMarginTop = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                9f,
                getResources().getDisplayMetrics());
        mTimeMarginTop = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                23f,
                getResources().getDisplayMetrics());
        mTitleAndTimeHorizontalPadding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                4f,
                getResources().getDisplayMetrics());
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
        mCategoryTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTitlePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTimePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mRedMarkPaint.setColor(Color.parseColor("#4DF61A1A"));
        mBlueMarkPaint.setColor(Color.parseColor("#262626"));//每个时间段颜色


        mBlueMarkBorderPaint.setColor(Color.parseColor("#434343"));//每个时间段颜色 描边
//        mBlueMarkBorderPaint.setStrokeWidth(DensityUtils.dp2px(getContext(),1));              //线宽
//        mBlueMarkBorderPaint.setAntiAlias(true);
//        mBlueMarkBorderPaint.setStyle(Paint.Style.STROKE);

        mSelectMarkBorderPaint.setColor(Color.parseColor("#ffffff"));//每个时间段颜色 描边
//        mSelectMarkBorderPaint.setStrokeWidth(DensityUtils.dp2px(getContext(),1));              //线宽
//        mSelectMarkBorderPaint.setAntiAlias(true);
//        mSelectMarkBorderPaint.setStyle(Paint.Style.STROKE);

        mBackgroundPaint.setColor(Color.parseColor("#00000000"));//整个背景
        mTitlePaint.setColor(Color.parseColor("#80ffffff"));
        mTimePaint.setColor(Color.parseColor("#80ffffff"));
        mCategoryTextPaint.setColor(mTextColor);
        mCategoryTextPaint.setTextSize(mTextSize);
        mTitlePaint.setTextSize(
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_SP, 10f, getResources().getDisplayMetrics()));
        mTimePaint.setTextSize(
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_SP, 8f, getResources().getDisplayMetrics()));
        mRect = new RectF();
        mNinePatchLift = new NinePatch(thumbImageLeft, thumbImageLeft.getNinePatchChunk(), null);
        mNinePatchLiftPressed = new NinePatch(thumbImageLeftPressed, thumbImageLeftPressed.getNinePatchChunk(), null);
        mNinePatchRight = new NinePatch(thumbImageRight, thumbImageRight.getNinePatchChunk(), null);
        mNinePatchRightPressed = new NinePatch(thumbImageRightPressed, thumbImageRightPressed.getNinePatchChunk(), null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        drawCategory(canvas);//分类绘制暂时不需要
        drawBackground(canvas);//背景绘制

        drawMarks(canvas);//时间条绘制
        drawTitleAndTime(canvas);//文字绘制
        drawThumbLeftRight(canvas);

    }

    private void drawCategory(Canvas canvas) {
        final float halfWidth = (getWidth() - getPaddingLeft() - getPaddingRight()) / 2f;
        float horizontalPadding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                40f,
                getResources().getDisplayMetrics());
        float left = -mCurrentValue * mLineDividerWidth - horizontalPadding + halfWidth + mScrollOffset;
        float verticalPadding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                1f,
                getResources().getDisplayMetrics());
        float offset = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                1f,
                getResources().getDisplayMetrics());
        if (left - offset < 0) {
            return;
        }
        float top = 0;
        canvas.drawBitmap(mBitmap, left, top, null);
        Paint.FontMetrics fontMetrics = mCategoryTextPaint.getFontMetrics();
        float textSize = -fontMetrics.ascent - fontMetrics.descent;
        top += mBitmap.getHeight() + fontMetrics.leading - fontMetrics.top + verticalPadding;
        offset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                2f,
                getResources().getDisplayMetrics());
        if (mText.toString().length() > 2) {
            left -= TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    5f,
                    getResources().getDisplayMetrics());
        }
        canvas.drawText(mText.toString(), left + offset, top, mCategoryTextPaint);
    }

    private void drawTitleAndTime(Canvas canvas) {
        for (Mark mark : getVisibleMarks()) {
            updateTitleAndTimeRectF(mark);
            canvas.save();
            canvas.clipRect(mTitleAndTimeRectF);
            Rect bounds = new Rect();
            mTitlePaint.getTextBounds(mark.getTitle(), 0, mark.getTitle().length(), bounds);
            float textLength = bounds.width();
            float totalLength = mark.getDuration() * mLineDividerWidth;
            Log.d(TAG, "textLength: " + textLength + ", totalLength: " + totalLength + ", duration: " + mark.getDuration() + ", mLineDividerWidth: " + mLineDividerWidth);
            String title;
            if (textLength >= totalLength - 2 * mTitleAndTimeHorizontalPadding) {
                int len = (int) (totalLength / mTitlePaint.getTextSize() - 1);
                len = Math.max(1, len);
                title = mark.getTitle().substring(0,
                        Math.min(len - 1, mark.getTitle().length())) + "...";
            } else {
                title = mark.getTitle();
            }
            canvas.drawText(title,
                    mTitleAndTimeRectF.left,
                    mTitleMarginTop + mTitlePaint.getTextSize(),
                    mTitlePaint);
            float durationSecs = mark.getDuration() / 10f;
            if (durationSecs > 60) {
                canvas.drawText(
                        TimeUtils.getDurationMillisecond((int) (durationSecs * 1000f)),
                        mTitleAndTimeRectF.left,
                        mTimeMarginTop + mTimePaint.getTextSize(), mTimePaint);
            } else {
                canvas.drawText(
                        mFormatter.format(durationSecs) + "秒",
                        mTitleAndTimeRectF.left,
                        mTimeMarginTop + mTimePaint.getTextSize(), mTimePaint);
            }
            canvas.restore();
        }
    }

    /**
     * 绘制时间线背景
     */
    private void drawBackground(Canvas canvas) {
        final float halfWidth = (getWidth() - getPaddingLeft() - getPaddingRight()) / 2f;
        float leftmost = (mMinValue - mCurrentValue) * mLineDividerWidth + halfWidth + mScrollOffset;
        leftmost = Math.max(getPaddingLeft(), leftmost);
        float rightmost = (mMaxValue - mCurrentValue) * mLineDividerWidth + halfWidth + mScrollOffset;
        rightmost = Math.min(rightmost, getWidth() - getPaddingRight());
        mBackgroundRectF.set(leftmost, 0, rightmost, getHeight());
        canvas.drawRect(mBackgroundRectF, mBackgroundPaint);
    }

    /**
     * 绘制笔迹
     */
    private void drawMarks(Canvas canvas) {
        List<Mark> visibleMarks = getVisibleMarks();
        for (Mark mark : visibleMarks) {
            updateMarkRectF(mark, false);
            if (!mark.isDraw()) {
                continue;
            }

            if (mark.getType() == Mark.MARK_TYPE_RED) {
                canvas.drawRoundRect(mMarkRectF, mRectCorner, mRectCorner, mRedMarkPaint);
//                canvas.drawRoundRect(mMarkRectF, mRectCorner, mRectCorner, mBlueMarkBorderPaint);
            } else {
                if (mEditMark != null && mEditMark.getId().equals(mark.getId())) {
                    canvas.drawRoundRect(mMarkRectBrodF, mRectCorner, mRectCorner, mSelectMarkBorderPaint);
                } else {
                    canvas.drawRoundRect(mMarkRectBrodF, mRectCorner, mRectCorner, mBlueMarkBorderPaint);
                }
                canvas.drawRoundRect(mMarkRectF, mRectCorner, mRectCorner, mBlueMarkPaint);
            }
        }
    }

    /**
     * 画左右手柄
     *
     * @param canvas
     */
    private void drawThumbLeftRight(Canvas canvas) {
        if (mEditMark == null) {
            return;
        }
        canvas.save();
//        updateMarkRectF(mEditMark,true);

        drawThumb(normalizedToScreenLeft(mEditMark.getStart()), canvas, thumbLeft);
        drawThumb(normalizedToScreenRight(mEditMark.getEnd()), canvas, thumbRight);
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

    private void initTimeValue() {
        List<Mark> visibleMarks = mDataList;
        mEditMarkPosition = 0;
        for (Mark mark : visibleMarks) {
            if (mark.getMarkStatus() == Mark.EDIT) {
                mEditMark = mark;
                if (is_ACTION_Down) {
                    mStartTimeValue = mark.getStart();
                    mEndTimeValue = mark.getEnd();
                }

                break;
            }
            mEditMarkPosition++;
        }
        if (mEditMark == null) {
            return;
        }
        try {
            if (is_ACTION_Down) {
                double min = getApproximateMin(mEditMark.getStart(), mDataList);
                double max = getApproximateMax(mEditMark.getEnd(), mDataList);
                mStartTimeValue = min;
                mEndTimeValue = max;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
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

    /**
     * 找到屏幕上的可视笔迹
     *
     * @return
     */
    private List<Mark> getVisibleMarks() {
        final List<Mark> result = mVisibleMarkList;
        result.clear();
        if (mDataList == null) {
            return result;
        }
        final float halfWidth = (getWidth() - getPaddingLeft() - getPaddingRight()) / 2f;
        final int halfCount = (int) Math.ceil(halfWidth / mLineDividerWidth);
        final int currentValue = mCurrentValue;
        int startValue = currentValue - halfCount > mMinValue ? currentValue - halfCount : mMinValue;
        int endValue = currentValue + halfCount < mMaxValue ? currentValue + halfCount : mMaxValue;
        for (Mark mark : mDataList) {
            if ((mark.getEnd() > startValue && mark.getEnd() <= endValue) ||
                    (mark.getStart() >= startValue && mark.getStart() < endValue) ||
                    (mark.getStart() <= startValue && mark.getEnd() >= endValue)) {
                result.add(mark);
            }
        }
        return result;
    }

    private void updateTitleAndTimeRectF(Mark mark) {
        final float halfWidth = (getWidth() - getPaddingLeft() - getPaddingRight()) / 2f;
        float left = (mark.getStart() - mCurrentValue) * mLineDividerWidth +
                halfWidth +
                mScrollOffset;
        mTitleAndTimeRectF.set(left + mTitleAndTimeHorizontalPadding,
                0,
                left + mark.getHeadLength() * mLineDividerWidth - mTitleAndTimeHorizontalPadding,
                getHeight());
    }

    private void updateMarkRectF(Mark mark, boolean isBorder) {
        final float halfWidth = (getWidth() - getPaddingLeft() - getPaddingRight()) / 2f;
        float border = 0;
//        if(!isBorder){
        border = DensityUtils.dp2px(getContext(), 1);
//        }
        float left = (mark.getStart() - mCurrentValue) * mLineDividerWidth +
                halfWidth +
                mScrollOffset;
        float right = (Math.min(mark.getEnd(), mMaxValue) - mCurrentValue) * mLineDividerWidth +
                halfWidth +
                mScrollOffset;
        mMarkRectF.set(left + border, 0 + border, right - border, getHeight() - border);
        mMarkRectBrodF.set(left, 0, right, getHeight());
        Log.i("club", "club_drawThumbLeftRight:updateMarkRectF_left:" + left + " right：" + right);

    }

    public float transformScreenPositionIntoScaleValue(float touchX) {
        float offset = mScrollOffset / mLineDividerWidth;
        final float halfWidth = (getWidth() - getPaddingLeft() - getPaddingRight()) / 2f;
        return mCurrentValue - offset + (touchX - halfWidth) / mLineDividerWidth;
    }

    public float transformScaleValueIntoScreenPosition(float scaleValue) {
        final float halfWidth = (getWidth() - getPaddingLeft() - getPaddingRight()) / 2f;
        return (scaleValue - mCurrentValue) * mLineDividerWidth + halfWidth + mScrollOffset;
    }

    private Mark findMarkInTouch(float scaleValue) {
        if (mVisibleMarkList == null) {
            return null;
        }
        Mark result = null;
        float f = Float.MAX_VALUE;
        for (Mark mark : mVisibleMarkList) {
            float start = mark.getStart();
            float end = mark.getEnd();
            if (scaleValue >= mark.getStart() && scaleValue <= mark.getEnd()) {
                if (f > scaleValue - mark.getStart()) {
                    f = Math.min(scaleValue - mark.getStart(), f);
                    result = mark;
                }
            }
        }
        return result;
    }

    private List<Mark> findOverlappedMarkList(float scaleValue) {
        List<Mark> overlapped = new ArrayList<>();
        if (mVisibleMarkList == null) {
            return overlapped;
        }
        for (Mark mark : mVisibleMarkList) {
            if (scaleValue >= mark.getStart() && scaleValue <= mark.getEnd()) {
                overlapped.add(mark);
            }
        }
        return overlapped;
    }

    @Override
    public void onScroll(float scrollOffset, int currentValue) {
        mScrollOffset = scrollOffset;
        mCurrentValue = currentValue;
        invalidate();
    }

    @Override
    public void onFinished(float scrollOffset, int currentValue) {
        mScrollOffset = scrollOffset;
        mCurrentValue = currentValue;
        invalidate();
    }

    @Override
    public void setScaleList(int max) {
        mMinValue = 0;
        mMaxValue = max;
        invalidate();
    }


    @Override
    public void setScaleWidth(int width) {
        mLineDividerWidth = width;
    }

    @Override
    public void selectScale(int currentValue, float offset) {
        if (currentValue < mMinValue) {
            currentValue = mMinValue;
        } else if (currentValue > mMaxValue) {
            currentValue = mMaxValue;
        }
        mCurrentValue = currentValue;
        mScrollOffset = offset;
        invalidate();
    }

    public Bounds getMarkBounds(float touchX, float touchY) {
        final int top = getTop();
        final int bottom = getBottom();
        if (touchY < top || touchY > bottom) {
            return null;
        }
        float scaleValue = transformScreenPositionIntoScaleValue(touchX);
        Mark mark = findMarkInTouch(scaleValue);
        if (mark == null) {
            return null;
        }
        mBounds.left = transformScaleValueIntoScreenPosition(mark.getStart());
        mBounds.top = top;
        mBounds.right = transformScaleValueIntoScreenPosition(mark.getEnd());
        mBounds.bottom = bottom;
        mBounds.destMark = mark;
        mBounds.originMark = mark.copy();
        mBounds.overlapped = findOverlappedMarkList(scaleValue);
        mBounds.trackId = Mark.TRACK_MIDDLE;

//        switch (getId()) {
//            case R.id.highlight_mark_view_top:
//                mBounds.trackId = Mark.TRACK_TOP;
//                break;
//            case R.id.highlight_mark_view_middle:
//                mBounds.trackId = Mark.TRACK_MIDDLE;
//                break;
//            case R.id.highlight_mark_view_bottom:
//                mBounds.trackId = Mark.TRACK_BOTTOM;
//                break;
//            default:
//                mBounds.trackId = -1;
//        }
        return mBounds;
    }

    public void removeMark(Mark mark) {
        if (mVisibleMarkList != null) {
            mVisibleMarkList.remove(mark);
        }
        if (mDataList != null) {
            mDataList.remove(mark);
        }
        invalidate();
    }

    public void addMark(Mark mark) {
        if (mVisibleMarkList != null) {
            mVisibleMarkList.add(mark);
        }
        if (mDataList != null) {
            mDataList.add(mark);
        }
        invalidate();
    }

    public void removeAll() {
        if (mVisibleMarkList != null) {
            mVisibleMarkList.clear();
        }
        if (mDataList != null) {
            mDataList.clear();
        }
        invalidate();
    }

    public void addAll(List<? extends Mark> markList) {
        if (markList == null) {
            return;
        }
        if (mDataList != null) {
            mEditMark = null;
            mDataList.clear();
            mDataList.addAll(markList);
        }
        initTimeValue();
        invalidate();
    }

    public List<Mark> getVisibleMarkList() {
        return mVisibleMarkList;
    }

    private void clearBounds() {
        mBounds.left = 0;
        mBounds.top = 0;
        mBounds.right = 0;
        mBounds.bottom = 0;
        mBounds.destMark = null;
        mBounds.trackId = -1;
    }

    public float[] getFirstMarkLoc() {
        float[] loc = new float[2];
        loc[0] = mMarkRectF.left + mMarkRectF.width() / 2;
        loc[1] = mMarkRectF.top + mMarkRectF.height() / 2;
        return loc;
    }

    float lasTx = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mEditMark == null) {
            return super.onTouchEvent(event);
        }
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
                    is_ACTION_Down = true;
                    onStartDivideLineTrackingTouch();
                    invalidate();
                } else if (event.getY() < getHeight()) {//整体滚动
                    return super.onTouchEvent(event);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i("tag", "ACTION_MOVE pressedThumb" + pressedThumb);

                if (pressedThumb != null) {//滑动分割线
                    is_ACTION_Down = false;
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
                is_ACTION_Down = true;
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
        if (mEditMark == null) {
            return false;
        }
        final float x = event.getX();
        Log.i("club", "club_trackTouchEvent:mStartTimeValue=" + mStartTimeValue + "mEndTimeValue=" + mEndTimeValue + " x=" + x);

        if (Thumb.MIN.equals(pressedThumb)) {
            float start = calculateDragX(x);
            Log.i("club", "club_trackTouchEvent:start=" + start + "paddingMargin=" + paddingMargin + " x=" + x);
            if (mEditMark.getEnd() - start > 5) {
                mEditMark.setStart(start);
                mEditMark.setStartPositionInClip(start);
            }
        } else if (Thumb.MAX.equals(pressedThumb)) {
            float end = calculateDragX(x);
            if (end - mEditMark.getStart() > 5) {
                mEditMark.setEnd(end);
                mEditMark.setEndPositionInClip(end);
            }

            Log.i("club", "club_trackTouchEvent:start=" + end + "paddingMargin=" + paddingMargin + " x=" + x);

        } else {
            Log.i("else", "pointerIndex =" + x);
        }
//        Log.i("club","club_trackTouchEvent:Mark;getStart="+mEditMark.getStart()+"getEnd="+mEditMark.getEnd());
        Log.i("club", "club_trackTouchEvent----------------------------------------------------------------------------------------------------------------" + mEditMark.getStart() + "getEnd=" + mEditMark.getEnd());

//        mVisibleMarkList.set(mEditMarkPosition,mEditMark);
        if (mOnMarkEditCut != null) {
            mOnMarkEditCut.onMarkEditCut(is_ACTION_UP, mEditMarkPosition, mEditMark);
        }
        return true;

    }

    /**
     * 计算手柄滑动距离
     *
     * @return
     */
    private float calculateDragX(float x) {
        final float halfWidth = (getWidth() -
                getPaddingLeft() -
                getPaddingRight()) / 2f;
        float leftBound = (x - halfWidth) / mLineDividerWidth + mCurrentValue;
        leftBound = (float) Math.max(mStartTimeValue, Math.min(leftBound, mEndTimeValue));
        return leftBound;
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


    private Thumb evalPressedThumb(float touchX) {
        Thumb result = null;
        boolean minThumbPressed = isInMinThumbRange(touchX, mEditMark.getStart());
        boolean maxThumbPressed = isInMaxThumbRange(touchX, mEditMark.getEnd());

        if (minThumbPressed && maxThumbPressed) {
            if (touchX <= normalizedToScreenLeft(mEditMark.getStart())) {
                result = Thumb.MIN;
            } else if (touchX >= normalizedToScreenRight(mEditMark.getEnd())) {
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

    private boolean isInMinThumbRange(float touchX, double normalizedThumbValue) {
        double value = Math.abs(touchX - normalizedToScreenLeft(normalizedThumbValue));
        return value <= 2 * paddingMargin;

    }

    private boolean isInMaxThumbRange(float touchX, double normalizedThumbValue) {
        double value = Math.abs(touchX - normalizedToScreenRight(normalizedThumbValue));

        return value <= 2 * paddingMargin;
    }

    private double normalizedToScreenLeft(double normalizedCoord) {
        final float halfWidth = (getWidth() - getPaddingLeft() - getPaddingRight()) / 2f;
        float left = (float) ((normalizedCoord - mCurrentValue) * mLineDividerWidth +
                halfWidth +
                mScrollOffset);
        Log.i("club", "club_drawThumbLeftRight:updateMarkRectF_" + " left：" + left);

        return left;
    }

    private double normalizedToScreenRight(double normalizedCoord) {
        final float halfWidth = (getWidth() - getPaddingLeft() - getPaddingRight()) / 2f;

        float right = (float) ((Math.min(normalizedCoord, mMaxValue) - mCurrentValue) * mLineDividerWidth +
                halfWidth +
                mScrollOffset);
        Log.i("club", "club_drawThumbLeftRight:updateMarkRectF_" + " right：" + right);

        return right;
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

    /**
     * 获取接近最小值
     *
     * @param start
     * @param marks
     * @return
     */
    private synchronized double getApproximateMin(double start, List<Mark> marks) {
        if (marks == null && marks.size() == 0) {
            return mMinValue;
        }
        if (marks.size() == 1) {
            return mMinValue;
        }
        List<Double> list = new ArrayList();
        for (int i = 0; i < marks.size(); i++) {
            list.add(new Double(marks.get(i).getEnd()));
        }
        Collections.sort(list);
        Collections.reverse(list);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).doubleValue() <= start) {
                return list.get(i).doubleValue();
            }
        }
        return mMinValue;
      /*  boolean tag=false;

        double minDifference = Math.abs(marks.get(0).getEnd() - start);
        int minIndex = 0;
        for (int i = 1; i < marks.size(); i++) {
            double temp = Math.abs(marks.get(i).getEnd() - start);
            if (temp < minDifference&&marks.get(i).getEnd()<start) {
                minIndex = i;
                minDifference = temp;
                tag=true;
            }
        }
        if(tag){
            return marks.get(minIndex).getEnd();

        }else {
            return mMinValue;
        }*/
    }

    /**
     * 获取接近最大值
     *
     * @param end
     * @param marks
     * @return
     */
    private synchronized double getApproximateMax(double end, List<Mark> marks) {
        if (marks == null && marks.size() == 0) {
            return mMaxValue;
        }
        if (marks.size() == 1) {
            return mMaxValue;
        }
        List<Double> list = new ArrayList();
        for (int i = 0; i < marks.size(); i++) {
            list.add(new Double(marks.get(i).getStart()));
        }
        Collections.sort(list);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).doubleValue() >= end) {
                return list.get(i).doubleValue();
            }
        }
        return mMaxValue;
        /*double minDifference = Math.abs(marks.get(0).getStart() - end);
        int minIndex = 0;
        boolean tag=false;
        for (int i = 1; i < marks.size(); i++) {
            double temp = Math.abs(marks.get(i).getStart() - end);
            if (temp < minDifference&&marks.get(i).getStart()>end) {
                minIndex = i;
                minDifference = temp;
                tag=true;
            }
        }
        if(tag){
            return marks.get(minIndex).getStart();

        }else {
            return mMaxValue;

        }*/
    }

    private OnMarkEditCut mOnMarkEditCut;

    public void setOnMarkEditCut(OnMarkEditCut mOnMarkEditCut) {
        this.mOnMarkEditCut = mOnMarkEditCut;
    }

    public interface OnMarkEditCut {
        void onMarkEditCut(boolean isDown, int position, Mark mark);
    }
}
