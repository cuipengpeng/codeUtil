package com.test.xcamera.home.activation;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.framwork.base.view.MOBaseActivity;
import com.test.xcamera.R;
import com.test.xcamera.home.ActivationHelper;
import com.test.xcamera.login.LoginActivty;
import com.test.xcamera.utils.CameraToastUtil;
import com.test.xcamera.utils.SPUtils;
import com.test.xcamera.view.DeleteDialog;

import butterknife.OnClick;

/**
 * Author: mz
 * Time:  2019/10/22
 */
public class StartActivationActivty extends MOBaseActivity {
    private int status;
    private String activeId;
    private ActivationHelper helper;
    private DeleteDialog deleteDialog;

    @Override
    public int initView() {
        return R.layout.activiity_start_activation;
    }

    @Override
    public void initData() {
        helper = new ActivationHelper(this);
        status = getIntent().getIntExtra("status", -1);
        activeId = getIntent().getStringExtra("activeId");
//        isLogin();
        deleteDialog=new DeleteDialog((Activity) mContext);


    }


    public void isLogin() {
        if (SPUtils.isLogin(mContext)) {
            //绑定手机号页面
            startAct(ConfirmActivationPhone.class);
        } else {
            startAct(LoginActivty.class);
        }
    }


    @Override
    public void disconnectedUSB() {
        super.disconnectedUSB();
        CameraToastUtil.show("该设备已断开", mContext);
        onBackPressed();
    }


    @OnClick(R.id.tv_next)
    public void onViewClicked() {
        if(!SPUtils.isLogin(mContext)){
            deleteDialog.showTitleAndContent("请登录后激活","相机将与您的账号信息绑定", View.VISIBLE,View.VISIBLE);
            deleteDialog.showDialog(new DeleteDialog.SureOnClick() {
                @Override
                public void sure_button() {
//                    startAct(LoginActivty.class);
                    starAct();//
                    finish();
                }
            });
        }else{
            startAct(ConfirmActivationPhone.class);
            finish();
        }


    }

    public void starAct(){
        Intent intent=new Intent(mContext,LoginActivty.class);
        intent.putExtra("isActivation","1");
        startActivity(intent);
    }
    @Override
    public void onBackPressed() {
        CameraToastUtil.show("解禁流程不能退出" ,mContext);
    }
}
