package com.test.bank.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 55 on 2018/1/5.
 */

public class RiskEvaluationQuestionBean implements Serializable{
    private List<RiskQuestionSubject> subjects;

    public RiskEvaluationQuestionBean() {
    }

    public List<RiskQuestionSubject> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<RiskQuestionSubject> subjects) {
        this.subjects = subjects;
    }

    @Override
    public String toString() {
        return "RiskEvaluationQuestionBean{" +
                "subjects=" + subjects +
                '}';
    }

    public static class RiskQuestionSubject implements Serializable{
        private String qtitle;
        private String integrityflag;
        private String qanswer;
        private String qno;
        private String myanswer;
        private List<RiskQuestionItem> qitem;

        public RiskQuestionSubject() {
        }

        private String address;
        private String province;
        private String city;
        private String postcode;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getPostcode() {
            return postcode;
        }

        public void setPostcode(String postcode) {
            this.postcode = postcode;
        }

        public String getQtitle() {
            return qtitle;
        }

        public void setQtitle(String qtitle) {
            this.qtitle = qtitle;
        }

        public String getIntegrityflag() {
            return integrityflag;
        }

        public void setIntegrityflag(String integrityflag) {
            this.integrityflag = integrityflag;
        }

        public String getQanswer() {
            return qanswer;
        }

        public void setQanswer(String qanswer) {
            this.qanswer = qanswer;
        }

        public String getQno() {
            return qno;
        }

        public void setQno(String qno) {
            this.qno = qno;
        }

        public String getMyanswer() {
            return myanswer;
        }

        public void setMyanswer(String myanswer) {
            this.myanswer = myanswer;
        }

        public List<RiskQuestionItem> getQitem() {
            return qitem;
        }

        public void setQitem(List<RiskQuestionItem> qitem) {
            this.qitem = qitem;
        }

        @Override
        public String toString() {
            return "RiskQuestionSubject{" +
                    "qtitle='" + qtitle + '\'' +
                    ", integrityflag='" + integrityflag + '\'' +
                    ", qanswer='" + qanswer + '\'' +
                    ", qno='" + qno + '\'' +
                    ", myanswer='" + myanswer + '\'' +
                    ", qitem=" + qitem +
                    '}';
        }

        public static class RiskQuestionItem implements Serializable{
            private String itemtitle;
            private String itemvalue;
            private boolean selected;

            public RiskQuestionItem() {
            }

            public String getItemtitle() {
                return itemtitle;
            }

            public void setItemtitle(String itemtitle) {
                this.itemtitle = itemtitle;
            }

            public String getItemvalue() {
                return itemvalue;
            }

            public void setItemvalue(String itemvalue) {
                this.itemvalue = itemvalue;
            }

            public boolean isSelected() {
                return selected;
            }

            public void setSelected(boolean selected) {
                this.selected = selected;
            }

            @Override
            public String toString() {
                return "RiskQuestionItem{" +
                        "itemtitle='" + itemtitle + '\'' +
                        ", itemvalue='" + itemvalue + '\'' +
                        ", selected=" + selected +
                        '}';
            }
        }

    }
}
