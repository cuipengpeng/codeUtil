package com.caishi.chaoge.request;

import com.caishi.chaoge.base.BaseActivity;
import com.caishi.chaoge.base.BaseBean;
import com.caishi.chaoge.base.BaseRequest;
import com.caishi.chaoge.base.BaseRequestInterface;
import com.caishi.chaoge.bean.LoginBean;
import com.caishi.chaoge.http.BaseObserver;
import com.caishi.chaoge.http.RetrofitFactory;
import com.caishi.chaoge.utils.LogUtil;

import java.util.Map;

import io.reactivex.Observable;


/**
 * 创建者： wangchao
 * 描述：加关注
 */

public class OtherLoginRequest extends BaseRequest {
    private BaseActivity context;

    private OtherLoginRequest(BaseActivity context) {
        super(context);
        this.context = context;
    }


    public static OtherLoginRequest newInstance(BaseActivity context) {
        return new OtherLoginRequest(context);

    }


    /**
     * 第三方登录
     */
    public void otherLogin(Map<String,Object> map,
                               final BaseRequestInterface<LoginBean> requestInterface) {
        Observable<BaseBean<LoginBean>> homeData = RetrofitFactory.getInstance().otherLogin(map);
        homeData.compose(compose((context).<BaseBean<LoginBean>>bindToLifecycle())).
                subscribe(new BaseObserver<LoginBean>() {
                    @Override
                    protected void onRequestSuccess(int state, String msg, LoginBean loginBean) {
                        if (loginBean == null)
                            requestInterface.error(-1, "无数据");
                        else
                            requestInterface.success(state, msg, loginBean);
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
