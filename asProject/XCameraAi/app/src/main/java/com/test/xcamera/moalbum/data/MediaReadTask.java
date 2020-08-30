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

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.test.xcamera.R;
import com.test.xcamera.accrssory.AccessoryManager;
import com.test.xcamera.bean.MoAlbumItem;
import com.test.xcamera.bean.MoImage;
import com.test.xcamera.bean.MoVideo;
import com.test.xcamera.managers.ConnectionManager;
import com.test.xcamera.moalbum.bean.MoAlbumFile;
import com.test.xcamera.moalbum.bean.MoAlbumFolder;
import com.test.xcamera.utils.Constants;

import java.util.ArrayList;


public class MediaReadTask extends Thread {

    private final Context context;
    private final String MEDIA_IMAGE_TYPE = "image";
    private final String MEDIA_VIDEO_TYPE = "video";
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            ResultWrapper wrapper = (ResultWrapper) msg.obj;
            onPostExecute(wrapper);
        }
    };

    public interface Callback {
        /**
         * Callback the results.
         *
         * @param moAlbumFolders album folder list.
         */
//        void onScanCallback(ArrayList<MoAlbumFolder> moAlbumFolders, ArrayList<MoAlbumFile> checkedFiles);
        void onScanCallback(ArrayList<MoAlbumFolder> moAlbumFolders);

        void onSuccess(int count, MoImage thumbnail);

        void onFailed();
    }

    static class ResultWrapper {
        private ArrayList<MoAlbumFolder> mMoAlbumFolders;
        private ArrayList<MoAlbumFile> mMoAlbumFiles;
    }

    //    private int mFunction;
//    private List<MoAlbumFile> mCheckedFiles;
    private MediaReader mMediaReader;
    private Callback mCallback;
    private boolean isAddAppAlbumItem = false;

    public MediaReadTask(Context context, MediaReader mediaReader, Callback callback) {
        this.context = context;
        this.mMediaReader = mediaReader;
        this.mCallback = callback;
    }

    public void execute() {
        this.start();
    }

    @Override
    public void run() {
        ArrayList<MoAlbumFolder> moAlbumFolders = mMediaReader.getAllMedia();
        ArrayList<MoAlbumFile> checkedFiles = new ArrayList<>();

        ResultWrapper wrapper = new ResultWrapper();
        wrapper.mMoAlbumFolders = moAlbumFolders;
        wrapper.mMoAlbumFiles = checkedFiles;

        for (int i = 0; i < wrapper.mMoAlbumFolders.size(); i++) {
            MoAlbumFolder folder = wrapper.mMoAlbumFolders.get(i);
            if (TextUtils.isEmpty(folder.getName())) {
                folder.setName("");
            }
            folder.setIsCamera(false);
            if (folder.getName() != null && folder.getName().contains(Constants.appDir)) {
                isAddAppAlbumItem = true;
                folder = wrapper.mMoAlbumFolders.remove(i);
                folder.setName(context.getResources().getString(R.string.app_gallery));
                folder.setType(0);
                wrapper.mMoAlbumFolders.add(0, folder);
            } else {
                folder.setType(2);
            }
            ArrayList<MoAlbumItem> albumItems = new ArrayList<>();
            for (int j = 0; j < folder.getAlbumFiles().size(); j++) {
                MoAlbumFile file = folder.getAlbumFiles().get(j);
                MoAlbumItem item = new MoAlbumItem();
                if (file.getMediaType() == MoAlbumFile.TYPE_IMAGE) {
                    item.setmCreateTime(file.getAddDate());
                    item.setmType(MEDIA_IMAGE_TYPE);
                    MoImage image = new MoImage();
                    image.setmUri(file.getPath());
                    image.setmSize(file.getSize());
                    item.setmImage(image);
                    MoImage thumbnail = new MoImage();
                    thumbnail.setmUri(file.getPath());
                    item.setmThumbnail(thumbnail);
                    item.setmIsCamrea(false);
                } else {
                    item.setmCreateTime(file.getAddDate());
                    item.setmType(MEDIA_VIDEO_TYPE);
                    MoVideo video = new MoVideo();
                    video.setmUri(file.getPath());
                    video.setmSize(file.getSize());
                    video.setmHeight(file.getHeight());
                    video.setmWidth(file.getWidth());
                    video.setmDuration((int) file.getDuration());
                    item.setmVideo(video);
                    MoImage thumbnail = new MoImage();
                    thumbnail.setmUri(file.getPath());
                    item.setmThumbnail(thumbnail);
                    item.setmIsCamrea(false);
                }
                albumItems.add(item);
            }
            folder.setMoAlbumItems(albumItems);
        }
        if (!isAddAppAlbumItem) {
            MoAlbumFolder folder = new MoAlbumFolder();
            folder.setName(context.getResources().getString(R.string.app_gallery));
            folder.setType(0);
            folder.setIsCamera(false);
            folder.setItemCount(0);
            folder.setThumbnailUri(null);
            wrapper.mMoAlbumFolders.add(0, folder);
        }
        Message message = Message.obtain();
        message.obj = wrapper;
        handler.sendMessage(message);
//        return wrapper;
    }

//    @Override
//    protected ResultWrapper doInBackground(Void... params) {
//        ArrayList<MoAlbumFolder> moAlbumFolders = mMediaReader.getAllMedia();
//        ArrayList<MoAlbumFile> checkedFiles = new ArrayList<>();
//
//        ResultWrapper wrapper = new ResultWrapper();
//        wrapper.mMoAlbumFolders = moAlbumFolders;
//        wrapper.mMoAlbumFiles = checkedFiles;
//
//        for (int i = 0; i < wrapper.mMoAlbumFolders.size(); i++) {
//            MoAlbumFolder folder = wrapper.mMoAlbumFolders.get(i);
//            if (TextUtils.isEmpty(folder.getName())) {
//                folder.setName("");
//            }
//            folder.setIsCamera(false);
//            if (folder.getName() != null && folder.getName().contains(Constants.appDir)) {
//                isAddAppAlbumItem = true;
//                folder = wrapper.mMoAlbumFolders.remove(i);
//                folder.setName(context.getResources().getString(R.string.app_gallery));
//                folder.setType(0);
//                wrapper.mMoAlbumFolders.add(0, folder);
//            } else {
//                folder.setType(2);
//            }
//            ArrayList<MoAlbumItem> albumItems = new ArrayList<>();
//            for (int j = 0; j < folder.getAlbumFiles().size(); j++) {
//                MoAlbumFile file = folder.getAlbumFiles().get(j);
//                MoAlbumItem item = new MoAlbumItem();
//                if (file.getMediaType() == MoAlbumFile.TYPE_IMAGE) {
//                    item.setmCreateTime(file.getAddDate());
//                    item.setmType(MEDIA_IMAGE_TYPE);
//                    MoImage image = new MoImage();
//                    image.setmUri(file.getPath());
//                    image.setmSize(file.getSize());
//                    item.setmImage(image);
//                    MoImage thumbnail = new MoImage();
//                    thumbnail.setmUri(file.getPath());
//                    item.setmThumbnail(thumbnail);
//                    item.setmIsCamrea(false);
//                } else {
//                    item.setmCreateTime(file.getAddDate());
//                    item.setmType(MEDIA_VIDEO_TYPE);
//                    MoVideo video = new MoVideo();
//                    video.setmUri(file.getPath());
//                    video.setmSize(file.getSize());
//                    video.setmHeight(file.getHeight());
//                    video.setmWidth(file.getWidth());
//                    video.setmDuration((int) file.getDuration());
//                    item.setmVideo(video);
//                    MoImage thumbnail = new MoImage();
//                    thumbnail.setmUri(file.getPath());
//                    item.setmThumbnail(thumbnail);
//                    item.setmIsCamrea(false);
//                }
//                albumItems.add(item);
//            }
//            folder.setMoAlbumItems(albumItems);
//        }
//        if (!isAddAppAlbumItem) {
//            MoAlbumFolder folder = new MoAlbumFolder();
//            folder.setName(context.getResources().getString(R.string.app_gallery));
//            folder.setType(0);
//            folder.setIsCamera(false);
//            folder.setItemCount(0);
//            folder.setThumbnailUri(null);
//            wrapper.mMoAlbumFolders.add(0, folder);
//        }
//        return wrapper;
//    }


    private void onPostExecute(ResultWrapper wrapper) {
        if (AccessoryManager.getInstance().mIsRunning) {

            albumListCallBack(wrapper, 0, null, true);

            ConnectionManager.getInstance().albumCount(new ConnectionManager.AlbumCountListener() {
                @Override
                public void onSuccess(int count, MoImage thumbnail) {
                    if (mCallback != null) {
                        mCallback.onSuccess(count, thumbnail);
                    }


                    //albumListCallBack(wrapper, count > 0 ? count : 0, count > 0 ? thumbnail.getmUri() : null, true);
                }

                @Override
                public void onFailed() {
                    if (mCallback != null) {
                        mCallback.onFailed();
                    }
                    // albumListCallBack(wrapper, 0, null, true);
                }
            });
        } else {
            albumListCallBack(wrapper, 0, null, false);
        }
    }

    private void albumListCallBack(ResultWrapper wrapper, int count, String thumbnail, boolean isAdd) {
        MoAlbumFolder folder = new MoAlbumFolder();
        folder.setName("相机相册");
        folder.setType(1);
        folder.setIsCamera(true);
        folder.setItemCount(count);
        folder.setThumbnailUri(thumbnail);
        if (isAdd) {
            if (wrapper.mMoAlbumFolders.size() > 0)
                wrapper.mMoAlbumFolders.add(0, folder);
            else
                wrapper.mMoAlbumFolders.add(folder);
        }
        mCallback.onScanCallback(wrapper.mMoAlbumFolders);
    }

}