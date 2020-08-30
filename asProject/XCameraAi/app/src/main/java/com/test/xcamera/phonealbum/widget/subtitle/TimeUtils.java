package com.test.xcamera.phonealbum.widget.subtitle;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

/**
 * Created by liutao on 6/22/16.
 */
public class TimeUtils {

    private static StringBuilder sFormatBuilder = new StringBuilder();
    private static Formatter sFormatter = new Formatter(sFormatBuilder, Locale.getDefault());

    public static String getDuration(int timeMs) {
        if (timeMs < 0) {
            timeMs = 0;
        }
        int millisecond = timeMs % 1000;

        int totalSeconds = timeMs / 1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        //如果大于500毫秒，秒加一
        if (millisecond >= 500) {
            seconds = seconds + 1;
        }
        sFormatBuilder.setLength(0);
        if (hours > 0) {
            return sFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return sFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    public static String getDurationCentisecond(long timeMs) {
        if (timeMs < 0) {
            timeMs = 0;
        }
        long millisecond = timeMs % 1000;

        long totalSeconds = timeMs / 1000;
        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long centisecond = millisecond / 100 * 10;
        centisecond += millisecond % 100 / 10;
        sFormatBuilder.setLength(0);
        return sFormatter.format("%02d:%02d.%02d", minutes, seconds, centisecond).toString();
    }


    public static String getDurationChineseTwo(final int timeMs) {
        int totalSeconds = timeMs / 1000;
        int millisecond = timeMs % 1000;
        int seconds = totalSeconds % 60;
        int minutes = totalSeconds / 60;
        //如果大于500毫秒，秒加一
        String vlaue = String.valueOf(millisecond).substring(0, 1);
        //如果大于100分钟不显示秒，防止字符串在UI越界
        if (minutes > 100) {
            //如果大于30秒，加一分钟
            if (seconds > 30) {
                minutes = minutes + 1;
            }
            return minutes + "分";
        } else if (minutes > 0) {
            return minutes +  "分" + seconds + "." + vlaue + "秒";
        } else {
            return seconds + "." + vlaue + "秒";
        }
    }

    public static String getDurationMillisecond(final int timeMs) {
        int millisecond = timeMs % 1000;
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        //如果大于500毫秒，秒加一
        if (millisecond >= 500) {
            seconds = seconds ;
        }
        sFormatBuilder.setLength(0);
        millisecond=millisecond/33;
        if(millisecond>=30){
            millisecond=29;
        }
        if (hours > 0) {
            return sFormatter.format("%d:%02d:%02d:%02d", hours, minutes, seconds, millisecond).toString();
        } else {
            return sFormatter.format("%02d:%02d:%02d", minutes, seconds, millisecond).toString();
        }
    }

    public static String getDate(long dateAdded) {
        Date date = new Date(dateAdded);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return format.format(date);
    }

    public static String getDate2(long dateAdded) {
        Date date = new Date(dateAdded);
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault());
        return format.format(date);
    }



    public static String getQuotationFormatedDuration(int duration) {
        int totalSeconds = duration / 1000;
        int millisecond = duration % 1000;

        int seconds = totalSeconds % 60;
        int minutes = totalSeconds / 60;
        //如果大于500毫秒，秒加一
        if (millisecond >= 500) {
            seconds = seconds + 1;
        }
        sFormatBuilder.setLength(0);
        return sFormatter.format("%d'%02d''", minutes, seconds).toString();
    }

    public static String getQuotationFormatedDurationMillisecond(int duration) {
        int millisecond = duration % 1000;

        int totalSeconds = duration / 1000;

        int seconds = totalSeconds % 60;
        int minutes = totalSeconds / 60;

        sFormatBuilder.setLength(0);
        return sFormatter.format("%02d'%02d''%03d", minutes, seconds, millisecond).toString();
    }
}
