package com.test.xcamera.album.preview.presenter;

import android.app.Dialog;
import android.os.Handler;

import com.test.xcamera.R;
import com.test.xcamera.moalbum.activity.MyAlbumPreview;
import com.test.xcamera.bean.MoAlbumItem;
import com.test.xcamera.managers.ConnectionManager;
import com.test.xcamera.mointerface.MoRequestCallback;
import com.test.xcamera.utils.DownLoadRequest;
import com.test.xcamera.utils.DownLoadRequestNew;
import com.test.xcamera.utils.FileUtil;
import com.test.xcamera.view.basedialog.dialog.CancelDownDialog;

import java.util.ArrayList;

/**
 * author zxc
 * createTime 2019/10/14  15:49
 */
public class MediaPreviewActivityPresenter {
    private final MyAlbumPreview mediaPreviewActivity;
    private final Handler handler;
    private long mSoumFileSize;
    private DownLoadRequestNew mDownLoad;
    private CancelDownDialog cancelDownDialog;
    //    private DeleteProgressDialog deleteProgressDialog;

    public MediaPreviewActivityPresenter(MyAlbumPreview mediaPreviewActivity, Handler handler) {
        this.handler = handler;
        this.mediaPreviewActivity = mediaPreviewActivity;
    }

    /**
     * 删除本地文件
     *
     * @param list
     */
    public void deleteLocalMedia(final ArrayList<MoAlbumItem> list) {
//        if (deleteProgressDialog != null) {
//            deleteProgressDialog = null;
//        }
//        deleteProgressDialog = new DeleteProgressDialog(mediaPreviewActivity);
//        deleteProgressDialog.showDialog(list.size());

        FileUtil.deleteMedias(mediaPreviewActivity, list, handler, 1, MyAlbumPreview.DELETE_LOCAL_FILE_OK);
    }

    /**
     * 删除
     *
     * @param arrayList
     */
    public void deleteMedia(ArrayList<String> arrayList) {
        ConnectionManager.getInstance().deleteMedia(arrayList, new MoRequestCallback() {
            @Override
            public void onSuccess() {
                mediaPreviewActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mediaPreviewActivity.deleteOKRefreshUI();
                    }
                });
            }

            @Override
            public void onFailed() {
            }
        });
    }

    public DownLoadRequestNew getmDownLoad() {
        return mDownLoad;
    }

    /**
     * 开始下载文件
     *
     * @param albumItems
     */
    public void startDownloadFile(ArrayList<MoAlbumItem> albumItems) {
        mDownLoad = new DownLoadRequestNew(handler);
        mDownLoad.saveCurDownLoad(mediaPreviewActivity, albumItems);
        mDownLoad.addData(albumItems);
        mSoumFileSize = mDownLoad.getSumCount();
        mDownLoad.downCameraFile();
    }

    /**
     * 取消下载的提示框
     */
    public void showCancelDialog() {
        cancelDownDialog = new CancelDownDialog(mediaPreviewActivity);
        if (!mediaPreviewActivity.isDestroyed()) {
            cancelDownDialog.setDialogContent(14,
                    mediaPreviewActivity.getResources().getString(R.string.cancel_download));
            cancelDownDialog.showGoneTitleDialog();
            cancelDownDialog.setCancelListener(new CancelDownDialog.CancelListener() {
                @Override
                public void cancel(Dialog mDialog) {
                    if (mDownLoad != null) {
                        DownLoadRequest.removeCurDownLoadError(mediaPreviewActivity);
                        mDialog.dismiss();
                        mediaPreviewActivity.cancelDown();
//                        mDownLoad.cancelDownload();
                    }
                }
            });
        }
    }

    public void dismissDialog() {
        if (cancelDownDialog != null && !mediaPreviewActivity.isDestroyed()) {
            cancelDownDialog.dismissDialog();
        }
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
