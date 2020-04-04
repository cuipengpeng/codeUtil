package com.caishi.chaoge.request;

import com.caishi.chaoge.base.BaseActivity;
import com.caishi.chaoge.base.BaseBean;
import com.caishi.chaoge.base.BaseRequest;
import com.caishi.chaoge.base.BaseRequestInterface;
import com.caishi.chaoge.bean.TemplateBean;
import com.caishi.chaoge.bean.VoiceTranslateBean;
import com.caishi.chaoge.http.BaseObserver;
import com.caishi.chaoge.http.RetrofitFactory;
import com.caishi.chaoge.utils.LogUtil;

import java.util.ArrayList;

import io.reactivex.Observable;

/**
 * 语音识别（阿里）
 */
public class RecognitionRequest extends BaseRequest {

    private BaseActivity context;

    private RecognitionRequest(BaseActivity context) {
        super(context);
        this.context = context;
    }


    public static RecognitionRequest newInstance(BaseActivity context) {
        return new RecognitionRequest(context);

    }


    /**
     * 语音识别
     */
    public void getRecognition(String fileLink, final BaseRequestInterface<ArrayList<VoiceTranslateBean>> requestInterface) {

        Observable<BaseBean<ArrayList<VoiceTranslateBean>>> boundMobileBean = RetrofitFactory.getInstance().getRecognition(fileLink);
        boundMobileBean.compose(compose((context).<BaseBean<ArrayList<VoiceTranslateBean>>>bindToLifecycle())).
                subscribe(new BaseObserver<ArrayList<VoiceTranslateBean>>() {
                    @Override
                    protected void onRequestSuccess(int state, String msg, ArrayList<VoiceTranslateBean> templateBean) {
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
