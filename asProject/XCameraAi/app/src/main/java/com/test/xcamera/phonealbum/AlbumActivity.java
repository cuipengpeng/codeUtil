package com.test.xcamera.phonealbum;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dueeeke.videoplayer.player.IjkVideoView;
import com.dueeeke.videoplayer.player.PlayerConfig;
import com.editvideo.TimelineUtil;
import com.editvideo.ToastUtil;
import com.framwork.base.adapter.BaseRecyclerAdapter;
import com.framwork.base.view.MOBaseActivity;
import com.test.xcamera.R;
import com.test.xcamera.accrssory.AccessoryManager;
import com.test.xcamera.application.AiCameraApplication;
import com.test.xcamera.bean.AlbumDirectory;
import com.test.xcamera.bean.BaseData;
import com.test.xcamera.bean.MoAlbumItem;
import com.test.xcamera.bean.MoImage;
import com.test.xcamera.home.GoUpActivity;
import com.test.xcamera.managers.ConnectionManager;
import com.test.xcamera.mointerface.MoErrorCallback;
import com.test.xcamera.mointerface.MoRequestCallback;
import com.test.xcamera.phonealbum.adapter.AlbumAdapter;
import com.test.xcamera.phonealbum.adapter.VideoEditAdapter;
import com.test.xcamera.phonealbum.presenter.AlbumEditContract;
import com.test.xcamera.phonealbum.presenter.AlbumEditPresenter;
import com.test.xcamera.phonealbum.usecase.MediaClipDataTran;
import com.test.xcamera.phonealbum.usecase.VideoImportCheck;
import com.test.xcamera.phonealbum.widget.MediaDataEmpty;
import com.test.xcamera.phonealbum.widget.MediaDataLoading;
import com.test.xcamera.picasso.Picasso;
import com.test.xcamera.player.AVFrame;
import com.test.xcamera.statistic.StatisticMaterialEdit;
import com.test.xcamera.util.AACDecoderUtil;
import com.test.xcamera.utils.AlbumHelper;
import com.test.xcamera.utils.Constants;
import com.test.xcamera.utils.DensityUtils;
import com.test.xcamera.utils.DisplayUtils;
import com.test.xcamera.utils.DlgUtils;
import com.test.xcamera.utils.DownLoadRequest;
import com.test.xcamera.utils.GlideUtils;
import com.test.xcamera.utils.ItemTouchHelperCallBack;
import com.test.xcamera.utils.LogAccessory;
import com.test.xcamera.utils.LoggerUtils;
import com.test.xcamera.utils.PUtil;
import com.test.xcamera.utils.StorageUtils;
import com.test.xcamera.utils.glide.VideoFrameUtils;
import com.test.xcamera.utils.proxy.Perform;
import com.test.xcamera.utils.proxy.click.NonDuplicateFactory;
import com.test.xcamera.view.VideoPreviewTextureView;
import com.test.xcamera.widget.ActivityContainer;
import com.test.xcamera.widget.AlbumPlayController;
import com.editvideo.MediaConstant;
import com.editvideo.MediaData;
import com.editvideo.dataInfo.ClipInfo;
import com.editvideo.dataInfo.TimelineData;
import com.test.xcamera.widget.LogDataLayout;
import com.meicam.sdk.NvsStreamingContext;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


public class AlbumActivity extends MOBaseActivity implements AlbumEditContract.View,
        AccessoryManager.PreviewDataCallback {
    private static final String TAG = "AlbumActivity";
    @BindView(R.id.tv_middle_title)
    TextView tvMiddleTitle;
    @BindView(R.id.right_tv_titlee)
    TextView rightTvTitlee;
    @BindView(R.id.ryl_view)
    RecyclerView gridRecycleView;
    @BindView(R.id.drop_rey)
    RecyclerView dragRecycleView;
    @BindView(R.id.cover_img)
    ImageView coverImg;
    @BindView(R.id.iv_albumctivity_videoIcon)
    ImageView videoIconImageView;
    @BindView(R.id.ptv_albumctivity_videoPlay)
    VideoPreviewTextureView mPreviewTextureView;
    @BindView(R.id.vc_albumctivity_videoControler)
    AlbumPlayController mDouYinController;
    @BindView(R.id.ijk_albumctivity_ijkPlayer)
    IjkVideoView mIjkVideoView;
    @BindView(R.id.ll_albumctivity_delItem)
    LinearLayout delItemLinearLayout;
    @BindView(R.id.iv_albumctivity_deleteIcon)
    ImageView deleteIconImageView;
    @BindView(R.id.touchRemoveTextView)
    TextView touchRemoveTextView;
    @BindView(R.id.tv_drag_tip)
    TextView mDragTip;
    @BindView(R.id.frame_play_view)
    FrameLayout mFramePlayView;
    @BindView(R.id.mFrameMedia)
    FrameLayout mFrameMedia;
    @BindView(R.id.usbFrame)
    public FrameLayout mUSBFrameTest;

    private MediaDataEmpty mMediaDataEmpty;
    private MediaDataLoading mMediaDataLoading;
    private final int MEDIA_DATA_LOADING = 0;
    private final int MEDIA_DATA_EMPTY = 1;
    List<AlbumDirectory> mAlbumDirectoryList = new ArrayList<>();
    private AlbumAdapter gridViewAdapter;
    private VideoEditAdapter dragAdapter;
    private static String CAMERA_SDCARD_DIR;
    private static String APP_GALARY;
    private static String APP_MYGALARY;
    public static final String KEY_OF_REQUEST_CODE = "requestCodeKey";
    private int requestCode;
    private boolean loading = false;
    private int mPageCount = 0;
    private AlbumDirectory mSDcardAlbumDirectory = new AlbumDirectory();
    private ArrayList<MediaData> mediaDataList = new ArrayList<>();
    private int mSelectClipPosition = -1;
    private int mSelectPlayMedia = 0;
    private boolean mIsPause = true;
    private Dialog dialog;
    private AACDecoderUtil mAacDecoderUtil;
    private AlbumEditPresenter mPresenter;

    @OnClick({R.id.left_iv_title, R.id.tv_middle_title, R.id.right_tv_titlee, R.id.right_iv_title, R.id.ryl_view})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.left_iv_title:
                NonDuplicateFactory.proxy(new Perform() {
                    @Override
                    public void perform() {
                        stopCameraPlay(true);
                        release();
                        finish();
                    }
                });
                break;
            case R.id.tv_middle_title:
                dialog = showAlbumDirListDialog(this);
                break;
            case R.id.right_tv_titlee: //导入数据
                NonDuplicateFactory.proxy(new Perform() {
                    @Override
                    public void perform() {
                        StatisticMaterialEdit.getInstance().setOnEvent(StatisticMaterialEdit.MaterialEdit_Import);
                        if (NvsStreamingContext.getInstance() == null) {
                            TimelineUtil.initStreamingContext();
                        }
                        downloadSDcardFilesAndEditVideo();
                    }
                });

                break;
            case R.id.right_iv_title:
                break;
            case R.id.ryl_view:
                break;
        }
    }

    private boolean mIsOpenVideoEdit = false;
    private boolean mIsStartVideoEdit = false;

    @Override
    public void openVideoEdit(boolean isDownLoad) {
        if (StorageUtils.getAvailableExternalMemorySize() < (400 * 1024 * 1024)) {
            ToastUtil.showToast(AiCameraApplication.getContext(), mContext.getResources().getString(R.string.video_import_stor_no));
            return;
        }
        int check = checkVideo();
        LogAccessory.getInstance().showLog("下载完成:Download_Status:" + mPresenter.getDownloadStatus()
                + "check:" + check
                + "mIsOnResume:" + mIsOnResume
        );

        if (check > 0) {
            Log.i("club", "club_openVideoEdit:" + (mPresenter.getDownloadStatus()));
            if (dragAdapter.mData.size() <= check) {
                ToastUtil.showToast(AiCameraApplication.getContext(), getString(R.string.video_import_error));
                return;
            } else if (mPresenter.getDownloadStatus()) {
                ToastUtil.showToast(AiCameraApplication.getContext(), getString(R.string.video_import_part_error, check));
            }
        }
        if (requestCode == MyVideoEditActivity.REQUEST_CODE) {
            List<ClipInfo> clipInfos = MediaClipDataTran.mediaData2ClipInfo(dragAdapter.mData);
            VideoImportCheck.getInstance().checkVideoIsConvertImport(clipInfos, new WeakReference<>(this), new VideoImportCheck.OnImportCheckCallBack() {
                @Override
                public void onCheckFinish() {
                    VideoImportCheck.mVideoImportCheck.setOnImportCheckCallBack(null);
                    VideoImportCheck.reset();
                    if (mSelectClipPosition != -1) {
                        TimelineData.instance().getClipInfoData().addAll(mSelectClipPosition, clipInfos);
                    } else {
                        TimelineData.instance().getClipInfoData().addAll(clipInfos);
                    }
                    Intent intent = new Intent();
                    setResult(MyVideoEditActivity.RESULT_OK_IN_ALBUM, intent);
                    release();
                    finish();
                }
            });

        } else {
            LogAccessory.getInstance().showLog("下载完成:mIsOnResume" + mIsOnResume
            );
            if (mIsOnResume) {
                mDouYinController.setVisibility(View.GONE);
                mPreviewTextureView.setVisibility(View.GONE);
                coverImg.setVisibility(View.GONE);
                videoIconImageView.setVisibility(View.VISIBLE);
                mIsOpenVideoEdit = true;
                MyVideoEditActivity.startMyVideoEditActivity(AlbumActivity.this, dragAdapter.mData);
            } else {
                mIsStartVideoEdit = true;
                Log.i("club", "club_openVideoEdit:IsOnResume" + mIsOnResume);
            }
        }
    }

    @Override
    public void checkVideoIsConvert() {
        VideoImportCheck.getInstance().checkVideoIsConvert(dragAdapter.mData);
    }

    @Override
    public void showCameraAlbumCount(int count, MoImage thumbnail) {
        if (count < 0) {
            count = 0;
        }
        mAlbumCount = count;
        for (int i = 0; i < mAlbumDirectoryList.size(); i++) {
            final AlbumDirectory albumDirectory = mAlbumDirectoryList.get(i);
            if (CAMERA_SDCARD_DIR.equalsIgnoreCase(albumDirectory.getBucketName())) {
                albumDirectory.setCount(mAlbumCount);
            }
        }
    }


    @Override
    public int initView() {
        return R.layout.activity_album;
    }

    @Override
    public void initData() {
        mPreviewTextureView.setCodeTimeOut(90 * 1000);
        CAMERA_SDCARD_DIR = getString(R.string.camera_gallery);
        APP_GALARY = getString(R.string.app_gallery);
        APP_MYGALARY = getString(R.string.app_mygallery);
        mPresenter = AlbumEditPresenter.getInstance(this, this);
        /*测试相机连接专用*/
        if (LogAccessory.getInstance().isIsShowViewLog()) {
            LogDataLayout logDataLayout = new LogDataLayout(this);
            logDataLayout.setCallBack(new LogDataLayout.LogViewCallBack() {
                @Override
                public void onCallBack() {
                    mUSBFrameTest.removeAllViews();
                    mUSBFrameTest.setVisibility(View.GONE);
                }
            });
            mUSBFrameTest.removeAllViews();
            mUSBFrameTest.addView(logDataLayout);
            mUSBFrameTest.setVisibility(View.VISIBLE);
        } else {
            mUSBFrameTest.setVisibility(View.GONE);
        }
        noStatusBar();
        setStatusBarColor(getResources().getColor(R.color.color_020202), 0);
        rightTvTitlee.setKeepScreenOn(true);
        ActivityContainer.getInstance().addActivity(this);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mFramePlayView.getLayoutParams();
        params.height = PUtil.getScreenH(this) / 2 - DensityUtils.dp2px(mContext, 60);
        mFramePlayView.setLayoutParams(params);

        RelativeLayout.LayoutParams rightTvTitleeParams = (RelativeLayout.LayoutParams) rightTvTitlee.getLayoutParams();
        rightTvTitleeParams.height = DensityUtils.dp2px(mContext, 32);
        rightTvTitleeParams.width = DensityUtils.dp2px(mContext, 56);
        rightTvTitlee.setLayoutParams(rightTvTitleeParams);
        rightTvTitlee.setVisibility(View.VISIBLE);
        rightTvTitlee.setText(getString(R.string.video_edit_import));
        rightTvTitlee.setBackgroundResource(R.drawable.circle_corner_gray_bg_normal_2dp);
        rightTvTitlee.setTextColor(getResources().getColorStateList(R.color.color_666666));
        rightTvTitlee.setClickable(false);

        requestCode = getIntent().getIntExtra(KEY_OF_REQUEST_CODE, -1);
        mSelectClipPosition = getIntent().getIntExtra("mSelectClipPosition", -1);
        Drawable drawable = getResources().getDrawable(R.mipmap.icon_mo_album_down);
        drawable.setBounds(0, 0, 50, 50);
        tvMiddleTitle.setCompoundDrawables(null, null, drawable, null);

        mSDcardAlbumDirectory = new AlbumDirectory();
        mSDcardAlbumDirectory.setBucketName(CAMERA_SDCARD_DIR);
        mediaDataList = new ArrayList<>();
        mSDcardAlbumDirectory.setImageList(mediaDataList);
        File appGalaryDir = new File(Constants.mRootPath, Constants.mAppFilePath);
        if (!appGalaryDir.exists()) {
            appGalaryDir.mkdirs();
        }
        if (mAacDecoderUtil == null) {
            mAacDecoderUtil = new AACDecoderUtil();
//            if (mAacDecoderUtil.prepare()) {
            mAacDecoderUtil.init();
//            }
        }

        initRecycleView();
        initIjkPlayer();

        DownLoadRequest.removeCurDownLoadError(mContext);
        getPageData();
    }

    private void registerSdcardStatusCallBack() {
        ConnectionManager.getInstance().setErrorI((data) -> {
            //如果不是正在显示 则不处理
            if (mIsPause) return;
            if (data.event == MoErrorCallback.SD_EVENT) {
                switch (data.status) {
                    case MoErrorCallback.SD_OUT:
                        if (mPresenter != null) {
                            mPresenter.resetDownloadStatus();
                        }
                        cleanCameraSdOut();
                        DlgUtils.toast(AlbumActivity.this, getString(R.string.sdka_out));
                        if (mSelectMediaData != null && mSelectMediaData.isRemoteData()) {
                            if (mPreviewTextureView != null) {
                                mPreviewTextureView.setVisibility(View.GONE);
                            }
                            if (coverImg != null) {
                                coverImg.setVisibility(View.GONE);
                            }
                            if (videoIconImageView != null) {
                                videoIconImageView.setVisibility(View.VISIBLE);

                            }
                        }
                        mSDcardAlbumDirectory.setCount(0);
                        mSDcardAlbumDirectory.getImageList().clear();
                        if (CAMERA_SDCARD_DIR.equalsIgnoreCase(tvMiddleTitle.getText().toString().trim())) {
                            for (AlbumDirectory directory : mAlbumDirectoryList) {
                                if (CAMERA_SDCARD_DIR.equals(directory.getBucketName())) {

                                    directory.getImageList().clear();
                                    directory.setCount(0);
                                    gridViewUpdate(true, directory.getImageList());
                                    break;
                                }
                            }
                        }
                        break;
                    case MoErrorCallback.SD_IN:
                        mPageCount = 0;
                        if (AccessoryManager.getInstance().mIsRunning) {//相机连接显示相机相册
                            showMediaFrame(MEDIA_DATA_LOADING);
                            getCameraAlbumCount();
                            getRemoteSDcardCameraMediaList(true, 0);
                        }
                        break;
                    case MoErrorCallback.SD_IN_FAIL:
                        DlgUtils.toast(AlbumActivity.this, getString(R.string.sdka_error));

                        break;
                    case MoErrorCallback.SD_FULL:
                        break;
                    case MoErrorCallback.SD_LOW:
                        DlgUtils.toast(AlbumActivity.this, getString(R.string.sdcard_low));

                        break;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        release();
        super.onBackPressed();

    }

    boolean mIsOnResume = false;

    @Override
    protected void onResume() {
        super.onResume();
        if (NvsStreamingContext.getInstance() == null) {
            TimelineUtil.initStreamingContext();
        }
        mIsOnResume = true;
        registerSdcardStatusCallBack();

        if (mIsStartVideoEdit) {
            openVideoEdit(false);
        }
        mIsStartVideoEdit = false;
        /*视频编辑返回后刷新数据*/
        if (mIsOpenVideoEdit) {
            getPageData();
            dragAdapter.mData.clear();
            dragAdapter.notifyDataSetChanged();
            changeImportButtonState();
        }
        mIsPause = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        mIsOnResume = false;
        ConnectionManager.getInstance().setErrorI(null);
        if (mIjkVideoView.isPlaying() && mDouYinController.getIvPlay().getVisibility() == View.GONE)
            stopVideo();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mIsPause = true;
    }

    private void getPageData() {
        getLocalAlbumDirectoryForListDialog();

        if (AccessoryManager.getInstance().mIsRunning) {
            mIsCreateLoading = true;
        }
    }

    public void initRecycleView() {
        gridViewAdapter = new AlbumAdapter(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return (gridViewAdapter.isHeaderView(position) || gridViewAdapter.isBottomView(position)) ? gridLayoutManager.getSpanCount() : 1;
            }
        });
        gridRecycleView.setLayoutManager(gridLayoutManager);
        gridRecycleView.setAdapter(gridViewAdapter);

        gridViewAdapter.setOnRecyclerItemClickListener(new BaseRecyclerAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                NonDuplicateFactory.proxy(new Perform() {
                    @Override
                    public void perform() {
                        MediaData media = gridViewAdapter.mData.get(pos);

                        if (!media.isState() && !VideoImportCheck.getInstance().checkVideoImport(media, dragAdapter.mData.size())) {
                            return;
                        }
                        if (media.isState()) {
                            dragAdapter.mData.remove(media);
                        } else {
                            dragAdapter.mData.add(media);
                        }
                        media.setState(!media.isState());
                        gridViewAdapter.notifyItemChanged(pos);
                        dragAdapter.notifyDataSetChanged();
                        showPositionItemForDragRecycleView(dragAdapter.mData.size() - 1);

                        changeImportButtonState();
                    }
                });

            }
        });

        gridRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (gridRecycleView == null) {
                    return;
                }
                if (!loading && !recyclerView.canScrollVertically(1)) {
                    mPageCount += 1;
                    getRemoteSDcardCameraMediaList(false, mPageCount);
                }
            }
        });
        dragRecycleView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        dragAdapter = new VideoEditAdapter(this);
        dragAdapter.setVideoEdit(false);
        dragRecycleView.setAdapter(dragAdapter);

        dragAdapter.setOnRecyclerItemClickListener(new BaseRecyclerAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                showPositionItemForDragRecycleView(pos);
            }
        });

        deleteIconImageView.setImageResource(R.drawable.ic_edit_no_delete);
        ItemTouchHelperCallBack itemTouchHelperCallBack = new ItemTouchHelperCallBack(delItemLinearLayout, dragAdapter);
        itemTouchHelperCallBack.setDragListener(new ItemTouchHelperCallBack.DragListener() {
            @Override
            public void onDragStart() {
                mIsDraging = true;
                delItemLinearLayout.setAnimation(AnimationUtils.loadAnimation(AlbumActivity.this, R.anim.tranlate_dialog_out));
                delItemLinearLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onDragFinish(boolean delete, int selectedPosition) {
                mIsDraging = false;
                delItemLinearLayout.setAnimation(AnimationUtils.loadAnimation(AlbumActivity.this, R.anim.tranlate_dialog_in));
                delItemLinearLayout.setVisibility(View.INVISIBLE);
                if (selectedPosition < 0 || selectedPosition >= dragAdapter.mData.size() || dragAdapter.mData.size() == 0) {
                    ToastUtil.showToast(AiCameraApplication.getContext(), getString(R.string.video_import_del_error));
                    return;
                }
                String selectPath = dragAdapter.mData.get(selectedPosition).getPath();
                LoggerUtils.printLog("selectPath = " + selectPath);
                if (delete&&!mPresenter.isDownloadStatus()) {
                    dragAdapter.mData.remove(selectedPosition);
                    //更新和删除的item对应的底部的相册的选中状态
                    List<MediaData> dataList;
                    boolean findItem = false;
                    for (int i = 0; i < mAlbumDirectoryList.size(); i++) {
                        dataList = mAlbumDirectoryList.get(i).getImageList();
                        for (int j = 0; j < dataList.size(); j++) {
                            if (dataList.get(j).getPath().equals(selectPath)) {
                                dataList.get(j).setState(false);
                                findItem = true;
                                break;
                            }
                        }
                        if (findItem) {
                            break;
                        }
                    }
                    gridViewAdapter.notifyDataSetChanged();
                }
                int position = selectedPosition;
                if (position < 0 || position >= dragAdapter.mData.size()) {
                    position = dragAdapter.mData.size() - 1;
                }
                showPositionItemForDragRecycleView(position);
                dragAdapter.notifyDataSetChanged();

                changeImportButtonState();
            }

            @Override
            public void onDragAreaChange(boolean isInside) {
                if (isInside) {
                    delItemLinearLayout.setBackgroundColor(getResources().getColor(R.color.color_E85551));
                    deleteIconImageView.setImageResource(R.drawable.ic_edit_deleted);
                    touchRemoveTextView.setText(AlbumActivity.this.getResources().getString(R.string.remove));
                } else {
                    delItemLinearLayout.setBackgroundColor(getResources().getColor(R.color.color_F55555));
                    deleteIconImageView.setImageResource(R.drawable.ic_edit_no_delete);
                    touchRemoveTextView.setText(AlbumActivity.this.getResources().getString(R.string.touch_text));
                }
            }
        });

        ItemTouchHelper helper = new ItemTouchHelper(itemTouchHelperCallBack);
        helper.attachToRecyclerView(dragRecycleView);
    }

    public void changeImportButtonState() {
        if (mDragTip == null || rightTvTitlee == null || dragRecycleView == null) {
            return;
        }
        if (dragAdapter.mData.size() > 0) {
            mDragTip.setVisibility(View.GONE);
            rightTvTitlee.setClickable(true);
            rightTvTitlee.setTextColor(Color.WHITE);
            rightTvTitlee.setBackgroundResource(R.drawable.circle_corner_green_bg_normal_2dp);
            dragRecycleView.smoothScrollToPosition(dragAdapter.mData.size() - 1);//滑动到指定位置
        } else {
            if (mDragTip == null || rightTvTitlee == null) {
                return;
            }
            mDragTip.setVisibility(View.VISIBLE);
            rightTvTitlee.setClickable(false);
            rightTvTitlee.setTextColor(getResources().getColorStateList(R.color.color_666666));
            rightTvTitlee.setBackgroundResource(R.drawable.circle_corner_gray_bg_normal_2dp);
        }
    }

    /**
     * 获取本地相册列表
     */
    private void getLocalAlbumDirectoryForListDialog() {
        showMediaFrame(MEDIA_DATA_LOADING);
        AlbumHelper.getAllPhotoAndVideoByDifference(this, mymap -> {
            Log.i("club", "club:LocalMediaCallback:" + "hideMediaFrame");
            if (tvMiddleTitle == null) {
                return;
            }
            hideMediaFrame();
            if (mAlbumDirectoryList != null && mAlbumDirectoryList.size() > 0) {
                mAlbumDirectoryList.clear();
            }
            mAlbumDirectoryList.addAll(mymap.values());
            if (AccessoryManager.getInstance().mIsRunning) {
                if (!mAlbumDirectoryList.contains(mSDcardAlbumDirectory)) {
                    mAlbumDirectoryList.add(0, mSDcardAlbumDirectory);
                }
                getCameraAlbumCount();
            }
            int appDirPosition = -1;
            for (int i = 0; i < mAlbumDirectoryList.size(); i++) {
                AlbumDirectory albumDirectory = mAlbumDirectoryList.get(i);
                if (AccessoryManager.getInstance().mIsRunning) {//相机连接显示相机相册
                    if (CAMERA_SDCARD_DIR.equalsIgnoreCase(albumDirectory.getBucketName())) {
                        appDirPosition = i;
                        showMediaFrame(MEDIA_DATA_LOADING);
                        getRemoteSDcardCameraMediaList(true, 0);
                        break;
                    }
                }
                if (Constants.appDir.equalsIgnoreCase(albumDirectory.getBucketName())) {
                    appDirPosition = i;
                    albumDirectory.setBucketName(APP_GALARY);
                    break;
                } else if (Constants.myGalleryDir.equalsIgnoreCase(albumDirectory.getBucketName())) {
                    albumDirectory.setBucketName(APP_MYGALARY);
                }

            }
            if (appDirPosition < 0) {
                AlbumDirectory albumDirectory = new AlbumDirectory();
                albumDirectory.setBucketName(APP_GALARY);
                albumDirectory.setImageList(new ArrayList<>());
                mAlbumDirectoryList.add(0, albumDirectory);
                appDirPosition = 0;
            }
            if (currentIndex != -1) {
                appDirPosition = currentIndex;
            }
            if (!CAMERA_SDCARD_DIR.equalsIgnoreCase(mAlbumDirectoryList.get(appDirPosition).getBucketName())) {
                gridViewUpdate(true, mAlbumDirectoryList.get(appDirPosition).getImageList());
            }
            if (tvMiddleTitle != null) {
                tvMiddleTitle.setText(mAlbumDirectoryList.get(appDirPosition).getBucketName());
            }
            if (iv_close != null) {
                iv_close.setText(mAlbumDirectoryList.get(appDirPosition).getBucketName());
            }

        });
    }

    boolean mIsCreateLoading;

    /**
     * 获取相机sdcard媒体文件列表
     */
    private void getRemoteSDcardCameraMediaList(final boolean refresh, int pageIndex) {
        loading = true;
        if (mPresenter != null) {
            mPresenter.getRemoteSDcardCameraMediaList(refresh, pageIndex);
        }
    }

    @Override
    public void showCameraMediaListSuccess(boolean refresh, ArrayList<MoAlbumItem> items) {
        if (tvMiddleTitle == null) {
            return;
        }
        if (items == null || items.size() == 0) {
            hideMediaFrame();
            if (refresh && CAMERA_SDCARD_DIR.equalsIgnoreCase(tvMiddleTitle.getText().toString().trim())) {
                showMediaFrame(MEDIA_DATA_EMPTY);
            }
            return;
        }
        mIsCreateLoading = false;
        loading = false;
        if (!mAlbumDirectoryList.contains(mSDcardAlbumDirectory)) {
            mAlbumDirectoryList.add(0, mSDcardAlbumDirectory);
        }
        if (mAlbumDirectoryList.size() <= 0) {
            showMediaFrame(MEDIA_DATA_EMPTY);
            return;
        }
        hideMediaFrame();
        int sdcardIndex = -1;
        for (int i = 0; i < mAlbumDirectoryList.size(); i++) {
            if (CAMERA_SDCARD_DIR.equalsIgnoreCase(mAlbumDirectoryList.get(i).getBucketName())) {
                sdcardIndex = i;
                break;
            }
        }

        if (refresh) {
            mAlbumDirectoryList.get(sdcardIndex).getImageList().clear();
        }

        MoAlbumItem albumItem;
        for (int i = 0; i < items.size(); i++) {
            albumItem = items.get(i);
            MediaData mediaData = new MediaData();
            mediaData.setRemoteData(true);
            mediaData.setRemotePath(albumItem.getmThumbnail().getmUri());
            mediaData.setPath(albumItem.getmThumbnail().getmUri());
            mediaData.setData(System.currentTimeMillis());
            mediaData.setRotate(albumItem.getRotate());
            if (albumItem.isVideo()) {
                mediaData.setType(MediaConstant.VIDEO);
                mediaData.setRemoteVideoUri(albumItem.getmVideo().getmUri());
                mediaData.setVideoSize(albumItem.getmVideo().getmSize());
                mediaData.setDuration(albumItem.getmVideo().getmDuration());
            } else {
                mediaData.setRemoteImageUri(albumItem.getmImage().getmUri());
                mediaData.setImageSize(albumItem.getmImage().getmSize());
                mediaData.setType(MediaConstant.IMAGE);
            }
            mAlbumDirectoryList.get(sdcardIndex).getImageList().add(mediaData);
        }

        if (CAMERA_SDCARD_DIR.equalsIgnoreCase(tvMiddleTitle.getText().toString().trim())) {
            if (mAlbumDirectoryList.get(sdcardIndex).getImageList().size() >= mAlbumCount) {
                gridViewAdapter.setIsShowBottom(true);
                loading = true;
            } else {
                gridViewAdapter.setIsShowBottom(false);

            }
            gridViewAdapter.updateData(true, mAlbumDirectoryList.get(sdcardIndex).getImageList());
        }
    }

    @Override
    public void showCameraMediaListFailed() {
        if (tvMiddleTitle == null) {
            return;
        }
        hideMediaFrame();
        showMediaFrame(MEDIA_DATA_EMPTY);
        loading = false;
    }

    @Override
    public void cancelDownload() {
        if(mSelectPlayMedia>=0&&mSelectPlayMedia<dragAdapter.mData.size()){
            showPositionItemForDragRecycleView(mSelectPlayMedia);
        }

    }

    public void initIjkPlayer() {
        PlayerConfig config = new PlayerConfig.Builder().setLooping().build();
        mIjkVideoView.setPlayerConfig(config);
        mDouYinController.setMediaPlayer(mIjkVideoView);
    }

    /**
     * 下载sdcard文件, 并导入
     */
    private void downloadSDcardFilesAndEditVideo() {
        stopVideo();
        stopCameraPlay(true);
        mIjkVideoView.release();
        if (mPresenter != null) {
            mPresenter.downloadSDCardFilesAndEditVideo(dragAdapter.mData);
        }
    }

    private int checkVideo() {
        int tag = 0;
        MediaData mediaData;
        for (int i = 0; i < dragAdapter.mData.size(); i++) {
            mediaData = dragAdapter.mData.get(i);
            if (!new File(mediaData.getPath()).exists()) {
                tag++;
            } else if (mediaData.getType() == MediaConstant.VIDEO) {
                if (VideoImportCheck.getInstance().getVideoBitRate(mediaData.getPath()) == 0) {
                    tag++;
                }
            }
        }
        return tag;
    }

    private MediaData mSelectMediaData;

    /**
     * 显示拖拽列表指定位置的view
     *
     * @param position
     */
    public void showPositionItemForDragRecycleView(int position) {
        stopVideo();
        mIjkVideoView.release();
        videoIconImageView.setVisibility(View.GONE);
        mSelectPlayMedia=position;
        if (dragAdapter.mData.size() > 0) {
            dragAdapter.setSelectPosition(position, false);
            mSelectMediaData = dragAdapter.mData.get(position);
            if (mSelectMediaData.getType() == MediaConstant.VIDEO && mSelectMediaData.isRemoteData()) {
                stopCameraPlay(false);
            } else {
                stopCameraPlay(true);
            }
            mIjkVideoView.setVisibility(View.GONE);
            mPreviewTextureView.setVisibility(View.GONE);
            mDouYinController.setVisibility(View.GONE);
            coverImg.setVisibility(View.GONE);
            if (mSelectMediaData.getType() == MediaConstant.VIDEO) {
                mDouYinController.setVisibility(View.VISIBLE);
                mDouYinController.mPauseImageView.setVisibility(View.GONE);
                AccessoryManager.getInstance().setPreviewDataCallback(this);
                mDouYinController.setRemoteVideoUrl(mSelectMediaData.isRemoteData());
                if (mSelectMediaData.isRemoteData()) {
                    mPreviewTextureView.setVisibility(View.VISIBLE);
                    startCameraPlay();
                } else {
                    mPreviewTextureView.releaseThread();
                    //点击完播放
                    mIjkVideoView.setVisibility(View.VISIBLE);
                    startPlay(mSelectMediaData.getPath());
                }


            } else {
                coverImg.setVisibility(View.VISIBLE);
                if (mSelectMediaData.isRemoteData()) {
                    coverImg.setImageResource(R.mipmap.bank_thumbnail_local);
                    loadBitmapDelayed(200);
                } else {
                    setRotation(mSelectMediaData.getRotate(), coverImg);
                    GlideUtils.GlideLoader(this, mSelectMediaData.getPath(), coverImg);
                }
            }
        } else {
            stopCameraPlay(true);
            mPreviewTextureView.setVisibility(View.GONE);
            mIjkVideoView.setUrl(null);
            videoIconImageView.setVisibility(View.VISIBLE);
            coverImg.setVisibility(View.GONE);
        }
        String currentUrl = mIjkVideoView.getmCurrentUrl();
        mDouYinController.setCurrentUrl(currentUrl);

    }

    private void loadBitmapDelayed(long time) {
        mLoadHandler.removeMessages(0);
        Message msg = mLoadHandler.obtainMessage();
        msg.what = 0;
        msg.obj = time;
        mLoadHandler.sendMessageDelayed(msg, time);
    }

    private Handler mLoadHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (coverImg != null) {
                        coverImg.setRotation(90);
                        setRotation(mSelectMediaData.getRotate(), coverImg);
                        VideoFrameUtils.loadVideoThumbnail(AlbumActivity.this, mSelectMediaData.getRemotePath(), coverImg, 0);
                    }
                    break;
            }
        }
    };

    private void setRotation(int rotate, ImageView imageView) {
        int range = 0;
        if (rotate == 1) {
            range = 270;
        } else if (rotate == 2) {
            range = 180;
        } else if (rotate == 3) {
            range = 90;
        }
        imageView.setRotation(range);
    }

    @Override
    public void connectedUSB() {
        super.connectedUSB();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mIsDisconnectedUSB = true;
                VideoImportCheck.getInstance().sendCancelMediaFileConvert();
                VideoImportCheck.reset();
                GoUpActivity.startGoUpActivity(AlbumActivity.this);
                release();
                finish();
            }
        });
    }

    private boolean mIsDisconnectedUSB = true;

    @Override
    public void disconnectedUSB() {
        super.disconnectedUSB();
        if (mPresenter != null) {
            mPresenter.resetDownloadStatus();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mPresenter != null) {
                    mPresenter.clearDownloadHandleMsg();
                }
                if (gridRecycleView == null) {
                    return;
                }
                cleanCameraSdOut();
                if (CAMERA_SDCARD_DIR.equals(tvMiddleTitle.getText())) {
                    tvMiddleTitle.setText(APP_GALARY);
                    updateList();
                }
                if (mIsDisconnectedUSB) {
                    mIsDisconnectedUSB = false;
                    show(getString(R.string.disconenct_usb));
                }

                if (mAlbumDirectoryList != null && mAlbumDirectoryList.size() > 0) {
                    mAlbumDirectoryList.remove(0);
                }
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                    dialog = null;
                    tvMiddleTitle.setText(APP_GALARY);
                    showAlbumDirListDialog(AlbumActivity.this);
                }
                if (mSelectMediaData != null && mSelectMediaData.isRemoteData()) {
                    if (mDouYinController != null)
                        mDouYinController.setVisibility(View.GONE);
                    if (mPreviewTextureView != null)
                        mPreviewTextureView.setVisibility(View.GONE);
                    if (coverImg != null)
                        coverImg.setVisibility(View.GONE);
                    if (videoIconImageView != null)
                        videoIconImageView.setVisibility(View.VISIBLE);
                }

            }
        });
    }

    public void updateList() {
        for (int i = 0; i < mAlbumDirectoryList.size(); i++) {
            final AlbumDirectory albumDirectory = mAlbumDirectoryList.get(i);
            if (albumDirectory.getBucketName().equals(tvMiddleTitle.getText().toString())) {
                albumDirectory.index = i;
                gridViewUpdate(true, albumDirectory.getImageList());
                break;
            }

        }
    }

    private void cleanCameraSdOut() {
        if (mPresenter != null) {
            mPresenter.downloadVideoDel();
            mPresenter.downloadDestroy();
        }
        if (dragAdapter != null && dragAdapter.mData != null) {
            if (dragAdapter != null) {
                List<MediaData> mediaData = dragAdapter.mData;
                Iterator item = mediaData.iterator();
                while (item.hasNext()) {
                    MediaData model = (MediaData) item.next();
                    if (model.isRemoteData()) {
                        item.remove();
                    }
                }
            }
            dragAdapter.notifyDataSetChanged();
        }
        changeImportButtonState();
    }

    @NonNull
    private MoRequestCallback getCallback(final MediaData mediaData) {
        final MoRequestCallback moRequestCallback = new MoRequestCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                    }
                });

            }

            @Override
            public void onFailed() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                    }
                });

            }
        };

        return moRequestCallback;
    }

    /**
     * 相机视频播放器
     */
    private void startCameraPlay() {
        mPreviewTextureView.releaseThread();
        mPlayHandler.removeMessages(0);
        mPlayHandler.removeMessages(1);
        if (mSelectMediaData.isRemoteData()) {
            mPlayHandler.sendEmptyMessage(1);
        } else {
            mPlayHandler.sendEmptyMessageDelayed(1, 200);
        }

    }

    private Handler mPlayHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if (mSelectMediaData == null) {
                        ToastUtil.showToast(AiCameraApplication.getContext(), getString(R.string.video_import_play_error));
                        return;
                    }
                    if (mPreviewTextureView.getVisibility() == View.GONE) {
                        LoggerUtils.printLog("隐藏中不用重播");
                        return;
                    }
                    if (AccessoryManager.getInstance().mIsRunning)
                        ConnectionManager.getInstance().start_play(getCallback(mSelectMediaData), mSelectMediaData.getRemoteVideoUri());

                    break;
                case 1:
                    rotate(mSelectMediaData.getRotate());

                    mPreviewTextureView.releaseCodec();
                    mPreviewTextureView.init();
                    mPlayHandler.sendEmptyMessageDelayed(0, 300);
            }
        }
    };

    private void rotate(int rotate) {
        int range = 0;
        int h = PUtil.getScreenH(this) / 2 - DensityUtils.dp2px(mContext, 60);
        int w = (int) (h * 1080 / 1920f);
        if (rotate == 0) {
            range = 0;
            mPreviewTextureView.setRotate(false);

        } else if (rotate == 1) {
            range = 270;
            mPreviewTextureView.setRotate(true);
        } else if (rotate == 2) {
            range = 180;
            mPreviewTextureView.setRotate(false);

        } else if (rotate == 3) {
            range = 90;
            mPreviewTextureView.setRotate(true);

        }
        mPreviewTextureView.setmRotation(range);
        mPreviewTextureView.setHeightRotation(h);
        mPreviewTextureView.setWidthRotation(w);
    }

    /**
     * 相机停止视频播放
     */
    private void stopCameraPlay(boolean isStopThread) {
        if (mPreviewTextureView != null && isStopThread) {
            mPreviewTextureView.releaseThread();
        }
        if (AccessoryManager.getInstance().mIsRunning) {
            ConnectionManager.getInstance().stopPlay(null);
        }
    }

    /**
     * 手机视频播放器
     *
     * @param video
     */
    private void startPlay(String video) {
        mDouYinController.setSelect(false);
        mIjkVideoView.setUrl(video);
        mIjkVideoView.setScreenScale(IjkVideoView.SCREEN_SCALE_DEFAULT);
        mIjkVideoView.start();
    }


    public void stopVideo() {
        mIjkVideoView.pause();
        if (AccessoryManager.getInstance().mIsRunning) {
            ConnectionManager.getInstance().pausePlay(null);
        }
        if (mDouYinController != null) {
            mDouYinController.getIvPlay().setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        ActivityContainer.getInstance().removeActivity(this);
        release();
        super.onDestroy();
    }

    private void release() {
        if (mPreviewTextureView != null) {
            mPreviewTextureView.release();
            mPreviewTextureView = null;
        }
        if (mLoadHandler != null) {
            mLoadHandler.removeCallbacksAndMessages(null);
        }
        if (mPlayHandler != null) {
            mPlayHandler.removeCallbacksAndMessages(null);
        }
        if (mPresenter != null) {
            mPresenter.downloadDestroy();
            mPresenter.destroy();
        }
        if (AccessoryManager.getInstance().mIsRunning) {
            ConnectionManager.getInstance().stopPlay(null);
        }

        if (mIjkVideoView != null) {
            mIjkVideoView.release();
        }
        if (mAacDecoderUtil != null) {
            mAacDecoderUtil.stop();
            mAacDecoderUtil = null;
        }
    }

    /**
     * 显示媒体加载中和加载后占位图
     *
     * @param status
     */
    private void showMediaFrame(int status) {
        try {
            if (mFrameMedia == null) {
                return;
            }
            mFrameMedia.removeAllViews();
            mFrameMedia.setVisibility(View.VISIBLE);
            mFrameMedia.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            if (status == MEDIA_DATA_LOADING) {
                if (mMediaDataLoading == null) {
                    mMediaDataLoading = new MediaDataLoading(this);
                }
                mFrameMedia.removeAllViews();
                mFrameMedia.addView(mMediaDataLoading);

            } else if (status == MEDIA_DATA_EMPTY) {
                if (mMediaDataEmpty == null) {
                    mMediaDataEmpty = new MediaDataEmpty(this);
                }
                mFrameMedia.addView(mMediaDataEmpty);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void hideMediaFrame() {
        if (mFrameMedia == null) {
            return;
        }
        mFrameMedia.removeAllViews();
        mFrameMedia.setVisibility(View.GONE);
    }

    private int mAlbumCount = 0;

    private void getCameraAlbumCount() {
        if (mPresenter != null) {
            mPresenter.getCameraAlbumCount();
        }
    }

    private int currentIndex = -1;

    /**
     * 显示相册目录列表页面
     *
     * @param context
     * @return
     */
    private TextView iv_close;

    public Dialog showAlbumDirListDialog(Context context) {
        final Dialog customDialog = new Dialog(context, R.style.top_dialog_style);
        LinearLayout dialogLinearLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.top_dialog_layout, null);
        ScrollView scrollView = dialogLinearLayout.findViewById(R.id.sv_albumActivity_albumDialog);
        LinearLayout listLinearLayout = dialogLinearLayout.findViewById(R.id.lin1);
        TextView iv_close = dialogLinearLayout.findViewById(R.id.iv_close);

        iv_close.setText(tvMiddleTitle.getText());
        Drawable drawable = getResources().getDrawable(R.mipmap.icon_mo_album_down);
        drawable.setBounds(0, 0, 50, 50);
        iv_close.setCompoundDrawables(null, null, drawable, null);
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.dismiss();
            }
        });

        LinearLayout itemView;
        ImageView imageView;
        TextView title, tv_count;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, DisplayUtils.dpInt2px(context, 100));
        int margin = DisplayUtils.dpInt2px(context, 10);

        for (int i = 0; i < mAlbumDirectoryList.size(); i++) {
            final AlbumDirectory albumDirectory = mAlbumDirectoryList.get(i);
            albumDirectory.index = i;
            itemView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.top_dialog_item, null);
            if (i == 0) {
                params.setMargins(margin, margin, margin, margin);
            } else {
                params.setMargins(margin, 0, margin, margin);
            }
            itemView.setLayoutParams(params);
            final int finalI = i;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentIndex = albumDirectory.index;
                    tvMiddleTitle.setText(albumDirectory.getBucketName());
                    Log.i("CURRENT_TEST", "onClick: " + albumDirectory.index);
                    gridViewUpdate(true, albumDirectory.getImageList());
                    customDialog.dismiss();
                }
            });
            //相机目录
            if (CAMERA_SDCARD_DIR.equalsIgnoreCase(albumDirectory.getBucketName())) {
                listLinearLayout.addView(itemView, 0);
                //手机创建app相册
            } else if (Constants.appDir.equalsIgnoreCase(albumDirectory.getBucketName()) || APP_GALARY.equalsIgnoreCase(albumDirectory.getBucketName())) {
                albumDirectory.setBucketName(APP_GALARY);
                if (listLinearLayout.getChildCount() > 0 && CAMERA_SDCARD_DIR.equals(mAlbumDirectoryList.get(0).getBucketName())) {
                    listLinearLayout.addView(itemView, 1);
                } else {
                    listLinearLayout.addView(itemView, 0);
                }
                //其他目录
            } else if (Constants.myGalleryDir.equalsIgnoreCase(albumDirectory.getBucketName()) || APP_MYGALARY.equalsIgnoreCase(albumDirectory.getBucketName())) {
                albumDirectory.setBucketName(APP_MYGALARY);
                if (listLinearLayout.getChildCount() > 0 && CAMERA_SDCARD_DIR.equals(mAlbumDirectoryList.get(0).getBucketName())) {
                    listLinearLayout.addView(itemView, 1);
                } else {
                    listLinearLayout.addView(itemView, 0);
                }
                //其他目录
            } else {
                listLinearLayout.addView(itemView);
            }

            imageView = itemView.findViewById(R.id.iv_image);
            title = itemView.findViewById(R.id.tv_ablum_name);
            tv_count = itemView.findViewById(R.id.tv_ablum_count);
            if (albumDirectory.getImageList().size() > 0) {
                if (CAMERA_SDCARD_DIR.equalsIgnoreCase(albumDirectory.getBucketName())) {
                    Picasso.with(context).load(albumDirectory.getImageList().get(0).getRemotePath()).placeholder(R.mipmap.bank_thumbnail_local)
                            .error(R.mipmap.bank_thumbnail_local).into(imageView);
                } else {
                    GlideUtils.GlideLoader(context, albumDirectory.getImageList().get(0).getPath(), imageView);
                }
            }
            title.setText(albumDirectory.getBucketName());
            if (mIsCreateLoading && CAMERA_SDCARD_DIR.equalsIgnoreCase(albumDirectory.getBucketName())) {
                tv_count.setText("- -");
            }
            if (CAMERA_SDCARD_DIR.equalsIgnoreCase(albumDirectory.getBucketName())) {
                tv_count.setText(albumDirectory.getCount() + "");

            } else {
                tv_count.setText(albumDirectory.getImageList().size() + "");

            }
        }

        Window window = customDialog.getWindow();
        WindowManager.LayoutParams localLayoutParams = window
                .getAttributes();
        window.getDecorView().setPadding(0, 0, 0, 0);
        localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        localLayoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        localLayoutParams.x = 0;
        localLayoutParams.y = 0;
        scrollView.setMinimumWidth(1000);
        customDialog.onWindowAttributesChanged(localLayoutParams);
        customDialog.setCanceledOnTouchOutside(true);
        customDialog.setCancelable(true);
        customDialog.setContentView(dialogLinearLayout);
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            if (!activity.isFinishing()) {
                customDialog.show();
            }
        }
        return customDialog;
    }

    private void gridViewUpdate(boolean refresh, List<MediaData> mediaDataList) {
        if (CAMERA_SDCARD_DIR.equalsIgnoreCase(tvMiddleTitle.getText().toString().trim()) &&
                mediaDataList.size() < mAlbumCount) {
            gridViewAdapter.setIsShowBottom(false);
        } else {
            gridViewAdapter.setIsShowBottom(true);
        }
        gridViewAdapter.updateData(refresh, mediaDataList);
        if (mediaDataList.size() > 0) {
            hideMediaFrame();
        } else {
            showMediaFrame(MEDIA_DATA_EMPTY);
        }
    }

    private long frameMs = 0;

    private void refreshSeekBar(BaseData videoBaseData) {
        if (videoBaseData == null || mPreviewTextureView == null || mPreviewTextureView.getVisibility() == View.GONE) {
            return;
        }
        frameMs = videoBaseData.getmVideoFrameInfo().getmTimeMs();
        if ((mSelectMediaData.getDuration() - frameMs) < 70) {
            ConnectionManager.getInstance().seekPlay(new MoRequestCallback() {
                @Override
                public void onSuccess() {
                }
            }, 0);
        }
    }

    @Override
    public void onVideoDataAvailable(BaseData baseData) {
        AVFrame avFrame = new AVFrame(baseData, baseData.getmDataSize(), false, true);
        if (avFrame == null || mPreviewTextureView == null) {
            return;
        }
        mPreviewTextureView.onReceived(avFrame);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                refreshSeekBar(baseData);
            }
        });
    }

    @Override
    public void onAudioDataAvailable(BaseData baseData) {
        if (mAacDecoderUtil != null) {
            mAacDecoderUtil.onReceivedAudioData(baseData.getmAudioFrameInfo());
        }
    }

    public static void open(Context context, List<MediaData> list) {
        Intent intent = new Intent(context, AlbumActivity.class);
        context.startActivity(intent);
    }

    private boolean mIsDraging = false;

    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (ev.getActionMasked() == MotionEvent.ACTION_DOWN || ev.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN) {
            Log.i("club", "club-dispatchTouchEvent:" + mIsDraging);
            if (mIsDraging) {
                return false;
            }
            return super.dispatchTouchEvent(ev);
        }

        return super.dispatchTouchEvent(ev);
    }
}
