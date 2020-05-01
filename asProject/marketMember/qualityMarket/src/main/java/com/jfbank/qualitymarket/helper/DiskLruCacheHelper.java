package com.jfbank.qualitymarket.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.jakewharton.disklrucache.DiskLruCache;
import com.jfbank.qualitymarket.AppContext;
import com.jfbank.qualitymarket.util.CommonUtils;
import com.jfbank.qualitymarket.util.ConvertUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 在Application onCreate中调用init初始化，调用put存储，get获取等。其他api见方法。
 * <p>
 * 功能：磁盘缓存帮助类<br>
 * 作者：赵海<br>
 * 时间： 2017/7/10 0010<br>.
 * 版本：1.2.0
 */
public class DiskLruCacheHelper {
    private static final String DIR_NAME = "diskCache";//缓存路径
    private static final int MAX_COUNT = 100 * 1024 * 1024;//默认最大缓存
    private static final int DEFAULT_APP_VERSION = 1;//app默认版本号

    private static final String TAG = "DiskLruCacheHelper";
    private static DiskLruCache mDiskLruCache;//缓存对象

    /**
     * 初始化 磁盘对象  在Application onCreate中
     *
     * @param context 上下文
     */
    public static void init(Context context) {
        mDiskLruCache = generateCache(context, DIR_NAME, MAX_COUNT);
    }

    /**
     * /**
     * 初始化 磁盘对象  在Application onCreate中
     *
     * @param context 上下文
     * @param dirName 目录名称
     */
    public static void init(Context context, String dirName) {
        mDiskLruCache = generateCache(context, dirName, MAX_COUNT);
    }

    /**
     * 初始化 磁盘对象  在Application onCreate中
     *
     * @param context  上下文
     * @param dirName  目录名称
     * @param maxCount 最大缓存
     */
    public static void init(Context context, String dirName, int maxCount) {
        mDiskLruCache = generateCache(context, dirName, maxCount);
    }

    /**
     * 初始化 磁盘对象  在Application onCreate中
     *
     * @param dir 文件路径
     */
    public static void init(File dir) {
        mDiskLruCache = generateCache(null, dir, MAX_COUNT);
    }

    /**
     * 初始化 磁盘对象  在Application onCreate中
     *
     * @param context 上下文
     * @param dir     文件路径
     */
    public static void init(Context context, File dir) {
        mDiskLruCache = generateCache(context, dir, MAX_COUNT);
    }

    /**
     * 初始化 磁盘对象  在Application onCreate中
     *
     * @param context  上下文
     * @param dir      文件路径
     * @param maxCount 最大缓存
     */
    public static void init(Context context, File dir, int maxCount) {
        mDiskLruCache = generateCache(context, dir, maxCount);
    }

    /**
     * 初始化缓存对象
     *
     * @param context  上下文
     * @param dir      文件路径
     * @param maxCount 最大缓存
     * @return
     */
    private static DiskLruCache generateCache(Context context, File dir, int maxCount) {
        try {
            int appVersion = context == null ? DEFAULT_APP_VERSION : Integer.parseInt(CommonUtils.getVersionCode(context));//获取版本号
            //开启缓存，并创建缓存框架
            DiskLruCache diskLruCache = DiskLruCache.open(
                    dir,
                    appVersion,
                    DEFAULT_APP_VERSION,
                    maxCount);

            return diskLruCache;
        } catch (IOException e) {//异常，返回null
            return null;
        }
    }

    private static ExecutorService putService;

    public static ExecutorService putCacheThread() {
        if (putService == null) {
            synchronized (Executors.class) {
                putService = Executors.newFixedThreadPool(15);
            }
        }
        return putService;
    }
    private static ExecutorService getService;
    public static ExecutorService getCacheThread() {
        if (getService == null) {
            synchronized (Executors.class) {
                getService = Executors.newFixedThreadPool(5);
            }
        }
        return getService;
    }
    /**
     * 获取缓存对象
     *
     * @param context  上下文
     * @param dirName  文件名称
     * @param maxCount 最大缓存
     * @return
     */
    private static DiskLruCache generateCache(Context context, String dirName, int maxCount) {

        //开启缓存，并创建缓存框架
        return generateCache(context, getDiskCacheDir(context, dirName), maxCount);
    }
    // =======================================
    // ============== String 数据 读写 =============
    // =======================================

    /**
     * 缓存存储
     *
     * @param key   键
     * @param value 值
     */
    public static void put(final String key, final String value) {
        putCacheThread().submit(new Runnable() {
            @Override
            public void run() {
                DiskLruCache.Editor edit = null; //
                BufferedWriter bw = null;
                try {//新建存储Value对象的edit，并存儲
                    edit = editor(key);
                    if (edit == null) return;
                    OutputStream os = edit.newOutputStream(0);
                    bw = new BufferedWriter(new OutputStreamWriter(os));
                    bw.write(value);
                    edit.commit();//write CLEAN
                } catch (IOException e) {//異常則，
                    e.printStackTrace();
                    try {
                        //釋放edit锁
                        edit.abort();//write REMOVE
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                } finally {
                    try {//关闭BufferedWriter
                        if (bw != null)
                            bw.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    /**
     * 获取缓存对象
     *
     * @param key 键值
     * @return String类型值
     */
    public static String getAsString(String key) {
        InputStream inputStream = null;
        try {
            //write READ
            inputStream = get(key);
            if (inputStream == null) return null;
            StringBuilder sb = new StringBuilder();
            int len = 0;
            byte[] buf = new byte[128];
            while ((len = inputStream.read(buf)) != -1) {
                sb.append(new String(buf, 0, len));
            }
            return sb.toString();


        } catch (IOException e) {
            e.printStackTrace();
            if (inputStream != null)
                try {
                    inputStream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
        }
        return null;
    }

    /**
     * 缓存存储
     *
     * @param key        键
     * @param jsonObject 值
     */
    public static void put(String key, JSONObject jsonObject) {
        put(key, jsonObject.toString());
    }

    /**
     * 获取缓存
     *
     * @param key 键
     * @return 返回JSONObject
     */
    public static JSONObject getAsJson(String key) {
        String val = getAsString(key);
        try {
            if (val != null)
                return new JSONObject(val);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    // =======================================
    // ============ JSONArray 数据 读写 =============
    // =======================================

    /**
     * 缓存存储
     *
     * @param key       键
     * @param jsonArray 值
     */
    public static void put(String key, JSONArray jsonArray) {
        put(key, jsonArray.toString());
    }

    public JSONArray getAsJSONArray(String key) {
        String JSONString = getAsString(key);
        try {
            JSONArray obj = new JSONArray(JSONString);
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // =======================================
    // ============== byte 数据 读写 =============
    // =======================================

    /**
     * 缓存存储  --byte数据 到 缓存中
     *
     * @param key   保存的key
     * @param value 保存的数据
     */
    public static void put(final String key, final byte[] value) {
        putCacheThread().submit(new Runnable() {
            @Override
            public void run() {
                OutputStream out = null;
                DiskLruCache.Editor editor = null;
                try {
                    editor = editor(key);
                    if (editor == null) {
                        return;
                    }
                    out = editor.newOutputStream(0);
                    out.write(value);
                    out.flush();
                    editor.commit();//write CLEAN
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        editor.abort();//write REMOVE
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                } finally {
                    if (out != null) {
                        try {
                            out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

    }

    /**
     * 获取缓存
     *
     * @param key 健
     * @return 返回二进制
     */
    public static byte[] getAsBytes(String key) {
        byte[] res = null;
        InputStream is = get(key);
        if (is == null) return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            byte[] buf = new byte[256];
            int len = 0;
            while ((len = is.read(buf)) != -1) {
                baos.write(buf, 0, len);
            }
            res = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }


    // =======================================
    // ============== 序列化 数据 读写 =============
    // =======================================

    /**
     * 存储对象
     *
     * @param key   键
     * @param value 值（序列化对象Serializable）
     */
    public static void put(final String key, final Serializable value) {
        putCacheThread().submit(new Runnable() {
            @Override
            public void run() {
                DiskLruCache.Editor editor = editor(key);
                ObjectOutputStream oos = null;
                if (editor == null) return;
                try {
                    OutputStream os = editor.newOutputStream(0);
                    oos = new ObjectOutputStream(os);
                    oos.writeObject(value);
                    oos.flush();
                    editor.commit();
                } catch (IOException e) {
                    e.printStackTrace();
                    try {
                        editor.abort();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                } finally {
                    try {
                        if (oos != null)
                            oos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 获取Serializable对象
     *
     * @param key 键
     * @param <T>
     * @return
     */
    public static <T> T getAsSerializable(String key) {
        T t = null;
        InputStream is = get(key);
        ObjectInputStream ois = null;
        if (is == null) return null;
        try {
            ois = new ObjectInputStream(is);
            t = (T) ois.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ois != null)
                    ois.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return t;
    }

    /**
     * 存储列表
     *
     * @param key   键
     * @param value 值
     * @param <T>   泛型
     */
    public static <T> void put(String key, List<T> value) {
        if (!CommonUtils.isEmptyList(value)) {
            put(key, new Gson().toJson(value));
        }
    }

    /**
     * 返回序列化对象列表
     *
     * @param key   键
     * @param clazz 序列化对象数组[]class
     * @param <T>
     * @return
     */
    public static <T> List<T> getAsSerializableList(String key, Class<T[]> clazz) {
        String value = getAsString(key);
        if (TextUtils.isEmpty(value)) {
            return new ArrayList<>();
        } else {
            return Arrays.asList(new Gson().fromJson(value, clazz));
        }
    }

    // =======================================
    // ============== bitmap 数据 读写 =============
    // =======================================

    /**
     * 存储bitmap
     *
     * @param key    键
     * @param bitmap 值
     */
    public static void put(String key, Bitmap bitmap) {
        put(key, ConvertUtils.bitmap2Bytes(bitmap));
    }

    /**
     * 获取bitmap
     *
     * @param key 键
     * @return 值
     */
    public static Bitmap getAsBitmap(String key) {
        try {
            byte[] bytes = getAsBytes(key);
            if (bytes == null) return null;
            return ConvertUtils.bytes2Bitmap(bytes);
        } catch (Exception e) {
            return null;
        }
    }

    // =======================================
    // ============= drawable 数据 读写 =============
    // =======================================

    /**
     * 存储 Drawable
     *
     * @param key   键
     * @param value 值
     */
    public static void put(String key, Drawable value) {
        put(key, ConvertUtils.drawable2Bitmap(value));
    }

    /**
     * 获取Drawable
     *
     * @param key 键
     * @return
     */
    public static Drawable getAsDrawable(String key) {
        byte[] bytes = getAsBytes(key);
        if (bytes == null) {
            return null;
        }
        return ConvertUtils.bitmap2Drawable(ConvertUtils.bytes2Bitmap(bytes));
    }

    // =======================================
    // ============= other methods =============
    // =======================================

    /**
     * 删除存储缓存
     *
     * @param key 键
     * @return
     */
    public static boolean remove(String key) {
        if (mDiskLruCache == null)
            return false;
        try {//删除存储
            key = ConvertUtils.hashKeyForDisk(key);
            return mDiskLruCache.remove(key);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 关闭系缓存，在退出app调用
     *
     * @throws IOException
     */
    public static void close() throws IOException {
        if (mDiskLruCache == null)
            return;
        mDiskLruCache.close();
    }

    /**
     * 删除缓存，并开启新的缓存。
     */
    public static void delete() {
        if (mDiskLruCache == null)
            return;
        try {
            mDiskLruCache.delete();
            mDiskLruCache = generateCache(AppContext.mContext, DIR_NAME, MAX_COUNT);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

    }

    /**
     * 强制将缓冲区中的数据到文件系统
     *
     * @throws IOException
     */
    public static void flush() throws IOException {
        if (mDiskLruCache == null)
            return;
        mDiskLruCache.flush();
    }

    /**
     * 缓存是否关闭
     *
     * @return
     */
    public static boolean isClosed() {
        if (mDiskLruCache == null)
            return false;
        return mDiskLruCache.isClosed();
    }

    /**
     * 缓存大小
     *
     * @return
     */
    public static long size() {
        long size = 0;
        DiskLruCache mDiskLruCache = generateCache(AppContext.mContext, DIR_NAME, MAX_COUNT);
        if (mDiskLruCache == null) {
            return size;
        }
        size = mDiskLruCache.size();
        try {
            mDiskLruCache.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return size;
    }

    public static void setMaxSize(long maxSize) {
        if (mDiskLruCache == null)
            return;
        mDiskLruCache.setMaxSize(maxSize);
    }

    public static File getDirectory() {
        if (mDiskLruCache == null)
            return null;
        return mDiskLruCache.getDirectory();
    }

    public static long getMaxSize() {
        if (mDiskLruCache == null)
            return -1;
        return mDiskLruCache.getMaxSize();
    }


    // =======================================
    // ===遇到文件比较大的，可以直接通过流读写 =====
    // =======================================

    /**
     * 获取缓存Editor
     *
     * @param key 键
     * @return
     */
    public static DiskLruCache.Editor editor(String key) {
        if (mDiskLruCache == null)//为空，则不返回null
            return null;
        try {//
            key = ConvertUtils.hashKeyForDisk(key);
            //wirte DIRTY
            DiskLruCache.Editor edit = mDiskLruCache.edit(key);
            //edit maybe null :the entry is editing
            if (edit == null) {
                Log.w(TAG, "the entry spcified key:" + key + " is editing by other . ");
            }
            return edit;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * 读取缓存
     *
     * @param key 键值
     * @return InputStream
     */
    public static InputStream get(String key) {
        if (mDiskLruCache == null)
            return null;
        try {
            //初始化Snapshot对象，获取key对应的实体。InputStream
            DiskLruCache.Snapshot snapshot = mDiskLruCache.get(ConvertUtils.hashKeyForDisk(key));
            if (snapshot == null) //not find entry , or entry.readable = false
            {//初始化Snapshot对象为空
                Log.e(TAG, "not find entry , or entry.readable = false");
                return null;
            }
            //不为空，则读取
            return snapshot.getInputStream(0);

        } catch (IOException e) {//读取异常
            e.printStackTrace();
            return null;
        }

    }

    public static boolean isContainKey(String key) {
        if (mDiskLruCache == null)
            return false;
        try {
            //初始化Snapshot对象，获取key对应的实体。InputStream
            DiskLruCache.Snapshot snapshot = mDiskLruCache.get(ConvertUtils.hashKeyForDisk(key));
            if (snapshot == null) //not find entry , or entry.readable = false
            {//初始化Snapshot对象为空
                Log.e(TAG, "not find entry , or entry.readable = false");
                return false;
            } else {
                return true;
            }

        } catch (IOException e) {//读取异常
            e.printStackTrace();
            return false;
        }

    }

    /**
     * 缓存大小
     *
     * @return
     */
    public static String getCacheSize() {
        return ConvertUtils.byteUnit(size());
    }

    // =======================================
    // ============== 序列化 数据 读写 =============
    // =======================================

    /**
     * 获取缓存文件
     *
     * @param context    上下文
     * @param uniqueName 文件名称
     * @return
     */
    public static File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }

}