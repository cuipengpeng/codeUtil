package com.hospital.checkup.utils;

import android.util.Log;
import com.hospital.checkup.BuildConfig;


public class LogUtils {

    public static void printLog(String log) {
        if (BuildConfig.DEBUG) {
            String clazzName2 = new Throwable().getStackTrace()[1].getClassName();
            String funcName2 = new Throwable().getStackTrace()[1].getMethodName();
            int lineNumber = new Throwable().getStackTrace()[1].getLineNumber();

            int maxLogLength = 3000;
            if (log.length() > 0 && log.length() <= maxLogLength) {
                Log.d(clazzName2 + "--" + funcName2+"("+lineNumber+")--##########", "jsonStr = " + log);
            } else {
                while (log.length() > maxLogLength) {
                    String logContent = log.substring(0, maxLogLength);
                    log = log.replace(logContent, "");
                    Log.d(clazzName2 + "--" + funcName2+"("+lineNumber+")--##", "jsonStr = " + logContent);
                }
                Log.d(clazzName2 + "--" + funcName2+"("+lineNumber+")--##########", "jsonStr = " + log);
            }
        }
    }

}
