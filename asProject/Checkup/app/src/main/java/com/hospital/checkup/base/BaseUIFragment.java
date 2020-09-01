package com.hospital.checkup.base;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.hospital.checkup.R;

import butterknife.BindView;
import butterknife.ButterKnife;


public abstract class BaseUIFragment extends Fragment implements IBaseView {
    public static String TAG = BaseUIFragment.class.getName();
    @BindView(R.id.rl_base_titleBar)
    protected RelativeLayout titleBarRelativeLayout;
    @BindView(R.id.tv_base_rightMenu)
    protected TextView baseRightMenuTextView;
    @BindView(R.id.iv_base_rightMenu)
    protected ImageView baseRightMenuImageView;
    @BindView(R.id.tv_base_back)
    protected TextView baseBackTextView;
    @BindView(R.id.rl_base_back)
    protected RelativeLayout baseBackRelativeLayout;
    @BindView(R.id.iv_base_back)
    protected ImageView baseBackImageView;
    @BindView(R.id.tv_base_title)
    protected TextView titleTextView;
    @BindView(R.id.v_base_statusBar)
    protected View baseStatusBarView;

    @BindView(R.id.ll_baseActivity_noDataView)
    protected LinearLayout noDataViewLinearLayout;
    @BindView(R.id.ll_baseActivity_netwrokErrorView)
    protected LinearLayout netwrokErrorViewLinearLayout;


    private View subScreenView = null;
    RelativeLayout replaceViewRelativeLayout;
    protected boolean showBaseUITitle = false;
    public FragmentActivity mContext;
    private int subLayoutId = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        subLayoutId = getSubLayoutId();
        if (subLayoutId != 0) {
            subScreenView = LayoutInflater.from(getActivity()).inflate(R.layout.activity_base_ui, null);
            View contentView = LayoutInflater.from(getActivity()).inflate(subLayoutId, null);
            replaceViewRelativeLayout = subScreenView.findViewById(R.id.ll_baseActivity_contentView);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            replaceViewRelativeLayout.addView(contentView, layoutParams);
        } else {
            subScreenView = getContentView();
        }

        if (subLayoutId == 0 && subScreenView == null) {
            throw new IllegalArgumentException("layoutResId error: " + subLayoutId + " -- Or subScreenView is illegal: subScreenView = " + subScreenView);
        }

        super.onCreate(savedInstanceState);
        ButterKnife.bind(this, subScreenView);

        hideAllViews();
//        showContentView();
        TAG = this.getClass().getName();
        mContext = getActivity();

        initLocalDataView();
        initTitleBar();
        initPageData();
        initPageSetting();

        return subScreenView;
    }

    protected View getContentView() {
        return null;
    }

    protected void initLocalDataView() {
    }


    protected abstract String getPageTitle();

    protected abstract int getSubLayoutId();

    protected abstract void initPageData();


    private void initPageSetting() {
        if (!showBaseUITitle) {
            titleBarRelativeLayout.setVisibility(View.GONE);
            baseStatusBarView.setVisibility(View.GONE);
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
            public void onClick(View view) {
                getActivity().finish();
            }
        });
    }

    protected void setPageTitle() {
        titleTextView.setText(getPageTitle());
    }

    protected void setRightMenu() {

    }

    public void hideAllViews() {
        replaceViewRelativeLayout.setVisibility(View.GONE);
        netwrokErrorViewLinearLayout.setVisibility(View.GONE);
        noDataViewLinearLayout.setVisibility(View.GONE);
    }

    @Override
    public void showContentView() {
        replaceViewRelativeLayout.setVisibility(View.VISIBLE);
        netwrokErrorViewLinearLayout.setVisibility(View.GONE);
        noDataViewLinearLayout.setVisibility(View.GONE);
    }

    @Override
    public void showNoDataView() {
        replaceViewRelativeLayout.setVisibility(View.GONE);
        netwrokErrorViewLinearLayout.setVisibility(View.GONE);
        noDataViewLinearLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void showNetworkErrorView() {
        replaceViewRelativeLayout.setVisibility(View.GONE);
        netwrokErrorViewLinearLayout.setVisibility(View.VISIBLE);
        noDataViewLinearLayout.setVisibility(View.GONE);
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
