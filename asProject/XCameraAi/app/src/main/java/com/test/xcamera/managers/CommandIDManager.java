package com.test.xcamera.managers;

/**
 * Created by zll on 2019/7/4.
 */

public class CommandIDManager {
    private static final String TAG = "CommandIDManager";
    private static CommandIDManager singleton = null;
    private static Object lock = new Object();
    private int mMessageID = 1;

    public static CommandIDManager getInstance() {
        synchronized (lock) {
            if (singleton == null) {
                singleton = new CommandIDManager();
            }
        }
        return singleton;
    }

    public synchronized int getMsgID() {
        return mMessageID++;
    }
}
