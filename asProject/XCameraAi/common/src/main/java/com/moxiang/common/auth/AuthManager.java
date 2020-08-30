package com.moxiang.common.auth;

import android.app.Activity;

import com.bytedance.sdk.open.aweme.TikTokConstants;
import com.bytedance.sdk.open.aweme.TikTokOpenApiFactory;
import com.bytedance.sdk.open.aweme.api.TiktokOpenApi;
import com.bytedance.sdk.open.aweme.authorize.model.Authorization;

/**
 * Created by admin on 2020/2/17.
 */

public class AuthManager {
    private static TiktokOpenApi bdOpenApi;
    private static String mScope = "user_info";
    public static final String CODE_KEY = "code";
    // 默认抖音
    public static int targetAppId = TikTokConstants.TARGET_APP.AWEME;
    private static AuthManager instance;
    private AuthManager() {
    }

    public static AuthManager getInstance() {
        if (instance == null) {
            synchronized (AuthManager.class) {
                if (instance == null) {
                    instance = new AuthManager();
                }
            }
        }
        return instance;
    }

    public static boolean authLoginDouyin(Activity activity) {
        bdOpenApi = TikTokOpenApiFactory.create(activity, TikTokConstants.TARGET_APP.AWEME);
        targetAppId = TikTokConstants.TARGET_APP.AWEME;
        Authorization.Request request = new Authorization.Request();
        // 用户授权时必选权限
        request.scope = mScope;
        // 用于保持请求和回调的状态，授权请求后原样带回给第三方。
        request.state = "ww";
        return bdOpenApi.authorize(request);
    }

}
