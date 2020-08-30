package com.test.xcamera.album.preview;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.framwork.base.view.MOBaseActivity;
import com.test.xcamera.R;
import com.test.xcamera.album.adapter.LocalMediaPreviewAdater;
import com.test.xcamera.moalbum.fragment.CameraVideoPreviewFragment;
import com.test.xcamera.album.preview.presenter.LocalMediaPreviewActivityPresenter;
import com.test.xcamera.bean.MoAlbumItem;
import com.test.xcamera.utils.CameraToastUtil;
import com.test.xcamera.view.DeleteDialog;
import com.test.xcamera.view.InformationsPopWindow;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * author zxc
 * createTime 2019/10/12  下午10:48
 * 大图预览页面
 */
public class LocalMediaPreviewActivity extends MOBaseActivity implements ViewPager.OnPageChangeListener {
    private static final int DOWN_LOADFILE = 10000;
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
    @BindView(R.id.portraitDownPB)
    ProgressBar portraitDownPB;
    @BindView(R.id.landscapeDownImage)
    ImageView landscapeDownImage;
    @BindView(R.id.landscapeDownPB)
    ProgressBar landscapeDownPB;
    @BindView(R.id.titleImage)
    ImageView titleImage;
    @BindView(R.id.informationIncon)
    ImageView informationIncon;

    private PagerChange pagerChange;
    private LocalMediaPreviewAdater mediaPreviewAdater;
    private LocalMediaPreviewActivityPresenter mediaPreviewActivityPresenter;
    private ArrayList<MoAlbumItem> albumList;
    private long sumLen = 0;
    private boolean downloading = false;
    private MoAlbumItem currentDeleteAlbumItem;
    private InformationsPopWindow informationsPopWindow;
    private MoAlbumItem currentAlbumItem;
    @SuppressLint("HandlerLeak")
    private Handler uihandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWN_LOADFILE:
                    int len = (int) msg.obj;
                    sumLen += len;
                    long mSunCount = mediaPreviewActivityPresenter.getmSunCount();
                    if (sumLen == mSunCount) {
                        downloading = false;
                        CameraToastUtil.show("下载成功", LocalMediaPreviewActivity.this);
                        sumLen = 0;
                        downIconUI();
                    }
                    break;
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTitleStatus(true);
        noStatusBar();
        super.onCreate(savedInstanceState);
    }

    @Override
    public int initView() {
        return R.layout.local_media_activity_layout;
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
        }
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            bottomLayout.setVisibility(View.VISIBLE);
            leftLayout.setVisibility(View.GONE);
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            bottomLayout.setVisibility(View.GONE);
            leftLayout.setVisibility(View.VISIBLE);
        }
        downIconUI();
    }

    @Override
    public void initData() {

        if (deleteDialog == null) {
            deleteDialog = new DeleteDialog(this);
        }

        albumList = (ArrayList<MoAlbumItem>) getIntent().getSerializableExtra("albumlist");
        int index = getIntent().getIntExtra("index", -1);
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            bottomLayout.setVisibility(View.VISIBLE);
            leftLayout.setVisibility(View.GONE);
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            bottomLayout.setVisibility(View.GONE);
            leftLayout.setVisibility(View.VISIBLE);
        }

        mediaPreviewAdater = new LocalMediaPreviewAdater(this.getSupportFragmentManager(), albumList);
        previewViewPager.setAdapter(mediaPreviewAdater);
        previewViewPager.setCurrentItem(index);
        previewViewPager.addOnPageChangeListener(this);
        setMediaType(index);
        mediaPreviewActivityPresenter = new LocalMediaPreviewActivityPresenter(this, uihandler);
        informationsPopWindow = new InformationsPopWindow(this, informationIncon);
    }

    @OnClick({R.id.titleTextView, R.id.landscapeLayoutShare,
            R.id.landscapeLayoutDown, R.id.landscapeLayoutLike,
            R.id.landscapeLayoutDelete, R.id.portraitLayoutShare,
            R.id.portraitLayoutDown, R.id.portraitLayoutLike,
            R.id.portraitLayoutDelete, R.id.titleBack, R.id.titleLayout})
    public void onViewClicked(View view) {
        int currentItem = previewViewPager.getCurrentItem();
        switch (view.getId()) {
            case R.id.titleLayout:
            case R.id.titleTextView:
                informationsPopWindow.show(titleLayout, albumList.get(previewViewPager.getCurrentItem()));
                //显示详细信息
                break;
            case R.id.landscapeLayoutShare:
            case R.id.portraitLayoutShare:
                break;
            case R.id.landscapeLayoutDown:
            case R.id.portraitLayoutDown:
                downloading = true;
                ArrayList<MoAlbumItem> downTempList = new ArrayList();
                downTempList.add(albumList.get(currentItem));
                mediaPreviewActivityPresenter.startDownloadFile(downTempList);
                downIconUI();
                break;
            case R.id.landscapeLayoutLike:
            case R.id.portraitLayoutLike:
                mediaPreviewActivityPresenter.like(currentAlbumItem, currentAlbumItem.getCollect() == 1 ? 0 : 1);
                break;
            case R.id.landscapeLayoutDelete:
            case R.id.portraitLayoutDelete:
                delete(currentItem);
                break;
            case R.id.titleBack:
                if (pagerChange != null) {
                    pagerChange.onBackPressed();
                }
                break;
        }
    }


    private void delete(int currentItem) {
        currentDeleteAlbumItem = albumList.get(currentItem);
        final ArrayList<MoAlbumItem> list = new ArrayList<>();
        list.add(currentDeleteAlbumItem);

        deleteDialog.showDialog(new DeleteDialog.SureOnClick() {
            @Override
            public void sure_button() {
                mediaPreviewActivityPresenter.deleteMedia(list);
            }
        }, this.getResources().getString(R.string.delete_dialog_content));
    }

    /**
     * 删除成功
     */
    public void deleteOKRefreshUI() {
        CameraToastUtil.show("删除成功", LocalMediaPreviewActivity.this);
        if (albumList.size() == 0) {
            LocalMediaPreviewActivity.this.finish();
        }
        albumList.remove(currentDeleteAlbumItem);
        mediaPreviewAdater.refreshData(albumList);
    }

    /**
     * 控制左边和下边的布局的显示
     */
    public void controlLayoutVisible() {
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            bottomLayout.setVisibility(View.VISIBLE);
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
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
        informationsPopWindow.showPageSelect(titleLayout, albumList.get(previewViewPager.getCurrentItem()));
        setMediaType(position);
    }

    private void setMediaType(int position) {
        currentAlbumItem = albumList.get(position);

        if (!currentAlbumItem.getmType().equals("video")) {
            titleImage.setVisibility(View.GONE);
        } else {
            titleImage.setVisibility(View.VISIBLE);
        }

        if (currentAlbumItem.getCollect() == 1) {
            landscapeLikeImageView.setImageResource(R.mipmap.alreadylike);
            portraitLikeImageView.setImageResource(R.mipmap.alreadylike);
        } else {
            landscapeLikeImageView.setImageResource(R.mipmap.defaultlike);
            portraitLikeImageView.setImageResource(R.mipmap.defaultlike);
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
        if (pagerChange != null) {
            pagerChange.onBackPressed();
        }
    }

    private void downIconUI() {
        if (downloading) {
            portraitDownImage.setVisibility(View.GONE);
            portraitDownPB.setVisibility(View.VISIBLE);

            landscapeDownImage.setVisibility(View.GONE);
            landscapeDownPB.setVisibility(View.VISIBLE);
        } else {
            portraitDownImage.setVisibility(View.VISIBLE);
            portraitDownPB.setVisibility(View.GONE);

            landscapeDownImage.setVisibility(View.VISIBLE);
            landscapeDownPB.setVisibility(View.GONE);
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

    public interface PagerChange {
        void onBackPressed();
    }
}
