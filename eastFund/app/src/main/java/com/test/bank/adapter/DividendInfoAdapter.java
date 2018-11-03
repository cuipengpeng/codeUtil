package com.test.bank.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.test.bank.R;
import com.test.bank.bean.FundInfoBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 */

public class DividendInfoAdapter extends RecyclerView.Adapter<DividendInfoAdapter.ViewHolder> {
    Context mContext;
    List<FundInfoBean.Divlist> mDataList = new ArrayList<FundInfoBean.Divlist>();

    public DividendInfoAdapter(Context context) {
        this.mContext = context;
        this.mDataList.clear();
    }

    public void upateData(boolean isRefresh, List<FundInfoBean.Divlist> data) {
        if (isRefresh) {
            this.mDataList.clear();
        }
        this.mDataList.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dividend_info_list, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if(position%2 == 1){
            holder.itemLinearLayout.setBackgroundResource(R.color.appGrayTitleBackgroundColor);
        }else {
            holder.itemLinearLayout.setBackgroundResource(R.color.color_ffffff);
        }

        holder.rightRegisterDateTextView.setText(mDataList.get(position).getRegi_date_net_val());
        holder.dividendProvideDateTextView.setText(mDataList.get(position).getPay_date());
        holder.unitDividendTextView.setText(mDataList.get(position).getUnit_div());
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_dividendInfoActivity_item_rightRegisterDate)
        TextView rightRegisterDateTextView ;
        @BindView(R.id.tv_dividendInfoActivity_item_dividendProvideDate)
        TextView dividendProvideDateTextView ;
        @BindView(R.id.tv_dividendInfoActivity_item_unitDividend)
        TextView unitDividendTextView ;
        @BindView(R.id.ll_dividendInfoActivity_item)
        LinearLayout itemLinearLayout;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
