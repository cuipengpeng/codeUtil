package com.hospital.checkup.utils;

public class StringUtil {
	
	public static boolean notEmpty(String string){
		boolean notEmpty = false;
		if(!"".equals(string) && string != null && !"null".equals(string)){
			notEmpty = true;
		}
		return notEmpty;
	}
	
	public static boolean isNull(String string){
		boolean isNull = false;
		if("".equals(string) || string == null || "null".equals(string)){
			isNull = true;
		}
		return isNull;
	}
}
