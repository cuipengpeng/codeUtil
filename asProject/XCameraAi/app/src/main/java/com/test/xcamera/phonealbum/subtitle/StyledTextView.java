package com.test.xcamera.phonealbum.subtitle;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.TextView;


public class StyledTextView extends AppCompatTextView {
    private static final String TAG = StyledTextView.class.getSimpleName();
    private TextStyle mTextStyle;
    private TextView mStrokeTextView;
    private String mStartColor;

    public StyledTextView(Context context) {
        this(context, null);
    }

    public StyledTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public void init(Context context, AttributeSet attrs){
        mStrokeTextView = new TextView(context, attrs);
        mStrokeTextView.setGravity(getGravity());
    }

    @Override
    public void setLayoutParams (ViewGroup.LayoutParams params) {
        super.setLayoutParams(params);
        mStrokeTextView.setLayoutParams(params);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mStrokeTextView.measure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mTextStyle != null) {
            mStrokeTextView.draw(canvas);
//            canvas.clipPath()
        }
        super.onDraw(canvas);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if(changed) {
            if (mTextStyle != null) {
                if (mStartColor != null && mTextStyle.getEndColor() != null) {
                    Shader shader = new LinearGradient(
                            0, 0, 0, getHeight() - getPaddingTop() - getPaddingBottom(),
                            Color.parseColor(mStartColor),
                            Color.parseColor(mTextStyle.getEndColor()),
                            Shader.TileMode.CLAMP);
                    getPaint().setShader(shader);
                } else if (mStartColor != null) {
                    getPaint().setShader(null);
                    setTextColor(Color.parseColor(mStartColor));
                } else {
                    getPaint().setShader(null);
                    setTextColor(Color.WHITE);
                }
            }
        }
        mStrokeTextView.layout(left, top, right, bottom);
    }

    public void setTextStyle(TextStyle style) {
        mTextStyle = style;
        if (style == null) {
            return;
        }
        mStartColor = style.getStartColor();
        TextPaint tp = mStrokeTextView.getPaint();
        tp.setStrokeWidth(style.getFirstStrokeWidth());
        tp.setFakeBoldText(false);
        tp.setStrokeJoin(Paint.Join.MITER);
        tp.setStrokeMiter(1);
        tp.setStrokeCap(Paint.Cap.ROUND);
        tp.setStyle(Paint.Style.FILL_AND_STROKE);
        mStrokeTextView.setTextColor(Color.parseColor(style.getFirstStrokeColor()));
        if (style.getSecondStrokeColor() != null) {
            mStrokeTextView.setShadowLayer(style.getSecondStrokeWidth(),
                    0, 0, Color.parseColor(style.getSecondStrokeColor()));
        } else {
            mStrokeTextView.setShadowLayer(0, 0, 0, Color.TRANSPARENT);
        }
        if (mStartColor != null && mTextStyle.getEndColor() != null) {
            Shader shader = new LinearGradient(
                    0, 0, 0, getHeight() - getPaddingTop() - getPaddingBottom(),
                    Color.parseColor(mStartColor),
                    Color.parseColor(mTextStyle.getEndColor()),
                    Shader.TileMode.CLAMP);
            getPaint().setShader(shader);
            setTextColor(Color.parseColor(mStartColor));
        } else if (mStartColor != null) {
            getPaint().setShader(null);
            setTextColor(Color.parseColor(mStartColor));
        } else {
            getPaint().setShader(null);
            setTextColor(Color.WHITE);
        }
        invalidate();
    }

    public void setStartColor(String color) {
        mStartColor = color;
        if (mTextStyle == null) {
            return;
        }
        if (mTextStyle.getEndColor() != null) {
            Shader shader = new LinearGradient(
                    0, 0, 0, getHeight() - getPaddingTop() - getPaddingBottom(),
                    Color.parseColor(mStartColor),
                    Color.parseColor(mTextStyle.getEndColor()),
                    Shader.TileMode.CLAMP);
            getPaint().setShader(shader);
            setTextColor(Color.parseColor(mStartColor));
        } else {
            getPaint().setShader(null);
            setTextColor(Color.parseColor(mStartColor));
        }
        invalidate();
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility != VISIBLE) {
            mTextStyle = null;
            mStartColor = "#ffffff";
        }
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (mStrokeTextView != null) {
            mStrokeTextView.setText(text, type);
        }
        super.setText(text, type);
    }

    @Override
    public void setTextAlignment(int textAlignment) {
        if (mStrokeTextView != null) {
            mStrokeTextView.setTextAlignment(textAlignment);
        }
        super.setTextAlignment(textAlignment);
        setText(getText());
    }

    @Override
    public void setTypeface(Typeface tf) {
        if (mStrokeTextView != null) {
            mStrokeTextView.setTypeface(tf);
        }
        super.setTypeface(tf);
    }

    @Override
    public void setTextSize(int unit, float size) {
        if (mStrokeTextView != null) {
            mStrokeTextView.setTextSize(unit, size);
        }
        super.setTextSize(unit, size);
    }

    public void reset() {
        if (mStrokeTextView != null) {
            mStrokeTextView.setTypeface(Typeface.DEFAULT);
            mStrokeTextView.setShadowLayer(0, 0, 0, Color.TRANSPARENT);
            mStrokeTextView.setText("");
        }
        setTypeface(Typeface.DEFAULT);
        setTextColor(Color.WHITE);
        setText("");
        mTextStyle = null;
        mStartColor = null;
        getPaint().setShader(null);
    }
}
