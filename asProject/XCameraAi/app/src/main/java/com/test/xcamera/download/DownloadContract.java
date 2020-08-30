package com.test.xcamera.download;

import com.moxiang.common.mvp.IModel;
import com.moxiang.common.mvp.IView;

import java.io.File;

/**
 * Created by admin on 2020/2/12.
 */

public interface DownloadContract {

    interface DownloadView extends IView {
        //下载开始
        void onStart();
        //下载进度
        void onProgress(int progress);
        //下载完成
        void onFinish(File path);
        //下载失败void onStart();
        void onFail(String errorInfo);
    }

    interface DownloadModel extends IModel {
    }
}
