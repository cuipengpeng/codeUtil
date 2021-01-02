package com.hospital.checkup.view.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.hospital.checkup.BuildConfig;
import com.hospital.checkup.R;
import com.hospital.checkup.base.BaseApplication;
import com.hospital.checkup.base.BaseUILocalDataActivity;
import com.hospital.checkup.bean.UserInfoBean;
import com.hospital.checkup.bluetooth.BleController;
import com.hospital.checkup.bluetooth.ConnectCallback;
import com.hospital.checkup.bluetooth.ScanCallback;
import com.hospital.checkup.http.HttpRequest;
import com.hospital.checkup.utils.LogUtils;
import com.hospital.checkup.widget.CustomMarkerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

public class CalibrationStartActivity extends BaseUILocalDataActivity {

    @BindView(R.id.btn_calibrationStartActivity_start)
    Button startButton;
    @BindView(R.id.btn_calibrationStartActivity_stop)
    Button stopButton;
    @BindView(R.id.btn_calibrationStartActivity_save)
    Button saveButton;
    @BindView(R.id.btn_calibrationStartActivity_reset)
    Button resetButton;
    @BindView(R.id.iv_calibrationStartActivity_circleIcon01)
    ImageView icon01ImageView;
    @BindView(R.id.iv_calibrationStartActivity_circleIcon02)
    ImageView icon02ImageView;
    @BindView(R.id.iv_calibrationStartActivity_circleIcon03)
    ImageView icon03ImageView;
    @BindView(R.id.chart1)
    LineChart chart;

    private Runnable realTimePointThread;
    private RealTimePointRunnable realTimePointRunnable;
    private boolean stopCalibration = true;

    private LineDataSet set1, set2, set3;
    private ArrayList<Entry> entryList01 = new ArrayList<>();
    private ArrayList<Entry> entryList02 = new ArrayList<>();
    private ArrayList<Entry> entryList03 = new ArrayList<>();
    private boolean showLegend01 = true;
    private boolean showLegend02 = true;
    private boolean showLegend03 = true;

    private  int lineSetcount = 0;

    @OnClick({R.id.btn_calibrationStartActivity_start, R.id.btn_calibrationStartActivity_stop, R.id.btn_calibrationStartActivity_save,
            R.id.rl_calibrationStartActivity_legend01, R.id.rl_calibrationStartActivity_legend02, R.id.rl_calibrationStartActivity_legend03,
            R.id.btn_calibrationStartActivity_reset})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_calibrationStartActivity_start:
                icon01ImageView.setBackgroundResource(R.drawable.circle_corner_blue_bg_normal_2dp);
                icon02ImageView.setBackgroundResource(R.drawable.circle_corner_yellow_bg_normal_2dp);
                icon03ImageView.setBackgroundResource(R.drawable.circle_corner_green_bg_normal_2dp);
                if(stopCalibration){
                    stopCalibration = false;
                    chart.clear();
                    new Thread(realTimePointThread).start();
                }
                disableAllButton();
                stopButton.setEnabled(true);
                stopButton.setBackgroundResource(R.drawable.circle_corner_red_bg_normal_2dp);
                if (!BleController.getInstance().isEnable()) {
                    Toast.makeText(BaseApplication.applicationContext, "请打开蓝牙", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
                } else {
                    if (BleController.getInstance().isConnectok) {
                        BleController.getInstance().enableNotification(true, BleController.getInstance().mBleGattCharacteristic);
                    } else {
                        scanBleDeviceAndConnect();
                    }
                }
                break;
            case R.id.btn_calibrationStartActivity_stop:
                stopCalibration = true;
                disableAllButton();
                saveButton.setEnabled(true);
                saveButton.setBackgroundResource(R.drawable.circle_corner_blue_bg_normal_2dp);
                resetButton.setEnabled(true);
                resetButton.setBackgroundResource(R.drawable.circle_corner_red_bg_normal_2dp);
                BleController.getInstance().enableNotification(false, BleController.getInstance().mBleGattCharacteristic);
                break;
            case R.id.btn_calibrationStartActivity_save:
                enableStartButton();
                break;
            case R.id.btn_calibrationStartActivity_reset:
                enableStartButton();
//                CalibrationStartHorizontalActivity.open(this, entryList01, entryList02, entryList03);
                break;
            case R.id.rl_calibrationStartActivity_legend01:
                if(lineSetcount<=1 && showLegend01){
                    return;
                }
                showLegend01 = !showLegend01;
                if(showLegend01){
                    icon01ImageView.setBackgroundResource(R.drawable.circle_corner_blue_bg_normal_2dp);
                    printLogEntryList(entryList01);
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
                chart.invalidate();
                break;
            case R.id.rl_calibrationStartActivity_legend02:
                if(lineSetcount<=1 && showLegend02){
                    return;
                }
                showLegend02 = !showLegend02;
                if(showLegend02){
                    icon02ImageView.setBackgroundResource(R.drawable.circle_corner_yellow_bg_normal_2dp);
                    printLogEntryList(entryList02);
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
                chart.invalidate();
                break;
            case R.id.rl_calibrationStartActivity_legend03:
                if(lineSetcount<=1 && showLegend03){
                    return;
                }
                showLegend03 = !showLegend03;
                if(showLegend03){
                    icon03ImageView.setBackgroundResource(R.drawable.circle_corner_green_bg_normal_2dp);
                    printLogEntryList(entryList03);
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
                chart.invalidate();
                break;
        }
    }

    @Override
    protected String getPageTitle() {
        return "标定";
    }

    @Override
    protected int getSubLayoutId() {
        return R.layout.activity_calibration_start;
    }

    @Override
    protected void initPageData() {
        if (BleController.getInstance().isEnable() && !BleController.getInstance().isConnectok) {
            scanBleDeviceAndConnect();
        }
        initChart();
        initLineDataSet();
        enableStartButton();
    }

    private void initChart() {
        // no description text
        chart.getDescription().setEnabled(false);
        chart.setNoDataText("暂无数据");
        // enable touch gestures
        chart.setTouchEnabled(true);

        chart.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setDrawGridBackground(false);
        chart.setHighlightPerDragEnabled(true);

//        chart.setMarker(new CustomMarkerView(this, R.layout.marker_view));
//        chart.setMarkerView(new MarkerView(this, R.layout.marker_view));
        CustomMarkerView markerView = new CustomMarkerView(this, R.layout.marker_view);
        markerView.setChartView(chart);
        chart.setMarker(markerView);

//        MyMarkerView mv = new MyMarkerView(getContext(), R.layout.custom_marker_view);
//        mv.setChartView(lineChart);
//        lineChart.setMarker(mv);

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(true);

        // set an alternative background color
//        chart.setBackgroundColor(Color.WHITE);

        chart.animateX(1500);

        // get the legend (only possible after setting data)
        Legend l = chart.getLegend();
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

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(11f);
        xAxis.setAxisMinimum(0f);
        xAxis.setTextColor(Color.GRAY);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextColor(Color.GRAY);
        leftAxis.setAxisMaximum(800f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);
//        rightAxis.setTextColor(Color.RED);
//        rightAxis.setAxisMaximum(900);
//        rightAxis.setAxisMinimum(-200);
//        rightAxis.setDrawGridLines(false);
//        rightAxis.setDrawZeroLine(false);
//        rightAxis.setGranularityEnabled(false);

        realTimePointRunnable = new RealTimePointRunnable();
        realTimePointThread = new Runnable() {
            @Override
            public void run() {
                entryList01.clear();
                entryList02.clear();
                entryList03.clear();

                for(int i=0; i<500;i++){
                    if(stopCalibration){
                        lineSetcount = 3;
                        break;
                    }
                    realTimePointRunnable.index = i;
                    runOnUiThread(realTimePointRunnable);
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                LogUtils.printLog("init--0");
                printLogEntryList(((LineDataSet)chart.getData().getDataSetByIndex(0)).getEntries());
                LogUtils.printLog("init--1");
                printLogEntryList(((LineDataSet)chart.getData().getDataSetByIndex(1)).getEntries());
                LogUtils.printLog("init--2");
                printLogEntryList(((LineDataSet)chart.getData().getDataSetByIndex(2)).getEntries());
            }
        };
    }

    class RealTimePointRunnable implements Runnable{
        public int index =0;

        @Override
        public void run() {
            addEntry(index,300);
        }
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

        // create a data object with the data sets
        LineData data = new LineData(set1, set2, set3);
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(9f);

        // set data
        chart.setData(data);
    }

    private void addEntry(int index, float range) {
        if (chart.getData() == null || chart.getData().getDataSetCount() <= 0) {
            initLineDataSet();
        }
        LineDataSet set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
        LineDataSet set2 = (LineDataSet) chart.getData().getDataSetByIndex(1);
        LineDataSet set3 = (LineDataSet) chart.getData().getDataSetByIndex(2);

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
        chart.setVisibleXRangeMaximum(50);
        // chart.setVisibleYRange(30, AxisDependency.LEFT);

        // move to the latest entry
        chart.moveViewToX(chart.getData().getEntryCount());

        // this automatically refreshes the chart (calls invalidate())
        // chart.moveViewTo(data.getXValCount()-7, 55f,
        // AxisDependency.LEFT);
    }

    public static void printLogEntryList(List<Entry> entryList) {
        if(BuildConfig.DEBUG){
            StringBuilder stringBuilder = new StringBuilder();
            for(int i=0; i<entryList.size(); i++){
                stringBuilder.append(entryList.get(i).getY()+"--");
            }
            LogUtils.printLog(stringBuilder.toString());
        }
    }

    private void notifyDataSetChangeForChart() {
        //data.addEntry(new Entry(set.getEntryCount(), (float) (Math.random() * 40) + 30f), 0);
        chart.getData().notifyDataChanged();
        // let the chart know it's data has changed
        chart.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    public static void scanBleDeviceAndConnect() {
        BleController.getInstance().scanBle(true, new ScanCallback() {
            boolean findDevice = false;

            @Override
            public void onStopScanBle() {
                if (findDevice) {
                    Toast.makeText(BaseApplication.applicationContext, "扫描到蓝牙设备，开始连接", Toast.LENGTH_SHORT).show();
                    BleController.getInstance().connect(BleController.DEVICE_ADDRESS, new ConnectCallback() {
                        @Override
                        public void onConnSuccess() {
                            Toast.makeText(BaseApplication.applicationContext, "连接成功", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onConnFailed() {

                        }
                    });
                }
            }

            @Override
            public void onScanning(BluetoothDevice device, int rssi, byte[] scanRecord) {
                if (BleController.DEVICE_ADDRESS.equalsIgnoreCase(device.getAddress())) {
                    findDevice = true;
                }
            }
        });
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        LogUtils.printLog("蓝牙已关闭2222222");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        LogUtils.printLog("蓝牙已打开111");
                        scanBleDeviceAndConnect();
                        break;
                }
            }
        }
    };

    private void disableAllButton(){
        startButton.setEnabled(false);
        startButton.setBackgroundResource(R.drawable.circle_corner_disable_gray_bg_2dp);
        stopButton.setEnabled(false);
        stopButton.setBackgroundResource(R.drawable.circle_corner_disable_gray_bg_2dp);
        saveButton.setEnabled(false);
        saveButton.setBackgroundResource(R.drawable.circle_corner_disable_gray_bg_2dp);
        resetButton.setEnabled(false);
        resetButton.setBackgroundResource(R.drawable.circle_corner_disable_gray_bg_2dp);
    }

    private void enableStartButton() {
        disableAllButton();
        startButton.setEnabled(true);
        startButton.setBackgroundResource(R.drawable.circle_corner_green_bg_normal_2dp);
    }

    public void saveMeasureLog(String username, String passwd){
        Map<String, String> deviceParams = new HashMap();
        deviceParams.put("createtime", "");
        deviceParams.put("deviceId", "");
        deviceParams.put("deviceName", "");
        deviceParams.put("deviceRemark", "");
        deviceParams.put("deviceSerialNo", "");
        deviceParams.put("status", "");
        deviceParams.put("updatetime", "");

        Map<String, String> modelParams = new HashMap();
        modelParams.put("modelId", "");
        modelParams.put("modelName", "");
        modelParams.put("modelRemark", "");
        modelParams.put("status", "");

        Map<String, String> operatorParams = new HashMap();
        operatorParams.put("createtime", "");
        operatorParams.put("deviceId", "");
        operatorParams.put("gender", "");
        operatorParams.put("operatorCompany", "");
        operatorParams.put("operatorId", "");
        operatorParams.put("operatorNameEn", "");
        operatorParams.put("operatorNameZh", "");
        operatorParams.put("operatorPassword", "");
        operatorParams.put("operatorRemark", "");
        operatorParams.put("updatetime", "");

        Map<String, String> testerParams = new HashMap();
        testerParams.put("createtime", "");
        testerParams.put("testName", "");
        testerParams.put("testGender", "");
        testerParams.put("testMobile", "");
        testerParams.put("testRemark", "");
        testerParams.put("testId", "");
        testerParams.put("testBirth", "");
        HttpRequest.post(HttpRequest.RequestType.POST,this, HttpRequest.CHECKUP_LOGIN, testerParams, new HttpRequest.HttpResponseCallBack(){

            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                BaseApplication.userInfo = JSON.parseObject(response.body().toString().trim(), UserInfoBean.class);
                startActivity(new Intent(CalibrationStartActivity.this, MainAcyivity.class));
                finish();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    public static void open(Context context) {
        Intent intent = new Intent(context, CalibrationStartActivity.class);
        context.startActivity(intent);
    }
}
