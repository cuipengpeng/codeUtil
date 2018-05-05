package com.test.bank.bean;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by 55 on 2018/2/9.
 * 智能客服问答Bean
 */

public class SmartQABean extends DataSupport implements Serializable {

    private static final long serialVersionUID = -3450064362986273896L;

    private String answer;
    private boolean getAnswer;
    private String mobile;
    private String question;
    private String suggestQuestion;
    private Long time;

    public int viewType;

    public boolean isShowDate;

    public SmartQABean() {
    }


    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public boolean isGetAnswer() {
        return getAnswer;
    }

    public void setGetAnswer(boolean getAnswer) {
        this.getAnswer = getAnswer;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getSuggestQuestion() {
        return suggestQuestion;
    }

    public void setSuggestQuestion(String suggestQuestion) {
        this.suggestQuestion = suggestQuestion;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "SmartQABean{" +
                "answer='" + answer + '\'' +
                ", getAnswer=" + getAnswer +
                ", mobile='" + mobile + '\'' +
                ", question='" + question + '\'' +
                ", suggestQuestion='" + suggestQuestion + '\'' +
                ", time=" + time +
                '}';
    }
}
