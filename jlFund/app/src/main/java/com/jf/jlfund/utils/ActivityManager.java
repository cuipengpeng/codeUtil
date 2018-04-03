package com.jf.jlfund.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.jf.jlfund.base.BaseActivity;
import com.jf.jlfund.view.activity.MainActivity;

import java.util.EmptyStackException;
import java.util.NoSuchElementException;
import java.util.Stack;

/**
 * activity堆栈管理类
 */
public class ActivityManager {

    private static final String TAG = "ActivityManager";

    private static Stack<Activity> activityStack;
    private static ActivityManager instance;

    private ActivityManager() {
    }

    /**
     * 单一实例
     */
    public static ActivityManager getInstance() {
        if (instance == null) {
            synchronized (ActivityManager.class) {
                if (instance == null) {
                    instance = new ActivityManager();
                }
            }
        }
        return instance;
    }

    /**
     * 添加Activity到堆栈
     */
    public void pushActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<>();
        }
        activityStack.add(activity);
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity currentActivity() {
        try {
            return activityStack.peek();
        } catch (Exception e) {
            if (e instanceof NoSuchElementException) {
                Log.e(TAG, "currentActivity: --> " + e.getMessage());
            } else if (e instanceof EmptyStackException) {
                Log.e(TAG, "currentActivity: --> " + e.getMessage());
            }
        }
        return null;
    }


    /**
     * 弹出activity
     *
     * @param activity
     */
    public void popActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
        }
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void removeAndFinishLastActivity() {
        Activity activity = activityStack.lastElement();
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
        }
    }

    /**
     * 结束指定的Activity
     */
    public void removeAndFinishLastActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                removeAndFinishLastActivity(activity);
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }


    /**
     * 跳转到主页
     *
     * @param act
     */
    public void goMain(Context act) {
        finishAllGo(act, MainActivity.class);
    }

    /**
     * 结束全部，跳转到指定activity
     *
     * @param act
     * @param clazz
     */
    public void finishAllGo(Context act, Class<? extends BaseActivity> clazz) {
        finishAllActivity();
        Intent intent = createActivityIntent(act, clazz);
        act.startActivity(intent);
    }

    /**
     * 启动一个activity
     *
     * @param clazz
     */
    public static Intent createActivityIntent(Context context, Class<? extends Activity> clazz) {
        Intent intent = new Intent(context, clazz);
        if (!(context instanceof Activity)) {
            //FLAG_ACTIVITY_NEW_TASK  如果栈还存在,就用;如果不存在,就创建新的栈
            //FLAG_ACTIVITY_CLEAR_TOP   如果目标Activity存在,那么销毁目标Activity和它之上的所有Activity，重新创建目标Activity
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        return intent;
    }

    /**
     * 退出应用程序
     */
    @SuppressWarnings("deprecation")
    public void exitApp() {
        try {
            finishAllActivity();
            android.app.ActivityManager activityMgr = (android.app.ActivityManager) currentActivity().getSystemService(Context.ACTIVITY_SERVICE);
            activityMgr.restartPackage(currentActivity().getPackageName());
            System.exit(0);
        } catch (Exception e) {
        }
    }
}
