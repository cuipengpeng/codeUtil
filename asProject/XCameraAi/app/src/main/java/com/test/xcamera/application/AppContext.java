package com.test.xcamera.application;

import android.content.Context;

/**
 * Created by zhanglonglong on 2016/6/15.
 */
public class AppContext {
    private static Context mContext;

    public static Context getInstance() {
        return mContext;
    }

    public static void init(Context context) {
        mContext = context;
    }
}
