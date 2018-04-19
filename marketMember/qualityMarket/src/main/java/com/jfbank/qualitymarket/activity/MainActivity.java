package com.jfbank.qualitymarket.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.jfbank.qualitymarket.AppContext;
import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.base.BaseActivity;
import com.jfbank.qualitymarket.base.BaseFragment;
import com.jfbank.qualitymarket.callback.AsyncResponseCallBack;
import com.jfbank.qualitymarket.constants.EventBusConstants;
import com.jfbank.qualitymarket.dao.StoreService;
import com.jfbank.qualitymarket.fragment.CategoryFragment;
import com.jfbank.qualitymarket.fragment.DiscoverFragment;
import com.jfbank.qualitymarket.fragment.HomeFragment;
import com.jfbank.qualitymarket.fragment.MyAccountFragment;
import com.jfbank.qualitymarket.js.JsRequstInterface;
import com.jfbank.qualitymarket.listener.DialogListener;
import com.jfbank.qualitymarket.model.User;
import com.jfbank.qualitymarket.net.HttpRequest;
import com.jfbank.qualitymarket.service.FingerPrintService;
import com.jfbank.qualitymarket.util.AMapUtils;
import com.jfbank.qualitymarket.util.ActivityManager;
import com.jfbank.qualitymarket.util.CommonUtils;
import com.jfbank.qualitymarket.util.ConstantsUtil;
import com.jfbank.qualitymarket.util.DialogUtils;
import com.jfbank.qualitymarket.util.LogUtil;
import com.jfbank.qualitymarket.util.TDUtils;
import com.jfbank.qualitymarket.util.UnicornUtils;
import com.jfbank.qualitymarket.util.UpdateVersionUtils;
import com.jfbank.qualitymarket.util.UserUtils;
import com.qiyukf.nimlib.sdk.NimIntent;

import org.simple.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 主页面
 *
 * @author 崔朋朋
 */

public class MainActivity extends BaseActivity implements AMapLocationListener {
    /*
     *tabView 初始化
     */
    @InjectView(R.id.tv_mainActivity_01)
    TextView firstTextView;
    @InjectView(R.id.tv_mainActivity_02)
    TextView secondTextView;
    @InjectView(R.id.tv_mainActivity_03)
    TextView thirdTextView;
    @InjectView(R.id.tv_mainActivity_04)
    TextView fourthTextView;
    /**
     * 跳转参数
     */
    public static final String KEY_OF_BOTTOM_MENU = "bottomMenuKey";
    private String selectedBottomMenu = "";
    /*
     * Fragment申明
     */
    private FragmentManager mFragmentManager;//Fragment管理类
    private BaseFragment homeFragment;//首页
    private BaseFragment categoryFragment;//分类
    private BaseFragment borrowMoneyFragment;//发现
    private BaseFragment myaccountFragment;//我的
    ArrayList<TextView> tabTextView;
    /*
     * 定位
     */
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;
    public final int SELECTED_BOTTOM_MENU2 = 2001;
    /**
     * 定位回调Handle处理器
     */
    public Handler mHandler = new Handler() {
        public void dispatchMessage(android.os.Message msg) {
            switch (msg.what) {
                case AMapUtils.MSG_LOCATION_FINISH: // 定位完成
                    AMapLocation loc = (AMapLocation) msg.obj;
                    String result = AMapUtils.getLocationStr(loc);
                    AppContext.location.setLongitude(loc.getLongitude() + "");
                    AppContext.location.setLatitude(loc.getLatitude() + "");
                    LogUtil.printLog("高德定位：" + result);
                    break;
                case SELECTED_BOTTOM_MENU2://选中分类
                    showTab(2);
                    break;
            }
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        ActivityManager.getInstance().finishAllActivity();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAgent(false);
        ButterKnife.inject(this);

        //启动火眼sdk收集数据服务
        Intent intent =new Intent(this, FingerPrintService.class);
        startService(intent);

        tabTextView = new ArrayList<>();
        tabTextView.add(firstTextView);
        tabTextView.add(secondTextView);
        tabTextView.add(thirdTextView);
        tabTextView.add(fourthTextView);
        UpdateVersionUtils.checkVersionUpdate(this);//更新检测
        initLocation();//初始化定位功能
        checkPermission(this, ConstantsUtil.PERMISSION_WRITE_EXTERNAL_STORAGE_REQUEST_CODE);//请求存储权限
        mFragmentManager = getSupportFragmentManager();
        //设置状态栏的颜色
        CommonUtils.setStatusTransColor(this);
        initSelect();
    }

    @Override
    protected String getPageName() {
        return getString(R.string.str_pagename_main);
    }

    /**
     * 初始化定位功能
     */
    private void initLocation() {
        // 高德地图初始化
        locationClient = new AMapLocationClient(this.getApplicationContext());
        locationOption = new AMapLocationClientOption();
        // 设置定位模式为低功耗模式
        locationOption.setLocationMode(AMapLocationMode.Battery_Saving);
        // 设置定位监听
        locationClient.setLocationListener(this);
        // 设置是否需要显示地址信息
        locationOption.setNeedAddress(true);
        /**
         * 设置发送定位请求的时间间隔,最小值为1000，如果小于1000，按照1000算 只有持续定位设置定位间隔才有效，单次定位无效
         */
        // locationOption.setInterval(Long.valueOf(1000));
        locationOption.setOnceLocation(true);
        // 设置定位参数
        locationClient.setLocationOption(locationOption);
        // 启动定位
        locationClient.startLocation();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        initSelect();
    }

    /**
     * 初始化选中TabView和Fragment
     */
    private void initSelect() {
        Intent intent = getIntent();
        if (intent.hasExtra(NimIntent.EXTRA_NOTIFY_CONTENT)) {
            // 打开客服窗口
            UnicornUtils.startUnicorn(this);
            // 最好将intent清掉，以免从堆栈恢复时又打开客服窗口
//            setIntent(new Intent());
        } else {
            int selectPosition = 1;
            //用于通知事件的跳转
            selectedBottomMenu = getIntent().getStringExtra(KEY_OF_BOTTOM_MENU);
            if (MyAccountFragment.TAG.equals(selectedBottomMenu)) {//我的
                selectPosition = 4;
            } else if (DiscoverFragment.TAG.equals(selectedBottomMenu)) {//发现
                selectPosition = 3;
            } else if (CategoryFragment.TAG.equals(selectedBottomMenu)) {//分类
                selectPosition = 2;
            } else {//首页
                selectPosition = 1;
            }
            showTab(selectPosition);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        qualityUserShow();
    }

    @OnClick({R.id.tv_mainActivity_01, R.id.tv_mainActivity_02, R.id.tv_mainActivity_03, R.id.tv_mainActivity_04})
    public void onViewClick(View v) {
        switch (v.getId()) {
            case R.id.tv_mainActivity_01://首页
                showTab(1);
                break;
            case R.id.tv_mainActivity_02://分类
                showTab(2);
                break;
            case R.id.tv_mainActivity_03://发现
                showTab(3);
                break;
            case R.id.tv_mainActivity_04://我的
                showTab(4);
                break;
        }

    }


    /**
     * 选中显示那个界面
     *
     * @param position 1：首页  2：分类  3：发现  4：我的
     */
    private void showTab(int position) {
        selectTab(position - 1);
        if (position == 1) {//首页
            showFragent(1);
        } else if (position == 2) {//分类
            showFragent(2);
        } else if (position == 3) {//发现
            showFragent(3);
        } else if (position == 4) {//我的
            showFragent(4);
        }

    }

    /**
     * 选择切换fragment
     */
    public void showFragent(int position) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        /*
         *隐藏所有Fragment
         */
        if (homeFragment != null)//首页
            fragmentTransaction.hide(homeFragment);
        if (categoryFragment != null)//分类
            fragmentTransaction.hide(categoryFragment);
        if (borrowMoneyFragment != null)//发现
            fragmentTransaction.hide(borrowMoneyFragment);
        if (myaccountFragment != null)//我的
            fragmentTransaction.hide(myaccountFragment);

        /*
         *初始化Fragment或者显示选中Fragment
         */
        if (position == 1) {//首页
            if (homeFragment == null) {
                homeFragment = new HomeFragment();///new ProductFragment();
                fragmentTransaction.add(R.id.fragment_main_context, homeFragment);
            } else {
                fragmentTransaction.show(homeFragment);
            }
            TDUtils.onEvent(mContext, "100001", "首页", TDUtils.getInstance().putUserid().buildParams());
        } else if (position == 2) {//分类
            if (categoryFragment == null) {
                categoryFragment = new CategoryFragment();
                fragmentTransaction.add(R.id.fragment_main_context, categoryFragment);
            } else {
                fragmentTransaction.show(categoryFragment);
            }
            TDUtils.onEvent(mContext, "100001", "品类", TDUtils.getInstance().putUserid().buildParams());
        } else if (position == 3) {//发现
            if (borrowMoneyFragment == null) {
//                borrowMoneyFragment = new BorrowMoneyActivity();
                borrowMoneyFragment = new DiscoverFragment();
                fragmentTransaction.add(R.id.fragment_main_context, borrowMoneyFragment);
            } else {
                fragmentTransaction.show(borrowMoneyFragment);
            }
            TDUtils.onEvent(mContext, "100001", "发现", TDUtils.getInstance().putUserid().buildParams());
        } else if (position == 4) {//我的
            if (myaccountFragment == null) {
                myaccountFragment = new MyAccountFragment();
                if (AppContext.isLogin) {
                    ((MyAccountFragment) myaccountFragment).getCreditLines(null);
                }
                fragmentTransaction.add(R.id.fragment_main_context, myaccountFragment);
            } else {
                fragmentTransaction.show(myaccountFragment);
            }
            TDUtils.onEvent(mContext, "100001", "我的", TDUtils.getInstance().putUserid().buildParams());
        }
        fragmentTransaction.commit();
    }


    public static void checkPermission(Activity activity, int requestCode) {
        String permissionName = "";
        if (requestCode == ConstantsUtil.PERMISSION_WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            permissionName = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        } else if (requestCode == ConstantsUtil.PERMISSION_CAMERA_REQUEST_CODE) {
            permissionName = Manifest.permission.CAMERA;
        } else if (requestCode == ConstantsUtil.PERMISSION_LOCATION_REQUEST_CODE) {
            //gps定位权限
            permissionName = Manifest.permission.ACCESS_FINE_LOCATION;
        }

        if (ContextCompat.checkSelfPermission(activity, permissionName) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{permissionName}, requestCode);
        }
    }

    public static void showDenyPermissionDialog(Activity activity, int requestCode, int[] grantResults) {
        String permissionName = "相关";
        switch (requestCode) {
            case ConstantsUtil.PERMISSION_WRITE_EXTERNAL_STORAGE_REQUEST_CODE:
                permissionName = "存储";
                break;
            case ConstantsUtil.PERMISSION_CAMERA_REQUEST_CODE:
                permissionName = "照相机";
                break;
            case ConstantsUtil.PERMISSION_LOCATION_REQUEST_CODE:
                permissionName = "访问位置";
                break;
        }

        String appName = activity.getResources().getString(R.string.app_name);
        String prompt = "您拒绝了" + appName + "的" + permissionName + "权限。 使用该功能，请先在系统管理中打开该权限";
        if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            JsRequstInterface.popupDialog(activity, prompt, ConstantsUtil.PERMISSION_WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        // super.onSaveInstanceState(outState);
        // //将这一行注释掉，阻止activity保存fragment的状态
    }

    @SuppressLint("NewApi")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {
            showDenyPermissionDialog(this, requestCode, grantResults);
        }
    }

    /**
     * 刷新userKey，用于判断是否显示发现
     */
    private void qualityUserShow() {
        if (AppContext.user == null || !AppContext.isLogin) {
            return;
        }
        Map<String,String> params = new HashMap<>();
        params.put("uid", AppContext.user.getUid());
        params.put("token", AppContext.user.getToken());
        HttpRequest.post(mContext, HttpRequest.QUALITY_MARKET_WEB_URL + HttpRequest.QUALITYUSERSHOW, params,
                new AsyncResponseCallBack() {

                    @Override
                    public void onResult(String arg2) {
                        String jsonStr = new String(arg2);
                        JSONObject jsonObject = JSON.parseObject(jsonStr);
                        if (ConstantsUtil.RESPONSE_SUCCEED == jsonObject
                                .getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
                            Log.e("ssss", jsonStr);
                            User userInfoBean = JSON.parseObject(jsonObject
                                            .getJSONObject(ConstantsUtil.RESPONSE_DATA_JSON_ARRAY_FIELD_NAME).toJSONString(),
                                    User.class);
                            if (AppContext.user != null && AppContext.isLogin) {//不为空，则做处理
                                AppContext.user.setUserKey(userInfoBean.getUserKey());
                                AppContext.user.setImmediateRepayment(userInfoBean.getImmediateRepayment());
                                AppContext.user.setUname(userInfoBean.getUname());
                                AppContext.user.setNickName(userInfoBean.getNickName());
                                AppContext.user.setChannel(userInfoBean.getChannel());
                                AppContext.user.setMobile(userInfoBean.getMobile());
                                AppContext.user.setClientId(userInfoBean.getClientId());
                                AppContext.user.setCreditLine(userInfoBean.getCreditLine());
                                AppContext.user.setEnchashmentLine(userInfoBean.getEnchashmentLine());
                                AppContext.user.setLoginTime(userInfoBean.getLoginTime());
                                AppContext.user.setRealNameUrl(userInfoBean.getRealNameUrl());
                                AppContext.user.setBillDate(userInfoBean.getBillDate());
                                AppContext.user.setIdName(userInfoBean.getIdName());
                                AppContext.user.setIdNumber(userInfoBean.getIdNumber());
                                AppContext.user.setIsContact(userInfoBean.getIsContact());
                                AppContext.user.setChannelName(userInfoBean.getChannelName());
                                new StoreService(mContext).saveUserInfo(AppContext.user);//退出应用前保存数据
                                EventBus.getDefault().post(new Object(), EventBusConstants.EVENTT_UPDATE_BILLENTER_STATUS);
                            }
                        }else if (ConstantsUtil.RESPONSE_TOKEN_FAIL ==jsonObject.getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
                            UserUtils.tokenFailDialog(mContext, jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME), null);
                        }
                    }

                    @Override
                    public void onFailed(String path, String msg) {
                    }
                });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppContext.clearWebViewCache(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            DialogUtils.showTwoBtnDialog(this, null, "是否退出程序?", "取消", "确定", new DialogListener.DialogClickLisenter() {
                @Override
                public void onDialogClick(int type) {
                    if (type == CLICK_CANCEL) {

                    } else {
                        new StoreService(MainActivity.this).saveUserInfo(AppContext.user);//退出应用前保存数据
                        ActivityManager.getInstance().finishAllActivity();
                        AppContext.clearWebViewCache(MainActivity.this);
                        finish();
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                }
            });
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
            // String addressName = aMapLocation.getAddress();
            // dimension = (float) aMapLocation.getLatitude();
            // longitude = (float) aMapLocation.getLongitude();
            // PositionBean positionBean = new PositionBean(longitude + "",
            // dimension + "", addressName);
            // StoreService.getParamsInstance().saveLatLong(positionBean);
        } else if (aMapLocation != null && aMapLocation.getErrorCode() == 22) { // 链接异常
            Toast.makeText(this, "连接异常", Toast.LENGTH_SHORT).show();
        } else if (aMapLocation != null && aMapLocation.getErrorCode() == 23) { // 链接超时
            Toast.makeText(this, "链接超时", Toast.LENGTH_SHORT).show();
        } else {
            Log.e("TAG", (aMapLocation == null) + ":::" + aMapLocation.getErrorCode()); // 09-12
            // 14:13:12.757:
            // E/TAG(28485):
            // false:::7
            // Toast.makeText(this, "定位错误,请检查您的网络", Toast.LENGTH_SHORT).show();
        }

        if (null != aMapLocation) {
            Message msg = mHandler.obtainMessage();
            msg.obj = aMapLocation;
            msg.what = AMapUtils.MSG_LOCATION_FINISH;
            mHandler.sendMessage(msg);
        }
    }


    /**
     * 检查当前网络是否可用
     *
     * @return
     */

    public static boolean isNetworkAvailable(Activity activity) {
        Context context = activity.getApplicationContext();
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        } else {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
            if (networkInfo != null && networkInfo.length > 0) {
                for (int i = 0; i < networkInfo.length; i++) {
                    // 判断当前网络状态是否为连接状态
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    /**
     * 设置tabView选中问题
     */
    private void selectTab(int position) {

        for (int i = 0; i < tabTextView.size(); i++) {
            if (position == i) {
                tabTextView.get(i).setSelected(true);
            } else {
                tabTextView.get(i).setSelected(false);
            }
        }
    }

}
