package com.test.bank.base;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;

import com.test.bank.R;
import com.test.bank.utils.ActivityManager;
import com.test.bank.utils.EasyPermissions;
import com.test.bank.utils.LogUtils;
import com.test.bank.utils.NetUtil;
import com.test.bank.utils.SPUtil;
import com.test.bank.view.activity.LoginActivity;
import com.test.bank.weight.dialog.CommonDialogFragment;
import com.test.bank.weight.dialog.ProgressDialogFragment;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

public abstract class CommonActivity extends AppCompatActivity implements IBaseView, EasyPermissions.PermissionCallbacks {
    protected static final int RC_PERM = 123;
    protected String mClassName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getInstance().pushActivity(this);
        mClassName = getClass().getName();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isCountPage()) {     //如果Activity中包含多个Fragment，则该方法需在Fragment中调用
            MobclickAgent.onPageStart(mClassName);
        }
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isCountPage()) {     //如果Activity中包含多个Fragment，则该方法需在Fragment中调用
            MobclickAgent.onPageEnd(mClassName);
        }
        MobclickAgent.onPause(this);
    }


    /**
     * 该Activity中是否包含Fragment【默认为true】
     * 当该Activity包含多个fragment时重写并返回false
     *
     * @return
     */
    protected boolean isCountPage() {
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.getInstance().popActivity(this);
    }


    /**
     * 权限回调接口
     */
    private CheckPermListener mListener;

    public interface CheckPermListener {
        //权限通过后的回调方法
        void superPermission();
    }

    public void checkPermissions(CheckPermListener listener, int resString, String... mPerms) {
        mListener = listener;
        if (EasyPermissions.hasPermissions(this, mPerms)) {
            if (mListener != null)
                mListener.superPermission();
        } else {
            EasyPermissions.requestPermissions(this, getString(resString),
                    RC_PERM, mPerms);
        }
    }

    /**
     * 用户权限处理,
     * 如果全部获取, 则直接过.
     * 如果权限缺失, 则提示Dialog.
     *
     * @param requestCode  请求码
     * @param permissions  权限
     * @param grantResults 结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        //同意了某些权限可能不是全部
    }

    @Override
    public void onPermissionsAllGranted() {
        if (mListener != null)
            mListener.superPermission();//同意了全部权限的回调
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

        EasyPermissions.checkDeniedPermissionsNeverAskAgain(this,
                "当前应用缺少必要权限。请点击\"设置\" - \"权限\" - 打开所需权限。最后点击两次后退按钮，即可返回。",
                R.string.permission_setting,
                R.string.permissio_cancel,
                null, perms);
    }

    /**
     * 点击空白区域隐藏键盘.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (getCurrentFocus() != null) {
                LogUtils.e("onTouchEvent: " + getCurrentFocus().toString() + " token: " + getCurrentFocus().getWindowToken().toString());
                if (getCurrentFocus().getWindowToken() != null) {
                    imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            } else {
                LogUtils.e("getCurrentFocus is null....");
            }
        }
        return super.onTouchEvent(event);
    }

    public static ProgressDialogFragment progressDialogFragment;
    public volatile static boolean isShowing = false;

    /**
     * 展示加载等待提示
     */
    @Override
    public void showProgressDialog() {
        if (progressDialogFragment == null) {
            progressDialogFragment = new ProgressDialogFragment();
            LogUtils.e("showProgressDialog --> reCreate..." + progressDialogFragment.toString());
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        progressDialogFragment = (ProgressDialogFragment) getSupportFragmentManager().findFragmentByTag("progressDialogFragment");
        if (!isShowing) {
            isShowing = true;
            LogUtils.e("showProgressDialog: " + progressDialogFragment.toString());
            fragmentTransaction.add(progressDialogFragment, "progressDialogFragment");
            fragmentTransaction.commitAllowingStateLoss();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        onStateNotSaved();
    }

    /**
     * 隐藏加载等待提示
     */
    @Override
    public void hideProgressDialog() {
        if (progressDialogFragment != null && isShowing) {
            LogUtils.e("hideProgressDialog: " + progressDialogFragment.toString());
            progressDialogFragment.dismiss();
            isShowing = false;
        }
    }


    @Override
    public void onForceUpdate() {

    }

    @Override
    public void onTokenInvalid() {
        //弹出token失效框，重新登录
        NetUtil.setJPushAliasAndTags("");
        showCommonDialog("您的账户在另一设备登录", "重新登录", "", new CommonDialogFragment.OnLeftClickListener() {
            @Override
            public void onClickLeft() {
                SPUtil.getInstance().clearUserInfo();
                LoginActivity.open(CommonActivity.this, LoginActivity.KEY_TOKEN_INVALID_GO_MAIN);
            }
        }, null, true, false);
    }

    /**
     * @param content
     * @param leftTxt
     * @param rightTxt
     * @param onLeftClickListener
     * @param onRightClickListener
     * @param isOneButton
     * @param isCanceledOnTouchOutside
     */
    public CommonDialogFragment showCommonDialog(String content, String leftTxt, String rightTxt, CommonDialogFragment.OnLeftClickListener onLeftClickListener, CommonDialogFragment.OnRightClickListener onRightClickListener, boolean isOneButton, boolean isCanceledOnTouchOutside) {
        FragmentTransaction mFragTransaction = getSupportFragmentManager().beginTransaction();
        CommonDialogFragment commonDialogFragment = (CommonDialogFragment) getSupportFragmentManager().findFragmentByTag("commondialog");
        if (commonDialogFragment == null) {
            commonDialogFragment = new CommonDialogFragment();
            commonDialogFragment.setContent(content);
            commonDialogFragment.setLeftTxt(leftTxt);
            commonDialogFragment.setRightTxt(rightTxt);
            commonDialogFragment.setOnLeftClickListener(onLeftClickListener);
            commonDialogFragment.setOneButtonEnable(isOneButton);
            commonDialogFragment.setOnRightClickListener(onRightClickListener);
            commonDialogFragment.setCancelable(isCanceledOnTouchOutside);
            commonDialogFragment.setIsCanceledOnTouchOutside(isCanceledOnTouchOutside);
        }
        if (!commonDialogFragment.isAdded()) {
            mFragTransaction.add(commonDialogFragment, "dialogFragment");
            mFragTransaction.commitAllowingStateLoss();
        }
        return commonDialogFragment;
    }


}
