package com.test.xcamera.moalbum.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.editvideo.ToastUtil;
import com.framwork.base.view.MOBaseActivity;
import com.test.xcamera.R;
import com.test.xcamera.album.adapter.MediaPreviewAdater;
import com.test.xcamera.bean.AlbumListBean;
import com.test.xcamera.bean.MoImage;
import com.test.xcamera.bean.MoVideo;
import com.test.xcamera.managers.ConnectionManager;
import com.test.xcamera.moalbum.fragment.CameraVideoPreviewFragment;
import com.test.xcamera.moalbum.fragment.LocalVideoPreviewFragment;
import com.test.xcamera.album.preview.presenter.MediaPreviewActivityPresenter;
import com.test.xcamera.bean.MoAlbumItem;
import com.test.xcamera.mointerface.MoErrorCallback;
import com.test.xcamera.statistic.StatisticShare;
import com.test.xcamera.util.AACDecoderUtil;
import com.test.xcamera.utils.CameraToastUtil;
import com.test.xcamera.utils.DownLoadRequest;
import com.test.xcamera.utils.DownLoadRequestNew;
import com.test.xcamera.utils.SharedPreferencesUtil;
import com.test.xcamera.utils.StorageUtils;
import com.test.xcamera.view.basedialog.dialog.AlbumDeleteDialog;
import com.test.xcamera.view.basedialog.dialog.CommonDownloadDialog;
import com.test.xcamera.view.basedialog.dialog.PreviewCameraDisconnectDialog;
import com.moxiang.common.share.IShare;
import com.moxiang.common.share.ShareManager;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * author zxc
 * createTime 2019/10/12  下午10:48
 * 大图预览页面
 */
public class MyAlbumPreview extends MOBaseActivity implements ViewPager.OnPageChangeListener {
    private final String TAG = "MyAlbumPreview";
    private static final int DOWN_LOADFILE = 10000;
    public static final int DELETE_LOCAL_FILE_OK = 10001;
    @BindView(R.id.titleTextView)
    TextView titleTextView;
    @BindView(R.id.titleLayout)
    LinearLayout titleLayout;
    @BindView(R.id.titleRl)
    RelativeLayout titleRl;
    @BindView(R.id.landscapeLayoutShare)
    RelativeLayout landscapeLayoutShare;
    @BindView(R.id.landscapeLayoutDown)
    RelativeLayout landscapeLayoutDown;
    @BindView(R.id.landscapeLayoutLike)
    RelativeLayout landscapeLayoutLike;
    @BindView(R.id.landscapeLayoutDelete)
    RelativeLayout landscapeLayoutDelete;
    @BindView(R.id.landscapeLikeImageView)
    ImageView landscapeLikeImageView;
    @BindView(R.id.leftLayout)
    LinearLayout leftLayout;
    @BindView(R.id.portraitLayoutShare)
    RelativeLayout portraitLayoutShare;
    @BindView(R.id.portraitLayoutDown)
    RelativeLayout portraitLayoutDown;
    @BindView(R.id.portraitLayoutLike)
    RelativeLayout portraitLayoutLike;
    @BindView(R.id.portraitLikeImageView)
    ImageView portraitLikeImageView;
    @BindView(R.id.portraitLayoutDelete)
    RelativeLayout portraitLayoutDelete;
    @BindView(R.id.bottomLayout)
    LinearLayout bottomLayout;
    @BindView(R.id.previewViewPager)
    ViewPager previewViewPager;
    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.portraitDownImage)
    ImageView portraitDownImage;
    @BindView(R.id.landscapeDownImage)
    ImageView landscapeDownImage;
    @BindView(R.id.titleImage)
    ImageView titleImage;
    @BindView(R.id.informationIncon)
    ImageView informationIncon;

    private PagerChange pagerChange;
    private MediaPreviewAdater mediaPreviewAdater;
    private MediaPreviewActivityPresenter mediaPreviewActivityPresenter;
    private ArrayList<MoAlbumItem> albumList;
    private long sumLen = 0;
    private boolean downloading = false;
    private MoAlbumItem currentDeleteAlbumItem;
    private boolean mIsCamera = false;
    private boolean mIsShare = false;
    private boolean isDowned = false;
    private ShareManager.ShareChooser mShareChooser;
    private MoAlbumItem currentAlbumItem;
    private PreviewCameraDisconnectDialog previewCameraDisconnectDialog;
    public AACDecoderUtil mAacDecoderUtil;
    private boolean fragment;

    @SuppressLint("HandlerLeak")
    private Handler uihandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWN_LOADFILE:
                    String format = "";
                    int len = (int) msg.obj;
                    sumLen += len;
                    long mSunCount = mediaPreviewActivityPresenter.getmSunCount();
                    if (sumLen == mSunCount) {
                        mediaPreviewActivityPresenter.dismissDialog();
                        downloading = false;
                        CameraToastUtil.show(getResources().getString(R.string.download_ok), MyAlbumPreview.this);
                        sumLen = 0;
                        isDowned = true;
                        format = String.format(getResources().getString(R.string.download_progress), 100) + "%";
                        downIconUI();
                    } else {
                        int progress = (int) (((double) sumLen / (double) mSunCount) * 100);
                        format = String.format(getResources().getString(R.string.download_progress), progress) + "%";
                        Log.i("PROGRESS_TEST", "handleMessage: sumLen = " + sumLen + " mSunCount = " + mSunCount + "  format = " + format);
                    }
                    if (commonDownloadDialog != null)
                        commonDownloadDialog.setProgress(format);
                    break;
                case DELETE_LOCAL_FILE_OK:
                    deleteOKRefreshUI();
                    break;

                case DownLoadRequest.DOWN_LOADFILE_OK:
                    if (mIsShare) {
                        String downFilePath = msg.getData().getString("path");
                        shareToNoDown(downFilePath);
                    }
                    break;
            }
        }
    };
    private CommonDownloadDialog commonDownloadDialog;

    @Override
    public int initView() {
        return R.layout.media_activity_layout;
    }

    @Override
    public void initClick() {

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        Fragment currentFragment = mediaPreviewAdater.getCurrentFragment();
        if (currentFragment instanceof CameraVideoPreviewFragment) {
            int videoStatus = ((CameraVideoPreviewFragment) currentFragment).getvideoStatus();
            if (videoStatus == 1 || videoStatus == 4) {
                return;
            }
        } else if (currentFragment instanceof LocalVideoPreviewFragment) {
            boolean playVideo = ((LocalVideoPreviewFragment) currentFragment).isPlayVideo();
            if (playVideo) {
                return;
            }
        }

        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (titleRl.getVisibility() == View.VISIBLE) {
                bottomLayout.setVisibility(View.VISIBLE);
                leftLayout.setVisibility(View.GONE);
            }
            setTitleStatus(false);
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (titleRl.getVisibility() == View.VISIBLE) {
                bottomLayout.setVisibility(View.GONE);
                leftLayout.setVisibility(View.VISIBLE);
            }
            setTitleStatus(true);
        }
        //由于横竖屏切换分享布局是二套，屏幕旋转的时候不能重新加载布局，固横竖屏切换隐藏掉分享框。
        if (!isDestroyed())
            ShareManager.getInstance().dismissDialog();
        noStatusBar();
    }

    @Override
    public void connectedUSB() {
    }

    private boolean isStop = false;

    @Override
    protected void onStop() {
        super.onStop();
        isStop = true;
    }

    @Override
    public void disconnectedUSB() {
        if (mIsCamera) {
            DownLoadRequest.removeCurDownLoadError(this);
            finish();
        } else {

        }
    }

    private void cameraError(String content) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isDestroyed() || previewCameraDisconnectDialog == null) {
                    return;
                }
                if (commonDownloadDialog != null && commonDownloadDialog.isShowing()) {
                    commonDownloadDialog.dismissDialog();
                    commonDownloadDialog = null;
                }
                mediaPreviewActivityPresenter.dismissDialog();
                previewCameraDisconnectDialog.setDialogContent(14, content);
                previewCameraDisconnectDialog.showGoneTitleDialog();
                previewCameraDisconnectDialog.setSureClick(new PreviewCameraDisconnectDialog.SureClick() {
                    @Override
                    public void onSure(Dialog mDialog) {
                        mDialog.dismiss();
                        finish();
                        if (fragment) {
                            return;
                        }
                        MyAlbumList.openThis(MyAlbumPreview.this, "album_preview");
                    }
                });
            }
        });
    }

    @Override
    public void initData() {
        int index = -1;
        if (previewCameraDisconnectDialog == null)
            previewCameraDisconnectDialog = new PreviewCameraDisconnectDialog(this);
        if (getIntent() != null) {
            mIsCamera = getIntent().getBooleanExtra("iscamera", false);
            fragment = getIntent().getBooleanExtra("fragment", false);
        }
        String album_list = SharedPreferencesUtil.instance().getString("album_list");
        if (album_list != null) {
            AlbumListBean albumListBean = gson.fromJson(album_list, AlbumListBean.class);
            albumList = albumListBean.albumItems;/* (ArrayList<MoAlbumItem>) getIntent().getSerializableExtra("albumlist");*/
        }
        if (getIntent() != null)
            index = getIntent().getIntExtra("index", -1);
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            bottomLayout.setVisibility(View.VISIBLE);
            leftLayout.setVisibility(View.GONE);
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            bottomLayout.setVisibility(View.GONE);
            leftLayout.setVisibility(View.VISIBLE);
        }

        if (mAacDecoderUtil == null) {
            mAacDecoderUtil = new AACDecoderUtil();
//            if (mAacDecoderUtil.prepare()) {
            mAacDecoderUtil.init();
//            }
        }
        mediaPreviewAdater = new MediaPreviewAdater(this.getSupportFragmentManager(), albumList);
        previewViewPager.setAdapter(mediaPreviewAdater);
        previewViewPager.setCurrentItem(index);
        previewViewPager.addOnPageChangeListener(this);
        setMediaType(index);
        mediaPreviewActivityPresenter = new MediaPreviewActivityPresenter(this, uihandler);
        //   informationsPopWindow = new InformationsPopWindow(this, informationIncon);

        setEditState(mIsCamera ? View.VISIBLE : View.GONE);
        if (!mIsCamera) {
            titleLayout.setEnabled(false);
            titleTextView.setEnabled(false);
            informationIncon.setVisibility(View.GONE);
        }

        setTitleStatus(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
        noStatusBar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ConnectionManager.getInstance().setErrorI((data) -> {
            if (data.event == MoErrorCallback.SD_EVENT) {
                switch (data.status) {
                    case MoErrorCallback.SD_OUT:
                        cameraError(getResources().getString(R.string.sdka_out));
                        break;
                    case MoErrorCallback.SD_IN:
                        break;
                    case MoErrorCallback.SD_IN_FAIL:
                        cameraError(getResources().getString(R.string.sdka_error));
                        break;
                    case MoErrorCallback.SD_FULL:
                        break;
                    case MoErrorCallback.SD_LOW:
                        break;
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        ConnectionManager.getInstance().setErrorI(null);
    }

    @OnClick({R.id.titleTextView, R.id.landscapeLayoutShare,
            R.id.landscapeLayoutDown, R.id.landscapeLayoutLike,
            R.id.landscapeLayoutDelete, R.id.portraitLayoutShare,
            R.id.portraitLayoutDown, R.id.portraitLayoutLike,
            R.id.portraitLayoutDelete, R.id.titleBack, R.id.titleLayout/*, R.id.editImage*/})
    public void onViewClicked(View view) {
        int currentItem = previewViewPager.getCurrentItem();
        switch (view.getId()) {
            case R.id.titleLayout:
            case R.id.titleTextView:
                //informationsPopWindow.show(titleLayout, albumList.get(previewViewPager.getCurrentItem()));
                //显示详细信息
                break;
            case R.id.landscapeLayoutShare:
            case R.id.portraitLayoutShare:
                if (downloading) {
                    CameraToastUtil.show(getResources().getString(R.string.already_downloading),
                            MyAlbumPreview.this);
                    return;
                }
                if (mIsCamera && !isDowned) {
                    mIsShare = true;
                    share(true, currentItem);
                } else {
                    share(false, currentItem);
                }
//                startShare();
                break;
            case R.id.landscapeLayoutDown:
            case R.id.portraitLayoutDown:
                mIsShare = false;
                if (downloading || isDowned) {
                    CameraToastUtil.show(downloading ? getResources().getString(R.string.already_downloading) :
                                    getResources().getString(R.string.already_download),
                            MyAlbumPreview.this);
                    return;
                }
                startDownLoad(currentItem);

                break;
            case R.id.landscapeLayoutLike:
            case R.id.portraitLayoutLike:
                mediaPreviewActivityPresenter.like(currentAlbumItem, currentAlbumItem.getCollect() == 1 ? 0 : 1);
                break;
            case R.id.landscapeLayoutDelete:
            case R.id.portraitLayoutDelete:
                if (downloading) {
                    CameraToastUtil.show(getResources().getString(R.string.already_downloading),
                            MyAlbumPreview.this);
                    return;
                }
                delete(currentItem);
                break;
            case R.id.titleBack:
                if (downloading) {
                    mediaPreviewActivityPresenter.showCancelDialog();
                } else {
                    if (pagerChange != null) {
                        pagerChange.onBackPressed();
                        if (fragment) {
                            return;
                        }
                    }
                    MyAlbumList.openThis(this, "album_preview");
                }
                break;
        }
    }

    private void share(boolean isDowned, int currentItem) {
        ShareManager.ShareEntity shareEntity = new ShareManager.ShareEntity();
        shareEntity.setThumbUrl(albumList.get(currentItem).getmThumbnail().getmUri());
        shareEntity.setBackHandle(isDowned);
        shareEntity.setOnStatisticShare(new ShareManager.OnStatisticShare() {
            @Override
            public void onCallBack(ShareManager.ShareChooser shareChooser) {
                StatisticShare.getInstance().shareTo(shareChooser, StatisticShare.Album);
            }
        });
        ShareManager.getInstance().showMoSharePlatform(this, shareEntity, new IShare() {
            @Override
            public void goHome() {

            }

            @Override
            public void goPublishPage() {

            }

            @Override
            public void onItemClick(ShareManager.ShareChooser shareChooser) {
                mShareChooser = shareChooser;
                startDownLoad(currentItem);
            }

            @Override
            public void onStart(SHARE_MEDIA share_media) {

            }

            @Override
            public void onResult(SHARE_MEDIA share_media) {

            }

            @Override
            public void onError(SHARE_MEDIA share_media, Throwable throwable) {

            }

            @Override
            public void onCancel(SHARE_MEDIA share_media) {

            }
        }, null);
    }

    private boolean isDownOk(long size) {
        boolean b = StorageUtils.externalMemoryAvailable();
        if (!b) {
            ToastUtil.showToast(this, getResources().getString(R.string.stor_no_external));
            return false;
        }
        long availableExternalMemorySize = StorageUtils.getAvailableExternalMemorySize();
        if ((availableExternalMemorySize - size) < (50 * 1024 * 1024)) {
            ToastUtil.showToast(this, getResources().getString(R.string.stor_no));
            return false;
        }
        return true;
    }

    private void startDownLoad(int currentItem) {
        MoAlbumItem item = albumList.get(currentItem);
        if (item.getmType().contains("photo")) {
            MoImage moImage = item.getmImage();
            boolean downOk = isDownOk(moImage.getmSize());
            if (!downOk) {
                return;
            }
        } else {
            MoVideo moVideo = item.getmVideo();
            boolean downOk = isDownOk(moVideo.getmSize());
            if (!downOk) {
                return;
            }
        }

        downloading = true;
        ArrayList<MoAlbumItem> downTempList = new ArrayList();
        albumList.get(currentItem).setmDownloadFileName(System.currentTimeMillis() + "");
        downTempList.add(albumList.get(currentItem));
        mediaPreviewActivityPresenter.startDownloadFile(downTempList);
        downIconUI();
    }

    private void delete(int currentItem) {
        if (albumList.size() == 0 || currentItem < 0 || currentItem > albumList.size() - 1) {
            return;
        }
        currentDeleteAlbumItem = albumList.get(currentItem);
        final ArrayList<String> deleteTempList = new ArrayList();
        if (currentDeleteAlbumItem.getmType().contains("video")) {
            deleteTempList.add(currentDeleteAlbumItem.getmVideo().getmUri());
        } else {
            deleteTempList.add(currentDeleteAlbumItem.getmImage().getmUri());
        }

        showDeleteDialog(deleteTempList);
    }

    private void showDeleteDialog(ArrayList<String> deleteTempList) {
        AlbumDeleteDialog albumDeleteDialog = new AlbumDeleteDialog(this);
        albumDeleteDialog.setDialogContent(18, this.getResources().getString(R.string.delete_dialog_content));
        albumDeleteDialog.showGoneTitleDialog();
        albumDeleteDialog.setButtonClickListener(new AlbumDeleteDialog.ButtonClickListener() {
            @Override
            public void sureButton(Dialog mDialog) {
                if (mIsCamera) {
                    isDowned = false;
                    mediaPreviewActivityPresenter.deleteMedia(deleteTempList);
                } else {
                    ArrayList<MoAlbumItem> localFile = new ArrayList<>();
                    localFile.add(currentDeleteAlbumItem);
                    mediaPreviewActivityPresenter.deleteLocalMedia(localFile);
                }

                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
            }

            @Override
            public void cancelButton(Dialog mDialog) {
                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
            }
        });
    }

    /**
     * 删除成功
     */
    public void deleteOKRefreshUI() {
        int pageIndex = albumList.indexOf(currentDeleteAlbumItem);
        albumList.remove(currentDeleteAlbumItem);

        if (mIsCamera) {
            mediaPreviewAdater.refreshData(albumList);
        } else {
            mediaPreviewAdater.notifyDataSetChanged();
        }

        setMediaType(pageIndex);

        CameraToastUtil.show(getResources().getString(R.string.delete_ok), this);
        if (albumList.size() == 0) {
            this.finish();
        }
    }

    /**
     * 控制左边和下边的布局的显示
     */
    public void controlLayoutVisible() {
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT && bottomLayout != null) {
            bottomLayout.setVisibility(View.VISIBLE);
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE && leftLayout != null) {
            leftLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 控制左边和下边布局的隐藏
     */
    public void controlLayoutGone() {
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            bottomLayout.setVisibility(View.GONE);
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            leftLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        hideOrVisibilityTitle(View.VISIBLE);
        controlLayoutVisible();
        if (downloading) {
            //如果是正在下载的话,当前页面不能滑动
            return;
        }
        setMediaType(position);
        isDowned = false;
        Log.i(TAG, "onPageSelected: 大图页面改变了");
    }

    private void setMediaType(int position) {
        if (albumList.size() == 0 || position == albumList.size() || position < 0) {
            return;
        }

        currentAlbumItem = albumList.get(position);
        String path = "";
        if (!currentAlbumItem.getmType().contains("video")) {
            path = currentAlbumItem.getmImage().getmUri();
            titleImage.setVisibility(View.GONE);
        } else {
            path = currentAlbumItem.getmVideo().getmUri();
            titleImage.setVisibility(View.VISIBLE);
        }
        if (currentAlbumItem.ismIsCamrea()) {
            Log.i(TAG, "onPageSelected: 相机资源");
            if (!currentAlbumItem.getmType().contains("video")) {
                titleTextView.setText("Photo");
            } else {
                titleTextView.setText("Video");
            }

            if (currentAlbumItem.getCollect() == 1) {
                landscapeLikeImageView.setImageResource(R.mipmap.alreadylike);
                portraitLikeImageView.setImageResource(R.mipmap.alreadylike);
            } else {
                landscapeLikeImageView.setImageResource(R.mipmap.defaultlike);
                portraitLikeImageView.setImageResource(R.mipmap.defaultlike);
            }

        } else {
            String name = path.substring(path.lastIndexOf("/") + 1);
            name = name.substring(0, name.length() - 4);
            titleImage.setVisibility(View.GONE);
            titleTextView.setText(name);
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    @Override
    public void onBackPressed() {
        if (downloading) {
            mediaPreviewActivityPresenter.showCancelDialog();
        } else {
            if (pagerChange != null) {
                pagerChange.onBackPressed();
            }
        }
    }


    private void downIconUI() {
        if (commonDownloadDialog == null) {
            commonDownloadDialog = new CommonDownloadDialog(this);
            commonDownloadDialog.setCancelable(false);
        }
        if (downloading) {
            commonDownloadDialog.showDialog(true);
            commonDownloadDialog.setCloseDialogListener(new CommonDownloadDialog.CloseDialogListener() {
                @Override
                public void closeDialog() {

                }

                @Override
                public void cancelDialog() {
                    if (commonDownloadDialog.getmDialog() != null) {
                        commonDownloadDialog.getmDialog().dismiss();
                    }
                    sumLen = 0;
                    downloading = false;
                    DownLoadRequestNew.removeCurDownLoadError(mContext);
                    if (mediaPreviewActivityPresenter.getmDownLoad() != null)
                        mediaPreviewActivityPresenter.getmDownLoad().stopDownload();
                }
            });
        } else {
            if (commonDownloadDialog != null) {
                commonDownloadDialog.setCloseDialogListener(null);
                commonDownloadDialog.dismissDialog();
                commonDownloadDialog = null;
            }
        }
    }

    public void setPagerChange(PagerChange pagerChange) {
        this.pagerChange = pagerChange;
    }

    /**
     * 喜欢或者不喜欢成功回调
     *
     * @param moAlbumItem
     */
    public void likeSuccess(final MoAlbumItem moAlbumItem) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                currentAlbumItem.setCollect(moAlbumItem.getCollect());
                int currentItem = previewViewPager.getCurrentItem();
                setMediaType(currentItem);
            }
        });
    }


    @Override
    protected void onDestroy() {
        if (mAacDecoderUtil != null) {
            mAacDecoderUtil.stop();
            mAacDecoderUtil = null;
        }
//        if (informationsPopWindow != null) {
//            informationsPopWindow.onDismiss();
//        }
        previewCameraDisconnectDialog = null;
        if (commonDownloadDialog != null) {
            commonDownloadDialog.destroy();
            commonDownloadDialog = null;
        }
        ShareManager.getInstance().destroy();
        super.onDestroy();
    }

    public void cancelDown() {
        finish();
    }

    public void hideOrVisibilityTitle(int hideOrVisibility) {
        if (titleRl != null)
            titleRl.setVisibility(hideOrVisibility);
    }


    public interface PagerChange {
        void onBackPressed();
    }

    private void setEditState(int visibility) {
        portraitLayoutLike.setVisibility(visibility);
        portraitLayoutDown.setVisibility(visibility);
        landscapeLayoutLike.setVisibility(visibility);
        landscapeLayoutDown.setVisibility(visibility);
    }

    private void shareToNoDown(String path) {
        if (mShareChooser == null) {
            return;
        }
        ShareManager.ShareEntity shareEntity = new ShareManager.ShareEntity();
        shareEntity.setThumbUrl(path);
        shareEntity.setBackHandle(false);
        shareEntity.setOnStatisticShare(new ShareManager.OnStatisticShare() {
            @Override
            public void onCallBack(ShareManager.ShareChooser shareChooser) {
                StatisticShare.getInstance().shareTo(shareChooser, StatisticShare.Album);
            }
        });
        ShareManager.getInstance().shareTo(this, shareEntity, mShareChooser, new IShare() {
            @Override
            public void goHome() {

            }

            @Override
            public void goPublishPage() {

            }

            @Override
            public void onItemClick(ShareManager.ShareChooser shareChooser) {

            }

            @Override
            public void onStart(SHARE_MEDIA share_media) {

            }

            @Override
            public void onResult(SHARE_MEDIA share_media) {

            }

            @Override
            public void onError(SHARE_MEDIA share_media, Throwable throwable) {

            }

            @Override
            public void onCancel(SHARE_MEDIA share_media) {

            }
        });
    }
}
