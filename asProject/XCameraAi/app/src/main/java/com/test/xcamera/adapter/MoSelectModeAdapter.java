package com.test.xcamera.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.test.xcamera.R;
import com.test.xcamera.activity.CameraMode;
import java.util.List;

/**
 * Created by zll on 2019/10/21.
 */

public class MoSelectModeAdapter extends RecyclerView.Adapter<MoSelectModeAdapter.ViewHolder>
        implements View.OnClickListener {
    private OnItemClickListener mListener;
    private List<CameraMode> mDatas = CameraMode.mCameraMode;

    public MoSelectModeAdapter() {

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_fpv_select_mode, viewGroup, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.textView.setTag(this.mDatas.get(position));
        viewHolder.textView.setText(this.mDatas.get(position).text);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public void onClick(View view) {
        if (mListener != null) {
            mListener.onItemClick(view, (CameraMode) view.getTag());
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            textView = (TextView) itemView;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, CameraMode item);
    }
}
