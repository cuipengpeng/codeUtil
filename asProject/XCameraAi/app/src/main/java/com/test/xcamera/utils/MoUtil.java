package com.test.xcamera.utils;

import android.content.Context;

/**
 * Created by zll on 2019/11/20.
 */

public class MoUtil {
    public static String getString(Context context, int resID) {
        return context.getResources().getString(resID);
    }

    public static long getXmlSize(String uri) {
        uri = uri.substring(uri.indexOf("size="));
        uri = uri.substring(0, uri.indexOf("&type"));
        return Long.valueOf(uri.substring(5));
    }
}
