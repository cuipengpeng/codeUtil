package com.jfbank.qualitymarket.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.activity.BillDetailActivity;
import com.jfbank.qualitymarket.callback.IBillPendingCallBack;
import com.jfbank.qualitymarket.model.BillBean;
import com.jfbank.qualitymarket.util.CommonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 功能：待还款账单<br>
 * 作者：赵海<br>
 * 时间： 2016/12/29 0029<br>.
 * 版本：1.2.0
 */

public class BillPendingAdapter extends BaseAdapter {
    Fragment mFragment;
    Context mContext;
    List<BillBean> data = new ArrayList<>();
    IBillPendingCallBack.IAdapterCallBack iCallBackFrag;
    boolean hasCurrentBill = false;
    boolean hasOverDue = false;
    public boolean isAllSY = false;
    float benjing = 0.0f;
    float fuwufei = 0.0f;
    float weiyuejin = 0.0f;

    public List<BillBean> getData() {
        return data;
    }

    /**
     * 更新数据
     *
     * @param newData
     */
    public void updateData(List<BillBean> newData) {
        if (newData == null) {
            data.clear();
            notifyDataSetChanged();
            return;
        }

        this.data = newData;
        hasOverDue = false;
        hasCurrentBill = false;
        isAllSY = false;
        checkOverOrCurrent();
        Map<String, String> mapFirstPosition = new HashMap<>();
        for (int i = 0; i < this.data.size(); i++) {
            String integer = mapFirstPosition.get(data.get(i).getOrderId());
            if (integer != null) {//和前一笔为同一个商品
                data.get(i).setFirstPosition(Integer.parseInt(integer));
            } else {
                data.get(i).setFirstPosition(i);
                mapFirstPosition.put(data.get(i).getOrderId(), i + "");
            }
            setCheckEnable(data.get(i));
            if (data.get(i).getRepayStatusing() == 1) {
                data.get(i).setCheckEnable(false);
            }
        }
        notifyDataSetChanged();
    }

    /**
     * 标志是否逾期了
     *
     * @param billBean
     */
    public void setCheckEnable(BillBean billBean) {
        if (hasOverDue) {
            if (billBean.getBillRemainDaysStatus() != 0) {//不是逾期
                billBean.setCheckEnable(false);
            } else {
                billBean.setCheckEnable(true);
            }
        } else {
            if (hasCurrentBill) {
                if (billBean.getBillRemainDaysStatus() != 1) {//不是当前账单
                    billBean.setCheckEnable(false);
                } else {
                    billBean.setCheckEnable(true);
                }
            } else {
                billBean.setCheckEnable(true);
            }
        }
    }

    public BillPendingAdapter(Fragment mFragment, IBillPendingCallBack.IAdapterCallBack iCallBackFrag) {
        this.mFragment = mFragment;
        mContext = mFragment.getContext();
        this.iCallBackFrag = iCallBackFrag;
    }

    @Override
    public int getCount() {
        return CommonUtils.isEmptyList(data) ? 0 : data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_billpending, null);
            viewHolder = new ViewHolder();

            viewHolder.llBillpendingItem = (LinearLayout) convertView.findViewById(R.id.ll_billpendingitem);
            viewHolder.ivBillpendingCheck = (ImageView) convertView.findViewById(R.id.iv_billpending_check);
            viewHolder.tvBillpendingMoney = (TextView) convertView.findViewById(R.id.tv_billpending_money);
            viewHolder.tvBillpendingDes = (TextView) convertView.findViewById(R.id.tv_billpending_des);
            viewHolder.tvBillpendingTime = (TextView) convertView.findViewById(R.id.tv_billpending_time);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.llBillpendingItem.setEnabled(data.get(position).isCheckEnable());
        viewHolder.ivBillpendingCheck.setEnabled(data.get(position).isCheckEnable());
        viewHolder.ivBillpendingCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int firstPosition = data.get(position).getFirstPosition();
                if (data.get(position).isCheck()) {//选中则取消全选中
                    data.get(position).setCheck(false);
                    for (int i = firstPosition; i < data.size(); i++) {
                        if (data.get(i).getFirstPosition() == firstPosition) {
                            data.get(i).setCheckAll(false);
                        }
                    }
                } else {
                    data.get(position).setCheck(true);
                    boolean isYuQi = isFormless(position);
                    for (int i = data.get(position).getFirstPosition(); i < data.size(); i++) {//第一个商品开始循环
                        if (data.get(i).getFirstPosition() == firstPosition) {//只会更新当前商品
                            if (isYuQi) {
                                data.get(i).setCheckAll(false);
                            } else {
                                data.get(i).setCheckAll(true);
                            }
                        }
                    }
                }
                notifyDataSetChanged();
            }
        });
        viewHolder.llBillpendingItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, BillDetailActivity.class);
                intent.putExtra("orderId", getData().get(position).getOrderId());
                mFragment.startActivity(intent);
                mFragment.getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
        viewHolder.tvBillpendingDes.setText("[" + data.get(position).getStage() + "/" + data.get(position).getTotalPeriod() + "]" + data.get(position).getProductName());
        viewHolder.tvBillpendingMoney.setText(data.get(position).getTotalAmount() + "元");
        viewHolder.tvBillpendingTime.setText(data.get(position).getRemainDays());
        viewHolder.ivBillpendingCheck.setSelected(data.get(position).isCheck());
        return convertView;
    }

    /**
     * 同一个商品分期是否跨越还款
     *
     * @param position
     * @return
     */
    private boolean isFormless(int position) {
        boolean isFormless = false;
        int firstPosition = data.get(position).getFirstPosition();
        for (int i = data.get(position).getFirstPosition(); i < data.size(); i++) {
            if (!data.get(i).isCheck() && data.get(i).isCheckEnable() && firstPosition == data.get(i).getFirstPosition()) {
                isFormless = true;
            }
        }
        return isFormless;

    }

    /**
     * 同一个商品分期是否跨越还款
     *
     * @return
     */
    public boolean isFormless() {
        boolean isFormless = false;
        int dataSize = data.size();
        for (int i = 0; i < dataSize; i++) {
            int currentPosition = data.get(i).getFirstPosition();
            int noCheck = dataSize;
            if (currentPosition == i) {
                for (int j = currentPosition; j < dataSize; j++) {//循环该商品
                    if (!data.get(j).isCheck() && data.get(j).isCheckEnable() && currentPosition == data.get(j).getFirstPosition()) {//该商品有没有选择的
                        noCheck = j;
                    } else if (data.get(j).isCheck() && data.get(j).isCheckEnable() && currentPosition == data.get(j).getFirstPosition()) {//该商品选中
                        if (noCheck < j) {
                            return true;
                        }
                    }
                }
            }

        }
        return isFormless;
    }

    @Override
    public void notifyDataSetChanged() {
        checkisShowPay();
        super.notifyDataSetChanged();

    }

    /**
     * 是否显示立即支付和全选
     */
    private void checkisShowPay() {
        boolean isShow = false;
        boolean isCheck = true;
        boolean isContinueShow = true;
        benjing = 0.0f;
        fuwufei = 0.0f;
        weiyuejin = 0.0f;
        isAllSY = false;
        for (int i = 0; i < data.size(); i++) {
            if (isContinueShow) {
                if (data.get(i).isCheck()) {
                    isShow = true;
                    isContinueShow = false;
                }
            }
            if (isCheck)
                if (!data.get(i).isCheck() && data.get(i).isCheckEnable()) {
                    isCheck = false;
                }
            if (data.get(i).isCheck() && data.get(i).isCheckEnable()) {
                benjing = benjing + data.get(i).getPrincipalAmount();
                if (hasOverDue) {//逾期
                    if (data.get(i).getBillRemainDaysStatus() == 0) {
                        fuwufei = fuwufei + data.get(i).getInterestAmount() + data.get(i).getOverdueAmount();
                    }
                } else {//无逾期
                    if (hasCurrentBill) {//当前
                        fuwufei = fuwufei + data.get(i).getInterestAmount();
                    } else {
                        //无当前和逾期
                        //选中
                        if (data.get(i).getTotalPeriod() - data.get(data.get(i).getFirstPosition()).getStage() > 0) {
                            //超过一期
                            if (data.get(i).isCheckAll())//全选
                            {
                                if (data.get(i).getFirstPosition() == i) {//第1笔加违约金和利息
                                    isAllSY = true;
                                    fuwufei = fuwufei + data.get(i).getInterestAmount();
                                    weiyuejin = weiyuejin + data.get(i).getViolateamtAmount();
                                } else {
                                }
                            } else {
                                fuwufei = fuwufei + data.get(i).getInterestAmount();
                            }
                        } else {//只有一期
                            fuwufei = fuwufei + data.get(i).getInterestAmount();
                        }

                    }
                }
            }
        }
        iCallBackFrag.onShowPay(isShow, isCheck, benjing + fuwufei + weiyuejin);
    }


    /**
     * 本金、违约金、服务费
     *
     * @return
     */
    public float[] getToast() {
        return new float[]{benjing, fuwufei, weiyuejin};

    }


    /**
     * 是否有当前还款或者逾期
     *
     * @return
     */
    public void checkOverOrCurrent() {
        boolean isOverDueContinue = true;
        boolean isCurrentContinue = true;
        for (int i = 0; i < data.size(); i++) {
            if (isOverDueContinue && data.get(i).getBillRemainDaysStatus() == 0) {
                hasOverDue = true;
                isOverDueContinue = false;
            }
            if (isCurrentContinue && data.get(i).getBillRemainDaysStatus() == 1) {
                hasCurrentBill = true;
                isCurrentContinue = false;
            }
        }
    }

    class ViewHolder {
        ImageView ivBillpendingCheck;
        TextView tvBillpendingMoney;
        TextView tvBillpendingDes;
        TextView tvBillpendingTime;
        public LinearLayout llBillpendingItem;
    }
}
