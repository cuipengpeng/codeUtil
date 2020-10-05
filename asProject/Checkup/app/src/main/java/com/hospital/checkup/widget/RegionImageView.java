package com.hospital.checkup.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.hospital.checkup.view.fragment.MeasureHomeFragment;

@SuppressLint("AppCompatCustomView")
public class RegionImageView extends ImageView {

    private Context mContext;
    private OnImageViewAreaClickListener mAreaClickListener;
    private final int mColumn = 2;
    private final int mRaw = 4;

    float downX= 0f;
    float downY= 0f;
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
            downX= event.getRawX();
            downY= event.getRawY();
            return true;
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            float upX= event.getRawX();
            float upY= event.getRawY();
            if(Math.abs(upX- downX)>50 || Math.abs(upY-downY)>50){
                return super.onTouchEvent(event);
            }
            float x = event.getX();
            float y = event.getY();
            int area = 0;
            if (x>=0 && x<getWidth()/mColumn && y>0 && y<getHeight()/mRaw) {
                area = MeasureHomeFragment.BODY_CODE_1;
            } else if (x>=getWidth()/mColumn && x<=getWidth() && y>0 && y<getHeight()/mRaw) {
                area = MeasureHomeFragment.BODY_CODE_2;
            } else if (x>0 && x<getWidth()/mColumn  && y>=getHeight()/mRaw && y<=getHeight()/mRaw*2) {
                area = MeasureHomeFragment.BODY_CODE_3;
            } else if (x>=getWidth()/mColumn && x<=getWidth() &&  y>=getHeight()/mRaw && y<=getHeight()/mRaw*2) {
                area = MeasureHomeFragment.BODY_CODE_4;
            } else if (x>0 && x<getWidth()/mColumn  && y>=getHeight()/mRaw*2 && y<=getHeight()/mRaw*3) {
                area = MeasureHomeFragment.BODY_CODE_5;
            } else if (x>=getWidth()/mColumn && x<=getWidth() &&  y>=getHeight()/mRaw*2 && y<=getHeight()/mRaw*3) {
                area = MeasureHomeFragment.BODY_CODE_6;
            }else if (x>0 && x<getWidth()/mColumn  && y>=getHeight()/mRaw*3 && y<=getHeight()) {
                area = MeasureHomeFragment.BODY_CODE_7;
            } else if (x>=getWidth()/mColumn && x<=getWidth() &&  y>=getHeight()/mRaw*3 && y<=getHeight()) {
                area = MeasureHomeFragment.BODY_CODE_8;
            }
            mAreaClickListener.onAreaClick(area);
//            return true;
            return super.onTouchEvent(event);
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