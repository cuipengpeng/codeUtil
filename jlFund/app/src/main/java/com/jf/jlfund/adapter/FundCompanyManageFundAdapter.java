package com.jf.jlfund.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jf.jlfund.R;
import com.jf.jlfund.bean.FundCompanyBean;
import com.jf.jlfund.utils.CommonUtil;
import com.jf.jlfund.utils.StringUtil;
import com.jf.jlfund.view.activity.SingleFundDetailActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 */

public class FundCompanyManageFundAdapter extends RecyclerView.Adapter<FundCompanyManageFundAdapter.ViewHolder> {
    Context mContext;
    List<FundCompanyBean.Fundlist> mDataList = new ArrayList<>();

    public FundCompanyManageFundAdapter(Context context, List<FundCompanyBean.Fundlist> dataList) {
        this.mContext = context;
        this.mDataList = dataList;
    }

    public void upateData(boolean isRefresh, List<FundCompanyBean.Fundlist> data) {
        if (isRefresh) {
            this.mDataList.clear();
        }
        this.mDataList.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_fund_company_activity_manage_fund, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if(position%2 == 1){
            holder.itemLinearLayout.setBackgroundResource(R.color.appGrayTitleBackgroundColor);
        }else  {
            holder.itemLinearLayout.setBackgroundResource(R.color.color_ffffff);
        }
        holder.itemLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtil.startActivity(mContext, SingleFundDetailActivity.class, mDataList.get(position).getFundcode());
            }
        });

        holder.fundNameTextView.setText(mDataList.get(position).getFundname());
        holder.fundTypeTextView.setText(mDataList.get(position).getFundtype());
        StringUtil.setMoneyTextView(mContext, mDataList.get(position).getOyr_yld()+"", holder.fundYieldTextView);
        holder.fundYieldTextView.setText(holder.fundYieldTextView.getText().toString().trim()+"%");
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_fundCompanyActivity_item_fundName)
        TextView fundNameTextView ;
        @BindView(R.id.tv_fundCompanyActivity_item_fundType)
        TextView fundTypeTextView ;
        @BindView(R.id.tv_fundCompanyActivity_item_fundYield)
        TextView fundYieldTextView ;
        @BindView(R.id.ll_fundCompanyActivity_item)
        LinearLayout itemLinearLayout;


        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
