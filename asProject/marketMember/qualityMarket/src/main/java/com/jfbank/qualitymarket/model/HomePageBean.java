package com.jfbank.qualitymarket.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能：<br>
 * 作者：RaykerTeam
 * 时间： 2016/11/16 0016<br>.
 * 版本：1.0.0
 */

public class HomePageBean implements Parcelable {
    List<BannerHomeBean> bannerhome;//   	首页banner列表	N
    List<NavigationHomeBean> navigation;//  	首页八个图标对象列表	N
    List<ProductFloorBean> productfloor;//	每个对象包含两个数组对象 banner跟product和对象参数	N
    List<BannerHomeBean> Bannerhome2;//   	首页banner列表	N
    List<QualityNewsBean> news;
    int floorcount;
    int floor;

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public List<QualityNewsBean> getNews() {
        return news;
    }

    public void setNews(List<QualityNewsBean> news) {
        this.news = news;
    }

    public int getFloorcount() {
        return floorcount;
    }

    public void setFloorcount(int floorcount) {
        this.floorcount = floorcount;
    }

    public List<BannerHomeBean> getBannerhome2() {
        return Bannerhome2;
    }

    public void setBannerhome2(List<BannerHomeBean> bannerhome2) {
        Bannerhome2 = bannerhome2;
    }

    public List<BannerHomeBean> getBannerhome() {
        return bannerhome;
    }

    public void setBannerhome(List<BannerHomeBean> bannerhome) {
        this.bannerhome = bannerhome;
    }

    public List<NavigationHomeBean> getNavigation() {
        return navigation;
    }

    public void setNavigation(List<NavigationHomeBean> navigation) {
        this.navigation = navigation;
    }

    public List<ProductFloorBean> getProductfloor() {
        return productfloor;
    }

    public void setProductfloor(List<ProductFloorBean> productfloor) {
        this.productfloor = productfloor;
    }

    public HomePageBean() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.bannerhome);
        dest.writeTypedList(this.navigation);
        dest.writeTypedList(this.productfloor);
        dest.writeTypedList(this.Bannerhome2);
        dest.writeList(this.news);
        dest.writeInt(this.floorcount);
    }

    protected HomePageBean(Parcel in) {
        this.bannerhome = in.createTypedArrayList(BannerHomeBean.CREATOR);
        this.navigation = in.createTypedArrayList(NavigationHomeBean.CREATOR);
        this.productfloor = in.createTypedArrayList(ProductFloorBean.CREATOR);
        this.Bannerhome2 = in.createTypedArrayList(BannerHomeBean.CREATOR);
        this.news = new ArrayList<QualityNewsBean>();
        in.readList(this.news, QualityNewsBean.class.getClassLoader());
        this.floorcount = in.readInt();
    }

    public static final Creator<HomePageBean> CREATOR = new Creator<HomePageBean>() {
        @Override
        public HomePageBean createFromParcel(Parcel source) {
            return new HomePageBean(source);
        }

        @Override
        public HomePageBean[] newArray(int size) {
            return new HomePageBean[size];
        }
    };
}
