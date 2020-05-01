package com.jfbank.qualitymarket.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfbank.qualitymarket.listener.DialogListener;
import com.jfbank.qualitymarket.net.HttpRequest;
import com.jfbank.qualitymarket.callback.AsyncResponseCallBack;
 import java.util.HashMap; import java.util.Map;

import org.apache.http.Header;

/**
 * 功能：软件更新工具类<br>
 * 作者：赵海<br>
 * 时间： 2017/3/21 0021<br>.
 * 版本：1.2.0
 */

public class UpdateVersionUtils {
    public final static  String ApkFileMimeType="application/vnd.android.package-archive";
    /**
     * 更行版本
     *
     * @param activity   当前activity
     * @param jsonObject 更新响应的json字符串
     */
    public static void updateVersion(final Activity activity, JSONObject jsonObject) {
        // 0无最新版本,1有最新版本不需要更新,2有最新版本,强制更新
        int code = jsonObject.getIntValue("code");
        final String downLoadUrl = jsonObject.getString("url");
        final String newVersion = jsonObject.getString("newVersion");
        String message = jsonObject.getString("message");
        if (code == 1) {
            DialogUtils.showTwoBtnDialog(activity, true, null, message + newVersion, "取消", "更新", new DialogListener.DialogClickLisenter() {
                @Override
                public void onDialogClick(int type) {
                    if (type == CLICK_SURE) {
                        downloadApk(activity, downLoadUrl, newVersion);
                    } else {

                    }
                }
            });
        } else if (code == 2) {
            DialogUtils.showOneBtnDialog(activity, false, null, message + newVersion, "更新", new DialogListener.DialogClickLisenter() {
                @Override
                public void onDialogClick(int type) {
                    downloadApk(activity, downLoadUrl, newVersion);
                }
            });
        }
    }

    /**
     * make true current connect service is wifi
     *
     * @param mContext
     * @return
     */
    private static boolean isWifi(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    /**
     * 软件升级下载
     *
     * @param activity
     * @param downLoadUrl
     * @param newVersion
     */
    public static void downloadApk(Activity activity, String downLoadUrl, String newVersion) {
        String fileName = "PingZhiShangCheng_V" + newVersion.replaceAll(".", "_") + ".apk";
        DownloadFileUtil.downloadApk(activity, downLoadUrl, "万卡商城", "V_" + newVersion + "更新安装包", ApkFileMimeType, fileName);
    }

    /**
     * 打开浏览器更新新版本
     *
     * @param activity
     * @param downLoadUrl
     */

    private static void openBrowser(final Activity activity, final String downLoadUrl) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(downLoadUrl);
        intent.setData(content_url);
        activity.startActivity(intent);
    }

    /**
     * 检查版本更新
     */
    public static void checkVersionUpdate(final Activity activity) {
        Map<String,String> params = new HashMap<>();
        HttpRequest.post(activity, HttpRequest.QUALITY_MARKET_WEB_URL + HttpRequest.CHECK_VERSION_UPDATE, params,
                new AsyncResponseCallBack() {

                    @Override
                    public void onResult(String arg2) {
                        String jsonStr = new String(arg2);
                        LogUtil.printLog("检查版本更新：" + jsonStr);

                        JSONObject jsonObject = JSON.parseObject(jsonStr);

                        if (ConstantsUtil.RESPONSE_SUCCEED == jsonObject
                                .getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
                            updateVersion(activity, jsonObject);
                        } else if (ConstantsUtil.RESPONSE_TOKEN_FAIL == jsonObject
                                .getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
                        } else {

                        }
                    }

                    @Override
                    public void onFailed(String path, String msg) {

                    }
                });
    }
}
