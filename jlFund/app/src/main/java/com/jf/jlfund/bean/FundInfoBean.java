package com.jf.jlfund.bean;

import com.jf.jlfund.base.BaseBean;

import java.io.Serializable;
import java.util.ArrayList;

/*
* 描    述：<br>
* 作    者：崔朋朋<br>
* 时    间：2017/12/12<br>
*/
public class FundInfoBean extends BaseBean{

    private String fundtype;
    private String fundcode;
    private int fundstatus;
    private String fundstateName;
    private ArrayList<Divlist> divlist;//分红列表
    private String act_val;//首募规模
    private String createdate;
    private String new_nav;//最新份额
    private String new_val;//最新规模
    private String custrisk;
    private String fundname;

    private long collectTime;
    private String daygrowth;
    private String fundCode;
    private String fundName;
    private String nav;

    public long getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(long collectTime) {
        this.collectTime = collectTime;
    }

    public String getDaygrowth() {
        return daygrowth;
    }

    public void setDaygrowth(String daygrowth) {
        this.daygrowth = daygrowth;
    }

    public String getFundCode() {
        return fundCode;
    }

    public void setFundCode(String fundCode) {
        this.fundCode = fundCode;
    }

    public String getFundName() {
        return fundName;
    }

    public void setFundName(String fundName) {
        this.fundName = fundName;
    }

    public String getNav() {
        return nav;
    }

    public void setNav(String nav) {
        this.nav = nav;
    }



    public String getFundstateName() {
        return fundstateName;
    }

    public void setFundstateName(String fundstateName) {
        this.fundstateName = fundstateName;
    }
    public String getFundtype() {
        return fundtype;
    }

    public void setFundtype(String fundtype) {
        this.fundtype = fundtype;
    }

    public String getFundcode() {
        return fundcode;
    }

    public void setFundcode(String fundcode) {
        this.fundcode = fundcode;
    }

    public ArrayList<Divlist> getDivlist() {
        return divlist;
    }

    public void setDivlist(ArrayList<Divlist> divlist) {
        this.divlist = divlist;
    }

    public int getFundstatus() {
        return fundstatus;
    }

    public void setFundstatus(int fundstatus) {
        this.fundstatus = fundstatus;
    }

    public String getAct_val() {
        return act_val;
    }

    public void setAct_val(String act_val) {
        this.act_val = act_val;
    }

    public String getCreatedate() {
        return createdate;
    }

    public void setCreatedate(String createdate) {
        this.createdate = createdate;
    }

    public String getNew_nav() {
        return new_nav;
    }

    public void setNew_nav(String new_nav) {
        this.new_nav = new_nav;
    }

    public String getNew_val() {
        return new_val;
    }

    public void setNew_val(String new_val) {
        this.new_val = new_val;
    }

    public String getCustrisk() {
        return custrisk;
    }

    public void setCustrisk(String custrisk) {
        this.custrisk = custrisk;
    }

    public String getFundname() {
        return fundname;
    }

    public void setFundname(String fundname) {
        this.fundname = fundname;
    }


    public static class Divlist implements Serializable{
        private String regi_date_net_val;
        private String pay_date;
        private String unit_div;

        public String getRegi_date_net_val() {
            return regi_date_net_val;
        }

        public void setRegi_date_net_val(String regi_date_net_val) {
            this.regi_date_net_val = regi_date_net_val;
        }

        public String getPay_date() {
            return pay_date;
        }

        public void setPay_date(String pay_date) {
            this.pay_date = pay_date;
        }

        public String getUnit_div() {
            return unit_div;
        }

        public void setUnit_div(String unit_div) {
            this.unit_div = unit_div;
        }
    }


}
