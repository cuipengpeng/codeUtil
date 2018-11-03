package com.test.bank.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.test.bank.R;
import com.test.bank.bean.TradeNoticeBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 */

public class TradeNoticePreferredAdapter extends RecyclerView.Adapter<TradeNoticePreferredAdapter.ViewHolder> {
    Context mContext;
    List<TradeNoticeBean.Chag_rate_list> mDataList = new ArrayList<>();

    public TradeNoticePreferredAdapter(Context context) {
        this.mContext = context;
        this.mDataList.clear();
    }

    public void upateData(boolean isRefresh, List<TradeNoticeBean.Chag_rate_list> data) {
        if (isRefresh) {
            this.mDataList.clear();
        }
        this.mDataList.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_fund_buy_rule, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if(position == (mDataList.size()-1)){
            holder.dividerLineTextView.setVisibility(View.GONE);
        }else {
            holder.dividerLineTextView.setVisibility(View.VISIBLE);
        }

        holder.keyTextView.setText(mDataList.get(position).getPurchase_amount_interval());
        holder.valueTextView.setText(mDataList.get(position).getRatio());
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
        View dividerLineTextView;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
