package com.hospital.checkup.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hospital.checkup.R;
import com.hospital.checkup.base.BaseUILocalDataActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
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

    @OnClick({R.id.btn_calibrationActivity_calibration, R.id.btn_calibrationActivity_measure})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_calibrationActivity_calibration:
                CalibrationStartActivity.open(this);
                break;
            case R.id.btn_calibrationActivity_measure:
                MeasureHomeActivity.open(this);
                break;
        }
    }

    @Override
    protected String getPageTitle() {
        return "标定";
    }

    @Override
    protected int getSubLayoutId() {
        return R.layout.activity_calibration;
    }

    @Override
    protected void initPageData() {

    }

    public static void open(Context context) {
        Intent intent = new Intent(context, CalibrationActivity.class);
        context.startActivity(intent);
    }
}
