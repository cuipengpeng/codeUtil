package com.jf.jlfund.bean;

/**
 * Created by 55 on 2018/2/8.
 * 根据卡号查询银行名称等信息
 */

public class BankSmsCodeBean {

    private BankAuthCodeBean authCode;
    private BankInfoBean bankInfo;

    public BankSmsCodeBean() {
    }

    public BankAuthCodeBean getAuthCode() {
        return authCode;
    }

    public void setAuthCode(BankAuthCodeBean authCode) {
        this.authCode = authCode;
    }

    public BankInfoBean getBankInfo() {
        return bankInfo;
    }

    public void setBankInfo(BankInfoBean bankInfo) {
        this.bankInfo = bankInfo;
    }

    @Override
    public String toString() {
        return "BankSmsCodeBean{" +
                "authCode=" + authCode +
                ", bankInfo=" + bankInfo +
                '}';
    }

    public static class BankAuthCodeBean{
        private String accoreqserial;
        private String banksessionid;
        private String code;
        private String message;
        private String otherserial;

        public BankAuthCodeBean() {
        }

        public String getAccoreqserial() {
            return accoreqserial;
        }

        public void setAccoreqserial(String accoreqserial) {
            this.accoreqserial = accoreqserial;
        }

        public String getBanksessionid() {
            return banksessionid;
        }

        public void setBanksessionid(String banksessionid) {
            this.banksessionid = banksessionid;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getOtherserial() {
            return otherserial;
        }

        public void setOtherserial(String otherserial) {
            this.otherserial = otherserial;
        }

        @Override
        public String toString() {
            return "BankAuthCodeBean{" +
                    "accoreqserial='" + accoreqserial + '\'' +
                    ", banksessionid='" + banksessionid + '\'' +
                    ", code='" + code + '\'' +
                    ", message='" + message + '\'' +
                    ", otherserial='" + otherserial + '\'' +
                    '}';
        }
    }

    public static class BankInfoBean{
        private String bankSerial;
        private String bankname;        //银行名称
        private String icon;        //银行ICON
        private String limitday;        //日限额
        private String limitonce;        //单次限额

        public BankInfoBean() {
        }

        public String getBankSerial() {
            return bankSerial;
        }

        public void setBankSerial(String bankSerial) {
            this.bankSerial = bankSerial;
        }

        public String getBankname() {
            return bankname;
        }

        public void setBankname(String bankname) {
            this.bankname = bankname;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getLimitday() {
            return limitday;
        }

        public void setLimitday(String limitday) {
            this.limitday = limitday;
        }

        public String getLimitonce() {
            return limitonce;
        }

        public void setLimitonce(String limitonce) {
            this.limitonce = limitonce;
        }

        @Override
        public String toString() {
            return "BankInfoBean{" +
                    "bankSerial='" + bankSerial + '\'' +
                    ", bankname='" + bankname + '\'' +
                    ", icon='" + icon + '\'' +
                    ", limitday='" + limitday + '\'' +
                    ", limitonce='" + limitonce + '\'' +
                    '}';
        }
    }
}
