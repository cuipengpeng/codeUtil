package com.test.xcamera.application;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.test.xcamera.accrssory.AccessoryManager;
import com.test.xcamera.accrssory.USBReceiverStatus;
import com.test.xcamera.utils.LogAccessory;

/**
 * Created by smz on 2020/3/16.
 */

public class AccessoryReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        LogAccessory.getInstance().showLog(0,"权限广播--成功接收：onReceive ——"+intent.getAction());

        if (AccessoryManager.getInstance().mCommunicator != null)
            AccessoryManager.getInstance().mCommunicator.onReceive(USBReceiverStatus.TYPE_AccessoryReceiver,context, intent,null);
    }
}