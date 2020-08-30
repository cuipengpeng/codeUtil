package com.test.xcamera.widget;

import android.app.Activity;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

public class ActivityContainer {
    private ActivityContainer(){

    }

    private static ActivityContainer instance = new ActivityContainer();
    public  List<WeakReference<Activity>> activityStack = new LinkedList<>();


    public static ActivityContainer getInstance(){
        return instance;
    }

    public List<WeakReference<Activity>> getActivityStack() {
        return activityStack;
    }
    public void addActivity(Activity aty) {
        activityStack.add(new WeakReference<>(aty));
    }

    public void removeActivity(Activity aty) {
        activityStack.remove(new WeakReference<>(aty));
    }
/*
结束所有的Activity*/

    public void finishAllActivity(){
        for (int i = 0 , size = activityStack.size(); i < size;i++) {
            if (null != activityStack.get(i)&&activityStack.get(i).get()!=null) {
                activityStack.get(i).get().finish();
            }
        }
        activityStack.clear();
    }

}