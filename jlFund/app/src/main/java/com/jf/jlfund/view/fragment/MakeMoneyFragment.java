package com.jf.jlfund.view.fragment;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.jf.jlfund.R;
import com.jf.jlfund.base.BaseBean;
import com.jf.jlfund.base.BaseFragment;
import com.jf.jlfund.bean.BuyGoodFundBean;
import com.jf.jlfund.bean.ChaseHotBean;
import com.jf.jlfund.bean.GraspChanceBean;
import com.jf.jlfund.bean.MakeMoneyBannerBean;
import com.jf.jlfund.bean.MakeMoneyCurrentPlusBean;
import com.jf.jlfund.bean.MakeMoneyListBean;
import com.jf.jlfund.http.NetService;
import com.jf.jlfund.http.ParamMap;
import com.jf.jlfund.inter.OnResponseListener;
import com.jf.jlfund.utils.DensityUtil;
import com.jf.jlfund.utils.LogUtils;
import com.jf.jlfund.utils.MD5;
import com.jf.jlfund.utils.SPUtil;
import com.jf.jlfund.utils.UIUtils;
import com.jf.jlfund.view.activity.BuyGoodFundActivity;
import com.jf.jlfund.view.activity.ChaseHotActivity;
import com.jf.jlfund.view.activity.CurrentPlusActivity;
import com.jf.jlfund.view.activity.GraspChanceActivity;
import com.jf.jlfund.view.activity.LoginActivity;
import com.jf.jlfund.view.activity.PutInActivity;
import com.jf.jlfund.view.activity.RapidBindCardActivity;
import com.jf.jlfund.view.activity.RiskPreferenceActivity;
import com.jf.jlfund.view.activity.SimpleH5Activity;
import com.jf.jlfund.weight.BannerImageLoader;
import com.jf.jlfund.weight.CommonTitleBar;
import com.jf.jlfund.weight.errorview.DefaultErrorPageBean;
import com.jf.jlfund.weight.errorview.ErrorBean;
import com.jf.jlfund.weight.holder.MakeMoneyBuyGoodFundHolder;
import com.jf.jlfund.weight.holder.MakeMoneyChaseHotHolder;
import com.jf.jlfund.weight.holder.MakeMoneyGraspChanceHolder;
import com.umeng.analytics.MobclickAgent;
import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;

/**
 * Created by 55 on 2017/11/29.
 */

public class MakeMoneyFragment extends BaseFragment {
    private static final String TAG = "MakeMoneyFragment";

    @BindView(R.id.commonTitleBar_makeMoney)
    CommonTitleBar commonTitleBar;

    @BindView(R.id.scrollView_makeMoney_content)
    ScrollView scrollView;

    @BindView(R.id.banner_makeMoney)
    Banner maxBanner;
    @BindView(R.id.banner_ad)
    Banner minBanner;

    @BindView(R.id.ll_current_plus)
    LinearLayout llCurrentPlus;
    @BindView(R.id.ll_grasp_chance)
    LinearLayout llGraspChance;
    @BindView(R.id.ll_chase_hot)
    LinearLayout llChaseHot;
    @BindView(R.id.ll_buy_good_fund)
    LinearLayout llBuyGoodFunds;

    @BindView(R.id.tv_makeMoney_currentPlus_slogn)
    TextView tvCurrentPlusSlogn;
    @BindView(R.id.tv_makeMoney_currentPlus_secondTitle)
    TextView tvCurrentPlusSecondSlogn;
    @BindView(R.id.tv_current_plus_title)
    TextView tvCurrentPlusTitle;    //活期+标题
    @BindView(R.id.tv_current_plus_rate)
    TextView tvCurrentPlusRate;    //活期+利率
    @BindView(R.id.tv_current_plus_period)
    TextView tvCurrentPlusPeriod;    //活期+时间

    @BindView(R.id.tv_grasp_chance_more)
    TextView tvGraspChanceMore;
    @BindView(R.id.tv_chase_hot_more)
    TextView tvChaseHotMore;
    @BindView(R.id.tv_buy_good_fund_more)
    TextView tvBuyGoodFundMore;


    @BindView(R.id.ll_grasp_chance_content)
    LinearLayout llGraspChanceContent;
    @BindView(R.id.ll_chase_hot_content)
    LinearLayout llChaseHotContent;
    @BindView(R.id.ll_buy_good_fund_content)
    LinearLayout llBuyGoodFundContent;


    List<GraspChanceBean> graspChanceList;  //抓机会列表
    List<ChaseHotBean> chaseHotList;    //追热点列表
    List<BuyGoodFundBean> buyGoodFundList;  //买好基列表

    private List<String> maxBannerImgUrl;
    private List<String> minBannerImgUrl;

    public static MakeMoneyFragment getInstance() {
        MakeMoneyFragment fragment = new MakeMoneyFragment();
        return fragment;
    }

    @Override
    protected void init() {
        Log.e(TAG, "init: ......bbbbbbbbbbbb");
        adjustMinBannerHeight();
        initData();
        initListener();
    }

    private void adjustMinBannerHeight() {
        int screenWidth = DensityUtil.getScreenWidth();
        int minBannerHeight = screenWidth / 3;
        LogUtils.d("minBannerHeight: " + minBannerHeight + " .. " + screenWidth);
        ViewGroup.LayoutParams layoutParams = minBanner.getLayoutParams();
        layoutParams.height = minBannerHeight;
        minBanner.setLayoutParams(layoutParams);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_make_money;
    }

    private void initData() {
        if (maxBannerImgUrl == null) {
            maxBannerImgUrl = new ArrayList<>();
        }
        if (minBannerImgUrl == null) {
            minBannerImgUrl = new ArrayList<>();
        }
        if (graspChanceList == null) {
            graspChanceList = new ArrayList<>();
        }
        if (chaseHotList == null) {
            chaseHotList = new ArrayList<>();
        }
        if (buyGoodFundList == null) {
            buyGoodFundList = new ArrayList<>();
        }
    }

    FundSearchFragment fundSearchFragment;

    private void initListener() {

        commonTitleBar.setOnRightClickListener(new CommonTitleBar.onRightClickListener() {
            @Override
            public void onClickRight() {
                MobclickAgent.onEvent(getContext(), "click_btn_makeMoneyFragment_fundSearch");
                fundSearchFragment = (FundSearchFragment) getChildFragmentManager().findFragmentByTag("FundSearchFragment");
                if (fundSearchFragment == null) {
                    fundSearchFragment = new FundSearchFragment();
                }
                FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
//                fragmentTransaction.setCustomAnimations(R.anim.bottom_in, R.anim.bottom_out, R.anim.bottom_in, R.anim.bottom_out);
                if (!fundSearchFragment.isAdded()) {
                    fragmentTransaction.add(fundSearchFragment, "FundSearchFragment");
                    fragmentTransaction.commit();
                }
            }
        });

        maxBanner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                MobclickAgent.onEvent(getContext(), "click_makeMoneyFragment_large_banner");
                if (makeMoneyBannerBean.getMaxBanner() != null && makeMoneyBannerBean.getMaxBanner().size() > 0)
                    SimpleH5Activity.open(getActivity(), makeMoneyBannerBean.getMaxBanner().get(position).getAddressUrl(), makeMoneyBannerBean.getMaxBanner().get(position).getHtmlTitle());
            }
        });

        minBanner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                MobclickAgent.onEvent(getContext(), "click_makeMoneyFragment_small_banner");
                if (makeMoneyBannerBean.getMinBanner() != null && makeMoneyBannerBean.getMinBanner().size() > 0)
                    SimpleH5Activity.open(getActivity(), makeMoneyBannerBean.getMinBanner().get(position).getAddressUrl(), makeMoneyBannerBean.getMinBanner().get(position).getHtmlTitle());
            }
        });
    }

    @Override
    protected void doBusiness() {
        Log.e(TAG, "doBusiness: ......bbbbbb");
        //3大列表
        postRequest(new OnResponseListener<MakeMoneyListBean>() {
            @Override
            public Observable<BaseBean<MakeMoneyListBean>> createObservalbe() {
                ParamMap paramMap = new ParamMap();
                return NetService.getNetService().getMakeMoneyList(paramMap);
            }

            @Override
            public void onResponse(BaseBean<MakeMoneyListBean> result) {
                inflate3List(result.getData());
            }

            @Override
            public void onError(String errorMsg) {

            }
        }, true);
        //活期+
        postRequest(new OnResponseListener<List<MakeMoneyCurrentPlusBean>>() {
            @Override
            public Observable<BaseBean<List<MakeMoneyCurrentPlusBean>>> createObservalbe() {
                return NetService.getNetService().getMakeMoneyCurrentPlusList(new ParamMap());
            }

            @Override
            public void onResponse(BaseBean<List<MakeMoneyCurrentPlusBean>> result) {
                if (result.isSuccess() && result.getData() != null && !result.getData().isEmpty()) {
                    if (result.getData().get(0) != null) {
                        llCurrentPlus.setVisibility(View.VISIBLE);
                        inflateCurrentPlusData(result.getData().get(0));
                    }
                } else {
                    llCurrentPlus.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(String errorMsg) {
            }
        }, false);
        //banner
        postRequest(new OnResponseListener<MakeMoneyBannerBean>() {
            @Override
            public Observable<BaseBean<MakeMoneyBannerBean>> createObservalbe() {
                ParamMap paramMap = new ParamMap();
                if (SPUtil.getInstance().isLogin()) {
                    paramMap.put("token", SPUtil.getInstance().getToken());
                }
                paramMap.put("sign", MD5.sign(paramMap));
                return NetService.getNetService().getMakeMoneyBanner(paramMap);
            }

            @Override
            public void onResponse(BaseBean<MakeMoneyBannerBean> result) {
                inflateBannerData(result.getData());
            }

            @Override
            public void onError(String errorMsg) {

            }
        }, false);
    }

    private MakeMoneyBannerBean makeMoneyBannerBean;

    /**
     * 填充大小banner
     *
     * @param result
     */
    private void inflateBannerData(MakeMoneyBannerBean result) {
        if (result == null || maxBanner == null || minBanner == null) {
            return;
        }
        makeMoneyBannerBean = result;
        if (result.getMaxBanner() == null || result.getMaxBanner().isEmpty()) {
            return;
        }
        for (int i = 0; i < result.getMaxBanner().size(); i++) {
            maxBannerImgUrl.add(result.getMaxBanner().get(i).getImgUrl());
        }

        maxBanner.setDelayTime(3000);   //3秒
        maxBanner.setImageLoader(new BannerImageLoader());
        maxBanner.setImages(maxBannerImgUrl);
        maxBanner.start();

        if (result.getMinBanner() == null || result.getMinBanner().isEmpty()) {
            minBanner.setVisibility(View.GONE);
        } else {
            minBanner.setVisibility(View.VISIBLE);
        }

        for (int i = 0; i < result.getMinBanner().size(); i++) {
            minBannerImgUrl.add(result.getMinBanner().get(i).getImgUrl());
        }

        minBanner.setDelayTime(3000);
        minBanner.setImageLoader(new BannerImageLoader());
        minBanner.setImages(minBannerImgUrl);
        minBanner.start();
    }

    private String currPlusFundCode = "";

    private void inflateCurrentPlusData(MakeMoneyCurrentPlusBean result) {
        if (result == null) {
            return;
        }
        currPlusFundCode = result.getFundCode();
        UIUtils.setText(tvCurrentPlusSlogn, result.getSlogan(), "");
        UIUtils.setText(tvCurrentPlusSecondSlogn, result.getSecondTitle(), "");
        UIUtils.setText(tvCurrentPlusTitle, result.getInnerTitle(), "");
        UIUtils.setText(tvCurrentPlusPeriod, result.getWealthTypeName(), "--");
        if (result.getProfit() == null || Double.isNaN(result.getProfit())) {
            tvCurrentPlusRate.setText("--");
            tvCurrentPlusRate.setTextColor(ContextCompat.getColor(getContext(), R.color.color_b9bbca));
            return;
        }
        double profit = result.getProfit();
        if (!Double.isNaN(profit)) {
            String rate = profit + "";
            if (rate.contains(".")) {
                int fCount = rate.split("\\.")[1].length();
                for (int i = fCount; i < 4; i++) {
                    rate = rate + "0";
                }
                if (profit > 0) {
                    rate = "+" + rate;
                } else if (profit == 0) {
                    tvCurrentPlusRate.setTextColor(ContextCompat.getColor(getContext(), R.color.color_b9bbca));
                } else if (profit < 0) {
                    tvCurrentPlusRate.setTextColor(ContextCompat.getColor(getContext(), R.color.color_18b293));
                }
                rate = rate + "%";
                tvCurrentPlusRate.setText(rate);
            }
        } else {
            UIUtils.setRate(tvCurrentPlusRate, result.getProfit() + "", false);
        }
    }


    private void inflate3List(MakeMoneyListBean result) {
        if (result == null) {
            return;
        }
        if (tvGraspChanceMore != null && tvChaseHotMore != null && tvBuyGoodFundMore != null) {
            tvGraspChanceMore.setVisibility(result.getChangeCount() > 3 ? View.VISIBLE : View.GONE);        //==3条不显示
            tvChaseHotMore.setVisibility(result.getFindNavCount() > 6 ? View.VISIBLE : View.GONE);        //==6条不显示
            tvBuyGoodFundMore.setVisibility(result.getNbCount() > 3 ? View.VISIBLE : View.GONE);        //==3条不显示
        }
        //抓机会
        if (result.getChangeCount() == 0) {
            llGraspChance.setVisibility(View.GONE);
        } else {
            llGraspChance.setVisibility(View.VISIBLE);
            if (llGraspChanceContent != null && result.getNavCatchMap() != null && !result.getNavCatchMap().isEmpty()) {
                for (int i = 0; i < result.getNavCatchMap().size(); i++) {
                    llGraspChanceContent.addView(new MakeMoneyGraspChanceHolder(i).inflateData(result.getNavCatchMap().get(i)));
                }
            }
        }
        //追热点
        if (result.getFindNavCount() == 0) {
            llChaseHot.setVisibility(View.GONE);
        } else {
            llChaseHot.setVisibility(View.VISIBLE);
            if (llChaseHotContent != null && result.getFindNav() != null && !result.getFindNav().isEmpty()) {
                for (int i = 0; i < result.getFindNav().size(); i++) {//追热点
                    llChaseHotContent.addView(new MakeMoneyChaseHotHolder(i).inflateData(result.getFindNav().get(i)));
                }
            }
        }
        //买好基
        if (result.getNbCount() == 0) {
            llBuyGoodFunds.setVisibility(View.GONE);
        } else {
            llBuyGoodFunds.setVisibility(View.VISIBLE);
            if (llBuyGoodFundContent != null && result.getNavNbMap() != null && !result.getNavNbMap().isEmpty()) {
                for (int i = 0; i < result.getNavNbMap().size(); i++) {
                    llBuyGoodFundContent.addView(new MakeMoneyBuyGoodFundHolder(i).inflateData(result.getNavNbMap().get(i)));
                }
            }
        }
    }

    @OnClick({R.id.tv_current_plus_buy_now, R.id.tv_grasp_chance_more, R.id.tv_chase_hot_more, R.id.tv_buy_good_fund_more, R.id.ll_current_plus})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_current_plus_buy_now:
                MobclickAgent.onEvent(getContext(), "click_makeMoneyFragment_currPlus_buyNow");
                if (!SPUtil.getInstance().isLogin()) {
                    LoginActivity.open(getContext());
                } else {
                    if (!SPUtil.getInstance().getUserInfo().getBindBankCard()) {
//                        ToastUtils.showShort("绑卡功能尚未开发，敬请期待");
                        RapidBindCardActivity.open(getContext());
                    } else {
                        if (TextUtils.isEmpty(SPUtil.getInstance().getUserInfo().getRiskLevel())) {
                            startActivity(new Intent(getContext(), RiskPreferenceActivity.class));
                        } else {
                            PutInActivity.open(getContext(), currPlusFundCode);
                        }
                    }
                }
                break;
            case R.id.tv_grasp_chance_more:
                MobclickAgent.onEvent(getContext(), "client_makeMoneyFragment_graspChance_more");
                GraspChanceActivity.open(getContext());
                break;
            case R.id.tv_chase_hot_more:
                MobclickAgent.onEvent(getContext(), "client_makeMoneyFragment_chaseHot_more");
                ChaseHotActivity.open(getContext());
                break;
            case R.id.tv_buy_good_fund_more:
                MobclickAgent.onEvent(getContext(), "client_makeMoneyFragment_buyGoodFund_more");
                BuyGoodFundActivity.open(getContext());
                break;
            case R.id.ll_current_plus:
                MobclickAgent.onEvent(getContext(), "client_makeMoneyFragment_currPlus");
                if (!SPUtil.getInstance().isLogin()) {
                    LoginActivity.open(getContext());
                } else {
                    startActivity(new Intent(getContext(), CurrentPlusActivity.class));
                }
                break;
        }
    }

    @Override
    protected boolean isCountPage() {
        return true;
    }

    @Override
    protected ErrorBean getErrorBean() {
        return new DefaultErrorPageBean(scrollView);
    }
}
