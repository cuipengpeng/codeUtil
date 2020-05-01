package com.jfbank.qualitymarket.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfbank.qualitymarket.AppContext;
import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.base.BaseActivity;
import com.jfbank.qualitymarket.callback.AsyncResponseCallBack;
import com.jfbank.qualitymarket.listener.DialogListener;
import com.jfbank.qualitymarket.net.HttpRequest;
import com.jfbank.qualitymarket.util.CommonUtils;
import com.jfbank.qualitymarket.util.ConstantsUtil;
import com.jfbank.qualitymarket.util.DialogUtils;
import com.jfbank.qualitymarket.helper.DiskLruCacheHelper;
import com.jfbank.qualitymarket.util.LogUtil;
import com.jfbank.qualitymarket.util.StringUtil;
import com.jfbank.qualitymarket.util.ToastUtil;
import com.jfbank.qualitymarket.util.UserUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


/**
 * 账户信息
 *
 * @author 崔朋朋
 */
public class AccountInfoActivity extends BaseActivity {
    public static final String TAG = AccountInfoActivity.class.getName();
    @InjectView(R.id.rl_title)
    RelativeLayout rlTitle;
    @InjectView(R.id.tv_title)
    TextView tvTitle;
    @InjectView(R.id.tv_myAccountFragment_username)
    TextView usernameTextView;
    @InjectView(R.id.tv_myAccountFragment_phoneNumber)
    TextView phoneNumberTextView;
    @InjectView(R.id.tv_myAccountFragment_inviteCode)
    TextView inviteCodeTextView;
    @InjectView(R.id.iv_myAccountFragment_profile)
    ImageView profileImageView;
    @InjectView(R.id.btn_accountInfoActivity_logout)
    Button logoutButton;
    @InjectView(R.id.rl_accountInfoActivity_organization)
    RelativeLayout organizationRelativeLayout;

    @InjectView(R.id.v_accountInfoActivity_border_organization)
    View borderOrganizationView;
    @InjectView(R.id.tv_accountInfoActivity_organizationName)
    TextView organizationNameTextView;
    @InjectView(R.id.tv_version)
    TextView tvVersion;
    @InjectView(R.id.tv_cache_size)
    TextView tvCacheSize;
    @InjectView(R.id.ll_clear_cache)
    LinearLayout llClearCache;

    @OnClick({R.id.ll_clear_cache, R.id.tv_modifyLoginPassword, R.id.iv_back, R.id.btn_accountInfoActivity_logout, R.id.tv_myaddress})
    void onViewClick(View v) {
        switch (v.getId()) {
            case R.id.tv_modifyLoginPassword:
                Intent intent = new Intent(this, FindPasswordActivity.class);
                intent.putExtra(FindPasswordActivity.KEY_OF_COME_FROM, TAG);
                startActivity(intent);
                finish();
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_accountInfoActivity_logout:
                logout();
                break;
            case R.id.ll_clear_cache:
                if (DiskLruCacheHelper.size() <= 0) {
                    ToastUtil.show("缓存已经清理干净了~");
                } else {
                    DialogUtils.showTwoBtnDialog(mContext, "提示", "确定要清除缓存么？", "取消", "确定", new DialogListener.DialogClickLisenter() {
                        @Override
                        public void onDialogClick(int type) {
                            if (type == CLICK_SURE) {
                                DiskLruCacheHelper.delete();
                                tvCacheSize.setText(DiskLruCacheHelper.getCacheSize() + "");
                            }
                        }
                    });
                }
                break;
            case R.id.tv_myaddress://我的收获地址
                if (AppContext.isLogin) {
                    Intent intentAddress = new Intent();
                    intentAddress.setClass(mContext, MyReceivingAddressActivity.class);
                    intentAddress.putExtra(ConfirmOrderActivity.KEY_OF_SET_CONSIGNEE_ADDRESS, true);
                    startActivity(intentAddress); // 测试
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_info);
        ButterKnife.inject(this);
        CommonUtils.setTitle(this, rlTitle);
        tvTitle.setText(R.string.str_pagename_accountinfo);
        tvVersion.setText("v." + AppContext.getAppVersionName(mContext));
        if (AppContext.isLogin) {
            String idName = AppContext.user.getIdName();
            usernameTextView.setText(TextUtils.isEmpty(idName) ? "未实名" : idName);
            phoneNumberTextView.setText(AppContext.user.getMobile());
            inviteCodeTextView.setText(AppContext.user.getSelfInviteCode());
//			Picasso.with(this).load("http://f1.sharesdk.cn/imgs/2014/02/26/owWpLZo_638x960.jpg").placeholder(R.drawable.ic_launcher).resize(100, 100).into(profileImageView);
        } else {
            usernameTextView.setText("未登录");
            phoneNumberTextView.setVisibility(View.GONE);
            ;
            profileImageView.setBackgroundResource(R.drawable.myaccount_page_no_real_name_profile);
        }

        if (StringUtil.isNull(AppContext.user.getChannelName())) {
            organizationRelativeLayout.setVisibility(View.GONE);
            borderOrganizationView.setVisibility(View.GONE);
        } else {
            organizationRelativeLayout.setVisibility(View.VISIBLE);
            borderOrganizationView.setVisibility(View.VISIBLE);
            organizationNameTextView.setText(AppContext.user.getChannelName());
        }
        tvCacheSize.setText(DiskLruCacheHelper.getCacheSize() + "");
    }

    @Override
    protected String getPageName() {
        return getString(R.string.str_pagename_accountinfo);
    }

    private void logout() {
        logoutButton.setEnabled(false);
        if (AppContext.user == null || !AppContext.isLogin) {
            logoutButton.setEnabled(true);
            logoutClearInfo();
        } else {
            Map<String, String> a;
            Map<String, String> params = new HashMap<>();
            params.put("mobile", AppContext.user.getMobile());
            params.put("token", AppContext.user.getToken());
            params.put("uid", AppContext.user.getUid());
            HttpRequest.post(mContext, HttpRequest.QUALITY_MARKET_WEB_URL + HttpRequest.USER_LOGOUT, params, new AsyncResponseCallBack() {

                @Override
                public void onResult(String arg2) {
                    logoutButton.setEnabled(true);
                    String jsonStr = new String(arg2);
                    LogUtil.printLog("用户退出：" + jsonStr);
                    JSONObject jsonObject = JSON.parseObject(jsonStr);

                    if (ConstantsUtil.RESPONSE_SUCCEED == jsonObject.getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
                        logoutClearInfo();
                    } else {
                        Toast.makeText(AccountInfoActivity.this, "用户退出失败", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailed(String path, String msg) {
                    logoutButton.setEnabled(true);
                    Toast.makeText(AccountInfoActivity.this, ConstantsUtil.ORIGIN_PAGE_FAIL_TO_CONNECT_SERVER + "用户退出失败", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * 退出登录清除数据
     */
    private void logoutClearInfo() {
        UserUtils.reSetUser();
        UserUtils.loginOutClearInfo(this);
        finish();
    }


}
