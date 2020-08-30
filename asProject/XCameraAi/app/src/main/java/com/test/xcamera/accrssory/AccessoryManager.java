package com.test.xcamera.accrssory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import com.test.xcamera.application.AiCameraApplication;
import com.test.xcamera.bean.BaseData;
import com.test.xcamera.constants.LogcatConstants;
import com.test.xcamera.managers.ConnectionManager;
import com.test.xcamera.managers.DataManager;
import com.test.xcamera.mointerface.MoRequestValueCallback;
import com.test.xcamera.utils.LogAccessory;
import com.moxiang.common.logging.Logcat;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * usb通信
 * Created by zll on 2019/5/29.
 */

public class AccessoryManager {
    private static final String TAG = "AccessoryManager";
    static String folderName = Environment.getExternalStorageDirectory().getPath() + "/test_image";
    static String fileName = "image.jpg";

    public AccessoryCommunicator mCommunicator;
    private SoftReference<PreviewDataCallback> mPreviewDataCallback;
    private ConnectStateListener mConnectStateListener;
    public boolean mIsRunning = false;
    private boolean mLinkedFlag = false;
    public SendMessageThread sendMessageThread;

    public static int mConnectCount;
    //记录录像期间 MoFPVActivity启动次数
    public static int mMoFPVActCreateCount;

    private static class SingletonHoler {
        private static AccessoryManager instance = new AccessoryManager();
    }

    public static AccessoryManager getInstance() {
        return SingletonHoler.instance;
    }


    private AccessoryManager() {
        init();
    }

    public void init() {
        Log.e("=====", "AccessoryManager  init  mIsRunning==>" + mIsRunning);
        mIsRunning = false;
        // 初始化
        mCommunicator = new AccessoryCommunicator() {

            @Override
            public void onReceive(byte[] payload, int length, long time) {
                mIsRunning = true;
                if (!mLinkedFlag) {
                    if (mConnectStateListener != null) {
                        AiCameraApplication.mApplication.mHandler.post(() -> {
                            LogAccessory.getInstance().showLog("相机与手机正式连接 connectedUSB()------------------");

                            mIsRunning = true;
                            mConnectStateListener.connectedUSB();
                        });
                    }
                    mLinkedFlag = true;
                }
//                DataManager.getInstance().addData(payload);
                DataManager.getInstance().addData(payload, length);
            }

            @Override
            public void onError(String msg) {
                mIsRunning = false;
                Log.i(TAG, "onError: " + msg);
            }

            @Override
            public void onConnected() {
                LogAccessory.getInstance().showLog("执行连接 onConnected() ");
                mLinkedFlag = false;
                mIsRunning = false;     //收到消息为true 更保险
                ++mConnectCount;
                Log.i(TAG, "onConnected: ");
//                if (mConnectStateListener != null) {
//                    mConnectStateListener.connectedUSB();
//                }
            }

            @Override
            public void onDisconnected() {
                mLinkedFlag = false;
                mIsRunning = false;
                AiCameraApplication.isShowHardwareUpdateTips = false;
                LogAccessory.getInstance().showLog("执行销毁 onDisconnected() ");
                Logcat.v().tag(LogcatConstants.AOACONNECT).msg("disconnected usb : listener " + mConnectStateListener).out();
                if (mConnectStateListener != null) {
                    mConnectStateListener.disconnectedUSB();
                }

                //断开以后停止接收 线程停止
                if (mCommunicator != null)
                    mCommunicator.running = false;
            }

            @Override
            public void onPermissionFailed() {
                mIsRunning = false;
                Log.i(TAG, "permission denied for accessory");
            }
        };
    }

    public static class USBReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
//            if (AccessoryManager.getInstance().mCommunicator != null)
//                AccessoryManager.getInstance().mCommunicator.onReceive(context, intent);
        }
    }

    public void setPreviewDataCallback(PreviewDataCallback callback) {
        mPreviewDataCallback = new SoftReference<>(callback);
    }

    public void setConnectStateListener(ConnectStateListener listener, String tag) {
        mConnectStateListener = listener;
    }

    /**
     * 发送命令
     *
     * @param cmd
     */
    public void sendCommand(byte[] cmd) {
        if (mCommunicator != null) {
            mCommunicator.send(cmd);
        }
    }

    /**
     * 已经解析好的音视频数据
     *
     * @param baseData
     */
    public void onVideoDataAvailable(BaseData baseData) {
        if (mPreviewDataCallback != null && mPreviewDataCallback.get() != null) {
            mPreviewDataCallback.get().onVideoDataAvailable(baseData);
        } else {
            Log.e("=====", "onVideoDataAvailable==>mPreviewDataCallback is null");
        }
    }

    public void onAudioDataAvailable(BaseData baseData) {
        if (mPreviewDataCallback != null && mPreviewDataCallback.get() != null) {
            mPreviewDataCallback.get().onAudioDataAvailable(baseData);
        } else {
            Log.e("=====", "onAudioDataAvailable==>mPreviewDataCallback is null");
        }
    }

//    /**
//     * 已经解析好的控制命令
//     *
//     * @param baseData
//     */
//    public void onCtrlDataAvailable(BaseData baseData) {
//        if (baseData != null) {
//            if (baseData.getmCtrlInfo().getmCtrlCmd() == CtrlCmdType.STREAM_IO_CTRL_CMD_SNAP) {
//                // 拍照的结果
//                byte[] data = baseData.getmCtrlInfo().getmData();
//                fileName = System.currentTimeMillis() + ".jpg";
//                String path = FileUtil.writeFileToSDCard(FileUtil.createMediaFilePath(".jpg"), data);
//                if (mPreviewDataCallback != null) {
//                    // 生成缩略图供预览页显示
//                    Bitmap bitmap = BitmapUtils.decodeSampledBitmapFromFd(path, 100, 100);
////                    mPreviewDataCallback.onTakePhoto(bitmap, path);
////                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//                }
//            } else if (baseData.getmCtrlInfo().getmCtrlCmd() == CtrlCmdType.STREAM_IO_CTRL_CMD_START_RECORD) {
//                // 开始录制视频
//                Log.d(TAG, "onCtrlDataAvailable: start record");
//                if (mPreviewDataCallback != null) {
//                    WorkStateManager.getInstance().setmWorkState(WorkState.RECORDING);
//                    String filePath = FileUtil.createMediaFilePath(".mp4");
//                    Log.d(TAG, "startRecording: " + filePath);
////                    Mp4V2Native.getInstance().startRecord(filePath);
////                    mPreviewDataCallback.onStartRecord(filePath);
//                }
//            } else if (baseData.getmCtrlInfo().getmCtrlCmd() == CtrlCmdType.STREAM_IO_CTRL_CMD_END_RECORD) {
//                // 结束录制视频
////                String filePath = Mp4V2Native.getInstance().stopRecord();
////                Log.d(TAG, "stopRecord: " + filePath);
////                WorkStateManager.getInstance().setmWorkState(WorkState.STANDBY);
////                byte[] data = baseData.getmCtrlInfo().getmData();
////                fileName = System.currentTimeMillis() + ".txt";
////                FileUtil.writeFileToSDCard(folderName, fileName, data);
////                if (mPreviewDataCallback != null) {
////                    Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Images.Thumbnails.MINI_KIND);
////                    mPreviewDataCallback.onStopRecord(filePath, bitmap);
////                }
//            }
//        }
//    }

    public static class SendMessageThread extends Thread {
        private LinkedBlockingQueue<byte[]> messageList = new LinkedBlockingQueue<>();

        private FileOutputStream outStream;
        private boolean mRunning = true;
        private boolean initCmd = false;
        private int syncTimeCount = 0;
        private AtomicLong heartSendTime = new AtomicLong(0);

        public SendMessageThread(FileOutputStream outStream) {
            this.outStream = outStream;
        }

        public void stopThread() {
            mRunning = false;
            initCmd = false;
            this.interrupt();
        }

        //同步时间 很重要 如果失败 进行重试
        private void syncTime() {
            ConnectionManager.getInstance().syncTime(System.currentTimeMillis(), new MoRequestValueCallback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onFailed(int errCode) {
                    if (errCode != 2)
                        if (syncTimeCount++ < 3)
                            syncTime();
                }
            });
        }


        public void addMessage(byte[] data) {
            if (data == null || data.length == 0)
                return;
            try {
                messageList.put(data);
            } catch (InterruptedException e) {
//                e.printStackTrace();
                Log.e("=====", "SendMessageThread addMessage err==>" + e.getMessage());
            }
        }

        @Override
        public void run() {
            ConnectionManager.getInstance().initCallback();
            syncTime();
            while (mRunning) {

                try {
                    outStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }

//                while (messageList.size() <= 0) {
//                    synchronized (this) {
//                        try {
//                            LoggerUtils.printLog("send: ctrl data--wait");
//                            wait();
//                            FileUtil.writeFileToSDCard(testPath, "wait".getBytes(), testName, true, true, false);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }

//                byte[] cmd = messageList.remove(0);
//                    time = System.currentTimeMillis();
//                byte[] cmd = messageList.removeFirst();
                byte[] cmd = null;
//                if (System.currentTimeMillis() - heartSendTime.get() > 470 && !AccessoryManager.getInstance().mUpdateFlag) {
//                    Debug.debugUsb("send heart time==>" + (System.currentTimeMillis() - heartSendTime.get()));
//                    cmd = CommandManager.getInstance().heart();
//                    heartSendTime.set(System.currentTimeMillis());
//                } else {
//                if (messageList.isEmpty()) {
//                    try {
//                        Thread.currentThread().sleep(50);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    continue;
//                }

                try {
                    cmd = messageList.take();
                } catch (InterruptedException e) {
//                    e.printStackTrace();
                    Log.e("=====", "SendMessageThread messageList.take==>" + e.getMessage());
                }
                if (cmd == null)
                    continue;

//                Log.e("=====", "write==>" + cmd.length);
                try {
//                    Log.e("=====", "write==>" + cmd.length);
//                    Logcat.v().tag(LogcatConstants.FPV_PREVIEW).msg(String.format("SendMessageThread write==>len:%d  size:%d", cmd.length, messageList.size())).out();
                    outStream.write(cmd);
                    outStream.flush();
//                    long l = System.currentTimeMillis() - time;
//                    String s = String.valueOf(l);
//                    FileUtil.writeFileToSDCard(testPath, cmd, testName, true, true, false);
//                    FileUtil.writeFileToSDCard(testPath, s.getBytes(), testName, true, true, false);
                    Thread.sleep(30);
                } catch (Exception e) {
//                    Log.e("=====", "write err==>" + e.getMessage());
                    Logcat.e().tag(LogcatConstants.FPV_PREVIEW).msg("SendMessageThread write err==>" + e.getMessage()).out();
//                    FileUtil.writeFileToSDCard(testPath, e.getMessage().getBytes(), testName1, true, true, false);
//                    e.printStackTrace();
                }
            }
        }
    }

    private static long time = 0;

//    static String testPath = Environment.getExternalStorageDirectory().getPath() + "/MoTest";
//    static String testName = "send.txt";
//    static String testName1 = "exc22.txt";

    public interface PreviewDataCallback {
        void onVideoDataAvailable(BaseData baseData);

        default void onAudioDataAvailable(BaseData baseData) {
        }

        //        void onTakePhoto(Bitmap thumbnail, String filePath);
//
//        void onStartRecord(String filePath);
//
//        void onStopRecord(String filePath, Bitmap thumbnail);
    }

    public interface ConnectStateListener {
        void connectedUSB();

        void disconnectedUSB();
    }
}