package com.test.xcamera.utils;

import android.os.Environment;

import com.test.xcamera.BuildConfig;
import com.test.xcamera.R;
import com.test.xcamera.application.AiCameraApplication;

import java.io.File;

/**
 * creat by mz  2019.9.24
 */
public class Constants {

    public static String base_url = BuildConfig.API_HOST;//需要最后加"/"
//    public static String base_url = "http://m.camera.moxiang.tv/";//需要最后加"/"
//    public static String base_url = "http://m.camera.xmceshi.cn:1080/";//需要最后加"/"

    public static String apk_down_url = "api/app/package/";

    public static String mRootPath = Environment.getExternalStorageDirectory().getPath();
    public static String file_separator = File.separator;
    public static String mAppFilePath = "MoAiCamera"; //文件夹  项目文件夹
    //    public static String hard_down_file = "hardFile";//固件版本文件名字
    public static String apkfile = "apk_file.apk";//apk文件名字

    public static boolean isConnect;// 增加是否连接usb 是否成功的标记

    public static String mFileDirector = mRootPath + file_separator + mAppFilePath + file_separator;

    public static String hard_down_dir = mRootPath + file_separator + mAppFilePath + "/";//+ hard_down_file;//固件版本详细路径
    public static String apk_down_filedetail = mRootPath + file_separator + mAppFilePath + "/";//+ System.currentTimeMillis() + apkfile;//app版本详细路径

    public static final String cacheDir = "TodayWonderfulCache"; //今日精彩
    public static final String sampleVideoDir = "SampleVideo"; //今日精彩
    public static final String appDir = AiCameraApplication.getContext().getResources().getString(R.string.app_gallery); //
    public static final String myGalleryDir = AiCameraApplication.getContext().getResources().getString(R.string.morangeProduct); //
    public static final String dyGalleryDir = "dyGallery"; //
    public static final String storageLocalPath = Environment.getExternalStorageDirectory().getPath() + "/" + mAppFilePath + "/"; //
    public static final String appLocalPath = storageLocalPath + appDir; //
    public static final String myGalleryLocalPath = storageLocalPath + myGalleryDir; //
    public static final String dyGalleryLocalPath = storageLocalPath + dyGalleryDir; //
    public static final String cacheLocalPath = storageLocalPath + cacheDir; //
    public static final String sampleVideoLocalPath = storageLocalPath + cacheDir + "/" + sampleVideoDir; //
    public static final int TOKEN_INVALID = 20002;
    public static final String markPngAndVideo = mRootPath + file_separator + "markdir/";
    public static String MediaTypeImag = "imag/*";
    public static String MediaTypeVideo = "video/*";

    public static String getFileIdToUrl(String fileId) {
        return Constants.base_url + "api/file/" + fileId;
    }


    public static String template_img_url = "file:///android_asset/";

    public static String template_path = "segmenttemplate/";

}
