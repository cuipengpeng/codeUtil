package com.moxiang.common.base;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by admin on 2019/10/15.
 */

public abstract class BaseAdapter<T> extends RecyclerView.Adapter<BaseHolder<T>> {
    protected List<T> mInfos;
    private BaseHolder<T> mHolder;
    protected OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public BaseAdapter(List<T> infos) {
        super();
        this.mInfos = infos;
    }

    @NonNull
    @Override
    public BaseHolder<T> onCreateViewHolder(@NonNull ViewGroup parent, final int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(getLayoutId(viewType), parent, false);
        mHolder = getHolder(view, viewType);
        //设置Item点击事件
        mHolder.setOnItemClickListener(new BaseHolder.OnViewClickListener() {
            @Override
            public void onViewClick(View view, int position) {
                if (mOnItemClickListener != null && mInfos.size() > 0) {
                    mOnItemClickListener.onItemClick(view, viewType, mInfos.get(position), position);
                }
            }
        });
        return mHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseHolder<T> tBaseHolder, int position) {
        tBaseHolder.setData(mInfos.get(position), position);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public abstract int getLayoutId(int viewType);

    @NonNull
    public abstract BaseHolder<T> getHolder(@NonNull View v, int viewType);

    public List<T> getInfos() {
        return mInfos;
    }

    public T getItem(int position) {
        return mInfos == null ? null : mInfos.get(position);
    }

    public static void releaseAllHolder(RecyclerView recyclerView) {
        if (recyclerView == null) return;
        for (int i = recyclerView.getChildCount() - 1; i >= 0; i--) {
            final View view = recyclerView.getChildAt(i);
            RecyclerView.ViewHolder viewHolder = recyclerView.getChildViewHolder(view);
            if (viewHolder != null && viewHolder instanceof BaseHolder) {
                ((BaseHolder) viewHolder).onRelease();
            }
        }
    }

    /**
     * item 点击事件
     *
     * @param <T>
     */
    public interface OnRecyclerViewItemClickListener<T> {

        /**
         * item 被点击
         *
         * @param view     被点击的 {@link View}
         * @param viewType 布局类型
         * @param data     数据
         * @param position 在 RecyclerView 中的位置
         */
        void onItemClick(@NonNull View view, int viewType, @NonNull T data, int position);
    }

    /**
     * 设置 item 点击事件
     *
     * @param listener
     */
    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

}
