package com.test.xcamera.accrssory;

import android.util.Log;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by smz on 2020/4/29.
 * <p>
 * 数据的对象池 用于缓存usb接收的一条数据 防止频繁创建、释放数组 造成的内存抖动
 */
public class CacheArray {
    private static final int MAX_SIZE = 17 * 10000;
    LRU<Integer, Cache[]> lru = new LRU<>(
            5, 0.75f, true);
    private Cache largeCache = null;
    private boolean isLargeCache = true;

    public void unInit() {
        lru.clear();
        largeCache = null;
    }

    public void clearLargeCache() {
//        Log.e("#####", "==>clearLargeCache  locked:" + (largeCache == null ? "null" : largeCache.locked));
        if (largeCache != null && !largeCache.locked)
            largeCache = null;
    }

    public Cache get(int len) {
        //对数据的长度取整 将取整后的长度为key
        int lenNum = String.valueOf(len).length();
        double num = Math.pow(10, lenNum);
        double head = len / num;
        int size = (int) ((head < 0.5 ? 0.5 : 1) * num);

        //用于全速下载时分配的缓存 缓存太大 只分配一块 如果被占用 则使用临时创建的数组
        if (len > 90 * 10000 && len < 120 * 10000 && isLargeCache) {
            if (largeCache == null) {
                try {
                    largeCache = new Cache(120 * 10000, true, true, len);
                } catch (OutOfMemoryError e) {
                    Log.e("=====", "create memory err：" + e.getMessage());
                    //分配内存失败处理
                    isLargeCache = false;
                    largeCache = null;
                    System.gc();
                }
                if (isLargeCache)
                    return largeCache;
            }
//            Log.e("#####", "largeCache==>" + len);
            if (largeCache.tryLock(len) && isLargeCache)
                return largeCache;
        }

        //缓存最大20 0000  防止占用内存过大 导致oom
        if (size >= MAX_SIZE)
            size = MAX_SIZE;
        if (len > MAX_SIZE) {
//            Log.e("#####", "max  len---==>" + len);
            return new Cache(len, false, true, len);
        }
        //每种长度的数据对多缓存三分Cache 最多10种
//        Log.e("=====", "lru==>" + lru.toString());
        synchronized (this) {
            if (lru.containsKey(size)) {
                Cache cache[] = lru.get(size);
                for (int i = 0; i < cache.length; i++) {
                    if (cache[i] != null) {
                        if (cache[i].tryLock(len)) {
                            return cache[i];
                        }
                    } else {
                        cache[i] = new Cache(size, true, true, len);
                        lru.put(size, cache);
                        return cache[i];
                    }
                }

            } else {
                Cache cache[] = new Cache[size == MAX_SIZE ? 1 : 3];
                cache[0] = new Cache(size, true, true, len);
                lru.put(size, cache);
                return cache[0];
            }
//            Log.e("#####", "len size==>" + len);
            return new Cache(len, false, true, len);
        }
    }

    public static class Cache {
        public byte[] data;
        public boolean locked;
        public int len;
        private boolean cached;
        private int tryCount;

        public Cache(int size, boolean cached, boolean lock, int len) {
            this.data = new byte[size];
            this.locked = lock;
            this.len = len;
            this.cached = cached;
        }

        private synchronized boolean tryLock(int len) {
//            tryCount++;
//            if (tryCount > 10) {
//                Log.e("#####", "tryLock==>" + toString());
//                tryCount = 0;
//                locked = false;
//                len = 0;
//            }

            if (!this.locked) {
                tryCount = 0;
                this.locked = true;
                this.len = len;
                return true;
            }
            return false;
        }

        public synchronized void clear() {
//            if (cached) {
//                Arrays.fill(data, (byte) 0);
            tryCount = 0;
            locked = false;
            len = 0;
//            }
        }

        @Override
        public String toString() {
            return "Cache{" +
                    "locked=" + locked +
                    ", len=" + len +
                    ", cached=" + cached +
                    ", tryCount=" + tryCount +
                    '}';
        }
    }
}

class LRU<K, V> extends LinkedHashMap<K, V> implements Map<K, V> {
    private static final long serialVersionUID = 1L;
    private static final int MAX_NUM = 5;

    public LRU(int initialCapacity,
               float loadFactor,
               boolean accessOrder) {
        super(initialCapacity, loadFactor, accessOrder);
    }

    /**
     * @param eldest
     * @return
     * @description 重写LinkedHashMap中的removeEldestEntry方法
     * 删除最不经常使用的元素
     * @author rico
     * @created 2017年5月12日 上午11:32:51
     * @see java.util.LinkedHashMap#removeEldestEntry(java.util.Map.Entry)
     */
    @Override
    protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
        // TODO Auto-generated method stub
        if (size() > MAX_NUM) {
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        LRU<Character, Integer> lru = new LRU<Character, Integer>(
                16, 0.75f, true);

        String s = "abcdefghijkl";
        for (int i = 0; i < s.length(); i++) {
            lru.put(s.charAt(i), i);
        }
        System.out.println("LRU中key为h的Entry的值为： " + lru.get('h'));
        System.out.println("LRU的大小 ：" + lru.size());
        System.out.println("LRU ：" + lru);
    }
}

