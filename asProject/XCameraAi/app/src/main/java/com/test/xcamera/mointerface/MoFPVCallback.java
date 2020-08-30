package com.test.xcamera.mointerface;

import com.test.xcamera.bean.MoImage;
import com.test.xcamera.bean.MoSettingModel;
import com.test.xcamera.bean.MoShotSetting;
import com.test.xcamera.bean.MoSystemInfo;

/**
 * FPV页面状态回调
 * Created by zll on 2019/10/23.
 */

public interface MoFPVCallback {
    int RECORD_START = 0;
    int RECORD_START_FAILED = 1;
    int RECORDING = 2;
    int RECORD_STOP = 3;
    int RECORD_STOP_FAILED = 4;
    int PHOTO_START = 100;
    int PHOTO_START_FAILED = 101;
    int PHOTOING = 102;
    int PHOTO_STOP = 103;
    int PHOTO_STOP_FAILED = 104;
    void requestFailed(int state);

    void requestReason(int reason);

    /**
     * 结束Activity
     */
    void onFinish(String type);

    /**
     * 接口请求成功
     */
    void requestSuccess();

    /**
     * 接口响应失败
     */
    void requestFailed();

    /**
     * 开流成功
     */
    void startPreviewSuccess();

    /**
     * 拍摄页更多设置
     */
    void showMoreSetting();

    /**
     * 重启解码器
     */
    void restartMediaCodec();

    /**
     * 切换到延时摄影模式
     */
    void selectLapseMode(boolean needSendCmd);

    /**
     * 切换到慢动作模式
     */
    void selectSlowMotionMode(boolean needSendCmd);

    /**
     * 切换到录视频模式
     */
    void selectVideoMode(boolean needSendCmd);

    /**
     * 切换到模板拍摄模式
     */
    void selectStoryMode(boolean needSendCmd);

    /**
     * 切换到拍照模式
     */
    void selectPhotoMode(boolean needSendCmd);

    /**
     * 切换到长曝光模式
     */
    void selectLongExploreMode(boolean needSendCmd);

    /**
     * 切换到美颜模式
     */
    void selectBeauty(boolean needSendCmd);

    /**
     * 选择美颜滤镜
     */
    void chooseBeautyType();

    /**
     * 切换拍摄模式成功
     */
    void changeShotModeSuccess();

    /**
     * 设置延时摄影的值
     * */
    void setLapseValue(String value);
//    /**
//     * 拍照
//     */
//    void takePhoto();
//
//    /**
//     * 开始录视频
//     */
//    void startTakeVideo();
//
//    /**
//     * 结束录视频
//     */
//    void stopTakeVideo();

    /**
     * 开始录视频成功
     */
    void startTakeVideoSuccess(String uri, int reason);

    /**
     * 拍摄结果
     * @param image  缩略图对象
     */
    void takePhotoResult(MoImage image);

    /**
     * 拍摄结果
     * @param image 缩略图对象
     */
    void takeVideoResult(MoImage image);
    /**
     * 点击拍摄结束按钮时的回调
     * @param
     */
    void takeVideoResultImm();

//    /**
//     * 数码变焦
//     * @param level 变焦倍率
//     */
//    void digitalZoom(int level);
//
//    /**
//     * 自拍模式的切换
//     */
//    void rotateCamera(int type);
//
//    /**
//     * 开始自动跟踪
//     */
//    void startAutoTrack();
//
//    /**
//     * 关闭自动跟踪
//     */
//    void stopAutoTrack();
//
//    /**
//     * 跳转到相册页
//     */
//    void startAlbumActivity();

    /**
     * 开始画框跟踪
     * @param rectInfo
     */
    void startFingerTrack(int[] rectInfo);

    /**
     * 获取系统信息
     * @param systemInfo
     */
    void getSystemInfo(MoSystemInfo systemInfo);

    /**
     * 同步拍摄设置
     * @param shotSetting
     */
    void syncShotSettings(MoShotSetting shotSetting);
/**
     * 同步拍摄设置
     * @param shotSetting
     */
    void syncShotSettingsEx(MoSettingModel shotSetting);

    /**
     * 倒计时时间切换
     * @param delayTime
     */
    void changeDelayTime(int delayTime);

    /**
     * 拍摄倒计时照片
     */
    void takeDelayPhoto();

    /**
     * 拍摄倒计时照片
     */
    void takePhotoStart();

    /**
     * 隐藏控制云台的摇杆
     */
    void hideControlNavi(int visibility);

    /**
     * 更多设置
     */
    void startMoreSettingActivity();

    /**
     * 拍摄页的曝光值是否显示
     * @param visibility
     */
    void changeExploreVisibility(int visibility);

    /**
     * 曝光值
     * @param value
     */
    void setExploreValue(String value);

    void stopAutoTrack();

    void startAutoTrack();

    void setResolFps(int resolution, int fps);
    void setSlowMotionValue(int mSpeed, boolean refresh);
}
