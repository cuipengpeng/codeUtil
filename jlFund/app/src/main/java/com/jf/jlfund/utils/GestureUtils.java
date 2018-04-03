package com.jf.jlfund.utils;

import android.content.Context;
import android.text.TextUtils;

import com.jf.jlfund.bean.GestureVerify;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 */

public class GestureUtils {
    public static boolean isSet(Context context) {
//        if (User.getInstance().type == User.DEFULT_USER) {
//            return false;
//        }
        return isContantPsd(context);
    }

    /**
     * 获取当天时间
     *
     * @return
     */
    public static String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateNowStr = sdf.format(new Date());
        return dateNowStr;
    }
    public static boolean isContantPsd(Context context, String phoneNum) {
//        String gesture_isSet = (String) SPUtils.get(context, "gesture_isSet", "");
//        if (!TextUtils.isEmpty(gesture_isSet)) {
//            GestureVerify gestureVerify = GsonUtils.gson.fromJson(gesture_isSet, GestureVerify.class);
//            if (gestureVerify != null && TextUtils.equals(phoneNum, gestureVerify.getMobile())) {
//                return true;
//            }
//        }
        return false;
    }

    public static boolean isContantPsd(Context context) {
        return true;
//        return isContantPsd(context, User.getInstance().mobile);
    }

//    public static void removeGesuter(Context context) {
//        SPUtils.remove(context, "gesture_isSet");
//    }

    public static void setGesture(Context context, GestureVerify gestureVerify) {
        if (gestureVerify != null) {
//            SPUtil.put(context, "gesture_isSet", GsonUtils.gson.toJson(gestureVerify));
        }
    }

    public static GestureVerify getData(Context context) {
//        String gesture_isSet = (String) SPUtils.get(context, "gesture_isSet", "");
//        if (!TextUtils.isEmpty(gesture_isSet)) {
//            GestureVerify gestureVerify = GsonUtils.gson.fromJson(gesture_isSet, GestureVerify.class);
//            return gestureVerify;
//        }
        return null;
    }

    public static void reSetValidTimeTime(Context context, String phoneNum, long time) {
        if (isContantPsd(context, phoneNum)) {
            GestureVerify gestureVerify = GestureUtils.getData(context);
            gestureVerify.setValidTime(time);
            gestureVerify.setCountErrorNum(4);
            gestureVerify.setCurrentDate(GestureUtils.getCurrentDate());
            GestureUtils.setGesture(context, gestureVerify);
        }
    }

    public static String getProtectedMobile(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber) || phoneNumber.length() < 11) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        builder.append(phoneNumber.subSequence(0, 3));
        builder.append("****");
        builder.append(phoneNumber.subSequence(7, 11));
        return builder.toString();
    }
}
