package com.test.xcamera.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import com.test.xcamera.application.AiCameraApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 文件工具
 * Created by smz on 2019/7/10.
 */
public class FileUtils {
    public static final String ATT_PATH = "att";// 附件
    private static final String APP_FILE_PATH = "carmer";
    private static final String DOCUMENT_PATH = "document";// 文档路径
    private static final String SEPARATOR = File.separator;// 分隔符
    private static final String FILE_PROVIDER_AUTHORITIES = "com.yunmo.cn";

    private static File mRootPath = Environment.getExternalStorageDirectory();
    private static String mAppFilePath = "XiaoMoCamera"; //文件夹  项目文件夹
    private static String mDocumentPath = "";
    private static String mImagesRootPath;
    private static String mImagesCompressPath;

    /**
     * 判断SDCard是否可用
     */
    public static boolean existSDCard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 项目根目录
     */
    public static String getAppFilePath() {
        if (TextUtils.isEmpty(mAppFilePath)) {
            String path = mRootPath + SEPARATOR + mAppFilePath;
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
        }
        return mAppFilePath;
    }


    /**
     * 创建文件夹 的文件
     *
     * @param
     * @return
     */
    public static String creatFile(String parentpath, String childrenpath) {
        String filePath = "";
        if (!TextUtils.isEmpty(parentpath) && TextUtils.isEmpty(childrenpath)) {
            File file = new File(parentpath);
            if (!file.exists()) {
                file.mkdirs();
            }
            File childern = new File(file, childrenpath);
            if (!childern.exists()) {
                try {
                    childern.createNewFile();

                    filePath = childern.getAbsolutePath();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return filePath;
    }

    /**
     * 判断是否存在此文件
     *
     * @param path
     * @return
     */
    public static boolean isExistFile(String path) {
        File file = new File(path);
        return file.exists();
    }

    /**
     * 创建项目根目录下的文件
     */
    private static String getDocumentPath() {
        if (TextUtils.isEmpty(mDocumentPath)) {
            mDocumentPath = getAppFilePath() + SEPARATOR + mDocumentPath;
            File file = new File(mDocumentPath);
            if (!file.exists()) {
                file.mkdirs();
            }
        }
        return mDocumentPath;
    }

    /**
     * 项目拍照图片根目录
     */
    public static String getImagesRootPath() {
        if (TextUtils.isEmpty(mImagesRootPath)) {
            mImagesRootPath = mRootPath + SEPARATOR + "MoCamera/images/";
            File file = new File(mImagesRootPath);
            if (!file.exists()) {
                file.mkdirs();
            }
        }
        return mImagesRootPath;
    }

    /**
     * 压缩图片保存路径
     */
    public static String getImagesCompressPath() {
        if (TextUtils.isEmpty(mImagesCompressPath)) {
            mImagesCompressPath = mRootPath + SEPARATOR + "DCIM/images/compress";
            File file = new File(mImagesCompressPath);
            if (!file.exists()) {
                file.mkdirs();
            }
        }
        return mImagesCompressPath;
    }

    /**
     * 是否已经下载apk
     */
    public static boolean isExistApkFile(String appFilePath) {
        File file = new File(appFilePath);
        return file.exists();
    }

    /**
     * 判断document下文件是否存在
     */
    public static boolean isExistsFile(String noName, String typeName) {
        File file = new File(getDocumentPath() + SEPARATOR + noName + SEPARATOR + typeName);
        return file.exists();
    }

    public static String getFilePath(String fileName) {
        return getDocumentPath() + SEPARATOR + fileName;
    }

    /**
     * 获取附件文件夹路径
     *
     * @return 返回附件文件夹路径
     */
    static String getPathOfDocument(String noName, String typeName) {
        if (TextUtils.isEmpty(noName)) {
            return "";
        }
        String path = getDocumentPath() + SEPARATOR + noName + SEPARATOR + typeName;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    static Intent openFile(String filePath) {
        if (filePath.endsWith(".ppt") || filePath.endsWith(".pptx") || filePath.endsWith(".pps") ||
                filePath.endsWith(".ppsx")) {
            // Android获取一个用于打开PPT文件的intent
            return getFileIntent(filePath, "application/vnd.ms-powerpoint");
        } else if (filePath.endsWith(".xlsx") || filePath.endsWith(".xlsm") || filePath.endsWith(".xltm") ||
                filePath.endsWith(".xlsb") || filePath.endsWith(".xlam") || filePath.endsWith(".xls")) {
            // Android获取一个用于打开Excel文件的intent
            return getFileIntent(filePath, "application/vnd.ms-excel");
        } else if (filePath.endsWith(".doc") || filePath.endsWith(".docx") || filePath.endsWith(".dot") ||
                filePath.endsWith("dotx") || filePath.endsWith("dotm")) {
            // Android获取一个用于打开Word文件的intent
            return getFileIntent(filePath, "application/msword");
        } else if (filePath.endsWith(".pdf")) {
            // Android获取一个用于打开PDF文件的intent
            return getFileIntent(filePath, "application/pdf");
        } else if (filePath.endsWith(".bmp") || filePath.endsWith(".jpg") || filePath.endsWith(".jpeg")
                || filePath.endsWith(".png") || filePath.endsWith("gif") || filePath.endsWith("tif")
                || filePath.endsWith("tiff")) {
            // Android获取一个用于打开图片文件的intent
            return getFileIntent(filePath, "image/*");
        } else if (filePath.endsWith(".txt")) {
            // Android获取一个用于打开文本文件的intent
            return getFileIntent(filePath, "text/plain");
        } else {
            // Android获取一个用于打开APK文件的intent
            return getFileIntent(filePath, "*/*");
        }
    }

    // Android获取一个用于打开文件的intent
    public static Intent getFileIntent(String param, String type) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(generateUriForFile(new File(param)), type);
        return intent;
    }

    /**
     * 7.0及以上共享文件问题
     */
    public static Uri generateUriForFile(File file) {
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(AiCameraApplication.mApplication, FILE_PROVIDER_AUTHORITIES, file);
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }

    /**
     * 删除文件
     */
    public static boolean deleteFile(String filePath) {
        boolean flag = false;
        if (!TextUtils.isEmpty(filePath)) {
            File file = new File(filePath);
            if (file.isFile()) { // 判断是否是文件
                flag = file.delete();
            }
        }
        return flag;
    }
    /**
     * 删除文件夹以及目录下的文件
     * @param   filePath 被删除目录的文件路径
     * @return  目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String filePath) {
        boolean flag = false;
        //如果filePath不以文件分隔符结尾，自动添加文件分隔符
        if (!filePath.endsWith(File.separator)) {
            filePath = filePath + File.separator;
        }
        File dirFile = new File(filePath);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        flag = true;
        File[] files = dirFile.listFiles();
        //遍历删除文件夹下的所有文件(包括子目录)
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                //删除子文件
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) break;
            } else {
                //删除子目录
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) break;
            }
        }
        if (!flag) return false;
        //删除当前空目录
        return true;
    }
    /**
     * 更新相册
     */
    public static void galleryAddPic(Context context, File file) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = generateUriForFile(file);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    public static void renameFile(String oldPath, String newPath) {
        if (TextUtils.isEmpty(oldPath)) {
            return;
        }

        if (TextUtils.isEmpty(newPath)) {
            return;
        }

        File file = new File(oldPath);
        file.renameTo(new File(newPath));
    }

    /**
     * 根据系统时间、前缀、后缀产生一个文件
     */
    public static File createFile(File folder, String prefix, String suffix) {
        if (!folder.exists() || !folder.isDirectory()) folder.mkdirs();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA);
        String filename = prefix + dateFormat.format(new Date(System.currentTimeMillis())) + suffix;
        return new File(folder, filename);
    }

    /**
     * 递归遍历文件夹中所有的文件
     *
     * @param dir
     * @param spance
     */
    public List<String> listFile(File dir, String spance) {
        List<String> list = new ArrayList<>();
        File[] files = dir.listFiles();   //列出所有的子文件
        for (File file : files) {
            if (file.isFile())//如果是文件，则输出文件名字
            {
                list.add(file.getName());
            } else if (file.isDirectory())//如果是文件夹，则输出文件夹的名字，并递归遍历该文件夹
            {
                listFile(file, "|--" + spance);//递归遍历
            }
        }
        return list;
    }

    /**
     * bimap 写入文件
     *
     * @param bitmap
     */
    public static String BitmapToFile(Bitmap bitmap) {
        String path = Constants.mFileDirector + System.currentTimeMillis() + ".jpeg";
        File file = new File(Constants.mFileDirector, System.currentTimeMillis() + ".jpeg");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            return path;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (bitmap != null)
                bitmap.recycle();
        }
        return "";
    }

    public static File getMusicSelectPath(String name, Context context) {
        return new File(getMusicPath(context) + File.separator + name);
    }

    public static String getMusicPath(Context context) {
        File file = new File(context.getExternalCacheDir().getAbsolutePath() + File.separator + "music");
        if (!file.exists()) {
            file.mkdir();
        }
        return file.getAbsolutePath();
    }
    public static String getProjectPath() {
        File file = new File(AiCameraApplication.getContext().getExternalFilesDir("project").getAbsolutePath() +File.separator+"video_edit");
        if (!file.exists()) {
            file.mkdir();
        }
        return file.getAbsolutePath();
    }

    /**
     * 格式化容量
     * <p>
     * 以最大单位 保持小数后一位
     */
    public static String formatSize(long size) {
        double GB = 1024.0 * 1024.0 * 1024.0;
        double MB = 1024.0 * 1024.0;
        double KB = 1024.0;
        if (size >= GB) {
            float data = (float) (size / GB);
            return String.format(Locale.ENGLISH, "%.2fG", data);
        } else if (size >= MB) {
            float data = Math.round((size / MB) * 10f) / 10f;
            return String.format(Locale.ENGLISH, "%.1fM", data);
        }
        return "0M";
    }

//    public static File getEffectPath(String name, Context context) {
//        return new File(getMusicPath(context) + File.separator + name);
//    }

    public static String getEffectPath(Context context) {
        File file = new File(context.getExternalCacheDir().getAbsolutePath() + File.separator + "dy_effect");
        if (!file.exists()) {
            file.mkdir();
        }
        return file.getAbsolutePath();
    }
}