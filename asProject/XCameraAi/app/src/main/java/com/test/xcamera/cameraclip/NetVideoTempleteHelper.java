package com.test.xcamera.cameraclip;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.test.xcamera.api.http.HttpRequest;
import com.test.xcamera.application.AiCameraApplication;
import com.test.xcamera.bean.VideoTemplete;
import com.test.xcamera.bean.VideoTempleteIdBean2;
import com.test.xcamera.cameraclip.bean.ThemeTypeTimeLengthBean;
import com.test.xcamera.cameraclip.bean.VideoSegment;
import com.test.xcamera.constants.LogcatConstants;
import com.test.xcamera.util.TextReader;
import com.test.xcamera.utils.Constants;
import com.test.xcamera.utils.DownloadWebFileUtil;
import com.test.xcamera.utils.Md5Util;
import com.test.xcamera.utils.SPUtils;
import com.test.xcamera.utils.StringUtil;
import com.test.xcamera.utils.ZipFileUtil;
import com.moxiang.common.logging.Logcat;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetVideoTempleteHelper {

    public static final String TEMPLETE_FILE_SUFFIX_MP3 = ".mp3";
    public static final String TEMPLETE_FILE_SUFFIX_MP4 = ".mp4";
    public static final String TEMPLETE_FILE_SUFFIX_IMG01 = ".jpg";
    public static final String TEMPLETE_FILE_SUFFIX_IMG02 = ".png";
    public static final String TEMPLETE_FILE_SUFFIX_IMG03 = ".jpeg";
    public static final String TEMPLETE_FILE_SUFFIX_JSON = ".json";
    public static final String  SINGLE_PERSON = "0";
    public static final String  DOUBLE_PERSON= "1";
    public static final String MULTI_PERSON = "2";
    public static final String  PET= "3";
    public static final String  PERSON_AND_PET= "4";
    public List<VideoTemplete> mNetVideoTempleteList = new ArrayList<>();
    private Context mContext;

    public void getNetVideoTemplete(Context context, OneKeyMakeVideoHelper oneKeyMakeVideoHelper, NetTempleteCallback netTempleteCallback) {
        this.mContext = context;
        List<ThemeTypeTimeLengthBean> themeTypeTimeLengthList = oneKeyMakeVideoHelper.mThemeTypeTimeLengthList;
        List<Integer> sceneClassTimeLengthList = oneKeyMakeVideoHelper.mSceneClassTimeLengthList;
        for(int i=0; i<oneKeyMakeVideoHelper.mThemeTypeTimeLengthList.size(); i++){
            VideoSegment.ThemeType themeType = oneKeyMakeVideoHelper.mThemeTypeTimeLengthList.get(i).getThemeType();
            if(VideoSegment.ThemeType.NONE==themeType){
                oneKeyMakeVideoHelper.mThemeTypeTimeLengthList.remove(i);
                break;
            }
        }
        if(oneKeyMakeVideoHelper.mThemeTypeTimeLengthList.size()>3){
            themeTypeTimeLengthList=oneKeyMakeVideoHelper.mThemeTypeTimeLengthList.subList(0,3);
        }
        if(oneKeyMakeVideoHelper.mSceneClassTimeLengthList.size()>3){
            sceneClassTimeLengthList=oneKeyMakeVideoHelper.mSceneClassTimeLengthList.subList(0,3);
        }

        StringBuilder themeTypeStr = new StringBuilder();
        StringBuilder sceneTypeStr = new StringBuilder();
        for(int i=0; i<themeTypeTimeLengthList.size(); i++){
            VideoSegment.ThemeType themeType = themeTypeTimeLengthList.get(i).getThemeType();
            if(VideoSegment.ThemeType.SINGLE_PERSON==themeType){
                themeTypeStr.append(SINGLE_PERSON+",");
            }else if(VideoSegment.ThemeType.DOUBLE_PERSON==themeType){
                   themeTypeStr.append(DOUBLE_PERSON+",");
            }else if(VideoSegment.ThemeType.MULTI_PERSON==themeType){
                themeTypeStr.append(MULTI_PERSON+",");
            }else if(VideoSegment.ThemeType.PET==themeType){
                themeTypeStr.append(PET+",");
            }else if(VideoSegment.ThemeType.PERSON_AND_PET==themeType){
                themeTypeStr.append(PERSON_AND_PET+",");
            }
        }
        for(int i=0; i<sceneClassTimeLengthList.size(); i++){
            sceneTypeStr.append(sceneClassTimeLengthList.get(i)+",");
        }
        Map<String, String> paramsMap = new HashMap<>();
        if(StringUtil.notEmpty(themeTypeStr.toString())){
            paramsMap.put("major",themeTypeStr.substring(0, themeTypeStr.length()-1));
            paramsMap.put("majorTopN","6");
            Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("get net template param major  "+themeTypeStr.substring(0, themeTypeStr.length()-1) ).out();
        }
//        else {
//            paramsMap.put("major", "0,1,2,4");
//            paramsMap.put("majorTopN","3");
//        }
        if(StringUtil.notEmpty(sceneTypeStr.toString())){
            paramsMap.put("scene", sceneTypeStr.substring(0, sceneTypeStr.length()-1));
            Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("get net template param scene  "+sceneTypeStr.substring(0, sceneTypeStr.length()-1)).out();
        }
        paramsMap.put("sceneTopN","6");
        paramsMap.put("totalDuration", ((int)OneKeyMakeVideoXmlParser.TOTAL_DURATION/1000)+"");
        paramsMap.put("partDuration", ((int)OneKeyMakeVideoXmlParser.MIN_FILE_DURATION/1000)+"");
        Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("get net template param total duration  "+((int)OneKeyMakeVideoXmlParser.TOTAL_DURATION/1000)+" part duration "+((int)OneKeyMakeVideoXmlParser.MIN_FILE_DURATION/1000)).out();
        HttpRequest.postSubUrl(HttpRequest.RequestType.GET, HttpRequest.MATCH_TEMPLETE, paramsMap, new HttpRequest.HttpResponseCallBack() {
            @Override
            public void onSuccess(String response) {
                Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("get net template json  "+response).out();
                VideoTempleteIdBean2 videoTempleteIdBean = new Gson().fromJson(response, VideoTempleteIdBean2.class);
                List<Integer> videoTempleteIdList = new ArrayList<>();

                VideoTempleteIdBean2.ShortBean shortBean = videoTempleteIdBean.getShortX();
                dealTempleteId(videoTempleteIdList, shortBean, oneKeyMakeVideoHelper);
                int shortTempleteIdCount = videoTempleteIdList.size();
                VideoTempleteIdBean2.ShortBean longBean = videoTempleteIdBean.getLongX();
                dealTempleteId(videoTempleteIdList, longBean, oneKeyMakeVideoHelper);

                if(videoTempleteIdList.size()==0){
                    loadLocalVideoTemplete(netTempleteCallback);
//                    netTempleteCallback.onFail(mContext.getResources().getString(R.string.cannotMatchNetTempleteId));
                    return;
                }
                StringBuilder templeteIdStr = new StringBuilder();
                for(int i=0; i<videoTempleteIdList.size();i++){
                    templeteIdStr.append(videoTempleteIdList.get(i)+",");
                }
                String templeteDetailUrl = HttpRequest.GET_TEMPLETE_DETAIL+templeteIdStr.substring(0, templeteIdStr.length()-1);
                HttpRequest.postSubUrl(HttpRequest.RequestType.GET, templeteDetailUrl, new HashMap<>(), new HttpRequest.HttpResponseCallBack() {
                    @Override
                    public void onSuccess(String response) {
                        Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("get net template detail  json  "+response).out();
                        mNetVideoTempleteList = new Gson().fromJson(response, new TypeToken<List<VideoTemplete>>() {}.getType());
                        //对返回回来的bean按照请求的顺序排序
                        Map<Integer, VideoTemplete> templeteIdHashMap =new HashMap<>();
                        for(VideoTemplete videoTemplete: mNetVideoTempleteList){
                            templeteIdHashMap.put(videoTemplete.getId(), videoTemplete);
                        }
                        List<VideoTemplete> templeteList = new ArrayList<>();
                        for(int i=0; i<videoTempleteIdList.size();i++){
                            templeteList.add(templeteIdHashMap.get(videoTempleteIdList.get(i)));
                        }

                        mNetVideoTempleteList = templeteList;
                        for(int i=0;i<shortTempleteIdCount;i++){
                            mNetVideoTempleteList.get(i).setShortTemplete(true);
                        }
                        VideoTemplete videoTemplete;
                        if (shortTempleteIdCount > 0) {
                             videoTemplete = mNetVideoTempleteList.get(0);
                             downloadTempleteZipFile(context, videoTemplete,true, netTempleteCallback);
                        }else {
                            loadLocalVideoTemplete(netTempleteCallback);
                        }
                    }

                    @Override
                    public void onFailure(String response) {
                        Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("get net template detail fail  "+response).out();
                        loadLocalVideoTemplete(netTempleteCallback);
//                        netTempleteCallback.onFail(response);
                    }
                });
            }

            @Override
            public void onFailure(String response) {
                Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("get net template fail  "+response).out();
                loadLocalVideoTemplete(netTempleteCallback);
//                netTempleteCallback.onFail(response);
            }
        });
    }

    public void loadLocalVideoTemplete(NetTempleteCallback netTempleteCallback) {
        VideoTemplete videoTemplete=null;
        List<VideoTemplete> videoTempleteList = OneKeyMakeVideoHelper.convertTempleteJsonToBean(AiCameraApplication.mApplication);
        for(int i=0;i<videoTempleteList.size();i++){
            if(videoTempleteList.get(i).isShortTemplete()){
                videoTemplete=videoTempleteList.get(i);
                break;
            }
        }
        if(videoTemplete==null && videoTempleteList.size()>0){
            videoTemplete = videoTempleteList.get(0);
        }
        if(videoTemplete!=null){
            netTempleteCallback.onSuccess(videoTemplete);
        }
    }

    private void dealTempleteId(List<Integer> videoTempleteIdList, VideoTempleteIdBean2.ShortBean shortBean, OneKeyMakeVideoHelper oneKeyMakeVideoHelper) {
        //遍历主体
        for (int i =0; i<shortBean.getMajor().size();i++){
            List<Integer> majorTemplateIdList = shortBean.getMajor().get(i).getTemplateIds();
            for(int m=0; m<majorTemplateIdList.size(); m++){
                if(!videoTempleteIdList.contains(majorTemplateIdList.get(m))){
                    videoTempleteIdList.add(majorTemplateIdList.get(m));
                }
            }
        }

        //遍历场景
        for (int i =0; i<shortBean.getScene().size();i++){
            List<Integer> sceneTemplateIdList = shortBean.getScene().get(i).getTemplateIds();
            for(int m=0; m<sceneTemplateIdList.size(); m++){
                if(!videoTempleteIdList.contains(sceneTemplateIdList.get(m))){
                    videoTempleteIdList.add(sceneTemplateIdList.get(m));
                }
            }
        }
    }

    public void downloadTempleteZipFile(Context context, VideoTemplete videoTemplete, boolean loadLocalTemplete, NetTempleteCallback netTempleteCallback) {
         Object cacheObject = SPUtils.readObject(context,getTempleteZipCacheKey(videoTemplete.getPackageFileId()+""), new VideoTemplete());
        if(cacheObject instanceof  VideoTemplete){
            VideoTemplete cacheVideoTemplete = (VideoTemplete)cacheObject;
            File cacheZipFile = new File(cacheVideoTemplete.getLocalZipFilePath());
            if(cacheZipFile.exists()){
                String savedZipDir = cacheZipFile.getParent();
                ZipFileUtil.unZipFiles(cacheZipFile, cacheZipFile.getParent());
                File[] templeteFileArr= new File(savedZipDir).listFiles();
                File[] tmpFile = new File(savedZipDir).listFiles();
                Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("load cache net templete       timestamp="+System.currentTimeMillis()+"       templeteId="+cacheVideoTemplete.getPackageFileId()).out();
                netTempleteCallback.onSuccess(cacheVideoTemplete);
                return;
            }
        }

        String zipUrl = Constants.getFileIdToUrl(videoTemplete.getPackageFileId() + "");
        String fileName = System.currentTimeMillis() + ".zip";
        String savedZipDir = context.getExternalCacheDir().getAbsolutePath()+"/"+getTempleteZipCacheKey(videoTemplete.getPackageFileId()+"");
//                                        Toast.makeText(TodayVideoListActivity.this, savedZipDir, Toast.LENGTH_LONG).show();

        Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("begin download net templete zip file     timestamp="+System.currentTimeMillis()+"      zipUrl="+zipUrl).out();
        DownloadWebFileUtil.getInstance().downloadBigFile(zipUrl, savedZipDir, fileName, new DownloadWebFileUtil.DownloadProgressCallback() {

            @Override
            public void onStart() {
            }

            @Override
            public void onProgress(int progress) {
                netTempleteCallback.onProgress((int) (progress*DownloadVideoTempleteDataUtil.NET_TEMPLETE_PERCENT));
            }

            @Override
            public void onFinish(File path) {
                ZipFileUtil.unZipFiles(path,savedZipDir);
                File[] templeteFileArr= new File(savedZipDir).listFiles();
                File[] tmpFile = new File(savedZipDir).listFiles();
                String zipFileDir = savedZipDir;
                for (int i=0; i<tmpFile.length;i++){
                    if(!tmpFile[i].getName().startsWith("_") && tmpFile[i].isDirectory()){
                        templeteFileArr = tmpFile[i].listFiles();
                        zipFileDir = savedZipDir+"/"+tmpFile[i].getName();
                    }
                }
                VideoTemplete netVideoTemplete = new VideoTemplete();
                String mp3Path = null;
                String mp4Path = null;
                String imgPath = null;
                for (int i=0; i<templeteFileArr.length;i++){
                    String templeteFileName = templeteFileArr[i].getName();
                    String fullPath = zipFileDir+"/"+templeteFileName;
                    if(templeteFileName.endsWith(NetVideoTempleteHelper.TEMPLETE_FILE_SUFFIX_JSON)){
                        try {
                            String jsonStr = TextReader.getFileContent(new FileInputStream(templeteFileArr[i]));
                            netVideoTemplete = new Gson().fromJson(jsonStr, VideoTemplete.class);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                if(netVideoTemplete.getVideo_segment()==null || netVideoTemplete.getVideo_segment().size()==0){
                    loadLocalVideoTemplete(netTempleteCallback);
//                    netTempleteCallback.onFail(mContext.getResources().getString(R.string.templeteJsonException));
                    return;
                }
                for (int i=0; i<templeteFileArr.length;i++){
                    String templeteFileName = templeteFileArr[i].getName();
                    String fullPath = zipFileDir+"/"+templeteFileName;
                    if(templeteFileName.endsWith(NetVideoTempleteHelper.TEMPLETE_FILE_SUFFIX_MP3) ||templeteFileName.equalsIgnoreCase(netVideoTemplete.getBgm().getAudio_url())){
                        mp3Path = fullPath;
                    }
                }

                netVideoTemplete.setNetTemplete(true);
                netVideoTemplete.setLocalZipFilePath(path.getAbsolutePath());
                netVideoTemplete.setId(videoTemplete.getId());
                netVideoTemplete.setCoverFileId(videoTemplete.getCoverFileId());
                netVideoTemplete.setVideoFileId(videoTemplete.getVideoFileId());
                netVideoTemplete.setDescription(videoTemplete.getDescription());
//                netVideoTemplete.setLocalSampleVideoPath(mp4Path);
//                netVideoTemplete.setIcon(imgPath);
                netVideoTemplete.getBgm().setAudio_url(mp3Path);
                SPUtils.writeObject(context, getTempleteZipCacheKey(videoTemplete.getPackageFileId()+""), netVideoTemplete);

                netTempleteCallback.onSuccess(netVideoTemplete);
            }

            @Override
            public void onFail(String errorInfo) {
                if(loadLocalTemplete){
                    loadLocalVideoTemplete(netTempleteCallback);
                }else {
                    netTempleteCallback.onFail(errorInfo);
                }

            }
        });
    }

    public static String getTempleteZipCacheKey(String templeteZipId){
        String zipUrl = Constants.getFileIdToUrl(templeteZipId);
        return Md5Util.getMD5(zipUrl);
    }

    public interface NetTempleteCallback{
        void onSuccess(VideoTemplete videoTemplete);
        void onProgress(int progress);
        void onFail(String error);
    }
}
