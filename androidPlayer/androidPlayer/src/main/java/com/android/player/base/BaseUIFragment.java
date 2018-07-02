package com.android.player.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.player.R;

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

    @BindView(R.id.ll_baseActivity_noDataView)
    protected LinearLayout noDataViewLinearLayout;
    @BindView(R.id.ll_baseActivity_netwrokErrorView)
    protected LinearLayout netwrokErrorViewLinearLayout;


    private View subScreenView = null;
    LinearLayout replaceViewLinearLayout;
    protected boolean showBaseUITitle = false;
    public BaseUIActivity mContext;
    private int subLayoutId = 0;

    protected String mClassName;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mClassName = getClass().getName();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        subLayoutId = getSubLayoutId();
        if (subLayoutId != 0) {
            subScreenView = LayoutInflater.from(getActivity()).inflate(R.layout.activity_base_ui, null);
            View contentView = LayoutInflater.from(getActivity()).inflate(subLayoutId, null);
            replaceViewLinearLayout = subScreenView.findViewById(R.id.ll_baseActivity_contentView);
            replaceViewLinearLayout.addView(contentView);
        } else {
            subScreenView = getContentView();
        }

        if (subLayoutId == 0 && subScreenView == null) {
            throw new IllegalArgumentException("layoutResId error: " + subLayoutId + " -- Or subScreenView is illegal: subScreenView = " + subScreenView);
        }


//        View subScreenView = LayoutInflater.from(getActivity()).inflate(R.layout.activity_base_ui, null);
//        View contentView = LayoutInflater.from(getActivity()).inflate(subLayoutId, null);
//        replaceViewLinearLayout = subScreenView.findViewById(R.id.ll_baseActivity_contentView);
//        replaceViewLinearLayout.addView(contentView);
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this, subScreenView);
        hideAllViews();
//        showContentView();
        TAG = this.getClass().getName();
        return subScreenView;
    }

    protected View getContentView() {
        return null;
    }


    @Override
    public void onStart() {
        super.onStart();
        initTitleBar();
        initPageData();
        initPageSetting();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isCountPage() && !TextUtils.isEmpty(mClassName)) {
//            MobclickAgent.onPageStart(mClassName);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isCountPage() && !TextUtils.isEmpty(mClassName)) {
//            MobclickAgent.onPageEnd(mClassName);
        }
    }

    /**
     * 是否统计该页面，默认false，只有该Activity包含多个fragment时重写该方法并返回true
     *
     * @return
     */
    protected boolean isCountPage() {
        return false;
    }

    protected abstract String getPageTitle();

    protected abstract int getSubLayoutId();

    protected abstract void initPageData();


    private void initPageSetting() {
        if (!showBaseUITitle) {
            titleBarRelativeLayout.setVisibility(View.GONE);
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
        replaceViewLinearLayout.setVisibility(View.GONE);
        netwrokErrorViewLinearLayout.setVisibility(View.GONE);
        noDataViewLinearLayout.setVisibility(View.GONE);
    }

    @Override
    public void showContentView() {
        replaceViewLinearLayout.setVisibility(View.VISIBLE);
        netwrokErrorViewLinearLayout.setVisibility(View.GONE);
        noDataViewLinearLayout.setVisibility(View.GONE);
    }

    @Override
    public void showNoDataView() {
        replaceViewLinearLayout.setVisibility(View.GONE);
        netwrokErrorViewLinearLayout.setVisibility(View.GONE);
        noDataViewLinearLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void showNetworkErrorView() {
        replaceViewLinearLayout.setVisibility(View.GONE);
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

}
