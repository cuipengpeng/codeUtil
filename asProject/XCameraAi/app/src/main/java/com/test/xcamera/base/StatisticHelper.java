package com.test.xcamera.base;

import android.content.Context;
import android.text.TextUtils;

import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

/**
 * Created by admin on 2019/11/5.
 * 统计使用，现在只内置了友盟统计，以后会增加其他的统计。
 */

public class StatisticHelper {
    public static void onEvent(Context context, String action) {
        if (TextUtils.isEmpty(action)) {
            return;
        }
        UmengStatistic.umengOnEvent(context, action);
    }
    public static void onEvent(Context context, String action,String value) {
        if (TextUtils.isEmpty(action)||TextUtils.isEmpty(value)) {
            return;
        }
        UmengStatistic.umengOnEvent(context, action, value);

    }
    public static void onEvent(Context context, String action, HashMap<String, Object> map) {
        if (map == null || map.size() == 0) {
            return;
        }
        UmengStatistic.umengOnEvent(context, action, map);
    }

    public static void onPause(Context context) {
        UmengStatistic.umengOnPause(context);
    }

    public static void onResume(Context context) {
        UmengStatistic.umengOnResume(context);
    }


    private static class UmengStatistic {

        private static boolean openUmeng = true;
        private static void umengOnEvent(Context context, String id) {
            if (openUmeng) {
                MobclickAgent.onEvent(context, id);
            }
        }
        private static void umengOnEvent(Context context, String id, String value) {
            if (openUmeng) {
                MobclickAgent.onEvent(context, id, value);
            }
        }
        private static void umengOnEvent(Context context, String id, HashMap<String, Object> map) {
            if (openUmeng) {
                MobclickAgent.onEventObject(context, id, map);
            }
        }

        private static void umengOnResume(Context context) {
            if (openUmeng) {
                MobclickAgent.onResume(context);
            }
        }

        private static void umengOnPause(Context context) {
            if (openUmeng) {
                MobclickAgent.onPause(context);
            }
        }

    }

}
