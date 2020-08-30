package com.test.xcamera.phonealbum.subtitle;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.test.xcamera.application.AiCameraApplication;
import com.test.xcamera.phonealbum.widget.subtitle.bean.Mark;
import com.test.xcamera.phonealbum.widget.subtitle.bean.TitleSubtitleMark;
import com.test.xcamera.utils.DensityUtils;


/**
 * Created by liutao on 26/04/2017.
 */

public class SubtitleView extends FrameLayout {

    private static final String TAG = SubtitleView.class.getSimpleName();

    private StyledTextView mTextView;
    private ImageView mDeleteView;
    private ImageView mEditView;
    private Paint mBorderPaint;
    private Path mPath;
    private Mark mMark;

    private Handler mEventHandler = new Handler(Looper.getMainLooper());

    private Runnable mSingleClickTask = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "Single Tap");
            if (mOnEditSubtitleListener != null) {
                mOnEditSubtitleListener.onSingleTapMovieSubtitle(SubtitleView.this);
            }
        }
    };

    private OnEditSubtitleListener mOnEditSubtitleListener;

    public void setOnEditSubtitleListener(OnEditSubtitleListener listener) {
        mOnEditSubtitleListener = listener;
    }

    public SubtitleView(@NonNull Context context) {
        this(context, null);
    }

    public SubtitleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
        mPath = new Path();
        mBorderPaint = new Paint();
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setColor(ContextCompat.getColor(getContext(), android.R.color.white));
        mBorderPaint.setStyle(Paint.Style.STROKE);
        float dash = DensityUtils.dp2px(AiCameraApplication.getContext(),2f);
        mBorderPaint.setPathEffect(new DashPathEffect(new float[]{dash, dash}, 0));
        mBorderPaint.setStrokeWidth(1);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTextView = (StyledTextView) getChildAt(0);
        mDeleteView = (ImageView) getChildAt(1);
        mEditView = (ImageView) getChildAt(2);
        if (isSelected()) {
            mDeleteView.setVisibility(VISIBLE);
            mEditView.setVisibility(VISIBLE);
        } else {
            mDeleteView.setVisibility(INVISIBLE);
            mEditView.setVisibility(INVISIBLE);
        }
        mDeleteView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisibility(INVISIBLE);
                Log.d(TAG, "Delete subtitle");
                if (mOnEditSubtitleListener != null) {
                    mOnEditSubtitleListener.onDeleteMovieSubtitle(SubtitleView.this);
                }
            }
        });
        final GestureDetector gestureDetector = new GestureDetector(getContext(),
                new GestureDetector.SimpleOnGestureListener() {

                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        Log.d(TAG, "Double Tap");
                        mEventHandler.removeCallbacks(mSingleClickTask);
                        if (mOnEditSubtitleListener != null) {
                            mOnEditSubtitleListener.onDoubleTapMovieSubtitle(SubtitleView.this);
                        }
                        return super.onDoubleTap(e);
                    }

                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        mEventHandler.postDelayed(mSingleClickTask, 200);
                        return super.onSingleTapUp(e);
                    }

                    @Override
                    public boolean onDown(MotionEvent e) {
                        return true;
                    }
                });
        mTextView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
    }



    @Override
    protected void onDraw(Canvas canvas) {
        if (isSelected()) {
            drawBorder(canvas);
        }
        super.onDraw(canvas);
    }

    private void drawBorder(Canvas canvas) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mTextView.getLayoutParams();
        float offset = params.getMarginStart() + getPaddingStart();
        float bottomOffset = params.bottomMargin;
        float x1 = offset;
        float y1 = offset;
        // match parent
        float x2 = getWidth() - offset;
        float y2 = offset;
        float x3 = x1;
        // height wrap content
        float y3 = getHeight() - bottomOffset;
        float x4 = x2;
        float y4 = y3;

        mPath.rewind();
        mPath.moveTo(x1, y1);
        mPath.lineTo(x2, y2);
        canvas.drawPath(mPath, mBorderPaint);

        mPath.moveTo(x1, y1);
        mPath.lineTo(x3, y3);
        canvas.drawPath(mPath, mBorderPaint);

        mPath.moveTo(x2, y2);
        mPath.lineTo(x4, y4);
        canvas.drawPath(mPath, mBorderPaint);

        mPath.rewind();
        mPath.moveTo(x3, y3);
        mPath.lineTo(x4, y4);
        canvas.drawPath(mPath, mBorderPaint);
    }

    @Override
    public void setVisibility(int visibility) {
        if (visibility != VISIBLE) {
            mTextView.reset();
        }
        super.setVisibility(visibility);
    }

    public void setText(String text) {
        if (TextUtils.isEmpty(text)) {
            mTextView.setText("");
        } else {
            mTextView.setText(text);
        }
    }

    public void setTextStyle(TextStyle style) {
        if (style == null) {
            mTextView.reset();
            return;
        }
        mTextView.setTextStyle(style);
    }

    public void setTextColor(String color, float lightness) {
//        if (color == null || mTextView == null) {
//            return;
//        }
//        try {
//            int c = Color.parseColor(color);
//            String strColor = Utils.getHexString(Utils.colorAtLightness(c, lightness), false);
//            mTextView.setTextColor(Color.parseColor(strColor));
//        } catch (Exception e) {
//            mTextView.setTextColor(Color.WHITE);
//        }
    }

    public void setAlignment(Layout.Alignment alignment) {
        if (alignment == null) {
            mTextView.setTextAlignment(TEXT_ALIGNMENT_CENTER);
            return;
        }
        if (alignment == Layout.Alignment.ALIGN_CENTER) {
            mTextView.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        } else if (alignment == Layout.Alignment.ALIGN_NORMAL) {
            mTextView.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
        } else if (alignment == Layout.Alignment.ALIGN_OPPOSITE) {
            mTextView.setTextAlignment(TEXT_ALIGNMENT_VIEW_END);
        } else {
            mTextView.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        }
    }

    public void setTypeface(Typeface typeface) {
        if (typeface == null) {
            return;
        }
        mTextView.setTypeface(typeface);
    }

    public void setStartColor(String color) {
        if (color == null) {
            return;
        }
        mTextView.setStartColor(color);
    }

    public void setMark(final TitleSubtitleMark mark) {
//        if (mark == null) {
//            setVisibility(INVISIBLE);
//            mMark = null;
//            return;
//        }
//        if (mMark != null && mMark.equals(mark)) {
//            return;
//        }
//        setVisibility(VISIBLE);
//        setText(mark.getText());
//        setTextStyle(mark.getTextStyle());
//        setAlignment(mark.getAlignment());
//        boolean isItalic;
//        if (mark.getTextStyle() != null) {
//            isItalic = mark.getTextStyle().isItalic();
//        } else {
//            isItalic = false;
//        }
//        setTypeface(FontsManager.getInstance().getFont(mark.getFontId()), isItalic);
//        int color = Utils.colorAtHSV(Color.parseColor(mark.getFontColor()),
//                mark.getColorLightness(), mark.getColorSaturation());
//        String sc = Utils.getHexString(color, true);
//        setStartColor(sc);
//        mMark = mark.copy();
//        mTextView.addOnLayoutChangeListener(new OnLayoutChangeListener() {
//            @Override
//            public void onLayoutChange(View v, int left, int top, int right,
//                                       int bottom, int oldLeft, int oldTop,
//                                       int oldRight, int oldBottom) {
//                if (mMark == null || !mMark.equals(mark)) {
//                    Log.d(TAG, "onLayoutChange, return!");
//                    return;
//                }
//                Log.d(TAG, "onLayoutChange, reset mark!");
//                mark.setX(getMappedCenterX());
//                mark.setY(getMappedCenterY());
//                mark.setCanvasWidth(getCanvasWidth());
//                mark.setCanvasHeight(getCanvasHeight());
//                mark.setTextSize(getTextSize());
//                mark.setScale(1f);
//                mark.setAngle(0f);
//                mark.setLineLength(getLineLength());
//                mark.setLineHeight(getLineHeight());
//                mMark = mark.copy();
//            }
//        });
    }

    @Override
    public void setSelected(boolean selected) {
        if (selected) {
            mDeleteView.setVisibility(VISIBLE);
            mEditView.setVisibility(VISIBLE);
        } else {
            mDeleteView.setVisibility(INVISIBLE);
            mEditView.setVisibility(INVISIBLE);
        }
        super.setSelected(selected);
    }

    public int getCanvasWidth() {
        ViewGroup parent = (ViewGroup) this.getParent();
        return parent.getWidth();
    }

    public int getCanvasHeight() {
        ViewGroup parent = (ViewGroup) this.getParent();
        return parent.getHeight();
    }

    public float getMappedCenterX() {
        return 0f;
    }

    public float getMappedCenterY() {
        ViewGroup parent = (ViewGroup) this.getParent();
        FrameLayout.LayoutParams containerParams = (LayoutParams) getLayoutParams();
        int outerTopMargin = containerParams.topMargin;
        int outerBottomMargin = containerParams.bottomMargin;
        int gap = outerTopMargin + outerBottomMargin;
        int h= getHeight();
        int ph= parent.getHeight();
        return ((gap + getHeight()) / 2f) / (parent.getHeight() / 2f) - 1f;
    }

    // in sp
    public void setTextSize(float textSize) {
        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
    }

    public float getTextSize() {
        return mTextView.getTextSize();
    }

    public Mark getMark() {
        return mMark;
    }

    private int getLineLength() {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mTextView.getLayoutParams();
        return getWidth() - (params.leftMargin +
                params.rightMargin +
                getPaddingStart() +
                getPaddingEnd() +
                mTextView.getPaddingStart() +
                mTextView.getPaddingEnd());
    }

    private int getLineHeight() {
        return mTextView.getHeight() - mTextView.getPaddingTop() - mTextView.getPaddingBottom();
    }

//    private void setTypeface(final Font fd, final boolean isItalic) {
//        if (fd == null) {
//            if (isItalic) {
//                mTextView.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
//            } else {
//                mTextView.setTypeface(Typeface.DEFAULT);
//            }
//            return;
//        }
//        fd.getTypeface(new FontTypefaceCallBack() {
//            @Override
//            public void onSuccess(String fontIdNo, Typeface typeface) {
//                if (typeface != null && fd.getFontIdNo().equals(fontIdNo)) {
//                    if (isItalic) {
//                        mTextView.setTypeface(Typeface.create(typeface, Typeface.ITALIC));
//                    } else {
//                        mTextView.setTypeface(typeface);
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(FailureInfo failureInfo) {
//                if (isItalic) {
//                    mTextView.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
//                } else {
//                    mTextView.setTypeface(Typeface.DEFAULT);
//                }
//            }
//        });
//    }
}
