package com.jf.jlfund.bean;

import java.io.Serializable;

/**
 * Created by 55 on 2018/3/13.
 */

public class TestResultBean implements Serializable {

    private static final long serialVersionUID = -3450064362986273896L;

    private String fundcode;
    private String fundname;
    private String institutionGrade;        //机构评级
    private String institutionHold;         //机构持仓
    private String stability;               //稳定性
    private String tag;                     //标签，以”,”分割
    private String profit;                  //三年年化收益
    private String withstandRisk;           //抗风险能力

    public TestResultBean() {
    }

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

    public String getInstitutionGrade() {
        return institutionGrade;
    }

    public void setInstitutionGrade(String institutionGrade) {
        this.institutionGrade = institutionGrade;
    }

    public String getInstitutionHold() {
        return institutionHold;
    }

    public void setInstitutionHold(String institutionHold) {
        this.institutionHold = institutionHold;
    }

    public String getStability() {
        return stability;
    }

    public void setStability(String stability) {
        this.stability = stability;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getProfit() {
        return profit;
    }

    public void setProfit(String profit) {
        this.profit = profit;
    }

    public String getWithstandRisk() {
        return withstandRisk;
    }

    public void setWithstandRisk(String withstandRisk) {
        this.withstandRisk = withstandRisk;
    }

    @Override
    public String toString() {
        return "TestResultBean{" +
                "fundcode='" + fundcode + '\'' +
                ", fundname='" + fundname + '\'' +
                ", institutionGrade='" + institutionGrade + '\'' +
                ", institutionHold='" + institutionHold + '\'' +
                ", stability='" + stability + '\'' +
                ", tag='" + tag + '\'' +
                ", profit='" + profit + '\'' +
                ", withstandRisk='" + withstandRisk + '\'' +
                '}';
    }
}
