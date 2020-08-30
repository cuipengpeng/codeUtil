package com.test.xcamera.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.test.xcamera.application.AiCameraApplication;
import com.test.xcamera.bean.User;
import com.test.xcamera.bean.VideoTemplete;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by mz
 *
 */
public class SPUtils {
    // 保存在手机里面的文件名
    public static final String FILE_NAME = "com.aimocarma.data";
    public static final String KEY_OF_TOKEN = "token";
    public static final String KEY_OF_USER_INFO = "userInfoKey";
    public static final String KEY_OF_ONLINE_ENV = "testEnvKey";


    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     */
    public static void put(Context context, String key, Object object) {
        if (object == null) {
            //保存值为空，移除相关内容
            remove(context, key);
            return;
        }
        String type = object.getClass().getSimpleName();
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if ("String".equals(type)) {
            editor.putString(key, (String) object);
        } else if ("Integer".equals(type)) {
            editor.putInt(key, (Integer) object);
        } else if ("Boolean".equals(type)) {
            editor.putBoolean(key, (Boolean) object);
        } else if ("Float".equals(type)) {
            editor.putFloat(key, (Float) object);
        } else if ("Long".equals(type)) {
            editor.putLong(key, (Long) object);
        } else if ("PersistentCookieStore".equals(type)) {
        }
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     */
    public static Object get(Context context, String key, Object defaultObject) {
        if (defaultObject == null) {
            return null;
        }
        String type = defaultObject.getClass().getSimpleName();
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        if ("String".equals(type)) {
            return sp.getString(key, (String) defaultObject);
        } else if ("Integer".equals(type)) {
            return sp.getInt(key, (Integer) defaultObject);
        } else if ("Boolean".equals(type)) {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if ("Float".equals(type)) {
            return sp.getFloat(key, (Float) defaultObject);
        } else if ("Long".equals(type)) {
            return sp.getLong(key, (Long) defaultObject);
        }
        return defaultObject;
    }

    /**
     * 移除某个key值已经对应的值
     */
    public static void remove(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 清除所有数据
     */
    public static void clear(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 查询某个key是否已经存在
     *
     * @param key 要查询的key
     * @return true：包含，false：不包含
     */
    public static boolean contains(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return sp.contains(key);
    }

    /**
     * 返回所有的键值对
     *
     * @return 键值对集合
     */
    public static Map<String, ?> getAll(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return sp.getAll();
    }

    /**
     * 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
     *
     * @author mz
     */
    private static class SharedPreferencesCompat {
        private static final Method sApplyMethod = findApplyMethod();

        /**
         * 反射查找apply的方法
         *
         * @return
         */
        @SuppressWarnings({"unchecked", "rawtypes"})
        private static Method findApplyMethod() {
            try {
                Class clz = SharedPreferences.Editor.class;
                return clz.getMethod("apply");
            } catch (NoSuchMethodException e) {
            }

            return null;
        }

        /**
         * 如果找到则使用apply执行，否则使用commit
         *
         * @param editor
         */
        public static void apply(SharedPreferences.Editor editor) {
            try {
                if (sApplyMethod != null) {
                    sApplyMethod.invoke(editor);
                    return;
                }
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
            }
            editor.commit();
        }
    }

    /**
     * 判断token是否登陆过
     * @param mcontext
     * @return
     */
    public static boolean isLogin(Context mcontext){
        String   token = (String) SPUtils.get(mcontext,"token","");
        return !TextUtils.isEmpty(token);
    }
    /**
     * 退出登录
     * @param mcontext
     * @return
     */
    public static boolean unLogin(Context mcontext){
        SPUtils.clearUserInfo(mcontext);
        SPUtils.remove(mcontext,"token");
        SPUtils.remove(mcontext,"user");
        SPUtils.remove(mcontext,KEY_OF_USER_INFO);
        return true;
    }
    /**
     * 获取token
     * @param mcontext
     * @return
     */
    public static String  getToken(Context mcontext){
        String   token = (String) SPUtils.get(mcontext,"token","");
        return token;
    }


    /**
     * 获取User对象
     * @param mcontext
     * @return
     */
    public static User.UserDetail  getUser(Context mcontext){
        String str= (String) get(mcontext,"user","");
        Gson gson=new Gson();
       return gson.fromJson(str, User.UserDetail.class);
    }

    /**
     * 获取设备的uuid
     * @param mContext
     * @return
     */
    public static  String  getUuid(Context mContext){
        String str= (String) SPUtils.get(mContext,"UUID","");
        if(TextUtils.isEmpty(str)){
            String uuid= DeviceIdUtil.getDeviceId(AiCameraApplication.getContext());
            SPUtils.put(mContext,"UUID",uuid);
        }
        return (String) SPUtils.get(mContext,"UUID","");
    }

    private static void clearUserInfo(Context context) {
        SPUtils.put(context, KEY_OF_TOKEN, "");
        SPUtils.writeObject(context, SPUtils.KEY_OF_USER_INFO, new User.UserDetail());
    }

    public static Object readObject(Context context, String key, Object returnInstance) {
        Object object = new Object();
        SharedPreferences sharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        if(sharedPreferences.contains(key)){
            LoggerUtils.printLog("sharedPreferences.getString()=  "+sharedPreferences.getString(key, "{}"));
            if(returnInstance instanceof  VideoTemplete){
                object = new Gson().fromJson(sharedPreferences.getString(key, "{}"), VideoTemplete.class);
            }else if(returnInstance instanceof User.UserDetail){
                object = new Gson().fromJson(sharedPreferences.getString(key, "{}"), User.UserDetail.class);
            }
        }
        return object;
    }



    public static void writeObject(Context context, String key, Object cacheData) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
         Gson gson = new Gson();
        if(cacheData instanceof User.UserDetail){
            AiCameraApplication.userDetail  = (User.UserDetail) cacheData;
        }
        LoggerUtils.printLog(" JSON.toJSONString(userInfo)="+ gson.toJson(cacheData));
        sharedPreferences.edit().putString(key, gson.toJson(cacheData)).commit();
    }
}