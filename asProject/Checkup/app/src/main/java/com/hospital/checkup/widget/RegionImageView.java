package com.hospital.checkup.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

@SuppressLint("AppCompatCustomView")
public class RegionImageView extends ImageView {

    private Context mContext;
    private OnImageViewAreaClickListener mAreaClickListener;
    private final int mColumn = 2;
    private final int mRaw = 3;

    public RegionImageView(Context context) {
        this(context, null);
    }

    public RegionImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();
            int area = 0;
            if (x>=0 && x<getWidth()/mColumn && y>0 && y<getHeight()/mRaw) {
                area = 1;
            } else if (x>=getWidth()/mColumn && x<=getWidth() && y>0 && y<getHeight()/mRaw) {
                area = 2;
            } else if (x>0 && x<getWidth()/mColumn  && y>=getHeight()/mRaw && y<=getHeight()/mRaw*2) {
                area = 3;
            } else if (x>=getWidth()/mColumn && x<=getWidth() &&  y>=getHeight()/mRaw && y<=getHeight()/mRaw*2) {
                area = 4;
            } else if (x>0 && x<getWidth()/mColumn  && y>=getHeight()/mRaw*2 && y<=getHeight()) {
                area = 5;
            } else if (x>=getWidth()/mColumn && x<=getWidth() &&  y>=getHeight()/mRaw*2 && y<=getHeight()) {
                area = 6;
            }
            mAreaClickListener.onAreaClick(area);
        }
        return super.onTouchEvent(event);
    }


    public void setOnImageViewAreaClickListener(OnImageViewAreaClickListener clickListener) {
        this.mAreaClickListener = clickListener;
    }

    public interface OnImageViewAreaClickListener {
        void onAreaClick(int areaIndex);
    }

}