package com.test.xcamera.bean;


import com.framwork.base.BaseResponse;

import java.io.Serializable;
import java.util.List;

/**
 * Author: mz
 * Time:  2019/9/26
 */
public class AppVersion extends BaseResponse {
    @Override
    public String toString() {
        return "AppVersion{" +
                "data=" + data +
                '}';
    }

    private List<AppVersionDetail> data;

    public List<AppVersionDetail> getData() {
        return data;
    }

    public void setData(List<AppVersionDetail> data) {
        this.data = data;
    }

    public static class AppVersionDetail implements Serializable {
        @Override
        public String toString() {
            return "AppVersionDetail{" +
                    "id=" + id +
                    ", version='" + version + '\'' +
                    ", buildVersion=" + buildVersion +
                    ", name='" + name + '\'' +
                    ", description='" + description + '\'' +
                    ", pkgSign='" + pkgSign + '\'' +
                    ", pkgSize=" + pkgSize +
                    ", releaseTime=" + releaseTime +
                    ", pkgType=" + pkgType +
                    '}';
        }

        /**
         * id : 49
         * version : 1.1.1.100002
         * buildVersion : 100002
         * name : test
         * description : test
         * pkgSign : 7ca28a5db7b6312aa24ad6380c9c5843
         * pkgSize : 593
         * releaseTime : 1569307504047
         */

        private int id;
        private String version;
        private int buildVersion;
        private String name;
        private String description;
        private String pkgSign;
        private int pkgSize;
        private long releaseTime;
        private int pkgType;

        public int getPkgType() {
            return pkgType;
        }

        public void setPkgType(int pkgType) {
            this.pkgType = pkgType;
        }


        public String getFeature() {
            return feature;
        }

        public void setFeature(String feature) {
            this.feature = feature;
        }

        private String feature;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public int getBuildVersion() {
            return buildVersion;
        }

        public void setBuildVersion(int buildVersion) {
            this.buildVersion = buildVersion;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getPkgSign() {
            return pkgSign;
        }

        public void setPkgSign(String pkgSign) {
            this.pkgSign = pkgSign;
        }

        public int getPkgSize() {
            return pkgSize;
        }

        public void setPkgSize(int pkgSize) {
            this.pkgSize = pkgSize;
        }

        public long getReleaseTime() {
            return releaseTime;
        }

        public void setReleaseTime(long releaseTime) {
            this.releaseTime = releaseTime;
        }
    }
}
