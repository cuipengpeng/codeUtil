package com.jfbank.qualitymarket.net;

import android.util.Log;

import com.jfbank.qualitymarket.AppContext;
 import java.util.HashMap; import java.util.Map;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

/**
 * 功能：RequestParmsUtils创建参数<br>
 * 作者：赵海<br>
 * 时间： 2017/4/17 0017<br>.
 * 版本：1.2.0
 */

public class RequestParmsUtils {
    Map<String, String> params;
    Map<String, File> paramsFiles;
    private final static String PARAMS_PAGENUM = "pageNo";

    public RequestParmsUtils() {
        params = new HashMap<>();
    }

    /**
     * 初始化参数对象
     *
     * @return
     */
    public static RequestParmsUtils getParamsInstance() {
        RequestParmsUtils requestParams = new RequestParmsUtils();
        return requestParams;
    }

    /**
     * 添加参数
     *
     * @param key   参数名称
     * @param value 参数值
     * @return
     */
    public RequestParmsUtils addParams(String key, String value) {
        params.put(key, value);
        return this;
    }

    /**
     * 添加参数
     *
     * @param key   参数名称
     * @param value 参数值
     * @return
     */
    public RequestParmsUtils addFileParams(String key, File value) {
        if (paramsFiles == null)
            paramsFiles = new HashMap<>();
        try {
            paramsFiles.put(key, value);
        } catch (Exception e) {
            Log.e("Exception_addFileParams", e.getMessage() + "");
        }
        return this;
    }


    /**
     * 添加分页
     *
     * @param value
     * @return
     */
    public RequestParmsUtils addPageNum(int value) {
        params.put(PARAMS_PAGENUM, value+"");
        return this;
    }

    public RequestParmsUtils needLogin() {
        if (AppContext.isLogin && AppContext.user != null) {
            params.put("uid", AppContext.user.getUid());
            params.put("token", AppContext.user.getToken());
        }
        return this;
    }

    /**
     * 新建返回BulidParams
     *
     * @return
     */
    public Map<String, File> bulidFilesParams() {
        return paramsFiles;
    }

    /**
     * 新建返回BulidParams
     *
     * @return
     */
    public Map<String, String> bulidParams() {
        return params;
    }
}
