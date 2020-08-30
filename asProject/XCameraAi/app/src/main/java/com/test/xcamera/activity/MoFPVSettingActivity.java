package com.test.xcamera.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.framwork.base.view.MOBaseActivity;
import com.test.xcamera.R;
import com.test.xcamera.adapter.MoFPVSettingTrackAdapter;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by zll on 2019/10/18.
 */

public class MoFPVSettingActivity extends MOBaseActivity {

    @BindView(R.id.activity_fpv_more_setting_back)
    ImageView mBackBtn;
    @BindView(R.id.activity_fpv_more_setting_camera)
    ImageView mCameraImg;
    @BindView(R.id.activity_fpv_more_setting_camera_name)
    TextView mCameraName;
    @BindView(R.id.activity_fpv_more_setting_connect_state)
    TextView mConnectState;
    @BindView(R.id.activity_fpv_more_setting_battery_image)
    ImageView mBatteryImg;
    @BindView(R.id.activity_fpv_more_setting_battery_text)
    TextView mBatteryText;
    @BindView(R.id.activity_fpv_more_setting_sd_card)
    TextView mBatterySDCard;
//    @BindView(R.id.activity_fpv_more_setting_professional_switch)
//    Switch mProfessionalSwitch;
    @BindView(R.id.activity_fpv_more_setting_recyclerview)
    RecyclerView mRecyclerView;
    @BindView(R.id.activity_fpv_more_setting_kangshanshuo)
    Switch mKangShanShuoSwitch;
    @BindView(R.id.activity_fpv_more_setting_wuwei_layout)
    RelativeLayout mWuweiLayotu;
    @BindView(R.id.activity_fpv_more_setting_speaker_layout)
    RelativeLayout mSpakerLayout;
    @BindView(R.id.activity_fpv_more_setting_led_layout)
    RelativeLayout mLEDLayout;
    @BindView(R.id.activity_fpv_more_setting_peijian_layout)
    RelativeLayout mPeijianLayout;

    private MoFPVSettingTrackAdapter mAdapter;

    @Override
    public int initView() {
        return R.layout.activity_fpv_more_setting;
    }

    @Override
    public void initClick() {

    }

    @Override
    public void initData() {
        mAdapter = new MoFPVSettingTrackAdapter();

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mAdapter);
    }

    @OnClick({R.id.activity_fpv_more_setting_back, R.id.activity_fpv_more_setting_camera,
            R.id.activity_fpv_more_setting_camera_name, R.id.activity_fpv_more_setting_connect_state,
            R.id.activity_fpv_more_setting_battery_image, R.id.activity_fpv_more_setting_battery_text,
            R.id.activity_fpv_more_setting_sd_card,
            R.id.activity_fpv_more_setting_recyclerview, R.id.activity_fpv_more_setting_kangshanshuo,
            R.id.activity_fpv_more_setting_wuwei_layout, R.id.activity_fpv_more_setting_speaker_layout,
            R.id.activity_fpv_more_setting_led_layout, R.id.activity_fpv_more_setting_peijian_layout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.activity_fpv_more_setting_back:
                break;
            case R.id.activity_fpv_more_setting_camera:
                break;
            case R.id.activity_fpv_more_setting_camera_name:
                break;
            case R.id.activity_fpv_more_setting_connect_state:
                break;
            case R.id.activity_fpv_more_setting_battery_image:
                break;
            case R.id.activity_fpv_more_setting_battery_text:
                break;
            case R.id.activity_fpv_more_setting_sd_card:
                break;
//            case R.id.activity_fpv_more_setting_professional_switch:
//                break;
            case R.id.activity_fpv_more_setting_recyclerview:
                break;
            case R.id.activity_fpv_more_setting_kangshanshuo:
                break;
            case R.id.activity_fpv_more_setting_wuwei_layout:
                break;
            case R.id.activity_fpv_more_setting_speaker_layout:
                break;
            case R.id.activity_fpv_more_setting_led_layout:
                break;
            case R.id.activity_fpv_more_setting_peijian_layout:
                break;
        }
    }
}
