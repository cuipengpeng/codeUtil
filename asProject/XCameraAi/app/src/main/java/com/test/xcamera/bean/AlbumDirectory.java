package com.test.xcamera.bean;


import com.editvideo.MediaData;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by smz on 2019/7/5.
 */

public class AlbumDirectory implements Serializable {
    private String bucketName;
    private ArrayList<MediaData> imageList = new ArrayList<>();
    private int count = 0;
    public int index;

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public ArrayList<MediaData> getImageList() {
        return imageList;
    }

    public void setImageList(ArrayList<MediaData> imageList) {
        this.imageList = imageList;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
