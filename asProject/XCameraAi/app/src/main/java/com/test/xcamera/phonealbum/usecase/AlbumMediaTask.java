package com.test.xcamera.phonealbum.usecase;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.editvideo.MediaData;
import com.editvideo.MediaUtils;
import com.test.xcamera.bean.AlbumDirectory;
import com.test.xcamera.utils.AlbumHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlbumMediaTask extends AsyncTask <Void, Void, Map<String, AlbumDirectory>>{
    private AlbumHelper.LocalMediaCallbackByDifferent mLocalMediaCallback;
    private Context mActivity;
    public AlbumMediaTask(Context activity, AlbumHelper.LocalMediaCallbackByDifferent localMediaCallback){
        mLocalMediaCallback=localMediaCallback;
        mActivity=activity;
    }
    @Override
    protected  Map<String, AlbumDirectory> doInBackground(Void... params) {
        //图片
        final List<MediaData> allMediaList = new ArrayList<>();

        final List<MediaData> allPhotoList = MediaUtils.getInstance().getPhotoInfo(mActivity);
        final List<MediaData> allVideoList = MediaUtils.getInstance().getVideoInfo(mActivity);

        //下面是排序
        if(allPhotoList!=null){
            allMediaList.addAll(allPhotoList);
        }
        if(allVideoList!=null){
            allMediaList.addAll(allVideoList);
        }
        allMediaList.addAll(allVideoList);
        //采用冒泡排序的方式排列数据
        MediaUtils.getInstance().sortByTimeRepoList(allMediaList);

        Map<String, AlbumDirectory> map = new HashMap<>();

        for (MediaData media : allMediaList) {

            String bucketId = media.getBucketId();
            String bucketName = media.getBucketName();
            AlbumDirectory bucket = map.get(media.getBucketId());
            if (bucket == null) {
                bucket = new AlbumDirectory();
                map.put(bucketId, bucket);
                bucket.setImageList(new ArrayList<MediaData>());
                bucket.setBucketName(bucketName);
            }
            bucket.getImageList().add(media);
        }

        return map;
    }

    @Override
    protected void onPostExecute( Map<String, AlbumDirectory> mediaData) {
        Log.i("club","club:LocalMediaCallback:"+mediaData.size());
        if(mLocalMediaCallback!=null){
            mLocalMediaCallback.onLocalMediaCallbackByDifferent(mediaData);
        }
    }
}
