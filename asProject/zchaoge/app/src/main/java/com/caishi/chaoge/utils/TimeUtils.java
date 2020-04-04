package com.caishi.chaoge.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

/**
 * Created by victorxie on 4/11/16.
 */
public class TimeUtils {

    public static long getTodayStartTime() {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static String formatTime(long milliseconds) {

        if (milliseconds <= 0) {
            return "00:00";
        }
        long seconds = milliseconds / 1000;
        int minutes = (int) (seconds / 60);

        if (minutes < 60) {
            return String.format(Locale.ROOT, "%02d:%02d", minutes, seconds % 60);
        }

        return String.format(Locale.ROOT, "%02d:%02d:%02d", minutes / 60, minutes % 60, seconds % 60);
    }

    /**
     * 将毫秒时间格式化成常用时间
     *
     * @param timeMs
     * @return
     */
    public static String formatTime(long timeMs, boolean needHours) {

        if (timeMs <= 0) {
            String defaultStr;
            if (needHours) {
                defaultStr = "00:00:00";
            } else {
                defaultStr = "00:00";
            }
            return defaultStr;
        }
        int totalSeconds = (int) (timeMs / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        StringBuilder mFormatBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        if (hours > 0 || needHours) {
            return mFormatter.format("%02d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    public static String formatDateTime(long time, String format) {

        return new SimpleDateFormat(format, Locale.ROOT).format(new Date(time));
    }

    public static String formatDate(long time) {

        return new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).format(new Date(time));
    }

    public static String formatDatePath() {

        return new SimpleDateFormat("yyyyMMdd", Locale.ROOT).format(new Date());
    }

    public static String getYearDiff(long pubTime) {

        return getDateDiff(pubTime, "yyyy-MM-dd");
    }

    public static String getMonthDiff(long pubTime) {

        return getDateDiff(pubTime, "MM-dd");
    }

    public static String getDateDiff(long createTime, long refreshTime ) {

        long diff = refreshTime - createTime;
        long min = diff / 60000;
        if (min <= 1) {
            return "1m ago...";
        } else if (min < 60) {
            return min + " m ago";
        } else if (min < 1440) {
            return min / 60 + " h ago";
        } else {
            if((min/1440)>30) {
                //如果需要向后计算日期 -改为+
                Date nowDate = new Date(createTime);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM,yyyy", Locale.ENGLISH);
                String dateOk = simpleDateFormat.format(nowDate);
                return dateOk;
            }
            return min / 1440 + "d ago";
        }
    }

    public static String getDateTimeDiff(long createTime, long refreshTime ) {

        long diff = refreshTime - createTime;
        long min = diff / 60000;

        if (min < 1440) {
            return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(createTime));
        } else if (min >= 1440 && min <= 2880) {
            return "Yesterday";
        } else {
            return new SimpleDateFormat("yy/MM/dd", Locale.getDefault()).format(new Date(createTime));
        }
    }

    public static String getTimeline(long createTime, long refreshTime) {

        long diff = refreshTime - createTime;
        long min = diff / 60000;
        if (min <= 1440) {
            return "Today";
        } else {
            Calendar time = Calendar.getInstance();
            time.setTime(new Date(createTime));
            int month = time.get(Calendar.MONTH) + 1;
            int day = time.get(Calendar.DAY_OF_MONTH);
            return day + "/" + month;
        }
    }

    public static String getDateDiff(long pubTime, String format) {

        long diff = System.currentTimeMillis() - pubTime;
        long hour = diff / 3600000;
        if (hour < 1) {
            long min = diff / 60000;
            if (min > 0) {
                return min + "m ago";
            }
            return "1m ago...";
        } else if (hour < 10) {
            return hour + "h ago";
        } else {
            return new SimpleDateFormat(format, Locale.ROOT).format(new Date(pubTime));
        }
    }

    public static String formatDiff(long diff){
        if ( diff<=0 )
            return "past";
        if ( diff > 30*24*60*60*1000L )
            return "future";
        return String.format(Locale.getDefault(), "%02d:%02d", diff/(60*60*1000),1+((diff/(60*1000))%60));
    }

    public static String formatToHours(long time) {
        if (time / 1800 == 0) {
            return "duration: 0.5 hour(s)";
        } else if (time % 1800 == 0){
            return "duration" + time * 1.0 / 3600 + "hour(s)";
        } else {
            double l = Math.floor(time * 1.0 / 3600 + 0.5);
            if ((time - Math.floor(time * 1.0 / 3600) * 3600) < 1800) {
                l += 0.5;
            }

            return "duration" + (l % 0.5 == 0 ? l : (int)l) + "hour(s)";
        }
    }

    public static String formatToExactHours(long time) {
        long hour = time / 3600;
        long minutes = (time - 3600 * hour) / 60;
        long second = time - 3600 * hour - minutes * 60;
        if (hour == 0) {
            return minutes + "minutes" + (second == 0 ? "" : second + "seconds");
        } else {
            return hour + "hours" + minutes + "minutes" + (second == 0 ? "" : second + "seconds");
        }
    }
    /**
     * 将字符串转为时间戳
     * @param dateString
     * @param pattern
     * @return
     */
    public static String getStringToDate(String dateString, String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        Date date = new Date();
        try{
            date = dateFormat.parse(dateString);
        } catch(ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date.getDay()+"/"+date.getMonth()+"/"+date.getYear();
    }



    public static long toTime(String lrc) {
        String[] tmp = lrc.split("\\.");
        long ms = Long.parseLong(tmp[1]) * 10;

        String[] sTmp = tmp[0].split(":");
        long s = Long.parseLong(sTmp[1]);

        long m = Long.parseLong(sTmp[0]);

        long time = ms + (m * 60 + s) * 1000;
        return time;
    }

    public static String toLRC(long time) {

        //四舍五入
        time = time % 10 > 5 ? (time + 10) / 10 : time / 10;
        //毫秒
        long ms = time % 100;

        //秒
        long s = time / 100 % 60;

        //分
        long m = time / 100 / 60;

        return String.format("%02d:%02d.%02d",m,s,ms);
    }

}
