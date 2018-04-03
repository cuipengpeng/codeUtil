package com.jf.jlfund.weight.errorview;

import android.content.Context;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * 为了适配在带有头布局的recyclerview的页面中自动显示异常页面的基类
 * Created by 55 on 2017/10/10.
 */

public abstract class RecyclerViewWithHeaderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "RecycleViewWithHeaderAd";

    private int TYPE_ERROR_PAGE = 4444;

    public Context mContext;

    protected View errorView;

    public RecyclerViewWithHeaderAdapter(Context context) {
        mContext = context;
    }

    /**
     * 刷新errorView， 如果刷新errorView为null 则表示替换为原来的内容view.
     *
     * @param errorView
     */
    public void notifyPageChanged(View errorView) {
        this.errorView = errorView;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: viewType is " + viewType);
        if (viewType == TYPE_ERROR_PAGE) {
            return new ErrorPageViewHolder(new LinearLayout(mContext));
        }
        return onCreateOriginalViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ErrorPageViewHolder) {
            Log.d(TAG, "onBindViewHolder: instanceof ErrorPageViewHolder");
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ((ErrorPageViewHolder) holder).itemView.setLayoutParams(params);
            ((ErrorPageViewHolder) holder).refreshView(errorView);
        } else {
            onBindOriginalViewHolder(holder, position - getHeaderCount());  //减去头布局的个数，防止下标越界
        }
    }


    @Override
    public int getItemViewType(int position) {
        if (position >= getHeaderCount() && errorView != null) {
            return TYPE_ERROR_PAGE;
        }
        return getOriginalViewType(position);
    }

    @Override
    public int getItemCount() {
        if (errorView == null) {     //不展示错误页面
            return getHeaderCount() + getOriginalItemCount();
        }
        return getHeaderCount() + 1;
    }

    protected abstract int getOriginalItemCount();   //相当于getItemCount

    protected abstract int getHeaderCount();  //获取头布局数量

    protected abstract int getOriginalViewType(int position);   //相当于 getItemViewType(int pos)

    protected abstract RecyclerView.ViewHolder onCreateOriginalViewHolder(ViewGroup parent, int viewType);

    protected abstract void onBindOriginalViewHolder(RecyclerView.ViewHolder holder, int position);

    /**
     * 异常页面的viewHolder
     */
    public class ErrorPageViewHolder extends RecyclerView.ViewHolder {

        private View rootView;

        public ErrorPageViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
        }

        public void refreshView(View errorView) {
            if (rootView instanceof ViewGroup) {
                ((ViewGroup) rootView).removeAllViews();
                errorView.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                if (errorView.getParent() != null) {
                    ViewGroup parent = (ViewGroup) errorView.getParent();
                    parent.removeAllViews();
                }
                ((ViewGroup) rootView).addView(errorView);
            }
        }
    }
}
