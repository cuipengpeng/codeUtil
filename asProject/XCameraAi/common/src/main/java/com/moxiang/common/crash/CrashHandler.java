package com.moxiang.common.crash;

import android.content.Context;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by admin on 2019/11/5.
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    public static final String TAG = "CrashHandler";

    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private static CrashHandler INSTANCE = new CrashHandler();
    private Context mContext;
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        mContext = context;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
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
        final String strhh = saveCrashInfo2File(ex);
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext, strhh, Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();
        //收集设备参数信息,保存日志文件
        writeFileSdcardFile(FileUtils.SDPATH, "Crash_" + System.currentTimeMillis() + ".txt", saveCrashInfo2File(ex), ex.getMessage());
        return true;
    }

    /**
     * 收集设备信息与错误日志
     *
     * @param e
     */
    public String saveCrashInfo2File(Throwable e) {
        StringBuilder sb = new StringBuilder();
        sb.append("生产厂商：\n");
        sb.append(Build.MANUFACTURER).append("\n\n");
        sb.append("手机型号：\n");
        sb.append(Build.MODEL).append("\n\n");
        sb.append("系统版本：\n");
        sb.append(Build.VERSION.RELEASE).append("\n\n");
        sb.append("异常时间：\n");
        sb.append(formatter.format(new Date())).append("\n\n");
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
    public void writeFileSdcardFile(String path, String fileName, String write_str, String ex) {
        if (!FileUtils.file.exists()) {
            FileUtils.CreateDir();
        }

        if (!FileUtils.checkFilePathExists(path+fileName)) {
            try {
                new File(path+fileName).createNewFile();
            } catch (IOException e) {

                e.printStackTrace();
            }
        }
        try {
            FileOutputStream fout = new FileOutputStream(path + fileName);
            byte[] bytes = write_str.getBytes();
            fout.write(bytes);
            fout.close();
            Log.e(TAG, "保存成功" + path + fileName);
            //此地做上传错误日志代码
//            uploadLogFile(new File(path + fileName), ex);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "保存失败");
        }
    }
}
