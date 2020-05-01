package com.jfbank.qualitymarket.model;

import java.io.Serializable;

/**
 * @author 崔朋朋
 */
public class User extends ResponseBean {
    private String uid;    //用户uid
    private String createTime;//创建时间
    private String enchashmentLine;//取现额度
    private String uname;//用户姓名
    private String billDate;//账单日
    private String idName;//身份证姓名
    private String clientId; //
    private String function;//调用功能
    private String version; //客户端版本号
    private String nickName; //用户昵称
    private String token; //登录token
    private String inviteCode; //邀请码
    private String channel;//渠道
    private String mobile; //手机号
    private String idNumber; //身份证号
    private String loginTime; //登录时间
    private String selfInviteCode; //自身邀请码
    private String isContact;//是否上传通讯录  0没有,1有
    private String couponCount;//红包数
    private String billCount;//本期应还
    private String settledDay;//剩余天数
    private String userKey;//联合登录，用户key值
    private String creditLine;//信用额度
    private String usableLine;//可用额度
    private boolean showDetail;//是否获得额度
    private String creditlineApplyId4show;        //申请额度ID
    private String Status1; //还款日状态
    private String faceCode; //人脸识别分值
    private String faceId; //人脸识别id
    private String channelName; //门店名称
    private int  ImmediateRepayment;//立即还款	ImmediateRepayment	Int			0不显示,1显示
    private String  realNameUrl;//

    private String message;//内容
    private String url;//跳转连接
//    1未实名
//    2未设置交易密码
//    3用户无额度
//    4用户额度未激活
//    5用户额度已冻结
//    6未开卡
//    7已冻结
//    8额度状态未审批
//    9信用额度不足
//    0成功
    private String step;

    public String getRealNameUrl() {
        return realNameUrl;
    }

    public void setRealNameUrl(String realNameUrl) {
        this.realNameUrl = realNameUrl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public int getImmediateRepayment() {
        return ImmediateRepayment;
    }

    public void setImmediateRepayment(int immediateRepayment) {
        ImmediateRepayment = immediateRepayment;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getFaceId() {
        return faceId;
    }

    public void setFaceId(String faceId) {
        this.faceId = faceId;
    }

    public String getStatus1() {
        return Status1;
    }

    public void setStatus1(String status1) {
        Status1 = status1;
    }

    /**
     * @return the userKey
     */
    public String getUserKey() {
        return userKey;
    }

    /**
     * @param userKey the userKey to set
     */
    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public String getFaceCode() {
        return faceCode;
    }

    public void setFaceCode(String faceCode) {
        this.faceCode = faceCode;
    }


    public User() {
        super();
    }

    public boolean isShowDetail() {
        return showDetail;
    }

    public boolean getShowDetail() {
        return showDetail;
    }

    public void setShowDetail(boolean showDetail) {
        this.showDetail = showDetail;
    }

    public String getCreditlineApplyId4show() {
        return creditlineApplyId4show;
    }

    public void setCreditlineApplyId4show(String creditlineApplyId4show) {
        this.creditlineApplyId4show = creditlineApplyId4show;
    }

    public String getCouponCount() {
        return couponCount;
    }

    public void setCouponCount(String couponCount) {
        this.couponCount = couponCount;
    }

    public String getBillCount() {
        return billCount;
    }

    public void setBillCount(String billCount) {
        this.billCount = billCount;
    }


    public String getSettledDay() {
        return settledDay;
    }


    public void setSettledDay(String settledDay) {
        this.settledDay = settledDay;
    }


    public String getIsContact() {
        return isContact;
    }

    public void setIsContact(String isContact) {
        this.isContact = isContact;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getEnchashmentLine() {
        return enchashmentLine;
    }

    public void setEnchashmentLine(String enchashmentLine) {
        this.enchashmentLine = enchashmentLine;
    }

    public String getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(String loginTime) {
        this.loginTime = loginTime;
    }

    public String getSelfInviteCode() {
        return selfInviteCode;
    }

    public void setSelfInviteCode(String selfInviteCode) {
        this.selfInviteCode = selfInviteCode;
    }

    public String getCreditLine() {
        return creditLine;
    }

    public void setCreditLine(String creditLine) {
        this.creditLine = creditLine;
    }

    public String getUsableLine() {
        return usableLine;
    }

    public void setUsableLine(String usableLine) {
        this.usableLine = usableLine;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getBillDate() {
        return billDate;
    }

    public void setBillDate(String billDate) {
        this.billDate = billDate;
    }

    public String getIdName() {
        return idName;
    }

    public void setIdName(String idName) {
        this.idName = idName;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getStatusDetail() {
        return statusDetail;
    }

    public void setStatusDetail(String statusDetail) {
        this.statusDetail = statusDetail;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

}
