package com.test.xcamera.bean;

import java.io.Serializable;

/**
 * Created by zll on 2019/7/3.
 */

public class MoDataEx implements Serializable {
    public int msg_id;
    public int result;

    @Override
    public String toString() {
        return "MoDataEx{" +
                "msg_id=" + msg_id +
                ", result=" + result +
                '}';
    }
}
