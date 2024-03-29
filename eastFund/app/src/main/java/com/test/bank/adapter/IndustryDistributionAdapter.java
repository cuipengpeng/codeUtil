package com.test.bank.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.test.bank.R;
import com.test.bank.bean.IndustryDistributionBean;
import com.test.bank.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 */

public class IndustryDistributionAdapter extends RecyclerView.Adapter<IndustryDistributionAdapter.ViewHolder> {
    Context mContext;
    List<IndustryDistributionBean.IndustryConf> mDataList = new ArrayList<>();

    public IndustryDistributionAdapter(Context context) {
        this.mContext = context;
        this.mDataList.clear();
    }

    public void upateData(boolean isRefresh, List<IndustryDistributionBean.IndustryConf> data) {
        if (isRefresh) {
            this.mDataList.clear();
        }
        this.mDataList.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_industry_distribution, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.keyTextView.setText(mDataList.get(position).getIndu_sname());
        holder.valueTextView.setText(StringUtil.moneyDecimalFormat2(mDataList.get(position).getSect_val_prop()+"")+"%");
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_industryDistributionFragment_item_key)
        TextView keyTextView ;
        @BindView(R.id.tv_industryDistributionFragment_item_value)
        TextView valueTextView ;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
