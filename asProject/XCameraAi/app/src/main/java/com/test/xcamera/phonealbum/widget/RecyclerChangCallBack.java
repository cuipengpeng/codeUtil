package com.test.xcamera.phonealbum.widget;

import android.os.Handler;
import android.support.v7.widget.RecyclerView;

public class RecyclerChangCallBack {
    private RecyclerView recyclerView;
    private OnChangCallBack mOnChangCallBack;
    public RecyclerChangCallBack(RecyclerView recyclerView){
        this.recyclerView=recyclerView;
    }

    public void setOnChangCallBack(OnChangCallBack mOnChangCallBack) {
        this.mOnChangCallBack = mOnChangCallBack;
        postAndNotifyAdapter();
    }

    public static RecyclerChangCallBack getInstance(RecyclerView recyclerView) {
        return new RecyclerChangCallBack(recyclerView);
    }
    Handler handler = new Handler();
    int i=0;
    public void postAndNotifyAdapter() {
        i++;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!recyclerView.isComputingLayout()) {
                    if(mOnChangCallBack!=null){
                        mOnChangCallBack.CallBack(true);
                    }
                } else {
                    postAndNotifyAdapter();
                }
            }
        },0);
    }
    public interface OnChangCallBack{
        void CallBack(boolean isSucc);
    }
}
