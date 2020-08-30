package com.test.xcamera.album.gallery;

import android.os.Handler;
import android.os.Message;

import com.test.xcamera.bean.MoAlbumItem;
import com.test.xcamera.bean.MoImage;
import com.test.xcamera.bean.MoVideo;
import com.test.xcamera.managers.ConnectionManager;
import com.test.xcamera.mointerface.MoRequestCallback;
import com.test.xcamera.utils.DownLoadRequest;
import com.test.xcamera.utils.FileUtil;
import com.test.xcamera.view.DeleteProgressDialog;
import com.test.xcamera.view.DownProgressDialog;
import com.editvideo.MediaData;
import com.editvideo.MediaUtils;
import com.editvideo.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * author : zxc
 * create_time : 2019/10/10 19:01
 * description
 */
public class LocalFilterMediaListPresenter {
    private static final int DOWN_LOADFILE = 10000;
    private final LocalFilterMediaListActivity cameraPhotoListActivity;
    private int mSunCount = 0;
    private DownLoadRequest mDownLoad;
    private DeleteProgressDialog deleteProgressDialog;
    private int sumDeleteCount;
    private int sumLen = 0;
    private double mProgeress = 0;
    private DownProgressDialog downProgressDialog;
//    @SuppressLint("HandlerLeak")
//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case DOWN_LOADFILE:
//                    int len = (int) msg.obj;
//                    sumLen += len;
//                    double current = 0;
//                    int mSunCount = getmSunCount();
//                    if (getmSunCount() != 0)
//                        current = round(100 * len, mSunCount, 2);
//                    mProgeress += current;
//                    if (sumLen == mSunCount) {
//                        mProgeress = 100;
//                    }
//                    if (mProgeress >= 100) {
//                        sumLen = 0;
//                        mProgeress = 0;
//                        cameraPhotoListActivity.downLoadFileSuccess();
//                        downProgressDialog.dismissDialog();
//                    }
//                    downProgressDialog.refreshProgress(mProgeress, sumLen);
//                    break;
//            }
//        }
//    };


    public LocalFilterMediaListPresenter(LocalFilterMediaListActivity localFilterMediaListActivity) {
        this.cameraPhotoListActivity = localFilterMediaListActivity;
    }

    /**
     * 开始下载文件
     *
     * @param albumItems

    public void startDownloadFile(ArrayList<MoAlbumItem> albumItems) {
    if (mDownLoad != null) {
    mDownLoad = null;
    }
    if (downProgressDialog != null) {
    downProgressDialog = null;
    }


    mDownLoad = new DownLoadRequest(handler);
    mDownLoad.addData(albumItems);
    mSunCount = mDownLoad.getSumCount();
    mDownLoad.downCameraFile();

    downProgressDialog = new DownProgressDialog(cameraPhotoListActivity);
    downProgressDialog.showDialog(mSunCount);
    }*/

    /**
     * 获取选取的照片和视频的总大小
     *
     * @return public int getmSunCount() {
    return mSunCount;
    }
     */
    /**
     * 清除之前下载集合中的的数据

     public void clear() {
     mDownLoad.clear();
     }
     */
    /**
     * 停止预览流
     */
    public void stopPreview() {
        ConnectionManager.getInstance().stopPreview(new MoRequestCallback() {
            @Override
            public void onSuccess() {
                cameraPhotoListActivity.getCameraMediaList();
            }

            @Override
            public void onFailed() {

            }
        });
    }

    /**
     * 获取本地照片列表
     */
    public void getLocalFilterMediaList() {
        MediaUtils.getInstance().getAllPhotoAndVideo(cameraPhotoListActivity, new MediaUtils.LocalMediaCallback() {
            @Override
            public void onLocalMediaCallback(final List<MediaData> allMediaTemp) {
                final ArrayList<MoAlbumItem> albumItems = new ArrayList<>();
                for (MediaData mediaData : allMediaTemp) {
                    if (!mediaData.getPath().contains("MO_AICamera")) {
                        continue;
                    }
                    if (mediaData.getPath().endsWith(".mp4")) {

                        MoAlbumItem moAlbumItem = new MoAlbumItem();
                        MoVideo videoM = new MoVideo();
                        MoImage moImage = new MoImage();

                        moAlbumItem.setmType("video");
                        moAlbumItem.setmCreateTime(mediaData.getData());
                        moAlbumItem.setmID(mediaData.getId() + "");

                        videoM.setmDuration((int) (mediaData.getDuration() / 1000));
                        videoM.setmUri(mediaData.getPath());
                        moImage.setmUri(mediaData.getPath());

                        moAlbumItem.setmThumbnail(moImage);
                        moAlbumItem.setmImage(moImage);
                        moAlbumItem.setmVideo(videoM);

                        albumItems.add(moAlbumItem);

                    } else {

                        MoAlbumItem moAlbumItem = new MoAlbumItem();
                        MoImage moImage = new MoImage();
                        moAlbumItem.setmType("image");
                        moAlbumItem.setmCreateTime(mediaData.getData());
                        moAlbumItem.setmID(mediaData.getId() + "");
                        moImage.setmUri(mediaData.getPath());
                        moAlbumItem.setmThumbnail(moImage);

                        albumItems.add(moAlbumItem);
                    }
                }

                cameraPhotoListActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cameraPhotoListActivity.refreshList(albumItems);
                    }
                });
            }
        });
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int flag = msg.what;
            switch (flag) {
                case 1:
                    int count = (int) msg.obj;
                    deleteProgressDialog.refreshProgress((count + 1));
                    break;
                case 2:
                    cameraPhotoListActivity.deleteFileSuccess();
                    ToastUtil.showToast(cameraPhotoListActivity, "deleteMedia response success");
                    deleteProgressDialog.dismissDialog();
                    break;
            }
        }
    };

    public void deleteMedia(final ArrayList<MoAlbumItem> list) {
        if (deleteProgressDialog != null) {
            deleteProgressDialog = null;
        }
        deleteProgressDialog = new DeleteProgressDialog(cameraPhotoListActivity);
        deleteProgressDialog.showDialog(list.size());

        FileUtil.deleteMedias(cameraPhotoListActivity, list, handler, 1, 2);
    }
}
