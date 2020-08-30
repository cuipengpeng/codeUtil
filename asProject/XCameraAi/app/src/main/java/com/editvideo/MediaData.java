package com.editvideo;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.os.Parcel;
import android.support.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by CaoZhiChao on 2018/6/5 11:07
 */
@SuppressLint("ParcelCreator")
public class MediaData implements  IThumbViewInfo{
    private int id;
    private int type;
    private long imageSize; //相机图片大小
    private long videoSize; //相机视频大小
    private String path;
    private String remoteVideoUri;
    private String remoteImageUri;
    private String thumbPath;
    private long duration;
    private long data;
    private String displayName;
    private boolean state = false;  //是否被选择
    private boolean remoteData = false;  //是否是相机数据。相机图片加载必须用picasso，本地图片加载用glide
    private boolean showingDragItem = false;  //是否是正在显示的放大的item
    private boolean showEditItemIcon = false;  //显示编辑item的icon
    private int position; //记录点击时显示的大小的位置
    private String bucketName ; //专辑的名字
    private String bucketId ;//专辑的id
    private Rect mBounds; // 记录坐标  大图预览用
    private String mDownloadFileName; //从相机端下载的文件名

    private String remotePath;
    private long trimIn;
    private float m_speed=1;
    private int rotate;

    public int getRotate() {
        return rotate;
    }

    public void setRotate(int rotate) {
        this.rotate = rotate;
    }

    public float getM_speed() {
        return m_speed;
    }

    public void setM_speed(float m_speed) {
        this.m_speed = m_speed;
    }

    public String getmDownloadFileName() {
        return mDownloadFileName;
    }

    public void setmDownloadFileName(String mDownloadFileName) {
        this.mDownloadFileName = mDownloadFileName;
    }

    public boolean isShowEditItemIcon() {
        return showEditItemIcon;
    }

    public void setShowEditItemIcon(boolean showEditItemIcon) {
        this.showEditItemIcon = showEditItemIcon;
    }

    public String getRemoteImageUri() {
        return remoteImageUri;
    }

    public void setRemoteImageUri(String remoteImageUri) {
        this.remoteImageUri = remoteImageUri;
    }

    public long getImageSize() {
        return imageSize;
    }

    public void setImageSize(long imageSize) {
        this.imageSize = imageSize;
    }

    public long getVideoSize() {
        return videoSize;
    }

    public void setVideoSize(long videoSize) {
        this.videoSize = videoSize;
    }

    public boolean isShowingDragItem() {
        return showingDragItem;
    }

    public void setShowingDragItem(boolean showingDragItem) {
        this.showingDragItem = showingDragItem;
    }

    public void setBounds(Rect bounds) {
        mBounds = bounds;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getBucketId() {
        return bucketId;
    }

    public void setBucketId(String bucketId) {
        this.bucketId = bucketId;
    }

    public boolean isRemoteData() {
        return remoteData;
    }

    public void setRemoteData(boolean remoteData) {
        this.remoteData = remoteData;
    }
    public String getRemoteVideoUri() {
        return remoteVideoUri;
    }

    public void setRemoteVideoUri(String remoteVideoUri) {
        this.remoteVideoUri = remoteVideoUri;
    }

    public long getTrimIn() {
        return trimIn;
    }

    public void setTrimIn(long trimIn) {
        this.trimIn = trimIn;
    }

    public MediaData() {
    }

    //图片是视频的区别就是没有duration
    public MediaData(int id, int type, String path, String thumbPath, long data, String displayName, boolean state) {
        this.id = id;
        this.type = type;
        this.path = path;
        this.thumbPath = thumbPath;
        this.data = data;
        this.displayName = displayName;
        this.state = state;
        this.duration = -1;
        this.m_speed=1;
        trimIn=0;
    }

    //视频
    public MediaData(int id, int type, String path, String thumbPath, long duration, long data, String displayName, boolean state) {
        this.id = id;
        this.type = type;
        this.path = path;
        this.thumbPath = thumbPath;
        this.duration = duration;
        this.data = data;
        this.displayName = displayName;
        this.state = state;
        trimIn=0;
        this.m_speed=1;
    }




    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getDate() {
        String yearMonthDate ="yyyy-MM-dd";
        return new SimpleDateFormat(yearMonthDate)
                .format(new Date(data));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPath() {
        return path == null ? "" : path;
    }

    public void setPath(String path) {
        this.path = path == null ? "" : path;
    }

    public String getRemotePath() {
        return remotePath == null ? "" : remotePath;
    }

    public void setRemotePath(String remotePath) {
        this.remotePath = remotePath == null ? "" : remotePath;

    }

    public String getThumbPath() {
        return thumbPath == null ? "" : thumbPath;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath == null ? "" : thumbPath;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public long getData() {
        return data;
    }

    public void setData(long data) {
        this.data = data;
    }

    public String getDisplayName() {
        return displayName == null ? "" : displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName == null ? "" : displayName;
    }

    @Override
    public String toString() {
        return id+"--"+type+"--"+path+"--"+thumbPath+"--"+data+"--"+displayName+"--"+state+"--"+"--"+position+"--"+"--"+"--"+"--";
    }

    @Override
    public String getUrl() {
        return path;
    }

    @Override
    public Rect getBounds() {
        return mBounds;
    }

    @Nullable
    @Override
    public String getVideoUrl() {
        return path;
    }




    /* --------*/


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.type);
        dest.writeString(this.path);
        dest.writeString(this.thumbPath);
        dest.writeLong(this.duration);
        dest.writeLong(this.data);
        dest.writeString(this.displayName);
        dest.writeByte(this.state ? (byte) 1 : (byte) 0);
        dest.writeInt(this.position);
        dest.writeString(this.bucketName);
        dest.writeString(this.bucketId);
        dest.writeParcelable(this.mBounds, flags);
    }

    protected MediaData(Parcel in) {
        this.id = in.readInt();
        this.type = in.readInt();
        this.path = in.readString();
        this.thumbPath = in.readString();
        this.duration = in.readLong();
        this.data = in.readLong();
        this.displayName = in.readString();
        this.state = in.readByte() != 0;
        this.position = in.readInt();
        this.bucketName = in.readString();
        this.bucketId = in.readString();
        this.mBounds = in.readParcelable(Rect.class.getClassLoader());
    }

    public static final Creator<MediaData> CREATOR = new Creator<MediaData>() {
        @Override
        public MediaData createFromParcel(Parcel source) {
            return new MediaData(source);
        }

        @Override
        public MediaData[] newArray(int size) {
            return new MediaData[size];
        }
    };

    public MediaData clone(){
        MediaData mediaData = new MediaData();
        mediaData.setId(this.id);
        mediaData.setType(this.getType());
        mediaData.setImageSize(this.getImageSize());
        mediaData.setVideoSize(this.getVideoSize());
        mediaData.setPath(this.getPath());
        mediaData.setRemoteVideoUri(this.getRemoteVideoUri());
        mediaData.setRemoteImageUri(this.getRemoteImageUri());
        mediaData.setThumbPath(this.getThumbPath());
        mediaData.setDuration(this.getDuration());
        mediaData.setData(this.getData());
        mediaData.setDisplayName(this.getDisplayName());
        mediaData.setRemoteData(this.isRemoteData());
        mediaData.setPosition(this.getPosition());
        mediaData.setBounds(this.getBounds());
        mediaData.setBucketId(this.getBucketId());
        mediaData.setBucketName(this.getBucketName());
        mediaData.setmDownloadFileName(this.getmDownloadFileName());
        mediaData.setmDownloadFileName(this.getmDownloadFileName());
        mediaData.setTrimIn(this.getTrimIn());
        return mediaData;
    }

}
