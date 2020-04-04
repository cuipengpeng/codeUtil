package com.caishi.chaoge.request;

import com.caishi.chaoge.base.BaseActivity;
import com.caishi.chaoge.base.BaseBean;
import com.caishi.chaoge.base.BaseRequest;
import com.caishi.chaoge.base.BaseRequestInterface;
import com.caishi.chaoge.bean.NewMessageBean;
import com.caishi.chaoge.http.BaseObserver;
import com.caishi.chaoge.http.RetrofitFactory;
import com.caishi.chaoge.utils.LogUtil;

import java.util.ArrayList;
import java.util.Map;

import io.reactivex.Observable;

/**
 * 获取新粉丝  点赞   评论列表
 */
public class GetNewMessageRequest extends BaseRequest {

    private BaseActivity context;

    private GetNewMessageRequest(BaseActivity context) {
        super(context);
        this.context = context;
    }


    public static GetNewMessageRequest newInstance(BaseActivity context) {
        return new GetNewMessageRequest(context);

    }


    /**
     * 获取新粉丝 新粉丝  点赞   评论列表
     */
    public void getNewMessage(int pageFlag, Map<String, Object> map, final BaseRequestInterface<ArrayList<NewMessageBean>> requestInterface) {
        Observable<BaseBean<ArrayList<NewMessageBean>>> newMessageBean = null;
        switch (pageFlag) {
            case 0:
                newMessageBean = RetrofitFactory.getInstance().getNewFans(map);
                break;
            case 1:
                newMessageBean = RetrofitFactory.getInstance().getNewLikes(map);
                break;
            case 2:
                newMessageBean = RetrofitFactory.getInstance().getNewMomentComments(map);
                break;
        }
        if (null == newMessageBean) {
            requestInterface.error(-1, "newMessageBean为null");
            return;
        }
        newMessageBean.compose(compose((context).<BaseBean<ArrayList<NewMessageBean>>>bindToLifecycle())).
                subscribe(new BaseObserver<ArrayList<NewMessageBean>>() {
                    @Override
                    protected void onRequestSuccess(int state, String msg, ArrayList<NewMessageBean> newFansBeans) {
                        if (newFansBeans == null)
                            requestInterface.error(-1, "无数据");
                        else
                            requestInterface.success(state, msg, newFansBeans);
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
