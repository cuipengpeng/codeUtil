package com.test.xcamera.utils;

import android.support.annotation.StringRes;

import com.test.xcamera.application.AiCameraApplication;

public class StringUtil {

    public static boolean notEmpty(String str) {
        boolean notEmpty = false;
        if (str != null && !"".equals(str)) {
            notEmpty = true;
        }
        return notEmpty;
    }

    public static boolean isNull(String str) {
        boolean empty = false;
        if (str == null || "".equals(str)) {
            empty = true;
        }
        return empty;
    }

    /**
     * 使用Application获取string的值 方便使用
     * */
    public static String getStr(@StringRes int stringId) {
        if (AiCameraApplication.getContext().getResources() != null)
            return AiCameraApplication.getContext().getResources().getString(stringId);
        return "";
    }
}
