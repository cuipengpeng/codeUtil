package com.jf.jlfund.bean;

import com.jf.jlfund.base.BaseBean;

import java.io.Serializable;
import java.util.ArrayList;

/*
* 描    述：<br>
* 作    者：崔朋朋<br>
* 时    间：2017/12/12<br>
*/
public class FundArchivesWithAnnouncementBean extends BaseBean{
    private int id;
    private String fnd_org_name;
    private int fundstate;
    private ArrayList<Indi_name_list> indi_name_list;
    private String build_date;
    private int declarestate; //申购状态    1-开放 2-暂停
    private ArrayList<Disc_main_list> disc_main_list;
    private String fnd_size;
    private int withdrawstate;//赎回状态   1开放  2暂停

    public static class Indi_name_list implements Serializable{
        private int leave_rsn;
        private String indi_name;
        private int indi_id;

        public int getLeave_rsn() {
            return leave_rsn;
        }

        public void setLeave_rsn(int leave_rsn) {
            this.leave_rsn = leave_rsn;
        }

        public String getIndi_name() {
            return indi_name;
        }

        public void setIndi_name(String indi_name) {
            this.indi_name = indi_name;
        }

        public int getIndi_id() {
            return indi_id;
        }

        public void setIndi_id(int indi_id) {
            this.indi_id = indi_id;
        }
    }

    public static class Disc_main_list implements Serializable{
        private String id;
        private String is_acce;
        private String title;
        private String declaredate;
        private int is_content;
        private String h5_url;


        public String getH5_url() {
            return h5_url;
        }

        public void setH5_url(String h5_url) {
            this.h5_url = h5_url;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

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
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFnd_org_name() {
        return fnd_org_name;
    }

    public void setFnd_org_name(String fnd_org_name) {
        this.fnd_org_name = fnd_org_name;
    }

    public int getFundstate() {
        return fundstate;
    }

    public void setFundstate(int fundstate) {
        this.fundstate = fundstate;
    }

    public ArrayList<Indi_name_list> getIndi_name_list() {
        return indi_name_list;
    }

    public void setIndi_name_list(ArrayList<Indi_name_list> indi_name_list) {
        this.indi_name_list = indi_name_list;
    }

    public String getBuild_date() {
        return build_date;
    }

    public void setBuild_date(String build_date) {
        this.build_date = build_date;
    }

    public int getDeclarestate() {
        return declarestate;
    }

    public void setDeclarestate(int declarestate) {
        this.declarestate = declarestate;
    }

    public ArrayList<Disc_main_list> getDisc_main_list() {
        return disc_main_list;
    }

    public void setDisc_main_list(ArrayList<Disc_main_list> disc_main_list) {
        this.disc_main_list = disc_main_list;
    }

    public String getFnd_size() {
        return fnd_size;
    }

    public void setFnd_size(String fnd_size) {
        this.fnd_size = fnd_size;
    }

    public int getWithdrawstate() {
        return withdrawstate;
    }

    public void setWithdrawstate(int withdrawstate) {
        this.withdrawstate = withdrawstate;
    }
}
