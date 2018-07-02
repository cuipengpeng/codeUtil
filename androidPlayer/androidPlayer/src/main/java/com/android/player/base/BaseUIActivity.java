package com.android.player.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.player.R;

import butterknife.BindView;
import butterknife.ButterKnife;


public abstract class BaseUIActivity extends FragmentActivity implements IBaseView {
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

    @BindView(R.id.ll_baseActivity_noDataView)
    protected LinearLayout baseNoDataViewLinearLayout;
    @BindView(R.id.ll_baseActivity_netwrokErrorView)
    protected LinearLayout baseNetwrokErrorViewLinearLayout;

    LinearLayout replaceViewLinearLayout;
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
            replaceViewLinearLayout.addView(contentView);
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
        TAG = this.getClass().getName();
//        mDialog = new LoadingAlertDialog(this);
        mContext = this;

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
}
