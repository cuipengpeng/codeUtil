package com.test.xcamera.moalbum.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.framwork.base.view.MOBaseActivity;
import com.test.xcamera.R;
import com.test.xcamera.accrssory.AccessoryManager;
import com.test.xcamera.accrssory.UsbDispose;
import com.test.xcamera.activity.MoFPVActivity;
import com.test.xcamera.album.MomediaWrraper;
import com.test.xcamera.album.SwipeRefreshView;
import com.test.xcamera.album.adapter.CameraPhotoListAdapter;
import com.test.xcamera.album.sectionrec.SectionedSpanSizeLookup;
import com.test.xcamera.bean.AlbumListBean;
import com.test.xcamera.bean.MoAlbumItem;
import com.test.xcamera.cameraclip.NewerGuideActivity;
import com.test.xcamera.managers.ConnectionManager;
import com.test.xcamera.moalbum.bean.MoAlbumFolder;
import com.test.xcamera.moalbum.interfaces.MoAlbumCallback;
import com.test.xcamera.moalbum.my_album.MyAlbumInterface;
import com.test.xcamera.moalbum.my_album.MyAlbumPresenter;
import com.test.xcamera.moalbum.my_album.MoAlbumPresenter;
import com.test.xcamera.mointerface.MoErrorCallback;
import com.test.xcamera.utils.CameraToastUtil;
import com.test.xcamera.utils.DensityUtils;
import com.test.xcamera.utils.DownLoadRequest;
import com.test.xcamera.utils.SPUtils;
import com.test.xcamera.utils.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;

/**
 * Created by zll on 2019/11/21.
 */

public abstract class BaseAlbumListActivity extends MOBaseActivity implements MoAlbumCallback,
        AccessoryManager.ConnectStateListener, SwipeRefreshLayout.OnRefreshListener, SwipeRefreshView.OnLoadMoreListener,
        CameraPhotoListAdapter.MyAlbumAdapterCallback, CameraPhotoListAdapter.OnItemClickListener, MyAlbumInterface.PresenterToView {
    public final static String AC_FLAG = "acFlag";

    @BindView(R.id.activity_mo_album_back)
    protected ImageView mBackBtn;
    @BindView(R.id.activity_mo_album_edit_layout)
    protected RelativeLayout mEditLayout;
    @BindView(R.id.activity_mo_album_edit_image)
    protected ImageView mEditBtn;
    @BindView(R.id.activity_mo_album_edit_text)
    protected TextView mEditText;
    @BindView(R.id.activity_mo_album_media_rc)
    protected RecyclerView mMediaRecyclerView;
    @BindView(R.id.activity_mo_album_media_refresh_view)
    protected SwipeRefreshView mSwipeRefreshView;
    @BindView(R.id.activity_mo_album_media_view_group)
    protected RelativeLayout mRecyclerViewGroup;
    @BindView(R.id.photo_list_bottom_layout)
    protected LinearLayout photo_list_bottom_layout;
    @BindView(R.id.activity_camera_photo_list_download)
    protected ImageView mDownLoadImg;
    @BindView(R.id.activity_camera_photo_list_like)
    protected ImageView mLikeImg;
    @BindView(R.id.activity_camera_photo_list_delete)
    protected ImageView mDeleteImg;
    @BindView(R.id.albumNullLayout)
    protected RelativeLayout albumNullLayout;
    @BindView(R.id.downRl)
    protected RelativeLayout downRl;
    @BindView(R.id.likeRl)
    protected RelativeLayout likeRl;
    @BindView(R.id.deleteRl)
    protected RelativeLayout deleteRl;
    @BindView(R.id.cameraAlbum)
    protected TextView cameraAlbum;
    @BindView(R.id.appAlbum)
    protected TextView appAlbum;
    @BindView(R.id.myAlbumSelect)
    protected TextView myAlbumSelect;
    @BindView(R.id.albumTitle)
    protected LinearLayout albumTitleLayout;

    protected CameraPhotoListAdapter albumListAdapter;
    protected MomediaWrraper mMediaWrraper = new MomediaWrraper();
    protected boolean isSelectedPhotoStatus = false;
    protected int mType = -1;
    protected String acFlag;
    protected boolean isSdCardOut = false;
    protected boolean isLoadMoreData = false;
    protected boolean isFinish = false;
    protected MyAlbumPresenter myAlbumPresenter;
    protected MoAlbumPresenter mAblumPresenter;

    //把两个目录的文件都放到这个集合中
    protected HashMap<Integer, ArrayList<MoAlbumItem>> fileMap = new HashMap<>();
    protected ArrayList<MoAlbumItem> cameraAlbumData = new ArrayList<>();
    //用来区分是跳转到大图预览还是按home键了
    protected boolean isSkipPreview = false;
    protected int myPage = 0;
    private UsbDispose mUsbDispose = new UsbDispose();


    @Override
    public int initView() {
        return R.layout.activity_my_album;
    }

    @Override
    public void initData() {
        fileMap.put(0, new ArrayList<>());
        fileMap.put(1, null);
        openGuideActivity();

        acFlag = getIntent().getStringExtra(AC_FLAG);
        initContentRecyclerView();

        setTopBtnState(View.VISIBLE);
        initSdcardListener();
        bottomIconSelected(true);

        if (myAlbumPresenter == null)
            myAlbumPresenter = new MyAlbumPresenter(this, this);

        initAlbumListData();
    }


    private void initContentRecyclerView() {
        mMediaRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.left = DensityUtils.dp2px(BaseAlbumListActivity.this, 2);
                outRect.right = DensityUtils.dp2px(BaseAlbumListActivity.this, 2);
                outRect.bottom = DensityUtils.dp2px(BaseAlbumListActivity.this, 2);
                outRect.top = DensityUtils.dp2px(BaseAlbumListActivity.this, 2);
            }
        });

        ((SimpleItemAnimator) mMediaRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        albumListAdapter = new CameraPhotoListAdapter(this, mMediaWrraper, this);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setGridLayoutManager(5);
            setTitleStatus(true);
        } else {
            setTitleStatus(false);
            setGridLayoutManager(3);
        }
        noStatusBar();

        mMediaRecyclerView.setAdapter(albumListAdapter);
        albumListAdapter.setOnItemClickListener(this);

        mSwipeRefreshView.setColorSchemeResources(R.color.appThemeColor);
        mSwipeRefreshView.setOnRefreshListener(this);
        mSwipeRefreshView.setOnLoadMoreListener(this);
    }


    private void initAlbumListData() {
        myPage = 0;
        if (AccessoryManager.getInstance().mIsRunning) {
            if (mSwipeRefreshView != null) {
                mSwipeRefreshView.setRefreshing(true);
            }
            if (!TextUtils.isEmpty(acFlag) && acFlag.equals("fpv")) {
                myAlbumPresenter.stopPreview();
            } else {
                myAlbumPresenter.getCameraAlbumList(myPage);
            }
        }

        myAlbumPresenter.getMobileAlbumData();
    }

    private void openGuideActivity() {
        boolean hasGuideGoup = getSharedPreferences(SPUtils.FILE_NAME, Context.MODE_PRIVATE).getBoolean(NewerGuideActivity.KEY_OF_ALBUM, false);
        if (!hasGuideGoup && AccessoryManager.getInstance().mIsRunning) {
            NewerGuideActivity.open(this, NewerGuideActivity.NEWER_GUIDE_ALBUM);
        }
    }

    protected void bottomIconSelected(boolean isSelected) {
        mDownLoadImg.setSelected(isSelected);
        mLikeImg.setSelected(isSelected);
        mDeleteImg.setSelected(isSelected);

        mDownLoadImg.setEnabled(!isSelected);
        mLikeImg.setEnabled(!isSelected);
        mDeleteImg.setEnabled(!isSelected);
    }

    /**
     * listener sdcard status
     */
    private void initSdcardListener() {
        ConnectionManager.getInstance().setErrorI((data) -> {
            if (data.event == MoErrorCallback.SD_EVENT) {
                switch (data.status) {
                    case MoErrorCallback.SD_OUT:
                        getAlbumData();
                        cameraError(BaseAlbumListActivity.this.getResources().getString(R.string.sdka_out));
                        break;
                    case MoErrorCallback.SD_IN:
                        getAlbumData();
                        break;
                    case MoErrorCallback.SD_IN_FAIL:
                        CameraToastUtil.show(getResources().getString(R.string.sdka_error), getBaseContext());
                        break;
                    case MoErrorCallback.SD_FULL:
                        break;
                    case MoErrorCallback.SD_LOW:
                        break;
                }
            }
        });
    }

    protected void editButtonControl(boolean selectStatus) {
        setBottomEditState();
        mEditBtn.setVisibility(selectStatus ? View.GONE : View.VISIBLE);
        mBackBtn.setVisibility(selectStatus ? View.GONE : View.VISIBLE);
        mEditText.setVisibility(selectStatus ? View.VISIBLE : View.GONE);
        setSelectedPhotoListStatus(selectStatus);
        bottomLayoutAnimation(!selectStatus);
        albumTitleLayout.setVisibility(selectStatus ? View.GONE : View.VISIBLE);
        myAlbumSelect.setVisibility(selectStatus ? View.VISIBLE : View.GONE);
        if (!selectStatus) {
            myAlbumSelect.setText(getResources().getString(R.string.select_camera_list_item));
        }
    }

    /**
     * 设置照片列表页面,是不是选择状态
     */
    public void setSelectedPhotoListStatus(boolean isSelectedPhotoStatus) {
        try {
            this.isSelectedPhotoStatus = isSelectedPhotoStatus;
            if (albumListAdapter == null) {
                mMediaRecyclerView.setAdapter(albumListAdapter);
            } else {
                albumListAdapter.setSelectedStatus(isSelectedPhotoStatus);
                albumListAdapter.notifyDataSetChanged();
            }
            if (!isSelectedPhotoStatus) {
                //在相册页面的时候,保留原始的集合.在相册页面点击取消的时候.把之前的list给重新赋值在重新传递进去
                ArrayList<MoAlbumItem> listUIitemBeans = albumListAdapter.getMomediaWrraper().getListUIitemBeans();
                for (MoAlbumItem image : listUIitemBeans) {
                    image.isChecked = false;
                }
                albumListAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {

        }
    }

    /**
     * 底部的，删除 喜欢，下载 布局的动画
     *
     * @param isUpOrdown true 向上移动
     *                   false 向下移动
     */
    private void bottomLayoutAnimation(final boolean isUpOrdown) {
        if (isUpOrdown) {
            photo_list_bottom_layout.setVisibility(View.GONE);
        } else {
            photo_list_bottom_layout.setVisibility(View.VISIBLE);
        }
    }

    protected void goBack() {
        if (acFlag != null && (TextUtils.equals(acFlag, "fpv"))) {
            if (isFinish) {
                Intent intent = new Intent(BaseAlbumListActivity.this, MoFPVActivity.class);
                startActivity(intent);
                this.finish();
            }
        } else {
            this.finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (isSelectedPhotoStatus) {
            if (albumListAdapter != null && albumListAdapter.getSelectedPhotoList().size() > 0)
                albumListAdapter.clearSelectedPhotoList();
            editButtonControl(false);
        } else {
            goBack();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setGridLayoutManager(5);
            setTitleStatus(true);
        } else {
            setTitleStatus(false);
            setGridLayoutManager(3);
        }
        noStatusBar();
    }

    private void setBottomEditState() {
        if (mLikeImg == null || mDownLoadImg == null) {
            return;
        }
        if (mType == 1) {
            likeRl.setVisibility(View.VISIBLE);
            mLikeImg.setVisibility(View.VISIBLE);
            mDownLoadImg.setVisibility(View.VISIBLE);
            downRl.setVisibility(View.VISIBLE);
        } else {
            mLikeImg.setVisibility(View.GONE);
            likeRl.setVisibility(View.GONE);
            mDownLoadImg.setVisibility(View.GONE);
            downRl.setVisibility(View.GONE);
        }
    }

    private void setGridLayoutManager(int count) {
        //TODO 后面的数字是设置一行需要显示多少个,目前显示的是3个
        GridLayoutManager layoutManager = new GridLayoutManager(this, count);
        SectionedSpanSizeLookup lookup = new SectionedSpanSizeLookup(albumListAdapter, layoutManager);
        layoutManager.setSpanSizeLookup(lookup);

        mMediaRecyclerView.setLayoutManager(layoutManager);
    }

    private void setTopBtnState(int visibility) {
        mBackBtn.setVisibility(visibility);
        mEditLayout.setVisibility(visibility);
    }


    @Override
    public void connectedUSB() {
        super.connectedUSB();
        Log.i("TEST_LOGI", "connectedUSB: Usb 连接成功");
        mUsbDispose.setSyncStatus(null);
        mUsbDispose.dispose();
    }

    @Override
    public void disconnectedUSB() {
        mAblumPresenter.removeHandler();
        DownLoadRequest.removeCurDownLoadError(this);
        cameraError(getResources().getString(R.string.disconenct_usb));
        fileMap.put(1, null);
        mType = 0;
        getAlbumData();
    }

    private void cameraError(String string) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (string.equals(BaseAlbumListActivity.this.getResources().getString(R.string.sdka_out))) {
                    acFlag = "sdcard_out";
                    editButtonControl(false);
                    setSelectedPhotoListStatus(false);
                    albumListAdapter.clearSelectedPhotoList();
                }
                mAblumPresenter.dimiss();
                if (!isSdCardOut) {
                    isSdCardOut = true;
                    CameraToastUtil.show(string, getBaseContext());
                }
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (isSkipPreview) {
            getAlbumData();
        }
        isSkipPreview = false;
    }


    @Override
    public void refreshMobileAlbumData(MoAlbumFolder requestAlbumFolders) {
        fileMap.put(requestAlbumFolders.getType(), requestAlbumFolders.getMoAlbumItems());
        if (!AccessoryManager.getInstance().mIsRunning) {
            if (mSwipeRefreshView != null) {
                mSwipeRefreshView.setRefreshing(false);
            }
            mMediaWrraper.clear();
            notifyData(requestAlbumFolders.getMoAlbumItems(), requestAlbumFolders.getType());
        } else if (mType == 0) {
            if (mSwipeRefreshView != null) {
                mSwipeRefreshView.setRefreshing(false);
            }
            mMediaWrraper.clear();
            notifyData(requestAlbumFolders.getMoAlbumItems(), requestAlbumFolders.getType());
        }
    }

    @Override
    public void refreshCameraAlbumData(MoAlbumFolder requestAlbumFolders, int page) {
        isFinish = true;
        isLoadMoreData = false;
        if (page == 0) {
            if (cameraAlbumData != null && fileMap != null) {
                cameraAlbumData.clear();
                fileMap.put(1, null);
                if (mMediaWrraper != null)
                    mMediaWrraper.clear();
                cameraAlbumData.addAll(requestAlbumFolders.getMoAlbumItems());
                fileMap.put(requestAlbumFolders.getType(), cameraAlbumData);
                notifyData(cameraAlbumData, requestAlbumFolders.getType());
            }
        } else {
            if (requestAlbumFolders.getMoAlbumItems().size() > 0) {
                if (cameraAlbumData != null && fileMap != null) {
                    cameraAlbumData.addAll(requestAlbumFolders.getMoAlbumItems());
                    fileMap.put(requestAlbumFolders.getType(), cameraAlbumData);
                    notifyData(cameraAlbumData, requestAlbumFolders.getType());
                }
            } else {
                //数据到底了toast提示
                CameraToastUtil.show(getResources().getString(R.string.already_bottom), this);
            }
        }

        if (mSwipeRefreshView != null && mSwipeRefreshView.isRefreshing()) {
            mSwipeRefreshView.setRefreshing(false);
        }

        myPage++;
    }


    @Override
    public void stopFpvModeFailed() {
        isFinish = true;
        if (AccessoryManager.getInstance().mIsRunning) {
            myAlbumPresenter.getCameraAlbumList(myPage);
        }
    }

    @Override
    public void stopFpvModeSuccess() {
        isFinish = true;
        if (AccessoryManager.getInstance().mIsRunning) {
            myAlbumPresenter.getCameraAlbumList(myPage);
        }
    }

    @Override
    public void stopFpvPreviewFailed() {
        myAlbumPresenter.stopFpvModel();
    }

    @Override
    public void stopFpvPreviewSuccess() {
        myAlbumPresenter.stopFpvModel();
    }

    @Override
    public void interfaceError() {
        isLoadMoreData = false;
        isFinish = true;
        if (mSwipeRefreshView != null)
            mSwipeRefreshView.setRefreshing(false);
        albumNullLayoutControl(true);
    }

    protected void getAlbumData() {
        myPage = 0;
        if (mType == 1 && AccessoryManager.getInstance().mIsRunning) {
            myAlbumPresenter.getCameraAlbumList(myPage);
        } else if (mType == 0) {
            myAlbumPresenter.getMobileAlbumData();
        }
    }

    protected void notifyData(ArrayList<MoAlbumItem> cameraAlbumData, int type) {
        if(mMediaWrraper==null){
            return;
        }
        this.mType = type;
        if (cameraAlbumData == null && mType == 1) {
            albumNullLayoutControl(true);
        } else {
            if (cameraAlbumData.size() > 0) {
                albumNullLayoutControl(false);
                mMediaWrraper.addAll(cameraAlbumData);
                albumListAdapter.notifyDataSetChanged();
            } else {
                albumNullLayoutControl(true);
            }
        }
        titleControl(type);
    }

    protected void albumNullLayoutControl(boolean isVisible) {
        if (isVisible) {
            if (mEditBtn != null) {
                mEditBtn.setEnabled(false);
                mEditBtn.setImageResource(R.drawable.album_manager);
            }
            if (mSwipeRefreshView != null)
                mSwipeRefreshView.setVisibility(View.GONE);
            if (albumNullLayout != null)
                albumNullLayout.setVisibility(View.VISIBLE);
            if (mMediaRecyclerView != null)
                mMediaRecyclerView.setVisibility(View.GONE);
        } else {
            if (mEditBtn != null) {
                mEditBtn.setEnabled(true);
                mEditBtn.setImageResource(R.drawable.icon_mo_album_edit);
            }
            if (mSwipeRefreshView != null)
                mSwipeRefreshView.setVisibility(View.VISIBLE);
            if (albumNullLayout != null)
                albumNullLayout.setVisibility(View.GONE);
            if (mMediaRecyclerView != null)
                mMediaRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void titleControl(int type) {
        if (type == 0) {
            appAlbum.setTextColor(Color.parseColor("#FFFFFF"));
            cameraAlbum.setTextColor(Color.parseColor("#666666"));
        } else {
            appAlbum.setTextColor(Color.parseColor("#666666"));
            cameraAlbum.setTextColor(Color.parseColor("#FFFFFF"));
        }
    }

    protected void openActivity(ArrayList<MoAlbumItem> list, int position) {
        AlbumListBean albumListBean = new AlbumListBean();
        albumListBean.albumItems = list;
        String json = gson.toJson(albumListBean);
        SharedPreferencesUtil.instance().saveString("album_list", json);
        Intent intent = new Intent(BaseAlbumListActivity.this, MyAlbumPreview.class);
        intent.putExtra("index", position);
        intent.putExtra("iscamera", mType == 1);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAblumPresenter != null) {
            mAblumPresenter.destoryCallBack();
            mAblumPresenter = null;
        }

        if (mMediaWrraper != null) {
            mMediaWrraper.clear();
            mMediaWrraper = null;
        }

        if (albumListAdapter != null) {
            albumListAdapter.destory();
            albumListAdapter = null;
        }

        System.gc();
    }
}
