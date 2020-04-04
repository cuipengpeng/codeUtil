package com.caishi.chaoge.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.caishi.chaoge.utils.ConstantUtils.FILE_BASE_PATH;
import static com.caishi.chaoge.utils.ConstantUtils.VIDEO_PATH;


public class FileUtils extends com.rd.lib.utils.FileUtils {
    private static final String TAG = "FileUtils";
    public static final String TTF_EXTENSION = "ttf";

    /**
     * 保存方法
     */
    public static String saveBitmap(Bitmap bm) {
        String path = Environment.getExternalStorageDirectory().getPath() + "/" +
                FILE_BASE_PATH + VIDEO_PATH;
        File f = new File(path);
        if (!f.exists()) {
            f.mkdirs();
        }
        String fileName = path + System.currentTimeMillis() + ".png";
        try {
            FileOutputStream out = new FileOutputStream(fileName);
            bm.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            Log.i(TAG, "已经保存===" + fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileName;
    }

    /**
     * 获取全部文件列表
     *
     * @return
     */
    public static List<File> getAllFiles(String path) {

        List<File> list = new ArrayList<>();
        File rootFile = new File(path);
        if (!rootFile.exists()) {
            rootFile.mkdirs();
        } else {
            File[] files = rootFile.listFiles();
            if (files != null) {
                for (File file : files) {
                    list.add(file);
                }
            }
        }
        return list;
    }

    /**
     * 判断网络文件是否存在本地
     *
     * @param path 本地文件夹地址
     * @param url  网络地址
     * @return 存在返回保存地址  不存在返回null
     */
    public static String fileIsExists(String path, String url) {
        String filePath = null;
        if (Utils.isEmpty(url)) {
            return "1";//等于空说明不需要下载
        }
        String name = url.substring(url.lastIndexOf("/") + 1);
        List<File> allFiles = getAllFiles(path);
        for (int i = 0; i < allFiles.size(); i++) {
            if (allFiles.get(i).getName().equals(name)) {
                filePath = allFiles.get(i).getAbsolutePath();
            }
        }
        return filePath;
    }

    /**
     * 获取全部字体文件列表
     *
     * @return
     */
    public static List<File> getFontFiles(String path) {

        List<File> list = new ArrayList<>();
        File rootFile = new File(path);
        if (!rootFile.exists()) {
            rootFile.mkdirs();
        } else {
            File[] files = rootFile.listFiles();
            if (files != null) {
                for (File file : files) {
                    list.add(file);
                }
            }
        }
        return list;
    }

    /**
     * 删除所有文件
     *
     * @return
     */
    public static void deleteAllFiles(String fileBasePath) {
        File rootFile = new File(fileBasePath);
        if (!rootFile.exists()) {
        } else {
            File[] files = rootFile.listFiles();
            if (files != null && files.length > 0)
                for (File file : files) {
                    file.delete();
                }
        }
    }

    /**
     * 删除文件
     *
     * @return
     */
    public static boolean deleteFile(String fileBasePath) {
        File rootFile = new File(fileBasePath);
        if (rootFile.exists()) {
            return rootFile.delete();
        }
        return false;
    }


    /**
     * 创建文件夹
     */
    public static void createFileMkdir(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }

    }

    //判断文件是否存在
    public static boolean fileIsExists(String strFile) {
        try {
            File f = new File(strFile);
            if (!f.exists()) {
                return false;
            }

        } catch (Exception e) {
            return false;
        }

        return true;
    }

    /**
     * 获取系统所有图片
     *
     * @param context 上下文
     * @return 图片list
     */
    public static List<String> getSystemPhotoList(Context context) {
        List<String> result = new ArrayList<>();
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        if (cursor == null || cursor.getCount() <= 0) return null; // 没有图片
        while (cursor.moveToNext()) {
            int index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            String path = cursor.getString(index); // 文件地址
            File file = new File(path);
            if (file.exists()) {
                result.add(path);
            }
        }

        return result;
    }

    public static float getDuration(String path) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mediaPlayer.getDuration() / 1000f;

    }

    public static File getExternalFilesDirEx(Context context, String type) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File ef = context.getExternalFilesDir(type);
            if (ef != null && ef.isDirectory()) {
                return ef;
            }
        }
        return new File(Environment.getExternalStorageDirectory(), type);
    }

}
