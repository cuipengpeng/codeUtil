/*
 * Copyright 2017 Yan Zhenjie.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.test.xcamera.moalbum.data;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.annotation.WorkerThread;

import com.test.xcamera.R;
import com.test.xcamera.moalbum.bean.MoAlbumFile;
import com.test.xcamera.moalbum.bean.MoAlbumFolder;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by YanZhenjie on 2017/8/15.
 */
public class MediaReader {

    private Context mContext;

    public MediaReader(Context context) {
        this.mContext = context;
    }

    /**
     * Image attribute.
     */
    private static final String[] IMAGES = {
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.LATITUDE,
            MediaStore.Images.Media.LONGITUDE,
            MediaStore.Images.Media.SIZE
    };

    /**
     * Scan for image files.
     */
    @WorkerThread
    private void scanImageFile(Map<String, MoAlbumFolder> albumFolderMap, MoAlbumFolder allFileFolder) {
        ContentResolver contentResolver = mContext.getContentResolver();
        Cursor cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                IMAGES,
                null,
                null,
                null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String path = cursor.getString(0);
                File file = new File(path);
                long size = cursor.getLong(6);
                if (file != null && file.exists() && size > 0) {
                    String bucketName = cursor.getString(1);
                    String mimeType = cursor.getString(2);
                    long addDate = cursor.getLong(3);
                    float latitude = cursor.getFloat(4);
                    float longitude = cursor.getFloat(5);
                    MoAlbumFile imageFile = new MoAlbumFile();
                    imageFile.setMediaType(MoAlbumFile.TYPE_IMAGE);
                    imageFile.setPath(path);
                    imageFile.setBucketName(bucketName);
                    imageFile.setMimeType(mimeType);
                    imageFile.setAddDate(addDate);
                    imageFile.setLatitude(latitude);
                    imageFile.setLongitude(longitude);
                    imageFile.setSize(size);

                    allFileFolder.addAlbumFile(imageFile);
                    MoAlbumFolder moAlbumFolder = albumFolderMap.get(bucketName);

                    if (moAlbumFolder != null) {
                        moAlbumFolder.addAlbumFile(imageFile);
                        moAlbumFolder.setIsCamera(false);
                    } else {
                        moAlbumFolder = new MoAlbumFolder();
                        moAlbumFolder.setName(bucketName);
                        moAlbumFolder.addAlbumFile(imageFile);
                        moAlbumFolder.setIsCamera(false);

                        albumFolderMap.put(bucketName, moAlbumFolder);
                    }
                }
            }
            cursor.close();
        }
    }

    /**
     * Video attribute.
     */
    private static final String[] VIDEOS = {
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Video.Media.MIME_TYPE,
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Media.LATITUDE,
            MediaStore.Video.Media.LONGITUDE,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.WIDTH,
            MediaStore.Video.Media.HEIGHT
    };

    /**
     * Scan for image files.
     */
    @WorkerThread
    private void scanVideoFile(Map<String, MoAlbumFolder> albumFolderMap, MoAlbumFolder allFileFolder) {
        ContentResolver contentResolver = mContext.getContentResolver();
        Cursor cursor = contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                VIDEOS,
                null,
                null,
                null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String path = cursor.getString(0);
                long size = cursor.getLong(6);
                long duration = cursor.getLong(7);
                File file = new File(path);
                if (file != null && file.exists() && size > 0 && duration > 0) {
                    String bucketName = cursor.getString(1);
                    String mimeType = cursor.getString(2);
                    long addDate = cursor.getLong(3);
                    float latitude = cursor.getFloat(4);
                    float longitude = cursor.getFloat(5);
                    int width = cursor.getInt(8);
                    int height = cursor.getInt(9);
                    MoAlbumFile videoFile = new MoAlbumFile();
                    videoFile.setMediaType(MoAlbumFile.TYPE_VIDEO);
                    videoFile.setPath(path);
                    videoFile.setBucketName(bucketName);
                    videoFile.setMimeType(mimeType);
                    videoFile.setAddDate(addDate);
                    videoFile.setLatitude(latitude);
                    videoFile.setLongitude(longitude);
                    videoFile.setSize(size);
                    videoFile.setDuration(duration);
                    videoFile.setWidth(width);
                    videoFile.setHeight(height);
                    allFileFolder.addAlbumFile(videoFile);
                    MoAlbumFolder moAlbumFolder = albumFolderMap.get(bucketName);

                    if (moAlbumFolder != null) {
                        moAlbumFolder.addAlbumFile(videoFile);
                        moAlbumFolder.setIsCamera(false);
                    } else {
                        moAlbumFolder = new MoAlbumFolder();
                        moAlbumFolder.setName(bucketName);
                        moAlbumFolder.addAlbumFile(videoFile);
                        moAlbumFolder.setIsCamera(false);

                        albumFolderMap.put(bucketName, moAlbumFolder);
                    }
                }
            }
            cursor.close();
        }
    }

    /**
     * Scan the list of pictures in the library.
     */
    @WorkerThread
    public ArrayList<MoAlbumFolder> getAllImage() {
        Map<String, MoAlbumFolder> albumFolderMap = new HashMap<>();
        MoAlbumFolder allFileFolder = new MoAlbumFolder();
        allFileFolder.setChecked(true);
        allFileFolder.setName(mContext.getString(R.string.album_all_images));

        scanImageFile(albumFolderMap, allFileFolder);

        ArrayList<MoAlbumFolder> moAlbumFolders = new ArrayList<>();
        Collections.sort(allFileFolder.getAlbumFiles());
        moAlbumFolders.add(allFileFolder);

        for (Map.Entry<String, MoAlbumFolder> folderEntry : albumFolderMap.entrySet()) {
            MoAlbumFolder moAlbumFolder = folderEntry.getValue();
            Collections.sort(moAlbumFolder.getAlbumFiles());
            moAlbumFolders.add(moAlbumFolder);
        }
        return moAlbumFolders;
    }

    /**
     * Scan the list of videos in the library.
     */
    @WorkerThread
    public ArrayList<MoAlbumFolder> getAllVideo() {
        Map<String, MoAlbumFolder> albumFolderMap = new HashMap<>();
        MoAlbumFolder allFileFolder = new MoAlbumFolder();
        allFileFolder.setChecked(true);
        allFileFolder.setName(mContext.getString(R.string.album_all_videos));

        scanVideoFile(albumFolderMap, allFileFolder);

        ArrayList<MoAlbumFolder> moAlbumFolders = new ArrayList<>();
        Collections.sort(allFileFolder.getAlbumFiles());
        moAlbumFolders.add(allFileFolder);

        for (Map.Entry<String, MoAlbumFolder> folderEntry : albumFolderMap.entrySet()) {
            MoAlbumFolder moAlbumFolder = folderEntry.getValue();
            Collections.sort(moAlbumFolder.getAlbumFiles());
            moAlbumFolders.add(moAlbumFolder);
        }
        return moAlbumFolders;
    }

    /**
     * Get all the multimedia files, including videos and pictures.
     */
    @WorkerThread
    public ArrayList<MoAlbumFolder> getAllMedia() {
        Map<String, MoAlbumFolder> albumFolderMap = new HashMap<>();
        MoAlbumFolder allFileFolder = new MoAlbumFolder();
        allFileFolder.setChecked(true);
        allFileFolder.setName(mContext.getString(R.string.album_all_images_videos));

        scanImageFile(albumFolderMap, allFileFolder);
        scanVideoFile(albumFolderMap, allFileFolder);

        ArrayList<MoAlbumFolder> moAlbumFolders = new ArrayList<>();
        Collections.sort(allFileFolder.getAlbumFiles());
//        moAlbumFolders.add(allFileFolder);

        for (Map.Entry<String, MoAlbumFolder> folderEntry : albumFolderMap.entrySet()) {
            MoAlbumFolder moAlbumFolder = folderEntry.getValue();
            Collections.sort(moAlbumFolder.getAlbumFiles());
            moAlbumFolders.add(moAlbumFolder);
        }
        return moAlbumFolders;
    }

}