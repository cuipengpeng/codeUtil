package com.test.xcamera.tiktokapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.bytedance.sdk.open.aweme.TikTokConstants;
import com.bytedance.sdk.open.aweme.TikTokOpenApiFactory;
import com.bytedance.sdk.open.aweme.api.TikTokApiEventHandler;
import com.bytedance.sdk.open.aweme.api.TiktokOpenApi;
import com.bytedance.sdk.open.aweme.authorize.model.Authorization;
import com.bytedance.sdk.open.aweme.common.model.BaseReq;
import com.bytedance.sdk.open.aweme.common.model.BaseResp;
import com.bytedance.sdk.open.aweme.share.Share;
import com.editvideo.ToastUtil;
import com.test.xcamera.R;
import com.test.xcamera.application.AiCameraApplication;

/**
 * 主要功能：接受授权返回结果的activity
 * <p>
 * 注：该activity必须在程序包名下 bdopen包下定义
 * <p>
 * 也可通过request.callerLocalEntry = "com.xxx.xxx...activity"; 定义自己的回调类
 */
public class TikTokEntryActivity extends Activity implements TikTokApiEventHandler {

    TiktokOpenApi ttOpenApi;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ttOpenApi = TikTokOpenApiFactory.create(this, TikTokConstants.TARGET_APP.AWEME);
        ttOpenApi.handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq req) {

    }

    @Override
    public void onResp(BaseResp resp) {
        if (resp.getType() == TikTokConstants.ModeType.SHARE_CONTENT_TO_TT_RESP) {
            Share.Response response = (Share.Response) resp;
            if (response.subErrorCode == 20007) {
                ToastUtil.showToast(AiCameraApplication.getContext(), R.string.dy_error_20007);
            } else if (response.subErrorCode == 20008) {
                ToastUtil.showToast(AiCameraApplication.getContext(), R.string.dy_error_20008);
            } else if (response.subErrorCode == 20010) {
                ToastUtil.showToast(AiCameraApplication.getContext(), R.string.dy_error_20010);
            } else if (response.subErrorCode == 20011) {
                ToastUtil.showToast(AiCameraApplication.getContext(), R.string.dy_error_20011);
            } else if (response.subErrorCode == 20012) {
                ToastUtil.showToast(AiCameraApplication.getContext(), R.string.dy_error_20012);
            } else if (response.subErrorCode == 20013) {
                ToastUtil.showToast(AiCameraApplication.getContext(), R.string.dy_error_20013);
            } else if (response.subErrorCode == 20015) {
                ToastUtil.showToast(AiCameraApplication.getContext(), R.string.dy_error_20015);
            } else if (response.subErrorCode == 22001) {
                ToastUtil.showToast(AiCameraApplication.getContext(), R.string.dy_error_22001);
            } else if (response.subErrorCode == -1) {
                ToastUtil.showToast(AiCameraApplication.getContext(), R.string.dy_error_net);
            }
        } else if (resp.getType() == TikTokConstants.ModeType.SEND_AUTH_RESPONSE) {
            // 授权成功可以获得authCode
            Authorization.Response response = (Authorization.Response) resp;
            Log.d("AuthResultTest", "authCode " + response.authCode);
            if (resp.isSuccess()) {
//                Toast.makeText(this, "Authorization success with permissions：" + response.grantedPermissions +
//                        ", code:" + response.authCode, Toast.LENGTH_LONG).show();
            } else {
//                Toast.makeText(this, "Authorization failed", Toast.LENGTH_LONG).show();
            }
        }
        finish();
    }

    @Override
    public void onErrorIntent(@Nullable Intent intent) {
        // 错误数据
        Toast.makeText(AiCameraApplication.getContext(), "Intent出错", Toast.LENGTH_LONG).show();
    }
}
