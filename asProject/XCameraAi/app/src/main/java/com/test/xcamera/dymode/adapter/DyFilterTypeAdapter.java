package com.test.xcamera.dymode.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.test.xcamera.R;
import com.test.xcamera.view.VerticalTextView;
import com.ss.android.ugc.effectmanager.effect.model.EffectCategoryResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zll on 2020/4/8.
 */

public class DyFilterTypeAdapter extends RecyclerView.Adapter<DyFilterTypeAdapter.ViewHolder>{
    private List<EffectCategoryResponse> mResponses;
    private OnTypeClickListener mItemClickListener;
    private int lastSelectPos = 0;
    private View lastSelectedView = null;
    private View.OnClickListener mOnClickListener;

    public DyFilterTypeAdapter(OnTypeClickListener listener, List<EffectCategoryResponse> responseList) {
        mItemClickListener = listener;
        if (mResponses == null) {
            mResponses = new ArrayList<>();
        }
        if (responseList != null) {
            mResponses.addAll(responseList);
        }
//        mResponses = responseList;
        initClickListener();
    }

    public void setData(List<EffectCategoryResponse> responseList) {
        if (mResponses != null) {
            mResponses.clear();
            mResponses.addAll(responseList);
        }
        lastSelectPos = -1;
        notifyDataSetChanged();
    }

    public void resetSelect() {
        lastSelectPos = -1;
        notifyDataSetChanged();
    }

    public void selectFirst() {
        lastSelectPos = 0;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.dy_filter_type_item_layout, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        if (mResponses == null) return;
        EffectCategoryResponse response = mResponses.get(position);
        String name = response.getName();
        viewHolder.type.setText(name);
        if (lastSelectPos == position) {
            viewHolder.type.setTextColor(0XFFFFFFFF);
        } else {
            viewHolder.type.setTextColor(0XFF7A7A7A);
        }
        if (mOnClickListener != null) {
            viewHolder.type.setTag(position);
            viewHolder.itemView.setFocusable(true);
            viewHolder.type.setOnClickListener(mOnClickListener);
        }
    }

    @Override
    public int getItemCount() {
        if (mResponses != null)
            return mResponses.size();
        return 0;
    }

    public void initClickListener() {
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectIndex = Integer.parseInt(v.getTag().toString());
                if (selectIndex != lastSelectPos) {
                    if (lastSelectedView != null && (int) lastSelectedView.getTag() == lastSelectPos) {
                        lastSelectedView.setSelected(false);
                    }
                    v.setSelected(true);
                    lastSelectPos = selectIndex;
                    lastSelectedView = v;
                    mItemClickListener.onItemClick(selectIndex, mResponses.get(selectIndex));
                    notifyDataSetChanged();
                }
            }
        };
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        VerticalTextView type;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            type = itemView.findViewById(R.id.dy_fpv_filter_item_type);
        }
    }

    public interface OnTypeClickListener {
        void onItemClick(int position, EffectCategoryResponse response);
    }
}
