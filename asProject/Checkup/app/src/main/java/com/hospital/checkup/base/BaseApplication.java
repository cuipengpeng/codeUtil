package com.hospital.checkup.base;

import android.app.Application;
import android.content.Context;
import androidx.multidex.MultiDex;
import com.hospital.checkup.bean.UserInfoBean;
import com.hospital.checkup.http.HttpRequest;


public class BaseApplication extends Application {
    public static Context applicationContext;
    public static UserInfoBean userInfo = new UserInfoBean();

    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = getApplicationContext();
        HttpRequest.initEnvironment();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static Context getContext() {
        return applicationContext;
    }
}
