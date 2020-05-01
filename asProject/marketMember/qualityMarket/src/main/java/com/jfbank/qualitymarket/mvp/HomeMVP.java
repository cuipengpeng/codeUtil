package com.jfbank.qualitymarket.mvp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jfbank.qualitymarket.AppContext;
import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.base.BaseModel;
import com.jfbank.qualitymarket.base.BasePresenter;
import com.jfbank.qualitymarket.base.BaseView;
import com.jfbank.qualitymarket.callback.AsyncResponseCallBack;
import com.jfbank.qualitymarket.model.HomePageBean;
import com.jfbank.qualitymarket.model.MsProductBean;
import com.jfbank.qualitymarket.model.NavigationHomeBean;
import com.jfbank.qualitymarket.model.QualityNewsBean;
import com.jfbank.qualitymarket.net.HttpRequest;
import com.jfbank.qualitymarket.net.RequestParmsUtils;
import com.jfbank.qualitymarket.net.Response;
import com.jfbank.qualitymarket.util.ConstantsUtil;

import java.util.HashMap;
import java.util.List;

/**
 * 首页MVP
 * Created by Herri on 2017/04/17
 */
public class HomeMVP {

    /**
     *
     */
    public interface View extends BaseView {
        /**
         * 更新首页banner，type，列表数据
         */

        void upHomeView(final boolean isInit, final boolean isRefresh, int floor, HomePageBean response);

        /**
         * 更新首页秒杀数据
         */

        void updateMsgNumView(Integer data);

        /**
         * 更新秒杀功能
         *
         * @param response
         */

        void updateMSView(Response<List<MsProductBean>> response);

        /**
         * 修改通知
         *
         * @param list 通知列表
         */
        void updateNoticeView(final List<QualityNewsBean> list);

        /**
         * 更新分类推荐点击事件
         */
        void updateTypeView(final List<NavigationHomeBean> navigation);
    }

    public static class Model extends BaseModel {
    }

    public static class Presenter extends BasePresenter<Model, View> {
        /**
         * 网络获秒杀数据
         */

        public void getMSData() {
            mModel.requestNetData(HttpRequest.ACTIVITYPRODUCTSHOW, RequestParmsUtils.getParamsInstance().bulidParams(), new AsyncResponseCallBack(this) {
                @Override
                public void onResult(String responseStr) {
                    Response<List<MsProductBean>> response = JSON.parseObject(responseStr, new TypeReference<Response<List<MsProductBean>>>() {
                    });
                    if (response.getStatus() == ConstantsUtil.RESPONSE_SUCCEED) {
                        mView.updateMSView(response);
                    } else {
                        mView.updateMSView(null);
                    }
                }
            });
        }

        /**
         * 网络获取消息条数
         */

        public void getMsgNumData() {
            if (AppContext.isLogin) {
                mModel.requestNetData(HttpRequest.USERMESSAGEUNREAD, RequestParmsUtils.getParamsInstance().needLogin().bulidParams(), new AsyncResponseCallBack(this) {
                    @Override
                    public void onResult(String responseStr) {
                        Response<Integer> response = JSON.parseObject(responseStr, new TypeReference<Response<Integer>>() {
                        });
                        if (response.getStatus() == ConstantsUtil.RESPONSE_SUCCEED) {
                            mView.updateMsgNumView(response.getData());
                        } else {
                            mView.updateMsgNumView(0);
                        }
                    }
                });
            } else {//未登录
                mView.updateMsgNumView(0);
            }
        }

        /**
         * 获取首页数据
         *
         * @param isInit 是否第一次初始化数据
         */
        public void getIndexData(final boolean isInit, final boolean isRefresh, final int floor) {

            mModel.requestNetData(HttpRequest.HOME_PAGE_INDEX, RequestParmsUtils.getParamsInstance().addParams("floor", floor + "").bulidParams(), new AsyncResponseCallBack() {
                @Override
                public void onResult(String responseStr) {
                    Response<HomePageBean> response = JSON.parseObject(responseStr, new TypeReference<Response<HomePageBean>>() {
                    });
                    if (response.getStatus() == ConstantsUtil.RESPONSE_SUCCEED) {//请求成功
                        mView.upHomeView(isInit, isRefresh, floor, response.getData());
                        if (isInit)
                            mView.showContent();
                    } else if (response.getStatus() == ConstantsUtil.RESPONSE_NO_DATA) {
                        if (isInit || isRefresh) {
                            mView.setEmpty(R.mipmap.ic_default_empty, response.getStatusDetail(), false, null);
                            mView.showEmpty();
                        }

                    } else {
                        if (isInit) {
                            mView.setError(-1, response.getStatusDetail(), true, null);
                            mView.showError();
                        } else {

                        }
                    }
                }

                @Override
                public void onFailed(String path, String msg) {
                    super.onFailed(path, msg);
                    if (floor == 1) {
                        if (isInit) {
                            mView.setError(-1, msg, true, null);
                            mView.showError();
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
                    if (floor == 1) {
                        if (isRefresh) {
                            mView.onFinishReFreshView();
                        }
                    } else {
                        mView.onFinishLoadMoreView();
                    }
                }
            });
        }
    }
}