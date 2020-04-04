package com.caishi.chaoge.bean;

import com.caishi.chaoge.base.BaseBean;

public class LoginBean extends BaseBean {
    public String userId = "123456";//		userId
    public String credential;        //token
    public String avatar;        //头像
    public String mobile;        //手机号
    public String nickname;        //nic
    public int enable;        //是否封禁 0封禁
    public int isFirst;        //是否第一次登陆  0 不是 1是
}
