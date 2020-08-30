package com.test.xcamera.home.activation;

import android.view.View;
import android.widget.TextView;

import com.framwork.base.view.MOBaseActivity;
import com.test.xcamera.R;
import com.test.xcamera.bean.User;
import com.test.xcamera.home.ActivationHelper;
import com.test.xcamera.login.LoginActivty;
import com.test.xcamera.utils.CameraToastUtil;
import com.test.xcamera.utils.SPUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Author: mz
 * Time:  2019/10/22
 */
public class ConfirmActivationPhone extends MOBaseActivity {

    @BindView(R.id.tv_change_phone)
    TextView tvChangePhone;
    @BindView(R.id.tv_confirm)
    TextView tvConfirm;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    private ActivationHelper helper;

    @Override
    public int initView() {
        return R.layout.activity_confirm_activation_phone;
    }

    @Override
    public void initData() {

        helper = new ActivationHelper(this);
        User.UserDetail detail = SPUtils.getUser(mContext);
        tvPhone.setText(detail.getPhone());
    }


    @OnClick({R.id.tv_change_phone, R.id.tv_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_change_phone:
                startAct(LoginActivty.class);
                break;
            case R.id.tv_confirm:
                 startAct(ActivatingActivity.class);
                 finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        CameraToastUtil.show("解禁流程不能退出" ,mContext);
    }
}
