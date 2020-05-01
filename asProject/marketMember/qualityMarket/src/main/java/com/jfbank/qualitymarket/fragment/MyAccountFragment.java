package com.jfbank.qualitymarket.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfbank.qualitymarket.AppContext;
import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.activity.AccountInfoActivity;
import com.jfbank.qualitymarket.activity.AfterSalesActivity;
import com.jfbank.qualitymarket.activity.BankCardListActivity;
import com.jfbank.qualitymarket.activity.LoginActivity;
import com.jfbank.qualitymarket.activity.MainActivity;
import com.jfbank.qualitymarket.activity.MyOrderActivity;
import com.jfbank.qualitymarket.activity.MyRedPacketActivity;
import com.jfbank.qualitymarket.activity.PrepaymentActivity;
import com.jfbank.qualitymarket.adapter.BottomFunctionMenuAdapter;
import com.jfbank.qualitymarket.base.BaseFragment;
import com.jfbank.qualitymarket.callback.AsyncResponseCallBack;
import com.jfbank.qualitymarket.constants.EventBusConstants;
import com.jfbank.qualitymarket.dao.StoreService;
import com.jfbank.qualitymarket.model.BottomFunctionMenu;
import com.jfbank.qualitymarket.model.User;
import com.jfbank.qualitymarket.net.HttpRequest;
import com.jfbank.qualitymarket.util.CommonUtils;
import com.jfbank.qualitymarket.util.ConstantsUtil;
import com.jfbank.qualitymarket.util.JumpUtil;
import com.jfbank.qualitymarket.util.LogUtil;
import com.jfbank.qualitymarket.util.StringUtil;
import com.jfbank.qualitymarket.util.UnicornUtils;
import com.jfbank.qualitymarket.util.UserUtils;
import com.qiyukf.unicorn.api.Unicorn;
import com.qiyukf.unicorn.api.UnreadCountChangeListener;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


/**
 * 我的tab页面
 *
 * @author 崔朋朋
 */

public class MyAccountFragment extends BaseFragment implements JumpUtil.JumpInterface, UnreadCountChangeListener {
    public static final String TAG = MyAccountFragment.class.getName();
    @InjectView(R.id.rl_title)
    RelativeLayout rlTitle;
    @InjectView(R.id.tv_title)
    TextView tvTitle;
    @InjectView(R.id.iv_back)
    ImageView ivBack;
    @InjectView(R.id.iv_right_menu)
    ImageView ivRightMenu;
    @InjectView(R.id.rl_myAccountFragment_myOrder)
    RelativeLayout myOrderRelativeLayout;
    @InjectView(R.id.rl_myaccountFragment_receiveGoods)
    LinearLayout receiveGoodsRelativeLayout;
    @InjectView(R.id.rl_myaccountFragment_waitForPay)
    LinearLayout waitForPayRelativeLayout;
    @InjectView(R.id.rl_myaccountFragment_waitForSend)
    LinearLayout waitForSendRelativeLayout;
    @InjectView(R.id.rl_myaccountFragment_afterSale)
    LinearLayout afterSaleRelativeLayout;


    //激活白条
    @InjectView(R.id.rl_myaccountFragment_activiteBorrow)
    RelativeLayout activiteBorrowRelativeLayout;
    @InjectView(R.id.btn_myAccountFragment_activeBorrow)
    Button activeBorrowButton;
    //白条额度
    @InjectView(R.id.rl_myaccountFragment_borrowLine)
    RelativeLayout borrowLineRelativeLayout;
    @InjectView(R.id.tv_myaccountFragment_borrowLine)
    TextView borrowLineTextView;
    @InjectView(R.id.tv_myaccountFragment_viewDetail)
    TextView viewDetailTextView;

    //红包个数
    @InjectView(R.id.rl_myaccountFragment_redPacket)
    RelativeLayout redPacketRelativeLayout;
    @InjectView(R.id.tv_myaccountFragment_redPacket)
    TextView redPacketTextView;
    @InjectView(R.id.tv_myaccountFragment_avaliableCredit)
    TextView avaliableCreditTextView;
    //本期待还
    @InjectView(R.id.rl_myaccountFragment_waitForRepaymentInDate)
    RelativeLayout waitForRepaymentInDateRelativeLayout;
    @InjectView(R.id.tv_myaccountFragment_waitForRepayment)
    TextView waitForRepaymentTextView;
    @InjectView(R.id.tv_myaccountFragment_repaymentDay)
    TextView repaymentDayTextView;
    @InjectView(R.id.tv_myaccountFragment_repaymentDayText)
    TextView repaymentDayTextTextView;
    @InjectView(R.id.tv_myaccountFragment_repaymentNow)
    TextView repaymentNowTextView;

    @InjectView(R.id.rl_myaccountFragment_instalment)
    RelativeLayout instalmentRelativeLayout;
    @InjectView(R.id.ll_myaccountFragment_instalment)
    LinearLayout instalmentLinearLayout;

    @InjectView(R.id.tv_myaccountFragment_waitForPay)
    ImageView waitForPayImageView;
    @InjectView(R.id.tv_myaccountFragment_waitForSend)
    ImageView waitForSendImageView;
    @InjectView(R.id.tv_myaccountFragment_receiveGoods)
    ImageView receiveGoodsImageView;
    @InjectView(R.id.tv_myaccountFragment_afterSale)
    ImageView afterSaleImageView;
    @InjectView(R.id.tv_myaccountFragment_waitForPayBadgeCount)
    TextView waitForPayBadgeCountTextView;
    @InjectView(R.id.tv_myaccountFragment_waitForSendBadgeCount)
    TextView waitForSendBadgeCountTextView;
    @InjectView(R.id.tv_myaccountFragment_receiveGoodsBadgeCount)
    TextView receiveGoodsBadgeCountTextView;
    @InjectView(R.id.tv_myaccountFragment_afterSaleBadgeCount)
    TextView afterSaleBadgeCountTextView;

    @InjectView(R.id.gv_myaccountFragment_bottomFunctionMenu)
    GridView bottomFunctionMenuGridView;
    private BottomFunctionMenuAdapter bottomFunctionMenuAdapter;
    private List<BottomFunctionMenu> bottomFunctionMenuList = new ArrayList<BottomFunctionMenu>();

    public static DecimalFormat moneyDecimalFormat = new DecimalFormat("#0.00");

    /**
     * 不需要登录
     *
     * @param v
     */
    @OnClick({R.id.iv_right_menu})
    public void OnViewClick2(View v) {
        switch (v.getId()) {
            case R.id.iv_right_menu:
                //常见问题
                String commonIssueUrl = HttpRequest.QUALITY_MARKET_WEB_URL + HttpRequest.H5_PAGE_SUB_URL + HttpRequest.H5_PAGE_COMMON_PROBLEM + "?Plat=" + ConstantsUtil.PLAT;
                if (AppContext.isLogin && AppContext.user != null) {//登录后上传平台号和手机号
                    commonIssueUrl = commonIssueUrl + "&mobile=" + AppContext.user.getMobile();
                }
                CommonUtils.startWebViewActivity(getActivity(), commonIssueUrl, true, false);
                break;
        }
    }


    /**
     * 需验证登录
     *
     * @param v
     */
    @OnClick({R.id.rl_myaccountFragment_instalment,
            R.id.ll_myaccountFragment_instalment,
            R.id.rl_myAccountFragment_myOrder, R.id.rl_myaccountFragment_redPacket,
            R.id.rl_myaccountFragment_receiveGoods, R.id.rl_myaccountFragment_waitForPay,
            R.id.rl_myaccountFragment_waitForSend, R.id.rl_myaccountFragment_afterSale,
            R.id.btn_myAccountFragment_activeBorrow, R.id.tv_myaccountFragment_borrowLine,
            R.id.tv_myaccountFragment_viewDetail, R.id.iv_back,
            R.id.rl_myaccountFragment_waitForRepaymentInDate})
    public void OnViewClick(View v) {
        if (!AppContext.isLogin) {
            launchLoginActivity();
            return;
        }
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.tv_myaccountFragment_borrowLine:
                //白条额度
                break;
            case R.id.tv_myaccountFragment_viewDetail:
            case R.id.btn_myAccountFragment_activeBorrow:
                CommonUtils.startWebViewActivity(getActivity(), getOneCardUrlParamter(getActivity(), AppContext.user.getUrl()), true, false, false);
                break;
            case R.id.rl_myaccountFragment_redPacket:
                //红包个数
                intent.setClass(getActivity(), MyRedPacketActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_myaccountFragment_instalment:
                //立即还款
                intent.setClass(getActivity(), PrepaymentActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_myaccountFragment_waitForRepaymentInDate:
                viewInstallmentBill();
                break;
            case R.id.rl_myaccountFragment_receiveGoods:
                //待收货订单
                intent.setClass(getActivity(), MyOrderActivity.class);
                intent.putExtra(MyOrderActivity.KEY_OF_QUERY_ORDER, MyOrderActivity.ORDER_STATUS_WAIT_FOR_TAKE_OVER_GOODS);
                startActivity(intent);
                break;
            case R.id.rl_myaccountFragment_afterSale:
                //申请售后
                JumpUtil.GotoActivity(this, AfterSalesActivity.class);
                break;
            case R.id.rl_myaccountFragment_waitForPay:
                //待支付订单
                intent.setClass(getActivity(), MyOrderActivity.class);
                intent.putExtra(MyOrderActivity.KEY_OF_QUERY_ORDER, MyOrderActivity.ORDER_STATUS_WAIT_FOR_PAY);
                startActivity(intent);
                break;
            case R.id.rl_myaccountFragment_waitForSend:
                //待发货订单
                intent.setClass(getActivity(), MyOrderActivity.class);
                intent.putExtra(MyOrderActivity.KEY_OF_QUERY_ORDER, MyOrderActivity.ORDER_STATUS_WAIT_FOR_SEND_GOODS);
                startActivity(intent);
                break;
            case R.id.rl_myAccountFragment_myOrder:
                //我的订单
                startActivity(new Intent(getActivity(), MyOrderActivity.class));
                break;
            case R.id.iv_back:
                //账户信息
                startActivity(new Intent(getActivity(), AccountInfoActivity.class));
                break;
        }
    }

    /**
     * 查看分期账单
     */
    private void viewInstallmentBill() {
        String instalmentUrl = HttpRequest.QUALITY_MARKET_WEB_URL + HttpRequest.H5_PAGE_SUB_URL + HttpRequest.H5_PAGE_INSTALLMENT_BILL + "?uid="
                + AppContext.user.getUid() + "&token=" + AppContext.user.getToken();
        CommonUtils.startWebViewActivity(getActivity(), instalmentUrl, true, false);
    }


    /**
     * 启动登录页面
     */
    public void launchLoginActivity() {
        if (!AppContext.isLogin) {
            Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
            loginIntent.putExtra(LoginActivity.KEY_OF_COME_FROM, TAG);
            startActivity(loginIntent);
            return;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!MainActivity.isNetworkAvailable(getActivity())) {
            Toast.makeText(getActivity(), ConstantsUtil.ORIGIN_PAGE_FAIL_TO_CONNECT_SERVER, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_myaccount, container, false);
        ButterKnife.inject(this, view);
        viewDetailTextView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//下划线

        bottomFunctionMenuAdapter = new BottomFunctionMenuAdapter(bottomFunctionMenuList, getActivity());
        bottomFunctionMenuGridView.setAdapter(bottomFunctionMenuAdapter);
        bottomFunctionMenuGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                BottomFunctionMenu bottomFunctionMenu = (BottomFunctionMenu) bottomFunctionMenuAdapter.getItem(i);
                if(bottomFunctionMenu != null){
                    // Y 需要登录  N不需要登录
                    if("Y".equals(bottomFunctionMenu.getAuthority()) && (AppContext.user == null || !AppContext.isLogin)){
                        launchLoginActivity();
                        return;
                    }


                    /**
                     * 跳转本地页面或者H5页面
                     * “campuslocal://myBankCard” 跳转本地我的银行卡页面
                     "services://inviteFriends" 跳转本地邀请好友页面
                     "services://myAuthentication","在线客服"
                     */
                    String jumpUrl = bottomFunctionMenu.getUrl();
                    LogUtil.printLog("jumpURL="+jumpUrl+ "--imageURL="+bottomFunctionMenu.getIconImage()+"--Type="+bottomFunctionMenu.getType());

                    //type=3 标识万卡链接 万卡链接需要指定的参数
                    if(bottomFunctionMenu.getType() == 3){
                        jumpUrl = getOneCardUrlParamter(getActivity(), jumpUrl);
                    }else if (bottomFunctionMenu.getType()!=3 && jumpUrl.startsWith("http") && AppContext.isLogin && AppContext.user != null) {//登录后上传平台号和手机号
                        //标识品质H5链接， 和项目经理沟通，品质H5页面不添加参数
                    }

                    if(StringUtil.notEmpty(jumpUrl)){
                        if(jumpUrl.endsWith("myBankCard")){
                            //我的银行卡
                            Intent intent = new Intent();
                            intent.setClass(getActivity(), BankCardListActivity.class);
                            intent.putExtra(BankCardListActivity.KEY_OF_BANK_CARD_COME_FROM, TAG);
                            getActivity().startActivity(intent);
                        }else if(jumpUrl.endsWith("InviteFriends")){
                            //邀请朋友
                            CommonUtils.oneKeyShare(getActivity(), "", "", "", "", false);
                        }else if(jumpUrl.endsWith("myAuthentication")){
                            //"在线客服"
                            UnicornUtils.startUnicorn(mContext);
                        }else {
                            CommonUtils.startWebViewActivity(getActivity(), jumpUrl, true, false);
                        }
                    }

                }
            }
        });

        reInitData();
        ivRightMenu.setImageResource(R.mipmap.myaccount_page_common_issue);
        ivRightMenu.setVisibility(View.VISIBLE);
        tvTitle.setText("我的");
        CommonUtils.setStatusBarTitle(getActivity(), rlTitle);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        EventBus.getDefault().register(this);
        Unicorn.addUnreadCountChangeListener(this, true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Unicorn.addUnreadCountChangeListener(this, false);
        EventBus.getDefault().unregister(this);
    }

    /**
     * 更新token失效问题
     *
     * @param params
     */
    @Subscriber(tag = EventBusConstants.EVENTT_UPDATE_TOKAN_RESET, mode = ThreadMode.MAIN)
    public void updateTokenFaild(Object params) {
        reInitData();
    }

    /**
     * 初始化我的默认数据
     */
    private void reInitData() {
        ivBack.setImageResource(R.mipmap.ic_logo_login);
        activeBorrowButton.setText("开通万卡");
        showActiviteBorrow();
        disableRepaymentNow();
        redPacketTextView.setText(getWhiteAvaliableLineTextStyle("0个"), TextView.BufferType.SPANNABLE);
        avaliableCreditTextView.setText(getWhiteAvaliableLineTextStyle("0.00元"), TextView.BufferType.SPANNABLE);
        waitForRepaymentTextView.setText(getRedRepaymentTextStyle(getActivity(), "0.00元"), TextView.BufferType.SPANNABLE);
        borrowLineTextView.setText(getBorrowLineTextStyle("¥ ..."), TextView.BufferType.SPANNABLE);
        repaymentDayTextView.setText(getRepaymentDayTextStyle("剩余   天"), TextView.BufferType.SPANNABLE);

        hideAllBadgeViews();
    }

    /**
     * 隐藏所有的badge view
     */
    private void hideAllBadgeViews() {
        waitForPayBadgeCountTextView.setVisibility(View.GONE);
        waitForSendBadgeCountTextView.setVisibility(View.GONE);
        receiveGoodsBadgeCountTextView.setVisibility(View.GONE);
        afterSaleBadgeCountTextView.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        requestData();
    }

    @Subscriber(tag = EventBusConstants.EVENTT_UPDATE_BILLENTER_STATUS, mode = ThreadMode.MAIN)
    public void updateImmediateRepayment(Object params) {
        if (0 == AppContext.user.getImmediateRepayment()) {
            disableRepaymentNow();
        } else if (1 == AppContext.user.getImmediateRepayment()) {
            repaymentNowTextView.setText("立即还款");
            instalmentRelativeLayout.setEnabled(true);
        }
    }

    /**
     * 禁用立即还款
     */
    private void disableRepaymentNow() {
        repaymentNowTextView.setText("");
        instalmentRelativeLayout.setEnabled(false);
    }


    @Override
    public String getPageName() {
        return getString(R.string.str_pagename_mycenter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ConstantsUtil.PERMISSION_WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permission Granted
            } else {
                // Permission Denied
            }
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {// 重新显示到最前端中
            requestData();
        }
    }

    /**
     * 请求首页数据
     */
    private void requestData() {
        getBottomFunctionMenu();

        if (AppContext.isLogin) {
            getRedPacketCount();
            getCreditLines(TAG);
            getOrderCount();
            updateImmediateRepayment(null);
            ivBack.setImageResource(R.mipmap.ic_logo_logined);
        } else {
            reInitData();
        }
    }

    /**
     * 显示激活白条
     */
    private void showActiviteBorrow() {
        activiteBorrowRelativeLayout.setVisibility(View.VISIBLE);
        borrowLineRelativeLayout.setVisibility(View.GONE);
        avaliableCreditTextView.setText(getWhiteAvaliableLineTextStyle("0.00元"), TextView.BufferType.SPANNABLE);
        SpannableString borrowMoneyText = getBorrowLineTextStyle("¥ 0.00");
        borrowLineTextView.setText(borrowMoneyText, TextView.BufferType.SPANNABLE);
    }

    /**
     * 显示我的额度
     */
    private void showMyLine() {
        activiteBorrowRelativeLayout.setVisibility(View.GONE);
        borrowLineRelativeLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 查询信用额度
     */
    public void getCreditLines(final String comeFrom) {
        Map<String, String> params = new HashMap<>();
        params.put("uid", AppContext.user.getUid());
        params.put("token", AppContext.user.getToken());

        HttpRequest.post(mContext, HttpRequest.QUALITY_MARKET_WEB_URL + HttpRequest.GET_CREDIT_LINES, params,
                new AsyncResponseCallBack() {

                    @Override
                    public void onResult(String arg2) {
                        try {
                            String jsonStr = new String(arg2);
                            LogUtil.printLog("查询信用额度：" + jsonStr);
                            JSONObject jsonObject = JSON.parseObject(jsonStr);
                            if (ConstantsUtil.RESPONSE_SUCCEED == jsonObject
                                    .getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
                                User user = JSON.parseObject(jsonObject.getJSONObject(ConstantsUtil.RESPONSE_DATA_JSON_ARRAY_FIELD_NAME).toString(), User.class);
                                if ( AppContext.user != null && AppContext.isLogin) {
                                    AppContext.user.setCreditLine(user.getCreditLine());
                                    AppContext.user.setUsableLine(user.getUsableLine());
                                    AppContext.user.setUrl(user.getUrl());
                                    AppContext.user.setMessage(user.getMessage());
                                    AppContext.user.setStep(user.getStep());
                                    AppContext.user.setIdName(user.getIdName());
                                    AppContext.user.setIdNumber(user.getIdNumber());
                                    new StoreService(mContext).saveUserInfo(AppContext.user);//退出应用前保存数据
                                }

                                if (MyAccountFragment.TAG.equals(comeFrom)) {
                                    //        1未实名
                                    //        2未设置交易密码
                                    //        3用户无额度
                                    //        4用户额度未激活
                                    //        5用户额度已冻结
                                    //        6未开卡
                                    //        7已冻结
                                    //        8额度状态未审批
                                    //        9信用额度不足
                                    //        0成功
                                    if ("0".equals(user.getStep())) {
                                        SpannableString styledText = getWhiteAvaliableLineTextStyle(moneyDecimalFormat.format(Double.valueOf(user.getUsableLine())) + "元");
                                        avaliableCreditTextView.setText(styledText, TextView.BufferType.SPANNABLE);
                                        SpannableString styledText2 = getBorrowLineTextStyle("¥ " + moneyDecimalFormat.format(Double.valueOf(user.getCreditLine())));
                                        borrowLineTextView.setText(styledText2, TextView.BufferType.SPANNABLE);
                                        showMyLine();
                                    } else {
                                        activeBorrowButton.setText(user.getMessage());
                                        showActiviteBorrow();
                                    }
                                }


                            } else if (ConstantsUtil.RESPONSE_TOKEN_FAIL == jsonObject
                                    .getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
                                UserUtils.tokenFailDialog(mContext, jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME), null);
                            } else {
                                Toast.makeText(getActivity(), jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME), Toast.LENGTH_SHORT).show();
                                if (MyAccountFragment.TAG.equals(comeFrom)) {
                                    showActiviteBorrow();
                                }
                            }
                        } catch (Exception e) {
                            Log.e("查询信用额度：", "数据出错");
                        }
                    }

                    @Override
                    public void onFailed(String path, String msg) {
                        // Toast.makeText(getActivity(),
                        // ConstantsUtil.FAIL_TO_CONNECT_SERVER+"查询信用额度失败",
                        // Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * 查询红包个数，账单日，本期待还
     */
    private void getRedPacketCount() {
        Map<String, String> params = new HashMap<>();
        params.put("uid", AppContext.user.getUid());
        params.put("mobile", AppContext.user.getMobile());
        params.put("token", AppContext.user.getToken());

        HttpRequest.post(mContext, HttpRequest.QUALITY_MARKET_WEB_URL + HttpRequest.GET_RED_PACKET, params,
                new AsyncResponseCallBack() {

                    @Override
                    public void onResult(String arg2) {
                        String jsonStr = new String(arg2);
                        LogUtil.printLog("查询红包个数，账单日，本期待还：" + jsonStr);

                        JSONObject jsonObject = JSON.parseObject(jsonStr);

                        //0:未生成账单
                        //1:未还款
                        //2：逾期
                        if (ConstantsUtil.RESPONSE_SUCCEED == jsonObject
                                .getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {

                            User user = JSON.parseObject(jsonObject.toString(), User.class);
                            redPacketTextView.setText(getWhiteAvaliableLineTextStyle(user.getCouponCount() + "个"), TextView.BufferType.SPANNABLE);
                            if (StringUtil.notEmpty(user.getBillCount())) {
                                waitForRepaymentTextView.setText(getRedRepaymentTextStyle(getActivity(), user.getBillCount() + "元"), TextView.BufferType.SPANNABLE);
                            }
                            if (StringUtil.notEmpty(user.getSettledDay())) {
                                //0:未生成账单
                                //1:未还款
                                //2：逾期
                                if ("1".equals(user.getStatus1())) {
                                    repaymentDayTextView.setText(getRepaymentDayTextStyle("剩余 " + user.getSettledDay() + "天"), TextView.BufferType.SPANNABLE);
                                } else if ("2".equals(user.getStatus1())) {
                                    repaymentDayTextView.setText(getRepaymentDayTextStyle("已逾期" + user.getSettledDay() + "天"), TextView.BufferType.SPANNABLE);
                                }
                            }

                            if (AppContext.user != null && AppContext.isLogin) {
                                AppContext.user.setCouponCount(user.getCouponCount());
                                AppContext.user.setBillCount(user.getBillCount());
                                AppContext.user.setSettledDay(user.getSettledDay());
                                AppContext.user.setStatus1(user.getStatus1());
                            }
                        } else if (ConstantsUtil.RESPONSE_TOKEN_FAIL == jsonObject
                                .getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
                            UserUtils.tokenFailDialog(mContext, jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME), null);
                        } else {
                            Toast.makeText(getActivity(), jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailed(String path, String msg) {
                        // Toast.makeText(getActivity(),
                        // ConstantsUtil.FAIL_TO_CONNECT_SERVER+"查询信用额度失败",
                        // Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * 获取底部功能菜单
     * 不要求登录
     */
    private void getBottomFunctionMenu() {
        Map<String,String> params = new HashMap<>();
        if(AppContext.user !=null && AppContext.isLogin){
            params.put("uid", AppContext.user.getUid());
            params.put("token", AppContext.user.getToken());
        }

        HttpRequest.post(mContext, HttpRequest.QUALITY_MARKET_WEB_URL + HttpRequest.GET_BOTTOM_FUNCTION_MENU, params,
                new AsyncResponseCallBack() {

                    @Override
                    public void onResult(String arg2) {
                        String jsonStr = new String(arg2);
                        LogUtil.printLog("获取底部功能菜单：" + jsonStr);

                        JSONObject jsonObject = JSON.parseObject(jsonStr);
                        if (ConstantsUtil.RESPONSE_SUCCEED == jsonObject
                                .getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {

                            bottomFunctionMenuList = JSON.parseArray(jsonObject.getString(ConstantsUtil.RESPONSE_DATA_JSON_ARRAY_FIELD_NAME), BottomFunctionMenu.class);
                            bottomFunctionMenuAdapter.updateData(bottomFunctionMenuList);
                        } else if (ConstantsUtil.RESPONSE_TOKEN_FAIL == jsonObject
                                .getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
                            // LoginActivity.tokenFailDialog(getActivity(),
                            // jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME),
                            // TAG);
                        } else {
                            Toast.makeText(getActivity(), jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailed(String path, String msg) {
                        // Toast.makeText(getActivity(),
                        // ConstantsUtil.FAIL_TO_CONNECT_SERVER+"查询信用额度失败",
                        // Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * 获取各类订单数
     */
    private void getOrderCount() {
        Map<String, String> params = new HashMap<>();
        params.put("uid", AppContext.user.getUid());
        params.put("token", AppContext.user.getToken());
        params.put("version", AppContext.getAppVersionName(getActivity()));
        params.put("channel", ConstantsUtil.CHANNEL_CODE);

        HttpRequest.post(mContext, HttpRequest.QUALITY_MARKET_WEB_URL + HttpRequest.GET_ORDER_COUNT, params,
                new AsyncResponseCallBack() {

                    @Override
                    public void onResult(String arg2) {
                        String jsonStr = new String(arg2);
                        LogUtil.printLog("获取各类订单数：" + jsonStr);

                        JSONObject jsonObject = JSON.parseObject(jsonStr);

                        if (ConstantsUtil.RESPONSE_SUCCEED == jsonObject
                                .getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
                            int waitForPayCount = jsonObject.getIntValue("dzf");
                            int waitForSendCount = jsonObject.getIntValue("dfh");
                            int waitForReceiveGoodsCount = jsonObject.getIntValue("dsh");
                            int afterSaleCount = jsonObject.getIntValue("shth");


                            hideAllBadgeViews();

                            if (waitForPayCount > 0) {
                                waitForPayBadgeCountTextView.setVisibility(View.VISIBLE);
                                if (waitForPayCount < 99) {
                                    waitForPayBadgeCountTextView.setText(waitForPayCount + "");
                                } else {
                                    waitForPayBadgeCountTextView.setText("99+");
                                }
                            }
                            if (waitForSendCount > 0) {
                                waitForSendBadgeCountTextView.setVisibility(View.VISIBLE);
                                if (waitForSendCount < 99) {
                                    waitForSendBadgeCountTextView.setText(waitForSendCount + "");
                                } else {
                                    waitForSendBadgeCountTextView.setText("99+");
                                }
                            }
                            if (waitForReceiveGoodsCount > 0) {
                                receiveGoodsBadgeCountTextView.setVisibility(View.VISIBLE);
                                if (waitForReceiveGoodsCount < 99) {
                                    receiveGoodsBadgeCountTextView.setText(waitForReceiveGoodsCount + "");
                                } else {
                                    receiveGoodsBadgeCountTextView.setText("99+");
                                }
                            }
                            if (afterSaleCount > 0) {
                                afterSaleBadgeCountTextView.setVisibility(View.VISIBLE);
                                if (afterSaleCount < 99) {
                                    afterSaleBadgeCountTextView.setText(afterSaleCount + "");
                                } else {
                                    afterSaleBadgeCountTextView.setText("99+");
                                }
                            }
                        } else if (ConstantsUtil.RESPONSE_TOKEN_FAIL == jsonObject
                                .getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
                            UserUtils.tokenFailDialog(getActivity(),
                                    jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME), TAG);
                        } else {
                            Toast.makeText(getActivity(), jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailed(String path, String msg) {
                        // Toast.makeText(getActivity(),
                        // ConstantsUtil.FAIL_TO_CONNECT_SERVER+"获取各类订单数失败",
                        // Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * 本期待还的text style
     *
     * @param
     * @return
     */
    public static SpannableString getRedRepaymentTextStyle(Activity activity, String money) {
        SpannableString styledText = new SpannableString(money);
        styledText.setSpan(new TextAppearanceSpan(activity, R.style.style_themeRed_22sp), 0, (money.length() - 1),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        styledText.setSpan(new TextAppearanceSpan(activity, R.style.style_themeRed_12sp), (money.length() - 1),
                money.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return styledText;
    }

    /**
     * 红包的text style
     *
     * @param
     * @return
     */
    private SpannableString getWhiteAvaliableLineTextStyle(String money) {
        SpannableString styledText = new SpannableString(money);
        styledText.setSpan(new TextAppearanceSpan(getActivity(), R.style.style_themeRed_16sp), 0, (money.length() - 1),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        styledText.setSpan(new TextAppearanceSpan(getActivity(), R.style.style_themeRed_12sp), (money.length() - 1),
                money.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return styledText;
    }


    /**
     * 白条额度的text style
     *
     * @param
     * @return
     */
    private SpannableString getBorrowLineTextStyle(String money) {
        SpannableString styledText2 = new SpannableString(money);
        styledText2.setSpan(new TextAppearanceSpan(getActivity(), R.style.style_white_16sp), 0,
                2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        styledText2.setSpan(new TextAppearanceSpan(getActivity(), R.style.money_white_style_40sp), 2, money.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return styledText2;
    }

    /**
     * 还款日的text style
     *
     * @param day
     * @return
     */
    private SpannableString getRepaymentDayTextStyle(String day) {
        SpannableString styledText3 = new SpannableString(day);
        styledText3.setSpan(new TextAppearanceSpan(getActivity(), R.style.style_themeRed_12sp), 0,
                3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        styledText3.setSpan(new TextAppearanceSpan(getActivity(), R.style.style_themeRed_22sp), 3, (day.length() - 1),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        styledText3.setSpan(new TextAppearanceSpan(getActivity(), R.style.style_themeRed_12sp), (day.length() - 1),
                day.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return styledText3;
    }


    @Override
    public void onUnreadCountChange(int count) {
//        serViceCountNumView.setBadgeCount(count);
//        if (count > 0) {
//            serViceCountNumView.setVisibility(View.VISIBLE);
//        } else {
//            serViceCountNumView.setVisibility(View.GONE);
//        }
    }

    @Override
    public void finish() {

    }

    public static String getOneCardUrlParamter(Context context, String url) {
        String ret = "";
        String imei = AppContext.getIMEI(context);
        String[] s = imei.split(" ");
        imei = "";
        for (String s1 : s) {
            imei = imei + s1;
        }
        String model = Build.MODEL.trim();
        String[] ss = model.split(" ");
        model = "";
        for (String ss1 : ss) {
            model = model + ss1;
        }

        PackageInfo appManager;
        String version = "6";
        try {
            appManager = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            appManager = null;
        }
        if (appManager != null) {
            version = String.valueOf(appManager.versionCode);
        }

        ret = url + "?source=" + ConstantsUtil.CHANNEL_OF_PINZHI_IN_ONE_CARD + "&token=" + AppContext.user.getToken() + "&lat=" + AppContext.location.getLatitude() + "&lng=" + AppContext.location.getLongitude()
                + "&unitType=" + model + "&deviceId=" + imei + "&fromChanner=h5" + "&version=" + version;

        return ret;
    }
}

