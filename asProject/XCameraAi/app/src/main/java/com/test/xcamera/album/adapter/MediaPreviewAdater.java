package com.test.xcamera.album.adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import com.test.xcamera.moalbum.fragment.CameraPhotoPreviewFragment;
import com.test.xcamera.moalbum.fragment.CameraVideoPreviewFragment;
import com.test.xcamera.moalbum.fragment.LocalPhotoPreviewFragment;
import com.test.xcamera.moalbum.fragment.LocalVideoPreviewFragment;
import com.test.xcamera.bean.MoAlbumItem;

import java.util.ArrayList;

/**
 * author zxc
 * createTime 2019/10/12  下午11:41
 */
public class MediaPreviewAdater extends FragmentStatePagerAdapter {
    private ArrayList<MoAlbumItem> list;
    private Fragment currentFragment;

    public MediaPreviewAdater(FragmentManager fm, ArrayList<MoAlbumItem> list) {
        super(fm);
        this.list = list;
    }

    @Override
    public Fragment getItem(int i) {
        MoAlbumItem moAlbumItem = list.get(i);
        if (!moAlbumItem.isVideo()) {
            if (moAlbumItem.ismIsCamrea()) {
                return CameraPhotoPreviewFragment.newInstanceFragment(moAlbumItem);
            } else {
                return LocalPhotoPreviewFragment.newInstanceFragment(moAlbumItem);
            }
        } else {
            if (moAlbumItem.ismIsCamrea()) {
                return CameraVideoPreviewFragment.newInstanceFragment(moAlbumItem);
            } else {
                return LocalVideoPreviewFragment.newInstanceFragment(moAlbumItem);
            }
        }
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        Log.i("TEST_TEST", "getItemPosition: start refresh !!!!");
        return POSITION_NONE;
    }

    public Fragment getCurrentFragment() {
        return currentFragment;
    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.setPrimaryItem(container, position, object);
        currentFragment = (Fragment) object;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    public void refreshData(ArrayList<MoAlbumItem> albumList) {
        this.list = albumList;
        notifyDataSetChanged();
    }
    
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }
}
