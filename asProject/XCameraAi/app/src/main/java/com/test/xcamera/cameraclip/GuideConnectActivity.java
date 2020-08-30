package com.test.xcamera.cameraclip;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.framwork.base.view.MOBaseActivity;
import com.test.xcamera.R;
import com.test.xcamera.home.GoUpActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class GuideConnectActivity extends MOBaseActivity{
    @BindView(R.id.left_iv_title)
    ImageView leftIvTitle;
    @BindView(R.id.tv_middle_title)
    TextView tvMiddleTitle;


    @OnClick({R.id.left_iv_title, R.id.tv_guideConnectActivity_use})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.left_iv_title:
                finish();
                break;
            case R.id.tv_guideConnectActivity_use:
                break;
        }
    }

    @Override
    public int initView() {
        return R.layout.activity_guide_connect;
    }

    @Override
    public void initData() {
        leftIvTitle.setImageResource(R.mipmap.icon_delete_white);
        tvMiddleTitle.setText(getResources().getString(R.string.deviceConnect));
    }

    @Override
    public void connectedUSB() {
        startAct(GoUpActivity.class);
    }
}
