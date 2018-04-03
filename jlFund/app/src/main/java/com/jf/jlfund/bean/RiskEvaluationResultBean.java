package com.jf.jlfund.bean;

/**
 * Created by 55 on 2018/1/8.
 */

public class RiskEvaluationResultBean {
    private String custrisk;

    public RiskEvaluationResultBean() {
    }

    public String getCustrisk() {
        return custrisk;
    }

    public void setCustrisk(String custrisk) {
        this.custrisk = custrisk;
    }

    @Override
    public String toString() {
        return "RiskEvaluationResultBean{" +
                "custrisk='" + custrisk + '\'' +
                '}';
    }
}
