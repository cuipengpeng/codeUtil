package com.test.xcamera.cameraclip;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.test.xcamera.application.AiCameraApplication;
import com.test.xcamera.bean.VideoTemplete;
import com.test.xcamera.cameraclip.bean.ThemeTypeTimeLengthBean;
import com.test.xcamera.cameraclip.bean.VideoFile;
import com.test.xcamera.cameraclip.bean.VideoScoreType;
import com.test.xcamera.cameraclip.bean.VideoSegment;
import com.test.xcamera.cameraclip.bean.VideoTag;
import com.test.xcamera.constants.LogcatConstants;
import com.test.xcamera.util.TextReader;
import com.test.xcamera.utils.DateUtils;
import com.test.xcamera.utils.LoggerUtils;
import com.test.xcamera.utils.StringUtil;
import com.moxiang.common.logging.Logcat;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class OneKeyMakeVideoHelper {

    private static final float DURATION_FOR_VIDEO_SEGMENT = 3000; //视频片段阈值
    private static final int SIDE_KEY_MAX_SEGMENT_COUNT = 8;
    public static final String ASSET_TEMPLETE_DIR = "segmenttemplate";
    public static final String SUFFER_FIX_TEMPLETE_JSON = ".json";
    public static final String FOLDER_TYPE_NONE = "none";
    public static final String FOLDER_TYPE_ONE_PERSON = "onePerson";
    public static final String FOLDER_TYPE_TWO_PERSON = "twoPerson";
    public static final String FOLDER_TYPE_MANY_PERSON = "manyPerson";
    public static final String FOLDER_TYPE_PERSON_AND_PET = "petAndPerson";
    public static final String FOLDER_TYPE_PET = "pet";
    public static final String FOLDER_TYPE_LONG_TEMPLETE = "longTemplete";
    public static final String FOLDER_TYPE_SHORT_TEMPLETE = "shortTemplete";
    private static final String THEME_ALL_PERSON = "000011100000000000000000";
    private static final String THEME_PET = "000000000000011000000110";
    private static final String THEME_MULTI_PERSON = "000010000000000000000000";
    private static final String THEME_DOUBLE_PERSON = "000001000000000000000000";
    private static final String THEME_SINGLE_PERSON = "000000100000000000000000";
    private static final String SINGLE_WOMAN_PERSON = "0000001000000000";
    private static final String MORE_THAN_DOUBLE_WOMAN = "0000110000000000";
    private static final String SINGLE_MAN_PERSON = "0000000000000010";
    private static final String MORE_THAN_DOUBLE_MAN = "0000000000001100";

    private List<VideoTemplete> allVideoTempleteList = new ArrayList<>();
    public List<VideoSegment> mAllSortedVideoSegmentList = new ArrayList<>();
    public List<ThemeTypeTimeLengthBean> mThemeTypeTimeLengthList = new ArrayList<>();
    public List<Integer> mSceneClassTimeLengthList = new ArrayList<>();
    public static MakeVideoType makeVideoType;
    public static String makeVideoDir;
    private Context mContext;
    public VideoSegment.VideoTempleteType videoTempleteType;

    public OneKeyMakeVideoHelper(Context context, MakeVideoType makeVideoType) {
        this.mContext = context;
        setMakeVideoType(makeVideoType);
        allVideoTempleteList.addAll(convertTempleteJsonToBean(context));
    }

    public static void setMakeVideoType(MakeVideoType makeVideoType) {
        OneKeyMakeVideoHelper.makeVideoType = makeVideoType;
        if (makeVideoType == MakeVideoType.TODAY_WONDERFUL) {
            makeVideoDir = FOLDER_TYPE_LONG_TEMPLETE;
        } else {
            makeVideoDir = FOLDER_TYPE_SHORT_TEMPLETE;
        }
    }

    /**
     * 一键成片
     */
    public VideoTemplete fillVideoTempleteForOneKeyMakeVideo() {
        //选模板
        VideoTemplete videoTemplete = null;
        for (int i = 0; i < allVideoTempleteList.size(); i++) {
            if (allVideoTempleteList.get(i).isShortTemplete()) {
                videoTemplete = allVideoTempleteList.get(i);
                break;
            }
        }

        //填充模板所需的视频片段和时长
        newFillVideoTempleteData(videoTemplete, mAllSortedVideoSegmentList);
        return videoTemplete;

    }


    /**
     * 转换模板Json to  bean
     */
    public static List<VideoTemplete> convertTempleteJsonToBean(Context context) {
        List<String> jsonTempletePathList = getTempleteJsonPathFromAssert(context);

        List<VideoTemplete> videoTempleteList = new ArrayList<>();
        Gson gson = new Gson();
        for (int i = 0; i < jsonTempletePathList.size(); i++) {
            try {
                String templeteJsonPath = jsonTempletePathList.get(i);
                InputStream inputStream = context.getAssets().open(templeteJsonPath);
                String templeteJsonContent = TextReader.getFileContent(inputStream);
                VideoTemplete template = gson.fromJson(templeteJsonContent, VideoTemplete.class);
                if (templeteJsonPath.contains(FOLDER_TYPE_SHORT_TEMPLETE)) {
                    template.setShortTemplete(true);
                }

                if (templeteJsonPath.contains(FOLDER_TYPE_NONE)) {
                    template.setThemeType(VideoSegment.ThemeType.NONE);
                } else if (templeteJsonPath.contains(FOLDER_TYPE_ONE_PERSON)) {
                    template.setThemeType(VideoSegment.ThemeType.SINGLE_PERSON);
                } else if (templeteJsonPath.contains(FOLDER_TYPE_TWO_PERSON)) {
                    template.setThemeType(VideoSegment.ThemeType.DOUBLE_PERSON);
                } else if (templeteJsonPath.contains(FOLDER_TYPE_MANY_PERSON)) {
                    template.setThemeType(VideoSegment.ThemeType.MULTI_PERSON);
                } else if (templeteJsonPath.contains(FOLDER_TYPE_PET)) {
                    if (templeteJsonPath.contains(FOLDER_TYPE_PERSON_AND_PET)) {
                        template.setThemeType(VideoSegment.ThemeType.PERSON_AND_PET);
                    } else {
                        template.setThemeType(VideoSegment.ThemeType.PET);




                    }
                }
                videoTempleteList.add(template);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return videoTempleteList;
    }

    /**
     * 从asset目录下读取json文件路径
     *
     * @return
     */
    private static List<String> getTempleteJsonPathFromAssert(Context context) {
        List<String> jsonTempletePathList = new ArrayList<>();
        try {
            String[] shortOrLongTempleteDirList = context.getAssets().list(ASSET_TEMPLETE_DIR);
            if (shortOrLongTempleteDirList.length > 0) {
                for (int j = 0; j < shortOrLongTempleteDirList.length; j++) {
                    String[] templeteDirList = context.getAssets().list(ASSET_TEMPLETE_DIR + "/" + shortOrLongTempleteDirList[j]);
                    if (templeteDirList.length > 0) {
                        for (int m = 0; m < templeteDirList.length; m++) {
                            String[] templeteFileList = context.getAssets().list(ASSET_TEMPLETE_DIR + "/" + shortOrLongTempleteDirList[j] + "/" + templeteDirList[m]);
                            if (templeteFileList.length > 0) {
                                for (int p = 0; p < templeteFileList.length; p++) {
                                    if (templeteFileList[p].endsWith(SUFFER_FIX_TEMPLETE_JSON)) {
                                        jsonTempletePathList.add(ASSET_TEMPLETE_DIR + "/" + shortOrLongTempleteDirList[j] + "/"+ templeteDirList[m] + "/" + templeteFileList[p]);
                                        break;
                                    }
                                }
                            }

                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonTempletePathList;
    }

    /**
     * 为当天所有视频文件的视频片段进行打分
     *
     * @param videoFileList
     * @return
     */
    public List<VideoSegment> newScoreVideoSegmentForTodayVideoFiles(List<VideoFile> videoFileList) {
        List<VideoSegment> allVideoSegmentList = new ArrayList<>();
        Map<VideoSegment.ThemeType, Long> themeTypeLongMap = new HashMap<>();
        List<ThemeTypeTimeLengthBean> themeTypeTimeLengthList = new ArrayList<>();
        Map<Integer, Long> sceneClassLongMap = new HashMap<>();
        List<Map.Entry<Integer, Long>> sceneClassTimeLengthList = new ArrayList<>();

        //遍历固件返回xml中的今日精彩的所有视频文件
        for (int m = 0; m < videoFileList.size(); m++) {
            Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("video segment calculation score  start :" + " file list size " + allVideoSegmentList.size() + " position " + m).out();
            List<VideoSegment> videoSegmentList = videoFileList.get(m).getVideoSegmentList();
            List<VideoScoreType> videoScoreTypeList = videoFileList.get(m).getVideoScoreTypeList();

            //对每个视频片段按标签权重计算得分
            for (int i = 0; i < videoSegmentList.size(); i++) {

                VideoSegment videoSegment = videoSegmentList.get(i);
                Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("video segment position :" + i + " start time " + DateUtils.DateFormat(videoSegment.getStart_time())+" start time timestamp "+videoSegment.getStart_time() + " end time " + DateUtils.DateFormat(videoSegment.getEnd_time())).out();
                VideoSegment.ThemeType themeType = getVideoSegmentThemeType(videoSegment);
                Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("object detect type 0:INGLE_PERSON, 1:DOUBLE_PERSON, 2:MULTI_PERSON, 3:PET, 4:PERSON_AND_PET, 5:NONE :" + themeType).out();

                long existTimeLength = 0l;
                if (themeTypeLongMap.containsKey(themeType)) {
                    existTimeLength = themeTypeLongMap.get(themeType);
                }
                long newTimeLength = existTimeLength + videoSegment.getEnd_time() - videoSegment.getStart_time();
                themeTypeLongMap.put(themeType, newTimeLength);
                videoSegment.setThemeType(themeType);

                String sceneClass = videoSegment.getVideoTag().getScene_class();
                Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("object scene  :" + sceneClass).out();
                if (StringUtil.notEmpty(sceneClass)) {
                    int sceneType = Integer.valueOf(sceneClass);
                    long existSceneClassTimeLength = 0l;
                    if (sceneClassLongMap.containsKey(sceneType)) {
                        existSceneClassTimeLength = sceneClassLongMap.get(sceneType);
                    }
                    long newSceneClassTimeLength = existSceneClassTimeLength + videoSegment.getEnd_time() - videoSegment.getStart_time();
                    sceneClassLongMap.put(sceneType, newSceneClassTimeLength);
                }

                boolean roation = false;
                for (int j=0; j<videoSegment.getRotationList().size();j++){
                    if(videoSegment.getRotationList().get(j) != 0){
                        videoSegment.setScore(0);
                        roation = true;
                        Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("has rotation       video segment position :" + i + " start time " + DateUtils.DateFormat(videoSegment.getStart_time())+" start time timestamp "+videoSegment.getStart_time() + " end time " + DateUtils.DateFormat(videoSegment.getEnd_time())+" score =0").out();
                        Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg(" segment  total score " + 0).out();
                        break;
                    }
                }
                boolean noFace=false;
                Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("No_face_out_ratio: " + videoSegment.getVideoTag().getNo_face_out_ratio()).out();
                if(videoSegment.getVideoTag().getNo_face_out_ratio()<100){
                    noFace=true;
                    videoSegment.setScore(0);
                }

                if(roation || noFace){
                    continue;
                }

                float score = getVideoSegmentAlgorithmScore(videoSegment);

                ScoreItemConstants scoreItemConstants = new ScoreItemConstants();
                if(scoreItemConstants.sceneMap.containsKey(sceneClass)){
                    score+=scoreItemConstants.sceneMap.get(sceneClass);
                    Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("scene item score +"+scoreItemConstants.sceneMap.get(sceneClass)).out();
                }
                if(videoSegment.isPetCloseUp()){
                    score+=ScoreItemConstants.SCORE_FOR_PET_CLOSE_UP;
                    Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("Pet CloseUp score  :"+ScoreItemConstants.SCORE_FOR_PET_CLOSE_UP).out();
                }
                if (themeType != VideoSegment.ThemeType.NONE) {
                    score += ScoreItemConstants.SCORE_FOR_THEME;
                    Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("detect type score :"+ScoreItemConstants.SCORE_FOR_THEME).out();
                }
                if (videoSegment.getVideoTag().getForwardFaceList().size() > 0) {
                    score += ScoreItemConstants.SCORE_FOR_FORWARD_FACE;
                    Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg(" forward face score :"+ScoreItemConstants.SCORE_FOR_FORWARD_FACE).out();
                }
                if (videoSegment.getVideoTag().getHappyFaceList().size() > 0) {
                    score += ScoreItemConstants.SCORE_FOR_HAPPY_FACE;
                    Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg(" happy face  score :"+ScoreItemConstants.SCORE_FOR_HAPPY_FACE).out();
                }
                if (VideoScoreType.CameraMode.HI_PDT_WORKMODE_LPSE_REC.getValue().equals(videoSegment.getRec_mode())
                        || VideoScoreType.CameraMode.HI_PDT_WORKMODE_TRACK_LPSE_REC.getValue().equals(videoSegment.getRec_mode())){
                    score += ScoreItemConstants.SCORE_FOR_LPSE_REC;
                    Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg(" lpse rec  score :"+ScoreItemConstants.SCORE_FOR_LPSE_REC).out();
                }

                if (videoSegment.getVideoScoreTypeList() == null) {
                    videoSegment.setVideoScoreTypeList(new ArrayList<>());
                }
                for (int j = 0; j < videoScoreTypeList.size(); j++) {
                    VideoScoreType videoScoreType = videoScoreTypeList.get(j);
                    if ((videoScoreType.getScoreType() == VideoScoreType.ScoreType.MARK) && !videoSegment.isAddMarkScore()
                            && videoScoreType.getStart_time() < videoSegment.getEnd_time()
                            && videoScoreType.getStart_time() > videoSegment.getStart_time()) {
                        score += ScoreItemConstants.SCORE_FOR_MARK;
                        videoSegment.setAddMarkScore(true);
                        videoSegment.getVideoScoreTypeList().add(videoScoreType);
                        Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg(" mark   score:"+ScoreItemConstants.SCORE_FOR_MARK).out();
                    } else if (videoScoreType.getScoreType() == VideoScoreType.ScoreType.TRACE_VIDEO) {
                        if (isAddScoreForCurrentVideoSegment(videoSegment, videoScoreType)) {
                            float radio = calculateVideoScoreAspect(videoSegment, videoScoreType);
                            score = ScoreItemConstants.SCORE_FOR_TRACE_VIDEO * radio + score;
                            videoSegment.getVideoScoreTypeList().add(videoScoreType);
                            Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg(" track  该视频片段中trace的时长占整个片段时长的比例  ratio*10 : ratio " + ScoreItemConstants.SCORE_FOR_TRACE_VIDEO * radio + " trace start time " + videoScoreType.getStart_time() + " end time  " + videoScoreType.getEnd_time()).out();
                        }
                    } else if (videoScoreType.getScoreType() == VideoScoreType.ScoreType.PIZ_STILL) {
                        if (isAddScoreForCurrentVideoSegment(videoSegment, videoScoreType)) {
                            float radio = calculateVideoScoreAspect(videoSegment, videoScoreType);
                            score = ScoreItemConstants.SCORE_FOR_PIZ_STILL * radio + score;
                            videoSegment.getVideoScoreTypeList().add(videoScoreType);
                            Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg(" piz still  该视频片段中piz still 的时长占整个片段时长的比例  ratio*15 : ratio " + ScoreItemConstants.SCORE_FOR_PIZ_STILL * radio + " piz still start time " + videoScoreType.getStart_time() + " end time  " + videoScoreType.getEnd_time()).out();
                        }
                    }
                }
                LoggerUtils.printLog("score = " + score);
                videoSegment.setScore(score);
                Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg(" segment  total score " + score).out();
            }
            allVideoSegmentList.addAll(videoSegmentList);
            Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("video segment calculation score  end :").out();
        }

        ThemeTypeTimeLengthBean themeTypeTimeLength;
        for (Map.Entry entry : themeTypeLongMap.entrySet()) {
            themeTypeTimeLength = new ThemeTypeTimeLengthBean();
            themeTypeTimeLength.setThemeType((VideoSegment.ThemeType) entry.getKey());
            themeTypeTimeLength.setTimeLength((Long) entry.getValue());
            themeTypeTimeLengthList.add(themeTypeTimeLength);
            Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg(" list object detect type  :" + entry.getKey() + " total time " + entry.getValue()).out();
        }
        //对每个视频片段主体按时长进行降序排序
        Collections.sort(themeTypeTimeLengthList, new Comparator<ThemeTypeTimeLengthBean>() {
            public int compare(ThemeTypeTimeLengthBean file, ThemeTypeTimeLengthBean newFile) {
                if (file.getTimeLength() > newFile.getTimeLength()) {//降序
                    return -1;
                } else if (file.getTimeLength() == newFile.getTimeLength()) {
                    return 0;
                } else {
                    return 1;
                }
            }
        });

        for (Map.Entry<Integer, Long> entry : sceneClassLongMap.entrySet()) {
            sceneClassTimeLengthList.add(entry);
            Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg(" list scene   :" + entry.getKey() + " total time " + entry.getValue()).out();
        }

        //对每个视频片段场景按时长进行降序排序
        Collections.sort(sceneClassTimeLengthList, new Comparator<Map.Entry<Integer, Long>>() {
            public int compare(Map.Entry<Integer, Long> file, Map.Entry<Integer, Long> newFile) {
                if (file.getValue() > newFile.getValue()) {//降序
                    return -1;
                } else if (file.getValue() == newFile.getValue()) {
                    return 0;
                } else {
                    return 1;
                }
            }
        });
        if (themeTypeTimeLengthList.size() == 0) {
            videoTempleteType = VideoSegment.VideoTempleteType.SCENE;
        }
        if (sceneClassTimeLengthList.size() == 0) {
            videoTempleteType = VideoSegment.VideoTempleteType.THEME;
        }
        if (themeTypeTimeLengthList.size() > 0 && sceneClassTimeLengthList.size() > 0) {
            if (themeTypeLongMap.get(themeTypeTimeLengthList.get(0).getThemeType()) >= sceneClassLongMap.get(sceneClassTimeLengthList.get(0).getKey())) {
                videoTempleteType = VideoSegment.VideoTempleteType.THEME;
            } else {
                videoTempleteType = VideoSegment.VideoTempleteType.SCENE;
            }
        }
        Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg(" default template type   :" + videoTempleteType).out();
        mThemeTypeTimeLengthList = themeTypeTimeLengthList;
        mSceneClassTimeLengthList.clear();
        for (int i = 0; i < sceneClassTimeLengthList.size(); i++) {
            mSceneClassTimeLengthList.add(sceneClassTimeLengthList.get(i).getKey());
        }

        //对每个视频片段按得分进行降序排序
        Collections.sort(allVideoSegmentList, new Comparator<VideoSegment>() {
            public int compare(VideoSegment file, VideoSegment newFile) {
                if (file.getScore() > newFile.getScore()) {//降序
                    return -1;
                } else if (file.getScore() == newFile.getScore()) {
                    return 0;
                } else {
                    return 1;
                }
            }
        });
        mAllSortedVideoSegmentList = allVideoSegmentList;
        AiCameraApplication.mApplication.mAllSortedVideoSegmentList = allVideoSegmentList;
        return allVideoSegmentList;
    }

    /**
     * 获取片段主体
     *
     * @param videoSegment
     * @return
     */
    @NonNull
    private VideoSegment.ThemeType getVideoSegmentThemeType(VideoSegment videoSegment) {
        VideoTag videoTag = videoSegment.getVideoTag();
        if (videoTag != null && StringUtil.notEmpty(videoTag.getObject_detect())) {
            String objectDetectStr = videoTag.getObject_detect();
//            Toast.makeText(mContext, "objectDetectStr="+objectDetectStr, Toast.LENGTH_LONG).show();
            int objectDetect = Integer.valueOf(objectDetectStr);
            // 表示有人
            if ((objectDetect & Integer.parseInt(THEME_ALL_PERSON, 2)) > 0) {
                // 有宠物
                if ((objectDetect & Integer.parseInt(THEME_PET, 2)) > 0) {
                    return VideoSegment.ThemeType.PERSON_AND_PET;
                } else { // 有人 没宠物
                    if ((objectDetect & Integer.parseInt(THEME_MULTI_PERSON, 2)) > 0) {
                        // 只要第一组八位中的第四位是1，就表示多人
                        return VideoSegment.ThemeType.MULTI_PERSON;
                    } else if ((objectDetect & Integer.parseInt(THEME_DOUBLE_PERSON, 2)) > 0) {
                        // 第一组八位中的第三位是1，就表示两人(此时已经排除了多人的情况)
                        return VideoSegment.ThemeType.DOUBLE_PERSON;
                    } else if ((objectDetect & Integer.parseInt(THEME_SINGLE_PERSON, 2)) > 0) {
                        //第一组八位中的第二位是1，就表示单人(此时已经排除了多人、两人的情况)
                        return VideoSegment.ThemeType.SINGLE_PERSON;
                    }
                }
            } else { // 没人有宠物
                if ((objectDetect & Integer.parseInt(THEME_PET, 2)) > 0) {
                    return VideoSegment.ThemeType.PET;
                }
            }
        }
        return VideoSegment.ThemeType.NONE; // 其他情况
    }

    /**
     * 获取片段算法分值
     *
     * @param videoSegment
     * @return
     */
    @NonNull
    private float getVideoSegmentAlgorithmScore(VideoSegment videoSegment) {
        float score =0f;
        VideoTag videoTag = videoSegment.getVideoTag();
        if (videoTag != null && StringUtil.notEmpty(videoTag.getObject_detect())) {
            String objectDetectStr = videoTag.getObject_detect();
            String genderClassStr = videoTag.getGender_class();
            int objectDetect = Integer.valueOf(objectDetectStr);
            int genderClass = 0;
            if(StringUtil.notEmpty(genderClassStr)){
                genderClass = Integer.valueOf(genderClassStr);
            }
            if ((objectDetect & Integer.parseInt(THEME_MULTI_PERSON, 2)) > 0) {
                Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("objectDetect  :多人   score=0"  ).out();
                return score;
            }
            if ((objectDetect & Integer.parseInt(THEME_DOUBLE_PERSON, 2)) > 0) {
                // 第一组八位中的第三位是1，就表示两人(此时已经排除了多人的情况)
                if ((objectDetect & Integer.parseInt(THEME_PET, 2)) > 0) {
                    Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("objectDetect  :双人、宠物        score=5"  ).out();
                    score=ScoreItemConstants.SCORE_FOR_DOUBLE_PERSON_AND_PET;
                }
                if ((genderClass & Integer.parseInt(SINGLE_MAN_PERSON, 2)) > 0
                        &&(genderClass & Integer.parseInt(MORE_THAN_DOUBLE_MAN, 2)) == 0
                        &&(genderClass & Integer.parseInt(MORE_THAN_DOUBLE_WOMAN, 2)) == 0
                        &&(genderClass & Integer.parseInt(SINGLE_WOMAN_PERSON, 2)) > 0) {
                    score+=ScoreItemConstants.SCORE_FOR_DOUBLE_PERSON_MAN_AND_WOMAN;
                    Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("objectDetect  :双人、一男一女     score=5"  ).out();
                }
                return score;
            }
            if ((objectDetect & Integer.parseInt(THEME_SINGLE_PERSON, 2)) > 0 && (objectDetect & Integer.parseInt(THEME_PET, 2)) > 0) {
                //第一组八位中的第二位是1，就表示单人(此时已经排除了多人、两人的情况)
                Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("objectDetect  :单人、宠物      score=5"  ).out();
                return ScoreItemConstants.SCORE_FOR_SINGLE_PERSON_AND_PET;
            }
        }
//            Toast.makeText(mContext, "objectDetectStr="+objectDetectStr, Toast.LENGTH_LONG).show();
        return score;
    }



    /**
     * 填充模板所需的 视频片段数 和 所需时长
     *
     * @param videoTemplete
     * @param allSortedVideoSegmentList
     */
    public static VideoTemplete newFillVideoTempleteData(VideoTemplete videoTemplete, List<VideoSegment> allSortedVideoSegmentList) {
        if (makeVideoType == MakeVideoType.TODAY_WONDERFUL) {
            if(allSortedVideoSegmentList.size()<= videoTemplete.getVideo_segment().size()){
//            if(true){
                todayWondelfulFillVideoSegmentCount(videoTemplete, allSortedVideoSegmentList);
            }else {
                todayWonderfulFillScoreGroupVideoSegmentList(videoTemplete, allSortedVideoSegmentList);
            }
        } else {
            sideKeyFillVideoSegmentCount(videoTemplete, allSortedVideoSegmentList);
        }

//        (模板所需的视频片段数 和 所需时长填充完毕) --》 对模板中的视频片段按开始时间进行排序
        Collections.sort(videoTemplete.getVideoSegmentList(), new Comparator<VideoSegment>() {
            public int compare(VideoSegment file, VideoSegment newFile) {
                if (file.getStart_time() < newFile.getStart_time()) {//升序
                    return -1;
                } else if (file.getStart_time() == newFile.getStart_time()) {
                    return 0;
                } else {
                    return 1;
                }
            }
        });

        // (每个视频模板所需的视频片段数进行补充) --》 对每个视频模板所需的视频 时长 进行补充
        for (int i = 0; (i < videoTemplete.getVideoSegmentList().size() && i < videoTemplete.getVideo_segment().size()); i++) {
            VideoSegment videoSegment = videoTemplete.getVideoSegmentList().get(i);
            //视频片段时长
            long videoLength = videoSegment.getEnd_time() - videoSegment.getStart_time();
            //视频文件时长
            long totalLength = videoSegment.getClose_time() - videoSegment.getCreate_time();
            //模板片段所需时长
            long templeteLength = (long) (Float.valueOf(videoTemplete.getVideo_segment().get(i).getVideo_segment_length()) * 1000);
            Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("match template download time templeteLength " + templeteLength+" segment time " + videoLength+" file  length " + totalLength).out();
            if (videoLength > templeteLength) {
                //视频片段时长过长 需要往两边裁剪
                long halfLength = templeteLength / 2;
                List<String> happyFaceList = videoSegment.getVideoTag().getHappyFaceList();
                List<String> forwardFaceList = videoSegment.getVideoTag().getForwardFaceList();

                //获取该片段的第一个笑脸和正脸的时间点
                long happyFaceCreateTime = 0;
                if (happyFaceList.size() > 0) {
                    happyFaceCreateTime = Long.valueOf(happyFaceList.get(0));
                }
                long forwardFaceCreateTime = 0;
                if (forwardFaceList.size() > 0) {
                    forwardFaceCreateTime = Long.valueOf(forwardFaceList.get(0));
                }

                //遍历打分项列表videoScoreTypeList 获取第一个Mark 和云台静止对象
                List<VideoScoreType> videoScoreTypeList = videoSegment.getVideoScoreTypeList();
                VideoScoreType mark = null;
                VideoScoreType pizStill = null;
                boolean findMark = false;
                boolean findPizStill = false;
                for (int k = 0; k < videoScoreTypeList.size(); k++) {
                    if ((mark == null) && videoScoreTypeList.get(k).getScoreType() == VideoScoreType.ScoreType.MARK) {
                        mark = videoScoreTypeList.get(k);
                        findMark = true;
                    }
                    if ((pizStill == null) && videoScoreTypeList.get(k).getScoreType() == VideoScoreType.ScoreType.PIZ_STILL) {
                        pizStill = videoScoreTypeList.get(k);
                        findPizStill = true;
                    }
                    if (findMark && findPizStill) {
                        break;
                    }
                }

                if (mark != null) {
                    Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("mark timePoint=" +mark.getStart_time() ).out();
                    dealTempleteTimeLenth(videoSegment, halfLength, mark.getStart_time());
                } else if (happyFaceCreateTime > 0) {
                    Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("happy face timePoint=" +happyFaceCreateTime).out();
                    dealTempleteTimeLenth(videoSegment, halfLength, happyFaceCreateTime);
                } else if (pizStill != null) {
                    long timePoint = (Math.min(pizStill.getEnd_time(),videoSegment.getEnd_time()) - Math.max(pizStill.getStart_time(), videoSegment.getStart_time())) / 2+Math.max(pizStill.getStart_time(), videoSegment.getStart_time());
                    Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("pizStill timePoint=" +timePoint).out();
                    dealTempleteTimeLenth(videoSegment, halfLength, timePoint);
                } else if (forwardFaceCreateTime > 0) {
                    Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("forward face timePoint=" +forwardFaceCreateTime).out();
                    dealTempleteTimeLenth(videoSegment, halfLength, forwardFaceCreateTime);
                } else {
                    // 没有以上标记项
                    // startTime不变
                    Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("no timePoint").out();
                    dealTempleteTimeLenth(videoSegment, halfLength, videoSegment.getStart_time()+halfLength);
                }
                Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("match template download real downtime segment start time  videoLength>templeteLength  " + DateUtils.DateFormat(videoSegment.getStart_time())+"  start timestamp="+videoSegment.getStart_time() + "  segment end time="+ DateUtils.DateFormat(videoSegment.getEnd_time())+"  end timestamp="+videoSegment.getEnd_time() + " score=" + videoSegment.getScore() + "   file path " + videoSegment.getFilePath()
                        + " create time " + DateUtils.DateFormat(videoSegment.getCreate_time()) + " close time " + DateUtils.DateFormat(videoSegment.getClose_time()) + " halfLength " + halfLength + " forwardFaceCreateTime " + DateUtils.DateFormat(forwardFaceCreateTime)+" need segment length  "+(videoSegment.getEnd_time() - videoSegment.getStart_time())).out();
            } else if (videoLength < templeteLength) {
                //视频片段时长不够  需要往两边扩充
                long needExpandLength = (long) ((templeteLength - videoLength) / 2.0f);
//                if ((totalLength + needExpandLength * 2) <= templeteLength) {
                if (totalLength <= templeteLength) {
                    videoSegment.setStart_time(videoSegment.getCreate_time());
                    videoSegment.setEnd_time(videoSegment.getClose_time());
                } else {
                    videoSegment.setStart_time(videoSegment.getStart_time() - needExpandLength);
                    videoSegment.setEnd_time(videoSegment.getEnd_time() + needExpandLength);
                    if (videoSegment.getStart_time() < videoSegment.getCreate_time()) {
                        //扩展碰到视频文件的左边界
                        long needAppendEndTime = videoSegment.getCreate_time() - videoSegment.getStart_time();
                        videoSegment.setStart_time(videoSegment.getCreate_time());
                        videoSegment.setEnd_time(videoSegment.getEnd_time() + needAppendEndTime);
                    }
                    if (videoSegment.getEnd_time() > videoSegment.getClose_time()) {
                        //扩展碰到视频文件的右边界
                        long needAppendStartTime = videoSegment.getEnd_time() - videoSegment.getClose_time();
                        videoSegment.setEnd_time(videoSegment.getClose_time());
                        videoSegment.setStart_time(videoSegment.getStart_time() - needAppendStartTime);
                        if (videoSegment.getStart_time() < videoSegment.getCreate_time()) {
                            //扩展碰到视频文件的左边界
                            videoSegment.setStart_time(videoSegment.getCreate_time());
                        }
                    }
                }
                Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("match template download real downtime segment start time  videoLength<templeteLength  " + DateUtils.DateFormat(videoSegment.getStart_time()) +" start timestamp="+videoSegment.getStart_time()+ "  segment end time=" + DateUtils.DateFormat(videoSegment.getEnd_time())+"  end timestamp="+videoSegment.getEnd_time() + " score=" + videoSegment.getScore() + "  file path " + videoSegment.getFilePath() + " create time "
                        + DateUtils.DateFormat(videoSegment.getCreate_time()) + " close time " + DateUtils.DateFormat(videoSegment.getClose_time()) + " templeteLength " + templeteLength + " videoLength " + videoLength+" need segment length  "+(videoSegment.getEnd_time() - videoSegment.getStart_time())).out();
            }
        }


//        for(int i=0;i<videoTemplete.getVideoSegmentList().size();i++){
//            LoggerUtils.printLog(videoTemplete.getVideoSegmentList().get(i).toString()+"--shichang="+videoTemplete.getVideo_segment().get(i).getVideo_segment_length()+"totalLength = "+(videoTemplete.getVideoSegmentList().get(i).getClose_time()-videoTemplete.getVideoSegmentList().get(i).getCreate_time()));
//        }
        return videoTemplete;
    }

    /**
     * 按高中低分 分组后 随机选取填充视频片段个数
     * @param videoTemplete
     * @param allSortedVideoSegmentList
     */
    private static void todayWonderfulFillScoreGroupVideoSegmentList(VideoTemplete videoTemplete, List<VideoSegment> allSortedVideoSegmentList) {
        List<VideoSegment> highScoreVideoSegmentList = new ArrayList<>();
        List<VideoSegment> mediumScoreVideoSegmentList = new ArrayList<>();
        List<VideoSegment> lowScoreVideoSegmentList = new ArrayList<>();
        List<VideoSegment> restScoreVideoSegmentList = new ArrayList<>();

        int highScoreCount = (int) (allSortedVideoSegmentList.size()*VideoSegmentCountPercentConstants.VIDEO_FILE_HIGH_SCORE_PERCENT);
        int mediumScoreCount = (int) (allSortedVideoSegmentList.size()*VideoSegmentCountPercentConstants.VIDEO_FILE_MEDIUM_SCORE_PERCENT);
        int lowScoreCount = (int) (allSortedVideoSegmentList.size()*VideoSegmentCountPercentConstants.VIDEO_FILE_LOW_SCORE_PERCENT);
        Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("highScoreCount count=" +highScoreCount+"  mediumScoreCount count=" +mediumScoreCount
                +"  lowScoreCount count=" +lowScoreCount).out();
        for(int i=0; i<allSortedVideoSegmentList.size(); i++){
            if(i<highScoreCount){
                highScoreVideoSegmentList.add(allSortedVideoSegmentList.get(i).clone());
            }else if(i<(highScoreCount+mediumScoreCount)){
                mediumScoreVideoSegmentList.add(allSortedVideoSegmentList.get(i).clone());
            }else if(i<(highScoreCount+mediumScoreCount+lowScoreCount)){
                lowScoreVideoSegmentList.add(allSortedVideoSegmentList.get(i).clone());
            }else {
                restScoreVideoSegmentList.add(allSortedVideoSegmentList.get(i).clone());
            }
         }
        Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("highScoreVideoSegmentList count=" +highScoreVideoSegmentList.size()+"  mediumScoreVideoSegmentList count=" +mediumScoreVideoSegmentList.size()
                +"  lowScoreVideoSegmentList count=" +lowScoreVideoSegmentList.size()+"  restScoreVideoSegmentList count=" +restScoreVideoSegmentList.size()
        +"  allSortedVideoSegmentList count="+allSortedVideoSegmentList.size()).out();

        int needFillCountForHighScore = (int) (videoTemplete.getVideo_segment().size()*VideoSegmentCountPercentConstants.VIDEO_TEMPLETE_PERCENT_FOR_HIGH_SCORE);
        int needFillCountForMediumScore = (int) (videoTemplete.getVideo_segment().size()*VideoSegmentCountPercentConstants.VIDEO_TEMPLETE_PERCENT_FOR_MEDIUM_SCORE);
        int needFillCountForLowScore = videoTemplete.getVideo_segment().size()-needFillCountForHighScore-needFillCountForMediumScore;
        Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("needFillCountForHighScore count=" +needFillCountForHighScore+"  needFillCountForMediumScore count=" +needFillCountForMediumScore
                +"  needFillCountForLowScore count=" +needFillCountForLowScore).out();

        videoTemplete.getVideoSegmentList().clear();//切记：添加一定要清空list
        int needFill2ScoreListCount = needFillCountForHighScore+needFillCountForMediumScore;
        int needFill3ScoreListCount = needFillCountForHighScore+needFillCountForMediumScore+needFillCountForLowScore;
        fillGroupedVideoSegmentList(videoTemplete, highScoreVideoSegmentList, needFillCountForHighScore, needFillCountForHighScore, restScoreVideoSegmentList);
        Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("actuall fill videoSegment for HighScore count=" +videoTemplete.getVideoSegmentList().size()+"  restScoreVideoSegmentList count=" +restScoreVideoSegmentList.size()).out();
        fillGroupedVideoSegmentList(videoTemplete, mediumScoreVideoSegmentList, needFillCountForMediumScore, needFill2ScoreListCount, restScoreVideoSegmentList);
        Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("actuall fill videoSegment for High+MediumScore count =" +videoTemplete.getVideoSegmentList().size()+"  restScoreVideoSegmentList count=" +restScoreVideoSegmentList.size()).out();
        fillGroupedVideoSegmentList(videoTemplete, lowScoreVideoSegmentList, needFillCountForLowScore, needFill3ScoreListCount, restScoreVideoSegmentList);
        Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("actuall fill videoSegment for High Medium LowScore count=" +videoTemplete.getVideoSegmentList().size()+"  restScoreVideoSegmentList count=" +restScoreVideoSegmentList.size()).out();

        Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("need videoSegment count: videoTemplete.getVideo_segment().size()="+videoTemplete.getVideo_segment().size()+"  actuall fill videoSegment count: videoTemplete.getVideoSegmentList().size()=" +videoTemplete.getVideoSegmentList().size()).out();
        //填充的视频片段数不够，从剩余的片段中补充
        if(videoTemplete.getVideoSegmentList().size() < videoTemplete.getVideo_segment().size()){
            Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("begin first 11111 add videoSegment: restScoreVideoSegmentList.size()="+restScoreVideoSegmentList.size()).out();
            List<VideoSegment> tmpRestScoreList = new ArrayList();
            boolean  hasFilledVideoSegmentCount = randomFillVideoSegmentList(videoTemplete, restScoreVideoSegmentList, videoTemplete.getVideo_segment().size(),tmpRestScoreList);

            restScoreVideoSegmentList.addAll(tmpRestScoreList);
            if(!hasFilledVideoSegmentCount){
                //对剩余视频片段按得分进行降序排序
                Collections.sort(restScoreVideoSegmentList, new Comparator<VideoSegment>() {
                    public int compare(VideoSegment file, VideoSegment newFile) {
                        if (file.getScore() > newFile.getScore()) {//降序
                            return -1;
                        } else if (file.getScore() == newFile.getScore()) {
                            return 0;
                        } else {
                            return 1;
                        }
                    }
                });

                Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("begin second 2222 add videoSegment: restScoreVideoSegmentList.size()="+restScoreVideoSegmentList.size()).out();
                for(int i=0; i<restScoreVideoSegmentList.size();i++){
                    VideoSegment videoSegment = restScoreVideoSegmentList.get(i);
                    videoTemplete.getVideoSegmentList().add(videoSegment.clone());
                    if(videoTemplete.getVideoSegmentList().size()>=videoTemplete.getVideo_segment().size()){
                        break;
                    }
                }
            }
        }
    }


    /**
     * 填充 按高 中 低分 分组的视频片段列表
     * @param videoTemplete
     * @param specificScoreGroupedVideoSegmentList
     * @param restScoreList
     * @param needFillCountForSpecificScore
     * @param needFillSeveralScoreCount
     */
    public static void fillGroupedVideoSegmentList(VideoTemplete videoTemplete, List<VideoSegment> specificScoreGroupedVideoSegmentList, int needFillCountForSpecificScore, int needFillSeveralScoreCount, List<VideoSegment> restScoreList) {
        Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("specificScoreGroupedVideoSegmentList count=" +specificScoreGroupedVideoSegmentList.size()+"  needFillCountForSpecificScore count=" +needFillCountForSpecificScore).out();

        if(specificScoreGroupedVideoSegmentList.size()<=needFillCountForSpecificScore){
            selectAndFillVideoSegmentList(videoTemplete, videoTemplete.getVideoSegmentList(), specificScoreGroupedVideoSegmentList);
        }else {
            randomFillVideoSegmentList(videoTemplete, specificScoreGroupedVideoSegmentList, needFillSeveralScoreCount, restScoreList);
        }
    }

    /**
     * 随机选取不相邻的视频片段填充视频模板
     * @param videoTemplete
     * @param specificScoreGroupedVideoSegmentList
     * @param needFillSeveralScoreCount
     * @param restScoreList
     * @return
     */
    private static boolean randomFillVideoSegmentList(VideoTemplete videoTemplete, List<VideoSegment> specificScoreGroupedVideoSegmentList, int needFillSeveralScoreCount, List<VideoSegment> restScoreList) {
        boolean hasFilledVideoSegmentCount = false;
        Random random = new Random();
        Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("random select not adjoin videoSegment: videoTemplete.getVideoSegmentList().size()="+videoTemplete.getVideoSegmentList().size()+"   needFillSeveralScoreCount count=" +needFillSeveralScoreCount+"   specificScoreGroupedVideoSegmentList count=" +specificScoreGroupedVideoSegmentList.size()).out();
        while(videoTemplete.getVideoSegmentList().size()<needFillSeveralScoreCount && specificScoreGroupedVideoSegmentList.size()>0){
             int position = random.nextInt(specificScoreGroupedVideoSegmentList.size());
              VideoSegment videoSegment = specificScoreGroupedVideoSegmentList.get(position);
              boolean notAdjoin = checkVideoSegmentAdjoin(videoSegment, videoTemplete.getVideoSegmentList(), getMaxVideoSegmentLengthInVideoTemplete((videoTemplete)));
              if(notAdjoin){
                  videoTemplete.getVideoSegmentList().add(videoSegment.clone());
              }else {
                  restScoreList.add(videoSegment.clone());
              }
              specificScoreGroupedVideoSegmentList.remove(videoSegment);
        }

        if(videoTemplete.getVideoSegmentList().size()>=videoTemplete.getVideo_segment().size()){
            hasFilledVideoSegmentCount = true;
        }
        return hasFilledVideoSegmentCount;
    }

    /**
     * 侧面键填充模板所需的视频片段数
     *
     * @param videoTemplete
     * @param allSortedVideoSegmentList
     */
    private static void sideKeyFillVideoSegmentCount(VideoTemplete videoTemplete, List<VideoSegment> allSortedVideoSegmentList) {
        //同一个视频文件按得分高低排好序的视频片段列表
        Map<String, List<VideoSegment>> sideKeyVideoSegmentMap = new HashMap<>();
        //把来自同一个视频文件的视频片段进行合并
        for (int i = 0; i < allSortedVideoSegmentList.size(); i++) {
            VideoSegment videoSegment = allSortedVideoSegmentList.get(i);
            if (sideKeyVideoSegmentMap.containsKey(videoSegment.getFilePath())) {
                sideKeyVideoSegmentMap.get(videoSegment.getFilePath()).add(videoSegment);
            } else {
                List<VideoSegment> videoSegmentList = new ArrayList<>();
                videoSegmentList.add(videoSegment);
                sideKeyVideoSegmentMap.put(videoSegment.getFilePath(), videoSegmentList);
            }
            Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("side key file list sorts file name  " + videoSegment.getFilePath() + " start time  " + DateUtils.DateFormat(videoSegment.getStart_time()) + " end time " + DateUtils.DateFormat(videoSegment.getEnd_time()) + " store " + videoSegment.getScore()).out();
        }

        videoTemplete.getVideoSegmentList().clear();
        //遍历所有视频文件
        for (List<VideoSegment> segmentList : sideKeyVideoSegmentMap.values()) {
            Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("side key file segment count--  " + segmentList.size()).out();
            boolean hasThemeVideoFragment = false;
            int count=0;
            //遍历一个视频文件中按得分高低排好序的视频片段列表
            for (int i = 0; i < segmentList.size(); i++) {
                VideoSegment videoSegment = segmentList.get(i);
                //判断视频片段是否==无主体
                if (videoSegment.getThemeType() != VideoSegment.ThemeType.NONE) {
                    hasThemeVideoFragment = true;
                    videoTemplete.getVideoSegmentList().add(videoSegment.clone());
                    count++;
                    Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("side key select segment file name  " + videoSegment.getFilePath() + " start time  " + DateUtils.DateFormat(videoSegment.getStart_time()) + " end time " + DateUtils.DateFormat(videoSegment.getEnd_time()) + " store " + videoSegment.getScore()).out();
                    //侧面键每个文件取两个片段
                    if(count>=2){
                        break;
                    }
                }
            }

            //侧面键每个文件取两个片段
            if (count == 0) {
                videoTemplete.getVideoSegmentList().add(segmentList.get(0).clone());
                Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("side key select segment none object  file name  " + segmentList.get(0).getFilePath() + " start time  " + DateUtils.DateFormat(segmentList.get(0).getStart_time()) + " end time " + DateUtils.DateFormat(segmentList.get(0).getEnd_time()) + " store " + segmentList.get(0).getScore()).out();
                if(segmentList.size()>1){
                    videoTemplete.getVideoSegmentList().add(segmentList.get(1).clone());
                    Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("side key select segment none object  file name  " + segmentList.get(1).getFilePath() + " start time  " + DateUtils.DateFormat(segmentList.get(1).getStart_time()) + " end time " + DateUtils.DateFormat(segmentList.get(1).getEnd_time()) + " store " + segmentList.get(1).getScore()).out();
                }
            } else if (count == 1) {
                for (int i = 0; i < segmentList.size(); i++) {
                    VideoSegment videoSegment = segmentList.get(i);
                    if (videoSegment.getVideoSegmentId() != videoTemplete.getVideoSegmentList().get(videoTemplete.getVideoSegmentList().size()-1).getVideoSegmentId()) {
                        videoTemplete.getVideoSegmentList().add(videoSegment.clone());
                        Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("side key select  2 segment none object file name--  " + videoSegment.getFilePath() + " start time  " + DateUtils.DateFormat(videoSegment.getStart_time()) + " end time " + DateUtils.DateFormat(videoSegment.getEnd_time()) + " store " + videoSegment.getScore()).out();
                        break;
                    }
                }
            }
        }

        Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("side key select segment count--  " + videoTemplete.getVideoSegmentList().size()).out();
        List<VideoSegment> singleVideoSegmentList = new ArrayList<>();
        List<VideoSegment> doubleVideoSegmentList = new ArrayList<>();
        for(int i=0; i<videoTemplete.getVideoSegmentList().size();i++){
            if((i+1)%2==1){
                singleVideoSegmentList.add(videoTemplete.getVideoSegmentList().get(i));
            }else {
                doubleVideoSegmentList.add(videoTemplete.getVideoSegmentList().get(i));
            }
        }
        Collections.sort(doubleVideoSegmentList, new Comparator<VideoSegment>() {
                    @Override
                    public int compare(VideoSegment item1, VideoSegment item2) {
                        if (item1.getCreate_time() > item2.getCreate_time()) {//升序
                            return 1;
                        } else if (item1.getCreate_time() == item2.getCreate_time()) {
                            return 0;
                        } else {
                            return -1;
                        }
                    }
                }
        );
        Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("side key  templete need segment count="+videoTemplete.getVideo_segment().size()+"       single select segment count--  " + singleVideoSegmentList.size()).out();
        videoTemplete.getVideoSegmentList().clear();
        videoTemplete.getVideoSegmentList().addAll(singleVideoSegmentList);
        int needAddCount  = videoTemplete.getVideo_segment().size()-singleVideoSegmentList.size();
        Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("side key needAddCount--  " + needAddCount).out();
        for(int i=0; (i<needAddCount && i<doubleVideoSegmentList.size()); i++){
            videoTemplete.getVideoSegmentList().add(doubleVideoSegmentList.get(i));
        }
        Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("side key select segment final count--  " + videoTemplete.getVideoSegmentList().size()).out();
    }


    /**
     * 今日精彩模板所需的视频片段数填充
     *
     * @param videoTemplete
     * @param sortedAllVideoSegmentList
     */
    private static void todayWondelfulFillVideoSegmentCount(VideoTemplete videoTemplete, List<VideoSegment> sortedAllVideoSegmentList) {


        videoTemplete.getVideoSegmentList().clear();
        //1.对模板所需的视频 片段数 进行补充
        selectAndFillVideoSegmentList(videoTemplete, videoTemplete.getVideoSegmentList(), sortedAllVideoSegmentList);

        //对scoredVideoList循环完毕，找到的视频段数还是小于needCount,
        //从所有视频片段中，除了已经存在于resultArray的片段之外，再按分数取前restCount个片段
        if (videoTemplete.getVideoSegmentList().size() < videoTemplete.getVideo_segment().size()) {

            int needBeiShu = videoTemplete.getVideo_segment().size() / sortedAllVideoSegmentList.size();
            if (needBeiShu > 0) {
                videoTemplete.getVideoSegmentList().clear();
                int needAddCount = videoTemplete.getVideo_segment().size() - sortedAllVideoSegmentList.size() * needBeiShu;
                for (int i = 0; i < needBeiShu; i++) {
                    for (int j = 0; j < sortedAllVideoSegmentList.size(); j++) {
                        videoTemplete.getVideoSegmentList().add(sortedAllVideoSegmentList.get(j).clone());
                        Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("real template size > segment size，match other video segment to template  position " + j + " start time  " + DateUtils.DateFormat(sortedAllVideoSegmentList.get(j).getStart_time()) + " end time " + DateUtils.DateFormat(sortedAllVideoSegmentList.get(j).getEnd_time()) + " store " + sortedAllVideoSegmentList.get(j).getScore() + " file " + sortedAllVideoSegmentList.get(j).getFilePath()).out();
                    }
                }
                for (int i = 0; i < needAddCount; i++) {
                    videoTemplete.getVideoSegmentList().add(sortedAllVideoSegmentList.get(i).clone());
                    Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("real template size > segment size，needAddCount match other video segment to template  position " + i + " start time  " + DateUtils.DateFormat(sortedAllVideoSegmentList.get(i).getStart_time()) + " end time " + DateUtils.DateFormat(sortedAllVideoSegmentList.get(i).getEnd_time()) + " store " + sortedAllVideoSegmentList.get(i).getScore() + " file " + sortedAllVideoSegmentList.get(i).getFilePath()).out();
                }
            } else {
                List<String> segmentIdList = new ArrayList<>();
                for (int j = 0; j < videoTemplete.getVideoSegmentList().size(); j++) {
                    segmentIdList.add(videoTemplete.getVideoSegmentList().get(j).getVideoSegmentId());
                }

                int restCount = videoTemplete.getVideo_segment().size() - videoTemplete.getVideoSegmentList().size();
                for (int i = 0; i < restCount; i++) {
                    for (int j = 0; j < sortedAllVideoSegmentList.size(); j++) {
                        if (!segmentIdList.contains(sortedAllVideoSegmentList.get(j).getVideoSegmentId())) {
                            VideoSegment videoSegment = sortedAllVideoSegmentList.get(j).clone();
                            videoTemplete.getVideoSegmentList().add(videoSegment);
                            segmentIdList.add(videoSegment.getVideoSegmentId());
                            Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("real template size < segment size, match other video segment to template  position " + j + " start time  " + DateUtils.DateFormat(sortedAllVideoSegmentList.get(j).getStart_time()) + " end time " + DateUtils.DateFormat(sortedAllVideoSegmentList.get(j).getEnd_time()) + " store " + sortedAllVideoSegmentList.get(j).getScore() + " file " + sortedAllVideoSegmentList.get(j).getFilePath()).out();
                            break;
                        }
                    }
                }
            }
        }
    }

    private static void selectAndFillVideoSegmentList(VideoTemplete videoTemplete, List<VideoSegment> needFillVideoSegmentList, List<VideoSegment> sortedGroupOrAllVideoSegmentList) {
        long maxTemplateLength = getMaxVideoSegmentLengthInVideoTemplete(videoTemplete);


        for (int i = 0; i < sortedGroupOrAllVideoSegmentList.size(); i++) {
            VideoSegment videoSegment = sortedGroupOrAllVideoSegmentList.get(i);
            Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("add video segment to template  position " + i + " start time  " + DateUtils.DateFormat(videoSegment.getStart_time()) +" start time timesp "+videoSegment.getStart_time() + " end time " + DateUtils.DateFormat(videoSegment.getEnd_time()) + " store " + videoSegment.getScore() + " file " + videoSegment.getFilePath()).out();
            if (i == 0) {
                needFillVideoSegmentList.add(videoSegment.clone());
                Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("real match video segment to template  position " + i + " start time  " + DateUtils.DateFormat(videoSegment.getStart_time()) + " end time " + DateUtils.DateFormat(videoSegment.getEnd_time()) + " store " + videoSegment.getScore() + " file " + videoSegment.getFilePath()).out();
                continue;
            }
            boolean matched = checkVideoSegmentAdjoin(videoSegment, needFillVideoSegmentList, maxTemplateLength);

            if (matched) {
                needFillVideoSegmentList.add(videoSegment.clone());
                Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("real match video segment to template  position " + i + " start time  " + DateUtils.DateFormat(videoSegment.getStart_time()) + " end time " + DateUtils.DateFormat(videoSegment.getEnd_time()) + " store " + videoSegment.getScore() + " file " + videoSegment.getFilePath()).out();
            }

            //填充的视频片段数 >= 模板所需的视频片段数， 则直接跳出循环
            if (needFillVideoSegmentList.size() >= videoTemplete.getVideo_segment().size()) {
                break;
            }
        }
    }

    /**
     * 获取模板所需的视频片段中最大的视频片段长度
     * @param videoTemplete
     * @return
     */
    private static long getMaxVideoSegmentLengthInVideoTemplete(VideoTemplete videoTemplete) {
        //找出模板所需片段中所需的最大时长
        long maxTemplateLength = 0l;
        for (int i = 0; i < videoTemplete.getVideo_segment().size(); i++) {
            long segmentLength = (long) (Float.valueOf(videoTemplete.getVideo_segment().get(i).getVideo_segment_length()) * 1000);
            if (segmentLength > maxTemplateLength) {
                maxTemplateLength = segmentLength;
            }
        }
        return maxTemplateLength;
    }

    /**
     * 检查视频片段是否和列表中的片段相邻
     * @param videoSegment
     * @param needFillVideoSegmentList
     * @param maxTemplateLength
     * @return
     */
    private static boolean checkVideoSegmentAdjoin(VideoSegment videoSegment, List<VideoSegment> needFillVideoSegmentList, long maxTemplateLength) {
        long fileTotalLength = videoSegment.getClose_time() - videoSegment.getCreate_time();
        boolean matched = true;
        for (int j = 0; j < needFillVideoSegmentList.size(); j++) {
            //产品要求每个视频片段是3s，选出的片段必须相隔1个片段，则阈值 = 3 * (1+1) = 6
            //模版所需要片段时长 > 原视频本身长度时，那就直接舍弃该视频片段
            Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("match video segment apace  select start time   "+videoSegment.getStart_time()+" list segment start time "+needFillVideoSegmentList.get(j).getStart_time()+ "  space " + Math.abs(videoSegment.getStart_time() - needFillVideoSegmentList.get(j).getStart_time()) + " fix space  " + DURATION_FOR_VIDEO_SEGMENT * 3 + " fileTotalLength " + fileTotalLength + " maxTemplateLength " + maxTemplateLength ).out();
            if (Math.abs(videoSegment.getStart_time() - needFillVideoSegmentList.get(j).getStart_time()) < DURATION_FOR_VIDEO_SEGMENT * 2
                    || fileTotalLength < maxTemplateLength) {
                matched = false;
                break;
            }
        }
        return matched;
    }

    private static void dealTempleteTimeLenth(VideoSegment videoSegment, long halfLength, long timePoint) {
        videoSegment.setStart_time(timePoint - halfLength);
        videoSegment.setEnd_time(timePoint + halfLength);
        if (videoSegment.getStart_time() < videoSegment.getCreate_time()) {
            //裁剪碰到视频文件的左边界
            long needAppendEndTime = videoSegment.getCreate_time() - videoSegment.getStart_time();
            videoSegment.setStart_time(videoSegment.getCreate_time());
            videoSegment.setEnd_time(videoSegment.getEnd_time() + needAppendEndTime);
        } else if (videoSegment.getEnd_time() > videoSegment.getClose_time()) {
            //裁剪碰到视频文件的右边界
            long needAppendStartTime = videoSegment.getEnd_time() - videoSegment.getClose_time();
            videoSegment.setEnd_time(videoSegment.getClose_time());
            videoSegment.setStart_time(videoSegment.getStart_time() - needAppendStartTime);
        }
    }


    /**
     * 为当天所有视频文件的视频片段进行打分
     *
     * @param videoFileList
     * @return
     */
    @NonNull
    public static List<VideoSegment> scoreVideoSegmentForTodayVideoFiles(List<VideoFile> videoFileList) {
        List<VideoSegment> allVideoSegmentList = new ArrayList<>();
        //遍历固件返回xml中的今日精彩的所有视频文件
        for (int m = 0; m < videoFileList.size(); m++) {
            List<VideoSegment> videoSegmentList = videoFileList.get(m).getVideoSegmentList();
            List<VideoScoreType> videoScoreTypeList = videoFileList.get(m).getVideoScoreTypeList();

            //对每个视频片段按标签权重计算得分
            for (int i = 0; i < videoSegmentList.size(); i++) {
                float score = 0f;
                VideoSegment videoSegment = videoSegmentList.get(i);
                if (VideoScoreType.CameraMode.HI_PDT_WORKMODE_LPSE_REC.getValue().equals(videoSegment.getRec_mode())) {
                    score += ScoreItemConstants.SCORE_FOR_LPSE_REC;
                }
                for (int j = 0; j < videoScoreTypeList.size(); j++) {
                    VideoScoreType videoScoreType = videoScoreTypeList.get(j);
                    if ((videoScoreType.getScoreType() == VideoScoreType.ScoreType.MARK) && !videoSegment.isAddMarkScore()
                            && videoScoreType.getStart_time() < videoSegment.getEnd_time()
                            && videoScoreType.getStart_time() > videoSegment.getStart_time()) {
                        score += ScoreItemConstants.SCORE_FOR_MARK;
                        videoSegment.setAddMarkScore(true);
                    } else if (videoScoreType.getScoreType() == VideoScoreType.ScoreType.TRACE_VIDEO) {
                        if (isAddScoreForCurrentVideoSegment(videoSegment, videoScoreType)) {
                            float radio = calculateVideoScoreAspect(videoSegment, videoScoreType);
                            score = ScoreItemConstants.SCORE_FOR_TRACE_VIDEO * radio + score;
                        }
                    } else if (videoScoreType.getScoreType() == VideoScoreType.ScoreType.PIZ_STILL) {
                        if (isAddScoreForCurrentVideoSegment(videoSegment, videoScoreType)) {
                            float radio = calculateVideoScoreAspect(videoSegment, videoScoreType);
                            score = ScoreItemConstants.SCORE_FOR_PIZ_STILL * radio + score;
                        }
                    }
                }
                LoggerUtils.printLog("score = " + score);
                videoSegment.setScore(score);
            }
            allVideoSegmentList.addAll(videoSegmentList);
        }

        //对每个视频片段按得分进行降序排序
        Collections.sort(allVideoSegmentList, new Comparator<VideoSegment>() {
            public int compare(VideoSegment file, VideoSegment newFile) {
                if (file.getScore() > newFile.getScore()) {//降序
                    return -1;
                } else if (file.getScore() == newFile.getScore()) {
                    return 0;
                } else {
                    return 1;
                }
            }
        });
        return allVideoSegmentList;
    }

    /**
     * 填充模板所需的 视频片段数 和 所需时长
     *
     * @param videoTemplete
     * @param allVideoSegmentList
     */
    public static VideoTemplete fillVideoTempleteData(VideoTemplete videoTemplete, List<VideoSegment> allVideoSegmentList) {
        //1.对每个视频模板所需的视频 片段数 进行补充
        if (videoTemplete.getVideo_segment().size() > allVideoSegmentList.size()) {
            int needBeiShu = videoTemplete.getVideo_segment().size() / allVideoSegmentList.size();
            int needAddCount = videoTemplete.getVideo_segment().size() - allVideoSegmentList.size() * needBeiShu;
            for (int i = 0; i < needBeiShu; i++) {
                videoTemplete.getVideoSegmentList().addAll(allVideoSegmentList);
            }
            for (int i = 0; i < needAddCount; i++) {
                videoTemplete.getVideoSegmentList().add(allVideoSegmentList.get(i));
            }

//                int needAddVideoFragmentCount = videoTemplete.getVideo_segment().size()-videoTemplete.getVideoSegmentList().size();
//                if(needAddVideoFragmentCount>0){
//                    for(int i=0; (i<allVideoSegmentList.size()&&needAddVideoFragmentCount>0);i++){
//                        boolean  exist = false;
//                        for(int j=0;j<videoTemplete.getVideoSegmentList().size();i++) {
//                            if(videoTemplete.getVideoSegmentList().get(j).getVideo_id()==allVideoSegmentList.get(i).getVideo_id()){
//                                exist = true;
//                                break;
//                            }
//                        }
//                        if(!exist){
//                            videoTemplete.getVideoSegmentList().add(allVideoSegmentList.get(i));
//                            needAddVideoFragmentCount--;
//                        }
//                    }
//                }
        } else {
            for (int i = 0; i < videoTemplete.getVideo_segment().size(); i++) {
                videoTemplete.getVideoSegmentList().add(allVideoSegmentList.get(i));
            }
        }

        // (每个视频模板所需的视频片段数进行补充) --》 对每个视频模板所需的视频 时长 进行补充
        for (int i = 0; i < videoTemplete.getVideo_segment().size(); i++) {
            VideoSegment videoSegment = videoTemplete.getVideoSegmentList().get(i);
            //视频片段时长
            long videoLength = videoSegment.getEnd_time() - videoSegment.getStart_time();
            //视频文件时长
            long totalLength = videoSegment.getClose_time() - videoSegment.getCreate_time();
            //模板片段所需时长
            long templeteLength = (long) (Float.valueOf(videoTemplete.getVideo_segment().get(i).getVideo_segment_length()) * 1000);
            if (videoLength < templeteLength) {
                //视频片段时长不够  需要往两边扩充
                long needExpandLength = (long) ((templeteLength - videoLength) / 2.0f);
                if ((totalLength + needExpandLength * 2) <= templeteLength) {
                    videoSegment.setStart_time(videoSegment.getCreate_time());
                    videoSegment.setEnd_time(videoSegment.getClose_time());
                } else {
                    videoSegment.setStart_time(videoSegment.getStart_time() - needExpandLength);
                    videoSegment.setEnd_time(videoSegment.getEnd_time() + needExpandLength);
                    if (videoSegment.getStart_time() < videoSegment.getCreate_time()) {
                        //扩展碰到视频文件的左边界
                        long needAppendEndTime = videoSegment.getCreate_time() - videoSegment.getStart_time();
                        videoSegment.setStart_time(videoSegment.getCreate_time());
                        videoSegment.setEnd_time(videoSegment.getEnd_time() + needAppendEndTime);
                    }
                    if (videoSegment.getEnd_time() > videoSegment.getClose_time()) {
                        //扩展碰到视频文件的右边界
                        long needAppendStartTime = videoSegment.getEnd_time() - videoSegment.getClose_time();
                        videoSegment.setEnd_time(videoSegment.getClose_time());
                        videoSegment.setStart_time(videoSegment.getStart_time() - needAppendStartTime);
                    }
                }
            } else if (videoLength > templeteLength) {
                //视频片段时长过长  需要往两边裁剪
                long needClipLength = (long) ((videoLength - templeteLength) / 2.0f);
                videoSegment.setStart_time(videoSegment.getStart_time() + needClipLength);
                videoSegment.setEnd_time(videoSegment.getEnd_time() - needClipLength);
            }
        }

        //(模板所需的视频片段数 和 所需时长填充完毕) --》 对模板中的视频片段按开始时间进行排序
        Collections.sort(videoTemplete.getVideoSegmentList(), new Comparator<VideoSegment>() {
            public int compare(VideoSegment file, VideoSegment newFile) {
                if (file.getStart_time() < newFile.getStart_time()) {//升序
                    return -1;
                } else if (file.getStart_time() == newFile.getStart_time()) {
                    return 0;
                } else {
                    return 1;
                }
            }
        });

//        for(int i=0;i<videoTemplete.getVideoSegmentList().size();i++){
//            LoggerUtils.printLog(videoTemplete.getVideoSegmentList().get(i).toString()+"--shichang="+videoTemplete.getVideo_segment().get(i).getVideo_segment_length()+"totalLength = "+(videoTemplete.getVideoSegmentList().get(i).getClose_time()-videoTemplete.getVideoSegmentList().get(i).getCreate_time()));
//        }
        return videoTemplete;
    }

    /**
     * 计算打分项所占比值
     *
     * @param videoSegment
     * @param videoScoreType
     * @return
     */
    private static float calculateVideoScoreAspect(VideoSegment videoSegment, VideoScoreType videoScoreType) {
        long start = Math.max(videoSegment.getStart_time(), videoScoreType.getStart_time());
        long end = Math.min(videoSegment.getEnd_time(), videoScoreType.getEnd_time());
        return (end - start) * 1.0f / (videoSegment.getEnd_time() - videoSegment.getStart_time()) * 1.0f;
    }


    /**
     * 加分项是否落在当前视频片段的区间内
     *
     * @param videoSegment
     * @param videoSegmentTag
     * @return
     */
    private static boolean isAddScoreForCurrentVideoSegment(VideoSegment videoSegment, VideoScoreType videoSegmentTag) {
        return (videoSegmentTag.getStart_time() >= videoSegment.getStart_time() && videoSegmentTag.getStart_time() <= videoSegment.getEnd_time())
                || (videoSegmentTag.getEnd_time() >= videoSegment.getStart_time() && videoSegmentTag.getEnd_time() <= videoSegment.getEnd_time())
                || (videoSegment.getEnd_time() >= videoSegmentTag.getStart_time() && videoSegment.getEnd_time() <= videoSegmentTag.getEnd_time())
                || (videoSegment.getStart_time() >= videoSegmentTag.getStart_time() && videoSegment.getStart_time() <= videoSegmentTag.getEnd_time());
    }


    public enum MakeVideoType {
        TODAY_WONDERFUL, SIDE_KEY
    }
}
