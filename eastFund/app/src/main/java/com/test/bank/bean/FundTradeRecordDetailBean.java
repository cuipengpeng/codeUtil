package com.test.bank.bean;

/**
 * Created by 55 on 2018/1/23.
 */

public class FundTradeRecordDetailBean {
    private String fundcode;
    private String bankInfo;        //赎回使用(银行卡名称和后四位)
    private String fundname;
    private int tradetype;      //交易类型1普赎2快赎3申购
    private String trademoney;      //交易金额
    private String tradeshare;      //赎回份额
    private int tradestatus;        //交易状态 0待确认、1已确认、2已撤单、3异常、4交易关闭
    private String tradedate;       //交易申请时间
    private String tradeaffirmdate;     //交易确认时间   (在第二个节点展示)
    private Boolean tradeaffirmdate_flag;       //第二个节点  true 亮 false不亮
    private String tradetoacctdate;     //交易到账时间   (赎回第三个节点)
    private Boolean tradetoacctdate_flag;   //交易到账标示     (赎回第三个节点) true 亮 false不亮
    private String tradeincomedate;     //交易查看收益时间   (购买第三个节点)
    private Boolean tradeincomedate_flag;        //交易查看收益标示    (购买第三个节点) true 亮 false不亮
    private String comfirmmoney;        //确认金额
    private String confirmshare;        //确认份额
    private String confirmnav;          //确认净值
    private String confirmnavdate;      //确认净值日期
    private String factorage;           //手续费
    private String tradeno;             //订单号
    private Boolean iscancellations;     //是否可撤单  true 可以撤单   false不可以
    private Boolean isBuyWithdrawnAccount;   //false //购买撤单到银行卡 false 没有 true 到银行卡
    private String paystatus;       //0支付成功、1支付失败、2未支付、3已到账
    /**
     * 基金状态
     * 0	其他
     * 1	正常上市
     * 2	暂停上市
     * 3	终止上市
     * 4	发行配售期间
     * 5	发行失败
     * 6	发行未上市
     * 7	发行不流通
     * 8	暂缓发行
     * <p>
     * 备注：
     * <p>
     * 正常上市：    可以申购   可以赎回      正常
     * 暂停上市       停止申购   停止赎回       和封闭期无关  正常
     * 终止上市       停止申购   停止赎回       和封闭期无关   正常
     * 发行配售期间    可以申购  不能赎回       认购期
     * 发行失败                                   认购
     * 发行未上市        募集结束但未上市    封闭期
     * 发行不流通         封闭期
     */
    private String fundstate;

    public FundTradeRecordDetailBean() {
    }

    public String getFundcode() {
        return fundcode;
    }

    public void setFundcode(String fundcode) {
        this.fundcode = fundcode;
    }

    public String getBankInfo() {
        return bankInfo;
    }

    public void setBankInfo(String bankInfo) {
        this.bankInfo = bankInfo;
    }

    public String getFundname() {
        return fundname;
    }

    public void setFundname(String fundname) {
        this.fundname = fundname;
    }

    public int getTradetype() {
        return tradetype;
    }

    public void setTradetype(int tradetype) {
        this.tradetype = tradetype;
    }

    public String getTrademoney() {
        return trademoney;
    }

    public void setTrademoney(String trademoney) {
        this.trademoney = trademoney;
    }

    public String getTradeshare() {
        return tradeshare;
    }

    public void setTradeshare(String tradeshare) {
        this.tradeshare = tradeshare;
    }

    public int getTradestatus() {
        return tradestatus;
    }

    public void setTradestatus(int tradestatus) {
        this.tradestatus = tradestatus;
    }

    public String getTradedate() {
        return tradedate;
    }

    public void setTradedate(String tradedate) {
        this.tradedate = tradedate;
    }

    public String getTradeaffirmdate() {
        return tradeaffirmdate;
    }

    public void setTradeaffirmdate(String tradeaffirmdate) {
        this.tradeaffirmdate = tradeaffirmdate;
    }

    public Boolean getTradeaffirmdate_flag() {
        return tradeaffirmdate_flag;
    }

    public void setTradeaffirmdate_flag(Boolean tradeaffirmdate_flag) {
        this.tradeaffirmdate_flag = tradeaffirmdate_flag;
    }

    public String getTradetoacctdate() {
        return tradetoacctdate;
    }

    public void setTradetoacctdate(String tradetoacctdate) {
        this.tradetoacctdate = tradetoacctdate;
    }

    public Boolean getTradetoacctdate_flag() {
        return tradetoacctdate_flag;
    }

    public void setTradetoacctdate_flag(Boolean tradetoacctdate_flag) {
        this.tradetoacctdate_flag = tradetoacctdate_flag;
    }

    public String getTradeincomedate() {
        return tradeincomedate;
    }

    public void setTradeincomedate(String tradeincomedate) {
        this.tradeincomedate = tradeincomedate;
    }

    public Boolean getTradeincomedate_flag() {
        return tradeincomedate_flag;
    }

    public void setTradeincomedate_flag(Boolean tradeincomedate_flag) {
        this.tradeincomedate_flag = tradeincomedate_flag;
    }

    public String getComfirmmoney() {
        return comfirmmoney;
    }

    public void setComfirmmoney(String comfirmmoney) {
        this.comfirmmoney = comfirmmoney;
    }

    public String getConfirmshare() {
        return confirmshare;
    }

    public void setConfirmshare(String confirmshare) {
        this.confirmshare = confirmshare;
    }

    public String getConfirmnav() {
        return confirmnav;
    }

    public void setConfirmnav(String confirmnav) {
        this.confirmnav = confirmnav;
    }

    public String getConfirmnavdate() {
        return confirmnavdate;
    }

    public void setConfirmnavdate(String confirmnavdate) {
        this.confirmnavdate = confirmnavdate;
    }

    public String getFactorage() {
        return factorage;
    }

    public void setFactorage(String factorage) {
        this.factorage = factorage;
    }

    public String getTradeno() {
        return tradeno;
    }

    public void setTradeno(String tradeno) {
        this.tradeno = tradeno;
    }

    public Boolean getIscancellations() {
        return iscancellations;
    }

    public void setIscancellations(Boolean iscancellations) {
        this.iscancellations = iscancellations;
    }

    public Boolean getIsBuyWithdrawnAccount() {
        return isBuyWithdrawnAccount;
    }

    public void setIsBuyWithdrawnAccount(Boolean isBuyWithdrawnAccount) {
        this.isBuyWithdrawnAccount = isBuyWithdrawnAccount;
    }

    public String getPaystatus() {
        return paystatus;
    }

    public void setPaystatus(String paystatus) {
        this.paystatus = paystatus;
    }

    public String getFundstate() {
        return fundstate;
    }

    public void setFundstate(String fundstate) {
        this.fundstate = fundstate;
    }

    @Override
    public String toString() {
        return "FundTradeRecordDetailBean{" +
                "fundcode='" + fundcode + '\'' +
                ", bankInfo='" + bankInfo + '\'' +
                ", fundname='" + fundname + '\'' +
                ", tradetype=" + tradetype +
                ", trademoney='" + trademoney + '\'' +
                ", tradeshare='" + tradeshare + '\'' +
                ", tradestatus=" + tradestatus +
                ", tradedate='" + tradedate + '\'' +
                ", tradeaffirmdate='" + tradeaffirmdate + '\'' +
                ", tradeaffirmdate_flag=" + tradeaffirmdate_flag +
                ", tradetoacctdate='" + tradetoacctdate + '\'' +
                ", tradetoacctdate_flag=" + tradetoacctdate_flag +
                ", tradeincomedate='" + tradeincomedate + '\'' +
                ", tradeincomedate_flag=" + tradeincomedate_flag +
                ", comfirmmoney='" + comfirmmoney + '\'' +
                ", confirmshare='" + confirmshare + '\'' +
                ", confirmnav='" + confirmnav + '\'' +
                ", confirmnavdate='" + confirmnavdate + '\'' +
                ", factorage='" + factorage + '\'' +
                ", tradeno='" + tradeno + '\'' +
                ", iscancellations='" + iscancellations + '\'' +
                ", isBuyWithdrawnAccount='" + isBuyWithdrawnAccount + '\'' +
                ", paystatus='" + paystatus + '\'' +
                ", fundstate='" + fundstate + '\'' +
                '}';
    }
}
