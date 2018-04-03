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
import com.jfbank.qualitymarket.listener.DialogListener;
import com.jfbank.qualitymarket.model.SalesProgressBean;
import com.jfbank.qualitymarket.mvp.SalesProgressMVP;
import com.jfbank.qualitymarket.util.CommonUtils;
import com.jfbank.qualitymarket.util.DialogUtils;
import com.jfbank.qualitymarket.util.LogUtil;
import com.jfbank.qualitymarket.util.StringUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 功能：售后进度列表适配器<br>
 * 作者：赵海<br>
 * 时间： 2017/4/20 0020<br>.
 * 版本：1.2.0
 */

public class SalesProgressAdapter extends RecyclerView.Adapter<SalesProgressAdapter.ViewHolder> {
    SalesProgressMVP.Presenter mPresenter;
    Context mContext;
    List<SalesProgressBean> data = new ArrayList<>();
    String orderReturnGoodsStatus[] = {"审核退货中", "待寄送商品", "拒绝退货", "退货受理中", "退货受理中", "退货完成", "已取消"};
    String orderChangeGoodsStatus[] = {"换货审核中", "待买家发货", "拒绝换货", "待收取换货", "换货完成", "已取消", "待商户发货", "待买家收货"};

    public SalesProgressAdapter(Context context, SalesProgressMVP.Presenter presenter) {
        this.mContext = context;
        this.mPresenter = presenter;
    }

    /**
     * 刷新列表数据
     *
     * @param isRefresh
     * @param data
     */
    public void upateData(boolean isRefresh, List<SalesProgressBean> data) {
        if (isRefresh) {
            this.data.clear();
        }
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    /**
     * 刷新数据，取消订单成功
     */
    public void updateCancelOrderSuccess(int position) {
        data.get(position).setCancelButtonDisplay(0);
        if (data.get(position).getIdentification() == 1) {
            data.get(position).setRefundOrderStatus(6);
        } else if (data.get(position).getIdentification() == 2) {
            data.get(position).setRefundOrderStatus(5);
        }
        notifyItemChanged(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_salesprogress, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.tvOrderid.setText(data.get(position).getRefundOrderId());
        if (data.get(position).getIdentification() == 1) {
            holder.tvOrderidType.setText("退货订单号：");
            holder.tvOrderState.setText(orderReturnGoodsStatus[data.get(position).getRefundOrderStatus()]);
        } else if (data.get(position).getIdentification() == 2) {
            holder.tvOrderidType.setText("换货订单号：");
            holder.tvOrderState.setText(orderChangeGoodsStatus[data.get(position).getRefundOrderStatus()]);
        }
        holder.tvOrderApplytime.setText("申请时间：" + data.get(position).getCreateTime());
        holder.tvOrderGoodsname.setText(data.get(position).getProductName());
        holder.tvOrderPaymoney.setText("一次性付：" + data.get(position).getProductPrice() + "元");

        LogUtil.printLog("position="+position+"--data.get(position).getSkuParameters()="+data.get(position).getSkuParameters());
        if (StringUtil.isNull(data.get(position).getSkuParameters())){
            holder.spuParamsTextView.setVisibility(View.GONE);
        }else{
            holder.spuParamsTextView.setText(data.get(position).getSkuParameters());
            holder.spuParamsTextView.setVisibility(View.VISIBLE);
        }
        holder.tvOrderFirstpay.setText(Html.fromHtml("<font color='#999999'>首付:</font>" + "<font color='#333333'>" + data.get(position).getFirstPayment() + "</font>" + "<font color='#999999'>元</font>"));
        holder.tvOrderPayofmonth.setText(Html.fromHtml("<font color='#999999'>月付:</font>" + "<font color='#333333'>" + data.get(position).getMonthPay() + "*" + data.get(position).getMonthnum() + "</font>" + "<font color='#999999'>期</font>"));
        if (TextUtils.isEmpty(data.get(position).getProductImage())) {
            Picasso.with(mContext).load(R.drawable.load_image_place_holder).into(holder.ivOrderGoodspic);
        } else {
            Picasso.with(mContext).load(data.get(position).getProductImage()).placeholder(R.drawable.load_image_place_holder).into(holder.ivOrderGoodspic);
        }
        if (data.get(position).getCancelButtonDisplay() == 1) {
            holder.tvCacelapply.setVisibility(View.VISIBLE);
        } else {
            holder.tvCacelapply.setVisibility(View.GONE);
        }
        holder.tvCacelapply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//取消订单
                DialogUtils.showTwoBtnDialog(mContext, null, "撤销申请后您将不能重新发起售后申请，是否确认撤销？", null, "确认", new DialogListener.DialogClickLisenter() {
                    @Override
                    public void onDialogClick(int type) {
                        if (type == CLICK_SURE) {
                            mPresenter.cancelOrderId(data.get(position).getRefundOrderId(), data.get(position).getIdentification(), position);
                        }
                    }

                });
            }
        });
        holder.tvQueryprogress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//进度查询
                mPresenter.startProgressActivity(data.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return CommonUtils.isEmptyList(data) ? 0 : data.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.tv_orderid_type)
        TextView tvOrderidType;
        @InjectView(R.id.tv_orderid)
        TextView tvOrderid;
        @InjectView(R.id.tv_order_state)
        TextView tvOrderState;
        @InjectView(R.id.iv_order_goodspic)
        ImageView ivOrderGoodspic;
        @InjectView(R.id.tv_order_goodsname)
        TextView tvOrderGoodsname;
        @InjectView(R.id.tv_order_paymoney)
        TextView tvOrderPaymoney;
        @InjectView(R.id.tv_order_applytime)
        TextView tvOrderApplytime;
        @InjectView(R.id.tv_order_firstpay)
        TextView tvOrderFirstpay;
        @InjectView(R.id.tv_order_payofmonth)
        TextView tvOrderPayofmonth;
        @InjectView(R.id.tv_cacelapply)
        TextView tvCacelapply;
        @InjectView(R.id.tv_queryprogress)
        TextView tvQueryprogress;
        @InjectView(R.id.tv_AfterSalesActivity_progressCheckItem_spuParams)
        TextView spuParamsTextView;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }
}
