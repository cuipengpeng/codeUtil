package com.caishi.chaoge.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.caishi.chaoge.ui.widget.dialog.LoadDialog;
import com.caishi.chaoge.utils.LogUtil;
import com.caishi.chaoge.utils.ToastUtils;
import com.umeng.analytics.MobclickAgent;


public abstract class BaseFragment extends Fragment implements View.OnClickListener {
    public View view;
    public FragmentActivity mContext;
    // public static String userID;//当前视频播放的用户ID

    /**
     * 是否对用户可见
     */
    protected boolean mIsVisible;
    /**
     * 是否加载完成
     * 当执行完oncreatview,View的初始化方法后方法后即为true
     */
    protected boolean mIsPrepare;
    private LoadDialog loadDialog;

    public BaseFragment() { /* compiled code */ }

    private Fragment currentFragment;
    /**
     * 日志输出标志
     **/
    protected final String TAG = this.getClass().getSimpleName();


    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        LogUtil.i("BaseFragment+++onCreateView");
        mContext = getActivity();
//        EventBus.getDefault().register(this);
        if (null != view) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (null != parent) {
                parent.removeView(view);
            }
        } else {
            view = inflater.inflate(initContentView(), container, false);
        }
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if ((isVisibleToUser && isResumed())) {
            onResume();
        }
        if (getUserVisibleHint()) {
            mIsVisible = true;
            onVisible();
        } else {
            mIsVisible = false;
            onInvisible();
        }
    }


    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(this.getClass().getSimpleName()); //统计页面("MainScreen"为页面名称，可自定义)
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(this.getClass().getSimpleName());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LogUtil.i("BaseFragment+++onActivityCreated");
        initView(view);
        if (isLazyLoad()) {
            mIsPrepare = true;
            onLazyLoad();
        } else {
            doBusiness();
            setListener();
        }
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }


    /**
     * 是否懒加载
     *
     * @return the boolean
     */
    protected boolean isLazyLoad() {
        return true;
    }

    /**
     * 用户可见时执行的操作
     */
    protected void onVisible() {
        onLazyLoad();
    }

    /**
     * 用户不可见执行
     */
    protected void onInvisible() {
    }

    private void onLazyLoad() {
        if (mIsPrepare) {
            mIsPrepare = false;
            doBusiness();
            setListener();
        }
    }

    /**
     * 初始化UI setContentView
     */
    protected abstract int initContentView();

    /**
     * 初始化控件
     *
     * @param view
     */
    protected abstract void initView(View view);

    /**
     * 逻辑处理
     */
    protected abstract void doBusiness();

    /**
     * 设置监听
     */
    public abstract void setListener();


    @Override
    public void onClick(View v) {
        widgetClick(v);
    }

    /**
     * View点击
     **/
    public abstract void widgetClick(View v);


    /**
     * 绑定控件
     *
     * @param resId
     * @return
     */
    protected <T extends View> T $(int resId) {
        return view.findViewById(resId);
    }


    /**
     * Toast显示
     *
     * @param string 内容
     */
    public void showToast(String string) {
        ToastUtils.show(getActivity(), string);
    }

    /**
     * 居中Toast显示
     *
     * @param string 内容
     */
    public void showCentreToast(String string) {
        ToastUtils.showCentreToast(getActivity(), string);
    }

    /**
     * 页面跳转
     *
     * @param clz class
     */
    public void startActivity(Class<?> clz) {
        startActivity(new Intent(getActivity(), clz));
    }

    /**
     * 页面跳转finish
     *
     * @param clz class
     */
    public void startActivityFinish(Class<?> clz) {
        startActivity(new Intent(getActivity(), clz));
        getActivity().finish();
    }

    /**
     * 携带数据的页面跳转
     *
     * @param clz    class
     * @param bundle 数据
     */
    public void startActivity(Class<?> clz, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(mContext, clz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }


    /**
     * 含有Bundle通过Class打开编辑界面
     *
     * @param cls         cls
     * @param bundle      bundle
     * @param requestCode requestCode
     */
    public void startActivityForResult(Class<?> cls, Bundle bundle, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(mContext, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }

    /**
     * 页面跳转finish
     *
     * @param cls    目标Activity
     * @param bundle 数据
     */
    protected void startActivityFinish(Class<?> cls, Bundle bundle) {
        startActivity(cls, bundle);
        getActivity().finish();
    }

    /**
     * 显示加载框
     */
    protected LoadDialog showLoadView() {
        loadDialog = LoadDialog.newInstance();
        loadDialog.showLoadDialog(mContext, "");
        return loadDialog;

    }

    /**
     * 显示加载框
     */
    protected LoadDialog showLoadView(String hint) {
        loadDialog.showLoadDialog(mContext, hint);
        return loadDialog;
    }

    /**
     * 取消加载框
     */
    public LoadDialog closeDialog() {
        if (loadDialog != null)
            loadDialog.dismissDialog();
        return loadDialog;
    }


    /**
     * fragemnt切换
     */
    public void switchFragment(Fragment targetFragment, int containerViewId) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction().addToBackStack(null);
        if (!targetFragment.isAdded()) {
            if (currentFragment != null) {
                transaction.hide(currentFragment).add(containerViewId, targetFragment).commit();
            } else {
                transaction.add(containerViewId, targetFragment).commit();
            }
        } else {
            transaction.hide(currentFragment).show(targetFragment).commit();
        }
        currentFragment = targetFragment;
    }


//    /**
//     * 切换viewPager
//     *
//     * @param position
//     */
//    public void switchViewPager(int position) {
//        ((MainActivity) mContext).svp_main_layout.setCurrentItem(position, true);
//    }

    /**
     * 是否登录
     *
     * @return true登录，false没有登录
     */
    public boolean isLogin() {
        return ((BaseActivity) mContext).isLogin();
    }

    /**
     * 获取userID
     */
    public String getCGUserId() {
        return ((BaseActivity) mContext).getCGUserId();
    }

    /**
     * 获取userToken
     */
    public String getCredential() {
        return ((BaseActivity) mContext).getCredential();
    }


}