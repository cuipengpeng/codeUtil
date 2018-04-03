package com.jf.jlfund.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.jf.jlfund.base.BaseApplication;

import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;


/**
 * 网络工具类
 */
public class NetUtil {


    /**
     * 判断网络是否连接
     */
    public static boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) BaseApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isAvailable();
    }

    /**
     * 判断是否是wifi网络
     */
    public static boolean isNetworkWifi() {

        ConnectivityManager cm = (ConnectivityManager) BaseApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (null != info && info.getType() == ConnectivityManager.TYPE_WIFI)
            return true;
        return false;
    }

    /**
     * 停止推送服务
     * <p/>
     * 调用了本 API 后，JPush 推送服务完全被停止。具体表现为：
     * JPush Service 不在后台运行
     * 收不到推送消息
     * 极光推送所有的其他 API 调用都无效,不能通过 JPushInterface.init 恢复，需要调用resumePush恢复。
     */
    public static void stopPushService() {
        JPushInterface.stopPush(BaseApplication.getContext());
    }

    /**
     * 恢复推送服务
     * <p/>
     * 调用了此 API 后，极光推送完全恢复正常工作。
     */
    public static void resumePushService() {
        JPushInterface.resumePush(BaseApplication.getContext());
    }

    public static void setJPushAliasAndTags(String tagOrAlias) {
        LogUtils.e("setJPushAliasAndTags......");
        setJPushAliasAndTags(tagOrAlias, 0);
    }

    public static void setJPushAliasAndTags(final String tagOrAlias, final int reSetCount) {
        LogUtils.e("setJPushAliasAndTags......tagOrAlias: " + tagOrAlias + " reSetCount: " + reSetCount);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                LogUtils.e("setJPushAliasAndTags --> isUIThread:" + Looper.getMainLooper().isCurrentThread());
                //由于网络连接不稳定的原因，有一定的概率 JPush SDK 设置别名与标签会失败。 失败后会进行重试【重试间隔：1s，重试次数：3次】
                JPushInterface.setAliasAndTags(BaseApplication.getContext(), tagOrAlias, null, new TagAliasCallback() {
                    /**
                     * @param responseCode  0 表示调用成功。  其他返回码请参考错误码定义。
                     * @param alias 原设置的别名
                     * @param tags  原设置的d标签
                     */
                    @Override
                    public void gotResult(int responseCode, String alias, Set<String> tags) {
                        LogUtils.d("JPushInterface.getResult().response: " + responseCode);
                        //如果登陆失败尝试再次登陆3次
                        if (reSetCount > 3) {
                            return;
                        }
                        if (responseCode != 0) {
                            setJPushAliasAndTags(tagOrAlias, reSetCount + 1);
                        } else {
                            String registerId = JPushInterface.getRegistrationID(BaseApplication.getContext());
                            LogUtils.e("registerId: " + registerId);
                        }
                        if (!TextUtils.isEmpty(alias)) {
                            LogUtils.e("alias ： " + alias);
                        }
                        if (tags != null) {
                            LogUtils.e(tags.toString());
                        }
                    }
                });
            }
        }, 1000);
    }
}
