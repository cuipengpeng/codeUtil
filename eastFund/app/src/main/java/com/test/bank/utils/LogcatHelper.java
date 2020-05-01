package com.test.bank.utils;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;


public class LogcatHelper {
    private static String logDir;
    private WriteThread mWriteThread = null;
    private int mPId;
    private String  logcatFileName = "Xlogcat";

    private static volatile LogcatHelper logcatHelper = null;
    private LogcatHelper(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {// 优先保存到SD卡中
            logDir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + logcatFileName;
        } else {// 如果SD卡不存在，就保存到本应用的目录下
            logDir = context.getFilesDir().getAbsolutePath()+ File.separator + logcatFileName;
        }
        File file = new File(logDir);
        if (!file.exists()) {
            file.mkdirs();
        }
        mPId = android.os.Process.myPid();
        mWriteThread = new WriteThread(String.valueOf(mPId), logDir);
        mWriteThread.start();
    }

    public static LogcatHelper getInstance(Context context) {
        if(logcatHelper ==null){
            synchronized (LogcatHelper.class){
                if (logcatHelper == null) {
                    logcatHelper = new LogcatHelper(context);
                }
            }
        }
        return logcatHelper;
    }

    public void stop() {
        if (mWriteThread != null) {
            mWriteThread.stopLogs();
            mWriteThread = null;
        }
    }

    private class WriteThread extends Thread {

        private Process logcatProc;
        private BufferedReader mReader = null;
        private boolean mRunning = true;
        String cmds = null;
        private String mPID;
        private FileOutputStream out = null;
        private SimpleDateFormat mFileDateFormat = null;

        public WriteThread(String pid, String dir) {
            mFileDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            mPID = pid;
            File file = new File(dir, mFileDateFormat.format(new Date())+ ".log");
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                try {
                    file.createNewFile();
                    out = new FileOutputStream(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            /**
             * 日志等级：*:v , *:d , *:w , *:e , *:f , *:s
             * 显示当前mPID程序的 E和W等级的日志.
             * */
             cmds = "logcat *:d| grep \"(" + mPID + ")\"";
//             cmds = "logcat  | grep \"(" + mPID + ")\"";//打印所有日志信息
//             cmds = "logcat -s aoaconnect";//打印标签过滤信息
//            cmds = "logcat *:e *:i | grep \"(" + mPID + ")\"";
//            cmds = "logcat *.d | grep aoacon";
        }


        @Override
        public void run() {
            try {
                logcatProc = Runtime.getRuntime().exec(cmds);
                mReader = new BufferedReader(new InputStreamReader(
                        logcatProc.getInputStream()), 1024);
                String line = null;
                while (mRunning && (line = mReader.readLine()) != null) {
                    if (!mRunning) {
                        break;
                    }
                    if (line.length() == 0) {
                        Thread.sleep(10);
                        continue;
                    }
                    if (out != null && line.contains(mPID)) {
                        out.write((System.currentTimeMillis() + "  " + line + "\n").getBytes());
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (logcatProc != null) {
                    logcatProc.destroy();
                    logcatProc = null;
                }
                if (mReader != null) {
                    try {
                        mReader.close();
                        mReader = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    out = null;
                }
            }
        }

        public void stopLogs() {
            mRunning = false;
        }
    }

}
