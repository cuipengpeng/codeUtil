package com.test.xcamera.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;

import com.test.xcamera.R;
import com.test.xcamera.utils.ViewUitls;

public class VerticalTextView extends android.support.v7.widget.AppCompatTextView {
    public final static int ORIENTATION_UP_TO_DOWN = 0;
    public final static int ORIENTATION_DOWN_TO_UP = 1;
    public final static int ORIENTATION_LEFT_TO_RIGHT = 2;
    public final static int ORIENTATION_RIGHT_TO_LEFT = 3;

    Rect text_bounds = new Rect();
    private int direction;
    private int mMeasureCount = 0;
    //默认2dp的padding
    private int padding = 0;

    public VerticalTextView(Context context) {
        super(context);
    }

    public VerticalTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        padding = ViewUitls.dp2px(context, 2);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.verticaltextview);
        direction = a.getInt(R.styleable.verticaltextview_direction, 0);
        a.recycle();

        requestLayout();
        invalidate();
    }

    public void setDirection(int direction) {
        this.direction = direction;

        requestLayout();
        invalidate();
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);

        requestLayout();
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        getPaint().getTextBounds(getText().toString(), 0, getText().length(),
                text_bounds);
        text_bounds.right = (int) getPaint().measureText(getText(), 0, getText().length());

        if (getPaddingLeft() == 0 && getPaddingTop() == 0
                && getPaddingBottom() == 0 && getPaddingRight() == 0) {
            this.setPadding(padding, padding, padding, padding);
        }

        if (direction == ORIENTATION_LEFT_TO_RIGHT
                || direction == ORIENTATION_RIGHT_TO_LEFT) {
            setMeasuredDimension(measureHeight(widthMeasureSpec),
                    measureWidth(heightMeasureSpec));
        } else if (direction == ORIENTATION_UP_TO_DOWN
                || direction == ORIENTATION_DOWN_TO_UP) {
            setMeasuredDimension(measureWidth(widthMeasureSpec),
                    measureHeight(heightMeasureSpec));
        }
        mMeasureCount++;
        System.out.println("onMeasure " + mMeasureCount);
    }

    private int measureWidth(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = text_bounds.height() + getPaddingTop()
                    + getPaddingBottom();
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    private int measureHeight(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = text_bounds.width() + getPaddingLeft() + getPaddingRight();
            // result = text_bounds.width();
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
//		 super.onDraw(canvas);

        canvas.save();

        int startX = 0;
        int startY = 0;
        int stopX = 0;
        int stopY = 0;
        Path path = new Path();
        if (direction == ORIENTATION_UP_TO_DOWN) {
            startX = (getWidth() - text_bounds.height() >> 1);
            startY = (getHeight() - text_bounds.width() >> 1);
            stopX = (getWidth() - text_bounds.height() >> 1);
            stopY = (getHeight() + text_bounds.width() >> 1);
            path.moveTo(startX, startY);
            path.lineTo(stopX, stopY);
        } else if (direction == ORIENTATION_DOWN_TO_UP) {
            startX = (getWidth() + text_bounds.height() >> 1);
            startY = (getHeight() + text_bounds.width() >> 1);
            stopX = (getWidth() + text_bounds.height() >> 1);
            stopY = (getHeight() - text_bounds.width() >> 1);
            path.moveTo(startX, startY);
            path.lineTo(stopX, stopY);
        } else if (direction == ORIENTATION_LEFT_TO_RIGHT) {
            startX = (getWidth() - text_bounds.width() >> 1);
            startY = (getHeight() + text_bounds.height() >> 1);
            stopX = (getWidth() + text_bounds.width() >> 1);
            stopY = (getHeight() + text_bounds.height() >> 1);
            path.moveTo(startX, startY);
            path.lineTo(stopX, stopY);
        } else if (direction == ORIENTATION_RIGHT_TO_LEFT) {
            startX = (getWidth() + text_bounds.width() >> 1);
            startY = (getHeight() - text_bounds.height() >> 1);
            stopX = (getWidth() - text_bounds.width() >> 1);
            stopY = (getHeight() - text_bounds.height() >> 1);
            path.moveTo(startX, startY);
            path.lineTo(stopX, stopY);
        }

        this.getPaint().setColor(this.getCurrentTextColor());
//		canvas.drawLine(startX, startY, stopX, stopY, this.getPaint());
        canvas.drawTextOnPath(getText().toString(), path, 0, 0, this.getPaint());

        canvas.restore();
    }
}
