package com.test.xcamera.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.test.xcamera.bean.MoAlbumItem;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by zll on 2019/5/10.
 */

public class FileUtil {
    private static final String MEDIA_DIR = "DCIM/XiaoMo";
    private static SimpleDateFormat mFormatter = new SimpleDateFormat(
            "yyyyMMddHHmmssSSS", Locale.getDefault());
    public static final String path = Environment.getExternalStorageDirectory().getPath() + "/MoTest";
    private final static String FILTER_FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MO_AICamera/";

    public static String getCurrentFilterFilePath() {
        return currentFilePath;
    }


    private static String currentFilePath;

    public static byte[] readFile(String file) {
        // 需要读取的文件，参数是文件的路径名加文件名
        // 以字节流方法读取文件

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            // 设置一个，每次 装载信息的容器
            byte[] buffer = new byte[1024 * 100];
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            // 开始读取数据
            int len = 0;// 每次读取到的数据的长度
            while ((len = fis.read(buffer)) != -1) {// len值为-1时，表示没有数据了
                // append方法往sb对象里面添加数据
                outputStream.write(buffer, 0, len);
            }
            // 输出字符串
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void writeFileToSDCard(String folderPath, byte[] buffer, final String fileName, final boolean append, final boolean autoLine, boolean replace) {
//        System.out.println("writeFileToSDCard buffer = " + buffer);

        File fileDir = new File(folderPath);
        if (!fileDir.exists()) {
            if (!fileDir.mkdirs()) {
                return;
            }
        }
        File file;
        if (TextUtils.isEmpty(fileName)) {
            file = new File(folderPath + "/app_log.txt");
        } else {
            file = new File(folderPath + "/" + fileName);
        }
        RandomAccessFile raf = null;
        FileOutputStream out = null;
        try {
            if (append) {
                raf = new RandomAccessFile(file, "rw");
                if (replace && file.length() > 2) {
                    raf.seek(file.length() - 2);
                } else {
                    raf.seek(file.length());
                }
                if (autoLine && file.length() > 0) {
                    raf.write("\r\n".getBytes());
                }
                raf.write(buffer);
            } else {
                out = new FileOutputStream(file);
                out.write(buffer);
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (raf != null) {
                    raf.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 下载图片的
     *
     * @param folderPath
     * @param fileName
     * @param data
     * @return
     */
    public static String writeFileToSDCard(String folderPath, String fileName, byte[] data) {
        File fileDir = new File(folderPath);
        if (!fileDir.exists()) {
            if (!fileDir.mkdirs()) {
                return "";
            }
        }
        File file;
        file = new File(folderPath + "/" + fileName);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            out.write(data);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file.getAbsolutePath();
    }

    public interface WriteFile {
        void ok();
    }

    /**
     * 下载视频文件的
     *
     * @param folderPath
     * @param fileName
     * @param data
     * @param offset
     * @param size
     * @return
     */
    public static String writeMp4ToSDCard(String folderPath, String fileName, byte[] data, int offset, int size) {
        File fileDir = new File(folderPath);
        if (!fileDir.exists()) {
            if (!fileDir.mkdirs()) {
                return "";
            }
        }
        File file;
        file = new File(folderPath + "/" + fileName);

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            FileChannel ch = out.getChannel();
            ch.position(offset);
            ch.write(ByteBuffer.wrap(data));

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null && file.length() == size) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("file ==" + file.length());
        return file.getAbsolutePath();
    }

    public static String writeFileToSDCard(String filePath, byte[] data) {
//        File fileDir = new File(folderPath);
//        if (!fileDir.exists()) {
//            if (!fileDir.mkdirs()) {
//                return "";
//            }
//        }
        File file;
        file = new File(filePath);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            out.write(data);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file.getAbsolutePath();
    }

    /**
     * 创建文件名
     *
     * @param extension
     * @return
     */
    public static String createMediaFilePath(String extension) {
        String filePath = null;
        boolean isVideo = ".mp4".equals(extension);
        String root_path = getMediaDirectory(isVideo);
        if (root_path != null) {
            Date date = new Date(System.currentTimeMillis());
            String fileName = mFormatter.format(date) + extension;
            File root = new File(root_path + "/");
            if (!root.exists()) {
                root.mkdirs();
            }
            filePath = root.getAbsolutePath() + "/" + fileName;
        }
        return filePath;
    }

    /**
     * @return
     */
    public static String getMediaDirectory(boolean isVideo) {
        if (Environment.getExternalStorageDirectory() == null) {
            return null;
        }
        String userId;
        if (isVideo) {
            userId = "video";
        } else {
            userId = "image";
        }
        String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + MEDIA_DIR + "/" + userId;
        return dir;
    }

    public static void sendScanBroadcast(Context context, final String file) {
        if (context == null) return;
        if (TextUtils.isEmpty(file)) return;
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.parse("file://" + file));
        context.sendBroadcast(intent);

    }

    public static String getBeautyPhotoPath() {
        return FILTER_FILE_PATH;
    }

    public static String getBeautyPhotoName() {
        return System.currentTimeMillis() + ".jpg";
    }

    /**
     * 创建滤镜视频文件
     *
     * @return
     */
    public static File getFilterVideoFile() {
        currentFilePath = Constants.appLocalPath + "/" + System.currentTimeMillis() + ".mp4";
        File file = new File(currentFilePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    /**
     * 删除选中的照片
     *
     * @param context
     * @param
     * @return
     */
    public static ArrayList<MoAlbumItem> deleteMedias(final Context context, final ArrayList<MoAlbumItem> albumItems,
                                                      final Handler handler, final int deleteing, final int deleteOk) {
        new Thread() {
            @Override
            public void run() {
                File file = null;
                long sleepTime = 0;
                for (int i = 0; i < albumItems.size(); i++) {
                    MoAlbumItem moAlbumItem = albumItems.get(i);
                    if (!moAlbumItem.getmType().equals("video")) {
                        String thumbnailFileUri = albumItems.get(i).getmThumbnail().getmUri();
                        file = new File(thumbnailFileUri);
                        if (file.exists()) {
                            boolean b = file.delete();
                            Log.d("deleteMedias", "result: " + b + "   " + thumbnailFileUri
                                    + "  is file " + file.isFile() + "  is directory " + file.isDirectory());
                        }
                        sleepTime = 50;
                    } else {
                        String videoPath = albumItems.get(i).getmThumbnail().getmUri();
                        if (videoPath.startsWith("file:///")) {
                            videoPath = videoPath.replace("file:///", "/");
                        }
                        file = new File(videoPath);
                        if (file.exists()) {
                            boolean b = file.delete();
                            Log.d("deleteMedias", "result: " + b + "   " + videoPath
                                    + "  is file " + file.isFile() + "  is directory " + file.isDirectory());
                        }
                        sleepTime = 100;
                    }
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                            Uri.parse("file://" + file.getAbsolutePath())));

                    Message message = new Message();
                    message.obj = i;
                    message.what = deleteing;
                    handler.sendMessage(message);
                }

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message message = new Message();
                message.obj = albumItems;
                message.what = deleteOk;
                handler.sendMessage(message);
            }
        }.start();
        return albumItems;
    }


    public static void writeLog(byte[] log) {
        File logFile = getLogFile();
        if (logFile != null) {
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(logFile);
                fileOutputStream.write(log);
                fileOutputStream.flush();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                        fileOutputStream = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private static File getLogFile() {
        String logFilePath = Constants.mRootPath + "/log_log_test.txt";
        File file = new File(logFilePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }
}
