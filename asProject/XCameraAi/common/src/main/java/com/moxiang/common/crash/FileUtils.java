package com.moxiang.common.crash;

import android.os.Environment;

import java.io.File;

/**
 * Created by admin on 2019/11/5.
 */

public class FileUtils {
    public static String SDPATH = Environment.getExternalStorageDirectory() + "/MoCrash/";
    public static File file = new File(SDPATH);

    /**
     * 创建文件
     */
    public static void CreateDir() {
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    /**
     * 检查路径是否存在
     *
     * @param path
     * @return
     */
    public static boolean checkFilePathExists(String path) {
        return new File(path).exists();
    }

}
