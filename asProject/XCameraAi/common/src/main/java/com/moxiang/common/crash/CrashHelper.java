package com.moxiang.common.crash;

import android.content.Context;


import com.moxiang.common.BuildConfig;


/**
 * Created by admin on 2019/11/5.
 */

public class CrashHelper {
    //保存日志文件
    public static final boolean DEBUG = BuildConfig.DEBUG;
    //debug产生错误日志
    public static final boolean HAVE_LOG = DEBUG;

    public static void init(Context context) {
        if (!FileUtils.checkFilePathExists(FileUtils.SDPATH))
            FileUtils.CreateDir();
        if (HAVE_LOG) {
            CrashHandler crashHandler = CrashHandler.getInstance();
            crashHandler.init(context);
        }
    }

}
