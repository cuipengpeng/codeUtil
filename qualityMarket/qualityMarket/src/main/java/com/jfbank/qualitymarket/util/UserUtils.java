package com.jfbank.qualitymarket.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.flyco.dialog.widget.MaterialDialog;
import com.jfbank.qualitymarket.AppContext;
import com.jfbank.qualitymarket.activity.AccountInfoActivity;
import com.jfbank.qualitymarket.activity.LoginActivity;
import com.jfbank.qualitymarket.activity.PayActivity;
import com.jfbank.qualitymarket.constants.EventBusConstants;
import com.jfbank.qualitymarket.dao.StoreService;
import com.jfbank.qualitymarket.listener.DialogListener;
import com.qiyukf.unicorn.api.Unicorn;

import org.simple.eventbus.EventBus;

import cn.jpush.android.api.JPushInterface;

/**
 * 功能：用户工具类<br>
 * 作者：赵海<br>
 * 时间： 2017/3/21 0021<br>.
 * 版本：1.2.0
 */

public class UserUtils {

    /**
     * 退出登录清除数据
     *
     * @param context
     */
    public static void loginOutClearInfo(Context context) {
        Unicorn.logout();
        AppContext.clearWebViewCache(context);
        new StoreService(context).clearUserInfo();
        JPushInterface.clearAllNotifications(context.getApplicationContext());
        JPushInterface.setAliasAndTags(context.getApplicationContext(), "", null, null);
    }

    public static void reSetUser() {
        AppContext.isLogin = false;
        AppContext.user = null;
    }

    private static MaterialDialog dialogToken;

    /**
     * token实现对话框
     *
     * @param activity 当前的activity
     * @param errorMsg 错误的message
     * @param TAG      跳转tag
     */
    public static void tokenFailDialog(final Activity activity, String errorMsg, final String TAG) {
        if (!activity.isFinishing() && ((dialogToken != null && !dialogToken.isShowing()) || dialogToken == null)) {
            dialogToken = DialogUtils.showOneBtnDialog(activity, false, null, errorMsg, null, new DialogListener.DialogClickLisenter() {
                @Override
                public void onDialogClick(int type) {
                    loginOutClearInfo(activity);
                    Intent loginIntent = new Intent(activity, LoginActivity.class);
                    loginIntent.putExtra(LoginActivity.KEY_OF_COME_FROM, LoginActivity.TOKEN_FAIL_TAG);
                    activity.startActivity(loginIntent);
                    if (PayActivity.TAG.equals(TAG)) {
                        activity.finish();
                    }
                }
            });
        }
    }
}
