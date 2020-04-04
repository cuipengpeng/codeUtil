package com.caishi.chaoge.request;

import com.caishi.chaoge.base.BaseActivity;
import com.caishi.chaoge.base.BaseBean;
import com.caishi.chaoge.base.BaseRequest;
import com.caishi.chaoge.base.BaseRequestInterface;
import com.caishi.chaoge.bean.RecommendBean;
import com.caishi.chaoge.bean.TemplateBean;
import com.caishi.chaoge.http.BaseObserver;
import com.caishi.chaoge.http.RetrofitFactory;
import com.caishi.chaoge.utils.LogUtil;

import java.util.ArrayList;
import java.util.Map;

import io.reactivex.Observable;

/**
 * 获取模板详情
 */
public class GetTemplateRequest extends BaseRequest {

    private BaseActivity context;

    private GetTemplateRequest(BaseActivity context) {
        super(context);
        this.context = context;
    }


    public static GetTemplateRequest newInstance(BaseActivity context) {
        return new GetTemplateRequest(context);

    }


    /**
     * 获取模板详情
     */
    public void getModelById(String modelId, final BaseRequestInterface<TemplateBean> requestInterface) {

        Observable<BaseBean<TemplateBean>> boundMobileBean = RetrofitFactory.getInstance().getModelById(modelId);
        boundMobileBean.compose(compose((context).<BaseBean<TemplateBean>>bindToLifecycle())).
                subscribe(new BaseObserver<TemplateBean>() {
                    @Override
                    protected void onRequestSuccess(int state, String msg, TemplateBean templateBean) {
                        if (templateBean == null)
                            requestInterface.error(-1, "无数据");
                        else
                            requestInterface.success(state, msg, templateBean);
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
