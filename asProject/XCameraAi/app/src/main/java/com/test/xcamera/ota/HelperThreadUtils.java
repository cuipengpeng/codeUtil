package com.test.xcamera.ota;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.test.xcamera.utils.LoggerUtils;

import java.io.FileOutputStream;

/**
 * Created by zhouxuecheng on
 * Create Time 2020/3/21
 * e-mail zhouxuecheng1991@163.com
 * <p>
 * 这个类的主要作用就是用来发送升级消息的,handlerThread 对消息的控制
 */

public class HelperThreadUtils {

    private HandlerThread handlerThread;
    public int upload_start = 98;
    public int uploading = 99;
    public int upload_finish = 100;
    public int upgrade = 101;
    private FileOutputStream outStream;
    private MessageHandler messageHandler;

    private HelperThreadUtils() {
        initHandlerThread();
    }

    public void init(FileOutputStream outStream) {
        this.outStream = outStream;
    }

    private static HelperThreadUtils messageHelper
            = new HelperThreadUtils();

    public static HelperThreadUtils instance() {
        return messageHelper;
    }

    private void initHandlerThread() {
        handlerThread = new HandlerThread("");
        handlerThread.start();
        messageHandler = new MessageHandler(handlerThread.getLooper());
    }

    private class MessageHandler extends Handler {
        public MessageHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                outStream.flush();

                byte[] messageData = (byte[]) msg.obj;
                int what = msg.what;
                outStream.write(messageData);
                LoggerUtils.i("OTA_LOG", "OTA 线程发送数据 !" + what);
                outStream.flush();
//                if (what == upgrade) {
//                    destory();
//                }
            } catch (Exception e) {

            }
        }
    }

    public void destory() {
        LoggerUtils.i("OTA_LOG", "销毁当前 handler !!!!");
        if (handlerThread != null) {
            handlerThread.quit();
            handlerThread = null;
        }
        if (messageHandler != null) {
            messageHandler.removeCallbacks(null);
            messageHandler = null;
        }
    }

    public void setData(byte[] data, int type_key) {
        Message message = Message.obtain();
        message.what = type_key;
        message.obj = data;
        if (messageHandler != null)
            messageHandler.sendMessage(message);
    }

    public void setData(byte[] data) {
        Message message = Message.obtain();
        message.obj = data;
        if (messageHandler != null) {
            messageHandler.sendMessage(message);
        }
    }

    public void setDataDelayed(byte[] data) {
        Message message = Message.obtain();
        message.obj = data;
        if (messageHandler != null) {
            messageHandler.sendMessageDelayed(message,30);
        }
    }
}
