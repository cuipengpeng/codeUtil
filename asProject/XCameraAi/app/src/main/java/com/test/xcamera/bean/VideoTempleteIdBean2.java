package com.test.xcamera.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class VideoTempleteIdBean2 implements Serializable {


    /**
     * short : {"major":[{"code":0,"templateIds":[28]},{"code":4,"templateIds":[28]}],"scene":[{"code":95,"templateIds":[28]},{"code":0,"templateIds":[28]}]}
     * long : {"major":[{"code":0,"templateIds":[28]},{"code":4,"templateIds":[28]}],"scene":[{"code":95,"templateIds":[28]},{"code":0,"templateIds":[28]}]}
     */

    @SerializedName("short")
    private ShortBean shortX;
    @SerializedName("long")
    private ShortBean longX;

    public ShortBean getShortX() {
        return shortX;
    }

    public void setShortX(ShortBean shortX) {
        this.shortX = shortX;
    }

    public ShortBean getLongX() {
        return longX;
    }

    public void setLongX(ShortBean longX) {
        this.longX = longX;
    }

    public static class ShortBean implements Serializable {
        private List<MajorBean> major;
        private List<SceneBean> scene;

        public List<MajorBean> getMajor() {
            return major;
        }

        public void setMajor(List<MajorBean> major) {
            this.major = major;
        }

        public List<SceneBean> getScene() {
            return scene;
        }

        public void setScene(List<SceneBean> scene) {
            this.scene = scene;
        }

        public static class MajorBean implements Serializable {
            /**
             * code : 0
             * templateIds : [28]
             */

            private int code;
            private List<Integer> templateIds;

            public int getCode() {
                return code;
            }

            public void setCode(int code) {
                this.code = code;
            }

            public List<Integer> getTemplateIds() {
                return templateIds;
            }

            public void setTemplateIds(List<Integer> templateIds) {
                this.templateIds = templateIds;
            }
        }

        public static class SceneBean implements Serializable {
            /**
             * code : 95
             * templateIds : [28]
             */

            private int code;
            private List<Integer> templateIds;

            public int getCode() {
                return code;
            }

            public void setCode(int code) {
                this.code = code;
            }

            public List<Integer> getTemplateIds() {
                return templateIds;
            }

            public void setTemplateIds(List<Integer> templateIds) {
                this.templateIds = templateIds;
            }
        }
    }
}
