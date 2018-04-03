package com.jfbank.qualitymarket.util;
import android.app.Activity;
import java.util.Stack;

/**
 * Created by lyy on 2015/11/4.
 * APP生命周期管理类管理
 */
public class ActivityManager {
    private static final String TAG = "ActivityManager";
    private Stack<Activity> mActivityStack = new Stack<>();
    private Stack<Activity> mOrderStack = new Stack<>();//下单Activity管理
    private Stack<Activity> mPayStack = new Stack<>();//下单支付Activity管理

    private static class ActivityManagerUtilsInstance {
        private static ActivityManager INSTANCE = new ActivityManager();
    }
    /**
     * 添加支付流程Activity
     * @param activity
     */
    public void addPayActivity(Activity activity) {
        if (mPayStack == null) {
            mPayStack = new Stack<>();
        }
        mPayStack.add(activity);
    }

    /**
     * 移除支付流程Activity
     * @param activity
     */
    public void removePayActivity(Activity activity) {
        if (activity != null) {
            mPayStack.remove(activity);
        }
    }
    /**
     * 结束支付流程Activity
     */
    public void finishPayllActivity() {
        for (int i = 0, size = mPayStack.size(); i < size; i++) {
            if (mPayStack.get(i) != null && mPayStack.size() > 0&&!mPayStack.get(i).isFinishing()) {
                mPayStack.get(i).finish();
            }
        }
        mPayStack.clear();
    }
    /**
     * 添加订单Activity
     * @param activity
     */
    public void addOrderActivity(Activity activity) {
        if (mOrderStack == null) {
            mOrderStack = new Stack<>();
        }
        mOrderStack.add(activity);
    }

    /**
     * 移除订单Activity
     * @param activity
     */
    public void removeOrderActivity(Activity activity) {
        if (activity != null) {
            mOrderStack.remove(activity);
        }
    }
    /* 获取实例 */
    public static ActivityManager getInstance() {
        return ActivityManagerUtilsInstance.INSTANCE;
    }

    /**
     * 获取Activity栈
     *
     * @return
     */
    public Stack<Activity> getActivityStack() {
        return mActivityStack;
    }

    /**
     * 堆栈大小
     */
    public int getActivitySize() {
        return mActivityStack.size();
    }

    /**
     * 获取指定的Activity
     */
    public Activity getActivity(int location) {
        return mActivityStack.get(location);
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        if (mActivityStack == null) {
            mActivityStack = new Stack<>();
        }
        mActivityStack.add(activity);
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity getCurrentActivity() {
        return mActivityStack.lastElement();
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishActivity() {
        finishActivity(mActivityStack.lastElement());
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            mActivityStack.remove(activity);
            activity.finish();
        }
    }

    /**
     * 移除指定的Activity
     */
    public void removeActivity(Activity activity) {
        if (activity != null) {
            mActivityStack.remove(activity);
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        for (Activity activity : mActivityStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }
    /**
     * 结束所有订单Activity
     */
    public void finishOrderAllActivity() {
        for (int i = 0, size = mOrderStack.size(); i < size; i++) {
            if (mOrderStack.get(i) != null && mOrderStack.size() > 0&&!mOrderStack.get(i).isFinishing()) {
                mOrderStack.get(i).finish();
            }
        }
        mOrderStack.clear();
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (int i = 0, size = mActivityStack.size(); i < size; i++) {
            if (mActivityStack.get(i) != null && mActivityStack.size() > 0) {
                mActivityStack.get(i).finish();
            }
        }
        mActivityStack.clear();
    }


}
