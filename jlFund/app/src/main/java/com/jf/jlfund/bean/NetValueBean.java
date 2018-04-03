package com.jf.jlfund.bean;

import java.util.ArrayList;

/*
* 描    述：<br>
* 作    者：崔朋朋<br>
* 时    间：2017/12/12<br>
*/
public class NetValueBean {

    private ArrayList<Navs> navs;

    public static class Navs {
        private String tradeDate;
        private String unit_net;
        private String unit_net_chng_pct;
        private String accum_net;

        public String getTradeDate() {
            return tradeDate;
        }

        public void setTradeDate(String tradeDate) {
            this.tradeDate = tradeDate;
        }

        public String getUnit_net() {
            return unit_net;
        }

        public void setUnit_net(String unit_net) {
            this.unit_net = unit_net;
        }

        public String getUnit_net_chng_pct() {
            return unit_net_chng_pct;
        }

        public void setUnit_net_chng_pct(String unit_net_chng_pct) {
            this.unit_net_chng_pct = unit_net_chng_pct;
        }

        public String getAccum_net() {
            return accum_net;
        }

        public void setAccum_net(String accum_net) {
            this.accum_net = accum_net;
        }
    }

    public ArrayList<Navs> getNavs() {
        return navs;
    }

    public void setNavs(ArrayList<Navs> navs) {
        this.navs = navs;
    }
}
