package com.test.bank.view.fragment;

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.test.bank.R;
import com.test.bank.adapter.TradeNoticePreferredAdapter;
import com.test.bank.adapter.TradeNoticeRansomAdapter;
import com.test.bank.base.BaseUIFragment;
import com.test.bank.bean.FundArchivesWithAnnouncementBean;
import com.test.bank.bean.TradeNoticeBean;
import com.test.bank.http.HttpRequest;
import com.test.bank.utils.StringUtil;
import com.test.bank.view.activity.TradeNoticeActivity;

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
* 时    间：2018/2/8<br>
*/
public class FundBuyRuleFragment extends BaseUIFragment {

    @BindView(R.id.tv_tradeNoticeActivity_tradeStatus)
    TextView tradeStatusTextView;
    @BindView(R.id.iv_tradeNoticeActivity_fundBuy)
    ImageView fundBuyImageView;
    @BindView(R.id.tv_tradeNoticeActivity_fundBuy)
    TextView fundBuyTextView;
    @BindView(R.id.iv_tradeNoticeActivity_fundSell)
    ImageView fundSellImageView;
    @BindView(R.id.tv_tradeNoticeActivity_fundSell)
    TextView fundSellTextView;
    @BindView(R.id.tv_tradeNoticeActivity_tradeStatusComment)
    TextView tradeStatusCommentTextView;

    @BindView(R.id.tv_tradeNoticeActivity_buyFlowKey)
    TextView buyFlowKeyTextView;
    @BindView(R.id.tv_singleFundDetailActivity_tradeRule1)
    TextView tradeRule1TextView;
    @BindView(R.id.tv_singleFundDetailActivity_tradeRule2)
    TextView tradeRule2TextView;
    @BindView(R.id.tv_singleFundDetailActivity_tradeRule3)
    TextView tradeRule3TextView;
    @BindView(R.id.tv_tradeNoticeActivity_buyFlowDesc)
    TextView buyFlowTextView;

    @BindView(R.id.tv_tradeNoticeActivity_preferredRatesTitleKey)
    TextView preferredRatesTitleKeyTextView;
    @BindView(R.id.ll_tradeNoticeActivity_preferredRatesChart)
    LinearLayout preferredRatesChartLinearLayout;
    @BindView(R.id.tv_tradeNoticeActivity_preferredRatesDesc)
    TextView preferredRatesDescTextView;

    @BindView(R.id.tv_tradeNoticeActivity_ransomRatesTitleKey)
    TextView ransomRatesTitleKeyTextView;
    @BindView(R.id.ll_tradeNoticeActivity_ransomRatesChart)
    LinearLayout ransomRatesChartLinearLayout;
    @BindView(R.id.tv_tradeNoticeActivity_ransomRatesDesc)
    TextView ransomRatesDescTextView;
    @BindView(R.id.rv_tradeNoticeActivity_preferredRates)
    RecyclerView preferredRatesRecyclerView;
    @BindView(R.id.rv_tradeNoticeActivity_ransomRates)
    RecyclerView ransomRatesRecyclerView;


    @BindView(R.id.tv_tradeNoticeActivity_fundManageRatesValue)
    TextView fundManageRatesValueTextView;
    @BindView(R.id.tv_tradeNoticeActivity_fundTrusteeshipRatesValue)
    TextView fundTrusteeshipRatesValueTextView;
    @BindView(R.id.v_tradeNoticeActivity_fundServiceRatesDivider)
    View fundServiceRatesDividerView;
    @BindView(R.id.tv_tradeNoticeActivity_fundServiceRatesKey)
    TextView fundServiceRatesKeyTextView;
    @BindView(R.id.tv_tradeNoticeActivity_fundServiceRatesValue)
    TextView fundServiceRatesValueTextView;

    TradeNoticeRansomAdapter ransomAdapter;
    TradeNoticePreferredAdapter preferredAdapter;
    List<TradeNoticeBean.Chag_rate_list> mChag_rate_lists = new ArrayList<TradeNoticeBean.Chag_rate_list>();
    List<TradeNoticeBean.Call_rate_list> mCall_rate_lists = new ArrayList<TradeNoticeBean.Call_rate_list>();
    private FundArchivesWithAnnouncementBean mFundArchivesWithAnnouncementBean = new FundArchivesWithAnnouncementBean();

    public static final String KEY_OF_TRADE_STATUS = "tradeStatusKey";

    public static final int TRADE_STATUS_OPEN = 601;
    public static final int TRADE_STATUS_CLOSE = 602;
    public static final int TRADE_STATUS_BUY = 603;
    private TradeNoticeBean tradeNoticeBean = new TradeNoticeBean();
    private String fundCode;


    private int currentRuleType = -1;
    public static final int RULE_TYPE_BUY = 1001;
    public static final int RULE_TYPE_SELL = 1002;

    public static Fragment newInstance(int ruleType){
        FundBuyRuleFragment fundBuyRuleFragment = new FundBuyRuleFragment();
        fundBuyRuleFragment.currentRuleType = ruleType;
        return  fundBuyRuleFragment;
    }

    @Override
    protected String getPageTitle() {
        return null;
    }

    @Override
    protected int getSubLayoutId() {
        return R.layout.fragment_fund_buy_rule;
    }

    @Override
    protected void initPageData() {
        fundCode = ((TradeNoticeActivity)getActivity()).fundCode;
        preferredAdapter = new TradeNoticePreferredAdapter(getActivity(), mChag_rate_lists);
        preferredRatesRecyclerView.setAdapter(preferredAdapter);
        preferredRatesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        ransomAdapter = new TradeNoticeRansomAdapter(getActivity(), mCall_rate_lists);
        ransomRatesRecyclerView.setAdapter(ransomAdapter);
        ransomRatesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mFundArchivesWithAnnouncementBean =((TradeNoticeActivity)getActivity()).mFundArchivesWithAnnouncementBean;
//        declarestate	int	申购状态    1-开放 2-暂停
//        withdrawstate	int	赎回状态   1开放  2暂停
        if(mFundArchivesWithAnnouncementBean.getDeclarestate()==1 && mFundArchivesWithAnnouncementBean.getWithdrawstate()==1){
            fundBuyTextView.setText("开放买入");
            fundSellTextView.setText("开放卖出");
            fundBuyImageView.setBackgroundResource(R.mipmap.trade_rules_correct);
            fundSellImageView.setBackgroundResource(R.mipmap.trade_rules_correct);
            tradeStatusCommentTextView.setText("* 基金处于开放期，支持买入和卖出");
        }else if(mFundArchivesWithAnnouncementBean.getDeclarestate()==1 && mFundArchivesWithAnnouncementBean.getWithdrawstate()==2){
            fundBuyTextView.setText("开放买入");
            fundSellTextView.setText("暂停卖出");
            fundBuyImageView.setBackgroundResource(R.mipmap.trade_rules_correct);
            fundSellImageView.setBackgroundResource(R.mipmap.trade_rules_error);
            tradeStatusCommentTextView.setText("* 基金处于开放期，具体请以基金公司最新披露的基金信息公告为准");
        }else if(mFundArchivesWithAnnouncementBean.getDeclarestate()==2 && mFundArchivesWithAnnouncementBean.getWithdrawstate()==2){
            fundBuyTextView.setText("暂停买入");
            fundSellTextView.setText("暂停卖出");
            fundBuyImageView.setBackgroundResource(R.mipmap.trade_rules_error);
            fundSellImageView.setBackgroundResource(R.mipmap.trade_rules_error);
            tradeStatusCommentTextView.setText("* 暂时无法买入、卖出，具体请以基金公司最新披露的基金信息公告为准");
        }

        tradeRule1TextView.setText("T日");

        getFundCompanyInfo();
    }

    private void getFundCompanyInfo() {
        Map<String, String> params =  new HashMap<String, String>();
        params.put("fundcode", fundCode);

        HttpRequest.post(HttpRequest.APP_INTERFACE_WEB_URL +HttpRequest.TRADE_NOTICE, params, this, new HttpRequest.HttpResponseCallBank() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                tradeNoticeBean = JSON.parseObject(response.body(), TradeNoticeBean.class);

                switch (currentRuleType){
                    case RULE_TYPE_BUY:
                        showPreferredRates();
                        break;
                    case RULE_TYPE_SELL:
                        showRansomRates();
                        break;
                }

                List<TradeNoticeBean.Chag_rate_list> chag_rate_list = tradeNoticeBean.getChag_rate_list();
                preferredAdapter.upateData(true, chag_rate_list);

                List<TradeNoticeBean.Call_rate_list> call_rate_list = tradeNoticeBean.getCall_rate_list();
                ransomAdapter.upateData(true, call_rate_list);

                fundManageRatesValueTextView.setText(StringUtil.getRateWithSign(tradeNoticeBean.getMng_fee_ratio()).substring(1)+"(每年)");
                fundTrusteeshipRatesValueTextView.setText(tradeNoticeBean.getTru_fee_ratio()+"%(每年)");
                if(StringUtil.notEmpty(tradeNoticeBean.getSale_fee_ratio()+"")){
                    fundServiceRatesDividerView.setVisibility(View.VISIBLE);
                    fundServiceRatesKeyTextView.setVisibility(View.VISIBLE);
                    fundServiceRatesValueTextView.setVisibility(View.VISIBLE);
                    fundServiceRatesValueTextView.setText(tradeNoticeBean.getSale_fee_ratio()+"%(每年)");
                }else {
                    fundServiceRatesDividerView.setVisibility(View.GONE);
                    fundServiceRatesKeyTextView.setVisibility(View.GONE);
                    fundServiceRatesValueTextView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    public void showPreferredRates(){
        buyFlowKeyTextView.setText("买入流程");

        tradeRule2TextView.setText(tradeNoticeBean.getBuyDate().getConfirmDate()+"日");
        tradeRule3TextView.setText(tradeNoticeBean.getBuyDate().getProfitDate()+"日净值更新后");

        buyFlowTextView.setVisibility(View.VISIBLE);
        preferredRatesTitleKeyTextView.setVisibility(View.VISIBLE);
        preferredRatesChartLinearLayout.setVisibility(View.VISIBLE);
        preferredRatesDescTextView.setVisibility(View.VISIBLE);

        ransomRatesTitleKeyTextView.setVisibility(View.GONE);
        ransomRatesChartLinearLayout.setVisibility(View.GONE);
        ransomRatesDescTextView.setVisibility(View.GONE);
    }

    public void showRansomRates(){
        buyFlowKeyTextView.setText("卖出流程");

        tradeRule2TextView.setText(tradeNoticeBean.getSellDate().getConfirmDate()+"日");
        tradeRule3TextView.setText(tradeNoticeBean.getSellDate().getToAccount()+"日24:00前");

        buyFlowTextView.setVisibility(View.GONE);
        preferredRatesTitleKeyTextView.setVisibility(View.GONE);
        preferredRatesChartLinearLayout.setVisibility(View.GONE);
        preferredRatesDescTextView.setVisibility(View.GONE);

        ransomRatesTitleKeyTextView.setVisibility(View.VISIBLE);
        ransomRatesChartLinearLayout.setVisibility(View.VISIBLE);
        ransomRatesDescTextView.setVisibility(View.VISIBLE);
    }

}
