package com.jfbank.qualitymarket.model;

import com.alibaba.fastjson.JSON;

import org.json.JSONObject;

/**
 * 功能：<br>
 * 作者：赵海<br>
 * 时间： 2017/5/15 0015<br>.
 * 版本：1.2.0
 */

public class MenuNavBean {

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    /**
     * type : button
     * achieve : showShare
     * param : {"achieve":"share","url":"aHR0cHM6Ly9zY3Rlc3QuOWZiYW5rLmNvbS9xdWFsaXR5bWFya2V0cy8jL2RldGFpbD9wcm9kdWN0Tm89cHpzYzE0ODk0OTIzNTEwMDEmUGxhdD1pT1MmdmVyPTEuNC4wJmZyb21DaGFubmVsPXB6c2M=","title":"黑奢惠活动添加商品1","content":"我在万卡商城发现了一个不错的商品，赶快来看看吧!","authority":"N"}
     */
    private String action;
    private String type;
    private String achieve;

    /**
     * achieve : share
     * url : aHR0cHM6Ly9zY3Rlc3QuOWZiYW5rLmNvbS9xdWFsaXR5bWFya2V0cy8jL2RldGFpbD9wcm9kdWN0Tm89cHpzYzE0ODk0OTIzNTEwMDEmUGxhdD1pT1MmdmVyPTEuNC4wJmZyb21DaGFubmVsPXB6c2M=
     * title : 黑奢惠活动添加商品1
     * content : 我在万卡商城发现了一个不错的商品，赶快来看看吧!
     * authority : N
     */


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAchieve() {
        return achieve;
    }

    public void setAchieve(String achieve) {
        this.achieve = achieve;
    }

    public String getParamStr(String jsonStr) {
        return JSON.parseObject(jsonStr).getString("param");
    }


}
