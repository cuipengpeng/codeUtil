package com.test.bank.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.test.bank.utils.LogUtils;
import com.test.bank.view.activity.GestureVerifyActivity;

/**
 */

public class LockScreenReceiver extends BroadcastReceiver {
    private boolean isRegisterReceiver = false;
    private boolean validLockScreen = true;


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_SCREEN_ON)) {
            LogUtils.printLog("屏幕打开广播...");
        } else if (action.equals(Intent.ACTION_SCREEN_OFF)) {
            LogUtils.printLog("屏幕关闭广播...");
            if(validLockScreen){
                GestureVerifyActivity.putLockScreenStartTime(System.currentTimeMillis());
                validLockScreen=false;
            }
        }else if (Intent.ACTION_USER_PRESENT.equals(intent.getAction())) {
            LogUtils.printLog("解锁广播");
            validLockScreen=true;
        }
    }

    public void registerScreenActionReceiver(Context mContext) {
        if (!isRegisterReceiver) {
            isRegisterReceiver = true;
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            filter.addAction(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_USER_PRESENT);
            mContext.registerReceiver(this, filter);
        }
    }

    public void unRegisterScreenActionReceiver(Context mContext) {
        if (isRegisterReceiver) {
            isRegisterReceiver = false;
            mContext.unregisterReceiver(this);
        }
    }
}
