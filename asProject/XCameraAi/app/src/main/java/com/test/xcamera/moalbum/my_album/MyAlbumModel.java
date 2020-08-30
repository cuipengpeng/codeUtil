package com.test.xcamera.moalbum.my_album;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.test.xcamera.R;
import com.test.xcamera.bean.MoAlbumItem;
import com.test.xcamera.bean.MoImage;
import com.test.xcamera.bean.MoVideo;
import com.test.xcamera.managers.ConnectionManager;
import com.test.xcamera.moalbum.bean.MoAlbumFile;
import com.test.xcamera.moalbum.bean.MoAlbumFolder;
import com.test.xcamera.moalbum.data.MediaReader;
import com.test.xcamera.mointerface.MoAlbumListCallback;
import com.test.xcamera.mointerface.MoRequestCallback;
import com.test.xcamera.utils.Constants;

import java.util.ArrayList;

/**
 * Created by zhouxuecheng on
 * Create Time 2020/7/23
 * e-mail zhouxuecheng1991@163.com
 */

public class MyAlbumModel {
    private final MyAlbumInterface.ModelToPresenter modelToPresenter;
    private final Context context;
    private MediaReader mMediaReader;
    private final String MEDIA_IMAGE_TYPE = "image";
    private final String MEDIA_VIDEO_TYPE = "video";

    public MyAlbumModel(Context context, MyAlbumInterface.ModelToPresenter modelToPresenter) {
        this.modelToPresenter = modelToPresenter;
        this.context = context;
        mMediaReader = new MediaReader(context);
    }

    /**
     * 这个方法获取的只是本地的数据
     */
    public void getMobileAlbumListData() {
        ArrayList<MoAlbumFolder> allMedia = mMediaReader.getAllMedia();
        new MyPackageThread(allMedia).start();
    }

    private class MyPackageThread extends Thread {
        private final ArrayList<MoAlbumFolder> moAlbumFolders;
        private boolean isAddAppAlbumItem;
        private ArrayList<MoAlbumFolder> requestAlbumFolders;

        public MyPackageThread(ArrayList<MoAlbumFolder> moAlbumFolders) {
            this.moAlbumFolders = moAlbumFolders;
            this.requestAlbumFolders = new ArrayList<>();
        }

        @Override
        public void run() {
            MoAlbumFolder folder = null;
            for (int i = 0; i < moAlbumFolders.size(); i++) {
                folder = moAlbumFolders.get(i);
                if (!TextUtils.isEmpty(folder.getName()) &&
                        folder.getName().contains(Constants.appDir)) {
                    folder.setIsCamera(false);
                    isAddAppAlbumItem = true;
                    folder.setName(context.getResources().getString(R.string.app_gallery));
                    folder.setType(0);

                    ArrayList<MoAlbumItem> albumItems = getMoAlbumItems(folder);
                    folder.setMoAlbumItems(albumItems);

                    requestAlbumFolders.add(0, folder);
                    break;
                }
            }

            if (!isAddAppAlbumItem) {
                folder = new MoAlbumFolder();
                folder.setName(context.getResources().getString(R.string.app_gallery));
                folder.setType(0);
                folder.setIsCamera(false);
                folder.setItemCount(0);
                folder.setThumbnailUri(null);
                requestAlbumFolders.add(0, folder);
            }

            modelToPresenter.setMobileAlbumData(folder);
        }

        /**
         * 对该文件夹中的数据进行分类组装成外面需要使用的数据
         *
         * @param folder
         * @return
         */
        @NonNull
        private ArrayList<MoAlbumItem> getMoAlbumItems(MoAlbumFolder folder) {
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
            return albumItems;
        }
    }

    public void stopFpvMode() {
        ConnectionManager.getInstance().appFpvMode(0, new MoRequestCallback() {
            @Override
            public void onSuccess() {
                modelToPresenter.stopFpvModeSuccess();
            }

            @Override
            public void onFailed() {
                modelToPresenter.stopFpvModeFailed();
            }
        });
    }


    public void stopFpvPreview() {
        ConnectionManager.getInstance().stopPreview(new MoRequestCallback() {
            @Override
            public void onSuccess() {
                modelToPresenter.stopFpvPreviewSuccess();
            }

            @Override
            public void onFailed() {
                modelToPresenter.stopFpvPreviewFailed();
            }
        });
    }

    /**
     * 请求相机数据
     */
    public void getCameraAlbumListData(int page) {
        ConnectionManager.getInstance().getAlbumList(new MoAlbumListCallback() {
            @Override
            public void onSuccess(ArrayList<MoAlbumItem> items) {
                MoAlbumFolder folder = new MoAlbumFolder();
                folder.setName(context.getResources().getString(R.string.camera_album));
                folder.setType(1);
                folder.setIsCamera(true);
                folder.setItemCount(0);
                folder.setMoAlbumItems(items);

                modelToPresenter.setCameraAlbumData(folder, page);
            }

            @Override
            public void onFailed() {
                modelToPresenter.interfaceError("getAlbumList error");
            }
        }, page);
    }
}
