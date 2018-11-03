package com.test.bank.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.test.bank.R;
import com.test.bank.bean.BaobaoBuyRecordBean;
import com.test.bank.utils.ConstantsUtil;
import com.test.bank.utils.StringUtil;
import com.test.bank.view.activity.PutInResultActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 */

public class TradeRecordListAdapter extends RecyclerView.Adapter<TradeRecordListAdapter.ViewHolder> {
    public static final String KEY_OF_TRADE_RECORD_NUMBER = "tradeRecordNumberKey";
    Context mContext;
    List<BaobaoBuyRecordBean.TradeHistory> mDataList = new ArrayList<>();

    public TradeRecordListAdapter(Context context) {
        this.mContext = context;
        this.mDataList.clear();
    }

    public void upateData(boolean isRefresh, List<BaobaoBuyRecordBean.TradeHistory> data) {
        if (isRefresh) {
            this.mDataList.clear();
        }
        this.mDataList.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_trade_record_list, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (position == mDataList.size() - 1) {
            holder.dividerLineView.setVisibility(View.GONE);
        } else {
            holder.dividerLineView.setVisibility(View.VISIBLE);
        }

        holder.tradeTypeTextView.setText(ConstantsUtil.REPLACE_BLANK_SYMBOL);
        switch (mDataList.get(position).getTradetype()) {
            case 1:
                holder.tradeTypeTextView.setText("普通取出");
                holder.tradeAmountTextView.setText("-" + StringUtil.moneyDecimalFormat2(mDataList.get(position).getTradeshare() + ""));
                break;
            case 2:
                holder.tradeTypeTextView.setText("快速取出");
                holder.tradeAmountTextView.setText("-" + StringUtil.moneyDecimalFormat2(mDataList.get(position).getTradeshare() + ""));
                break;
            case 3:
                holder.tradeAmountTextView.setText("+" + StringUtil.moneyDecimalFormat2(mDataList.get(position).getTrademoney() + ""));
                if (mDataList.get(position).getPaystatus() == 0 || mDataList.get(position).getPaystatus() == 2) {
                    holder.tradeTypeTextView.setText("存入");
                } else if (mDataList.get(position).getPaystatus() == 1) {
                    holder.tradeTypeTextView.setText("存入失败");
                }
                break;
        }
        holder.tradeDateTextView.setText(mDataList.get(position).getTradedate());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PutInResultActivity.open(mContext, mDataList.get(position).getTradeno() );
            }
        });

    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_tradeRecordListFragment_item_tradeType)
        TextView tradeTypeTextView;
        @BindView(R.id.tv_tradeRecordListFragment_item_tradeDate)
        TextView tradeDateTextView;
        @BindView(R.id.tv_tradeRecordListFragment_item_tradeAmount)
        TextView tradeAmountTextView;
        @BindView(R.id.tv_tradeRecordListFragment_item_dividerLine)
        View dividerLineView;


        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
