package com.test.xcamera.managers;

import com.test.xcamera.bean.MoRange;
import com.test.xcamera.mointerface.MoDownloadCallback;
import com.test.xcamera.utils.BytesUtil;

/**
 * Created by zll on 2019/7/22.
 */

public class MoDownloadManager {
    private static final String TAG = "MoDownloadManager";
    private static final int PACKAGE_MAX_SIZE = 256 * 1024;
    private static MoDownloadManager singleton = null;
    private static Object lock = new Object();
    private byte[] mData = null;
    private DownloadResultCallback mDownloadResultCallback;

    public static MoDownloadManager getInstance() {
        synchronized (lock) {
            if (singleton == null) {
                singleton = new MoDownloadManager();
            }
        }
        return singleton;
    }

    public void setDownloadResultCallback(DownloadResultCallback callback) {
        mDownloadResultCallback = callback;
    }

    public void startDownload(String uri, int size) {
        int length;
        if (size < PACKAGE_MAX_SIZE) {
            length = size;
            MoRange range = new MoRange();
            range.setmOffset(0);
            range.setmLength(length);
            ConnectionManager.getInstance().downloadFile(uri, range, new MoDownloadCallback() {
                @Override
                public void callback(byte[] data) {
//                    mData = data;
                    if (mDownloadResultCallback != null) {
                        mDownloadResultCallback.callback(data);
                    }
                }
            });
        } else {
            int count = 0;
            int offset = size / PACKAGE_MAX_SIZE;
            final int tmp = size - (PACKAGE_MAX_SIZE * offset);
            for (int i = 0; i < offset; i++) {
                MoRange range = new MoRange();
                range.setmOffset(count);
                range.setmLength(PACKAGE_MAX_SIZE);
                ConnectionManager.getInstance().downloadFile(uri, range, new MoDownloadCallback() {
                    @Override
                    public void callback(byte[] data) {
                        if (mData == null) {
                            mData = data;
                        } else {
                            mData = BytesUtil.byteMergerAll(mData, data);
                        }
                        if (tmp == 0 && mDownloadResultCallback != null) {
                            mDownloadResultCallback.callback(mData);
                            mData = null;
                        }
                    }
                });
                count++;
            }
            if (tmp > 0) {
                MoRange range = new MoRange();
                range.setmOffset(count++);
                range.setmLength(tmp);
                ConnectionManager.getInstance().downloadFile(uri, range, new MoDownloadCallback() {
                    @Override
                    public void callback(byte[] data) {
                        mData = BytesUtil.byteMergerAll(mData, data);
                        if (mDownloadResultCallback != null) {
                            mDownloadResultCallback.callback(mData);
                            mData = null;
                        }
                    }
                });
            }
        }
    }

    public interface DownloadResultCallback {
        void callback(byte[] data);
    }
}
