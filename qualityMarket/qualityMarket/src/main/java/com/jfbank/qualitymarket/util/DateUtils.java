/**
 * 文件名：CommonUtils.java
 * 全路径：com.jfbank.qualitymarket.util.CommonUtils
 */
package com.jfbank.qualitymarket.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 功能：时间转化工具<br>
 * 作者：赵海<br>
 * 时间：2016年10月20日 下午2:25:11 <br>
 * 版本：1.1.0<br>
 * 
 */
public class DateUtils {
	// 默认显示日期的格式
	public static final String DATAFORMAT_STR = "yyyy-MM-dd";
	// 默认显示日期的格式
	public static final String YYYY_MM_DATAFORMAT_STR = "yyyy-MM";

	// 默认显示日期时间的格式
	public static final String DATATIMEF_STR = "yyyy-MM-dd HH:mm:ss";

	// 默认显示简体中文日期的格式
	public static final String ZHCN_DATAFORMAT_STR = "yyyy年MM月dd日";

	// 默认显示简体中文日期时间的格式
	public static final String ZHCN_DATATIMEF_STR = "yyyy年MM月dd日HH时mm分ss秒";

	// 默认显示简体中文日期时间的格式
	public static final String ZHCN_DATATIMEF_STR_4yMMddHHmm = "yyyy年MM月dd日HH时mm分";
	private static DateFormat dateFormat = null;
	// private static DateFormat dateTimeFormat = null;
	//
	// private static DateFormat zhcnDateFormat = null;
	//
	// private static DateFormat zhcnDateTimeFormat = null;
	static {
		dateFormat = new SimpleDateFormat(DATAFORMAT_STR);
		// dateTimeFormat = new SimpleDateFormat(DATATIMEF_STR);
		// zhcnDateFormat = new SimpleDateFormat(ZHCN_DATAFORMAT_STR);
		// zhcnDateTimeFormat = new SimpleDateFormat(ZHCN_DATATIMEF_STR);
	}

	/**
	 * 时间格式转化
	 * 
	 * @param time
	 * @return
	 */
	public static String getYYYYMMDate(long time) {
		Date date = new Date(time);
		return dateFormat.format(date);
	}

}
