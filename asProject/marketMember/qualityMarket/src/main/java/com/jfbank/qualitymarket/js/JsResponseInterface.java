package com.jfbank.qualitymarket.js;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.jfbank.qualitymarket.AppContext;
import com.jfbank.qualitymarket.callback.AsyncResponseCallBack;
import com.jfbank.qualitymarket.listener.DialogListener;
import com.jfbank.qualitymarket.model.ContactBean;
import com.jfbank.qualitymarket.net.HttpRequest;
import com.jfbank.qualitymarket.net.NetUtil;
import com.jfbank.qualitymarket.util.CommonUtils;
import com.jfbank.qualitymarket.util.ConstantsUtil;
import com.jfbank.qualitymarket.util.DialogUtils;
import com.jfbank.qualitymarket.util.FaceDetectUtil;
import com.jfbank.qualitymarket.util.PhoneTools;
import com.jfbank.qualitymarket.util.ToastUtil;
import com.jfbank.qualitymarket.util.UserUtils;
import com.jfbank.qualitymarket.widget.LoadingAlertDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;

/**
 * 功能：Android(Java)调用网页H5方法<br>
 * 作者：赵海<br>
 * 时间： 2017/2/17 0017<br>.
 * 版本：1.2.5
 */

public class JsResponseInterface {
    Activity mActivity;//当前上下文
    WebView mWebView;//当前webView

    public JsResponseInterface(Activity mActivity, WebView mWebView) {
        this.mActivity = mActivity;
        this.mWebView = mWebView;
    }

    /**
     * 上传通讯录
     */
    public void responseAllContact() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                List<ContactBean> contactList = new ArrayList<ContactBean>();
                List<ContactBean> phoneList = PhoneTools.getContactPhone(mActivity);  //手机通讯录有数据
                if (!CommonUtils.isEmptyList(phoneList)) {
                    for (ContactBean contactBean : phoneList) { // 遍历手机通讯录
                        contactList.add(contactBean);
                    }
                }
                List<ContactBean> simSimContractList = PhoneTools.getSimContracts(mActivity);//遍历sim卡通讯录
                if (!CommonUtils.isEmptyList(simSimContractList)) {
                    for (com.jfbank.qualitymarket.model.ContactBean contactBean : com.jfbank.qualitymarket.util.PhoneTools.getSimContracts(mActivity)) { // 遍历sim
                        if (contactList.contains(contactBean)) {
                            continue;
                        }
                        contactList.add(contactBean);
                    }
                }
                String json = com.alibaba.fastjson.JSONArray.toJSONString(contactList);
                mWebView.loadUrl("javascript: responseAllContact('" + json + "')");
            }
        });
    }

    /**
     * 拍照回显
     *
     * @param picBase64String
     */
    public void jsHandlerCamera(final String picBase64String) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (null == picBase64String) {
                    ToastUtil.show("图片上传失败，请稍后重试");
                    return;
                }
                JSONObject object = new JSONObject();
                try {
                    object.put("image", picBase64String);
                    mWebView.loadUrl("javascript: jsHandlerCamera(" + object.toString() + ")");
                } catch (JSONException e) {
                    Toast.makeText(mActivity, "图片上传失败，请稍后重试", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 选择号码联系人
     */
    public void selectedContact(final Intent data, final String requestName) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ContactBean contactBean = PhoneTools.getPhoneContact(mActivity, data);
                if (contactBean != null) {
                    if (TextUtils.isEmpty(contactBean.getMobilePhone())) {
                        Toast.makeText(mActivity, "该联系人手机号为空", Toast.LENGTH_SHORT).show();
                    } else {
                        if (CommonUtils.isMobilePhoneVerify(contactBean.getMobilePhone())) {
                            if (TextUtils.equals(requestName, "selectedContact")) {
                                mWebView.loadUrl("javascript: selectedContact('" + JSONObject.toJSON(contactBean)
                                        + "')");
                            } else if (TextUtils.equals(requestName, "getContactInfo")) {
                                mWebView.loadUrl("javascript: getContactInfo('" + contactBean.getContactName() + "','" + contactBean.getMobilePhone()
                                        + "')");
                            }

                        } else {
                            Toast.makeText(mActivity, "请选择正确格式的手机号", Toast.LENGTH_SHORT).show();
                        }

                    }
                } else {
                    Toast.makeText(mActivity, "请到[设置]手动开启读取通讯录权限", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 人脸识别回调
     *
     * @param path
     */
    public void jsHandlerFunc(String path) {
        if (FaceDetectUtil.getImageListName(path) != null && FaceDetectUtil.getImageListName(path).size() == 4) {
            final Map<String, String> header = new HashMap<>();
            header.put("token", AppContext.user.getToken());
            header.put("deviceType", "android");
            header.put("proId", "pzsc893e3c86b26eb1b10a858a47db45");
            header.put("version", "1.0");
            header.put("t", System.currentTimeMillis() + "");
            Map<String, File> postFormBuilder = new HashMap<>();
            for (int i = 0; i < 4; i++) {
                postFormBuilder.put("imgfile" + (i + 1), FaceDetectUtil.getImageLisFile(path).get(i));
            }
            final LoadingAlertDialog mDialogProgress = new LoadingAlertDialog(mActivity);
            final Call<String> call = HttpRequest.uploadFileWithHeader(mActivity, HttpRequest.ONCARD_API_FACE_IDENTIFY, header, NetUtil.toParamsFiles(postFormBuilder, "multipart/form-data"), new AsyncResponseCallBack() {
                @Override
                public void onResult(String responseStr) {
                    mDialogProgress.dismiss();
                    try {
                        JSONObject jsonObject = JSON.parseObject(responseStr);
                        int status = jsonObject.getIntValue("status");
                        if (status == 1) {
                            mWebView.loadUrl("javascript: jsHandlerFunc('" + 1 + "')");
                        }
//                        else if (status == 2 || status == 3 || status == 4) {
//                            mWebView.loadUrl("javascript: jsHandlerFunc('" + 0 + "')");
//                        }
                        else if (status == 1008) {//失效，请重新登录
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    UserUtils.tokenFailDialog(mActivity, "Token失效，请重新登录", null);
                                }
                            });
                        } else {
                            DialogUtils.showTwoBtnDialog(mActivity, "提示", "人脸识别失败，不要灰心\n你可以再次尝试或者上传身份证照片", "上传身份证", "再次尝试", new DialogListener.DialogClickLisenter() {
                                @Override
                                public void onDialogClick(int type) {
                                    if (type == CLICK_CANCEL) {
                                        mWebView.loadUrl("javascript: jsHandlerFunc('" + 0 + "')");
                                    } else if (type == CLICK_SURE) {
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                JsRequstInterface.jsHandlerFunc(mActivity);
                                            }
                                        }, 100);

                                    }
                                }
                            });
                        }
                    } catch (Exception e) {
                        Log.e("人脸识别", e.getMessage());
                    }
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    mDialogProgress.dismiss();
                }

                @Override
                public void onFailed(String path, String msg) {
                    super.onFailed(path, msg);
                    ToastUtil.show(ConstantsUtil.ORIGIN_PAGE_FAIL_TO_CONNECT_SERVER);
                }
            });
            mDialogProgress.show("正在进行识别验证");
            mDialogProgress.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    if (call != null&& !call.isCanceled()) {
                        call.cancel();
                    }
                }
            });
        }
    }
}
