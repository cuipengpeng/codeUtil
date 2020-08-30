package com.test.xcamera.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.test.xcamera.R;

import java.util.ArrayList;

/**
 * Created by zll on 2019/10/21.
 */

public class MoSelectShotModeAdapter extends RecyclerView.Adapter<MoSelectShotModeAdapter.ViewHolder>
        implements View.OnClickListener {
    private OnItemClickListener mListener;
    private ArrayList<Integer> mList;

    public MoSelectShotModeAdapter(ArrayList<Integer> list) {
        mList = list;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.select_shot_mode_item_layout, viewGroup, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.itemView.setTag(position);
        viewHolder.textView.setText(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onClick(View view) {
        if (mListener != null) {
            mListener.onItemClick(view, (int) view.getTag());
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.select_shot_mode_item_text);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}
