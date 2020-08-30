package com.test.xcamera.phonealbum.presenter;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.editvideo.MediaConstant;
import com.editvideo.MediaData;
import com.editvideo.ToastUtil;
import com.test.xcamera.R;
import com.test.xcamera.accrssory.AccessoryManager;
import com.test.xcamera.application.AiCameraApplication;
import com.test.xcamera.bean.MoAlbumItem;
import com.test.xcamera.bean.MoImage;
import com.test.xcamera.bean.MoVideo;
import com.test.xcamera.managers.ConnectionManager;
import com.test.xcamera.mointerface.MoAlbumListCallback;
import com.test.xcamera.phonealbum.usecase.VideoImportCheck;
import com.test.xcamera.utils.AppExecutors;
import com.test.xcamera.utils.DownLoadRequest;
import com.test.xcamera.utils.DownLoadRequestNew;
import com.test.xcamera.utils.FileUtils;
import com.test.xcamera.utils.LogAccessory;
import com.test.xcamera.utils.StorageUtils;
import com.test.xcamera.view.basedialog.dialog.CommonDownloadDialog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class AlbumEditPresenter implements AlbumEditContract.Presenter{
    public static final String CAMERA_MEDIA_TYPE_VIDEO = "video";
    public static final String CAMERA_MEDIA_TYPE_IMAGE = "image";
    private WeakReference<Context> mContext;
    private WeakReference<AlbumEditContract.View> mView;

    private DownLoadRequestNew mDownLoadRequest;
    private CommonDownloadDialog downProgressDialog;
    private long mSunCount = 0;
    private long sumLen = 0;

    public static AlbumEditPresenter getInstance(Context context,AlbumEditContract.View view) {
        return new AlbumEditPresenter(context,view);
    }
    private AlbumEditPresenter(Context context,AlbumEditContract.View view){
        mContext=new WeakReference<>(context);
        mView=new WeakReference<>(view);
    }
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DownLoadRequest.DOWN_LOADFILE:
                    int len = (int) msg.obj;
                    sumLen += len;
                    Log.i("club", "club:downVideo：cur_sumLen:" + sumLen + " mSunCount:" + mSunCount);
                    if (sumLen == mSunCount) {
                        if (!AccessoryManager.getInstance().mIsRunning) {
                            ToastUtil.showToast(AiCameraApplication.getContext().getApplicationContext(), AiCameraApplication.getContext().getString(R.string.video_import_download_error));
                            return;
                        }
                        mDownLoadRequest.clear();
                        if(downError){
                            downRemedy();
                        }else {
                            downSuccess();
                        }
                    }
                    if (downProgressDialog != null) {
                        downProgressDialog.setProgress(
                                FileUtils.formatSize(sumLen) + "  /  "
                                        + FileUtils.formatSize(mSunCount));
                    }
                    break;
                case DownLoadRequest.DOWN_LOAD_OK:
                    if(mView!=null&&mView.get()!=null){
                        mView.get().checkVideoIsConvert();
                    }
                    break;
                case DownLoadRequest.DOWN_LOAD_TIME_OUT:
                    ConnectionManager.getInstance().flush();
                    if(mDownLoadRequest.getDownSize()-1==mDownLoadRequest.getIndex()){
                        downError=true;
                    }

                    break;
                case DownLoadRequest.DOWN_LOAD_ERROR:

                    if (mDownLoadRequest != null) {
                        mDownLoadRequest.stopDownload();
                    }
                    if (downProgressDialog != null) {
                        downProgressDialog.dismissDialog();
                        downProgressDialog.setCloseDialogListener(null);

                    }
                    if (AccessoryManager.getInstance().mCommunicator != null) {
                        AccessoryManager.getInstance().mCommunicator.closeAccessory("AlbumActivity:download");
                    }
                    mDownLoadRequest.clear();
                    ToastUtil.showToast(AiCameraApplication.getContext().getApplicationContext(), AiCameraApplication.getContext().getString(R.string.download_error));

                    break;
            }
        }
    };

    /**
     * 下载超时补救
     */
    private void downRemedy(){
        if(mMoAlbumItem!=null){
            List<MoAlbumItem> moAlbumItemList = new ArrayList<>();
            moAlbumItemList.add(mMoAlbumItem);
            mDownLoadRequest=new DownLoadRequestNew(new Handler());
            mDownLoadRequest.saveCurDownLoad(mContext.get(), moAlbumItemList);
            mDownLoadRequest.addData(moAlbumItemList);
            mDownLoadRequest.downCameraFile();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    downSuccess();
                }
            },100);
        }
    }
    private void downSuccess(){
        mDownLoadRequest.stopDownload();
        downProgressDialog.dismissDialog();
        downProgressDialog.setCloseDialogListener(null);
        if(mView!=null&&mView.get()!=null){
            mView.get().openVideoEdit(true);
        }
    }
    @Override
    public void getCameraAlbumCount() {
        ConnectionManager.getInstance().albumCount(new ConnectionManager.AlbumCountListener() {
            @Override
            public void onSuccess(int count, MoImage thumbnail) {
                AppExecutors.getInstance().mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if(mView!=null&&mView.get()!=null){
                            mView.get().showCameraAlbumCount(count,thumbnail);
                        }
                    }
                });


            }

            @Override
            public void onFailed() {

            }
        });
    }

    @Override
    public void getRemoteSDcardCameraMediaList(boolean refresh, int pageIndex) {
        ConnectionManager.getInstance().getAlbumList(new MoAlbumListCallback() {
            @Override
            public void onSuccess(ArrayList<MoAlbumItem> items) {
                AppExecutors.getInstance().mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if(mView!=null&&mView.get()!=null){
                            mView.get().showCameraMediaListSuccess(refresh,items);
                        }
                    }
                });
            }

            @Override
            public void onFailed() {
                AppExecutors.getInstance().mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if(mView!=null&&mView.get()!=null){
                            mView.get().showCameraMediaListFailed();
                        }
                    }
                });
            }
        }, pageIndex);
    }
    private MoAlbumItem mMoAlbumItem;
    private boolean downError=false;
    @Override
    public void downloadSDCardFilesAndEditVideo(List<MediaData> mediaDataList) {
        if(mContext.get()==null){
            return;
        }
        boolean downError=false;
        long mTotalSize = 0;
        resetDownloadStatus();
        List<MoAlbumItem> moAlbumItemList = new ArrayList<>();
        MediaData mediaData;
        for (int i = 0; i < mediaDataList.size(); i++) {
            mediaData = mediaDataList.get(i);
            if (mediaData.isRemoteData()) {
                MoAlbumItem moAlbumItem = new MoAlbumItem();
                moAlbumItem.setmDownloadFileName("remote-" + System.currentTimeMillis() + i);
                if (mediaData.getType() == MediaConstant.VIDEO) {
                    mediaDataList.get(i).setPath(DownLoadRequest.fileDirectorPath + "/" + moAlbumItem.getmDownloadFileName() + ".mp4");
                    moAlbumItem.setmType(CAMERA_MEDIA_TYPE_VIDEO);
                    moAlbumItem.setmVideo(new MoVideo());
                    moAlbumItem.getmVideo().setmUri(mediaData.getRemoteVideoUri());
                    moAlbumItem.getmVideo().setmSize(mediaData.getVideoSize());
                    if(mMoAlbumItem==null){
                        mMoAlbumItem=new MoAlbumItem();
                        mMoAlbumItem.setmDownloadFileName("Remedy");
                        mMoAlbumItem.setmType(CAMERA_MEDIA_TYPE_VIDEO);
                        mMoAlbumItem.setmVideo(new MoVideo());
                        mMoAlbumItem.getmVideo().setmUri(mediaData.getRemoteVideoUri());
                        mMoAlbumItem.getmVideo().setmSize(mediaData.getVideoSize());
                        mTotalSize=mTotalSize+mediaData.getVideoSize();
                    }
                } else {
                    mediaDataList.get(i).setPath(DownLoadRequest.fileDirectorPath + "/" + moAlbumItem.getmDownloadFileName() + ".jpg");
                    moAlbumItem.setmImage(new MoImage());
                    moAlbumItem.getmImage().setmSize(mediaData.getImageSize());
                    moAlbumItem.getmImage().setmUri(mediaData.getRemoteImageUri());
                    moAlbumItem.setmType(CAMERA_MEDIA_TYPE_IMAGE);
                    if(mMoAlbumItem==null){
                        mMoAlbumItem=new MoAlbumItem();
                        mMoAlbumItem.setmDownloadFileName("Remedy_image");
                        mMoAlbumItem.setmImage(new MoImage());
                        mMoAlbumItem.getmImage().setmSize(mediaData.getImageSize());
                        mMoAlbumItem.getmImage().setmUri(mediaData.getRemoteImageUri());
                        mMoAlbumItem.setmType(CAMERA_MEDIA_TYPE_IMAGE);
                        mTotalSize=mTotalSize+mediaData.getImageSize();

                    }
                }
                moAlbumItemList.add(moAlbumItem);

            }
        }
        if (moAlbumItemList.size() > 0) {
            if (StorageUtils.getAvailableExternalMemorySize() < mTotalSize+100*1024*1024) {
                ToastUtil.showToast(AiCameraApplication.getContext(), AiCameraApplication.getContext().getResources().getString(R.string.video_down_stor_no));
                return;
            }
            mISDownLoad =true;
            VideoImportCheck.getInstance().checkVideoIsConvert(mediaDataList);
            mDownLoadRequest = new DownLoadRequestNew(handler);
            mDownLoadRequest.saveCurDownLoad(mContext.get(), moAlbumItemList);
            mDownLoadRequest.addData(moAlbumItemList);
            mSunCount = mDownLoadRequest.getTotalSize();
            mDownLoadRequest.downCameraFile();
            if(downProgressDialog==null){
                downProgressDialog = new CommonDownloadDialog(mContext.get());
            }
            downProgressDialog.setCancelable(false);
            downProgressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    mISDownLoad =false;
                    resetDownloadStatus();
                    if (mDownLoadRequest != null) {
                        mDownLoadRequest.stopDownload();
                    }
                }
            });
            downProgressDialog.setViewWidth(150);
            downProgressDialog.showDialog(true);
            downProgressDialog.setCloseDialogListener(new CommonDownloadDialog.CloseDialogListener() {
                @Override
                public void closeDialog() {
                    if(mView!=null&&mView.get()!=null){
                        mView.get().cancelDownload();
                    }
                }

                @Override
                public void cancelDialog() {
                    downProgressDialog.dismissDialog();
                }
            });
            downProgressDialog.setProgress(
                    FileUtils.formatSize(sumLen) + "  /  "
                            + FileUtils.formatSize(mSunCount));
        } else {
            if(mView!=null&&mView.get()!=null){
                mView.get().openVideoEdit(false);
            }
        }
    }

    @Override
    public void downloadDestroy() {
        if (downProgressDialog != null
                && downProgressDialog.isShowing()) {
            downProgressDialog.dismissDialog();
            downProgressDialog.setCloseDialogListener(null);
        }
        if (downProgressDialog != null) {
            downProgressDialog.destroy();
            downProgressDialog = null;
        }
        if(handler!=null){
            handler.removeCallbacksAndMessages(null);
        }
        if (mDownLoadRequest != null) {
            mDownLoadRequest.clear();
        }

    }

    @Override
    public void clearDownloadHandleMsg() {
        if(handler!=null){
            handler.sendEmptyMessage(DownLoadRequest.DOWN_LOAD_ERROR);
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void downloadVideoDel() {
        if (mDownLoadRequest != null) {
            mDownLoadRequest.deleteFile();
        }
    }

    @Override
    public boolean getDownloadStatus() {
        LogAccessory.getInstance().showLog("下载完成:mSunCount:" + mSunCount+" /sumLen:"+sumLen);

        return mSunCount==sumLen;
    }

    @Override
    public boolean resetDownloadStatus() {
        mSunCount=0;
        sumLen=0;
        return false;
    }
    private boolean mISDownLoad =false;
    @Override
    public boolean isDownloadStatus() {
        return mISDownLoad;
    }

    @Override
    public void destroy() {
        mView=null;
        mContext=null;
    }

}
