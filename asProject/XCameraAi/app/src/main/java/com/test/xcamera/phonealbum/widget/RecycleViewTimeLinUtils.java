package com.test.xcamera.phonealbum.widget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;


import com.test.xcamera.phonealbum.bean.BaseThumbBean;
import com.test.xcamera.utils.PUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class RecycleViewTimeLinUtils {

    private RecyclerView recycleView;

    private RecycleViewTimeLinUtils utils;

    private Map<Integer, Integer> itemHeights;
    private Map<Integer, Integer> itemWidths;

    private int nowItemPos, totalHeight = 0,totalWidth = 0, pos;
    private View nowView;
    private LinearLayoutManager layoutManager;

//    public RecycleViewTimeLinUtils() {
//
//    }


    public RecycleViewTimeLinUtils(RecyclerView recyclerView) {
        this.recycleView = (recyclerView);
        itemHeights = new WeakHashMap<>();
        itemWidths = new HashMap<>();
        nowItemPos = 0;
        layoutManager = (LinearLayoutManager) recycleView.getLayoutManager();
    }

//    public RecycleViewTimeLinUtils with(RecyclerView recyclerView) {
//        utils = new RecycleViewTimeLinUtils(recyclerView);
//
//        return utils;
//    }

    public synchronized int getScrollY() {
        int pos = layoutManager.findFirstVisibleItemPosition();
        int lastPos = layoutManager.findLastVisibleItemPosition();
        for (int i = pos; i <= lastPos; i++) {
            nowView = layoutManager.findViewByPosition(i);
            if (!itemHeights.containsKey(i)) {
                totalHeight = 0;
                totalHeight += nowView.getHeight();
                if (totalHeight == 0) break;
                itemHeights.put(i, totalHeight);
            }

        }
//        nowView = layoutManager.findViewByPosition(pos);
//        if (pos == nowItemPos) {
//            if (!itemHeights.containsKey(pos)) {
//                totalHeight = 0;
//                if (itemHeights.containsKey(pos - 1))
//                    totalHeight = itemHeights.get(pos - 1);
//                totalHeight += nowView.getMeasuredHeight();
//                itemHeights.put(nowItemPos, totalHeight);
//            }
//        }
        int height = 0;
//        if (itemHeights.containsKey(pos - 1)) height = itemHeights.get(pos - 1);
        for (int i = 0; i < pos; i++) height += itemHeights.get(i);
        if (pos != nowItemPos) nowItemPos = pos;
        nowView = layoutManager.findViewByPosition(pos);
        if (nowView != null) height -= nowView.getTop();
        return height;
    }
    public synchronized int getScrollX(List<BaseThumbBean> baseThumbBeanList) {
        try {
            int pos = layoutManager.findFirstVisibleItemPosition();
            int Width = 0;
            for (int i = 0; i < pos; i++) {
                Width += baseThumbBeanList.get(i).getViewStartIndex();
            }
            nowView = layoutManager.findViewByPosition(pos);
            if (nowView != null) Width -= nowView.getLeft();
            return Width;
        }catch (Exception e){

            e.printStackTrace();
            return -1;
        }
    }
    public synchronized void scrollToXTwo(Context context, LinearLayoutManager layoutManager, int x) {
        int width= PUtil.dip2px(context,80);
        int halfScreen = PUtil.getScreenW(context)/2;
        int position;
        int offset;
        if(x<=halfScreen){
            position=0;
            offset=x;
        }else {
            position=(x-halfScreen)/width+1;
            offset=(x-halfScreen)%width;
        }
        layoutManager.scrollToPositionWithOffset(position,-offset);

    }

    public synchronized void scrollToX(Context context, LinearLayoutManager layoutManager, int x, List<BaseThumbBean> baseThumbBeanList) {
        int halfScreen = PUtil.getScreenW(context)/2;
        int position=0;
        int offset=0;
        int totalWidth=0;
        if(x<=halfScreen){
            position=0;
            offset=x;
        }else {
            for (int i = 1; i <= baseThumbBeanList.size()-1; i++) {
                BaseThumbBean w=baseThumbBeanList.get(i);
                if(totalWidth<x&&x<=totalWidth+w.getViewStartIndex()){
                    position=i;
                    offset=(x-halfScreen)-totalWidth;
                    break;
                }
                totalWidth=totalWidth+w.getViewStartIndex();
            }

        }
        layoutManager.scrollToPositionWithOffset(position,-offset);

    }
}
