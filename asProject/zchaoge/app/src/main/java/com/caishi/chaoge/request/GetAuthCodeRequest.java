package com.caishi.chaoge.request;

import com.caishi.chaoge.base.BaseActivity;
import com.caishi.chaoge.base.BaseBean;
import com.caishi.chaoge.base.BaseRequest;
import com.caishi.chaoge.base.BaseRequestInterface;
import com.caishi.chaoge.http.BaseObserver;
import com.caishi.chaoge.http.RetrofitFactory;
import com.caishi.chaoge.utils.LogUtil;
import com.caishi.chaoge.utils.ToastUtils;

import io.reactivex.Observable;

/**
 * 获取验证码
 */
public class GetAuthCodeRequest extends BaseRequest {

    private BaseActivity context;

    private GetAuthCodeRequest(BaseActivity context) {
        super(context);
        this.context = context;
    }


    public static GetAuthCodeRequest newInstance(BaseActivity context) {
        return new GetAuthCodeRequest(context);

    }


    /**
     * 获取验证码
     */
    public void getValidateAuthCode(String mobile, final BaseRequestInterface<Boolean> requestInterface) {

        Observable<BaseBean<Boolean>> homeData = RetrofitFactory.getInstance().getValidateAuthCode(mobile);
        homeData.compose(compose((context).<BaseBean<Boolean>>bindToLifecycle())).
                subscribe(new BaseObserver<Boolean>() {
                    @Override
                    protected void onRequestSuccess(int state, String msg, Boolean publishBean) {
                        if (publishBean == null)
                            requestInterface.error(-1, "无数据");
                        else
                            requestInterface.success(state, msg, publishBean);
                    }

                    @Override
                    protected void onRequestError(int state, String msg) {
                        if (state >= 40000) {
                            ToastUtils.show(context, msg);
                        }
                        errorType(context, state);
                        requestInterface.error(state, msg);
                        LogUtil.i(msg);
                    }
                });
    }
}
