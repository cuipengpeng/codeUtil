package com.jf.jlfund.bean;

import java.util.ArrayList;

/*
* 描    述：<br>
* 作    者：崔朋朋<br>
* 时    间：2017/12/12<br>
*/
public class IndustryDistributionBean {

    private String rpt_cls;
    private ArrayList<IndustryConf> industryConf;
    private String perioddate;

    public static class IndustryConf {
        private float sect_val_prop;
        private String indu_sname;

        public float getSect_val_prop() {
            return sect_val_prop;
        }

        public void setSect_val_prop(float sect_val_prop) {
            this.sect_val_prop = sect_val_prop;
        }

        public String getIndu_sname() {
            return indu_sname;
        }

        public void setIndu_sname(String indu_sname) {
            this.indu_sname = indu_sname;
        }
    }

    public String getRpt_cls() {
        return rpt_cls;
    }

    public void setRpt_cls(String rpt_cls) {
        this.rpt_cls = rpt_cls;
    }

    public ArrayList<IndustryConf> getIndustryConf() {
        return industryConf;
    }

    public void setIndustryConf(ArrayList<IndustryConf> industryConf) {
        this.industryConf = industryConf;
    }

    public String getPerioddate() {
        return perioddate;
    }

    public void setPerioddate(String perioddate) {
        this.perioddate = perioddate;
    }
}
