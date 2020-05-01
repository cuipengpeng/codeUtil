package com.jfbank.qualitymarket.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;


/**
 * 功能：<br>
 * 作者：赵海<br>
 * 时间： 2017/3/1 0001<br>.
 * 版本：1.2.0
 */

public class FeedRootRecyclerView extends BetterRecyclerView {

//    public int getScollYDistance() {
//        LinearLayoutManager layoutManager = (LinearLayoutManager) getLayoutManager();
//        int position = layoutManager.findFirstVisibleItemPosition();
//        View firstVisiableChildView = layoutManager.findViewByPosition(position);
//        int itemHeight = firstVisiableChildView.getHeight();
//        return (position) * itemHeight - firstVisiableChildView.getTop();
//        }
//
//
//@Override
//public boolean canPullDown() {
//        return getScollYDistance()<=0 ? true : false;
//        }
//
//@Override
//public boolean canPullUp() {
//        return true;
//        }
public FeedRootRecyclerView(Context context) {
        this(context, null);
        }

public FeedRootRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
        }

public FeedRootRecyclerView(Context context, @Nullable AttributeSet  attrs, int defStyle) {
        super(context, attrs, defStyle);
        }

@Override
public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    /* do nothing */
        }
        }