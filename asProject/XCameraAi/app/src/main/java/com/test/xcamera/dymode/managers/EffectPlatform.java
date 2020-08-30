package com.test.xcamera.dymode.managers;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.test.xcamera.application.AiCameraApplication;
import com.test.xcamera.dymode.utils.FileUtils;
import com.test.xcamera.dymode.utils.ImplJsonConverter;
import com.test.xcamera.dymode.utils.ImplNetWorker;
import com.ss.android.ugc.effectmanager.EffectConfiguration;
import com.ss.android.ugc.effectmanager.EffectManager;
import com.ss.android.ugc.effectmanager.common.task.ExceptionResult;
import com.ss.android.ugc.effectmanager.effect.listener.IEffectDownloadProgressListener;
import com.ss.android.ugc.effectmanager.effect.listener.IFetchEffectChannelListener;
import com.ss.android.ugc.effectmanager.effect.model.Effect;
import com.ss.android.ugc.effectmanager.effect.model.EffectCategoryResponse;
import com.ss.android.ugc.effectmanager.effect.model.EffectChannelResponse;
import com.ss.android.ugc.effectmanager.link.model.host.Host;
import com.ss.android.vesdk.VESDK;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Created by zll on 2020/2/24.
 */

public class EffectPlatform {
    private static final String TAG = "EffectPlatform";
    private static final String ACCESS_KEY = "56351e7052ed11ea85f25bb3eaf08f69";
    private static final String TEST_PANEL = "default";
    public static final String PANEL_DEFAULT = "default";
    public static final String PANEL_FILTER = "filter";

    private static EffectPlatform singleton = null;
    private static Object lock = new Object();
    private EffectManager mEffectManager;
    private Context mContext;
    private EffectChannelResponse mEffectChannelResponse;
    private EffectChannelResponse mEffectFilterResponse;
    private List<EffectCategoryResponse> mFilterTypes;
    private EffectDownloadListener mDownloadListener;
    private boolean initOK;

    public static EffectPlatform getInstance() {
        synchronized (lock) {
            if (singleton == null) {
                singleton = new EffectPlatform();
            }
        }
        return singleton;
    }

    public EffectPlatform() {
        initEffectManager();
    }

    public void setDownloadListener(EffectDownloadListener mDownloadListener) {
        this.mDownloadListener = mDownloadListener;
    }

    public void initEffectManager() {
        List<Host> hosts = new ArrayList<>();
        hosts.add(new Host("https://effect.snssdk.com"));
        EffectConfiguration configuration = new EffectConfiguration.Builder()
                .accessKey(ACCESS_KEY)
                .channel("default")
//                .channel("test")
                .sdkVersion(VESDK.getEffectSDKVer())
                .appVersion("1.0.0.100019")
                .platform("android")
                .deviceType(Build.MODEL)
                .JsonConverter(new ImplJsonConverter())
                .deviceId(Build.DEVICE)
                .retryCount(3)
//                .effectDir(new File(FileUtils.ROOT_DIR))
                .effectDir(new File(FileUtils.getDyEffectPath(AiCameraApplication.getContext())))
                .effectNetWorker(new ImplNetWorker())
                .executor(Executors.newFixedThreadPool(5))
                //链路配置
                .hosts(hosts)
//                .appID(TEST_APP_ID_Aweme)
                .context(AiCameraApplication.getContext())
                .netWorkChangeMonitor(true)
                .lazy(true)
//                .repeatTime(2)
                .build();

        mEffectManager = new com.ss.android.ugc.effectmanager.EffectManager();
        initOK = mEffectManager.init(configuration);
        Log.d(TAG, "initEffectManager: state = " + initOK);

    }

    public void initEffectList() {
        fetchEffectListFormCache(PANEL_DEFAULT);
        fetchEffectListFormCache(PANEL_FILTER);
        if (!initOK) {
            Log.d(TAG, "initEffectManager: EffectPlatform 初始化失败，请检查错误日志");
        } else {
            fetchEffectList(PANEL_DEFAULT, null);
            fetchEffectList(PANEL_FILTER, null);
        }
    }

    public void fetchEffectList(String panel, EffectRequestCallback callback) {
        mEffectManager.fetchEffectList(panel, false, new IFetchEffectChannelListener() {
            @Override
            public void onSuccess(EffectChannelResponse response) {
                if (response != null) {
                    if (response.getPanel().equals(PANEL_DEFAULT)) {
                        mEffectChannelResponse = response;
                    } else {
                        mEffectFilterResponse = response;
                        mFilterTypes = mEffectFilterResponse.getCategoryResponseList();
                    }
                    checkLocalSate(response.getPanel(), callback);
                    Log.d(TAG, "the panel is " + response.getPanel());
                }
            }

            @Override
            public void onFail(ExceptionResult e) {
                if (e.getException() != null) e.getException().printStackTrace();
                else Log.d(TAG, "onFail: " + e.getMsg());
                if (callback != null) {
                    callback.onFailed();
                }
            }
        });
    }

    public void fetchEffectListFormCache(String pannel) {
        mEffectManager.fetchEffectListFromCache(pannel, new IFetchEffectChannelListener() {
            @Override
            public void onSuccess(EffectChannelResponse response) {
//                response.getPanelModel();
                Log.d(TAG, "onSuccess: 从缓存获取特效列表成功");
                if (response != null) {
                    if (response.getPanel().equals(PANEL_DEFAULT)) {
                        mEffectChannelResponse = response;
                    } else {
                        mEffectFilterResponse = response;
                        mFilterTypes = mEffectFilterResponse.getCategoryResponseList();
                    }
                    checkLocalSate(response.getPanel(), null);
                    Log.d(TAG, "the panel is " + response.getPanel());
                }
            }

            @Override
            public void onFail(ExceptionResult e) {
                if (e.getException() != null) e.getException().printStackTrace();
                else Log.d(TAG, "onFail: 从缓存获取特效列表失败：" + e.getMsg());
            }
        });
    }

    public void fetchEffect(Effect effect, EffectDownloadListener listener) {
        mDownloadListener=listener;
        mEffectManager.fetchEffect(effect, new IEffectDownloadProgressListener() {
            @Override
            public void onSuccess(Effect effect) {
                Log.d(TAG, "download success " + effect.getZipPath() + ", unzip path = " + effect.getUnzipPath());
                if(mDownloadListener!=null){
                    mDownloadListener.onSuccess(effect);
                }
            }

            @Override
            public void onFail(Effect failedEffect, ExceptionResult e) {
                if (e.getException() != null) e.getException().printStackTrace();
                if(mDownloadListener!=null){
                    mDownloadListener.onFailed(failedEffect, e);
                }
                Log.d(TAG, "onFail: " + e.getMsg() + " error code:" + e.getErrorCode());
            }

            @Override
            public void onStart(Effect effect) {
                Log.d(TAG, "onStart: ");
                if(mDownloadListener!=null) {
                    listener.onStart(effect);
                }
            }

            @Override
            public void onProgress(Effect effect, int progress, long totalSize) {
                Log.i("TAG", "progress1:" + progress);
                if(mDownloadListener!=null) {

                    listener.onProgress(effect, progress, totalSize);
                }
            }
        });
    }

    public boolean isEffectDownloaded(Effect effect) {
        return mEffectManager.isEffectDownloaded(effect);
    }

    public List<Effect> getEffectList(String panel) {
        if (panel.equals(PANEL_DEFAULT)) {
            if (mEffectChannelResponse != null) {
                return mEffectChannelResponse.getAllCategoryEffects();
            }
            return null;
        } else {
            if (mEffectFilterResponse != null) {
                return mEffectFilterResponse.getAllCategoryEffects();
            }
            return null;
        }
    }

    /***
     * 获取滤镜分类信息
     * @return
     */
    public List<EffectCategoryResponse> getFilterTypes() {
        if (mFilterTypes != null)
            return mFilterTypes;
        return null;
    }

    public List<Effect> getFirstFilters() {
        if (mFilterTypes != null)
            return mFilterTypes.get(0).getTotalEffects();
        return null;
    }

    public String getFirtTypeName() {
        if (mFilterTypes != null)
            return mFilterTypes.get(0).getName();
        return "";
    }

    public boolean hasPropsData() {
//        Log.d(TAG, "hasPropsData mEffectChannelResponse: " + (mEffectChannelResponse != null)
//        + ", getAllCategoryEffects: " + (mEffectChannelResponse.getAllCategoryEffects() != null)
//        + ", size: " + (mEffectChannelResponse.getAllCategoryEffects().size() > 0));
        return mEffectChannelResponse != null && mEffectChannelResponse.getAllCategoryEffects() != null
                && mEffectChannelResponse.getAllCategoryEffects().size() > 0;
    }

    public boolean hasFiltersData() {
        return mEffectFilterResponse != null && mEffectFilterResponse.getAllCategoryEffects() != null
                && mEffectFilterResponse.getAllCategoryEffects().size() > 0;
    }

    public void syncData(boolean isDownloaded, String panel, int position, Effect effect) {
        effect.setDownloaded(isDownloaded);
        if (panel.equals(PANEL_DEFAULT)) {
            mEffectChannelResponse.getAllCategoryEffects().remove(position);
            mEffectChannelResponse.getAllCategoryEffects().add(position, effect);
        } else {
            mEffectFilterResponse.getAllCategoryEffects().remove(position);
            mEffectFilterResponse.getAllCategoryEffects().add(position, effect);
        }
    }

    private void checkLocalSate(String panel, EffectRequestCallback callback) {
        if (panel.equals(PANEL_DEFAULT)) {
            for (Effect effect : mEffectChannelResponse.getAllCategoryEffects()) {
                effect.setDownloaded(isEffectDownloaded(effect));
            }
        } else {
            for (int i = 0; i < mFilterTypes.size(); i++) {
                EffectCategoryResponse response = mFilterTypes.get(i);
                for (int j = 0; j < response.getTotalEffects().size(); j++) {
                    Effect effect = response.getTotalEffects().get(j);
                    effect.setDownloaded(isEffectDownloaded(effect));
                    effect.setSource(100);
                }
            }
//            for (Effect effect : mEffectFilterResponse.getAllCategoryEffects()) {
//                effect.setDownloaded(isEffectDownloaded(effect));
//                effect.setSource(100);
//            }
        }
        if (callback != null) {
            callback.onSuccess();
        }
    }

//    public void release() {
//        if (mEffectManager != null) {
//            mEffectManager.destroy();
//        }
//    }

    public interface EffectDownloadListener {
        void onStart(Effect effect);

        void onProgress(Effect effect, int progress, long totalSize);

        void onSuccess(Effect effect);

        void onFailed(Effect failedEffect, ExceptionResult e);
    }

    public interface EffectRequestCallback {
        void onSuccess();

        void onFailed();
    }
}
