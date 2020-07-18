package com.test.bank.utils;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;


public class LogcatProcess {
    private WriteLogThread writeLogThread = null;
    private int mPId;

    private static LogcatProcess logcatProcess = null;
    private LogcatProcess() {
        mPId = android.os.Process.myPid();
        writeLogThread = new WriteLogThread(mPId+"");
        writeLogThread.start();
    }

    public static LogcatProcess getInstance(Context context) {
        if (logcatProcess == null) {
            synchronized (LogcatProcess.class){
                if(logcatProcess ==null){
                    logcatProcess = new LogcatProcess();
                }
            }
        }
        return logcatProcess;
    }

    public void start() {
    }

    public void stop() {
        if (writeLogThread != null) {
            writeLogThread.stopLogs();
            writeLogThread = null;
        }
    }

    private class WriteLogThread extends Thread {
        private Process logcatProc;
        private BufferedReader mReader = null;
        private boolean mRunning = true;
        String cmds = null;
        private String mPID;
        private FileOutputStream out = null;
        private SimpleDateFormat mfileDateFormat;
        private SimpleDateFormat mlogTimeFormat;
        private String logFileAbsolutePath;
        private final File logFile;

        public WriteLogThread(String pid) {
            mPID = pid;
            mfileDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            logFileAbsolutePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/logDir/"+mfileDateFormat.format(new Date())+".log";
            logFile = new File(logFileAbsolutePath);
            mlogTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
//              日志等级：*:v , *:d , *:w , *:e , *:f , *:s
            cmds = "logcat  | grep \"(" + mPID + ")\"";//打印所有日志信息
//             cmds = "logcat *:d| grep \"(" + mPID + ")\"";
//             cmds = "logcat -s test";//打印标签过滤信息
//            cmds = "logcat *:e *:i | grep \"(" + mPID + ")\"";
//            cmds = "logcat *.d | grep test";
        }

        public void stopLogs() {
            mRunning = false;
        }

        @Override
        public void run() {
            try {
                logcatProc = Runtime.getRuntime().exec(cmds);
                mReader = new BufferedReader(new InputStreamReader(logcatProc.getInputStream()), 1024);
                String line;
                while (mRunning && (line = mReader.readLine()) != null) {
                    if (!mRunning) {
                        break;
                    }
                    if (line.length() == 0) {
                        continue;
                    }

                    if (!logFile.exists()) {
                        logFile.getParentFile().mkdirs();
                        try {
                            logFile.createNewFile();
                            out = new FileOutputStream(logFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if (out != null && line.contains(mPID)) {
                        String timeStamp= mlogTimeFormat.format(new Date());
                        out.write((line + "\n").getBytes());
                    }
                }
            } catch (IOException e) {
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
                        out = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
