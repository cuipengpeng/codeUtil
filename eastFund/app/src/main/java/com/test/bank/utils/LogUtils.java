package com.test.bank.utils;

import android.text.TextUtils;
import android.util.Log;

import com.elvishew.xlog.XLog;
import com.test.bank.BuildConfig;

import java.util.Map;
import java.util.Set;


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
        //打印运行时接口的具体实现	this.getClass().getName()
        printLog(log, 2,false);
    }

    /**
     * print current thread stack frame
     */
    public static void printLog(String log, int level, boolean showTraceLevel) {
		//kotlin打印log方法只要这一个，把后两个参数加上默认值即可。上面单个参数的方法可不要。

//		UI控件查看工具：<android-sdk>/tools/uiautomatorviewer
//		查看当前栈顶的Activity的Fragment ：
//		adb shell dumpsys activity activities | less
//		adb shell dumpsys SurfaceFlinger| grep -20n 'composer'
//		adb shell dumpsys media.camera | less > dump.txt
//		adb shell dumpsys activity your.package.name
//		adb shell dumpsys activity com.android.camera |less
//		
//		adb shell getenforce	查看当前 Selinux 功能是 permissive(关闭)还是 enforce(打开)的	
//		adb shell setenforce 0	#关闭selinux权限，设置成模式permissive	
//		关闭selinux权限，通过adb命令setenforce 0来关闭（如果是非系统权限的apk,需要执行这步关闭selinux权限的操作，不然后面应用读取prop属性没有权限）；
//		adb shell setenforce 1	#打开Selinux：设置成模式enforce	
//		说明：setenforce 修改的状态在设备重启后会失效，需要重新执行命令重新设置。
//        monkey压测：
//        https://developer.android.com/studio/test/monkey
//        https://blog.csdn.net/daihuimaozideren/article/details/77529345
//        adb shell monkey -p com.mlab.cam --ignore-native-crashes --ignore-crashes --throttle 200 --pct-trackball 0 --pct-syskeys 0 -v --bugreport 3000000
//        adb shell monkey -v --throttle 200 --ignore-crashes --pct-touch 40 --pct-motion 35 --pct-appswitch 5 --pct-anyevent 5 --pct-trackball 0 --pct-syskeys 5 --pct-pinchzoom 5 -p com.mlab.cam --bugreport 3000000
//
//		adb shell dumpsys activity com.android.camera |less |grep 'ACTIVITY'
//		adb shell dumpsys activity activities | sed -En -e '/Running activities/,/Run #0/p'
//		adb shell dumpsys meminfo | grep pid
//		adb shell dumpsys cpuinfo | grep pid
//		adb shell dumpsys activity -h
//		adb shell dumpsys -h
//		adb shell dumpsys -l

        //StackTraceElement[] stackTraceArray = Thread.currentThread().getStackTrace();
        StackTraceElement[] stackTraceArray = new Throwable().getStackTrace();
        String tag = "aaaaaa";
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
            while (log.length() > maxLogLength) {
                String logContent = log.substring(0, maxLogLength);
                log = log.replace(logContent, "");
                Log.d(tag, traceLevel+"##--jsonStr = " + logContent);
            }
            Log.d(tag, traceLevel+"##########--jsonStr = " + log);
        }
    }

    /**
     * java打印当前进程的所有线程
     *
     * 线程切换Handler.sendMessage()/Thread.start()
     */
    public static void printThread() {
        Map<Thread, StackTraceElement[]> stacksMap = Thread.getAllStackTraces();
        Set<Thread> set = stacksMap.keySet();
        int index =0;
        for (Thread thread : set) {
            index++;
            printLog("================================================  "+index+" / "+set.size());
            StackTraceElement[] stackTraceElements = stacksMap.get(thread);
            printLog("---- print thread: name:"+thread.getName()+" id:"+thread.getId()+" Priority:"+thread.getPriority()+" start ------");
            StringBuilder stringBuilder = new StringBuilder("    ");
            for (StackTraceElement st : stackTraceElements) {
                printLog("StackTraceElement: " + st.toString());
//                stringBuilder.append(st.getClassName()+".")
//                        .append(st.getMethodName()+"(")
//                        .append(st.getFileName()+":")
//                        .append(st.getLineNumber()+")");
//                printLog("StackTraceElement: " + stringBuilder.toString());
            }
            printLog("---- print thread: name:"+thread.getName()+" id:"+thread.getId()+" Priority:"+thread.getPriority()+" end ------");
        }
    }
}
