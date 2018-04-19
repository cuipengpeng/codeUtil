package com.jfbank.qualitymarket.mvp;

import android.os.Bundle;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jfbank.qualitymarket.activity.ProgressCheckForGoodsRejectedActivity;
import com.jfbank.qualitymarket.activity.ProgressCheckForLogisticsActivity;
import com.jfbank.qualitymarket.adapter.TakePicAdapter;
import com.jfbank.qualitymarket.base.BaseModel;
import com.jfbank.qualitymarket.base.BasePresenter;
import com.jfbank.qualitymarket.base.BaseView;
import com.jfbank.qualitymarket.callback.AsyncResponseCallBack;
import com.jfbank.qualitymarket.net.BaseResponse;
import com.jfbank.qualitymarket.net.HttpRequest;
import com.jfbank.qualitymarket.net.RequestParmsUtils;
import com.jfbank.qualitymarket.net.Response;
import com.jfbank.qualitymarket.util.ConstantsUtil;
import com.jfbank.qualitymarket.util.JumpUtil;
import com.jfbank.qualitymarket.util.StringUtil;
import com.jfbank.qualitymarket.util.ToastUtil;

import org.apache.http.Header;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Herri on 2017/04/25
 */
public class SalesReturnSubmitMVP {


    public interface View extends BaseView {

    }

    public static class Model extends BaseModel {

    }

    public static class Presenter extends BasePresenter<Model, View> {

        /**
         * 取消订单
         *
         * @param returnOrderId  订单id
         * @param identification 1:退货订单，2：换货订单
         */
        public void cancelOrderId(final String returnOrderId, final String identification) {
            mModel.requestNetData(HttpRequest.CANCELAPPLY, RequestParmsUtils.getParamsInstance().needLogin().addParams("returnOrderId", returnOrderId).addParams("identification", identification + "").bulidParams(), new AsyncResponseCallBack(this) {
                @Override
                public void onResult(String responseStr) {
                    Response<BaseResponse> response = JSON.parseObject(responseStr, new TypeReference<Response<BaseResponse>>() {
                    });
                    if (response.getStatus() == ConstantsUtil.RESPONSE_SUCCEED) {//请求成功
                        mView.msgToast(response.getStatusDetail());
                        ProgressCheckForGoodsRejectedActivity activity = ((ProgressCheckForGoodsRejectedActivity) mContext);
                        activity.queryGoodsRejectedProgress(identification, activity.orderId, returnOrderId);
                    } else if (response.getStatus() != ConstantsUtil.RESPONSE_TOKEN_FAIL) {
                        mView.msgToast(response.getStatusDetail());
                    }
                }

                @Override
                public void onStart() {
                    super.onStart();
                    mView.showDialog(true, "正在撤销售后申请...");
                }
            });
        }

        /**
         * 调用接口调用接口上传图片
         *
         * @param requestParmsUtils 接口参数对象
         * @param filePaths         文件路劲
         * @param position          上传第几张
         */
        public void uploadIamgePic(final RequestParmsUtils requestParmsUtils, final String[] filePaths, final int position) {
            mModel.uploadOneFileWithText(HttpRequest.UPLOAD_ATTACT_URL, RequestParmsUtils.getParamsInstance().needLogin().bulidParams(),new File(filePaths[position]), "imgfile", new AsyncResponseCallBack() {

                @Override
                public void onStart() {
                    super.onStart();
                    mView.showDialog(false, "正在上传第" + (position + 1) + "张图片");
                }

                @Override
                public void onResult(String responseStr) {
                    if (StringUtil.notEmpty(responseStr)) {
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
                            applyAfterLogistics(requestParmsUtils);
                        }
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

        /**
         * 申请售后
         */
        public void applyAfterLogistics(String identification, String orderId, final String express, String logisticCode, String freight, ArrayList<String> data) {
            if (TextUtils.isEmpty(express)) {
                mView.msgToast("请您选择物流公司");
                return;
            }

            if (TextUtils.isEmpty(logisticCode) || logisticCode.length() < 6) {
                mView.msgToast("请您输入正确的快递单号");
                return;
            }
            if (TextUtils.isEmpty(freight)) {
                mView.msgToast("请您输入正确的运费");
                return;
            }

            int size = data.size() > 3 ? 3 : data.size();
            if (data.contains(TakePicAdapter.ADD_PIC)) {
                size = size - 1;
            }
            String pics[] = new String[size];
            for (int i = 0; i < size; i++) {
                pics[i] = data.get(i);
            }
            RequestParmsUtils requestParmsUtils = RequestParmsUtils.getParamsInstance()
                    .needLogin()
                    .addParams("identification", identification)
                    .addParams("orderId", orderId)
                    .addParams("express", express)
                    .addParams("logisticCode", logisticCode)
                    .addParams("freight", freight);

            if (size > 0) {//包含图片，申请
                uploadIamgePic(requestParmsUtils, pics, 0);
            } else {//不包含图片，申请
                applyAfterLogistics(requestParmsUtils);
            }
        }

        /**
         * 申请售后
         *
         * @param requestParmsUtils 申请售后参数对象
         */
        public void applyAfterLogistics(RequestParmsUtils requestParmsUtils) {
            mModel.requestNetData(HttpRequest.RETURNED_SUBMITLOGISTICS,
                    requestParmsUtils.bulidParams(), new AsyncResponseCallBack(this) {
                        @Override
                        public void onResult(String responseStr) {
                            Response<BaseResponse> response = JSON.parseObject(responseStr, new TypeReference<Response<BaseResponse>>() {
                            });
                            if (response.getStatus() == ConstantsUtil.RESPONSE_SUCCEED) {//请求成功
                                Bundle bundle = new Bundle();
                                ProgressCheckForGoodsRejectedActivity activity = ((ProgressCheckForGoodsRejectedActivity) mContext);
                                bundle.putString(ConstantsUtil.EXTRA_ORDERID, activity.orderId);
                                bundle.putString(ConstantsUtil.EXTRA_RETURNEDGOODSORDERID, activity.returnedGoodsOrderId);
                                bundle.putString(ConstantsUtil.EXTRA_IDENTIFICATION, activity.identification + "");
                                JumpUtil.GotoActivity(mView, bundle, ProgressCheckForLogisticsActivity.class);
                                mView.finish();
                            } else if (response.getStatus() != ConstantsUtil.RESPONSE_TOKEN_FAIL) {
                                mView.msgToast(response.getStatusDetail());
                            }
                        }

                        @Override
                        public void onStart() {
                            super.onStart();
                            mView.showDialog(false, "正在提交...");
                        }

                        @Override
                        public void onFinish() {
                            super.onFinish();
                        }
                    });
        }
    }
}