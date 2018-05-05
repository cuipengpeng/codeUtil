package com.test.bank.utils;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * Created by 55 on 2017/12/22.
 */
public class LRULinkedList<T> implements Serializable {
    //默认的缓存大小
    private static int CAPACITY = 0;

    //引用一个双向链接表
    private LinkedList<T> list;

    //构造函数
    public LRULinkedList(int capacity) {
        this.CAPACITY = capacity;
        list = new LinkedList<T>();
    }

    //添加一个元素
    public synchronized void put(T t) {

        if (list != null && list.contains(t)) {
            list.remove(t);
        }
        removeLeastVisitElement();
        list.addFirst(t);
    }

    //移除最近访问次数最少的元素
    private synchronized void removeLeastVisitElement() {

        int size = size();

        //注意，这儿必须得是CAPACITY - 1否则所获的size比原来大1
        if (size > (CAPACITY - 1)) {
            T t = list.removeLast();
            System.out.println("本次被踢掉的元素是:" + t.toString());
        }
    }

    //获取第N个索引下面的元素
    public synchronized T get(int index) {
        return list.get(index);
    }

    //清空缓存
    public synchronized void clear() {
        list.clear();
    }

    //获取链接表的大小
    public int size() {
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    //toString方法
    public String toString() {
        return list.toString();
    }
}
