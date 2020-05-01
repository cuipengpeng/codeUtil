package com.jfbank.qualitymarket.model;

/*
* 描    述：账单<br>
* 作    者：崔朋朋<br>
* 时    间：2016/12/30<br>
*/
public class BillBean {

    private String installmentAmount;//分期金额
    private String remainDays;//还款剩余天数
    private int totalPeriod;//总期数
    private String productName;//商品名称
    private String productNo;//商品ID
    private int stage;//当前期数
    private String settledDate;//结算日期
    private String id;//账单id
    private String orderId;
    private String repaymentId;//还款计划ID
    private String remark;//备注
    private String status;//扣款记录状态 1 还款中 2 扣款成功  3 扣款失败
    private String appid;//借款工单ID
    private String payTime;//还款时间
    private String repayAmt;//分期金额
    private String billIds;
    private String pageCount;   //总页数
    private String pageNo;//页数
    private boolean isCheck;//是否选中
    private int billRemainDaysStatus;//		账单逾期或剩余状态		逾期时：  0// 还款剩余天数为0时：   1  还款剩余天数大于0时：2
    private float principalAmount;//	BigDecimal	本金
    private float interestAmount;//		BigDecimal	利息
    private float overdueAmount;//		BigDecimal	逾期罚息金额
    private float violateamtAmount;//		BigDecimal	违约金
    private float totalAmount;//		BigDecimal	总金额		总金额为分期金额+逾期罚息（即本金+利息+逾期罚息）
    private int firstPosition = 0;//第一笔位置
    private boolean checkEnable = false;//是否可选
    private boolean checkAll = false;//是否全选
    private int repayStatusing;

    public int getRepayStatusing() {
        return repayStatusing;
    }

    public void setRepayStatusing(int repayStatusing) {
        this.repayStatusing = repayStatusing;
    }

    public String getBillIds() {
        return billIds;
    }

    public void setBillIds(String billIds) {
        this.billIds = billIds;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public boolean isCheckAll() {
        return checkAll;
    }

    public void setCheckAll(boolean checkAll) {
        this.checkAll = checkAll;
    }

    public boolean isCheckEnable() {
        return checkEnable;
    }

    public void setCheckEnable(boolean checkEnable) {
        this.checkEnable = checkEnable;
    }

    public String getProductNo() {
        return productNo;
    }

    public void setProductNo(String productNo) {
        this.productNo = productNo;
    }

    public int getFirstPosition() {
        return firstPosition;
    }

    public void setFirstPosition(int firstPosition) {
        this.firstPosition = firstPosition;
    }

    public int getBillRemainDaysStatus() {
        return billRemainDaysStatus;
    }

    public void setBillRemainDaysStatus(int billRemainDaysStatus) {
        this.billRemainDaysStatus = billRemainDaysStatus;
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

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public String getRepaymentId() {
        return repaymentId;
    }

    public void setRepaymentId(String repaymentId) {
        this.repaymentId = repaymentId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getPayTime() {
        return payTime;
    }

    public void setPayTime(String payTime) {
        this.payTime = payTime;
    }

    public String getRepayAmt() {
        return repayAmt;
    }

    public void setRepayAmt(String repayAmt) {
        this.repayAmt = repayAmt;
    }


    public String getInstallmentAmount() {
        return installmentAmount;
    }

    public void setInstallmentAmount(String installmentAmount) {
        this.installmentAmount = installmentAmount;
    }

    public String getRemainDays() {
        return remainDays;
    }

    public void setRemainDays(String remainDays) {
        this.remainDays = remainDays;
    }

    public int getTotalPeriod() {
        return totalPeriod;
    }

    public void setTotalPeriod(int totalPeriod) {
        this.totalPeriod = totalPeriod;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getStage() {
        return stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }

    public String getSettledDate() {
        return settledDate;
    }

    public void setSettledDate(String settledDate) {
        this.settledDate = settledDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPageCount() {
        return pageCount;
    }

    public void setPageCount(String pageCount) {
        this.pageCount = pageCount;
    }

    public String getPageNo() {
        return pageNo;
    }

    public void setPageNo(String pageNo) {
        this.pageNo = pageNo;
    }


}
