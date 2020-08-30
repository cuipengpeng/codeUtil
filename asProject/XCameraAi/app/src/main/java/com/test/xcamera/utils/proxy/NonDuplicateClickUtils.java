package com.test.xcamera.utils.proxy;



public class NonDuplicateClickUtils {
    private static final int INTERVAL = 500;
    private static long lastClickTime;

    public static boolean isDuplicateClick() {
        long current = System.currentTimeMillis();
        boolean result = current - lastClickTime <= INTERVAL;
        lastClickTime = current;
        return result;
    }
}
