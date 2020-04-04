package com.xiao.nicevideoplayer;

import android.content.Context;
import android.os.Environment;

import com.danikula.videocache.CacheListener;
import com.danikula.videocache.HttpProxyCacheServer;

import java.io.File;

public class ProxyCacheManager implements CacheListener {
    //视频代理
    protected HttpProxyCacheServer proxy;
    protected File mCacheDir;
    private Context mContext;
    private static ProxyCacheManager proxyCacheManager;

    private ProxyCacheManager(){}
    /**
     单例管理器
     */
    public static synchronized ProxyCacheManager instance(Context context) {
        if (proxyCacheManager == null) {
            proxyCacheManager = new ProxyCacheManager();
            proxyCacheManager.mContext = context;
        }
        return proxyCacheManager;
    }

    @Override
    public void onCacheAvailable(File cacheFile, String url, int percentsAvailable) {
    }

    public HttpProxyCacheServer getProxy(File cacheDir) {
        return proxy == null ? (proxy = newProxy(cacheDir)) : proxy;
    }

    private HttpProxyCacheServer newProxy(File cacheDir) {
        if (cacheDir == null) {
//            cacheDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+"/ChaoGe");
//            cacheDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PODCASTS);
            cacheDir = NiceVideoPlayer.SAVED_DIR;
        }

        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        return new HttpProxyCacheServer.Builder(mContext)
                .maxCacheSize(1024 * 1024 * 1024)       // 1 Gb for cache
                .cacheDirectory(cacheDir)
                .build();
    }
}
