package com.test.bank.bean;

import java.io.Serializable;
import java.util.ArrayList;

/*
* 描    述：<br>
* 作    者：崔朋朋<br>
* 时    间：2017/12/12<br>
*/
public class FundManagerDetailBean {

    private ArrayList<FundManager> fundManager;

    public static class Fundlist implements Serializable{
        private String fundcode;
        private String fundsname;
        private String chng_pct;
        private String declaredate;

        public String getFundcode() {
            return fundcode;
        }

        public void setFundcode(String fundcode) {
            this.fundcode = fundcode;
        }

        public String getFundsname() {
            return fundsname;
        }

        public void setFundsname(String fundsname) {
            this.fundsname = fundsname;
        }

        public String getChng_pct() {
            return chng_pct;
        }

        public void setChng_pct(String chng_pct) {
            this.chng_pct = chng_pct;
        }

        public String getDeclaredate() {
            return declaredate;
        }

        public void setDeclaredate(String declaredate) {
            this.declaredate = declaredate;
        }
    }

    public static class FundManager implements Serializable{
        private String resume;
        private String indi_name;
        private String declaredate;
        private ArrayList<Fundlist> fundlist;

        public String getResume() {
            return resume;
        }

        public void setResume(String resume) {
            this.resume = resume;
        }

        public String getIndi_name() {
            return indi_name;
        }

        public void setIndi_name(String indi_name) {
            this.indi_name = indi_name;
        }

        public String getDeclaredate() {
            return declaredate;
        }

        public void setDeclaredate(String declaredate) {
            this.declaredate = declaredate;
        }

        public ArrayList<Fundlist> getFundlist() {
            return fundlist;
        }

        public void setFundlist(ArrayList<Fundlist> fundlist) {
            this.fundlist = fundlist;
        }
    }

    public ArrayList<FundManager> getFundManager() {
        return fundManager;
    }

    public void setFundManager(ArrayList<FundManager> fundManager) {
        this.fundManager = fundManager;
    }
}
