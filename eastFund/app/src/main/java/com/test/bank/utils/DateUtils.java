package com.test.bank.utils;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by 55 on 2017/8/23.
 */

public class DateUtils {

    /**
     * 通过Date获取 年-月-日
     *
     * @param date
     * @return 2017-08
     */
    public static String getMonthFromDate(Date date) {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        String result = sdf.format(date);
        return result;
    }

    /**
     * 两个日期之间的月份间隔绝对值
     *
     * @param currentMonth 2017-09
     * @param selectMonth  2017-06
     * @return 3
     */
    public static int monthBetween(String currentMonth, String selectMonth) {
        LogUtils.e("monthBetween: " + currentMonth + "  -- " + selectMonth);
        int result = 0;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
            Calendar c1 = Calendar.getInstance();
            Calendar c2 = Calendar.getInstance();
            c1.setTime(sdf.parse(currentMonth));
            c2.setTime(sdf.parse(selectMonth));
            result = (c2.get(Calendar.YEAR) - c1.get(Calendar.YEAR)) * 12 + c2.get(Calendar.MONTH) - c1.get(Calendar.MONTH);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result == 0 ? 1 : Math.abs(result);
    }

    /**
     * 返回给定时间的前一个月
     *
     * @param monthOfYear 2017-08
     * @return 2017-07
     */
    public static String getPreviousMonthOfYear(String monthOfYear) {
        if (TextUtils.isEmpty(monthOfYear)) {
            LogUtils.e("param Of month is null or empty.");
            return "";
        }

        if (!monthOfYear.contains("-")) {
            return "";
        }

        String[] arr = monthOfYear.split("-");
        if (arr.length < 2) {
            return "";
        }
        int year = Integer.valueOf(arr[0]);
        int month = Integer.valueOf(arr[1]);

        if (month - 1 > 0) {
            return getFormatedMonthOfYear(year, month);
        } else {
            return getFormatedMonthOfYear(year - 1, 12);
        }
    }

    /**
     * 月份前边加0
     *
     * @param year  2017
     * @param month 8
     * @return 2017-08
     */
    public static String getFormatedMonthOfYear(int year, int month) {
        if (month < 10) {
            return year + "-0" + month;
        }
        return year + "-" + month;
    }
}
