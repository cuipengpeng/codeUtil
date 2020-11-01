package com.pepe.aplayer.util;


import android.util.Log;

import com.pepe.aplayer.BuildConfig;


public class LogUtil {

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
