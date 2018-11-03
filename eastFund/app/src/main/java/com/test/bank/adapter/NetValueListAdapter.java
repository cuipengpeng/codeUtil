package com.test.bank.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.test.bank.R;
import com.test.bank.bean.NetValueBean;
import com.test.bank.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 */

public class NetValueListAdapter extends RecyclerView.Adapter<NetValueListAdapter.ViewHolder> {
    Context mContext;
    List<NetValueBean.Navs> mDataList = new ArrayList<NetValueBean.Navs>();

    public NetValueListAdapter(Context context) {
        this.mContext = context;
        this.mDataList.clear();
    }

    public void upateData(boolean isRefresh, List<NetValueBean.Navs> data) {
        if (isRefresh) {
            this.mDataList.clear();
        }
        this.mDataList.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_net_value_list, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (position % 2 == 1) {
            holder.itemLinearLayout.setBackgroundResource(R.color.appGrayTitleBackgroundColor);
        } else {
            holder.itemLinearLayout.setBackgroundResource(R.color.color_ffffff);
        }

        holder.dateTextView.setText(mDataList.get(position).getTradeDate());
        holder.unitNetValueTextView.setText(mDataList.get(position).getUnit_net());
        holder.accumulateNetValueTextView.setText(mDataList.get(position).getAccum_net());
        String dayRaiseStr = mDataList.get(position).getUnit_net_chng_pct();
        if (StringUtil.notEmpty(dayRaiseStr)) {
            double dayRaise = StringUtil.trunkDouble(Float.valueOf(dayRaiseStr));
            holder.dayRaiseTextView.setText(StringUtil.getRateWithSign(dayRaiseStr));
            if (dayRaise > 0) {
                holder.dayRaiseTextView.setTextColor(mContext.getResources().getColor(R.color.appRedColor));
            } else if (dayRaise < 0) {
                holder.dayRaiseTextView.setTextColor(mContext.getResources().getColor(R.color.appNegativeRateColor));
            } else {
                holder.dayRaiseTextView.setTextColor(mContext.getResources().getColor(R.color.appContentColor));
            }
        }
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_netValueListActivity_item_date)
        TextView dateTextView;
        @BindView(R.id.tv_netValueListActivity_item_unitNetValue)
        TextView unitNetValueTextView;
        @BindView(R.id.tv_netValueListActivity_item_accumulateNetValue)
        TextView accumulateNetValueTextView;
        @BindView(R.id.tv_netValueListActivity_item_dayRaise)
        TextView dayRaiseTextView;
        @BindView(R.id.ll_netValueListActivity_item)
        LinearLayout itemLinearLayout;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
