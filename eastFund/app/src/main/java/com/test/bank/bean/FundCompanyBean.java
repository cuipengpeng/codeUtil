package com.test.bank.bean;

import java.util.ArrayList;

/*
* 描    述：<br>
* 作    者：崔朋朋<br>
* 时    间：2017/12/12<br>
*/
public class FundCompanyBean {

    private String fnd_org_name;
    private float mana_scale;//管理规模
    private String build_date;
    private String mana_ranking;//规模排名
    private int indi_num; //基金经理人数
    private ArrayList<Fundlist> fundlist;

    public static class Fundlist {
        private String fundtype;
        private String fundcode;
        private float oyr_yld;
        private String custrisk;
        private String fundname;

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

        public float getOyr_yld() {
            return oyr_yld;
        }

        public void setOyr_yld(float oyr_yld) {
            this.oyr_yld = oyr_yld;
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
    }

    public String getFnd_org_name() {
        return fnd_org_name;
    }

    public void setFnd_org_name(String fnd_org_name) {
        this.fnd_org_name = fnd_org_name;
    }

    public float getMana_scale() {
        return mana_scale;
    }

    public void setMana_scale(float mana_scale) {
        this.mana_scale = mana_scale;
    }

    public String getBuild_date() {
        return build_date;
    }

    public void setBuild_date(String build_date) {
        this.build_date = build_date;
    }

    public String getMana_ranking() {
        return mana_ranking;
    }

    public void setMana_ranking(String mana_ranking) {
        this.mana_ranking = mana_ranking;
    }

    public int getIndi_num() {
        return indi_num;
    }

    public void setIndi_num(int indi_num) {
        this.indi_num = indi_num;
    }

    public ArrayList<Fundlist> getFundlist() {
        return fundlist;
    }

    public void setFundlist(ArrayList<Fundlist> fundlist) {
        this.fundlist = fundlist;
    }
}
