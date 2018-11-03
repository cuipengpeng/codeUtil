package com.test.bank.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.test.bank.R;
import com.test.bank.bean.PositionAllocationBean;
import com.test.bank.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 */

public class PositionAllocationBondAdapter extends RecyclerView.Adapter<PositionAllocationBondAdapter.ViewHolder> {
    Context mContext;
    List<PositionAllocationBean.BndAssetConf> mDataList = new ArrayList<>();

    public PositionAllocationBondAdapter(Context context) {
        this.mContext = context;
        this.mDataList.clear();
    }

    public void upateData(boolean isRefresh, List<PositionAllocationBean.BndAssetConf> data) {
        if (isRefresh) {
            this.mDataList.clear();
        }
        this.mDataList.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_position_allocation_stock, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if(position == mDataList.size()-1){
            holder.dividerLineView.setVisibility(View.GONE);
        }else {
            holder.dividerLineView.setVisibility(View.VISIBLE);
        }

        holder.keyTextView.setText(mDataList.get(position).getBondsname());
        holder.valueTextView.setText(StringUtil.moneyDecimalFormat2(mDataList.get(position).getTot_val_prop()+"")+"%");
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_positionAllocationFragment_item_key)
        TextView keyTextView ;
        @BindView(R.id.tv_positionAllocationFragment_item_value)
        TextView valueTextView ;
        @BindView(R.id.tv_positionAllocationFragment_item_dividerLine)
        View dividerLineView;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
