package com.test.xcamera.utils.glide;

import java.util.Objects;

public class OWLImageFid {
    private long time;
    private String mUrl;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OWLImageFid imageFid = (OWLImageFid) o;
        return time == imageFid.time &&
                Objects.equals(mUrl, imageFid.mUrl);
    }

    @Override
    public int hashCode() {

        return Objects.hash(time, mUrl);
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String gtmUrl() {
        return mUrl;
    }

    public void setUrl(String mUrl) {
        this.mUrl = mUrl;
    }
}
