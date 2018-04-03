package com.jf.jlfund.utils;

import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import com.jf.jlfund.R;

/**
 * Created by 55 on 2018/1/12.
 */

public class MyCountDownTimer extends CountDownTimer {

    TextView tvGetCheckCode;

    public MyCountDownTimer(TextView tvGetCheckCode) {
        this(60000, 1000, tvGetCheckCode);
    }

    public MyCountDownTimer(long millisInFuture, long countDownInterval, TextView tvGetCheckCode) {
        super(millisInFuture, countDownInterval);
        this.tvGetCheckCode = tvGetCheckCode;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        if (tvGetCheckCode != null) {
            tvGetCheckCode.setText("重新获取(" + millisUntilFinished / 1000 + ")");
            tvGetCheckCode.setTextColor(ContextCompat.getColor(tvGetCheckCode.getContext(), R.color.color_b9bbca));
            tvGetCheckCode.setClickable(false);
        }
    }

    @Override
    public void onFinish() {
        if (tvGetCheckCode != null) {
            tvGetCheckCode.setText("获取验证码");
            tvGetCheckCode.setClickable(true);
            tvGetCheckCode.setTextColor(ContextCompat.getColor(tvGetCheckCode.getContext(), R.color.color_0084ff));
            SPUtil.getInstance().putLong(SPUtil.KEY_SMS_CODE_TIMESTAMP_FORGET_RESET_LOGIN_PWD, -1);
        }
    }

    //获取验证码失败，【重置倒计时，但是显示的是获取验证码，而不是重新获取验证码】
//        if (countDownTimer != null) {
//            countDownTimer.onFinish();
//            countDownTimer.cancel();
//        }
}