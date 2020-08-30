package com.xiaoyi.yicamera.mp4recorder2;

import android.os.Environment;
import android.text.TextUtils;

import com.test.xcamera.player.AVFrame;

import java.io.File;
import java.util.Date;

public class Mp4V2Native {
    private final static String TAG = Mp4V2Native.class.getSimpleName();

    private static class Mp4V2NativeHolder {
        private static final Mp4V2Native instance = new Mp4V2Native();
    }

    protected enum RecordState {
        RECORD_STATE_STOP,
        RECORD_STATE_GET_INFO,
        RECORD_STATE_CREATE_FILE,
        RECORD_STATE_CREATE_TRACK,
        RECORD_STATE_WRITE_DATA
    }

//    static {
//        System.loadLibrary("mp4v2");
//        System.loadLibrary("Mp4RecorderNative");
//    }

    private Mp4V2Native() {
        //constructor
        width = 0;
        height = 0;
    }

    public static final Mp4V2Native getInstance() {
        return Mp4V2NativeHolder.instance;
    }

    private static final int INFO_PRE_START = 1001;
    private static final int INFO_POST_START = 1002;
    private static final int INFO_CANCELLED = 1003;
    private static final int INFO_PRE_STOP = 1004;
    private static final int INFO_POST_STOP = 1005;
    private static final int INFO_INFO = 2001;

    private static final int ERROR_UNKNOWN = -1001;
    private static final int ERROR_FILE_PATH_NULL = -1002;
    private static final int ERROR_CREATE_FILE_FAIL = -1003;

    private static final byte MP4_MPEG4_AUDIO_TYPE = 0x40;
    private static final byte MP4_MPEG2_AAC_LC_AUDIO_TYPE = 0x67;
    private static final int SAMPLE_DURATION = 1024;
    private static final int AUDIO_SAMPLE_RATE_8K = 8000;
    private static final int AUDIO_SAMPLE_RATE_16K = 16000;


    private String filePath;

    private int width;//视频宽度

    private int height;//视频高度

    private int frameRate;//帧率

    private short lastReceivedFrameNo;

    private boolean isRecording = false;

    private boolean isGetVideoInfo;

    private MP4RecordCallback mp4RecordCallback;

    private onRecordListener recordListener;

    private RecordState RECORD_STATE_VIDEO;

    private RecordState RECORD_STATE_AUDIO;

    private AVRecordFrame avVideoFrame;

    private AVRecordFrame avAudioFrame;

    //testMethod用来测试jni,不要随意调用这个函数,不然内部状态会不对
    public native String testMethod();

    private native boolean init();

    private native boolean createMP4File();

    private native boolean createMP4VideoTrack();

    private native boolean getVideoInfo(byte[] videoBytes, int length);

    private native boolean writeVideoToFile(byte[] videoBytes, int length);

    private native boolean createMP4AudioTrack(int timeScale, int duration, byte audioType);

    private native boolean getAudioInfo(byte[] audioBytes, int length, int offset);

    private native boolean writeAudioToFile(byte[] audioBytes, int length);

    private native boolean destroy();

    public String getFilePath() {
        return filePath;
    }

    public boolean isRecording() {
        return isRecording;
    }

    public void setRecording(boolean recording) {
        isRecording = recording;
    }

    public interface onRecordListener {
        void onPreStart(String inFilePath);

        void onPostStart();

        void onCancelled();

        void onRecordInfo(int code);

        void onRecordError(int code);

        void onPreStop();

        void onPostStop(String outFilePath);
    }

    public interface MP4RecordCallback {
        void onSuccess(String fileName);

        void onFailed(String fileName);
    }

    private class AVRecordFrame {
        public int frameInterval;
        public boolean isDataValid;
        public String deviceType;
        public AVFrame avFrame;

        public AVRecordFrame(AVFrame avFrame, int frameInterval, boolean isDataValid, String deviceType) {
            this.avFrame = avFrame;
            this.frameInterval = frameInterval;
            this.isDataValid = isDataValid;
        }

        public AVRecordFrame(AVFrame avFrame) {
            this.avFrame = avFrame;
            this.frameInterval = 0;
            this.isDataValid = false;
            this.deviceType = null;
        }

        public AVRecordFrame() {
            this.avFrame = null;
            this.deviceType = null;
            this.frameInterval = 0;
            this.isDataValid = false;
        }
    }

    public void startRecord(String path) {

        if (TextUtils.isEmpty(path)) {
            String sdStatus = Environment.getExternalStorageState();
            if (sdStatus.equals(Environment.MEDIA_MOUNTED)) {
                File sdDir = Environment.getExternalStorageDirectory();
                if (sdDir.exists()) {
                    Date time = new Date();
                    filePath = Environment.getExternalStorageDirectory() + File.separator + time.getTime() + ".mp4";
                } else {
                    filePath = null;
                }
            } else {
                filePath = null;
            }
        } else {
            filePath = path;
        }

        width = 0;
        height = 0;
        frameRate = 0;

        isRecording = true;
        RECORD_STATE_VIDEO = RecordState.RECORD_STATE_GET_INFO;
        RECORD_STATE_AUDIO = RecordState.RECORD_STATE_CREATE_TRACK;
        init();
    }

    public String stopRecord() {
        isRecording = false;
        RECORD_STATE_VIDEO = RecordState.RECORD_STATE_STOP;
        RECORD_STATE_AUDIO = RecordState.RECORD_STATE_STOP;
        avVideoFrame = null;
        avAudioFrame = null;

        destroy();
        if (!TextUtils.isEmpty(filePath)) {
            return filePath;
        } else {
            return null;
        }
    }

    public boolean deleteTrashFile() {
        if (!TextUtils.isEmpty(filePath)) {
            File file = new File(filePath);
            if (file != null) {
                if (file.isFile() && file.exists()) {
                    return file.delete();
                }
                return true;
            }
            return true;
        }
        return true;
    }


    public void recordVideoFrame(AVFrame avFrame,  int mSpeedMode) {
        if (!isRecording) {
        }
        if (avVideoFrame == null) {
            avVideoFrame = new AVRecordFrame();
        }

        if ((avVideoFrame != null) && (avVideoFrame.isDataValid == true)) {
            try {
                long intervalMSec = (avFrame.getTimeStamp() * 1000 + avFrame.getTimestamp_ms()) - (avVideoFrame.avFrame.getTimeStamp() * 1000 + avVideoFrame.avFrame.getTimestamp_ms());

                if (intervalMSec > 0) {
                    avVideoFrame.frameInterval = (int) intervalMSec;
                } else {
                    avVideoFrame.frameInterval = 0;
                }
                recordVideoFrame(avVideoFrame.avFrame, avVideoFrame.deviceType, avVideoFrame.frameInterval,30, mSpeedMode);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        if (avVideoFrame != null) {
            avVideoFrame.avFrame = avFrame;
            avVideoFrame.isDataValid = true;
        }
    }

    public void recordVideoFrame(AVFrame avFrame, String deviceType, int interval, int frameRate, int mSpeedMode) {
        if (!isRecording) {
        }

        if ((width != avFrame.getVideoWidth()) || (height != avFrame.getVideoHeight())) {
            if (width != 0 && height != 0) {//发起时无需重置,在录制中发生分辨率改变才重置
                resetRecord();
            }
            width = avFrame.getVideoWidth();
            height = avFrame.getVideoHeight();
            this.frameRate = 20;

            if(frameRate != 0){
                this.frameRate = frameRate;
            }
            if(mSpeedMode == 8){
                this.frameRate *= 2;
            }
        }

        if (RECORD_STATE_VIDEO == RecordState.RECORD_STATE_GET_INFO) {
            isGetVideoInfo = false;
            isGetVideoInfo = getVideoInfo(avFrame.frmData, avFrame.frmData.length);
            if (isGetVideoInfo) {
                RECORD_STATE_VIDEO = RecordState.RECORD_STATE_CREATE_FILE;
            }
        }

        if (RECORD_STATE_VIDEO == RecordState.RECORD_STATE_CREATE_FILE) {
            if ((!TextUtils.isEmpty(filePath)) && (createMP4File() == true)) {
                RECORD_STATE_VIDEO = RecordState.RECORD_STATE_CREATE_TRACK;
            }
        }

        if (RECORD_STATE_VIDEO == RecordState.RECORD_STATE_CREATE_TRACK) {
            if (createMP4VideoTrack() == true) {
                RECORD_STATE_VIDEO = RecordState.RECORD_STATE_WRITE_DATA;
            }
        }

        if (RECORD_STATE_VIDEO == RecordState.RECORD_STATE_WRITE_DATA) {
            if (writeVideoToFile(avFrame.frmData, interval) == false) {
            }
        }
    }

    public void recordAudioFrame(AVFrame avFrame, String deviceType) {
        if (isRecording && (avFrame != null)) {
            if (avAudioFrame == null) {
                avAudioFrame = new AVRecordFrame();
            }

            if ((avAudioFrame != null) && (avAudioFrame.isDataValid == true)) {
                long intervalMSec = ((avFrame.getTimeStamp() * 1000 + avFrame.getTimestamp_ms()) - (avAudioFrame.avFrame.getTimeStamp() * 1000 + avAudioFrame.avFrame.getTimestamp_ms()));
                if (intervalMSec > 0) {
                    avAudioFrame.frameInterval = (int) intervalMSec;
                } else {
                    avAudioFrame.frameInterval = 0;
                }
                recordAudioFrame(avAudioFrame.avFrame, avAudioFrame.deviceType, avAudioFrame.frameInterval);
            }

            if (avAudioFrame != null) {
                avAudioFrame.avFrame = avFrame;
                avAudioFrame.isDataValid = true;
                avAudioFrame.deviceType = deviceType;
            }
        }
    }

    public void recordAudioFrame(AVFrame avFrame, String deviceType, int interval) {
        if (isRecording && (avFrame != null)) {
            if (RECORD_STATE_AUDIO == RecordState.RECORD_STATE_CREATE_TRACK) {
                int sampleRate = AUDIO_SAMPLE_RATE_16K;

                if (createMP4AudioTrack(sampleRate, SAMPLE_DURATION, MP4_MPEG4_AUDIO_TYPE) == true) {
                    RECORD_STATE_AUDIO = RecordState.RECORD_STATE_GET_INFO;
                }
            }

            if (RECORD_STATE_AUDIO == RecordState.RECORD_STATE_GET_INFO) {
                if (getAudioInfo(avFrame.frmData, avFrame.frmData.length, 0) == true) {
                    RECORD_STATE_AUDIO = RecordState.RECORD_STATE_WRITE_DATA;
                }
            }

            if (RECORD_STATE_AUDIO == RecordState.RECORD_STATE_WRITE_DATA) {
                if (writeAudioToFile(avFrame.frmData, interval) == true) {
                } else {
                }
            }
        }
    }

    private void resetRecord() {
        destroy();
        init();
        RECORD_STATE_VIDEO = RecordState.RECORD_STATE_GET_INFO;
        RECORD_STATE_AUDIO = RecordState.RECORD_STATE_CREATE_TRACK;
    }

    private void nativeCallBack(int msg, int arg, String extraStr) {
        switch (msg) {
            case INFO_PRE_START:
                if (recordListener != null) {
                    recordListener.onPreStart(extraStr);
                }
                break;
            case INFO_POST_START:
                if (recordListener != null) {
                    recordListener.onPostStart();
                }
                break;
            case INFO_CANCELLED:
                if (recordListener != null) {
                    recordListener.onCancelled();
                }
                break;
            case INFO_PRE_STOP:
                if (recordListener != null) {
                    recordListener.onPreStop();
                }
                break;
            case INFO_POST_STOP:
                if (recordListener != null) {
                    recordListener.onPostStop(extraStr);
                }
                break;
            case ERROR_UNKNOWN:
                if (recordListener != null) {
                    recordListener.onRecordError(arg);
                }
                break;
            case INFO_INFO:
                if (recordListener != null) {
                    recordListener.onRecordInfo(arg);
                }
                break;
            case ERROR_FILE_PATH_NULL:
                if (recordListener != null) {
                    recordListener.onRecordError(arg);
                }
                break;
            default:
                break;
        }
    }
}
