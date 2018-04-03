package com.jf.jlfund.bean;

/**
 * Created by 55 on 2018/3/15.
 */

public class VersionUpdateBean {
    private String content;
    private boolean forceUpdate;
    private String url;
    private String version;
    private long apkSize = -1L;

    public VersionUpdateBean() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isForceUpdate() {
        return forceUpdate;
    }

    public void setForceUpdate(boolean forceUpdate) {
        this.forceUpdate = forceUpdate;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public long getApkSize() {
        return apkSize;
    }

    public void setApkSize(long apkSize) {
        this.apkSize = apkSize;
    }

    @Override
    public String toString() {
        return "VersionUpdateBean{" +
                "content='" + content + '\'' +
                ", forceUpdate=" + forceUpdate +
                ", url='" + url + '\'' +
                ", version='" + version + '\'' +
                ", apkSize='" + apkSize + '\'' +
                '}';
    }
}
