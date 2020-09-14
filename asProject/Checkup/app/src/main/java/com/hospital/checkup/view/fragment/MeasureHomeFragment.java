package com.hospital.checkup.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hospital.checkup.R;
import com.hospital.checkup.base.BaseApplication;
import com.hospital.checkup.base.BaseUILocalDataFragment;
import com.hospital.checkup.http.HttpRequest;
import com.hospital.checkup.view.activity.CalibrationActivity;
import com.hospital.checkup.view.activity.TesterDetailActivity;
import com.hospital.checkup.view.activity.WebViewActivity;
import com.hospital.checkup.widget.RegionImageView;

import butterknife.BindView;
import butterknife.OnClick;

public class MeasureHomeFragment extends BaseUILocalDataFragment {
    @BindView(R.id.iv_measureHomeActivity_body)
    RegionImageView bodyImageView;

    @BindView(R.id.ll_measureHomeActivity_adddMeasurer)
    LinearLayout llMeasureHomeActivityAdddMeasurer;
    @BindView(R.id.tv_measureHomeActivity_measureBodyArea)
    TextView tvMeasureHomeActivityMeasureBodyArea;
    @BindView(R.id.ll_measureHomeActivity_measureBodyArea)
    LinearLayout llMeasureHomeActivityMeasureBodyArea;
    @BindView(R.id.tv_measureHomeActivity_measureContent)
    public TextView measureContentTextView;
    @BindView(R.id.tv_measureHomeActivity_doctorName)
    public TextView doctorNameTextView;
    @BindView(R.id.tv_measureHomeActivity_measurerName)
    public TextView measurerNameTextView;
    @BindView(R.id.iv_measureHomeActivity_addMeasurer)
    public ImageView addUserImageView;
    @BindView(R.id.iv_measureHomeActivity_addDoctor)
    public ImageView addDoctorImageView;
    @BindView(R.id.btn_measureHomeActivity_measure)
    Button measureButton;
    @BindView(R.id.ll_measureHomeActivity_addDoctor)
    LinearLayout llMeasureHomeActivityAddDoctor;

    public static final int BODY_CODE_1 = 1;
    public static final int BODY_CODE_2 = 2;
    public static final int BODY_CODE_3 = 3;
    public static final int BODY_CODE_4 = 4;
    public static final int BODY_CODE_5 = 5;
    public static final int BODY_CODE_6 = 6;

    @OnClick({R.id.btn_measureHomeActivity_measure, R.id.ll_measureHomeActivity_addDoctor, R.id.ll_measureHomeActivity_addMeasurer,
            R.id.ll_measureHomeActivity_measureBodyArea, R.id.ll_measureHomeActivity_measureContent})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_measureHomeActivity_measure:
                CalibrationActivity.open(getActivity());
                break;
            case R.id.ll_measureHomeActivity_addDoctor:
                WebViewActivity.open(getActivity(), HttpRequest.H5_ADD_DOCTOR, true);
                break;
            case R.id.ll_measureHomeActivity_addMeasurer:
                WebViewActivity.open(getActivity(), HttpRequest.H5_ADD_TESTER, true);
                break;
            case R.id.ll_measureHomeActivity_measureBodyArea:
                break;
            case R.id.ll_measureHomeActivity_measureContent:
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
                Toast.makeText(BaseApplication.applicationContext, "----" + areaIndex, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void open(Context context) {
        Intent intent = new Intent(context, MeasureHomeFragment.class);
        context.startActivity(intent);
    }

}
