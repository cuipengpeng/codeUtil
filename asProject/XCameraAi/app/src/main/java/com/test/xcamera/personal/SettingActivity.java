package com.test.xcamera.personal;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.editvideo.ScreenUtils;
import com.framwork.base.view.MOBaseActivity;
import com.test.xcamera.BuildConfig;
import com.test.xcamera.R;
import com.test.xcamera.application.AiCameraApplication;
import com.test.xcamera.bean.User;
import com.test.xcamera.login.LoginActivty;
import com.test.xcamera.utils.PackageUtils;
import com.test.xcamera.utils.SPUtils;


import butterknife.BindView;
import butterknife.OnClick;

public class SettingActivity extends MOBaseActivity implements PersonUserInterface {
    private User.UserDetail mUserDetail;
    @BindView(R.id.tv_middle_title)
    TextView tvMiddleTitle;
    @BindView(R.id.tv_vision_value)
    TextView tv_vision_value;
    @BindView(R.id.liner_unlogin)
    LinearLayout unlogin;
    @BindView(R.id.rl_settingActivity_switchEnv)
    RelativeLayout switchEnvRelativeLayout;
    public static void startSettingActivity(Context context) {
        Intent intent = new Intent(context, SettingActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int initView() {
        return R.layout.activity_setting;
    }

    @Override
    public void initData() {
        if(BuildConfig.DEBUG){
            switchEnvRelativeLayout.setVisibility(View.VISIBLE);
        }else {
            switchEnvRelativeLayout.setVisibility(View.GONE);
        }
        tv_vision_value.setText(PackageUtils.getVersionName(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        tvMiddleTitle.setText(R.string.person_setting);
        mUserDetail = AiCameraApplication.userDetail;
        if (AiCameraApplication.isLogin()) {
            unlogin.setVisibility(View.VISIBLE);
        }else {
            unlogin.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.rl_name, R.id.rl_introduce, R.id.liner_unlogin,R.id.left_iv_title, R.id.rl_settingActivity_switchEnv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_settingActivity_switchEnv:
                showSwitchEnvDialog(this);
                break;
            case R.id.rl_name:
                PersonAgreeActivity.startPersonAgreeActivity(this,PersonAgreeActivity.mType_User);
                break;
            case R.id.rl_introduce:
                PersonAgreeActivity.startPersonAgreeActivity(this,PersonAgreeActivity.mType_Privacy);
                break;
            case R.id.left_iv_title:
                this.finish();
                break;
            case R.id.liner_unlogin:
                UNLoginDialog deleteDialog=new UNLoginDialog(this);
                deleteDialog.showGoneTitleDialog();
                deleteDialog.setDialogContent(15,getString(R.string.person_get_out));
                deleteDialog.setButtonClickListener(new UNLoginDialog.ButtonClickListener() {
                    @Override
                    public void sureButton(Dialog mDialog) {
                        SPUtils.unLogin(mContext);
                        SettingActivity.this.finish();
                    }

                    @Override
                    public void cancelButton(Dialog mDialog) {

                    }
                });

                break;
        }
    }


    @Override
    public User.UserDetail getUserMassage() {
        return mUserDetail;
    }

    @Override
    public void editUserCallBack(String msg) {
        mUserDetail = SPUtils.getUser(this);
    }

    @Override
    public void unTokenCallBack(String msg) {
        SPUtils.unLogin(mContext);
        Intent intent = new Intent(this, LoginActivty.class);
        startActivity(intent);
    }

    public void showSwitchEnvDialog(Context context){
        Dialog dialog = new Dialog(this, R.style.ActionSheetDialogStyle);
        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_setting_actvity_switch_env, null);
        TextView cancel = contentView.findViewById(R.id.tv_settingActivity_cancelEnv);
        TextView testEnv = contentView.findViewById(R.id.tv_settingActivity_testEnv);
        TextView onlineEnv = contentView.findViewById(R.id.tv_settingActivity_onlineEnv);

        cancel.setOnClickListener(v -> dialog.dismiss());
        testEnv.setOnClickListener(v -> {
            switchEnv(context, dialog, false);
        });
        onlineEnv.setOnClickListener(v -> {
            switchEnv(context, dialog, true);
        });
        dialog.setContentView(contentView);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.y = 0;
        lp.width = ScreenUtils.getScreenWidth(context);
        dialogWindow.setAttributes(lp);
        dialog.show();
    }

    private void switchEnv(Context context, Dialog dialog, boolean onlineEnv) {
        dialog.dismiss();
        SharedPreferences sharedPreferences = context.getSharedPreferences(SPUtils.FILE_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(SPUtils.KEY_OF_ONLINE_ENV, onlineEnv).commit();
        android.os.Process.killProcess(android.os.Process.myPid());
    }


}
