package com.test.xcamera.accrssory;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.Environment;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.test.xcamera.BuildConfig;
import com.test.xcamera.managers.ConnectionManager;
import com.test.xcamera.managers.MessageModel;
import com.test.xcamera.managers.MoCommandHandler;
import com.test.xcamera.ota.HelperThreadUtils;
import com.test.xcamera.application.AiCameraApplication;
import com.test.xcamera.constants.LogcatConstants;
import com.test.xcamera.home.MoAoaActivity;
import com.test.xcamera.managers.DataManager;
import com.test.xcamera.utils.LogAccessory;
import com.moxiang.common.logging.Logcat;

import java.io.Closeable;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


/**
 * USB配件模式
 */

public abstract class AccessoryCommunicator {
    private static final String TAG = AccessoryCommunicator.class.getSimpleName();
    public static final String ACTION_USB_PERMISSION =
            "com.meetvr.aicamera.USB_PERMISSION";
    public static final String ACTION_USB_ACCESSORY =
            "com.meetvr.aicamera.USB_ACCESSORY";
    public static String filePath = Environment.getExternalStorageDirectory().getPath() + "/MoTest";
    private UsbManager usbManager;
    private ParcelFileDescriptor fileDescriptor;
    private FileInputStream inStream;
    private FileOutputStream outStream;
    public boolean running;

    private long mTime = -1;
    private long mLastTime = -1;
    private long mLastPermissionTime = 0;

    private boolean mNeedReconnect = true;
    private CommunicationThread mCommunicationThread;

    private Timer mTimer = new Timer();
    private TimeOutTask mTimeOutTask = null;

    private class TimeOutTask extends TimerTask {
        @Override
        public void run() {
            for (Map.Entry<Integer, MessageModel> entry : MoCommandHandler.getInstance().mCacheMsg.entrySet()) {
//                Log.e("=====", "mTimeOutTask==>" + MoCommandHandler.getInstance().mCacheMsg.size());
                if (entry.getValue() == null) {
                    MoCommandHandler.getInstance().mCacheMsg.remove(entry.getKey());
                    continue;
                }

                if (System.currentTimeMillis() - entry.getValue().timestamp > entry.getValue().waitTime) {
                    Log.e("=====", "mTimeOutTask  key==>" + entry.getKey() + "  time:" + (System.currentTimeMillis() - entry.getValue().timestamp));
                    entry.getValue().tryCount++;
                    ConnectionManager.getInstance().flush();
                }

                if (entry.getValue().tryCount > 1) {
                    MoCommandHandler.getInstance().mCacheMsg.remove(entry.getKey());
                    Logcat.v().tag(LogcatConstants.FPV_PREVIEW).msg("消息返回超时 时间:" + System.currentTimeMillis() + "  msgId:" + entry.getKey()).out();
                }
            }
        }
    }

    public AccessoryCommunicator() {
        usbManager = (UsbManager) AiCameraApplication.getContext().getSystemService(Context.USB_SERVICE);
        mNeedReconnect = true;
//        checkConnect();
    }

    public void send(byte[] payload) {
        if (AccessoryManager.getInstance().sendMessageThread != null)
            AccessoryManager.getInstance().sendMessageThread.addMessage(payload);
    }

    private void receive(final byte[] payload, final int length) {

        if (mLastTime > 0) {
            mTime = System.nanoTime() - mLastTime;
        } else {
            mTime = 0;
        }
        onReceive(payload, length, mTime);
        mLastTime = System.nanoTime();
    }

    public abstract void onReceive(final byte[] payload, final int length, long time);

    public abstract void onError(String msg);

    public abstract void onConnected();

    public abstract void onDisconnected();

    public abstract void onPermissionFailed();

    private class CommunicationThread extends Thread {
        private boolean running = true;
        long time = 0;

        public void stopThread() {
            running = false;
        }

        @Override
        public void run() {
            LogAccessory.getInstance().showLog(" 接收消息线程 start runing：" + running);
            byte[] msg = new byte[Constants.BUFFER_SIZE_IN_BYTES];

            while (running) {
                if (inStream == null)
                    continue;
                try {
                    /*time=System.currentTimeMillis();
                    LogAccessory.getInstance().showLog("\n\n");
                    LogAccessory.getInstance().showLog("read.stream start:"+(System.currentTimeMillis()-time));*/
                    int len = inStream.read(msg); //等待消息时会阻塞在这里
//                    Log.e("=====", "len==>" + len);
                    /*LogAccessory.getInstance().showLog("read.stream end:"+(System.currentTimeMillis()-time)+" len="+len);*/
                    if (len > 0) {
                        /* time=System.currentTimeMillis();*/
                        receive(msg, len);
//                        receive(BytesUtil.subBytes(msg, 0, len), len);
                        /* LogAccessory.getInstance().showLog("receive:"+(System.currentTimeMillis()-time)+" msg="+msg.length);*/
                    }
                } catch (Exception ex) {
                    closeAccessory("11111");
                    Logcat.v().tag(LogcatConstants.AOACONNECT).msg("disconnected usb  exception: ex " + ex.toString()).out();
                    onError("USB Receive Failed " + ex.toString() + "\n");
                }
            }
        }
    }

    private void openAccessory(UsbAccessory accessory) {
        if (fileDescriptor == null) {
            try {
                fileDescriptor = usbManager.openAccessory(accessory);
            } catch (RuntimeException e) {
                Log.e("=====", "openAccessory==>" + e.getLocalizedMessage());
            }
            LogAccessory.getInstance().showLog("openAccessory _执行" + fileDescriptor);
            if (fileDescriptor == null) {
                setNeedReconnect(true);
                return;
            }
            setNeedReconnect(false);
            startAOAConnect();

        } else {
            startAOAConnect();
//            onError("could not connect");
            LogAccessory.getInstance().showLog("出错：openAccessory  fileDescriptor没有成功销毁 ");

        }
    }

    public void startAOAConnect() {
        LogAccessory.getInstance().showLog("openAccessory _成功打开AOA传输" + fileDescriptor);
        FileDescriptor fd = fileDescriptor.getFileDescriptor();
        inStream = new FileInputStream(fd);
        outStream = new FileOutputStream(fd);
        MoCommandHandler.getInstance().clear();

        //接收消息的线程
        if (mCommunicationThread == null) {
            mCommunicationThread = new CommunicationThread();
            mCommunicationThread.setName("read msg thread");
            mCommunicationThread.setPriority(10);
            mCommunicationThread.start();
        }

        // start send message thread
        if (AccessoryManager.getInstance().sendMessageThread == null) {
            AccessoryManager.getInstance().sendMessageThread = new AccessoryManager.SendMessageThread(outStream);
            AccessoryManager.getInstance().sendMessageThread.setName("send msg thread");
            AccessoryManager.getInstance().sendMessageThread.setPriority(10);
            AccessoryManager.getInstance().sendMessageThread.start();
            HelperThreadUtils.instance().init(outStream);
        }

        DataManager.getInstance().init();

        if (mTimeOutTask == null) {
            try {
                mTimeOutTask = new TimeOutTask();
                mTimer.schedule(mTimeOutTask, 500, 80);
            } catch (Exception e) {
                Log.e("=====", "==>" + e.getMessage());
            }
        }

        onConnected();
    }

    public void closeAccessory(String tag) {
        try{
            onDisconnected();
            LogAccessory.getInstance().showLog("closeAccessory : " + tag);
            running = false;
            if (AccessoryManager.getInstance().sendMessageThread != null) {
                AccessoryManager.getInstance().sendMessageThread.stopThread();
                AccessoryManager.getInstance().sendMessageThread = null;
            }
            if (mCommunicationThread != null) {
                mCommunicationThread.stopThread();
                mCommunicationThread = null;
            }

            if (MoCommandHandler.getInstance().mCacheMsg != null)
                MoCommandHandler.getInstance().mCacheMsg.clear();

            if (mTimeOutTask != null) {
                mTimeOutTask.cancel();
                mTimeOutTask = null;
            }
            DataManager.getInstance().release();
        }catch (Exception e){
            e.printStackTrace();
        }


        if (outStream != null) {
            closeQuietly(outStream);
            outStream = null;
        }
        if (inStream != null) {
            closeQuietly(inStream);
            inStream = null;
        }
        if (fileDescriptor != null) {
            closeQuietly(fileDescriptor);
            fileDescriptor = null;
        }
    }

    private void closeQuietly(Closeable closable) {
        try {
            closable.close();
        } catch (IOException exception) {
            if(BuildConfig.DEBUG){
                exception.printStackTrace();
            }
        }
    }

    public interface OnAccessoryCallBack {
        void onCallBack(boolean isSucc);
    }

    public synchronized void onReceive(int type, Context context, Intent intent, OnAccessoryCallBack callBack) {

        String action = intent.getAction();
        LogAccessory.getInstance().showLog(type, "执行：onReceive _" + action);
        if (ACTION_USB_PERMISSION.equals(action)) {
            UsbAccessory accessory = intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
            if (!checkAccessory(accessory)) {
                if (callBack != null) {
                    callBack.onCallBack(false);
                }
                LogAccessory.getInstance().showLog(type, "执行：onReceive ——授权失败 accessory=" + accessory);
                return;
            }

            if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                if (callBack != null) {
                    callBack.onCallBack(true);
                }
                openAccessory(accessory);
            } else {
                if (callBack != null) {
                    callBack.onCallBack(false);
                }
                LogAccessory.getInstance().showLog(type, "执行：onReceive ——授权失败error");
//                DlgUtils.toastCenter(context, "取消权限将无法连接相机");
                setNeedReconnect(true);
                onPermissionFailed();
            }
        } else if (UsbManager.ACTION_USB_ACCESSORY_ATTACHED.equals(action)) {
            UsbAccessory usbAccessory = intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
            if (!checkAccessory(usbAccessory)) {
                if (callBack != null) {
                    callBack.onCallBack(false);
                }
                return;
            }
            if (usbManager.hasPermission(usbAccessory)) {
                if (callBack != null) {
                    callBack.onCallBack(true);
                }
                openAccessory(usbAccessory);
            } else if (isNeedReconnect()) {
                LogAccessory.getInstance().showLog(type, "执行：onReceive ——授权广播操作");
                setNeedReconnect(false);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0);
                usbManager.requestPermission(usbAccessory, pendingIntent);
            }
        } else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
            setNeedReconnect(true);
            AccessoryManager.getInstance().mIsRunning = false;
            closeAccessory("22222");
            mLastPermissionTime = System.currentTimeMillis();
            Logcat.v().tag(LogcatConstants.AOACONNECT).msg("disconnected usb : usb accessory detached " + action).out();

        }
    }

    /**
     * 取消链接
     *
     * @param intent
     */
    public void detachedConnect(Intent intent) {
        if (MoAoaActivity.mMoAoaActivity != null) {
            MoAoaActivity.mMoAoaActivity.finish();
        }
        if (!mNeedReconnect) {
            intent.setAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
            onReceive(USBReceiverStatus.TYPE_AccessoryDETACHED, AiCameraApplication.getContext(), intent, null);
            mCountConnect = 0;
        }
        mIsStartMoAoaActivity = false;
        AccessoryManager.getInstance().mIsRunning = false;

    }

    private int mCountConnect = 0;
    private Handler mHandler = new Handler();

    public void setNeedReconnect(boolean mNeedReconnect) {
        this.mNeedReconnect = mNeedReconnect;
    }

    public boolean isNeedReconnect() {
        return mNeedReconnect;
    }

    public static boolean mIsStartMoAoaActivity = false;

    /**
     * 相机usb连接
     *
     * @param context
     * @param intent
     */
    public void usbConnect(Context context, final Intent intent) {
        mHandler.postDelayed(() -> {
            if (isNeedReconnect()) {
                LogAccessory.getInstance().showLog("Usb Status 连接广播---是否需要连接：" + mNeedReconnect);
            }
            if (isNeedReconnect()) {
                final UsbAccessory[] accessoryList = usbManager.getAccessoryList();
                if (accessoryList == null || accessoryList.length == 0) {
                    mCountConnect++;
                    if (mCountConnect < 3) {
                        usbConnect(context, intent);
                    }
                    LogAccessory.getInstance().showLog("获取 UsbAccessory null 失败");
                } else {
                    if (!mIsStartMoAoaActivity) {
                        LogAccessory.getInstance().showLog("获取到 UsbAccessory ");
                        Intent moIntent = new Intent(AiCameraApplication.getContext(), MoAoaActivity.class);
                        moIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        moIntent.putExtra(UsbManager.EXTRA_ACCESSORY, accessoryList[0]);
                        context.startActivity(moIntent);
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (!mIsStartMoAoaActivity && isNeedReconnect()) {
                                    AccessoryCommunicator.mIsStartMoAoaActivity = true;
                                    onReceive(USBReceiverStatus.TYPE_USBReceiverStatus, AiCameraApplication.getContext(), moIntent, null);
                                }
                            }
                        }, 200);
                    }


                }
            }
        }, 800);
    }

    private boolean checkAccessory(UsbAccessory usbAccessory) {
        if (usbAccessory == null || !"Moxiang".equals(usbAccessory.getManufacturer())) {
            setNeedReconnect(true);
            return false;
        }
        return true;
    }

}
