package com.jfbank.qualitymarket.mvp;

import com.jfbank.qualitymarket.base.BaseModel;
import com.jfbank.qualitymarket.base.BasePresenter;
import com.jfbank.qualitymarket.base.BaseView;

/**
 * 退换货列表接口
 * Created by Herri on 2017/04/19
 */
public class AfterSalesMVP {


    public interface View extends BaseView {

    }

    public static class Model extends BaseModel {

    }

    public static class Presenter extends BasePresenter<Model, View> {


    }
}