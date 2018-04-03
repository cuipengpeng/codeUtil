package com.jf.jlfund.view.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jf.jlfund.R;
import com.jf.jlfund.base.BaseActivity;
import com.jf.jlfund.base.BaseBean;
import com.jf.jlfund.bean.FundHoldDetailBean;
import com.jf.jlfund.bean.FundTrendBean;
import com.jf.jlfund.http.NetService;
import com.jf.jlfund.http.ParamMap;
import com.jf.jlfund.inter.OnResponseListener;
import com.jf.jlfund.utils.CommonUtil;
import com.jf.jlfund.utils.SPUtil;
import com.jf.jlfund.utils.ToastUtils;
import com.jf.jlfund.utils.UIUtils;
import com.jf.jlfund.view.fragment.PosOrRateDetailDailog;
import com.jf.jlfund.weight.CommonTitleBar;
import com.jf.jlfund.weight.holder.FundPosCloseHolder;
import com.jf.jlfund.weight.holder.LineChartViewHolder;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;

/**
 * 基金持仓页
 */
public class FundPositionsActivity extends BaseActivity {
    @BindView(R.id.commonTitleBar_fundDetail)
    CommonTitleBar commonTitleBar;
    @BindView(R.id.tv_fp_icon_y)
    TextView tvIconYesterdayIncomeOrCharge;
    @BindView(R.id.tv_fp_yesterdayIncome)
    TextView tvYesterdayIncome; //昨日收益
    @BindView(R.id.tv_fp_amount)
    TextView tvAmount;          //金额

    @BindView(R.id.tv_fp_totalIncome)
    TextView tvTotalIncome;     //累计收益

    @BindView(R.id.tv_fp_posAmount)
    TextView tvPosAmount;       //持仓金额
    @BindView(R.id.tv_fp_posCost)
    TextView tvPosCost;     //持仓成本价
    @BindView(R.id.tv_fp_netValue)
    TextView tvNetValues;     //最新净值
    @BindView(R.id.tv_fp_notEnsureAmount)
    TextView tvNotEnsureAmount;     //待确认金额
    @BindView(R.id.tv_fp_posLot)
    TextView tvPosLot;     //持有份额
    @BindView(R.id.tv_fp_dayIncrease)
    TextView tvDayIncrease;     //日涨幅

    @BindView(R.id.ll_fp_container)
    LinearLayout llContainer;

    @BindView(R.id.rl_fp_tradeRecord)
    RelativeLayout rlTradeInWay;
    @BindView(R.id.tv_fp_icon_tradeRecord)
    TextView tvTradeRecordInWayRight;
    @BindView(R.id.tv_fp_tradeRecord)
    TextView tvTradeRecordInWay;

    @BindView(R.id.tv_fp_sale)
    TextView tvSale;
    @BindView(R.id.tv_fp_buy)
    TextView tvBuy;

    private String fundCode;
    private String fundName;
    private boolean isNormalFund;

    @Override
    protected void init() {
        if (getIntent() != null) {
            fundCode = getIntent().getStringExtra(PARAM_FUND_CODE);
            fundName = getIntent().getStringExtra(PARAM_FUND_NAME);
            isNormalFund = getIntent().getBooleanExtra(PARAM_FUND_STATUS, false);
        }

        commonTitleBar.setPrimaryTitle(fundName);
        commonTitleBar.setSubTitle(fundCode);

        commonTitleBar.setOnRightClickListener(new CommonTitleBar.onRightClickListener() {
            @Override
            public void onClickRight() {
                MobclickAgent.onEvent(FundPositionsActivity.this, "click_btn_fundPosActivity_fundDetail");
                CommonUtil.startActivity(FundPositionsActivity.this, SingleFundDetailActivity.class, fundCode);
            }
        });
        if (isNormalFund) {
            requestLineChartData();
        } else {
            llContainer.addView(new FundPosCloseHolder().inflateData(1));  //进度
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_fund_positions;
    }

    @Override
    protected void doBusiness() {
        postRequest(new OnResponseListener<FundHoldDetailBean>() {
            @Override
            public Observable<BaseBean<FundHoldDetailBean>> createObservalbe() {
                ParamMap paramMap = new ParamMap();
                paramMap.put("token", SPUtil.getInstance().getToken());
                paramMap.putLast("fundcode", fundCode);
                return NetService.getNetService().getHoldFundDetail(paramMap);
            }

            @Override
            public void onResponse(BaseBean<FundHoldDetailBean> result) {
                if (result.isSuccess()) {
                    inflateData(result.getData());
                }
            }

            @Override
            public void onError(String errorMsg) {
                ToastUtils.showShort(errorMsg);
            }
        });
    }

    FundHoldDetailBean bean;

    private void inflateData(FundHoldDetailBean data) {
        if (data == null) {
            return;
        }
        bean = data;

        if (!TextUtils.isEmpty(data.getShowtype()) && "0".equals(data.getShowtype())) {
            tvIconYesterdayIncomeOrCharge.setText("手续费(元)");
        } else {
            tvIconYesterdayIncomeOrCharge.setText("昨日收益(元)");
        }

        if (!TextUtils.isEmpty(data.getYestincome())) {
            UIUtils.setIncome(tvYesterdayIncome, data.getYestincome(), "--", 2, true, true);
        } else {
            UIUtils.setText(tvYesterdayIncome, "--");    //昨日收益
        }

        double holdAmount = 0;
        if (!TextUtils.isEmpty(data.getFundvolbalance())) {
            holdAmount = Double.parseDouble(data.getFundvolbalance());
        }
        double notEnsureAmount = 0;
        if (!TextUtils.isEmpty(data.getUnconfbalance())) {
            notEnsureAmount = Double.parseDouble(data.getUnconfbalance());
        }

        UIUtils.setIncome(tvAmount, (notEnsureAmount + holdAmount) + "", "--", 2, false, false);

        UIUtils.setIncome(tvTotalIncome, data.getAddincome(), "--", 2, true, true);

        UIUtils.setIncome(tvPosAmount, data.getFundvolbalance(), "0.00", 2, false, false);//持有金额
        UIUtils.setIncome(tvPosCost, data.getFundvolunivalence(), "--", 4, false, false);   //持仓成本价
        String navDate = data.getNavdate();
        if (TextUtils.isEmpty(navDate)) {
            navDate = "xx-xx";
        }
        String netValues = data.getNav();
        if (TextUtils.isEmpty(netValues)) {
            netValues = "--";
        }
        netValues = UIUtils.fillUpZeroInAmountEnd(data.getNav(), 4, "--");
        UIUtils.setText(tvNetValues, netValues + "(" + navDate + ")");

        UIUtils.setText(tvNotEnsureAmount, UIUtils.fillUpZeroInAmountEnd(data.getUnconfbalance(), 2, "--"));     //待确认金额
        UIUtils.setText(tvPosLot, UIUtils.fillUpZeroInAmountEnd(data.getAvailablevol(), 2, "--"));       //持有份额
        UIUtils.setRate(tvDayIncrease, data.getDaygrowth());

        if (data.getFunddealing() > 0) {
            UIUtils.setText(tvTradeRecordInWay, data.getFunddealing() + "");
            tvTradeRecordInWayRight.setText("笔交易确认中");
        } else {
//            rlTradeInWay.setVisibility(View.GONE);
            tvTradeRecordInWay.setText("");
            tvTradeRecordInWayRight.setText("");
        }

        if ("1".equals(data.getDeclarestate())) {     //申购状态：1-开放  2-暂停
            tvBuy.setSelected(true);
        } else {
            tvBuy.setSelected(false);
        }

        if ("1".equals(data.getWithdrawstate()) && !data.getTotalfundvolbalance_mode1().equals("0")) {       //赎回状态   1开放  2暂停
            tvSale.setSelected(true);
        } else {
            tvSale.setSelected(false);
        }
    }

    private void requestLineChartData() {
        postRequest(new OnResponseListener<FundTrendBean>() {
            @Override
            public Observable<BaseBean<FundTrendBean>> createObservalbe() {
                ParamMap paramMap = new ParamMap();
                if (SPUtil.getInstance().isLogin())
                    paramMap.put("token", SPUtil.getInstance().getToken());
                paramMap.putLast("fundcode", fundCode);
                return NetService.getNetService().getSingleFundTrend(paramMap);
            }

            @Override
            public void onResponse(BaseBean<FundTrendBean> result) {
                if (result.isSuccess()) {
                    llContainer.removeAllViews();
                    llContainer.addView(new LineChartViewHolder().inflateData(result.getData()));
                }
            }

            @Override
            public void onError(String errorMsg) {

            }
        });
    }

    @OnClick({R.id.rl_fp_tradeRecord, R.id.tv_fp_posCost, R.id.tv_fp_sale, R.id.tv_fp_buy})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_fp_tradeRecord:
                MobclickAgent.onEvent(FundPositionsActivity.this, "click_fundPosActivity_tradeRecord");
                SingleFundTradeRecordActivity.open(this, fundName, fundCode);
                break;
            case R.id.tv_fp_posCost:
                popPosCostValueDialog();
                break;
            case R.id.tv_fp_sale:
                MobclickAgent.onEvent(this, "click_btn_fundPosActivity_sale");
                if (tvSale.isSelected()) {
                    FundSaleOutActivity.open(this, fundCode, fundName);
                } else {
                    if (bean != null && "0".equals(bean.getTotalfundvolbalance_mode1())) {
                        showOneBtnDialog("当前无可卖出份额", "确定", null);
                    }
                }
                break;
            case R.id.tv_fp_buy:
                MobclickAgent.onEvent(this, "click_btn_fundPosActivity_buy");
                FundBuyInActivity.open(this, fundCode, fundName);
                break;
        }
    }

    PosOrRateDetailDailog posOrRateDetailDailog;

    private void popPosCostValueDialog() {
        posOrRateDetailDailog = (PosOrRateDetailDailog) getSupportFragmentManager().findFragmentByTag("posOrRateDetailDailog");
        if (posOrRateDetailDailog == null) {
            posOrRateDetailDailog = PosOrRateDetailDailog.getInstance(-1, null);
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//                fragmentTransaction.setCustomAnimations(R.anim.bottom_in, R.anim.bottom_out, R.anim.bottom_in, R.anim.bottom_out);
        if (!posOrRateDetailDailog.isAdded()) {
            fragmentTransaction.add(posOrRateDetailDailog, "posOrRateDetailDailog");
            fragmentTransaction.commit();
        }
    }

    public static String PARAM_FUND_CODE = "fundCode";
    public static String PARAM_FUND_NAME = "fundName";
    public static String PARAM_FUND_STATUS = "fundStatus";

    /**
     * 跳转到基金持仓详情
     *
     * @param context
     * @param fundCode
     * @param fundName
     * @param isNormalFund 当fundStatus 为1,2,3,6,7的时候为正常状态，可以显示走势图，否则显示一个进度
     */
    public static void open(Context context, String fundCode, String fundName, boolean isNormalFund) {
        Intent intent = new Intent(context, FundPositionsActivity.class);
        intent.putExtra(PARAM_FUND_CODE, fundCode);
        intent.putExtra(PARAM_FUND_NAME, fundName);
        intent.putExtra(PARAM_FUND_STATUS, isNormalFund);
        context.startActivity(intent);
    }
}
