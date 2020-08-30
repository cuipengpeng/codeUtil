package com.framwork.base.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.jaeger.library.StatusBarUtil;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by DELL on 2019/7/2.
 */

public abstract class MOBaseFragment extends Fragment {

    private Unbinder mUnBinder;

    public boolean isTitleStatus() {
        return titleStatus;
    }

    /**
     * 设置是否显示状态栏
     *
     * @param titleStatus
     */
    public void setTitleStatus(boolean titleStatus) {
        this.titleStatus = titleStatus;
    }

    private boolean titleStatus;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(initView(), null);

        //绑定初始化ButterKnife
        mUnBinder = ButterKnife.bind(this, view);
//        noStatusBar();
//        setStatusBar();
        initClick();
        initData();
        return view;
    }

    public abstract int initView();

    public void initClick() {
    }

    public abstract void initData();


    public void show(String msg) {
        Toast toast = Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnBinder.unbind();
    }

    public void startAct(Class clazz) {
        Intent intent = new Intent(getActivity(), clazz);
        startActivity(intent);
    }

    /**
     * 状态栏 是否显示
     */
    public void noStatusBar() {
//        if(isTitleStatus()){
//            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //隐藏状态栏
//        }else{
//
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//显示状态栏
//        }
    }

    /**
     * 沉浸式
     */
    protected void setStatusBar() {
        //为 fragment 头部是 ImageView 的设置状态栏透明
        StatusBarUtil.setTranslucentForImageViewInFragment(getActivity(), 50, null);
//        StatusBarUtil.setLightMode(getActivity());
    }

    protected void hideStatusBar(Activity activity) {
        if (isStatusbarVisible(activity)) {
            int uiOptions = activity.getWindow().getDecorView().getSystemUiVisibility();
            uiOptions |= View.SYSTEM_UI_FLAG_FULLSCREEN;
            activity.getWindow().getDecorView().setSystemUiVisibility(uiOptions);
        }
    }

    private boolean isStatusbarVisible(Activity activity) {
        int uiOptions = activity.getWindow().getDecorView().getSystemUiVisibility();
        boolean isStatusbarHide = ((uiOptions | View.SYSTEM_UI_FLAG_FULLSCREEN) == uiOptions);
        return !isStatusbarHide;
    }

}
