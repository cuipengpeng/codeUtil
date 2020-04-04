package com.caishi.chaoge.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期
 */
public class DateUtils {


	/**
	 * 获取本地系统日期和时间
	 *
	 * @return
	 */
	public static String getDayAndTime(Date date) {
		String dayAndTime = "yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat sdf = new SimpleDateFormat(dayAndTime);
		return sdf.format(date);
	}

}
