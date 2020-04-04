package com.caishi.chaoge.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class SelectTempletFragmentPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> mDataList = new ArrayList<Fragment>();

    public SelectTempletFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        this.mDataList.clear();
    }

    public void upateData(boolean isRefresh, List<Fragment> fragmentList) {
        if (isRefresh) {
            this.mDataList.clear();
        }
        this.mDataList.addAll(fragmentList);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Fragment getItem(int position) {
        return mDataList.get(position);
    }
}
