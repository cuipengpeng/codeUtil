package com.test.bank.utils;

import android.os.Environment;
import android.util.Log;

import com.test.bank.BuildConfig;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LogToFileUtil {
	private WriteThread writeThread = null;
	private SimpleDateFormat mlogTimeFormat = null;

	private static volatile LogToFileUtil logToFileUtil = null;
	private LogToFileUtil() {
		writeThread = new WriteThread();
		mlogTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
		writeThread.start();
	}

	public static LogToFileUtil getInstance() {
		if (logToFileUtil == null) {
			synchronized (LogToFileUtil.class) {
				if (logToFileUtil == null) {
					logToFileUtil = new LogToFileUtil();
				}
			}
		}
		return logToFileUtil;
	}
 
	public static void printLogE(String str, Exception e) {
		//将Exception的错误信息转换成String
		String log = "";
		try {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			log = str + "\r\n" + sw.toString() + "\r\n";
		} catch (Exception e2) {
			log = str + " fail to print Exception";
		}
		printLog(log);
	}

	public static void printLog(String log) {
		//打印运行时接口的具体实现	this.getClass().getName()
		printLog(log, 2,false);
	}

	/**
	 * print current thread stack frame
	 */
	public static void printLog(String log, int level, boolean showTraceLevel) {
//		UI控件查看工具：<android-sdk>/tools/uiautomatorviewer
//		查看当前栈顶的Activity的Fragment ：
//		adb shell dumpsys activity activities | less
//		adb shell dumpsys activity com.android.camera |less
//
//		adb shell dumpsys activity your.package.name
//		adb shell dumpsys activity com.android.camera |less |grep 'ACTIVITY'
//		adb shell dumpsys activity activities | sed -En -e '/Running activities/,/Run #0/p'
//		adb shell dumpsys meminfo | grep pid
//		adb shell dumpsys cpuinfo | grep pid
//		adb shell dumpsys activity -h
//		adb shell dumpsys -h
//		adb shell dumpsys -l
		//StackTraceElement[] stackTraceArray = Thread.currentThread().getStackTrace();
		StackTraceElement[] stackTraceArray = new Throwable().getStackTrace();
		String tag = "";
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
			tag = clazzName2 + "." + funcName2+"("+lineNumber+")";
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
	
	private synchronized void writeLogToFile(String tag, String str) {
		String time = mlogTimeFormat.format(new Date());
		writeThread.enqueue(time + " " + tag + " " + str);
	}
 
	//线程保持常在,不工作时休眠,需要工作时再唤醒就可。线程池，handler消息队列的实现原理就是这样
	private class WriteThread extends Thread {
		private boolean isRunning = false;
		private String logFileAbsolutePath = null;
		private final File logFile;
		private Object lock = new Object();
		private ConcurrentLinkedQueue<String> mQueue = new ConcurrentLinkedQueue<String>();
		private SimpleDateFormat mfileDateFormat = null;
		private FileWriter filerWriter;
		private BufferedWriter bufWriter;


		public WriteThread() {
			mfileDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			logFileAbsolutePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/logDir/"+mfileDateFormat.format(new Date())+".log";
			logFile = new File(logFileAbsolutePath);
			isRunning = true;
		}
 
		//将需要写入文本的字符串添加到队列中.线程休眠时,再唤醒线程写入文件
		public void enqueue(String str) {
			mQueue.add(str);
			if (!isRunning) {
				synchronized (lock) {
					lock.notify();
				}
			}
		}

		@Override
		public void run() {
			try {
				while (true) {
					synchronized (lock) {
						isRunning = true;
						while (!mQueue.isEmpty()) {
							if (!logFile.exists()) {
								logFile.getParentFile().mkdirs();
								try {
									logFile.createNewFile();
									filerWriter = new FileWriter(logFile, true);
									bufWriter = new BufferedWriter(filerWriter);
								} catch (IOException e) {
									e.printStackTrace();
								}
							}

							try {
							//append data to logFile
							bufWriter.write(mQueue.poll());
							bufWriter.newLine();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
//						bufWriter.flush();
						isRunning = false;
						try {
							lock.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally {
				if(bufWriter!=null){
					try {
						bufWriter.close();
						bufWriter=null;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if(filerWriter!=null){
					try {
						filerWriter.close();
						filerWriter=null;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
 
		/**
		 * 删除日志文件
		 */
		public void deleteLogFile() {
			File file = new File(logFileAbsolutePath);
			if (file.exists()) {
				file.delete();
			}
		}
	}
}