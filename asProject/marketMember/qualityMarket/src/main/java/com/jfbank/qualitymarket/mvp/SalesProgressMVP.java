package com.jfbank.qualitymarket.mvp;

import android.os.Bundle;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.activity.ProgressCheckForGoodsRejectedActivity;
import com.jfbank.qualitymarket.activity.ProgressCheckForLogisticsActivity;
import com.jfbank.qualitymarket.base.BaseModel;
import com.jfbank.qualitymarket.base.BasePresenter;
import com.jfbank.qualitymarket.base.BaseView;
import com.jfbank.qualitymarket.callback.AsyncResponseCallBack;
import com.jfbank.qualitymarket.model.SalesProgressBean;
import com.jfbank.qualitymarket.net.BaseResponse;
import com.jfbank.qualitymarket.net.HttpRequest;
import com.jfbank.qualitymarket.net.RequestParmsUtils;
import com.jfbank.qualitymarket.net.Response;
import com.jfbank.qualitymarket.util.ConstantsUtil;
import com.jfbank.qualitymarket.util.JumpUtil;

import java.util.List;

/**
 * Created by Herri on 2017/04/20
 */
public class SalesProgressMVP {


    public interface View extends BaseView {
        /**
         * 更新列表
         *
         * @param response
         */
        void updateListView(Response<List<SalesProgressBean>> response);

        /**
         * 成功取消订单
         *
         * @param position
         */
        void cancelOrderSuceess(int position);
    }

    public static class Model extends BaseModel {

    }

    public static class Presenter extends BasePresenter<Model, View> {
        /**
         * 跳转到进度详情界面
         * identification为1即退货: 0-审核退货中, 1-待寄送商品, 2-拒绝退货 ，
         * 6-已取消，如果identification为2即换货: 0-换货审核中 2-拒绝换货,
         * 5-已取消 这些状态跳转ProgressCheckForGoodsRejectedActivity，
         * 其他状态跳转ProgressCheckForLogisticsActivity
         *
         * @param data
         */
        public void startProgressActivity(SalesProgressBean data) {
            Bundle bundle = new Bundle();
            bundle.putString(ConstantsUtil.EXTRA_ORDERID, data.getOrderId());
            bundle.putString(ConstantsUtil.EXTRA_RETURNEDGOODSORDERID, data.getRefundOrderId());
            bundle.putString(ConstantsUtil.EXTRA_IDENTIFICATION, data.getIdentification() + "");
            if ((data.getIdentification() == 1 &&
                    (data.getRefundOrderStatus() == 0 ||
                            data.getRefundOrderStatus() == 2 || data.getRefundOrderStatus() == 6))
                    || (data.getIdentification() == 2 &&
                    (data.getRefundOrderStatus() == 0 || data.getRefundOrderStatus() == 2 ||
                            data.getRefundOrderStatus() == 5)) || data.getRefundOrderStatus() == 1) {
                JumpUtil.GotoActivity(mView, bundle, ProgressCheckForGoodsRejectedActivity.class);
            } else {
                JumpUtil.GotoActivity(mView, bundle, ProgressCheckForLogisticsActivity.class);
            }
        }

        /**
         * 获取列表数据
         * public
         *
         * @param isInit    是否初始化
         * @param isRefresh 是否刷新
         * @param pageNum   页数
         */
        public void getListData(final boolean isInit, final boolean isRefresh, int pageNum) {
            mModel.requestNetData(HttpRequest.GET_QUERYSCHEDULE, RequestParmsUtils.getParamsInstance().needLogin().addPageNum(pageNum).bulidParams(), new AsyncResponseCallBack(this) {
                @Override
                public void onResult(String responseStr) {
                    Response<List<SalesProgressBean>> response = JSON.parseObject(responseStr, new TypeReference<Response<List<SalesProgressBean>>>() {
                    });
                    if (response.getStatus() == ConstantsUtil.RESPONSE_SUCCEED) {//请求成功
                        mView.updateListView(response);
                        if (isInit)
                            mView.showContent();
                    } else if (response.getStatus() == ConstantsUtil.RESPONSE_NO_DATA) {
                        if (isInit) {
                            mView.setEmpty(R.mipmap.ic_default_empty, response.getStatusDetail(), false, null);
                            mView.showEmpty();
                        }

                    } else {
                        if (isInit) {
                            mView.setError(-1, response.getStatusDetail(), false, null);
                            mView.showError();
                        } else {

                        }
                    }
                }

                @Override
                public void onStart() {
                    super.onStart();
                    if (isInit)
                        mView.showLoading();
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    if (!isInit && isRefresh) {
                        mView.onFinishReFreshView();
                    } else if (!isInit && !isRefresh) {
                        mView.onFinishLoadMoreView();
                    }

                }
            });
        }

        /**
         * 取消订单
         *
         * @param orderId        订单id
         * @param identification 1:退货订单，2：换货订单
         * @param position       列表位置
         */
        public void cancelOrderId(final String orderId, final int identification, final int position) {
            mModel.requestNetData(HttpRequest.CANCELAPPLY, RequestParmsUtils.getParamsInstance().needLogin().addParams("returnOrderId", orderId).addParams("identification", identification + "").bulidParams(), new AsyncResponseCallBack(this) {
                @Override
                public void onResult(String responseStr) {
                    Response<BaseResponse> response = JSON.parseObject(responseStr, new TypeReference<Response<BaseResponse>>() {
                    });
                    if (response.getStatus() == ConstantsUtil.RESPONSE_SUCCEED) {//请求成功
                        mView.msgToast(response.getStatusDetail());
                        mView.cancelOrderSuceess(position);
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
    }
}