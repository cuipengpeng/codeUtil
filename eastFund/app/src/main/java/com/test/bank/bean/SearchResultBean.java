package com.test.bank.bean;

import com.test.bank.utils.LogUtils;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 55 on 2017/12/13.
 */

public class SearchResultBean implements Serializable {
    private List<SearchResultItem> results;
    private static final long serialVersionUID = -3450064362986273896L;

    public SearchResultBean() {
    }

    public List<SearchResultItem> getResults() {
        return results;
    }

    public void setResults(List<SearchResultItem> results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return "SearchResultBean{" +
                "results=" + results +
                '}';
    }

    public static class SearchResultItem implements Serializable {
        private static final long serialVersionUID = -3450064362986273896L;
        private String fundname;
        private String fundresCode;
        private int collected;  //是否添加了自选

        public SearchResultItem() {
        }

        public String getFundname() {
            return fundname;
        }

        public void setFundname(String fundname) {
            this.fundname = fundname;
        }

        public String getFundresCode() {
            return fundresCode;
        }

        public void setFundresCode(String fundresCode) {
            this.fundresCode = fundresCode;
        }

        public int getCollected() {
            return collected;
        }

        public void setCollected(int collected) {
            this.collected = collected;
        }

        @Override
        public String toString() {
            return "SearchResultItem{" +
                    "fundname='" + fundname + '\'' +
                    ", fundresCode='" + fundresCode + '\'' +
                    ", collected=" + collected +
                    '}';
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (null == obj || getClass() != obj.getClass()) {
                return false;
            }
            SearchResultItem tmp = (SearchResultItem) obj;
            if (tmp.fundresCode != null && tmp.fundresCode.equals(this.fundresCode)) {
                LogUtils.d("============================>>>" + fundresCode);
                return true;
            }
            return false;
        }
    }
}
