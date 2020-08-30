package com.test.xcamera.cameraclip.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class VideoTempleteFragmentPagetAdapter extends FragmentPagerAdapter {

    public List<Fragment> mFragmentList = new ArrayList<>();

    public VideoTempleteFragmentPagetAdapter(FragmentManager fm) {
        super(fm);
    }

    public void updateData(boolean refresh, List<Fragment> fragmentList){
        if(refresh){
            mFragmentList.clear();
        }
        mFragmentList.addAll(fragmentList);
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int i) {
        return mFragmentList.get(i);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

}
