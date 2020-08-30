package com.test.xcamera.managers;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.test.xcamera.accrssory.AccessoryManager;
import com.test.xcamera.accrssory.CacheArray;
import com.test.xcamera.accrssory.CircleQueue;
import com.test.xcamera.bean.BaseData;
import com.test.xcamera.constants.LogcatConstants;
import com.test.xcamera.mointerface.MoCommandCallback;
import com.test.xcamera.mointerface.MoDownloadCallback;
import com.test.xcamera.mointerface.OtaCallback;
import com.test.xcamera.utils.LoggerUtils;
import com.moxiang.common.logging.Logcat;

import org.json.JSONObject;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by zll on 2019/5/16.
 */

public class DataManager {
    private static final String TAG = "DataManager";
    public static final String SPLIT_URL = "#";
    public static final int HEAD_COUNT = 12;
    private static DataManager singleton = null;
    private static Object lock = new Object();
    private static Object LOCK = new Object();
    private byte[] mTotalData = null;

    private ParseDataThread mParseDataThread;
    public static String filePath = Environment.getExternalStorageDirectory().getPath() + "/MoTest";
//    private ArrayList<CacheArray.Cache> mDataList = new ArrayList<>();
    //    private LinkedList<CacheArray.Cache> mDataList = new LinkedList<>();
    private static boolean mIsConnected = false;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public CircleQueue mCircleQueue = new CircleQueue();
    public CacheArray mCacheArray = new CacheArray();
    private LinkedBlockingQueue<CacheArray.Cache> mDataList = new LinkedBlockingQueue();

    public static DataManager getInstance() {
        synchronized (lock) {
            if (singleton == null) {
                singleton = new DataManager();
            }
        }
        return singleton;
    }

    public DataManager() {
    }


    int addCount = 0;
    long currentTime;

    public void addData(byte[] bytes, int len) {
//        Log.e("=====", "recv  len==>" + len + "--" + mCircleQueue.size());
        //已经申请了1.5M内存 如果还是放不下 说明有问题
        if (!mCircleQueue.put(bytes, len)) {
            try {
                throw new Exception("从usb接收的数据过大 需要处理");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        CacheArray.Cache head = mCircleQueue.get(HEAD_COUNT, false);
        if (head == null) return;
        BaseData baseData = new BaseData(head.data);
        if (!"MO".equals(baseData.getmMagic())) {
            Log.e("=====", "mergerData: data error!!!");
            return;
        }
        int dataSize = baseData.getmDataSize();
        if (dataSize <= 0)
            throw new RuntimeException("baseData len is:" + dataSize);

        head.clear();
//            Log.e("=====", "get  len==>" + (dataSize + HEAD_COUNT + 4) + "--" + mCircleQueue.size());
        CacheArray.Cache dataInfo = mCircleQueue.get(dataSize + HEAD_COUNT + 4);
        if (dataInfo == null) return;

//        if (dataInfo.len <= 0)
//            throw new RuntimeException("create Cache len is:" + dataInfo.len + " real data len is:" + dataSize);
//
//        dataInfo.len = dataSize;
        try {
            mDataList.put(dataInfo);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void init() {
        mCircleQueue.init(0);
        if (mParseDataThread == null) {
            mParseDataThread = new ParseDataThread();
            mParseDataThread.setName("ParseDataThread");
            mParseDataThread.start();
        }
    }

    public void release() {
        mTotalData = null;
        addCount = 0;
        if (mParseDataThread != null) {
            mParseDataThread.stopThread();
            mParseDataThread = null;
        }

        DataManager.getInstance().mCacheArray.unInit();
        DataManager.getInstance().mCircleQueue.unInit();

        singleton = null;
    }

    /**
     * 分配的换成较大 需要及时清理
     */
    public void clearLargeCache() {
        mCacheArray.clearLargeCache();
    }

    private OtaCallback otaCallback;

    /**
     * 解析数据的Thread
     */
    public class ParseDataThread extends Thread {
        private boolean isRunning = true;
        private final Object LOCK = new Object();

        @Override
        public void run() {
            while (isRunning) {
                CacheArray.Cache data = null;
                try {
                    data = mDataList.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (data == null) continue;
                BaseData baseData = new BaseData(data.data);
                baseData.processMessage(baseData.getmMsgType(), data.data, data.len);
                data.clear();

                if (baseData.getmVideoFrameInfo() != null || baseData.getmAudioFrameInfo() != null
                        || baseData.getmCtrlInfo() != null || baseData.getmDownloadInfo() != null) {
                    mIsConnected = true;

//                    Log.e("=====", "Type==>" + baseData.getmMsgType());

                    switch (baseData.getmMsgType()) {
                        //画框跟踪的消息和指令消息一起处理
                        case STREAM_IO_TRACK:
                        case STREAM_IO_TYPE_IOCTRL:
                            handleMessage(baseData);
                            break;
                        case STREAM_IO_TYPE_VIDEO:
//                            Log.e("=====", "STREAM_IO_TYPE_VIDEO==>" + mDataAvailableCallback);
                            AccessoryManager.getInstance().onVideoDataAvailable(baseData);
                            //两秒打印一次
//                            if (videoPacketCountTest++ % 60 == 0) {
//                                if (videoPacketCountTest >= Integer.MAX_VALUE - 100)
//                                    videoPacketCountTest = 0;
////                                Logcat.v().tag(LogcatConstants.FPV_PREVIEW).msg("reveice video data==>STREAM_IO_TYPE_VIDEO").out();
//                            }
                            break;
                        case STREAM_IO_TYPE_AUDIO:
                            AccessoryManager.getInstance().onAudioDataAvailable(baseData);
                            break;
                        case STREAM_IO_DOWNLOAD_FILE:
                            String uri = baseData.getmDownloadInfo().getmUri();
                            uri = uri + DataManager.SPLIT_URL + baseData.getmDownloadInfo().getmOffset();
                            MoDownloadCallback callback = MoDownloadHandler.getInstance().getDownloadCallback(uri);
                            if (callback != null) {
                                callback.callback(baseData.getmDownloadInfo().getData());
                            }
                            break;
                        case STREAM_ACTIVATE:
                            handleMessage(baseData);
                            break;
                        case STREAM_OTA:
                            handleMessage(baseData);
                            break;
                        case STREAM_HEART:
                            mIsConnected = true;
                            if (mHandler != null) {
                                mHandler.removeCallbacks(mRunnable);
                            }
                            JSONObject heart = baseData.getmCtrlInfo().getJsonObject();

                            try {
                                String s = heart.optString("heart");
                                if (!TextUtils.isEmpty(s)) {
                                    mHandler.postDelayed(mRunnable, 1 * 1000);
                                }
                            } catch (Exception e) {
                            }
                            break;
                    }
                }
            }
        }

        public void stopThread() {
            isRunning = false;
            this.interrupt();
        }

        private void handleMessage(BaseData baseData) {
            int msgID;
            JSONObject jsonObject = baseData.getmCtrlInfo().getJsonObject();
            MoCommandCallback commandCallback = null;
            try {
                Log.e("recive_log==", "json==>" + jsonObject.toString());
                parseOta(jsonObject);
                Logcat.v().tag(LogcatConstants.FPV_PREVIEW).msg("receive cmd msg==>" + jsonObject.toString()).out();

                msgID = jsonObject.optInt("msg_id");
                String action = jsonObject.optString("action");

                if (action.equals("delete_media_resp")) {
                    LoggerUtils.i("MoAlbumPresenter", "msgID " + msgID + " 删除成功 !");
                    commandCallback = MoCommandHandler.getInstance().getDeleteCmdCallback(msgID);
                } else if (msgID == -2) {
                    commandCallback = MoCommandHandler.getInstance().getDeleteCmdCallback(msgID);
                } else {
                    commandCallback = MoCommandHandler.getInstance().getCmdCallback(msgID);
//                    ToastUtil.showToast(AppContext.getInstance(), "commandCallback " + commandCallback);
                }
                if (commandCallback != null) {
                    commandCallback.callback(jsonObject);
                }
            } catch (Exception e) {
                e.printStackTrace();
//                FileUtil.writeFileToSDCard(FileUtil.path, e.getMessage().getBytes(), "exc.txt", false, false, true);
            }
        }
    }

    public void setOtaCallback(OtaCallback otaCallback) {
        this.otaCallback = otaCallback;
    }

    /**
     * 解析ota升级成功
     *
     * @param data
     */
    private void parseOta(JSONObject data) {
        if (otaCallback != null) {
            if (data != null) {
                Log.i("DEVICE_PULL_LOG", "initCallback: data = " + data);
                int value = data.optInt("event");
                Log.i("DEVICE_PULL_LOG", "value  = " + value);
                if (value == OtaCallback.EVENT) {
                    int status = data.optInt("ota_result");
                    otaCallback.otaStatus(status);
                }
            }
        }
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mIsConnected = false;
        }
    };

    public static boolean isConnected() {
        return mIsConnected;
    }

    public static void setConnected(boolean connected) {
        mIsConnected = false;
    }
}
