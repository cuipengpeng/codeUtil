package com.caishi.chaoge.request;

import com.caishi.chaoge.base.BaseActivity;
import com.caishi.chaoge.base.BaseBean;
import com.caishi.chaoge.base.BaseRequest;
import com.caishi.chaoge.base.BaseRequestInterface;
import com.caishi.chaoge.bean.FontBean;
import com.caishi.chaoge.http.BaseObserver;
import com.caishi.chaoge.http.RetrofitFactory;
import com.caishi.chaoge.utils.LogUtil;

import java.util.ArrayList;

import io.reactivex.Observable;

/**
 * 字体
 */
public class FontListRequest extends BaseRequest {

    private BaseActivity context;

    private FontListRequest(BaseActivity context) {
        super(context);
        this.context = context;
    }


    public static FontListRequest newInstance(BaseActivity context) {
        return new FontListRequest(context);

    }


    /**
     * 字体
     */
    public void getFontList(final BaseRequestInterface<ArrayList<FontBean>> requestInterface) {


        Observable<BaseBean<ArrayList<FontBean>>> boundMobileBean = RetrofitFactory.getInstance().getFontList();
        boundMobileBean.compose(compose((context).<BaseBean<ArrayList<FontBean>>>bindToLifecycle())).
                subscribe(new BaseObserver<ArrayList<FontBean>>() {
                    @Override
                    protected void onRequestSuccess(int state, String msg, ArrayList<FontBean> mineDataBean) {
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
