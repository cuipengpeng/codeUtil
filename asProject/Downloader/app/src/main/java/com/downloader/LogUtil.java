package com.downloader;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LogUtil {
	private WriteThread writeThread = null;
	private SimpleDateFormat mlogTimeFormat = null;

	private static volatile LogUtil logUtil = null;
	private LogUtil() {
		writeThread = new WriteThread();
		mlogTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
		writeThread.start();
	}

	public static LogUtil getInstance() {
		if (logUtil == null) {
			synchronized (LogUtil.class) {
				if (logUtil == null) {
					logUtil = new LogUtil();
				}
			}
		}
		return logUtil;
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

    //命令行查看当前系统显示的是哪个activity
    //adb shell dumpsys activity activities | less

	public static void printLog(String totalLog) {
	   printLog(totalLog, 2,false);
	}
	
		public static void printLog(String totalLog, int level, boolean showTraceLevel) {
		if (BuildConfig.DEBUG) {
			String clazzName2 = new Throwable().getStackTrace()[1].getClassName();
			String funcName2 = new Throwable().getStackTrace()[1].getMethodName();
			int lineNumber = new Throwable().getStackTrace()[1].getLineNumber();
			String traceLevel = "";
            if(showTraceLevel){
                traceLevel="TraceLevel="+level;
            }

			int maxLogLength = 3000;
			String tag=null;
			String logStr=null;
			if (totalLog.length() <= maxLogLength) {
				tag= clazzName2 + "--" + funcName2+"("+lineNumber+")";
				logStr = traceLevel+"##########--jsonStr = " + totalLog;
				Log.d(tag, logStr);
				LogUtil.getInstance().writeLogToFile(tag, logStr);
			} else {
				// 由于logcat默认的message长度为4000，因此超过该长度就会截取剩下的字段导致log数据不全
				// 使用分段的方式来输出足够长度的message
				while (totalLog.length() > maxLogLength) {
					String logContent = totalLog.substring(0, maxLogLength);
					totalLog = totalLog.replace(logContent, "");

					tag= clazzName2 + "--" + funcName2+"("+lineNumber+")";
					logStr = traceLevel+"##--jsonStr = " + logContent;
					Log.d(tag, logStr);
					LogUtil.getInstance().writeLogToFile(tag, logStr);
				}
				tag= clazzName2 + "--" + funcName2+"("+lineNumber+")";
				logStr = traceLevel+"##########--jsonStr = " + totalLog;
				Log.d(tag, logStr);
				LogUtil.getInstance().writeLogToFile(tag, logStr);
			}
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
