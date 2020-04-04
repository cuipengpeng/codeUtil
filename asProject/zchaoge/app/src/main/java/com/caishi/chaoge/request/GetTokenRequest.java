package com.caishi.chaoge.request;

import com.caishi.chaoge.base.BaseBean;
import com.caishi.chaoge.base.BaseRequest;
import com.caishi.chaoge.base.BaseRequestInterface;
import com.caishi.chaoge.bean.AliStsTokenBean;
import com.caishi.chaoge.http.BaseObserver;
import com.caishi.chaoge.http.RetrofitFactory;
import com.caishi.chaoge.utils.LogUtil;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import io.reactivex.Observable;

/**
 * 创建者： wangchao
 * 描述：获取上传阿里OSS服务器Token
 */

public class GetTokenRequest extends BaseRequest {
    private RxAppCompatActivity context;

    private GetTokenRequest(RxAppCompatActivity context) {
        super(context);
        this.context = context;
    }


    public static GetTokenRequest newInstance(RxAppCompatActivity context) {
        return new GetTokenRequest(context);

    }


    /**
     * 获取OSStoken
     */
    public void getTokenData(
            final BaseRequestInterface<AliStsTokenBean> requestInterface) {

        Observable<BaseBean<AliStsTokenBean>> homeData = RetrofitFactory.getInstance().getToken();
        homeData.compose(compose((context).<BaseBean<AliStsTokenBean>>bindToLifecycle())).
                subscribe(new BaseObserver<AliStsTokenBean>() {
                    @Override
                    protected void onRequestSuccess(int state, String msg, AliStsTokenBean aliStsTokenBean) {
                        if (aliStsTokenBean == null)
                            requestInterface.error(-1, "无数据");
                        else
                            requestInterface.success(state, msg, aliStsTokenBean);
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
