package com.test.bank.http;

import com.test.bank.utils.DeviceUtil;
import com.test.bank.utils.MD5;

import java.util.TreeMap;

/**
 * Created by 55 on 2017/12/5.
 */

public class ParamMap extends TreeMap<String, String> {

    public ParamMap() {
        this(false);
    }

    public ParamMap(boolean isAddSign) {
        put("channelCode", "app");
        put("mobileSystem", "android");
        put("version", "1");
        put("equipment", DeviceUtil.getDevicecId());
        if (isAddSign) {
            put("sign", MD5.sign(this));
        }
    }

    public String putLast(String key, String value) {
        put(key, value);
        return put("sign", MD5.sign(this));
    }
}
