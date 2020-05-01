package com.jfbank.qualitymarket.model;

public class BottomFunctionMenu{

    private String iconImage; //icon的url链接
    private String iconName;// icon的标签
    private int type; // //类型1标识客户端页面 2标识H5页面  3标识万卡链接
    /**
     * 跳转本地页面或者H5页面
     * “campuslocal://myBankCard” 跳转本地我的银行卡页面
      "services://inviteFriends" 跳转本地邀请好友页面
     */
    private String url;
    private String authority; // Y 需要登录  N不需要登录
    private int integral; //积分点数

    public int getIntegral() {
        return integral;
    }

    public void setIntegral(int integral) {
        this.integral = integral;
    }

    public String getIconImage() {
        return iconImage;
    }

    public void setIconImage(String iconImage) {
        this.iconImage = iconImage;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }


}