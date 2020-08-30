package com.test.xcamera.album.gallery.camera;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;

import com.test.xcamera.bean.MoAlbumItem;
import com.test.xcamera.managers.ConnectionManager;
import com.test.xcamera.mointerface.MoAlbumListCallback;
import com.test.xcamera.mointerface.MoRequestCallback;
import com.test.xcamera.utils.DownLoadRequest;
import com.test.xcamera.view.DeleteProgressDialog;
import com.test.xcamera.view.DownProgressDialog;
import com.editvideo.ToastUtil;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * author : zxc
 * create_time : 2019/10/10 19:01
 * description
 */
public class CameraPhotoListPresenter {
    private static final int DOWN_LOADFILE = 10000;
    private final CameraPhotoListActivity cameraPhotoListActivity;
    private long mSunCount = 0;
    private DownLoadRequest mDownLoad;
    private DeleteProgressDialog deleteProgressDialog;
    private int sumDeleteCount;
    private long sumLen = 0;
    private DownProgressDialog downProgressDialog;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWN_LOADFILE:
                    int len = (int) msg.obj;
                    sumLen += len;
//                    double current = 0;
                    long mSunCount = getmSunCount();
//                    if (getmSunCount() != 0)
//                        current = round(100 * len, mSunCount, 2);
                    if (sumLen == mSunCount) {
                        sumLen = 0;
                        cameraPhotoListActivity.downLoadFileSuccess();
                        downProgressDialog.dismissDialog();
                    }
                    double curLen = (double) sumLen / mSunCount * 100;

                    downProgressDialog.refreshProgress(curLen, curLen);
                    break;
            }
        }
    };


    public CameraPhotoListPresenter(CameraPhotoListActivity cameraPhotoListActivity) {
        this.cameraPhotoListActivity = cameraPhotoListActivity;
    }

    /**
     * 开始下载文件
     *
     * @param albumItems
     */
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
        downProgressDialog.showDialog(100);
    }

    /**
     * 获取选取的照片和视频的总大小
     *
     * @return
     */
    public long getmSunCount() {
        return mSunCount;
    }

    /**
     * 清除之前下载集合中的的数据
     */
    public void clear() {
        mDownLoad.clear();
    }

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
     * 请求照片列表的数据
     *
     * @param page
     */
    public void requestCameraData(final int page) {
        ConnectionManager.getInstance().getAlbumList(new MoAlbumListCallback() {
            @Override
            public void onSuccess(final ArrayList<MoAlbumItem> items) {
                cameraPhotoListActivity.refreshList(items);
            }

            @Override
            public void onFailed() {

            }
        }, page);
    }

    public void deleteMedia(final ArrayList<String> arrayList) {
        if (deleteProgressDialog != null) {
            deleteProgressDialog = null;
        }
        deleteProgressDialog = new DeleteProgressDialog(cameraPhotoListActivity);
        deleteProgressDialog.showDialog(arrayList.size());

        ConnectionManager.getInstance().deleteMedia(arrayList, new MoRequestCallback() {
            @Override
            public void onSuccess() {
                cameraPhotoListActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        sumDeleteCount += 1;
                        if (sumDeleteCount == arrayList.size()) {
                            sumDeleteCount = 0;
                            cameraPhotoListActivity.deleteFileSuccess();
                            ToastUtil.showToast(cameraPhotoListActivity, "deleteMedia response success");
                            deleteProgressDialog.dismissDialog();

                        } else {
                            deleteProgressDialog.refreshProgress(sumDeleteCount);
                        }
                    }
                });
            }

            @Override
            public void onFailed() {
                cameraPhotoListActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(cameraPhotoListActivity, "deleteMedia response failed");
                    }
                });
            }
        });
    }

    public static double round(int v1, int v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("此参数错误");
        }
        BigDecimal one = new BigDecimal(Double.toString(v1));
        BigDecimal two = new BigDecimal(Double.toString(v2));
        return one.divide(two, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static double round(long v1, long v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("此参数错误");
        }
        BigDecimal one = new BigDecimal(Double.toString(v1));
        BigDecimal two = new BigDecimal(Double.toString(v2));
        return one.divide(two, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    private int likeIndex = 0;

    /**
     * 喜欢
     * 和下载的思路是一样的喜欢完成一下,在去喜欢下一个
     *
     * @param selectedPhotoList
     */
    public void like(final ArrayList<MoAlbumItem> selectedPhotoList) {
        if (likeIndex < selectedPhotoList.size()) {
            MoAlbumItem item = selectedPhotoList.get(likeIndex);
            String uri = "";
            if (!item.isVideo()) {
                uri = item.getmImage().getmUri();
            } else {
                uri = item.getmVideo().getmUri();
            }
            ConnectionManager.getInstance().like(new MoRequestCallback() {
                @Override
                public void onSuccess() {
                    likeIndex += 1;
                    like(selectedPhotoList);
                }

                @Override
                public void onFailed() {

                }
            }, uri, 1);
        } else {
            //喜欢完成
            cameraPhotoListActivity.likeSuccess(selectedPhotoList);
        }
    }
}
