package com.test.xcamera.album;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Created by 周 on 2019/10/11.
 */

public class TimerHanler extends Handler {
    private final TimerListenre timerListenre;
    private int START_TIMER = 100;
    private int count;

    public TimerHanler(TimerListenre timerListenre) {
        this.timerListenre = timerListenre;
    }

    @Override
    public void handleMessage(Message msg) {
        if (count > 0) {
            count--;
            Log.i("HANHHHHHHH", "handleMessage: " + count);
            if (timerListenre != null) {
                timerListenre.onTimerDoing(count);
            }
            this.sendEmptyMessageDelayed(START_TIMER, 1000);
        } else {
            if (timerListenre != null) {
                timerListenre.onFinish();
            }
        }
    }

    /**
     * 开始计时
     *
     * @param count
     */
    public void start(int count) {
        this.count = count;
        this.removeMessages(START_TIMER);
        this.sendEmptyMessageDelayed(START_TIMER, 1000);
    }

    /**
     * 取消计时
     */
    public void cancelTimer() {
        this.removeMessages(START_TIMER);
    }

    /**
     * 继续计时
     *
     * @param totalCount
     */
    public void resumeStart(int totalCount) {
        this.count = totalCount;
        this.sendEmptyMessage(START_TIMER);
    }

    public interface TimerListenre {
        void onTimerDoing(int count);

        void onFinish();
    }

}
