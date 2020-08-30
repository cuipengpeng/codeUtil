package com.test.xcamera.album.preview.presenter;

import android.os.Handler;
import android.os.Message;

import com.test.xcamera.album.preview.LocalMediaPreviewActivity;
import com.test.xcamera.bean.MoAlbumItem;
import com.test.xcamera.managers.ConnectionManager;
import com.test.xcamera.mointerface.MoRequestCallback;
import com.test.xcamera.utils.DownLoadRequest;
import com.test.xcamera.utils.FileUtil;


import java.util.ArrayList;

/**
 * author zxc
 * createTime 2019/10/14  15:49
 */
public class LocalMediaPreviewActivityPresenter {
    private final LocalMediaPreviewActivity mediaPreviewActivity;
    private final Handler handler;
    private long mSoumFileSize;

    public LocalMediaPreviewActivityPresenter(LocalMediaPreviewActivity mediaPreviewActivity, Handler handler) {
        this.handler = handler;
        this.mediaPreviewActivity = mediaPreviewActivity;
    }

    private Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int flag = msg.what;
            switch (flag) {
                case 1:
                    break;
                case 2:
                    mediaPreviewActivity.deleteOKRefreshUI();
                    break;
            }
        }
    };

    /**
     * 删除
     *
     * @param arrayList
     */
    public void deleteMedia(ArrayList<MoAlbumItem> arrayList) {
        FileUtil.deleteMedias(mediaPreviewActivity, arrayList, uiHandler, 1, 2);
    }

    /**
     * 开始下载文件
     *
     * @param albumItems
     */
    public void startDownloadFile(ArrayList<MoAlbumItem> albumItems) {
        DownLoadRequest mDownLoad = new DownLoadRequest(handler);
        mDownLoad.addData(albumItems);
        mSoumFileSize = mDownLoad.getSumCount();
        mDownLoad.downCameraFile();
    }

    public long getmSunCount() {
        return mSoumFileSize;
    }

    /**
     * 喜欢
     *
     * @param moAlbumItem
     */
    public void like(final MoAlbumItem moAlbumItem, final int likeFlag) {
        String uri = "";
        if (!moAlbumItem.isVideo()) {
            uri = moAlbumItem.getmImage().getmUri();
        } else {
            uri = moAlbumItem.getmVideo().getmUri();
        }
        ConnectionManager.getInstance().like(new MoRequestCallback() {
            @Override
            public void onSuccess() {
                moAlbumItem.setCollect(likeFlag);
                mediaPreviewActivity.likeSuccess(moAlbumItem);
            }

            @Override
            public void onFailed() {

            }
        }, uri, likeFlag);
    }
}
