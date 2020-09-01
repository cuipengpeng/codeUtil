package com.hospital.checkup.js;

import android.app.Activity;
import android.webkit.WebView;
import android.widget.Toast;

import com.hospital.checkup.base.BaseApplication;
import com.hospital.checkup.listener.IWebJsLisenter;

import java.util.logging.Handler;

public class JsRequstInterface {
    IWebJsLisenter iWebJsLisenter;
    private WebView webView;
    Activity activity;

    public JsRequstInterface(Activity activity, WebView webView, IWebJsLisenter iWebJsLisenter) {
        this.activity = activity;
        this.webView = webView;
        this.iWebJsLisenter = iWebJsLisenter;
    }

    @android.webkit.JavascriptInterface
    public void requestAllContact() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                iWebJsLisenter.requstAllContacts();
            }
        });
    }

    @android.webkit.JavascriptInterface
    public void logout(String str) {
        Toast.makeText(BaseApplication.applicationContext, "------"+str, Toast.LENGTH_SHORT).show();
        webView.loadUrl("javascript:javacalljswith('1111')");
    }

    @android.webkit.JavascriptInterface
    public void addDoctor(String str) {
        Toast.makeText(BaseApplication.applicationContext, "------"+str, Toast.LENGTH_SHORT).show();
        webView.loadUrl("javascript:javacalljswith('啦啦啦啦')");
    }

    @android.webkit.JavascriptInterface
    public void openAddressBook() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                iWebJsLisenter.openContacts("selectedContact");
            }
        });
    }

    @android.webkit.JavascriptInterface
    public void refresh() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                iWebJsLisenter.onRefreshWebUrl();
            }
        });
    }

    /**
     * @param param
     */
    @android.webkit.JavascriptInterface
    public void goMyAuthentication(String param) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.finish();
            }
        });
    }
}