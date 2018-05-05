package com.test.bank.bean;

/**
 * Created by 55 on 2018/2/12.
 */

public class SuggestQuestion {
    private String question_str;
    private String qid;

    public SuggestQuestion() {
    }

    public String getQuestion_str() {
        return question_str;
    }

    public void setQuestion_str(String question_str) {
        this.question_str = question_str;
    }

    public String getQid() {
        return qid;
    }

    public void setQid(String qid) {
        this.qid = qid;
    }

    @Override
    public String toString() {
        return "SuggestQuestion{" +
                "question_str='" + question_str + '\'' +
                ", qid='" + qid + '\'' +
                '}';
    }
}
