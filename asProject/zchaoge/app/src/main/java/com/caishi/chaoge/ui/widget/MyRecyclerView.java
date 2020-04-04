package com.caishi.chaoge.ui.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

public class MyRecyclerView extends RecyclerView {

    private float x;
    private float y;

    public MyRecyclerView(Context context) {
        super(context);
    }

    public MyRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

//    PointF downP = new PointF();
//    PointF curP = new PointF();
//
//    private float xDown;// 记录手指按下时的横坐标。
//    private float xMove;// 记录手指移动时的横坐标。
//    private float yDown;// 记录手指按下时的纵坐标。
//
//    @Override
//    public boolean onTouchEvent(MotionEvent ev) {
//        // 每次进行onTouch事件都记录当前的按下的坐标
//        curP.x = ev.getX();
//        curP.y = ev.getY();
//
//        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
//            // 这两个参数记录的暂时不知道做什么用，也没有地方进行引用。
//            downP.x = ev.getX();
//            downP.y = ev.getY();
//
//            xDown = ev.getX();
//            yDown = ev.getY();
//
//            // 此句代码是为了通知他的父ViewPager现在进行的是本控件的操作，不要对我的操作进行干扰
//            getParent().requestDisallowInterceptTouchEvent(true);
//        }
//
//        // 重点方法在这里了，移动的时候进行判断
//        if (ev.getAction() == MotionEvent.ACTION_MOVE) {
//
//            xMove = ev.getX();
//            float yMove = ev.getY();
//            // 这里判断是横向还是纵向移动，
//            if (Math.abs(yMove - yDown) < Math.abs(xMove - xDown) && Math.abs(xMove - xDown) > 2) {
//                // 这里还要再进行一次判断，不然横向滑动的时候直接没效果了
//                if (Math.abs(xMove - xDown) > 2) {
//                    // 通知父控件不要进行拦截了，事件自己消费
//                    getParent().requestDisallowInterceptTouchEvent(false);
//                } else {
//                    // 这里返回False 事件自己消费了，不用往下传递。
//                  //  return false;
//                }
//            } else {
//                // 通知父控件进行事件拦截
//                getParent().requestDisallowInterceptTouchEvent(true);
//            }
//        }
//        return super.onTouchEvent(ev);
//    }

}
