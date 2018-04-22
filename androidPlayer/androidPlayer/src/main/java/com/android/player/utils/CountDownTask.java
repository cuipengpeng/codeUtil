package com.android.player.utils;

import android.os.Handler;
import android.os.Message;

public class CountDownTask {
	public static final int COUNT_DOWN_TASK = 10001;
	public static final int COUNT_DOWN_OVER = 20001;
	public static final String FORMAT_MINUTE = "MM分ss秒";
	public static final String FORMAT_HOUR = "hh小时MM分ss秒";

	private final long yearLevelValue = 365*24*60*60*1000 ;
	private final long monthLevelValue = 30*24*60*60*1000 ;
	private final long dayLevelValue = 24*60*60*1000 ;
	private final long hourLevelValue = 60*60*1000 ;
	private final long minuteLevelValue = 60*1000 ;
	private final long secondLevelValue = 1000 ;

	public static void countDwon(int time, Handler handler , String countDownFormat){
		String format = FORMAT_HOUR;
		if(FORMAT_HOUR.equals(countDownFormat)){
			format = FORMAT_HOUR;
		}else if(FORMAT_MINUTE.equals(countDownFormat)){
			format = FORMAT_MINUTE;
		}

		if(time>0){
			Message message = new Message();
			time-=1;
			message.what = COUNT_DOWN_TASK;
			message.arg1 = time;
			message.obj = format;
			handler.sendMessageDelayed(message, 1000);
		}else {
			Message message = new Message();
			message.what = COUNT_DOWN_OVER;
			handler.sendMessageDelayed(message, 1000);
		}
	}

	/**
	 * 倒计时时间转换
	 * @param period 要倒计时的时间
	 * @param countDownFormat  是普通商品还是秒杀商品
	 * @return 汉字形式的时间
	 */
	public String getRemainTimeString(long period, String countDownFormat) {//根据毫秒差计算时间差
		/*******计算出时间差中的年、月、日、天、时、分、秒*******/
		int year = (int) (period/yearLevelValue);
		int month = (int) ((period - year*yearLevelValue)/monthLevelValue);
		int day = (int) ((period - year*yearLevelValue - month*monthLevelValue)/dayLevelValue);
		int hour = (int) ((period - year*yearLevelValue - month*monthLevelValue - day*dayLevelValue)/hourLevelValue);
		int minute = (int) ((period - year*yearLevelValue - month*monthLevelValue - day*dayLevelValue - hour*hourLevelValue)/minuteLevelValue) ;
		int second = (int) ((period - year*yearLevelValue - month*monthLevelValue - day*dayLevelValue - hour*hourLevelValue - minute*minuteLevelValue)/secondLevelValue) ;

//		String result = year+"年"+month+"月"+day+"天"+hour+"时"+minute+"分"+second+"秒";
		String result = hour+"时"+minute+"分"+second+"秒";
		if(FORMAT_HOUR.equals(countDownFormat)){
			result = hour+"小时"+minute+"分"+second+"秒";
		}else if(FORMAT_MINUTE.equals(countDownFormat)){
			result = minute+"分"+second+"秒";
		}

//		if(second>0){
//			result = second+"秒";
//		}
//		if(minute>0){
//			result = minute+"分"+second+"秒";
//		}
//		if(hour>0){
//			result = hour+"小时"+minute+"分"+second+"秒";
//		}
		return result;
	}
}
