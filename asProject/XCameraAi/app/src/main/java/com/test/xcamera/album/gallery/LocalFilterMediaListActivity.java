package com.test.xcamera.album.gallery;

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
import com.test.xcamera.album.adapter.LocalFilterPhotoListAdapter;
import com.test.xcamera.album.preview.LocalMediaPreviewActivity;
import com.test.xcamera.album.sectionrec.SectionedSpanSizeLookup;
import com.test.xcamera.bean.MoAlbumItem;
import com.test.xcamera.utils.DensityUtils;
import com.test.xcamera.view.DeleteDialog;
import com.editvideo.ToastUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by 周 on 2019/11/10.
 */

public class LocalFilterMediaListActivity extends MOBaseActivity implements View.OnClickListener, LocalFilterMediaListInterface {

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
    @BindView(R.id.activity_camera_photo_list_delete)
    ImageView mDeleteImg;
    @BindView(R.id.photo_list_bottom_layout)
    LinearLayout photo_list_bottom_layout;
    @BindView(R.id.mRecyclerViewGroup)
    RelativeLayout mRecyclerViewGroup;

    private LocalFilterPhotoListAdapter mPhotolisAdapter;
    private MomediaWrraper listList = new MomediaWrraper();
    private boolean isSelectedPhotoStatus = false;
    private LocalFilterMediaListPresenter cameraPhotoListPresenter;
    private int measuredHeight;
    private int fromYDelta = 0;
    private int toYDelta = 0;


    @OnClick({R.id.activity_camera_photo_list_back_btn, R.id.activity_camera_photo_list_edit_text,
            R.id.activity_camera_photo_list_edit_btn, R.id.activity_camera_photo_list_download,
            R.id.activity_camera_photo_list_delete,
            R.id.activity_camera_photo_list_middle_title})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.activity_camera_photo_list_back_btn:
                if (isSelectedPhotoStatus) {
                    editButtonControl(false, this.getResources().getString(R.string.camera_gallery_filter));
                } else {
                    this.finish();
                }
                break;
            case R.id.activity_camera_photo_list_edit_text:
                editButtonControl(false, this.getResources().getString(R.string.camera_gallery_filter));
                break;
            case R.id.activity_camera_photo_list_edit_btn:
                if (mPhotolisAdapter.getSelecetPhotoCount() == 0) {
                    editButtonControl(true, this.getResources().getString(R.string.select_camera_list_item));
                }
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

            deleteDialog.showDialog(new DeleteDialog.SureOnClick() {
                @Override
                public void sure_button() {

                    cameraPhotoListPresenter.deleteMedia(mPhotolisAdapter.getSelectedPhotoList());
                }
            }, this.getResources().getString(R.string.delete_dialog_content));
        }
    }

    @Override
    public int initView() {
        return R.layout.activity_local_filter_media_list;
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

        cameraPhotoListPresenter = new LocalFilterMediaListPresenter(this);

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
            mPhotolisAdapter = new LocalFilterPhotoListAdapter(LocalFilterMediaListActivity.this, listList, new LocalFilterPhotoListAdapter.SelectedPhotoCountCallback() {
                @Override
                public void selectedPhotoCount(int choosed) {
                    mTitle.setText(String.format(getResources().getString(R.string.already_selecte_count), choosed));
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
//        cameraPhotoListPresenter.stopPreview();
        cameraPhotoListPresenter.getLocalFilterMediaList();

        mPhotolisAdapter.setOnItemClickListener(new LocalFilterPhotoListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ArrayList<MoAlbumItem> list, int position) {
                Intent intent = new Intent(LocalFilterMediaListActivity.this, LocalMediaPreviewActivity.class);
                intent.putExtra("albumlist", list);
                intent.putExtra("index", position);
                intent.putExtra("currentalbum", list.get(position));
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
        return LocalFilterMediaListActivity.this;
    }

    @Override
    public void downLoadFileSuccess() {
        //点击进入大图逻辑
        mEditBtn.setVisibility(View.VISIBLE);
        mEditText.setVisibility(View.GONE);
        mTitle.setText(this.getResources().getString(R.string.camera_gallery_filter));
        bottomLayoutAnimation(true);
        setSelectedPhotoListStatus(false);
//        cameraPhotoListPresenter.clear();//把数据全部清理
        mPhotolisAdapter.clearSelectedPhotoList();
    }

    @Override
    public void getCameraMediaList() {
        cameraPhotoListPresenter.getLocalFilterMediaList();
    }

    @Override
    public void deleteFileSuccess() {
        mPhotolisAdapter.clearSelectedPhotoList();
        cameraPhotoListPresenter.getLocalFilterMediaList();
    }

    @Override
    public void refreshList(ArrayList<MoAlbumItem> albumItems) {
        if (albumItems != null && albumItems.size() > 0) {
            listList.clear();
            listList.addAll(albumItems);
            mPhotolisAdapter.notifyDataSetChanged();
        } else {
            ToastUtil.showToast(LocalFilterMediaListActivity.this, this.getResources().getString(R.string.already_bottom));
        }
        mSwipeRefreshView.setRefreshing(false);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onBackPressed() {
        if (isSelectedPhotoStatus) {
            editButtonControl(false, this.getResources().getString(R.string.camera_gallery_filter));
        } else {
            LocalFilterMediaListActivity.this.finish();
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
}
