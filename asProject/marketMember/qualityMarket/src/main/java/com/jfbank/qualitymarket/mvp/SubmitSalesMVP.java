package com.jfbank.qualitymarket.mvp;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jfbank.qualitymarket.activity.ApplyAfterSaleSuccessActivity;
import com.jfbank.qualitymarket.adapter.TakePicAdapter;
import com.jfbank.qualitymarket.base.BaseModel;
import com.jfbank.qualitymarket.base.BasePresenter;
import com.jfbank.qualitymarket.base.BaseView;
import com.jfbank.qualitymarket.callback.AsyncResponseCallBack;
import com.jfbank.qualitymarket.model.SalesProgressBean;
import com.jfbank.qualitymarket.net.HttpRequest;
import com.jfbank.qualitymarket.net.RequestParmsUtils;
import com.jfbank.qualitymarket.net.Response;
import com.jfbank.qualitymarket.util.ConstantsUtil;
import com.jfbank.qualitymarket.util.JumpUtil;
import com.jfbank.qualitymarket.util.StringUtil;

import org.apache.http.Header;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Herri on 2017/04/24
 */
public class SubmitSalesMVP {


    public interface View extends BaseView {

    }

    public static class Model extends BaseModel {

    }

    public static class Presenter extends BasePresenter<Model, View> {
        /**
         * 调用接口调用接口上传图片
         *
         * @param requestParmsUtils 接口参数对象
         * @param filePaths         文件路劲
         * @param position          上传第几张
         */
        public void uploadIamgePic(final RequestParmsUtils requestParmsUtils, final String[] filePaths, final int position) {
            if (filePaths[position].contains("http")) {
                setUploadPicUrl(requestParmsUtils, filePaths, filePaths[position], position);
            } else {
                mModel.uploadOneFileWithText(HttpRequest.UPLOAD_ATTACT_URL, RequestParmsUtils.getParamsInstance().needLogin().bulidParams(), new File(filePaths[position]), "imgfile", new AsyncResponseCallBack() {


                    @Override
                    public void onStart() {
                        super.onStart();
                        mView.showDialog(false, "正在上传第" + (position + 1) + "张图片");
                    }

                    @Override
                    public void onResult(String responseStr) {
                        if (StringUtil.notEmpty(responseStr)) {
                            setUploadPicUrl(requestParmsUtils, filePaths, responseStr, position);
                        } else {
                            mView.msgToast("上传图片失败，请稍后重试");
                        }
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        mView.disMissDialog();
                    }

                });
            }
        }

        /**
         * 修改线上地址
         *
         * @param requestParmsUtils 请求参数
         * @param filePaths         图片路径
         * @param responseStr       图片内容
         * @param position          位置
         * @return
         */
        private RequestParmsUtils setUploadPicUrl(final RequestParmsUtils requestParmsUtils, String filePaths[], String responseStr, final int position) {
            if (position == 0) {//第一张
                requestParmsUtils.addParams("picOne", responseStr);
            } else if (position == 1) {//第2张
                requestParmsUtils.addParams("picTwo", responseStr);
            } else if (position == 2) {//第3张
                requestParmsUtils.addParams("picThree", responseStr);
            }
            if (position < filePaths.length - 1) {//不是最后一张，继续上传
                uploadIamgePic(requestParmsUtils, filePaths, position + 1);
            } else if (position == filePaths.length - 1) {//最后一张,调用接口
                applyAfterSales(requestParmsUtils);
            }
            return requestParmsUtils;
        }

        /**
         * 申请售后
         *
         * @param reasonTitle        退换货原因标题
         * @param reasonExplainTitle 退换货说明标题
         * @param identification     1：退货;2:换货
         * @param orderId            订单id
         * @param reason             退换货原因
         * @param explain            退换货说明
         * @param data               图片
         */
        public void applyAfterSales(String reasonTitle, String reasonExplainTitle, String identification, String orderId, final String reason, String explain, ArrayList<String> data) {
            if (TextUtils.isEmpty(reason)) {
                mView.msgToast("请您选择" + reasonTitle);
                return;
            }
            if (TextUtils.isEmpty(explain)) {
                mView.msgToast("请您输入" + reasonExplainTitle);
                return;
            }
            RequestParmsUtils requestParmsUtils = RequestParmsUtils.getParamsInstance()
                    .needLogin()
                    .addParams("identification", identification)
                    .addParams("orderId", orderId)
                    .addParams("reason", reason)
                    .addParams("explain", explain);
            int size = data.size() > 3 ? 3 : data.size();
            if (data.contains(TakePicAdapter.ADD_PIC)) {
                size = size - 1;
            }
            String pics[] = new String[size];
            for (int i = 0; i < size; i++) {
                pics[i] = data.get(i);
            }

            if (size > 0) {//包含图片，申请
                uploadIamgePic(requestParmsUtils, pics, 0);
            } else {//不包含图片，申请
                applyAfterSales(requestParmsUtils);
            }
        }

        /**
         * 申请售后
         *
         * @param requestParmsUtils 申请售后参数对象
         */
        public void applyAfterSales(RequestParmsUtils requestParmsUtils) {
            mModel.requestNetData(HttpRequest.RETURNED_GOODS_SUMITAPPLY,
                    requestParmsUtils.bulidParams(), new AsyncResponseCallBack(this) {
                        @Override
                        public void onResult(String responseStr) {
                            Response<SalesProgressBean> response = JSON.parseObject(responseStr, new TypeReference<Response<SalesProgressBean>>() {
                            });
                            if (response.getStatus() == ConstantsUtil.RESPONSE_SUCCEED) {//请求成功
                                Bundle extraParams = new Bundle();
                                extraParams.putString(ConstantsUtil.EXTRA_ORDERID, response.getData().getOriginalOrderId());
                                extraParams.putString(ConstantsUtil.EXTRA_RETURNEDGOODSORDERID, response.getData().getRefundOrderId());
                                extraParams.putString(ConstantsUtil.EXTRA_IDENTIFICATION, response.getData().getIdentification() + "");
                                JumpUtil.GotoActivity(mView, extraParams, ApplyAfterSaleSuccessActivity.class);
                                mView.finish();
                            } else if (response.getStatus() != ConstantsUtil.RESPONSE_TOKEN_FAIL) {
                                mView.msgToast(response.getStatusDetail());
                            }
                        }


                        @Override
                        public void onStart() {
                            super.onStart();
                            mView.showDialog(false, "正在申请售后...");
                        }

                        @Override
                        public void onFinish() {
                            super.onFinish();
                        }
                    });
        }
    }
}