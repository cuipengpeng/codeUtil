package com.hospital.checkup.view.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.hospital.checkup.R;
import com.hospital.checkup.base.BaseUILocalDataActivity;
import com.hospital.checkup.http.HttpRequest;
import com.hospital.checkup.view.adapter.TestRecordListAdapter;
import com.hospital.checkup.widget.RegionImageView;

import butterknife.BindView;
import butterknife.OnClick;

public class MeasureHomeActivity extends BaseUILocalDataActivity {
    @BindView(R.id.iv_measureHomeActivity_body)
    RegionImageView bodyImageView;
    @BindView(R.id.iv_measureHomeActivity_addMeasurer)
    ImageView addUserImageView;
    @BindView(R.id.iv_measureHomeActivity_addDoctor)
    ImageView addDoctorImageView;
    @BindView(R.id.btn_measureHomeActivity_measure)
    Button measureButton;

    @OnClick({R.id.btn_measureHomeActivity_measure, R.id.iv_measureHomeActivity_addDoctor, R.id.iv_measureHomeActivity_addMeasurer})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_measureHomeActivity_measure:
                TesterDetailActivity.open(this, "");
                break;
            case R.id.iv_measureHomeActivity_addDoctor:
                WebViewActivity.open(this, HttpRequest.H5_ADD_DOCTOR);
                break;
            case R.id.iv_measureHomeActivity_addMeasurer:
                WebViewActivity.open(this, HttpRequest.H5_ADD_TESTER);
                break;
        }
    }

    @Override
    protected String getPageTitle() {
        return "测量";
    }

    @Override
    protected int getSubLayoutId() {
        return R.layout.activity_measure_home;
    }

    @Override
    protected void initPageData() {
        bodyImageView.setOnImageViewAreaClickListener(new RegionImageView.OnImageViewAreaClickListener() {
            @Override
            public void onAreaClick(int areaIndex) {
                Toast.makeText(mContext, "----"+areaIndex, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void open(Context context){
        Intent intent = new Intent(context, MeasureHomeActivity.class);
        context.startActivity(intent);
    }

}
