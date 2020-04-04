package com.caishi.chaoge.bean;

import java.util.ArrayList;

public class CityBean {

    public ArrayList<Provinces> provinces;

    public class Provinces {
        public String name;
        public ArrayList<String> child;


    }
}
