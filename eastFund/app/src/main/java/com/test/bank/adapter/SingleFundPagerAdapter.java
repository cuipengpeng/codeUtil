package com.test.bank.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/*
* 描    述：<br>
* 作    者：崔朋朋<br>
* 时    间：2018/2/6<br>
*/
public class SingleFundPagerAdapter extends PagerAdapter {
    private List<View> mDataList = new ArrayList<View>();

    public SingleFundPagerAdapter(List<View> viewList){
        this.mDataList.clear();
        mDataList.addAll(viewList);
    }

    public void upateData(boolean isRefresh, List<View> viewList) {
        if (isRefresh) {
            this.mDataList.clear();
        }
        this.mDataList.addAll(viewList);
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {

        return mDataList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup view, int position, Object object) {
        view.removeView(mDataList.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        view.addView(mDataList.get(position));
        return mDataList.get(position);
    }
}
