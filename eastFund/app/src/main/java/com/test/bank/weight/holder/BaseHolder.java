package com.test.bank.weight.holder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.test.bank.utils.ActivityManager;

/**
 * Created by 55 on 2017/12/6.
 */

public abstract class BaseHolder<Data> {
    protected final String TAG = this.getClass().getSimpleName();
    protected Context mContext;
    protected View rootView;
    protected Data data;

    public BaseHolder() {
        rootView = createRootView();    //代码生成view【简单的条目可以代码生成，不必再写资源文件】
        if (rootView == null) {
            rootView = LayoutInflater.from(ActivityManager.getInstance().currentActivity()).inflate(getLayoutId(), null, false);
        }
        if (rootView != null) {
            mContext = rootView.getContext();
        }
        initView(rootView);
    }

    protected abstract void initView(View rootView);

    protected abstract int getLayoutId();

    public View inflateData(Data data) {
        this.data = data;
        if (data != null) {
            updateView();
        }
        return rootView;
    }

    protected View createRootView() {
        return null;
    }

    protected abstract void updateView();
}
