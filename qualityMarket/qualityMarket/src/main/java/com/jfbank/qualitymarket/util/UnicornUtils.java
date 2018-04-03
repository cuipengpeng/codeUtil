package com.jfbank.qualitymarket.util;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.jfbank.qualitymarket.AppContext;
import com.jfbank.qualitymarket.activity.LoginActivity;
import com.jfbank.qualitymarket.activity.MainActivity;
import com.jfbank.qualitymarket.callback.AsyncResponseCallBack;
import com.jfbank.qualitymarket.fragment.MyAccountFragment;
import com.jfbank.qualitymarket.model.MyAuthenticationBean;
import com.jfbank.qualitymarket.model.UnicornUserInfoBean;
import com.jfbank.qualitymarket.net.HttpRequest;
import com.jfbank.qualitymarket.widget.LoadingAlertDialog;
import com.qiyukf.unicorn.api.ConsultSource;
import com.qiyukf.unicorn.api.SavePowerConfig;
import com.qiyukf.unicorn.api.StatusBarNotificationConfig;
import com.qiyukf.unicorn.api.Unicorn;
import com.qiyukf.unicorn.api.YSFOptions;
import com.qiyukf.unicorn.api.YSFUserInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 功能：七鱼客服Utils<br>
 * 作者：赵海<br>
 * 时间： 2017/4/1 0001<br>.
 * 版本：1.2.0
 */

public class UnicornUtils {
    /**
     * 如果返回值为null，则全部使用默认参数。
     *
     * @return
     */
    public static YSFOptions options() {
        YSFOptions options = new YSFOptions();
        options.statusBarNotificationConfig = new StatusBarNotificationConfig();
        options.savePowerConfig = new SavePowerConfig();
        options.statusBarNotificationConfig.notificationEntrance= MainActivity.class;
        return options;
    }

    /**
     * 跳转到登录界面
     * @param context
     */
    public static void startUnicorn(final Activity context) {
        if (AppContext.isLogin&&AppContext.user!=null){
            final    LoadingAlertDialog     mDialog = new LoadingAlertDialog(context);
            mDialog.show(ConstantsUtil.NETWORK_REQUEST_IN);

            Map<String,String> params = new HashMap<>();

            params.put("uid", AppContext.user.getUid());
            params.put("token", AppContext.user.getToken());

            params.put("ver", AppContext.getAppVersionName(context));
            params.put("Plat", ConstantsUtil.PLAT);

            Log.e("TAG", params.toString());

            HttpRequest.myAuthentication(context,params, new AsyncResponseCallBack() {

                @Override
                public void onResult(String arg2) {
                    if (mDialog.isShowing()) {
                        mDialog.dismiss();
                    }
                    if (null != arg2 && arg2.length() > 0) {
                        Log.e("TAG", ")))" + new String(arg2));
                       startChat (context,new String(arg2));
                    }else{
                        Toast.makeText(context, ConstantsUtil.ORIGIN_PAGE_FAIL_TO_CONNECT_SERVER, Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailed(String path, String msg) {
                    if (mDialog.isShowing()) {
                        mDialog.dismiss();
                    }
                    Toast.makeText(context, ConstantsUtil.ORIGIN_PAGE_FAIL_TO_CONNECT_SERVER, Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Intent loginIntent = new Intent(context, LoginActivity.class);
            loginIntent.putExtra(LoginActivity.KEY_OF_COME_FROM, MyAccountFragment.TAG);
            context.startActivity(loginIntent);
        }
    }

    /**
     * 跳转
     * @param json
     */
    protected static void startChat(Activity context, String json) {
        try {
            MyAuthenticationBean bean = JSON.parseObject(json, MyAuthenticationBean.class);
            if (null != bean && ConstantsUtil.RESPONSE_SUCCEED == Integer.parseInt(bean.getStatus())) {
                ArrayList<UnicornUserInfoBean>dataLogin=new ArrayList<>();
                UnicornUserInfoBean userName=new UnicornUserInfoBean();
                String userNameStr=AppContext.user.getIdName();
                if (TextUtils.isEmpty(userNameStr)){
                    userNameStr="未实名用户";
                }
                userName.setValue(userNameStr);
                userName.setKey("real_name");
                userName.setHidden(false);
                dataLogin.add(userName);

                UnicornUserInfoBean userPhone=new UnicornUserInfoBean();
                String userPhoneStr=AppContext.user.getMobile();
                userPhone.setValue(userPhoneStr);
                userPhone.setKey("mobile_phone");
                userPhone.setHidden(false);
                dataLogin.add(userPhone);

                UnicornUserInfoBean userEmial=new UnicornUserInfoBean();
                userEmial.setValue("");
                userEmial.setKey("email");
                userEmial.setHidden(true);
                dataLogin.add(userEmial);
              String  mBseInfoAuthenticationStatus = bean.getData().getStatus();
                if (!"editing".equals(mBseInfoAuthenticationStatus)||TextUtils.isEmpty(mBseInfoAuthenticationStatus)) {                //只要不是editing状态



                    if ("scorePassed".equals(bean.getData().getStatus()) || "auditPassed".equals(bean.getData().getStatus())) {
                        //已通过
                        UnicornUserInfoBean userMyAccount=new UnicornUserInfoBean();
                        userMyAccount.setValue("已认证");
                        userMyAccount.setKey("authen_realname");
                        userMyAccount.setLabel("实名认证");
                        userMyAccount.setIndex(0);
                        dataLogin.add(userMyAccount);

                        UnicornUserInfoBean userInfo=new UnicornUserInfoBean();
                        userInfo.setValue("已认证");
                        userInfo.setKey("person_info");
                        userInfo.setLabel("个人资料");
                        userInfo.setIndex(1);
                        dataLogin.add(userInfo);

                        UnicornUserInfoBean userOperator=new UnicornUserInfoBean();
                        userOperator.setValue("已授权");
                        userOperator.setKey("operator");
                        userOperator.setLabel("运营商授权");
                        userOperator.setIndex(2);
                        dataLogin.add(userOperator);
                    }

                    if (!"".equals(bean.getData().getSesameStatus())&&Integer.parseInt(bean.getData().getSesameStatus())==7) {
                        UnicornUserInfoBean userSesame=new UnicornUserInfoBean();
                        userSesame.setValue("已授权");
                        userSesame.setKey("sesame");
                        userSesame.setLabel("芝麻授权");
                        userSesame.setIndex(3);
                        dataLogin.add(userSesame);
                    }
                    if (!"".equals(bean.getData().getShebaoStatus())&&Integer.parseInt(bean.getData().getShebaoStatus())==7) {
                        UnicornUserInfoBean userSocialSecurity=new UnicornUserInfoBean();
                        userSocialSecurity.setValue("已授权");
                        userSocialSecurity.setKey("social_security");
                        userSocialSecurity.setLabel("社保授权");
                        userSocialSecurity.setIndex(4);
                        dataLogin.add(userSocialSecurity);
                    }
                }
                YSFUserInfo userInfo = new YSFUserInfo();
                userInfo.userId = AppContext.user.getUid();
//                userInfo.authToken = AppContext.user.getToken();
                userInfo.data=JSONArray.toJSONString(dataLogin);
                Unicorn.setUserInfo(userInfo);
                String title = "万卡商城在线客服";
                /**
                 * 设置访客来源，标识访客是从哪个页面发起咨询的，
                 * 用于客服了解用户是从什么页面进入三个参数分别为
                 * 来源页面的url，来源页面标题，来源页面额外信息（可自由定义）。
                 * 设置来源后，在客服会话界面的"用户资料"栏的页面项，可以看到这里设置的值。
                 */
                ConsultSource source = new ConsultSource("", title, "");
                /**
                 * 请注意： 调用该接口前，应先检查Unicorn.isServiceAvailable()，
                 * 如果返回为false，该接口不会有任何动作
                 *
                 * @param context 上下文
                 * @param title   聊天窗口的标题
                 * @param source  咨询的发起来源，包括发起咨询的url，title，描述信息等
                 */
                Unicorn.openServiceActivity(context, title, source);
            } else if (ConstantsUtil.RESPONSE_TOKEN_FAIL == Integer.parseInt(bean.getStatus())) {
                UserUtils.tokenFailDialog(context,bean.getStatusDetail(),null);
            } else {
                Toast.makeText(context, bean.getStatusDetail(), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
