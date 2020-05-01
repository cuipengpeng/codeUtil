package com.jfbank.qualitymarket.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.jfbank.qualitymarket.activity.CategoryActivity;
import com.jfbank.qualitymarket.activity.MainActivity;
import com.jfbank.qualitymarket.activity.OrderDetailActivity;
import com.jfbank.qualitymarket.activity.WebViewActivity;
import com.jfbank.qualitymarket.fragment.MyAccountFragment;
import com.jfbank.qualitymarket.fragment.MyOrderFragment;
import com.jfbank.qualitymarket.model.JpushMessageBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * 
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {
	private static final String TAG = "JPush";
	public static int num = 0;
	RecyclerView view;
	@Override
	public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
		Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));
		
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
            //send the Registration Id to your server...
                        
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
        	Log.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
        	processCustomMessage(context, bundle);
        
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);
			//AppShortCutUtil.generatorNumIcon2(context,null,true,num+"");
		} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户点击打开了通知");
			explainJson(context,bundle.getString(JPushInterface.EXTRA_EXTRA));
        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

        } else if(JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
        	boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
        	Log.w(TAG, "[MyReceiver]" + intent.getAction() +" connected state change to "+connected);
        } else {
        	Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
        }
	}

	/**
	 * 解释json数据
	 * @param context
	 * @param json
	 */
	private void explainJson(Context context, String json) {
		//打开自定义的Activity
		Intent intent = new Intent();
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		//intent.setFlags(/*Intent.FLAG_ACTIVITY_NEW_TASK |*/ Intent.FLAG_ACTIVITY_CLEAR_TOP );

		if ("".equals(json) || null == json){
			intent.setClass(context,MainActivity.class);
			context.startActivity(intent);
			return;
		}
		Log.e("TAG",json);
		JpushMessageBean bean = JSON.parseObject(json, JpushMessageBean.class);
		if ("orderdetail".equalsIgnoreCase(bean.getGoview())) {					//订单详情页面
			intent.setClass(context,OrderDetailActivity.class);
			intent.putExtra(MyOrderFragment.KEY_OF_ORDER_ID, bean.getOrderId());
		}else if ("mycenter".equalsIgnoreCase(bean.getGoview())){				//个人中心页面
			Log.e("TAG","1111111111111111111");
			intent.setClass(context,MainActivity.class);
			intent.putExtra(MainActivity.KEY_OF_BOTTOM_MENU, MyAccountFragment.TAG);
		}else if("WEBONLY".equalsIgnoreCase(bean.getGoview())){					//web页面
			intent.setClass(context, WebViewActivity.class);
			intent.putExtra(WebViewActivity.KEY_OF_HTML_URL,bean.getUrl());
			intent.putExtra(WebViewActivity.KEY_OF_HTML_TITLE,"万卡商城");
		}else if("CLASSIFY".equalsIgnoreCase(bean.getGoview())){				//品类页面
			intent.setClass(context,CategoryActivity.class);
			intent.putExtra("upCategoroyType",bean.getClassifyID());
		}else{
			intent.setClass(context,MainActivity.class);
		}
		context.startActivity(intent);
	}

	// 打印所有的 intent extra 数据
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
			}else if(key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)){
				sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
			} else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
				if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
					Log.i(TAG, "This message has no Extra data");
					continue;
				}

				try {
					JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
					Iterator<String> it =  json.keys();

					while (it.hasNext()) {
						String myKey = it.next().toString();
						sb.append("\nkey:" + key + ", value: [" +
								myKey + " - " +json.optString(myKey) + "]");
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
	
	//send msg to MainActivity
	private void processCustomMessage(Context context, Bundle bundle) {
		String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
		Log.e("TAG",extras);
	/*	if (MainActivity.isForeground) {
			String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
			String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
			Intent msgIntent = new Intent(MainActivity.MESSAGE_RECEIVED_ACTION);
			msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
			if (!ExampleUtil.isEmpty(extras)) {
				try {
					JSONObject extraJson = new JSONObject(extras);
					if (extraJson.length() > 0) {
						msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras);
					}
				} catch (JSONException e) {

				}

			}
			context.sendBroadcast(msgIntent);
		}*/
	}


}
