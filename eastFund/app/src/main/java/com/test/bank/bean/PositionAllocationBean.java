package com.test.bank.bean;

import java.util.ArrayList;

/*
* 描    述：<br>
* 作    者：崔朋朋<br>
* 时    间：2017/12/12<br>
*/
public class PositionAllocationBean {

    private ArrayList<Scalelist> scalelist;
    private String rpt_cls;
    private String perioddate;

    public static class StockAssetConf {
        private float hld_stk_val;
        private float stock_pct;
        private String stock_name;

        public float getHld_stk_val() {
            return hld_stk_val;
        }

        public void setHld_stk_val(float hld_stk_val) {
            this.hld_stk_val = hld_stk_val;
        }

        public float getStock_pct() {
            return stock_pct;
        }

        public void setStock_pct(float stock_pct) {
            this.stock_pct = stock_pct;
        }

        public String getStock_name() {
            return stock_name;
        }

        public void setStock_name(String stock_name) {
            this.stock_name = stock_name;
        }
    }

    public static class BndAssetConf {
        private String bondsname;
        private float tot_val_prop;

        public String getBondsname() {
            return bondsname;
        }

        public void setBondsname(String bondsname) {
            this.bondsname = bondsname;
        }

        public float getTot_val_prop() {
            return tot_val_prop;
        }

        public void setTot_val_prop(float tot_val_prop) {
            this.tot_val_prop = tot_val_prop;
        }
    }

    public static class Scalelist {
        private String balance_cla;
        private ArrayList<StockAssetConf> stockAssetConf;
        private ArrayList<BndAssetConf> bndAssetConf;
        private float val_pct;

        public ArrayList<BndAssetConf> getBndAssetConf() {
            return bndAssetConf;
        }

        public void setBndAssetConf(ArrayList<BndAssetConf> bndAssetConf) {
            this.bndAssetConf = bndAssetConf;
        }

        public String getBalance_cla() {
            return balance_cla;
        }

        public void setBalance_cla(String balance_cla) {
            this.balance_cla = balance_cla;
        }

        public ArrayList<StockAssetConf> getStockAssetConf() {
            return stockAssetConf;
        }

        public void setStockAssetConf(ArrayList<StockAssetConf> stockAssetConf) {
            this.stockAssetConf = stockAssetConf;
        }

        public float getVal_pct() {
            return val_pct;
        }

        public void setVal_pct(float val_pct) {
            this.val_pct = val_pct;
        }
    }

    public ArrayList<Scalelist> getScalelist() {
        return scalelist;
    }

    public void setScalelist(ArrayList<Scalelist> scalelist) {
        this.scalelist = scalelist;
    }

    public String getRpt_cls() {
        return rpt_cls;
    }

    public void setRpt_cls(String rpt_cls) {
        this.rpt_cls = rpt_cls;
    }

    public String getPerioddate() {
        return perioddate;
    }

    public void setPerioddate(String perioddate) {
        this.perioddate = perioddate;
    }

}
