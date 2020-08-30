package com.test.xcamera.widget;

import android.app.Activity;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

public class ActivityContainerHome {
    private ActivityContainerHome() {

    }

    private static ActivityContainerHome instance = new ActivityContainerHome();

    public List<WeakReference<Activity>> activityStack = new LinkedList<>();

    public int getActivityStackSize() {
        return activityStack == null ? 0 : activityStack.size();
    }

    public static ActivityContainerHome getInstance() {
        return instance;
    }

    public void addActivity(Activity aty) {
        activityStack.add(new WeakReference<>(aty));
    }

    private WeakReference<Activity> mFPVActivty;
    private FPVCloseInterface mFPVCloseInterface;

    public void finishFPVActivty() {
        if (mFPVActivty != null && mFPVActivty.get() != null) {
            if (this.mFPVCloseInterface != null)
                this.mFPVCloseInterface.onClose();
            mFPVActivty.get().finish();
        }
    }

    public void setFPVActivty(WeakReference<Activity> mFPVActivty) {
        this.mFPVActivty = mFPVActivty;
    }

    public void setFPVClose(FPVCloseInterface close) {
        this.mFPVCloseInterface = close;
    }

    /**
     * 结束所有的Activity
     */
    public void finishAllActivity() {
        if (activityStack == null) {
            return;
        }
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (activityStack.get(i) != null && null != activityStack.get(i).get()) {
                activityStack.get(i).get().finish();
            }
        }
        activityStack.clear();
    }

    public interface FPVCloseInterface {
        void onClose();
    }
}