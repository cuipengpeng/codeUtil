package com.test.xcamera.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by zhouxuecheng on
 * Create Time 2020/5/12
 * e-mail zhouxuecheng1991@163.com
 */

public class SecondMarkBean implements Serializable {
    public ArrayList<MarkBean> list;

    public class MarkBean implements Serializable {
        public String path;
    }
}
