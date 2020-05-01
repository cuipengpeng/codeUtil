package com.jfbank.qualitymarket.base;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jfbank.qualitymarket.callback.AsyncResponseCallBack;
import com.jfbank.qualitymarket.net.BaseResponse;
import com.jfbank.qualitymarket.util.ConstantsUtil;
import com.jfbank.qualitymarket.util.PhotoUtils;
import com.jph.takephoto.compress.CompressConfig;

import java.util.ArrayList;


/**
 * T-MVP Presenter基类
 * Created by baixiaokang on 16/4/22.
 */
public abstract class BasePresenter<M extends BaseModel, T extends BaseView> implements AsyncResponseCallBack.OnResultCommonLisenter {
    public Context mContext;
    public M mModel;
    public T mView;

    public void setVM(Context context, T v, M m) {
        this.mView = v;
        this.mModel = m;
        this.onStart();
        this.mModel.onStart(context);
        this.mContext = context;
    }

    public void onStart() {

    }

    ;

    public void onDestroy() {
        mModel.onDestory();
        mView.onViewDestory();
    }

    /**
     * 拍照
     */
    public void onPickFromCapture() {
        CompressConfig config = new CompressConfig.Builder()
                .setMaxSize(1024 * 900)
                .setMaxPixel(1920)
                .create();
        config.enableReserveRaw(true);
        mView.getTakePhoto().onEnableCompress(config, true);
        mView.getTakePhoto().onPickFromCapture(PhotoUtils.getOutputUri(mContext));
    }

    /**
     * 图库
     */
    public void onPickFromGallery() {
        CompressConfig config = new CompressConfig.Builder()
                .setMaxSize(1024 * 900)
                .setMaxPixel(1920)
                .create();
        config.enableReserveRaw(true);
        mView.getTakePhoto().onEnableCompress(config, true);
        mView.getTakePhoto().onPickFromGallery();
    }

    /**
     * 取消请求
     */
    public void cancel() {
        mModel.cancel();
    }

    public void onAsyNetStart() {

    }

    @Override
    public void onAsyNetCancel() {

    }

    /**
     * 查看原图-多张
     *
     * @param parent         父布局
     * @param photoAddress   图片地址
     * @param curSelectedPos 默认显示位置
     * @param picType        0:网络，1：本地
     */
    public void showPhotoList(ViewGroup parent, @NonNull ArrayList<String> photoAddress, int curSelectedPos, int picType) {
        mView.showPhotoList(parent, photoAddress, curSelectedPos, picType);
    }

    /**
     * 查看原图-单张
     *
     * @param view         图片view
     * @param photoAddress 图片地址
     * @param picType      0:网络，1：本地
     */
    public void showPhotoOne(View view, String photoAddress, int picType) {
        mView.showPhotoOne(view, photoAddress, picType);
    }

    @Override
    public void onAsyNetResult(String url, String responseStr) {
        BaseResponse response = JSON.parseObject(responseStr, new TypeReference<BaseResponse>() {
        });
        if (response.getStatus() == ConstantsUtil.RESPONSE_TOKEN_FAIL) {//TOken失效，重新登录
            mView.onTokenFailure(response.getStatusDetail());
        }
    }


    public void onAsyNetFailure(String url, String s) {
        mView.onNetFailure(url, s);
    }

    public void onAsyNetFinish() {
        mView.disMissDialog();
    }
}
