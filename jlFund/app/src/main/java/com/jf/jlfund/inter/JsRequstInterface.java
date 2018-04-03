package com.jf.jlfund.inter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.webkit.WebView;
import android.widget.Toast;

import com.jf.jlfund.utils.DownloadFileUtil;
import com.jf.jlfund.utils.LogUtils;
import com.jf.jlfund.view.activity.WebViewActivity;

/**
 * webview的js请求java(Android)接口类
 *
 * @author 崔朋朋
 */
public class JsRequstInterface {
    public static final String TAG = JsRequstInterface.class.getName();
    public static final String CATEGORY_ID = "categoryId";
    public static final String CLASSIFY_ID = "classifyId";
    public static final int EXTRA_QUESTCODE_FACEFUNC = 0x33;
    public static final int REQUEST_CODE_TAKE_IDCARD = 0x36;
    private String downloadUrl;
    private String downloadFileName;

    Activity mActivity;//当前上下文
    WebView mWebView;//当前webView

    public JsRequstInterface(Activity mActivity, WebView mWebView) {
        this.mActivity = mActivity;
        this.mWebView = mWebView;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    // 点击获取验证码后的倒计时任务
                    String pdfPath = DownloadFileUtil.getFile(downloadUrl, downloadFileName).getAbsolutePath();
                    Toast.makeText(mActivity, "下载文件成功：" + pdfPath, Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent("android.intent.action.VIEW");
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Uri uri = Uri.fromFile(DownloadFileUtil.getFile(downloadUrl, downloadFileName));
                    intent.setDataAndType(uri, "application/pdf");
                    mActivity.startActivity(intent);
                    break;
                case 2:
                    // 发送取消订单请求
                    Toast.makeText(mActivity, "下载文件失败", Toast.LENGTH_SHORT).show();
                    break;

            }
        }

        ;
    };


    /**
     *
     * @param param 登录参数
     */
    @android.webkit.JavascriptInterface
    public void downloadFile(String param) {
        LogUtils.printLog(param);

         Message message = ((WebViewActivity)mActivity).pdfHtmlHandler.obtainMessage();
        message.what = 1;
        message.obj = param;
        ((WebViewActivity)mActivity).pdfHtmlHandler.sendMessage(message);

//        DownloadFileUtil.downFile(handler,param,new Date().getTime()+"");
    }
}