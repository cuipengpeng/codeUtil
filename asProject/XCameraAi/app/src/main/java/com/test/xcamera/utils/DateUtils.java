package com.test.xcamera.utils;

import android.text.BidiFormatter;
import android.text.TextDirectionHeuristics;
import android.text.TextUtils;
import android.view.View;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by smz on 2019/11/12.
 */

public class DateUtils {

    public static String timeToDate(String pattrern, long time) {
        //"yyyy-MM-dd HH:mm:ss"
        SimpleDateFormat format0 = new SimpleDateFormat(pattrern);
        String str = format0.format(time);//这个就是把时间戳经过处理得到期望格式的时间
        return str;
    }

    public static String DateFormat(String pattrern, long date) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattrern);//yyyy年MM月dd日 HH:mm:ss
        String dateString = sdf.format(date);
        return dateString;
    }

    public static String DateFormat(long seconds) {
        String format = "yyyy-MM-dd HH:mm:ss:SSS";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(seconds));
    }

    /**
     * format video time
     *
     * @param time
     * @return
     */
    public static String timeFormat(float time) {
        time = time / 1000;
        float hour = time / 3600;
        float min = (time / 60) % 60;
        float sec = time % 60;

        DecimalFormat df = new DecimalFormat("00");
        String value = String.format("%s:%s:%s", df.format(hour), df.format(min), df.format(sec));
        return value;
    }

    /**
     * format video time
     *
     * @param timeMs
     * @return
     */
    public static String stringForTime(long timeMs) {
        if (timeMs > 500 && timeMs < 1000) {
            timeMs = 1000;
        } else if ((timeMs % 1000) > 500) {//四舍五入
            timeMs = timeMs + 500;
        }
        StringBuilder mFormatBuilder;
        Formatter mFormatter;
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        int totalSeconds = (int) (timeMs / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    /**
     * format video time
     *
     * @param time
     * @return
     */
    public static String timeFormat_(long time) {
        time = time / 1000;
        float hour = (float) (time / 3600);
        float min = (float) ((time / 60) % 60);
        float sec = (float) (time % 60);

        DecimalFormat df = new DecimalFormat("00");
        String value = String.format("%s:%s", df.format(min), df.format(sec));
        return value;
    }

    /**
     * 获取手机时区
     *
     * @return
     */
    public static String getTimeZone() {
        Calendar mDummyDate;
        mDummyDate = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        mDummyDate.setTimeZone(now.getTimeZone());
        mDummyDate.set(now.get(Calendar.YEAR), 11, 31, 13, 0, 0);
        return getTimeZoneText(now.getTimeZone(), true);
    }

    private static String getTimeZoneText(TimeZone tz, boolean includeName) {
        Date now = new Date();

        SimpleDateFormat gmtFormatter = new SimpleDateFormat("ZZZZ");
        gmtFormatter.setTimeZone(tz);
        String gmtString = gmtFormatter.format(now);
        BidiFormatter bidiFormatter = BidiFormatter.getInstance();
        Locale l = Locale.getDefault();
        boolean isRtl = TextUtils.getLayoutDirectionFromLocale(l) == View.LAYOUT_DIRECTION_RTL;
        gmtString = bidiFormatter.unicodeWrap(gmtString,
                isRtl ? TextDirectionHeuristics.RTL : TextDirectionHeuristics.LTR);

        if (!includeName) {
            return gmtString;
        }

        return gmtString;
    }

}
