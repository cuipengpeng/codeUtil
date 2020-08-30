/*
 * Copyright 2016 Yan Zhenjie.
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
package com.test.xcamera.moalbum.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.test.xcamera.bean.MoAlbumItem;

import java.util.ArrayList;

/**
 * <p>Album folder, contains selected status and pictures.</p>
 * Created by Yan Zhenjie on 2016/10/14.
 */
public class MoAlbumFolder implements Parcelable {

    /**
     * Folder name.
     */
    private String name;
    /**
     * Image list in folder.
     */
    private ArrayList<MoAlbumFile> mMoAlbumFiles = new ArrayList<>();
    private ArrayList<MoAlbumItem> moAlbumItems = new ArrayList<>();
    /**
     * checked.
     */
    private boolean isChecked;

    // 当前相册的类型 0:APP相册  1:相机相册  2:手机其他相册
    private int mType;
    private String mThumbnailUri;
    private int mItemCount;
    private boolean mIsCamera;

    public MoAlbumFolder() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<MoAlbumFile> getAlbumFiles() {
        return mMoAlbumFiles;
    }

    public void addAlbumFile(MoAlbumFile mAlbumFile) {
        mMoAlbumFiles.add(mAlbumFile);
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public int getType() {
        return mType;
    }

    public void setType(int mType) {
        this.mType = mType;
    }

    public String getThumbnailUri() {
        if (mMoAlbumFiles != null && !mMoAlbumFiles.isEmpty()) {
            setThumbnailUri(mMoAlbumFiles.get(0).getPath());
        }
        return mThumbnailUri;
    }

    public void setThumbnailUri(String mThumbnailUri) {
        this.mThumbnailUri = mThumbnailUri;
    }

    public boolean isCameraAlbum() {
        return mType == 1;
    }

    public int getCount() {
        if (mMoAlbumFiles != null && !mMoAlbumFiles.isEmpty()) {
            setItemCount(mMoAlbumFiles.size());
        }
        return mItemCount;
    }

    public void setItemCount(int mItemCount) {
        this.mItemCount = mItemCount;
    }

    public boolean isCamera() {
        return mIsCamera;
    }

    public void setIsCamera(boolean mIsCamera) {
        this.mIsCamera = mIsCamera;
    }

    protected MoAlbumFolder(Parcel in) {
        name = in.readString();
        mMoAlbumFiles = in.createTypedArrayList(MoAlbumFile.CREATOR);
        isChecked = in.readByte() != 0;
    }

    public ArrayList<MoAlbumItem> getMoAlbumItems() {
        return moAlbumItems;
    }

    public void setMoAlbumItems(ArrayList<MoAlbumItem> moAlbumItems) {
        this.moAlbumItems = moAlbumItems;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeTypedList(mMoAlbumFiles);
        dest.writeByte((byte) (isChecked ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MoAlbumFolder> CREATOR = new Creator<MoAlbumFolder>() {
        @Override
        public MoAlbumFolder createFromParcel(Parcel in) {
            return new MoAlbumFolder(in);
        }

        @Override
        public MoAlbumFolder[] newArray(int size) {
            return new MoAlbumFolder[size];
        }
    };
}