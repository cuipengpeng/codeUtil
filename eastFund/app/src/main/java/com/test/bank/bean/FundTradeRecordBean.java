package com.test.bank.bean;

/**
 * Created by 55 on 2018/1/18.
 */

public class FundTradeRecordBean {

    public FundTradeRecordBean() {
    }

    private String fundcode;
    private String fundname;
    private Integer tradetype;  //交易类型  (1普赎2快赎3申购)',
    private String trademoney;    //交易金额  (买入时候取值)
    private String tradeshare;      //交易份额(赎回时候取值)
    private Integer tradestatus;     //交易状态 0待确认、1已确认、2已撤单、3异常、4交易关闭
    private Long tradedate;        //交易申请时间戳
    private String tradeaffirmdate; //交易确认时间
    private String tradetoacctdate; //交易确认时间
    private Boolean tradetoacctdate_flag; //交易确认时间
    private String canceltradedate;         //撤单时间
    private String comfirmmoney;         //确认金额
    private String confirmshare;         //确认份额
    private String confirmnav;         //确认净值
    private String confirmnavdate;         //确认净值日期
    private String factorage;         //手续费
    private String tradeno;         //订单编号
    private Boolean iscancellations;    //是否可撤单
    private String paystatus;       //扣款状态(0支付成功、1支付失败、2未支付、3已到账)  - 到账状态（3-到账，和tradetype=2时连用，）
    /**
     * 基金状态
     0	其他
     1	正常上市
     2	暂停上市
     3	终止上市
     4	发行配售期间
     5	发行失败
     6	发行未上市
     7	发行不流通
     8	暂缓发行

     备注：

     正常上市：    可以申购   可以赎回      正常
     暂停上市       停止申购   停止赎回       和封闭期无关  正常
     终止上市       停止申购   停止赎回       和封闭期无关   正常
     发行配售期间    可以申购  不能赎回       认购期
     发行失败                                   认购
     发行未上市        募集结束但未上市    封闭期
     发行不流通         封闭期
     */
    private String fundstate;

    public String getFundcode() {
        return fundcode;
    }

    public void setFundcode(String fundcode) {
        this.fundcode = fundcode;
    }

    public String getFundname() {
        return fundname;
    }

    public void setFundname(String fundname) {
        this.fundname = fundname;
    }

    public Integer getTradetype() {
        return tradetype;
    }

    public void setTradetype(Integer tradetype) {
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

    public Integer getTradestatus() {
        return tradestatus;
    }

    public void setTradestatus(Integer tradestatus) {
        this.tradestatus = tradestatus;
    }

    public Long getTradedate() {
        return tradedate;
    }

    public void setTradedate(Long tradedate) {
        this.tradedate = tradedate;
    }

    public String getTradeaffirmdate() {
        return tradeaffirmdate;
    }

    public void setTradeaffirmdate(String tradeaffirmdate) {
        this.tradeaffirmdate = tradeaffirmdate;
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

    public String getCanceltradedate() {
        return canceltradedate;
    }

    public void setCanceltradedate(String canceltradedate) {
        this.canceltradedate = canceltradedate;
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
        return "FundTradeRecordBean{" +
                "fundcode='" + fundcode + '\'' +
                ", fundname='" + fundname + '\'' +
                ", tradetype=" + tradetype +
                ", trademoney='" + trademoney + '\'' +
                ", tradeshare='" + tradeshare + '\'' +
                ", tradestatus=" + tradestatus +
                ", tradedate=" + tradedate +
                ", tradeaffirmdate='" + tradeaffirmdate + '\'' +
                ", tradetoacctdate='" + tradetoacctdate + '\'' +
                ", tradetoacctdate_flag=" + tradetoacctdate_flag +
                ", canceltradedate='" + canceltradedate + '\'' +
                ", comfirmmoney='" + comfirmmoney + '\'' +
                ", confirmshare='" + confirmshare + '\'' +
                ", confirmnav='" + confirmnav + '\'' +
                ", confirmnavdate='" + confirmnavdate + '\'' +
                ", factorage='" + factorage + '\'' +
                ", tradeno='" + tradeno + '\'' +
                ", iscancellations=" + iscancellations +
                ", paystatus='" + paystatus + '\'' +
                ", fundstate='" + fundstate + '\'' +
                '}';
    }
}
