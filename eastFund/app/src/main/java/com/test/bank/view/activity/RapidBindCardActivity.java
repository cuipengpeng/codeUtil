package com.test.bank.view.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.test.bank.R;
import com.test.bank.base.BaseActivity;
import com.test.bank.base.BaseBean;
import com.test.bank.bean.BankSmsCodeBean;
import com.test.bank.http.NetService;
import com.test.bank.http.ParamMap;
import com.test.bank.inter.OnResponseListener;
import com.test.bank.utils.Aes;
import com.test.bank.utils.BankTextWatcher;
import com.test.bank.utils.ImageUtils;
import com.test.bank.utils.LogUtils;
import com.test.bank.utils.MyCountDownTimer;
import com.test.bank.utils.SPUtil;
import com.test.bank.utils.StringUtil;
import com.test.bank.utils.ToastUtils;
import com.test.bank.utils.UIUtils;
import com.test.bank.weight.PayPasswordView;
import com.test.bank.weight.dialog.CommonDialogFragment;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;

public class RapidBindCardActivity extends BaseActivity {
    private static final String TAG = "RapidBindCardActivity";

    @BindView(R.id.et_rapidBindCard_name)
    EditText etName;
    @BindView(R.id.et_rapidBindCard_idCard)
    EditText etIDCard;
    @BindView(R.id.et_rapidBindCard_bankNo)
    EditText etBankNo;
    @BindView(R.id.et_rapidBindCard_phoneNum)
    EditText etPhoneNum;
    @BindView(R.id.et_rapidBindCard_checkCode)
    EditText etCheckCode;
    @BindView(R.id.tv_rapidBindCard_checkSupportBankAndAmount)
    TextView tvCheckSupportBankAndAmount;
    @BindView(R.id.tv_rapidBindCard_getCheckCode)
    TextView tvGetCheckCode;
    @BindView(R.id.iv_rapidBindCard_check)
    ImageView ivCheck;
    @BindView(R.id.iv_rapidBindCard_clearIdCard)
    ImageView ivClearIDCard;
    @BindView(R.id.iv_rapidBindCard_clearBankNo)
    ImageView ivClearBankNo;
    @BindView(R.id.tv_rapidBindCard_protocalAgree)
    TextView tvProtocalAgree;
    @BindView(R.id.tv_rapidBindCard_jinniuProtocal)
    TextView tvJinniuProtocal;
    @BindView(R.id.tv_rapidBindCard_bankProtocal)
    TextView tvBankProtocal;

    @BindView(R.id.tv_rapidBindCard_nextStep)
    TextView tvNextStep;

    private MyCountDownTimer countDownTimer;

    private boolean isGoRiskTestAfterOpenAccount = false;

    private String bankProtocalUrl;

    @Override
    protected void init() {
        if (getIntent() != null) {
            isGoRiskTestAfterOpenAccount = getIntent().getBooleanExtra(PARAM_IS_GO_RISK_TEST_AFTER_OPEN_ACCOUNT, false);
        }
        initView();
        initListener();
        countDownTimer = new MyCountDownTimer(tvGetCheckCode);
    }

    private void initView() {
        ivCheck.setSelected(true);      //默认勾选
    }


    private void initListener() {
        BankTextWatcher.bind(etBankNo, ivClearBankNo);       //获取信息放到获取验证码处
        UIUtils.setViewsVisiiblityOnTextWatcher(etIDCard, ivClearIDCard);
        etIDCard.setKeyListener(UIUtils.getIDCardFilter());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_rapid_bind_card;
    }

    @Override
    protected void doBusiness() {

    }

    private void inflateBankInfo(String bankName) {
        initProtocal(bankName);
        if ("招商银行".equals(bankName)) {
            bankProtocalUrl = getResources().getString(R.string.protocal_zhaoshang);
        } else if ("华夏银行".equals(bankName)) {
            bankProtocalUrl = getResources().getString(R.string.protocal_huaxia);
        } else if ("浦发银行".equals(bankName)) {
            bankProtocalUrl = getResources().getString(R.string.protocal_pufa);
        }
    }


    private void initProtocal(String bankName) {
        String leftProtocal = "已阅读并同意《金牛基金服务协议》";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < leftProtocal.length(); i++) {
            sb.append("　");     //全角空格
        }
        if (!TextUtils.isEmpty(bankName)) {
            if (bankName.equals("浦发银行") || bankName.equals("招商银行") || bankName.equals("华夏银行"))
                sb.append("　、").append("《").append(bankName).append("快捷支付服务协议》").toString();
        }
        tvBankProtocal.setText(sb.toString());
    }


//服务协议 默认勾选，当银行择为： 默认勾选，当银行择为： 默认勾选，当银行择为： 默认勾选，当银行择为： 默认勾选，当银行择为： 默认勾选，当银行择为： 默认勾选，当银行择为： 默认勾选，当银行择为： 默认勾选，当银行择为： 默认勾选，当银行择为： 默认勾选，当银行择为： 默认勾选，当银行择为： 浦发 银行 、招商银行 、招商银行 、招商银行 、招商银行 、招商银行 、招商、华夏银行时 华夏银行时 华夏银行时 ，服务 协议增加一份 协议增加一份 协议增加一份 《XX 银行快捷支付服务 银行快捷支付服务 银行快捷支付服务 银行快捷支付服务 协议 》

    @OnClick({R.id.tv_rapidBindCard_getCheckCode, R.id.iv_rapidBindCard_clearIdCard, R.id.iv_rapidBindCard_clearBankNo, R.id.iv_rapidBindCard_check, R.id.tv_rapidBindCard_checkSupportBankAndAmount, R.id.tv_rapidBindCard_protocalAgree, R.id.tv_rapidBindCard_jinniuProtocal, R.id.tv_rapidBindCard_bankProtocal, R.id.tv_rapidBindCard_nextStep})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.tv_rapidBindCard_getCheckCode:
                getBankCheckCode();
                break;
            case R.id.iv_rapidBindCard_clearIdCard:
                etIDCard.setText("");
                break;
            case R.id.iv_rapidBindCard_clearBankNo:
                etBankNo.setText("");
                break;
            case R.id.iv_rapidBindCard_check:
                ivCheck.setSelected(!ivCheck.isSelected());
                tvNextStep.setEnabled(ivCheck.isSelected());
                break;
            case R.id.tv_rapidBindCard_checkSupportBankAndAmount:
                SupportBankAndAmountActivity.open(this);
                break;
            case R.id.tv_rapidBindCard_protocalAgree:
                break;
            case R.id.tv_rapidBindCard_jinniuProtocal:
//                SmartServerActivity.open(this);
                SimpleH5Activity.open(this, getResources().getString(R.string.protocal_jinniu));
                break;
            case R.id.tv_rapidBindCard_bankProtocal:
                SimpleH5Activity.open(this, bankProtocalUrl);
                break;
            case R.id.tv_rapidBindCard_nextStep:
                openAccount();
                break;
        }
    }

    private void openAccount() {
        if (TextUtils.isEmpty(etName.getText().toString().trim())) {
            ToastUtils.showShort("姓名不可为空");
            return;
        }
        if (TextUtils.isEmpty(etIDCard.getText().toString().trim())) {
            ToastUtils.showShort("身份证号码不可为空");
            return;
        }
        if (TextUtils.isEmpty(getRealBankNo())) {
            ToastUtils.showShort("银行卡号不可为空");
            return;
        }
        if (TextUtils.isEmpty(etPhoneNum.getText().toString().trim())) {
            ToastUtils.showShort("银行预留手机号不可为空");
            return;
        }
        if (TextUtils.isEmpty(etCheckCode.getText().toString().trim())) {
            ToastUtils.showShort("验证码不可为空");
            return;
        }
        if (TextUtils.isEmpty(accountRequestSerial)) {
            ToastUtils.showShort("请先获取验证码");
            return;
        }

        if (tvNextStep.isEnabled()) {
            showTradePwdInputDialog();
        }
    }


    private void requestOpenAccount() {
        postRequest(new OnResponseListener<String>() {
            @Override
            public Observable<BaseBean<String>> createObservalbe() {
                ParamMap paramMap = new ParamMap();
                paramMap.put("token", SPUtil.getInstance().getToken());
                paramMap.put("accoName", etName.getText().toString().trim());
                paramMap.put("bankno", Aes.encryptAES(getRealBankNo()));
                paramMap.put("channelShortName", "JinNiuLiCai");
                paramMap.put("idCard", Aes.encryptAES(etIDCard.getText().toString().trim()));
                paramMap.put("accoMobile", Aes.encryptAES(etPhoneNum.getText().toString().trim()));
                paramMap.put("pwd", Aes.encryptAES(tradePwd));
                paramMap.put("authCode", Aes.encryptAES(etCheckCode.getText().toString().trim()));      //短信验证码
                paramMap.putLast("accoreqserial", Aes.encryptAES(accountRequestSerial));    //短信验证码接口传过来的一个序列号
//                paramMap.put("bankName", bankInfoBean.getBankname());
//                paramMap.putLast("bankSerial", bankInfoBean.getBankno());
                return NetService.getNetService().openAccount(paramMap);
            }

            @Override
            public void onResponse(BaseBean<String> result) {
                if (result.isSuccess()) {
                    if (payPasswordView != null)
                        payPasswordView.onCheckPayPwdSuccess(new ImageUtils.OnGifPlayListener() {
                            @Override
                            public void onGifPlayFinish() {
                                if (isGoRiskTestAfterOpenAccount) {     //开户完成之后跳转到风险测评指导页【开户与该风险测评均出现一次】
                                    startActivity(new Intent(RapidBindCardActivity.this, RiskTestResultActivity.class));
                                }
                                finish();
                            }
                        }, "绑卡成功");

                } else {        //{"data":null,"msgStatus":true,"resCode":"3203","resMsg":"验证码错误"}
                    if (payPasswordView != null)
                        payPasswordView.onCheckPayPwdFailed("绑卡失败");
                }
            }

            @Override
            public void onError(String errorMsg) {
                if (payPasswordView != null)
                    payPasswordView.onCheckPayPwdFailed("绑卡失败");
            }
        }, false);
    }

    private String accountRequestSerial = "";

    private void getBankCheckCode() {
        if (TextUtils.isEmpty(etName.getText().toString().trim())) {
            ToastUtils.showShort("姓名不可为空");
            return;
        }
        if (TextUtils.isEmpty(etIDCard.getText().toString().trim())) {
            ToastUtils.showShort("身份证号码不可为空");
            return;
        }
        if (TextUtils.isEmpty(getRealBankNo())) {
            ToastUtils.showShort("银行卡号不可为空");
            return;
        }
        if (TextUtils.isEmpty(etPhoneNum.getText().toString().trim())) {
            ToastUtils.showShort("银行预留手机号不可为空");
            return;
        }

        postRequest(new OnResponseListener<BankSmsCodeBean>() {
            @Override
            public Observable<BaseBean<BankSmsCodeBean>> createObservalbe() {
                ParamMap paramMap = new ParamMap();
                paramMap.put("token", SPUtil.getInstance().getToken());
                paramMap.put("accoMobile", Aes.encryptAES(etPhoneNum.getText().toString().trim()));
                paramMap.put("bankno", Aes.encryptAES(getRealBankNo()));
                paramMap.put("accoName", etName.getText().toString().trim());
                paramMap.putLast("idCard", Aes.encryptAES(etIDCard.getText().toString().trim()));
                return NetService.getNetService().getBankCheckCode(paramMap);
            }

            @Override
            public void onResponse(BaseBean<BankSmsCodeBean> result) {
                if (result != null && result.isSuccess()) {
                    inflateBankInfo(result.getData().getBankInfo().getBankname());
                    accountRequestSerial = result.getData().getAuthCode().getAccoreqserial();
                    LogUtils.e("验证序列号： " + accountRequestSerial);
                    if (countDownTimer != null) {
                        countDownTimer.start();
                    }
                    ToastUtils.showShort("验证码已发送");
                }
            }

            @Override
            public void onError(String errorMsg) {

            }
        });
    }

    /**
     * 获取真实的银行卡号【去掉所有空格】
     *
     * @return
     */
    private String getRealBankNo() {
        return etBankNo.getText().toString().replaceAll(" ", "").trim();
    }

    public static boolean isFirstInput = true;
    private String firstInputTradePwd = "";
    private String tradePwd;

    private void showTradePwdInputDialog() {
        inputPayPasswd(this, new PayPasswordView.OnPayListener() {
            @Override
            public void onCancelPay() {

            }

            @Override
            public void onSurePay(String password, final PayPasswordView payPasswordView) {
                if (isFirstInput) {
                    firstInputTradePwd = password;
                    isFirstInput = false;
                    if (StringUtil.isWeakPassword(password)) {
                        showOneBtnDialog("您设置的密码过于简单，请重新输入", "确定");
                        isFirstInput = true;
                        payPasswordView.showSetTradePwdStatus();
                    } else {
                        payPasswordView.showSetTradePwdAgain(firstInputTradePwd);
                    }
                } else {
                    if (firstInputTradePwd.equals(password)) {      //第一次输入
                        tradePwd = password;
                        requestOpenAccount();
                        isFirstInput = true;
                        firstInputTradePwd = "";
                    } else {
                        showCommonDialog("您的密码两次输入不一致", "重新设置", "再次输入", new CommonDialogFragment.OnLeftClickListener() {
                            @Override
                            public void onClickLeft() {
                                isFirstInput = true;
                                firstInputTradePwd = "";
                                payPasswordView.showSetTradePwdStatus();
                            }
                        }, new CommonDialogFragment.OnRightClickListener() {
                            @Override
                            public void onClickRight() {
                                payPasswordView.showSetTradePwdAgain(firstInputTradePwd);
                            }
                        });
                    }
                }
            }
        });
    }

    private PayPasswordView payPasswordView;

    /**
     * 弹出交易密码对话框
     *
     * @param activity
     */
    public void inputPayPasswd(final Activity activity, PayPasswordView.OnPayListener onPayListener) {
        try {
            Dialog tradePasswordDialog = new Dialog(activity, R.style.payBillInstalmentDialog);
            payPasswordView = new PayPasswordView(activity, tradePasswordDialog, onPayListener);
            final View dialogView = payPasswordView.getView();
            payPasswordView.showSetTradePwdStatus();
            tradePasswordDialog.setCancelable(true);
            tradePasswordDialog.setCanceledOnTouchOutside(true);
            tradePasswordDialog.setContentView(dialogView);
            tradePasswordDialog.setTitle("");
            Window dialogWindow = tradePasswordDialog.getWindow();
            dialogWindow.setGravity(Gravity.BOTTOM);
//		dialogWindow.setWindowAnimations(R.style.dialogstyle); // 添加动画
            WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
            dialogWindow.setBackgroundDrawable(activity.getResources().getDrawable(R.color.transparent));
            lp.x = 0; // 新位置X坐标
            lp.y = -20; // 新位置Y坐标
            lp.width = (int) activity.getResources().getDisplayMetrics().widthPixels; // 宽度
//		lp.height = WindowManager.LayoutParams.WRAP_CONTENT; // 高度
//		dialogView.measure(0,0);
            lp.height = (int) activity.getResources().getDisplayMetrics().heightPixels;
            lp.alpha = 1f; // 透明度
            dialogWindow.setAttributes(lp);
            tradePasswordDialog.show();
        } catch (Exception e) {
            LogUtils.e("!!!!!!!!!!!!!: " + e.getMessage());
        }
    }

    public static void open(Context context) {
        open(context, true);
    }

    private static String PARAM_IS_GO_RISK_TEST_AFTER_OPEN_ACCOUNT = "isGoRiskTestAfterOpenAccount";

    public static void open(Context context, boolean isGoRiskTestAfterOpenAcocount) {
        Intent intent = new Intent(context, RapidBindCardActivity.class);
        intent.putExtra(PARAM_IS_GO_RISK_TEST_AFTER_OPEN_ACCOUNT, isGoRiskTestAfterOpenAcocount);
        context.startActivity(intent);
    }
}
