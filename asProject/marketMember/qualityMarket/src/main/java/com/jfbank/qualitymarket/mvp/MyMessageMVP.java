package com.jfbank.qualitymarket.mvp;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jfbank.qualitymarket.AppContext;
import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.base.BaseModel;
import com.jfbank.qualitymarket.base.BasePresenter;
import com.jfbank.qualitymarket.base.BaseView;
import com.jfbank.qualitymarket.callback.AsyncResponseCallBack;
import com.jfbank.qualitymarket.model.MessageBean;
import com.jfbank.qualitymarket.net.HttpRequest;
import com.jfbank.qualitymarket.net.RequestParmsUtils;
import com.jfbank.qualitymarket.net.Response;
import com.jfbank.qualitymarket.util.CommonUtils;
import com.jfbank.qualitymarket.util.ConstantsUtil;

import java.util.List;

/**
 * Created by Herri on 2017/04/26
 */
public class MyMessageMVP {


    public interface View extends BaseView {
        /**
         * 更新消息列表
         *
         * @param isRefresh
         * @param response
         */
        void updateListView(final boolean isRefresh, Response<List<MessageBean>> response);

        /**
         * 更新消息状态
         *
         * @param position
         */
        void updateMsgStatus(final int position);

        /**
         * 删除消息
         *
         * @param position
         */
        void updateDeleteMsg(int position);
    }

    public static class Model extends BaseModel {

    }

    public static class Presenter extends BasePresenter<Model, View> {
        /**
         * 更新消息状态
         *
         * @param position
         */
        public void deleteMsg(final int position, MessageBean messageBean) {
            mModel.requestNetData(HttpRequest.UPDATEQUALITYUSERMESSAGE, RequestParmsUtils.getParamsInstance().needLogin().addParams("type", 1 + "").addParams("messageId", messageBean.getId()).bulidParams(), new AsyncResponseCallBack(this) {
                @Override
                public void onResult(String responseStr) {
                    Response<List<MessageBean>> response = JSON.parseObject(responseStr, new TypeReference<Response<List<MessageBean>>>() {
                    });
                    if (response.getStatus() == ConstantsUtil.RESPONSE_SUCCEED) {//请求成功
                        mView.updateDeleteMsg(position);
                    } else {
                    }
                }

                @Override
                public void onStart() {
                    super.onStart();
                    mView.showDialog(true,"正在处理消息...");
                }
            });
        }

        /**
         * 更新消息条数
         *
         * @param isRefesh 是否刷新
         */
        public void getData(final boolean isInit, final boolean isRefesh, int pageNum) {
            mModel.requestNetData(HttpRequest.QUALITYUSERMESSAGESHOW, RequestParmsUtils.getParamsInstance().needLogin().addPageNum(pageNum).bulidParams(), new AsyncResponseCallBack(this) {
                @Override
                public void onResult(String responseStr) {
                    Response<List<MessageBean>> response = JSON.parseObject(responseStr, new TypeReference<Response<List<MessageBean>>>() {
                    });
                    if (response.getStatus() == ConstantsUtil.RESPONSE_SUCCEED) {//请求成功
                        mView.updateListView(isRefesh, response);
                        if (isInit)
                            mView.showContent();
                    } else if (response.getStatus() == ConstantsUtil.RESPONSE_NO_DATA) {
                        if (isInit||isRefesh) {
                            mView.setEmpty(R.drawable.ic_no_msg,"暂无消息", false, null);
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
                    if (!isInit && isRefesh) {
                        mView.onFinishReFreshView();
                    } else if (!isInit && !isRefesh) {
                        mView.onFinishLoadMoreView();
                    }
                }
            });
        }

        /**
         * 更新消息状态
         *
         * @param position
         */
        public void updateMsgStatus(final int position, MessageBean messageBean) {
            CommonUtils.startIntent(mContext, messageBean.getAppPage(), messageBean.getAppParams());
            if (TextUtils.equals("0", messageBean.getReadStatus()) && AppContext.isLogin) {//消息未读则修改状态，否则不修改

                mModel.requestNetData(HttpRequest.UPDATEQUALITYUSERMESSAGE, RequestParmsUtils.getParamsInstance().needLogin().addParams("messageId", messageBean.getId()).bulidParams(), new AsyncResponseCallBack(this) {
                    @Override
                    public void onResult(String responseStr) {
                        Response<List<MessageBean>> response = JSON.parseObject(responseStr, new TypeReference<Response<List<MessageBean>>>() {
                        });
                        if (response.getStatus() == ConstantsUtil.RESPONSE_SUCCEED) {//请求成功
                            mView.updateMsgStatus(position);
                        } else {
                        }
                    }
                });
            }
        }
    }
}