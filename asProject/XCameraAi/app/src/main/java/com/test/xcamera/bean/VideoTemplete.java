package com.test.xcamera.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.test.xcamera.cameraclip.bean.VideoSegment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class VideoTemplete implements Parcelable {

    /**
     * segment_num : 14
     * video_length : 31.16
     * name : 生活的仪式感
     * icon : template3.png
     * content : 邂逅生活的仪式感，传释激情的律动。
     * example_video : template3.mp4
     * bgm : {"audio_url":"template333.mp4","volume":"10"}
     * filter : {"model":"0","name":"鲜艳","intensity":0.8}
     * caption_list : [{"in_point":0,"duration":1.8,"caption_text":"VLOG 01"},{"in_point":1,"duration":2.8,"caption_text":"MONDY"},{"in_point":2,"duration":3.8,"caption_text":"12/JANUARY"},{"in_point":27.68,"duration":1.16,"caption_text":"BEIJING"},{"in_point":28.84,"duration":1.16,"caption_text":"2020.01.12"},{"in_point":30,"duration":1.16,"caption_text":"PM 14.30"}]
     * video_segment : [{"video_segment_length":"1.35","transition":{"model":0,"name":""},"video_speed":"1","camera_control":[{"time":"","x":"","y":"","z":""}]},{"video_segment_length":"0.8","transition":{"model":0,"name":"向下甩入"},"video_speed":"1","camera_control":[{"time":"","x":"","y":"","z":""}]},{"video_segment_length":"0.23","transition":{"model":0,"name":""},"video_speed":"1","camera_control":[{"time":"","x":"","y":"","z":""}]},{"video_segment_length":"1.66","transition":{"model":0,"name":""},"video_speed":"1","camera_control":[{"time":"","x":"","y":"","z":""}]},{"video_segment_length":"1.07","transition":{"model":0,"name":""},"video_speed":"1","camera_control":[{"time":"","x":"","y":"","z":""}]},{"video_segment_length":"0.25","transition":{"model":0,"name":"右旋转"},"video_speed":"1","camera_control":[{"time":"","x":"","y":"","z":""}]},{"video_segment_length":"0.68","transition":{"model":0,"name":"色差放大"},"video_speed":"1","camera_control":[{"time":"","x":"","y":"","z":""}]},{"video_segment_length":"0.55","transition":{"model":0,"name":""},"video_speed":"1","camera_control":[{"time":"","x":"","y":"","z":""}]},{"video_segment_length":"0.89","transition":{"model":0,"name":"快速上移"},"video_speed":"1","camera_control":[{"time":"","x":"","y":"","z":""}]},{"video_segment_length":"1.03","transition":{"model":0,"name":"快速左移"},"video_speed":"1","camera_control":[{"time":"","x":"","y":"","z":""}]},{"video_segment_length":"6.58","transition":{"model":0,"name":"色差放大"},"video_speed":"1","camera_control":[{"time":"","x":"","y":"","z":""}]},{"video_segment_length":"7.10","transition":{"model":0,"name":"淡入淡出"},"video_speed":"1","camera_control":[{"time":"","x":"","y":"","z":""}]},{"video_segment_length":"4.13","transition":{"model":0,"name":"顶点拉伸"},"video_speed":"1","camera_control":[{"time":"","x":"","y":"","z":""}]},{"video_segment_length":"4.84","transition":{"model":0,"name":"模糊缩小"},"video_speed":"1","camera_control":[{"time":"","x":"","y":"","z":""}]}]
     */

    private boolean check=false;
    private boolean hasClickOnce = false;
    private boolean netTemplete = false;
    private boolean shortTemplete = false;
    private int segment_num;
    private int videoWidth=1920;
    private int videoHeight=1080;
    private double video_length;

    public int getVideoWidth() {
        return videoWidth;
    }

    public void setVideoWidth(int videoWidth) {
        this.videoWidth = videoWidth;
    }

    public int getVideoHeight() {
        return videoHeight;
    }

    public void setVideoHeight(int videoHeight) {
        this.videoHeight = videoHeight;
    }
    private String name;
    private String icon;
    private String content;
    private String example_video;
    private float vignette;//暗礁
    private float sharpen;//锐度
    private float exposure;//曝光
    private float saturation;//饱和度
    private float contrast_ratio;//对比度
    private BgmBean bgm;
    private FilterBean filter;
    private List<CaptionListBean> caption_list;
    private List<VideoSegmentBean> video_segment;
    private List<VideoSegment> videoSegmentList = new ArrayList<>();
    private VideoSegment.ThemeType themeType;
    private String localSampleVideoPath;
    private String localZipFilePath;

    private int id;
    private int coverFileId;
    private int videoFileId;
    private String author;
    private String description;
    private int packageFileId;
    private String coverUrl;
    private String videoUrl;
    private int partNum;
    private double duration;
    private int bgmId;

    public float getVignette() {
        return vignette;
    }

    public void setVignette(float vignette) {
        this.vignette = vignette;
    }

    public float getSharpen() {
        return sharpen;
    }

    public void setSharpen(float sharpen) {
        this.sharpen = sharpen;
    }

    public float getExposure() {
        return exposure;
    }

    public void setExposure(float exposure) {
        this.exposure = exposure;
    }

    public float getSaturation() {
        return saturation;
    }

    public void setSaturation(float saturation) {
        this.saturation = saturation;
    }

    public float getContrast_ratio() {
        return contrast_ratio;
    }

    public void setContrast_ratio(float contrast_ratio) {
        this.contrast_ratio = contrast_ratio;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public int getPartNum() {
        return partNum;
    }

    public void setPartNum(int partNum) {
        this.partNum = partNum;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public int getBgmId() {
        return bgmId;
    }

    public void setBgmId(int bgmId) {
        this.bgmId = bgmId;
    }

    public String getLocalZipFilePath() {
        return localZipFilePath;
    }

    public void setLocalZipFilePath(String localZipFilePath) {
        this.localZipFilePath = localZipFilePath;
    }

    public boolean isShortTemplete() {
        return shortTemplete;
    }

    public void setShortTemplete(boolean shortTemplete) {
        this.shortTemplete = shortTemplete;
    }
    public boolean isNetTemplete() {
        return netTemplete;
    }

    public void setNetTemplete(boolean netTemplete) {
        this.netTemplete = netTemplete;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCoverFileId() {
        return coverFileId;
    }

    public void setCoverFileId(int coverFileId) {
        this.coverFileId = coverFileId;
    }

    public int getVideoFileId() {
        return videoFileId;
    }

    public void setVideoFileId(int videoFileId) {
        this.videoFileId = videoFileId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPackageFileId() {
        return packageFileId;
    }

    public void setPackageFileId(int packageFileId) {
        this.packageFileId = packageFileId;
    }

    public String getLocalSampleVideoPath() {
        return localSampleVideoPath;
    }

    public void setLocalSampleVideoPath(String localSampleVideoPath) {
        this.localSampleVideoPath = localSampleVideoPath;
    }

    public VideoSegment.ThemeType getThemeType() {
        return themeType;
    }

    public void setThemeType(VideoSegment.ThemeType themeType) {
        this.themeType = themeType;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public boolean isHasClickOnce() {
        return hasClickOnce;
    }

    public void setHasClickOnce(boolean hasClickOnce) {
        this.hasClickOnce = hasClickOnce;
    }

    public List<VideoSegment> getVideoSegmentList() {
        return videoSegmentList;
    }

    public void setVideoSegmentList(List<VideoSegment> videoSegmentList) {
        this.videoSegmentList = videoSegmentList;
    }

    public int getSegment_num() {
        return segment_num;
    }

    public void setSegment_num(int segment_num) {
        this.segment_num = segment_num;
    }

    public double getVideo_length() {
        return video_length;
    }

    public void setVideo_length(double video_length) {
        this.video_length = video_length;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getExample_video() {
        return example_video;
    }

    public void setExample_video(String example_video) {
        this.example_video = example_video;
    }

    public BgmBean getBgm() {
        return bgm;
    }

    public void setBgm(BgmBean bgm) {
        this.bgm = bgm;
    }

    public FilterBean getFilter() {
        return filter;
    }

    public void setFilter(FilterBean filter) {
        this.filter = filter;
    }

    public List<CaptionListBean> getCaption_list() {
        return caption_list;
    }

    public void setCaption_list(List<CaptionListBean> caption_list) {
        this.caption_list = caption_list;
    }

    public List<VideoSegmentBean> getVideo_segment() {
        return video_segment;
    }

    public void setVideo_segment(List<VideoSegmentBean> video_segment) {
        this.video_segment = video_segment;
    }

    public static class BgmBean  implements Serializable {
        /**
         * audio_url : template333.mp4
         * volume : 10
         */

        private String audio_url;
        private String volume;

        public String getAudio_url() {
            return audio_url;
        }

        public void setAudio_url(String audio_url) {
            this.audio_url = audio_url;
        }

        public String getVolume() {
            return volume;
        }

        public void setVolume(String volume) {
            this.volume = volume;
        }
    }

    public static class FilterBean  implements Serializable {
        /**
         * model : 0
         * name : 鲜艳
         * intensity : 0.8
         */

        private String model;
        private String name;
        private double intensity;

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getIntensity() {
            return intensity;
        }

        public void setIntensity(double intensity) {
            this.intensity = intensity;
        }
    }

    public static class CaptionListBean implements Serializable{
        /**
         * in_point : 0
         * duration : 2.8
         * caption : [{"caption_text":"VLOG 01","caption_text_color":"#ffff1234","caption_Typeface":"123"},{"caption_text":"VLOG 01","caption_text_color":"#ffff1234","caption_Typeface":"123"},{"caption_text":"VLOG 01","caption_text_color":"#ffff1234","caption_Typeface":"123"}]
         * caption_name : 9CBB2257-4532-4AD8-8F63-622CDA959531
         * caption_text : MONDY
         */

        private double in_point;
        private double duration;
        private String caption_name;
        private String caption_text;
        private List<CaptionListBean.CaptionBean> caption = new ArrayList<>();

        public double getIn_point() {
            return in_point;
        }

        public void setIn_point(double in_point) {
            this.in_point = in_point;
        }

        public double getDuration() {
            return duration;
        }

        public void setDuration(double duration) {
            this.duration = duration;
        }

        public String getCaption_name() {
            return caption_name;
        }

        public void setCaption_name(String caption_name) {
            this.caption_name = caption_name;
        }

        public String getCaption_text() {
            return caption_text;
        }

        public void setCaption_text(String caption_text) {
            this.caption_text = caption_text;
        }

        public List<CaptionListBean.CaptionBean> getCaption() {
            return caption;
        }

        public void setCaption(List<CaptionListBean.CaptionBean> caption) {
            this.caption = caption;
        }

        public static class CaptionBean implements Serializable{
            /**
             * caption_text : VLOG 01
             * caption_text_color : #ffff1234
             * caption_Typeface : 123
             */

            private String caption_text;
            private String caption_text_color;
            private String caption_Typeface;

            public String getCaption_text() {
                return caption_text;
            }

            public void setCaption_text(String caption_text) {
                this.caption_text = caption_text;
            }

            public String getCaption_text_color() {
                return caption_text_color;
            }

            public void setCaption_text_color(String caption_text_color) {
                this.caption_text_color = caption_text_color;
            }

            public String getCaption_Typeface() {
                return caption_Typeface;
            }

            public void setCaption_Typeface(String caption_Typeface) {
                this.caption_Typeface = caption_Typeface;
            }
        }
    }

    public static class VideoSegmentBean  implements Serializable {
        /**
         * video_segment_length : 1.35
         * transition : {"model":0,"name":""}
         * video_speed : 1
         * camera_control : [{"time":"","x":"","y":"","z":""}]
         */

        private String video_segment_length;
        private TransitionBean transition;
        private String video_speed;
        private List<CameraControlBean> camera_control;

        public String getVideo_segment_length() {
            return video_segment_length;
        }

        public void setVideo_segment_length(String video_segment_length) {
            this.video_segment_length = video_segment_length;
        }

        public TransitionBean getTransition() {
            return transition;
        }

        public void setTransition(TransitionBean transition) {
            this.transition = transition;
        }

        public String getVideo_speed() {
            return video_speed;
        }

        public void setVideo_speed(String video_speed) {
            this.video_speed = video_speed;
        }

        public List<CameraControlBean> getCamera_control() {
            return camera_control;
        }

        public void setCamera_control(List<CameraControlBean> camera_control) {
            this.camera_control = camera_control;
        }

        public static class TransitionBean  implements Serializable {
            /**
             * model : 0
             * name :
             */

            private int model;
            private String name;

            public int getModel() {
                return model;
            }

            public void setModel(int model) {
                this.model = model;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }

        public static class CameraControlBean  implements Serializable {
            /**
             * time :
             * x :
             * y :
             * z :
             */

            private String time;
            private String x;
            private String y;
            private String z;

            public String getTime() {
                return time;
            }

            public void setTime(String time) {
                this.time = time;
            }

            public String getX() {
                return x;
            }

            public void setX(String x) {
                this.x = x;
            }

            public String getY() {
                return y;
            }

            public void setY(String y) {
                this.y = y;
            }

            public String getZ() {
                return z;
            }

            public void setZ(String z) {
                this.z = z;
            }
        }
    }

    public VideoTemplete() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.check ? (byte) 1 : (byte) 0);
        dest.writeByte(this.hasClickOnce ? (byte) 1 : (byte) 0);
        dest.writeByte(this.netTemplete ? (byte) 1 : (byte) 0);
        dest.writeByte(this.shortTemplete ? (byte) 1 : (byte) 0);
        dest.writeInt(this.segment_num);
        dest.writeInt(this.videoWidth);
        dest.writeInt(this.videoHeight);
        dest.writeDouble(this.video_length);
        dest.writeString(this.name);
        dest.writeString(this.icon);
        dest.writeString(this.content);
        dest.writeString(this.example_video);
        dest.writeFloat(this.vignette);
        dest.writeFloat(this.sharpen);
        dest.writeFloat(this.exposure);
        dest.writeFloat(this.saturation);
        dest.writeFloat(this.contrast_ratio);
        dest.writeSerializable(this.bgm);
        dest.writeSerializable(this.filter);
        dest.writeList(this.caption_list);
        dest.writeList(this.video_segment);
        dest.writeTypedList(this.videoSegmentList);
        dest.writeInt(this.themeType == null ? -1 : this.themeType.ordinal());
        dest.writeString(this.localSampleVideoPath);
        dest.writeString(this.localZipFilePath);
        dest.writeInt(this.id);
        dest.writeInt(this.coverFileId);
        dest.writeInt(this.videoFileId);
        dest.writeString(this.author);
        dest.writeString(this.description);
        dest.writeInt(this.packageFileId);
        dest.writeString(this.coverUrl);
        dest.writeString(this.videoUrl);
        dest.writeInt(this.partNum);
        dest.writeDouble(this.duration);
        dest.writeInt(this.bgmId);
    }

    protected VideoTemplete(Parcel in) {
        this.check = in.readByte() != 0;
        this.hasClickOnce = in.readByte() != 0;
        this.netTemplete = in.readByte() != 0;
        this.shortTemplete = in.readByte() != 0;
        this.segment_num = in.readInt();
        this.videoWidth = in.readInt();
        this.videoHeight = in.readInt();
        this.video_length = in.readDouble();
        this.name = in.readString();
        this.icon = in.readString();
        this.content = in.readString();
        this.example_video = in.readString();
        this.vignette = in.readFloat();
        this.sharpen = in.readFloat();
        this.exposure = in.readFloat();
        this.saturation = in.readFloat();
        this.contrast_ratio = in.readFloat();
        this.bgm = (BgmBean) in.readSerializable();
        this.filter = (FilterBean) in.readSerializable();
        this.caption_list = new ArrayList<CaptionListBean>();
        in.readList(this.caption_list, CaptionListBean.class.getClassLoader());
        this.video_segment = new ArrayList<VideoSegmentBean>();
        in.readList(this.video_segment, VideoSegmentBean.class.getClassLoader());
        this.videoSegmentList = in.createTypedArrayList(VideoSegment.CREATOR);
        int tmpThemeType = in.readInt();
        this.themeType = tmpThemeType == -1 ? null : VideoSegment.ThemeType.values()[tmpThemeType];
        this.localSampleVideoPath = in.readString();
        this.localZipFilePath = in.readString();
        this.id = in.readInt();
        this.coverFileId = in.readInt();
        this.videoFileId = in.readInt();
        this.author = in.readString();
        this.description = in.readString();
        this.packageFileId = in.readInt();
        this.coverUrl = in.readString();
        this.videoUrl = in.readString();
        this.partNum = in.readInt();
        this.duration = in.readDouble();
        this.bgmId = in.readInt();
    }

    public static final Creator<VideoTemplete> CREATOR = new Creator<VideoTemplete>() {
        @Override
        public VideoTemplete createFromParcel(Parcel source) {
            return new VideoTemplete(source);
        }

        @Override
        public VideoTemplete[] newArray(int size) {
            return new VideoTemplete[size];
        }
    };
}
