package com.test.bank.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.test.bank.R;
import com.test.bank.bean.FundManagerDetailBean;
import com.test.bank.utils.CommonUtil;
import com.test.bank.utils.StringUtil;
import com.test.bank.view.activity.SingleFundDetailActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 */

public class FundManagerManageFundAdapter extends RecyclerView.Adapter<FundManagerManageFundAdapter.ViewHolder> {
    Context mContext;
    List<FundManagerDetailBean.Fundlist> mDataList = new ArrayList<FundManagerDetailBean.Fundlist>();

    public FundManagerManageFundAdapter(Context context) {
        this.mContext = context;
        this.mDataList.clear();
    }

    public void upateData(boolean isRefresh, List<FundManagerDetailBean.Fundlist> data) {
        if (isRefresh) {
            this.mDataList.clear();
        }
        this.mDataList.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_fund_manager_activity_manage_fund, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if(position%2 == 1){
            holder.itemLinearLayout.setBackgroundResource(R.color.appGrayTitleBackgroundColor);
        }else  {
            holder.itemLinearLayout.setBackgroundResource(R.color.color_ffffff);
        }

        holder.fundNameTextView.setText(mDataList.get(position).getFundsname());
        String[] workDateArray = mDataList.get(position).getDeclaredate().split(" ");
        if (workDateArray.length >1){
            holder.manageDateTextView.setText(workDateArray[0]+"\n"+workDateArray[1]);
        }else {
            holder.manageDateTextView.setText(workDateArray[0]);
        }
        StringUtil.setMoneyTextView(mContext, mDataList.get(position).getChng_pct()+"", holder.fundTotalYieldTextView);
        holder.fundTotalYieldTextView.setText(holder.fundTotalYieldTextView.getText().toString().trim()+"%");

        holder.itemLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtil.startActivity(mContext, SingleFundDetailActivity.class, mDataList.get(position).getFundcode());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_FundManagerActivity_item_manageDate)
        TextView manageDateTextView ;
        @BindView(R.id.tv_FundManagerActivity_item_fundName)
        TextView fundNameTextView ;
        @BindView(R.id.tv_FundManagerActivity_item_fundTotalYield)
        TextView fundTotalYieldTextView ;
        @BindView(R.id.ll_FundManagerActivity_item)
        LinearLayout itemLinearLayout;


        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
