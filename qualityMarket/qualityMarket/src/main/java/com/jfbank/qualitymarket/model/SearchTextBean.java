package com.jfbank.qualitymarket.model;

import java.util.ArrayList;

/*
* 描    述：<br>
* 作    者：崔朋朋<br>
* 时    间：2017/1/12<br>
*/
public class SearchTextBean {
    private String Searchfordefault;
    private ArrayList<String> SearchHotWord;
    private ArrayList<String> data;

    public ArrayList<String> getData() {
        return data;
    }

    public void setData(ArrayList<String> data) {
        this.data = data;
    }

    public String getSearchfordefault() {
        return Searchfordefault;
    }

    public void setSearchfordefault(String Searchfordefault) {
        this.Searchfordefault = Searchfordefault;
    }

    public ArrayList<String> getSearchHotWord() {
        return SearchHotWord;
    }

    public void setSearchHotWord(ArrayList<String> SearchHotWord) {
        this.SearchHotWord = SearchHotWord;
    }


}
