package com.test.xcamera.managers;

import com.test.xcamera.mointerface.MoCommandCallback;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zll on 2019/7/4.
 */

public class MoCommandHandler {
    private static final String TAG = "MoCommandHandler";
    private static MoCommandHandler singleton = null;
    private static Object lock = new Object();
    private HashMap<Integer, MoCommandCallback> cmdTable;
    public ConcurrentHashMap<Integer, MessageModel> mCacheMsg = new ConcurrentHashMap<>();

    public static MoCommandHandler getInstance() {
        synchronized (lock) {
            if (singleton == null) {
                singleton = new MoCommandHandler();
            }
        }
        return singleton;
    }

    public MoCommandHandler() {
        cmdTable = new HashMap<>();
    }

    public void addCommand(int msgID, MoCommandCallback callback) {
        addCommand(msgID, -1, callback);
    }

    /**
     * 添加消息
     * <p>
     * usb传输消息时 相机端返回但是app端接收不到  目前无法定位问题
     * 猜测和缓冲区有关系  如果指令发送 隔一段时间未返回 则将缓冲区刷满 可以获取想要的消息
     *
     * @param waitTime 超时时间  <0 不加入超时  =0 默认超时时间 >0 自定义超时时间
     */
    public void addCommand(int msgID, int waitTime, MoCommandCallback callback) {
        if (cmdTable != null) {
            if (msgID > 0) {
                cmdTable.put(msgID, callback);
                if (waitTime >= 0)
                    mCacheMsg.put(msgID, new MessageModel(msgID, System.currentTimeMillis(), waitTime));
            }
        }
    }

    public void addDefaultCmd(int msgID, MoCommandCallback callback) {
        if (cmdTable != null) {
            if (callback != null) {
                cmdTable.put(msgID, callback);
            }
        }
    }

    public MoCommandCallback getCmdCallback(int msgID) {
        mCacheMsg.remove(msgID);

        if (cmdTable == null) return null;
        return cmdTable.remove(msgID);
    }

    public MoCommandCallback getDeleteCmdCallback(int msgID) {
        if (cmdTable == null) return null;
        return cmdTable.get(msgID);
    }

    public void clear() {
        if (cmdTable != null)
            cmdTable.clear();
        mCacheMsg.clear();
    }
}
