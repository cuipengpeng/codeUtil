package com.test.xcamera.accrssory;

import android.util.Log;

import com.test.xcamera.managers.DataManager;

import java.util.Random;

/**
 * Created by smz on 2020/4/28.
 * <p>
 * 环形数组 用于缓存usb收到的数据 防止频繁的内存申请释放进行gc
 * 会造成收不到usb数据 或者usb数据发送不过来
 */

public class CircleQueue {
    //默认申请2M的缓存
    private final static int SIZE = (int) (1024 * 1024 * 2f);
    private int maxSize;
    private int front;
    private int tail;
    private byte[] arr;
    private int size;

    public CircleQueue() {
    }

    public void init(int arrMaxSize) {
        if (arrMaxSize == 0)
            arrMaxSize = SIZE;
        if (arr != null && (arr.length == arrMaxSize + 1) && (maxSize == arrMaxSize + 1)) {
            clear();
            return;
        }
        maxSize = arrMaxSize;
        maxSize += 1;
        arr = new byte[maxSize];
        front = 0;
        tail = 0;
        size = 0;
    }

    public void unInit() {
        arr = null;
        maxSize = 0;
        front = 0;
        tail = 0;
        size = 0;
    }

    public void log(String str) {
        Log.e("#####", str);
    }

    public void clear() {
        front = 0;
        tail = 0;
        size = 0;
    }

    public void release() {
        arr = null;
    }

    // 添加数据到队列
    public boolean put(byte[] data, int len) {
        if (size + len >= maxSize) {
            log("==>merge data err: cache is full");
            return false;
        }

        if (tail + len > maxSize) {
            int first = maxSize - tail;
            System.arraycopy(data, 0, arr, tail, first);
            tail = tail + len - maxSize;
            System.arraycopy(data, first, arr, 0, tail);
        } else {
            System.arraycopy(data, 0, arr, tail, len);
            tail += len;
        }
        if (tail == maxSize)
            tail = 0;

        size += len;
        return true;
    }

    public CacheArray.Cache get(int len) {
        return get(len, true);
    }

    public boolean remove(int len) {
        if (size == 0) {
            log("==>cache is empty");
            return false;
        }
        if (len > size)
            return false;

        if (front + len > maxSize) {
            int firstLen = maxSize - front;
            front = len - firstLen;
        } else {
            front += len;
        }
        if (front == maxSize)
            front = 0;

        size -= len;
        return true;
    }

    // 获取队列的数据，出队列
    public CacheArray.Cache get(int len, boolean remove) {

        if (len <= 0) {
            return null;
        }

        if (size == 0) {
            log("==>cache is empty");
            return null;
        }
        if (len > size)
            return null;

        int f = front;
        CacheArray.Cache cache = DataManager.getInstance().mCacheArray.get(len);

        if (f + len > maxSize) {
            int firstLen = maxSize - f;
            System.arraycopy(arr, f, cache.data, 0, firstLen);
            f = len - firstLen;
            System.arraycopy(arr, 0, cache.data, firstLen, f);
        } else {
            System.arraycopy(arr, f, cache.data, 0, len);
            f += len;
        }
        if (f == maxSize)
            f = 0;

        if (remove) {
            size -= cache.len;
            front = f;
        }
        return cache;
    }

    // 求出当前队列有效数据的个数
    public int size() {
        return this.size;
    }

    public static void test() {
        boolean debug = true;
        Random r = new Random();
        CircleQueue cq = new CircleQueue();
        cq.init(30);

        int crc = 0;
        long ccc = 0;
        Log.e("=====", "==>main");
        while (true) {
            int putLen = r.nextInt(10) + 1;
            if (cq.size + putLen < cq.maxSize) {
                byte[] bArr = new byte[putLen];
                for (int i = 0; i < bArr.length; i++)
                    bArr[i] = getData();
                if (!cq.put(bArr, bArr.length))
                    cq.log("==>put err\n");
            }

            int getLen = r.nextInt(10) + 1;
            if (cq.size != 0 || getLen <= cq.size) {
                byte[] outArr = cq.get(getLen).data;
                if (outArr != null) {
                    for (byte b : outArr) {
                        if (crc == 100) {
                            crc = 0;
                            if (ccc++ % 10000 == 0)
                                cq.log("ccc==>" + ccc);
                        }
                        if (crc++ != b) {
                            cq.log("err----------------");
                            return;
                        }
                    }
                }
            }

//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }
    }

    static byte count = 0;

    static byte getData() {
        if (count == 100)
            count = 0;
        return count++;
    }
}
