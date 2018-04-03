package com.jf.jlfund.weight.errorview;

import android.view.View;

/**
 * Created by 55 on 2017/10/10.
 * <p>
 * 完全替换的ErrorPageBean
 * 即不考虑传入的replacedView包含头布局的情况
 */

public class DefaultErrorPageBean extends ErrorBean {


    public DefaultErrorPageBean(View replacedView) {
        super(replacedView);
    }

    public DefaultErrorPageBean(View replacedView, String hintText) {
        super(replacedView, hintText);
    }

    public DefaultErrorPageBean(View replacedView, int imgId) {
        super(replacedView, imgId);
    }

    public DefaultErrorPageBean(View replacedView, String hintText, int imgId) {
        super(replacedView, hintText, imgId);
    }
}
