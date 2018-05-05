package com.test.bank.bean;

import java.util.List;

/**
 * Created by 55 on 2018/1/9.
 */

public class AddressBean {
    private Integer id;
    private String province;
    private List<City> citys;

    public AddressBean() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public List<City> getCitys() {
        return citys;
    }

    public void setCitys(List<City> citys) {
        this.citys = citys;
    }

    @Override
    public String toString() {
        return "AddressBean{" +
                "id=" + id +
                ", province='" + province + '\'' +
                ", citys=" + citys +
                '}';
    }

    public static class City{
        private String city;

        public City() {
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        @Override
        public String toString() {
            return "City{" +
                    "city='" + city + '\'' +
                    '}';
        }
    }
}
