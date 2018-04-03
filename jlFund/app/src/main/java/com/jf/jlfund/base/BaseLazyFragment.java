package com.jf.jlfund.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import com.jf.jlfund.utils.LogUtils;

public abstract class BaseLazyFragment extends Fragment {
    private static final String TAG = "BaseLazyFragment";

    private boolean isUserVisible = false;    //当前Fragment是否可见
    private boolean isViewInited = false;   //view是否初始化完成
    private boolean isFirstLoad = true;  //是否是第一次加载

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        isViewInited = true;
        lazyLoadData();
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        this.isUserVisible = isVisibleToUser;
        if (isVisibleToUser) {
            lazyLoadData();
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    private void lazyLoadData() {
        if (!isNeedLazyLoad()) {
            return;
        }

        if (!isFirstLoad || !isUserVisible || !isViewInited) {
            return;
        }
        doBusiness();
        isFirstLoad = false;
    }


    /**
     * 开始业务逻辑的处理
     * 如果不需要懒加载，则会在onResume中调用，如果懒加载，则会在第一次对用户可见的时候加载
     */
    protected void doBusiness() {
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isNeedLazyLoad()) {
            doBusiness();
        }
    }

    /**
     * 是否需要懒加载，默认需要
     *
     * @return
     */
    protected boolean isNeedLazyLoad() {
        return true;
    }

}
