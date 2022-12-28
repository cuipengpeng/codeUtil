package com.pepe.aplayer.util;


import android.util.Log;

import com.pepe.aplayer.BuildConfig;


public class LogUtil {
    
    public static void printLog(String log) {
        //打印运行时接口的具体实现	this.getClass().getName()
        printLog(log, 2,false);
    }

    public static void printLog(String log, int level, boolean showTraceLevel) {
        //kotlin打印log方法只要这一个，把后两个参数加上默认值即可。上面单个参数的方法可不要。
        //StackTraceElement[] stackTraceArray = Thread.currentThread().getStackTrace();
        StackTraceElement[] stackTraceArray = new Throwable().getStackTrace();
        String tag = "aaaaaaa";
        String traceLevel = "";
        if(showTraceLevel){
            StringBuilder stringBuilder = new StringBuilder();
            int printLevel = level+1;
            if(printLevel>stackTraceArray.length){
                printLevel=stackTraceArray.length;
            }
//            for(int i=1; i<printLevel; i++){
//                stringBuilder.append(stackTraceArray[i].getClassName()+"."+stackTraceArray[i].getMethodName()+"("+stackTraceArray[i].getLineNumber()+")--");
//            }
            for(int i=(printLevel-1); i>0; i--){
                stringBuilder.append(stackTraceArray[i].getClassName()+"."+stackTraceArray[i].getMethodName()+"("+stackTraceArray[i].getLineNumber()+")--");
            }
            traceLevel=stringBuilder.toString()+"TraceLevel="+level+"--threadName="+Thread.currentThread().getName()+"--threadId="+Thread.currentThread().getId();
        }else{
            String clazzName2 = stackTraceArray[level].getClassName();
            String funcName2 = stackTraceArray[level].getMethodName();
            int lineNumber = stackTraceArray[level].getLineNumber();
            traceLevel = clazzName2 + "." + funcName2+"("+lineNumber+")";
        }

        int maxLogLength = 3000;
        if (log.length() <= maxLogLength) {
            Log.d(tag, traceLevel+"##########--jsonStr = " + log);
        } else {
            // 由于logcat默认的message长度为4000，因此超过该长度就会截取剩下的字段导致log数据不全
            // 使用分段的方式来输出足够长度的message
            while (log.length() > maxLogLength) {
                String logContent = log.substring(0, maxLogLength);
                log = log.replace(logContent, "");
                Log.d(tag, traceLevel+"##--jsonStr = " + logContent);
            }
            Log.d(tag, traceLevel+"##########--jsonStr = " + log);
        }
    }

}
