package com.jf.jlfund.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jf.jlfund.R;
import com.jf.jlfund.bean.WanfenIncomeBean;
import com.jf.jlfund.utils.StringUtil;
import com.jf.jlfund.view.activity.SevenDayYieldListActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 */

public class SevenDayYieldListAdapter extends RecyclerView.Adapter<SevenDayYieldListAdapter.ViewHolder> {
    private Context mContext;
    private List<WanfenIncomeBean.List> mDataList = new ArrayList<>();
    private int mCurrentIncomeType = -1;

    public SevenDayYieldListAdapter(Context context, List<WanfenIncomeBean.List> dataList, int incomeType) {
        this.mContext = context;
        this.mDataList.clear();
        this.mDataList.addAll(dataList);
        this.mCurrentIncomeType  = incomeType ;
    }

    public void upateData(boolean isRefresh, List<WanfenIncomeBean.List> data) {
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
        holder.keyTextView.setText(mDataList.get(position).getTradeDate());

        switch (mCurrentIncomeType){
            case SevenDayYieldListActivity.SEVEN_DAY_YIELD:
                holder.valueTextView.setText(StringUtil.moneyDecimalFormat4(mDataList.get(position).getNumber()+"")+"%");
                break;
            case SevenDayYieldListActivity.WAN_FEN_INCOME:
                holder.valueTextView.setText(StringUtil.moneyDecimalFormat4(mDataList.get(position).getNumber()+""));
                break;
            case SevenDayYieldListActivity.ACCUMULATED_INCOME:
                holder.valueTextView.setText(StringUtil.moneyDecimalFormat2(mDataList.get(position).getNumber()+""));
                break;
        }
        holder.valueTextView.setTextColor(mContext.getResources().getColor(R.color.currentPlusActivityGold));
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

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
