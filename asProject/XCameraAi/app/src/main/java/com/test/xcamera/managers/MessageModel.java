package com.test.xcamera.managers;

/**
 * Created by smz on 2020/4/16.
 */

public class MessageModel {
    public int msgId;
    //如果超时 最多尝试两次
    public int tryCount;
    //最大的等待时间 默认100ms
    public int waitTime = 100;
    public long timestamp;

    public MessageModel(int msgId, long timestamp, int waitTime) {
        this.msgId = msgId;
        this.timestamp = timestamp;
        if (waitTime > 0)
            this.waitTime = waitTime;
    }
}
