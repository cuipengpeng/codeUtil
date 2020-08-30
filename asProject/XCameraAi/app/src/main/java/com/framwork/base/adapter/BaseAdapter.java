package com.framwork.base.adapter;

import android.content.Context;
import android.view.LayoutInflater;

import java.util.List;

/**
 * ListView基类adapter
 */
public abstract class BaseAdapter<T> extends android.widget.BaseAdapter {
    protected Context mContext;
    protected LayoutInflater mInflater;
    protected List<T> mData;

    public BaseAdapter(Context context) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    public BaseAdapter(Context context, List<T> data) {
        this(context);
        this.mData = data;
    }

    public List<T> getData() {
        return mData;
    }

    public void setData(List<T> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    public void appendData(List<T> data) {
        if (mData == null) {
            mData = data;
        } else {
            mData.addAll(data);
        }
    }

    @Override
    public int getCount() {
        return mData != null ? mData.size() : 0;
    }

    @Override
    public T getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}