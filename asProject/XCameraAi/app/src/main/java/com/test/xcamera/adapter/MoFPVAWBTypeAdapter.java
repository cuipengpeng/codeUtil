package com.test.xcamera.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.test.xcamera.R;
import com.test.xcamera.bean.AWBType;
import com.test.xcamera.enumbean.ScreenOrientationType;

import java.util.ArrayList;

/**
 * Created by zll on 2019/11/7.
 */

public class MoFPVAWBTypeAdapter extends RecyclerView.Adapter<MoFPVAWBTypeAdapter.ViewHolder> {
    private ArrayList<AWBType> mAwbTypes;
    private OnItemClickListener onItemClickListener;
    private ScreenOrientationType mOrientationType;

    public void setOnItemClickListener(OnItemClickListener clickListener) {
        onItemClickListener = clickListener;
    }

    public void setData(ArrayList<AWBType> types) {
        mAwbTypes = types;
        notifyDataSetChanged();
    }

    public void setScreenOrientation(ScreenOrientationType orientation) {
        mOrientationType = orientation;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.mo_fpv_awb_type_item_layout, viewGroup, false);
        MoFPVAWBTypeAdapter.ViewHolder viewHolder = new MoFPVAWBTypeAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        AWBType type = mAwbTypes.get(position);
        viewHolder.imageView.setBackgroundResource(type.getResID());
        viewHolder.imageView.setSelected(type.isSelected());
        if (mOrientationType == ScreenOrientationType.LANDSCAPE) {
            viewHolder.imageView.setRotation(0);
        } else if (mOrientationType == ScreenOrientationType.PORTRAIT) {
            viewHolder.imageView.setRotation(-90);
        }
    }

    @Override
    public int getItemCount() {
        return mAwbTypes.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.mo_fpv_awb_type_item_img);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onItemClick(getAdapterPosition(), mAwbTypes);
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position, ArrayList<AWBType> awbTypes);
    }
}
