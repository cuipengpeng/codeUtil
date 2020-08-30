package com.test.xcamera.test.view;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.test.xcamera.R;
import com.test.xcamera.test.contract.TestContract;
import com.test.xcamera.test.presenter.TestPresenter;
import com.moxiang.common.base.BaseActivity;


/**
 * Created by admin on 2019/11/6.
 */

public class TestActivity extends BaseActivity<TestPresenter> implements TestContract.ITestView {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter.getTestData();
    }

    @Override
    protected void initPresenter() {
        mPresenter = new TestPresenter();
    }

    @Override
    protected int bindLayout() {
        return R.layout.activity_test;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    @Override
    protected void initView(Bundle savedInstanceState) {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showNetWorkErrorView() {

    }

    @Override
    public void showNoDataView() {

    }
}
