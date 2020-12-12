package com.hospital.checkup.view.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.hospital.checkup.R;
import com.hospital.checkup.base.BaseUILocalDataActivity;
import com.hospital.checkup.widget.CustomMarkerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class CalibrationStartHorizontalActivity extends BaseUILocalDataActivity {
    
    @BindView(R.id.iv_calibrationStartHorizontalActivity_circleIcon01)
    ImageView icon01ImageView;
    @BindView(R.id.iv_calibrationStartHorizontalActivity_circleIcon02)
    ImageView icon02ImageView;
    @BindView(R.id.iv_calibrationStartHorizontalActivity_circleIcon03)
    ImageView icon03ImageView;
    @BindView(R.id.lc_calibrationStartHorizontalActivity_lineChart)
    LineChart lineChart;

    private LineDataSet set1, set2, set3;
    private List<Entry> entryList01 = new ArrayList<>();
    private List<Entry> entryList02 = new ArrayList<>();
    private List<Entry> entryList03 = new ArrayList<>();
    public static final String KEY_OF_ENTRY_LIST_01="entryList01Key";
    public static final String KEY_OF_ENTRY_LIST_02="entryList02Key";
    public static final String KEY_OF_ENTRY_LIST_03="entryList03Key";
    private boolean showLegend01 = true;
    private boolean showLegend02 = true;
    private boolean showLegend03 = true;

    private int lineSetcount = 3;

    @OnClick({R.id.rl_calibrationStartHorizontalActivity_legend01, R.id.rl_calibrationStartHorizontalActivity_legend02, 
            R.id.rl_calibrationStartHorizontalActivity_legend03})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_calibrationStartHorizontalActivity_legend01:
                if(lineSetcount<=1 && showLegend01){
                    return;
                }
                showLegend01 = !showLegend01;
                if(showLegend01){
                    icon01ImageView.setBackgroundResource(R.drawable.circle_corner_blue_bg_normal_2dp);
                    CalibrationStartActivity.printLogEntryList(entryList01);
                    for(int i=0; i<entryList01.size();i++){
                        set1.addEntryOrdered(entryList01.get(i).copy());
                    }
                    lineSetcount++;
                }else {
                    icon01ImageView.setBackgroundResource(R.drawable.circle_corner_disable_gray_bg_2dp);
                    lineSetcount--;
                    set1.clear();
                }
                notifyDataSetChangeForChart();
                lineChart.invalidate();
                break;
            case R.id.rl_calibrationStartHorizontalActivity_legend02:
                if(lineSetcount<=1 && showLegend02){
                    return;
                }
                showLegend02 = !showLegend02;
                if(showLegend02){
                    icon02ImageView.setBackgroundResource(R.drawable.circle_corner_yellow_bg_normal_2dp);
                    CalibrationStartActivity.printLogEntryList(entryList02);
                    for(int i=0; i<entryList02.size();i++){
                        set2.addEntryOrdered(entryList02.get(i).copy());
                    }
                    lineSetcount++;
                }else {
                    icon02ImageView.setBackgroundResource(R.drawable.circle_corner_disable_gray_bg_2dp);
                    lineSetcount--;
                    set2.clear();
                }
                notifyDataSetChangeForChart();
                lineChart.invalidate();
                break;
            case R.id.rl_calibrationStartHorizontalActivity_legend03:
                if(lineSetcount<=1 && showLegend03){
                    return;
                }
                showLegend03 = !showLegend03;
                if(showLegend03){
                    icon03ImageView.setBackgroundResource(R.drawable.circle_corner_green_bg_normal_2dp);
                    CalibrationStartActivity.printLogEntryList(entryList03);
                    for(int i=0; i<entryList03.size();i++){
                        set3.addEntryOrdered(entryList03.get(i).copy());
                    }
                    lineSetcount++;
                }else {
                    icon03ImageView.setBackgroundResource(R.drawable.circle_corner_disable_gray_bg_2dp);
                    lineSetcount--;
                    set3.clear();
                }
                notifyDataSetChangeForChart();
                lineChart.invalidate();
                break;
        }
    }

    @Override
    protected String getPageTitle() {
        return "标定";
    }

    @Override
    protected int getSubLayoutId() {
        return R.layout.activity_calibration_start_horizontal;
    }

    @Override
    protected void initPageData() {
        Intent intent= getIntent();
        entryList01= intent.getParcelableArrayListExtra(KEY_OF_ENTRY_LIST_01);
        entryList02= intent.getParcelableArrayListExtra(KEY_OF_ENTRY_LIST_02);
        entryList03= intent.getParcelableArrayListExtra(KEY_OF_ENTRY_LIST_03);
        lineSetcount=3;
        initChart();
        initLineDataSet();
    }

    private void initChart() {
        // no description text
        lineChart.getDescription().setEnabled(false);
        lineChart.setNoDataText("暂无数据");
        // enable touch gestures
        lineChart.setTouchEnabled(true);

        lineChart.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setDrawGridBackground(false);
        lineChart.setHighlightPerDragEnabled(true);

//        lineChart.setMarker(new CustomMarkerView(this, R.layout.marker_view));
//        lineChart.setMarkerView(new MarkerView(this, R.layout.marker_view));
        CustomMarkerView markerView = new CustomMarkerView(this, R.layout.marker_view);
        markerView.setChartView(lineChart);
        lineChart.setMarker(markerView);

//        MyMarkerView mv = new MyMarkerView(getContext(), R.layout.custom_marker_view);
//        mv.setChartView(lineChart);
//        lineChart.setMarker(mv);

        // if disabled, scaling can be done on x- and y-axis separately
        lineChart.setPinchZoom(true);

        // set an alternative background color
//        lineChart.setBackgroundColor(Color.WHITE);

        lineChart.animateX(1500);

        // get the legend (only possible after setting data)
        Legend l = lineChart.getLegend();
        l.setEnabled(false);
//        // modify the legend ...
//        l.setForm(Legend.LegendForm.LINE);
//        l.setTextSize(11f);
//        l.setTextColor(Color.GRAY);
//        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
//        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
//        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
//        l.setDrawInside(false);
//        l.setYOffset(11f);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(11f);
        xAxis.setAxisMinimum(0f);
        xAxis.setTextColor(Color.GRAY);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setTextColor(Color.GRAY);
        leftAxis.setAxisMaximum(800f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);

        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);
//        rightAxis.setTextColor(Color.RED);
//        rightAxis.setAxisMaximum(900);
//        rightAxis.setAxisMinimum(-200);
//        rightAxis.setDrawGridLines(false);
//        rightAxis.setDrawZeroLine(false);
//        rightAxis.setGranularityEnabled(false);
    }

    private void initLineDataSet() {
        // create a dataset and give it a type
//            set1 = new LineDataSet(values1, "DataSet 1");
        set1 = new LineDataSet(null, "DataSet 1");
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setColor(getResources().getColor(R.color.chartLineBlue));
        set1.setCircleColor(getResources().getColor(R.color.chartLineBlue));
        set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set1.setLineWidth(1f);
        set1.setCircleRadius(1.5f);
        set1.setCircleHoleRadius(2f);
        set1.setFillAlpha(65);
        set1.setFillColor(ColorTemplate.getHoloBlue());
        set1.setDrawCircles(true);
        set1.setDrawCircleHole(false);
        set1.setDrawValues(false);
        set1.setHighLightColor(Color.rgb(244, 117, 117));
        //set1.setFillFormatter(new MyFillFormatter(0f));
        //set1.setDrawHorizontalHighlightIndicator(false);
        //set1.setVisible(false);
        //set1.setCircleHoleColor(Color.WHITE);

        set2 = new LineDataSet(null, "DataSet 2");
        set2.setAxisDependency(YAxis.AxisDependency.LEFT);
        set2.setColor(getResources().getColor(R.color.chartLineYellow));
        set2.setCircleColor(getResources().getColor(R.color.chartLineYellow));
        set2.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set2.setLineWidth(1f);
        set2.setCircleRadius(1.5f);
        set2.setCircleHoleRadius(2f);
        set2.setFillAlpha(65);
        set2.setFillColor(Color.RED);
        set2.setDrawCircles(true);
        set2.setDrawCircleHole(false);
        set2.setDrawValues(false);
        set2.setHighLightColor(Color.rgb(244, 117, 117));
        //set2.setFillFormatter(new MyFillFormatter(900f));

        set3 = new LineDataSet(null, "DataSet 3");
        set3.setAxisDependency(YAxis.AxisDependency.LEFT);
        set3.setColor(getResources().getColor(R.color.chartLineGreen));
        set3.setCircleColor(getResources().getColor(R.color.chartLineGreen));
        set3.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set3.setLineWidth(1f);
        set3.setCircleRadius(1.5f);
        set3.setCircleHoleRadius(2f);
        set3.setFillAlpha(0);
        set3.setFillColor(ColorTemplate.colorWithAlpha(Color.YELLOW, 200));
        set3.setDrawCircles(true);
        set3.setDrawCircleHole(false);
        set3.setDrawValues(false);
        set3.setHighLightColor(Color.rgb(244, 117, 117));

        for(int i=0; i<entryList01.size();i++){
            set1.addEntryOrdered(entryList01.get(i).copy());
        }
        for(int i=0; i<entryList02.size();i++){
            set2.addEntryOrdered(entryList02.get(i).copy());
        }
        for(int i=0; i<entryList03.size();i++){
            set3.addEntryOrdered(entryList03.get(i).copy());
        }

        // create a data object with the data sets
        LineData data = new LineData(set1, set2, set3);
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(9f);

        // set data
        lineChart.setData(data);
    }

    private void addEntry(int index, float range) {
        if (lineChart.getData() == null || lineChart.getData().getDataSetCount() <= 0) {
            initLineDataSet();
        }
        LineDataSet set1 = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
        LineDataSet set2 = (LineDataSet) lineChart.getData().getDataSetByIndex(1);
        LineDataSet set3 = (LineDataSet) lineChart.getData().getDataSetByIndex(2);

        float val1 = (float) (Math.random() * range) + 300;
        Entry entry01 = new Entry(index, val1);
        entryList01.add(entry01);
        set1.addEntryOrdered(entry01);

        float val2 = (float) (Math.random() * range) + 200;
        Entry entry02 = new Entry(index, val2);
        entryList02.add(entry02);
        set2.addEntryOrdered(entry02);

        float val3 = (float) (Math.random() * range) + 350;
        Entry entry03 = new Entry(index, val3);
        entryList03.add(entry03);
        set3.addEntryOrdered(entry03);

//            ILineDataSet set = data.getDataSetByIndex(0);
        // set.addEntry(...); // can be called as well

//            if (set == null) {
//                set = createSet();
//                data.addDataSet(set);
//            }
        notifyDataSetChangeForChart();


        // limit the number of visible entries
        lineChart.setVisibleXRangeMaximum(50);
        // lineChart.setVisibleYRange(30, AxisDependency.LEFT);

        // move to the latest entry
        lineChart.moveViewToX(lineChart.getData().getEntryCount());

        // this automatically refreshes the lineChart (calls invalidate())
        // lineChart.moveViewTo(data.getXValCount()-7, 55f,
        // AxisDependency.LEFT);
    }

    private void notifyDataSetChangeForChart() {
        //data.addEntry(new Entry(set.getEntryCount(), (float) (Math.random() * 40) + 30f), 0);
        lineChart.getData().notifyDataChanged();
        // let the lineChart know it's data has changed
        lineChart.notifyDataSetChanged();
    }

    public static void open(Context context, ArrayList<Entry> entryList01, ArrayList<Entry> entryList02, ArrayList<Entry> entryList03) {
        Intent intent = new Intent(context, CalibrationStartHorizontalActivity.class);
        intent.putParcelableArrayListExtra(KEY_OF_ENTRY_LIST_01, entryList01);
        intent.putParcelableArrayListExtra(KEY_OF_ENTRY_LIST_02, entryList02);
        intent.putParcelableArrayListExtra(KEY_OF_ENTRY_LIST_03, entryList03);
        context.startActivity(intent);
    }
}
