package com.caishi.chaoge.request;

import com.caishi.chaoge.base.BaseActivity;
import com.caishi.chaoge.base.BaseBean;
import com.caishi.chaoge.base.BaseRequest;
import com.caishi.chaoge.base.BaseRequestInterface;
import com.caishi.chaoge.http.BaseObserver;
import com.caishi.chaoge.http.RetrofitFactory;
import com.caishi.chaoge.utils.LogUtil;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.util.Map;

import io.reactivex.Observable;

/**
 * 发布
 */
public class PublishRequest extends BaseRequest {

    private RxAppCompatActivity context;

    private PublishRequest(RxAppCompatActivity context) {
        super(context);
        this.context = context;
    }


    public static PublishRequest newInstance(RxAppCompatActivity context) {
        return new PublishRequest(context);

    }


    /**
     * 发布
     */
    public void publish(Map<String, Object> map, final BaseRequestInterface<String> requestInterface) {

        Observable<BaseBean<String>> homeData = RetrofitFactory.getInstance().publish(map);
        homeData.compose(compose((context).<BaseBean<String>>bindToLifecycle())).
                subscribe(new BaseObserver<String>() {
                    @Override
                    protected void onRequestSuccess(int state, String msg, String momentId) {
                        requestInterface.success(state, msg, momentId);
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
