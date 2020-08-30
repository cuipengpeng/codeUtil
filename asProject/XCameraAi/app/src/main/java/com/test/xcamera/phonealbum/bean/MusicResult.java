package com.test.xcamera.phonealbum.bean;

import com.framwork.base.BaseResponse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MusicResult extends BaseResponse {
    private List<MusicBean> data=new ArrayList<>();
    /**
     * pagination : {"currentPage":1,"pageSize":2,"total":19}
     */

    private PaginationBean pagination=new PaginationBean();

    public List<MusicBean> getData() {
        return data;
    }

    public void setData(List<MusicBean> data) {
        this.data = data;
    }

    public PaginationBean getPagination() {
        return pagination;
    }

    public void setPagination(PaginationBean pagination) {
        this.pagination = pagination;
    }

    public static class PaginationBean implements Serializable{
        /**
         * currentPage : 1
         * pageSize : 2
         * total : 19
         */

        private int currentPage;
        private int pageSize;
        private int total;

        public int getCurrentPage() {
            return currentPage;
        }

        public void setCurrentPage(int currentPage) {
            this.currentPage = currentPage;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }
    }
}
