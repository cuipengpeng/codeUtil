package com.jfbank.qualitymarket.util;

import android.content.Context;

import com.jfbank.qualitymarket.AppContext;
import com.sptalkingdata.sdk.SPAgent;

import java.util.HashMap;
import java.util.Map;

/**
 * 功能：TalkingData埋点工具类<br>
 * 作者：赵海<br>
 * 时间： 2017/6/14 0014<br>.
 * 版本：1.2.0
 */

public class TDUtils {
    Map<String, Object> params;

    /**
     * 获取TD对象
     *
     * @return
     */
    public static TDUtils getInstance() {
        return new TDUtils();
    }

    public TDUtils() {
        params = new HashMap<>();
    }

    /**
     * 埋点事件
     *
     * @param context
     * @param tdId    埋点id
     * @param label   埋点name
     * @param params  參數
     */
    public synchronized static void onEvent(Context context, String tdId, String label, Map<String, Object> params) {
        if (params == null || params.size() == 0) {
            SPAgent.onEvent(context, tdId, label);
        } else {
            SPAgent.onEvent(context, tdId, label, params);
        }
    }

    public synchronized static void onEventNavigation(Context context, int position, String label, Map<String, Object> params) {
        if (position > 9) {
            return;
        }
        onEvent(context, (position + 100022) + "", label, params);
    }

    public TDUtils putParams(String key, Object value) {
        params.put(key, value);
        return this;
    }

    public TDUtils putUserid() {
        if (AppContext.isLogin && AppContext.user != null)
            params.put("userid", AppContext.user.getUid());
        return this;
    }

    public Map<String, Object> buildParams() {
        return params;
    }

    /**
     * 埋点事件
     *
     * @param context
     * @param tdId    埋点id
     * @param label   埋点name
     */
    public static void onEvent(Context context, String tdId, String label) {
        onEvent(context, tdId, label, null);
        return;
    }
}
