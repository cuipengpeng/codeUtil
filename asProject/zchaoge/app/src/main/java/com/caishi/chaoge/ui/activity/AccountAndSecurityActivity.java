package com.caishi.chaoge.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caishi.chaoge.R;
import com.caishi.chaoge.base.BaseUILocalDataActivity;
import com.caishi.chaoge.bean.MineDataBean;
import com.caishi.chaoge.http.HttpRequest;
import com.caishi.chaoge.http.RequestURL;
import com.caishi.chaoge.utils.SPUtils;
import com.caishi.chaoge.utils.StringUtil;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareConfig;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

public class AccountAndSecurityActivity extends BaseUILocalDataActivity implements View.OnClickListener {
    @BindView(R.id.tv_accountAndSecurity_WeiXin)
    TextView tvAccountAndSecurityWeiXin;
    @BindView(R.id.rl_accountAndSecurity_bindWeiXin)
    RelativeLayout rlAccountAndSecurityBindWeiXin;
    @BindView(R.id.tv_accountAndSecurity_QQ)
    TextView tvAccountAndSecurityQQ;
    @BindView(R.id.rl_accountAndSecurity_bindQQ)
    RelativeLayout rlAccountAndSecurityBindQQ;
    @BindView(R.id.tv_accountAndSecurity_Weibo)
    TextView tvAccountAndSecurityWeibo;
    @BindView(R.id.tv_accountAndSecurity_phoneNumber)
    TextView bindPhoneNumberTextView;
    @BindView(R.id.rl_accountAndSecurity_bindWeibo)
    RelativeLayout rlAccountAndSecurityBindWeibo;

    private MineDataBean results = new MineDataBean();

    @Override
    protected String getPageTitle() {
        return "账号与安全";
    }

    @Override
    protected int getSubLayoutId() {
        return R.layout.activity_account_and_security;
    }

    @Override
    protected void initPageData() {
        rlAccountAndSecurityBindWeiXin.setOnClickListener(this);
        rlAccountAndSecurityBindQQ.setOnClickListener(this);
        rlAccountAndSecurityBindWeibo.setOnClickListener(this);
        UMShareAPI.get(this).deleteOauth(this, SHARE_MEDIA.WEIXIN, null);
        UMShareAPI.get(this).deleteOauth(this, SHARE_MEDIA.SINA, null);
        UMShareAPI.get(this).deleteOauth(this, SHARE_MEDIA.QQ, null);


        results = SPUtils.readThirdAccountBind(this, SPUtils.readCurrentLoginUserInfo(this).userId);
        MineDataBean.PartnerInfoMapBean partnerInfoMapBean = null;
        if(results != null){
            partnerInfoMapBean = results.partnerInfoMap;
            bindPhoneNumberTextView.setText(results.mobile);
        }
        if (partnerInfoMapBean == null) {
            partnerInfoMapBean = new MineDataBean.PartnerInfoMapBean();
            results.partnerInfoMap = partnerInfoMapBean;
        } else {
            if (StringUtil.notEmpty(results.partnerInfoMap.getQQ())) {
                tvAccountAndSecurityQQ.setText(results.partnerInfoMap.getQQ());
                tvAccountAndSecurityQQ.setCompoundDrawables(null, null, null, null);
                rlAccountAndSecurityBindQQ.setEnabled(false);
            }
            if (StringUtil.notEmpty(results.partnerInfoMap.getWebChat())) {
                tvAccountAndSecurityWeiXin.setText(results.partnerInfoMap.getWebChat());
                tvAccountAndSecurityWeiXin.setCompoundDrawables(null, null, null, null);
                rlAccountAndSecurityBindWeiXin.setEnabled(false);
            }
            if (StringUtil.notEmpty(results.partnerInfoMap.getWeiBo())) {
                tvAccountAndSecurityWeibo.setText(results.partnerInfoMap.getWeiBo());
                tvAccountAndSecurityWeibo.setCompoundDrawables(null, null, null, null);
                rlAccountAndSecurityBindWeibo.setEnabled(false);
            }
        }
    }

    @Override
    public void onClick(View v) {
        UMShareConfig config = new UMShareConfig();
        config.isNeedAuthOnGetUserInfo(true);
        UMShareAPI.get(AccountAndSecurityActivity.this).setShareConfig(config);

        switch (v.getId()) {
            case R.id.rl_accountAndSecurity_bindWeiXin:
                authorization(this, SHARE_MEDIA.WEIXIN);
                break;
            case R.id.rl_accountAndSecurity_bindQQ:
                authorization(this, SHARE_MEDIA.QQ);
                break;
            case R.id.rl_accountAndSecurity_bindWeibo:
                authorization(this, SHARE_MEDIA.SINA);
                break;
        }
    }

    //授权
    public void authorization(final Activity activity, SHARE_MEDIA share_media) {
        UMShareAPI.get(activity).getPlatformInfo(activity, share_media, new UMAuthListener() {
            @Override
            public void onStart(SHARE_MEDIA share_media) {
            }

            @Override
            public void onComplete(final SHARE_MEDIA share_media, int i, final Map<String, String> map) {
//                for (Map.Entry<String, String> entry : map.entrySet()) {
//                    LogUtil.printLog(entry.getKey() + "=" + entry.getValue());
//                }

                Map<String, String> paramMap = new HashMap<>();
                paramMap.put("accessToken", map.get("accessToken"));
                paramMap.put("expireTime", map.get("expiration"));
                paramMap.put("nickName", map.get("name"));
                paramMap.put("openId", map.get("openid"));
                String typeId = "00";
                if (share_media == SHARE_MEDIA.QQ) {
                    typeId = "01";
                } else if (share_media == SHARE_MEDIA.WEIXIN) {
                    typeId = "02";
                } else if (share_media == SHARE_MEDIA.SINA) {
                    typeId = "03";
                }
                paramMap.put("partnerTypeId", typeId);
                paramMap.put("partnerUId", map.get("uid"));
                paramMap.put("portrait", map.get("iconurl"));
                paramMap.put("refreshToken", map.get("refresh_token") != null ? map.get("refresh_token") : "");
                //TODO  scopeType测试时可不传，服务器端有默认值
//                paramMap.put("scopeType", "ChaoGe");
                paramMap.put("sessionKey", "");
                paramMap.put("sex", map.get("gender"));

                HttpRequest.post(false, HttpRequest.APP_INTERFACE_WEB_URL + RequestURL.THIRD_PARTER_BIND, paramMap, new HttpRequest.HttpResponseCallBank() {
                    @Override
                    public void onSuccess(String response) {
                        if (share_media == SHARE_MEDIA.QQ) {
                            tvAccountAndSecurityQQ.setText(map.get("name"));
                            tvAccountAndSecurityQQ.setCompoundDrawables(null, null, null, null);
                            rlAccountAndSecurityBindQQ.setEnabled(false);
                            results.partnerInfoMap.setQQ(map.get("name"));
                        } else if (share_media == SHARE_MEDIA.WEIXIN) {
                            tvAccountAndSecurityWeiXin.setText(map.get("name"));
                            tvAccountAndSecurityWeiXin.setCompoundDrawables(null, null, null, null);
                            rlAccountAndSecurityBindWeiXin.setEnabled(false);
                            results.partnerInfoMap.setWebChat(map.get("name"));
                        } else if (share_media == SHARE_MEDIA.SINA) {
                            tvAccountAndSecurityWeibo.setText(map.get("name"));
                            tvAccountAndSecurityWeibo.setCompoundDrawables(null, null, null, null);
                            rlAccountAndSecurityBindWeibo.setEnabled(false);
                            results.partnerInfoMap.setWeiBo(map.get("name"));
                        }
                        SPUtils.writeThirdAccountBind(AccountAndSecurityActivity.this, results);
                    }

                    @Override
                    public void onFailure(String t) {

                    }
                });
            }

            @Override
            public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
            }

            @Override
            public void onCancel(SHARE_MEDIA share_media, int i) {
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }
}
