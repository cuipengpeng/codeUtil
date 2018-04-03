package com.jfbank.qualitymarket.mvp;

import com.jfbank.qualitymarket.base.BaseModel;
import com.jfbank.qualitymarket.base.BasePresenter;
import com.jfbank.qualitymarket.base.BaseView;

/**
* Created by Herri on 2017/04/20
*/
public class ProgressCheckForGoodsRejectedMVP {



   public  interface View extends BaseView {
//      void updateGoodsRejectedProgress();
    }

  public  static class Model extends BaseModel {
       
 }
   public static class Presenter extends BasePresenter<Model, View> {

       public void queryGoodsRejectedProgress(){
//           mModel.requestNetData(HttpRequest.ACTIVITYPRODUCTSHOW, RequestParmsUtils.getParamsInstance().bulidParams(), new AsyncResponseCallBack(this) {
//               @Override
//               public void onResult(String responseStr) {
//                   Response<List<MsProductBean>> response = JSON.parseObject(responseStr, new TypeReference<Response<List<MsProductBean>>>() {
//                   });
//                   if (response.getStatus() == ConstantsUtil.RESPONSE_SUCCEED) {
//                       mView.updateMSView(response);
//                   } else {
//                       mView.updateMSView(null);
//                   }
//               }
//           });
       }
          
}}