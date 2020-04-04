package com.caishi.chaoge.bdopen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.bytedance.sdk.account.common.api.BDApiEventHandler;
import com.bytedance.sdk.account.common.model.BaseReq;
import com.bytedance.sdk.account.common.model.BaseResp;
import com.bytedance.sdk.account.open.aweme.DYOpenConstants;
import com.bytedance.sdk.account.open.aweme.api.TTOpenApi;
import com.bytedance.sdk.account.open.aweme.impl.TTOpenApiFactory;
import com.bytedance.sdk.account.open.aweme.share.Share;

public class BdEntryActivity extends Activity implements BDApiEventHandler {

    TTOpenApi ttOpenApi;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ttOpenApi= TTOpenApiFactory.create(this);

        ttOpenApi.handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq req) {

    }

    @Override
    public void onResp(BaseResp resp) {
        if (resp.getType() == DYOpenConstants.ModeType.SHARE_CONTENT_TO_DY_RESP) {
            Share.Response response = (Share.Response) resp;
            // errorcode在DYOpenConstants中定义
//            Toast.makeText(this, response.errorCode + "", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onErrorIntent(@Nullable Intent intent) {
        // 错误数据
    }
}