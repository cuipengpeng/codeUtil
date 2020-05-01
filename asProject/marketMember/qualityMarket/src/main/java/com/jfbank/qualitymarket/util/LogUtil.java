package com.jfbank.qualitymarket.util;

import com.jfbank.qualitymarket.BuildConfig;

import android.util.Log;

/**
 * 日志打印类
 * @author 崔朋朋
 */
public class LogUtil {
	public static void printLog(String log){
		if (BuildConfig.DEBUG) {
			String clazzName2 = new Throwable().getStackTrace()[1].getClassName();
			String funcName2 = new Throwable().getStackTrace()[1].getMethodName();
			Log.d(clazzName2 + "--" + funcName2 + "()##############################", "jsonStr = " + log);
		}
	}
}
