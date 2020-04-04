package com.caishi.chaoge.bean;

import com.caishi.chaoge.base.BaseBean;

import java.util.ArrayList;

public class FeedbackBean extends BaseBean {

    public ArrayList<Results> result;

    public class Results {
        public String questionId;
        public String question;
        public ArrayList<String> answerList;
        public ArrayList<AnswerBean> answerBeanList;
    }
}
