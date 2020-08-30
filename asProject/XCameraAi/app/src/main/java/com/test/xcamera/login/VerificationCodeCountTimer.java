package com.test.xcamera.login;

import android.os.CountDownTimer;

/**
 * Author: mz
 * Time:  2019/10/10
 */
public class VerificationCodeCountTimer extends CountDownTimer {
    public  CountDownTimerListener  timerListener;
    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */
    public VerificationCodeCountTimer(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
    }

    @Override
    public void onTick(long millisUntilFinished) {
       timerListener.onTimerDoing(millisUntilFinished);
    }

    @Override
    public void onFinish() {
         timerListener.onFinish();
    }


    public void  setTimerListener(CountDownTimerListener timerListener){
         this.timerListener=timerListener;
    }


}
