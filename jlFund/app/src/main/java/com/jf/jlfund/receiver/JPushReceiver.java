package com.jf.jlfund.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.jf.jlfund.adapter.TradeRecordListAdapter;
import com.jf.jlfund.utils.LogUtils;
import com.jf.jlfund.utils.SPUtil;
import com.jf.jlfund.view.activity.FundTradeRecordDetailActivity;
import com.jf.jlfund.view.activity.LoginActivity;
import com.jf.jlfund.view.activity.MainActivity;
import com.jf.jlfund.view.activity.PutInResultActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * <p>
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class JPushReceiver extends BroadcastReceiver {
    private static final String TAG = "JPush";
    private String serverToken = "";
    private String tradeno = "";
    private String showModule = "";
    private String tradetype = "";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Log.e(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));
        praseIntentExtras(bundle);
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
            //send the Registration Id to your server...
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息【》》》》请求补丁《《《《】: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
//            String localToken = SPUtil.getInstance().getToken();      //该块代码是解决当设备A中的账户在设备B登录时发送推送两个设备均能收到的问题。
//            Log.e(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId + " \n localToken: " + SPUtil.getInstance().getToken() + " serverToken: " + serverToken);
//            if (!localToken.equals(serverToken)) {
//                Log.e(TAG, "onReceive: token失效，不显示通知。。。。");
//                NotificationManager manager = (NotificationManager) BaseApplication.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
//                manager.cancel(notifactionId);
//            } else {
//                Log.e(TAG, "onReceive: token不失效，显示通知，");
//            }
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.e(TAG, "[MyReceiver] 用户点击打开了通知");
            clickToOpenActivity(context);
        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

        } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
            boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            Log.w(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
        } else {
            Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
        }
    }

    private void praseIntentExtras(Bundle bundle) {
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
                    Log.i(TAG, "This message has no Extra data");
                    continue;
                }

                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();
                    while (it.hasNext()) {
                        String myKey = it.next().toString();
                        if (myKey.equals("tradeno")) {
                            tradeno = json.optString(myKey);
                        } else if (myKey.equals("showModule")) {
                            showModule = json.optString(myKey);
                        } else if (myKey.equals("tradetype")) {
                            tradetype = json.optString(myKey);
                        } else if (myKey.equals("serverToken")) {
                            serverToken = json.optString(myKey);
                        }
                    }
                    LogUtils.e("praseIntentExtras --> showModule:" + showModule + " -- tradeno: " + tradeno + " -- tradetype: " + tradetype + " -- " + serverToken);
                } catch (JSONException e) {
                    Log.e(TAG, "Get message extra JSON error!");
                }
            }
        }
    }

    private void clickToOpenActivity(final Context context) {
        if (!TextUtils.isEmpty(tradeno) && !TextUtils.isEmpty(showModule) && !TextUtils.isEmpty(tradetype)) {
            if (!SPUtil.getInstance().isLogin()) {
                LoginActivity.open(context);
                return;
            }
            if (showModule.equals("1")) {     //跳转到基金交易详情
                Intent intent = new Intent(context, FundTradeRecordDetailActivity.class);
                intent.putExtra(FundTradeRecordDetailActivity.PARAM_TRADE_NO, tradeno);
                intent.putExtra(FundTradeRecordDetailActivity.PARAM_IS_AFTER_BUY_IN, true);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } else if (showModule.equals("2")) {   //跳转到活期+交易详情
                Intent intent = new Intent(context, PutInResultActivity.class);
                intent.putExtra(TradeRecordListAdapter.KEY_OF_TRADE_RECORD_NUMBER, tradeno);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        } else {
            Intent intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
                    Log.i(TAG, "This message has no Extra data");
                    continue;
                }
                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();
                    while (it.hasNext()) {
                        String myKey = it.next().toString();
                        sb.append("\nkey:" + key + ", value: [" +
                                myKey + " - " + json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Get message extra JSON error!");
                }

            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }
}
