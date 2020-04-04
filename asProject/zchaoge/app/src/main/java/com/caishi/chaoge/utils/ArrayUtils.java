package com.caishi.chaoge.utils;

import android.util.SparseIntArray;

import java.util.ArrayList;

/**
 * array相关
 */

public class ArrayUtils {

    /**
     * 获取数组之和
     *
     * @param array
     * @return
     */
    public static float getTotal(float[] array) {
        if (array == null)
            return 0;
        float tempMax = 0;
        for (int i = 0; i < array.length; i++) {
            tempMax += array[i];
        }
        return tempMax;
    }

    /**
     * 获取数组中最大值
     *
     * @param array 数组
     * @return 最大值
     */
    public static double getMax(double[] array) {
        if (array == null || array.length == 1)
            return 0;
        double tempMax = 0;
        for (int i = 0; i < array.length; i++) {
            if (tempMax < array[i])
                tempMax = array[i];
        }
        return tempMax;
    }

    /**
     * //冒泡排序算法
     *
     * @param args 数组
     * @return 排序之后的数组
     */
    public static double[] bubbleSort(double[] args) {
        for (int i = 0; i < args.length - 1; i++) {
            for (int j = i + 1; j < args.length; j++) {
                if (args[i] > args[j]) {
                    double temp = args[i];
                    args[i] = args[j];
                    args[j] = temp;
                }
            }
        }
        return args;
    }

    /**
     * 获取double[] 数组的平均值
     *
     * @param array
     * @return
     */
    public static double getAverage(double[] array) {
        if (array == null || array.length == 1)
            return 0;
        double total = 0;
        for (int i = 0; i < array.length; i++) {
            total += array[i];
        }
        return total / array.length;
    }

    /**
     * 比较是否数量增加
     *
     * @param pre
     * @param cur
     * @return
     */
    public static boolean isAdd(SparseIntArray pre, SparseIntArray cur, int key) {
        // 同一对象
        if (pre == cur)
            return false;
        int preValue = 0;
        int curValue = 0;
        if (pre != null)
            preValue = pre.get(key);
        if (cur != null)
            curValue = cur.get(key);
        return preValue < curValue;
    }

    /**
     * 比较两个SparseIntArray是否key和值都相等
     *
     * @param sia1
     * @param sia2
     * @return
     */
    public static boolean isEqual(SparseIntArray sia1, SparseIntArray sia2) {
        if (sia1 == sia2)
            return true;
        if (sia1 == null || sia2 == null)
            return false;
        if (sia1.equals(sia2))
            return true;
        // 判断size是否相等
        if (sia1.size() != sia2.size())
            return false;
        // 判断key和value
        for (int i = 0; i < sia1.size(); i++) {
            int key = sia1.keyAt(i);
            int value = sia1.get(key);
            if (value != sia2.get(key))
                return false;
        }
        return true;
    }

    /**
     * 获取id在ids中的index
     *
     * @param ids
     * @param id
     * @return
     */
    public static int getIndex(ArrayList<Integer> ids, int id) {
        if (ids == null || ids.size() < 1 || id <= 0)
            return -1;
        for (int i = 0; i < ids.size(); i++) {
            if (ids.get(i) == id)
                return i;
        }
        return -1;
    }

    /**
     * 获取id在ids中的index
     *
     * @param ids
     * @param id
     * @return
     */
    public static int getIndex(int[] ids, int id) {
        if (ids == null || ids.length < 1 || id <= 0)
            return -1;
        for (int i = 0; i < ids.length; i++) {
            if (ids[i] == id)
                return i;
        }
        return -1;
    }

    /**
     * 获取key在keys中的index
     *
     * @param keys
     * @param key
     * @return
     */
    public static int getIndex(String[] keys, String key) {
        if (keys == null || keys.length < 1)
            return -1;
        for (int i = 0; i < keys.length; i++) {
            if (keys[i].equals(key))
                return i;
        }
        return -1;
    }
}
