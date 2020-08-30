package com.moxiang.common.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.InflateException;

import com.moxiang.common.mvp.BasePresenter;
import com.moxiang.common.mvp.IPresenter;
import com.moxiang.common.mvp.IView;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by admin on 2019/10/15.
 */

public abstract class BaseActivity<P extends BasePresenter> extends AppCompatActivity implements IView {
    protected P mPresenter;
    private Unbinder mUnbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            int layoutResID = bindLayout();
            if (layoutResID != 0) {
                setContentView(layoutResID);
                mUnbinder = ButterKnife.bind(this);
            }
        } catch (Exception e) {
            if (e instanceof InflateException) throw e;
            e.printStackTrace();
        }
        initPresenter();
        if (mPresenter != null) {
            mPresenter.attachView(this);
        }
        initView(savedInstanceState);
        initData(savedInstanceState);
    }

    /**
     * 初始化mPresenter
     */
    protected abstract void initPresenter();
    protected abstract int bindLayout();
    protected abstract void initData( Bundle savedInstanceState);
    protected abstract void initView( Bundle savedInstanceState);

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUnbinder != null && mUnbinder != Unbinder.EMPTY)
            mUnbinder.unbind();
        this.mUnbinder = null;
        if (mPresenter != null)
            mPresenter.onDestroy();//释放资源
        this.mPresenter = null;
    }
}
