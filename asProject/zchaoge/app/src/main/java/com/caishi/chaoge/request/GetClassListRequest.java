package com.caishi.chaoge.request;

import com.caishi.chaoge.base.BaseActivity;
import com.caishi.chaoge.base.BaseBean;
import com.caishi.chaoge.base.BaseRequest;
import com.caishi.chaoge.base.BaseRequestInterface;
import com.caishi.chaoge.bean.ClassListBean;
import com.caishi.chaoge.bean.FontBean;
import com.caishi.chaoge.http.BaseObserver;
import com.caishi.chaoge.http.RetrofitFactory;
import com.caishi.chaoge.utils.LogUtil;

import java.util.ArrayList;

import io.reactivex.Observable;

/**
 *  获取分类信息
 */
public class GetClassListRequest extends BaseRequest {

    private BaseActivity context;

    private GetClassListRequest(BaseActivity context) {
        super(context);
        this.context = context;
    }


    public static GetClassListRequest newInstance(BaseActivity context) {
        return new GetClassListRequest(context);

    }


    /**
     * 获取分类信息
     *
     * @param type 那个类别 音乐 music 剧本 script 背景图 backGround 模板 model
     */
    public void getClassList( String type ,final BaseRequestInterface<ArrayList<ClassListBean>> requestInterface) {
        Observable<BaseBean<ArrayList<ClassListBean>>> boundMobileBean = RetrofitFactory.getInstance().getClassList(type);
        boundMobileBean.compose(compose((context).<BaseBean<ArrayList<ClassListBean>>>bindToLifecycle())).
                subscribe(new BaseObserver<ArrayList<ClassListBean>>() {
                    @Override
                    protected void onRequestSuccess(int state, String msg, ArrayList<ClassListBean> mineDataBean) {
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
