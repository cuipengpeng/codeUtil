package com.test.xcamera.album.gallery.camera;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.framwork.base.view.MOBaseActivity;
import com.test.xcamera.R;
import com.test.xcamera.album.MomediaWrraper;
import com.test.xcamera.album.SwipeRefreshView;
import com.test.xcamera.album.adapter.CameraPhotoListAdapter;
import com.test.xcamera.moalbum.activity.MyAlbumPreview;
import com.test.xcamera.album.sectionrec.SectionedSpanSizeLookup;
import com.test.xcamera.bean.MoAlbumItem;
import com.test.xcamera.utils.CameraToastUtil;
import com.test.xcamera.utils.DensityUtils;
import com.test.xcamera.view.DeleteDialog;
import com.editvideo.ToastUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by zll on 2019/8/21.
 * <p>
 * 相机照片列表页面
 */

public class CameraPhotoListActivity extends MOBaseActivity implements View.OnClickListener, CameraPhotoListInterface {

    @BindView(R.id.activity_camera_photo_list_refresh_view)
    SwipeRefreshView mSwipeRefreshView;
    @BindView(R.id.activity_camera_photo_list_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.activity_camera_photo_list_middle_title)
    TextView mTitle;
    @BindView(R.id.activity_camera_photo_list_edit_text)
    TextView mEditText;
    @BindView(R.id.activity_camera_photo_list_back_btn)
    ImageView mBackBtn;
    @BindView(R.id.activity_camera_photo_list_edit_btn)
    ImageView mEditBtn;
    @BindView(R.id.activity_camera_photo_list_download)
    ImageView mDownLoadImg;
    @BindView(R.id.activity_camera_photo_list_like)
    ImageView mLikeImg;
    @BindView(R.id.activity_camera_photo_list_delete)
    ImageView mDeleteImg;
    @BindView(R.id.photo_list_bottom_layout)
    LinearLayout photo_list_bottom_layout;
    @BindView(R.id.mRecyclerViewGroup)
    RelativeLayout mRecyclerViewGroup;

    private CameraPhotoListAdapter mPhotolisAdapter;
    private MomediaWrraper listList = new MomediaWrraper();
    private boolean isSelectedPhotoStatus = false;
    private int mPage = 0;
    private CameraPhotoListPresenter cameraPhotoListPresenter;
    private int measuredHeight;
    private int fromYDelta = 0;
    private int toYDelta = 0;


    @OnClick({R.id.activity_camera_photo_list_back_btn, R.id.activity_camera_photo_list_edit_text,
            R.id.activity_camera_photo_list_edit_btn, R.id.activity_camera_photo_list_download,
            R.id.activity_camera_photo_list_like, R.id.activity_camera_photo_list_delete,
            R.id.activity_camera_photo_list_middle_title})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.activity_camera_photo_list_back_btn:
                if (isSelectedPhotoStatus) {
                    editButtonControl(false, this.getResources().getString(R.string.camera_grallery));
                } else {
                    this.finish();
                }
                break;
            case R.id.activity_camera_photo_list_edit_text:
                editButtonControl(false, this.getResources().getString(R.string.camera_grallery));
                break;
            case R.id.activity_camera_photo_list_edit_btn:
                if (mPhotolisAdapter.getSelecetPhotoCount() == 0) {
                    editButtonControl(true, this.getResources().getString(R.string.select_camera_list_item));
                }
                break;
            case R.id.activity_camera_photo_list_download:
                if (mPhotolisAdapter.getSelecetPhotoCount() > 0) {
                    cameraPhotoListPresenter.startDownloadFile(mPhotolisAdapter.getSelectedPhotoList());
                } else {
                    ToastUtil.showToast(CameraPhotoListActivity.this, this.getResources().getString(R.string.plase_down_content));
                }
                break;
            case R.id.activity_camera_photo_list_like:
                mLikeImg.setClickable(false);
                cameraPhotoListPresenter.like(mPhotolisAdapter.getSelectedPhotoList());
                break;
            case R.id.activity_camera_photo_list_delete:
                delete();
                break;
            case R.id.activity_camera_photo_list_middle_title:

                break;
        }
    }

    private void editButtonControl(boolean b, String title) {
        mTitle.setText(title);
        mEditBtn.setVisibility(b ? View.GONE : View.VISIBLE);
        mEditText.setVisibility(b ? View.VISIBLE : View.GONE);
        setSelectedPhotoListStatus(b);
        bottomLayoutAnimation(!b);
    }

    private void delete() {
        if (mPhotolisAdapter.getSelectedPhotoList() != null && mPhotolisAdapter.getSelectedPhotoList().size() > 0) {
            final ArrayList<String> uris = new ArrayList<>();
            for (int i = 0; i < mPhotolisAdapter.getSelectedPhotoList().size(); i++) {
                MoAlbumItem albumItem = mPhotolisAdapter.getSelectedPhotoList().get(i);
                if (albumItem.isVideo()) {
                    uris.add(albumItem.getmVideo().getmUri());
                } else {
                    uris.add(albumItem.getmImage().getmUri());
                }
            }

            deleteDialog.showDialog(new DeleteDialog.SureOnClick() {
                @Override
                public void sure_button() {
                    cameraPhotoListPresenter.deleteMedia(uris);
                }
            }, this.getResources().getString(R.string.delete_dialog_content));
        }
    }

    @Override
    public int initView() {
        return R.layout.activity_camera_photo_list;
    }

    @Override
    public void initClick() {

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setGridLayoutManager(5);
        } else {
            setGridLayoutManager(3);
        }
    }

    @Override
    public void initData() {

        if (deleteDialog == null) {
            deleteDialog = new DeleteDialog(this);
        }

        cameraPhotoListPresenter = new CameraPhotoListPresenter(this);

        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.left = DensityUtils.dp2px(getContext(), 2);
                outRect.right = DensityUtils.dp2px(getContext(), 2);
                outRect.bottom = DensityUtils.dp2px(getContext(), 2);
                outRect.top = DensityUtils.dp2px(getContext(), 2);
            }
        });

        ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        if (mPhotolisAdapter == null) {
            mPhotolisAdapter = new CameraPhotoListAdapter(CameraPhotoListActivity.this, listList, new CameraPhotoListAdapter.MyAlbumAdapterCallback() {
                @Override
                public void selectedAlbumCount(int choosed) {
                    mTitle.setText(String.format(getResources().getString(R.string.already_selecte_count), choosed));
                }

                @Override
                public void clearAlbume() {

                }
            });
        }

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setGridLayoutManager(5);
        } else {
            setGridLayoutManager(3);
        }

        mRecyclerView.setAdapter(mPhotolisAdapter);

        mSwipeRefreshView.setColorSchemeResources(R.color.refreshcolor_id);

        mSwipeRefreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isSelectedPhotoStatus) {
                    mPhotolisAdapter.setSelectedStatus(false);
                }

                mSwipeRefreshView.setLoading(false);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshView.setRefreshing(false);
                    }
                }, 1000);
            }
        });

        mSwipeRefreshView.setOnLoadMoreListener(new SwipeRefreshView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                boolean refreshing = mSwipeRefreshView.isRefreshing();
                if (!refreshing) {
                    cameraPhotoListPresenter.requestCameraData(mPage);
                }
            }
        });

        photo_list_bottom_layout.measure(0, 0);
        measuredHeight = photo_list_bottom_layout.getMeasuredHeight();
    }

    private void setGridLayoutManager(int count) {
        //TODO 后面的数字是设置一行需要显示多少个,目前显示的是3个
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), count);
        SectionedSpanSizeLookup lookup = new SectionedSpanSizeLookup(mPhotolisAdapter, layoutManager);
        layoutManager.setSpanSizeLookup(lookup);

        mRecyclerView.setLayoutManager(layoutManager);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mSwipeRefreshView.setRefreshing(true);
        cameraPhotoListPresenter.stopPreview();

        mPhotolisAdapter.setOnItemClickListener(new CameraPhotoListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ArrayList<MoAlbumItem> list, int position) {
                Intent intent = new Intent(CameraPhotoListActivity.this, MyAlbumPreview.class);
                intent.putExtra("albumlist", list);
                intent.putExtra("index", position);
                intent.putExtra("currentalbum", list.get(position));
                intent.putExtra("iscamera", true);
                startActivity(intent);
            }
        });
    }

    /**
     * 设置照片列表页面,是不是选择状态
     */
    public void setSelectedPhotoListStatus(boolean isSelectedPhotoStatus) {
        try {
            this.isSelectedPhotoStatus = isSelectedPhotoStatus;
            if (mPhotolisAdapter == null) {
                mRecyclerView.setAdapter(mPhotolisAdapter);
            } else {
                mPhotolisAdapter.setSelectedStatus(isSelectedPhotoStatus);
                mPhotolisAdapter.notifyDataSetChanged();
            }
            if (!isSelectedPhotoStatus) {
                //在相册页面的时候,保留原始的集合.在相册页面点击取消的时候.把之前的list给重新赋值在重新传递进去
                ArrayList<MoAlbumItem> listUIitemBeans = mPhotolisAdapter.getMomediaWrraper().getListUIitemBeans();
                for (MoAlbumItem image : listUIitemBeans) {
                    image.isChecked = false;
                }
                mPhotolisAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {

        }
    }


    private Context getContext() {
        return CameraPhotoListActivity.this;
    }

    @Override
    public void downLoadFileSuccess() {
        mEditBtn.setVisibility(View.VISIBLE);
        mEditText.setVisibility(View.GONE);
        mTitle.setText(this.getResources().getString(R.string.camera_grallery));
        bottomLayoutAnimation(true);
        setSelectedPhotoListStatus(false);
        cameraPhotoListPresenter.clear();//把数据全部清理
        mPhotolisAdapter.clearSelectedPhotoList();
    }

    @Override
    public void getCameraMediaList() {
        mPage = 0;
        cameraPhotoListPresenter.requestCameraData(mPage);
    }

    @Override
    public void deleteFileSuccess() {
        mPage = 0;
        mPhotolisAdapter.clearSelectedPhotoList();
        cameraPhotoListPresenter.requestCameraData(mPage);
    }

    @Override
    public void refreshList(ArrayList<MoAlbumItem> albumItems) {
        if (albumItems != null && albumItems.size() > 0) {
            if (mPage == 0) {
                listList.clear();
            }
            listList.addAll(albumItems);
            mPhotolisAdapter.notifyDataSetChanged();
        } else {
            if (mPage == 0) {
                cameraDataNull();
            } else {
                ToastUtil.showToast(CameraPhotoListActivity.this, this.getResources().getString(R.string.already_bottom));
            }
        }
        mSwipeRefreshView.setRefreshing(false);
        mPage++;
    }

    private void cameraDataNull() {
        ToastUtil.showToast(CameraPhotoListActivity.this, "请拍摄照片或视频");
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onBackPressed() {
        if (isSelectedPhotoStatus) {
            editButtonControl(false, this.getResources().getString(R.string.camera_grallery));
        } else {
            CameraPhotoListActivity.this.finish();
        }
    }

    /**
     * 地步的，删除 喜欢，下载 布局的动画
     *
     * @param isUpOrdown true 向上移动
     *                   false 向下移动
     */
    private void bottomLayoutAnimation(final boolean isUpOrdown) {
        if (isUpOrdown) {
            fromYDelta = -measuredHeight;
            toYDelta = 0;
        } else {
            toYDelta = -measuredHeight;
            fromYDelta = 0;
        }

        ObjectAnimator translationY = new ObjectAnimator().ofFloat(photo_list_bottom_layout, "translationY", fromYDelta, toYDelta);
        AnimatorSet animatorSet = new AnimatorSet();  //组合动画
        animatorSet.playTogether(translationY); //设置动画
        animatorSet.setDuration(500);  //设置动画时间
        animatorSet.start();
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                if (!isUpOrdown) {
                    params.setMargins(0, 0, 0, measuredHeight);
                } else {
                    params.setMargins(0, 0, 0, 0);
                }
                mRecyclerViewGroup.setLayoutParams(params);
            }
        });
    }

    /**
     * 喜欢完成刷新UI
     *
     * @param selectedPhotoList
     */
    public void likeSuccess(ArrayList<MoAlbumItem> selectedPhotoList) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mPhotolisAdapter.clearSelectedPhotoList();
                editButtonControl(false, getResources().getString(R.string.camera_grallery));
                mPhotolisAdapter.notifyDataSetChanged();
            }
        });
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mLikeImg.setClickable(true);
                CameraToastUtil.show(getResources().getString(R.string.like_success), mContext);
            }
        }, 500);
    }

    private Handler mHandler = new Handler();
}
