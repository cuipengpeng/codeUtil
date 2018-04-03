package com.jf.jlfund.view.activity;

import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.jf.jlfund.R;
import com.jf.jlfund.base.BaseUIActivity;
import com.jf.jlfund.bean.BaobaoAssertInfoBean;
import com.jf.jlfund.bean.SevenDayYieldTrendChartBean;
import com.jf.jlfund.http.HttpRequest;
import com.jf.jlfund.utils.DensityUtil;
import com.jf.jlfund.utils.SPUtil;
import com.jf.jlfund.utils.StringUtil;
import com.jf.jlfund.weight.MyMarkerView;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

import static com.jf.jlfund.base.BaseApplication.getContext;

/*
* 描    述：<br>
* 作    者：崔朋朋<br>
*/
public class CurrentPlusActivity extends BaseUIActivity {

    @BindView(R.id.tv_currentPlusActivity_getOut)
    TextView getOutTextView;
    @BindView(R.id.tv_currentPlusActivity_putIn)
    TextView putInTextView;
    @BindView(R.id.ll_currentPlusActivity_putInAndGetOut)
    LinearLayout putInAndGetOutLinearLayout;
    @BindView(R.id.ll_currentPlusActivity_productDetail)
    LinearLayout productDetailLinearLayout;
    @BindView(R.id.tv_currentPlusActivity_notification)
    TextView notificationeTextView;
    @BindView(R.id.tv_currentPlusActivity_totalAmountKey)
    TextView totalAmountKeyTextView;
    @BindView(R.id.iv_currentPlusActivity_productDetail)
    ImageView productDetailImageView;
    @BindView(R.id.tv_currentPlusActivity_totalAmountTips)
    TextView totalAmountTipsTextView;
    @BindView(R.id.tv_currentPlusActivity_totalAmountValue)
    TextView totalAmountValueTextView;
    @BindView(R.id.tv_currentPlusActivity_yesterdayIncome)
    TextView yesterdayIncomeTextView;
    @BindView(R.id.tv_currentPlusActivity_accumulatedIncome)
    TextView accumulatedIncomeTextView;
    @BindView(R.id.tv_currentPlusActivity_wanFenYield)
    TextView wanFenYieldTextView;
    @BindView(R.id.ll_currentPlusActivity_yieldTrend)
    LinearLayout yieldTrendLinearLayout;
    @BindView(R.id.tv_currentPlusActivity_beginPutIn)
    TextView beginPutInTextView;
    @BindView(R.id.ll_currentPlusActivity_noLogin)
    LinearLayout noLoginLinearLayout;
    @BindView(R.id.currentPlusActivity_lineChart)
    LineChart lineChart001;

    @BindView(R.id.tv_y_1)
    TextView tvY1;
    @BindView(R.id.v_dividerLine1)
    View vDividerLine1;
    @BindView(R.id.tv_y_2)
    TextView tvY2;
    @BindView(R.id.v_dividerLine2)
    View vDividerLine2;
    @BindView(R.id.tv_y_3)
    TextView tvY3;
    @BindView(R.id.v_dividerLine3)
    View vDividerLine3;
    @BindView(R.id.tv_y_4)
    TextView tvY4;
    @BindView(R.id.v_dividerLine4)
    View vDividerLine4;
    @BindView(R.id.tv_y_5)
    TextView tvY5;
    @BindView(R.id.v_dividerLine5)
    View vDividerLine5;
    @BindView(R.id.tv_x_1)
    TextView tvX1;
    @BindView(R.id.tv_x_2)
    TextView tvX2;
    @BindView(R.id.tv_x_3)
    TextView tvX3;
    @BindView(R.id.tv_x_4)
    TextView tvX4;
    @BindView(R.id.tv_x_5)
    TextView tvX5;
    @BindView(R.id.tv_x_6)
    TextView tvX6;
    @BindView(R.id.tv_x_7)
    TextView tvX7;
    @BindView(R.id.ll_currentPlusActivity_chart_xaxis)
    LinearLayout chartXaxisLinearLayout;
    @BindView(R.id.holder_lineChart)
    LineChart lineChart;

    private String fundCode;
    private boolean userLogin = false;
    private BaobaoAssertInfoBean baobaoAssertInfoBean = new BaobaoAssertInfoBean();
    private final int MAX_XAXIS_COUNT = 7;

    ArrayList<Entry> thisFundValues = new ArrayList<>();   //本基金
    ArrayList<Entry> hs300Values = new ArrayList<>();  //沪深300
    private ArrayList<Double> valueOfYList = new ArrayList<>();

    @OnClick({R.id.tv_currentPlusActivity_beginPutIn, R.id.tv_currentPlusActivity_getOut, R.id.tv_currentPlusActivity_putIn,
            R.id.ll_currentPlusActivity_sevenDayYield, R.id.iv_currentPlusActivity_productDetail, R.id.ll_currentPlusActivity_productDetail})
    public void onItemClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.iv_currentPlusActivity_productDetail:
            case R.id.ll_currentPlusActivity_productDetail:
                MobclickAgent.onEvent(this, "click_btn_currentPlusActivity_questionOrProductDetail");  //统计点击事件
                Intent starter = new Intent(this, WebViewActivity.class);
                starter.putExtra(WebViewActivity.KEY_OF_HTML_URL, HttpRequest.JL_FUND_WEB_URL + "v1/huoqi_page.do");
                startActivity(starter);
                break;
            case R.id.tv_currentPlusActivity_getOut:
                MobclickAgent.onEvent(this, "click_btn_currentPlusActivity_getOut");  //统计点击事件
                intent.setClass(this, GetOutActivity.class);
                intent.putExtra(GetOutActivity.KEY_OF_BAOBAO_ASSERT_INFO_BEAN, baobaoAssertInfoBean);
                startActivity(intent);
                break;
            case R.id.tv_currentPlusActivity_beginPutIn:
//                SPUtil.getInstance().getUserInfo().setBindBankCard(true);
                MobclickAgent.onEvent(this, "click_btn_currentPlusActivity_begin_putIn");  //统计点击事件
                if (!SPUtil.getInstance().getUserInfo().getBindBankCard()) {
                    Toast.makeText(this, "用户未开户请先开户", Toast.LENGTH_SHORT).show();
                    return;
                }
                String riskLevel = SPUtil.getInstance().getUserInfo().getRiskLevel();
//                riskLevel="";
                if (StringUtil.isNull(riskLevel)) {
                    intent.setClass(this, RiskPreferenceActivity.class);
                    intent.putExtra(RiskPreferenceActivity.PARAM_RISK_LEVEL, riskLevel);
                    startActivity(intent);
                    return;
                }

                intent.setClass(this, PutInActivity.class);
                intent.putExtra(SingleFundDetailActivity.KEY_OF_FUND_CODE, baobaoAssertInfoBean.getFundCode());
                startActivity(intent);
                break;
            case R.id.tv_currentPlusActivity_putIn:
                MobclickAgent.onEvent(this, "click_btn_currentPlusActivity_putIn");  //统计点击事件
                intent.setClass(this, PutInActivity.class);
                intent.putExtra(SingleFundDetailActivity.KEY_OF_FUND_CODE, baobaoAssertInfoBean.getFundCode());
                startActivity(intent);
                break;
            case R.id.ll_currentPlusActivity_sevenDayYield:
                MobclickAgent.onEvent(this, "click_btn_currentPlusActivity_incomeOfTenThousand");  //统计点击事件
                intent.setClass(this, SevenDayYieldListActivity.class);
                startActivity(intent);
                break;
        }
    }


    @Override
    protected String getPageTitle() {
        return "活期+";
    }

    @Override
    protected int getSubLayoutId() {
        return R.layout.activity_current_plus;
    }

    @Override
    protected void initPageData() {
        fundCode = getIntent().getStringExtra(SingleFundDetailActivity.KEY_OF_FUND_CODE);

        baseBackImageView.setBackgroundResource(R.mipmap.left_arrow_white);

        baseTitleTextView.setTextColor(Color.WHITE);
        baseTitleBarRelativeLayout.setBackgroundResource(R.color.currentPlusActivityGold);

        notificationeTextView.setVisibility(View.GONE);
        totalAmountTipsTextView.setVisibility(View.GONE);
        initLineChart();
        disableGetOutButton();
    }


    @Override
    protected void onResume() {
        super.onResume();
        getFundCompanyInfo();
    }

    private void disableGetOutButton() {
        getOutTextView.setEnabled(false);
        getOutTextView.setBackgroundResource(R.color.currentPlusActivity_goldButton_Unenable);
    }

    protected void setRightMenuImageView() {
        baseRightMenuImageView.setVisibility(View.VISIBLE);
        baseRightMenuImageView.setBackgroundResource(R.mipmap.current_plus_activity_trade_record_list);
        baseRightMenuImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CurrentPlusActivity.this, TradeRecordListActivity.class);
                CurrentPlusActivity.this.startActivity(intent);
            }
        });
    }

    private void enableGetOutButton() {
        getOutTextView.setEnabled(true);
        getOutTextView.setBackgroundResource(R.color.currentPlusActivityGold);
    }

    private void getFundCompanyInfo() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", SPUtil.getInstance().getToken());

        HttpRequest.post(HttpRequest.JL_FUND_TEST_ENV_WEB_URL + HttpRequest.BAOBAO_ASSERT_INFO, params, this, new HttpRequest.HttpResponseCallBank() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                baobaoAssertInfoBean = JSON.parseObject(response.body(), BaobaoAssertInfoBean.class);

                if (baobaoAssertInfoBean.getIsNewMsg()) {
                    getSevenDayYield();
                    setRightMenuImageView();
                    productDetailLinearLayout.setVisibility(View.VISIBLE);
                    putInAndGetOutLinearLayout.setVisibility(View.VISIBLE);
                    yieldTrendLinearLayout.setVisibility(View.VISIBLE);
                    productDetailImageView.setVisibility(View.GONE);
                    noLoginLinearLayout.setVisibility(View.GONE);

                    if (StringUtil.doubleValue(baobaoAssertInfoBean.getMaxRedemptionStare()) > 0) {
                        enableGetOutButton();
                    }

                    if (!baobaoAssertInfoBean.getIsWorkDay() && StringUtil.notEmpty(baobaoAssertInfoBean.getMsg())) {
                        totalAmountTipsTextView.setVisibility(View.VISIBLE);
                        totalAmountTipsTextView.setText(baobaoAssertInfoBean.getMsg());
                    } else {
                        totalAmountTipsTextView.setVisibility(View.GONE);
                    }

                    if (StringUtil.notEmpty(baobaoAssertInfoBean.getContent())) {
                        notificationeTextView.setVisibility(View.VISIBLE);
                        notificationeTextView.setFocusable(true);
                        notificationeTextView.requestFocus();
                        notificationeTextView.setText(baobaoAssertInfoBean.getContent());
                    } else {
                        notificationeTextView.setVisibility(View.GONE);
                    }

                    totalAmountValueTextView.setText(StringUtil.moneyDecimalFormat2(baobaoAssertInfoBean.getFundvolbalance()));
                    if (StringUtil.doubleValue(baobaoAssertInfoBean.getTotalyestincome()) > 0) {
                        yesterdayIncomeTextView.setText("+" + StringUtil.moneyDecimalFormat2(baobaoAssertInfoBean.getTotalyestincome()));
                    } else {
                        yesterdayIncomeTextView.setText(StringUtil.moneyDecimalFormat2(baobaoAssertInfoBean.getTotalyestincome()));
                    }
                    accumulatedIncomeTextView.setText("+" + StringUtil.moneyDecimalFormat2(baobaoAssertInfoBean.getTotalcountincome()));
                } else {
                    noLoginLinearLayout.setVisibility(View.VISIBLE);
                    productDetailImageView.setVisibility(View.VISIBLE);
                    putInAndGetOutLinearLayout.setVisibility(View.GONE);
                    productDetailLinearLayout.setVisibility(View.GONE);
                    yieldTrendLinearLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }


    private void initLineChart() {
        lineChart.setLogEnabled(true);
//        lineChart.setTouchEnabled(false);
        lineChart.setPinchZoom(false);
//        lineChart.setDragEnabled(false);
        lineChart.setScaleEnabled(false);
        lineChart.setHighlightPerTapEnabled(true);
        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.animateX(500);

        lineChart.getXAxis().setEnabled(false);
        lineChart.getAxisLeft().setEnabled(false);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getLegend().setEnabled(false);

        lineChart.setOnClickListener(null);
        lineChart.setOnTouchListener(null);
//        dynamicSetMagin();
        lineChart.setViewPortOffsets(0, 0, DensityUtil.dip2px(4), 0);
//        lineChart.setEntryPointFTransferListener(new EntryPointTransferListener() {
//            @Override
//            public void onTransferFinish(Map<Entry, PointF> entryPointFMap) {
//                Log.e(TAG, "onTransferFinish: map.size: " + entryPointFMap.size());
//                for (Entry entry : entryPointFMap.keySet()) {
////                    Log.d(TAG, "onTransferFinish >>> key: " + entry.toString() + "  --  value: " + entryPointFMap.get(entry).toString());
//                }
//            }
//        });


        MyMarkerView mv = new MyMarkerView(getContext(), R.layout.custom_marker_view);
        mv.setChartView(lineChart);
        lineChart.setMarker(mv);
    }


    /**
     * 获取七日年化收益率折线图数据
     */
    private void getSevenDayYield() {
        Map<String, String> params = new HashMap<String, String>();

        HttpRequest.post(HttpRequest.JL_FUND_TEST_ENV_WEB_URL + HttpRequest.SEVEN_DAY_YIELD_LIST, params, this, new HttpRequest.HttpResponseCallBank() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                SevenDayYieldTrendChartBean sevenDayYieldTrendChartBean = JSON.parseObject(response.body(), SevenDayYieldTrendChartBean.class);
                wanFenYieldTextView.setText("万份收益 " + sevenDayYieldTrendChartBean.getGrowth());


                valueOfYList.clear();
                thisFundValues.clear();
                hs300Values.clear();

                for (int i = 0; i < sevenDayYieldTrendChartBean.getGrowthratelist().size(); i++) {
                    double fundValueOfYAxis = Float.parseFloat(sevenDayYieldTrendChartBean.getGrowthratelist().get(i).getYear_yld());
//                    double hs300ValueOfYAxis = Float.parseFloat(sevenDayYieldTrendChartBean.getGrowthratelist().get(i).getGrowthrate());
                    valueOfYList.add(fundValueOfYAxis);
//                    valueOfYList.add(hs300ValueOfYAxis);

                    thisFundValues.add(new Entry(i, (float) fundValueOfYAxis));
//                    hs300Values.add(new Entry(i, (float) hs300ValueOfYAxis));
                }

                //给y轴赋值
                double maxValue = Collections.max(valueOfYList);
                double minValue = Collections.min(valueOfYList);
                String[] yLabels = StringUtil.getCurrentPlusYAxisLabel(minValue, maxValue);
                tvY1.setText(yLabels[0]);
                tvY2.setText(yLabels[1]);
                tvY3.setText(yLabels[2]);
                tvY4.setText(yLabels[3]);
                tvY5.setText(yLabels[4]);


                for (int i = 0; (i < sevenDayYieldTrendChartBean.getGrowthratelist().size() && i < MAX_XAXIS_COUNT); i++) {
                    ((TextView) chartXaxisLinearLayout.getChildAt(i)).setText(sevenDayYieldTrendChartBean.getGrowthratelist().get(i).getTradedate());
                }

                //隐藏少于7个的view
                for (int i = (MAX_XAXIS_COUNT - 1); i >= sevenDayYieldTrendChartBean.getGrowthratelist().size(); i--) {
                    chartXaxisLinearLayout.getChildAt(i).setVisibility(View.GONE);
                }


                LineDataSet set1;
                LineDataSet set2;

                if (lineChart.getData() != null &&
                        lineChart.getData().getDataSetCount() > 0) {
                    set1 = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
                    set1.getEntryForIndex(thisFundValues.size() - 1).setIcon(getResources().getDrawable(R.mipmap.current_plus_activity_markview_point));
                    set2 = (LineDataSet) lineChart.getData().getDataSetByIndex(1);
                    set1.setValues(thisFundValues);
                    set2.setValues(hs300Values);
                    lineChart.getData().notifyDataChanged();
                    lineChart.notifyDataSetChanged();
                    lineChart.invalidate();
                } else {
                    set1 = new LineDataSet(thisFundValues, "");
                    set1.setDrawValues(false);
                    set1.enableDashedHighlightLine(10f, 5f, 0f);
                    set1.setDrawHighlightIndicators(false);
                    set1.setColor(Color.parseColor("#ff801a"));
                    set1.setLineWidth(2f);
                    set1.setHighlightEnabled(true);
//            set1.setHighLightColor(Color.GREEN);
//            set1.setHighlightLineWidth(3);
                    set1.setDrawFilled(false);
                    set1.setFillAlpha(255);
                    set1.setFillColor(Color.WHITE);
                    set1.setDrawCircles(false);
                    set1.setCircleColor(Color.parseColor("#ff801a"));
                    set1.setCircleRadius(4f);
                    set1.setDrawCircleHole(false);
                    set1.setCircleHoleRadius(2f);
                    set1.setDrawIcons(true);
                    set1.getEntryForIndex(thisFundValues.size() - 1).setIcon(getResources().getDrawable(R.mipmap.current_plus_activity_markview_point));

                    set2 = new LineDataSet(hs300Values, "");
                    set2.setDrawIcons(false);
                    set2.setDrawValues(false);
                    set2.setDrawCircles(false);
                    set2.enableDashedHighlightLine(10f, 5f, 0f);
                    set2.setColor(Color.parseColor("#0084ff"));
                    set2.setLineWidth(1.5f);

                    ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
                    dataSets.add(set1);
                    dataSets.add(set2);

                    LineData data = new LineData(dataSets);

                    lineChart.setData(data);
                    Entry entry = set1.getEntryForIndex(thisFundValues.size() - 1);
                    lineChart.highlightValue(new Highlight(entry.getX(), entry.getY(), 0), false);
                    lineChart.invalidate();
                }


            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
//                netValueListRecyclerView.refreshComplete();
            }
        });
    }

}
