package com.jfbank.qualitymarket.dao;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.jfbank.qualitymarket.model.User;
import com.jfbank.qualitymarket.util.ConstantsUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 保存用户信息
 *
 * @author 崔朋朋
 */
public class StoreService {
    private SharedPreferences sharedPreferences;

    public StoreService(Context context) {
        super();
        sharedPreferences = context.getSharedPreferences(ConstantsUtil.SHARED_PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
    }

//    public void saveUserInfo(User user) {
//        if (user != null) {
//            Editor editor = sharedPreferences.edit();
//            editor.putString("user", JSON.toJSONString(user));
//            editor.commit();
//        }
//    }
    public void saveUserInfo(User user) {
        if (user != null) {
            Editor editor = sharedPreferences.edit();
            Field[] fields = user.getClass().getDeclaredFields();
            for (Field field : fields) {
                Method method = null;
                Object value = null;
                String name = field.getName();
                Log.e("eee", name + "tt");
                String upperName = name.substring(0, 1).toUpperCase()
                        + name.substring(1);
                try {
                    method = user.getClass()
                            .getMethod("get" + upperName);
                    value = method.invoke(user);
                    editor.putString(name, String.valueOf(value));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            editor.commit();
        }
    }

        public User getUserInfo() {
        User user = new User();
        Field[] fields = user.getClass().getDeclaredFields();
        for (Field field : fields) {
            Method method = null;
            String name = field.getName();
            String upperName = name.substring(0, 1).toUpperCase()
                    + name.substring(1);
            try {
                method = user.getClass()
                        .getMethod("set" + upperName, String.class);
                method.invoke(user, sharedPreferences.getString(name, ""));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return user;
    }
//    public void clearUserInfo() {
//        Editor editor = sharedPreferences.edit();
//        Field[] fields = User.class.getDeclaredFields();
//        for (Field field : fields) {
//            editor.putString(field.getName(), "");
//        }
//        editor.commit();
//    }
//    public User getUserInfo() {
//        User user;
//        try {
//            user = JSON.parseObject(sharedPreferences.getString("user", ""), User.class);
//        } catch (Exception e) {
//            user = new User();
//        }
//        return user;
//    }

    public void clearUserInfo() {
        Editor editor = sharedPreferences.edit();
        editor.putString("user", "");
        editor.commit();
    }
}


