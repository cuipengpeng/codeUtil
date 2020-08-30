package com.test.xcamera.utils;

/**
 * Created by zll on 2019/5/27.
 */

public class BottomSelectorUtil {
    public final static int BOTTOM_MIN_INDEX = 0;
    public final static int BOTTOM_MAX_INDEX = 5;
    private static int BOTTOM_SELECTED_INDEX = 2;

    public static int getBottomCurrentSelectedIndex() {
        return BOTTOM_SELECTED_INDEX;
    }

    public static void setBottomSelectedIndex(int index) {
        BOTTOM_SELECTED_INDEX = index;
    }
}
