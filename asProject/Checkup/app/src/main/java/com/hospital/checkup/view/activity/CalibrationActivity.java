package com.hospital.checkup.view.activity;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hospital.checkup.R;
import com.hospital.checkup.base.BaseApplication;
import com.hospital.checkup.base.BaseUILocalDataActivity;
import com.hospital.checkup.bean.TestModelBean;
import com.hospital.checkup.bluetooth.BleController;

import butterknife.BindView;
import butterknife.OnClick;

public class CalibrationActivity extends BaseUILocalDataActivity {

    @BindView(R.id.iv_calibrationActivity_body)
    ImageView ivCalibrationActivityBody;
    @BindView(R.id.tv_calibrationActivity_jointAngle)
    TextView jointAngleTextView;
    @BindView(R.id.tv_calibrationActivity_localAngle)
    TextView localAngleTextView;
    @BindView(R.id.tv_calibrationActivity_remoteAngle)
    TextView remoteAngleTextView;
    @BindView(R.id.btn_calibrationActivity_calibration)
    Button calibrationButton;
    @BindView(R.id.btn_calibrationActivity_measure)
    Button measureButton;

    private static final String KEY_OF_TEST_MODEL_BEAN = "testModelBeanKey";
    private final int CALIBRATION_START = 101;
    private final int CALIBRATION_FINISH = 102;
    private int currentCalibration = CALIBRATION_START;
    private boolean stopCalibration = true;

    @OnClick({R.id.btn_calibrationActivity_calibration, R.id.btn_calibrationActivity_measure})
    public void onViewClicked(View view) {
        if (!BleController.getInstance().isEnable()) {
            Toast.makeText(BaseApplication.applicationContext, "请打开蓝牙", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
            return;
        }
        switch (view.getId()) {
            case R.id.btn_calibrationActivity_calibration:
                switch (currentCalibration){
                    case CALIBRATION_START:
                        if(stopCalibration){
                            stopCalibration = false;
                            new Thread(randomAngleThread).start();
                            calibrationButton.setText("完成");
                            currentCalibration = CALIBRATION_FINISH;
                        }
                        break;
                    case CALIBRATION_FINISH:
                        stopCalibration = true;
                        currentCalibration = CALIBRATION_START;
                        calibrationButton.setText("标定");
                        break;
                }
                break;
            case R.id.btn_calibrationActivity_measure:
                stopCalibration = true;
                CalibrationStartActivity.open(this);
//                MeasureHomeActivity.open(this);
                break;
        }
    }

    @Override
    protected String getPageTitle() {
        return "测量标定";
    }

    @Override
    protected int getSubLayoutId() {
        return R.layout.activity_calibration;
    }

    @Override
    protected void initPageData() {
        TestModelBean testModelBean = (TestModelBean) getIntent().getSerializableExtra(KEY_OF_TEST_MODEL_BEAN);
        boolean jump=false;
        for(TestModelBean.ChildrenBeanX child2:testModelBean.getChildren()){
            if(child2.isSelected()){
                for(TestModelBean.ChildrenBeanX.ChildrenBean child3: child2.getChildren()){
                    if(child3.isSelected()){
                        Glide.with(this).load(child3.getModelExample()).into(ivCalibrationActivityBody);
                        jump = true;
                        break;
                    }
                }
                if(jump){
                    break;
                }
            }
        }
        randomAngleRunnable = new RandomAngleRunnable();
            randomAngleThread = new Runnable() {
            @Override
            public void run() {
                while (true){
                    if(stopCalibration){
                        break;
                    }
                    randomAngleRunnable.angle01 = ((int) (Math.random() * 100));
                    randomAngleRunnable.angle02 = ((int) (Math.random() * 100));
                    randomAngleRunnable.angle03 = ((int) (Math.random() * 100));
                    runOnUiThread(randomAngleRunnable);
                    try {
                        Thread.sleep(80);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    private Runnable randomAngleThread;
    private RandomAngleRunnable randomAngleRunnable;
    public class RandomAngleRunnable implements Runnable{
        public int angle01;
        public int angle02;
        public int angle03;

        @Override
        public void run() {
            jointAngleTextView.setText(angle01+"");
            localAngleTextView.setText(angle02+"");
            remoteAngleTextView.setText(angle03+"");
        }
    }

    public static void open(Context context, TestModelBean testModelBean) {
        Intent intent = new Intent(context, CalibrationActivity.class);
        intent.putExtra(KEY_OF_TEST_MODEL_BEAN, testModelBean);
        context.startActivity(intent);
    }
}
