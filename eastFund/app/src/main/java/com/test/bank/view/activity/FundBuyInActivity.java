package com.test.bank.view.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.test.bank.R;
import com.test.bank.base.BaseActivity;
import com.test.bank.base.BaseBean;
import com.test.bank.bean.FundTradeResultBean;
import com.test.bank.bean.GetOutBankCardInfoBean;
import com.test.bank.http.NetService;
import com.test.bank.http.ParamMap;
import com.test.bank.inter.OnResponseListener;
import com.test.bank.utils.Aes;
import com.test.bank.utils.ImageUtils;
import com.test.bank.utils.LogUtils;
import com.test.bank.utils.SPUtil;
import com.test.bank.utils.StringUtil;
import com.test.bank.utils.ToastUtils;
import com.test.bank.utils.UIUtils;
import com.test.bank.view.fragment.PosOrRateDetailDailog;
import com.test.bank.weight.CommonTitleBar;
import com.test.bank.weight.PayPasswordView;
import com.test.bank.weight.dialog.CommonDialogFragment;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;

public class FundBuyInActivity extends BaseActivity {
    @BindView(R.id.ll_fundBuyIn_rootView)
    LinearLayout rootView;
    @BindView(R.id.commonTitleBar_fundBuyIn)
    CommonTitleBar commonTitleBar;
    @BindView(R.id.iv_fundBuyIn_bankIcon)
    ImageView ivBankIcon;
    @BindView(R.id.tv_fundBuyIn_bankNameAndNo)
    TextView tvBankNameAndNo;
    @BindView(R.id.tv_fundBuyIn_bankInfoTip)
    TextView tvBankCardTip;
    @BindView(R.id.et_fundBuyIn_amount)
    EditText etAmount;
    @BindView(R.id.tv_fundBuyIn_rate)
    TextView tvRate;
    @BindView(R.id.tv_fundBuyIn_rateDetail)
    TextView tvRateDetail;
    @BindView(R.id.tv_fundBuyIn_lowestBuyInAmount)
    TextView tvLowestBuyInAmount;
    @BindView(R.id.tv_fundBuyIn)
    TextView tvBuy;
    @BindView(R.id.tv_fundBuyIn_tradeDesc1)
    TextView tvTradeDesc1;
    @BindView(R.id.tv_fundBuyIn_tradeDesc2)
    TextView tvTradeDesc2;
    @BindView(R.id.tv_fundBuyIn_tradeDesc3)
    TextView tvTradeDesc3;
    @BindView(R.id.iv_fundBuyIn_clear)
    ImageView ivClear;


    private String fundCode;
    private String funcName;

    @Override
    protected void init() {
        if (getIntent() != null) {
            fundCode = getIntent().getStringExtra(PARAM_FUND_CODE);
            funcName = getIntent().getStringExtra(PARAM_FUND_NAME);
        }
        commonTitleBar.setPrimaryTitle(funcName);
        commonTitleBar.setSubTitle(fundCode);
        initListener();
    }

    String textBeforeChange = "";
//    boolean hasDot = false;

    private void initListener() {
        tvBuy.setEnabled(false);
        etAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                textBeforeChange = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                LogUtils.e("onTextChanged: " + s.toString());
                ivClear.setVisibility(s.length() == 0 ? View.GONE : View.VISIBLE);
                if (s.length() == 0) {
                    tvBuy.setEnabled(false);
                    tvLowestBuyInAmount.setVisibility(View.GONE);
                } else {
                    if (s.charAt(0) == '.') {       //首位不可输入小数点，若有小数点，小数点后最多可输入两位数
                        etAmount.setText("");
                        return;
                    }

                    if (s.charAt(0) == '0') {   //首位输入0后，不可继续输入整数，可输入小数点及小数点后两位
                        if (s.length() > 1 && s.charAt(1) != '.') {
                            etAmount.setText("0");
                            etAmount.setSelection(etAmount.getText().toString().length());
                            return;
                        }
                        if (s.length() > 4 && s.charAt(1) == '.') {     //若有小数点，小数点后最多可输入两位数
                            s = s.subSequence(0, 4);        //放开逻辑判断
                            etAmount.setText(s);
                            etAmount.setSelection(etAmount.getText().toString().length());
//                            hasDot = true;
                        }
                    }
                    if (s.length() > 9 && !s.toString().contains(".")) {         //输入9位整数后只能输入小数点及两位小数
                        etAmount.setText(s.subSequence(0, 9));
                        etAmount.setSelection(etAmount.getText().toString().length());
                        return;
                    }

                    //小数点.最多输入2位
                    if (s.toString().indexOf(".") != -1 && s.toString().length() > (s.toString().indexOf(".") + 1 + 2)) {
                        s = s.toString().substring(0, (s.toString().indexOf(".") + 1 + 2));
                        etAmount.setText(s.toString());
                        etAmount.setSelection(etAmount.getText().toString().length());
                    }

                    double inputAmount = Double.parseDouble(s.toString().trim());
                    if (inputAmount >= lowestBuyInAmount && inputAmount < Math.min(bankLimitAmount, fundBuyInLimitAmount)) {
                        tvLowestBuyInAmount.setVisibility(View.GONE);
                        tvBuy.setEnabled(true);
                    } else {
                        tvLowestBuyInAmount.setVisibility(View.VISIBLE);
                        if (inputAmount <= lowestBuyInAmount) {
                            tvLowestBuyInAmount.setText("最低买入金额" + lowestBuyInAmount + "元");
                        } else {
                            if (inputAmount > Math.min(bankLimitAmount, fundBuyInLimitAmount)) {
                                tvLowestBuyInAmount.setText(bankLimitAmount < fundBuyInLimitAmount ? "买入金额超过银行卡限额" : "超过基金购买限额");
                            } else if (inputAmount > Math.max(bankLimitAmount, fundBuyInLimitAmount)) {
                                tvLowestBuyInAmount.setText(bankLimitAmount > fundBuyInLimitAmount ? "买入金额超过银行卡限额" : "超过基金购买限额");
                            }
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_fund_buy_in;
    }

    @Override
    protected void doBusiness() {
//        rootView.setVisibility(View.GONE);
        postRequest(new OnResponseListener<GetOutBankCardInfoBean>() {
            @Override
            public Observable<BaseBean<GetOutBankCardInfoBean>> createObservalbe() {
                ParamMap params = new ParamMap();
                params.put("token", SPUtil.getInstance().getToken());
                params.put("fundcode", fundCode);
                params.putLast("type", "0"); //type为0时必须传入fundcode，为1时无需传入默认fundcode为宝宝代码: 按接口要求"活期+"页面type写死传1
                return NetService.getNetService().getBuyInData(params);
            }

            @Override
            public void onResponse(BaseBean<GetOutBankCardInfoBean> result) {
                if (result.isSuccess()) {
//                    rootView.setVisibility(View.VISIBLE);
                    inflateData(result.getData());
                }
            }

            @Override
            public void onError(String errorMsg) {

            }
        });
    }

    GetOutBankCardInfoBean bean;
    Double lowestBuyInAmount = -1d; //最低买入金额
    int bankLimitAmount = -1;   //银行卡限额
    Long fundBuyInLimitAmount = -1L;  //基金最大买入限额
    String fundRiskLevel;

    private void inflateData(GetOutBankCardInfoBean data) {
        if (etAmount == null || tvRate == null || tvTradeDesc2 == null || tvBankCardTip == null) {
            return;
        }
        bean = data;
        lowestBuyInAmount = Double.parseDouble(bean.getPert_val_low_lim());
        bankLimitAmount = bean.getLimit_each();
        fundBuyInLimitAmount = Long.parseLong(bean.getMax_buy_money());
        fundRiskLevel = (String) data.getRisklevel();
        UIUtils.setText(tvBankNameAndNo, data.getBank_name() + "(" + data.getBank_card() + ")");
        etAmount.setHint("最低买入金额" + UIUtils.fillUpZeroInAmountEnd(lowestBuyInAmount, 2) + "元");
        ImageUtils.displayImage(this, data.getBank_logo_url(), ivBankIcon);
        UIUtils.setText(tvTradeDesc2, "* " + data.getConfirmeddate() + "可查询确认份额，当日净值更新后即可查看首笔盈亏");
        UIUtils.setText(tvBankCardTip, "该卡本次最多支付" + StringUtil.transferToDollar(data.getLimit_each() + "") + "元");
        UIUtils.setText(tvRate, "费率：" + data.getBuy_rate());
    }

    @OnClick({R.id.tv_fundBuyIn_rateDetail, R.id.tv_fundBuyIn, R.id.iv_fundBuyIn_clear})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_fundBuyIn_rateDetail:
                MobclickAgent.onEvent(this, "click_btn_fundBuyInActivity_rateDetail");
                if (bean == null)
                    return;
                popRateDetailDialog();
                break;
            case R.id.tv_fundBuyIn:
                MobclickAgent.onEvent(this, "click_btn_fundBuyInActivity_confirmBuyIn");
                ivClear.setVisibility(View.GONE);   //确认购买后，清空icon消失
                checkOutRiskLevel();
                break;
            case R.id.iv_fundBuyIn_clear:
                etAmount.setText("");
                break;
        }
    }

    private String userRiskLevel;

    private void checkOutRiskLevel() {
        userRiskLevel = SPUtil.getInstance().getUserInfo().getRiskLevel();
        LogUtils.e("userRiskLevel: :" + userRiskLevel);
        if (!TextUtils.isEmpty(userRiskLevel)) {
            if ("保守型".equals(userRiskLevel)) {
                showCommonDialog("风险提示\n该基金的风险等级超出您当前的风险承受能力，请重新进行风险测评以确认该产品适合您购买。", "取消", "确认", null, new CommonDialogFragment.OnRightClickListener() {
                    @Override
                    public void onClickRight() {
                        RiskPreferenceActivity.open(FundBuyInActivity.this, userRiskLevel);
                    }
                });
            } else { //若用户为谨慎型（含）及以上，校验基金风险等级与用户风险等级
                if (!verifyUserRiskLevel()) {
                    showCommonDialog("该基金的风险等级超出您当前的风险承受能力。", "取消", "确认购买", null, new CommonDialogFragment.OnRightClickListener() {
                        @Override
                        public void onClickRight() {
                            showTradePwdInputDialog();
                        }
                    });
                } else {
                    showTradePwdInputDialog();
                }
            }
        } else {
            RiskPreferenceActivity.open(FundBuyInActivity.this, "");
        }
    }

    private void showTradePwdInputDialog() {
        inputPayPasswd(this, new PayPasswordView.OnPayListener() {
            @Override
            public void onCancelPay() {

            }

            @Override
            public void onSurePay(String password, final PayPasswordView payPasswordView) {
                buyFund(password);
            }
        });
    }

    private void buyFund(final String password) {
        postRequest(new OnResponseListener<FundTradeResultBean>() {
            @Override
            public Observable<BaseBean<FundTradeResultBean>> createObservalbe() {
                ParamMap paramMap = new ParamMap();
                paramMap.put("token", SPUtil.getInstance().getToken());
                paramMap.put("fundcode", fundCode);
                paramMap.put("buyMoney", etAmount.getText().toString());
                paramMap.put("buytype", "0");
                paramMap.put("pwd", Aes.encryptAES(password));
                paramMap.putLast("fundType", bean.getFundType() + "");
                return NetService.getNetService().buyFund(paramMap);
            }

            @Override
            public void onResponse(final BaseBean<FundTradeResultBean> result) {
                if (result.isSuccess()) {
                    if (payPasswordView != null) {
                        payPasswordView.onCheckPayPwdSuccess(new ImageUtils.OnGifPlayListener() {
                            @Override
                            public void onGifPlayFinish() {
                                FundTradeRecordDetailActivity.open(FundBuyInActivity.this, result.getData().getTradeId(), false);
                            }
                        });
                    }
                } else {
                    if (payPasswordView != null)
                        payPasswordView.onCheckPayPwdFailed(new ImageUtils.OnGifPlayListener() {
                            @Override
                            public void onGifPlayFinish() {
                                if (result.getResCode().equals("3011")) {
                                    showCommonDialog(result.getResMsg(), "重新输入", "忘记密码", null, new CommonDialogFragment.OnRightClickListener() {
                                        @Override
                                        public void onClickRight() {
                                            ResetTradePwdActivity.open(FundBuyInActivity.this);
                                        }
                                    });
                                } else if (result.getResCode().equals("3013") || result.getResCode().equals("3014")) {
                                    showCommonDialog(result.getResMsg(), "取消", "找回密码", null, new CommonDialogFragment.OnRightClickListener() {
                                        @Override
                                        public void onClickRight() {
                                            ResetTradePwdActivity.open(FundBuyInActivity.this);
                                        }
                                    });
                                } else {
                                    ToastUtils.showShort("服务异常，请重试~");
                                }
                            }
                        });
                }
            }

            @Override
            public void onError(final String errorMsg) {
                if (payPasswordView != null)
                    payPasswordView.onCheckPayPwdFailed(new ImageUtils.OnGifPlayListener() {
                        @Override
                        public void onGifPlayFinish() {
//                            ToastUtils.showShort(errorMsg);
                        }
                    });
            }
        }, false);
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
            LogUtils.e("e: " + e.getMessage());
        }
    }

    private boolean verifyUserRiskLevel() {
        if (TextUtils.isEmpty(userRiskLevel)) {
            return false;
        }
        if ("0".equals(fundRiskLevel)) {
            return true;
        }
        if ("6".equals(fundRiskLevel) && "激进型".equals(userRiskLevel)) {
            return true;
        }
        if ("5".equals(fundRiskLevel) && compareRiskLevel(userRiskLevel, "进取型")) {
            return true;
        }
        if ("4".equals(fundRiskLevel) && compareRiskLevel(userRiskLevel, "积极型")) {
            return true;
        }
        if ("3".equals(fundRiskLevel) && compareRiskLevel(userRiskLevel, "积极型")) {
            return true;
        }
        if ("2".equals(fundRiskLevel) && compareRiskLevel(userRiskLevel, "稳健型")) {
            return true;
        }
        if ("1".equals(fundRiskLevel) && compareRiskLevel(userRiskLevel, "稳健型")) {
            return true;
        }
        return false;
    }

    private Map<String, Integer> riskLevelMap;

    private boolean compareRiskLevel(String userRiskLevel, String comparedRiskLevel) {
        if (riskLevelMap == null) {
            riskLevelMap = new HashMap<>();
            riskLevelMap.put("保守型", 1);
            riskLevelMap.put("谨慎型", 2);
            riskLevelMap.put("稳健型", 3);
            riskLevelMap.put("积极型", 4);
            riskLevelMap.put("进取型", 5);
            riskLevelMap.put("激进型", 6);
        }
        boolean isFit = false;
        if (riskLevelMap.containsKey(userRiskLevel) && riskLevelMap.containsKey(comparedRiskLevel)) {
            isFit = riskLevelMap.get(userRiskLevel) >= riskLevelMap.get(comparedRiskLevel);
        }
        return isFit;
    }

    PosOrRateDetailDailog posOrRateDetailDailog;

    private void popRateDetailDialog() {
        posOrRateDetailDailog = (PosOrRateDetailDailog) getSupportFragmentManager().findFragmentByTag("posOrRateDetailDailog");
        if (posOrRateDetailDailog == null) {
            posOrRateDetailDailog = PosOrRateDetailDailog.getInstance(1, bean);
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//                fragmentTransaction.setCustomAnimations(R.anim.bottom_in, R.anim.bottom_out, R.anim.bottom_in, R.anim.bottom_out);
        if (!posOrRateDetailDailog.isAdded()) {
            fragmentTransaction.add(posOrRateDetailDailog, "posOrRateDetailDailog");
            fragmentTransaction.commit();
        }
    }

    private static String PARAM_FUND_CODE = "fundCode";
    private static String PARAM_FUND_NAME = "fundName";

    public static void open(Context context, String fundCode, String fundName) {
        Intent intent = new Intent(context, FundBuyInActivity.class);
        intent.putExtra(PARAM_FUND_CODE, fundCode);
        intent.putExtra(PARAM_FUND_NAME, fundName);
        context.startActivity(intent);
    }
}
