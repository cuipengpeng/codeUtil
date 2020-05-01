package com.jfbank.qualitymarket.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.model.SaleApplyOrderBean;
import com.jfbank.qualitymarket.mvp.SalesApplyMVP;
import com.jfbank.qualitymarket.util.CommonUtils;
import com.jfbank.qualitymarket.util.ConstantsUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 功能：售后可申请列表适配器<br>
 * 作者：赵海<br>
 * 时间： 2017/4/20 0020<br>.
 * 版本：1.2.0
 */

public class SalesApplyAdapter extends RecyclerView.Adapter<SalesApplyAdapter.ViewHolder> {
    SalesApplyMVP.Presenter mPresenter;
    Context mContext;
    List<SaleApplyOrderBean> data = new ArrayList<>();

    public SalesApplyAdapter(Context context, SalesApplyMVP.Presenter presenter) {
        this.mContext = context;
        this.mPresenter = presenter;
    }

    public void upateData(boolean isRefresh, List<SaleApplyOrderBean> data) {
        if (isRefresh) {
            this.data.clear();
        }
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_salesapply, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.tvOrderid.setText(data.get(position).getOrderId());
        holder.tvOrderApplytime.setText(data.get(position).getOrderTime());
        holder.tvOrderGoodsname.setText(data.get(position).getProductName());
        holder.tvOrderPaymoney.setText("一次性付：" + data.get(position).getProductPrice()+"元");
        holder.spuParamsTextView.setText(data.get(position).getSkuParameters());
        holder.tvOrderFirstpay.setText(Html.fromHtml("<font color='#999999'>首付:</font>" + "<font color='#333333'>" + data.get(position).getFirstPayment() + "</font>" + "<font color='#999999'>元</font>"));
        holder.tvOrderPayofmonth.setText(Html.fromHtml("<font color='#999999'>月付:</font>" + "<font color='#333333'>" + data.get(position).getMonthPay() + "</font>" + "<font color='#333333'>"+ "*" + data.get(position).getMonthNumber() + "</font>" + "<font color='#999999'>期</font>"));
        holder.tvOrderGoodsnum.setText(Html.fromHtml("<font color='#999999'>共</font>" + "<font color='#333333'>" + data.get(position).getBuycount() + "</font>" + "<font color='#999999'>件商品</font>"));
        if (TextUtils.isEmpty(data.get(position).getProductImage())) {
            Picasso.with(mContext).load(R.drawable.load_image_place_holder).into(holder.ivOrderGoodspic);
        } else {
            Picasso.with(mContext).load(data.get(position).getProductImage()).placeholder(R.drawable.load_image_place_holder).into(holder.ivOrderGoodspic);
        }

        int status = data.get(position).getOrderStatus();
        if (status < 0 || status > 8) {//订单状态确定
            status = 0;
        }
        holder.tvOrderState.setText(ConstantsUtil.ORDER_STATUS[status]);
        if (data.get(position).getAftermarketButton() == 1) {
            holder.tvApply.setText("退款成功");
        } else {
            holder.tvApply.setText("申请售后");
        }
        if (data.get(position).getAftermarketButton() == 2) {//可点
            holder.tvApply.setEnabled(true);
        } else {
            holder.tvApply.setEnabled(false);
        }

        holder.tvApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.applyAfterSales(data.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return CommonUtils.isEmptyList(data) ? 0 : data.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.tv_orderid)
        TextView tvOrderid;
        @InjectView(R.id.tv_order_applytime)
        TextView tvOrderApplytime;
        @InjectView(R.id.tv_order_state)
        TextView tvOrderState;
        @InjectView(R.id.iv_order_goodspic)
        ImageView ivOrderGoodspic;
        @InjectView(R.id.tv_order_goodsname)
        TextView tvOrderGoodsname;
        @InjectView(R.id.tv_order_paymoney)
        TextView tvOrderPaymoney;
        @InjectView(R.id.tv_order_goodsnum)
        TextView tvOrderGoodsnum;
        @InjectView(R.id.tv_order_firstpay)
        TextView tvOrderFirstpay;
        @InjectView(R.id.tv_order_payofmonth)
        TextView tvOrderPayofmonth;
        @InjectView(R.id.tv_apply)
        TextView tvApply;
        @InjectView(R.id.tv_AfterSalesActivity_afterSaleItem_spuParams)
        TextView spuParamsTextView;

        ViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }
}
