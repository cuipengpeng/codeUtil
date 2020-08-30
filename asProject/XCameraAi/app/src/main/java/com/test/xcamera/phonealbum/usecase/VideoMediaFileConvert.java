package com.test.xcamera.phonealbum.usecase;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.text.TextUtils;
import android.util.Log;

import com.editvideo.ParameterSettingValues;
import com.editvideo.PathUtils;
import com.editvideo.Util;
import com.editvideo.dataInfo.ClipInfo;
import com.editvideo.dataInfo.RecordClipsInfo;
import com.test.xcamera.utils.FileUtils;
import com.test.xcamera.utils.LoggerUtils;
import com.meicam.sdk.NvsAVFileInfo;
import com.meicam.sdk.NvsMediaFileConvertor;
import com.meicam.sdk.NvsSize;
import com.meicam.sdk.NvsStreamingContext;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * 视频转码
 */
public class VideoMediaFileConvert implements NvsMediaFileConvertor.MeidaFileConvertorCallback {
    private static final String TAG = "ConvertFiles";
    private static final int MESSAGE_CONVERT_START = 1;
    private static final int MESSAGE_CONVERT_FINISH = 2;
    private static final int MESSAGE_CONVERT_CANCEL = 3;

    public static final int MESSAGE_CONVERT_Error = 102;
    public static final int MESSAGE_ALL_VIDEO_CONVERT_FINISHED = 201;
    private Map<String, ClipInfo> mClipInfoMap = new HashMap<>();
    private int mIndex = 0;
    public final static int mProgressCode = 101;
    private long mCurrentTaskId;
    private String mDstPath;
    private String mDir;
    private List<ClipInfo> mClipList;
    private VideoMediaFileConvert.ConvertThread mConvertThread;
    private Handler mConvertHandler;
    private Handler mUIHandler;
    private NvsMediaFileConvertor mMediaFileConvertor;
    boolean isOnFinsh = false;
    private boolean mIsConvertIng = false;

    @Override
    public void onProgress(long taskId, float progress) {

        if (!isOnFinsh) {
            mUIHandler.removeMessages(MESSAGE_CONVERT_Error);
            mUIHandler.sendEmptyMessageDelayed(MESSAGE_CONVERT_Error,5000);
            Message message = mUIHandler.obtainMessage();
            message.what = mProgressCode;
            message.arg1 = (int) (100 * progress);
            mUIHandler.sendMessage(message);
            Log.i("club", "club_onProgress:" + progress);
        }
    }

    @Override
    public void onFinish(long taskId, String srcFile, String dstFile, int errorCode) {
        mUIHandler.removeMessages(mProgressCode);
        if (mCurrentTaskId != taskId) {
            return;
        }
        clipConvertComplete(dstFile,
                errorCode == NvsMediaFileConvertor.CONVERTOR_ERROR_CODE_NO_ERROR);
    }

    @Override
    public void notifyAudioMuteRage(long l, long l1, long l2) {

    }

    private class ConvertThread extends HandlerThread {

        public ConvertThread(String name) {
            super(name);
        }

        public ConvertThread(String name, int priority) {
            super(name, priority);
        }

        @Override
        protected void onLooperPrepared() {
            super.onLooperPrepared();
        }
    }

    public void addClipList(List<ClipInfo> clipList) {
        addClipMap(clipList);
        if (mClipList != null) {
            mClipList = clipList;
        } else {
            mClipList = clipList;

        }
    }

    public void addClipMap(List<ClipInfo> clipList) {
        for (ClipInfo info : clipList) {
            if (!mClipInfoMap.containsKey(info.getM_fileOldPath())) {
                mClipInfoMap.put(info.getM_fileOldPath(), info);
            }
        }
    }

    public VideoMediaFileConvert(RecordClipsInfo info) {
        mUIHandler = new UiHandler();
        mClipList = info.getClipList();
        addClipMap(mClipList);
        mConvertThread = new VideoMediaFileConvert.ConvertThread("convert thread");
        mConvertThread.start();
        mConvertHandler = new Handler(mConvertThread.getLooper()) {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                switch (msg.what) {
                    case MESSAGE_CONVERT_START: {
                        if (!convertFile()) {
                            clipConvertComplete(null, false);
                        }
                        break;
                    }
                    case MESSAGE_CONVERT_FINISH: {
                        mUIHandler.removeMessages(MESSAGE_CONVERT_Error);
                        finishConvert();
                        mConvertThread.quit();
                        break;
                    }
                    case MESSAGE_CONVERT_CANCEL: {
                        mUIHandler.removeMessages(MESSAGE_CONVERT_Error);
                        cancelConvert();
                        mConvertThread.quit();
                        break;
                    }
                }

            }
        };
    }

    public void sendConvertFileMsg() {
        mUIHandler.sendEmptyMessageDelayed(MESSAGE_CONVERT_Error,5000);
        isOnFinsh = false;
        mIsConvertIng = true;
        mConvertHandler.sendEmptyMessage(MESSAGE_CONVERT_START);
    }

    public void sendFinishConvertMsg() {
        mConvertHandler.sendEmptyMessage(MESSAGE_CONVERT_FINISH);
    }

    public void sendCancelConvertMsg() {
        mUIHandler.removeMessages(MESSAGE_CONVERT_Error);
        /**
         * 这里通过反射解决异常：Handler sending message to a Handler on a dead thread
         */
        Field messageQueueField = null;
        try {
            messageQueueField = Looper.class.getDeclaredField("mQueue");
            messageQueueField.setAccessible(true);
            Class<MessageQueue> messageQueueClass = (Class<MessageQueue>) Class.forName("android.os.MessageQueue");
            Constructor<MessageQueue>[] messageQueueConstructor = (Constructor<MessageQueue>[]) messageQueueClass.getDeclaredConstructors();
            for (Constructor<MessageQueue> constructor : messageQueueConstructor) {
                constructor.setAccessible(true);
                Class[] types = constructor.getParameterTypes();
                for (Class clazz : types) {
                    if (clazz.getName().equalsIgnoreCase("boolean")) {
                        messageQueueField.set(mConvertHandler.getLooper(), constructor.newInstance(true));
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mConvertHandler.sendEmptyMessage(MESSAGE_CONVERT_CANCEL);
    }

    private void finishConvert() {
        if (mMediaFileConvertor != null) {
            mMediaFileConvertor.release();
        }
        mMediaFileConvertor = null;
    }

    private void cancelConvert() {
        if (mMediaFileConvertor == null) {
            return;
        }
        mMediaFileConvertor.cancelTask(mCurrentTaskId);
        mMediaFileConvertor = null;
        if (!TextUtils.isEmpty(mDstPath)) {
            File file = new File(mDstPath);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    public boolean convertFile() {
        if (mIndex > mClipList.size() - 1 || mClipList.isEmpty()) {
            return false;
        }
        ClipInfo clipInfo = mClipList.get(mIndex);
        mDir= FileUtils.getProjectPath();
        File fileDir = new File(mDir);
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }
        if (clipInfo.isIsConvert()) {
            clipConvertComplete(clipInfo.getFilePath(),true);
            return true;
        }
        if (mMediaFileConvertor == null) {
            mMediaFileConvertor = new NvsMediaFileConvertor();
        }

        mMediaFileConvertor.setMeidaFileConvertorCallback(this, null);
        String mSrcPath = clipInfo.getFilePath();
        clipInfo.changeTrimIn(0);
        clipInfo.changeTrimOut(getVideoDuration(clipInfo));
        mDstPath = mDir + File.separator + "CONVERT_" + mIndex + "_ size" + mClipList.size() + "_" + System.currentTimeMillis() + "_" + PathUtils.getFileName(mSrcPath);
        LoggerUtils.printLog("video convertStart = " + clipInfo.getTrimIn() + "-->convertEnd" + clipInfo.getTrimOut());

        double bitrate = ParameterSettingValues.instance().getCompileBitrate();
        Hashtable<String, Object> config = new Hashtable<>();
        NvsSize nvsSize = getVideoNvsSize(clipInfo.getFilePath());
        if (bitrate != 0) {
            config.put(NvsStreamingContext.COMPILE_BITRATE, bitrate * 1000000);
        } else {
            config.put(NvsStreamingContext.COMPILE_BITRATE, 20 * 1000000);
            int height = Util.VIDEO_RES_1440;
            if (nvsSize != null) {
                if (nvsSize.height > nvsSize.width) {
                    height = Util.VIDEO_RES_2560;
                }
            }
            config.put(NvsMediaFileConvertor.CONVERTOR_CUSTOM_VIDEO_HEIGHT, height);
        }
        if(mMediaFileConvertor!=null){
            mCurrentTaskId = mMediaFileConvertor.convertMeidaFile(mSrcPath, mDstPath, false,
                    mClipList.get(mIndex).getTrimIn(), mClipList.get(mIndex).getTrimOut(), config);
        }

        return true;
    }

    public void clipConvertComplete(String newPath, boolean isSuccess) {
        isOnFinsh = true;
        if (mMediaFileConvertor != null) {
            mMediaFileConvertor.setMeidaFileConvertorCallback(null, null);
        }

        if (!isSuccess) {
            sendConvertFileMsg();
            return;
        }
        ClipInfo clip = mClipList.get(mIndex);
        clip.setFilePath(isSuccess ? newPath : clip.getFilePath());
        clip.setDuration(getVideoDuration(clip) / 1000);
        if (mOnMediaConvertCallBack != null) {
            mOnMediaConvertCallBack.onClipFinish(clip, isSuccess);
        }
        mIndex++;
        if (mIndex < mClipList.size()) {
            sendConvertFileMsg();
            LoggerUtils.printLog("当前转码视频Index-->" + mIndex);
        } else {//全部转码完毕
            mIsConvertIng = false;
            sendFinishConvertMsg();
            Message msg = mUIHandler.obtainMessage();
            msg.obj = mDstPath;
            msg.what = MESSAGE_ALL_VIDEO_CONVERT_FINISHED;
            mUIHandler.sendMessage(msg);
        }
    }

    public boolean isIsConvertIng() {
        return mIsConvertIng;
    }

    public long getVideoDuration(ClipInfo clip) {
        NvsAVFileInfo info = null;
        long duration = clip.getDuration();
        if (NvsStreamingContext.getInstance() != null) {
            info = NvsStreamingContext.getInstance().getAVFileInfo(clip.getFilePath());
        }
        if (info != null) {
            duration = info.getDuration();
        }
        return duration;
    }

    public static NvsSize getVideoNvsSize(String path) {
        NvsAVFileInfo info = null;
        if (NvsStreamingContext.getInstance() != null) {
            info = NvsStreamingContext.getInstance().getAVFileInfo(path);
        }
        NvsSize size = null;
        if (info != null) {
            size = info.getVideoStreamDimension(0);
        }

        return size;
    }


    public void onConvertDestroy() {
        if (null != mConvertHandler) {
            mConvertHandler.removeCallbacksAndMessages(null);
        }
    }

    class UiHandler extends Handler {


        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_ALL_VIDEO_CONVERT_FINISHED://转码结束
                    if (mOnMediaConvertCallBack != null) {
                        mOnMediaConvertCallBack.onFinish(mClipList, true);
                    }
                    break;
                case VideoMediaFileConvert.mProgressCode://转码中
                    if (mOnMediaConvertCallBack != null) {
                        if (mIndex >= 0 && mIndex < mClipList.size()) {
                            mOnMediaConvertCallBack.onProgress(mClipList.get(mIndex), mIndex, msg.arg1);
                        }
                    }
                    break;
                    case VideoMediaFileConvert.MESSAGE_CONVERT_Error://转码失败
                        sendCancelConvertMsg();
                        finishConvert();
                        sendFinishConvertMsg();
                        if (mOnMediaConvertCallBack != null) {
                        mOnMediaConvertCallBack.onError();
                    }
                    break;

            }
        }
    }

    private OnMediaConvertCallBack mOnMediaConvertCallBack;

    public void setOnMediaConvertCallBack(OnMediaConvertCallBack mOnMediaConvertCallBack) {
        this.mOnMediaConvertCallBack = mOnMediaConvertCallBack;
    }

    public interface OnMediaConvertCallBack {
        void onProgress(ClipInfo clip, int index, int progress);

        void onClipFinish(ClipInfo clipInfo, boolean isSuccess);

        void onFinish(List<ClipInfo> clipInfoList, boolean isSuccess);
        void onError();
    }
}
