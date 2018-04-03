package com.jf.jlfund.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jf.jlfund.R;
import com.jf.jlfund.bean.BuyGoodFundBean;
import com.jf.jlfund.utils.StringUtil;
import com.jf.jlfund.utils.UIUtils;
import com.jf.jlfund.view.activity.BuyGoodFundDetailActivity;

import java.util.List;

/**
 * Created by 55 on 2017/12/8.
 */

public class BuyGoodFundAdapter extends RecyclerView.Adapter<BuyGoodFundAdapter.BuyGoodFundViewHolder> {

    private Context context;
    private List<BuyGoodFundBean> mData;

    public BuyGoodFundAdapter(Context context, List<BuyGoodFundBean> buyGoodFundList) {
        this.context = context;
        this.mData = buyGoodFundList;
    }

    @Override
    public BuyGoodFundViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BuyGoodFundViewHolder(LayoutInflater.from(context).inflate(R.layout.item_make_money_buy_good_fund, parent, false));
    }

    @Override
    public void onBindViewHolder(final BuyGoodFundViewHolder holder, int position) {
        UIUtils.setText(holder.tvPeriod, mData.get(position).getWealthTypeName(), "--");
        UIUtils.setRate(holder.tvRate, mData.get(position).getProfit());
        UIUtils.setText(holder.tvTitle, mData.get(position).getTitle(), "--");
        UIUtils.setText(holder.tvDesc, mData.get(position).getSecondTitle(), "--");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BuyGoodFundDetailActivity.open(context, mData.get(holder.getAdapterPosition()).getId(), true);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    class BuyGoodFundViewHolder extends RecyclerView.ViewHolder {
        TextView tvRate;
        TextView tvTitle;
        TextView tvDesc;
        TextView tvPeriod;

        public BuyGoodFundViewHolder(View itemView) {
            super(itemView);
            tvRate = itemView.findViewById(R.id.tv_item_buy_good_fund_rate);
            tvTitle = itemView.findViewById(R.id.tv_item_buy_good_fund_title);
            tvDesc = itemView.findViewById(R.id.tv_item_buy_good_fund_desc);
            tvPeriod = itemView.findViewById(R.id.tv_item_buy_good_fund_period);
        }
    }
}
