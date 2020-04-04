package com.caishi.chaoge.ui.adapter;

import android.animation.AnimatorSet;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.caishi.chaoge.R;
import com.caishi.chaoge.bean.PagerItemBean;
import com.caishi.chaoge.ui.widget.AutoLocateHorizontalView;

import java.util.ArrayList;
import java.util.List;


public class MainTabAdapter extends RecyclerView.Adapter implements AutoLocateHorizontalView.IAutoLocateHorizontalView {
    private Context context;
    private View itemView;
    private List<PagerItemBean> list = new ArrayList<>();
    private AnimatorSet animatorSet;

    public MainTabAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<PagerItemBean> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.layout_tab_view, parent, false);
        this.itemView = itemView;
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public View getItemView() {
        return itemView;
    }

    @Override
    public void onViewSelected(boolean isSelected, int pos, RecyclerView.ViewHolder holder, int itemWidth) {
        ViewHolder holder1 = (ViewHolder) holder;
        holder1.tv_tab_info.setText(list.get(pos).mContent);
        int color;
        if (isSelected) {
            color = Color.parseColor("#ffffff");
        } else {
            color = Color.parseColor("#B3ffffff");
        }
        holder1.tv_tab_info.setTextColor(color);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_tab_info;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_tab_info = itemView.findViewById(R.id.tv_tab_info);
        }
    }
}
