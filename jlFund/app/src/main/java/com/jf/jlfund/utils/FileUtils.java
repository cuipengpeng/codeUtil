package com.jf.jlfund.utils;

import android.content.res.AssetManager;
import android.os.Environment;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.jf.jlfund.base.BaseApplication;
import com.jf.jlfund.bean.SearchResultBean;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;

import static com.jf.jlfund.base.BaseApplication.getContext;


/**
 * app 文件管理工具
 */
public class FileUtils {
    //判断SD卡是否挂载
    public static boolean isSDCardAvailable() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable())//不可卸载的外部外部存储
            return true;
        return false;
    }


    //获取SD卡路径
    public static String getExternalStoragePath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }


    //根据path创建文件夹
    public static boolean createDirs(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists() || !file.isDirectory()) {
            return file.mkdirs();
        }
        return true;
    }


    //**********************  手机缓存相关操作 ****************************************//

    //清除所有文件(缓存,files)
    public static void clearAll() {
        clearAllCache();
        clearAllFiles();
    }

    //获取全部缓存大小(手机的和sd卡的)
    public static String getTotalCacheSize() {
        long cacheSize;
        try {
            cacheSize = getFolderSize(getContext().getCacheDir());
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                cacheSize += getFolderSize(getContext().getExternalCacheDir());
            }
            return getFormatSize(cacheSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0KB";
    }

    //清除所有缓存(手机和SD卡)
    public static void clearAllCache() {
        deleteDir(getContext().getCacheDir());
        if (isSDCardAvailable()) {
            deleteDir(getContext().getExternalCacheDir());
        }
    }


    //获取全部长久保存的随应用卸载的files大小(手机和sd卡)
    public static String getTotalFilesSize() {
        long cacheSize;
        try {
            cacheSize = getFolderSize(getContext().getFilesDir());
            if (isSDCardAvailable()) {
                cacheSize += getFolderSize(getDownloadAppDir());
                cacheSize += getFolderSize(getPictureAppDir());
                cacheSize += getFolderSize(getDocumentsAppDir());
            }
            return getFormatSize(cacheSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0KB";
    }

    //清除所有长久保存的随应用卸载的files(手机和sd卡)
    public static void clearAllFiles() {
        deleteDir(getContext().getFilesDir());
        if (isSDCardAvailable()) {
            deleteDir(getDownloadAppDir());
            deleteDir(getPictureAppDir());
            deleteDir(getDocumentsAppDir());
        }
    }

    //删除文件
    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
        }
        return dir == null || dir.delete();
    }


    // 获取文件大小
    private static long getFolderSize(File file) throws Exception {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                // 如果下面还有文件
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);
                } else {
                    size = size + fileList[i].length();
                    LogUtils.i("file【 path >> " + fileList[i].getAbsolutePath() + "size >> " + fileList[i].length() + "  totalSize : " + size + " 】");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }


    //格式化单位
    private static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 0.1) {
//            return size + "B";
            return "0KB";
        }

        if (kiloByte < 1) {
            BigDecimal result0 = new BigDecimal(Double.toString(kiloByte));
            return result0.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
                + "TB";
    }

    //*****************************************************************************//


    //************************  随应用卸载  文件夹  ******************************//
    //获取临时缓存文件目录,缓存,没必要再区分那么详细了/Android/data
    //判断是否存在SD卡(root后可见/data/data)
    public static File getCacheDir() {
        if (isSDCardAvailable()) {
            return getContext().getExternalCacheDir();
        } else {
            return getContext().getCacheDir();
        }
    }

    //                   --------缓存  (需要什么缓存文件夹,依此类推写出方法就行,)-------     //
    //获取okhttp网络缓存目录
    public static File getHttpCacheDir() {
        if (isSDCardAvailable()) {
            String path = getContext().getExternalCacheDir() + File.separator + "okhttp";
            if (createDirs(path)) {
                return new File(path);
            } else {
                return getContext().getCacheDir();
            }
        } else {
            return getContext().getCacheDir();
        }
    }

    //获取错误日志保存目录
    public static File getCrashCacheDir() {
        if (isSDCardAvailable()) {
            String path = getContext().getExternalCacheDir() + File.separator + "crash";
            if (createDirs(path)) {
                return new File(path);
            } else {
                return getContext().getCacheDir();
            }
        } else {
            return getContext().getCacheDir();
        }
    }

    public static void clearCrashFile() {
        File crashFileDir = getCrashCacheDir();
        if (crashFileDir.exists()) {
            crashFileDir.delete();
        }
    }

    //获取图片缓存目录
    public static File getImageCacheDir() {
        if (isSDCardAvailable()) {
            String path = getContext().getExternalCacheDir() + File.separator + "image";
            if (createDirs(path)) {
                return new File(path);
            } else {
                return getContext().getCacheDir();
            }
        } else {
            return getContext().getCacheDir();
        }
    }


    //                -----   长久保存file-------//
    //获取下载目录
    public static File getDownloadAppDir() {
        return getAppDir(Environment.DIRECTORY_DOWNLOADS);
    }


    //获取文档目录
    public static File getDocumentsAppDir() {
        return getAppDir(Environment.DIRECTORY_DOCUMENTS);
    }


    //获取picture目录
    public static File getPictureAppDir() {
        return getAppDir(Environment.DIRECTORY_PICTURES);
    }

    //获取随app而生的目录，当SD卡存在时，获取SD卡上的file目录/Android/data
    //当SD卡不存在时，获取本地的file目录(root后可见/data/data)
    private static File getAppDir(String type) {
        if (isSDCardAvailable()) {
            return getContext().getExternalFilesDir(type);
        } else {
            return getContext().getFilesDir();
        }

    }

    //************************  永久保存  文件夹  ******************************************//
    //获取下载目录
    public static File getDownloadPhoneDir() {
        return getPhoneDir(Environment.DIRECTORY_DOWNLOADS);
    }

    //获取文档目录
    public static File getDocumentsPhoneDir() {
        return getPhoneDir(Environment.DIRECTORY_DOCUMENTS);
    }

    //获取picture目录
    public static File getPicturePhoneDir() {
        return getPhoneDir(Environment.DIRECTORY_PICTURES);
    }


    //获取手机的目录，当SD卡存在时，获取SD卡上的目录
    //当SD卡不存在时获取手机本录(root后地目可见 /data)
    private static File getPhoneDir(String type) {
        if (isSDCardAvailable()) {
            return Environment.getExternalStoragePublicDirectory(type);
        } else {
            ToastUtils.showLong("未安装内存卡!");
            return null;
        }
    }

    //*************************************************************************************//


    /**
     * 拷贝文件
     *
     * @param srcPath    原路径
     * @param targetPath 目标路径
     */
    public static void copyFile(String srcPath, String targetPath) {
        if (TextUtils.isEmpty(srcPath) || TextUtils.isEmpty(targetPath)) {
            return;
        }
        File srcFile = new File(srcPath);
        File targetFile = new File(targetPath);
        if (!srcFile.exists()) {
            ToastUtils.showShort("文件不存在");
            return;
        }
        if (!targetFile.exists())
            try {
                if (!targetFile.createNewFile()) {
                    ToastUtils.showShort("目标路径出错");
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(srcFile);
            fos = new FileOutputStream(targetFile);
            BufferedInputStream bis = new BufferedInputStream(fis);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            int len = 0;
            byte[] buf = new byte[2048];
            while ((len = bis.read(buf)) != -1) {
                bos.write(buf, 0, len);
            }
            bos.flush();
            bis.close();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(fis);
            close(fos);
        }
    }

    /**
     * 取搜索历史数据
     *
     * @return
     */
    public static void getSearchHistoryList(final OnReadFileFinishedListener onReadFileFinishedListener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                LRULinkedList<SearchResultBean.SearchResultItem> searchHistories = new LRULinkedList<>(10);
                File file = new File(getCacheDir(), "search_history.cache");
                try {
                    if (file.exists()) {
                        FileInputStream fis = new FileInputStream(file);
                        ObjectInputStream ois = new ObjectInputStream(fis);
                        searchHistories = (LRULinkedList<SearchResultBean.SearchResultItem>) ois.readObject();
                        if (onReadFileFinishedListener != null)
                            onReadFileFinishedListener.onReadFinished(searchHistories);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (onReadFileFinishedListener != null)
                        onReadFileFinishedListener.onReadFiled(e.getMessage());
                }
            }
        }).start();
    }

    public interface OnReadFileFinishedListener<T> {
        void onReadFinished(T result);

        void onReadFiled(String errorMsg);
    }

    /**
     * 存搜索历史数据
     *
     * @param list
     */
    public static void saveSearchHistoryList(final LRULinkedList<SearchResultBean.SearchResultItem> list) {
        LogUtils.d("begin save search history......");
        new Thread(new Runnable() {
            @Override
            public void run() {
                File file = new File(getCacheDir(), "search_history.cache");
                try {
                    if (file.exists()) {
                        file.delete();
                        file.createNewFile();
                    }
                    FileOutputStream fos = new FileOutputStream(file);
                    ObjectOutputStream oos = new ObjectOutputStream(fos);
                    oos.writeObject(list);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    /**
     * 将流写入文件
     *
     * @param inputStream
     * @param file
     * @param listener    写入完成的回调事件
     */
    public static void save(final InputStream inputStream, final File file, final FileWriteListener listener) {
        Log.e("zzzzzz", file.getAbsolutePath());
        if (file.exists()) {
            file.delete();
        }
        new WriteFileThread(inputStream, file, listener).start();
    }

    static class WriteFileThread extends Thread {
        InputStream inputStream;
        File file;
        FileOutputStream fos;
        FileWriteListener listener;

        public WriteFileThread(InputStream is, File file, FileWriteListener listener) {
            this.inputStream = is;
            this.file = file;
            this.listener = listener;
        }

        @Override
        public void run() {
            try {
                if (!file.exists()) {
                    file.createNewFile();
                }
                if (inputStream != null) {
                    fos = new FileOutputStream(file);
                    byte[] buf = new byte[1024];
                    int readCount;
                    while ((readCount = inputStream.read(buf)) != -1) {
                        fos.write(buf, 0, readCount);
                    }
                    if (listener != null)
                        listener.onWriteOver(file);
                }
            } catch (Exception e) {
                Log.e("WriteFileThread: EEEEE", e.toString());
                if (listener != null)
                    listener.onWriteFailed(e.getMessage());
            } finally {
                close(inputStream);
                close(fos);
            }
        }
    }

    public static void close(Closeable x) {
        if (x != null) {
            try {
                x.close();
            } catch (Exception e) {
                // skip
            }
        }
    }


    public interface FileWriteListener {
        void onWriteOver(File apkFile);

        void onWriteFailed(String errorMsg);
    }

    public static class ReadJsonFromAssertThread extends Thread {
        private String assertPath;
        private OnReadJsonListener onReadJsonListener;

        public ReadJsonFromAssertThread(String assertPath, OnReadJsonListener listener) {
            this.assertPath = assertPath;
            this.onReadJsonListener = listener;
        }

        @Override
        public void run() {
            StringBuilder jsonResult = new StringBuilder();
            try {
                AssetManager assetManager = BaseApplication.getContext().getAssets();
                BufferedReader bf = new BufferedReader(new InputStreamReader(
                        assetManager.open(assertPath)));
                String line;
                while ((line = bf.readLine()) != null) {
                    jsonResult.append(line);
                }
                if (onReadJsonListener != null) {
                    onReadJsonListener.onReadSuccess(jsonResult.toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
                if (onReadJsonListener != null) {
                    onReadJsonListener.onReadFailed(e.getMessage());
                }
            }
        }
    }

    /**
     * 删除无用安装包【可能是无用的或者损坏的】
     * 虽然并不推荐在BroadcastReceiver中执行或开启线程执行耗时操作
     * 但考虑到操作并不是前台必须的操作，即使线程中止也不会有任何的影响，经测试该线程执行时间均为0ms【！！】，且开启服务开销较大
     * 如有需要可后期优化
     */
    public static void deleteInstallPackage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File installDir = FileUtils.getDownloadAppDir();
                    if (installDir.exists() && installDir.isDirectory()) {
                        long startTime = SystemClock.currentThreadTimeMillis();
                        File[] apk = installDir.listFiles();
                        for (int i = 0; i < apk.length; i++) {
                            LogUtils.e("del --> " + apk[i].getAbsolutePath());
                            if (apk[i].getAbsolutePath().endsWith(".apk"))
                                apk[i].delete();
                        }
                        LogUtils.e("delInsallPackage >> Del Finished. use : " + (SystemClock.currentThreadTimeMillis() - startTime) + "ms");
                    }
                } catch (Exception ex) {
                    Log.e("deleteInstallPackage", "deleteInstallPackage.Exception >> " + ex.getMessage());
                }
            }
        }).start();
    }

    public interface OnReadJsonListener {
        void onReadSuccess(String jsonResult);

        void onReadFailed(String errorMsg);
    }
}
