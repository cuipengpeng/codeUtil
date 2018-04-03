package com.jf.jlfund.weight;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jf.jlfund.R;
import com.jf.jlfund.utils.ImageUtils;
import com.jf.jlfund.view.activity.ResetTradePwdActivity;

import java.util.ArrayList;

/**
 * 支付密码键盘
 * 作者：崔朋朋<br>
 */


@SuppressLint("InflateParams")
public class PayPasswordView implements OnClickListener {

    private TextView tvTitle;
    private RelativeLayout del;
    private ImageView closeDialog;
    private ImageView point;

    private TextView zero;
    private TextView one;
    private TextView two;
    private TextView three;
    private TextView four;
    private TextView five;
    private TextView six;
    private TextView seven;
    private TextView eight;
    private TextView nine;

    private TextView forgetOneCardTradePasswordTextView;
    private ImageView box1;
    private ImageView box2;
    private ImageView box3;
    private ImageView box4;
    private ImageView box5;
    private ImageView box6;

    private LinearLayout llKeyboard;
    private RelativeLayout rlGif;
    private ImageView ivGif;
    private TextView tvBindBankCardResult;  //提示绑卡成功或失败

    public ArrayList<String> mPasswordList = new ArrayList<String>();
    private View mView;
    private OnPayListener listener;
    @SuppressWarnings("unused")
    private Context mContext;
    public Dialog mDialog;

    public PayPasswordView(Context mContext, Dialog dialog, OnPayListener listener) {
        this.mDialog = dialog;
        mDialog.setCanceledOnTouchOutside(true);
        getDecorView(mContext, listener);
    }

    public void getDecorView(Context mContext, OnPayListener listener) {
        this.listener = listener;
        this.mContext = mContext;
        mView = LayoutInflater.from(mContext).inflate(R.layout.item_paypassword, null);
        findViewByid();
        setLintenter();
    }

    private void findViewByid() {
        tvTitle = mView.findViewById(R.id.pay_title);
        llKeyboard = mView.findViewById(R.id.keyboard);
        del = mView.findViewById(R.id.pay_keyboard_del);// 删除键
        point = mView.findViewById(R.id.pay_keyboard_point);// 计算小数点(未做处理)

        // 键盘1-9
        zero = mView.findViewById(R.id.pay_keyboard_zero);
        one = mView.findViewById(R.id.pay_keyboard_one);
        two = mView.findViewById(R.id.pay_keyboard_two);
        three = mView.findViewById(R.id.pay_keyboard_three);
        four = mView.findViewById(R.id.pay_keyboard_four);
        five = mView.findViewById(R.id.pay_keyboard_five);
        six = mView.findViewById(R.id.pay_keyboard_six);
        seven = mView.findViewById(R.id.pay_keyboard_seven);
        eight = mView.findViewById(R.id.pay_keyboard_eight);
        nine = mView.findViewById(R.id.pay_keyboard_nine);

        // 输入框 TextView
        box1 = mView.findViewById(R.id.pay_box1);
        box2 = mView.findViewById(R.id.pay_box2);
        box3 = mView.findViewById(R.id.pay_box3);
        box4 = mView.findViewById(R.id.pay_box4);
        box5 = mView.findViewById(R.id.pay_box5);
        box6 = mView.findViewById(R.id.pay_box6);

        closeDialog = mView.findViewById(R.id.iv_closeDialog);
        forgetOneCardTradePasswordTextView = mView.findViewById(R.id.tv_confirmOrderActivity_forgetOneCardTradePassword);// 取消
        rlGif = mView.findViewById(R.id.rl_itemPayPwd_gif);
        ivGif = mView.findViewById(R.id.iv_itemPayPassword_gif);
        tvBindBankCardResult = mView.findViewById(R.id.tv_itemPayPassword_bankcardResult);

    }

    private void setLintenter() {
        closeDialog.setOnClickListener(this);
        forgetOneCardTradePasswordTextView.setOnClickListener(this);
        point.setOnClickListener(this);
        del.setOnClickListener(this);
        zero.setOnClickListener(this);
        one.setOnClickListener(this);
        two.setOnClickListener(this);
        three.setOnClickListener(this);
        four.setOnClickListener(this);
        five.setOnClickListener(this);
        six.setOnClickListener(this);
        seven.setOnClickListener(this);
        eight.setOnClickListener(this);
        nine.setOnClickListener(this);
        del.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                parseActionType(KeyboardEnum.longdel);
                return false;
            }
        });
    }

    private void parseActionType(KeyboardEnum type) {
        if (type.getType() == KeyboardEnum.ActionEnum.add) {
            if (mPasswordList.size() < 6) {
                mPasswordList.add(type.getValue());
                updateUi();
                if (mPasswordList.size() == 6) {
                    String payValue = "";
                    for (int i = 0; i < mPasswordList.size(); i++) {
                        payValue += mPasswordList.get(i);
                    }
                    if (!TextUtils.isEmpty(firstInputPwd) && firstInputPwd.equals(payValue)) {
                        isShowGif = true;
                    } else {
                        isShowGif = false;
                    }
                    if (isShowGif) {
                        showCheckLoadingView();
                        Glide.with(mContext).load(R.drawable.gif_pay_process).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(ivGif);  //开始加载动画
                    }
                    listener.onSurePay(payValue, this);
                    firstInputPwd = "";
                }
            }

        } else if (type.getType() == KeyboardEnum.ActionEnum.delete) {
            if (mPasswordList.size() > 0) {
                mPasswordList.remove(mPasswordList.get(mPasswordList.size() - 1));
                updateUi();
            }
        } else if (type.getType() == KeyboardEnum.ActionEnum.longClick) {
            mPasswordList.clear();
            updateUi();
        }

    }

    public void showCheckLoadingView() {
        llKeyboard.setVisibility(View.GONE);
        rlGif.setVisibility(View.VISIBLE);
    }

    public void showKeyBoardView() {
        llKeyboard.setVisibility(View.VISIBLE);
        rlGif.setVisibility(View.GONE);
    }

    private boolean isShowGif = false;

    public void setGifEnable(boolean isShowGif) {
        this.isShowGif = isShowGif;
    }

    public void showSetTradePwdStatus() {
        firstInputPwd = "";
        if (tvTitle != null) {
            tvTitle.setText("设置交易密码");
        }
        if (closeDialog != null) {
            closeDialog.setImageResource(R.mipmap.ic_delete_gray);
        }
        if (forgetOneCardTradePasswordTextView != null) {
            forgetOneCardTradePasswordTextView.setVisibility(View.INVISIBLE);
        }
        mPasswordList.clear();
        updateUi();
    }

    private String firstInputPwd;

    /**
     * 再次输入交易密码
     */
    public void showSetTradePwdAgain(String firstInputPwd) {
        this.firstInputPwd = firstInputPwd;
        if (tvTitle != null) {
            tvTitle.setText("再次输入交易密码");
        }
        if (closeDialog != null) {
            closeDialog.setImageResource(R.drawable.icon_back);
        }
        if (forgetOneCardTradePasswordTextView != null) {
            forgetOneCardTradePasswordTextView.setVisibility(View.INVISIBLE);
        }
        mPasswordList.clear();
        updateUi();
    }

    public interface OnPayListener {
        void onCancelPay();

        void onSurePay(String password, PayPasswordView payPasswordView);

    }


    public void onCheckPayPwdSuccess(final ImageUtils.OnGifPlayListener onGifPlayListener) {
        onCheckPayPwdSuccess(onGifPlayListener, "");
    }

    public void onCheckPayPwdSuccess(String bindBankCardResult) {
        onCheckPayPwdSuccess(null, bindBankCardResult);
    }

    public void onCheckPayPwdSuccess(final ImageUtils.OnGifPlayListener onGifPlayListener, String bindBankCardResult) {
        showCheckLoadingView();
        ImageUtils.displayGifImage(mContext, R.drawable.gif_pay_success, ivGif, new ImageUtils.OnGifPlayListener() {
            @Override
            public void onGifPlayFinish() {
                if (mDialog != null && mDialog.isShowing())
                    mDialog.dismiss();
                if (onGifPlayListener != null)
                    onGifPlayListener.onGifPlayFinish();
            }
        });
        if (!TextUtils.isEmpty(bindBankCardResult)) {
            tvBindBankCardResult.setVisibility(View.VISIBLE);
            tvBindBankCardResult.setText(bindBankCardResult);
        } else {
            tvBindBankCardResult.setVisibility(View.GONE);
        }
    }

    public void onCheckPayPwdFailed(final ImageUtils.OnGifPlayListener onGifPlayListener) {
        onCheckPayPwdFailed(onGifPlayListener, "");
    }

    public void onCheckPayPwdFailed() {
        onCheckPayPwdFailed(null, "");
    }

    public void onCheckPayPwdFailed(String bindBankCardResult) {
        onCheckPayPwdFailed(null, bindBankCardResult);
    }

    public void onCheckPayPwdFailed(final ImageUtils.OnGifPlayListener onGifPlayListener, String bindBankCardResult) {
        showCheckLoadingView();
        Glide.with(mContext).load(R.drawable.gif_pay_failed).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(ivGif);
        ImageUtils.displayGifImage(mContext, R.drawable.gif_pay_failed, ivGif, new ImageUtils.OnGifPlayListener() {
            @Override
            public void onGifPlayFinish() {
                if (mDialog != null && mDialog.isShowing())
                    mDialog.dismiss();
                if (onGifPlayListener != null)
                    onGifPlayListener.onGifPlayFinish();
            }
        });
        if (!TextUtils.isEmpty(bindBankCardResult)) {
            tvBindBankCardResult.setVisibility(View.VISIBLE);
            tvBindBankCardResult.setText(bindBankCardResult);
        } else {
            tvBindBankCardResult.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == zero) {
            parseActionType(KeyboardEnum.zero);
        } else if (v == one) {
            parseActionType(KeyboardEnum.one);
        } else if (v == two) {
            parseActionType(KeyboardEnum.two);
        } else if (v == three) {
            parseActionType(KeyboardEnum.three);
        } else if (v == four) {
            parseActionType(KeyboardEnum.four);
        } else if (v == five) {
            parseActionType(KeyboardEnum.five);
        } else if (v == six) {
            parseActionType(KeyboardEnum.sex);
        } else if (v == seven) {
            parseActionType(KeyboardEnum.seven);
        } else if (v == eight) {
            parseActionType(KeyboardEnum.eight);
        } else if (v == nine) {
            parseActionType(KeyboardEnum.nine);
        } else if (v == del) {
            parseActionType(KeyboardEnum.del);
        } else if (v == forgetOneCardTradePasswordTextView) {
            ResetTradePwdActivity.open(mContext);
        } else if (v == closeDialog) {
            if (tvTitle.getText().equals("再次输入交易密码")) {
                if (tvTitle != null) {
                    tvTitle.setText("设置交易密码");
                }
                if (closeDialog != null) {
                    closeDialog.setImageResource(R.mipmap.ic_delete_gray);
                }
                if (forgetOneCardTradePasswordTextView != null) {
                    forgetOneCardTradePasswordTextView.setVisibility(View.INVISIBLE);
                }
                mPasswordList.clear();
                updateUi();
            } else {
                mDialog.dismiss();
            }
        }
    }

    /**
     * 刷新UI
     */
    public void updateUi() {
        box1.setVisibility(View.INVISIBLE);
        box2.setVisibility(View.INVISIBLE);
        box3.setVisibility(View.INVISIBLE);
        box4.setVisibility(View.INVISIBLE);
        box5.setVisibility(View.INVISIBLE);
        box6.setVisibility(View.INVISIBLE);

        if (mPasswordList.size() == 0) {
        } else if (mPasswordList.size() == 1) {
            box1.setVisibility(View.VISIBLE);
        } else if (mPasswordList.size() == 2) {
            box1.setVisibility(View.VISIBLE);
            box2.setVisibility(View.VISIBLE);
        } else if (mPasswordList.size() == 3) {
            box1.setVisibility(View.VISIBLE);
            box2.setVisibility(View.VISIBLE);
            box3.setVisibility(View.VISIBLE);
        } else if (mPasswordList.size() == 4) {
            box1.setVisibility(View.VISIBLE);
            box2.setVisibility(View.VISIBLE);
            box3.setVisibility(View.VISIBLE);
            box4.setVisibility(View.VISIBLE);
        } else if (mPasswordList.size() == 5) {
            box1.setVisibility(View.VISIBLE);
            box2.setVisibility(View.VISIBLE);
            box3.setVisibility(View.VISIBLE);
            box4.setVisibility(View.VISIBLE);
            box5.setVisibility(View.VISIBLE);

        } else if (mPasswordList.size() == 6) {
            box1.setVisibility(View.VISIBLE);
            box2.setVisibility(View.VISIBLE);
            box3.setVisibility(View.VISIBLE);
            box4.setVisibility(View.VISIBLE);
            box5.setVisibility(View.VISIBLE);
            box6.setVisibility(View.VISIBLE);

        }
    }

    public View getView() {
        return mView;
    }

    public enum KeyboardEnum {
        one(ActionEnum.add, "1"), two(ActionEnum.add, "2"), three(ActionEnum.add, "3"), four(ActionEnum.add, "4"), five(
                ActionEnum.add, "5"), sex(ActionEnum.add, "6"), seven(ActionEnum.add, "7"), eight(ActionEnum.add,
                "8"), nine(ActionEnum.add, "9"), zero(ActionEnum.add, "0"), del(ActionEnum.delete,
                "del"), longdel(ActionEnum.longClick, "longclick"), cancel(ActionEnum.cancel,
                "cancel"), sure(ActionEnum.sure, "sure"), point(ActionEnum.add, ".");

        public enum ActionEnum {
            add, delete, longClick, cancel, sure
        }

        private ActionEnum type;
        private String value;

        private KeyboardEnum(ActionEnum type, String value) {
            this.type = type;
            this.value = value;
        }

        public ActionEnum getType() {
            return type;
        }

        public void setType(ActionEnum type) {
            this.type = type;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

}
