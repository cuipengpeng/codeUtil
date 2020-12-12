package com.test.bank.utils;

import android.text.TextUtils;
import android.util.Log;

import com.elvishew.xlog.XLog;
import com.test.bank.BuildConfig;


public class LogUtils {
    private LogUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static boolean isDebug = true;// 是否需要打印bug，可以在application的onCreate函数里面初始化
    private static final String TAG = "Log";

    // 下面四个是默认tag的函数
    public static void i(String msg) {
        if (isDebug)
            Log.i(TAG, msg);
    }

    public static void d(String msg) {
        if (isDebug)
            Log.d(TAG, msg);
    }

    public static void e(String msg) {
        if (isDebug)
            Log.e(TAG, msg);
    }

    public static void v(String msg) {
        if (isDebug)
            Log.v(TAG, msg);
    }

    // 下面是传入自定义tag的函数
    public static void i(String tag, String msg) {
        if (isDebug)
            Log.i(tag, msg);
    }

    public static void d(String tag, String msg) {
        if (isDebug)
            Log.d(tag, msg);
    }

    public static void e(String tag, String msg) {
        if (isDebug)
            Log.e(tag, msg);
    }

    public static void v(String tag, String msg) {
        if (isDebug)
            Log.v(tag, msg);
    }

    public static void printLog(String log) {
        if (BuildConfig.DEBUG) {
            String clazzName2 = new Throwable().getStackTrace()[1].getClassName();
            String funcName2 = new Throwable().getStackTrace()[1].getMethodName();
            int lineNumber = new Throwable().getStackTrace()[1].getLineNumber();

            int maxLogLength = 3000;
            if (log.length() <= maxLogLength) {
                Log.d(clazzName2 + "--" + funcName2+"("+lineNumber+")", "##########--jsonStr = " + log);
            } else {
                // 由于logcat默认的message长度为4000，因此超过该长度就会截取剩下的字段导致log数据不全
                // 使用分段的方式来输出足够长度的message
                while (log.length() > maxLogLength) {
                    String logContent = log.substring(0, maxLogLength);
                    log = log.replace(logContent, "");
                    Log.d(clazzName2 + "--" + funcName2+"("+lineNumber+")", "##--jsonStr = " + logContent);
                }
                Log.d(clazzName2 + "--" + funcName2+"("+lineNumber+")", "##########--jsonStr = " + log);
            }
        }
    }
}
