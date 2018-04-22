package com.android.player.utils.encrypt;

public class LogUtils {
    public static void printLog(String log) {
//        if (BuildConfig.DEBUG) {
        if (true) {
            String clazzName2 = new Throwable().getStackTrace()[1].getClassName();
            String funcName2 = new Throwable().getStackTrace()[1].getMethodName();
            int lineNumber = new Throwable().getStackTrace()[1].getLineNumber();
            System.out.println(clazzName2 + "--" + funcName2+"("+lineNumber+")--##############################jsonStr = " + log);
        }
    }

    // 由于logcat默认的message长度为4000，因此超过该长度就会截取剩下的字段导致log数据不全
    // 使用分段的方式来输出足够长度的message
    public static void showAll(String str) {
        str = str.trim();
        int maxLength = 2000;
        if ("".equals(str) || str == null) {
        } else if (str.length() > 0 && str.length() <= maxLength) {
        	System.out.println(str);
        } else {
            while (str.length() > maxLength) {
                String logContent = str.substring(0, maxLength);
                str = str.replace(logContent, "");
            	System.out.println(logContent);

            }
        	System.out.println(str);
        }
    }

}
