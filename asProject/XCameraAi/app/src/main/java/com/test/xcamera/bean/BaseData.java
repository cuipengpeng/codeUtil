package com.test.xcamera.bean;

import com.test.xcamera.constants.StreamIOTypeConstants;
import com.test.xcamera.enumbean.StreamIOType;
import com.test.xcamera.utils.BytesUtil;

/**
 * 完整数据
 * Created by zll on 2019/5/10.
 */

public class BaseData {
    public static boolean needSave = false;
    private static final String TAG = "BaseData";
    // 协议标识
    private String mMagic = "";
    private int mVersion;
    // 协议类型 0:音视频  1:app发送的固件命令  2:固件的响应
    private StreamIOType mMsgType;
    // 数据长度
    private int mDataSize;
    // crc
    private int mCrc;
    // 视频信息
    private VideoFrameInfo mVideoFrameInfo;
    // 控制命令
    private CtrlInfo mCtrlInfo;
    // 下载
    private MoDownloadInfo mDownloadInfo;

    private AudioFrameInfo mAudioFrameInfo;

    public BaseData(byte[] head) {
        // 截取头信息
//        byte[] head = BytesUtil.subBytes(data, 0, 12);
        int m = head[0];
        int o = head[1];
        if (m == 77 && o == 79) {
            this.mMagic = "MO";
        }
//        this.mMagic = BytesUtil.byteToString(head, 0, false);
        this.mVersion = head[2];
        int type = head[3];
        this.mDataSize = BytesUtil.byteArrayToInt(head, 4, true);
        this.mCrc = BytesUtil.byteArrayToInt(head, 8, true);
        this.mMsgType = getMessageType(type);
    }

    public String getmMagic() {
        return mMagic;
    }

    public void setmMagic(String mMagic) {
        this.mMagic = mMagic;
    }

    public int getmVersion() {
        return mVersion;
    }

    public void setmVersion(int mVersion) {
        this.mVersion = mVersion;
    }

    public StreamIOType getmMsgType() {
        return mMsgType;
    }

    public void setmMsgType(StreamIOType mMsgType) {
        this.mMsgType = mMsgType;
    }

    public int getmDataSize() {
        return mDataSize;
    }

    public void setmDataSize(int mDataSize) {
        this.mDataSize = mDataSize;
    }

    public VideoFrameInfo getmVideoFrameInfo() {


        return mVideoFrameInfo;
    }

    public void setmVideoFrameInfo(VideoFrameInfo mVideoFrameInfo) {
        this.mVideoFrameInfo = mVideoFrameInfo;
    }

    public CtrlInfo getmCtrlInfo() {
        return mCtrlInfo;
    }

    public void setmCtrlInfo(CtrlInfo mCtrlInfo) {
        this.mCtrlInfo = mCtrlInfo;
    }

    public AudioFrameInfo getmAudioFrameInfo() {
        return mAudioFrameInfo;
    }

    public void setmAudioFrameInfo(AudioFrameInfo mAudioFrameInfo) {
        this.mAudioFrameInfo = mAudioFrameInfo;
    }

    public MoDownloadInfo getmDownloadInfo() {
        return mDownloadInfo;
    }

    public void setmDownloadInfo(MoDownloadInfo mDownloadInfo) {
        this.mDownloadInfo = mDownloadInfo;
    }

    /**
     * 获取消息类型（音视频or控制命令）
     *
     * @param type
     * @return
     */
    private StreamIOType getMessageType(int type) {
//        Log.d(TAG, "getMessageType: " + type);
        StreamIOType streamIOType = null;
        switch (type) {
            case StreamIOTypeConstants.STREAM_IO_TYPE_NONE:
                streamIOType = StreamIOType.STREAM_IO_TYPE_NONE;
                break;
            case StreamIOTypeConstants.STREAM_IO_TYPE_VIDEO:
                streamIOType = StreamIOType.STREAM_IO_TYPE_VIDEO;
                break;
            case StreamIOTypeConstants.STREAM_IO_TYPE_AUDIO:
                streamIOType = StreamIOType.STREAM_IO_TYPE_AUDIO;
                break;
            case StreamIOTypeConstants.STREAM_IO_TYPE_IOCTRL:
                streamIOType = StreamIOType.STREAM_IO_TYPE_IOCTRL;
                break;
            case StreamIOTypeConstants.STREAM_IO_TYPE_DOWNLOAD:
                streamIOType = StreamIOType.STREAM_IO_DOWNLOAD_FILE;
                break;
            case StreamIOTypeConstants.STREAM_IO_TRACK:
                streamIOType = StreamIOType.STREAM_IO_TRACK;
                break;
            case StreamIOTypeConstants.STREAM_ACTIVATE:
                streamIOType = StreamIOType.STREAM_ACTIVATE;
                break;
            case StreamIOTypeConstants.STREAM_OTA:
                streamIOType = StreamIOType.STREAM_OTA;
                break;
            case StreamIOTypeConstants.STREAM_HEART:
                streamIOType = StreamIOType.STREAM_HEART;
                break;
        }
        return streamIOType;
    }

    /**
     * 根据消息类型解析
     *
     * @param streamIOType
     * @param data
     */
    public void processMessage(StreamIOType streamIOType, byte[] data, int len) {
//        Log.d(TAG, "processMessage: " + streamIOType);
        switch (streamIOType) {
            case STREAM_IO_TYPE_NONE:
                break;
            case STREAM_IO_TYPE_VIDEO:
                // frame info
                mVideoFrameInfo = new VideoFrameInfo(data, mDataSize);
//                String content = mVideoFrameInfo.toString();
//                FileUtil.writeFileToSDCard(FileUtil.path, content.getBytes(), "aac.txt", true, true, false);
                break;
            case STREAM_IO_TYPE_AUDIO:
//                if (needSave) {
//                    FileUtil.writeFileToSDCard(FileUtil.path, data, "aac.txt", true, true, false);
//                }

                mAudioFrameInfo = new AudioFrameInfo(data, len);
//                Log.d(TAG, "processMessage: " + mAudioFrameInfo.toString());
                break;
            case STREAM_IO_TRACK:
            case STREAM_IO_TYPE_IOCTRL:
                mCtrlInfo = new CtrlInfo(data, mDataSize);
                break;
            case STREAM_IO_DOWNLOAD_FILE:
                mDownloadInfo = new MoDownloadInfo(data, len);
                break;
            case STREAM_ACTIVATE:
                mCtrlInfo = new CtrlInfo(data, mDataSize);
                break;
            case STREAM_OTA:
                mCtrlInfo = new CtrlInfo(data, mDataSize);
                break;
            case STREAM_HEART:
                mCtrlInfo = new CtrlInfo(data, mDataSize);
                break;
        }
    }

    @Override
    public String toString() {
        String audio = "";
        String video = "";
        String ctrl = "";
        String download = "";
        if (mAudioFrameInfo != null) {
            audio = mAudioFrameInfo.toString();
        }
        if (mVideoFrameInfo != null) {
            video = mVideoFrameInfo.toString();
        }
        if (mCtrlInfo != null) {
            ctrl = mCtrlInfo.toString();
        }
        if (mDownloadInfo != null) {
            download = mDownloadInfo.toString();
        }
        return "BaseData{" +
                "mMagic='" + mMagic + '\'' +
                ", mVersion=" + mVersion +
                ", mMsgType=" + mMsgType +
                ", mDataSize=" + mDataSize +
                ", mCrc=" + mCrc +
                ", mVideoFrameInfo=" + video +
                ", mCtrlInfo=" + ctrl +
                ", mAudioFrameInfo=" + audio +
                ", mDownloadInfo=" + download +
                '}';
    }
}
