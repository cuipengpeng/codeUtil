package com.test.xcamera.cameraclip;

import android.os.Handler;
import android.os.Looper;
import android.util.Xml;

import com.test.xcamera.application.AiCameraApplication;
import com.test.xcamera.cameraclip.bean.Rotation;
import com.test.xcamera.cameraclip.bean.VideoFile;
import com.test.xcamera.cameraclip.bean.VideoScoreType;
import com.test.xcamera.cameraclip.bean.VideoSegment;
import com.test.xcamera.cameraclip.bean.VideoTag;
import com.test.xcamera.constants.LogcatConstants;
import com.test.xcamera.utils.DateUtils;
import com.test.xcamera.utils.Md5Util;
import com.test.xcamera.utils.StringUtil;
import com.moxiang.common.logging.Logcat;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class OneKeyMakeVideoXmlParser {

    public static long MIN_FILE_DURATION = 3000;
    public static long TOTAL_DURATION = 3000;
    public static final long MIN_TOTAL_DURATION = 36*1000;
    private Handler handler;

    public OneKeyMakeVideoXmlParser() {
        handler = new Handler(Looper.getMainLooper());
    }

    /**
     * 解析固件返回的今日精彩的xml文件
     *
     * @param In
     * @return
     * @throws Exception
     */
    public void parserXml(InputStream In, OnParseXmlCallback onParseXmlCallback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("parse xml start").out();
                    List<VideoFile> videoFileList = new ArrayList<>();
                    VideoFile videoFile = null;
                    VideoSegment videoSegment = null;
                    VideoTag videoTag = null;
                    VideoScoreType videoScoreType = null;
                    List<String> forwardFaceList = new ArrayList<>();
                    List<String> happyFaceList = new ArrayList<>();

                    // 获取XmlPullParser解析的实例
                    XmlPullParser xmlPullParser = Xml.newPullParser();
                    // 设置XmlPullParser的参数
                    xmlPullParser.setInput(In, "utf-8");
                    // 获取事件类型
                    int type = xmlPullParser.getEventType();
                    TOTAL_DURATION = 0L;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onParseXmlCallback.onStart();
                        }
                    });
                    while (type != XmlPullParser.END_DOCUMENT) {
                        switch (type) {
                            case XmlPullParser.START_TAG:
                                if (StringUtil.notEmpty(xmlPullParser.getName()) && (xmlPullParser.getName().startsWith("list_") || xmlPullParser.getName().startsWith("qr_media_"))) {
                                    if (hasProperty(xmlPullParser, "label_duration")) {
                                        MIN_FILE_DURATION = Long.valueOf(xmlPullParser.getAttributeValue(null, "label_duration"));
                                    }
                                    if (hasProperty(xmlPullParser, "duration")) {
                                        TOTAL_DURATION = Long.valueOf(xmlPullParser.getAttributeValue(null, "duration"));
                                    }
                                } else if ("file".equals(xmlPullParser.getName())) {
                                    // 创建一个集合对象
                                    videoFile = new VideoFile();
                                    if (hasProperty(xmlPullParser, "name")) {
                                        videoFile.setName(xmlPullParser.getAttributeValue(null, "name"));
                                    }
                                    if (hasProperty(xmlPullParser, "create_time")) {
                                        videoFile.setCreate_time(Long.valueOf(xmlPullParser.getAttributeValue(null, "create_time")));
                                    }
                                    if (hasProperty(xmlPullParser, "close_time")) {
                                        videoFile.setClose_time(Long.valueOf(xmlPullParser.getAttributeValue(null, "close_time")));
                                    }
                                    if (hasProperty(xmlPullParser, "speedx")) {
                                        videoFile.setSpeedx(Float.valueOf(xmlPullParser.getAttributeValue(null, "speedx")));
                                    }
                                    Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("parse xml file  Name:" + videoFile.getName() + " crateTime " + DateUtils.DateFormat(videoFile.getCreate_time()) + " closeTime " + DateUtils.DateFormat(videoFile.getClose_time())+ "     speedx="+videoFile.getSpeedx()).out();
                                } else if ("label".equals(xmlPullParser.getName())) {
                                    videoSegment = new VideoSegment();
                                    if (videoSegment.getRotationList() == null) {
                                        videoSegment.setRotationList(new ArrayList<Integer>());
                                    }

                                    if (hasProperty(xmlPullParser, "create_time")) {
                                        videoSegment.setCreate_time(Long.valueOf(xmlPullParser.getAttributeValue(null, "create_time")));
                                    }
                                    if (hasProperty(xmlPullParser, "rec_mode")) {
                                        videoSegment.setRec_mode(xmlPullParser.getAttributeValue(null, "rec_mode"));
                                        if (VideoScoreType.CameraMode.HI_PDT_WORKMODE_LPSE_REC.getValue().equals(videoSegment.getRec_mode())
                                                || VideoScoreType.CameraMode.HI_PDT_WORKMODE_TRACK_LPSE_REC.getValue().equals(videoSegment.getRec_mode())
                                                || VideoScoreType.CameraMode.HI_PDT_WORKMODE_SLOW_REC.getValue().equals(videoSegment.getRec_mode())){
                                            videoFile.setRec_mode(videoSegment.getRec_mode());
                                        }
                                    }
                                    if (hasProperty(xmlPullParser, "end_time")) {
                                        videoSegment.setEnd_time(Long.valueOf(xmlPullParser.getAttributeValue(null, "end_time")));
                                    }
                                    if (hasProperty(xmlPullParser, "start_time")) {
                                        videoSegment.setStart_time(Long.valueOf(xmlPullParser.getAttributeValue(null, "start_time")));
                                    }
                                    videoSegment.setFilePath(videoFile.getName());
                                    videoSegment.setClose_time(videoFile.getClose_time());
                                    videoSegment.setVideoSegmentId(Md5Util.getMD5(videoSegment.getFilePath() + videoSegment.getCreate_time() + videoSegment.getClose_time() + videoSegment.getStart_time() + videoSegment.getEnd_time()));
                                    videoFile.getVideoSegmentList().add(videoSegment);
                                    Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("parse xml label  createTime:" + DateUtils.DateFormat(videoSegment.getCreate_time()) + " recMode " + videoSegment.getRec_mode() + " end_time " + DateUtils.DateFormat(videoSegment.getEnd_time()) + " startTime " + DateUtils.DateFormat(videoSegment.getStart_time()) + " start time timestamp " + videoSegment.getStart_time()).out();
                                } else if ("tag".equals(xmlPullParser.getName())) {
                                    videoTag = new VideoTag();
                                    if (hasProperty(xmlPullParser, "no_face_out_ratio")) {
                                        videoTag.setNo_face_out_ratio(Float.valueOf(xmlPullParser.getAttributeValue(null, "no_face_out_ratio")));
                                    }
                                    if (hasProperty(xmlPullParser, "scene_class")) {
                                        videoTag.setScene_class(xmlPullParser.getAttributeValue(null, "scene_class"));
                                    }
                                    if (hasProperty(xmlPullParser, "object_class")) {
                                        videoTag.setObject_class(xmlPullParser.getAttributeValue(null, "object_class"));
                                    }
                                    if (hasProperty(xmlPullParser, "gender_class")) {
                                        videoTag.setGender_class(xmlPullParser.getAttributeValue(null, "gender_class"));
                                    }
                                    if (hasProperty(xmlPullParser, "ori_face")) {
                                        videoTag.setOri_face(xmlPullParser.getAttributeValue(null, "ori_face"));
                                    }
                                    if (hasProperty(xmlPullParser, "object_detect")) {
                                        videoTag.setObject_detect(xmlPullParser.getAttributeValue(null, "object_detect"));
                                    }
                                    if (hasProperty(xmlPullParser, "face_num")) {
                                        videoTag.setFace_num(xmlPullParser.getAttributeValue(null, "face_num"));
                                    }
                                    if (hasProperty(xmlPullParser, "emotion")) {
                                        videoTag.setEmotion(xmlPullParser.getAttributeValue(null, "emotion"));
                                    }

                                    if (videoTag.getForwardFaceList() == null) {
                                        videoTag.setForwardFaceList(new ArrayList<String>());
                                    }
                                    if (videoTag.getHappyFaceList() == null) {
                                        videoTag.setHappyFaceList(new ArrayList<String>());
                                    }
                                    videoTag.setStart_time(videoSegment.getStart_time());
                                    videoTag.setEnd_time(videoSegment.getEnd_time());
                                    videoTag.setVideoSegmentId(videoSegment.getVideoSegmentId());
                                    videoSegment.setVideoTag(videoTag);
                                    Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("parse xml tag  scene_class:" + videoTag.getScene_class() + " object_class " + videoTag.getObject_class() + " ori_face " + videoTag.getOri_face() + " object_detect " + videoTag.getFace_num() + " face_num " + videoTag.getFace_num() + " emotion " + videoTag.getEmotion() + " gender " + videoTag.getGender_class()).out();
                                } else if ("face_forward_time".equals(xmlPullParser.getName())) {
                                    String forwardFace = xmlPullParser.nextText();
                                    if (StringUtil.notEmpty(forwardFace)) {
                                        videoTag.getForwardFaceList().add(forwardFace);
                                        Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("parse xml file  face forward time :" + DateUtils.DateFormat(Long.parseLong(forwardFace))).out();
                                    }
                                } else if ("face_happy_time".equals(xmlPullParser.getName())) {
                                    String happyFace = xmlPullParser.nextText();
                                    if (StringUtil.notEmpty(happyFace)) {
                                        videoTag.getHappyFaceList().add(happyFace);
                                        Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("parse xml file  face happy time :" + DateUtils.DateFormat(Long.parseLong(happyFace))).out();
                                    }
                                } else if ("rotation".equals(xmlPullParser.getName())) {
                                    Rotation rotation = new Rotation();
                                    if (hasProperty(xmlPullParser, "angle")) {
                                        int angle = Integer.valueOf(xmlPullParser.getAttributeValue(null, "angle"));
                                        rotation.setAngle(angle);
                                        videoSegment.getRotationList().add(angle);
                                        Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("parse xml file rotation angle :" + angle).out();
                                    }
                                    if (hasProperty(xmlPullParser, "timestamp")) {
                                        long timestamp = Long.valueOf(xmlPullParser.getAttributeValue(null, "timestamp"));
                                        rotation.setTimestamp(timestamp);
                                        Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("parse xml file rotation timestamp :" + timestamp).out();
                                    }
                                    videoTag.getRotationList().add(rotation);
                                } else if ("pet_closeup".equals(xmlPullParser.getName())) {
                                    String petCloseUp = xmlPullParser.nextText();
                                    if (StringUtil.notEmpty(petCloseUp)) {
                                        videoTag.getPetCloseupList().add(Long.valueOf(petCloseUp));
                                        videoSegment.setPetCloseUp(true);
                                        Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("parse xml file  pet closeup :" + petCloseUp).out();
                                    }
                                } else if ("mark".equals(xmlPullParser.getName())) {
                                    videoScoreType = new VideoScoreType();
                                    videoScoreType.setScoreType(VideoScoreType.ScoreType.MARK);
                                    if (hasProperty(xmlPullParser, "create_time")) {
                                        videoScoreType.setCreate_time(Long.valueOf(xmlPullParser.getAttributeValue(null, "create_time")));
                                    }
                                    if (hasProperty(xmlPullParser, "start_time")) {
                                        videoScoreType.setStart_time(Long.valueOf(xmlPullParser.getAttributeValue(null, "start_time")));
                                    }
                                    if (hasProperty(xmlPullParser, "end_time")) {
                                        videoScoreType.setEnd_time(Long.valueOf(xmlPullParser.getAttributeValue(null, "end_time")));
                                    }
                                    videoFile.getVideoScoreTypeList().add(videoScoreType);
                                    Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("parse xml file  mark create time :" + DateUtils.DateFormat(videoScoreType.getCreate_time()) + " start_time " + DateUtils.DateFormat(videoScoreType.getStart_time()) + " end_time " + DateUtils.DateFormat(videoScoreType.getEnd_time())).out();

                                } else if ("trace".equals(xmlPullParser.getName())) {
                                    videoScoreType = new VideoScoreType();
                                    videoScoreType.setScoreType(VideoScoreType.ScoreType.TRACE_VIDEO);
                                    if (hasProperty(xmlPullParser, "create_time")) {
                                        videoScoreType.setCreate_time(Long.valueOf(xmlPullParser.getAttributeValue(null, "create_time")));
                                    }
                                    if (hasProperty(xmlPullParser, "start_time")) {
                                        videoScoreType.setStart_time(Long.valueOf(xmlPullParser.getAttributeValue(null, "start_time")));
                                    }
                                    if (hasProperty(xmlPullParser, "end_time")) {
                                        videoScoreType.setEnd_time(Long.valueOf(xmlPullParser.getAttributeValue(null, "end_time")));
                                    }
                                    videoFile.getVideoScoreTypeList().add(videoScoreType);
                                    //                        String wind = xmlPullParser.nextText();
                                    //                        channel.setWind(wind);

                                    Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("parse xml file  trace create time:" + DateUtils.DateFormat(videoScoreType.getCreate_time()) + " start_time " + DateUtils.DateFormat(videoScoreType.getStart_time()) + " end time " + DateUtils.DateFormat(videoScoreType.getEnd_time())).out();
                                } else if ("ptz_still".equals(xmlPullParser.getName())) {
                                    videoScoreType = new VideoScoreType();
                                    videoScoreType.setScoreType(VideoScoreType.ScoreType.PIZ_STILL);
                                    if (hasProperty(xmlPullParser, "create_time")) {
                                        videoScoreType.setCreate_time(Long.valueOf(xmlPullParser.getAttributeValue(null, "create_time")));
                                    }
                                    if (hasProperty(xmlPullParser, "start_time")) {
                                        videoScoreType.setStart_time(Long.valueOf(xmlPullParser.getAttributeValue(null, "start_time")));
                                    }
                                    if (hasProperty(xmlPullParser, "end_time")) {
                                        videoScoreType.setEnd_time(Long.valueOf(xmlPullParser.getAttributeValue(null, "end_time")));
                                    }
                                    videoFile.getVideoScoreTypeList().add(videoScoreType);
                                    Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("parse xml file  ptz-still create time:" + DateUtils.DateFormat(videoScoreType.getCreate_time()) + " start_time " + DateUtils.DateFormat(videoScoreType.getStart_time()) + " end time " + DateUtils.DateFormat(videoScoreType.getEnd_time())).out();
                                }
                                break;
                            case XmlPullParser.END_TAG:   // 解析结束标志
                                if ("file".equals(xmlPullParser.getName())) {
                                    if ((videoFile.getClose_time() - videoFile.getCreate_time()) > MIN_FILE_DURATION) {
                                        videoFileList.add(videoFile);
                                    }
                                }
                                Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("parse xml end").out();
                                break;
                        }
                        // 不停的向下解析
                        type = xmlPullParser.next();
                    }

                    for (int i = 0; i < videoFileList.size(); i++) {
                        VideoFile videoFileCheck = videoFileList.get(i);
                        if (videoFileCheck.getVideoSegmentList().size() <= 0) {
                            VideoSegment videoSegmentCheck = new VideoSegment();
                            videoSegmentCheck.setVideoTag(new VideoTag());
                            videoSegmentCheck.setCreate_time(videoFileCheck.getCreate_time());
                            videoSegmentCheck.setClose_time(videoFileCheck.getClose_time());
                            videoSegmentCheck.setStart_time(videoFileCheck.getCreate_time());
                            videoSegmentCheck.setEnd_time(videoFileCheck.getClose_time());
                            videoSegmentCheck.setFilePath(videoFileCheck.getName());
                            videoSegmentCheck.setRec_mode(VideoScoreType.CameraMode.HI_PDT_WORKMODE_LPSE_REC.getValue());
                            videoSegmentCheck.setVideoSegmentId(Md5Util.getMD5(videoSegmentCheck.getFilePath() + videoSegmentCheck.getCreate_time() + videoSegment.getClose_time() + videoSegmentCheck.getStart_time() + videoSegmentCheck.getEnd_time()));
                            videoFileCheck.getVideoSegmentList().add(videoSegmentCheck);
                            Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("parse xml file  no lable create empty lable ").out();
                        }
                    }
                    dealSlowAndLpseVideoFile(videoFileList);

                    AiCameraApplication.mApplication.mVideoFileList = videoFileList;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onParseXmlCallback.onFinish(videoFileList);
                        }
                    });

                } catch (Exception e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onParseXmlCallback.onFail("fail to parse xml file ");
                        }
                    });

                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 处理延时摄影和慢动作视频
     * @param videoFileList
     */
    public void dealSlowAndLpseVideoFile(List<VideoFile> videoFileList) {
        for(int i=0; i<videoFileList.size();i++){
            //延时摄影     需要按照speedx倍数把片段进行合并
            if (VideoScoreType.CameraMode.HI_PDT_WORKMODE_LPSE_REC.getValue().equals(videoFileList.get(i).getRec_mode())
                    || VideoScoreType.CameraMode.HI_PDT_WORKMODE_TRACK_LPSE_REC.getValue().equals(videoFileList.get(i).getRec_mode())){

                float speed = videoFileList.get(i).getSpeedx();
                List<VideoScoreType> newVideoScoreTypeList = new ArrayList<>();
                List<VideoScoreType> oldVideoScoreTypeList = videoFileList.get(i).getVideoScoreTypeList();
                for (int m=0; m<oldVideoScoreTypeList.size();m++){
                    VideoScoreType scoreType = new VideoScoreType();
                    VideoScoreType oldScoreType = oldVideoScoreTypeList.get(m);
                    scoreType.setScoreType(oldScoreType.getScoreType());
                    scoreType.setCreate_time(oldScoreType.getCreate_time());
                    scoreType.setRec_mode(oldScoreType.getRec_mode());
                    scoreType.setStart_time((long) ((oldScoreType.getStart_time()-oldScoreType.getCreate_time())/speed*1d+oldScoreType.getCreate_time()));
                    scoreType.setEnd_time((long) ((oldScoreType.getEnd_time()-oldScoreType.getCreate_time())/speed*1d+oldScoreType.getCreate_time()));
                    newVideoScoreTypeList.add(scoreType);
                    Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("xml parse for LPSE: Rec_mode="+(scoreType.getRec_mode()+"  scoreType start time" + DateUtils.DateFormat(scoreType.getStart_time()) + " scoreType end time=" + DateUtils.DateFormat(scoreType.getEnd_time()) + " scoreType create time "
                            + DateUtils.DateFormat(scoreType.getCreate_time()) + " scoreType=" + scoreType.getScoreType())).out();
                }
                videoFileList.get(i).setVideoScoreTypeList(newVideoScoreTypeList);


                List<VideoSegment> newVideoSegmentList = new ArrayList<>();
                List<VideoSegment> oldVideoSegmentList = videoFileList.get(i).getVideoSegmentList();
                int segmentCount= (int) (oldVideoSegmentList.size()/speed);
                for (int n=0; n<segmentCount;n++){
                    VideoSegment segment = new VideoSegment();
                    int firstPositionInGroup = (int) (speed*n);
                    int lastPositionInGroup = (int) (speed*(n+1)-1);
                    VideoSegment oldSegmentFirstInGroup = oldVideoSegmentList.get(firstPositionInGroup);
                    VideoSegment oldSegmentLastInGroup = oldVideoSegmentList.get(lastPositionInGroup);
                    segment.setCreate_time(videoFileList.get(i).getCreate_time());
                    segment.setClose_time(videoFileList.get(i).getClose_time());
                    segment.setFilePath(videoFileList.get(i).getName());
                    if(n==0){
                        segment.setStart_time((long) ((oldSegmentFirstInGroup.getStart_time()-oldSegmentFirstInGroup.getCreate_time())/speed*1d+oldSegmentFirstInGroup.getCreate_time()));
                    }else {
                        segment.setStart_time(newVideoSegmentList.get(n-1).getEnd_time());
                    }
                    segment.setEnd_time((long) ((oldSegmentLastInGroup.getEnd_time()-oldSegmentLastInGroup.getCreate_time())/speed*1d+oldSegmentLastInGroup.getCreate_time()));
                    segment.setVideoSegmentId(Md5Util.getMD5(segment.getFilePath() + segment.getCreate_time() + segment.getClose_time() + segment.getStart_time() + segment.getEnd_time()));
                    VideoTag newVideoTag = oldSegmentFirstInGroup.getVideoTag().clone();
                    newVideoTag.setVideoSegmentId(segment.getVideoSegmentId());
                    newVideoTag.setStart_time(segment.getStart_time());
                    newVideoTag.setEnd_time(segment.getEnd_time());
                    segment.setVideoTag(newVideoTag);

                    newVideoTag.getForwardFaceList().clear();
                    newVideoTag.getHappyFaceList().clear();
                    newVideoTag.getPetCloseupList().clear();
                    newVideoTag.getRotationList().clear();
                    segment.setPetCloseUp(false);
                    segment.getRotationList().clear();
                    for(int m=firstPositionInGroup; m<=lastPositionInGroup;m++){
                        if(oldVideoSegmentList.get(m).isPetCloseUp()){
                            segment.setPetCloseUp(true);
                        }
                         List<Integer> rotationList= oldVideoSegmentList.get(m).getRotationList();
                        for (int p=0;p<rotationList.size();p++){
                            segment.getRotationList().add(rotationList.get(p).intValue());
                        }
                        VideoTag  tmpVideoTag=oldVideoSegmentList.get(m).getVideoTag();
                        for (int p=0;p<tmpVideoTag.getHappyFaceList().size();p++){
                            newVideoTag.getHappyFaceList().add(((long)((Long.valueOf(tmpVideoTag.getHappyFaceList().get(p))-segment.getCreate_time())/speed*1d+segment.getCreate_time()))+"");
                        }
                        for (int p=0;p<tmpVideoTag.getForwardFaceList().size();p++){
                            newVideoTag.getForwardFaceList().add(((long)((Long.valueOf(tmpVideoTag.getForwardFaceList().get(p))-segment.getCreate_time())/speed*1d+segment.getCreate_time()))+"");
                        }
                    }
                    segment.setRec_mode(videoFileList.get(i).getRec_mode());
                    newVideoSegmentList.add(segment);
                    Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("xml parse for LPSE: segment start time" + DateUtils.DateFormat(segment.getStart_time()) + " segment end time  " + DateUtils.DateFormat(segment.getEnd_time()) + "file path " + segment.getFilePath() + " create time "
                            + DateUtils.DateFormat(segment.getCreate_time()) + " close time " + DateUtils.DateFormat(segment.getClose_time()) + " segment length  "+(segment.getEnd_time() - segment.getStart_time())).out();
                }
                videoFileList.get(i).setVideoSegmentList(newVideoSegmentList);
            }
            //慢动作     需要按照speedx倍数把片段进行扩充
            if (VideoScoreType.CameraMode.HI_PDT_WORKMODE_SLOW_REC.getValue().equals(videoFileList.get(i).getRec_mode())){
                int segmentCount = (int) (1/videoFileList.get(i).getSpeedx());
                List<VideoScoreType> newVideoScoreTypeList = new ArrayList<>();
                List<VideoScoreType> oldVideoScoreTypeList = videoFileList.get(i).getVideoScoreTypeList();
                for (int m=0; m<oldVideoScoreTypeList.size();m++){
                    VideoScoreType scoreType = new VideoScoreType();
                    VideoScoreType oldScoreType = oldVideoScoreTypeList.get(m);
                    scoreType.setScoreType(oldScoreType.getScoreType());
                    scoreType.setCreate_time(oldScoreType.getCreate_time());
                    scoreType.setRec_mode(oldScoreType.getRec_mode());
                    scoreType.setStart_time((long) ((oldScoreType.getStart_time()-oldScoreType.getCreate_time())*segmentCount*1d+oldScoreType.getCreate_time()));
                    scoreType.setEnd_time((long) ((oldScoreType.getEnd_time()-oldScoreType.getCreate_time())*segmentCount*1d+oldScoreType.getCreate_time()));
                    newVideoScoreTypeList.add(scoreType);
                    Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("xml parse for SLOW: Rec_mode="+(scoreType.getRec_mode()+"  scoreType start time" + DateUtils.DateFormat(scoreType.getStart_time()) + " scoreType end time=" + DateUtils.DateFormat(scoreType.getEnd_time()) + " scoreType create time "
                            + DateUtils.DateFormat(scoreType.getCreate_time()) + " scoreType=" + scoreType.getScoreType())).out();
                }
                videoFileList.get(i).setVideoScoreTypeList(newVideoScoreTypeList);

                List<VideoSegment> newVideoSegmentList = new ArrayList<>();
                List<VideoSegment> oldVideoSegmentList = videoFileList.get(i).getVideoSegmentList();
                for (int m=0; m<oldVideoSegmentList.size();m++) {
                    VideoSegment oldSegmentCurrent = oldVideoSegmentList.get(m);

                    VideoTag oldVideoTag = oldSegmentCurrent.getVideoTag().clone();
                    List<String> forwardFaceList = new ArrayList<>();
                    List<String> happyFaceList = new ArrayList<>();
                    List<Long> petCloseupList = new ArrayList<>();
                    List<Rotation> rotationList = new ArrayList<>();
                    for(int p=0; p<oldVideoTag.getForwardFaceList().size(); p++){
                        forwardFaceList.add((long)((Long.valueOf(oldVideoTag.getForwardFaceList().get(p))-oldSegmentCurrent.getCreate_time())*segmentCount*1d+oldSegmentCurrent.getCreate_time())+"");
                        Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("xml parse for SLOW: forwardFace time" + DateUtils.DateFormat(Long.valueOf(forwardFaceList.get(p))) + " " ).out();
                    }
                    for(int p=0; p<oldVideoTag.getHappyFaceList().size(); p++){
                        happyFaceList.add((long)((Long.valueOf(oldVideoTag.getHappyFaceList().get(p))-oldSegmentCurrent.getCreate_time())*segmentCount*1d+oldSegmentCurrent.getCreate_time())+"");
                        Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("xml parse for SLOW: happyFace time" + DateUtils.DateFormat(Long.valueOf(happyFaceList.get(p))) + " " ).out();
                    }
                    for(int p=0; p<oldVideoTag.getPetCloseupList().size(); p++){
                        petCloseupList.add((long)((oldVideoTag.getPetCloseupList().get(p)-oldSegmentCurrent.getCreate_time())*segmentCount*1d+oldSegmentCurrent.getCreate_time()));
                        Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("xml parse for SLOW: pet Closeup time" + DateUtils.DateFormat(Long.valueOf(petCloseupList.get(p))) + " " ).out();
                    }
                    for(int p=0; p<oldVideoTag.getRotationList().size(); p++){
                        Rotation rotation = oldVideoTag.getRotationList().get(p).clone();
                        rotation.setTimestamp((long) ((rotation.getTimestamp()-oldSegmentCurrent.getCreate_time())*segmentCount*1d+oldSegmentCurrent.getCreate_time()));
                        rotationList.add(rotation);
                        Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("xml parse for SLOW: rotation time" + DateUtils.DateFormat(rotation.getTimestamp()) + "     rotation angle="+rotation.getAngle() ).out();
                    }
                    oldVideoTag.setRotationList(rotationList);
                    oldVideoTag.setForwardFaceList(forwardFaceList);
                    oldVideoTag.setHappyFaceList(happyFaceList);
                    oldVideoTag.setPetCloseupList(petCloseupList);

                    long segmentDuration= oldSegmentCurrent.getEnd_time()-oldSegmentCurrent.getStart_time();
                    for (int n=0; n<segmentCount;n++) {
                        VideoSegment segment = new VideoSegment();
                        segment.setCreate_time(videoFileList.get(i).getCreate_time());
                        segment.setClose_time(videoFileList.get(i).getClose_time());
                        segment.setFilePath(videoFileList.get(i).getName());
                        segment.setStart_time((long) ((oldSegmentCurrent.getStart_time()-oldSegmentCurrent.getCreate_time())*segmentCount*1d+oldSegmentCurrent.getCreate_time()+n*segmentDuration));
                        segment.setEnd_time(segment.getStart_time()+segmentDuration);
                        segment.setVideoSegmentId(Md5Util.getMD5(segment.getFilePath() + segment.getCreate_time() + segment.getClose_time() + segment.getStart_time() + segment.getEnd_time()));
                        VideoTag newVideoTag = oldSegmentCurrent.getVideoTag().clone();
                        newVideoTag.setStart_time(segment.getStart_time());
                        newVideoTag.setEnd_time(segment.getEnd_time());
                        newVideoTag.setVideoSegmentId(segment.getVideoSegmentId());
                        segment.setVideoTag(newVideoTag);

                        newVideoTag.getForwardFaceList().clear();
                        newVideoTag.getHappyFaceList().clear();
                        newVideoTag.getPetCloseupList().clear();
                        newVideoTag.getRotationList().clear();
                        segment.setPetCloseUp(false);
                        segment.getRotationList().clear();
                        for(int p=0; p<oldVideoTag.getForwardFaceList().size(); p++){
                            String time = oldVideoTag.getForwardFaceList().get(p);
                            if(isAddVideoTag(segment, Long.valueOf(time))){
                                newVideoTag.getForwardFaceList().add(time);
                            }
                        }
                        for(int p=0; p<oldVideoTag.getHappyFaceList().size(); p++){
                            String time = oldVideoTag.getHappyFaceList().get(p);
                            if(isAddVideoTag(segment, Long.valueOf(time))){
                                newVideoTag.getHappyFaceList().add(time);
                            }
                        }
                        for(int p=0; p<oldVideoTag.getPetCloseupList().size(); p++){
                            long  time = oldVideoTag.getPetCloseupList().get(p);
                            if(isAddVideoTag(segment, time)){
                                newVideoTag.getPetCloseupList().add(time);
                                segment.setPetCloseUp(true);
                            }
                        }
                        for(int p=0; p<oldVideoTag.getRotationList().size(); p++){
                            Rotation rotation =oldVideoTag.getRotationList().get(p).clone();
                            if(isAddVideoTag(segment, rotation.getTimestamp())){
                                newVideoTag.getRotationList().add(rotation);
                                segment.getRotationList().add((int) rotation.getAngle());
                            }
                        }
                        segment.setRec_mode(videoFileList.get(i).getRec_mode());
                        newVideoSegmentList.add(segment);
                        Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("xml parse for SLOW: segment start time" + DateUtils.DateFormat(segment.getStart_time()) + " segment end time  " + DateUtils.DateFormat(segment.getEnd_time()) + "file path " + segment.getFilePath() + " create time "
                                + DateUtils.DateFormat(segment.getCreate_time()) + " close time " + DateUtils.DateFormat(segment.getClose_time()) + " segment length  "+(segment.getEnd_time() - segment.getStart_time())).out();
                    }
                }
                videoFileList.get(i).setVideoSegmentList(newVideoSegmentList);
            }
        }
    }

    private static boolean hasProperty(XmlPullParser xmlPullParser, String propertyName) {
        return StringUtil.notEmpty(xmlPullParser.getAttributeValue(null, propertyName));
    }

    private static boolean isAddVideoTag(VideoSegment videoSegment, long timestamp) {
        return (timestamp >= videoSegment.getStart_time() && timestamp< videoSegment.getEnd_time());
    }

    public interface OnParseXmlCallback {
        void onStart();

        void onFinish(List<VideoFile> videoFileList);

        void onFail(String errorInfo);
    }
}
