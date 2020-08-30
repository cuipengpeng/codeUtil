package com.editvideo;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.text.TextUtils;
import android.util.Log;

import com.editvideo.dataInfo.ClipInfo;
import com.editvideo.dataInfo.RecordClipsInfo;
import com.test.xcamera.phonealbum.usecase.VideoMediaFileConvert;
import com.test.xcamera.utils.LoggerUtils;
import com.meicam.sdk.NvsAVFileInfo;
import com.meicam.sdk.NvsMediaFileConvertor;
import com.meicam.sdk.NvsSize;
import com.meicam.sdk.NvsStreamingContext;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by ms on 2018/9/7.
 * 将拍摄的视频片段转换为倒放片段
 */

public class ConvertFiles implements NvsMediaFileConvertor.MeidaFileConvertorCallback {

    private static final String TAG = "ConvertFiles";
    private static final int MESSAGE_CONVERT_START = 1;
    private static final int MESSAGE_CONVERT_FINISH = 2;
    private static final int MESSAGE_CONVERT_CANCEL = 3;
    public static final int MESSAGE_CONVERT_Error = 102;
    private int mIndex = 0;
    private int mFinishCode;
    public final static int mProgressCode=101;

    private long mCurrentTaskId;
    private String mDstPath;
    private String mDir;
    private RecordClipsInfo mClipsInfo;
    private List<ClipInfo> mClipList;
    public List<ClipInfo> mReverseClipList;
    private ConvertThread mConvertThread;
    private Handler mConvertHandler;
    private Handler mUIHandler;
    private NvsMediaFileConvertor mMediaFileConvertor;
    private int mSelectClipPosition;
    @Override
    public void onProgress(long taskId, float progress) {
        mUIHandler.removeMessages(MESSAGE_CONVERT_Error);
        mUIHandler.sendEmptyMessageDelayed(MESSAGE_CONVERT_Error,5000);
        Message message=mUIHandler.obtainMessage();
        message.what=mProgressCode;
        message.arg1=(int)(100*progress);
        mUIHandler.sendMessage(message);
        Log.i("club","club_onProgress:"+progress);
    }

    @Override
    public void onFinish(long taskId, String srcFile, String dstFile, int errorCode) {
        mUIHandler.removeMessages(MESSAGE_CONVERT_Error);
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

    public ConvertFiles(int selectClipPosition,RecordClipsInfo info, String dir, Handler handler, int finishCode) {
        mSelectClipPosition=selectClipPosition;
        mFinishCode = finishCode;
        mUIHandler = handler;
        mClipsInfo = info;
        mClipList = mClipsInfo.getClipList();
        mReverseClipList = mClipsInfo.getReverseClipList();
        mReverseClipList.clear();
        mDir = dir;
        File fileDir = new File(dir);
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }
        mConvertThread = new ConvertThread("convert thread");
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
                        finishConvert();
                        mConvertThread.quit();
                        break;
                    }
                    case MESSAGE_CONVERT_CANCEL: {
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
        mConvertHandler.sendEmptyMessage(MESSAGE_CONVERT_START);
    }

    public void sendFinishConvertMsg() {
        mConvertHandler.sendEmptyMessage(MESSAGE_CONVERT_FINISH);
    }

    public void sendCancelConvertMsg() {
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
        if (mMediaFileConvertor == null) {
            mMediaFileConvertor = new NvsMediaFileConvertor();
        }
        mMediaFileConvertor.setMeidaFileConvertorCallback(this, null);
        String mSrcPath = mClipList.get(mIndex).getFilePath();
        mDstPath = mDir + File.separator +"convert_"+ PathUtils.getFileName(mSrcPath);
        LoggerUtils.printLog("video convertStart =  " + mClipList.get(mIndex).getTrimIn() + "-->convertEnd" + mClipList.get(mIndex).getTrimOut());
        double bitrate = ParameterSettingValues.instance().getCompileBitrate();
        Hashtable<String, Object> config = new Hashtable<>();
        NvsSize nvsSize = VideoMediaFileConvert.getVideoNvsSize(mSrcPath);
        if (bitrate != 0) {
            config.put(NvsStreamingContext.COMPILE_BITRATE, bitrate * 1000000);
        } else {
            config.put(NvsStreamingContext.COMPILE_BITRATE, 20 * 1000000);
            int height = 720;
            if (nvsSize != null) {
                if (nvsSize.height > nvsSize.width) {
                    height = 1080;
                }
            }
            config.put(NvsMediaFileConvertor.CONVERTOR_CUSTOM_VIDEO_HEIGHT, height);
        }
        mCurrentTaskId = mMediaFileConvertor.convertMeidaFile(mSrcPath, mDstPath, true,-1, -1, config);
        return true;
    }

    public void clipConvertComplete(String newPath, boolean isSuccess) {
        ClipInfo clip = mClipList.get(mIndex);
        clip.setmIsConvertSuccess(isSuccess);
        ClipInfo reverseClip = new ClipInfo();
        reverseClip.setFilePath(isSuccess ? newPath : clip.getFilePath());
        reverseClip.setmIsConvertSuccess(isSuccess);
        long duration = clip.getDuration();
        if (isSuccess) {//转码成功,获取转码后视频时长
            NvsAVFileInfo info = null;
            if(NvsStreamingContext.getInstance()!=null){
                 info = NvsStreamingContext.getInstance().getAVFileInfo(newPath);
            }
            if (info != null) {
                duration = info.getDuration();
            }
        }
        reverseClip.setDuration(duration);
        if ( !isSuccess) {//本地素材视频,如果转码失败，则设置裁剪点
            reverseClip.changeTrimIn(clip.getTrimIn());
            reverseClip.changeTrimOut(clip.getTrimOut());
        }
        reverseClip.setmDurationBySpeed(clip.getmDurationBySpeed());
        reverseClip.setSpeed(clip.getSpeed());
        reverseClip.setRotateAngle(clip.getRotateAngle());
        mReverseClipList.add(reverseClip);
        mIndex++;
        if (mIndex < mClipList.size()) {
            sendConvertFileMsg();
            LoggerUtils.printLog("当前转码视频Index-->" + mIndex);
        } else {
            sendFinishConvertMsg();
            Message msg=mUIHandler.obtainMessage();
            msg.obj=mDstPath;
            msg.what=mFinishCode;
            mUIHandler.sendMessage(msg);
        }
    }

    public void onConvertDestroy() {
        if (null != mConvertHandler) {
            mConvertHandler.removeCallbacksAndMessages(null);
        }
    }
}
