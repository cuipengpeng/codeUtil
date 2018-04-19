package com.jfbank.qualitymarket;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;

/**
 * 所有Activity的处理类
 * 
 * @author 彭爱军
 * @date 2016年8月16日
 */
public class ActivitysManage {
	private List<Activity> mActivityList = new LinkedList<Activity>();
	private static ActivitysManage instance;

	private ActivitysManage() {
	}

	/**
	 * 单一实例
	 */
	public static ActivitysManage getActivitysManager() {
		if (instance == null) {
			instance = new ActivitysManage();
		}
		return instance;
	}

	/**
	 * 添加Activity到堆栈
	 */
	public void addActivity(Activity activity) {
		mActivityList.add(activity);
	}

	/**
	 * 结束指定的Activity
	 */
	public void finishActivity(Activity activity) {
		if (activity != null) {
			mActivityList.remove(activity);
			activity.finish();
			activity = null;
		}
	}

	/**
	 * 结束当前activity的前一个activity
	 */
	public void beforeOneActivity() {
		if (mActivityList.size() > 1) {
			Activity activity = mActivityList.get(mActivityList.size() - 2);
			mActivityList.remove(mActivityList.size() - 2);
			activity.finish();
		}
	}

	public void finishActivity() {
		if (mActivityList.size() > 1) {
			Activity activity = mActivityList.get(mActivityList.size() - 1);
			mActivityList.remove(mActivityList.size() - 1);
			activity.finish();
		}
	}

	/**
	 * 结束所有Activity
	 */
	public void finishAllActivity() {
		while (mActivityList.size() > 0) {
			Activity activity = mActivityList.get(mActivityList.size() - 1);
			mActivityList.remove(mActivityList.size() - 1);
			activity.finish();
		}
	}

	/**
	 * 退出应用程序
	 */
	public void AppExit() {
		try {
			finishAllActivity();
		} catch (Exception e) {
		}
	}
}
