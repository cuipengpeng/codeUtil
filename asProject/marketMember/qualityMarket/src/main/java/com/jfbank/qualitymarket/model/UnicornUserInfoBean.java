package com.jfbank.qualitymarket.model;

/**
 * 功能：客服对象<br>
 * 作者：赵海<br>
 * 时间： 2017/4/6 0006<br>.
 * 版本：1.2.0
 */

public class UnicornUserInfoBean {
 private String   key;// 数据项的名称，用于区别不同的数据。
 private int    index;// 用于排序，显示数据时数据项按index值升序排列；不设定index的数据项将排在后面；index相同或未设定的数据项将按照其在 JSON 中出现的顺序排列。
 private String   label;// 该项数据显示的名称。
 private String    value;// 该数据显示的值，类型不做限定，根据实际需要进行设定。
 private String   href;// 超链接地址。若指定该值，则该项数据将显示为超链接样式，点击后跳转到其值所指定的 URL 地址。
 private boolean   hidden;// 是否隐藏该item。目前仅对mobile和email有效。

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }
}
