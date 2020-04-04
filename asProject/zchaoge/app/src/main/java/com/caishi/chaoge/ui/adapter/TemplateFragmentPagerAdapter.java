package com.caishi.chaoge.ui.adapter;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.caishi.chaoge.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

public class TemplateFragmentPagerAdapter extends FragmentPagerAdapter {
    private List<BaseFragment> mDataList = new ArrayList<>();

    public TemplateFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        this.mDataList.clear();
    }

    public void update(boolean isRefresh, List<BaseFragment> fragmentList) {
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
    public BaseFragment getItem(int position) {
        return mDataList.get(position);
    }
}
