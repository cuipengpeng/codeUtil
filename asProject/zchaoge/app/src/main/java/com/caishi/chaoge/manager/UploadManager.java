package com.caishi.chaoge.manager;

import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.caishi.chaoge.bean.AliStsTokenBean;
import com.caishi.chaoge.http.HttpRequest;
import com.caishi.chaoge.http.RequestURL;
import com.caishi.chaoge.listener.OnUploadFileListener;
import com.caishi.chaoge.utils.CipherUtils;
import com.caishi.chaoge.utils.JsonUtils;
import com.caishi.chaoge.utils.LogUtil;
import com.caishi.chaoge.utils.SPUtils;
import com.caishi.chaoge.utils.TimeUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 阿里云上传工具
 */
public class UploadManager {

    public static final String SP_TOKEN_NAME = "OssToken";
    public static final String SP_TOKEN_KEY = "tokenKey";
    public static final String VIDEO_TOKEN_FILENAME = "video_token.json";
    public static final String PNG = ".png";
    public static final String JPG = ".jpg";
    public static final String MP3 = ".mp3";
    public static final String MP4 = ".mp4";
    public static final String WAV = ".wav";
    public static final String PREFIX_IMAGES = "image/";
    public static final String PREFIX_AUDIO = "audio/";
    public static final String PREFIX_VIDEO = "video/";

    private FragmentActivity mContext;
    private OSS mOss;
    private AliStsTokenBean mToken = null;
    private final List<OSSAsyncTask<PutObjectResult>> mTasks = new ArrayList<>();
    private final SPUtils tokenSP;


    public UploadManager(FragmentActivity context) {
        mContext = context;
        tokenSP = new SPUtils(context, SP_TOKEN_NAME);
        String tokenJson = (String) tokenSP.get(SP_TOKEN_KEY, "");
        LogUtil.i("s===" + tokenJson);
        LogUtil.i("System.currentTimeMillis()===" + System.currentTimeMillis());
        // 从缓存中读取token
        mToken = (JsonUtils.fromJson(tokenJson, AliStsTokenBean.class));

//        if (mToken == null || System.currentTimeMillis() >= mToken.expire_time) {
//            getToken(false, null, null, null);
//        }
    }

    /**
     * 对外提供的上传文件方法
     * 先检查token
     *
     * @param objKey 上传保存路径
     * @param bytes
     */
    public void upload(final String objKey, final Body bytes, final OnUploadFileListener onUploadFileListener) {
        if (mToken == null || System.currentTimeMillis() >= mToken.expire_time) {
            // token 不存在或过期，重新获取
            getToken(true, objKey, bytes, onUploadFileListener);
        } else {
            uploadFile(objKey, bytes, onUploadFileListener);
        }
    }

    private void getToken(final boolean uploadFile, final String objKey, final Body bytes, final OnUploadFileListener onUploadFileListener) {
        Map<String, String> paramsMap = new HashMap<String, String>();
        HttpRequest.post(true, HttpRequest.APP_INTERFACE_WEB_URL + RequestURL.GET_TOKEN, paramsMap, new HttpRequest.HttpResponseCallBank() {
            @Override
            public void onSuccess(String response) {
                Gson gson = new Gson();
                AliStsTokenBean aliStsTokenBean = gson.fromJson(response, AliStsTokenBean.class);
                tokenSP.put(SP_TOKEN_KEY, JsonUtils.toJson(aliStsTokenBean));
                mToken = aliStsTokenBean;
                if(uploadFile){
                    uploadFile(objKey, bytes, onUploadFileListener);
                }
            }

            @Override
            public void onFailure(String t) {

            }
        });
    }

    /**
     * 上传文件
     *
     * @param objKey 文件路径
     * @param body
     */
    private void uploadFile(final String objKey, Body body, final OnUploadFileListener onUploadFileListener) {

        AliStsTokenBean token = mToken;
        OSSStsTokenCredentialProvider provider = new OSSStsTokenCredentialProvider(
                token.access_key_id,
                token.access_key_secret,
                token.token);
        mOss = new OSSClient(mContext, token.endpoint, provider);
        PutObjectRequest put;
//        if (body.bytes == null) {
        put = new PutObjectRequest(token.bucket, objKey, body.path);
//        }
//        } else { //put = new PutObjectRequest(token.bucket, objKey, body.bytes);
//        }

        // 异步上传时可以设置进度回调
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
                int progress = (int) (100 * currentSize / totalSize);
                if (onUploadFileListener != null) {
                    onUploadFileListener.progress(progress, currentSize, totalSize);
                }
            }
        });


        // 上传到阿里云
        mTasks.add(mOss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest putObjectRequest, PutObjectResult putObjectResult) {
                if (onUploadFileListener != null) {
                    onUploadFileListener.success();
                }
            }

            @Override
            public void onFailure(PutObjectRequest putObjectRequest, ClientException e, ServiceException e1) {
                if (onUploadFileListener != null) {
                    String msg = "";
                    if (e != null) {
                        // 本地异常如网络异常等
                        msg = e.getMessage();
                    }
                    if (e1 != null) {
                        // 服务异常
                        msg = msg + "\n" + e1.toString();
                    }
                    onUploadFileListener.error(msg);
                }
                if (e != null) {
                    // 本地异常如网络异常等
                    e.printStackTrace();
                }
                if (e1 != null) {
                    // 服务异常
                    e1.printStackTrace();
                }
            }
        }));
    }


    public void release() {
        for (OSSAsyncTask<PutObjectResult> task : mTasks) {
            if (!task.isCompleted()) {
                task.cancel();
            }
        }
        mTasks.clear();
    }

    public String getObjKey(String suffix) {
        final String name = /*UserModel.getUserInfo().user_id +*/System.currentTimeMillis() + "0" + 0;
        return (UploadManager.MP4.equals(suffix) ? PREFIX_VIDEO : UploadManager.MP3.equals(suffix) ? PREFIX_AUDIO : PREFIX_IMAGES) +
                TimeUtils.formatDateTime(System.currentTimeMillis(), "yyyyMM/dd") + "/"
                + CipherUtils.hashWithMD5(name) + suffix;

    }

    /**
     * 存上传数据的类
     */
    public static class Body {
        public Body(String path) {
            this.path = path;
        }

        public String path;
    }
}
