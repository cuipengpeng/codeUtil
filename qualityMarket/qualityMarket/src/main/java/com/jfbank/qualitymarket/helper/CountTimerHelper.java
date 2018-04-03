package com.jfbank.qualitymarket.helper;

import android.os.CountDownTimer;

/**
 * 功能：CountTimerHelper秒杀倒计时<br>
 * 作者：赵海<br>
 * 时间： 2017/2/28 0028<br>.
 * 版本：1.2.0
 */

public abstract  class CountTimerHelper extends CountDownTimer{
    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */
    public  long millisInFuture;
    public CountTimerHelper(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        this.millisInFuture=millisInFuture;
    }

}
