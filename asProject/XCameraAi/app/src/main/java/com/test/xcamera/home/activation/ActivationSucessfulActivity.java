package com.test.xcamera.home.activation;

import android.widget.TextView;

import com.framwork.base.view.MOBaseActivity;
import com.test.xcamera.R;
import com.test.xcamera.utils.CameraToastUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Author: mz
 * Time:  2019/10/22
 */
public class ActivationSucessfulActivity extends MOBaseActivity {
    @BindView(R.id.tv_back_home)
    TextView tvBackHome;

    @Override
    public int initView() {
        return R.layout.activity_activation_sucessful;
    }

    @Override
    public void initData() {

    }


    @OnClick(R.id.tv_back_home)
    public void onViewClicked() {
        finish();
    }

    @Override
    public void onBackPressed() {
        CameraToastUtil.show("解禁流程不能退出" ,mContext);
    }
}
