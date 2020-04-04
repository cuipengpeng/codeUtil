package com.caishi.chaoge.request;

import com.caishi.chaoge.base.BaseActivity;
import com.caishi.chaoge.base.BaseBean;
import com.caishi.chaoge.base.BaseRequest;
import com.caishi.chaoge.base.BaseRequestInterface;
import com.caishi.chaoge.bean.MainFragmentBean;
import com.caishi.chaoge.http.BaseObserver;
import com.caishi.chaoge.http.RetrofitFactory;
import com.caishi.chaoge.utils.LogUtil;

import io.reactivex.Observable;

/**
 * 首页数据
 */
public class MainFragmentRequest extends BaseRequest {

    private BaseActivity context;

    private MainFragmentRequest(BaseActivity context) {
        super(context);
        this.context = context;
    }


    public static MainFragmentRequest newInstance(BaseActivity context) {
        return new MainFragmentRequest(context);

    }


    /**
     * 首页基础数据
     */
    public void basicData(final BaseRequestInterface<MainFragmentBean> requestInterface) {


        Observable<BaseBean<MainFragmentBean>> boundMobileBean = RetrofitFactory.getInstance().basicData();
        boundMobileBean.compose(compose((context).<BaseBean<MainFragmentBean>>bindToLifecycle())).
                subscribe(new BaseObserver<MainFragmentBean>() {
                    @Override
                    protected void onRequestSuccess(int state, String msg, MainFragmentBean mineDataBean) {
                        if (mineDataBean == null)
                            requestInterface.error(-1, "无数据");
                        else
                            requestInterface.success(state, msg, mineDataBean);
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
