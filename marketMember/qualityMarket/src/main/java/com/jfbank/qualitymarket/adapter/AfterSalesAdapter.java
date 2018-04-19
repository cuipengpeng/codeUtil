package com.jfbank.qualitymarket.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.fragment.BillPendingFragment;
import com.jfbank.qualitymarket.fragment.ClosedBillingFragment;
import com.jfbank.qualitymarket.fragment.SalesApplyFragment;
import com.jfbank.qualitymarket.fragment.SalesProgressFragment;
import com.jfbank.qualitymarket.fragment.SalesReturnSubmitFragment;
import com.jfbank.qualitymarket.util.CommonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 文 件 名：AfterSalesAdapter
 * 功    能：售后适配器
 * 作    者：赵海
 * 时    间：2016/7/11
 **/
public class AfterSalesAdapter extends FragmentPagerAdapter {
    Context context;
    List<TextView> tabItemList = new ArrayList<>();//菜单栏View
    String[] titleText = null;//菜单栏Text

    public AfterSalesAdapter(Context context, FragmentManager fm) {
        super(fm);
        titleText = context.getResources().getStringArray(R.array.str_tab_prograssstate);
        this.context = context;
    }
    /**
     * 获取当前TabView
     *
     * @param position
     * @return
     */
    public View getTabView(int position) {
        View tabView = LayoutInflater.from(context).inflate(R.layout.tab_textview_style1, null);
        TextView tvTabItem = (TextView) tabView.findViewById(R.id.tv_tab_item);
        tvTabItem.setText(titleText[position]);
        tabItemList.add(tvTabItem);
        return tabView;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new SalesApplyFragment();
            case 1:
                return new SalesProgressFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
