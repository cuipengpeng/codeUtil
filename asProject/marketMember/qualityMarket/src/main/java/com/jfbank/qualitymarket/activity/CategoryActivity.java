package com.jfbank.qualitymarket.activity;

import android.os.Bundle;
import android.widget.LinearLayout;

import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.base.BaseActivity;
import com.jfbank.qualitymarket.fragment.CategoryFragment;
import com.jfbank.qualitymarket.util.CommonUtils;

/**
 * 功能：<br>
 * 作者：RaykerTeam
 * 时间： 2016/11/18 0018<br>.
 * 版本：1.0.0
 */

public class CategoryActivity extends BaseActivity {
    private LinearLayout llCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
//设置状态栏的颜色
        CommonUtils.setStatusTransColor(this);
        llCategory = (LinearLayout) findViewById(R.id.ll_category);
        CategoryFragment categoryFragment = new CategoryFragment();
        Bundle args = new Bundle();
        args.putString("upCategoroyType", getIntent().getStringExtra("upCategoroyType"));
        args.putInt("fromType", 0x2);
        categoryFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction().add(R.id.ll_category, categoryFragment).show(categoryFragment).commit();
    }

    @Override
    protected String getPageName() {
        return getString(R.string.str_pagename_categoryact);
    }
}
