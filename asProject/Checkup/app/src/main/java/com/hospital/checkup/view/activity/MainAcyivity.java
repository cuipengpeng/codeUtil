package com.hospital.checkup.view.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hospital.checkup.R;
import com.hospital.checkup.base.BaseUILocalDataActivity;
import com.hospital.checkup.http.HttpRequest;
import com.hospital.checkup.view.fragment.WebViewFragment;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.OnClick;

public class MainAcyivity extends BaseUILocalDataActivity {

    @BindView(R.id.iv_mainActivity_bottom01)
    ImageView ivMainActivityBottom01;
    @BindView(R.id.iv_mainActivity_bottom02)
    ImageView ivMainActivityBottom02;
    @BindView(R.id.iv_mainActivity_bottom03)
    ImageView ivMainActivityBottom03;
    @BindView(R.id.rl_mainActivity_bottom)
    RelativeLayout rlMainActivityBottom;

    private WebViewFragment bottomMenuFragment01;
    private WebViewFragment bottomMenuFragment02;
    private WebViewFragment bottomMenuFragment03;
    private FragmentManager fragmentManager;
    public static final int BOTTOM_FRAGMENT01 = 1001;
    public static final int BOTTOM_FRAGMENT02 = 1002;
    public static final int BOTTOM_FRAGMENT03 = 1003;

    @OnClick({R.id.iv_mainActivity_bottom01, R.id.iv_mainActivity_bottom02, R.id.iv_mainActivity_bottom03})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_mainActivity_bottom01:
                showFragment(BOTTOM_FRAGMENT01);
                break;
            case R.id.iv_mainActivity_bottom02:
                showFragment(BOTTOM_FRAGMENT02);
                break;
            case R.id.iv_mainActivity_bottom03:
                showFragment(BOTTOM_FRAGMENT03);
                break;
        }
    }

    @Override
    protected String getPageTitle() {
        return "";
    }

    @Override
    protected int getSubLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    protected void initPageData() {
        showBaseUITitle = false;
        initFragment();
    }

    private void initFragment() {
        fragmentManager = getSupportFragmentManager();
        bottomMenuFragment01 = WebViewFragment.newInstance(HttpRequest.H5_ADD_DOCTOR);
        bottomMenuFragment02 = WebViewFragment.newInstance(HttpRequest.H5_RECORD);
        bottomMenuFragment03 = WebViewFragment.newInstance(HttpRequest.H5_SETTING);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.frame_layout, bottomMenuFragment01);
        fragmentTransaction.add(R.id.frame_layout, bottomMenuFragment02);
        fragmentTransaction.add(R.id.frame_layout, bottomMenuFragment03);
        fragmentTransaction.commit();

        showFragment(BOTTOM_FRAGMENT01);
    }

    public void showFragment(int id) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.hide(bottomMenuFragment01);
        fragmentTransaction.hide(bottomMenuFragment02);
        fragmentTransaction.hide(bottomMenuFragment03);
        switch (id) {
            case BOTTOM_FRAGMENT01:
                fragmentTransaction.show(bottomMenuFragment01);
                break;
            case BOTTOM_FRAGMENT02:
                fragmentTransaction.show(bottomMenuFragment02);
                break;
            case BOTTOM_FRAGMENT03:
                fragmentTransaction.show(bottomMenuFragment03);
                break;
        }
        fragmentTransaction.commit();
    }

}
