package com.jfbank.qualitymarket.mvp;

import com.jfbank.qualitymarket.base.BaseModel;
import com.jfbank.qualitymarket.base.BasePresenter;
import com.jfbank.qualitymarket.base.BaseView;

/**
* Created by Herri on 2017/07/19
*/
public class CameraPhotoMVP {



   public  interface View extends BaseView {
      
    }

  public  static class Model extends BaseModel {
       
 }
   public static class Presenter extends BasePresenter<Model, View> {

          
}}