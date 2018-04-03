package com.jf.jlfund.bean;

import java.util.ArrayList;

/*
* 描    述：<br>
* 作    者：崔朋朋<br>
* 时    间：2018/1/8<br>
*/
public class WanfenIncomeBean {

    private float summary;
    private ArrayList<List> list;

    public static class List {
        private String tradeDate;
        private float number;

        public String getTradeDate() {
            return tradeDate;
        }

        public void setTradeDate(String tradeDate) {
            this.tradeDate = tradeDate;
        }

        public float getNumber() {
            return number;
        }

        public void setNumber(float number) {
            this.number = number;
        }
    }

    public float getSummary() {
        return summary;
    }

    public void setSummary(float summary) {
        this.summary = summary;
    }

    public ArrayList<List> getList() {
        return list;
    }

    public void setList(ArrayList<List> list) {
        this.list = list;
    }

}
