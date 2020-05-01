//package com.jfbank.qualitymarket.activity;
//
//import android.annotation.SuppressLint;
//import android.content.Intent;
//import android.graphics.drawable.Drawable;
//import android.os.Bundle;
//import android.support.v4.app.FragmentManager;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.KeyEvent;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.Window;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.jfbank.qualitymarket.ActivitysManage;
//import com.jfbank.qualitymarket.AppContext;
//import com.jfbank.qualitymarket.R;
//import com.jfbank.qualitymarket.base.BaseActivity;
//import com.jfbank.qualitymarket.bean.PhoneData;
//import com.jfbank.qualitymarket.fragment.ApplySubmitfinishFragment;
//import com.jfbank.qualitymarket.fragment.BasicInfoFragment;
//import com.jfbank.qualitymarket.fragment.BasicInfoFragment.OnClickBtnListen;
//import com.jfbank.qualitymarket.fragment.IdentityAuthenticationFragment;
//import com.jfbank.qualitymarket.fragment.JinpoAuthorizationFragment;
//import com.jfbank.qualitymarket.fragment.PhoneVerificationFragment;
//import com.jfbank.qualitymarket.fragment.SesameCreditAuthorizationFragment;
//import com.jfbank.qualitymarket.model.AddressInfoBean;
//import com.jfbank.qualitymarket.util.CommonUtils;
//
//import qiu.niorgai.StatusBarCompat;
//
///**
// * 申请额度对应的页面
// *
// * @author 彭爱军
// * @date 2016年8月11日
// */
//public class ApplyAmountActivity extends BaseActivity {
//    /**
//     * 进入我的授权第几步：1：第一步实名认证 2:个人资料  3：绑定手机
//     */
//    public static final String KEY_OF_APPLYAMOUNT_STEP = "STEP";
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
//     * 实名认证
//     */
//    private ImageView mApply_amount_iv_autonym;
//    private View mApply_amount_view_autonym;
//    private TextView mApply_amount_tv_autonym;
//    /**
//     * 个人资料
//     */
//    private ImageView mApply_amount_iv_personage;
//    private View mApply_amount_view_right_personage;
//    private View mApply_amount_view_left_personage;
//    private TextView mApply_amount_tv_personage;
//    /**
//     * 绑定手机
//     */
//    private ImageView mApply_amount_iv_phone;
//    private View mApply_amount_view_phone;
//    private TextView mApply_amount_tv_phone;
//
//    /**
//     * 基本信息
//     */
//    private BasicInfoFragment mBasecInfoFragment;
//    /**
//     * 芝麻信用
//     */
//    private SesameCreditAuthorizationFragment mSesameFragment;
//    /**
//     * 手机验证
//     */
//    private PhoneVerificationFragment mPhoneFragment;
//    /**
//     * 身份认证
//     */
//    private IdentityAuthenticationFragment mIdentityFragment;
//    /**
//     * 社保
//     */
//    private JinpoAuthorizationFragment mJinpoFragment;
//    /**
//     * 提交成功
//     */
//    private ApplySubmitfinishFragment mSubmitFinish;
//    private FragmentManager fragmentManager;
//    /**
//     * 任务编码
//     */
//    protected String mTaskNo;
//    private int step; // 当前步骤
//    protected PhoneData mPhoneData;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        ActivitysManage.getActivitysManager().addActivity(this);
//        setContentView(R.layout.activity_apply_amount);
//        AppContext.setAddressInfo(new AddressInfoBean());
//        bindViews();
//    }
//
//    /**
//     * 初始化View以及设置监听
//     */
//    private void bindViews() {
//        rlTitle = (RelativeLayout) findViewById(R.id.rl_title);
//        ivBack = (ImageView) findViewById(R.id.iv_back);
//        tvTitle = (TextView) findViewById(R.id.tv_title);
//        CommonUtils.setTitle(this, rlTitle);
//        mApply_amount_iv_autonym = (ImageView) findViewById(R.id.apply_amount_iv_autonym);
//        mApply_amount_view_autonym = (View) findViewById(R.id.apply_amount_view_autonym);
//        mApply_amount_tv_autonym = (TextView) findViewById(R.id.apply_amount_tv_autonym);
//
//        mApply_amount_iv_personage = (ImageView) findViewById(R.id.apply_amount_iv_personage);
//        mApply_amount_view_right_personage = (View) findViewById(R.id.apply_amount_view_right_personage);
//        mApply_amount_view_left_personage = (View) findViewById(R.id.apply_amount_view_left_personage);
//        mApply_amount_tv_personage = (TextView) findViewById(R.id.apply_amount_tv_personage);
//
//        mApply_amount_iv_phone = (ImageView) findViewById(R.id.apply_amount_iv_phone);
//        mApply_amount_view_phone = (View) findViewById(R.id.apply_amount_view_phone);
//        mApply_amount_tv_phone = (TextView) findViewById(R.id.apply_amount_tv_phone);
//
//        tvTitle.setText("申请额度");
//
//        Drawable drawable = getResources().getDrawable(R.mipmap.ic_delete_gray);
//        ivBack.setImageDrawable(drawable);
//        ivBack.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                AppContext.user.setFaceCode("");
//
//                if (4 == step) {
//                    showFragment(step - 1);
//                } else {
//                    finish();
//                }
//            }
//        });
//        // 步骤一：添加一个FragmentTransaction的实例
//        fragmentManager = getSupportFragmentManager();
//        // 步骤二：用add()方法加上Fragment的对象rightFragment
//        Intent intent = getIntent();
//        if (null != intent) {
//            step = intent.getIntExtra(KEY_OF_APPLYAMOUNT_STEP, 1);
//            if (4 == step) {
//                showFragment(step + 1);
//            } else {
//                showFragment(step);
//            }
//        } else {
//            Toast.makeText(this, "获取step失败", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//
//    /**
//     * 根据不同的标志显示不同的fragment
//     *
//     * @param flag
//     */
//    private void showFragment(int flag) {
//        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
//        step = flag;
//        switch (flag) {
//            case 1:
//                tvTitle.setText("基本认证");
//                if (null == mIdentityFragment) {
//                    mIdentityFragment = new IdentityAuthenticationFragment();
//                }
//                transaction.replace(R.id.apply_amount_framegment, mIdentityFragment);
//                mIdentityFragment.setmOnClickBtnListen(new IdentityAuthenticationFragment.OnClickBtnListen() {
//
//                    @Override
//                    public void next(int step) {
//                        showFragment(step);
//                    }
//                });
//
//                break;
//            case 2:
//                setImageView(1);
//                tvTitle.setText("个人资料");
//                if (null == mBasecInfoFragment) {
//                    mBasecInfoFragment = new BasicInfoFragment();
//                }
//                transaction.replace(R.id.apply_amount_framegment, mBasecInfoFragment);
//                mBasecInfoFragment.setmOnClickBtnListen(new OnClickBtnListen() {
//
//                    @Override
//                    public void next(int step) {
//                        showFragment(step);
//                    }
//                });
//                break;
//            case 3:
//                setImageView(2);
//                tvTitle.setText("绑定手机号");
//                if (null == mPhoneFragment) {
//                    mPhoneFragment = new PhoneVerificationFragment();
//                } else {
//                    mSesameFragment = null;            //弄点小聪明。为了这个页面出现的情况。目前还没有相好
//                }
//                transaction.replace(R.id.apply_amount_framegment, mPhoneFragment);
//                mPhoneFragment.setmOnClickBtnListen(new PhoneVerificationFragment.OnClickBtnListen() {
//
//                    @Override
//                    public void next(boolean isHasNext, int step, PhoneData data) {
//                        // showFragment(4);
//                        if (isHasNext) {
//                            mPhoneData = data;
//                            showFragment(4);
//                        } else {
//                            showFragment(step);    //成功
//                        }
//                    }
//                });
//
//                break;
//            case 4:
//                setImageView(2);
//                tvTitle.setText("绑定手机号2/2");
//                if (null == mSesameFragment) {
//                    mSesameFragment = new SesameCreditAuthorizationFragment();
//                }
//                Bundle args = new Bundle();
//                args.putParcelable("DATA", mPhoneData);
//                mSesameFragment.setArguments(args);
//                transaction.replace(R.id.apply_amount_framegment, mSesameFragment);
//                mSesameFragment.setmOnClickBtnListen(new SesameCreditAuthorizationFragment.OnClickBtnListen() {
//
//                    @Override
//                    public void next(int step) {
//                        showFragment(step);
//                    }
//                });
//                break;
//            case 0:
//                setImageView(3);
//                Intent intent = new Intent(this, MyAuthenticationActivity.class);
//                intent.putExtra(MyAuthenticationActivity.KEY_OF_ENTER_MYAUTH_ORJHBT, false);
//                startActivity(intent);
//                finish();
//                break;
//
//            default:
//                Toast.makeText(this, "获取step失败", Toast.LENGTH_SHORT).show();
//                break;
//        }
//        transaction.commit();
//    }
//
//    /**
//     * 设置基本资料填写的步骤对应的图片
//     *
//     * @param step
//     */
//    private void setImageView(int step) {
//        switch (step) {
//            case 1:
//                setAutonymState();
//                break;
//            case 2:
//                setAutonymState();
//                setPersonageState();
//                break;
//            case 3:
//                setAutonymState();
//                setPersonageState();
//                setPhoneState();
//                break;
//
//            default:
//                break;
//        }
//    }
//
//    /**
//     * 设置绑定手机显示的状态
//     */
//    private void setPhoneState() {
//        mApply_amount_iv_phone.setBackgroundResource(R.drawable.bangd_2);
//        mApply_amount_tv_phone.setTextColor(0xff79c5f9);
//    }
//
//    /**
//     * 设置个人资料显示的状态
//     */
//    private void setPersonageState() {
//        mApply_amount_iv_personage.setBackgroundResource(R.drawable.geren_2);
//        mApply_amount_view_right_personage.setBackgroundColor(0xff79c5f9);
//        mApply_amount_tv_personage.setTextColor(0xff79c5f9);
//        mApply_amount_view_phone.setBackgroundColor(0xff79c5f9);
//    }
//
//    /**
//     * 设置实名认证显示的状态
//     */
//    private void setAutonymState() {
//        mApply_amount_iv_autonym.setBackgroundResource(R.drawable.shim_2);
//        mApply_amount_view_autonym.setBackgroundColor(0xff79c5f9);
//        mApply_amount_tv_autonym.setTextColor(0xff79c5f9);
//        mApply_amount_view_left_personage.setBackgroundColor(0xff79c5f9);
//    }
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            AppContext.user.setFaceCode("");
//
//            if (4 == step) {
//                showFragment(step - 1);
//                return true;
//            }
//        }
//        Log.e("TAG", "step:" + step);
//        return super.onKeyDown(keyCode, event);
//    }
//
//    @Override
//    protected void onResume() {
//        // TODO Auto-generated method stub
//        super.onResume();
//    }
//
//    @Override
//    protected String getPageName() {
//        String titleName = tvTitle.getText().toString();
//        if (TextUtils.isEmpty(titleName))
//            titleName = getClass().getName();
//        return titleName;
//    }
//
//    @SuppressLint("NewApi")
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (grantResults.length > 0) {
//            MainActivity.showDenyPermissionDialog(this, requestCode, grantResults);
//        }
//
//    }
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        Log.e("TAG", "act onSaveInstanceState");
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        ActivitysManage.getActivitysManager().finishActivity(this);
//    }
//}
