package com.test.bank.view.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.test.bank.R;
import com.test.bank.adapter.FundAnnouncementListShortAdapter;
import com.test.bank.adapter.SingleFundPagerAdapter;
import com.test.bank.base.BaseUIActivity;
import com.test.bank.bean.FundArchivesWithAnnouncementBean;
import com.test.bank.bean.FundTrendBean;
import com.test.bank.bean.SingleFundDetailBean;
import com.test.bank.http.HttpRequest;
import com.test.bank.utils.ConstantsUtil;
import com.test.bank.utils.DensityUtil;
import com.test.bank.utils.SPUtil;
import com.test.bank.utils.StringUtil;
import com.test.bank.weight.MyMarkerView;
import com.test.bank.weight.holder.LineChartViewHolder;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

import static com.test.bank.base.BaseApplication.getContext;

/*
* 描    述：<br>
* 作    者：崔朋朋<br>
* 时    间：2017/12/11<br>
*/
public class SingleFundDetailActivity extends BaseUIActivity {
    public static final String KEY_OF_FUND_STATUS = "fundStatusKey";
    public static final int FUND_STATUS_OPEN = 101;
    public static final int FUND_STATUS_BUY = 102;
    private int currentFundStatus = -1;

    @BindView(R.id.tv_singleFundDetailActivity_yesterdayRiseKey)
    TextView yesterdayRiseKeyTextView;
    @BindView(R.id.tv_singleFundDetailActivity_yesterdayRiseValue)
    TextView yesterdayRiseValueTextView;
    @BindView(R.id.tv_singleFundDetailActivity_netValueKey)
    TextView netValueKeyTextView;
    @BindView(R.id.tv_singleFundDetailActivity_netValueValue)
    TextView netValueValueTextView;
    @BindView(R.id.tv_singleFundDetailActivity_fundTypeTag1)
    TextView fundTypeTag1TextView;
    @BindView(R.id.tv_singleFundDetailActivity_fundTypeTag2)
    TextView fundTypeTag2TextView;
    @BindView(R.id.v_singleFundDetailActivity_fundTypeTagDivider)
    View fundTypeTagDividerView;

    @BindView(R.id.ll_singleFundDetailActivity_allStar)
    LinearLayout allStarLinearLayout;
    @BindView(R.id.tv_singleFundDetailActivity_noStar)
    TextView noStarTextView;
    @BindView(R.id.iv_singleFundDetailActivity_star1)
    ImageView star1ImageView;
    @BindView(R.id.iv_singleFundDetailActivity_star2)
    ImageView star2ImageView;
    @BindView(R.id.iv_singleFundDetailActivity_star3)
    ImageView star3ImageView;
    @BindView(R.id.iv_singleFundDetailActivity_star4)
    ImageView star4ImageView;
    @BindView(R.id.iv_singleFundDetailActivity_star5)
    ImageView star5ImageView;
    @BindView(R.id.iv_singleFundDetailActivity_starAsk)
    ImageView starAskImageView;


    @BindView(R.id.tv_singleFundDetailActivity_selfFundYield)
    TextView selfFundYieldTextView;
    @BindView(R.id.tv_singleFundDetailActivity_hs300Yield)
    TextView hs300YieldTextView;
    @BindView(R.id.ll_singleFundDetailActivity_yieldTrendChartFragment)
    LinearLayout yieldTrendChartFragmentLinearLayout;
    @BindView(R.id.tv_singleFundDetailActivity_last1Month)
    TextView last1MonthTextView;
    @BindView(R.id.tv_singleFundDetailActivity_last3Month)
    TextView last3MonthTextView;
    @BindView(R.id.tv_singleFundDetailActivity_last6Month)
    TextView last6MonthTextView;
    @BindView(R.id.tv_singleFundDetailActivity_last12Month)
    TextView last12MonthTextView;
    @BindView(R.id.ll_singleFundDetailActivity_yieldTrend)
    LinearLayout yieldTrendLinearLayout;

    @BindView(R.id.tv_singleFundDetailActivity_tradeRule1)
    TextView tradeRule1TextView;
    @BindView(R.id.tv_singleFundDetailActivity_tradeRule2)
    TextView tradeRule2TextView;
    @BindView(R.id.tv_singleFundDetailActivity_tradeRule3)
    TextView tradeRule3TextView;

    @BindView(R.id.tv_singleFundDetailActivity_buyFlowStep1Icon)
    TextView buyFlowStep1IconTextView;
    @BindView(R.id.tv_singleFundDetailActivity_buyFlowStep1Title)
    TextView buyFlowStep1TitleTextView;
    @BindView(R.id.ll_singleFundDetailActivity_buyFlowStep1Content)
    TextView buyFlowStep1ContentTextView;
    @BindView(R.id.ll_singleFundDetailActivity_buyFlow)
    LinearLayout buyFlowLinearLayout;

    @BindView(R.id.tv_singleFundDetailActivity_archivesTitle)
    TextView archivesTitleTextView;
    @BindView(R.id.v_singleFundDetailActivity_archivesTitleRedLine)
    View archivesTitleRedLineView;
    @BindView(R.id.tv_singleFundDetailActivity_announcementTitle)
    TextView announcementTitleTextView;
    @BindView(R.id.v_singleFundDetailActivity_announcementTitleRedLine)
    View announcementTitleRedLineView;
    TextView fundInfoTextView;

    TextView fundAmountTextView;
    TextView setupTimeTextView;
    RelativeLayout fundInfoRinearLayout;
    TextView positionAllocationValueTextView;
    LinearLayout positionAllocationLinearLayout;
    TextView fundManagerValueTextView;
    LinearLayout fundManagerLinearLayout;
    TextView fundCompanyValueTextView;
    LinearLayout fundCompanyLinearLayout;
    TextView tradeNoticeValueTextView;
    View tradeNoticeDiivderView;
    LinearLayout tradeNoticeLinearLayout;

    RecyclerView announcementListRecyclerView;
    TextView viewMoreTextView;

    LinearLayout archivesListLinearLayout;
    LinearLayout announcementListLinearLayout;

    @BindView(R.id.tv_singleFundDetailActivity_buyNow)
    TextView buyNowTextView;

    @BindView(R.id.vp_aftersales)
    ViewPager fundAnnouncementViewPager;

    private SingleFundPagerAdapter singleFundPagerAdapter;
    private List<View> viewList = new ArrayList<View>();

    public static final String KEY_OF_FUND_ARCHIVES_WITH_ANNOUNCEMENT_MODEL = "fundArchivesWithAnnouncementKey";
    private FundArchivesWithAnnouncementBean mFundArchivesWithAnnouncementBean = new FundArchivesWithAnnouncementBean();
    private FundTrendBean mFundTrendBean = new FundTrendBean();
    private SingleFundDetailBean singleFundDetailBean = new SingleFundDetailBean();
    private LineChartViewHolder lineChartViewHolder = new LineChartViewHolder();
    public static final String KEY_OF_TITLE = "titleKey";
    public static final String TITLE_SEPERATOR = "-";
    private String mTitle = "";
    public static final String KEY_OF_FUND_CODE = "fundCodeKey";
    private String fundCode;
    private FundAnnouncementListShortAdapter fundAnnouncementAdapter;
    private List<FundArchivesWithAnnouncementBean.Disc_main_list> fundAnnouncementList = new ArrayList<FundArchivesWithAnnouncementBean.Disc_main_list>();
    private List<Entry> values1 = new ArrayList<Entry>();
    private List<Entry> values2 = new ArrayList<Entry>();

    @OnClick({R.id.ll_singleFundDetailActivity_announcementTitle, R.id.ll_singleFundDetailActivity_archivesTitle, R.id.tv_singleFundDetailActivity_buyNow,
            R.id.tv_singleFundDetailActivity_last1Month, R.id.tv_singleFundDetailActivity_last3Month, R.id.tv_singleFundDetailActivity_last6Month,
            R.id.tv_singleFundDetailActivity_last12Month, R.id.ll_singleFundDetailActivity_yesterdayRise, R.id.ll_singleFundDetailActivity_tradeRule,
            R.id.iv_singleFundDetailActivity_starAsk})
    public void onItemClick(View v) {
        Intent intent = new Intent();
        List<FundTrendBean.TrendInfo> list = new ArrayList<FundTrendBean.TrendInfo>();
        switch (v.getId()) {
            case R.id.ll_singleFundDetailActivity_archivesTitle:
                showArchivesList();
                fundAnnouncementViewPager.setCurrentItem(0);
                break;
            case R.id.ll_singleFundDetailActivity_announcementTitle:
                showAnnouncementList();
                fundAnnouncementViewPager.setCurrentItem(1);
                break;
            case R.id.tv_singleFundDetailActivity_last1Month:
                clearChartData();
                list = mFundTrendBean.getLast1Month();
                values1.add(new Entry(4, -9.7f));
                values1.add(new Entry(6, -1.2f));
                values1.add(new Entry(9, -2.5f));
                values1.add(new Entry(12, 10.5f));
                values1.add(new Entry(15, -3.6f));

                values2.add(new Entry(3, 1.2f));
                values2.add(new Entry(6, 2.1f));
                values2.add(new Entry(9, 3.6f));
                values2.add(new Entry(12, 3.2f));
                values2.add(new Entry(15, 4.1f));
                bindData(1);
                break;
            case R.id.tv_singleFundDetailActivity_last3Month:
                clearChartData();
                list = mFundTrendBean.getLast3Month();
                values1.add(new Entry(2, 10));
                values1.add(new Entry(4, 20));
                values1.add(new Entry(6, 10));
                values1.add(new Entry(10, 18));
                values1.add(new Entry(39, 10));

                values2.add(new Entry(20, 30));
                values2.add(new Entry(29, 38));
                values2.add(new Entry(38, 30));
                values2.add(new Entry(20, 37));
                values2.add(new Entry(15, 36));
                bindData(3);
                break;
            case R.id.tv_singleFundDetailActivity_last6Month:
                clearChartData();
                list = mFundTrendBean.getLast6Month();
                values1.add(new Entry(4, 10));
                values1.add(new Entry(6, 15));
                values1.add(new Entry(9, 20));
                values1.add(new Entry(12, 5));
                values1.add(new Entry(15, 30));

                values2.add(new Entry(3, 110));
                values2.add(new Entry(6, 115));
                values2.add(new Entry(9, 130));
                values2.add(new Entry(12, 85));
                values2.add(new Entry(15, 90));
                bindData(6);
                break;
            case R.id.tv_singleFundDetailActivity_last12Month:
                clearChartData();
                list = mFundTrendBean.getLast12Month();
                values1.add(new Entry(4, -9.7f));
                values1.add(new Entry(6, -1.2f));
                values1.add(new Entry(9, -2.5f));
                values1.add(new Entry(12, 10.5f));
                values1.add(new Entry(15, -3.6f));

                values2.add(new Entry(3, 1.2f));
                values2.add(new Entry(6, 2.1f));
                values2.add(new Entry(9, 3.6f));
                values2.add(new Entry(12, 3.2f));
                values2.add(new Entry(15, 4.1f));
                bindData(12);
                break;
            case R.id.tv_singleFundDetailActivity_buyNow:
                if (!SPUtil.getInstance().isLogin()) {
                    LoginActivity.open(this);
                    return;
                }
                if (!SPUtil.getInstance().getUserInfo().getBindBankCard()) {
                    Toast.makeText(this, "请先开户", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (StringUtil.isNull(SPUtil.getInstance().getUserInfo().getRiskLevel())) {
                    RiskTestResultActivity.open(this);
                    return;
                }

                FundBuyInActivity.open(this, singleFundDetailBean.getFundcode(), singleFundDetailBean.getFundsname());

                break;
            case R.id.ll_singleFundDetailActivity_yesterdayRise:
                intent.setClass(this, NetValueListActivity.class);
                intent.putExtra(KEY_OF_FUND_CODE, fundCode);
                intent.putExtra(KEY_OF_TITLE, mTitle);
                startActivity(intent);
                break;
            case R.id.ll_singleFundDetailActivity_tradeRule:
                launchTradeNoticePage(intent);
                break;
            case R.id.iv_singleFundDetailActivity_starAsk:
                final Dialog dialog = new Dialog(this);
                View view = View.inflate(this, R.layout.dialog_fund_star, null);
                view.findViewById(R.id.tv_dialog_close).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//                dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(true);
                Display display = this.getWindowManager().getDefaultDisplay();
//                int width = display.getWidth();
//                int height = display.getHeight();
                int width = WindowManager.LayoutParams.FLAG_FULLSCREEN;
                int height = WindowManager.LayoutParams.FLAG_FULLSCREEN;
                //设置dialog的宽高为屏幕的宽高
                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width, height);
                dialog.setContentView(view, layoutParams);
//                dialog.setContentView(view);
                dialog.show();
                break;
        }
    }

    class MyViewOnclickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            switch (v.getId()) {
                case R.id.tv_singleFundDetailActivity_viewMore:
                    intent.setClass(SingleFundDetailActivity.this, FundAnnouncementListActivity.class);
                    intent.putExtra(KEY_OF_FUND_CODE, fundCode);
                    startActivity(intent);
                    break;
                case R.id.rl_singleFundDetailActivity_fundInfo:
                    intent.setClass(SingleFundDetailActivity.this, FundInfoActivity.class);
                    intent.putExtra(KEY_OF_FUND_CODE, fundCode);
                    startActivity(intent);
                    break;
                case R.id.ll_singleFundDetailActivity_positionAllocation:
                    intent.setClass(SingleFundDetailActivity.this, PositionAllocationActivity.class);
                    intent.putExtra(KEY_OF_FUND_CODE, fundCode);
                    startActivity(intent);
                    break;
                case R.id.ll_singleFundDetailActivity_fundManager:
                    intent.setClass(SingleFundDetailActivity.this, FundManagerListActivity.class);
                    intent.putExtra(KEY_OF_FUND_ARCHIVES_WITH_ANNOUNCEMENT_MODEL, mFundArchivesWithAnnouncementBean);
                    intent.putExtra(KEY_OF_FUND_CODE, fundCode);
                    startActivity(intent);
                    break;
                case R.id.ll_singleFundDetailActivity_fundCompany:
                    intent.setClass(SingleFundDetailActivity.this, FundCompanyActivity.class);
                    intent.putExtra(KEY_OF_FUND_CODE, fundCode);
                    startActivity(intent);
                    break;
                case R.id.ll_singleFundDetailActivity_tradeNotice:
                    launchTradeNoticePage(intent);
                    break;
            }
        }
    }

    public void launchTradeNoticePage(Intent intent) {
        if (currentFundStatus == FUND_STATUS_OPEN) {
            intent.setClass(SingleFundDetailActivity.this, TradeNoticeActivity.class);
        } else {
            intent.setClass(SingleFundDetailActivity.this, TradeNoticeShortActivity.class);
        }
        intent.putExtra(KEY_OF_FUND_CODE, fundCode);
        intent.putExtra(KEY_OF_FUND_ARCHIVES_WITH_ANNOUNCEMENT_MODEL, mFundArchivesWithAnnouncementBean);
        startActivity(intent);
    }

    private void clearChartData() {
        values1.clear();
        values2.clear();
    }

    @Override
    protected String getPageTitle() {
        return "";
    }

    @Override
    protected int getSubLayoutId() {
        return R.layout.activity_single_fund_detail;
    }

    @Override
    protected void initPageData() {
        fundCode = getIntent().getStringExtra(KEY_OF_FUND_CODE);

        archivesListLinearLayout = (LinearLayout) View.inflate(this, R.layout.view_fund_archieves, null);
        announcementListLinearLayout = (LinearLayout) View.inflate(this, R.layout.view_fund_announcement, null);

        MyViewOnclickListener myViewOnclickListener = new MyViewOnclickListener();
        fundInfoTextView = archivesListLinearLayout.findViewById(R.id.tv_singleFundDetailActivity_fundInfo);
        fundAmountTextView = archivesListLinearLayout.findViewById(R.id.tv_singleFundDetailActivity_fundAmount);
        setupTimeTextView = archivesListLinearLayout.findViewById(R.id.tv_singleFundDetailActivity_setupTime);
        fundInfoRinearLayout = archivesListLinearLayout.findViewById(R.id.rl_singleFundDetailActivity_fundInfo);
        fundInfoRinearLayout.setOnClickListener(myViewOnclickListener);
        positionAllocationValueTextView = archivesListLinearLayout.findViewById(R.id.tv_singleFundDetailActivity_positionAllocationValue);
        positionAllocationLinearLayout = archivesListLinearLayout.findViewById(R.id.ll_singleFundDetailActivity_positionAllocation);
        positionAllocationLinearLayout.setOnClickListener(myViewOnclickListener);
        positionAllocationLinearLayout.setOnClickListener(myViewOnclickListener);
        fundManagerValueTextView = archivesListLinearLayout.findViewById(R.id.tv_singleFundDetailActivity_fundManagerValue);
        fundManagerLinearLayout = archivesListLinearLayout.findViewById(R.id.ll_singleFundDetailActivity_fundManager);
        fundManagerLinearLayout.setOnClickListener(myViewOnclickListener);
        fundCompanyValueTextView = archivesListLinearLayout.findViewById(R.id.tv_singleFundDetailActivity_fundCompanyValue);
        fundCompanyLinearLayout = archivesListLinearLayout.findViewById(R.id.ll_singleFundDetailActivity_fundCompany);
        fundCompanyLinearLayout.setOnClickListener(myViewOnclickListener);
        tradeNoticeValueTextView = archivesListLinearLayout.findViewById(R.id.tv_singleFundDetailActivity_tradeNoticeValue);
        tradeNoticeDiivderView = archivesListLinearLayout.findViewById(R.id.v_singleFundDetailActivity_tradeNoticeDivider);
        tradeNoticeLinearLayout = archivesListLinearLayout.findViewById(R.id.ll_singleFundDetailActivity_tradeNotice);
        tradeNoticeLinearLayout.setOnClickListener(myViewOnclickListener);

        announcementListRecyclerView = announcementListLinearLayout.findViewById(R.id.rv_singleFundDetailActivity_announcementList);
        viewMoreTextView = announcementListLinearLayout.findViewById(R.id.tv_singleFundDetailActivity_viewMore);
        viewMoreTextView.setOnClickListener(myViewOnclickListener);
        fundAnnouncementAdapter = new FundAnnouncementListShortAdapter(this, fundAnnouncementList);
        announcementListRecyclerView.setAdapter(fundAnnouncementAdapter);
        announcementListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        viewList.add(archivesListLinearLayout);
        viewList.add(announcementListLinearLayout);
        singleFundPagerAdapter = new SingleFundPagerAdapter(viewList);
        fundAnnouncementViewPager.setAdapter(singleFundPagerAdapter);
        fundAnnouncementViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    showArchivesList();
                } else {
                    showAnnouncementList();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        disableBuyNowButton();

        showArchivesList();

        getFundArchivesWithAnnouncement();
    }


    private void disableBuyNowButton() {
        buyNowTextView.setEnabled(false);
        buyNowTextView.setBackgroundColor(getResources().getColor(R.color.appDisableButtonColor));
    }

    private void enableBuyNowButton() {
        buyNowTextView.setEnabled(true);
        buyNowTextView.setBackgroundColor(getResources().getColor(R.color.appRedColor));
    }


    @Override
    protected void setRightMenu() {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) baseRightMenuImageView.getLayoutParams();
        layoutParams.width = DensityUtil.dip2px(50);
        layoutParams.height = DensityUtil.dip2px(22);
        baseRightMenuImageView.setLayoutParams(layoutParams);
        baseRightMenuImageView.setBackgroundResource(R.mipmap.add_optional);
        baseRightMenuImageView.setVisibility(View.VISIBLE);
        baseRightMenuImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SPUtil.getInstance().isLogin()) {
                    baseRightMenuImageView.setEnabled(false);
                    if (singleFundDetailBean.isCollected()) {
                        deleteOptionalFund();
                    } else {
                        addToOptionalFund();
                    }
                } else {
                    LoginActivity.open(SingleFundDetailActivity.this);
                }
            }
        });
    }

    private void showCorrespondingFundStatusView(SingleFundDetailBean singleFundDetailBean) {
        switch (singleFundDetailBean.getFundstate()) {
            case 1:
            case 2:
            case 3:
            case 6:
            case 7:
                currentFundStatus = FUND_STATUS_OPEN;
                String yesIncome = singleFundDetailBean.getYestincome();
                if (StringUtil.notEmpty(yesIncome)) {
                    if (Float.valueOf(yesIncome) > 0) {
                        yesterdayRiseValueTextView.setText(generateYesterdayRaiseText("+" + yesIncome + "%"));
                        yesterdayRiseValueTextView.setTextColor(getResources().getColor(R.color.appRedColor));
                    } else if (Float.valueOf(yesIncome) < 0) {
                        yesterdayRiseValueTextView.setText(generateYesterdayRaiseText(yesIncome + "%"));
                        yesterdayRiseValueTextView.setTextColor(getResources().getColor(R.color.appNegativeRateColor));
                    } else {
                        yesterdayRiseValueTextView.setText(generateYesterdayRaiseText(yesIncome + "%"));
                        yesterdayRiseValueTextView.setTextColor(getResources().getColor(R.color.appContentColor));
                    }
                }
                netValueKeyTextView.setText("净值(" + singleFundDetailBean.getTradedate() + ")");
                String netValue = singleFundDetailBean.getFundnav();
                netValueValueTextView.setText(netValue);

                allStarLinearLayout.setVisibility(View.VISIBLE);
                noStarTextView.setVisibility(View.GONE);
                yieldTrendLinearLayout.setVisibility(View.VISIBLE);
                buyFlowLinearLayout.setVisibility(View.GONE);
                fundTypeTagDividerView.setVisibility(View.GONE);
                tradeNoticeLinearLayout.setVisibility(View.GONE);
                tradeNoticeDiivderView.setVisibility(View.GONE);
                break;
            default:
                currentFundStatus = FUND_STATUS_BUY;
                yesterdayRiseKeyTextView.setText("昨日涨幅");
                yesterdayRiseValueTextView.setText("--");
                yesterdayRiseValueTextView.setTextColor(getResources().getColor(R.color.appContentColor));
                netValueKeyTextView.setText("净值(--)");
                netValueValueTextView.setText("--");
                netValueValueTextView.setTextColor(getResources().getColor(R.color.appTitleColor));
                allStarLinearLayout.setVisibility(View.GONE);
                noStarTextView.setVisibility(View.VISIBLE);
                yieldTrendLinearLayout.setVisibility(View.GONE);
                buyFlowLinearLayout.setVisibility(View.VISIBLE);
                fundTypeTagDividerView.setVisibility(View.VISIBLE);
                tradeNoticeLinearLayout.setVisibility(View.VISIBLE);
                tradeNoticeDiivderView.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        getFundDetailInfo();
        getSingleFundYieldTrend();

        values1.add(new Entry(4, 10));
        values1.add(new Entry(6, 15));
        values1.add(new Entry(9, 20));
        values1.add(new Entry(12, 5));
        values1.add(new Entry(15, 30));

        values2.add(new Entry(3, 110));
        values2.add(new Entry(6, 115));
        values2.add(new Entry(9, 130));
        values2.add(new Entry(12, 85));
        values2.add(new Entry(15, 90));
        fillChartData();
        bindData(1);
    }

    /**
     * 选择最近几月的收益率走势
     *
     * @param monthTextView
     */
    private void selectMonthTextView(TextView monthTextView) {
        last1MonthTextView.setSelected(false);
        last1MonthTextView.setTextColor(getResources().getColor(R.color.appCommentColor));
        last3MonthTextView.setSelected(false);
        last3MonthTextView.setTextColor(getResources().getColor(R.color.appCommentColor));
        last6MonthTextView.setSelected(false);
        last6MonthTextView.setTextColor(getResources().getColor(R.color.appCommentColor));
        last12MonthTextView.setSelected(false);
        last12MonthTextView.setTextColor(getResources().getColor(R.color.appCommentColor));
        monthTextView.setSelected(true);
        monthTextView.setTextColor(getResources().getColor(R.color.appTitleColor));
    }

    /**
     * 显示基金档案内容
     */
    private void showArchivesList() {
        archivesTitleTextView.setTextColor(getResources().getColor(R.color.appRedColor));
        archivesTitleRedLineView.setVisibility(View.VISIBLE);

        announcementTitleTextView.setTextColor(getResources().getColor(R.color.appTitleColor));
        announcementTitleRedLineView.setVisibility(View.INVISIBLE);
    }

    /**
     * 显示基金公告内容
     */
    private void showAnnouncementList() {
        archivesTitleTextView.setTextColor(getResources().getColor(R.color.appTitleColor));
        archivesTitleRedLineView.setVisibility(View.INVISIBLE);

        announcementTitleTextView.setTextColor(getResources().getColor(R.color.appRedColor));
        announcementTitleRedLineView.setVisibility(View.VISIBLE);
    }

    /**
     * 获取基金详情
     */
    private void getFundDetailInfo() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("fundcode", fundCode);
        params.put("showModule", "1");  //来源（1-搜索，2-其他）
        params.put("token", SPUtil.getInstance().getToken());

        HttpRequest.post(HttpRequest.APP_INTERFACE_WEB_URL + HttpRequest.SINGLE_FUND_DETAIL, params, this, new HttpRequest.HttpResponseCallBank() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                singleFundDetailBean = JSON.parseObject(response.body(), SingleFundDetailBean.class);
                fundCode = singleFundDetailBean.getFundcode();

                baseTitleTextView.setText(generateTitleText(SingleFundDetailActivity.this, singleFundDetailBean.getFundsname(), singleFundDetailBean.getFundcode()));
                mTitle = singleFundDetailBean.getFundsname() + TITLE_SEPERATOR + singleFundDetailBean.getFundcode();
                if (SPUtil.getInstance().isLogin() && singleFundDetailBean.isCollected()) {
                    baseRightMenuImageView.setBackgroundResource(R.mipmap.optional_selected);
                } else {
                    baseRightMenuImageView.setBackgroundResource(R.mipmap.add_optional);
                }

                tradeRule1TextView.setText("今日15点前");
                tradeRule2TextView.setText(singleFundDetailBean.getConfirmDate());
                tradeRule3TextView.setText(singleFundDetailBean.getProfitDate());

                //1-开放 2-暂停
                if (singleFundDetailBean.getDeclarestate() == 1) {
                    enableBuyNowButton();
                } else if (singleFundDetailBean.getDeclarestate() == 2) {
                    disableBuyNowButton();
                }

                showCorrespondingFundStatusView(singleFundDetailBean);

                if (StringUtil.isNull(singleFundDetailBean.getFundtype())) {
                    fundTypeTag1TextView.setVisibility(View.GONE);
                }
                fundTypeTag1TextView.setText(singleFundDetailBean.getFundtype());
//                fundTypeTag1TextView.setBackgroundResource(R.drawable.rectangle_gray_border_bg);
//                fundTypeTag1TextView.setBackgroundResource(R.drawable.rectangle_gray_border_gray_bg);

                String risk = singleFundDetailBean.getCustrisk();
                if (StringUtil.isNull(risk)) {
                    fundTypeTag2TextView.setVisibility(View.GONE);
                }
                fundTypeTag2TextView.setText(risk);
                if ("高风险".equals(risk) || "中高风险".equals(risk)) {
//                    fundTypeTag2TextView.setBackgroundResource(R.drawable.rectangle_red_border_bg);
//                    fundTypeTag2TextView.setBackgroundResource(R.drawable.rectangle_gray_border_gray_bg);
                    fundTypeTag2TextView.setTextColor(getResources().getColor(R.color.appRedColor));
                } else if ("中风险".equals(risk)) {
//                    fundTypeTag2TextView.setBackgroundResource(R.drawable.rectangle_ffa200_border_bg);
//                    fundTypeTag2TextView.setBackgroundResource(R.drawable.rectangle_gray_border_gray_bg);
                    fundTypeTag2TextView.setTextColor(Color.parseColor("#ffa200"));
                } else if ("中低风险".equals(risk) || "低风险".equals(risk)) {
//                    fundTypeTag2TextView.setBackgroundResource(R.drawable.rectangle_18b293_border_bg);
//                    fundTypeTag2TextView.setBackgroundResource(R.drawable.rectangle_gray_border_gray_bg);
                    fundTypeTag2TextView.setTextColor(Color.parseColor("#18b293"));
                }

                star1ImageView.setBackgroundResource(R.mipmap.star_unselected);
                star2ImageView.setBackgroundResource(R.mipmap.star_unselected);
                star3ImageView.setBackgroundResource(R.mipmap.star_unselected);
                star4ImageView.setBackgroundResource(R.mipmap.star_unselected);
                star5ImageView.setBackgroundResource(R.mipmap.star_unselected);
                switch (singleFundDetailBean.getRating()) {
                    case 1:
                        star1ImageView.setBackgroundResource(R.mipmap.star_selected);
                        break;
                    case 2:
                        star1ImageView.setBackgroundResource(R.mipmap.star_selected);
                        star2ImageView.setBackgroundResource(R.mipmap.star_selected);
                        break;
                    case 3:
                        star1ImageView.setBackgroundResource(R.mipmap.star_selected);
                        star2ImageView.setBackgroundResource(R.mipmap.star_selected);
                        star3ImageView.setBackgroundResource(R.mipmap.star_selected);
                        break;
                    case 4:
                        star1ImageView.setBackgroundResource(R.mipmap.star_selected);
                        star2ImageView.setBackgroundResource(R.mipmap.star_selected);
                        star3ImageView.setBackgroundResource(R.mipmap.star_selected);
                        star4ImageView.setBackgroundResource(R.mipmap.star_selected);
                        break;
                    case 5:
                        star1ImageView.setBackgroundResource(R.mipmap.star_selected);
                        star2ImageView.setBackgroundResource(R.mipmap.star_selected);
                        star3ImageView.setBackgroundResource(R.mipmap.star_selected);
                        star4ImageView.setBackgroundResource(R.mipmap.star_selected);
                        star5ImageView.setBackgroundResource(R.mipmap.star_selected);
                        break;
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    /**
     * 基金档案附带公告
     */
    private void getFundArchivesWithAnnouncement() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("fundcode", fundCode);

        HttpRequest.post(HttpRequest.APP_INTERFACE_WEB_URL + HttpRequest.FUND_ARCHIVES_WITH_ANNOUNCEMENT, params, this, new HttpRequest.HttpResponseCallBank() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                mFundArchivesWithAnnouncementBean = JSON.parseObject(response.body(), FundArchivesWithAnnouncementBean.class);
                fundAmountTextView.setText("基金规模" + mFundArchivesWithAnnouncementBean.getFnd_size() + "亿元");
                if (StringUtil.isNull(mFundArchivesWithAnnouncementBean.getFnd_size())) {
                    fundAmountTextView.setText(ConstantsUtil.REPLACE_BLANK_SYMBOL);
                }
                setupTimeTextView.setText("成立时间: " + mFundArchivesWithAnnouncementBean.getBuild_date());

                String fundManager = "";
                List<FundArchivesWithAnnouncementBean.Indi_name_list> fundManagerList = mFundArchivesWithAnnouncementBean.getIndi_name_list();
                if (fundManagerList.size() == 1) {
                    fundManager = fundManagerList.get(0).getIndi_name();
                } else if (fundManagerList.size() == 2) {
                    fundManager = fundManagerList.get(0).getIndi_name() + "," + fundManagerList.get(1).getIndi_name();
                } else if (fundManagerList.size() > 2) {
                    fundManager = fundManagerList.get(0).getIndi_name() + "," + fundManagerList.get(1).getIndi_name() + "...";
                }
                fundManagerValueTextView.setText(fundManager);
                fundCompanyValueTextView.setText(mFundArchivesWithAnnouncementBean.getFnd_org_name());
                tradeNoticeValueTextView.setText("交易费率及金额");

                fundAnnouncementAdapter.upateData(true, mFundArchivesWithAnnouncementBean.getDisc_main_list());
                singleFundPagerAdapter.upateData(true, viewList);

                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) fundAnnouncementViewPager.getLayoutParams();
                layoutParams.width = ViewPager.LayoutParams.MATCH_PARENT;
                if (currentFundStatus == FUND_STATUS_BUY) {
                    layoutParams.height = DensityUtil.dip2px(340);
                    fundAnnouncementViewPager.setLayoutParams(layoutParams);
                    fundAnnouncementAdapter.setCount(3);
                } else {
                    layoutParams.height = DensityUtil.dip2px(275);
                    fundAnnouncementViewPager.setLayoutParams(layoutParams);
                    fundAnnouncementAdapter.setCount(2);
                    LinearLayout.LayoutParams textViewLayoutParams = (LinearLayout.LayoutParams) viewMoreTextView.getLayoutParams();
                    textViewLayoutParams.topMargin = DensityUtil.dip2px(20);
                    viewMoreTextView.setLayoutParams(textViewLayoutParams);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    /**
     * 单只基金收益率走势
     */
    private void getSingleFundYieldTrend() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("fundcode", fundCode);

        HttpRequest.post(HttpRequest.APP_INTERFACE_WEB_URL + HttpRequest.SINGLE_FUND_YIELD_TREND, params, this, new HttpRequest.HttpResponseCallBank() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                mFundTrendBean = JSON.parseObject(response.body(), FundTrendBean.class);
                yieldTrendLinearLayout.removeAllViews();
                yieldTrendLinearLayout.addView(lineChartViewHolder.inflateData(mFundTrendBean), 0);

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    /**
     * 添加自选
     */
    private void addToOptionalFund() {
        Map<String, String> params = new HashMap<String, String>();
//        params.put("fundcode", fundCode);
        params.put("sid", fundCode);
        params.put("token", SPUtil.getInstance().getToken());

        HttpRequest.post(HttpRequest.APP_INTERFACE_WEB_URL + HttpRequest.ADD_TO_OPTIONAL_FUND, params, this, new HttpRequest.HttpResponseCallBank() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                baseRightMenuImageView.setBackgroundResource(R.mipmap.optional_selected);
                singleFundDetailBean.setCollected(true);
                baseRightMenuImageView.setEnabled(true);
                Toast.makeText(SingleFundDetailActivity.this, "成功添加自选", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                baseRightMenuImageView.setEnabled(true);
            }
        });
    }

    /**
     * 删除自选
     */
    private void deleteOptionalFund() {
        Map<String, String> params = new HashMap<String, String>();
//        params.put("fundcode", fundCode);
        params.put("sid", fundCode);
        params.put("token", SPUtil.getInstance().getToken());

        HttpRequest.post(HttpRequest.APP_INTERFACE_WEB_URL + HttpRequest.DELETE_OPTIONAL_FUND, params, this, new HttpRequest.HttpResponseCallBank() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                baseRightMenuImageView.setBackgroundResource(R.mipmap.add_optional);
                singleFundDetailBean.setCollected(false);
                baseRightMenuImageView.setEnabled(true);
                Toast.makeText(SingleFundDetailActivity.this, "已删除自选", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                baseRightMenuImageView.setEnabled(true);
            }
        });
    }

    //设置中间文字
    public static SpannableString generateTitleText(Context context, String fundName, String fundCode) {
        String title = fundName + "\n" + fundCode;
        SpannableString s = new SpannableString(title);
        //s.setSpan(new RelativeSizeSpan(1.7f), 0, 14, 0);
        //s.setSpan(new StyleSpan(Typeface.NORMAL), 14, s.length() - 15, 0);
//        s.setSpan(new ForegroundColorSpan(resources.getColor(R.color.appTitleColor)), 0, 4, 0);
//        s.setSpan(new ForegroundColorSpan(resources.getColor(R.color.appContentColor)), 4, s.length(), 0);
        //s.setSpan(new RelativeSizeSpan(.8f), 14, s.length() - 15, 0);
        // s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - 14, s.length(), 0);
        // s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length() - 14, s.length(), 0);
        s.setSpan(new TextAppearanceSpan(context, R.style.style_titleBlack_17sp), 0, fundName.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        s.setSpan(new TextAppearanceSpan(context, R.style.style_titleBlack_12sp), fundName.length(), title.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return s;
    }

    //设置中间文字
    private SpannableString generateYesterdayRaiseText(String yesterdayRaise) {
        SpannableString s = new SpannableString(yesterdayRaise);
        s.setSpan(new TextAppearanceSpan(this, R.style.style_yesterdayRaiseValue_34sp), 0, yesterdayRaise.length() - 1,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        s.setSpan(new TextAppearanceSpan(this, R.style.style_yesterdayRaiseValue_22sp), yesterdayRaise.length() - 1, yesterdayRaise.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return s;
    }


    @BindView(R.id.lineChart)
    LineChart lineChart;

    private XAxis xAxis;
    private YAxis yAxis;

    protected void fillChartData() {
        //创建描述信息
        Description description = new Description();
        description.setText("");
        description.setTextColor(Color.RED);
        description.setTextSize(20);
        lineChart.setDescription(description);//设置图表描述信息

        lineChart.setNoDataText("没有数据熬");//没有数据时显示的文字
        lineChart.setNoDataTextColor(Color.BLUE);//没有数据时显示文字的颜色
        lineChart.setDrawGridBackground(false);//chart 绘图区后面的背景矩形将绘制
        lineChart.setDrawBorders(false);//禁止绘制图表边框的线
        //lineChart.setBorderColor(); //设置 chart 边框线的颜色。
        //lineChart.setBorderWidth(); //设置 chart 边界线的宽度，单位 dp。
        //lineChart.setLogEnabled(true);//打印日志
        //lineChart.notifyDataSetChanged();//刷新数据
        //lineChart.invalidate();//重绘

        setXAxis();
        setYAxis();
        setInterActive();
        setLegend();

        MyMarkerView mv = new MyMarkerView(getContext(), R.layout.custom_marker_view);
        mv.setChartView(lineChart);
        lineChart.setMarker(mv);

    }

    private void bindData(int lastMonth) {
        switch (lastMonth) {
            case 1:
                selectMonthTextView(last1MonthTextView);
                break;
            case 3:
                selectMonthTextView(last3MonthTextView);
                break;
            case 6:
                selectMonthTextView(last6MonthTextView);
                break;
            case 12:
                selectMonthTextView(last12MonthTextView);
                break;
        }

        //LineDataSet每一个对象就是一条连接线
        LineDataSet lineDataSet1;
        LineDataSet lineDataSet2;

        //判断图表中原来是否有数据
        if (lineChart.getData() != null) {
            //获取数据1
//            lineDataSet1 = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
//            lineDataSet2= (LineDataSet) lineChart.getData().getDataSetByIndex(1);
//            lineDataSet1.setValues(values1);
//            lineDataSet2.setValues(values2);
//            lineChart.invalidate();
            lineChart.clear();
        }
//        else {
        //设置数据1  参数1：数据源 参数2：图例名称
        lineDataSet1 = new LineDataSet(values1, "测试数据1");
        lineDataSet1.setColor(getResources().getColor(R.color.appRedColor));
        lineDataSet1.setCircleColor(Color.BLACK);
        lineDataSet1.setLineWidth(1f);//设置线宽
        lineDataSet1.setCircleRadius(3f);//设置焦点圆心的大小
        lineDataSet1.enableDashedHighlightLine(10f, 5f, 0f);//点击后的高亮线的显示样式
        lineDataSet1.setHighlightLineWidth(2f);//设置点击交点后显示高亮线宽
        lineDataSet1.setHighlightEnabled(true);//是否禁用点击高亮线
        lineDataSet1.setHighLightColor(Color.RED);//设置点击交点后显示交高亮线的颜色
        lineDataSet1.setValueTextSize(9f);//设置显示值的文字大小
        lineDataSet1.setDrawFilled(false);//设置禁用范围背景填充

        //格式化显示数据
        final DecimalFormat mFormat = new DecimalFormat("###,###,##0");
        lineDataSet1.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return mFormat.format(value);
            }
        });
        if (Utils.getSDKInt() >= 18) {
            // fill drawable only supported on api level 18 and above
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.rectangle_gray_border_bg);
            lineDataSet1.setFillDrawable(drawable);//设置范围背景填充
        } else {
            lineDataSet1.setFillColor(Color.BLACK);
        }

        //设置数据2
        lineDataSet2 = new LineDataSet(values2, "测试数据2");
        lineDataSet2.setColor(Color.parseColor("#0084ff"));
        lineDataSet2.setCircleColor(Color.GRAY);
        lineDataSet2.setLineWidth(1f);
        lineDataSet2.setCircleRadius(3f);
        lineDataSet2.setValueTextSize(10f);

        //保存LineDataSet集合
        ArrayList<ILineDataSet> dataList = new ArrayList<>();
        dataList.add(lineDataSet1); // add the datasets
        dataList.add(lineDataSet2);
        //创建LineData对象 属于LineChart折线图的数据集合
        LineData lineData = new LineData(dataList);
        // 添加到图表中
        lineChart.setData(lineData);
        //绘制图表
        lineChart.invalidate();
//        }
    }

    private void setXAxis() {
//获取此图表的x轴
        xAxis = lineChart.getXAxis();
        xAxis.setEnabled(true);//设置轴启用或禁用 如果禁用以下的设置全部不生效
        xAxis.setDrawAxisLine(true);//是否绘制轴线
        xAxis.setDrawGridLines(true);//设置x轴上每个点对应的线
        xAxis.setDrawLabels(true);//绘制标签  指x轴上的对应数值
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//设置x轴的显示位置
        //xAxis.setTextSize(20f);//设置字体
        //xAxis.setTextColor(Color.BLACK);//设置字体颜色
        //设置竖线的显示样式为虚线
        //lineLength控制虚线段的长度
        //spaceLength控制线之间的空间
        xAxis.enableGridDashedLine(10f, 10f, 0f);
//        xAxis.setAxisMinimum(0f);//设置x轴的最小值
//        xAxis.setAxisMaximum(10f);//设置最大值
        xAxis.setAvoidFirstLastClipping(true);//图表将避免第一个和最后一个标签条目被减掉在图表或屏幕的边缘
        xAxis.setLabelRotationAngle(10f);//设置x轴标签的旋转角度
//        设置x轴显示标签数量  还有一个重载方法第二个参数为布尔值强制设置数量 如果启用会导致绘制点出现偏差
//        xAxis.setLabelCount(10);
//        xAxis.setTextColor(Color.BLUE);//设置轴标签的颜色
//        xAxis.setTextSize(24f);//设置轴标签的大小
//        xAxis.setGridLineWidth(10f);//设置竖线大小
//        xAxis.setGridColor(Color.RED);//设置竖线颜色
//        xAxis.setAxisLineColor(Color.GREEN);//设置x轴线颜色
//        xAxis.setAxisLineWidth(5f);//设置x轴线宽度

        //        x轴默认显示4个或是几个标签。。。。
        xAxis.setLabelCount(3); //可以设置标签的显示数量
        //格式化x轴标签显示字符
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return "qqqqq";
            }
        });
    }

    private void setYAxis() {
        /**
         * Y轴默认显示左右两个轴线
         */
        //获取右边的轴线
        YAxis rightAxis = lineChart.getAxisRight();
        //设置图表右边的y轴禁用
        rightAxis.setEnabled(false);
        //获取左边的轴线
        YAxis leftAxis = lineChart.getAxisLeft();
        //设置网格线为虚线效果
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        //是否绘制0所在的网格线
        leftAxis.setDrawZeroLine(false);
    }

    private void setInterActive() {
        lineChart.setTouchEnabled(true); // 设置是否可以触摸
        lineChart.setDragEnabled(true);// 是否可以拖拽
        lineChart.setScaleEnabled(false);// 是否可以缩放 x和y轴, 默认是true
        lineChart.setScaleXEnabled(true); //是否可以缩放 仅x轴
        lineChart.setScaleYEnabled(true); //是否可以缩放 仅y轴
        lineChart.setPinchZoom(true);  //设置x轴和y轴能否同时缩放。默认是否
        lineChart.setDoubleTapToZoomEnabled(true);//设置是否可以通过双击屏幕放大图表。默认是true
        lineChart.setHighlightPerDragEnabled(true);//能否拖拽高亮线(数据点与坐标的提示线)，默认是true
        lineChart.setDragDecelerationEnabled(true);//拖拽滚动时，手放开是否会持续滚动，默认是true（false是拖到哪是哪，true拖拽之后还会有缓冲）
        lineChart.setDragDecelerationFrictionCoef(0.99f);//与上面那个属性配合，持续滚动时的速度快慢，[0,1) 0代表立即停止。
    }

    private void setLegend() {
        Legend l = lineChart.getLegend();//图例
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART_INSIDE);//设置图例的位置
        l.setTextSize(15f);//设置文字大小
        l.setForm(Legend.LegendForm.SQUARE);//正方形，圆形或线
        l.setFormSize(10f); // 设置Form的大小
        l.setWordWrapEnabled(true);//是否支持自动换行 目前只支持BelowChartLeft, BelowChartRight, BelowChartCenter
        l.setFormLineWidth(10f);//设置Form的宽度

        //禁用图例: 图例样式修改为NONE,DataSet的label文字颜色与背景相同
        l.setForm(Legend.LegendForm.NONE);
        l.setTextColor(Color.parseColor("#ffffff"));
    }

    private void setAnimotion() {
//        animateX(int durationMillis) : 水平轴动画 在指定时间内 从左到右
//        animateY(int durationMillis) : 垂直轴动画 从下到上
//        animateXY(int xDuration, int yDuration) : 两个轴动画，从左到右，从下到上
        lineChart.animateXY(1000, 1000);
    }

}
