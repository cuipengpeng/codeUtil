package com.caishi.chaoge.utils;


import android.os.Environment;

import com.caishi.chaoge.base.BaseApplication;
import com.rd.lib.utils.FileUtils;

/**
 * 常量类
 */

public class ConstantUtils {

    public static final String LOGIN_DOMAIN = "api.chaogevideo.com";

    public static final String UP = "UP";
    public static final String DOWN = "DOWN";

    public static final long SINGLE_TEXT_DURATION = 340;//单个文字的时长

    public static final int EDIT_MODE_CAPTION = 0;
    public static final int EDIT_MODE_STICKER = 1;
    public static final int EDIT_MODE_WATERMARK = 2;
    public static final int EDIT_MODE_THEMECAPTION = 3;
    //屏幕点击常量定义
    public final static int HANDCLICK_DURATION = 200;//点击时长，单位微秒
    public final static double HANDMOVE_DISTANCE = 10.0D;//touch移动距离，单位像素值

    public static String strCustomPath;

    static {
        strCustomPath = FileUtils.getExternalFilesDirEx(BaseApplication.getContext(), "chaoge").getAbsolutePath();
    }

    public static boolean ISFIRSTLAUNCH = true;
    /**
     * 是否有新版本
     */
    public static boolean IS_NEW_VERSION = false;
    //退出应用
    public static final String TAG_EXIT = "exit";
    /**
     * 请求超时时间
     */
    public static final int TIMEOUT = 10;

    //SharedPreferences


    //Activity flag

    /**
     * 在EditActivity中，区分是输入昵称还是输入签名
     */
    public static final String EDIT_FLAG = "editFlag";
    /**
     * 在EditActivity中，返回数据
     */
    public static final String EDIT_RETURN_DATA = "editReturnData";
    /**
     * 区分跳转LoginActivity的标记
     */
    public static final String LOGIN_FLAG = "loginFlag";


    /**
     * 本地文件路径
     */
    public static final String FILE_BASE_PATH = "ChaoGe/";
    /**
     * 本地模板文件地址
     */
    public static final String FILE_MODULE_PATH = "Module/";

    /**
     * 录音生成文件名字wav文件
     */
    public static final String USER_AUDIO_FILE = "chaoge_";

    /**
     * 生成视频的封面图路径
     */
    public static final String VIDEO_PATH = "Video/";
    /**
     * 生成视频的封面图的名字
     */
    public static final String SURFACE_PLOT = "surfacePlot";
    /**
     * 生成视频的保存位置
     */
    public static final String VIDEO_MP4_PATH = "Video/MP4/";


    /**
     * 下载音频的保存位置
     */
    public static final String DOWNLOAD_MP3_PATH = strCustomPath + "/" +
            FILE_BASE_PATH + "Download/MP3/";
    /**
     * 下载背景的保存位置
     */
    public static final String DOWNLOAD_BG_PATH = strCustomPath + "/" +
            FILE_BASE_PATH + "Download/Background/";
    /**
     * 下载图片的保存位置
     */
    public static final String DOWNLOAD_IMAGE_PATH = strCustomPath + "/" +
            FILE_BASE_PATH + "Download/Image/";

    /**
     * 下载字体的保存位置
     */
    public static final String DOWNLOAD_FONT_PATH = strCustomPath + "/" +
            FILE_BASE_PATH + "Download/font/";

    /**
     * 下载视频的保存位置
     */
    public static final String DOWNLOAD_MP4_PATH = strCustomPath + "/" +
            FILE_BASE_PATH + "Download/video/";
    /**
     * 生成视频的保存位置
     */
    public static final String CREATE_MP4_PATH = Environment.getExternalStorageDirectory().getPath() + "/" +
            FILE_BASE_PATH + "video/";

    public static String rootPath = FILE_BASE_PATH + "Audio";
    /**
     * 生成wav的保存位置
     */
    public final static String AUDIO_WAV_BASEPATH = "/" + rootPath + "/wav/";
    /**
     * 生成mp3的保存位置
     */
    public final static String AUDIO_MP3_BASEPATH = "/" + rootPath + "/mp3/";

    //Ctrl+Shift+U


    //全局配置文件名
    public static final String GLOBAL_CONFIG_FILE_NAME = "chaoGeGlobalConfig";
    public static final String KEY_OF_APP_START_AD = "appStartAdKey";
    public static final String KEY_OF_APP_START_AD_CLICK_URL = "appStartAdClickUrlKey";
    public static final String KEY_OF_APP_START_AD_ONLINE_TIME = "appStartAdOnlineTimeKey";
    public static final String KEY_OF_APP_START_AD_OFFLINE_TIME = "appStartAdOfflineTimeKey";
    public static final String KEY_OF_APP_START_AD_OFFLINE = "appStartAdOfflineKey";
    public static final String APP_ICON_LOCAL_STORE_KEY = "appIconLocalStoreKey";

}
