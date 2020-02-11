package com.test.bank.view.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.test.bank.R;
import com.test.bank.adapter.TradeNoticePreferredAdapter;
import com.test.bank.base.BaseUIActivity;
import com.test.bank.bean.TradeNoticeBean;
import com.test.bank.http.HttpRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Response;

/*
* 描    述：<br>
* 作    者：崔朋朋<br>
* 时    间：2017/12/11<br>
*/
public class TradeNoticeShortActivity extends BaseUIActivity {

    @BindView(R.id.tv_tradeNoticeShortActivity_tradeStatus)
    TextView tradeStatusTextView;
    @BindView(R.id.tv_tradeNoticeShortActivity_fundBuy)
    TextView fundBuyTextView;
    @BindView(R.id.tv_tradeNoticeShortActivity_operationRatesKey)
    TextView operationRatesKeyTextView;
    @BindView(R.id.tv_tradeNoticeShortActivity_fundManageRatesKey)
    TextView fundManageRatesKeyTextView;
    @BindView(R.id.tv_tradeNoticeShortActivity_fundTrusteeshipRatesKey)
    TextView fundTrusteeshipRatesKeyTextView;
    @BindView(R.id.rv_tradeNoticeShortActivity_preferredRates)
    RecyclerView preferredRatesRecyclerView;

    TradeNoticePreferredAdapter preferredAdapter;
    List<TradeNoticeBean.Chag_rate_list> mChag_rate_lists = new ArrayList<TradeNoticeBean.Chag_rate_list>();
    private String fundCode;


    @Override
    protected String getPageTitle() {
        return "交易须知";
    }

    @Override
    protected int getSubLayoutId() {
        return R.layout.activity_trade_notice_short;
    }

    @Override
    protected void initPageData() {
        fundCode = getIntent().getStringExtra(SingleFundDetailActivity.KEY_OF_FUND_CODE);
        preferredAdapter = new TradeNoticePreferredAdapter(this);
        preferredRatesRecyclerView.setAdapter(preferredAdapter);
        preferredRatesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        getFundCompanyInfo();
    }

    private void getFundCompanyInfo() {
        Map<String, String> params =  new HashMap<String, String>();
        params.put("fundcode", fundCode);

        HttpRequest.post(false, this, HttpRequest.APP_INTERFACE_WEB_URL +HttpRequest.TRADE_NOTICE, params, new HttpRequest.HttpResponseCallBack() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                TradeNoticeBean tradeNoticeBean = JSON.parseObject(response.body(), TradeNoticeBean.class);

                List<TradeNoticeBean.Chag_rate_list> chag_rate_list = tradeNoticeBean.getChag_rate_list();
                preferredAdapter.upateData(true, chag_rate_list);

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

}
