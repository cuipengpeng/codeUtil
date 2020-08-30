//package com.meetvr.aicamera.moalbum.activity;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.content.Intent;
//import android.content.res.Configuration;
//import android.graphics.Color;
//import android.graphics.Rect;
//
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//
//import android.support.annotation.Nullable;
//import android.support.v4.widget.SwipeRefreshLayout;
//import android.support.v7.widget.GridLayoutManager;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.support.v7.widget.SimpleItemAnimator;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.View;
//import android.view.animation.Animation;
//import android.view.animation.AnimationUtils;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.editvideo.ToastUtil;
//import com.framwork.base.view.MOBaseActivity;
//import com.meetvr.aicamera.MainActivity;
//import com.meetvr.aicamera.R;
//import com.meetvr.aicamera.accrssory.AccessoryManager;
//import com.meetvr.aicamera.activity.MoFPVActivity;
//import com.meetvr.aicamera.album.MomediaWrraper;
//import com.meetvr.aicamera.album.SwipeRefreshView;
//import com.meetvr.aicamera.album.adapter.CameraPhotoListAdapter;
//import com.meetvr.aicamera.album.sectionrec.SectionedSpanSizeLookup;
//import com.meetvr.aicamera.bean.AlbumListBean;
//import com.meetvr.aicamera.bean.MoAlbumItem;
//import com.meetvr.aicamera.bean.MoImage;
//import com.meetvr.aicamera.cameraclip.NewerGuideActivity;
//import com.meetvr.aicamera.constants.LogcatConstants;
//import com.meetvr.aicamera.home.GoUpActivity;
//import com.meetvr.aicamera.managers.ConnectionManager;
//import com.meetvr.aicamera.moalbum.adapter.MoAlbumFoldersAdapter;
//import com.meetvr.aicamera.moalbum.bean.MoAlbumFolder;
//import com.meetvr.aicamera.moalbum.data.MediaReadTask;
//import com.meetvr.aicamera.moalbum.data.MediaReader;
//import com.meetvr.aicamera.moalbum.interfaces.MoAlbumCallback;
//import com.meetvr.aicamera.moalbum.interfaces.MoAlbumFolderItemClickListener;
//import com.meetvr.aicamera.moalbum.my_album.MoAlbumPresenter;
//import com.meetvr.aicamera.mointerface.MoErrorCallback;
//import com.meetvr.aicamera.mointerface.MoRequestCallback;
//import com.meetvr.aicamera.utils.CameraToastUtil;
//import com.meetvr.aicamera.utils.DensityUtils;
//import com.meetvr.aicamera.utils.DlgUtils;
//import com.meetvr.aicamera.utils.DownLoadRequest;
//import com.meetvr.aicamera.utils.FileUtil;
//import com.meetvr.aicamera.utils.SPUtils;
//import com.meetvr.aicamera.utils.SharedPreferencesUtil;
//import com.meetvr.aicamera.view.basedialog.dialog.CommonDownloadDialog;
//import com.moxiang.common.logging.Logcat;
//
//import java.lang.ref.WeakReference;
//import java.util.ArrayList;
//
//import butterknife.BindView;
//import butterknife.OnClick;
//
///**
// * Created by zll on 2019/11/21.
// */
//
//public class AlbumListActivity extends MOBaseActivity implements MoAlbumCallback, MediaReadTask.Callback,
//        AccessoryManager.ConnectStateListener, SwipeRefreshLayout.OnRefreshListener, SwipeRefreshView.OnLoadMoreListener, CameraPhotoListAdapter.MyAlbumAdapterCallback, MoAlbumFolderItemClickListener, CameraPhotoListAdapter.OnItemClickListener {
//    private static final String TAG = "AlbumListActivity";
//    private static final String TAG_TEST = "AlbumList----";
//    private static final int REFRESH_CLASSIFY_ADAPTER = 1001;
//    @BindView(R.id.activity_mo_album_back)
//    ImageView mBackBtn;
//    @BindView(R.id.activity_mo_album_change_layout)
//    LinearLayout mChangeLayout;
//    @BindView(R.id.activity_mo_album_edit_layout)
//    RelativeLayout mEditLayout;
//    @BindView(R.id.activity_mo_album_edit_image)
//    ImageView mEditBtn;
//    @BindView(R.id.activity_mo_album_edit_text)
//    TextView mEditText;
//    @BindView(R.id.activity_mo_album_media_rc)
//    RecyclerView mMediaRecyclerView;
//    @BindView(R.id.activity_mo_album_media_refresh_view)
//    SwipeRefreshView mSwipeRefreshView;
//    @BindView(R.id.activity_mo_album_classify_rc)
//    RecyclerView mFolderRecyclerView;
//    @BindView(R.id.activity_mo_album_change_name)
//    TextView mTitleText;
//    @BindView(R.id.activity_mo_album_media_view_group)
//    RelativeLayout mRecyclerViewGroup;
//    @BindView(R.id.photo_list_bottom_layout)
//    LinearLayout photo_list_bottom_layout;
//    @BindView(R.id.activity_camera_photo_list_download)
//    ImageView mDownLoadImg;
//    @BindView(R.id.activity_camera_photo_list_like)
//    ImageView mLikeImg;
//    @BindView(R.id.activity_camera_photo_list_delete)
//    ImageView mDeleteImg;
//    @BindView(R.id.activity_mo_album_change_down_img)
//    ImageView mDownIcon;
//    @BindView(R.id.albumNullLayout)
//    RelativeLayout albumNullLayout;
//
//    @BindView(R.id.downRl)
//    RelativeLayout downRl;
//    @BindView(R.id.likeRl)
//    RelativeLayout likeRl;
//    @BindView(R.id.deleteRl)
//    RelativeLayout deleteRl;
//
//
//    private boolean mIsFront = false;
//    private MoAlbumPresenter mAblumPresenter;
//    private MediaReadTask mMediaReadTask;
//    private MoAlbumFoldersAdapter mFoldersAdapter;
//    private CameraPhotoListAdapter mPhotoListAdapter;
//    private MediaReader mMediaReader;
//    private ArrayList<MoAlbumFolder> mAlbumFolders;
//    private MomediaWrraper mMediaWrraper = new MomediaWrraper();
//    private boolean isSelectedPhotoStatus = false;
//    private int mPage;
//    private String mTitle;
//    // 当前相册的类型 0:APP相册  1:相机相册  2:手机其他相册
//    private int mType = -1;
//    private int mFolderPosition = 0;
//    private String jump_flag;
//    private boolean cameraIsConnect = true;
//    private boolean isSdCardOut = false;
//    private boolean isSendStopPreview = true;
//    private boolean isLoadMoreData = false;
//    private boolean isFinish = false;
//    private boolean isRefreshSuccess = false;
//
//    @SuppressLint("HandlerLeak")
//    private Handler mHandler = new MyHandler(this);
//
//
//    private static class MyHandler extends Handler {
//        private WeakReference<AlbumListActivity> mAlbumListActivity;
//
//        public MyHandler(AlbumListActivity context) {
//            mAlbumListActivity = new WeakReference<AlbumListActivity>(context);
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            if (mAlbumListActivity == null || mAlbumListActivity.get() == null)
//                return;
//
//            switch (msg.what) {
//                case REFRESH_CLASSIFY_ADAPTER:
//                    if (mAlbumListActivity.get().isDestroyed()) {
//                        return;
//                    }
//                    mAlbumListActivity.get().mAlbumFolders = (ArrayList<MoAlbumFolder>) msg.obj;
//                    if (mAlbumListActivity.get().mAlbumFolders == null) {
//                        return;
//                    }
//
//                    for (int i = 0; i < mAlbumListActivity.get().mAlbumFolders.size(); i++) {
//                        if (mAlbumListActivity.get().mFolderPosition == i) {
//                            mAlbumListActivity.get().mAlbumFolders.get(i).setChecked(true);
//                        } else {
//                            mAlbumListActivity.get().mAlbumFolders.get(i).setChecked(false);
//                        }
//                    }
//
//                    if (mAlbumListActivity.get().mFolderRecyclerView != null)
//                        mAlbumListActivity.get().mFolderRecyclerView.scrollToPosition(mAlbumListActivity.get().mFolderPosition);
//
//                    MoAlbumFolder moAlbumFolder = mAlbumListActivity.get().mAlbumFolders.get(0);
//                    if (moAlbumFolder == null) {
//                        return;
//                    }
//                    Log.i("UUUUUUU", "handleMessage: " + mAlbumListActivity.get().jump_flag);
//                    if (!TextUtils.isEmpty(mAlbumListActivity.get().jump_flag) && (TextUtils.equals(mAlbumListActivity.get().jump_flag, "fpv")
//                            || TextUtils.equals(mAlbumListActivity.get().jump_flag, "go_up") ||
//                            TextUtils.equals(mAlbumListActivity.get().jump_flag, "sdcard_out"))) {
//                        if (moAlbumFolder.getType() == 1) {
//                            //表明是相机相册
//                            mAlbumListActivity.get().notifyFolderAdapter(mAlbumListActivity.get().mAlbumFolders);
//                            if (mAlbumListActivity.get().mFolderPosition == 0) {
//                                mAlbumListActivity.get().mTitle = moAlbumFolder.getName();
//                                mAlbumListActivity.get().mType = moAlbumFolder.getType();
//                                if (mAlbumListActivity.get().mTitleText != null) {
//                                    mAlbumListActivity.get().mTitleText.setText(mAlbumListActivity.get().mTitle);
//                                }
//                                mAlbumListActivity.get().requestCameraAlbum();
//                            } else {
//                                mAlbumListActivity.get().refreshLocalData();
//                            }
//                        } else {
//                            mAlbumListActivity.get().refreshLocalData();
//                        }
//                    } else {
//                        mAlbumListActivity.get().refreshLocalData();
//                    }
//                    mAlbumListActivity.get().setBottomEditState();
//                    break;
//            }
//        }
//    }
//
//    private void refreshLocalData() {
//        if (mAlbumFolders.size() == 0 || mAlbumFolders.size() <= mFolderPosition) {
//            return;
//        }
//
//        MoAlbumFolder moAlbumFolder = mAlbumFolders.get(0);
//        if (moAlbumFolder.getType() == 1 && mFolderPosition == 0) {
//            mFolderPosition = mFolderPosition + 1;
//        } else {
//            if (!cameraIsConnect) {
//                mFolderPosition = 0;
//            }
//        }
//        boolean selectedStatus = false;
//        MoAlbumFolder folder = mAlbumFolders.get(mFolderPosition);
//        if (folder == null)
//            return;
//
//        notifyFolderAdapter(mAlbumFolders);
//
//        mTitle = folder.getName();
//        mType = folder.getType();
//        if (mPhotoListAdapter != null) {
//            selectedStatus = mPhotoListAdapter.isSelectedStatus();
//        }
//        if (mTitleText != null) {
//            if (!selectedStatus) {
//                mTitleText.setText(mTitle);
//            } else {
//                mTitleText.setText(getResources().getString(R.string.select_camera_list_item));
//            }
//        }
//        ArrayList<MoAlbumItem> items = folder.getMoAlbumItems();
//        if (items.size() > 0) {
//            mEditBtn.setEnabled(true);
//            albumNullLayoutControl(false);
//            if (items != null && items.size() > 0) {
//                mMediaWrraper.clear();
//                mMediaWrraper.addAll(items);
//                mPhotoListAdapter.notifyDataSetChanged();
//            }
//            if (mSwipeRefreshView != null) {
//                mSwipeRefreshView.setRefreshing(false);
//            }
//        } else {
//            mEditBtn.setEnabled(false);
//            albumNullLayoutControl(true);
//        }
//    }
//
//
//    @Override
//    public int initView() {
//        return R.layout.activity_mo_album;
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (mAblumPresenter != null) {
//            mAblumPresenter.destoryCallBack();
//            mAblumPresenter = null;
//        }
//        mMediaReader = null;
//        if (mHandler != null) {
//            mHandler.removeCallbacksAndMessages(null);
//            mHandler = null;
//        }
//        if (mAlbumFolders != null) {
//            mAlbumFolders.clear();
//            mAlbumFolders = null;
//        }
//        if (mMediaWrraper != null) {
//            mMediaWrraper.clear();
//            mMediaWrraper = null;
//        }
//        if (mPhotoListAdapter != null) {
//            mPhotoListAdapter.destory();
//            mPhotoListAdapter = null;
//        }
//        isSendStopPreview = true;
//        System.gc();
//    }
//
//    private void getActivityFlag() {
//        jump_flag = getIntent().getStringExtra("jump_flag");
//    }
//
//    private void initMediaScanClass() {
//        mMediaReader = new MediaReader(this);
//    }
//
//    private void initContentRecyclerView() {
//        mMediaRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
//            @Override
//            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//                outRect.left = DensityUtils.dp2px(AlbumListActivity.this, 2);
//                outRect.right = DensityUtils.dp2px(AlbumListActivity.this, 2);
//                outRect.bottom = DensityUtils.dp2px(AlbumListActivity.this, 2);
//                outRect.top = DensityUtils.dp2px(AlbumListActivity.this, 2);
//            }
//        });
//
//        ((SimpleItemAnimator) mMediaRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
//
//        mPhotoListAdapter = new CameraPhotoListAdapter(this, mMediaWrraper, this);
//
//        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            setGridLayoutManager(5);
//            setTitleStatus(true);
//        } else {
//            setTitleStatus(false);
//            setGridLayoutManager(3);
//        }
//        noStatusBar();
//
//        mMediaRecyclerView.setAdapter(mPhotoListAdapter);
//        mPhotoListAdapter.setOnItemClickListener(this);
//    }
//
//    private void dirRecyclerView() {
//        mSwipeRefreshView.setColorSchemeResources(R.color.appThemeColor);
//        mSwipeRefreshView.setOnRefreshListener(this);
//        mSwipeRefreshView.setOnLoadMoreListener(this);
//
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        mFolderRecyclerView.setLayoutManager(layoutManager);
//        mFoldersAdapter = new MoAlbumFoldersAdapter(this);
//        mFolderRecyclerView.setAdapter(mFoldersAdapter);
//        mFoldersAdapter.setOnItemClickListener(this);
//    }
//
//    @Override
//    public void initData() {
//        openGuideActivity();
//        getActivityFlag();
//        initMediaScanClass();
//        initContentRecyclerView();
//
//        dirRecyclerView();
//
//        setTopBtnState(View.VISIBLE);
//        initSdcardListener();
//        bottomIconSelected(true);
//    }
//
//    private void openGuideActivity() {
//        boolean hasGuideGoup = getSharedPreferences(SPUtils.FILE_NAME, Context.MODE_PRIVATE).getBoolean(NewerGuideActivity.KEY_OF_ALBUM, false);
//        if (!hasGuideGoup && AccessoryManager.getInstance().mIsRunning) {
//            NewerGuideActivity.open(this, NewerGuideActivity.NEWER_GUIDE_ALBUM);
//        }
//    }
//
//    private void bottomIconSelected(boolean isSelected) {
//        mDownLoadImg.setSelected(isSelected);
//        mLikeImg.setSelected(isSelected);
//        mDeleteImg.setSelected(isSelected);
//
//        mDownLoadImg.setEnabled(!isSelected);
//        mLikeImg.setEnabled(!isSelected);
//        mDeleteImg.setEnabled(!isSelected);
//    }
//
//    /**
//     * listener sdcard status
//     */
//    private void initSdcardListener() {
//        ConnectionManager.getInstance().setErrorI((data) -> {
//            if (data.event == MoErrorCallback.SD_EVENT) {
//                switch (data.status) {
//                    case MoErrorCallback.SD_OUT:
//                        cameraError(AlbumListActivity.this.getResources().getString(R.string.sdka_out));
//                        break;
//                    case MoErrorCallback.SD_IN:
//                        isSdCardOut = false;
//                        refreshFolders();
//                        break;
//                    case MoErrorCallback.SD_IN_FAIL:
//                        CameraToastUtil.show(getResources().getString(R.string.sdka_error), getBaseContext());
//                        break;
//                    case MoErrorCallback.SD_FULL:
//                        break;
//                    case MoErrorCallback.SD_LOW:
//                        break;
//                }
//            }
//        });
//    }
//
//    private void editButtonControl(boolean b, String title) {
//        setBottomEditState();
//        mTitleText.setText(title);
//        mEditBtn.setVisibility(b ? View.GONE : View.VISIBLE);
//        mBackBtn.setVisibility(b ? View.GONE : View.VISIBLE);
//        mEditText.setVisibility(b ? View.VISIBLE : View.GONE);
//        setSelectedPhotoListStatus(b);
//        bottomLayoutAnimation(!b);
//        if (b) {
//            mChangeLayout.setEnabled(false);
//            mDownIcon.setVisibility(View.GONE);
//        } else {
//            mChangeLayout.setEnabled(true);
//            mDownIcon.setVisibility(View.VISIBLE);
//        }
//    }
//
//    /**
//     * 设置照片列表页面,是不是选择状态
//     */
//    public void setSelectedPhotoListStatus(boolean isSelectedPhotoStatus) {
//        try {
//            this.isSelectedPhotoStatus = isSelectedPhotoStatus;
//            if (mPhotoListAdapter == null) {
//                mMediaRecyclerView.setAdapter(mPhotoListAdapter);
//            } else {
//                mPhotoListAdapter.setSelectedStatus(isSelectedPhotoStatus);
//                mPhotoListAdapter.notifyDataSetChanged();
//            }
//            if (!isSelectedPhotoStatus) {
//                //在相册页面的时候,保留原始的集合.在相册页面点击取消的时候.把之前的list给重新赋值在重新传递进去
//                ArrayList<MoAlbumItem> listUIitemBeans = mPhotoListAdapter.getMomediaWrraper().getListUIitemBeans();
//                for (MoAlbumItem image : listUIitemBeans) {
//                    image.isChecked = false;
//                }
//                mPhotoListAdapter.notifyDataSetChanged();
//            }
//        } catch (Exception e) {
//
//        }
//    }
//
//    /**
//     * 底部的，删除 喜欢，下载 布局的动画
//     *
//     * @param isUpOrdown true 向上移动
//     *                   false 向下移动
//     */
//    private void bottomLayoutAnimation(final boolean isUpOrdown) {
//        if (isUpOrdown) {
//            photo_list_bottom_layout.setVisibility(View.GONE);
//        } else {
//            photo_list_bottom_layout.setVisibility(View.VISIBLE);
//        }
//    }
//
//    @OnClick({R.id.activity_mo_album_back, R.id.activity_mo_album_change_layout,
//            R.id.activity_mo_album_edit_image, R.id.activity_mo_album_edit_text,
//            R.id.activity_camera_photo_list_download, R.id.activity_camera_photo_list_like,
//            R.id.activity_camera_photo_list_delete})
//    public void onViewClicked(View view) {
//        switch (view.getId()) {
//            case R.id.activity_mo_album_back:
//                if (isSelectedPhotoStatus) {
//                    if (mPhotoListAdapter != null && mPhotoListAdapter.getSelectedPhotoList().size() > 0)
//                        mPhotoListAdapter.clearSelectedPhotoList();
//                    editButtonControl(false, mTitle);
//                } else {
//                    back();
//                }
//                break;
//            case R.id.activity_mo_album_change_layout:
//                if (mFolderRecyclerView.getVisibility() == View.VISIBLE) {
//                    startExitAnim();
//                    mFolderRecyclerView.setVisibility(View.GONE);
//                    setTopBtnState(View.VISIBLE);
//                } else {
//                    startEnterAnim();
//                    mFolderRecyclerView.setVisibility(View.VISIBLE);
//                    setTopBtnState(View.GONE);
//                }
//                break;
//            case R.id.activity_mo_album_edit_image:
//                if (mPhotoListAdapter.getSelecetPhotoCount() == 0) {
//                    editButtonControl(true, this.getResources().getString(R.string.select_camera_list_item));
//                }
//                break;
//            case R.id.activity_mo_album_edit_text:
//                if (mPhotoListAdapter.getSelecetPhotoCount() > 0)
//                    mPhotoListAdapter.clearSelectedPhotoList();
//                editButtonControl(false, mTitle);
//                break;
//            case R.id.activity_camera_photo_list_download:
//                if (mPhotoListAdapter.getSelecetPhotoCount() > 0) {
//                    mAblumPresenter.startDownloadFile(mPhotoListAdapter.getSelectedPhotoList());
//                } else {
//                    CameraToastUtil.show(getResources().getString(R.string.plase_down_content), getBaseContext());
//                }
//                break;
//            case R.id.activity_camera_photo_list_like:
//                if (mPhotoListAdapter.getSelecetPhotoCount() > 0) {
//                    mAblumPresenter.like(mPhotoListAdapter.getSelectedPhotoList());
//                } else {
//                    CameraToastUtil.show(getResources().getString(R.string.select_camera_list_item), getBaseContext());
//                }
//                break;
//            case R.id.activity_camera_photo_list_delete:
//                if (mPhotoListAdapter.getSelecetPhotoCount() > 0) {
//                    delete();
//                } else {
//                    CameraToastUtil.show(getResources().getString(R.string.select_camera_list_item), getBaseContext());
//                }
//                break;
//        }
//    }
//
//    private void back() {
//        if (jump_flag != null && (TextUtils.equals(jump_flag, "fpv") || TextUtils.equals(jump_flag, "beauty"))) {
//            if (isFinish) {
//                Intent intent = new Intent(AlbumListActivity.this, MoFPVActivity.class);
//                startActivity(intent);
//                this.finish();
//            }
//        } else {
//            this.finish();
//        }
//    }
//
//    private void delete() {
//        if (mType == 1) {
//            if (mPhotoListAdapter.getSelectedPhotoList() != null && mPhotoListAdapter.getSelectedPhotoList().size() > 0) {
//                mAblumPresenter.deleteMedia(mPhotoListAdapter.getSelectedPhotoList());
//            }
//        } else {
//            mAblumPresenter.deleteLocalMedia(mPhotoListAdapter.getSelectedPhotoList());
//        }
//    }
//
//    @Override
//    public void onScanCallback(ArrayList<MoAlbumFolder> moAlbumFolders) {
//        if (mHandler == null) {
//            return;
//        }
//        Message message = Message.obtain();
//        message.obj = moAlbumFolders;
//        message.what = REFRESH_CLASSIFY_ADAPTER;
//        mHandler.sendMessage(message);
//    }
//
//    @Override
//    public void onSuccess(int count, final MoImage thumbnail) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                if (thumbnail != null && count > 0 && mAlbumFolders != null) {
//                    if (thumbnail.getmWidth() == 0
//                            || thumbnail.getmHeight() == 0
//                            || thumbnail.getmSize() == 0) {
//                        CameraToastUtil.show(AlbumListActivity.this.getResources().getString(R.string.thumbnail_error), getBaseContext());
//                    } else {
//                        refreshDirItem(count, thumbnail.getmUri());
//                    }
//                }
//            }
//        });
//    }
//
//    @Override
//    public void onFailed() {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                refreshDirItem(0, null);
//            }
//        });
//    }
//
//    private void refreshDirItem(int count, String url) {
//        MoAlbumFolder moAlbumFolder = mAlbumFolders.get(0);
//        moAlbumFolder.setName(getResources().getString(R.string.camera_gallery));
//        moAlbumFolder.setType(1);
//        moAlbumFolder.setIsCamera(true);
//        moAlbumFolder.setItemCount(count);
//        moAlbumFolder.setThumbnailUri(url);
//        mFoldersAdapter.notifyItemChanged(0);
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        isFinish = false;
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        isSendStopPreview = false;
//        isRefreshSuccess = false;
////        if (mPhotoListAdapter != null && mEditText.getVisibility() != View.VISIBLE)
////            mPhotoListAdapter.clearSelectedPhotoList();
////        if (mAblumPresenter != null)
////            mAblumPresenter.dimiss();
//    }
//
//    /**
//     * 需要停止fpv模式 再访问相机的接口
//     */
//    private void stopFpvMode() {
//        ConnectionManager.getInstance().appFpvMode(0, new MoRequestCallback() {
//            @Override
//            public void onSuccess() {
//                Log.i(TAG_TEST, "appFpvMode  onSuccess: ");
//                getCameraData();
//            }
//
//            @Override
//            public void onFailed() {
//                Log.i(TAG_TEST, "appFpvMode  onFailed: ");
//                isFinish = true;
//                getCameraData();
//            }
//        });
//    }
//
//    private void getCameraData() {
//        Log.i(TAG_TEST, "getCameraData: send message-----");
//        mPage = 0;
//        mAblumPresenter.requestCameraData(mPage);
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                isRefreshSuccess = true;
//                if (mSwipeRefreshView != null && mSwipeRefreshView.isRefreshing()) {
//                    mSwipeRefreshView.setRefreshing(false);
//                }
//            }
//        }, 1000 * 10);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (mAblumPresenter == null)
//            mAblumPresenter = new MoAlbumPresenter(this, this);
//        mIsFront = true;
//        //添加一个超时
//        mHandler.postDelayed(() -> {
//            isFinish = true;
//        }, 3000);
//
//        if (mEditText.getVisibility() == View.VISIBLE) {
//            return;
//        }
//        refreshFolders();
//
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        mIsFront = false;
//    }
//
//    //refresh cover view data
//    private void notifyFolderAdapter(ArrayList<MoAlbumFolder> moAlbumFolders) {
//        mFoldersAdapter.setData(moAlbumFolders);
//    }
//
//    @Override
//    public void downLoadFileSuccess() {
//        if (mEditBtn == null || mBackBtn == null
//                || mEditText == null || mChangeLayout == null || mDownIcon == null) {
//            return;
//        }
//        mEditBtn.setVisibility(View.VISIBLE);
//        mBackBtn.setVisibility(View.VISIBLE);
//        mEditText.setVisibility(View.GONE);
//        mChangeLayout.setEnabled(true);
//        mDownIcon.setVisibility(View.VISIBLE);
//        mTitleText.setText(mTitle);
//        bottomLayoutAnimation(true);
//        setSelectedPhotoListStatus(false);
//        mAblumPresenter.clear();//把数据全部清理
//        mPhotoListAdapter.clearSelectedPhotoList();
//
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                refreshFolders();
//            }
//        }, 1000);
//    }
//
//    @Override
//    public void deleteCameraFileSuccess() {
//        mPhotoListAdapter.clearSelectedPhotoList();
//        refreshFolders();
//    }
//
//    @Override
//    public void deleteLocalFileSuccess() {
//        mPhotoListAdapter.clearSelectedPhotoList();
//        refreshFolders();
//    }
//
//    @Override
//    public void refreshList(final ArrayList<MoAlbumItem> albumItems) {
//        Log.i(TAG_TEST, "getCameraData: call back success------");
//        isFinish = true;
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Log.i(TAG_TEST, "getCameraData: call back success---1---");
//                isLoadMoreData = false;
//                if (isDestroyed() || albumNullLayout == null
//                        || mMediaRecyclerView == null || mSwipeRefreshView == null) {
//                    return;
//                }
//                Log.i(TAG_TEST, "getCameraData: call back success---2---");
//                if (albumItems != null && albumItems.size() > 0) {
//                    if (albumNullLayout.getVisibility() == View.VISIBLE) {
//                        albumNullLayout.setVisibility(View.VISIBLE);
//                        mMediaRecyclerView.setVisibility(View.GONE);
//                    }
//                    if (mPage == 0) {
//                        Log.i(TAG_TEST, "getCameraData: call back success---3---");
//                        mMediaWrraper.clear();
//                    }
//                    isRefreshSuccess = true;
//                    albumNullLayoutControl(false);
//                    mMediaWrraper.addAll(albumItems);
//                    mPhotoListAdapter.notifyDataSetChanged();
//                    mPage++;
//                    Log.i(TAG_TEST, "getCameraData: call back success---4---");
//                } else {
//                    if (mPage == 0) {
//                        albumNullLayoutControl(true);
//                    } else {
//                        CameraToastUtil.show(AlbumListActivity.this.getResources().getString(R.string.already_bottom), getBaseContext());
//                    }
//                    Log.i(TAG_TEST, "getCameraData: call back success---5---");
//                }
//                mSwipeRefreshView.setRefreshing(false);
//                Log.i(TAG, "refreshList:  mpage = " + mPage);
//            }
//        });
//    }
//
//    @Override
//    public void refreshListFailed() {
//        Log.i(TAG_TEST, "getCameraData: call back failed------");
//        isLoadMoreData = false;
//        isFinish = true;
//        if (mSwipeRefreshView != null)
//            mSwipeRefreshView.setRefreshing(false);
//        albumNullLayoutControl(true);
//    }
//
//    private void requestCameraAlbum() {
//        if (mSwipeRefreshView != null) {
//            mSwipeRefreshView.setRefreshing(true);
//        }
//
//        if (!TextUtils.isEmpty(jump_flag) && TextUtils.equals(jump_flag, "fpv") && isSendStopPreview) {
//            mAblumPresenter.stopPreview(new MoAlbumPresenter.StopPreviewListener() {
//                @Override
//                public void onSuccess() {
//                    Log.i(TAG_TEST, "stopPreview  onSuccess: ");
//                    isSendStopPreview = false;
//                    stopFpvMode();
//                }
//
//                @Override
//                public void onFailed() {
//                    Log.i(TAG_TEST, "stopPreview  onFailed: ");
//                    isSendStopPreview = true;
//                    isFinish = true;
//                    stopFpvMode();
//                }
//            });
//        } else {
//            getCameraData();
//        }
//    }
//
//    @Override
//    public void getCameraMediaList() {
//
//    }
//
//    @Override
//    public void likeSuccess(ArrayList<MoAlbumItem> selectedPhotoList) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                ArrayList<MoAlbumItem> selectedPhotoList = mPhotoListAdapter.getSelectedPhotoList();
//                for (MoAlbumItem moAlbumItem : selectedPhotoList) {
//                    moAlbumItem.setCollect(1);
//                }
//
//                mPhotoListAdapter.clearSelectedPhotoList();
//                editButtonControl(false, mTitle);
//                mPhotoListAdapter.notifyDataSetChanged();
//            }
//        });
//    }
//
//    private void albumNullLayoutControl(boolean isVisible) {
//        if (isVisible) {
//            mSwipeRefreshView.setVisibility(View.GONE);
//            albumNullLayout.setVisibility(View.VISIBLE);
//            mMediaRecyclerView.setVisibility(View.GONE);
//        } else {
//            mSwipeRefreshView.setVisibility(View.VISIBLE);
//            albumNullLayout.setVisibility(View.GONE);
//            mMediaRecyclerView.setVisibility(View.VISIBLE);
//        }
//    }
//
//    @Override
//    public void onBackPressed() {
//        if (mIsFront) {
//            if (isSelectedPhotoStatus) {
//                if (mPhotoListAdapter != null && mPhotoListAdapter.getSelectedPhotoList().size() > 0)
//                    mPhotoListAdapter.clearSelectedPhotoList();
//                editButtonControl(false, mTitle);
//            } else {
//                back();
//            }
//        }
//    }
//
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            setGridLayoutManager(5);
//            setTitleStatus(true);
//        } else {
//            setTitleStatus(false);
//            setGridLayoutManager(3);
//        }
//        noStatusBar();
//    }
//
//    private void setBottomEditState() {
//        if (mLikeImg == null || mDownLoadImg == null) {
//            return;
//        }
//        if (mType == 1) {
//            likeRl.setVisibility(View.VISIBLE);
//            mLikeImg.setVisibility(View.VISIBLE);
//            mDownLoadImg.setVisibility(View.VISIBLE);
//            downRl.setVisibility(View.VISIBLE);
//        } else {
//            mLikeImg.setVisibility(View.GONE);
//            likeRl.setVisibility(View.GONE);
//            mDownLoadImg.setVisibility(View.GONE);
//            downRl.setVisibility(View.GONE);
//        }
//    }
//
//    private void setGridLayoutManager(int count) {
//        //TODO 后面的数字是设置一行需要显示多少个,目前显示的是3个
//        GridLayoutManager layoutManager = new GridLayoutManager(this, count);
//        SectionedSpanSizeLookup lookup = new SectionedSpanSizeLookup(mPhotoListAdapter, layoutManager);
//        layoutManager.setSpanSizeLookup(lookup);
//
//        mMediaRecyclerView.setLayoutManager(layoutManager);
//    }
//
//    private void startEnterAnim() {
//        Animation animation = AnimationUtils.loadAnimation(this, R.anim.album_folder_enter_top_anim);
//        mFolderRecyclerView.startAnimation(animation);
//        mFolderRecyclerView.setVisibility(View.VISIBLE);
//    }
//
//    private void startExitAnim() {
//        Animation animation = AnimationUtils.loadAnimation(this, R.anim.album_folder_exit_bottom_anim);
//        mFolderRecyclerView.startAnimation(animation);
//        mFolderRecyclerView.setVisibility(View.GONE);
//    }
//
//    private void setTopBtnState(int visibility) {
//        mBackBtn.setVisibility(visibility);
//        mEditLayout.setVisibility(visibility);
//    }
//
//    @Override
//    public void connectedUSB() {
//        cameraIsConnect = true;
////        startAct(GoUpActivity.class);
////        refreshFolders();
//    }
//
//    @Override
//    public void disconnectedUSB() {
//        mAblumPresenter.removeHandler();
//        DownLoadRequest.removeCurDownLoadError(this);
//        cameraError(getResources().getString(R.string.disconenct_usb));
//    }
//
//    private void cameraError(String string) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                cameraIsConnect = false;
//                if (string.equals(AlbumListActivity.this.getResources().getString(R.string.sdka_out))) {
//                    jump_flag = "sdcard_out";
//                    editButtonControl(false, mTitle);
//                    setSelectedPhotoListStatus(false);
//                    mPhotoListAdapter.clearSelectedPhotoList();
//                } else {
//                    // jump_flag = null;
//                }
//                refreshFolders();
//                mAblumPresenter.dimiss();
//                if (!isSdCardOut) {
//                    isSdCardOut = true;
//                    CameraToastUtil.show(string, getBaseContext());
//                }
//            }
//        });
//    }
//
//    private void refreshFolders() {
//        if (mMediaReadTask != null) {
//            mMediaReadTask.interrupt();
//            mMediaReadTask = null;
//        }
//        mMediaReadTask = new MediaReadTask(this, mMediaReader, this);
//        mMediaReadTask.execute();
//
//        editButtonControl(false, mTitle);
//        setSelectedPhotoListStatus(false);
//        mPhotoListAdapter.clearSelectedPhotoList();
//    }
//
//    @Override
//    public void onRefresh() {
//        //刷新是没有意义的,这个地方刷新什么事情都不需要做
//        if (!isSelectedPhotoStatus) {
//            mPhotoListAdapter.setSelectedStatus(false);
//        }
//        mSwipeRefreshView.setLoading(false);
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mSwipeRefreshView.setRefreshing(false);
//            }
//        }, 300);
//    }
//
//    @Override
//    public void onLoadMore() {
//        boolean refreshing = mSwipeRefreshView.isRefreshing();
//        if (!refreshing && mType == 1 && !isLoadMoreData) {
//            isLoadMoreData = true;
//            mAblumPresenter.requestCameraData(mPage);
//        }
//    }
//
//    @Override
//    public void selectedAlbumCount(int selectedPhotoCount) {
//        Log.i(TAG, "selectedAlbumCount: " + selectedPhotoCount);
//        if (selectedPhotoCount == 0) {
//            mTitleText.setText(getResources().getString(R.string.select_camera_list_item));
//            bottomIconSelected(true);
//        } else {
//            String mTitleString = getString(R.string.selecte_gallery_l, selectedPhotoCount);
//            mTitleText.setText(mTitleString);
//            bottomIconSelected(false);
//        }
//    }
//
//    @Override
//    public void clearAlbume() {
//        bottomIconSelected(true);
//    }
//
//    @Override
//    public void onFoldersItemClickListener(int position, MoAlbumFolder folder) {
//        mFolderPosition = position;
//        mTitle = folder.getName();
//        mType = folder.getType();
//        mTitleText.setText(mTitle);
//        for (int i = 0; i < mAlbumFolders.size(); i++) {
//            if (mAlbumFolders.get(i).getName().equals(mTitle)) {
//                mAlbumFolders.get(i).setChecked(true);
//            } else {
//                mAlbumFolders.get(i).setChecked(false);
//            }
//        }
//        notifyFolderAdapter(mAlbumFolders);
//        startExitAnim();
//        if (folder.getType() == 1) {
//            if (folder.getCount() > 0) {
//                requestCameraAlbum();
//                mEditBtn.setEnabled(true);
//            } else {
//                mEditBtn.setEnabled(false);
//                CameraToastUtil.show(getResources().getString(R.string.plase_takephoto), getBaseContext());
//                albumNullLayoutControl(true);
//                if (mMediaWrraper != null) {
//                    mMediaWrraper.clear();
//                    mMediaWrraper.addAll(folder.getMoAlbumItems());
//                    mPhotoListAdapter.notifyDataSetChanged();
//                }
//            }
//        } else {
//            albumNullLayoutControl(false);
//            ArrayList<MoAlbumItem> moAlbumItems = folder.getMoAlbumItems();
//            if (mMediaWrraper != null && moAlbumItems != null && moAlbumItems.size() > 0) {
//                mEditBtn.setEnabled(true);
//                mMediaWrraper.clear();
//                mMediaWrraper.addAll(folder.getMoAlbumItems());
//                mPhotoListAdapter.notifyDataSetChanged();
//            } else {
//                mEditBtn.setEnabled(false);
//                albumNullLayoutControl(true);
//            }
//        }
//        mMediaRecyclerView.setVisibility(View.VISIBLE);
//        setTopBtnState(View.VISIBLE);
//    }
//
//    @Override
//    public void onItemClick(ArrayList<MoAlbumItem> list, int position) {
//        if (mType == 1) {
//            if (!isRefreshSuccess) {
//                return;
//            }
//            if (AccessoryManager.getInstance().mIsRunning) {
//                openActivity(list, position);
//            } else {
//                CameraToastUtil.show(getResources().getString(R.string.camera_disconnect), getBaseContext());
//            }
//        } else {
//            openActivity(list, position);
//        }
//    }
//
//    private void openActivity(ArrayList<MoAlbumItem> list, int position) {
//        AlbumListBean albumListBean = new AlbumListBean();
//        albumListBean.albumItems = list;
//        String json = gson.toJson(albumListBean);
//        SharedPreferencesUtil.instance().saveString("album_list", json);
//        Intent intent = new Intent(AlbumListActivity.this, MyAlbumPreview.class);
//        intent.putExtra("index", position);
//        intent.putExtra("iscamera", mType == 1);
//        startActivity(intent);
//    }
//}
