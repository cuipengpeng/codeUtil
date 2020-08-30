package com.test.xcamera.accrssory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.test.xcamera.utils.LogAccessory;

public class USBReceiverStatus extends BroadcastReceiver {
    private final static String ACTION ="android.hardware.usb.action.USB_STATE";
    public final static int TYPE_USBReceiverStatus =1;
    public final static int TYPE_MoAoaActivity =2;
    public final static int TYPE_AccessoryReceiver =3;
    public final static int TYPE_AccessoryDETACHED =4;
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(ACTION)) {
            boolean connected = intent.getExtras().getBoolean("connected");
            if (connected) {
                AccessoryManager.getInstance().mCommunicator.usbConnect(context,intent);
            } else {
                AccessoryManager.getInstance().mCommunicator.detachedConnect(intent);
                LogAccessory.getInstance().showLog("Usb  Status 断开");

            }
        }

    }
}


