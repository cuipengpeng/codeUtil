package com.test.bank.view.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.test.bank.R;
import com.test.bank.bean.FundTrendBean;
import com.test.bank.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 收益率走势图Fragment
 */
public class YieldCurveFragment extends Fragment {
    private static final String TAG = "YieldCurveFragment";
    private static final String FUND_CODE = "FUND_CODE";
    private static final String LAST_MONTH_INDEX = "LAST_MONTH_INDEX";
    protected View rootView;

    private int lastMonthIndex;
    private String fundCode;

    LineChart lineChart;

    public YieldCurveFragment() {
    }

    public static YieldCurveFragment newInstance(String fundCode, int lastMonthIndex) {
        YieldCurveFragment fragment = new YieldCurveFragment();
        Bundle args = new Bundle();
        args.putString(FUND_CODE, fundCode);
        args.putInt(LAST_MONTH_INDEX, lastMonthIndex);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.i("onCreate.......");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtils.i("onCreateView.......");
        rootView = inflater.inflate(R.layout.fragment_yield_curve, container, false);
        lineChart = rootView.findViewById(R.id.lineChart_fragment_yield_curve);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        init();
    }

    protected void init() {
        if (getArguments() != null) {
            fundCode = getArguments().getString(FUND_CODE);
            lastMonthIndex = getArguments().getInt(LAST_MONTH_INDEX);
        }
        Log.i(TAG, "init: fundCode: " + fundCode + " lastMonthINdex: " + lastMonthIndex);
        initLineChart();
    }

    private void initLineChart() {
        lineChart.setLogEnabled(true);
        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.setPinchZoom(true);
        lineChart.getDescription().setEnabled(false);
        lineChart.animateX(1500);
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAvoidFirstLastClipping(false);
//        xAxis.setGridColor(Color.TRANSPARENT);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisLineColor(Color.parseColor("#f9f9f9"));
        xAxis.setAxisLineWidth(1);  //dp
//        xAxis.setSpaceMin();
        lineChart.getAxisLeft().setDrawZeroLine(false);
//        lineChart.getAxisLeft().setStartAtZero(false);
        lineChart.getAxisLeft().setAxisLineColor(Color.TRANSPARENT);
        lineChart.getAxisLeft().enableGridDashedLine(10f, 10f, 0f);

        IAxisValueFormatter formatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return xLables.get((int) (value % xLables.size()));
            }
        };
        lineChart.getXAxis().setValueFormatter(formatter);

//        lineChart.getAxisLeft().setGridColor(Color.parseColor("#ebebeb"));
        lineChart.getAxisRight().setEnabled(false);
//        xAxis.setGranularity(10f); // minimum axis-step (interval) is 1
//        lineChart.getLegend().setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        lineChart.getLegend().setEnabled(false);

    }

    private List<FundTrendBean.TrendInfo> trendInfoList;

    public void setData(List<FundTrendBean.TrendInfo> trendInfos) {
        this.trendInfoList = trendInfos;
        inflateData(trendInfos);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtils.e("onResume.......");
    }


    final ArrayList<String> xLables = new ArrayList<>();
    ArrayList<Entry> thisFundValues = new ArrayList<>();   //本基金
    ArrayList<Entry> hs300Values = new ArrayList<>();  //沪深300

    private void inflateData(List<FundTrendBean.TrendInfo> trendInfos) {
        if (trendInfos == null) {
            LogUtils.e("zzzzzz", "trendInfos is null....");
            return;
        }
        for (int i = 0; i < trendInfos.size(); i++) {
            thisFundValues.add(new Entry(i, Float.parseFloat(trendInfos.get(i).getFundChng())));
            hs300Values.add(new Entry(i, Float.parseFloat(trendInfos.get(i).getHs300Chng())));
            xLables.add(i, trendInfos.get(i).getTradedate());
        }

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
        } else {
            set1 = new LineDataSet(thisFundValues, "");
            set1.setDrawIcons(false);
            set1.setDrawValues(false);
            set1.setDrawCircles(false);
            set1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.setColor(Color.parseColor("#f35857"));
            set1.setLineWidth(1f);


            set2 = new LineDataSet(hs300Values, "");
            set2.setDrawIcons(false);
            set2.setDrawValues(false);
            set2.setDrawCircles(false);
            set2.enableDashedHighlightLine(10f, 5f, 0f);
            set2.setColor(Color.parseColor("#0084ff"));
            set2.setLineWidth(1f);
//            set2.setFormLineWidth(1f);

            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set1);
            dataSets.add(set2);

            LineData data = new LineData(dataSets);

            lineChart.setData(data);
        }
    }

    private List<FundTrendBean.TrendInfo> getData(FundTrendBean fundTrendBean) {
        if (lastMonthIndex == 1)
            return fundTrendBean.getLast1Month();
        if (lastMonthIndex == 3)
            return fundTrendBean.getLast3Month();
        if (lastMonthIndex == 6)
            return fundTrendBean.getLast6Month();
        return fundTrendBean.getLast12Month();
    }
}
