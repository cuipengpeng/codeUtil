package com.test.xcamera.moalbum.fragment;


import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.framwork.base.view.MOBaseFragment;
import com.test.xcamera.R;
import com.test.xcamera.bean.MoAlbumItem;
import com.test.xcamera.moalbum.activity.MyAlbumPreview;
import com.test.xcamera.utils.CustomGlideUtils;
import com.test.xcamera.utils.PUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * author zxc
 * createTime 2019/10/12  下午11:49
 */
public class CameraPhotoPreviewFragment extends MOBaseFragment implements MyAlbumPreview.PagerChange {
    @BindView(R.id.photoView)
    ImageView photoView;
    private MyAlbumPreview activity;
    private String srcUrl;
    private String thumbnailUrl;
    private int rotate;
    private boolean isHide = false;

    public static CameraPhotoPreviewFragment newInstanceFragment(MoAlbumItem moAlbumItem) {
        CameraPhotoPreviewFragment photoPreviewFragment = new CameraPhotoPreviewFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("moalbumitem", moAlbumItem);
        photoPreviewFragment.setArguments(bundle);
        return photoPreviewFragment;
    }

    @Override
    public int initView() {
        return R.layout.photo_preview_fragment_layout;
    }

    @Override
    public void initClick() {

    }

    @Override
    public void initData() {
        Bundle arguments = this.getArguments();
        MoAlbumItem item = (MoAlbumItem) arguments.getSerializable("moalbumitem");
        srcUrl = item.getmImage().getmUri();
        thumbnailUrl = item.getmThumbnail().getmUri();
        rotate = item.getRotate();

        activity = (MyAlbumPreview) getActivity();
        activity.setPagerChange(this);
        rotate(rotate);
    }

    @OnClick({R.id.photoView})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.photoView:
                if (isHide) {
                    isHide = false;
                    activity.hideOrVisibilityTitle(View.VISIBLE);
                    activity.controlLayoutVisible();
                } else {
                    isHide = true;
                    activity.controlLayoutGone();
                    activity.hideOrVisibilityTitle(View.GONE);
                }
                break;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        rotate(rotate);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        activity.finish();
    }

    private void rotate(int rotate) {

        int h = PUtil.getScreenH(getContext());
        int w = (int) (h * 1080 / 1920f);

        int range = 0;
        if (rotate == 0) {
            range = 0;
        } else if (rotate == 1) {
            range = 270;
            setVideoCoverParams(h, w);
        } else if (rotate == 2) {
            range = 180;
            setVideoCoverParams(h, w);
        } else if (rotate == 3) {
            range = 90;
            setVideoCoverParams(h, w);
        }

        CustomGlideUtils.loadAlbumPhotoAngle(getActivity(), thumbnailUrl, srcUrl, photoView, 0L, range);
    }

    private void setVideoCoverParams(int h, int w) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) photoView.getLayoutParams();
        layoutParams.width = w;
        layoutParams.height = h;
        photoView.setLayoutParams(layoutParams);
    }
}
