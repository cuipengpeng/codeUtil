package com.caishi.chaoge.utils;


import android.util.Log;

import com.caishi.chaoge.BuildConfig;

/**
 * Log
 */
@SuppressWarnings("unused")
public class LogUtil {
    private static final String TAG = "CHAOGELOG";
    private static final boolean LOG = BuildConfig.DEBUG;

    public static void i(String msg) {
        if (LOG)
            Log.i(TAG, msg);
    }

    public static void i(String tag, String msg) {
        if (LOG)
            Log.i(tag, msg);
    }

    public static void d(String msg) {
        if (LOG)
            Log.d(TAG, msg);
    }

    public static void d(String tag, String msg) {
        if (LOG)
            Log.d(tag, msg);
    }

    public static void w(String msg) {
        if (LOG)
            Log.w(TAG, msg);
    }

    public static void w(String tag, String msg) {
        if (LOG)
            Log.w(tag, msg);
    }

    public static void v(String msg) {
        if (LOG)
            Log.v(TAG, msg);
    }

    public static void v(String tag, String msg) {
        if (LOG)
            Log.v(tag, msg);
    }

    public static void e(String msg) {
        Log.e(TAG, msg);
    }

    public static void e(String tag, String msg) {
        Log.e(tag, msg);
    }

    public static void printLog(String log) {
        if (BuildConfig.DEBUG) {
            String clazzName2 = new Throwable().getStackTrace()[1].getClassName();
            String funcName2 = new Throwable().getStackTrace()[1].getMethodName();
            int lineNumber = new Throwable().getStackTrace()[1].getLineNumber();

            int maxLogLength = 3000;
            if (log.length() > 0 && log.length() <= maxLogLength) {
                Log.d(clazzName2 + "--" + funcName2+"("+lineNumber+")--##########", "jsonStr = " + log);
            } else {
                // 由于logcat默认的message长度为4000，因此超过该长度就会截取剩下的字段导致log数据不全
                // 使用分段的方式来输出足够长度的message
                while (log.length() > maxLogLength) {
                    String logContent = log.substring(0, maxLogLength);
                    log = log.replace(logContent, "");
                    Log.d(clazzName2 + "--" + funcName2+"("+lineNumber+")--###", "jsonStr = " + logContent);
                }
                Log.d(clazzName2 + "--" + funcName2+"("+lineNumber+")--##########", "jsonStr = " + log);
            }
        }
    }


}
