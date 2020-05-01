package com.jfbank.qualitymarket.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.base.BaseActivity;
import com.jfbank.qualitymarket.fragment.MyOrderFragment;
import com.jfbank.qualitymarket.util.CommonUtils;
import com.viewpagerindicator.TabPageIndicator;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 我的订单页面
 *
 * @author 崔朋朋
 */
public class MyOrderActivity extends BaseActivity {
    @InjectView(R.id.vp_myOrderActivity_myOrder)
    ViewPager myOrderViewPager;
    @InjectView(R.id.tpi_myOrderActivity_titleIndicator)
    TabPageIndicator titleIndicator;
    @InjectView(R.id.rl_title)
    RelativeLayout rlTitle;
    @InjectView(R.id.tv_title)
    TextView tvTitle;
    public boolean init = true;
    private String orderId = "";
    public static final String KEY_OF_ORDER_ID = "orderIdKey";

    //	"全部订单", 0,
//	"待支付", 1,
//	"待发货", 2, 
//	"待收货", 3, 
//	"已完成", 4, 
//	"已取消, 5,
//	"已取消, 6,
//	"已支付",7,
//	"已取消",8
    private final String[] CONTENT = new String[]{"全部订单", "待支付", "待发货", "待收货", "已完成"};
    public static final String ORDER_STATUS_ALL_ORDERS = "0";
    public static final String ORDER_STATUS_WAIT_FOR_PAY = "1";
    public static final String ORDER_STATUS_WAIT_FOR_SEND_GOODS = "2";
    public static final String ORDER_STATUS_WAIT_FOR_TAKE_OVER_GOODS = "3";
    public static final String ORDER_STATUS_FINISHED = "4";
    public static final String ORDER_STATUS_CANCELED = "5";
    public static final String ORDER_STATUS_REFUSED = "6";
    public static final String ORDER_STATUS_HAS_PAIED = "7";
    public static final String ORDER_STATUS_AUTO_CANCELED = "8";
//    public static final String ORDER_STATUS_WAIT_FOR_LOAN = "10";

    public static final String KEY_OF_QUERY_ORDER = "queryOrderKey";
    //所有订单
    private Fragment allOrdersFragment;
    //待支付订单
    private Fragment waitForPayFragment;
    //待发货订单
    private Fragment waitForSendGoodsFragment;
    //待收货订单
    private Fragment waitForTakeOverGoodsFragment;
    //已完成订单
    private Fragment alreadyFinishedFragment;

    @OnClick({R.id.iv_back})
    public void onViewClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_my_order);
        ButterKnife.inject(this);
        FragmentPagerAdapter adapter = new MyOrderAdapter(getSupportFragmentManager());
        myOrderViewPager.setAdapter(adapter);
        titleIndicator.setViewPager(myOrderViewPager);

        allOrdersFragment = MyOrderFragment.newInstance(ORDER_STATUS_ALL_ORDERS);
        waitForPayFragment = MyOrderFragment.newInstance(ORDER_STATUS_WAIT_FOR_PAY);
        waitForSendGoodsFragment = MyOrderFragment.newInstance(ORDER_STATUS_WAIT_FOR_SEND_GOODS);
        waitForTakeOverGoodsFragment = MyOrderFragment.newInstance(ORDER_STATUS_WAIT_FOR_TAKE_OVER_GOODS);
        alreadyFinishedFragment = MyOrderFragment.newInstance(ORDER_STATUS_FINISHED);

        Intent intent = getIntent();
        String queryOrder = intent.getStringExtra(KEY_OF_QUERY_ORDER);
        if (ORDER_STATUS_WAIT_FOR_PAY.equals(queryOrder)) {
            titleIndicator.setCurrentItem(1);
        } else if (ORDER_STATUS_WAIT_FOR_SEND_GOODS.equals(queryOrder)) {
            titleIndicator.setCurrentItem(2);
        } else if (ORDER_STATUS_WAIT_FOR_TAKE_OVER_GOODS.equals(queryOrder)) {
            titleIndicator.setCurrentItem(3);
        }
        tvTitle.setText(R.string.str_pagename_myorder);
        CommonUtils.setTitle(this,rlTitle);
    }

    @Override
    protected String getPageName() {
        return getString(R.string.str_pagename_myorder);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null) {
            String FRAGMENTS_TAG = "Android:support:fragments";
            outState.remove(FRAGMENTS_TAG);
        }
    }

    /**
     * 转换订单状态
     *
     * @param orderStatus 服务器端返回的数字形式订单状态
     * @return 汉字形式的订单状态
     */
    public static String getOrderStatus(String orderStatus) {
        String status = "...";
//		"全部订单", 0,
//		"待支付", 1,
//		"待发货", 2,
//		"待收货", 3, 
//		"已完成", 4, 
//		"已取消, 5,
//		"已取消, 6,
//		"已支付",7,
//		"已取消",8
        if (ORDER_STATUS_WAIT_FOR_PAY.equals(orderStatus)) {
            status = "待支付";
        } else if (ORDER_STATUS_WAIT_FOR_SEND_GOODS.equals(orderStatus)) {
            status = "待发货";
        } else if (ORDER_STATUS_WAIT_FOR_TAKE_OVER_GOODS.equals(orderStatus)) {
            status = "待收货";
        } else if (ORDER_STATUS_FINISHED.equals(orderStatus)) {
            status = "已完成";
        } else if (ORDER_STATUS_CANCELED.equals(orderStatus) || ORDER_STATUS_REFUSED.equals(orderStatus) || ORDER_STATUS_AUTO_CANCELED.equals(orderStatus)) {
            status = "已取消";
        } else if (ORDER_STATUS_HAS_PAIED.equals(orderStatus)) {
            status = "已支付";
        }

        return status;
    }

    class MyOrderAdapter extends FragmentPagerAdapter {
        public MyOrderAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment selectedFragment = null;
            switch (position) {
                case 0:
                    selectedFragment = allOrdersFragment;
                    break;
                case 1:
                    selectedFragment = waitForPayFragment;
                    break;
                case 2:
                    selectedFragment = waitForSendGoodsFragment;
                    break;
                case 3:
                    selectedFragment = waitForTakeOverGoodsFragment;
                    break;
                case 4:
                    selectedFragment = alreadyFinishedFragment;
                    break;
            }
            return selectedFragment;
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return CONTENT[position % CONTENT.length];
        }

        @Override
        public int getCount() {
            return CONTENT.length;
        }
    }
}
