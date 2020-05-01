package com.jfbank.qualitymarket.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.adapter.AfterSalesAdapter;
import com.jfbank.qualitymarket.base.BaseMvpActivity;
import com.jfbank.qualitymarket.constants.EventBusConstants;
import com.jfbank.qualitymarket.helper.ZoomOutPageTransformer;
import com.jfbank.qualitymarket.mvp.AfterSalesMVP;

import org.simple.eventbus.EventBus;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 功能：申请售后列表<br>
 * 作者：赵海<br>
 * 时间： 2017/4/19 0019<br>.
 * 版本：1.2.0
 */

public class AfterSalesActivity extends BaseMvpActivity<AfterSalesMVP.Presenter, AfterSalesMVP.Model> implements AfterSalesMVP.View {
    @InjectView(R.id.tbs_aftersales)
    TabLayout tbsAftersales;
    @InjectView(R.id.vp_aftersales)
    ViewPager vpAftersales;
    AfterSalesAdapter afterSalesAdapter;

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_after_sales;
    }

    @Override
    protected void initView() {
        tvTitle.setText(R.string.str_pagename_afersales);
        afterSalesAdapter = new AfterSalesAdapter(this, getSupportFragmentManager());
        vpAftersales.setAdapter(afterSalesAdapter);
        vpAftersales.setOffscreenPageLimit(2);
        //初始化tab
        tbsAftersales.setupWithViewPager(vpAftersales);
        vpAftersales.setPageTransformer(true, new ZoomOutPageTransformer());
        for (int i = 0; i < 2; i++) {
            TabLayout.Tab tab = tbsAftersales.getTabAt(i);
            tab.setCustomView(afterSalesAdapter.getTabView(i));
        }
        initSelet();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        initSelet();
    }

    /**
     * 默认选择
     */
    private void initSelet() {
        int seletType = getIntent().getIntExtra(ProgressCheckForGoodsRejectedActivity.KEY_OF_SELECT_APPLY_AFTER_SALE_TAB, 1);
        if (seletType == 1) {
            vpAftersales.setCurrentItem(0);
        } else if (seletType == 2) {
            vpAftersales.setCurrentItem(1);
        }
    }

    @Override
    protected void initData() {
    }


    @Override
    protected String getPageName() {
        return getString(R.string.str_pagename_afersales);
    }
}