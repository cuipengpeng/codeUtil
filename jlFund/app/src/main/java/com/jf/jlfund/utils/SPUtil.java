package com.jf.jlfund.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.jf.jlfund.base.BaseApplication;
import com.jf.jlfund.bean.UserInfo;

/**
 * SharedPreferences管理类
 */
public class SPUtil {
    private static SPUtil SPUtil = new SPUtil();
    private static SharedPreferences sp;
    private static Gson gson = new Gson();

    private SPUtil() {
    }

    public static SPUtil getInstance() {
        if (sp == null) {
            sp = BaseApplication.getContext().getSharedPreferences("JLFund", Context.MODE_PRIVATE);
        }
        return SPUtil;
    }

    public void putInt(String key, int value) {
        sp.edit().putInt(key, value).apply();
    }

    public void putLong(String key, long value) {
        sp.edit().putLong(key, value).apply();
    }

    public void putString(String key, String value) {
        sp.edit().putString(key, value).apply();
    }

    public void putBoolean(String key, boolean value) {
        sp.edit().putBoolean(key, value).apply();
    }

    public void putFloat(String key, float value) {
        sp.edit().putFloat(key, value).apply();
    }

    public int getInt(String key) {
        if (key == null) {
            return 0;
        }
        return sp.getInt(key, 0);
    }

    public long getLong(String key) {
        if (key == null) {
            return 0;
        }
        return sp.getLong(key, -1);
    }

    public String getString(String key) {
        return sp.getString(key, null);
    }

    public boolean getBoolean(String key) {
        return sp.getBoolean(key, false);
    }

    public float getFloat(String key) {
        return sp.getFloat(key, 0f);
    }

    //清除用户信息
    public void clearUserInfo() {
        sp.edit().putString("userInfo", "{}").apply();
    }

    public boolean isLogin() {
        return !TextUtils.isEmpty(getMobile());
    }

    //存储用户信息
    public void putUserInfo(UserInfo userInfo) {
        sp.edit().putString("userInfo", gson.toJson(userInfo)).apply();
    }

    //获取本地缓存用户信息
    public UserInfo getUserInfo() {
        return gson.fromJson(sp.getString("userInfo", "{}"), UserInfo.class);
    }

    public String getMobile() {
        UserInfo userInfo = getUserInfo();
        return userInfo == null ? "" : userInfo.getMobile();
    }

    //获取用户Token
    public String getToken() {
        UserInfo userInfo = getUserInfo();
        return null == userInfo ? "" : userInfo.getToken();
    }

    private static final String TIMESTAMP_OF_GET_SMS_CODE = "TIMESTAMP_OF_GET_SMS_CODE";

    public Long getLastDayOfWeek() {
        return sp.getLong(TIMESTAMP_OF_GET_SMS_CODE, 0);
    }

    public void setCurrDayOfWeek(long timestampOfGetSmsCode) {
        sp.edit().putLong(TIMESTAMP_OF_GET_SMS_CODE, timestampOfGetSmsCode).apply();
    }

    public static final String KEY_IS_OPEN_GENSTURE_PWD = "isOpenGensturePwd";    //是否开启了手势密码
    public static final String KEY_IS_OPEN_FINGER_PWD = "isOpenFingerPwd";    //是否开启了指纹密码
    public static final String KEY_SMS_CODE_TIMESTAMP_FORGET_RESET_LOGIN_PWD = "countdownTimeStamp_resetLoginPwd";    //忘记登录密码。。。倒计时时间戳记录，用于退出页面再次进入继续倒计时


    public static UserInfo readUserConfigInfo(Context context, UserInfo userInfo) {
        UserInfo configUserInfo = new UserInfo();
        SharedPreferences sharedPreferences = context.getSharedPreferences(ConstantsUtil.Config.USER_SETTING_CONFIG, Context.MODE_PRIVATE);
        if(sharedPreferences.contains(userInfo.getMobile())){
            //读取该用户之前的配置信息
            LogUtils.printLog("sharedPreferences.getString(userInfo.getMobile())="+sharedPreferences.getString(userInfo.getMobile(), "{}"));
            configUserInfo = JSON.parseObject(sharedPreferences.getString(userInfo.getMobile(), "{}"), UserInfo.class);
            userInfo.setGesturePassword(configUserInfo.getGesturePassword());
            userInfo.setHasFinderprintPassword(configUserInfo.isHasFinderprintPassword());
            userInfo.setHiddenAccountMoney(configUserInfo.isHiddenAccountMoney());
        }
        return userInfo;
    }


    public static void writeUserConfigInfo(Context context, UserInfo userInfo) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(ConstantsUtil.Config.USER_SETTING_CONFIG, Context.MODE_PRIVATE);
        LogUtils.printLog(" JSON.toJSONString(userInfo)="+ JSON.toJSONString(userInfo));
        sharedPreferences.edit().putString(userInfo.getMobile(), JSON.toJSONString(userInfo)).apply();

        SPUtil.getInstance().putUserInfo(userInfo);
    }
}
