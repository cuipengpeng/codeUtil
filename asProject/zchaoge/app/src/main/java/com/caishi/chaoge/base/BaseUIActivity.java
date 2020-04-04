package com.caishi.chaoge.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caishi.chaoge.R;
import com.caishi.chaoge.utils.StatusBarUtil;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.umeng.message.PushAgent;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
 * 描    述：<br>
 */
public abstract class BaseUIActivity extends RxAppCompatActivity implements IBaseView {
    public static String TAG = BaseUIActivity.class.getName();

    @BindView(R.id.rl_base_titleBar)
    protected RelativeLayout baseTitleBarRelativeLayout;
    @BindView(R.id.rl_base_back)
    protected RelativeLayout baseBackRelativeLayout;
    @BindView(R.id.iv_base_back)
    protected ImageView baseBackImageView;
    @BindView(R.id.tv_base_rightMenu)
    protected TextView baseRightMenuTextView;
    @BindView(R.id.iv_base_rightMenu)
    protected ImageView baseRightMenuImageView;
    @BindView(R.id.tv_base_title)
    protected TextView baseTitleTextView;
    @BindView(R.id.tv_base_back)
    protected TextView baseBackTextView;
    @BindView(R.id.v_base_statusBar)
    protected View v_base_statusBar;

    @BindView(R.id.ll_baseActivity_noDataView)
    protected LinearLayout baseNoDataViewLinearLayout;
    @BindView(R.id.ll_baseActivity_netwrokErrorView)
    protected LinearLayout baseNetwrokErrorViewLinearLayout;

    RelativeLayout replaceViewLinearLayout;
    private View subScreenView = null;
    private int subLayoutId = 0;
    protected boolean showBaseUITitle = true;
    public BaseUIActivity mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        subLayoutId = getSubLayoutId();
        if (subLayoutId != 0) {
            subScreenView = LayoutInflater.from(this).inflate(R.layout.activity_base_ui, null);
            View contentView = LayoutInflater.from(this).inflate(subLayoutId, null);
            replaceViewLinearLayout = subScreenView.findViewById(R.id.ll_baseActivity_contentView);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            replaceViewLinearLayout.addView(contentView, layoutParams);
        } else {
            subScreenView = getContentView();
        }

        if (subLayoutId == 0 && subScreenView == null) {
            throw new IllegalArgumentException("layoutResId error: " + subLayoutId + " -- Or subScreenView is illegal: subScreenView = " + subScreenView);
        }
        setContentView(subScreenView);
        ButterKnife.bind(this, subScreenView);
        hideAllViews();
//        showContentView();
//        mDialog = new LoadingAlertDialog(this);
        TAG = this.getClass().getName();
        mContext = this;
        PushAgent.getInstance(mContext).onAppStart();
        StatusBarUtil.translucentStatusBar(this);
        StatusBarUtil.setStatusBarTextColorStyle(this, true);
         LinearLayout.LayoutParams viewLayoutParams = (LinearLayout.LayoutParams) v_base_statusBar.getLayoutParams();
         viewLayoutParams.height = StatusBarUtil.getStatusBarHeight(this);
         v_base_statusBar.setLayoutParams(viewLayoutParams);

        initLocalDataView();
        initTitleBar();
        initPageData();
        initPageSetting();
    }


    protected View getContentView() {
        return null;
    }

    protected void initLocalDataView() {
    }

    private void initPageSetting() {
        if (!showBaseUITitle) {
            baseTitleBarRelativeLayout.setVisibility(View.GONE);
            v_base_statusBar.setVisibility(View.GONE);
        }

    }

    protected void initTitleBar() {
        setBackListener();
        setPageTitle();
        setRightMenu();
    }

    protected void setBackListener() {
        //        TextView baseBackImageView = subScreenView.findViewById(R.id.tv_base_back);
        baseBackRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    protected void setPageTitle() {
        baseTitleTextView.setText(getPageTitle());
    }

    protected void setRightMenu() {
    }

    protected abstract String getPageTitle();

    protected abstract int getSubLayoutId();

    protected abstract void initPageData();

    public void hideAllViews() {
        replaceViewLinearLayout.setVisibility(View.GONE);
        baseNetwrokErrorViewLinearLayout.setVisibility(View.GONE);
        baseNoDataViewLinearLayout.setVisibility(View.GONE);
    }

    @Override
    public void showContentView() {
        replaceViewLinearLayout.setVisibility(View.VISIBLE);
        baseNetwrokErrorViewLinearLayout.setVisibility(View.GONE);
        baseNoDataViewLinearLayout.setVisibility(View.GONE);
    }

    @Override
    public void showNoDataView() {
        replaceViewLinearLayout.setVisibility(View.GONE);
        baseNetwrokErrorViewLinearLayout.setVisibility(View.GONE);
        baseNoDataViewLinearLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void showNetworkErrorView() {
        replaceViewLinearLayout.setVisibility(View.GONE);
        baseNetwrokErrorViewLinearLayout.setVisibility(View.VISIBLE);
        baseNoDataViewLinearLayout.setVisibility(View.GONE);
    }

    @Override
    public void showProgressDialog() {

    }

    @Override
    public void hideProgressDialog() {

    }

    @Override
    public void onForceUpdate() {

    }

    @Override
    public void onTokenInvalid() {

    }

    public void open(Context context, Class clazz) {
        context.startActivity(new Intent(context, clazz));
    }

    public void open(Class clazz) {
        mContext.startActivity(new Intent(mContext, clazz));
    }
}
