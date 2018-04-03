package com.jfbank.qualitymarket.util;

import android.content.Context;
import android.widget.Toast;

/**
 * 简单封装的Toast，主要解决多次点击显示多次。即感觉长时间显示。
 * 
 * @author 彭爱军
 * @date 2016年8月16日
 */
public class MyToast {
	private static Toast toast;

	public static void showToast(Context context, String content) {
		if (toast == null) {
			toast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
		} else {
			toast.setText(content);
		}
		toast.show();
	}
	
}
