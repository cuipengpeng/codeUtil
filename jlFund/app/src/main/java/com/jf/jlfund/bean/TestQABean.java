package com.jf.jlfund.bean;

import android.text.TextUtils;

/**
 * Created by 55 on 2018/3/12.
 * 测一测问题和答案的实体类
 */

public class TestQABean {
    private int qaNo;   //第几题
    private String question;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private int answer = -1; //选择的答案：1 A; 2 B;3 C;4 D;

    public TestQABean() {
    }

    public TestQABean(int qaNo) {
        this.qaNo = qaNo;
    }

    public int getQaNo() {
        return qaNo;
    }

    public void setQaNo(int qaNo) {
        this.qaNo = qaNo;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getOptionA() {
        return optionA;
    }

    public void setOptionA(String optionA) {
        this.optionA = "A、" + optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public void setOptionB(String optionB) {
        this.optionB = "B、" + optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public void setOptionC(String optionC) {
        this.optionC = "C、" + optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public void setOptionD(String optionD) {
        if (!TextUtils.isEmpty(optionD)) {
            this.optionD = "D、" + optionD;
        }
    }

    public int getAnswer() {
        return answer;
    }

    public void setAnswer(int answer) {
        this.answer = answer;
    }

    @Override
    public String toString() {
        return "TestQABean{" +
                "qaNo=" + qaNo +
                ", question='" + question + '\'' +
                ", optionA='" + optionA + '\'' +
                ", optionB='" + optionB + '\'' +
                ", optionC='" + optionC + '\'' +
                ", optionD='" + optionD + '\'' +
                ", answer=" + answer +
                '}';
    }
}
