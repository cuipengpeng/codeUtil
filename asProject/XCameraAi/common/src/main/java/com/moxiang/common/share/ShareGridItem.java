package com.moxiang.common.share;

/**
 * Created by admin on 2019/11/16.
 */

public class ShareGridItem {
    private String label;
    private int logoRes;

    public ShareGridItem(String label, int logoRes) {
        this.label = label;
        this.logoRes = logoRes;
    }

    public String getLabel() {
        return label;
    }

    public int getLogoRes() {
        return logoRes;
    }

    @Override
    public String toString() {
        return "ShareGridItem{" +
                "label='" + label + '\'' +
                ", logoRes=" + logoRes +
                '}';
    }

}
