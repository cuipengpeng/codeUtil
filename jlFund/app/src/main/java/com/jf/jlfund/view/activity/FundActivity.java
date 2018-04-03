package com.jf.jlfund.view.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jf.jlfund.R;
import com.jf.jlfund.base.BaseActivity;
import com.jf.jlfund.base.BaseBean;
import com.jf.jlfund.bean.FundHomeBean;
import com.jf.jlfund.bean.GraspChanceBean;
import com.jf.jlfund.http.NetService;
import com.jf.jlfund.http.ParamMap;
import com.jf.jlfund.inter.OnResponseListener;
import com.jf.jlfund.utils.SPUtil;
import com.jf.jlfund.utils.StringUtil;
import com.jf.jlfund.utils.ToastUtils;
import com.jf.jlfund.utils.UIUtils;
import com.jf.jlfund.weight.CommonTitleBar;
import com.jf.jlfund.weight.holder.FundHoldHolder;
import com.jf.jlfund.weight.holder.FundRecommondFundHolder;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;

public class FundActivity extends BaseActivity {
    @BindView(R.id.commonTitleBar_fund)
    CommonTitleBar commonTitleBar;
    @BindView(R.id.tv_fund_totalAmount)
    TextView tvTotalAmount;
    @BindView(R.id.tv_fund_yesterdayIncome)
    TextView tvYesterdayIncome;
    @BindView(R.id.tv_fund_totalIncome)
    TextView tvTotalIncome;
    @BindView(R.id.iv_fund_everyoneBuy)
    ImageView ivEveryoneBuy;
    @BindView(R.id.ll_fund_container)
    LinearLayout llContainer;


    @Override
    protected void init() {
        initListener();
    }


    private void initListener() {
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_fund;
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (SPUtil.getInstance().isLogin()) {
            ivEveryoneBuy.setVisibility(View.GONE);
            requestFundData();
        } else {
            ivEveryoneBuy.setVisibility(View.VISIBLE);
            requesetRecommandFunds();
        }
    }

    @Override
    protected void doBusiness() {

    }

    private void requestFundData() {
        postRequest(new OnResponseListener<FundHomeBean>() {
            @Override
            public Observable<BaseBean<FundHomeBean>> createObservalbe() {
                ParamMap paramMap = new ParamMap();
                paramMap.putLast("token", SPUtil.getInstance().getToken());
                return NetService.getNetService().getFundHomeData(paramMap);
            }

            @Override
            public void onResponse(BaseBean<FundHomeBean> result) {
                if (result.isSuccess()) {
                    inflateData(result.getData());
                }
            }

            @Override
            public void onError(String errorMsg) {

            }
        });
    }

    private void inflateData(FundHomeBean data) {
        if (data == null || commonTitleBar == null || tvTotalAmount == null) {
            return;
        }

        if (data.isHasHisotryTrade() && data.getFunds().size() > 0) {
            commonTitleBar.setPattern(CommonTitleBar.PATTERN_TITLE_WITH_TWO_IMG);
            commonTitleBar.setRightImgSrc(R.drawable.icon_fund_record);
            commonTitleBar.setOnRightClickListener(new CommonTitleBar.onRightClickListener() {
                @Override
                public void onClickRight() {
                    MobclickAgent.onEvent(FundActivity.this, "click_btn_fundActivity_tradeRecord");
                    if (SPUtil.getInstance().isLogin()) {
                        FundTradeRecordActivity.open(FundActivity.this);
                    } else {
                        ToastUtils.showShort("请先登录");
                    }
                }
            });
        } else {
            commonTitleBar.setPattern(CommonTitleBar.PATTERN_TITLE_WITH_LEFT_IMG);
        }

        if (!TextUtils.isEmpty(data.getTotalAmount())) {
            tvTotalAmount.setText(StringUtil.trunkDouble(data.getTotalAmount()));
        }
        if (!TextUtils.isEmpty(data.getTotalIncome())) {
            UIUtils.setIncome(tvTotalIncome, data.getTotalIncome(), "--", 2, true, false);
        }
        if (!TextUtils.isEmpty(data.getYestodayIncome())) {
            UIUtils.setIncome(tvYesterdayIncome, data.getYestodayIncome(), "--", 2, true, false);
        }

        if (data.getFunds() != null && data.getFunds().size() > 0) {
            llContainer.removeAllViews();
            for (int i = 0; data.getFunds() != null && i < data.getFunds().size(); i++) {
                llContainer.addView(new FundHoldHolder().inflateData(data.getFunds().get(i)));
            }
        } else {
            ivEveryoneBuy.setVisibility(View.VISIBLE);
            inflateRecommandData(data.getWeBuy());
        }
    }

    private void requesetRecommandFunds() {
        postRequest(new OnResponseListener<List<GraspChanceBean>>() {
            @Override
            public Observable<BaseBean<List<GraspChanceBean>>> createObservalbe() {
                ParamMap paramMap = new ParamMap();
                paramMap.put("pageNo", "1");
                paramMap.put("rowsSize", "15");
                paramMap.putLast("showModule", "1");    //来源(2 赚钱抓机会 1 大家都在买)'
                return NetService.getNetService().getGraspChanceList(paramMap);
            }

            @Override
            public void onResponse(BaseBean<List<GraspChanceBean>> result) {
                if (result.isSuccess() && result.getData() != null) {
                    inflateRecommandData(result.getData());
                }
            }

            @Override
            public void onError(String errorMsg) {
            }
        });
    }

    private void inflateRecommandData(List<GraspChanceBean> data) {
        llContainer.removeAllViews();
        for (int i = 0; i < data.size(); i++) {
            data.get(i).setEveryBuyNo(i);
            llContainer.addView(new FundRecommondFundHolder().inflateData(data.get(i)));
        }
    }


    @OnClick({R.id.tv_fund_totalAmount})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_fund_totalAmount:

                break;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (SPUtil.getInstance().isLogin()) {
            ivEveryoneBuy.setVisibility(View.GONE);
            requestFundData();
        } else {
            ivEveryoneBuy.setVisibility(View.VISIBLE);
            requesetRecommandFunds();
        }
    }

    public static void open(Context context) {
        open(context, false);
    }

    public static void open(Context context, boolean isClearTop) {
        Intent intent = new Intent(context, FundActivity.class);
        if (isClearTop)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }
}
