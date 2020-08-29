/*
 * Copyright 2016 jeasonlzy(廖子尧)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.test.bank.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView的adapter基类
 */
public abstract class BaseRecyclerAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    protected Context mContext;
    public List<T> mData = new ArrayList<>();
    protected OnRecyclerItemClickListener mItemClickListener;

    public BaseRecyclerAdapter(Context context) {
        this.mContext = context;
    }


    public void updateData(boolean refresh, List<T> mediaDataList){
        if(refresh){
            mData.clear();
        }
        mData.addAll(mediaDataList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }


    public void setOnRecyclerItemClickListener(OnRecyclerItemClickListener clickListener) {
        this.mItemClickListener = clickListener;
    }

    public interface OnRecyclerItemClickListener {
        void onItemClick(View view, int position);
    }
}