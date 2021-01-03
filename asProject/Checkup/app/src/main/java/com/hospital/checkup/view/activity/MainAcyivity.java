package com.hospital.checkup.view.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.hospital.checkup.R;
import com.hospital.checkup.base.BaseApplication;
import com.hospital.checkup.base.BaseUILocalDataActivity;
import com.hospital.checkup.bean.DoctorBean;
import com.hospital.checkup.bean.MeasurerDetailBean;
import com.hospital.checkup.http.HttpRequest;
import com.hospital.checkup.js.JsRequstInterface;
import com.hospital.checkup.utils.RunningDataSingleInstance;
import com.hospital.checkup.view.fragment.MeasureHomeFragment;
import com.hospital.checkup.view.fragment.WebViewFragment;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.OnClick;

public class MainAcyivity extends BaseUILocalDataActivity {

    @BindView(R.id.iv_mainActivity_bottom01)
    ImageView bottom01ImageView;
    @BindView(R.id.iv_mainActivity_bottom02)
    ImageView bottom02ImageView;
    @BindView(R.id.iv_mainActivity_bottom03)
    ImageView bottom03ImageView;
    @BindView(R.id.tv_mainActivity_bottom01)
    TextView bottom01TextView;
    @BindView(R.id.tv_mainActivity_bottom02)
    TextView bottom02TextView;
    @BindView(R.id.tv_mainActivity_bottom03)
    TextView bottom03TextView;
    @BindView(R.id.rl_mainActivity_bottom)
    RelativeLayout rlMainActivityBottom;

    private Fragment currentFragment;
    private Fragment bottomMenuFragment01;
    private Fragment bottomMenuFragment02;
    private Fragment bottomMenuFragment03;
    private FragmentManager fragmentManager;
    public static final String KEY_OF_BOTTOM_TAB = "bottomTabTypeKey";
    public static final int BOTTOM_FRAGMENT01 = 1001;
    public static final int BOTTOM_FRAGMENT02 = 1002;
    public static final int BOTTOM_FRAGMENT03 = 1003;
    private long lastBackPressed;
    private static final int QUIT_INTERVAL = 2000;

    @OnClick({R.id.ll_mainActivity_bottom01, R.id.ll_mainActivity_bottom02, R.id.ll_mainActivity_bottom03})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_mainActivity_bottom01:
                showFragment(BOTTOM_FRAGMENT01);
                break;
            case R.id.ll_mainActivity_bottom02:
                showFragment(BOTTOM_FRAGMENT02);
                break;
            case R.id.ll_mainActivity_bottom03:
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
        bottomMenuFragment01 = new MeasureHomeFragment();
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
        bottom01ImageView.setImageResource(R.mipmap.bottom01_normal);
        bottom02ImageView.setImageResource(R.mipmap.bottom02_normal);
        bottom03ImageView.setImageResource(R.mipmap.bottom03_normal);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.hide(bottomMenuFragment01);
        fragmentTransaction.hide(bottomMenuFragment02);
        fragmentTransaction.hide(bottomMenuFragment03);
        switch (id) {
            case BOTTOM_FRAGMENT01:
                bottom01ImageView.setImageResource(R.mipmap.bottom01_selected);
                fragmentTransaction.show(bottomMenuFragment01);
                currentFragment = bottomMenuFragment01;
                break;
            case BOTTOM_FRAGMENT02:
                bottom02ImageView.setImageResource(R.mipmap.bottom02_selected);
                fragmentTransaction.show(bottomMenuFragment02);
                currentFragment = bottomMenuFragment02;
                break;
            case BOTTOM_FRAGMENT03:
                bottom03ImageView.setImageResource(R.mipmap.bottom03_selected);
                fragmentTransaction.show(bottomMenuFragment03);
                currentFragment = bottomMenuFragment03;
                break;
        }
        fragmentTransaction.commit();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        showFragment(intent.getIntExtra(KEY_OF_BOTTOM_TAB, BOTTOM_FRAGMENT01));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        if(intent != null){
             String json = intent.getStringExtra(JsRequstInterface.KEY_OF_JSON_DATA);
             switch (resultCode){
                 case JsRequstInterface.TYPE_ADD_TESTER:
                      MeasurerDetailBean measurerDetailBean = JSON.parseObject(json, MeasurerDetailBean.class);
                      RunningDataSingleInstance.getInstance().setTestID(measurerDetailBean.getTestId());
                     ((MeasureHomeFragment)bottomMenuFragment01).addUserImageView.setVisibility(View.GONE);
                     ((MeasureHomeFragment)bottomMenuFragment01).measurerNameTextView.setText(measurerDetailBean.getTestName());
                     break;
                 case JsRequstInterface.TYPE_ADD_DOCTOR:
                     DoctorBean doctorBean = JSON.parseObject(json, DoctorBean.class);
                     RunningDataSingleInstance.getInstance().setOperatorID(doctorBean.getOperatorId());
                     ((MeasureHomeFragment)bottomMenuFragment01).addDoctorImageView.setVisibility(View.GONE);
                     ((MeasureHomeFragment)bottomMenuFragment01).doctorNameTextView.setText(doctorBean.getOperatorNameZh());
                     break;
             }
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    public void onBackPressed() {
        if(currentFragment instanceof WebViewFragment){
            ((WebViewFragment)currentFragment).onBack();
            return;
        }

        long backPressed = System.currentTimeMillis();
        if (backPressed - lastBackPressed > QUIT_INTERVAL) {
            lastBackPressed = backPressed;
            Toast.makeText(BaseApplication.applicationContext, "再按一次退出", Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }
    }

    private OnBackListener mOnBackListener;

    public void setOnBackListener(OnBackListener onBackListener){
        this.mOnBackListener = onBackListener;
    }

    public interface OnBackListener{
        void onBack();
    }

    public static void open(Context context, int tabType){
        Intent intent = new Intent(context, MainAcyivity.class);
        intent.putExtra(KEY_OF_BOTTOM_TAB, tabType);
        context.startActivity(intent);
    }
}
