package com.test.xcamera.managers;

import android.graphics.Rect;
import android.os.Environment;
import android.util.Log;

import com.test.xcamera.accrssory.AccessoryManager;
import com.test.xcamera.bean.MoRange;
import com.test.xcamera.constants.CtrlCmdConstants;
import com.test.xcamera.constants.LogcatConstants;
import com.test.xcamera.mointerface.MoDownloadCallback;
import com.test.xcamera.utils.BytesUtil;
import com.test.xcamera.utils.FileUtil;
import com.test.xcamera.utils.LoggerUtils;
import com.moxiang.common.logging.Logcat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 命令
 * Created by zll on 2019/5/22.
 */

public class CommandManager {
    private static final String TAG = "CommandManager";
    private static CommandManager singleton = null;
    private static Object lock = new Object();
    private byte[] mTakePhoto, mStartRecord, mStopRecord;
    public byte[] mHeart;

    public static CommandManager getInstance() {
        synchronized (lock) {
            if (singleton == null) {
                singleton = new CommandManager();
            }
        }
        return singleton;
    }

    public CommandManager() {
//        takePhoto();
//        startRecord();
//        stopRecord();
    }

    /**
     * 拍照
     *
     * @return
     */
    public byte[] takePhoto() {
        long time = System.nanoTime();
        if (mTakePhoto == null) {
            byte[] cmd = BytesUtil.shortToByteArray(CtrlCmdConstants.STREAM_IO_CTRL_CMD_TAKE_PHOTO, true);
            byte[] cmdSeq = BytesUtil.shortToByteArray(CtrlCmdConstants.STREAM_IO_CTRL_CMD_SEQ, true);
            byte[] respRet = BytesUtil.shortToByteArray(CtrlCmdConstants.STREAM_IO_CTRL_RESP_RET, true);
            byte[] headerSize = BytesUtil.shortToByteArray(CtrlCmdConstants.STREAM_IO_CTRL_HEADER_SIZE, true);
            byte[] ctrlDataSize = BytesUtil.intToByteArray(CtrlCmdConstants.STREAM_IO_CTRL_DATA_SIZE, true);
//            mTakePhoto = BytesUtil.byteMergerAll(getHead(), cmd, cmdSeq, respRet, headerSize, ctrlDataSize);
        }
        Log.d(TAG, "takePhoto: use time = " + (System.nanoTime() - time));
        return mTakePhoto;
    }

    /**
     * 开始录视频
     *
     * @return
     */
    public byte[] startRecord() {
        long time = System.nanoTime();
        if (mStartRecord == null) {
            byte[] cmd = BytesUtil.shortToByteArray(CtrlCmdConstants.STREAM_IO_CTRL_CMD_START_RECORD, true);
            byte[] cmdSeq = BytesUtil.shortToByteArray(CtrlCmdConstants.STREAM_IO_CTRL_CMD_SEQ, true);
            byte[] respRet = BytesUtil.shortToByteArray(CtrlCmdConstants.STREAM_IO_CTRL_RESP_RET, true);
            byte[] headerSize = BytesUtil.shortToByteArray(CtrlCmdConstants.STREAM_IO_CTRL_HEADER_SIZE, true);
            byte[] ctrlDataSize = BytesUtil.intToByteArray(CtrlCmdConstants.STREAM_IO_CTRL_DATA_SIZE, true);
//            mStartRecord = BytesUtil.byteMergerAll(getHead(), cmd, cmdSeq, respRet, headerSize, ctrlDataSize);
        }
        Log.d(TAG, "startRecord: use time = " + (System.nanoTime() - time));
        return mStartRecord;
    }

    /**
     * 结束录视频
     *
     * @return
     */
    public byte[] stopRecord() {
        if (mStopRecord == null) {
            byte[] cmd = BytesUtil.shortToByteArray(CtrlCmdConstants.STREAM_IO_CTRL_CMD_END_RECORD, true);
            byte[] cmdSeq = BytesUtil.shortToByteArray(CtrlCmdConstants.STREAM_IO_CTRL_CMD_SEQ, true);
            byte[] respRet = BytesUtil.shortToByteArray(CtrlCmdConstants.STREAM_IO_CTRL_RESP_RET, true);
            byte[] headerSize = BytesUtil.shortToByteArray(CtrlCmdConstants.STREAM_IO_CTRL_HEADER_SIZE, true);
            byte[] ctrlDataSize = BytesUtil.intToByteArray(CtrlCmdConstants.STREAM_IO_CTRL_DATA_SIZE, true);
//            mStopRecord = BytesUtil.byteMergerAll(getHead(), cmd, cmdSeq, respRet, headerSize, ctrlDataSize);
        }
        return mStopRecord;
    }

    /**
     * 跟踪
     *
     * @param msg_id
     * @return
     */
    public byte[] startTrack(int msg_id) {
        JSONObject object = getBaseObject("get_tracking_objects", msg_id);
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * 停止跟踪
     *
     * @param msg_id
     * @return
     */
    public byte[] stopTrack(int msg_id) {
        JSONObject object = getBaseObject("stop_tracking", msg_id);
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    public static String filePath = Environment.getExternalStorageDirectory().getPath() + "/MoTest";

    /**
     * 云台控制
     *
     * @param x
     * @param y
     * @param msg_id
     * @return
     */
    public byte[] send_director(int[] x, int[] y, int msg_id) {
        JSONObject object = getBaseObject("navi_control", msg_id);
        try {
            JSONArray arr = new JSONArray();
            JSONObject object1 = new JSONObject();
            object1.put("direction", x[0]);
            object1.put("length", x[1]);
            JSONObject object2 = new JSONObject();
            object2.put("direction", y[0]);
            object2.put("length", y[1]);
            arr.put(object1).put(object2);
            object.put("direction", arr);
        } catch (JSONException e) {
//            FileUtil.writeFileToSDCard(filePath, "exception.txt", ExceptionUtil.getExceptionDetail(e).getBytes());
            e.printStackTrace();
        }
        System.out.println("获取的json。。" + object);
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * 跟踪物体
     *
     * @param msgId     消息自增id
     * @param object_id 跟踪物体id
     * @return
     */
    public byte[] startLocationObject(int msgId, int object_id) {
        JSONObject object = getBaseObject("start_tracking", msgId);
        try {
            object.put("object_id", object_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;

    }

    /**
     * 开启播放uri
     *
     * @param msgId
     * @param uri
     */
    public byte[] start_play(int msgId, String uri) {
        JSONObject object = getBaseObject("playback_start", msgId);
        try {
            object.put("uri", uri);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            object.put("start_time", 0);
        } catch (Exception e) {
        }
        try {
            object.put("type", 0);
        } catch (Exception e) {
        }
        Log.d(TAG, "start_play: object = " + object.toString());
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * 走回放接口 但是是下载流程
     *
     * @param msgId
     * @param uri   type : 0  正常回放   1  download
     * @return
     */
    public byte[] start_play_download(int msgId, String uri, long time) {
        JSONObject object = getBaseObject("playback_start", msgId);
        try {
            object.put("uri", uri);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            object.put("start_time", time);
        } catch (Exception e) {
        }
        try {
            object.put("type", 1);
        } catch (Exception e) {
        }
        Log.d(TAG, "start_play: object = " + object.toString());
        byte[] content = object.toString().getBytes();
        int size = content.length;
        Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("download file start cmd and msg id cmd = :"+object.toString()).out();
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * 停止播放
     *
     * @param msgID
     * @return
     */
    public byte[] stopPlay(int msgID) {
        JSONObject object = getBaseObject("playback_stop", msgID);
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * 暂停播放
     *
     * @param msgID
     * @return
     */
    public byte[] pausePlay(int msgID) {
        JSONObject object = getBaseObject("playback_pause", msgID);
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * 恢复播放
     *
     * @param msgID
     * @return
     */
    public byte[] resumePlay(int msgID) {
        JSONObject object = getBaseObject("playback_resume", msgID);
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * 播放指定的时间
     *
     * @param msgID
     * @param mills
     * @return
     */
    public byte[] seekPlay(int msgID, long mills) {
        JSONObject object = getBaseObject("playback_seek", msgID);
        try {

            object.put("start_time", mills);
        } catch (Exception e) {
        }
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * 开始预览
     *
     * @return
     */
    public byte[] startPreview(int msgID) {
        JSONObject object = getBaseObject("start_preview", msgID);
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * 结束预览
     *
     * @return
     */
    public byte[] stopPreview(int msgID) {
        JSONObject object = getBaseObject("stop_preview", msgID);
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * 切换拍照模式
     *
     * @param type photo:拍照  video:视频
     * @return
     */
    public byte[] switchMode(String type, int msgID) {
        JSONObject object = getBaseObject("switch_mode", msgID);
        try {
            object.put("type", type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * 拍照
     *
     * @return
     */
    public byte[] takePhoto2(int msgID) {
        JSONObject object = getBaseObject("take_photo", msgID);
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * 设置照片拍照参数
     *
     * @param proportion 1.16:9  2.4:3  3.1:1
     * @return
     */
    public byte[] takePhotoSetting(int proportion, int msgID) {
        JSONObject object = getBaseObject("take_photo_setting", msgID);
        try {
            object.put("proportion", proportion);
        } catch (Exception e) {
            e.printStackTrace();
        }
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * 开始录制视频
     *
     * @return
     */
    public byte[] takeVideoStart(int msgID) {
        JSONObject object = getBaseObject("take_video_start", msgID);
        Log.d(TAG, "takeVideoStart: " + object.toString());
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * 结束录制视频
     *
     * @return
     */
    public byte[] takeVideoStop(int msgID) {
        JSONObject object = getBaseObject("take_video_stop", msgID);
        Log.d(TAG, "takeVideoStop: " + object.toString());
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * 设置视频拍摄参数
     *
     * @param resolution 1:4K  2:2K  3:1080P
     * @param fps        1:30  2:60  3:120  4:240
     * @return
     */
    public byte[] takeVideoSetting(int resolution, int fps, int msgID) {
        JSONObject object = getBaseObject("take_video_setting", msgID);
        try {
            object.put("resolution", resolution);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            object.put("fps", fps);
        } catch (Exception e) {
            e.printStackTrace();
        }
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * 获取相册列表
     *
     * @return
     */
    public byte[] albumList(int msgID, int page) {
        JSONObject object = getBaseObject("album_list", msgID);
        try {
            object.put("page", page);
        } catch (Exception e) {
        }
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * 获取系统信息
     *
     * @return
     */
    public byte[] syncSystemInfo(int msgID) {
        JSONObject object = getBaseObject("sync_system_info", msgID);
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * 云台控制
     *
     * @param direction 0:up  1:left  2:down  3:right
     * @return
     */
    public byte[] naviControl(int direction, int msgID) {
        JSONObject object = getBaseObject("navi_control", msgID);
        try {
            object.put("direction", direction);
        } catch (Exception e) {
            e.printStackTrace();
        }
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * Mark视频
     *
     * @param videoUri  视频的地址
     * @param timeStamp 添加mark的时间
     * @return
     */
    public byte[] markVideo(String videoUri, long timeStamp, int msgID) {
        JSONObject object = getBaseObject("mark_video", msgID);
        try {
            object.put("video_uri", videoUri);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            object.put("timestamp", timeStamp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * 滤镜开关
     *
     * @param title 滤镜名称
     * @return
     */
    public byte[] selectFilter(String title, int msgID) {
        JSONObject object = getBaseObject("select_filter", msgID);
        try {
            object.put("title", title);
        } catch (Exception e) {
            e.printStackTrace();
        }
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * 美颜开关
     *
     * @param value 0:off  1:on
     * @return
     */
    public byte[] switchBeauty(int value, int msgID) {
        JSONObject object = getBaseObject("switch_beauty", msgID);
        try {
            object.put("switch", value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    public void downloadSingleFile(String uri, long totalSize, MoDownloadCallback moDownloadCallback) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("action", "high_speed_download");
            jsonObject.put("msg_id", msgID);
            jsonObject.put("uri", uri);
            jsonObject.put("size", totalSize);
            jsonObject.put("pkg_len", 1024*1024);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LoggerUtils.printLog("send request = "+jsonObject.toString());
        byte[] content = jsonObject.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);

        MoDownloadHandler.getInstance().addDownloadCommand(uri, moDownloadCallback);
        AccessoryManager.getInstance().sendCommand(cmd);
    }

    public byte[]  stopDownloadSingleFile() {
        int msgID = CommandIDManager.getInstance().getMsgID();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("action", "high_speed_download_stop");
            jsonObject.put("msg_id", msgID);

        } catch (Exception e) {
            e.printStackTrace();
        }
        LoggerUtils.printLog("send request = "+jsonObject.toString());
        byte[] content = jsonObject.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * 下载
     *
     * @param uri   文件uri
     * @param range 下载该文件的范围
     * @return
     */
    public byte[] downloadFile(String uri, MoRange range, int msgID) {
        JSONObject object = getBaseObject("download_file", msgID);
        try {
            object.put("uri", uri);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            object.put("range", range.toJsonObject());
        } catch (Exception e) {
            e.printStackTrace();
        }
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * 更新相机朝向
     *
     * @param direction 0:0  1:90  2:180  3:270
     * @return
     */
    public byte[] updateDirection(int direction, int msgID) {
        JSONObject object = getBaseObject("update_direction", msgID);
        try {
            object.put("direction", direction);
        } catch (Exception e) {
        }
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * 设置照片格式
     *
     * @param type  0:jpeg  1:j+R
     * @param msgID
     * @return
     */
    public byte[] setPhotoFormat(int type, int msgID) {
        JSONObject object = getBaseObject("format_setting", msgID);
        try {
            object.put("type", type);
        } catch (Exception e) {
        }
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * 删除照片或视频
     *
     * @param uris  文件的uri
     * @param msgID
     * @return
     */
    public byte[] deleteMedia(ArrayList<String> uris, int msgID) {
        JSONObject object = getBaseObject("delete_media", msgID);
        JSONArray uriArray = null;
        try {
            if (uris != null && uris.size() > 0) {
                uriArray = new JSONArray();
                for (int i = 0; i < uris.size(); i++) {
                    uriArray.put(uris.get(i));
                }
                object.put("count", uris.size());
                object.put("uri", uriArray);
            }
        } catch (Exception e) {
        }
        byte[] content = object.toString().getBytes();
        FileUtil.writeLog(content);
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * 抗闪烁开关
     *
     * @param enable 0:打开  1:关闭
     * @param freq
     * @param msgID
     * @return
     */
    public byte[] setAntiFicker(int enable, int freq, int msgID) {
        JSONObject object = getBaseObject("anti_flicker_setting", msgID);
        try {
            object.put("enable", enable);
        } catch (Exception e) {
        }
        try {
            object.put("freq", freq);
        } catch (Exception e) {
        }
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * 画面翻转
     *
     * @param type  0:后置拍照  1:自拍
     * @param msgID
     * @return
     */
    public byte[] setSelfie(int type, int msgID) {
        JSONObject object = getBaseObject("selfie", msgID);
        try {
            object.put("type", type);
        } catch (Exception e) {
        }
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;

    }

    /**
     * 3.3.17.获取可跟踪的物体
     *
     * @param status 0：关 1：开
     * @param msgID
     * @return
     */
    public byte[] getTracking(int status, int msgID) {
        JSONObject object = getBaseObject("get_tracking_objects", msgID);
        try {
            object.put("status", status);
        } catch (Exception e) {
        }
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;

    }

    /**
     * 画框跟踪
     *
     * @param msgID
     * @param rect          跟踪框的x,y,width,height
     * @param previewWidth  缩放后的预览宽
     * @param previewHeight 缩放后的预览高
     * @param bgr           当前帧的bgr数据
     * @return
     */
    public byte[] trackRect(int msgID, Rect rect, int previewWidth, int previewHeight, byte[] bgr) {
//        Log.e("=====", "trackRect: msgID = " + msgID + " values = " + rect.toString() + " previewWidth = " + previewWidth +
//                " previewHeight = " + previewHeight + " bgr = " + bgr.length);
        byte[] crc = BytesUtil.intToByteArray(0, true);

        byte[] msgId = BytesUtil.intToByteArray(msgID, false);
        byte[] preWidth = BytesUtil.intToByteArray(previewWidth, false);
        byte[] preHeight = BytesUtil.intToByteArray(previewHeight, false);
        byte[] x = BytesUtil.intToByteArray(rect.left, false);
        byte[] y = BytesUtil.intToByteArray(rect.top, false);
        byte[] width = BytesUtil.intToByteArray((rect.right - rect.left), false);
        byte[] height = BytesUtil.intToByteArray((rect.bottom - rect.top), false);
        byte[] format = new byte[]{0};
        byte[] size = BytesUtil.intToByteArray(bgr.length, false);
        byte[] trackData = BytesUtil.byteMergerAll(msgId, preWidth, preHeight, x, y, width, height, format, size, bgr);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(5, trackData.length), trackData, crc);
        return cmd;
    }

    /**
     * 获取设备信息
     *
     * @param msgID
     * @return
     */
    public byte[] getDeviceInfo(int msgID) {
        JSONObject object = getBaseObject("sync_device_info", msgID);
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * 同步拍摄设置
     *
     * @param msgID
     * @return
     */
    public byte[] syncShotSetting(int msgID) {
        JSONObject object = getBaseObject("sync_setting", msgID);
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * 延时摄影设置
     *
     * @param msgID
     * @param frame 抽帧
     * @return
     */
    public byte[] lapseRecordSetting(int msgID, float frame) {
        JSONObject object = getBaseObject("lapse_record_setting", msgID);
        try {
            object.put("frame", frame);
        } catch (Exception e) {
        }
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * 慢动作设置
     *
     * @param msgID
     * @param speed 倍速
     * @return
     */
    public byte[] slowMotionSetting(int msgID, int speed) {
        JSONObject object = getBaseObject("slow_motion_setting", msgID);
        try {
            object.put("speed", speed);
        } catch (Exception e) {
        }
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * 云台速度控制
     *
     * @param msgID
     * @param speed 0:灵敏  1:柔和
     * @return
     */
    public byte[] speedSetting(int msgID, int speed) {
        JSONObject object = getBaseObject("speed_setting", msgID);
        try {
            object.put("speed", speed);
        } catch (Exception e) {
        }
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * 数码变焦
     *
     * @param msgID
     * @param scale 变焦倍率（1-5）
     * @return
     */
    public byte[] digitalZoom(int msgID, int scale) {
        JSONObject object = getBaseObject("digital_zoom", msgID);
        try {
            object.put("scale", scale);
        } catch (Exception e) {
        }
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * 畸变校正
     *
     * @param msgID
     * @param type  0:normal  1:wide
     * @return
     */
    public byte[] distortionCorrection(int msgID, int type) {
        JSONObject object = getBaseObject("distortion_correction", msgID);
        try {
            object.put("type", type);
        } catch (Exception e) {
        }
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * 设置拍照倒计时
     *
     * @param msgID
     * @param time  倒计时 3s  5s  7s
     * @return
     */
    public byte[] setDelayTime(int msgID, int time) {
        JSONObject object = getBaseObject("delay_time_setting", msgID);
        try {
            object.put("delay_time", time);
        } catch (Exception e) {
        }
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * 请求解禁状态
     *
     * @param msgID
     * @return
     */
    public byte[] getActivateStatue(int msgID) {
        JSONObject object = getBaseObject("sync_activate_status", msgID);
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(6, size), content, crc);
        return cmd;
    }

    /**
     * 请求did
     *
     * @param msgID
     * @return
     */
    public byte[] getDidInfo(int msgID) {
        JSONObject object = getBaseObject("sync_did", msgID);
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(6, size), content, crc);
        return cmd;
    }

    /**
     * 请求写入激活
     *
     * @param msgID
     * @param activeCode 激活码
     * @param activeID   激活ID
     * @return
     */
    public byte[] activateDevice(int msgID, String activeCode, String activeID) {
        JSONObject object = getBaseObject("activate_device", msgID);
        try {
            object.put("activate_code", activeCode);
            object.put("active_id", activeID);
        } catch (Exception e) {
        }
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(6, size), content, crc);
        return cmd;
    }

    /**
     * 请求修改激活状态
     *
     * @param msgID
     * @return
     */
    public byte[] modifyStatus(int msgID, int status) {
        JSONObject object = getBaseObject("modify_activate_status", msgID);
        try {
            object.put("status", status);
        } catch (Exception e) {
        }
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(6, size), content, crc);
        return cmd;
    }

    /**
     * 升级条件校验
     *
     * @param msgID
     * @return
     */
    public byte[] uploadCheckcond(String version, int msgID) {
        JSONObject object = getBaseObject("upload_checkcond", msgID);
        try {
            object.put("version", version);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * 开始下载
     *
     * @param msgID
     * @return
     */
    public byte[] startUpload(int msgID, String filename) {
        JSONObject object = getBaseObject("upload_start", msgID);
        try {
            object.put("filename", filename);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * 下载数据
     *
     * @param msgID
     * @param pkgName 文件名
     * @param total   文件总长度
     * @param pkgSize 当前包的长度
     * @param data    当前包的数据
     * @return
     */
    public byte[] uploadData(int msgID, String pkgName, int total, int pkgSize, byte[] data) {
        byte[] id = BytesUtil.intToByteArray(msgID, false);
//        byte[] name = pkgName.getBytes();

        byte[] allLength = BytesUtil.intToByteArray(total, false);
        byte[] currentLength = BytesUtil.intToByteArray(pkgSize, false);
        byte[] finalData = BytesUtil.byteMergerAll(id, allLength, currentLength, data);
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] upload = BytesUtil.byteMergerAll(getHead(7, finalData.length), finalData, crc);
        return upload;
    }

    /**
     * 下载结束
     *
     * @param msgID
     * @return
     */
    public byte[] uploadFinish(int msgID) {
        JSONObject object = getBaseObject("upload_finish", msgID);
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * 升级
     *
     * @param msgID
     * @return
     */
    public byte[] upgrade(int msgID) {
        JSONObject object = getBaseObject("upgrade", msgID);
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * 获取精彩推荐数据
     *
     * @param msgID
     * @return
     */
    public byte[] getVideoByDate(int page, int msgID) {
        JSONObject object = getBaseObject("get_video_bydate", msgID);
        try {
            object.put("page", page);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LoggerUtils.printLog("send request = "+object.toString());
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * 获取素材片段
     *
     * @param msgID
     * @return
     */
    public byte[] getVideoSegment(long date, int msgID) {
        JSONObject object = getBaseObject("get_video_segment", msgID);
        try {
            object.put("date", date);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * 获取侧面键一键成片数据
     *
     * @param page
     * @param msgID
     * @return
     */
    public byte[] getVideoSideKey(int page, int msgID) {
        JSONObject object = getBaseObject("get_video_sidekey", msgID);
        try {
            object.put("page", page);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * 同步时间
     *
     * @param time  毫秒
     * @param msgID
     * @return
     */
    public byte[] syncTime(long time, int msgID) {
        JSONObject object = getBaseObject("sync_time", msgID);
        try {
            object.put("time", time);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * 获取侧面键一键成片素材片段
     *
     * @param video_icon
     * @param msgID
     * @return
     */
    public byte[] getVideoSegmentSideKey(String video_icon, int msgID) {
        JSONObject object = getBaseObject("get_video_segment_sidekey", msgID);
        try {
            object.put("video_icon", video_icon);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * 超高画质开关
     *
     * @param msgID
     * @param enable 0:打开  1:关闭
     * @return
     */
    public byte[] setSuperHighQuality(int msgID, int enable) {
        JSONObject object = getBaseObject("super_high_quality", msgID);
        try {
            object.put("value", enable);
        } catch (Exception e) {
        }
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * iso设置
     *
     * @param msgID
     * @param value 范围值
     * @return
     */
    public byte[] setISOValue(int msgID, int value) {
        JSONObject object = getBaseObject("iso_setting", msgID);
        try {
            object.put("value", value);
        } catch (Exception e) {
        }
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * 快门速度
     *
     * @param msgID
     * @param value 范围值
     * @return
     */
    public byte[] setShutter(int msgID, int value) {
        JSONObject object = getBaseObject("shutter_setting", msgID);
        try {
            object.put("value", value);
        } catch (Exception e) {
        }
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * EV设置
     *
     * @param msgID
     * @param value 范围值
     * @return
     */
    public byte[] setEV(int msgID, int value) {
        JSONObject object = getBaseObject("ev_setting", msgID);
        try {
            object.put("value", value);
        } catch (Exception e) {
        }
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * 白平衡
     *
     * @param msgID
     * @param value 范围值
     * @return
     */
    public byte[] setWhiteBalance(int msgID, int value) {
        JSONObject object = getBaseObject("white_balance_setting", msgID);
        try {
            object.put("value", value);
        } catch (Exception e) {
        }
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * 色彩设置
     *
     * @param msgID
     * @param value 范围值
     * @return
     */
    public byte[] setColor(int msgID, int value) {
        JSONObject object = getBaseObject("color_setting", msgID);
        try {
            object.put("value", value);
        } catch (Exception e) {
        }
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * 格式设置
     *
     * @param msgID
     * @param value 照片模式 0:Jpeg 2:J+R
     *              录像模式 0:mp4 1:mov
     * @return
     */
    public byte[] setFormat(int msgID, int value) {
        JSONObject object = getBaseObject("format_setting", msgID);
        try {
            object.put("type", value);
        } catch (Exception e) {
        }
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * 云台速度设置
     *
     * @param msgID
     * @param value 0:灵敏  1:柔和
     * @return
     */
    public byte[] setSpeed(int msgID, int value) {
        JSONObject object = getBaseObject("speed_setting", msgID);
        try {
            object.put("speed", value);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * 云台模式设置
     *
     * @param msgID
     * @param value 0: lock roll
     *              1: Lock Pitch&Roll
     *              2: FPV
     *              3: 全Lock
     * @return
     */
    public byte[] setModel(int msgID, int value) {
        JSONObject object = getBaseObject("model_setting", msgID);
        try {
            object.put("mode", value);
        } catch (Exception e) {
        }
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    public byte[] setExposureModel(int msgID, int value) {
        JSONObject object = getBaseObject("exposure_mode_setting", msgID);
        try {
            object.put("mode", value);
        } catch (Exception e) {
        }
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    public byte[] setAwbModel(int msgID, int value) {
        JSONObject object = getBaseObject("awb_mode_setting", msgID);
        try {
            object.put("mode", value);
        } catch (Exception e) {
        }
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * 电子防抖
     *
     * @param msgID
     * @param value 0:打开  1:关闭
     * @return
     */
    public byte[] setDis(int msgID, int value) {
        JSONObject object = getBaseObject("dis_setting", msgID);
        try {
            object.put("value", value);
        } catch (Exception e) {
        }
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * 设置曝光
     *
     * @param msgID
     * @param value 0:auto  1:manual
     * @return
     */
    public byte[] setExplore(int msgID, int value) {
        JSONObject object = getBaseObject("exposure_setting", msgID);
        try {
            object.put("value", value);
        } catch (Exception e) {
        }
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * 设置长曝光
     *
     * @param msgID
     * @param value 单位 秒
     * @return
     */
    public byte[] setLongExplore(int msgID, float value) {
        JSONObject object = getBaseObject("long_exposure_setting", msgID);
        try {
            long v = (long) (value * 1000 * 1000);
            object.put("exposure_duration", v);
        } catch (Exception e) {
        }
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * 设置白平衡
     *
     * @param msgID
     * @param value 0:auto  1:manual
     * @return
     */
    public byte[] setAwb(int msgID, int value) {
        JSONObject object = getBaseObject("awb_setting", msgID);
        try {
            object.put("value", value);
        } catch (Exception e) {
        }
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * 云台回中
     *
     * @param msgID
     * @return
     */
    public byte[] setPtzBack(int msgID) {
        JSONObject object = getBaseObject("ptz_back_center", msgID);
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * 设置HDR/WDR
     *
     * @param msgID
     * @param value 视频wdr 0:打开 1:关闭
     *              照片hdr 0:打开 1:关闭
     * @return
     */
    public byte[] setDynamicRange(int msgID, int value) {
        JSONObject object = getBaseObject("dynamic_range_setting", msgID);
        try {
            object.put("value", value);
        } catch (Exception e) {
        }
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * 获取相机相册总数
     *
     * @param msgID
     * @return
     */
    public byte[] getAlbumCount(int msgID) {
        JSONObject object = getBaseObject("get_album_count", msgID);
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * 获取xml文件信息
     *
     * @param msgID
     * @return
     */
    public byte[] getXmlInfo(int msgID, long date) {
        JSONObject object = getBaseObject("get_xml_info", msgID);
        try {
            object.put("date", date);
        } catch (Exception e) {
        }
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * 云台回中
     *
     * @param msgID
     * @return
     */
    public byte[] ptzBackCenter(int msgID) {
        JSONObject object = getBaseObject("ptz_back_center", msgID);
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * fpv模式
     *
     * @param msgID
     * @return
     */
    public byte[] appFpvMode(int msgID, int mode) {
        JSONObject object = getBaseObject("app_fpv_mode", msgID);
        try {
            object.put("mode", mode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * 获取当前的模式
     *
     * @param msgID
     * @return
     */
    public byte[] getCurMode(int msgID) {
        JSONObject object = getBaseObject("get_current_mode", msgID);
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * 云台保护恢复
     *
     * @param msgID
     * @return
     */
    public byte[] protectRecover(int msgID) {
        JSONObject object = getBaseObject("ptz_protect_recover", msgID);
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    public byte[] setBeautyLight(int msgID, int mode) {
        JSONObject object = getBaseObject("set_beauty_light", msgID);
        try {
            object.put("mode", mode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    public byte[] setBeautySmooth(int msgID, int mode) {
        JSONObject object = getBaseObject("set_beauty_smooth", msgID);
        try {
            object.put("mode", mode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    public byte[] setAbleBeauty(int msgID, int mode) {
        JSONObject object = getBaseObject("set_beauty", msgID);
        try {
            object.put("mode", mode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * 同步相机模式
     *
     * @param msgID
     * @return
     */
    public byte[] syncCameraInfo(int msgID) {
        JSONObject object = getBaseObject("sync_camera_info", msgID);
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * 切换抖音模式
     * @param msgID
     * @param mode 0：退出  1：进入
     * @return
     */
    public byte[] chooseDyMode(int msgID, int mode) {
        JSONObject object = getBaseObject("douyin_mode", msgID);
        try {
            object.put("mode", mode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * 切换抖音模式
     * @param msgID
     * @param mode 0：退出  1：进入
     * @return
     */
    public byte[] enterDyApp(int msgID, int mode) {
        JSONObject object = getBaseObject("enter_douyin_app", msgID);
        try {
            object.put("mode", mode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * 公共请求部分
     *
     * @param action 命令
     * @return
     */
    private JSONObject getBaseObject(String action, int msgID) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("msg_id", msgID);
            jsonObject.put("action", action);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LoggerUtils.printLog("send request = "+jsonObject.toString());
        return jsonObject;
    }

    public byte[] getHead(int type, int size) {
        String a = "MO";
        byte[] magic = a.getBytes();
        byte[] version = new byte[]{1};
        byte[] ioType = new byte[]{(byte) type};
        byte[] dataSize = BytesUtil.intToByteArray(size, true);
        byte[] crc = BytesUtil.intToByteArray(0, true);
        return BytesUtil.byteMergerAll(magic, version, ioType, dataSize, crc);
    }

    /**
     * 喜欢
     *
     * @param msgID
     * @param uri
     * @param likeFlag
     * @return
     */
    public byte[] like(int msgID, String uri, int likeFlag) {
        JSONObject object = getBaseObject("favorite", msgID);
        try {
            object.put("uri", uri);
            object.put("flag", likeFlag);
        } catch (Exception e) {
        }
        byte[] content = object.toString().getBytes();
        int size = content.length;
        byte[] crc = BytesUtil.intToByteArray(0, true);
        byte[] cmd = BytesUtil.byteMergerAll(getHead(3, size), content, crc);
        return cmd;
    }

    /**
     * 心跳包
     *
     * @return
     */
    public byte[] heart() {
        if (mHeart != null && mHeart.length > 0) {
            return mHeart;
        } else {
            JSONObject object = getBaseObject("heartbeat", -1);
            byte[] content = object.toString().getBytes();
            int size = content.length;
            byte[] crc = BytesUtil.intToByteArray(0, true);
            mHeart = BytesUtil.byteMergerAll(getHead(8, size), content, crc);
            return mHeart;
        }
    }
}
