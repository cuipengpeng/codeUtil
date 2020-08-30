package com.test.xcamera.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.test.xcamera.application.AiCameraApplication;
import com.test.xcamera.bean.MediaDownload;
import com.test.xcamera.bean.MoAlbumItem;
import com.editvideo.MediaData;
import com.test.xcamera.bean.MoImage;
import com.test.xcamera.bean.MoVideo;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DownLoadRequest extends Thread {
    public static final int DOWN_LOADFILE = 10000;
    public static final int DOWN_LOADFILE_OK = 10002;
    public static final int DOWN_LOAD_OK = 10003;
    public static final int DOWN_LOAD_ERROR = 10004;
    public static final int DOWN_LOAD_TIME_OUT = 10005;
    public static final String CUR_DOWNLOAD_STATUS = "MO_ALBUM_MEDIA_CUR_DOWNLOAD_STATUS";

    public static String fileDirectorPath = Constants.appLocalPath;
    private List<MoAlbumItem> queue;
    private Handler mhandler;
    private long sum_length = 0;
    private int index = 0;
    private String filePath = "";

    public DownLoadRequest(Handler handler) {
        this.mhandler = handler;
        queue = new ArrayList<>();
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
    }

    /**
     * 开始下载文件
     */
    public void downCameraFile() {
        DownloadConfig.getInstance().setCancelDown(false);
        this.start();
    }

    @Override
    public void run() {
        startDownload();
    }

    private String TAG = "DownLoadRequest";

    private void startDownload() {
        if (DownloadConfig.getInstance().getIsCancelDown()) {
            Log.i(TAG, "startDownload: cancel down load camera file !!!");
            return;
        } else {
            Log.i(TAG, "startDownload: start down load camera file !!!");
            Log.i("club", "club:downVideo：startDownload:szie" + queue.size());

            if (queue.size() == 0) {
                return;
            }
            if (index < queue.size()) {
                MoAlbumItem item = queue.get(index);
                if (!item.isImage()) {
                    //****如果出现损坏的资源将无法下载,循环下一个*****//
                    MoImage moImage = item.getmThumbnail();
                    if (moImage != null && (moImage.getmSize() == 0 || moImage.getmWidth() == 0 || moImage.getmHeight() == 0)) {
                        index++;
                        startDownload();
                    } else {
                        MoVideo moVideo = item.getmVideo();
                        if (moVideo != null && (moVideo.getmDuration() < 1000
                                || moVideo.getmWidth() == 0 || moVideo.getmHeight() == 0)) {
                            index++;
                            startDownload();
                        }
                    }
                }

                Uri uri = null;
                //TODO
                String fileName = System.currentTimeMillis() + "";
                if (item.getmDownloadFileName() != null && !"".equals(item.getmDownloadFileName())) {
                    fileName = item.getmDownloadFileName();
                }
                long size = 0;
                if (item.isVideo()) {
                    size = item.getmVideo().getmSize();
                    LoggerUtils.printLog(item.getmVideo().getmUri());
                    uri = Uri.parse(item.getmVideo().getmUri());
                    fileName += ".mp4";
                } else {
                    LoggerUtils.printLog(item.getmImage().getmUri());
                    size = item.getmImage().getmSize();
                    uri = Uri.parse(item.getmImage().getmUri());
                    fileName += ".jpg";
                }

                if (!item.isVideo()) {
                    filePath = fileDirectorPath + "/" + fileName;
                    Log.i(TAG, "startDownload: start down load photo index = " + index);
                    new DownloadCameraPhoto(mhandler, fileDirectorPath, fileName).request(uri, size, new DownloadCameraPhoto.Download() {
                        @Override
                        public void downLoadOk(String path) {
                            updateCurDownLoad(AiCameraApplication.getContext(), item);
                            index = index + 1;
                            startDownload();
                        }
                    });
                } else {
                    filePath = fileDirectorPath + "/" + fileName;
                    Log.i(TAG, "startDownload: start down load video index = " + index);
                    new DownloadCameraVideo(mhandler, fileDirectorPath, fileName).request(uri, size, new DownloadCameraVideo.Download() {
                        @Override
                        public void downLoadOk() {
                            updateCurDownLoad(AiCameraApplication.getContext(), item);
                            index = index + 1;
                            startDownload();
                        }
                    });
                }
            }
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
     * 取消下载
     */
    public void cancelDownload() {
        DownloadConfig.getInstance().setCancelDown(true);
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
        try {
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
        }catch (Exception e){
            e.printStackTrace();
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
