package com.test.bank.weight.holder;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.test.bank.R;
import com.test.bank.bean.FundTrendBean;
import com.test.bank.utils.DensityUtil;
import com.test.bank.utils.LogUtils;
import com.test.bank.utils.StringUtil;
import com.test.bank.utils.UIUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by 55 on 2017/12/21.
 */

public class LineChartViewHolder extends BaseHolder<FundTrendBean> {
    private static final String TAG = "LineChartViewHolder";

    @BindView(R.id.ll_lineChartRoot)
    LinearLayout llRoot;

    @BindView(R.id.holder_lineChart)
    LineChart lineChart;

    @BindView(R.id.tv_lineChartHolder_thisFundRate)
    TextView tvThisFundRate;
    @BindView(R.id.tv_lineChartHolder_hs300_rate)
    TextView tvHs300Rate;

    @BindView(R.id.tv_y_1)
    TextView tvY1;
    @BindView(R.id.tv_y_2)
    TextView tvY2;
    @BindView(R.id.tv_y_3)
    TextView tvY3;
    @BindView(R.id.tv_y_4)
    TextView tvY4;
    @BindView(R.id.tv_y_5)
    TextView tvY5;

    @BindView(R.id.ll_holderLineChart_xAxis)
    LinearLayout llXAxis;
    @BindView(R.id.tv_x_1)
    TextView tvX1;
    @BindView(R.id.tv_x_2)
    TextView tvX2;
    @BindView(R.id.tv_x_3)
    TextView tvX3;

    @BindView(R.id.tv_lineChartHolder_last_1_month)
    TextView tvLast1Month;
    @BindView(R.id.tv_lineChartHolder_last_3_month)
    TextView tvLast3Month;
    @BindView(R.id.tv_lineChartHolder_last_6_month)
    TextView tvLast6Monty;
    @BindView(R.id.tv_lineChartHolder_last_12_month)
    TextView tvLastYear;

    @BindView(R.id.iv_buyInBg)
    ImageView ivBuyIn;
    @BindView(R.id.iv_saleOutBg)
    ImageView ivSaleOut;

    @Override
    protected void initView(View rootView) {
        ButterKnife.bind(this, rootView);
        initLineChart();
        llRoot.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    LogUtils.e("rawX: " + event.getRawX() + " rawY: " + event.getRawY() + "  x: " + event.getX() + " y: " + event.getY());
                }
                return false;
            }
        });
        lineChart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    LogUtils.e("rawX: " + event.getRawX() + " rawY: " + event.getRawY() + "  x: " + event.getX() + " y: " + event.getY());
                }
                return false;
            }
        });

    }


    @Override
    protected int getLayoutId() {
        return R.layout.layout_line_chart_holder2;
    }

    private void initLineChart() {
        lineChart.setLogEnabled(true);
        lineChart.setTouchEnabled(false);
        lineChart.setPinchZoom(false);
        lineChart.setDragEnabled(false);
        lineChart.setScaleEnabled(false);
        lineChart.setHighlightPerTapEnabled(true);
        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.animateX(500);

        lineChart.getXAxis().setEnabled(false);
        lineChart.getAxisLeft().setEnabled(false);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getLegend().setEnabled(false);
        float xWidth = tvY1.getPaint().measureText("+99.00%");      //左对齐的边界：默认为十位数
        LogUtils.e("xWidth: " + xWidth);
        lineChart.setViewPortOffsets(xWidth, 0, 10, 0);
        dynamicSetMagin(xWidth);

        lineChart.setBuyPointBgLeft(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.bg_fund_chart_buy_point_left));
        lineChart.setBuyPointBgRight(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.bg_fund_chart_buy_point_right));
        lineChart.setBuyPointCircle(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon_fund_chart_buy_point_circle));

        lineChart.setSalePointBgLeft(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.bg_fund_chart_sale_point_left));
        lineChart.setSalePointBgRight(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.bg_fund_chart_sale_point_right));
        lineChart.setSaleCirclePoint(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon_fund_chart_sale_point_circle));

        lineChart.setOnGetBuyInAndSaleOutPointListener(new LineChart.OnGetBuyInAndSaleOutPointListener() {
            @Override
            public void OnGetPoint(LineChart.BuyInAndSaleOutInfo buyInPointInfo, LineChart.BuyInAndSaleOutInfo saleOutPointInfo) {
                if (buyInPointInfo != null) {
                    Log.e(TAG, "OnGetPoint: buyInPointF: " + buyInPointInfo.toString());
                    dynamicAdjustImagePos(ivBuyIn, buyInPointInfo);
                    ivBuyIn.setImageResource(buyInPointInfo.isLeft ? R.drawable.bg_fund_chart_buy_point_left : R.drawable.bg_fund_chart_buy_point_right);
                }
                if (saleOutPointInfo != null) {
                    Log.e(TAG, "OnGetPoint:  saleOutPointF: " + saleOutPointInfo.toString());
                    dynamicAdjustImagePos(ivSaleOut, saleOutPointInfo);
                    ivSaleOut.setImageResource(saleOutPointInfo.isLeft ? R.drawable.bg_fund_chart_sale_point_left : R.drawable.bg_fund_chart_sale_point_right);
                }
            }
        });
    }

    private void dynamicAdjustImagePos(ImageView imageView, LineChart.BuyInAndSaleOutInfo pointInfo) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) imageView.getLayoutParams();
        params.leftMargin = (int) (pointInfo.x + DensityUtil.dip2px(10));
        params.topMargin = (int) pointInfo.y + DensityUtil.dip2px(10);
        imageView.setLayoutParams(params);
    }

    private void dynamicSetMagin(float xWidth) {
//        int originalHeight = lineChart.getLayoutParams().height;
//        Log.e(TAG, "dynamicSetMagin: originalHeight: " + originalHeight);
//        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) lineChart.getLayoutParams();
//        layoutParams.leftMargin -= 39;  //border: 39.735
//        layoutParams.topMargin -= 39;
//        layoutParams.rightMargin -= 39;
//        layoutParams.bottomMargin -= 39;
//        lineChart.setLayoutParams(layoutParams);

        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) llXAxis.getLayoutParams();
        layoutParams.leftMargin = (int) xWidth;
        llXAxis.setLayoutParams(layoutParams);
    }


    ArrayList<Entry> thisFundValues = new ArrayList<>();   //本基金
    ArrayList<Entry> hs300Values = new ArrayList<>();  //沪深300
    ArrayList<Double> valueOfYList = new ArrayList<>();

    private boolean isTagFirstBuyPoint = false; //是否已经标记了第一个买点
    private int lastSalePointIndex = -1;

    private void refreshData(List<FundTrendBean.TrendInfo> trendInfos) {
        if (trendInfos == null) {
            LogUtils.e("zzzzzz", "trendInfos is null....");
            return;
        }

        UIUtils.setRate(tvThisFundRate, trendInfos.get(trendInfos.size() - 1).getFundChng());
        UIUtils.setRate(tvHs300Rate, trendInfos.get(trendInfos.size() - 1).getHs300Chng());

        //给x轴赋值
        if (trendInfos.size() > 3) {
            UIUtils.setText(tvX1, trendInfos.get(0).getTradedate());
            UIUtils.setText(tvX3, trendInfos.get(trendInfos.size() - 1).getTradedate());
            int centerIndex = trendInfos.size() % 2 == 0 ? (trendInfos.size() / 2 - 1) : trendInfos.size() / 2;
            UIUtils.setText(tvX2, trendInfos.get(centerIndex).getTradedate());
        }

        thisFundValues.clear();
        hs300Values.clear();
        valueOfYList.clear();

        for (int i = 0; i < trendInfos.size(); i++) {
            double fundValueOfYAxis = Float.parseFloat(trendInfos.get(i).getFundChng());
            double hs300ValueOfYAxis = Float.parseFloat(trendInfos.get(i).getHs300Chng());
            valueOfYList.add(fundValueOfYAxis);
            valueOfYList.add(hs300ValueOfYAxis);

            Entry thisFundEntry;

            if (trendInfos.get(i).getBuytradeinfo() != null && !TextUtils.isEmpty(trendInfos.get(i).getBuytradeinfo().getBuyIndex())) {//|| i % 4 == 0) {  //买点     如果同时为买卖点则优先显示买点
                thisFundEntry = new Entry(i, (float) fundValueOfYAxis, Entry.BUY_POINT);
                if (!isTagFirstBuyPoint) {      //标记第一个买点，画买点提示框，其余的显示一个黄点
                    thisFundEntry.setDrawBuyPointBg(true);
                    isTagFirstBuyPoint = true;
                } else {
                    thisFundEntry.setDrawBuyPointBg(false);
                }
            } else if (trendInfos.get(i).getSelltradeinfo() != null && !TextUtils.isEmpty(trendInfos.get(i).getSelltradeinfo().getTradetype())) {//|| i % 7 == 0) {  //卖点
                thisFundEntry = new Entry(i, (float) fundValueOfYAxis, Entry.SALE_POINT);
                lastSalePointIndex = i;     //记录最新卖点的下标,最后一个卖点要画卖点提示框，其他的都画一个蓝点
            } else {
                thisFundEntry = new Entry(i, (float) fundValueOfYAxis);
            }

            thisFundValues.add(thisFundEntry);
            hs300Values.add(new Entry(i, (float) hs300ValueOfYAxis));
        }

        if (lastSalePointIndex != -1 && thisFundValues.get(lastSalePointIndex).getBuyAndSalePointTag() != -1) {
            thisFundValues.get(lastSalePointIndex).setDrawSalePointBg(true);
        }

        //给y轴赋值
        double maxValue = Collections.max(valueOfYList);
        double minValue = Collections.min(valueOfYList);
        String[] yLabels = StringUtil.getYAxisLabel(minValue, maxValue);
        tvY1.setText(yLabels[0]);
        tvY2.setText(yLabels[1]);
        tvY3.setText(yLabels[2]);
        tvY4.setText(yLabels[3]);
        tvY5.setText(yLabels[4]);

        LineDataSet set1;
        LineDataSet set2;

        if (lineChart.getData() != null &&
                lineChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
            set2 = (LineDataSet) lineChart.getData().getDataSetByIndex(1);
            set1.setValues(thisFundValues);
            set2.setValues(hs300Values);
            lineChart.getData().notifyDataChanged();
            lineChart.notifyDataSetChanged();
            lineChart.invalidate();
        } else {
            set1 = new LineDataSet(thisFundValues, "");
            set1.setDrawIcons(false);
            set1.setDrawValues(false);
            set1.setDrawCircles(false);
            set1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.setColor(Color.parseColor("#f35857"));
            set1.setLineWidth(2f);
//            set1.setHighlightEnabled(true);   //点击显示高亮十字
//            set1.setHighLightColor(Color.GREEN);
//            set1.setHighlightLineWidth(3);


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
            lineChart.invalidate();
        }
    }

    private int currMonth = 1;      //防止不必要的赋值重绘

    @OnClick({R.id.tv_lineChartHolder_last_1_month, R.id.tv_lineChartHolder_last_3_month, R.id.tv_lineChartHolder_last_6_month, R.id.tv_lineChartHolder_last_12_month})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_lineChartHolder_last_1_month:
                if (currMonth != 1) {
                    isTagFirstBuyPoint = false;
                    refreshData(data.getLast1Month());
                    currMonth = 1;
                    updateIndicatorStatus(0);
                }
                break;
            case R.id.tv_lineChartHolder_last_3_month:
                if (currMonth != 3) {
                    isTagFirstBuyPoint = false;
                    refreshData(data.getLast3Month());
                    currMonth = 3;
                    updateIndicatorStatus(1);
                }
                break;
            case R.id.tv_lineChartHolder_last_6_month:
                if (currMonth != 6) {
                    isTagFirstBuyPoint = false;
                    refreshData(data.getLast6Month());
                    currMonth = 6;
                    updateIndicatorStatus(2);
                }
                break;
            case R.id.tv_lineChartHolder_last_12_month:
                if (currMonth != 12) {
                    isTagFirstBuyPoint = false;
                    refreshData(data.getLast12Month());
                    currMonth = 12;
                    updateIndicatorStatus(3);
                }
                break;
        }
    }

    private void updateIndicatorStatus(int index) {
        tvLast1Month.setSelected(index == 0);
        tvLast1Month.setTextColor(index == 0 ? ContextCompat.getColor(mContext, R.color.color_393b51) : ContextCompat.getColor(mContext, R.color.color_7e819b));
        tvLast3Month.setSelected(index == 1);
        tvLast3Month.setTextColor(index == 1 ? ContextCompat.getColor(mContext, R.color.color_393b51) : ContextCompat.getColor(mContext, R.color.color_7e819b));
        tvLast6Monty.setSelected(index == 2);
        tvLast6Monty.setTextColor(index == 2 ? ContextCompat.getColor(mContext, R.color.color_393b51) : ContextCompat.getColor(mContext, R.color.color_7e819b));
        tvLastYear.setSelected(index == 3);
        tvLastYear.setTextColor(index == 3 ? ContextCompat.getColor(mContext, R.color.color_393b51) : ContextCompat.getColor(mContext, R.color.color_7e819b));
    }

    @Override
    protected void updateView() {
        updateIndicatorStatus(0);
        refreshData(data.getLast1Month());
    }
}
