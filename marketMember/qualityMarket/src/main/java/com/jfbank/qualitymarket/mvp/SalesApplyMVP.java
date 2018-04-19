package com.jfbank.qualitymarket.mvp;

import android.os.Bundle;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.activity.ApplyAfterSalesActivity;
import com.jfbank.qualitymarket.base.BaseModel;
import com.jfbank.qualitymarket.base.BasePresenter;
import com.jfbank.qualitymarket.base.BaseView;
import com.jfbank.qualitymarket.callback.AsyncResponseCallBack;
import com.jfbank.qualitymarket.model.AfterSalesReasonBean;
import com.jfbank.qualitymarket.model.SaleApplyOrderBean;
import com.jfbank.qualitymarket.net.HttpRequest;
import com.jfbank.qualitymarket.net.RequestParmsUtils;
import com.jfbank.qualitymarket.net.Response;
import com.jfbank.qualitymarket.util.ConstantsUtil;
import com.jfbank.qualitymarket.util.JumpUtil;

import java.util.List;

/**
 * Created by Herri on 2017/04/20
 */
public class SalesApplyMVP {


    public interface View extends BaseView {
        void updateListView(Response<List<SaleApplyOrderBean>> response);
    }

    public static class Model extends BaseModel {

    }

    public static class Presenter extends BasePresenter<Model, View> {
        /**
         * 申请售后
         *
         * @param orderBean
         */
        public void applyAfterSales(final SaleApplyOrderBean orderBean) {
            mModel.requestNetData(HttpRequest.RETURN_GOODSAPPLYCHECK, RequestParmsUtils.getParamsInstance().needLogin().addParams("orderId", orderBean.getOrderId()).bulidParams(), new AsyncResponseCallBack(this) {
                @Override
                public void onResult(String responseStr) {
                    Response<AfterSalesReasonBean> response = JSON.parseObject(responseStr, new TypeReference<Response<AfterSalesReasonBean>>() {
                    });
                    if (response.getStatus() == ConstantsUtil.RESPONSE_SUCCEED) {//请求成功
                        Bundle extraParams = new Bundle();
                        int aftersalesType = response.getData().getTypeOfSupport();
                        extraParams.putInt(ConstantsUtil.EXTRA_AFTERSALES_TYPE, ((aftersalesType == 1 || aftersalesType == 2) ? 1 : (aftersalesType == 3 ? 2 : 1)));
                        extraParams.putSerializable(ConstantsUtil.EXTRA_AFTERSALESREASONBEAN, response.getData());
                        extraParams.putString(ConstantsUtil.EXTRA_ORDERID, orderBean.getOrderId());
                        extraParams.putString(ConstantsUtil.EXTRA_FIRSTPAYMENT, orderBean.getFirstPayment());
                        JumpUtil.GotoActivity(mView, extraParams, ApplyAfterSalesActivity.class);
                    } else if (response.getStatus() != ConstantsUtil.RESPONSE_TOKEN_FAIL) {
                        mView.msgToast(response.getStatusDetail());
                    }
                }

                @Override
                public void onStart() {
                    super.onStart();
                    mView.showDialog();
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                }
            });
        }

        /**
         * 获取列表数据
         * public
         *
         * @param isInit    是否初始化
         * @param isRefresh 是否刷新
         * @param pageNum   页数
         */
        public void getListData(final boolean isInit, final boolean isRefresh, final int pageNum) {
            mModel.requestNetData(HttpRequest.GET_QUERYAFTERSALES, RequestParmsUtils.getParamsInstance().needLogin().addPageNum(pageNum).bulidParams(), new AsyncResponseCallBack(this) {
                @Override
                public void onResult(String responseStr) {
                    Response<List<SaleApplyOrderBean>> response = JSON.parseObject(responseStr, new TypeReference<Response<List<SaleApplyOrderBean>>>() {
                    });
                    if (response.getStatus() == ConstantsUtil.RESPONSE_SUCCEED) {//请求成功
                        mView.updateListView(response);
                        if (isInit)
                            mView.showContent();
                    } else if (response.getStatus() == ConstantsUtil.RESPONSE_NO_DATA) {
                        if (isInit || isRefresh) {
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
    }
}