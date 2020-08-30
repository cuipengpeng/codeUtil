package com.test.xcamera.test.view;

import com.test.xcamera.R;
import com.test.xcamera.test.presenter.TestPresenter;
import com.moxiang.common.base.BaseFragment;

/**
 * Created by admin on 2020/2/12.
 */

public class TestFragment extends BaseFragment<TestPresenter> {
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

    @Override
    protected void initPresenter() {
        mPresenter = new TestPresenter();
    }

    @Override
    protected int bindLayout() {
        return R.layout.activity_test;
    }

    @Override
    protected void initData() {
        mPresenter.getTestData();
    }

    @Override
    protected void initView() {

    }
}
