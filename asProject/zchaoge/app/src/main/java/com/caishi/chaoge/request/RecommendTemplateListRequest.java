package com.caishi.chaoge.request;

import com.caishi.chaoge.base.BaseActivity;
import com.caishi.chaoge.base.BaseBean;
import com.caishi.chaoge.base.BaseRequest;
import com.caishi.chaoge.base.BaseRequestInterface;
import com.caishi.chaoge.bean.ClassListBean;
import com.caishi.chaoge.bean.RecommendBean;
import com.caishi.chaoge.http.BaseObserver;
import com.caishi.chaoge.http.RetrofitFactory;
import com.caishi.chaoge.utils.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;

/**
 * 获取推荐模板
 */
public class RecommendTemplateListRequest extends BaseRequest {

    private BaseActivity context;

    private RecommendTemplateListRequest(BaseActivity context) {
        super(context);
        this.context = context;
    }


    public static RecommendTemplateListRequest newInstance(BaseActivity context) {
        return new RecommendTemplateListRequest(context);

    }


    /**
     * 获取推荐模板
     */
    public void recommend(Map<String, Object> map, final BaseRequestInterface<ArrayList<RecommendBean>> requestInterface) {

        Observable<BaseBean<ArrayList<RecommendBean>>> boundMobileBean = RetrofitFactory.getInstance().recommend(map);
        boundMobileBean.compose(compose((context).<BaseBean<ArrayList<RecommendBean>>>bindToLifecycle())).
                subscribe(new BaseObserver<ArrayList<RecommendBean>>() {
                    @Override
                    protected void onRequestSuccess(int state, String msg, ArrayList<RecommendBean> mineDataBean) {
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
