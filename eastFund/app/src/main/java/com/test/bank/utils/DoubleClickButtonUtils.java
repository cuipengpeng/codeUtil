package com.test.bank.utils;

public class DoubleClickButtonUtils {
    private static long lastClickTime = 0;
    private static long DIFF          = 1000;
    private static int  lastButtonId  = -1;

    /**
     * 判断两次点击的间隔，如果小于1000，则认为是多次无效点击
     */
    public static boolean notFastDoubleClick() {
        return notFastDoubleClick(-1, DIFF);
    }

    /**
     * 判断两次点击的间隔，如果小于1000，则认为是多次无效点击
     */
    public static boolean notFastDoubleClick(int buttonId) {
        return notFastDoubleClick(buttonId, DIFF);
    }

    /**
     * 判断两次点击的间隔，如果小于diff，则认为是多次无效点击
     *
     * @param diff
     */
    public static boolean notFastDoubleClick(int buttonId, long diff) {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (lastButtonId == buttonId && lastClickTime > 0 && timeD < diff) {
            return false;
        }
        lastClickTime = time;
        lastButtonId = buttonId;
        return true;
    }
}