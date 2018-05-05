package com.test.bank.bean;

import java.util.ArrayList;

/*
* 描    述：<br>
* 作    者：崔朋朋<br>
*/
public class SevenDayYieldTrendChartBean {


    private String growthrate;
    private ArrayList<Growthratelist> growthratelist;
    private String growth;

    public static class Growthratelist {
        private String growthrate;
        private String year_yld;
        private String tradedate;

        public String getGrowthrate() {
            return growthrate;
        }

        public void setGrowthrate(String growthrate) {
            this.growthrate = growthrate;
        }

        public String getYear_yld() {
            return year_yld;
        }

        public void setYear_yld(String year_yld) {
            this.year_yld = year_yld;
        }

        public String getTradedate() {
            return tradedate;
        }

        public void setTradedate(String tradedate) {
            this.tradedate = tradedate;
        }
    }

    public String getGrowthrate() {
        return growthrate;
    }

    public void setGrowthrate(String growthrate) {
        this.growthrate = growthrate;
    }

    public ArrayList<Growthratelist> getGrowthratelist() {
        return growthratelist;
    }

    public void setGrowthratelist(ArrayList<Growthratelist> growthratelist) {
        this.growthratelist = growthratelist;
    }

    public String getGrowth() {
        return growth;
    }

    public void setGrowth(String growth) {
        this.growth = growth;
    }


}
