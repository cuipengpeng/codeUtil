package com.test.xcamera.moalbum.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.test.xcamera.R;
import com.test.xcamera.accrssory.AccessoryManager;
import com.test.xcamera.bean.MoAlbumItem;
import com.test.xcamera.moalbum.my_album.MoAlbumPresenter;
import com.test.xcamera.utils.CameraToastUtil;

import java.util.ArrayList;

import butterknife.OnClick;

/**
 * Created by zhouxuecheng on
 * Create Time 2020/7/24
 * e-mail zhouxuecheng1991@163.com
 */

public class MyAlbumList extends BaseAlbumListActivity {

    public static void openThis(Context context, String acFlag) {
        Intent intent = new Intent(context, MyAlbumList.class);
        intent.putExtra(AC_FLAG, acFlag);
        context.startActivity(intent);
    }

    @Override
    public void initData() {
        super.initData();
        mAblumPresenter = new MoAlbumPresenter(this, this);
    }

    @Override
    public void downLoadFileSuccess() {
        if (mEditBtn == null || mBackBtn == null || mEditText == null) {
            return;
        }
        editButtonControl(false);
        mAblumPresenter.clear();//把数据全部清理
        albumListAdapter.clearSelectedPhotoList();
        if (myAlbumPresenter != null) {
            myAlbumPresenter.getMobileAlbumData();
        }
    }

    @Override
    public void deleteCameraFileSuccess() {
        albumListAdapter.clearSelectedPhotoList();
        editButtonControl(false);
        getAlbumData();
    }

    @Override
    public void deleteLocalFileSuccess() {
        albumListAdapter.clearSelectedPhotoList();
        editButtonControl(false);
        getAlbumData();
    }

    @Override
    public void refreshList(final ArrayList<MoAlbumItem> albumItems) {
    }

    @Override
    public void likeSuccess(ArrayList<MoAlbumItem> selectedPhotoList) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ArrayList<MoAlbumItem> selectedPhotoList = albumListAdapter.getSelectedPhotoList();
                for (MoAlbumItem moAlbumItem : selectedPhotoList) {
                    moAlbumItem.setCollect(1);
                }

                albumListAdapter.clearSelectedPhotoList();
                editButtonControl(false);
                albumListAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void selectedAlbumCount(int selectedPhotoCount) {
        if (selectedPhotoCount == 0) {
            bottomIconSelected(true);
            myAlbumSelect.setText(this.getResources().getString(R.string.select_camera_list_item));
        } else {
            String mTitleString = getString(R.string.selecte_gallery_l, selectedPhotoCount);
            myAlbumSelect.setText(mTitleString);
            bottomIconSelected(false);
        }
    }

    @Override
    public void clearAlbume() {
        bottomIconSelected(true);
    }

    @Override
    public void onItemClick(ArrayList<MoAlbumItem> list, int position) {
        isSkipPreview = true;
        if (mType == 1) {
            if (!mSwipeRefreshView.isRefreshing()) {
                openActivity(list, position);
            } else {
                CameraToastUtil.show(getResources().getString(R.string.camera_album_loading), getBaseContext());
            }
        } else {
            openActivity(list, position);
        }
    }

    @Override
    public void onRefresh() {
        if (mType == 1 && AccessoryManager.getInstance().mIsRunning) {
            myPage = 0;
            myAlbumPresenter.getCameraAlbumList(myPage);
        } else if (mType == 0) {
            myAlbumPresenter.getMobileAlbumData();
        }
    }

    @Override
    public void onLoadMore() {
        boolean refreshing = mSwipeRefreshView.isRefreshing();
        if (!refreshing && mType == 1 && !isLoadMoreData) {
            isLoadMoreData = true;
            myAlbumPresenter.getCameraAlbumList(myPage);
        }
    }

    @Override
    public void refreshListFailed() {
        isLoadMoreData = false;
        isFinish = true;
        if (mSwipeRefreshView != null)
            mSwipeRefreshView.setRefreshing(false);
        albumNullLayoutControl(true);
    }

    @Override
    public void getCameraMediaList() {

    }

    @OnClick({R.id.activity_mo_album_back,
            R.id.activity_mo_album_edit_image, R.id.activity_mo_album_edit_text,
            R.id.activity_camera_photo_list_download, R.id.activity_camera_photo_list_like,
            R.id.activity_camera_photo_list_delete, R.id.cameraAlbum, R.id.appAlbum})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.activity_mo_album_back:
                if (isSelectedPhotoStatus) {
                    if (albumListAdapter != null && albumListAdapter.getSelectedPhotoList().size() > 0)
                        albumListAdapter.clearSelectedPhotoList();
                    editButtonControl(false);
                } else {
                    goBack();
                }
                break;
            case R.id.activity_mo_album_edit_image:
                if (albumListAdapter.getSelecetPhotoCount() == 0) {
                    editButtonControl(true);
                }
                break;
            case R.id.activity_mo_album_edit_text:
                if (albumListAdapter.getSelecetPhotoCount() > 0)
                    albumListAdapter.clearSelectedPhotoList();
                editButtonControl(false);
                break;
            case R.id.activity_camera_photo_list_download:
                if (albumListAdapter.getSelecetPhotoCount() > 0) {
                    mAblumPresenter.startDownloadFile(albumListAdapter.getSelectedPhotoList());
                } else {
                    CameraToastUtil.show(getResources().getString(R.string.plase_down_content), getBaseContext());
                }
                break;
            case R.id.activity_camera_photo_list_like:
                if (albumListAdapter.getSelecetPhotoCount() > 0) {
                    mAblumPresenter.like(albumListAdapter.getSelectedPhotoList());
                } else {
                    CameraToastUtil.show(getResources().getString(R.string.select_camera_list_item), getBaseContext());
                }
                break;
            case R.id.activity_camera_photo_list_delete:
                if (albumListAdapter.getSelecetPhotoCount() > 0) {
                    deleteAlbum();
                } else {
                    CameraToastUtil.show(getResources().getString(R.string.select_camera_list_item), getBaseContext());
                }
                break;
            case R.id.cameraAlbum:
                selectedCameraAlbum();
                break;
            case R.id.appAlbum:
                mType = 0;
                mMediaWrraper.clear();
                notifyData(fileMap.get(0), 0);
                break;
        }
    }

    private void selectedCameraAlbum() {
        mType = 1;
        mMediaWrraper.clear();
        if (AccessoryManager.getInstance().mIsRunning) {
            ArrayList<MoAlbumItem> moAlbumItems = fileMap.get(1);
            if (moAlbumItems == null) {
                if (mSwipeRefreshView != null) {
                    mSwipeRefreshView.setRefreshing(true);
                }
                getAlbumData();
            } else {
                notifyData(moAlbumItems, 1);
            }
        } else {
            //直接取出来的数据是null显示空页面
            notifyData(fileMap.get(1), 1);
        }
    }

    private void deleteAlbum() {
        if (mType == 1) {
            if (albumListAdapter.getSelectedPhotoList() != null && albumListAdapter.getSelectedPhotoList().size() > 0) {
                mAblumPresenter.deleteMedia(albumListAdapter.getSelectedPhotoList());
            }
        } else {
            mAblumPresenter.deleteLocalMedia(albumListAdapter.getSelectedPhotoList());
        }
    }
}
