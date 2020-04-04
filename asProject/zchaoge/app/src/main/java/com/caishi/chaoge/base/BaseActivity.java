package com.caishi.chaoge.base;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caishi.chaoge.R;
import com.caishi.chaoge.http.Account;
import com.caishi.chaoge.ui.activity.MainActivity;
import com.caishi.chaoge.ui.widget.dialog.LoadDialog;
import com.caishi.chaoge.utils.ConstantUtils;
import com.caishi.chaoge.utils.KeyboardLayout;
import com.caishi.chaoge.utils.LogUtil;
import com.caishi.chaoge.utils.SPUtils;
import com.caishi.chaoge.utils.ToastUtils;
import com.caishi.chaoge.utils.Utils;
import com.gyf.barlibrary.ImmersionBar;
import com.rd.xpk.editor.modal.T;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;


public abstract class BaseActivity extends RxAppCompatActivity implements View.OnClickListener {


    private long exitTime;
    public boolean isBackVisible = true;
    public boolean isGoBack = true;//是否使用父类的返回
    /**
     * 日志输出标志
     **/
    protected final String TAG = this.getClass().getSimpleName();
    /**
     * 当前Activity渲染的视图View
     **/
    private View mContextView = null;
    private Fragment currentFragment;
    public BaseActivity mContext;
    private LoadDialog loadDialog;
    private int backgroundColor = Color.parseColor("#FFFFFF");
    private int saveTextColor = Color.parseColor("#FFFFFF");
    private String baseTitle = "";
    private String baseTitleSave = "";
    private boolean isCircle = false;//是否使用边框圆角矩形
    private boolean isCircleBack = false;//是否使用边框圆角返回
    private boolean isShowSave = true; //是否显示右侧按钮
    public TextView tv_baseTitle_save;

    private void initTitle() {
        RelativeLayout rl_baseTitle_view = $(R.id.rl_base_view);
        LinearLayout ll_baseTitle_back = $(R.id.ll_baseTitle_back);
        TextView tv_baseTitle_title = $(R.id.tv_baseTitle_title);
        tv_baseTitle_save = $(R.id.tv_baseTitle_save);
        ImageView img_baseTitle_back = $(R.id.img_baseTitle_back);
        if (rl_baseTitle_view != null)
            rl_baseTitle_view.setBackgroundColor(backgroundColor);
        if (img_baseTitle_back != null && isCircleBack) {
            ViewGroup.LayoutParams layoutParams =
                    img_baseTitle_back.getLayoutParams();
            layoutParams.width = (int) getResources().getDimension(R.dimen._32dp);
            layoutParams.height = (int) getResources().getDimension(R.dimen._32dp);
            img_baseTitle_back.setLayoutParams(layoutParams);
            img_baseTitle_back.setImageResource(R.drawable.ic_back_white);
        }
        if (tv_baseTitle_title != null)
            tv_baseTitle_title.setText(baseTitle);

        if (tv_baseTitle_save != null) {
            tv_baseTitle_save.setVisibility(isShowSave ? View.VISIBLE : View.GONE);
            tv_baseTitle_save.setText(baseTitleSave);
            tv_baseTitle_save.setTextColor(saveTextColor);
            tv_baseTitle_save.setOnClickListener(this);
        }
        if (isCircle && tv_baseTitle_save != null)
            tv_baseTitle_save.setBackgroundResource(R.drawable.shape_bg_save_circle);
        if (ll_baseTitle_back != null)
            ll_baseTitle_back.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = BaseActivity.this;
        PushAgent.getInstance(mContext).onAppStart();
        loadDialog = LoadDialog.newInstance();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
            initBundle(bundle);
        if (null == mContextView) {
            mContextView = LayoutInflater.from(this).inflate(bindLayout(), null);
        }
        setContentView(mContextView);
        ActivityManager.getInstance().pushActivity(this);
        initView(mContextView);
        initTitle();
        doBusiness();
        setListener();
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * 显示短Toast
     *
     * @param msg
     */
    protected void showToast(final String msg) {
        ToastUtils.show(BaseActivity.this, msg);
    }

    /**
     * 显示短自定义居中Toast
     *
     * @param msg
     */
    protected void showCentreToast(final String msg) {
        ToastUtils.showCentreToast(BaseActivity.this, msg);
    }

    /**
     * 取消居中Toast
     */
    protected void closeTosat() {
        ToastUtils.cancel();
    }

    /**
     * 显示加载框
     */
    protected void showLoadView() {
        if (loadDialog != null)
            loadDialog.showLoadDialog(this, "");
    }

    /**
     * 显示加载框
     */
    protected void showLoadView(String hint) {
        if (loadDialog != null)
            loadDialog.showLoadDialog(this, hint);
    }

    /**
     * 取消加载框
     */
    protected void closeDialog() {
        if (loadDialog != null)
            loadDialog.dismissDialog();
    }


    /**
     * 退出app
     */
    protected void exitBackKey() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            showToast("再按一次退出程序");
            exitTime = System.currentTimeMillis();
        } else {
            quitApp();
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            boolean isExit = intent.getBooleanExtra(ConstantUtils.TAG_EXIT, false);
            if (isExit) {
                this.finish();
            }
        }
    }

    /**
     * 退出应用
     */
    public void quitApp() {
        startActivity(new Intent(this, MainActivity.class).putExtra(ConstantUtils.TAG_EXIT, true));
    }

    /**
     * 退出登录
     */
    public void quitLogin() {
        this.finish();
    }


    /**
     * 初始化参数
     *
     * @param bundle
     */
    public void initBundle(Bundle bundle) {

    }


    /**
     * 绑定布局
     *
     * @returnvoid
     */
    public abstract int bindLayout();

    /**
     * 设置返回键是否显示
     *
     * @param isBackVisible
     */
    public void setIsBackVisible(boolean isBackVisible) {
        this.isBackVisible = isBackVisible;
    }

    /**
     * 初始化控件
     *
     * @param view
     */
    public abstract void initView(final View view);

    /**
     * 业务操作
     */
    public abstract void doBusiness();

    /**
     * 绑定控件
     *
     * @param resId
     * @return
     */
    protected <T extends View> T $(int resId) {
        return super.findViewById(resId);
    }

    /**
     * 设置监听
     */
    public abstract void setListener();

    @Override
    public void onClick(View v) {
        if (!Utils.isFastClick()) {
            switch (v.getId()) {
                case R.id.ll_baseTitle_back:
                    KeyboardLayout.hideInput(this);
                    onBackPressed();
                    return;
                case R.id.tv_baseTitle_save:
                    KeyboardLayout.hideInput(this);
                    onSaveClick(v);
                    return;
            }
            widgetClick(v);
        }
    }

    /**
     * View点击
     **/
    public abstract void widgetClick(View v);

    /**
     * 标题栏背景颜色 内容
     *
     * @param titleText title显示内容
     * @param color     标题栏背景颜色
     * @param saveText  右侧保存按钮显示内容
     */
    public void setBaseTitle(String titleText, int color, String saveText) {
        this.baseTitle = titleText;
        if (color != 0)
            this.backgroundColor = color;
        this.baseTitleSave = saveText;
    }

    /**
     * 标题栏背景颜色 内容
     *
     * @param titleText  title显示内容
     * @param isShowSave 是否显示保存按钮
     */
    public void setBaseTitle(String titleText, boolean isShowSave) {
        this.baseTitle = titleText;
        this.isShowSave = isShowSave;
    }

    /**
     * 标题栏背景颜色 内容
     *
     * @param titleText     title显示内容
     * @param color         标题栏背景颜色
     * @param saveText      右侧保存按钮显示内容
     * @param saveTextColor 右侧保存按钮字体颜色
     * @param isCircle      右侧保存按钮样式
     */
    public void setBaseTitle(String titleText, int color, String saveText, int saveTextColor, boolean isCircle) {
        this.baseTitle = titleText;
        if (color != 0)
            this.backgroundColor = color;
        if (saveTextColor != 0)
            this.saveTextColor = saveTextColor;
        this.baseTitleSave = saveText;
        this.isCircle = isCircle;
    }

    /**
     * 标题栏背景颜色 内容
     *
     * @param color        标题栏背景颜色
     * @param saveText     右侧保存按钮显示内容
     * @param isCircleBack 返回按钮是否使用圆形的
     */
    public void setBaseTitle(int color, String saveText, boolean isCircleBack) {
        this.backgroundColor = color;
        this.baseTitleSave = saveText;
        this.isCircleBack = isCircleBack;
    }

    /**
     * 右侧保存按钮点击事件
     */
    public void onSaveClick(View v) {

    }

    /**
     * 页面跳转
     *
     * @param clz
     */
    public void startActivity(Class<?> clz) {
        startActivity(new Intent(this, clz));
    }

    /**
     * 页面跳转finish
     *
     * @param clz class
     */
    public void startActivityFinish(Class<?> clz) {
        startActivity(new Intent(this, clz));
        finish();
    }


    /**
     * 携带数据的页面跳转
     *
     * @param clz
     * @param bundle
     */
    public void startActivity(Class<?> clz, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(this, clz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /**
     * 含有Bundle通过Class打开编辑界面
     *
     * @param cls
     * @param bundle
     * @param requestCode
     */
    public void startActivityForResult(Class<?> cls, Bundle bundle, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }

    // 键盘关闭
    public void closeInputSoft() {
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LogUtil.d(TAG, "onRestart()");
    }


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(this.getClass().getSimpleName()); //手动统计页面("SplashScreen"为页面名称，可自定义)
        MobclickAgent.onResume(this); //统计时长

        LogUtil.d(TAG, "onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(this.getClass().getSimpleName()); //手动统计页面("SplashScreen"为页面名称，可自定义)，必须保证 onPageEnd 在 onPause 之前调用，因为SDK会在 onPause 中保存onPageEnd统计到的页面数据。
        MobclickAgent.onPause(this);
        LogUtil.d(TAG, "onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.d(TAG, "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 必须调用该方法，防止内存泄漏
        ImmersionBar.with(this).destroy();
        LogUtil.d(TAG, "onDestroy()");
    }

    /**
     * fragemnt切换
     */
    public void switchFragment(Fragment targetFragment, int containerViewId) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
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

    /**
     * 是否登录
     *
     * @return true登录，false没有登录
     */
    public boolean isLogin() {
        return SPUtils.isLogin(mContext);
    }

    /**
     * 获取userID
     */
    public String getCGUserId() {
        return Account.sUserId;
    }

    /**
     * 获取userToken
     */
    public String getCredential() {
        return Account.sUserToken;
    }

}



