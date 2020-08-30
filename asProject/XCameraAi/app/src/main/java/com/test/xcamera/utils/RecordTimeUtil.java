package com.test.xcamera.utils;

import android.os.Handler;
import android.os.Message;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zll on 2019/10/11.
 */

public class RecordTimeUtil {
    public static final int RECORD_TIME = 10101;
    private Timer timer;
    private TimerTask timerTask;
    private long time = 0;

    public void statTimer(final Handler handler, int period) {
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                time++;
                Message msg = Message.obtain();
                msg.what = RECORD_TIME;
                msg.obj = time;
                handler.sendMessage(msg);
            }
        };
        if (timer != null) {
            timer.schedule(timerTask, 0, period);
        }

    }

    public void statTimer(final Handler handler) {
        this.statTimer(handler, 1000);
    }

    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
        time = 0;
    }

    public static String formatTime(long time) {
        int h = (int) (time / 3600);
        int m = (int) ((time % 3600) / 60);
        int s = (int) (time % 60);
        if (h > 0)
            return String.format("%02d:%02d:%02d", h, m, s);
        return String.format("%02d:%02d", m, s);
    }
}
