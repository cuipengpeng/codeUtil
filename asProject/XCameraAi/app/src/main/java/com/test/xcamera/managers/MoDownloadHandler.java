package com.test.xcamera.managers;

import com.test.xcamera.mointerface.MoDownloadCallback;

import java.util.HashMap;

/**
 * Created by zll on 2019/7/4.
 */

public class MoDownloadHandler {
    private static final String TAG = "MoDownloadHandler";
    private static MoDownloadHandler singleton = null;
    private static Object lock = new Object();
    private HashMap<String, MoDownloadCallback> downloadTable;

    public static MoDownloadHandler getInstance() {
        synchronized (lock) {
            if (singleton == null) {
                singleton = new MoDownloadHandler();
            }
        }
        return singleton;
    }

    public MoDownloadHandler() {
        downloadTable = new HashMap<>();
    }

    public void addDownloadCommand(String uri, MoDownloadCallback callback) {
         synchronized (lock){
        if (downloadTable != null) {
            downloadTable.put(uri, callback);
        }
         }
    }

    public MoDownloadCallback getDownloadCallback(String uri) {
        if (downloadTable == null) return null;
          synchronized (lock) {
              MoDownloadCallback callback=downloadTable.remove(uri);
              if(callback==null){
                  callback=getNewDownloadCallback(uri);
              }
              return callback;
          }

    }


    /**
     * 为了支持新下载逻辑，新下载没有缓存offset参数
     * @param uri
     * @return
     */
    public MoDownloadCallback getNewDownloadCallback(String uri) {
        synchronized (lock) {
            if(uri.contains(DataManager.SPLIT_URL)){
                String newUrl=uri.split(DataManager.SPLIT_URL)[0];
                return downloadTable.get(newUrl);
            }else {
                return null;
            }
        }

    }
    /**
     * 删除 支持新下载逻辑
     * @param uri
     * @return
     */
    public void removeCallback(String uri) {
        synchronized (lock) {
            downloadTable.remove(uri);
        }
    }

    /**
     * 清空所有缓存
     */
    public void removeAllCallback() {
        synchronized (lock) {
            downloadTable.clear();
        }
    }

}
