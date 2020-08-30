package com.test.xcamera.bean;

import java.util.ArrayList;

/**
 * Created by zhouxuecheng on
 * Create Time 2019/10/30
 * e-mail zhouxuecheng1991@163.com
 */

public class ItemBean {
    public ArrayList<ItemData> list;

    public class ItemData {
        public int rId;
        public int model;
        public String name;
        public boolean isSlect;
        public int seekBarPosition;
        public int id;
        public String modelType;
        public String commonFilterId;
        public boolean isOperation;
        public int location;
        public int number;
    }
}
