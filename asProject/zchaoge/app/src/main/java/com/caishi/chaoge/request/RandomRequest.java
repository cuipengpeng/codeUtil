package com.caishi.chaoge.request;

import com.caishi.chaoge.base.BaseActivity;
import com.caishi.chaoge.base.BaseBean;
import com.caishi.chaoge.base.BaseRequest;
import com.caishi.chaoge.base.BaseRequestInterface;
import com.caishi.chaoge.bean.ScenarioBean;
import com.caishi.chaoge.http.BaseObserver;
import com.caishi.chaoge.http.RetrofitFactory;
import com.caishi.chaoge.utils.LogUtil;

import java.util.ArrayList;

import io.reactivex.Observable;

/**
 * 随机剧本
 */
public class RandomRequest extends BaseRequest {

    private BaseActivity context;

    private RandomRequest(BaseActivity context) {
        super(context);
        this.context = context;
    }


    public static RandomRequest newInstance(BaseActivity context) {
        return new RandomRequest(context);

    }


    /**
     * 随机剧本
     */
    public void getRandomList(final BaseRequestInterface<ArrayList<ScenarioBean>> requestInterface) {
        Observable<BaseBean<ArrayList<ScenarioBean>>> boundMobileBean = RetrofitFactory.getInstance().getRandomList(1);
        boundMobileBean.compose(compose((context).<BaseBean<ArrayList<ScenarioBean>>>bindToLifecycle())).
                subscribe(new BaseObserver<ArrayList<ScenarioBean>>() {
                    @Override
                    protected void onRequestSuccess(int state, String msg, ArrayList<ScenarioBean> mineDataBean) {
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
