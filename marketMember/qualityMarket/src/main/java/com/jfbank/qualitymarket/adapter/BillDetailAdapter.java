package com.jfbank.qualitymarket.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.callback.IBillPendingCallBack;
import com.jfbank.qualitymarket.model.AgingBean;
import com.jfbank.qualitymarket.util.CommonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 功能：分期账单详情适配器<br>
 * 作者：赵海<br>
 * 时间： 2016/12/29 0029<br>.
 * 版本：1.2.0
 */

public class BillDetailAdapter extends BaseAdapter {
    Context mContext;
    IBillPendingCallBack.IAdapterCallBack iCallBackBill;
    List<AgingBean> data = new ArrayList<>();
    boolean hasCurrentBill = false;
    boolean hasOverDue = false;
    public int firstCheckPosition = 0;
    public boolean isAllSY = false;
    float benjing = 0.0f;
    float fuwufei = 0.0f;
    float weiyuejin = 0.0f;

    public BillDetailAdapter(Context context, IBillPendingCallBack.IAdapterCallBack iCallBackBill) {
        this.mContext = context;
        this.iCallBackBill = iCallBackBill;
        this.mContext = context;
    }

    /**
     * 更新数据
     *
     * @param newData
     */
    public void updateData(List<AgingBean> newData) {
        boolean isCheckFirst = true;
        if (newData == null) {
            this.data.clear();
            notifyDataSetChanged();
        }
        this.data = newData;
        hasCurrentBill = false;
        hasOverDue = false;
        isAllSY = false;
        checkOverOrCurrent();
        for (int i = 0; i < this.data.size(); i++) {
            if (hasOverDue) {
                if (data.get(i).getRemainDaysStatus() != 0) {//不是逾期
                    data.get(i).setCheckEnable(false);
                } else {
                    data.get(i).setCheckEnable(true);
                }
            } else {
                if (hasCurrentBill) {
                    if (data.get(i).getRemainDaysStatus() != 1) {//不是当前账单
                        data.get(i).setCheckEnable(false);
                    } else {
                        data.get(i).setCheckEnable(true);
                    }
                } else {
                    data.get(i).setCheckEnable(true);
                }
            }
            if (data.get(i).getRemainDaysStatus() == 3) {
                data.get(i).setCheckEnable(false);
            }
            if (data.get(i).getRepayStatusing() == 1) {
                data.get(i).setCheckEnable(false);
            }
            if (isCheckFirst && data.get(i).isCheckEnable()) {
                firstCheckPosition = i;
                isCheckFirst = false;
            }

        }
        notifyDataSetChanged();
    }

    /**
     * 是否有当前还款或者逾期
     *
     * @return
     */
    public void checkOverOrCurrent() {
        boolean isOverDueContinue = true;
        boolean isCurrentContinue = true;
        int tqhkSize = 0;
        for (int i = 0; i < data.size(); i++) {
            if (isOverDueContinue && data.get(i).getRemainDaysStatus() == 0) {
                hasOverDue = true;
                isOverDueContinue = false;
            }
            if (isCurrentContinue && data.get(i).getRemainDaysStatus() == 1) {
                hasCurrentBill = true;
                isCurrentContinue = false;
            }
        }
    }

    public List<AgingBean> getData() {
        return data;
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
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_billdetail, null);

            viewHolder.llBilldetailItem = (LinearLayout) convertView.findViewById(R.id.ll_billdetailitem);
            viewHolder.ivBillpendingCheck = (ImageView) convertView.findViewById(R.id.iv_billpending_check);
            viewHolder.ivBillpendingMoney = (TextView) convertView.findViewById(R.id.iv_billpending_money);
            viewHolder.ivBillpendingNumber = (TextView) convertView.findViewById(R.id.iv_billpending_number);
            viewHolder.ivBillpendingTime = (TextView) convertView.findViewById(R.id.iv_billpending_time);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.llBilldetailItem.setEnabled(data.get(position).isCheckEnable());
        viewHolder.ivBillpendingCheck.setEnabled(data.get(position).isCheckEnable());
        viewHolder.ivBillpendingCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (data.get(position).isCheck()) {
                    data.get(position).setCheck(false);
                    for (int i = firstCheckPosition; i < data.size(); i++) {
                        if (data.get(i).isCheckEnable()) {
                            data.get(i).setCheckAll(false);
                        }
                    }
                } else {
                    data.get(position).setCheck(true);
                    boolean isFormlessAll = isFormlessAll();
                    for (int i = firstCheckPosition; i < data.size(); i++) {//第一个商品开始循环
                        if (isFormlessAll) {//没有全选
                            data.get(i).setCheckAll(false);
                        } else {
                            data.get(i).setCheckAll(true);
                        }
                    }
                }
                notifyDataSetChanged();
            }
        });
        viewHolder.ivBillpendingCheck.setSelected(data.get(position).isCheck());
        viewHolder.ivBillpendingMoney.setText(data.get(position).getFenqiAmount() + "元");
        viewHolder.ivBillpendingTime.setText(data.get(position).getRemainDays());
        viewHolder.ivBillpendingNumber.setText("第" + data.get(position).getStage() + "期");
        return convertView;
    }

    /**
     * 同一个商品分期是否跨越还款
     *
     * @return
     */
    private boolean isFormlessAll() {
        boolean isFormless = false;
        for (int i = firstCheckPosition; i < data.size(); i++) {
            if (!data.get(i).isCheck() && data.get(i).isCheckEnable()) {
                isFormless = true;
            }
        }
        return isFormless;

    }

    /**
     * 是否有越期
     *
     * @return
     */
    public boolean hasFormless() {
        int noChcek = data.size();
        for (int i = firstCheckPosition; i < data.size(); i++) {
            if (!data.get(i).isCheck() && data.get(i).isCheckEnable()) {
                noChcek = i;
            }
            if (data.get(i).isCheck() && data.get(i).isCheckEnable()) {
                if (noChcek < i) {
                    return true;
                }
            }
        }
        return false;

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
                if (data.get(i).isCheck() && data.get(i).isCheckEnable()) {
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
                    if (data.get(i).getRemainDaysStatus() == 0) {
                        fuwufei = fuwufei + data.get(i).getInterestAmount() + data.get(i).getOverdueAmount();
                    }
                } else {//无逾期
                    if (hasCurrentBill) {
                        fuwufei = fuwufei + data.get(i).getInterestAmount();
                        //当前
                    } else {
                        //无当前和逾期
                        //选中
                        if (data.get(data.size() - 1).getStage() - data.get(firstCheckPosition).getStage() > 0) {
                            //超过一期
                            if (data.get(i).isCheckAll())//全选
                            {
                                if (firstCheckPosition == i) {//第1笔加违约金和利息
                                    isAllSY = true;
                                    fuwufei = fuwufei + data.get(i).getInterestAmount();
                                    weiyuejin = weiyuejin + data.get(i).getViolateamtAmount();
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
        iCallBackBill.onShowPay(isShow, isCheck, fuwufei + weiyuejin + benjing);
    }

    /**
     * 本金、违约金、服务费
     *
     * @return
     */
    public float[] getToast() {
        return new float[]{benjing, fuwufei, weiyuejin};

    }

    class ViewHolder {
        ImageView ivBillpendingCheck;
        TextView ivBillpendingMoney;
        TextView ivBillpendingNumber;
        TextView ivBillpendingTime;
        LinearLayout llBilldetailItem;
    }
}
