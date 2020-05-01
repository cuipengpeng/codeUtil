//package com.jfbank.qualitymarket.activity;
//
//import android.content.Intent;
//import android.graphics.drawable.Drawable;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.Window;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.jfbank.qualitymarket.ActivitysManage;
//import com.jfbank.qualitymarket.AppContext;
//import com.jfbank.qualitymarket.R;
//import com.jfbank.qualitymarket.base.BaseActivity;
//import com.jfbank.qualitymarket.dao.StoreService;
//import com.jfbank.qualitymarket.fragment.PopDialogFragment;
//import com.jfbank.qualitymarket.model.MyAuthenticationBean;
//import com.jfbank.qualitymarket.model.MyAuthenticationBean.AuthenticationData;
//import com.jfbank.qualitymarket.net.HttpRequest;
//import com.jfbank.qualitymarket.util.CommonUtils;
//import com.jfbank.qualitymarket.util.ConstantsUtil;
//import com.jfbank.qualitymarket.util.LogUtil;
//import com.jfbank.qualitymarket.util.UserUtils;
//import com.jfbank.qualitymarket.widget.LoadingAlertDialog;
//import com.jfbank.qualitymarket.widget.MyPopupDialog;
//import com.jfbank.qualitymarket.callback.AsyncResponseCallBack;
// import java.util.HashMap; import java.util.Map;
//
//import org.apache.http.Header;
//
///**
// * 我的认证和激活白条页面
// *
// * @author 彭爱军
// * @date 2016年10月20日
// */
//public class MyAuthenticationActivity extends BaseActivity implements OnClickListener {
//    protected String TAG = WebViewActivity.class.getName();
//    /**
//     * 进入认证界面判断  true 进入我的认证页面，false进入激活白条页面
//     */
//    public static final String KEY_OF_ENTER_MYAUTH_ORJHBT = "STEP";
//    RelativeLayout rlTitle;
//    /**
//     * 显示标题内容
//     */
//    TextView tvTitle;
//    /**
//     * 返回
//     */
//    ImageView ivBack;
//
//    /**
//     * 如果是激活白条则隐藏基本资料
//     */
//    private LinearLayout mMy_authentication_ll_hide;
//    /**
//     * 实名认证
//     */
//    private LinearLayout mMy_authentication_ll_autonym;
//    private TextView mMy_authentication_tv_autonym;
//    private ImageView mMy_authentication_iv_autonym;
//    /**
//     * 个人资料
//     */
//    private LinearLayout mMy_authentication_ll_material;
//    private TextView mMy_authentication_tv_material;
//    private ImageView mMy_authentication_iv_material;
//    /**
//     * 绑定手机
//     */
//    private LinearLayout mMy_authentication_ll_phone;
//    private TextView mMy_authentication_tv_phone;
//    private ImageView mMy_authentication_iv_phone;
//    /**
//     * 极速受信的标题
//     */
//    private TextView mMy_authentication_tv_credit_title;
//    /**
//     * 芝麻信用分
//     */
//    private RelativeLayout mMy_authentication_rl_sesame;
//    private TextView mMy_authentication_tv_sesame;
//    /**
//     * 信用卡授权
//     */
//    private RelativeLayout mMy_authentication_rl_credit;
//    private TextView mMy_authentication_tv_credit;
//    /**
//     * 社保授权
//     */
//    private RelativeLayout mMy_authentication_rl_jinpo;
//    private TextView mMy_authentication_tv_jinpo;
//    /**
//     * 激活白条页面显示的提示语句
//     */
//    private TextView my_authentication_tv_white;
//    /**
//     * 显示"激活白条"还是"我的认证"
//     */
//    public static boolean activeBorrow = false;
//
//    /**
//     * 网编请求时加载框
//     */
//    private LoadingAlertDialog mDialog;
//
//    private boolean mIsAuthentication = true;    //默认显示我的认证页面。false显示激活白条页面
//    /**
//     * 额度申请id
//     */
//    private String mCreditlineApplyId;
//    private int mStep;
//    /**
//     * 基本信息认证状态
//     */
//    private String mBseInfoAuthenticationStatus;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.activity_my_authentication);
//        ActivitysManage.getActivitysManager().addActivity(this);
//        bindViews();
//        setOnClickListen();
//    }
//
//    /**
//     * 设置点击监听
//     */
//    private void setOnClickListen() {
//        mMy_authentication_ll_autonym.setOnClickListener(this);
//        mMy_authentication_ll_material.setOnClickListener(this);
//        mMy_authentication_ll_phone.setOnClickListener(this);
//
//        mMy_authentication_rl_sesame.setOnClickListener(this);
//        mMy_authentication_rl_credit.setOnClickListener(this);
//        mMy_authentication_rl_jinpo.setOnClickListener(this);
//
//        mMy_authentication_rl_credit.setVisibility(View.GONE);        //隐藏信用卡
//
//        ivBack.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//
//    }
//
//    /**
//     * 初始化views
//     */
//    private void bindViews() {
//        rlTitle = (RelativeLayout) findViewById(R.id.rl_title);
//        ivBack = (ImageView) findViewById(R.id.iv_back);
//        tvTitle = (TextView) findViewById(R.id.tv_title);
//        CommonUtils.setTitle(this, rlTitle);
//
//        mMy_authentication_ll_hide = (LinearLayout) findViewById(R.id.my_authentication_ll_hide);
//        mMy_authentication_ll_autonym = (LinearLayout) findViewById(R.id.my_authentication_ll_autonym);
//        mMy_authentication_tv_autonym = (TextView) findViewById(R.id.my_authentication_tv_autonym);
//        mMy_authentication_ll_material = (LinearLayout) findViewById(R.id.my_authentication_ll_material);
//        mMy_authentication_tv_material = (TextView) findViewById(R.id.my_authentication_tv_material);
//        mMy_authentication_ll_phone = (LinearLayout) findViewById(R.id.my_authentication_ll_phone);
//        mMy_authentication_tv_phone = (TextView) findViewById(R.id.my_authentication_tv_phone);
//        mMy_authentication_tv_credit_title = (TextView) findViewById(R.id.my_authentication_tv_credit_title);
//        mMy_authentication_rl_sesame = (RelativeLayout) findViewById(R.id.my_authentication_rl_sesame);
//        mMy_authentication_tv_sesame = (TextView) findViewById(R.id.my_authentication_tv_sesame);
//        mMy_authentication_rl_credit = (RelativeLayout) findViewById(R.id.my_authentication_rl_credit);
//        mMy_authentication_tv_credit = (TextView) findViewById(R.id.my_authentication_tv_credit);
//        mMy_authentication_rl_jinpo = (RelativeLayout) findViewById(R.id.my_authentication_rl_jinpo);
//        mMy_authentication_tv_jinpo = (TextView) findViewById(R.id.my_authentication_tv_jinpo);
//        my_authentication_tv_white = (TextView) findViewById(R.id.my_authentication_tv_white);
//
//        mMy_authentication_iv_autonym = (ImageView) findViewById(R.id.my_authentication_iv_autonym);
//        mMy_authentication_iv_material = (ImageView) findViewById(R.id.my_authentication_iv_material);
//        mMy_authentication_iv_phone = (ImageView) findViewById(R.id.my_authentication_iv_phone);
//
//        //TODO 需要从intent中获取显示我的认证或激活白条页面的标识
//        Intent intent = getIntent();
//        if (null != intent) {
//            mIsAuthentication = intent.getBooleanExtra(KEY_OF_ENTER_MYAUTH_ORJHBT, true);
//        }
//        if (mIsAuthentication) {
//            mMy_authentication_ll_hide.setVisibility(View.VISIBLE);
//            tvTitle.setText(R.string.str_pagename_myauth);
//            activeBorrow = false;
//            my_authentication_tv_white.setVisibility(View.GONE);
//        } else {
//            mMy_authentication_ll_hide.setVisibility(View.GONE);
//            mMy_authentication_tv_credit_title.setText("以下方式任选其一即可激活白条");
//            tvTitle.setText(R.string.str_pagename_jhbt);
//            my_authentication_tv_white.setVisibility(View.VISIBLE);
//            activeBorrow = true;
//            Drawable drawable = getResources().getDrawable(R.mipmap.ic_delete_gray);
//            ivBack.setImageDrawable(drawable);
//        }
//        mCreditlineApplyId = AppContext.user.getCreditlineApplyId4show();
//    }
//
//    @Override
//    public void onClick(View v) {
//        Intent intent = new Intent();
//        switch (v.getId()) {
//            case R.id.my_authentication_ll_autonym:        //实名认证
//                intent.setClass(this, ApplyAmountActivity.class);
//                intent.putExtra(ApplyAmountActivity.KEY_OF_APPLYAMOUNT_STEP, 1);
//                startActivity(intent);
//                break;
//            case R.id.my_authentication_ll_material:    //个人资料
//                if (2 != mStep) {
//                    popDialog("请先填写实名认证");
//                    break;
//                }
//                intent.setClass(this, ApplyAmountActivity.class);
//                intent.putExtra(ApplyAmountActivity.KEY_OF_APPLYAMOUNT_STEP, 2);
//                startActivity(intent);
//                break;
//            case R.id.my_authentication_ll_phone:        //绑定手机
//                if (3 != mStep) {
//                    if (1 == mStep) {
//                        popDialog("请先填写实名认证");
//                    } else {
//                        popDialog("请先填写个人资料");
//                    }
//                    break;
//                }
//                intent.setClass(this, ApplyAmountActivity.class);
//                intent.putExtra(ApplyAmountActivity.KEY_OF_APPLYAMOUNT_STEP, 3);
//                startActivity(intent);
//                break;
//            case R.id.my_authentication_rl_sesame:        //芝麻信用
//                if (!isCreditClick()) {        //判断授信是否可点击
//                    break;
//                }
//                queryZhimaCreditStatus();
//                if (!mIsAuthentication) {
//                    finish();
//                }
//                break;
//            case R.id.my_authentication_rl_credit:        //信用卡授权
//                if (!isCreditClick()) {        //判断授信是否可点击
//                    break;
//                }
//                intent.setClass(this, CreditCardAuthorizationActivity.class);
//                startActivity(intent);
//                break;
//            case R.id.my_authentication_rl_jinpo:        //社保授权
//                if (!isCreditClick()) {        //判断授信是否可点击
//                    break;
//                }
//                intent.setClass(this, SocialInsuranceAuthorizationActivity.class);
//                startActivity(intent);
//                if (!mIsAuthentication) {
//                    finish();
//                }
//                break;
//
//            default:
//                break;
//        }
//    }
//
//    /**
//     * 查询芝麻授信状态
//     */
//    private void queryZhimaCreditStatus() {
//        Map<String,String> params = new HashMap<>();
//        params.put("uid", AppContext.user.getUid());
//        params.put("token", AppContext.user.getToken());
//
//        HttpRequest.post(mContext,HttpRequest.QUALITY_MARKET_WEB_URL + HttpRequest.QUERY_ZHIMA_CREDIT_STATUS, params,
//                new AsyncResponseCallBack() {
//
//                    @Override
//                    public void onFailed(String path, String msg) {
//                        Toast.makeText(MyAuthenticationActivity.this,
//                                ConstantsUtil.ORIGIN_PAGE_FAIL_TO_CONNECT_SERVER + "查询芝麻信用状态失败", Toast.LENGTH_SHORT)
//                                .show();
//                    }
//
//                    @Override
//                    public void onResult(String arg2) {
//                        String jsonStr = new String(arg2);
//                        LogUtil.printLog("查询芝麻信用状态：" + jsonStr);
//
//                        JSONObject jsonObject = JSON.parseObject(jsonStr);
//
//                        if (ConstantsUtil.RESPONSE_SUCCEED == jsonObject
//                                .getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
//                            // 显示芝麻分值的url
//                            String zhimaScore = HttpRequest.QUALITY_MARKET_WEB_URL
//                                    + "views/credit/sesameCredit.html?uid=" + AppContext.user.getUid() + "&token="
//                                    + AppContext.user.getToken();
//                            CommonUtils.startWebViewActivity(MyAuthenticationActivity.this, zhimaScore, false, true);
//                        } else if (ConstantsUtil.RESPONSE_TOKEN_FAIL == jsonObject
//                                .getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
//                            UserUtils.tokenFailDialog(MyAuthenticationActivity.this,
//                                    jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME), TAG);
//                        } else {
//                            showZhimaCreditUrl();
//                        }
//
//                    }
//                });
//    }
//
//    /**
//     * 查询芝麻信用url
//     */
//    private void showZhimaCreditUrl() {
//        Map<String,String> params = new HashMap<>();
//        params.put("uid", AppContext.user.getUid());
//        params.put("token", AppContext.user.getToken());
//        HttpRequest.post(mContext,HttpRequest.QUALITY_MARKET_WEB_URL + HttpRequest.QUERY_ZHIMA_CREDIT, params,
//                new AsyncResponseCallBack() {
//
//                    @Override
//                    public void onFailed(String path, String msg) {
//                        Toast.makeText(MyAuthenticationActivity.this,
//                                ConstantsUtil.ORIGIN_PAGE_FAIL_TO_CONNECT_SERVER + "查询芝麻信用url失败", Toast.LENGTH_SHORT)
//                                .show();
//                    }
//
//                    @Override
//                    public void onResult(String arg2) {
//                        String jsonStr = new String(arg2);
//                        LogUtil.printLog("查询芝麻信用url：" + jsonStr);
//
//                        JSONObject jsonObject = JSON.parseObject(jsonStr);
//
//                        if (ConstantsUtil.RESPONSE_SUCCEED == jsonObject
//                                .getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
//                            CommonUtils.startWebViewActivity(MyAuthenticationActivity.this, jsonObject.getString("url"), false, true);
//
//                        } else if (ConstantsUtil.RESPONSE_TOKEN_FAIL == jsonObject
//                                .getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
//                            UserUtils.tokenFailDialog(MyAuthenticationActivity.this,
//                                    jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME), TAG);
//                        } else {
//                            Toast.makeText(MyAuthenticationActivity.this,
//                                    jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME), Toast.LENGTH_SHORT)
//                                    .show();
//                        }
//
//                    }
//                });
//    }
//
//    /**
//     * 授信是否可点击
//     *
//     * @return
//     */
//    private boolean isCreditClick() {        //即step不为0(即editing<==>mSpet!=0)或者基本信息状态为auditRefused或timout是不可以点击.即认证失败不可以点击
//        if ("editing".equals(mBseInfoAuthenticationStatus) || "auditRefused".equals(mBseInfoAuthenticationStatus) || "timeout".equals(mBseInfoAuthenticationStatus)) {
//            popDialog("必须填写基本资料才可以进行极速授信");
//            return false;
//        } else {
//            return true;
//        }
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        requestMyAuthentication();
//    }
//
//    @Override
//    protected String getPageName() {
//        Intent intent = getIntent();
//        if (getIntent().getBooleanExtra(KEY_OF_ENTER_MYAUTH_ORJHBT, true)) {
//            return getString(R.string.str_pagename_myauth);
//        } else {
//            return getString(R.string.str_pagename_jhbt);
//        }
//    }
//
//    /**
//     * 弹出对话框
//     */
//    private void popDialog(String str) {
//        final MyPopupDialog dialog = new MyPopupDialog(this, null, str, null, null, "确定", false);
//        dialog.setOnClickListen(new MyPopupDialog.OnClickListen() {
//
//            @Override
//            public void rightClick() {
//                dialog.dismiss();
//            }
//
//            @Override
//            public void leftClick() {
//                dialog.dismiss();
//
//            }
//        });
//        dialog.show();
//    }
//
//    /**
//     * 网络请求
//     */
//    private void requestMyAuthentication() {
//        if (null == mDialog) {
//            mDialog = new LoadingAlertDialog(this);
//        }
//        mDialog.show(ConstantsUtil.NETWORK_REQUEST_IN);
//
//        Map<String,String> params = new HashMap<>();
//
//        params.put("uid", AppContext.user.getUid());
//        params.put("token", AppContext.user.getToken());
//
//        params.put("ver", AppContext.getAppVersionName(this));
//        params.put("Plat", ConstantsUtil.PLAT);
//
//        Log.e("TAG", params.toString());
//
//        HttpRequest.myAuthentication(mContext,params, new AsyncResponseCallBack() {
//
//            @Override
//            public void onResult(String arg2) {
//                if (mDialog.isShowing()) {
//                    mDialog.dismiss();
//                }
//                if (null != arg2 && arg2.length() > 0) {
//                    Log.e("TAG", ")))" + new String(arg2));
//                    explainJson(new String(arg2));
//                }
//
//            }
//
//            @Override
//            public void onFailed(String path, String msg) {
//                if (mDialog.isShowing()) {
//                    mDialog.dismiss();
//                }
//                Toast.makeText(MyAuthenticationActivity.this, ConstantsUtil.ORIGIN_PAGE_FAIL_TO_CONNECT_SERVER, Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    /**
//     * 解释json
//     *
//     * @param json
//     */
//    protected void explainJson(String json) {
//        try {
//            MyAuthenticationBean bean = JSON.parseObject(json, MyAuthenticationBean.class);
//            if (null != bean && ConstantsUtil.RESPONSE_SUCCEED == Integer.parseInt(bean.getStatus())) {
//                mStep = Integer.parseInt(bean.getData().getStep());
//                mBseInfoAuthenticationStatus = bean.getData().getStatus();
//                if (!"editing".equals(mBseInfoAuthenticationStatus)) {                //只要不是editing状态
//                    showState(bean.getData());
//                    setAuthorizationState(bean.getData());
//                } else {
//                    authenticationState(mStep);
//                }
//            } else if (ConstantsUtil.RESPONSE_TOKEN_FAIL == Integer.parseInt(bean.getStatus())) {
//                showDialog(bean.getStatusDetail());
//            } else {
//                Toast.makeText(this, bean.getStatusDetail(), Toast.LENGTH_SHORT).show();
//            }
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 极速受信各自状态
//     *
//     * @param data
//     */
//    private void setAuthorizationState(AuthenticationData data) {
//        if (!"".equals(data.getShebaoStatus())) {
//            jinpoState(Integer.parseInt(data.getShebaoStatus()));
//        }
//
//        if (!"".equals(data.getSesameStatus())) {
//            sesameStatus(Integer.parseInt(data.getSesameStatus()));
//        }
//        //TODO  信用卡待完善
//    }
//
//    /**
//     * 芝麻授信显示的状态
//     *
//     * @param flag
//     */
//    private void sesameStatus(int flag) {
//        switch (flag) {
//            case 1:
//                mMy_authentication_tv_sesame.setText("去授权");
//                mMy_authentication_rl_sesame.setEnabled(true);
//                mMy_authentication_tv_sesame.setTextColor(0xff79c5f9);
//                break;
//            case 7:
//                mMy_authentication_tv_sesame.setText("已通过");
//                mMy_authentication_rl_sesame.setEnabled(false);
//                mMy_authentication_tv_sesame.setTextColor(0xff79c5f9);
//                break;
//            case 6:
//                mMy_authentication_tv_sesame.setText("认证失败");
//                mMy_authentication_rl_sesame.setEnabled(true);
//                mMy_authentication_tv_sesame.setTextColor(0xff79c5f9);
//                break;
//            case 8:
//                mMy_authentication_tv_sesame.setText("已过期");
//                mMy_authentication_rl_sesame.setEnabled(true);
//                mMy_authentication_tv_sesame.setTextColor(0xff79c5f9);
//                break;
//            default:
//                mMy_authentication_tv_sesame.setText("审核中");
//                mMy_authentication_tv_sesame.setTextColor(0xff79c5f9);
//                mMy_authentication_rl_sesame.setEnabled(false);
//                break;
//        }
//    }
//
//    /**
//     * 全部提交成功。即整个基本资料提交成功。  基本资料显示的状态
//     *
//     * @param data
//     */
//    private void showState(AuthenticationData data) {
//        if ("auditRefused".equals(data.getStatus())) {        //认证失败
//            setDefaultOrFailureState("认证失败", true);
//        } else if ("scorePassed".equals(data.getStatus()) || "auditPassed".equals(data.getStatus())) {            //已通过
//            setDefaultOrFailureState("已通过", false);
//        } else if ("submited".equals(data.getStatus()) || "inRulePassed".equals(data.getStatus())) {    //已提交
//            setDefaultOrFailureState("已提交", false);
//        } else if ("timeout".equals(data.getStatus())) {        //认证过期
//            setDefaultOrFailureState("认证过期", true);
//        } else {
//            setDefaultOrFailureState("已提交", false);
//        }
//    }
//
//
//    /**
//     * token失效时弹出对话框
//     *
//     * @param content
//     */
//    private void showDialog(String content) {
//        final PopDialogFragment dialog = PopDialogFragment.newDialog(false, false, null, content, null, null, "确定");
//        dialog.setOnClickListen(new PopDialogFragment.OnClickListen() {
//
//            @Override
//            public void rightClick() {
//                new StoreService(MyAuthenticationActivity.this).clearUserInfo();
//                Intent loginIntent = new Intent(MyAuthenticationActivity.this.getApplication(), LoginActivity.class);
//                loginIntent.putExtra(LoginActivity.KEY_OF_COME_FROM, LoginActivity.TOKEN_FAIL_TAG);
//                startActivity(loginIntent);
//                dialog.dismiss();
//            }
//
//            @Override
//            public void leftClick() {
//                dialog.dismiss();
//            }
//        });
//        dialog.show(getSupportFragmentManager(), "TAG");
//    }
//
//    /**
//     * 认证状态
//     *
//     * @param flag
//     */
//    private void authenticationState(int flag) {
//        switch (flag) {
//            case 1:
//                setDefaultOrFailureState("提交", true);
//                break;
//            case 2:
//                mMy_authentication_tv_autonym.setText("已提交");
//                mMy_authentication_tv_material.setText("提交");
//                mMy_authentication_tv_phone.setText("提交");
//
//                mMy_authentication_ll_autonym.setEnabled(false);
//                mMy_authentication_ll_material.setEnabled(true);
//                mMy_authentication_ll_phone.setEnabled(true);
//
//                mMy_authentication_tv_autonym.setTextColor(0xff79c5f9);
//                mMy_authentication_tv_material.setTextColor(0xff79c5f9);
//                mMy_authentication_tv_phone.setTextColor(0xff79c5f9);
//
//                mMy_authentication_iv_autonym.setBackgroundResource(R.drawable.shim_2);
//                mMy_authentication_iv_material.setBackgroundResource(R.drawable.geren_1);
//                mMy_authentication_iv_phone.setBackgroundResource(R.drawable.bangd_1);
//                break;
//            case 3:
//                mMy_authentication_tv_autonym.setText("已提交");
//                mMy_authentication_tv_material.setText("已提交");
//                mMy_authentication_tv_phone.setText("提交");
//
//                mMy_authentication_ll_autonym.setEnabled(false);
//                mMy_authentication_ll_material.setEnabled(false);
//                mMy_authentication_ll_phone.setEnabled(true);
//
//                mMy_authentication_tv_autonym.setTextColor(0xff79c5f9);
//                mMy_authentication_tv_material.setTextColor(0xff79c5f9);
//                mMy_authentication_tv_phone.setTextColor(0xff79c5f9);
//
//                mMy_authentication_iv_autonym.setBackgroundResource(R.drawable.shim_2);
//                mMy_authentication_iv_material.setBackgroundResource(R.drawable.geren_2);
//                mMy_authentication_iv_phone.setBackgroundResource(R.drawable.bangd_1);
//
//                break;
//            default:
//                break;
//        }
//    }
//
//    /**
//     * 设置默认或者认证失败、已提交、审核的状态。 基本资料
//     *
//     * @param str
//     * @param isClick true  可以点击
//     */
//    private void setDefaultOrFailureState(String str, boolean isClick) {
//        mMy_authentication_tv_autonym.setText(str);
//        mMy_authentication_tv_material.setText(str);
//        mMy_authentication_tv_phone.setText(str);
//
//        mMy_authentication_ll_autonym.setEnabled(isClick);
//        mMy_authentication_ll_material.setEnabled(isClick);
//        mMy_authentication_ll_phone.setEnabled(isClick);
//
//        if (isClick) {
//            mMy_authentication_tv_autonym.setTextColor(0xff79c5f9);
//            mMy_authentication_tv_material.setTextColor(0xff79c5f9);
//            mMy_authentication_tv_phone.setTextColor(0xff79c5f9);
//
//            mMy_authentication_iv_autonym.setBackgroundResource(R.drawable.shim_1);
//            mMy_authentication_iv_material.setBackgroundResource(R.drawable.geren_1);
//            mMy_authentication_iv_phone.setBackgroundResource(R.drawable.bangd_1);
//        } else {
//            mMy_authentication_tv_autonym.setTextColor(0xff79c5f9);
//            mMy_authentication_tv_material.setTextColor(0xff79c5f9);
//            mMy_authentication_tv_phone.setTextColor(0xff79c5f9);
//
//            mMy_authentication_iv_autonym.setBackgroundResource(R.drawable.shim_2);
//            mMy_authentication_iv_material.setBackgroundResource(R.drawable.geren_2);
//            mMy_authentication_iv_phone.setBackgroundResource(R.drawable.bangd_2);
//        }
//
//    }
//
//    /**
//     * 社保授权状态
//     *
//     * @param flag
//     */
//    private void jinpoState(int flag) {
//        switch (flag) {
//            case 1:
//                mMy_authentication_tv_jinpo.setText("去授权");
//                mMy_authentication_rl_jinpo.setEnabled(true);
//                mMy_authentication_tv_jinpo.setTextColor(0xff79c5f9);
//                break;
//            case 7:
//                mMy_authentication_tv_jinpo.setText("已通过");
//                mMy_authentication_rl_jinpo.setEnabled(false);
//                mMy_authentication_tv_jinpo.setTextColor(0xff79c5f9);
//                break;
//            case 6:
//                mMy_authentication_tv_jinpo.setText("认证失败");
//                mMy_authentication_rl_jinpo.setEnabled(true);
//                mMy_authentication_tv_jinpo.setTextColor(0xff79c5f9);
//                break;
//            case 8:
//                mMy_authentication_tv_jinpo.setText("已过期");
//                mMy_authentication_rl_jinpo.setEnabled(true);
//                mMy_authentication_tv_jinpo.setTextColor(0xff79c5f9);
//                break;
//            default:
//                mMy_authentication_tv_jinpo.setText("审核中");
//                mMy_authentication_tv_jinpo.setTextColor(0xff79c5f9);
//                mMy_authentication_rl_jinpo.setEnabled(false);
//                break;
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        ActivitysManage.getActivitysManager().finishActivity(this);
//    }
//
//}
