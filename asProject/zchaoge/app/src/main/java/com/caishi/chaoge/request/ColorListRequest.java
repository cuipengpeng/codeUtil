package com.caishi.chaoge.request;

import com.caishi.chaoge.base.BaseActivity;
import com.caishi.chaoge.base.BaseBean;
import com.caishi.chaoge.base.BaseRequest;
import com.caishi.chaoge.base.BaseRequestInterface;
import com.caishi.chaoge.http.BaseObserver;
import com.caishi.chaoge.http.RetrofitFactory;
import com.caishi.chaoge.utils.LogUtil;

import java.util.ArrayList;

import io.reactivex.Observable;

/**
 * 字体
 */
public class ColorListRequest extends BaseRequest {

    private BaseActivity context;

    private ColorListRequest(BaseActivity context) {
        super(context);
        this.context = context;
    }


    public static ColorListRequest newInstance(BaseActivity context) {
        return new ColorListRequest(context);

    }


    /**
     * 字体
     */
    public void getColorList(final BaseRequestInterface<ArrayList<String>> requestInterface) {


        Observable<BaseBean<ArrayList<String>>> boundMobileBean = RetrofitFactory.getInstance().getColorList();
        boundMobileBean.compose(compose((context).<BaseBean<ArrayList<String>>>bindToLifecycle())).
                subscribe(new BaseObserver<ArrayList<String>>() {
                    @Override
                    protected void onRequestSuccess(int state, String msg, ArrayList<String> mineDataBean) {
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
