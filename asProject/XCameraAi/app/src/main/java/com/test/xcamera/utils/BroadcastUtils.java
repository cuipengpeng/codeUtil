package com.test.xcamera.utils;

import android.content.Intent;

import com.test.xcamera.application.AppContext;

/**
 * Created by zll on 2019/7/18.
 */

public class BroadcastUtils {
    public static final String ACTION_USB_CONNECTED = "action_usb_connected";
    public static final String ACTION_USB_DISCONNECTED = "action_usb_disconnected";
    public static final String GET_SYSTEM_INFO = "get_system_info";

    public static void sendBroadcast(String action) {
        Intent intent = new Intent();
        intent.setAction(action);
        AppContext.getInstance().sendBroadcast(intent);
    }

    public static void sendBroadcast(String action, Intent intent) {
        intent.setAction(action);
        AppContext.getInstance().sendBroadcast(intent);
    }
}
