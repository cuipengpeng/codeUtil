package com.jf.jlfund.bean;

import java.util.List;

/**
 * 赚钱页面-列表
 * Created by 55 on 2017/12/14.
 */

public class MakeMoneyListBean {
    private Integer changeCount;    //抓机会条数
    private List<GraspChanceBean> navCatchMap;  //抓机会列表
    private Integer nbCount;        //买好基条数
    private List<BuyGoodFundBean> navNbMap; //买好基列表
    private Integer findNavCount;   //追热点条数
    private List<ChaseHotBean> findNav;  //追热点列表

    public MakeMoneyListBean() {
    }

    public Integer getChangeCount() {
        return changeCount;
    }

    public void setChangeCount(Integer changeCount) {
        this.changeCount = changeCount;
    }

    public List<BuyGoodFundBean> getNavNbMap() {
        return navNbMap;
    }

    public void setNavNbMap(List<BuyGoodFundBean> navNbMap) {
        this.navNbMap = navNbMap;
    }

    public List<GraspChanceBean> getNavCatchMap() {
        return navCatchMap;
    }

    public void setNavCatchMap(List<GraspChanceBean> navCatchMap) {
        this.navCatchMap = navCatchMap;
    }

    public List<ChaseHotBean> getFindNav() {
        return findNav;
    }

    public void setFindNav(List<ChaseHotBean> findNav) {
        this.findNav = findNav;
    }

    public Integer getFindNavCount() {
        return findNavCount;
    }

    public void setFindNavCount(Integer findNavCount) {
        this.findNavCount = findNavCount;
    }

    public Integer getNbCount() {
        return nbCount;
    }

    public void setNbCount(Integer nbCount) {
        this.nbCount = nbCount;
    }

    @Override
    public String toString() {
        return "MakeMoneyListBean{" +
                "changeCount=" + changeCount +
                ", navNbMap=" + navNbMap +
                ", navCatchMap=" + navCatchMap +
                ", findNav=" + findNav +
                ", findNavCount=" + findNavCount +
                ", nbCount=" + nbCount +
                '}';
    }
}
