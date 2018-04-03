package com.jf.jlfund.weight.errorview;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;

/**
 * Created by 55 on 2017/10/10.
 * <p>
 * 用于替换WebView页面
 */

public class WebViewErrorPageBean extends ErrorBean {

    private static final String TAG = "WebViewErrorPageBean";

    public String url;      //记录webView加载失败的url

    public WebViewErrorPageBean(View replacedView) {
        super(replacedView);
    }

    public WebViewErrorPageBean(View replacedView, String hintText) {
        super(replacedView, hintText);
    }

    public WebViewErrorPageBean(View replacedView, int imgId) {
        super(replacedView, imgId);
    }

    public WebViewErrorPageBean(View replacedView, String hintText, int imgId) {
        super(replacedView, hintText, imgId);
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public void transferToOriginalPage() {
        if (!(replacedView instanceof WebView)) {
            Log.e(TAG, "transferToOriginalPage: not a webView....");
            return;
        }
        if (TextUtils.isEmpty(url)) {
            Log.e(TAG, "transferToOriginalPage: url is null....");
            return;
        }

        super.transferToOriginalPage();

        ((WebView) replacedView).loadUrl(url);
    }
}
