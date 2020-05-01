package com.jfbank.qualitymarket.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.fragment.BillPendingFragment;
import com.jfbank.qualitymarket.fragment.ClosedBillingFragment;
import com.jfbank.qualitymarket.fragment.HomeFragment;
import com.jfbank.qualitymarket.util.CommonUtils;
import com.jfbank.qualitymarket.widget.BadgeView;

import java.util.ArrayList;
import java.util.List;

/**
 * 文 件 名：MainPagerAdapter
 * 功    能：主界面适配器
 * 作    者：赵海
 * 时    间：2016/7/11
 **/
public class PrepaymentAdapter extends FragmentPagerAdapter {
    Context context;
    List<TextView> tabItemList = new ArrayList<>();//菜单栏View
    String[] titleText = null;//菜单栏Text
    int[] titleIcon = {R.drawable.ic_tabs_nopay, R.drawable.ic_tabs_payed, R.drawable.ic_tabs_payall};//菜单栏ICON

    public PrepaymentAdapter(Context context, FragmentManager fm) {
        super(fm);
        titleText = context.getResources().getStringArray(R.array.str_tab_prepayment);
        this.context = context;
    }

    public List<TextView> getTabItemList() {
        return tabItemList;
    }

    /**
     * 获取当前TabView
     *
     * @param position
     * @return
     */
    public View getTabView(int position) {
        View tabView = LayoutInflater.from(context).inflate(R.layout.tab_textview, null);
        TextView tvTabItem = (TextView) tabView.findViewById(R.id.tv_tab_item);
        tvTabItem.setText(titleText[position]);
        tvTabItem.setCompoundDrawables(null, CommonUtils.getDrawable(context, titleIcon[position]), null, null);
        tvTabItem.setCompoundDrawablePadding((int) context.getResources().getDimension(R.dimen.d5));
        tabItemList.add(tvTabItem);
        return tabView;
    }

    public void selectorTab(int position) {
        for (int i = 0; i < tabItemList.size(); i++) {
            if (position == i) {
                tabItemList.get(position).setSelected(true);
            } else {
                tabItemList.get(i).setSelected(false);
            }
        }
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new BillPendingFragment();
            case 1:
                return ClosedBillingFragment.newInstance(ClosedBillingFragment.BILL_STATUS_ALREADY_REPAYMENT);
            case 2:
                return ClosedBillingFragment.newInstance(ClosedBillingFragment.BILL_STATUS_ALL);
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
