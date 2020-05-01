package com.jfbank.qualitymarket.model;

//联系人

public class ContactBean {
    private String contactName; // 联系人姓名
    private String company; // 公司或组织
    private String mobilePhone;//手机号
    private String homePhone;
    private String companyPhone;
    private String email;
    private String createTime;

    public ContactBean() {
    }

    public ContactBean(String contactName, String company, String mobilePhone,
                       String homePhone, String companyPhone, String email, String createTime) {
        super();
        this.contactName = contactName;
        this.company = company;
        if (mobilePhone != null && mobilePhone.contains("+86")) {
            this.mobilePhone = mobilePhone.replace("+86", "");
        } else {
            this.mobilePhone = mobilePhone;
        }
        this.homePhone = homePhone;
        this.companyPhone = companyPhone;
        this.email = email;
        this.createTime = createTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getHomePhone() {
        return homePhone;
    }

    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }

    public String getCompanyPhone() {
        return companyPhone;
    }

    public void setCompanyPhone(String companyPhone) {
        this.companyPhone = companyPhone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "ContactBean [contactName=" + contactName + ", company="
                + company + ", mobilePhone=" + mobilePhone + ", homePhone="
                + homePhone + ", companyPhone=" + companyPhone + ", email="
                + email + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((company == null) ? 0 : company.hashCode());
        result = prime * result
                + ((companyPhone == null) ? 0 : companyPhone.hashCode());
        result = prime * result
                + ((contactName == null) ? 0 : contactName.hashCode());
        result = prime * result + ((email == null) ? 0 : email.hashCode());
        result = prime * result
                + ((homePhone == null) ? 0 : homePhone.hashCode());
        result = prime * result
                + ((mobilePhone == null) ? 0 : mobilePhone.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ContactBean other = (ContactBean) obj;
        if (company == null) {
            if (other.company != null)
                return false;
        } else if (!company.equals(other.company))
            return false;
        if (companyPhone == null) {
            if (other.companyPhone != null)
                return false;
        } else if (!companyPhone.equals(other.companyPhone))
            return false;
        if (contactName == null) {
            if (other.contactName != null)
                return false;
        } else if (!contactName.equals(other.contactName))
            return false;
        if (email == null) {
            if (other.email != null)
                return false;
        } else if (!email.equals(other.email))
            return false;
        if (homePhone == null) {
            if (other.homePhone != null)
                return false;
        } else if (!homePhone.equals(other.homePhone))
            return false;
        if (mobilePhone == null) {
            if (other.mobilePhone != null)
                return false;
        } else if (!mobilePhone.equals(other.mobilePhone))
            return false;
        return true;
    }
}
