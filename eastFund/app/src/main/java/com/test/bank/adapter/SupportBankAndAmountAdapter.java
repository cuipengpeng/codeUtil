package com.test.bank.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.test.bank.R;
import com.test.bank.bean.SupportBankAndAmountBean;
import com.test.bank.utils.ImageUtils;
import com.test.bank.utils.StringUtil;
import com.test.bank.utils.UIUtils;

import java.util.List;

/**
 * Created by 55 on 2018/2/9.
 */

public class SupportBankAndAmountAdapter extends RecyclerView.Adapter<SupportBankAndAmountAdapter.SupportBankViewHolder> {

    private Context context;
    private List<SupportBankAndAmountBean> mData;

    public SupportBankAndAmountAdapter(Context context, List<SupportBankAndAmountBean> mData) {
        this.context = context;
        this.mData = mData;
    }

    @Override
    public SupportBankViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SupportBankViewHolder(LayoutInflater.from(context).inflate(R.layout.item_support_bank_and_amount, parent, false));
    }

    @Override
    public void onBindViewHolder(SupportBankViewHolder holder, int position) {
        ImageUtils.displayImage(context, mData.get(position).getIcon(), holder.ivBankIcon);
        UIUtils.setText(holder.tvBankName, mData.get(position).getBankname());
        UIUtils.setText(holder.tvBankAmount, "单笔限额" + StringUtil.transferToDollar(mData.get(position).getLimitonce()) + ",单日限额" + StringUtil.transferToDollar(mData.get(position).getLimitday()));
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    class SupportBankViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBankIcon;
        TextView tvBankName;
        TextView tvBankAmount;

        public SupportBankViewHolder(View itemView) {
            super(itemView);
            ivBankIcon = itemView.findViewById(R.id.iv_item_supportBank);
            tvBankName = itemView.findViewById(R.id.tv_item_supportBank_bankName);
            tvBankAmount = itemView.findViewById(R.id.tv_item_supportBank_amount);
        }
    }
}
