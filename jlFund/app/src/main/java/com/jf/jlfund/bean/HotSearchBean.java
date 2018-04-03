package com.jf.jlfund.bean;

import java.util.List;

/**
 * Created by 55 on 2017/12/18.
 */

public class HotSearchBean {
    private List<SearchResultBean.SearchResultItem> heats;

    public HotSearchBean() {
    }

    public List<SearchResultBean.SearchResultItem> getHeats() {
        return heats;
    }

    public void setHeats(List<SearchResultBean.SearchResultItem> heats) {
        this.heats = heats;
    }

    @Override
    public String toString() {
        return "HotSearchBean{" +
                "heats=" + heats +
                '}';
    }
}
