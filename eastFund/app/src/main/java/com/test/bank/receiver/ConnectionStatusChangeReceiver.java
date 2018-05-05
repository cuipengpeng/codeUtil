package com.test.bank.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.test.bank.utils.LogUtils;
import com.test.bank.utils.NetUtil;

/**
 * 网络连接的广播接收者。如果当前页面正处于网络连接异常的错误页面时恢复了网络连接，则页面自动刷新
 * Created by 55 on 2017/12/4.
 */

public class ConnectionStatusChangeReceiver extends BroadcastReceiver {
    boolean isNetworkConnected = NetUtil.isNetworkAvailable();

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.e("onReceive: " + isNetworkConnected + " --> " + NetUtil.isNetworkAvailable());
        if (!isNetworkConnected && NetUtil.isNetworkAvailable()) {      //off -- on
            LogUtils.e("NetStatus: Off --> On");
            if (onNetworkReconnectedListener != null) {
                onNetworkReconnectedListener.onNetworkReconnected();
            }
        } else if (isNetworkConnected && !NetUtil.isNetworkAvailable()) {   //on -- off
            LogUtils.e("NetStatus: On --> Off");
//            ToastUtils.showShort("网络连接异常");
        }
        isNetworkConnected = NetUtil.isNetworkAvailable();
    }

    private OnNetworkReconnectedListener onNetworkReconnectedListener;

    public void setOnNetworkReconnectedListener(OnNetworkReconnectedListener onNetworkReconnectedListener) {
        this.onNetworkReconnectedListener = onNetworkReconnectedListener;
    }

    /**
     * 当网络重新连接的回调【在BaseActivity/BaseFragment中刷新retryList集合】
     */
    public interface OnNetworkReconnectedListener {
        void onNetworkReconnected();
    }
}
