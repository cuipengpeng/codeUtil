package com.test.xcamera.moalbum.my_album;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.editvideo.ToastUtil;
import com.framwork.base.view.MOBaseActivity;
import com.test.xcamera.R;
import com.test.xcamera.accrssory.AccessoryManager;
import com.test.xcamera.application.AiCameraApplication;
import com.test.xcamera.bean.MoAlbumItem;
import com.test.xcamera.managers.ConnectionManager;
import com.test.xcamera.moalbum.interfaces.MoAlbumCallback;
import com.test.xcamera.mointerface.MoAlbumListCallback;
import com.test.xcamera.mointerface.MoRequestCallback;
import com.test.xcamera.utils.CameraToastUtil;
import com.test.xcamera.utils.DownLoadRequest;
import com.test.xcamera.utils.DownLoadRequestNew;
import com.test.xcamera.utils.FileUtil;
import com.test.xcamera.utils.LoggerUtils;
import com.test.xcamera.utils.StorageUtils;
import com.test.xcamera.view.DeleteAlbumDialog;
import com.test.xcamera.view.basedialog.dialog.CancelDownDialog;
import com.test.xcamera.view.basedialog.dialog.CommonDownloadDialog;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zll on 2019/11/21.
 */

public class MoAlbumPresenter {
    private final String TAG = "MoAlbumPresenter";
    private final int DOWN_LOADFILE = 10000;
    private Context mContext;
    private MoAlbumCallback mAlbumCallback;
    //    private DeleteProgressDialog deleteProgressDialog;
    private int sumDeleteCount;
    private long sumLen = 0;
    private long mSunCount = 0;
    private double mProgeress = 0;
    //    private DownProgressDialog downProgressDialog;
    private DownLoadRequestNew mDownLoad;
    private int likeIndex = 0;
    private DeleteAlbumDialog deleteAlbumDialog;
    private int deleteCameraAlbumCount = 0;
    private final int DELETE_LOCAl_ALBUM_SUCCESS = 2;
    private final int DELETE_LOCAl_ALBUM_DELETING = 1;


    private final int DELETE_CAMERA_ALBUM_SUCCESS = 3;
    private CommonDownloadDialog commonDownloadDialog;
    private MoAlbumItem item;
    private boolean downError;

    public MoAlbumPresenter(Context context, MoAlbumCallback callback) {
        mContext = context;
        if (mAlbumCallback == null)
            mAlbumCallback = callback;
    }

    public void destoryCallBack() {
        mAlbumCallback = null;
        mContext = null;
        commonDownloadDialog = null;
        deleteAlbumDialog = null;
        mDownLoad = null;
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }

    public void removeHandler() {
        if (sumLen != getmSunCount() && mDownLoad != null) {
            mDownLoad.stopDownload();
        }
//        if (handler != null) {
//            handler.removeCallbacksAndMessages(null);
//            handler = null;
//        }
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWN_LOADFILE:
                    if (commonDownloadDialog == null) {
                        return;
                    }
                    long len = (int) msg.obj;
                    sumLen += len;
                    long mSunCount = getmSunCount();

                    if (sumLen == mSunCount) {
                        sumLen = 0;
                        if (downError) {
                            downRemedy();
                        } else {
                            downSuccess();
                        }
                    }

                    int curLen = (int) ((((double) sumLen / (double) mSunCount)) * 100);
                    Log.i(TAG, "handleMessage:  curLen = " + (((double) sumLen / (double) mSunCount)) + " len = " + len + " sumLen = " + sumLen + " mSunCount = " + mSunCount);
                    if (commonDownloadDialog != null) {
                        String format = String.format(mContext.getResources().getString(R.string.download_progress), curLen) + "%";
                        commonDownloadDialog.setProgress(format);//.refreshProgress(curLen, curLen);
                    }
                    break;
                case DELETE_LOCAl_ALBUM_DELETING:
                    break;
                case DELETE_LOCAl_ALBUM_SUCCESS:
                    if (mAlbumCallback != null)
                        mAlbumCallback.deleteLocalFileSuccess();
                    break;
                case DELETE_CAMERA_ALBUM_SUCCESS:
                    if (mAlbumCallback != null)
                        mAlbumCallback.deleteCameraFileSuccess();
                    break;
                case DownLoadRequest.DOWN_LOAD_TIME_OUT:
                    Log.i(TAG, "handleMessage: 下载超时");
                    ConnectionManager.getInstance().flush();
                    if (mDownLoad.getDownSize() - 1 == mDownLoad.getIndex()) {
                        downError = true;
                    }

                    break;
                case DownLoadRequest.DOWN_LOAD_ERROR:
                    disconnectCamera();
                    break;
            }
        }
    };

    /**
     * 如果是 30 秒的超时,直接将相机断开连接,
     * 按照素材导入页面的逻辑处理,下载超时的情况
     */
    private void disconnectCamera() {
        if (mDownLoad != null) {
            mDownLoad.stopDownload();
            clear();
        }
        if (commonDownloadDialog != null)
            commonDownloadDialog.dismissDialog();
        if (AccessoryManager.getInstance().mCommunicator != null) {
            AccessoryManager.getInstance().mCommunicator.closeAccessory("AlbumActivity:download");
        }

        CameraToastUtil.show(AiCameraApplication.getContext().getString(R.string.download_error), AiCameraApplication.getContext().getApplicationContext());
    }

    /**
     * 下载超时补救
     */
    private void downRemedy() {
        if (item != null) {
            List<MoAlbumItem> moAlbumItemList = new ArrayList<>();
            moAlbumItemList.add(item);
            mDownLoad = new DownLoadRequestNew(new Handler());
            mDownLoad.saveCurDownLoad(mContext, moAlbumItemList);
            mDownLoad.addData(moAlbumItemList);
            mDownLoad.downCameraFile();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    downSuccess();
                }
            }, 100);
        }
    }

    private void downSuccess() {
        if (mDownLoad != null)
            mDownLoad.stopDownload();
        if (mAlbumCallback != null)
            mAlbumCallback.downLoadFileSuccess();
        if (commonDownloadDialog != null) {
            commonDownloadDialog.dismissDialog();
            commonDownloadDialog.setCloseDialogListener(null);
        }
    }

    /**
     * 清除之前下载集合中的的数据
     */
    public void clear() {
        if (mDownLoad != null) {
            mDownLoad.clear();
        }
    }

    /**
     * 开始下载文件
     *
     * @param albumItems
     */
    public void startDownloadFile(ArrayList<MoAlbumItem> albumItems) {
        long totalSize = 0;
        for (int i = 0; i < albumItems.size(); i++) {
            MoAlbumItem moAlbumItem = albumItems.get(i);
            if (moAlbumItem.isImage() && moAlbumItem.getmImage() != null) {
                totalSize += moAlbumItem.getmImage().getmSize();
            } else {
                if (moAlbumItem.getmVideo() != null)
                    totalSize += moAlbumItem.getmVideo().getmSize();
            }
            moAlbumItem.setmDownloadFileName(System.currentTimeMillis() + "" + i);
        }
        boolean b = StorageUtils.externalMemoryAvailable();
        if (!b) {
            ToastUtil.showToast(mContext, mContext.getResources().getString(R.string.stor_no_external));
            return;
        }
        long availableExternalMemorySize = StorageUtils.getAvailableExternalMemorySize();
        if ((availableExternalMemorySize - totalSize) < (50 * 1024 * 1024)) {
            ToastUtil.showToast(mContext, mContext.getResources().getString(R.string.stor_no));
            return;
        }
        if (mDownLoad != null) {
            mDownLoad = null;
        }

        if (commonDownloadDialog != null) {
            commonDownloadDialog = null;
        }
        item = albumItems.get(0);//这个是为了,在下载出错的时候做补救使用的
        mDownLoad = new DownLoadRequestNew(handler);
        mDownLoad.addData(albumItems);
        mDownLoad.saveCurDownLoad(mContext, albumItems);
        mSunCount = mDownLoad.getSumCount();
        mDownLoad.downCameraFile();

        commonDownloadDialog = new CommonDownloadDialog(mContext);
        commonDownloadDialog.showDialog(true);
        commonDownloadDialog.setCloseDialogListener(new CommonDownloadDialog.CloseDialogListener() {
            @Override
            public void closeDialog() {
//                sumLen = 0;
//                mAlbumCallback.downLoadFileSuccess();
//                DownLoadRequest.removeCurDownLoadError(mContext);
//                mDownLoad.stopDownload();
            }

            @Override
            public void cancelDialog() {
                if (commonDownloadDialog.getmDialog() != null) {
                    commonDownloadDialog.getmDialog().dismiss();
                }
                sumLen = 0;
                if (mAlbumCallback != null)
                    mAlbumCallback.downLoadFileSuccess();
                DownLoadRequestNew.removeCurDownLoadError(mContext);
                if (mDownLoad != null)
                    mDownLoad.stopDownload();
            }
        });
        commonDownloadDialog.setDialogCanceledOnTouchOutside(false);
    }

    /**
     * 取消下载的dialog
     */
    public void showCancelDownloadDialog() {
        Activity activity = ((MOBaseActivity) mContext);
        if (activity == null || activity.isDestroyed() || activity.isFinishing()) {
            return;
        }
        CancelDownDialog cancelDownDialog = new CancelDownDialog(activity);
        cancelDownDialog.setDialogContent(14,
                activity.getResources().getString(R.string.cancel_download));
        cancelDownDialog.showGoneTitleDialog();
        cancelDownDialog.setCancelListener(new CancelDownDialog.CancelListener() {
            @Override
            public void cancel(Dialog mDialog) {
                if (mDownLoad != null) {
                    DownLoadRequest.removeCurDownLoadError(activity);
                    mDialog.dismiss();
                }
            }
        });
    }

    /**
     * 删除相机文件
     *
     * @param arrayList
     */
    public void deleteMedia(final ArrayList<MoAlbumItem> arrayList) {
        if (deleteAlbumDialog == null)
            deleteAlbumDialog = new DeleteAlbumDialog((MOBaseActivity) mContext);
        final ArrayList<String> uris = new ArrayList<>();
        for (int i = 0; i < arrayList.size(); i++) {
            MoAlbumItem albumItem = arrayList.get(i);
            if (albumItem.isVideo()) {
                uris.add(albumItem.getmVideo().getmUri());
            } else {
                uris.add(albumItem.getmImage().getmUri());
            }
        }

        deleteAlbumDialog.showDialog(arrayList.size(), new DeleteAlbumDialog.Delete() {
            @Override
            public void delete() {
                ConnectionManager.getInstance().deleteMedia(uris, new MoRequestCallback() {
                    @Override
                    public void onSuccess() {
                        LoggerUtils.i(TAG, "onSuccess: 资源删除成功 !");
                        deleteCameraAlbumCount = deleteCameraAlbumCount + 1;
                        if (deleteCameraAlbumCount == arrayList.size()) {
                            handler.sendEmptyMessage(DELETE_CAMERA_ALBUM_SUCCESS);
                            deleteCameraAlbumCount = 0;
                        }
                    }

                    @Override
                    public void onFailed() {
                        LoggerUtils.i(TAG, "onSuccess: 资源删除失败 !");
                        ((MOBaseActivity) mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showToast(mContext, mContext.getResources().getString(R.string.delete_camera_error));
                            }
                        });
                    }
                });
            }
        });
    }

    /**
     * 删除本地文件
     *
     * @param list
     */
    public void deleteLocalMedia(final ArrayList<MoAlbumItem> list) {
        if (deleteAlbumDialog == null)
            deleteAlbumDialog = new DeleteAlbumDialog((MOBaseActivity) mContext);
        deleteAlbumDialog.showDialog(list.size(), new DeleteAlbumDialog.Delete() {
            @Override
            public void delete() {
                FileUtil.deleteMedias(mContext, list, handler, DELETE_LOCAl_ALBUM_DELETING, DELETE_LOCAl_ALBUM_SUCCESS);
            }
        });
    }

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
                    likeIndex = 0;
                }
            }, uri, 1);
        } else {
            likeIndex = 0;
            //喜欢完成
            if (mAlbumCallback != null)
                mAlbumCallback.likeSuccess(selectedPhotoList);
        }
    }

    /**
     * 获取选取的照片和视频的总大小
     *
     * @return
     */
    public long getmSunCount() {
        return mSunCount;
    }

    public static double round(int v1, int v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("此参数错误");
        }
        BigDecimal one = new BigDecimal(Double.toString(v1));
        BigDecimal two = new BigDecimal(Double.toString(v2));
        return one.divide(two, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
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
                if (mAlbumCallback != null)
                    mAlbumCallback.refreshList(items);
            }

            @Override
            public void onFailed() {
                if (mAlbumCallback != null)
                    mAlbumCallback.refreshListFailed();
            }
        }, page);
    }

    public interface StopPreviewListener {
        void onSuccess();

        void onFailed();
    }

    /**
     * 停止预览流
     */
    public void stopPreview(StopPreviewListener stopPreviewListener) {
        ConnectionManager.getInstance().stopPreview(new MoRequestCallback() {
            @Override
            public void onSuccess() {
                if (stopPreviewListener != null) {
                    stopPreviewListener.onSuccess();
                }
                if (mAlbumCallback != null)
                    mAlbumCallback.getCameraMediaList();
            }

            @Override
            public void onFailed() {
                if (stopPreviewListener != null) {
                    stopPreviewListener.onFailed();
                }
            }
        });
    }

    /**
     * 停止预览流
     */
    public void stopPreview() {
        ConnectionManager.getInstance().stopPreview(new MoRequestCallback() {
            @Override
            public void onSuccess() {
                if (mAlbumCallback != null)
                    mAlbumCallback.getCameraMediaList();
            }

            @Override
            public void onFailed() {

            }
        });
    }

    public void dimiss() {
        sumLen = 0;
        if (mDownLoad == null) {
            return;
        }
        if (commonDownloadDialog != null && commonDownloadDialog.isShowing()) {
            commonDownloadDialog.dismissDialog();
            commonDownloadDialog.setCloseDialogListener(null);
            commonDownloadDialog = null;
            clear();
        }
    }
}
