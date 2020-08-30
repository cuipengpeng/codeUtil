package com.test.xcamera.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.test.xcamera.R;
import com.test.xcamera.enumbean.ScreenOrientationType;
import com.test.xcamera.managers.MoFPVParametersManager;

import java.util.ArrayList;

/**
 * Created by zll on 2019/10/25.
 */

public class MoFPVSettingValueAdapter extends RecyclerView.Adapter<MoFPVSettingValueAdapter.ViewHolder> {
    private ArrayList<MoFPVParametersManager.ValueData> mValues;
    private ScreenOrientationType mOrientationType = ScreenOrientationType.LANDSCAPE;
    private Drawable mRightDrawable;
    private Context mContext;

    public MoFPVSettingValueAdapter(Context context) {
        mContext = context;
    }

    public void setData(ArrayList<MoFPVParametersManager.ValueData> strings) {
        mValues = strings;
        notifyDataSetChanged();
    }

    public void setScreenOrientation(ScreenOrientationType orientation) {
        mOrientationType = orientation;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.fpv_second_setting_item_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        String value = mValues.get(position).getValue();
//        int type = mValues.get(position).getType();
        viewHolder.mValue.setText(value);
//        if (type == 1) {
//            viewHolder.mLine.setBackgroundColor(0XFF02C7A9);
//        } else {
//            viewHolder.mLine.setBackgroundColor(0XFFFFFFFF);
//        }
        if (mOrientationType == ScreenOrientationType.LANDSCAPE) {
            viewHolder.mValue.setRotation(0);
            viewHolder.mValue.setCompoundDrawables(null, null, getRightDrawable(), null);
        } else {
            viewHolder.mValue.setRotation(-90);
            viewHolder.mValue.setCompoundDrawables(null, null, null, getBottomDrawable());
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView mValue;
        View mLine;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mValue = itemView.findViewById(R.id.fpv_second_setting_item_text);
            mLine = itemView.findViewById(R.id.fpv_second_setting_item_line);
        }
    }

    private Drawable getRightDrawable() {
        mRightDrawable = mContext.getResources().getDrawable(R.drawable.fpv_setting_line_right_selector);
        mRightDrawable.setBounds(0, 0, mRightDrawable.getMinimumWidth(), mRightDrawable.getMinimumHeight());
        return mRightDrawable;
    }

    private Drawable getBottomDrawable() {
        mRightDrawable = mContext.getResources().getDrawable(R.drawable.fpv_setting_line_bottom_selector);
        mRightDrawable.setBounds(0, 0, mRightDrawable.getMinimumWidth(), mRightDrawable.getMinimumHeight());
        return mRightDrawable;
    }
}
