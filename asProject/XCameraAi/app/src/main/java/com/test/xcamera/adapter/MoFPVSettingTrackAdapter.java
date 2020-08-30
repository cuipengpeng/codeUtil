package com.test.xcamera.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.test.xcamera.R;

/**
 * 更多设置页 - 拍摄更多设置
 * Created by zll on 2019/10/18.
 */

public class MoFPVSettingTrackAdapter extends RecyclerView.Adapter<MoFPVSettingTrackAdapter.ViewHolder> {

    private OnItemClickListener mListener;

    public MoFPVSettingTrackAdapter() {

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_fpv_more_setting_recycler_item_view, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.activity_fpv_more_setting_recycler_item_img);
            textView = itemView.findViewById(R.id.activity_fpv_more_setting_recycler_item_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.onItemClick(getAdapterPosition());
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
