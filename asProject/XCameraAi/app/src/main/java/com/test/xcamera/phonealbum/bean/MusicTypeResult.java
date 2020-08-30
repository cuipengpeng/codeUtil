package com.test.xcamera.phonealbum.bean;

import com.framwork.base.BaseResponse;

import java.io.Serializable;
import java.util.List;

public class MusicTypeResult extends BaseResponse {

    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean implements Serializable{
        /**
         * id : 1
         * name : 热门
         * description : 热门
         */

        private int id;
        private String name;
        private String description;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
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
    }
}
