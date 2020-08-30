package com.moxiang.common.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moxiang.common.mvp.BasePresenter;
import com.moxiang.common.mvp.IView;

import butterknife.ButterKnife;

/**
 * Created by admin on 2019/10/15.
 */

public abstract class BaseFragment<P extends BasePresenter> extends Fragment implements IView {
    protected P mPresenter;
    protected Context mContext;
    private View mRootView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(bindLayout(), null);
            ButterKnife.bind(this, mRootView);
            initPresenter();
            if (mPresenter != null) {
                mPresenter.attachView(this);
            }
            initData();
            initView();
        }
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }
        return mRootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) mPresenter.onDestroy();//释放资源
        this.mPresenter = null;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    /**
     * 初始化mPresenter
     */
    protected abstract void initPresenter();
    protected abstract int bindLayout();
    protected abstract void initData();
    protected abstract void initView();
}
