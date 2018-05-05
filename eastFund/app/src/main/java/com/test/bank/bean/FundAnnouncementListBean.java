package com.test.bank.bean;

import com.test.bank.base.BaseBean;

import java.util.ArrayList;

/*
* 描    述：<br>
* 作    者：崔朋朋<br>
* 时    间：2017/12/27<br>
*/
public class FundAnnouncementListBean extends BaseBean {

    private ArrayList<NoticeList> noticeList;

    public static class NoticeList {
        private String is_acce;
        private String title;
        private String declaredate;
        private int is_content;
        private String h5_url;
        private String disc_id;

        public String getIs_acce() {
            return is_acce;
        }

        public void setIs_acce(String is_acce) {
            this.is_acce = is_acce;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDeclaredate() {
            return declaredate;
        }

        public void setDeclaredate(String declaredate) {
            this.declaredate = declaredate;
        }

        public int getIs_content() {
            return is_content;
        }

        public void setIs_content(int is_content) {
            this.is_content = is_content;
        }

        public String getH5_url() {
            return h5_url;
        }

        public void setH5_url(String h5_url) {
            this.h5_url = h5_url;
        }

        public String getDisc_id() {
            return disc_id;
        }

        public void setDisc_id(String disc_id) {
            this.disc_id = disc_id;
        }
    }

    public ArrayList<NoticeList> getNoticeList() {
        return noticeList;
    }

    public void setNoticeList(ArrayList<NoticeList> noticeList) {
        this.noticeList = noticeList;
    }
}
