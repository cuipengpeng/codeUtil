package com.test.bank.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.test.bank.R;
import com.test.bank.bean.GraspChanceBean;
import com.test.bank.utils.UIUtils;
import com.test.bank.view.activity.GraspChanceDetailActivity;

import java.util.List;

/**
 * Created by 55 on 2017/12/8.
 */

public class GraspChanceAdapter extends RecyclerView.Adapter<GraspChanceAdapter.GraspChanceViewHolder> {

    private Context context;
    private List<GraspChanceBean> mData;

    public GraspChanceAdapter(Context context, List<GraspChanceBean> graspChanceBeanList) {
        this.context = context;
        this.mData = graspChanceBeanList;
    }

    @Override
    public GraspChanceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GraspChanceViewHolder(LayoutInflater.from(context).inflate(R.layout.item_grasp_chance, parent, false));
    }

    @Override
    public void onBindViewHolder(final GraspChanceViewHolder holder, final int position) {
        if (!TextUtils.isEmpty(mData.get(position).getProfit())) {
            double profit = Double.parseDouble(mData.get(position).getProfit());
            if (profit == 0D || Double.isNaN(profit)) {
                holder.rlLeft.setBackgroundResource(R.drawable.border_circle_b9bbca);
            } else if (profit > 0) {
                holder.rlLeft.setSelected(true);
                holder.tvPeriod.setTextColor(ContextCompat.getColor(context, R.color.color_f35857));
            } else {
                holder.rlLeft.setSelected(false);
                holder.tvPeriod.setTextColor(ContextCompat.getColor(context, R.color.color_18b293));
            }
            UIUtils.setRate(holder.tvRate, profit);
        } else {
            holder.rlLeft.setBackgroundResource(R.drawable.border_circle_b9bbca);
            holder.tvRate.setText("--");
        }
        UIUtils.setText(holder.tvTitle, mData.get(position).getTitle(), "--");
        UIUtils.setText(holder.tvDesc, mData.get(position).getFundsName() + " | " + mData.get(position).getFundscode(), "--");
        UIUtils.setText(holder.tvPeriod, mData.get(position).getWealthTypeName(), "--");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraspChanceDetailActivity.open(context, mData.get(position).getId(), mData.get(position).getFundscode());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    class GraspChanceViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout rlLeft;
        TextView tvTitle;
        TextView tvDesc;
        TextView tvRate;
        TextView tvPeriod;

        public GraspChanceViewHolder(View itemView) {
            super(itemView);
            rlLeft = itemView.findViewById(R.id.rl_left_content);
            tvRate = itemView.findViewById(R.id.tv_item_grasp_chance_rate);
            tvPeriod = itemView.findViewById(R.id.tv_item_grasp_chance_period);
            tvTitle = itemView.findViewById(R.id.tv_item_grasp_chance_title);
            tvDesc = itemView.findViewById(R.id.tv_item_grasp_chance_desc);
        }
    }
}
