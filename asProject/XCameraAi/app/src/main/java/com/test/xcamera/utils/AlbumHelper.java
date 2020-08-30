package com.test.xcamera.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore.Audio.Albums;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Images.Thumbnails;
import android.util.Log;

import com.test.xcamera.bean.AlbumDirectory;
import com.test.xcamera.bean.ImageBucket;
import com.test.xcamera.bean.ImageItem;
import com.editvideo.MediaData;
import com.editvideo.MediaUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 专辑帮助类
 *
 * @author Administrator
 */
public class AlbumHelper {
    final String TAG = getClass().getSimpleName();
    Context context;
    ContentResolver cr;

    // 缩略图列表
    HashMap<String, String> thumbnailList = new HashMap<String, String>();
    // 专辑列表
    List<HashMap<String, String>> albumList = new ArrayList<HashMap<String, String>>();

    HashMap<String, ImageBucket> bucketList = new HashMap<String, ImageBucket>();

    private static AlbumHelper instance;

    private AlbumHelper() {
    }

    public static AlbumHelper getHelper() {
//		if (instance == null) {
        instance = new AlbumHelper();
//		}
        return instance;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        if (this.context == null) {
            this.context = context;
            cr = context.getContentResolver();
        }
    }

    /**
     * 得到缩略图
     */
    private void getThumbnail() {
        String[] projection = {Thumbnails._ID, Thumbnails.IMAGE_ID,
                Thumbnails.DATA};
        Cursor cursor = cr.query(Thumbnails.EXTERNAL_CONTENT_URI, projection,
                null, null, null);
        getThumbnailColumnData(cursor);
    }


    public List<ImageItem> getThumb() {
        String[] projection = {Thumbnails._ID, Thumbnails.IMAGE_ID,
                Thumbnails.DATA};
        Cursor cursor = cr.query(Thumbnails.EXTERNAL_CONTENT_URI, projection,
                null, null, null);
        List<ImageItem> tmpList = new ArrayList<>();
        tmpList.addAll(getThumbnailColumnData(cursor));
        return tmpList;
    }

    /**
     * 从数据库中得到缩略图
     *
     * @param cur
     */
    private List<ImageItem> getThumbnailColumnData(Cursor cur) {
        if (cur.moveToFirst()) {
            int _id;
            int image_id;
            String image_path;
            int _idColumn = cur.getColumnIndex(Thumbnails._ID);
            int image_idColumn = cur.getColumnIndex(Thumbnails.IMAGE_ID);
            int dataColumn = cur.getColumnIndex(Thumbnails.DATA);

            do {
                // Get the field values
                _id = cur.getInt(_idColumn);
                image_id = cur.getInt(image_idColumn);
                image_path = cur.getString(dataColumn);

//				 Do something with the values.
                Log.i(TAG, _id + " image_id:" + image_id + " path:"
                        + image_path + "---");
                HashMap<String, String> hash = new HashMap<String, String>();
                hash.put("image_id", image_id + "");
                hash.put("path", image_path);
//				 thumbnailList.add(hash);
                thumbnailList.put("" + image_id, image_path);
            } while (cur.moveToNext());
        }

        List<ImageItem> tmpList = new ArrayList<ImageItem>();
        Iterator<Entry<String, String>> itr = thumbnailList.entrySet()
                .iterator();
        while (itr.hasNext()) {
            Entry<String, String> entry = itr
                    .next();
            ImageItem item = new ImageItem();
            item.setImagePath(entry.getValue());
            tmpList.add(item);
        }
        return tmpList;
    }

    /**
     * 得到原图
     */
    void getAlbum() {
        String[] projection = {Albums._ID, Albums.ALBUM, Albums.ALBUM_ART,
                Albums.ALBUM_KEY, Albums.ARTIST, Albums.NUMBER_OF_SONGS};
        Cursor cursor = cr.query(Albums.EXTERNAL_CONTENT_URI, projection, null,
                null, null);
        getAlbumColumnData(cursor);

    }

    /**
     * 从本地数据库中得到原图
     *
     * @param cur
     */
    private void getAlbumColumnData(Cursor cur) {
        if (cur.moveToFirst()) {
            int _id;
            String album;
            String albumArt;
            String albumKey;
            String artist;
            int numOfSongs;

            int _idColumn = cur.getColumnIndex(Albums._ID);
            int albumColumn = cur.getColumnIndex(Albums.ALBUM);
            int albumArtColumn = cur.getColumnIndex(Albums.ALBUM_ART);
            int albumKeyColumn = cur.getColumnIndex(Albums.ALBUM_KEY);
            int artistColumn = cur.getColumnIndex(Albums.ARTIST);
            int numOfSongsColumn = cur.getColumnIndex(Albums.NUMBER_OF_SONGS);

            do {
                // Get the field values
                _id = cur.getInt(_idColumn);
                album = cur.getString(albumColumn);
                albumArt = cur.getString(albumArtColumn);
                albumKey = cur.getString(albumKeyColumn);
                artist = cur.getString(artistColumn);
                numOfSongs = cur.getInt(numOfSongsColumn);

                // Do something with the values.
                Log.i(TAG, _id + " album:" + album + " albumArt:" + albumArt
                        + "albumKey: " + albumKey + " artist: " + artist
                        + " numOfSongs: " + numOfSongs + "---");
                HashMap<String, String> hash = new HashMap<String, String>();
                hash.put("_id", _id + "");
                hash.put("album", album);
                hash.put("albumArt", albumArt);
                hash.put("albumKey", albumKey);
                hash.put("artist", artist);
                hash.put("numOfSongs", numOfSongs + "");
                albumList.add(hash);

            } while (cur.moveToNext());

        }
    }

    /**
     * 是否创建了图片集
     */
    boolean hasBuildImagesBucketList = false;

    /**
     * 得到图片集
     */
    void buildImagesBucketList() {
        long startTime = System.currentTimeMillis();

        // 构造缩略图索引
        getThumbnail();

        // 构造相册索引
        String columns[] = new String[]{Media._ID, Media.BUCKET_ID,
                Media.PICASA_ID, Media.DATA, Media.DISPLAY_NAME, Media.TITLE,
                Media.SIZE, Media.BUCKET_DISPLAY_NAME};
        // 得到一个游标
        Cursor cur = cr.query(Media.EXTERNAL_CONTENT_URI, columns, null, null,
                null);
        if (cur.moveToFirst()) {
            // 获取指定列的索引
            int photoIDIndex = cur.getColumnIndexOrThrow(Media._ID);
            int photoPathIndex = cur.getColumnIndexOrThrow(Media.DATA);
            int photoNameIndex = cur.getColumnIndexOrThrow(Media.DISPLAY_NAME);
            int photoTitleIndex = cur.getColumnIndexOrThrow(Media.TITLE);
            int photoSizeIndex = cur.getColumnIndexOrThrow(Media.SIZE);
            int bucketDisplayNameIndex = cur
                    .getColumnIndexOrThrow(Media.BUCKET_DISPLAY_NAME);//专辑的id
            int bucketIdIndex = cur.getColumnIndexOrThrow(Media.BUCKET_ID);
            int picasaIdIndex = cur.getColumnIndexOrThrow(Media.PICASA_ID);
            // 获取图片总数
            int totalNum = cur.getCount();

            do {
                String _id = cur.getString(photoIDIndex);
                String name = cur.getString(photoNameIndex);
                String path = cur.getString(photoPathIndex);
                String title = cur.getString(photoTitleIndex);
                String size = cur.getString(photoSizeIndex);
                String bucketName = cur.getString(bucketDisplayNameIndex); //专辑的名字
                String bucketId = cur.getString(bucketIdIndex);
                String picasaId = cur.getString(picasaIdIndex);

                Log.i(TAG, _id + ", bucketId: " + bucketId + ", picasaId: "
                        + picasaId + " name:" + name + " path:" + path
                        + " title: " + title + " size: " + size + " bucket: "
                        + bucketName + "---");

                ImageBucket bucket = bucketList.get(bucketId);
                if (bucket == null) {
                    bucket = new ImageBucket();
                    bucketList.put(bucketId, bucket);
                    bucket.imageList = new ArrayList<ImageItem>();
                    bucket.bucketName = bucketName;
                }
                bucket.count++;
                ImageItem imageItem = new ImageItem();
                imageItem.imageId = _id;
                imageItem.imagePath = path;
                imageItem.thumbnailPath = thumbnailList.get(_id);
                bucket.imageList.add(imageItem);

            } while (cur.moveToNext());
        }

        Iterator<Entry<String, ImageBucket>> itr = bucketList.entrySet()
                .iterator();
        while (itr.hasNext()) {
            Entry<String, ImageBucket> entry = itr
                    .next();
            ImageBucket bucket = entry.getValue();
            Log.d(TAG, entry.getKey() + ", " + bucket.bucketName + ", "
                    + bucket.count + " ---------- ");
            for (int i = 0; i < bucket.imageList.size(); ++i) {
                ImageItem image = bucket.imageList.get(i);
                Log.d(TAG, "----- " + image.imageId + ", " + image.imagePath
                        + ", " + image.thumbnailPath);
            }
        }
        hasBuildImagesBucketList = true;
        long endTime = System.currentTimeMillis();
        Log.d(TAG, "use time: " + (endTime - startTime) + " ms");
    }

    /**
     * 得到图片集
     *
     * @param refresh
     * @return
     */
    public List<ImageBucket> getImagesBucketList(boolean refresh) {
        if (refresh || (!refresh && !hasBuildImagesBucketList)) {
            buildImagesBucketList();
        }
        List<ImageBucket> tmpList = new ArrayList<ImageBucket>();
        Iterator<Entry<String, ImageBucket>> itr = bucketList.entrySet()
                .iterator();
        while (itr.hasNext()) {
            Entry<String, ImageBucket> entry = itr
                    .next();
            tmpList.add(entry.getValue());
        }
        return tmpList;
    }

    /**
     * 得到原始图像路径
     *
     * @param image_id
     * @return
     */
    String getOriginalImagePath(String image_id) {
        String path = null;
        Log.i(TAG, "---(^o^)----" + image_id);
        String[] projection = {Media._ID, Media.DATA};
        Cursor cursor = cr.query(Media.EXTERNAL_CONTENT_URI, projection,
                Media._ID + "=" + image_id, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            path = cursor.getString(cursor.getColumnIndex(Media.DATA));

        }
        return path;
    }


    public interface LocalMediaCallbackByDifferent {
        void onLocalMediaCallbackByDifferent(Map<String, AlbumDirectory> map);
    }

    /**
     * 获取全部信息的 分专辑
     *
     * @param activity
     * @param localMediaCallbackByDifferent
     */

    public static void getAllPhotoAndVideoByDifference(final Activity activity, final LocalMediaCallbackByDifferent localMediaCallbackByDifferent) {
//        getAllPhotoInfo(activity_my, new LocalMediaCallback() {
//            @Override
//            public void onLocalMediaCallback(final List<MediaData> allPhotos) {
//                getAllVideoInfos(activity_my, new LocalMediaCallback() {
//                    @Override
//                    public void onLocalMediaCallback(List<MediaData> allVideos) {
//                        //图片
//                        final List<MediaData> allMediaList = new ArrayList<>();
//
//                        final List<MediaData> allPhotoList = new ArrayList<>();
//                        if (allPhotos != null && allPhotos.size() > 0) {
//                            allPhotoList.addAll(allPhotos);
//                        }
//                        //视频
//                        final List<MediaData> allVideoList = new ArrayList<>();
//                        if (allVideos != null && allVideos.size() > 0) {
//                            allVideoList.addAll(allVideos);
//                        }
//                        //下面是排序
//                        allMediaList.addAll(allPhotoList);
//                        allMediaList.addAll(allVideoList);
//                        //采用冒泡排序的方式排列数据
//                        sortByTimeRepoList(allMediaList);
//                        localMediaCallback.onLocalMediaCallback(allMediaList);
//                    }
//                });
//            }
//        });


        MediaUtils.getInstance().getAllPhotoAndVideo(activity, new MediaUtils.LocalMediaCallback() {
            @Override
            public void onLocalMediaCallback(List<MediaData> allMediaTemp) {


                Map<String, AlbumDirectory> map = new HashMap<>();

                for (MediaData media : allMediaTemp) {

                    String bucketId = media.getBucketId();
                    String bucketName = media.getBucketName();
                    AlbumDirectory bucket = map.get(media.getBucketId());
                    if (bucket == null) {
                        bucket = new AlbumDirectory();
                        map.put(bucketId, bucket);
                        bucket.setImageList(new ArrayList<MediaData>());
                        bucket.setBucketName(bucketName);
                    }
//				bucket.count++;

                    bucket.getImageList().add(media);
                }
                localMediaCallbackByDifferent.onLocalMediaCallbackByDifferent(map);
            }
        });
        Log.i("club","club:LocalMediaCallback:"+"AlbumMediaTask start");
//
//        AlbumMediaTask task=new AlbumMediaTask(activity,localMediaCallbackByDifferent);
//        task.execute();
    }
}
