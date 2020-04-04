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
 * 删除新粉丝  点赞   评论
 */
public class DeleteNewMessageRequest extends BaseRequest {

    private BaseActivity context;

    private DeleteNewMessageRequest(BaseActivity context) {
        super(context);
        this.context = context;
    }


    public static DeleteNewMessageRequest newInstance(BaseActivity context) {
        return new DeleteNewMessageRequest(context);

    }


    /**
     * 删除新粉丝 新粉丝  点赞   评论
     */
    public void deleteNewMessage(int pageFlag, String targetTime, final BaseRequestInterface<Boolean> requestInterface) {
        Observable<BaseBean<Boolean>> newMessageBean = null;
        switch (pageFlag) {
            case 0:
                newMessageBean = RetrofitFactory.getInstance().delNewFans(targetTime);
                break;
            case 1:
                newMessageBean = RetrofitFactory.getInstance().delNewLikes(targetTime);
                break;
            case 2:
                newMessageBean = RetrofitFactory.getInstance().delNewMomentComments(targetTime);
                break;
        }
        if (null == newMessageBean) {
            requestInterface.error(-1, "newMessageBean为null");
            return;
        }
        newMessageBean.compose(compose((context).<BaseBean<Boolean>>bindToLifecycle())).
                subscribe(new BaseObserver<Boolean>() {
                    @Override
                    protected void onRequestSuccess(int state, String msg, Boolean newFansBeans) {
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
