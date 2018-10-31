package com.test.bank.view.activity;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.test.bank.R;
import com.test.bank.base.BaseUIActivity;
import com.test.bank.bean.FundInfoBean;
import com.test.bank.http.HttpRequest;
import com.test.bank.utils.ConstantsUtil;
import com.test.bank.utils.LogUtils;
import com.test.bank.utils.StringUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/*
* 描    述：<br>
* 作    者：崔朋朋<br>
* 时    间：2017/12/11<br>
*/
public class FundInfoActivity extends BaseUIActivity {

    @BindView(R.id.tv_fundInfoActivity_fundNameValue)
    TextView fundNameValueTextView;
    @BindView(R.id.tv_fundInfoActivity_fundCodeValue)
    TextView fundCodeValueTextView;
    @BindView(R.id.tv_fundInfoActivity_fundTypeValue)
    TextView fundTypeValueTextView;
    @BindView(R.id.tv_fundInfoActivity_riskLevelValue)
    TextView riskLevelValueTextView;
    @BindView(R.id.tv_fundInfoActivity_setupTimeValue)
    TextView setupTimeValueTextView;
    @BindView(R.id.tv_fundInfoActivity_fundStatusValue)
    TextView fundStatusValueTextView;
    @BindView(R.id.tv_fundInfoActivity_firstCollectMoneyValue)
    TextView firstCollectMoneyValueTextView;
    @BindView(R.id.tv_fundInfoActivity_latestShareValue)
    TextView latestShareValueTextView;
    @BindView(R.id.tv_fundInfoActivity_latestMoneyValue)
    TextView latestMoneyValueTextView;

    public static final String KEY_OF_FUND_INFO_MODEL = "fundInfoModelKey";
    private String fundCode;
    private FundInfoBean mFundInfoBean =   new FundInfoBean();

    @OnClick({R.id.rl_fundInfoActivity_dividend})
    public void onItemClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.rl_fundInfoActivity_dividend:
                intent.setClass(this, DividendInfoActivity.class);
                intent.putExtra(KEY_OF_FUND_INFO_MODEL, mFundInfoBean);
                startActivity(intent);
                break;
        }
    }
    @Override
    protected String getPageTitle() {
        return "基金信息";
    }

    @Override
    protected int getSubLayoutId() {
        return R.layout.activity_fund_info;
    }

    @Override
    protected void initPageData() {
        fundCode = getIntent().getStringExtra(SingleFundDetailActivity.KEY_OF_FUND_CODE);

        getFundCompanyInfo();
    }

    private void getFundCompanyInfo() {
        Map<String, String> params =  new HashMap<String, String>();
        params.put("fundcode", fundCode);

        HttpRequest.post(HttpRequest.APP_INTERFACE_WEB_URL +HttpRequest.FUND_INFO_DETAIL, params, this, new HttpRequest.HttpResponseCallBank() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                mFundInfoBean = JSON.parseObject(response.body(), FundInfoBean.class);
                fundNameValueTextView.setText(mFundInfoBean.getFundname());
                fundCodeValueTextView.setText(mFundInfoBean.getFundcode());
                fundTypeValueTextView.setText(mFundInfoBean.getFundtype());
                riskLevelValueTextView.setText(mFundInfoBean.getCustrisk());
                setupTimeValueTextView.setText(mFundInfoBean.getCreatedate());
                LogUtils.printLog(TAG+"--"+mFundInfoBean.getFundstatus()+"--"+mFundInfoBean.getAct_val()+"--"+mFundInfoBean.getNew_nav()+"--"+mFundInfoBean.getNew_val()+"--");
                fundStatusValueTextView.setText(mFundInfoBean.getFundstateName()+"");
                firstCollectMoneyValueTextView.setText(mFundInfoBean.getAct_val()+"亿元");
                if(StringUtil.isNull(mFundInfoBean.getAct_val())){
                    firstCollectMoneyValueTextView.setText(ConstantsUtil.REPLACE_BLANK_SYMBOL);
                }
                latestShareValueTextView.setText(mFundInfoBean.getNew_nav()+"万份");
                if(StringUtil.isNull(mFundInfoBean.getNew_nav())){
                    latestShareValueTextView.setText(ConstantsUtil.REPLACE_BLANK_SYMBOL);
                }
                latestMoneyValueTextView.setText(mFundInfoBean.getNew_val()+"亿元");
                if(StringUtil.isNull(mFundInfoBean.getNew_val())){
                    latestMoneyValueTextView.setText(ConstantsUtil.REPLACE_BLANK_SYMBOL);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

}
