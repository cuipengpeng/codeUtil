package com.jfbank.qualitymarket.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 功能：时间转化器<br>
 * 作者：RaykerTeam
 * 时间： 2016/11/3 0003<br>.
 * 版本：1.0.0
 */

public class TimeUtils {
    /**
     * 完整的日期时间格式yyyy-MM-dd HH:mm:ss
     */
    public final static String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    /**
     * 时间格式  yyyy-MM-dd HH:mm
     */
    public final static String HOUR_TIME_FORMAT = "yyyy-MM-dd HH:mm";
    /**
     * 只有日期的格式
     */
    public final static String DATE_FORMAT = "yyyy-MM-dd";
    /**
     * 只有时间的格式
     */
    public final static String TIME_FORMAT = "HH:mm:ss";
    /**
     * 带中文的日期格式(2000年01月01日)
     */
    public final static String DATE_FORMAT_WITH_CHINESE = "yyyy年MM月dd日";
    /**
     * 短时间格式(HH:mm)
     */
    public final static String SHORT_TIME_FORMAT = "HH:mm";

    /**
     * 格式化日期  String 转String
     *
     * @param string    有效的日期字符
     * @param oldFormat 原格式化的格式
     * @param newFormat 新格式化的格式
     * @return 转化出错，默认为当前时间
     */
    public static String formatWithString(String string, String oldFormat, String newFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(newFormat);
        return sdf.format(transformStringDate(string, oldFormat));
    }

    /**
     * 格式化日期   转String
     *
     * @param time      有效的日期字符
     * @param newFormat 新格式化的格式
     * @return 转化出错，默认为当前时间
     */
    public static String formatWithString(long time, String newFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(newFormat);
        Date date = new Date(time);
        return sdf.format(date);
    }

    /**
     * 格式化日期  String 转String
     *
     * @param string    有效的日期字符
     * @param newFormat 新格式化的格式
     * @return 转化出错，默认为当前时间
     */
  public  static String formatWithString(String string, String newFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(newFormat);
        return sdf.format(transformStringDate(string, DATE_TIME_FORMAT));
    }

    /**
     * 通过SimpleDataFormat格式把string转换成Calendar
     *
     * @param string 日期字符串
     * @param format 目标日期格式
     * @return
     */
    private static Date transformStringDate(String string, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = sdf.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
            date = new Date();
        }
        return date;
    }
}
