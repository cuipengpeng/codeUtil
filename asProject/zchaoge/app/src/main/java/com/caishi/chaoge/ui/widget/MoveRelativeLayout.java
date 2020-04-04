package com.caishi.chaoge.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.caishi.chaoge.rd.overturn.DrawT0;
import com.caishi.chaoge.utils.DisplayMetricsUtil;
import com.caishi.chaoge.utils.LogUtil;

public class MoveRelativeLayout extends RelativeLayout {
    private float downX;
    private float downY;
    private int[] location = new int[2];
    private float screenWidth;
    private float screenHeight;
    public float mouthX;
    public float mouthY;
    public TouchCallBack mTouchCallBack;

    public MoveRelativeLayout(Context context) {
        this(context, null);
    }
    public MoveRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public MoveRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        screenWidth = DisplayMetricsUtil.getScreenWidth(context);
        screenHeight = DisplayMetricsUtil.getScreenHeight(context);
        mouthX = screenWidth/2;
        mouthY = screenHeight/2;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getRawX();
                downY = event.getRawY();
                LogUtil.printLog("downX = "+downX+"--downY="+downY);
                break;
            case MotionEvent.ACTION_MOVE:
                float toX = getX() + (event.getRawX() - downX);
                float toY = getY() + (event.getRawY() - downY);
//                if(toX>=0 && (getWidth()+toX)<=screenWidth){
////                    setTranslationX(toX);
//                    setX(toX);
//                }
                if(toY>=0 && toY<=screenHeight){
//                    setTranslationY(toY);
                    setY(toY);
                }
                downX = event.getRawX();
                downY = event.getRawY();

//                LogUtil.printLog("downX = "+toX+"--downY="+toY);
                break;
            case MotionEvent.ACTION_UP:
                getLocationOnScreen(location);//获取在整个屏幕内的绝对坐标
                mouthX = location[0]+getWidth()/2;
                mouthY = location[1];
                DrawT0.mouthX = mouthX;
                DrawT0.mouthY = mouthY;
                mTouchCallBack.onTouchUp((screenHeight-location[1])/3*1.05f);
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }

    public  interface  TouchCallBack{
        void onTouchUp(float height);
    }

    public void setTouchCallBack(TouchCallBack touchCallBack) {
        this.mTouchCallBack = touchCallBack;
    }
}