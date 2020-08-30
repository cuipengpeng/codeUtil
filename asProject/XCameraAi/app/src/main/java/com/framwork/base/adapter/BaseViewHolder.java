package com.framwork.base.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.framwork.base.adapter.BaseRecyclerAdapter;

/**
 * 统一RecycleView BaseViewHolder
 *
 * Created by mz on 2019/7/3.
 */
public class BaseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private BaseRecyclerAdapter.OnRecyclerItemClickListener mItemClickListener;

    public BaseViewHolder(View itemView, BaseRecyclerAdapter.OnRecyclerItemClickListener listener) {
        super(itemView);
        mItemClickListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (mItemClickListener != null) {
            mItemClickListener.onItemClick(v, getAdapterPosition());
        }
    }
}