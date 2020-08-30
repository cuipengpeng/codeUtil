package com.test.xcamera.bean;

import com.framwork.base.BaseResponse;

import java.io.Serializable;
import java.util.List;

/**
 * Created by smz on 2019/11/6.
 */

public class UploadBean extends BaseResponse {
    private List<UploadBeanDetail> data;

    public List<UploadBeanDetail> getData() {
        return data;
    }

    public void setData(List<UploadBeanDetail> data) {
        this.data = data;
    }

    public static class UploadBeanDetail implements Serializable {
        /**
         * name : 封面.jpg
         * fileId : 1
         */

        private String name;
        private int fileId;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getFileId() {
            return fileId;
        }

        public void setFileId(int fileId) {
            this.fileId = fileId;
        }
    }


}
