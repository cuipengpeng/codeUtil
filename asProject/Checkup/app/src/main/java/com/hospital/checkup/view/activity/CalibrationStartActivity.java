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
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.hospital.checkup.R;
import com.hospital.checkup.base.BaseApplication;
import com.hospital.checkup.base.BaseUILocalDataActivity;
import com.hospital.checkup.bluetooth.BleController;
import com.hospital.checkup.bluetooth.ConnectCallback;
import com.hospital.checkup.bluetooth.ScanCallback;
import com.hospital.checkup.utils.LogUtils;

import butterknife.BindView;
import butterknife.OnClick;

public class CalibrationStartActivity extends BaseUILocalDataActivity {

    @BindView(R.id.btn_calibrationStartActivity_start)
    Button startButton;
    @BindView(R.id.btn_calibrationStartActivity_stop)
    Button stopButton;
    @BindView(R.id.btn_calibrationStartActivity_save)
    Button saveButton;
    @BindView(R.id.chart1)
    LineChart chart;

    private Thread realTimePointThread;
    private RealTimePointRunnable realTimePointRunnable;
    private boolean stopCalibration = false;

    @OnClick({R.id.btn_calibrationStartActivity_start, R.id.btn_calibrationStartActivity_stop, R.id.btn_calibrationStartActivity_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_calibrationStartActivity_start:
                stopCalibration = false;
                chart.clear();
                realTimePointThread.start();
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
                BleController.getInstance().enableNotification(false, BleController.getInstance().mBleGattCharacteristic);
                break;
            case R.id.btn_calibrationStartActivity_save:
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
    }

    private void initChart() {
        // no description text
        chart.getDescription().setEnabled(false);

        // enable touch gestures
        chart.setTouchEnabled(true);

        chart.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setDrawGridBackground(false);
        chart.setHighlightPerDragEnabled(true);

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
        realTimePointThread = new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0; i<500;i++){
                    if(stopCalibration){
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
            }
        });
        realTimePointThread.start();
    }

    class RealTimePointRunnable implements Runnable{
        public int index =0;

        @Override
        public void run() {
            addEntry(index,300);
        }
    }

    private void initLineDataSet() {
        LineDataSet set1, set2, set3;
        // create a dataset and give it a type
//            set1 = new LineDataSet(values1, "DataSet 1");
        set1 = new LineDataSet(null, "DataSet 1");
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setColor(getResources().getColor(R.color.chartLineBlue));
        set1.setCircleColor(Color.GRAY);
        set1.setLineWidth(1f);
        set1.setCircleRadius(1.5f);
        set1.setFillAlpha(65);
        set1.setFillColor(ColorTemplate.getHoloBlue());
        set1.setHighLightColor(Color.rgb(244, 117, 117));
        set1.setDrawCircleHole(false);
        //set1.setFillFormatter(new MyFillFormatter(0f));
        //set1.setDrawHorizontalHighlightIndicator(false);
        //set1.setVisible(false);
        //set1.setCircleHoleColor(Color.WHITE);

        // create a dataset and give it a type
//            set2 = new LineDataSet(values2, "DataSet 2");
        set2 = new LineDataSet(null, "DataSet 2");
        set2.setAxisDependency(YAxis.AxisDependency.RIGHT);
        set2.setColor(getResources().getColor(R.color.chartLineYellow));
        set2.setCircleColor(Color.GRAY);
        set2.setLineWidth(1f);
        set2.setCircleRadius(1.5f);
        set2.setFillAlpha(65);
        set2.setFillColor(Color.RED);
        set2.setDrawCircleHole(false);
        set2.setHighLightColor(Color.rgb(244, 117, 117));
        //set2.setFillFormatter(new MyFillFormatter(900f));

//            set3 = new LineDataSet(values3, "DataSet 3");
        set3 = new LineDataSet(null, "DataSet 3");
        set3.setAxisDependency(YAxis.AxisDependency.RIGHT);
        set3.setColor(getResources().getColor(R.color.chartLineGreen));
        set3.setCircleColor(Color.GRAY);
        set3.setLineWidth(1f);
        set3.setCircleRadius(1.5f);
        set3.setFillAlpha(0);
        set3.setFillColor(ColorTemplate.colorWithAlpha(Color.YELLOW, 200));
        set3.setDrawCircleHole(false);
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
        set1.addEntryOrdered(new Entry(index, val1));
        float val2 = (float) (Math.random() * range) + 200;
        set2.addEntryOrdered(new Entry(index, val2));
        float val3 = (float) (Math.random() * range) + 350;
        set3.addEntryOrdered(new Entry(index, val3));

//            ILineDataSet set = data.getDataSetByIndex(0);
        // set.addEntry(...); // can be called as well

//            if (set == null) {
//                set = createSet();
//                data.addDataSet(set);
//            }

//            data.addEntry(new Entry(set.getEntryCount(), (float) (Math.random() * 40) + 30f), 0);
        chart.getData().notifyDataChanged();
        // let the chart know it's data has changed
        chart.notifyDataSetChanged();

        // limit the number of visible entries
        chart.setVisibleXRangeMaximum(50);
        // chart.setVisibleYRange(30, AxisDependency.LEFT);

        // move to the latest entry
        chart.moveViewToX(chart.getData().getEntryCount());

        // this automatically refreshes the chart (calls invalidate())
        // chart.moveViewTo(data.getXValCount()-7, 55f,
        // AxisDependency.LEFT);
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

    private void scanBleDeviceAndConnect() {
        BleController.getInstance().scanBle(true, new ScanCallback() {
            boolean findDevice = false;

            @Override
            public void onSuccess() {
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

    public static void open(Context context) {
        Intent intent = new Intent(context, CalibrationStartActivity.class);
        context.startActivity(intent);
    }
}
