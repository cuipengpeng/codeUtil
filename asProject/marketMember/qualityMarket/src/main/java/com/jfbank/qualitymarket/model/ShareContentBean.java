package com.jfbank.qualitymarket.model;

import android.text.TextUtils;
import android.util.Base64;

import com.jfbank.qualitymarket.util.CommonUtils;

/**
 * 功能：<br>
 * 作者：赵海<br>
 * 时间： 2017/5/15 0015<br>.
 * 版本：1.2.0
 */

public class ShareContentBean {


    /**
     * achieve : share
     * url : aHR0cHM6Ly9zY3Rlc3QuOWZiYW5rLmNvbS9xdWFsaXR5bWFya2V0cy8jL2RldGFpbD9wcm9kdWN0Tm89cHpzYzE0NzIxMjgyNDI4MzcmaXNBY3Rpdml0eT0wJmZyb21DaGFubmVsPXB6c2M=
     * title : 小米 Max 全网通 高配版 3GB内存 64GB ROM 金色 移动联通电信4G手机
     * content : 我在万卡商城发现了一个不错的商品，赶快来看看吧!
     * authority : N
     */
    private String achieve;
    private String url;
    private String title;
    private String content;
    private String authority;

    public String getAchieve() {
        return achieve;
    }

    public void setAchieve(String achieve) {
        this.achieve = achieve;
    }

    public String getBase64DecodeUrl() {
       return CommonUtils.getBase64Url(url);
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return TextUtils.isEmpty(content) ? "我在万卡商城发现了一个不错的商品，赶快来看看吧" : content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }
}
