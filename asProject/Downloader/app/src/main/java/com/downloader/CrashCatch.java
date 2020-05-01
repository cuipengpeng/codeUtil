package com.downloader;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class CrashCatch implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "CrashCatch";

    private static final String CRASH_DIR = Environment.getExternalStorageDirectory() + "/CrashDir/";
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private Context mContext;
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");
    private String crashTime = "";

    private static volatile CrashCatch instance;
    private CrashCatch() {
    }
    public static CrashCatch getInstance() {
        if(instance==null){
            synchronized (CrashCatch.class){
                if(instance==null){
                    instance = new CrashCatch();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化
     */
    public void init(Context context) {
        mContext = context;
        if(BuildConfig.DEBUG){
            Thread.setDefaultUncaughtExceptionHandler(this);
            mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        }
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(3000);
            } catch (Exception e) {
                Log.e(TAG, "error : ", e);
            }
            //退出程序
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    /**
     * 自定义错误处理,收集错误信息。
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(final Throwable ex) {
        if (ex == null) {
            return false;
        }
        final String error = getCrashInfo(ex);
        writeToFile(CRASH_DIR, "Crash_"+crashTime+"_" + System.currentTimeMillis() + ".txt", error, ex.getMessage());

        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext, error, Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();
        return true;
    }

    /**
     * 收集设备信息与错误日志
     *
     * @param e
     */
    private String getCrashInfo(Throwable e) {
        StringBuilder sb = new StringBuilder();
        sb.append("生产厂商：\n");
        sb.append(Build.MANUFACTURER).append("\n\n");
        sb.append("手机型号：\n");
        sb.append(Build.MODEL).append("\n\n");
        sb.append("系统版本：\n");
        sb.append(Build.VERSION.RELEASE).append("\n\n");
        sb.append("异常时间：\n");
        crashTime = formatter.format(new Date());
        sb.append(crashTime).append("\n\n");
        sb.append("异常类型：\n");
        sb.append(e.getClass().getName()).append("\n\n");
        sb.append("异常信息：\n");
        sb.append(e.getMessage()).append("\n\n");
        sb.append("异常堆栈：\n");
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        e.printStackTrace(printWriter);
        Throwable cause = e.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);
        return sb.toString();
    }

    /**
     * 保存错误信息到文件中
     *
     * @param path
     * @param fileName  文件名
     * @param write_str 错误日志
     * @param ex        错误信息
     */
    private void writeToFile(String path, String fileName, String write_str, String ex) {
        File logDir = new File(path);
        if (!logDir.exists()) {
            logDir.mkdirs();
        }

        File logFile = new File(path+fileName);
        try {
            if (!logFile.exists()) {
                logFile.createNewFile();
            }
            FileOutputStream fout = new FileOutputStream(path + fileName);
            byte[] bytes = write_str.getBytes();
            fout.write(bytes);
            fout.close();
            Log.e(TAG, "保存成功" + path + fileName);
            //此地做上传错误日志代码, 上传成功后删除log文件
//            uploadLogFile(new File(path + fileName), ex);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "保存失败");
        }
    }
}
