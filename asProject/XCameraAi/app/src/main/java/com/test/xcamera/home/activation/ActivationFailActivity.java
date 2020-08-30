package com.test.xcamera.home.activation;

import android.Manifest;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.framwork.base.view.MOBaseActivity;
import com.test.xcamera.R;
import com.test.xcamera.utils.CameraToastUtil;
import com.test.xcamera.utils.PermissionUtils;
import com.test.xcamera.utils.PhoneUtils;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Author: mz
 * Time:  2019/10/22
 */
public class ActivationFailActivity extends MOBaseActivity {
    @BindView(R.id.tv_agin)
    TextView tvAgin;
    @BindView(R.id.tv_back_home)
    TextView tvBackHome;
    @BindView(R.id.tv_call)
    TextView tvCall;

    @Override
    public int initView() {
        return R.layout.activity_activation_fail;
    }

    @Override
    public void initData() {

    }


    @OnClick({R.id.tv_agin, R.id.tv_back_home, R.id.tv_call})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_agin:
                startAct(ActivatingActivity.class);
                finish();
                break;
            case R.id.tv_back_home:
                finish();
                break;
            case R.id.tv_call:
                check_permisson();
                break;
        }
    }

    String[] PERMISSIONS = new String[]{Manifest.permission.CALL_PHONE
    };
    public final int ALBUM_PERMISSIONS_CODE = 3123;
    public void check_permisson() {
        PermissionUtils.checkAndRequestMorePermissions(mContext, PERMISSIONS, ALBUM_PERMISSIONS_CODE, new PermissionUtils.PermissionRequestSuccessCallBack() {
            @Override
            public void onHasPermission() {
                // 权限已被授予
                PhoneUtils.call("010-8576318");
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case ALBUM_PERMISSIONS_CODE:
                PermissionUtils.onRequestMorePermissionsResult(mContext, PERMISSIONS, new PermissionUtils.PermissionCheckCallBack() {
                    @Override
                    public void onHasPermission() {

                        PhoneUtils.call("010-8576318");
                    }

                    @Override
                    public void onUserHasAlreadyTurnedDown(String... permission) {
                        CameraToastUtil.show("需要" + Arrays.toString(permission) + "权限", mContext);
                    }

                    @Override
                    public void onUserHasAlreadyTurnedDownAndDontAsk(String... permission) {
                        CameraToastUtil.show("需要" + Arrays.toString(permission) + "权限", mContext);
                    }
                });
        }

    }
    @Override
    public void onBackPressed() {
        CameraToastUtil.show("解禁流程不能退出" ,mContext);
    }
}
