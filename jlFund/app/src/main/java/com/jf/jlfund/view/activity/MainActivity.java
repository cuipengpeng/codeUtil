package com.jf.jlfund.view.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jf.jlfund.R;
import com.jf.jlfund.base.BaseActivity;
import com.jf.jlfund.base.BaseApplication;
import com.jf.jlfund.http.OkHttpClientUtils;
import com.jf.jlfund.receiver.LockScreenReceiver;
import com.jf.jlfund.utils.DensityUtil;
import com.jf.jlfund.utils.LogUtils;
import com.jf.jlfund.utils.SPUtil;
import com.jf.jlfund.utils.ToastUtils;
import com.jf.jlfund.utils.VersionUpdateManager;
import com.jf.jlfund.view.fragment.MakeMoneyFragment;
import com.jf.jlfund.view.fragment.MyaccountFragment;
import com.jf.jlfund.view.fragment.OptionalFragment;
import com.jf.jlfund.weight.NoScrollViewPager;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {
    @BindView(R.id.viewPager_main)
    NoScrollViewPager viewPager;
    @BindView(R.id.ll_navi)
    LinearLayout llBottom;
    @BindView(R.id.tv_account)
    TextView tvAccount;
    @BindView(R.id.tv_makeMoney)
    TextView tvMakeMoney;
    @BindView(R.id.tv_yxj)
    TextView tvYXJ;
    @BindView(R.id.iv_main_robot)
    ImageView ivSmartServer;

    List<Fragment> fragmentList;
    private LockScreenReceiver lockScreenReceiver = new LockScreenReceiver();

    VersionUpdateManager versionUpdateManager;

    @Override
    protected void init() {
        ivSmartServer.post(new Runnable() {
            @Override
            public void run() {
                int screenHeight = DensityUtil.getScreenHeight();
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) ivSmartServer.getLayoutParams();
                int marginTop = (int) (screenHeight / 3.0 * 2);
                LogUtils.e("screenHeight: " + screenHeight + ", marginTop: " + marginTop);
                layoutParams.topMargin = marginTop;
                ivSmartServer.setLayoutParams(layoutParams);
            }
        });

        lockScreenReceiver.registerScreenActionReceiver(BaseApplication.applicationContext);

        initData();
        viewPager.setCurrentItem(1, false);
        updateTabStatus(1);
        initListener();
        versionUpdateManager = VersionUpdateManager.getInstance().checkVersionUpdate(this);
    }

    private void initListener() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                updateTabStatus(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public int index = -1;

    private void initData() {
        if (fragmentList == null) {
            fragmentList = new ArrayList<>();
        }
        fragmentList.add(MyaccountFragment.newInstance());
        fragmentList.add(MakeMoneyFragment.getInstance());
        fragmentList.add(OptionalFragment.newInstance(1));
        viewPager.setAdapter(new HomeViewPagerAdapter(getSupportFragmentManager()));
    }

    class HomeViewPagerAdapter extends FragmentPagerAdapter {

        public HomeViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return null == fragmentList ? 0 : fragmentList.size();
        }
    }

    public static boolean onFragmentVisibleToUserFlag = false;

    @OnClick({R.id.tv_account, R.id.tv_makeMoney, R.id.tv_yxj, R.id.iv_main_robot})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_account:
                if (viewPager.getCurrentItem() != 0) {
                    onFragmentVisibleToUserFlag = false;
                    viewPager.setCurrentItem(0, true);
                }
                break;
            case R.id.tv_makeMoney:
                if (viewPager.getCurrentItem() != 1) {
                    onFragmentVisibleToUserFlag = false;
                    viewPager.setCurrentItem(1, true);
                }
                break;
            case R.id.tv_yxj:
                if (viewPager.getCurrentItem() != 2) {
                    onFragmentVisibleToUserFlag = false;
                    viewPager.setCurrentItem(2, true);
                }
                break;
            case R.id.iv_main_robot:
                MobclickAgent.onEvent(this, "client_mainActivity_robot");
                if (!SPUtil.getInstance().isLogin()) {
                    LoginActivity.open(this, SmartServerActivity.class, true);
                } else {
                    SmartServerActivity.open(this);
                }
                break;
        }
    }

    private void updateTabStatus(int index) {
        tvAccount.setSelected(index == 0);
        tvAccount.setTextColor(ContextCompat.getColor(this, index == 0 ? R.color.color_4093ff : R.color.color_393b51));

        tvMakeMoney.setSelected(index == 1);
        tvMakeMoney.setTextColor(ContextCompat.getColor(this, index == 1 ? R.color.color_4093ff : R.color.color_393b51));

        tvYXJ.setSelected(index == 2);
        tvYXJ.setTextColor(ContextCompat.getColor(this, index == 2 ? R.color.color_4093ff : R.color.color_393b51));
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void doBusiness() {
    }

    public void setCurrentIndex(int index) {
        if (viewPager.getCurrentItem() == index) {
            return;
        }
        viewPager.setCurrentItem(index);
    }

    @Override
    protected boolean isCountPage() {
        return false;
    }

    long firstTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - firstTime > 2000) {
                ToastUtils.showShort("双击退出程序");
                firstTime = System.currentTimeMillis();
            } else {
                OkHttpClientUtils.cancelAllRequest();
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onDestroy() {
        lockScreenReceiver.unRegisterScreenActionReceiver(BaseApplication.applicationContext);
        super.onDestroy();
        if (progressDialogFragment != null) {
            progressDialogFragment.dismiss();
            progressDialogFragment = null;
        }
        if (versionUpdateManager != null) {
            versionUpdateManager.onActivityDestoryed();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        index = intent.getIntExtra(PARAM_INDEX, -1);
        LogUtils.e("zzzzzz", "onNewIntent.....index: " + index);
        if (index != viewPager.getCurrentItem() && index >= 0 && index <= 2) {
            viewPager.setCurrentItem(index);
        }
    }

    public final static String PARAM_INDEX = "param_index";

    public static void open(Context context, int index) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(PARAM_INDEX, index);
        context.startActivity(intent);
    }

}
