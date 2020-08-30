package com.test.xcamera.bean;

import com.framwork.base.BaseResponse;

import java.util.List;

/**
 * Created by smz on 2019/11/4
 *
 * 模板对象
 */

public class NewTemplate extends BaseResponse{


    /**
     * segment_num : 5
     * video_length : 15
     * name : 晒幸福
     * icon : img_cover_template_couples
     * content : 幸福快乐的时光
     * example_video : 1574240464024055.mp4
     * BGM : {"audio_url":"walking with a pet.mp3","volume":"10"}
     * filter : {"model":"0","name":"冰激凌","intensity":1}
     * video_segment : [{"video_segment_length":"3.07","transition":{"model":0,"name":""},"title_or_sticker":{"start_time":"","end_time":"","x":"","y":"","img_url":""},"video_speed":"1","camera_control":[{"time":"","x":"","y":"","z":""}]},{"video_segment_length":"4.03","transition":{"model":0,"name":""},"title_or_sticker":{"start_time":"","end_time":"","x":"","y":"","img_url":""},"video_speed":"1","camera_control":[{"time":"","x":"","y":"","z":""}]},{"video_segment_length":"3.1","transition":{"model":0,"name":""},"title_or_sticker":{"start_time":"","end_time":"","x":"","y":"","img_url":""},"video_speed":"1","camera_control":[{"time":"","x":"","y":"","z":""}]},{"video_segment_length":"4.67","transition":{"model":0,"name":""},"title_or_sticker":{"start_time":"","end_time":"","x":"","y":"","img_url":""},"video_speed":"1","camera_control":[{"time":"","x":"","y":"","z":""}]},{"video_segment_length":"0.13","transition":{"model":0,"name":""},"title_or_sticker":{"start_time":"","end_time":"","x":"","y":"","img_url":""},"video_speed":"1","camera_control":[{"time":"","x":"","y":"","z":""}]}]
     */

    private int segment_num;
    private int video_length;
    private String name;
    private String icon;
    private String content;
    private String example_video;
    private BGMBean BGM;
    private FilterBean filter;
    private boolean isCheck; //是否被点

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    private List<VideoSegmentBean> video_segment;

    public int getSegment_num() {
        return segment_num;
    }

    public void setSegment_num(int segment_num) {
        this.segment_num = segment_num;
    }

    public int getVideo_length() {
        return video_length;
    }

    public void setVideo_length(int video_length) {
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

    public BGMBean getBGM() {
        return BGM;
    }

    public void setBGM(BGMBean BGM) {
        this.BGM = BGM;
    }

    public FilterBean getFilter() {
        return filter;
    }

    public void setFilter(FilterBean filter) {
        this.filter = filter;
    }

    public List<VideoSegmentBean> getVideo_segment() {
        return video_segment;
    }

    public void setVideo_segment(List<VideoSegmentBean> video_segment) {
        this.video_segment = video_segment;
    }

    public static class BGMBean {
        /**
         * audio_url : walking with a pet.mp3
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

    public static class FilterBean {
        /**
         * model : 0
         * name : 冰激凌
         * intensity : 1
         */

        private String model;
        private String name;
        private int intensity;

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

        public int getIntensity() {
            return intensity;
        }

        public void setIntensity(int intensity) {
            this.intensity = intensity;
        }
    }

    public static class VideoSegmentBean {
        /**
         * video_segment_length : 3.07
         * transition : {"model":0,"name":""}
         * title_or_sticker : {"start_time":"","end_time":"","x":"","y":"","img_url":""}
         * video_speed : 1
         * camera_control : [{"time":"","x":"","y":"","z":""}]
         */

        private String video_segment_length;
        private TransitionBean transition;
        private TitleOrStickerBean title_or_sticker;
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

        public TitleOrStickerBean getTitle_or_sticker() {
            return title_or_sticker;
        }

        public void setTitle_or_sticker(TitleOrStickerBean title_or_sticker) {
            this.title_or_sticker = title_or_sticker;
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

        public static class TransitionBean {
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

        public static class TitleOrStickerBean {
            /**
             * start_time :
             * end_time :
             * x :
             * y :
             * img_url :
             */

            private String start_time;
            private String end_time;
            private String x;
            private String y;
            private String img_url;

            public String getStart_time() {
                return start_time;
            }

            public void setStart_time(String start_time) {
                this.start_time = start_time;
            }

            public String getEnd_time() {
                return end_time;
            }

            public void setEnd_time(String end_time) {
                this.end_time = end_time;
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

            public String getImg_url() {
                return img_url;
            }

            public void setImg_url(String img_url) {
                this.img_url = img_url;
            }
        }

        public static class CameraControlBean {
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
}
