package com.test.bank.utils;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.test.bank.bean.MediaData;
import com.test.bank.bean.MusicBean;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by ${gexinyu} on 2018/5/28.
 */

public class MediaUtils {


    //音乐和视频类型
    public static final int ALL_MEDIA = 0;
    public static final int VIDEO = 1;
    public static final int IMAGE = 2;
    /**
     * 获取视频的缩略图
     * 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。
     * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。
     *
     * @param videoPath 视频的路径
     * @param width     指定输出视频缩略图的宽度
     * @param height    指定输出视频缩略图的高度度
     * @param kind      参照MediaStore.Images(Video).Thumbnails类中的常量MINI_KIND和MICRO_KIND。
     *                  其中，MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96
     * @return 指定大小的视频缩略图
     */
    public static Bitmap getVideoThumbnail(String videoPath, int width, int height, int kind) {
        Bitmap bitmap = null;
        // 获取视频的缩略图
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
        if (bitmap != null) {
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        }
        return bitmap;
    }

    // 获取视频缩略图
    public static Bitmap getVideoThumbnail(String filePath) {
        Bitmap b = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            b = retriever.getFrameAtTime();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();

        } finally {
            try {
                retriever.release();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return b;
    }

    /**
     * 读取手机中所有图片信息
     */
    public static void getAllPhotoInfo(final Activity activity, final LocalMediaCallback localMediaCallback) {
        ThreadPoolUtils.getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                final List<MediaData> mediaBeen = new ArrayList<>();
                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

                String[] projection = {
                        MediaStore.Images.Media.DATA,
                        MediaStore.Images.Media.DATE_ADDED,
                        MediaStore.Images.Thumbnails.DATA,
                        MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                        MediaStore.Images.Media.BUCKET_ID
                };
                //全部图片
                String where = MediaStore.Images.Media.MIME_TYPE + "=? or "
                        + MediaStore.Images.Media.MIME_TYPE + "=? or "
                        + MediaStore.Images.Media.MIME_TYPE + "=?";
                //指定格式
                String[] whereArgs = {"image/jpeg", "image/png", "image/jpg"};
                //查询
                Cursor mCursor = activity.getContentResolver().query(
                        mImageUri, projection, null, null,
                        null);
                if (mCursor != null) {
                    while (mCursor.moveToNext()) {
                        // 获取图片的路径
                        int thumbPathIndex = mCursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA);
                        int timeIndex = mCursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED);
                        int pathIndex = mCursor.getColumnIndex(MediaStore.Images.Media.DATA);
                        int id = mCursor.getColumnIndex(MediaStore.Images.Media._ID);

                        //专辑信息
                        int bucketDisplayNameIndex = mCursor
                                .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);//专辑的id
                        int bucketIdIndex = mCursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID);

                        //专辑info

                        String BucketName = mCursor.getString(bucketDisplayNameIndex);
                        String bucketId = mCursor.getString(bucketIdIndex);

                        String thumbPath = mCursor.getString(thumbPathIndex);
                        Long date = mCursor.getLong(timeIndex) * 1000;
                        String filepath = mCursor.getString(pathIndex);
                        //判断文件是否存在，存在才去加入
                        boolean b = fileIsExists(filepath);
                        if (b) {
                            File f = new File(filepath);
                            MediaData fi = new MediaData(id, IMAGE, filepath, thumbPath, date, f.getName(), false);
                            fi.setBucketName(BucketName);
                            fi.setBucketId(bucketId);
                            mediaBeen.add(fi);
                        }
                    }
                    mCursor.close();
                }

                Log.i("MediaUtils", mediaBeen + "");
                //更新界面
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //采用冒泡排序的方式排列数据
                        sortByTimeRepoList(mediaBeen);
                        localMediaCallback.onLocalMediaCallback(mediaBeen);
                    }
                });
            }
        });
    }

    /**
     * 获取手机中所有视频的信息
     */
    public static void getAllVideoInfos(final Activity activity, final LocalMediaCallback localMediaCallback) {
        ThreadPoolUtils.getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                final List<MediaData> videoList = new ArrayList<>();
                Uri mVideoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                String[] projection = {MediaStore.Video.Thumbnails._ID
                        , MediaStore.Video.Thumbnails.DATA
                        , MediaStore.Video.Media.DURATION
                        , MediaStore.Video.Media.SIZE
                        , MediaStore.Video.Media.DATE_ADDED
                        , MediaStore.Video.Media.DISPLAY_NAME
                        , MediaStore.Video.Media.DATE_MODIFIED
                        , MediaStore.Video.Media.BUCKET_DISPLAY_NAME
                        , MediaStore.Video.Media.BUCKET_ID};
                //全部视频
                String where = MediaStore.Images.Media.MIME_TYPE + "=? or "
                        + MediaStore.Video.Media.MIME_TYPE + "=? or "
                        + MediaStore.Video.Media.MIME_TYPE + "=? or "
                        + MediaStore.Video.Media.MIME_TYPE + "=? or "
                        + MediaStore.Video.Media.MIME_TYPE + "=? or "
                        + MediaStore.Video.Media.MIME_TYPE + "=? or "
                        + MediaStore.Video.Media.MIME_TYPE + "=? or "
                        + MediaStore.Video.Media.MIME_TYPE + "=? or "
                        + MediaStore.Video.Media.MIME_TYPE + "=?";
                String[] whereArgs = {"video/mp4", "video/3gp", "video/aiv", "video/rmvb", "video/vob", "video/flv",
                        "video/mkv", "video/mov", "video/mpg"};
                Cursor mCursor = activity.getContentResolver().query(mVideoUri,
                        projection, null, null, MediaStore.Video.Media.DATE_ADDED + " DESC ");

                if (mCursor != null) {
                    while (mCursor.moveToNext()) {
                        // 获取视频的路径
                        int videoId = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Video.Media._ID));
                        String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Video.Media.DATA));
                        long duration = mCursor.getLong(mCursor.getColumnIndex(MediaStore.Video.Media.DURATION));
                        long size = mCursor.getLong(mCursor.getColumnIndex(MediaStore.Video.Media.SIZE)) / 1024; //单位kb

                        int bucketDisplayNameIndex = mCursor
                                .getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME);//专辑的id
                        int bucketIdIndex = mCursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_ID);

                        if (size < 0) {
                            //某些设备获取size<0，直接计算
                            Log.e("dml", "this video size < 0 " + path);
                            size = new File(path).length() / 1024;
                        }

                        //专辑info

                        String BucketName = mCursor.getString(bucketDisplayNameIndex);
                        String bucketId = mCursor.getString(bucketIdIndex);


                        String displayName = mCursor.getString(mCursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                        //用于展示相册初始化界面
                        int timeIndex = mCursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED);
                        Long date = mCursor.getLong(timeIndex) * 1000;
                        //需要判断当前文件是否存在  一定要加，不然有些文件已经不存在图片显示不出来
                        boolean fileIsExists = fileIsExists(path);
                        if (fileIsExists) {

                            MediaData mediaData = new MediaData(videoId, VIDEO, path, path, duration, date, displayName, false);
                            mediaData.setBucketName(BucketName);
                            mediaData.setBucketId(bucketId);
                            videoList.add(mediaData);
                        }
                    }
                    mCursor.close();
                }
                //更新界面
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //...
                        localMediaCallback.onLocalMediaCallback(videoList);
                    }
                });
            }
        });
    }

    /**
     * 所有信息
     */
    public static void getAllPhotoAndVideo(final Activity activity, final LocalMediaCallback localMediaCallback) {
        getAllPhotoInfo(activity, new LocalMediaCallback() {
            @Override
            public void onLocalMediaCallback(final List<MediaData> allPhotos) {
                getAllVideoInfos(activity, new LocalMediaCallback() {
                    @Override
                    public void onLocalMediaCallback(List<MediaData> allVideos) {
                        //图片
                        final List<MediaData> allMediaList = new ArrayList<>();

                        final List<MediaData> allPhotoList = new ArrayList<>();
                        if (allPhotos != null && allPhotos.size() > 0) {
                            allPhotoList.addAll(allPhotos);
                        }
                        //视频
                        final List<MediaData> allVideoList = new ArrayList<>();
                        if (allVideos != null && allVideos.size() > 0) {
                            allVideoList.addAll(allVideos);
                        }
                        //下面是排序
                        allMediaList.addAll(allPhotoList);
                        allMediaList.addAll(allVideoList);
                        //采用冒泡排序的方式排列数据
                        sortByTimeRepoList(allMediaList);
                        localMediaCallback.onLocalMediaCallback(allMediaList);
                    }
                });
            }
        });
    }


    /**
     * 根据类型返回media
     *
     * @param mActivity
     * @param index
     */
    public static void getMediasByType(Activity mActivity, int index, LocalMediaCallback localMediaCallback) {
        if (index == ALL_MEDIA) {
            getAllPhotoAndVideo(mActivity, localMediaCallback);
        } else if (index == VIDEO) {
            getAllVideoInfos(mActivity, localMediaCallback);
        } else {
            getAllPhotoInfo(mActivity, localMediaCallback);
        }
    }

    /**
     * 根据时间进行排序
     *
     * @param itemInfoList
     */
    public static void sortByTimeRepoList(List<MediaData> itemInfoList) {
        Collections.sort(itemInfoList, new Comparator<MediaData>() {
                    @Override
                    public int compare(MediaData item1, MediaData item2) {
                        Date date1 = new Date(item1.getData() * 1000L);
                        Date date2 = new Date(item2.getData() * 1000L);
                        return date2.compareTo(date1);
                    }
                }
        );
    }


    /**
     * 扫描系统里面的音频文件，返回一个list集合
     */
    public static List<MusicBean> getLocalMusicList(Context context) {
        List<MusicBean> list = new ArrayList<MusicBean>();

        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,
                null, MediaStore.Audio.AudioColumns.IS_MUSIC);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                MusicBean song = new MusicBean();
                //歌曲
                song.setName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)));
                //歌手
                song.setSinger(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)));
                //路径
                song.setPath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
                //时长
                song.setDuration(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));
                song.setSize(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)));
                if (song.getSize() > 1000 * 800) {
                    // 注释部分是切割标题，分离出歌曲名和歌手 （本地媒体库读取的歌曲信息不规范）
                    if (song.getName().contains("-")) {
                        String[] str = song.getName().split("-");
                        song.setSinger(str[0].trim());
                        song.setName(str[1].trim());
                    }
                    //设置拼音首字母
                    list.add(song);
                }

            }

            // 释放资源
            cursor.close();
        }

        return list;
    }

    /**
     * 定义一个方法用来格式化获取到的时间
     */
    public static String formatTime(int time) {
        if (time / 1000 % 60 < 10) {
            return time / 1000 / 60 + ":0" + time / 1000 % 60;
        } else {
            return time / 1000 / 60 + ":" + time / 1000 % 60;
        }
    }

    public static String formatTimeStrWithUs(long us) {
        int second = (int) (us / 1000000.0);
        int hh = second / 3600;
        int mm = second % 3600 / 60;
        int ss = second % 60;
        return hh > 0 ? String.format("%02d:%02d:%02d", hh, mm, ss) : String.format("%02d:%02d", mm, ss);
    }

    //获取数据库字段
    private static void getUriColumns(Context context, Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String[] columns = cursor.getColumnNames();

        for (String string : columns) {
            System.out.println(cursor.getColumnIndex(string) + " = " + string);
        }
        cursor.close();
    }

    public static ListOfAllMedia groupListByTime(List<MediaData> allMediaTemp) {
        //分组算法
        Map<String, List<MediaData>> skuIdMap = new LinkedHashMap<>();
        for (MediaData mediaData : allMediaTemp) {
            String strTime = new SimpleDateFormat("yyyy年MM月dd日").format(new Date(mediaData.getData()));
            List<MediaData> tempList = skuIdMap.get(strTime);
            /*如果取不到数据,那么直接new一个空的ArrayList**/
            if (tempList == null) {
                tempList = new ArrayList<>();
                tempList.add(mediaData);
                skuIdMap.put(strTime, tempList);
            } else {
                tempList.add(mediaData);
            }
        }
        List<List<MediaData>> lists = new ArrayList<>();
        List<MediaData> listOfOut = new ArrayList<>();
        for (Map.Entry<String, List<MediaData>> entry : skuIdMap.entrySet()) {
            MediaData mediaData = new MediaData();
            mediaData.setData(getIntTime(entry.getKey()));
            listOfOut.add(mediaData);
            lists.add(entry.getValue());
        }
        return new ListOfAllMedia(listOfOut, lists);
    }

    private static long getIntTime(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
        Date date;
        try {
            date = sdr.parse(time);
            return date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public interface LocalMediaCallback {

        void onLocalMediaCallback(List<MediaData> allMediaTemp);
    }


    public static class ListOfAllMedia {
        private List<MediaData> listOfParent;
        private List<List<MediaData>> listOfAll;

        public ListOfAllMedia(List<MediaData> listOfParent, List<List<MediaData>> listOfAll) {
            this.listOfParent = listOfParent;
            this.listOfAll = listOfAll;
        }

        public List<MediaData> getListOfParent() {
            if (listOfParent == null) {
                return new ArrayList<>();
            }
            return listOfParent;
        }

        public void setListOfParent(List<MediaData> listOfParent) {
            this.listOfParent = listOfParent;
        }

        public List<List<MediaData>> getListOfAll() {
            if (listOfAll == null) {
                return new ArrayList<>();
            }
            return listOfAll;
        }

        public void setListOfAll(List<List<MediaData>> listOfAll) {
            this.listOfAll = listOfAll;
        }
    }

    /**
     * 判断当前文件是否存在
     *
     * @param strFile
     * @return
     */
    public static boolean fileIsExists(String strFile) {
        try {
            File f = new File(strFile);
            if (!f.exists()) {
                return false;
            }

        } catch (Exception e) {
            return false;
        }

        return true;
    }
    
}
