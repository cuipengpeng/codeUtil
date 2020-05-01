package com.jfbank.qualitymarket.model;

/**
 * 功能：<br>
 * 作者：赵海<br>
 * 时间： 2016/12/30 0030<br>.
 * 版本：1.2.0
 */

public class AgingBean {
    private String fenqiAmount;//	分期金额
    private long stage;//    	当前期数
    private long billIds;//  	账单ID
    private String remainDays;//	剩余日期		如果该账单已还则返回已还
    private boolean isCheck;//是否选中
    private int remainDaysStatus;//		账单逾期或剩余状态		逾期时：  0// 还款剩余天数为0时：   1  还款剩余天数大于0时：2
    private float principalAmount;//	BigDecimal	本金
    private float interestAmount;//		BigDecimal	利息
    private float overdueAmount;//		BigDecimal	逾期罚息金额
    private float violateamtAmount;//		BigDecimal	违约金
    private float totalAmount;//		BigDecimal	总金额		总金额为分期金额+逾期罚息（即本金+利息+逾期罚息）
    private boolean checkEnable = false;//是否可选
    private boolean checkAll = false;//是否全选
    private int repayStatusing;

    public int getRepayStatusing() {
        return repayStatusing;
    }

    public void setRepayStatusing(int repayStatusing) {
        this.repayStatusing = repayStatusing;
    }

    public int getRemainDaysStatus() {
        return remainDaysStatus;
    }

    public void setRemainDaysStatus(int remainDaysStatus) {
        this.remainDaysStatus = remainDaysStatus;
    }

    public float getPrincipalAmount() {
        return principalAmount;
    }

    public void setPrincipalAmount(float principalAmount) {
        this.principalAmount = principalAmount;
    }

    public float getInterestAmount() {
        return interestAmount;
    }

    public void setInterestAmount(float interestAmount) {
        this.interestAmount = interestAmount;
    }

    public float getOverdueAmount() {
        return overdueAmount;
    }

    public void setOverdueAmount(float overdueAmount) {
        this.overdueAmount = overdueAmount;
    }

    public float getViolateamtAmount() {
        return violateamtAmount;
    }

    public void setViolateamtAmount(float violateamtAmount) {
        this.violateamtAmount = violateamtAmount;
    }

    public float getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(float totalAmount) {
        this.totalAmount = totalAmount;
    }

    public boolean isCheckEnable() {
        return checkEnable;
    }

    public void setCheckEnable(boolean checkEnable) {
        this.checkEnable = checkEnable;
    }

    public boolean isCheckAll() {
        return checkAll;
    }

    public void setCheckAll(boolean checkAll) {
        this.checkAll = checkAll;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public String getFenqiAmount() {
        return fenqiAmount;
    }

    public void setFenqiAmount(String fenqiAmount) {
        this.fenqiAmount = fenqiAmount;
    }

    public long getStage() {
        return stage;
    }

    public void setStage(long stage) {
        this.stage = stage;
    }

    public long getBillIds() {
        return billIds;
    }

    public void setBillIds(long billIds) {
        this.billIds = billIds;
    }

    public String getRemainDays() {
        return remainDays;
    }

    public void setRemainDays(String remainDays) {
        this.remainDays = remainDays;
    }

}
