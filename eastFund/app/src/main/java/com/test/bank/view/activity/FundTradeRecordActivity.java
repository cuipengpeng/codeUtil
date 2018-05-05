package com.test.bank.view.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.test.bank.R;
import com.test.bank.base.BaseActivity;
import com.test.bank.base.BaseFragment;
import com.test.bank.utils.DensityUtil;
import com.test.bank.utils.LogUtils;
import com.test.bank.utils.UIUtils;
import com.test.bank.view.fragment.FundRecordFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class FundTradeRecordActivity extends BaseActivity {

    @BindView(R.id.tv_fundRecord_all)
    TextView tvAll;
    @BindView(R.id.tv_fundRecord_processing)
    TextView tvProcessing;
    @BindView(R.id.view_fundRecord_bottom)
    View vBottom;
    @BindView(R.id.viewPager_fundRecord)
    ViewPager viewPager;

    List<BaseFragment> fragmentList;

    FundRecordFragmentAdapter pagerAdapter;

    @Override
    protected void init() {
        fragmentList = new ArrayList<>();
        fragmentList.add(FundRecordFragment.newInstance(true));
        fragmentList.add(FundRecordFragment.newInstance(false));

        pagerAdapter = new FundRecordFragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        viewPager.setCurrentItem(0);
        resetBottomLinePosition();
        initListener();
    }

    private void initListener() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                resetBottomLinePosition();
                tvAll.setTextColor(ContextCompat.getColor(FundTradeRecordActivity.this, position == 0 ? R.color.color_f35857 : R.color.color_b9bbca));
                tvProcessing.setTextColor(ContextCompat.getColor(FundTradeRecordActivity.this, position == 1 ? R.color.color_f35857 : R.color.color_b9bbca));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @OnClick({R.id.tv_fundRecord_all, R.id.tv_fundRecord_processing})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_fundRecord_all:
                if (viewPager.getCurrentItem() != 0) {
                    viewPager.setCurrentItem(0);
                    resetBottomLinePosition();
                }
                break;
            case R.id.tv_fundRecord_processing:
                if (viewPager.getCurrentItem() != 1) {
                    viewPager.setCurrentItem(1);
                    resetBottomLinePosition();
                }
                break;
        }
    }

    private int screenWidth = DensityUtil.getScreenWidth();
    float vWidth = DensityUtil.dip2px(17);

    private void resetBottomLinePosition() {
        if (viewPager.getCurrentItem() == 0) {
            float textWidth = UIUtils.measureTextWidth(tvAll, "全部");
            float offset = Math.abs(textWidth - vWidth) / 2;
            float leftMargin = (screenWidth / 2 - textWidth) / 2 + offset;
            LogUtils.e(" 0 leftMargin: " + leftMargin + " offset: " + offset);
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) vBottom.getLayoutParams();
            layoutParams.leftMargin = (int) leftMargin;
            vBottom.setLayoutParams(layoutParams);
        } else if (viewPager.getCurrentItem() == 1) {
            float textWidth = UIUtils.measureTextWidth(tvAll, "进行中");
            float offset = Math.abs(textWidth - vWidth) / 2;
            float leftMargin = screenWidth / 2 + (screenWidth / 2 - textWidth) / 2 + offset;
            LogUtils.e(" 1 leftMargin: " + leftMargin + " offset: " + offset);
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) vBottom.getLayoutParams();
            layoutParams.leftMargin = (int) leftMargin;
            vBottom.setLayoutParams(layoutParams);
        }
    }

    class FundRecordFragmentAdapter extends FragmentPagerAdapter {

        public FundRecordFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_fund_record;
    }

    @Override
    protected void doBusiness() {

    }

    @Override
    protected boolean isCountPage() {
        return false;
    }

    public static void open(Context context) {
        context.startActivity(new Intent(context, FundTradeRecordActivity.class));
    }
}
