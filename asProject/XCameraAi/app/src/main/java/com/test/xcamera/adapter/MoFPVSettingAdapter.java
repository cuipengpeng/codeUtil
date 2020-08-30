package com.test.xcamera.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.test.xcamera.R;
import com.test.xcamera.bean.MoFPVSetting;
import com.test.xcamera.enumbean.ScreenOrientationType;

import java.util.ArrayList;
import java.util.List;

/**
 * 相机拍摄设置adapter
 * Created by zll on 2019/10/15.
 */

public class MoFPVSettingAdapter extends RecyclerView.Adapter<MoFPVSettingAdapter.ViewHolder> {
    private ArrayList<MoFPVSetting> fpvSettings;
    private OnItemClickListener mListener;
    private ScreenOrientationType mOrientationType;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public void setData(ArrayList<MoFPVSetting> settings) {
        fpvSettings = settings;
        notifyDataSetChanged();
    }

    public void setScreenOrientation(ScreenOrientationType orientation) {
        mOrientationType = orientation;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.mo_shot_setting_item_view, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(viewHolder, position);
        } else {
            MoFPVSetting setting = fpvSettings.get(position);
            viewHolder.imageView.setBackgroundResource(setting.getmResourceID());
            if (setting.getmSelect() == 0) {
                viewHolder.imageView.setSelected(true);
            } else {
                viewHolder.imageView.setSelected(false);
            }
            if (setting.mShowSplit)
                viewHolder.splitView.setVisibility(View.VISIBLE);
            else
                viewHolder.splitView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        MoFPVSetting setting = fpvSettings.get(position);
        viewHolder.textView.setText(setting.getmTitle());
        viewHolder.imageView.setBackgroundResource(setting.getmResourceID());
        if (setting.getmSelect() == 0) {
            viewHolder.imageView.setSelected(true);
        } else {
            viewHolder.imageView.setSelected(false);
        }
        if (mOrientationType == ScreenOrientationType.LANDSCAPE) {
            viewHolder.imageView.setRotation(0);
        } else if (mOrientationType == ScreenOrientationType.PORTRAIT) {
            viewHolder.imageView.setRotation(-90);
        }
        if (setting.mShowSplit)
            viewHolder.splitView.setVisibility(View.VISIBLE);
        else
            viewHolder.splitView.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return fpvSettings.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;
        View splitView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.shot_setting_item_view_img);
            textView = itemView.findViewById(R.id.shot_setting_item_view_text);
            splitView = itemView.findViewById(R.id.shot_setting_item_split_view);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.onItemClick(getAdapterPosition(), fpvSettings);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position, ArrayList<MoFPVSetting> shotSettings);
    }
}
