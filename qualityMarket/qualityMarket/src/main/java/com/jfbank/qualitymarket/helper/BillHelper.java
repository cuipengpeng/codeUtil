package com.jfbank.qualitymarket.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfbank.qualitymarket.AppContext;
import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.activity.PrepaymentActivity;
import com.jfbank.qualitymarket.callback.IBillPendingCallBack;
import com.jfbank.qualitymarket.listener.IMeterialClickLisenter;
import com.jfbank.qualitymarket.net.HttpRequest;
import com.jfbank.qualitymarket.util.CommonUtils;
import com.jfbank.qualitymarket.util.ConstantsUtil;
import com.jfbank.qualitymarket.util.LogUtil;
import com.jfbank.qualitymarket.util.StringUtil;
import com.jfbank.qualitymarket.util.UserUtils;
import com.jfbank.qualitymarket.widget.LoadingAlertDialog;
import com.jfbank.qualitymarket.callback.AsyncResponseCallBack;
 import java.util.HashMap; import java.util.Map;

import org.apache.http.Header;

/**
 * 功能：<br>
 * 作者：赵海<br>
 * 时间： 2017/1/3 0003<br>.
 * 版本：1.2.0
 */

public class BillHelper {
    public static final String TAG = BillHelper.class.getName();
    private static Dialog payBillInstalmentDialog =null;

    /**
     * 弹出支付分期账单的对话框
     */
    public static void payBillInstalmentDialog(final Activity activity, final String moneyString, final String billIds, final IBillPendingCallBack.IPayCallBack callBack) {
        if (payBillInstalmentDialog!=null&&payBillInstalmentDialog.isShowing()){
            return;
        }
        payBillInstalmentDialog = new Dialog(activity, R.style.payBillInstalmentDialog);
        payBillInstalmentDialog.setCancelable(false);
        payBillInstalmentDialog.setCanceledOnTouchOutside(false);
        final View dialogView = View.inflate(activity, R.layout.pay_bill_instalment_dialog, null);
        final LinearLayout llBottom = (LinearLayout) dialogView.findViewById(R.id.ll_bottom);
        TextView bankCardNumberTextView = (TextView) dialogView.findViewById(R.id.tv_prepaymentActivity_bankCardNumber);
        TextView ownerNameTextView = (TextView) dialogView.findViewById(R.id.tv_prepaymentActivity_ownerName);
        TextView moneyTextView = (TextView) dialogView.findViewById(R.id.tv_prepaymentActivity_money);
        moneyTextView.setText("¥" + moneyString);
        if (PrepaymentActivity.bankCard != null && !TextUtils.isEmpty(PrepaymentActivity.bankCard.getBankCardNum()) && !TextUtils.isEmpty(PrepaymentActivity.bankCard.getBankName())&& PrepaymentActivity.bankCard.getBankCardNum().length()>=4)
            bankCardNumberTextView.setText(PrepaymentActivity.bankCard.getBankName() + "  (" + PrepaymentActivity.bankCard.getBankCardNum().substring(PrepaymentActivity.bankCard.getBankCardNum().length() - 4) + ")");
        else {
            Toast.makeText(activity, "请先绑定银行卡，再进行还款操作",
                    Toast.LENGTH_LONG).show();
            return;
        }
        String hideNameSymbol = "* * ";
        if (AppContext.user.getIdName().length() <= 2) {
            hideNameSymbol = "* ";
        }
        ownerNameTextView.setText(hideNameSymbol + AppContext.user.getIdName().substring(AppContext.user.getIdName().length() - 1));
        View cancelView = dialogView.findViewById(R.id.tv_prepaymentActivity_cancel);
        CommonUtils.makeMeterial(cancelView, new IMeterialClickLisenter() {
            @Override
            public void onMetrialClick(View v) {
                startBottomViewAmin(llBottom, 0, new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        payBillInstalmentDialog.dismiss();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
        });

        final Button confirmPayButton = (Button) dialogView.findViewById(R.id.btn_prepaymentActivity_confirmPay);
        confirmPayButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                confirmPayButton.setEnabled(false);
                payInstalmentBill(activity, confirmPayButton, billIds, moneyString, callBack);
                startBottomViewAmin(llBottom, 0, new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        payBillInstalmentDialog.dismiss();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }

        });

        payBillInstalmentDialog.setContentView(dialogView);
        payBillInstalmentDialog.setTitle("");

        Window dialogWindow = payBillInstalmentDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
//				dialogWindow.setWindowAnimations(R.style.dialogstyle); // 添加动画
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
        payBillInstalmentDialog.show();
        startBottomViewAmin(llBottom, 1, new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /**
     * 立即支付分期账单
     */
    private static void payInstalmentBill(final Activity activity, final  Button clickButton, String billIds, String clientAmt, final IBillPendingCallBack.IPayCallBack callBack) {

        final LoadingAlertDialog dialog = new LoadingAlertDialog(activity);
        dialog.show(activity.getString(R.string.str_pro_payloading));
        Map<String,String> params = new HashMap<>();
        params.put("uid", AppContext.user.getUid());
        params.put("token", AppContext.user.getToken());
        params.put("billIds", billIds);
        params.put("clientAmt", clientAmt);
        params.put("clientId", "android-安卓");

        HttpRequest.post(activity,HttpRequest.QUALITY_MARKET_WEB_URL + HttpRequest.PAY_INSTALMENT_BILL, params,
                new AsyncResponseCallBack() {

                    @Override
                    public void onFailed(String path, String msg) {
                        dialog.dismiss();
                        Toast.makeText(activity, ConstantsUtil.ORIGIN_PAGE_FAIL_TO_CONNECT_SERVER + "立即支付分期账单失败",
                                Toast.LENGTH_SHORT).show();
                        clickButton.setEnabled(true);
                    }

                    @Override
                    public void onResult(String arg2) {
                        dialog.dismiss();
                        String jsonStr = new String(arg2);
                        LogUtil.printLog("立即支付分期账单：" + jsonStr);
                        JSONObject jsonObject = JSON.parseObject(jsonStr);
                        String errorMsg = "立即支付分期账单失败";
                        int payResult = jsonObject
                                .getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME);
                        if (ConstantsUtil.RESPONSE_SUCCEED == payResult) {
//                            callBack.onPayResult(true,"");
                            errorMsg = jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME);
                            payResultDialog(activity, payResult, errorMsg, callBack);
                        } else if (ConstantsUtil.RESPONSE_TOKEN_FAIL == payResult) {
                            UserUtils.tokenFailDialog(activity, jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME), TAG);
                        } else {
//                            callBack.onPayResult(false,"");
                            if (StringUtil.notEmpty(jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME))) {
                                errorMsg = jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME);
                            }
                            payResultDialog(activity, payResult, errorMsg, callBack);
//                            Toast.makeText(activity, errorMsg, Toast.LENGTH_SHORT).show();
                        }
                        clickButton.setEnabled(true);
                    }
                });
    }

    /**
     * 支付弹出对话框
     *
     * @param activity
     * @param msg
     * @param payResult
     * @param callBack
     */
    private static void payResultDialog(Activity activity, final int payResult, final String msg, final IBillPendingCallBack.IPayCallBack callBack) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        AlertDialog dialog = null;

        dialog = builder.setMessage(msg)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        callBack.onPayResult(payResult, msg);
                    }

                }).create();
        if (dialog != null) {
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
    }

    /**
     * 底部组件显示和隐藏
     *
     * @param view     底部组件
     * @param visible  显示：View.VISIBLE和隐藏 View.GONE
     * @param listener 动画监听
     */
    public static void setBottomViewVisible(View view, int visible, Animation.AnimationListener listener) {
        if (visible == View.VISIBLE) {
            Animation animFadeIn = AnimationUtils.loadAnimation(view.getContext(),
                    R.anim.in_from_bottom);
            view.setVisibility(visible);
            if (listener != null)
                animFadeIn.setAnimationListener(listener);
            view.startAnimation(animFadeIn);

        } else if (visible == View.GONE) {
            Animation animFadeOut = AnimationUtils.loadAnimation(view.getContext(),
                    R.anim.out_from_bottom);
            if (listener != null)
                animFadeOut.setAnimationListener(listener);
            view.startAnimation(animFadeOut);
            view.setVisibility(visible);

        }

    }

    /**
     * 底部组件显示和隐藏
     *
     * @param view       底部组件
     * @param enterOrOut 底部进入：1，底部退出：0
     * @param listener   动画监听
     */
    private static void startBottomViewAmin(View view, int enterOrOut, Animation.AnimationListener listener) {
        if (enterOrOut == 1) {
            Animation animFadeIn = AnimationUtils.loadAnimation(view.getContext(),
                    R.anim.in_from_bottom);
            if (listener != null)
                animFadeIn.setAnimationListener(listener);
            view.startAnimation(animFadeIn);

        } else {
            Animation animFadeOut = AnimationUtils.loadAnimation(view.getContext(),
                    R.anim.out_from_bottom);
            if (listener != null)
                animFadeOut.setAnimationListener(listener);
            view.startAnimation(animFadeOut);
        }
    }
}
