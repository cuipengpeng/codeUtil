package com.test.xcamera.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.telephony.TelephonyManager;

import com.test.xcamera.application.AiCameraApplication;

/**
 * Author: mz
 * Time:  2019/10/16
 */
public class PhoneUtils {

    public static boolean isPhone() {
        TelephonyManager tm = (TelephonyManager) AiCameraApplication.mApplication.getSystemService(Context.TELEPHONY_SERVICE);
        return tm != null && tm.getPhoneType() != TelephonyManager.PHONE_TYPE_NONE;
    }

    @SuppressLint("MissingPermission")
    public static String getDeviceId() {
        TelephonyManager tm =
                (TelephonyManager) AiCameraApplication.mApplication.getSystemService(Context.TELEPHONY_SERVICE);
        return tm != null ? tm.getDeviceId() : null;
    }

    /**
     * 获取手机IMET
     * @return
     */
    @SuppressLint("MissingPermission")
    public static String getIMEI() {
        TelephonyManager tm =
                (TelephonyManager)AiCameraApplication.mApplication.getSystemService(Context.TELEPHONY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return tm != null ? tm.getImei() : null;
        }
        return tm != null ? tm.getDeviceId() : null;
    }
    @SuppressLint("MissingPermission")
    public static String getMEID() {
        TelephonyManager tm =
                (TelephonyManager) AiCameraApplication.mApplication.getSystemService(Context.TELEPHONY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return tm != null ? tm.getMeid() : null;
        } else {
            return tm != null ? tm.getDeviceId() : null;
        }
    }

//    /**
//     * 返送静默短信
//     * @param phoneNumber
//     * @param content
//     */
//    public static void sendSmsSilent(final String phoneNumber, final String content) {
//        if (TextUtils.isEmpty(content)) return;
//        PendingIntent sentIntent = PendingIntent.getBroadcast(AiCameraApplication.getmContext(), 0, new Intent(), 0);
//        SmsManager smsManager = SmsManager.getDefault();
//        if (content.length() >= 70) {
//            List<String> ms = smsManager.divideMessage(content);
//            for (String str : ms) {
//                smsManager.sendTextMessage(phoneNumber, null, str, sentIntent, null);
//            }
//        } else {
//            smsManager.sendTextMessage(phoneNumber, null, content, sentIntent, null);
//        }
//    }

    /**
     * 获取运营商
     * @return
     */
    public static String getSimOperatorByMnc() {
        TelephonyManager tm =
                (TelephonyManager)AiCameraApplication.mApplication.getSystemService(Context.TELEPHONY_SERVICE);
        String operator = tm != null ? tm.getSimOperator() : null;
        if (operator == null) return null;
        switch (operator) {
            case "46000":
            case "46002":
            case "46007":
                return "中国移动";
            case "46001":
                return "中国联通";
            case "46003":
                return "中国电信";
            default:
                return operator;
        }
    }


    /**
     * 拨打电话
     * @param phoneNumber
     */
    public static void call(final String phoneNumber) {
       AiCameraApplication.mApplication.startActivity(getCallIntent(phoneNumber, true));
    }

    public static Intent getCallIntent(final String phoneNumber, final boolean isNewTask) {
        Intent intent = new Intent("android.intent.action.CALL", Uri.parse("tel:" + phoneNumber));
        return getIntent(intent, isNewTask);
    }

    private static Intent getIntent(final Intent intent, final boolean isNewTask) {
        return isNewTask ? intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) : intent;
    }

    /**
     * 获取唯一 的uuid
     * @return
     */
    public static String getDeviceID() {
        String deviceID= "";
        try{
            //一共13位  如果位数不够可以继续添加其他信息
            deviceID= ""+Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +

                    Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +

                    Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +

                    Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +

                    Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +

                    Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +

                    Build.USER.length() % 10;
        }catch (Exception e){
            return "";
        }
        return deviceID+"03";
}
}
