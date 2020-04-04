package com.caishi.chaoge.request;

import com.caishi.chaoge.base.BaseBean;
import com.caishi.chaoge.base.BaseRequest;
import com.caishi.chaoge.base.BaseRequestInterface;
import com.caishi.chaoge.bean.HomeDataBean;
import com.caishi.chaoge.bean.MessageNumBean;
import com.caishi.chaoge.http.BaseObserver;
import com.caishi.chaoge.http.RetrofitFactory;
import com.caishi.chaoge.utils.LogUtil;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.util.ArrayList;

import io.reactivex.Observable;

/**
 * 根据动态Id查询动态
 */
public class FindMomentRequest extends BaseRequest {

    private RxAppCompatActivity context;

    private FindMomentRequest(RxAppCompatActivity context) {
        super(context);
        this.context = context;
    }


    public static FindMomentRequest newInstance(RxAppCompatActivity context) {
        return new FindMomentRequest(context);

    }


    /**
     * 根据动态Id查询动态
     */
    public void getMessageNum(String momentId,final BaseRequestInterface<HomeDataBean> requestInterface) {


        Observable<BaseBean<HomeDataBean>> boundMobileBean = RetrofitFactory.getInstance().findMoment(momentId);
        boundMobileBean.compose(compose((context).<BaseBean<HomeDataBean>>bindToLifecycle())).
                subscribe(new BaseObserver<HomeDataBean>() {
                    @Override
                    protected void onRequestSuccess(int state, String msg, HomeDataBean homeDataBeanArrayList) {
                        if (homeDataBeanArrayList == null)
                            requestInterface.error(-1, "无数据");
                        else
                            requestInterface.success(state, msg, homeDataBeanArrayList);
                    }

                    @Override
                    protected void onRequestError(int state, String msg) {
                        errorType(context, state);
                        requestInterface.error(state, msg);
                        LogUtil.i(msg);
                    }
                });


    }


}
