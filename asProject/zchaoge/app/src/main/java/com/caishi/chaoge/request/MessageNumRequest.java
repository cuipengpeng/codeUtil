package com.caishi.chaoge.request;

import com.caishi.chaoge.base.BaseActivity;
import com.caishi.chaoge.base.BaseBean;
import com.caishi.chaoge.base.BaseRequest;
import com.caishi.chaoge.base.BaseRequestInterface;
import com.caishi.chaoge.bean.MessageNumBean;
import com.caishi.chaoge.http.BaseObserver;
import com.caishi.chaoge.http.RetrofitFactory;
import com.caishi.chaoge.utils.LogUtil;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.util.ArrayList;

import io.reactivex.Observable;

/**
 * 消息数
 */
public class MessageNumRequest extends BaseRequest {

    private RxAppCompatActivity context;

    private MessageNumRequest(RxAppCompatActivity context) {
        super(context);
        this.context = context;
    }


    public static MessageNumRequest newInstance(RxAppCompatActivity context) {
        return new MessageNumRequest(context);

    }


    /**
     * 消息数
     */
    public void getMessageNum(String userId,final BaseRequestInterface<MessageNumBean> requestInterface) {


        Observable<BaseBean<MessageNumBean>> boundMobileBean = RetrofitFactory.getInstance().getMessageNum(userId);
        boundMobileBean.compose(compose((context).<BaseBean<MessageNumBean>>bindToLifecycle())).
                subscribe(new BaseObserver<MessageNumBean>() {
                    @Override
                    protected void onRequestSuccess(int state, String msg, MessageNumBean messageNumBean) {
                        if (messageNumBean == null)
                            requestInterface.error(-1, "无数据");
                        else
                            requestInterface.success(state, msg, messageNumBean);
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
