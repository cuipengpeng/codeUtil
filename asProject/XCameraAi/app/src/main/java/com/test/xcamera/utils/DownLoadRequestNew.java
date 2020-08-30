package com.test.xcamera.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.editvideo.MediaData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.test.xcamera.application.AiCameraApplication;
import com.test.xcamera.bean.MediaDownload;
import com.test.xcamera.bean.MoAlbumItem;
import com.test.xcamera.managers.ConnectionManager;
import com.test.xcamera.managers.DataManager;
import com.test.xcamera.managers.MoDownloadHandler;
import com.test.xcamera.mointerface.MoRequestCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DownLoadRequestNew extends Thread {
    public static final int DOWN_LOADFILE = 10000;
    public static final int DOWN_LOADFILE_OK = 10002;
    public static final int DOWN_LOAD_OK = 10003;
    public static final String CUR_DOWNLOAD_STATUS = "MO_ALBUM_MEDIA_CUR_DOWNLOAD_STATUS";

    public static String fileDirectorPath = Constants.appLocalPath;
    private List<MoAlbumItem> queue;
    private Handler mhandler;
    private long sum_length = 0;
    private int index = 0;
    private String filePath = "";

    public DownLoadRequestNew(Handler handler) {
        this.mhandler = handler;
        queue = new ArrayList<>();
    }

    public int getIndex() {
        return index;
    }
    public int getDownSize() {
        if(queue==null){
            return 0;
        }
        return queue.size();
    }
    /**
     * 获取下载内容的总大小
     *
     * @return
     */
    public long getSumCount() {
        for (int i = 0; i < queue.size(); i++) {
            MoAlbumItem item = queue.get(i);

            long size = 0;
            if (item.isVideo()) {
                size = item.getmVideo().getmSize();
            } else {
                size = item.getmImage().getmSize();
            }
            sum_length += size;
        }
        return sum_length;
    }

    long mTotalSize = 0;

    /**
     * 获取下载内容的总大小
     *
     * @return
     */
    public long getTotalSize() {
        for (int i = 0; i < queue.size(); i++) {
            MoAlbumItem item = queue.get(i);

            long size = 0;
            if (item.isVideo()) {
                size = item.getmVideo().getmSize();
            } else {
                size = item.getmImage().getmSize();
            }
            mTotalSize += size;
        }
        return mTotalSize;
    }

    /**
     * @param list
     */
    public void addData(List<MediaData> list, List<MoAlbumItem> items) {
        if ((list == null || list.size() == 0) && (items == null || items.size() == 0)) return;
        for (int i = 0; i < list.size(); i++) {
            String thum = list.get(i).getThumbPath();
            for (int j = 0; j < items.size(); j++) {
                MoAlbumItem item = items.get(j);
                String value = item.getmThumbnail().getmUri();
                if (thum.equals(value)) {
                    queue.add(item);
                }
            }
        }
    }

    public void addData(List<MoAlbumItem> items) {
        if (items == null || items.size() == 0) return;
        for (int j = 0; j < items.size(); j++) {
            MoAlbumItem item = items.get(j);
            if (item != null) {
                queue.add(item);
            }
        }
    }

    public void clear() {
        queue.clear();
        MoDownloadHandler.getInstance().removeAllCallback();
    }

    /**
     * 开始下载文件
     */
    public void downCameraFile() {
        this.start();
    }

    @Override
    public void run() {
        startDownload();
    }

    private String TAG = "DownLoadRequest";
    Uri mCurUri = null;

    private void startDownload() {
        Log.i(TAG, "startDownload: start down load camera file !!!");
        Log.i("club", "club:downVideo：startDownload:szie" + queue.size());
        if (queue.size() == 0) {
            return;
        }
        if (index < queue.size()) {
            MoAlbumItem item = queue.get(index);
            String type = item.getmType();

            //TODO
            String fileName = System.currentTimeMillis() + "";
            if (item.getmDownloadFileName() != null && !"".equals(item.getmDownloadFileName())) {
                fileName = item.getmDownloadFileName();
            }
            long size = 0;
            if (item.isVideo()) {
                size = item.getmVideo().getmSize();
                LoggerUtils.printLog(item.getmVideo().getmUri());
                mCurUri = Uri.parse(item.getmVideo().getmUri());
                fileName += ".mp4";
            } else {
                LoggerUtils.printLog(item.getmImage().getmUri());
                size = item.getmImage().getmSize();
                mCurUri = Uri.parse(item.getmImage().getmUri());
                fileName += ".jpg";
            }

            if (!item.isVideo()) {
                filePath = fileDirectorPath + "/" + fileName;
                Log.i(TAG, "startDownload: start down load photo index = " + index);
                new DownloadCameraPhotoNew(mhandler, fileDirectorPath, fileName).request(mCurUri, size, new DownloadCameraVideoNew.Download() {
                    @Override
                    public void downLoadOk(String path) {
                        updateCurDownLoad(AiCameraApplication.getContext(), item);
                        index = index + 1;
                        startDownload();

                        DataManager.getInstance().clearLargeCache();
                    }
                });
            } else {
                filePath = fileDirectorPath + "/" + fileName;
                Log.i(TAG, "startDownload: start down load video index = " + index);
                new DownloadCameraVideoNew(mhandler, fileDirectorPath, fileName).request(mCurUri, size, new DownloadCameraVideoNew.Download() {
                    @Override
                    public void downLoadOk(String path) {
                        updateCurDownLoad(AiCameraApplication.getContext(), item);
                        index = index + 1;
                        startDownload();

                        DataManager.getInstance().clearLargeCache();
                    }
                });
            }
        }
    }

    public void stopDownload() {
        if (mCurUri != null) {
            MoDownloadHandler.getInstance().removeCallback(mCurUri.toString());
        }
        ConnectionManager.getInstance().stopDownloadFile(new MoRequestCallback() {
            @Override
            public void onSuccess() {

            }
        });
        if (mhandler != null) {
            mhandler.removeMessages(DownLoadRequest.DOWN_LOAD_ERROR);
            mhandler.removeMessages(DownLoadRequest.DOWN_LOADFILE);
            mhandler.removeMessages(DownLoadRequest.DOWN_LOAD_OK);
        }

    }

    public static void deleteFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }

    public void deleteFile() {
        deleteFile(filePath);
    }

    /**
     * 更新已下载成功
     *
     * @param context
     * @param albumItem
     */
    public void updateCurDownLoad(Context context, MoAlbumItem albumItem) {
        Message message = mhandler.obtainMessage();
        message.what = DOWN_LOAD_OK;
        message.obj = albumItem;
        mhandler.sendMessage(message);
        String value = (String) SPUtils.get(context, CUR_DOWNLOAD_STATUS, "");
        if (!TextUtils.isEmpty(value)) {
            List<MediaDownload> albumItemList = new Gson().fromJson(value, new TypeToken<List<MediaDownload>>() {
            }.getType());
            if (albumItemList == null) {
                return;
            }
            Iterator item = albumItemList.iterator();
            while (item.hasNext()) {
                MediaDownload model = (MediaDownload) item.next();
                if (model.getName().equals(albumItem.getmDownloadFileName())) {
                    item.remove();
                    break;
                }
            }
            String str = new Gson().toJson(albumItemList);
            SPUtils.put(context, CUR_DOWNLOAD_STATUS, str);
        }

    }

    /**
     * 删除下载失败的文件
     *
     * @param context
     */
    public static void removeCurDownLoadError(Context context) {
        String value = (String) SPUtils.get(context, CUR_DOWNLOAD_STATUS, "");
        if (!TextUtils.isEmpty(value)) {
            List<MediaDownload> albumItemList = new Gson().fromJson(value, new TypeToken<List<MediaDownload>>() {
            }.getType());
            if (albumItemList == null) {
                return;
            }
            Iterator item = albumItemList.iterator();
            while (item.hasNext()) {
                MediaDownload model = (MediaDownload) item.next();
                String fileName = model.getName();

                if (model.isVideo()) {
                    fileName += ".mp4";
                } else {
                    fileName += ".jpg";
                }
                String path = fileDirectorPath + "/" + fileName;

                deleteFile(path);
                item.remove();
            }
        }
        SPUtils.remove(context, CUR_DOWNLOAD_STATUS);
    }

    /**
     * 缓存当前现在的文件
     *
     * @param context
     * @param albumItemList
     */
    public void saveCurDownLoad(Context context, List<MoAlbumItem> albumItemList) {
        if (albumItemList == null) {
            return;
        }
        List<MediaDownload> list = new ArrayList<>();
        for (MoAlbumItem item : albumItemList) {
            MediaDownload mediaDownload = new MediaDownload();
            mediaDownload.setName(item.getmDownloadFileName());
            mediaDownload.setVideo(item.isVideo());
            list.add(mediaDownload);
        }
        String value = new Gson().toJson(list);
        SPUtils.put(context, CUR_DOWNLOAD_STATUS, value);

    }


}
