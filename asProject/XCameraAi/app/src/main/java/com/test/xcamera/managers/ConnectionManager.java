package com.test.xcamera.managers;

import android.graphics.Rect;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.test.xcamera.ota.HelperThreadUtils;
import com.test.xcamera.mointerface.MoCurModeCallback;

import com.test.xcamera.accrssory.AccessoryManager;
import com.test.xcamera.application.AiCameraApplication;
import com.test.xcamera.bean.MoAlbumItem;
import com.test.xcamera.bean.MoDeviceInfo;
import com.test.xcamera.bean.MoErrorData;
import com.test.xcamera.bean.MoImage;
import com.test.xcamera.bean.MoRange;
import com.test.xcamera.bean.MoSettingModel;
import com.test.xcamera.bean.MoShotSetting;
import com.test.xcamera.bean.MoSyncCameraInfo;
import com.test.xcamera.bean.MoSystemInfo;
import com.test.xcamera.bean.MoVideoByDate;
import com.test.xcamera.bean.MoVideoSegment;
import com.test.xcamera.bean.SideKeyBean;
import com.test.xcamera.constants.LogcatConstants;
import com.test.xcamera.mointerface.AwbModeCallback;
import com.test.xcamera.mointerface.ExposModeCallback;
import com.test.xcamera.mointerface.MoAlbumCountCallback;
import com.test.xcamera.mointerface.MoAlbumListCallback;
import com.test.xcamera.mointerface.MoCheckUploadCallback;
import com.test.xcamera.mointerface.MoCommandCallback;
import com.test.xcamera.mointerface.MoDeviceInfoCallback;
import com.test.xcamera.mointerface.MoDownloadCallback;
import com.test.xcamera.mointerface.MoErrorCallback;
import com.test.xcamera.mointerface.MoGetActivateStatuCallback;
import com.test.xcamera.mointerface.MoGetDidInfoCallback;
import com.test.xcamera.mointerface.MoGetSideKeyCallback;
import com.test.xcamera.mointerface.MoGetVideoByDateCallback;
import com.test.xcamera.mointerface.MoGetVideoSegmentCallback;
import com.test.xcamera.mointerface.MoRequestCallback;
import com.test.xcamera.mointerface.MoRequestValueCallback;
import com.test.xcamera.mointerface.MoStartTakeVideoCallback;
import com.test.xcamera.mointerface.MoSyncCameraInfoCallback;
import com.test.xcamera.mointerface.MoSyncSettingCallback;
import com.test.xcamera.mointerface.MoSynvShotSettingCallback;
import com.test.xcamera.mointerface.MoSystemInfoCallback;
import com.test.xcamera.mointerface.MoTakeVideoCallback;
import com.test.xcamera.mointerface.MoTaklePhotoCallback;
import com.test.xcamera.mointerface.MoUploadDataCallback;
import com.test.xcamera.utils.FileUtils;
import com.test.xcamera.utils.DownloadSingleCameraFile;
import com.test.xcamera.utils.LoggerUtils;
import com.test.xcamera.utils.MoUtil;
import com.test.xcamera.utils.SPUtils;
import com.moxiang.common.logging.Logcat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zll on 2019/7/4.
 */

public class ConnectionManager {
    private static final String TAG = "ConnectionManager";
    private static ConnectionManager singleton = null;
    private static Object lock = new Object();
    public HeartThread mHeartThread;

    private int mAlbumCount = 0;
    private MoImage mThumbnail;


    public static ConnectionManager getInstance() {
        if (singleton == null)
            synchronized (lock) {
                if (singleton == null) {
                    singleton = new ConnectionManager();
                }
            }
        return singleton;
    }

    private ConnectionManager() {
    }

    private boolean sdcardIsExists = true;

    public boolean isSdcardIsExists() {
        return sdcardIsExists;
    }


//    public void albumCount() {
//        getAlbumCount(new MoAlbumCountCallback() {
//            @Override
//            public void onSuccess(int count, MoImage thumbnail) {
//                Log.i("GET_CAMERA_COUNT_LOG", "onSuccess: 获取相机资源数量!!");
//                mAlbumCount = count;
//                mThumbnail = thumbnail;
//            }
//
//            @Override
//            public void onFailed() {
//                Log.i("GET_CAMERA_COUNT_LOG", "onFailed: 获取相机资源数量失败!!");
//            }
//        });
//    }

    public interface AlbumCountListener {
        void onSuccess(int count, MoImage thumbnail);

        void onFailed();
    }

    public void albumCount(AlbumCountListener albumCountListener) {
        getAlbumCount(new MoAlbumCountCallback() {
            @Override
            public void onSuccess(int count, MoImage thumbnail) {
                Log.i("GET_CAMERA_COUNT_LOG", "onSuccess: 获取相机资源数量!!");
                mAlbumCount = count;
                mThumbnail = thumbnail;
                if (albumCountListener != null) {
                    albumCountListener.onSuccess(count, thumbnail);
                }
            }

            @Override
            public void onFailed() {
                if (albumCountListener != null) {
                    albumCountListener.onFailed();
                }
                Log.i("GET_CAMERA_COUNT_LOG", "onFailed: 获取相机资源数量失败!!");
            }
        });
    }


    /**
     * 添加默认的回调
     */
    public void initCallback() {
        MoCommandHandler.getInstance().addDefaultCmd(-2, (data) -> {
            if (this.errorCallback != null) {
                if (data != null) {
                    parseData(data);
                    MoErrorData err = new MoErrorData();
                    err.event = data.optInt("event", -1);
                    err.status = data.optInt("status", -1);
                    err.value = data.optInt("value", -1);
                    err.battery_mode = data.optInt("battery_mode", 0);
                    err.ptz_mode = data.optInt("ptz_mode", -1);
                    err.ptz_sensitivity = data.optInt("ptz_sensitivity", -1);
                    err.ptz_action = data.optInt("ptz_action", -1);
                    err.track_status = data.optInt("track_status", -1);
                    err.temperature = data.optInt("temperature", -1);
                    err.result = data.optInt("result", -1);
                    err.size = FileUtils.formatSize(data.optLong("size", 0));
                    AiCameraApplication.mApplication.mHandler.post(() -> {
                        if (this.errorCallback != null)
                            this.errorCallback.onError(err);
                    });
                }
            }

        });
    }

    private void parseData(JSONObject data) {
        int value = data.optInt("event");
        if (value == MoErrorCallback.SD_EVENT) {
            int status = data.optInt("status");
            switch (status) {
                case MoErrorCallback.SD_OUT:
                    sdcardIsExists = false;
                    break;
                case MoErrorCallback.SD_IN:
                    break;
                case MoErrorCallback.SD_IN_FAIL:
                    sdcardIsExists = false;
                    break;
                case MoErrorCallback.SD_FULL:
                    break;
                case MoErrorCallback.SD_LOW:
                    break;
            }
        }
    }

    private MoErrorCallback errorCallback;

    public void setErrorI(MoErrorCallback errorCallback) {
        this.errorCallback = errorCallback;
    }

//    public void sendHeartdataThread(FileOutputStream outStream) {
//        if (mHeartThread == null) {
//            mHeartThread = new HeartThread(outStream);
//            mHeartThread.start();
//        } else if (!mHeartThread.isAlive())
//            mHeartThread.start();
//    }


    public int getAlbumCount() {
        return mAlbumCount;
    }

    public void setAlbumCount(int mAlbumCount) {
        this.mAlbumCount = mAlbumCount;
    }

    public MoImage getFirstThumbnail() {
        return mThumbnail;
    }

    /**
     * 跟踪
     *
     * @param callback
     */
    public void startTrack(final MoRequestCallback callback) {
        int msg_id = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().startTrack(msg_id);

        sendCommand(cmd, msg_id, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                Log.d(TAG, "跟踪  startTrack callback: json = " + object.toString());
                handleResult(object, callback);
            }
        });
    }

    /**
     * 停止跟踪
     *
     * @param callback
     */
    public void stopTrack(final MoRequestCallback callback) {
        int msg_id = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().stopTrack(msg_id);

        sendCommand(cmd, msg_id, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                Log.d(TAG, "stopTrack   callback: json = " + object.toString());
                handleResult(object, callback);
            }
        });
    }

    public void send_director(int[] x, int[] y, final MoRequestCallback callback) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().send_director(x, y, msgID);

        HelperThreadUtils.instance().setDataDelayed(cmd);
        MoCommandHandler.getInstance().addCommand(msgID, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                Log.d(TAG, "send_director callback: json = " + object.toString());
                handleResult(object, callback);
            }
        });

//        sendCommand(cmd, msgID, new MoCommandCallback() {
//            @Override
//            public void callback(JSONObject object) {
//                Log.d(TAG, "send_director callback: json = " + object.toString());
//                handleResult(object, callback);
//            }
//        });
    }

    public void startLocationObject(int object_id, final MoRequestCallback callback) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().startLocationObject(msgID, object_id);
        sendCommand(cmd, msgID, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                Log.d(TAG, "startLocationObject callback: json = " + object.toString());
                handleResult(object, callback);
            }
        });
    }

    /**
     * 喜欢
     *
     * @param callback
     * @param uri      喜欢数据的uri
     * @param likeFlag 0,是取消
     *                 1,是喜欢
     */
    public void like(final MoRequestCallback callback, String uri, int likeFlag) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().like(msgID, uri, likeFlag);
        sendCommand(cmd, msgID, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                Log.d(TAG, "startLocationObject callback: json = " + object.toString());
                handleResult(object, callback);
            }
        });

    }


    /**
     * 开启播放
     *
     * @param callback
     */
    public void start_play(final MoRequestCallback callback, String uri) {
        int msg_id = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().start_play(msg_id, uri);

        sendCommand(cmd, msg_id, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                Log.d(TAG, "start_play   callback: json = " + object.toString());
                handleResult(object, callback);
            }
        });
    }

    /**
     * 开启播放
     *
     * @param callback
     */
    public void start_play_download(final MoRequestCallback callback, String uri, long time) {
        int msg_id = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().start_play_download(msg_id, uri, time);
        sendCommand(cmd, msg_id, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                LoggerUtils.d(TAG, "start_play_download   callback: json = " + object.toString());
                Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("download file get camera json :" + object.toString()).out();
                handleResult(object, callback);
            }
        });
    }

    /**
     * 停止播放
     *
     * @param callback
     */
    public void stopPlay(final MoRequestCallback callback) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().stopPlay(msgID);

        sendCommand(cmd, msgID, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                handleResult(object, callback);
            }
        });
    }

    /**
     * 暂停播放
     *
     * @param callback
     */
    public void pausePlay(final MoRequestCallback callback) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().pausePlay(msgID);

        sendCommand(cmd, msgID, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                handleResult(object, callback);
            }
        });
    }

    /**
     * 恢复播放
     *
     * @param callback
     */
    public void resumePlay(final MoRequestCallback callback) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().resumePlay(msgID);

        sendCommand(cmd, msgID, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                handleResult(object, callback);
            }
        });
    }

    /**
     * 快进播放
     *
     * @param callback
     */
    public void seekPlay(final MoRequestCallback callback, long mills) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().seekPlay(msgID, mills);

        sendCommand(cmd, msgID, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                handleResult(object, callback);
            }
        });
    }

    /**
     * 开始预览
     *
     * @param callback
     */
    public void startPreview(final MoRequestCallback callback) {
        Logcat.v().tag(LogcatConstants.FPV_PREVIEW).msg("send cmd msg==>startPreview").out();
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().startPreview(msgID);
        sendCommand(cmd, msgID, 200, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                Log.d(TAG, "startPreview callback: json = " + object.toString());
                handleResult(object, callback);
            }
        });
    }

    /**
     * 结束预览
     *
     * @param callback
     */
    public void stopPreview(final MoRequestCallback callback) {
        Logcat.v().tag(LogcatConstants.FPV_PREVIEW).msg("send cmd msg==>stopPreview").out();
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().stopPreview(msgID);
        sendCommand(cmd, msgID, 200, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                Log.d(TAG, "getCameraMediaList callback: json = " + object);
                handleResult(object, callback);
            }
        });
    }

    /**
     * 切换拍摄模式
     *
     * @param mode
     * @param callback
     */
    public void switchMode(String mode, final MoRequestCallback callback) {
        Logcat.v().tag(LogcatConstants.FPV_PREVIEW).msg("send cmd msg==>switchMode:" + mode).out();
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().switchMode(mode, msgID);
        sendCommand(cmd, msgID, 900, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                handleResult(object, callback);
            }
        });
    }

    /**
     * 拍照
     *
     * @param callback
     */
    public void takePhoto(final MoTaklePhotoCallback callback) {
        if (callback == null) return;

        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().takePhoto2(msgID);
        sendCommand(cmd, msgID, (object) -> {
            MoImage image = null;
            int result = object.optInt("result", -1);

            if (result == 0) {
                JSONObject imageObj = object.optJSONObject("thumbnail");
                if (imageObj != null) {
                    image = MoImage.parse(imageObj);
                }
                callback.onSuccess(image);
            } else {
                int reason = object.optInt("reason", -1);
                callback.onFailed(reason);
            }
        });
    }

    /**
     * 设置照片拍照参数
     *
     * @param proportion
     * @param callback
     */

    public void takePhotoSetting(int proportion, final MoRequestCallback callback) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().takePhotoSetting(proportion, msgID);
        sendCommand(cmd, msgID, 800, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                Log.d(TAG, "takePhotoSetting callback: json = " + object.toString());
                handleResult(object, callback);
            }
        });
    }

    /**
     * 开始录制视频
     *
     * @param callback
     */
    public void takeVideoStart(final MoStartTakeVideoCallback callback) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().takeVideoStart(msgID);
        sendCommand(cmd, msgID, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                Log.d(TAG, "takeVideoStart callback: json = " + object.toString());

                int result = object.optInt("result", -1);
                String uri = object.optString("uri", "");
                int reason = object.optInt("reason", -1);

                if (callback != null) {
                    if (result == 0)
                        callback.onSuccess(uri, reason);
                    else
                        callback.onFailed(reason);
                }
            }
        });
    }

    /**
     * 结束录制视频
     *
     * @param callback
     */
    public void takeVideoStop(final MoTakeVideoCallback callback) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().takeVideoStop(msgID);
        sendCommand(cmd, msgID, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                Log.d(TAG, "takeVideoStop callback: json = " + object.toString());
                int result = object.optInt("result", -1);
                if (callback != null) {
                    if (result == 0) {
                        JSONObject imageObj = object.optJSONObject("thumbnail");
                        MoImage image = MoImage.parse(imageObj);
                        callback.onSuccess(image);
                    } else {
                        callback.onFailed();
                    }
                }
            }
        });
    }

    /**
     * 设置视频拍摄参数
     *
     * @param resolution
     * @param fps
     * @param callback
     */
    public void takeVideoSetting(int resolution, int fps, final MoRequestCallback callback) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().takeVideoSetting(resolution, fps, msgID);
        sendCommand(cmd, msgID, 500, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                handleResult(object, callback);
            }
        });
    }

    /**
     * 获取相册列表
     *
     * @param callback
     * @param page
     */
    public void getAlbumList(final MoAlbumListCallback callback, int page) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().albumList(msgID, page);
        HelperThreadUtils.instance().setData(cmd);
        MoCommandHandler.getInstance().addCommand(msgID, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                ArrayList<MoAlbumItem> items = new ArrayList<>();
                Log.d(TAG, "getAlbumList callback: json = " + object.toString());
                if (object != null) {
                    try {
//                        int result = object.optInt("result");
                        JSONArray itemArray = object.optJSONArray("album_item_list");
                        if (itemArray != null && itemArray.length() > 0) {
                            items = parseArray(itemArray, items);
                        }
                        if (callback != null) {
                            callback.onSuccess(items);
                        }
//
//                        if (callback != null) {
//                            callback.onSuccess(items);
//                        }
                    } catch (Exception e) {
                        if (callback != null)
                            callback.onFailed();
                    }
                } else {
                    if (callback != null) {
                        callback.onFailed();
                    }
                }
            }
        });
    }

    private ArrayList<MoAlbumItem> parseArray(JSONArray itemArray, ArrayList<MoAlbumItem> items) {
        for (int i = 0; i < itemArray.length(); i++) {
            JSONObject itemObj = itemArray.optJSONObject(i);
            if (itemObj != null) {
                MoAlbumItem parse = MoAlbumItem.parse(itemObj);
                items.add(parse);
            }
        }
        return items;
    }


    /**
     * 获取系统信息
     *
     * @param callback
     */
    public void getSystemInfo(final MoSystemInfoCallback callback) {
        if (callback == null) return;

        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().syncSystemInfo(msgID);
        sendCommand(cmd, msgID, (object) -> {
            AiCameraApplication.mApplication.mHandler.post(() -> {
                int result = object.optInt("result", -1);
                if (result == 0) {
                    MoSystemInfo systemInfo = MoSystemInfo.parse(object);
                    callback.onSuccess(systemInfo);
                } else {
                    callback.onFailed();

                }
            });
        });
    }

    /**
     * 云台控制
     *
     * @param direction
     */
    public void naviControl(int direction) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().naviControl(direction, msgID);
        sendCommand(cmd, msgID, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {

            }
        });
    }

    /**
     * Mark视频
     *
     * @param videoUri
     * @param timeStamp
     */
    public void markVideo(String videoUri, long timeStamp, final MoRequestCallback moRequestCallback) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().markVideo(videoUri, timeStamp, msgID);
        sendCommand(cmd, msgID, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                handleResult(object, moRequestCallback);
            }
        });
    }

    /**
     * 滤镜开关
     *
     * @param title
     */
    public void selectFilter(String title) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().selectFilter(title, msgID);
        sendCommand(cmd, msgID, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {

            }
        });
    }

    /**
     * 美颜开关
     *
     * @param value
     */
    public void switchBeauty(int value) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().switchBeauty(value, msgID);
        sendCommand(cmd, msgID, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {

            }
        });
    }

    /**
     * 更新相机朝向
     *
     * @param direction
     */
    public void updateDirection(int direction) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().updateDirection(direction, msgID);
        sendCommand(cmd, msgID, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {

            }
        });
    }

    /**
     * 设置照片格式
     *
     * @param type
     * @param callback
     */
    public void setPhotoFormat(int type, final MoRequestCallback callback) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().setPhotoFormat(type, msgID);
        sendCommand(cmd, msgID, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                Log.d(TAG, "takeVideoStart callback: json = " + object.toString());
                handleResult(object, callback);
            }
        });
    }

    /**
     * 删除照片或视频
     *
     * @param uris
     * @param callback
     */
    public void deleteMedia(ArrayList<String> uris, final MoRequestCallback callback) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().deleteMedia(uris, msgID);
        sendCommand(cmd, msgID, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                Log.d(TAG, "takeVideoStart callback: json = " + object.toString());
                handleResult(object, callback);
            }
        });
    }

    /**
     * 抗闪烁开关
     *
     * @param enable
     * @param freq
     * @param callback
     */
    public void setAntiFicker(int enable, int freq, final MoRequestCallback callback) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().setAntiFicker(enable, freq, msgID);
        sendCommand(cmd, msgID, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                Log.d(TAG, "takeVideoStart callback: json = " + object.toString());
                handleResult(object, callback);
            }
        });
    }

    /**
     * 画面翻转
     *
     * @param type
     * @param callback
     */
    public void setSelfie(int type, final MoRequestCallback callback) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().setSelfie(type, msgID);
        sendCommand(cmd, msgID, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                Log.d(TAG, "takeVideoStart callback: json = " + object.toString());
                handleResult(object, callback);
            }
        });
    }

    /**
     * 3.3.17.获取可跟踪的物体
     *
     * @param status 0：关 1：开
     */
    public void getTracking(int status, final MoRequestCallback callback) {
        Log.e("=====", "getTracking==>" + status);
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().getTracking(status, msgID);
        sendCommand(cmd, msgID, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                Log.e("=====", "getTracking object==>" + object.toString());
                handleResult(object, callback);
            }
        });
    }

    /**
     * 画框跟踪
     *
     * @param rect
     * @param previewWidth
     * @param previewHeight
     * @param bgr
     */
    public void trackRect(int previewWidth, int previewHeight, Rect rect, byte[] bgr, MoRequestCallback callback) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().trackRect(msgID, rect, previewWidth, previewHeight, bgr);

        sendCommand(cmd, msgID, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                handleResult(object, callback);
            }
        });
    }

    /**
     * 获取设备信息
     *
     * @param callback
     */
    public void getDeviceInfo(final MoDeviceInfoCallback callback) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().getDeviceInfo(msgID);
        sendCommand(cmd, msgID, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                int result = 1;
                MoDeviceInfo deviceInfo = null;
                try {
                    result = object.optInt("result");
                    deviceInfo = MoDeviceInfo.parse(object);
                } catch (Exception e) {
                }

                if (callback != null) {
                    if (result == 0) {
                        callback.onSuccess(deviceInfo);
                        //将固件版本存起来
                        SPUtils.put(AiCameraApplication.mApplication, "hardwareVersion", deviceInfo.getmFirmwareVersion());
                        SPUtils.put(AiCameraApplication.mApplication, "hardwaredid", deviceInfo.getDid());
                    } else {
                        callback.onFailed();
                    }
                }
            }
        });
    }

    /**
     * 同步拍摄设置
     */
    public void syncShotSetting(final MoSynvShotSettingCallback callback) {
        if (callback == null) return;
        Logcat.v().tag(LogcatConstants.FPV_PREVIEW).msg("send cmd msg==>syncShotSetting 111").out();
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().syncShotSetting(msgID);
        sendCommand(cmd, msgID, 0, (object) -> {
            if (object != null) {
                int result = object.optInt("result", -1);
                if (result == 0) {
                    MoShotSetting shotSetting = MoShotSetting.parse(object);
                    callback.onSuccess(shotSetting);
                } else {
                    callback.onFailed();
                }
            }
        });
    }

    /**
     * 同步拍摄设置
     */
    public void syncShotSetting(final MoSynvShotSettingCallback callback, final MoSyncSettingCallback callbackProfession) {
        if (callback == null) return;

        Logcat.v().tag(LogcatConstants.FPV_PREVIEW).msg("send cmd msg==>syncShotSetting  222").out();
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().syncShotSetting(msgID);
        sendCommand(cmd, msgID, 0, (result) -> {
            int res = result.optInt("result", -1);
            if (res == 0) {
                MoShotSetting shotSetting = MoShotSetting.parse(result);
                callback.onSuccess(shotSetting);

                Gson gson = new Gson();
                MoSettingModel model = gson.fromJson(result.toString(), MoSettingModel.class);
                if (model != null)
                    model.parse(result);
                callbackProfession.onSuccess(model);
            } else {
                callback.onFailed();
                callbackProfession.onFailed();
            }
        });
    }

    /**
     * 同步拍摄设置
     */
    public void syncSetting(final MoSyncSettingCallback callback) {
        if (callback == null) return;

        Logcat.v().tag(LogcatConstants.FPV_PREVIEW).msg("send cmd msg==>syncSetting  333").out();
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().syncShotSetting(msgID);
        sendCommand(cmd, msgID, 0, (object) -> {
            int res = object.optInt("result", -1);
            if (res == 0) {
                Gson gson = new Gson();
                MoSettingModel model = gson.fromJson(object.toString(), MoSettingModel.class);
                if (model != null)
                    model.parse(object);
                callback.onSuccess(model);
            } else {
                callback.onFailed();
            }
        });
    }

    /**
     * 延时摄影设置
     *
     * @param frame
     * @param callback
     */
    public void laspeRecordSetting(int frame, final MoRequestCallback callback) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().lapseRecordSetting(msgID, frame);
        sendCommand(cmd, msgID, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                handleResult(object, callback);
            }
        });
    }

    /**
     * 慢动作设置
     *
     * @param speed
     * @param callback
     */
    public void slowMotionSetting(int speed, final MoRequestCallback callback) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().slowMotionSetting(msgID, speed);
        sendCommand(cmd, msgID, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                handleResult(object, callback);
            }
        });
    }

    /**
     * 云台速度设置
     *
     * @param speed
     * @param callback
     */
    public void speedSetting(int speed, final MoRequestCallback callback) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().speedSetting(msgID, speed);
        sendCommand(cmd, msgID, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                handleResult(object, callback);
            }
        });
    }

    /**
     * 数码变焦
     *
     * @param scale
     * @param callback
     */
    public void digitalZoom(int scale, final MoRequestCallback callback) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().digitalZoom(msgID, scale);
        sendCommand(cmd, msgID, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                handleResult(object, callback);
            }
        });
    }

    /**
     * 畸变校正
     *
     * @param type
     * @param callback
     */
    public void distortionCorrection(int type, final MoRequestCallback callback) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().distortionCorrection(msgID, type);
        sendCommand(cmd, msgID, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                handleResult(object, callback);
            }
        });
    }

    /**
     * 设置拍照倒计时
     *
     * @param time
     * @param callback
     */
    public void setDelayTime(int time, final MoRequestCallback callback) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().setDelayTime(msgID, time);
        sendCommand(cmd, msgID, (obj) -> {
            handleResult(obj, callback);
        });
    }

    /**
     * 请求解禁状态
     *
     * @param callback
     */
    public void getActivateStatue(final MoGetActivateStatuCallback callback) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().getActivateStatue(msgID);
        sendCommand(cmd, msgID, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                int result = 1;
                int status = -1;
                String activeID = "";
                if (object != null) {
                    try {
                        result = object.optInt("result");
                        status = object.optInt("status");
                        activeID = object.optString("active_id");
                    } catch (Exception e) {
                    }
                }
                if (callback != null) {
                    if (result == 0) {
                        callback.onSuccess(status, activeID);
                    } else {
                        callback.onFailed();
                    }
                }
            }
        });
    }

    /**
     * 请求did
     *
     * @param callback
     */
    public void getDidInfo(final MoGetDidInfoCallback callback) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().getDidInfo(msgID);
        sendCommand(cmd, msgID, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                int result = 1;
                String did = "";
                String nonce = "";
                if (object != null) {
                    try {
                        result = object.optInt("result");
                        did = object.optString("did");
                        nonce = object.optString("nonce");
                    } catch (Exception e) {
                    }
                }
                if (callback != null) {
                    if (result == 0) {
                        callback.onSuccess(did, nonce);
                    } else {
                        callback.onFailed();
                    }
                }
            }
        });
    }

    /**
     * 请求写入激活
     *
     * @param activateNum
     * @param callback
     */
    public void activateDevice(String activateNum, String activateID, final MoRequestCallback callback) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().activateDevice(msgID, activateNum, activateID);
        sendCommand(cmd, msgID, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                handleResult(object, callback);
            }
        });
    }

    /**
     * 请求修改激活状态
     *
     * @param callback
     */
    public void modifyStatus(int status, final MoRequestCallback callback) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().modifyStatus(msgID, status);
        sendCommand(cmd, msgID, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                handleResult(object, callback);
            }
        });
    }

    /**
     * 升级条件校验
     *
     * @param callback
     */
    public void uploadCheckcond(String version, final MoCheckUploadCallback callback) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().uploadCheckcond(version, msgID);
        sendCommand(cmd, msgID, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                int result = -1;
                int reason = -1;
                if (object != null) {
                    try {
                        result = object.optInt("result");
                        reason = object.optInt("reason");
                    } catch (Exception e) {
                    }
                }
                if (callback != null) {
                    if (result == 1) {
                        callback.onSuccess(reason);
                    } else {
                        callback.onFailed();
                    }
                }
            }
        });
    }

    /**
     * 下载开始
     *
     * @param callback
     */
    public void startUpload(String filename, final MoRequestCallback callback) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().startUpload(msgID, filename);
        HelperThreadUtils.instance().setData(cmd, HelperThreadUtils.instance().upload_start);
        sendUpdataPackageData(cmd, msgID, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                handleResult(object, callback);
            }
        });
    }

    /**
     * 上传数据
     *
     * @param pkgName
     * @param total
     * @param pkgSize
     * @param data
     * @param callback
     */
    public void uploadData(String pkgName, int total, int pkgSize, byte[] data, final MoUploadDataCallback callback) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().uploadData(msgID, pkgName, total, pkgSize, data);
        HelperThreadUtils.instance().setData(cmd, HelperThreadUtils.instance().uploading);
        sendUpdataPackageData(cmd, msgID, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                int result = -1;
                int progress = -1;
                if (object != null) {
                    try {
                        result = object.optInt("result");
                        progress = object.optInt("proc");
                    } catch (Exception e) {
                        callback.onFailed();
                    }
                }
                if (callback != null) {
                    if (result == 0) {
                        callback.onSuccess(progress);
                    } else {
                        callback.onFailed();
                    }
                }
            }
        });
    }

    /**
     * 上传结束
     *
     * @param callback
     */
    public void uploadFinish(final MoRequestCallback callback) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().uploadFinish(msgID);
        HelperThreadUtils.instance().setData(cmd, HelperThreadUtils.instance().upload_finish);
        sendUpdataPackageData(cmd, msgID, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                handleResult(object, callback);
            }
        });
    }

    /**
     * 固件开始升级
     *
     * @param callback
     */
    public void upgrade(final MoRequestCallback callback) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().upgrade(msgID);
        HelperThreadUtils.instance().setData(cmd, HelperThreadUtils.instance().upgrade);
        sendUpdataPackageData(cmd, msgID, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                handleResult(object, callback);
            }
        });
    }

    /**
     * 获取精彩推荐数据
     */
    public void getVideoByDate(int page, final MoGetVideoByDateCallback callback) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().getVideoByDate(page, msgID);
        sendCommand(cmd, msgID, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("camera json data object  " + object + "       timestamp=" + System.currentTimeMillis()).out();
                LoggerUtils.printLog("获取精彩推荐数据" + object + "");
                int result = -1;
                ArrayList<MoVideoByDate> videoByDates = new ArrayList<>();
                try {
                    result = object.optInt("result");
                    JSONArray array = object.optJSONArray("video_bydate_list");
                    if (array != null && array.length() > 0) {
                        for (int i = 0; i < array.length(); i++) {
                            videoByDates.add(MoVideoByDate.parse(array.getJSONObject(i)));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (callback != null) {
                    if (result == 0) {
                        callback.onSuccess(videoByDates);
                    } else {
                        callback.onFailed();
                    }
                }
            }
        });
    }

    /**
     * 获取素材片段
     *
     * @param callback
     */
    public void getVideoSegment(long date, final MoGetVideoSegmentCallback callback) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().getVideoSegment(date, msgID);
        sendCommand(cmd, msgID, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                int result = -1;
                ArrayList<MoVideoSegment> videoSegments = null;
                try {
                    result = object.optInt("result", -1);
                } catch (Exception e) {
                }
                try {
                    JSONArray array = object.optJSONArray("video_segment_list");
                    if (array != null && array.length() > 0) {
                        videoSegments = new ArrayList<>();
                        for (int i = 0; i < array.length(); i++) {
                            videoSegments.add(MoVideoSegment.parse(array.getJSONObject(i)));
                        }
                    }
                } catch (Exception e) {
                }
                if (callback != null) {
                    if (result == 0) {
                        callback.onSuccess(videoSegments);
                    } else {
                        callback.onFailed();
                    }
                }
            }
        });
    }

    /**
     * 获取侧面键一键成片数据
     *
     * @param page     页码
     * @param callback
     */
    public void getVideoSideKey(int page, final MoGetSideKeyCallback callback) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().getVideoSideKey(page, msgID);
        sendCommand(cmd, msgID, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                LoggerUtils.printLog("ce mian jian = " + object.toString());
                try {
                    List<SideKeyBean> sideKeyList = new Gson().fromJson(object.getString("video_bydate_list"), new TypeToken<List<SideKeyBean>>() {
                    }.getType());
                    if (callback != null) {
                        int result = object.optInt("result", -1);
                        if (result == 0) {
                            for (int i = 0; i < sideKeyList.size(); i++) {
                                LoggerUtils.printLog("sideKeyBean = " + sideKeyList.get(i));
                            }
                            callback.onSuccess(sideKeyList);
                        } else {
                            callback.onFailed();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 同步时间
     *
     * @param time     同步时间 毫秒
     * @param callback
     */
    public void syncTime(long time, final MoRequestValueCallback callback) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().syncTime(time, msgID);
        sendCommand(cmd, msgID, (JSONObject object) -> {
            int result = object.optInt("result", -1);
            if (callback != null) {
                if (result == 0) {
                    callback.onSuccess();
                } else {
                    callback.onFailed(result);
                }
            }
        });
    }

    /**
     * 获取侧面键一键成片素材片段
     *
     * @param video_icon
     * @param callback
     */
    public void getVideoSegmentSideKey(String video_icon, final MoGetVideoSegmentCallback callback) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().getVideoSegmentSideKey(video_icon, msgID);
        sendCommand(cmd, msgID, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                int result = -1;
                ArrayList<MoVideoSegment> videoSegments = null;
                try {
                    result = object.optInt("result");
                } catch (Exception e) {
                }
                try {
                    JSONArray array = object.optJSONArray("video_segment_list");
                    if (array != null && array.length() > 0) {
                        videoSegments = new ArrayList<>();
                        for (int i = 0; i < array.length(); i++) {
                            videoSegments.add(MoVideoSegment.parse(array.getJSONObject(i)));
                        }
                    }
                } catch (Exception e) {
                }
                if (callback != null) {
                    if (result == 0) {
                        callback.onSuccess(videoSegments);
                    } else {
                        callback.onFailed();
                    }
                }
            }
        });
    }

    /**
     * 超高画质开关
     *
     * @param enable   0:打开  1:关闭
     * @param callback
     */
    public void setSuperHighQuality(int enable, final MoRequestCallback callback) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().setSuperHighQuality(msgID, enable);
        sendCommand(cmd, msgID, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                handleResult(object, callback);
            }
        });
    }

    /**
     * iso设置
     *
     * @param value    范围值
     * @param callback
     */
    public void setISOValue(int value, final MoRequestCallback callback) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().setISOValue(msgID, value);
        sendCommand(cmd, msgID, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                handleResult(object, callback);
            }
        });
    }

    /**
     * 快门速度
     *
     * @param value    范围值
     * @param callback
     */
    public void setShutter(int value, final MoRequestCallback callback) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().setShutter(msgID, value);
        sendCommand(cmd, msgID, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                handleResult(object, callback);
            }
        });
    }

    /**
     * EV设置
     *
     * @param value    范围值
     * @param callback
     */
    public void setEV(int value, final MoRequestCallback callback) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().setEV(msgID, value);
        sendCommand(cmd, msgID, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                handleResult(object, callback);
            }
        });
    }

    /**
     * 白平衡
     *
     * @param value    范围值
     * @param callback
     */
    public void setWhiteBalance(int value, final MoRequestCallback callback) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().setWhiteBalance(msgID, value);
        sendCommand(cmd, msgID, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                handleResult(object, callback);
            }
        });
    }

    /**
     * 色彩设置
     *
     * @param value    范围值
     * @param callback
     */
    public void setColor(int value, final MoRequestCallback callback) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().setColor(msgID, value);
        sendCommand(cmd, msgID, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                handleResult(object, callback);
            }
        });
    }

    /**
     * 格式设置
     *
     * @param value    照片模式 0:Jpeg 2:J+R
     *                 录像模式 0:mp4 1:mov
     * @param callback
     */
    public void setFormat(int value, final MoRequestCallback callback) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().setFormat(msgID, value);
        sendCommand(cmd, msgID, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                handleResult(object, callback);
            }
        });
    }

    /**
     * 云台速度设置
     *
     * @param value    0:灵敏  1:柔和
     * @param callback
     */
    public void setSpeed(int value, final MoRequestCallback callback) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().setSpeed(msgID, value);
        sendCommand(cmd, msgID, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                handleResult(object, callback);
            }
        });
    }

    /**
     * 云台模式设置
     *
     * @param value    1: lock roll
     *                 2: Lock Pitch&Roll
     *                 3: FPV
     *                 4: 全Lock
     * @param callback
     */
    public void setModel(int value, final MoRequestCallback callback) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().setModel(msgID, value);
        sendCommand(cmd, msgID, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                handleResult(object, callback);
            }
        });
    }

    /**
     * 设置白平衡模式
     *
     * @param value 0：场景  1：自定义
     */
    public void setAwbModel(int value, final AwbModeCallback callback) {
        if (callback == null) return;

        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().setAwbModel(msgID, value);
        sendCommand(cmd, msgID, (object) -> {
            int ret = object.optInt("result", -1);
            if (ret == 0) {
                callback.onSuccess(object.optInt("value", -1));
            } else
                callback.onFailed();
        });
    }

    /**
     * 设置曝光模式
     *
     * @param value 0自动 1 自定义
     */
    public void setExposureModel(int value, final ExposModeCallback callback) {
        if (callback == null) return;
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().setExposureModel(msgID, value);
        sendCommand(cmd, msgID, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                int ret = object.optInt("result", -1);
                if (ret == 0) {
                    callback.onSuccess(object.optInt("iso", -1),
                            object.optInt("ev", -1), object.optInt("shutter", -1));
                } else
                    callback.onFailed();
            }
        });
    }

    /**
     * 电子防抖
     *
     * @param value    0:打开  1:关闭
     * @param callback
     */
    public void setDis(int value, final MoRequestCallback callback) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().setDis(msgID, value);
        sendCommand(cmd, msgID, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                handleResult(object, callback);
            }
        });
    }

    /**
     * 设置曝光
     *
     * @param value    0:auto  1:manual
     * @param callback
     */
    public void setExplore(int value, final MoRequestCallback callback) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().setExplore(msgID, value);
        sendCommand(cmd, msgID, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                handleResult(object, callback);
            }
        });
    }

    /**
     * 设置长曝光
     *
     * @param value
     * @param callback
     * @return
     */
    public void setLongExplore(float value, final MoRequestCallback callback) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().setLongExplore(msgID, value);
        sendCommand(cmd, msgID, ((object) -> {
            handleResult(object, callback);
        }));
    }

    /**
     * 设置白平衡
     *
     * @param value    0:auto  1:manual
     * @param callback
     */
    public void setAwb(int value, final MoRequestCallback callback) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().setAwb(msgID, value);
        sendCommand(cmd, msgID, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                handleResult(object, callback);
            }
        });
    }

    /**
     * 云台回中
     *
     * @param callback
     */
    public void setPtzBack(final MoRequestCallback callback) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().setPtzBack(msgID);
        sendCommand(cmd, msgID, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                handleResult(object, callback);
            }
        });
    }

    /**
     * 设置HDR/WDR
     *
     * @param value    视频wdr 0:打开 1:关闭
     *                 照片hdr 0:打开 1:关闭
     * @param callback
     */
    public void setDynamicRange(int value, final MoRequestCallback callback) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().setDynamicRange(msgID, value);
        sendCommand(cmd, msgID, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                handleResult(object, callback);
            }
        });
    }

    public void getAlbumCount(final MoAlbumCountCallback callback) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().getAlbumCount(msgID);
        sendCommand(cmd, msgID, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                int result = -1;
                MoImage thumbnail = null;
                int count = 0;
                try {
                    result = object.optInt("result", -1);
                    count = object.optInt("count");
                } catch (Exception e) {
                }
                try {
                    JSONObject thumbnailObj = object.optJSONObject("thumbnail");

                    if (thumbnailObj != null) {
                        thumbnail = MoImage.parse(thumbnailObj);
                    }
                    thumbnail.setRotate(object.optInt("rotate", 0));
                } catch (Exception e) {
                }
                if (callback != null) {
                    if (result == 0) {
                        callback.onSuccess(count, thumbnail);
                    } else {
                        callback.onFailed();
                    }
                }
            }
        });
    }

    /**
     * 获取xml信息
     *
     * @param date
     * @param callback
     */
    public void getXmlInfo(long date, DownloadSingleCameraFile.DownloadCallback callback) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().getXmlInfo(msgID, date);
        sendCommand(cmd, msgID, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                int result;
                String uri;
                LoggerUtils.printLog("xml path jin ri jingcai = " + object.toString());
                try {
                    result = object.optInt("result", -1);
                    uri = object.optString("uri");
                    if (callback != null) {
                        if (result == 0) {
                            long size = MoUtil.getXmlSize(uri);
                            new DownloadSingleCameraFile(DownloadSingleCameraFile.FileType.XML).startDownload(Uri.parse(uri), size, callback);
                        }
                    }
                } catch (Exception e) {
                }
            }
        });
    }

    /**
     * 云台回中
     *
     * @param callback
     */
    public void ptzBackCenter(MoRequestCallback callback) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().ptzBackCenter(msgID);
        sendCommand(cmd, msgID, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                handleResult(object, callback);
            }
        });
    }


    /**
     * 同步相机模式
     */
    public void syncCameraInfo(MoSyncCameraInfoCallback callback) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().syncCameraInfo(msgID);
        sendCommand(cmd, msgID, 0, (obj) -> {
            MoSyncCameraInfo info = null;
            if (obj != null) {
                Gson gson = new Gson();
                info = gson.fromJson(obj.toString(), MoSyncCameraInfo.class);
            }
            if (info != null && info.result == 0)
                callback.onSuccess(info);
            else
                callback.onFailed();
        });
    }

    /**
     * fpv模式
     */
    public void appFpvMode(int mode, MoRequestCallback callback) {
//        Log.e("=====", "appFpvMode  mode==>" + mode);
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().appFpvMode(msgID, mode);
        sendCommand(cmd, msgID, 500, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                handleResult(object, callback);
            }
        });
    }

    /**
     * 获取当前的模式
     */
    public void getCurMode(MoCurModeCallback callback) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().getCurMode(msgID);
        Logcat.e().tag(LogcatConstants.FPV_PREVIEW).msg("getCurMode  msgID==>" + msgID + "  back==>" + callback).out();
        sendCommand(cmd, msgID, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                Logcat.e().tag(LogcatConstants.FPV_PREVIEW).msg("getCurMode   back==>" + callback).out();
                if (callback == null) return;
                int result = object.optInt("result", -1);
                if (result == 0) {
                    callback.success(object.optInt("mode", -1));
                } else {
                    callback.onFailed();
                }
            }
        });
    }

    /**
     * 云台保护恢复
     */
    public void protectRecover(MoRequestCallback callback) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().protectRecover(msgID);
        sendCommand(cmd, msgID, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                handleResult(object, callback);
            }
        });
    }

    public void setBeautyLight(int mode, MoRequestCallback callback) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().setBeautyLight(msgID, mode);
        sendCommand(cmd, msgID, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                handleResult(object, callback);
            }
        });
    }

    public void setBeautySmooth(int mode, MoRequestCallback callback) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().setBeautySmooth(msgID, mode);
        sendCommand(cmd, msgID, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                handleResult(object, callback);
            }
        });
    }

    public void setAbleBeauty(int mode, MoRequestCallback callback) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().setAbleBeauty(msgID, mode);
        sendCommand(cmd, msgID, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                handleResult(object, callback);
            }
        });
    }

    /**
     * 切换抖音模式
     *
     * @param mode     0：退出  1：进入
     * @param callback
     */
    public void chooseDyMode(int mode, MoRequestCallback callback) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().chooseDyMode(msgID, mode);
        sendCommand(cmd, msgID, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                handleResult(object, callback);
            }
        });
    }

    /**
     * 切换抖音模式
     *
     * @param mode     0：退出  1：进入
     * @param callback
     */
    public void enterDyApp(int mode, MoRequestCallback callback) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().enterDyApp(msgID, mode);
        sendCommand(cmd, msgID, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                handleResult(object, callback);
            }
        });
    }

    public void stopDownloadFile(final MoRequestCallback callback) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().stopDownloadSingleFile();
        sendCommand(cmd, msgID, new MoCommandCallback() {
            @Override
            public void callback(JSONObject object) {
                handleResult(object, callback);
            }
        });
    }

    public void downloadFile(String uri, MoRange range, MoDownloadCallback callback) {
        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().downloadFile(uri, range, msgID);
        String newUri = uri + "#" + range.getmOffset();
        sendDownloadCommand(cmd, newUri, callback);
    }

    public void downloadFile(int msgID, String uri, MoRange range, MoDownloadCallback callback) {
//        int msgID = CommandIDManager.getInstance().getMsgID();
        byte[] cmd = CommandManager.getInstance().downloadFile(uri, range, msgID);
        String newUri = uri + "#" + range.getmOffset();
        sendDownloadCommand(cmd, newUri, callback);
    }

    private void sendCommand(byte[] cmd, int msgID, MoCommandCallback callback) {
        sendCommand(cmd, msgID, -1, callback);
    }

    private void sendCommand(byte[] cmd, int msgID, int cache, MoCommandCallback callback) {
        MoCommandHandler.getInstance().addCommand(msgID, cache, callback);
        AccessoryManager.getInstance().sendCommand(cmd);
    }

    private void sendUpdataPackageData(byte[] cmd, int msgID, MoCommandCallback callback) {
        MoCommandHandler.getInstance().addCommand(msgID, callback);
//        SendUpdataPackageManager.getInstance().addUpdataPackage(cmd);

//        boolean b = AccessoryManager.getInstance().sendCommands(cmd);
//        Log.i("BBBB_LOG", "sendCommands: " + b);
    }


    private void sendDownloadCommand(byte[] cmd, String uri, MoDownloadCallback callback) {
        MoDownloadHandler.getInstance().addDownloadCommand(uri, callback);
        AccessoryManager.getInstance().sendCommand(cmd);
    }

//    private void handleResultUI(JSONObject object, MoRequestCallback callback) {
//        if (callback == null)
//            return;
//
//        Observable.create((emitter) -> {
//            int res = -1;
//            if (object != null)
//                res = object.optInt("result", -1);
//            emitter.onNext(res);
//        })
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe((res) -> {
//                    if ((Integer) res == 0)
//                        callback.onSuccess();
//                    else
//                        callback.onFailed();
//                });
//    }

    private void handleResult(JSONObject object, MoRequestCallback callback) {
        if (callback == null) return;
        int result = object.optInt("result", -1);
        if (result == 0) {
            callback.onSuccess();
//                showToast("request success");
        } else {
            callback.onFailed();
//                showToast("request failed");
        }
    }

    public void flush() {
        Log.e("=====", "==>flush");
        ConnectionManager.getInstance().getSystemInfo(new MoSystemInfoCallback() {
            @Override
            public void onSuccess(MoSystemInfo systemInfo) {
            }

            @Override
            public void onFailed() {
            }
        });
    }

    public class HeartThread extends Thread {
        private FileOutputStream outStream;
        private boolean mRunning = true;
        private long lastHeartTime;

        public HeartThread(FileOutputStream outStream) {
            this.outStream = outStream;
        }

        @Override
        public void run() {
//            while (mRunning) {
//                try {
//                    byte[] cmd = CommandManager.getInstance().heart();
////                  AccessoryManager.getInstance().sendCommand(cmd);
////                    Log.e("==ST===", "==>HeartThread");
//                    outStream.write(cmd);
//                    outStream.flush();
//
//                    int diffTime = (int) (System.currentTimeMillis() - lastHeartTime);
//                    if (diffTime > 1900)
//                        Debug.debugUsb("send heart  timeout         ==>" + diffTime);
//                    lastHeartTime = System.currentTimeMillis();
//                    Debug.debugUsb("send heart==>" + outStream.getFD().valid() + "  time=>" + diffTime);
//
////                    LoggerUtils.printLog("send: heart beat data");
//                    Thread.sleep(500);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    Debug.debugUsb("heart   err---------------------------==>" + e.getMessage());
//                }
//            }
//            Debug.debugUsb("heart  ------------------------------------==>end");
        }

        public void stopHeartThread() {
            mRunning = false;
            Log.i("HEART_DATA", "run: stop send heart data message Thread !");
        }
    }
}