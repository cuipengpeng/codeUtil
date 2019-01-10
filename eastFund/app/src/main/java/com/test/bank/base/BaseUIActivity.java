package com.test.bank.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.test.bank.R;
import com.test.bank.inter.OnResponseListener;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;


public abstract class BaseUIActivity extends FragmentActivity implements IBaseView{
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

    RelativeLayout replaceViewRelativeLayout;
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
            replaceViewRelativeLayout = subScreenView.findViewById(R.id.ll_baseActivity_contentView);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            replaceViewRelativeLayout.addView(contentView, layoutParams);
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
        mContext = this;
//        mDialog = new LoadingAlertDialog(this);

        initLocalDataView();
        initTitleBar();
        initPageData();
        initPageSetting();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isCountPage()) {
            MobclickAgent.onPageStart(TAG);
            MobclickAgent.onResume(this); //统计时长
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isCountPage()) {
            MobclickAgent.onPageEnd(TAG);//必须保证 onPageEnd 在 onPause 之前调用，因为SDK会在 onPause 中保存onPageEnd统计到的页面数据。
            MobclickAgent.onPause(this); //统计时长
        }
    }


    /**
     * 是否统计该页面，默认true. 若忽略统计该页面时，则重写该方法并返回false
     *
     * @return
     */
    protected boolean isCountPage() {
        return true;
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
        replaceViewRelativeLayout.setVisibility(View.GONE);
        baseNetwrokErrorViewLinearLayout.setVisibility(View.GONE);
        baseNoDataViewLinearLayout.setVisibility(View.GONE);
    }

    @Override
    public void showContentView() {
        replaceViewRelativeLayout.setVisibility(View.VISIBLE);
        baseNetwrokErrorViewLinearLayout.setVisibility(View.GONE);
        baseNoDataViewLinearLayout.setVisibility(View.GONE);
    }

    @Override
    public void showNoDataView() {
        replaceViewRelativeLayout.setVisibility(View.GONE);
        baseNetwrokErrorViewLinearLayout.setVisibility(View.GONE);
        baseNoDataViewLinearLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void showNetworkErrorView() {
        replaceViewRelativeLayout.setVisibility(View.GONE);
        baseNetwrokErrorViewLinearLayout.setVisibility(View.VISIBLE);
        baseNoDataViewLinearLayout.setVisibility(View.GONE);
    }

    @Override
    public void onRequestError(int errorCode, OnResponseListener onResponseListener) {
    }

    @Override
    public void onRefreshSuccess(OnResponseListener onResponseListener) {
    }

    @Override
    public void showProgressDialog() {
    }

    @Override
    public void hideProgressDialog() {
    }


    @Override
    public void onTokenInvalid() {
    }

    @Override
    public void onForceUpdate() {
    }

    public void open(Class activityClass){
        mContext.startActivity(new Intent(mContext, activityClass));
    }
}
