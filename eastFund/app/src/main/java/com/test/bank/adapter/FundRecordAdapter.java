package com.test.bank.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.test.bank.R;
import com.test.bank.bean.FundTradeRecordBean;
import com.test.bank.utils.StringUtil;
import com.test.bank.utils.UIUtils;
import com.test.bank.view.activity.FundTradeRecordDetailActivity;

import java.util.List;

/**
 * Created by 55 on 2018/1/16.
 */

public class FundRecordAdapter extends RecyclerView.Adapter<FundRecordAdapter.FundRecordViewHolder> {

    private Context context;
    private List<FundTradeRecordBean> mData;

    public FundRecordAdapter(Context context, List<FundTradeRecordBean> mData) {
        this.context = context;
        this.mData = mData;
    }

    @Override
    public FundRecordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FundRecordViewHolder(LayoutInflater.from(context).inflate(R.layout.item_fund_record, parent, false));
    }

    @Override
    public void onBindViewHolder(FundRecordViewHolder holder, final int position) {
        if (mData.get(position).getTradetype() == 3) {      //买卖
            holder.imageView.setImageResource(R.drawable.icon_fund_buy);
            UIUtils.setText(holder.tvAmount, mData.get(position).getTrademoney() + "元");
        } else {
            holder.imageView.setImageResource(R.drawable.icon_fund_sale);
            UIUtils.setText(holder.tvAmount, mData.get(position).getTradeshare() + "份");
        }
        UIUtils.setText(holder.tvFundName, mData.get(position).getFundname());  //基金名称
        UIUtils.setText(holder.tvDate, StringUtil.transferTimeStampToDate(mData.get(position).getTradedate()));

        UIUtils.setText(holder.tvStatus, getTradeStatus(mData.get(position), holder.tvStatus));
        if (position == mData.size() - 1) {
            holder.vBottom.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FundTradeRecordDetailActivity.open(context, mData.get(position).getTradeno());
            }
        });
    }

    private String getTradeStatus(FundTradeRecordBean bean, TextView tvStatus) {
        String result = "";
        if (bean.getTradetype() == 3) { //买入
            if (0 == bean.getTradestatus()) {
                if ("0".equals(bean.getPaystatus()) || "2".equals(bean.getPaystatus())) {
                    result = "确认中";
                    tvStatus.setTextColor(ContextCompat.getColor(context, R.color.color_f35857));
                } else if ("1".equals(bean.getPaystatus())) {
                    result = "支付失败";
                    tvStatus.setTextColor(ContextCompat.getColor(context, R.color.color_7e819b));
                }
            } else if (1 == bean.getTradestatus()) {
                result = "确认成功";
                tvStatus.setTextColor(ContextCompat.getColor(context, R.color.color_0084ff));
            } else if (2 == bean.getTradestatus()) {
                result = "已撤单";
                tvStatus.setTextColor(ContextCompat.getColor(context, R.color.color_7e819b));
            } else if (3 == bean.getTradestatus()) {
                if ("0".equals(bean.getPaystatus())) {
                    result = "确认失败";
                    tvStatus.setTextColor(ContextCompat.getColor(context, R.color.color_7e819b));
                } else if ("1".equals(bean.getPaystatus())) {
                    result = "支付失败";
                    tvStatus.setTextColor(ContextCompat.getColor(context, R.color.color_7e819b));
                } else if ("2".equals(bean.getPaystatus())) {
                    result = "确认中";
                    tvStatus.setTextColor(ContextCompat.getColor(context, R.color.color_f35857));
                }
            } else if (4 == bean.getTradestatus()) {
                result = "确认失败";
                tvStatus.setTextColor(ContextCompat.getColor(context, R.color.color_7e819b));
            }
        } else {      //卖出
            if (0 == bean.getTradestatus()) {
                result = "确认中";
                tvStatus.setTextColor(ContextCompat.getColor(context, R.color.color_f35857));
            } else if (1 == bean.getTradestatus()) {
                result = "确认成功";
                tvStatus.setTextColor(ContextCompat.getColor(context, R.color.color_0084ff));
            } else if (2 == bean.getTradestatus()) {
                result = "已撤单";
                tvStatus.setTextColor(ContextCompat.getColor(context, R.color.color_7e819b));
            } else if (3 == bean.getTradestatus()) {
                result = "确认失败";
                tvStatus.setTextColor(ContextCompat.getColor(context, R.color.color_7e819b));
            }
        }
        return result;
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    class FundRecordViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView tvFundName;
        TextView tvAmount;
        TextView tvDate;
        TextView tvStatus;
        View vBottom;

        public FundRecordViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_itemFundRecord);
            tvFundName = itemView.findViewById(R.id.tv_itemFundRecord_fundName);
            tvAmount = itemView.findViewById(R.id.tv_itemFundRecord_amount);
            tvDate = itemView.findViewById(R.id.tv_itemFundRecord_date);
            tvStatus = itemView.findViewById(R.id.tv_itemFundRecord_status);
            vBottom = itemView.findViewById(R.id.v_itemFundRecord_bottom);
        }
    }
}
