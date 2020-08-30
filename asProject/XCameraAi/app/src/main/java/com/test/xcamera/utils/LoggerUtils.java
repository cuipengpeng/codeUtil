package com.test.xcamera.utils;


import android.util.Log;

import com.test.xcamera.BuildConfig;

/**
 * creat by mz
 */
public class LoggerUtils {
    private static final String TAG = "LoggerUtils";
    public static boolean isdebug= BuildConfig.DEBUG;


    public  static  void  w(String tag, String msg){
        if (isdebug) {
            Log.w(tag, msg == null ? "" : msg);
        }
    }
    public  static  void  v(String tag, String msg){
        if (isdebug) {
            Log.v(tag, msg == null ? "" : msg);

        }
    }



    public  static  void  i(String tag, String msg){
        if (isdebug) {
            Log.i(tag, msg == null ? "" : msg);

        }
    }
    public static void d(String tag, String msg) {
        if (isdebug) {
            Log.d(tag, msg == null ? "" : msg);

        }
    }

    public static void d(String tag, String msg, Throwable tr) {
        if (isdebug) {
            Log.d(tag, msg == null ? "" : msg, tr);
        }
    }

    public static void d(String msg) {
        if (isdebug) {
            Log.d(TAG, msg == null ? "" : msg);
        }
    }

    public static void d(String msg, Throwable tr) {
        if (isdebug) {
            Log.d(TAG, msg == null ? "" : msg, tr);
        }
    }

    public static void e(String tag, String msg) {
        if (isdebug) {
            Log.e(tag, msg == null ? "" : msg);
        }
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (isdebug) {
            Log.e(tag, msg == null ? "" : msg, tr);
        }
    }

    public static void e(String msg) {
        if (isdebug) {
            Log.e(TAG, msg == null ? "" : msg);
        }
    }

    public static void e(String msg, Throwable tr) {
        if (isdebug) {
            Log.e(TAG, msg == null ? "" : msg, tr);
        }

    }

    public static void printLog(String log) {
        if (BuildConfig.DEBUG) {
            String clazzName2 = new Throwable().getStackTrace()[1].getClassName();
            String funcName2 = new Throwable().getStackTrace()[1].getMethodName();
            int lineNumber = new Throwable().getStackTrace()[1].getLineNumber();

            int maxLogLength = 3000;
            if (log.length() > 0 && log.length() <= maxLogLength) {
                Log.d(clazzName2 + "--" + funcName2+"("+lineNumber+")--##########", "jsonStr = " + log);
//                Logcat.d(clazzName2 + "--" + funcName2+"("+lineNumber+")--##########"+ "jsonStr = " + log);
            } else {
                // 由于logcat默认的message长度为4000，因此超过该长度就会截取剩下的字段导致log数据不全
                // 使用分段的方式来输出足够长度的message
                while (log.length() > maxLogLength) {
                    String logContent = log.substring(0, maxLogLength);
                    log = log.replace(logContent, "");
                    Log.d(clazzName2 + "--" + funcName2+"("+lineNumber+")--##", "jsonStr = " + logContent);
//                    Logcat.d(clazzName2 + "--" + funcName2+"("+lineNumber+")--##"+ "jsonStr = " + logContent);
                }
                Log.d(clazzName2 + "--" + funcName2+"("+lineNumber+")--##########",  "jsonStr = " + log);
//                Logcat.d(clazzName2 + "--" + funcName2+"("+lineNumber+")--##########"+ "jsonStr = " + log);
            }
        }
    }
}
