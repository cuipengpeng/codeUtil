package com.jfbank.qualitymarket.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfbank.qualitymarket.AppContext;
import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.base.BaseFragment;
import com.jfbank.qualitymarket.model.BillBean;
import com.jfbank.qualitymarket.net.HttpRequest;
import com.jfbank.qualitymarket.util.ConstantsUtil;
import com.jfbank.qualitymarket.util.LogUtil;
import com.jfbank.qualitymarket.util.UserUtils;
import com.jfbank.qualitymarket.widget.LoadingAlertDialog;
import com.jfbank.qualitymarket.widget.TwinklingRefreshLayoutView;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.jfbank.qualitymarket.callback.AsyncResponseCallBack;
 import java.util.HashMap; import java.util.Map;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 已结清账单fragment
 *
 * @author 崔朋朋
 */

public class ClosedBillingFragment extends BaseFragment {
    public static final String TAG = MyAccountFragment.class.getName();
    @InjectView(R.id.iv_myOrderFragment_noData)
    ImageView noDataImageView;

    private List<BillBean> closedBillingList = new ArrayList<BillBean>();
    private ClosedBillingAdapter closedBillingAdapter;
    private int pageNo = 1;
    private int pageSize = 10;
    private int pageCount = -1;
    //防止当前页码在中间，用户上拉、下拉刷新时重复加载数据
    private int headPageNo = 1;//记录当前orderList中的顶部页码
    private int bottomPageNo = 1;//记录当前orderList中的底部页码
    //上拉刷新的基页码
    private final int BASE_PAGE_NO = 1;
    /**
     * 网编请求时加载框
     */
    private LoadingAlertDialog mDialog;
    /**
     * list
     */
    private TwinklingRefreshLayoutView refreshLayout;
    private ListView closedBillingListView;

    //	状态（BE_OVERDUE 逾期，ALREADY_REPAYMENT已结清，NO_REPAYMENT未还款,WAITING_SETTLE等待结算）（传0为查询所有账单）
    private String QUERY_BILL_INSTALMENT_URL = HttpRequest.QUERY_BILL_INSTALMENT;
    private boolean showStatusAlreadyRepayment = true;
    private String billStatus = BILL_STATUS_ALREADY_REPAYMENT;

    //逾期
    public static final String BILL_STATUS_BE_OVERDUE = "BE_OVERDUE";
    //已结清
    public static final String BILL_STATUS_ALREADY_REPAYMENT = "ALREADY_REPAYMENT";
    //未还款
    public static final String BILL_STATUS_NO_REPAYMENT = "NO_REPAYMENT";
    //等待结算
    public static final String BILL_STATUS_WAITING_SETTLE = "WAITING_SETTLE";
    //所有状态账单
    public static final String BILL_STATUS_ALL = "0";

    public static ClosedBillingFragment newInstance(String content) {
        ClosedBillingFragment fragment = new ClosedBillingFragment();
        fragment.closedBillingList.clear();
        fragment.billStatus = content;
        return fragment;
    }


    @SuppressWarnings("unchecked")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_closed_billing, container, false);
        ButterKnife.inject(this, view);


        closedBillingList.clear();
        refreshLayout = (TwinklingRefreshLayoutView) view.findViewById(R.id.refreshLayout);
        refreshLayout.setAutoLoadMore(true);
        refreshLayout.setEnableLoadmore(false,false);
        refreshLayout.setOverScrollBottomShow(false);
        //下拉刷新和上啦加载更多
        refreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(final TwinklingRefreshLayout refreshLayout) {//下拉刷新
                pageNo = 1;
                getBillInstalment(billStatus, false, true, false, showStatusAlreadyRepayment);
            }

            @Override
            public void onLoadMore(final TwinklingRefreshLayout refreshLayout) {//加载更多
                if (!showStatusAlreadyRepayment) {
                    pageNo += 1;
                }
                getBillInstalment(billStatus, false, false, false, showStatusAlreadyRepayment);
            }
        });
        closedBillingListView = (ListView) view.findViewById(R.id.lv_closedBillingFragment_closedeBillingList);
        closedBillingAdapter = new ClosedBillingAdapter(getActivity(), closedBillingList, billStatus);
        closedBillingListView.setAdapter(closedBillingAdapter);

        if (BILL_STATUS_ALL.equals(billStatus)) {
            QUERY_BILL_INSTALMENT_URL = HttpRequest.QUERY_ALL_BILL_INSTALMENT;
            showStatusAlreadyRepayment = false;
            refreshLayout.setEnableLoadmore(true);
        }

        noDataImageView.setVisibility(View.GONE);

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
//    	pageNo = currentMaxPageNo;
//    	pageNo = 1;
        headPageNo = pageNo;
        bottomPageNo = pageNo;
        getBillInstalment(billStatus, true, false, true, showStatusAlreadyRepayment);
    }

    @Override
    public String getPageName() {
        return "全部账单";
    }

    /**
     * 获取分期还款账单
     *
     * @param initRequest       是否是初始化请求
     * @param pullDownToRefresh 是否是下拉刷新
     * @param showLoadingDialog 是否显示网络加载框
     */
    private void getBillInstalment(String billStatus, final boolean initRequest, final boolean pullDownToRefresh, boolean showLoadingDialog, final boolean showStatusAlreadyRepayment) {
        noDataImageView.setVisibility(View.GONE);
        if (null == mDialog) {
            mDialog = new LoadingAlertDialog(getActivity());
        }
        if (showLoadingDialog)
            mDialog.show(ConstantsUtil.NETWORK_REQUEST_IN);

        Map<String,String> params = new HashMap<>();
        params.put("uid", AppContext.user.getUid());
        params.put("token", AppContext.user.getToken());
        params.put("billStatus", billStatus);
        params.put("pageNo", pageNo+"");

        HttpRequest.post(mContext, HttpRequest.QUALITY_MARKET_WEB_URL + QUERY_BILL_INSTALMENT_URL, params, new AsyncResponseCallBack() {

            @Override
            public void onFailed(String path, String msg) {
                if (mDialog.isShowing()) {
                    mDialog.dismiss();
                }
                Toast.makeText(ClosedBillingFragment.this.getActivity(), ConstantsUtil.ORIGIN_PAGE_FAIL_TO_CONNECT_SERVER, Toast.LENGTH_SHORT).show();
                if (initRequest||pullDownToRefresh) {
                    refreshLayout.finishRefreshing();
                } else {
                    refreshLayout.finishLoadmore();
                }
            }

            @Override
            public void onResult(String arg2) {
                if (mDialog.isShowing()) {
                    mDialog.dismiss();
                }
                String jsonStr = new String(arg2);
                LogUtil.printLog("获取分期还款账单：" + jsonStr);

                JSONObject jsonObject = JSON.parseObject(jsonStr);

                if (initRequest || pullDownToRefresh || showStatusAlreadyRepayment) {
                    closedBillingList.clear();
                }
                if (ConstantsUtil.RESPONSE_TOKEN_FAIL == jsonObject.getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
                    UserUtils.tokenFailDialog(ClosedBillingFragment.this.getActivity(), jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME), TAG);
                } else if ((ConstantsUtil.RESPONSE_SUCCEED == jsonObject.getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) || closedBillingList.size() > 0) {
                    pageCount = jsonObject.getIntValue("pageCount");
                    if (jsonObject.containsKey(ConstantsUtil.RESPONSE_DATA_JSON_ARRAY_FIELD_NAME)) {
                        JSONArray orderJsonArray = jsonObject.getJSONArray(ConstantsUtil.RESPONSE_DATA_JSON_ARRAY_FIELD_NAME);
                        BillBean order = null;
                        for (int i = 0; i < orderJsonArray.size(); i++) {
                            order = JSON.parseObject(orderJsonArray.get(i).toString(), BillBean.class);
                            if (pullDownToRefresh) {
                                closedBillingList.add(i, order);
                            } else {
                                closedBillingList.add(order);
                            }
                        }
                    } else {
                        if (!pullDownToRefresh) {
                            Toast.makeText(getActivity(), ConstantsUtil.NO_MORE_DATA_PROMPT, Toast.LENGTH_SHORT).show();
                        }
                    }
                    updateAdapterDataSet();

                    if (pullDownToRefresh) {
//						closedBillingListView.setSelection(pageSize);
                    }
                } else if ((ConstantsUtil.RESPONSE_NO_DATA == jsonObject.getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) && closedBillingList.size() <= 0) {
                    closedBillingList.clear();
                    updateAdapterDataSet();
                    noDataImageView.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(ClosedBillingFragment.this.getActivity(), "查询分期还款账单", Toast.LENGTH_SHORT).show();
                }
                if (initRequest||pullDownToRefresh) {
                    refreshLayout.finishRefreshing();
                } else {
                    refreshLayout.finishLoadmore();
                }
            }

        });
    }

    private void updateAdapterDataSet() {
        closedBillingAdapter.notifyDataSetChanged();
    }

    class ClosedBillingAdapter extends BaseAdapter {
        private List<BillBean> orderList;
        private Activity activity;
        private String billStatus;

        public ClosedBillingAdapter(Activity activity, List<BillBean> orderList, String billStatus) {
            super();
            this.orderList = orderList;
            this.activity = activity;
            this.billStatus = billStatus;
        }

        @Override
        public int getCount() {
            return orderList.size();
        }

        @Override
        public Object getItem(int position) {
            return orderList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.item_closed_bill, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.warningTextView.setVisibility(View.GONE);
            viewHolder.borderWarningView.setVisibility(View.GONE);
            viewHolder.borderwhite20dpView.setVisibility(View.GONE);
            viewHolder.billStatusTextView.setVisibility(View.GONE);


            final BillBean bill = orderList.get(position);
            if (ClosedBillingFragment.BILL_STATUS_ALREADY_REPAYMENT.equals(billStatus)) {
                viewHolder.borderwhite20dpView.setVisibility(View.VISIBLE);
                viewHolder.instalmentPriceTextView.setText(MyAccountFragment.moneyDecimalFormat.format(Double.valueOf(bill.getInstallmentAmount())) + "元");
                String repaymentDate = bill.getSettledDate().split(" ")[0];
                viewHolder.repaymentDateTextView.setText(repaymentDate.substring(5, (repaymentDate.length())) + "已还");
            }

            if (ClosedBillingFragment.BILL_STATUS_ALL.equals(billStatus)) {
                viewHolder.borderWarningView.setVisibility(View.VISIBLE);
                viewHolder.warningTextView.setVisibility(View.VISIBLE);
                viewHolder.warningTextView.setText(bill.getRemark());
                viewHolder.billStatusTextView.setVisibility(View.VISIBLE);
                viewHolder.billStatusTextView.setText(getBillStatus(bill.getStatus()));
                viewHolder.instalmentPriceTextView.setText(MyAccountFragment.moneyDecimalFormat.format(Double.valueOf(bill.getRepayAmt())) + "元");
                String repaymentDate = bill.getPayTime().split(" ")[0];
                viewHolder.repaymentDateTextView.setText(repaymentDate.substring(5, (repaymentDate.length())));

                //1 还款中 2 扣款成功  3 扣款失败
                if ("2".equals(bill.getStatus())) {
                    viewHolder.warningTextView.setVisibility(View.GONE);
                    viewHolder.borderWarningView.setVisibility(View.GONE);
                }
            }

            viewHolder.numberOfInstallmentsTextView.setText("[" + bill.getStage() + "/" + bill.getTotalPeriod() + "]");
            viewHolder.productNameTextView.setText(bill.getProductName());

            return convertView;
        }

        private String getBillStatus(String billStatus) {
            String billStatusString = "...";

            //1 还款中 2 扣款成功  3 扣款失败
            if ("1".equals(billStatus)) {
                billStatusString = "还款中";
            } else if ("2".equals(billStatus)) {
                billStatusString = "扣款成功";
            } else if ("3".equals(billStatus)) {
                billStatusString = "扣款失败";
            }
            return billStatusString;
        }


    }

    static class ViewHolder {
        @InjectView(R.id.tv_allBillFragment_billItem_instalmentPrice)
        TextView instalmentPriceTextView;
        @InjectView(R.id.tv_allBillFragment_billItem_billStatus)
        TextView billStatusTextView;
        @InjectView(R.id.tv_allBillFragment_billItem_numberOfInstallments)
        TextView numberOfInstallmentsTextView;
        @InjectView(R.id.tv_allBillFragment_billItem_productName)
        TextView productNameTextView;
        @InjectView(R.id.tv_allBillFragment_billItem_repaymentDate)
        TextView repaymentDateTextView;
        @InjectView(R.id.v_allbillFragment_billItem_border_warning)
        View borderWarningView;
        @InjectView(R.id.v_allbillFragment_billItem_border_white20dp)
        View borderwhite20dpView;
        @InjectView(R.id.tv_allbillFragment_billItem_warning)
        TextView warningTextView;

        public ViewHolder(View v) {
            super();
            ButterKnife.inject(this, v);
        }

    }


}
