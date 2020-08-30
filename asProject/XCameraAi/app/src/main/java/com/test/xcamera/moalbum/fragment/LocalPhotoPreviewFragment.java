package com.test.xcamera.moalbum.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.framwork.base.view.MOBaseFragment;
import com.test.xcamera.R;
import com.test.xcamera.moalbum.activity.MyAlbumPreview;
import com.test.xcamera.bean.MoAlbumItem;
import com.test.xcamera.utils.GlideUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * author zxc
 * createTime 2019/10/12  下午11:49
 */
public class LocalPhotoPreviewFragment extends MOBaseFragment implements MyAlbumPreview.PagerChange {
    @BindView(R.id.photoView)
    ImageView photoView;

    private boolean isHide = false;
    private MyAlbumPreview activity;

    public static LocalPhotoPreviewFragment newInstanceFragment(MoAlbumItem moAlbumItem) {
        LocalPhotoPreviewFragment photoPreviewFragment = new LocalPhotoPreviewFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("moalbumitem", moAlbumItem);
        photoPreviewFragment.setArguments(bundle);
        return photoPreviewFragment;
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
        GlideUtils.GlideLoader(getContext(), item.getmThumbnail().getmUri(), photoView);
        activity = (MyAlbumPreview) getActivity();
        activity.setPagerChange(this);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        activity.finish();
    }
}
