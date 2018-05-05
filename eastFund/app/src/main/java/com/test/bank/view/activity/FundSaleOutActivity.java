package com.test.bank.view.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.test.bank.utils.UIUtils;
import com.test.bank.view.fragment.PosOrRateDetailDailog;
import com.test.bank.weight.CommonTitleBar;
import com.test.bank.weight.PayPasswordView;
import com.test.bank.weight.dialog.CommonDialogFragment;
import com.umeng.analytics.MobclickAgent;

import java.math.BigDecimal;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;

public class FundSaleOutActivity extends BaseActivity {
    @BindView(R.id.rl_fundSaleOut_rootView)
    RelativeLayout rootView;
    @BindView(R.id.commonTitleBar_fundSaleOut)
    CommonTitleBar commonTitleBar;
    @BindView(R.id.et_fundSaleOut)
    EditText etAmount;
    @BindView(R.id.tv_fundSaleOut_saleAll)
    TextView tvSaleAll;
    @BindView(R.id.tv_fundSaleOut_failStatus)
    TextView tvStatus;
    @BindView(R.id.tv_fundSaleOut_rate)
    TextView tvSaleRate;
    @BindView(R.id.tv_fundSaleOut_tradeDesc1)
    TextView tvDesc1;
    @BindView(R.id.tv_fundSaleOut_tradeDesc2)
    TextView tvDesc2;
    @BindView(R.id.tv_fundSaleOut)
    TextView tvSaleOut;
    @BindView(R.id.iv_fundSaleOut_clear)
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


    private void initListener() {
        tvSaleOut.setEnabled(false);
        UIUtils.setViewsVisiiblityOnTextWatcher(etAmount, ivClear);
        etAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ivClear.setVisibility(s.length() == 0 ? View.GONE : View.VISIBLE);
                if (s.length() == 0) {
                    tvSaleOut.setEnabled(false);
                    tvStatus.setVisibility(View.GONE);
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

                    BigDecimal inputAmount = StringUtil.transferToBigDecimal(s.toString().trim());
                    if (inputAmount.compareTo(minSaleOutLog) == -1) {
                        tvStatus.setVisibility(View.VISIBLE);
                        tvStatus.setText("最少卖出" + UIUtils.fillUpZeroInAmountEnd(minSaleOutLog.doubleValue(), 2) + "份");
                        tvSaleOut.setEnabled(false);
                        return;
                    } else {
                        tvStatus.setVisibility(View.GONE);
                        tvSaleOut.setEnabled(true);
                    }

                    //输入份额大于持有份额，则显示所有份额，份额输入正确后，按钮变为可用
                    //持有份额＜最低卖出份额（只能全部赎回）
//                    {@code 1} if {@code this > val}, {@code -1} if {@code this < val},{@code 0} if {@code this == val}.
                    if (inputAmount.compareTo(maxSaleOutLog) == 1 || maxSaleOutLog.compareTo(minSaleOutLog) == -1) {
                        tvStatus.setVisibility(View.GONE);
                        etAmount.setText(maxSaleOutLog + "");
                        etAmount.setSelection(etAmount.getText().toString().length());
                        tvSaleOut.setEnabled(true);
                    } else if (inputAmount.compareTo(maxSaleOutLog) == -1) {
//                        校验剩余份额小于最低保有份额，提示：剩余份额小于最低保有份额，请一起赎回。
                        if ((maxSaleOutLog.subtract(inputAmount)).compareTo(minHoldLog) == -1) {
                            tvStatus.setVisibility(View.VISIBLE);
                            tvStatus.setText("剩余份额小于最低保有份额，请一起赎回");
                            tvSaleOut.setEnabled(false);
                            return;
                        } else {
                            tvStatus.setVisibility(View.GONE);
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
        return R.layout.activity_fund_sale_out;
    }

    @Override
    protected void doBusiness() {
//        rootView.setVisibility(View.GONE);
        postRequest(new OnResponseListener<GetOutBankCardInfoBean>() {
            @Override
            public Observable<BaseBean<GetOutBankCardInfoBean>> createObservalbe() {
                ParamMap params = new ParamMap();
                params.put("token", SPUtil.getInstance().getToken());
                params.put("fundcode", "001581");
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

    private BigDecimal maxSaleOutLog; //最大赎回份额
    private BigDecimal minSaleOutLog; //最低赎回份额
    private BigDecimal minHoldLog;     //最低持有份额


    private void inflateData(GetOutBankCardInfoBean data) {
        bean = data;
        maxSaleOutLog = StringUtil.transferToBigDecimal(bean.getMax_redemption_stare());
        minSaleOutLog = StringUtil.transferToBigDecimal(bean.getPer_min());
        minHoldLog = StringUtil.transferToBigDecimal(bean.getHoldmin());

        UIUtils.setText(tvSaleRate, "卖出费率：" + bean.getRedemption_rate());
        etAmount.setHint("最多可卖出" + bean.getMax_redemption_stare() + "份");
        UIUtils.setText(tvSaleRate, "费率：" + bean.getRedemption_rate());
        UIUtils.setText(tvDesc1, "预计到账时间：" + bean.getRedeemrefunddate() + "前");
    }

    @OnClick({R.id.tv_fundSaleOut_rateDetail, R.id.tv_fundSaleOut, R.id.iv_fundSaleOut_clear, R.id.tv_fundSaleOut_saleAll})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_fundSaleOut_rateDetail:
                MobclickAgent.onEvent(this, "click_btn_fundSaleOutActivity_rateDetail");
                if (bean == null)
                    return;
                popRateDetailDialog();
                break;
            case R.id.tv_fundSaleOut:
                MobclickAgent.onEvent(this, "click_btn_fundSaleOutActivity_confirmSaleOut");
                ivClear.setVisibility(View.GONE);
                showTradePwdInputDialog();
                break;
            case R.id.iv_fundSaleOut_clear:
                etAmount.setText("");
                break;
            case R.id.tv_fundSaleOut_saleAll:
                MobclickAgent.onEvent(this, "click_btn_fundSaleOutActivity_saleAll");
                etAmount.setText(bean.getMax_redemption_stare());
                etAmount.setSelection(etAmount.getText().toString().length());
                tvStatus.setVisibility(View.GONE);
                tvSaleOut.setEnabled(true);
                break;
        }
    }

    private void showTradePwdInputDialog() {
        inputPayPasswd(this, new PayPasswordView.OnPayListener() {
            @Override
            public void onCancelPay() {

            }

            @Override
            public void onSurePay(String password, PayPasswordView payPasswordView) {
                saleOutFund(password);
            }
        });
    }

    private void saleOutFund(final String password) {
        postRequest(new OnResponseListener<FundTradeResultBean>() {
            @Override
            public Observable<BaseBean<FundTradeResultBean>> createObservalbe() {
                ParamMap paramMap = new ParamMap();
                paramMap.put("token", SPUtil.getInstance().getToken());
                paramMap.put("buyShare", etAmount.getText().toString());
                paramMap.put("buytype", "0");
                paramMap.put("fundcode", "001581");
                paramMap.put("pwd", Aes.encryptAES(password));
                paramMap.putLast("fundType", bean.getFundType() + "");
                return NetService.getNetService().saleOutFund(paramMap);
            }

            @Override
            public void onResponse(final BaseBean<FundTradeResultBean> result) {
                if (result.isSuccess()) {
                    if (payPasswordView != null) {
                        payPasswordView.onCheckPayPwdSuccess(new ImageUtils.OnGifPlayListener() {
                            @Override
                            public void onGifPlayFinish() {
                                FundTradeRecordDetailActivity.open(FundSaleOutActivity.this, result.getData().getTradeId(), false);
                            }
                        });
                    }
                } else {
                    if (payPasswordView != null) {
                        payPasswordView.onCheckPayPwdFailed(new ImageUtils.OnGifPlayListener() {
                            @Override
                            public void onGifPlayFinish() {
                                if (result.getResCode().equals("3011")) {
                                    showCommonDialog(result.getResMsg(), "重新输入", "忘记密码", null, new CommonDialogFragment.OnRightClickListener() {
                                        @Override
                                        public void onClickRight() {
                                            ResetTradePwdActivity.open(FundSaleOutActivity.this);
                                        }
                                    });
                                } else if (result.getResCode().equals("3013") || result.getResCode().equals("3014")) {
                                    showCommonDialog(result.getResMsg(), "取消", "找回密码", null, new CommonDialogFragment.OnRightClickListener() {
                                        @Override
                                        public void onClickRight() {
                                            ResetTradePwdActivity.open(FundSaleOutActivity.this);
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            }

            @Override
            public void onError(String errorMsg) {
                if (payPasswordView != null) {
                    payPasswordView.onCheckPayPwdFailed();
                }
            }
        }, false);
    }

    PosOrRateDetailDailog posOrRateDetailDailog;

    private void popRateDetailDialog() {
        posOrRateDetailDailog = (PosOrRateDetailDailog) getSupportFragmentManager().findFragmentByTag("posOrRateDetailDailog");
        if (posOrRateDetailDailog == null) {
            posOrRateDetailDailog = PosOrRateDetailDailog.getInstance(2, bean);
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//                fragmentTransaction.setCustomAnimations(R.anim.bottom_in, R.anim.bottom_out, R.anim.bottom_in, R.anim.bottom_out);
        if (!posOrRateDetailDailog.isAdded()) {
            fragmentTransaction.add(posOrRateDetailDailog, "posOrRateDetailDailog");
            fragmentTransaction.commit();
        }
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
            LogUtils.e("!!!!!!!!!!!!!!: " + e.getMessage());
        }
    }

    private static String PARAM_FUND_CODE = "fundCode";
    private static String PARAM_FUND_NAME = "fundName";

    public static void open(Context context, String fundCode, String fundName) {
        Intent intent = new Intent(context, FundSaleOutActivity.class);
        intent.putExtra(PARAM_FUND_CODE, fundCode);
        intent.putExtra(PARAM_FUND_NAME, fundName);
        context.startActivity(intent);
    }
}
