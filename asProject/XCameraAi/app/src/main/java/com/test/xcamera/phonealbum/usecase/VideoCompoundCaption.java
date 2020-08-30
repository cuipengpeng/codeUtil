package com.test.xcamera.phonealbum.usecase;

import android.content.Context;
import android.graphics.PointF;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.editvideo.NvAsset;
import com.editvideo.NvAssetManager;
import com.editvideo.dataInfo.CompoundCaptionInfo;
import com.editvideo.dataInfo.TimelineData;
import com.google.gson.reflect.TypeToken;
import com.test.xcamera.application.AiCameraApplication;
import com.test.xcamera.phonealbum.bean.AssetItem;
import com.test.xcamera.phonealbum.bean.FontInfo;
import com.test.xcamera.phonealbum.widget.VideoEditManger;
import com.test.xcamera.utils.AppExecutors;
import com.test.xcamera.utils.ParseJsonFile;
import com.test.xcamera.utils.PathNameUtil;
import com.meicam.sdk.NvsAssetPackageManager;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsTimelineCompoundCaption;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class VideoCompoundCaption {
    public static final long COMPOUND_CAPTION_START_TIME = 0;
    public static final long COMPOUND_CAPTION_END_TIME = 4 * VideoEditManger.VIDEO_microsecond_TIME;
    private int mCaptionStyleType = NvAsset.ASSET_COMPOUND_CAPTION;
    private NvAssetManager mAssetManager;
    private VideoCompoundCallaBack mVideoCompoundCallaBack;
    private NvsStreamingContext mStreamingContext;


    public static VideoCompoundCaption getInstance() {
        return new VideoCompoundCaption();
    }

    public static VideoCompoundCaption getInstanceOther() {
        return new VideoCompoundCaption(1);
    }

    public interface VideoCompoundCallaBack {
        void OnCompoundListArrayList(ArrayList<AssetItem> list);
    }

    public VideoCompoundCaption(int type) {

    }

    public VideoCompoundCaption() {
        mAssetManager = NvAssetManager.sharedInstance();
        String bundlePath = "compoundcaption";
        mAssetManager.searchReservedAssets(mCaptionStyleType, bundlePath);
        mStreamingContext = NvsStreamingContext.getInstance();
        initCaptionFontInfoList();
    }

    public void getComCaptionList(VideoCompoundCallaBack mVideoCompoundCallaBack) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            ArrayList<AssetItem> list = getComCaptionList();
            AppExecutors.getInstance().mainThread().execute(new Runnable() {
                @Override
                public void run() {
                    if (mVideoCompoundCallaBack != null) {
                        mVideoCompoundCallaBack.OnCompoundListArrayList(list);
                    }
                }
            });
        });
    }

    private void initCaptionFontInfoList() {
        String fontJsonPath = "font/info.json";
        String fontJsonText = ParseJsonFile.readAssetJsonFile(AiCameraApplication.getContext(), fontJsonPath);
        if (TextUtils.isEmpty(fontJsonText)) {
            return;
        }
        ArrayList<FontInfo> fontInfoList = ParseJsonFile.fromJson(fontJsonText, new TypeToken<List<FontInfo>>() {
        }.getType());
        if (fontInfoList == null) {
            return;
        }
        int fontCount = fontInfoList.size();
        for (int idx = 0; idx < fontCount; idx++) {
            FontInfo fontInfo = fontInfoList.get(idx);
            if (fontInfo == null) {
                continue;
            }
            String fontAssetPath = "assets:/font/" + fontInfo.getFontFileName();
            mStreamingContext.registerFontByFilePath(fontAssetPath);
        }

        //Sd
//        String fontJsonPathSD = "storage/NvStreamingSdk/Asset/Font/info.json";
        String fontJsonPathSD = Environment.getExternalStorageDirectory() + "/NvStreamingSdk/Asset/Font/info.json";

        String fontJsonTextSD = ParseJsonFile.readSDJsonFile(AiCameraApplication.getContext(), fontJsonPathSD);
        if (TextUtils.isEmpty(fontJsonTextSD)) {
            return;
        }
        ArrayList<FontInfo> fontInfoListSD = ParseJsonFile.fromJson(fontJsonTextSD, new TypeToken<List<FontInfo>>() {
        }.getType());
        if (fontInfoListSD == null) {
            return;
        }
        int fontCountSD = fontInfoListSD.size();
        for (int idx = 0; idx < fontCountSD; idx++) {
            FontInfo fontInfo = fontInfoListSD.get(idx);
            if (fontInfo == null) {
                continue;
            }
            String fontAssetPathSD = Environment.getExternalStorageDirectory() + "/NvStreamingSdk/Asset/Font/" + fontInfo.getFontFileName();
            mStreamingContext.registerFontByFilePath(fontAssetPathSD);
        }
    }

    /**
     * 获取组合字幕列表
     *
     * @return
     */
    public ArrayList<AssetItem> getComCaptionList() {
        ArrayList<AssetItem> comList = new ArrayList<>();
        ArrayList<NvAsset> usableAsset = getAssetsDataList(mCaptionStyleType);
        String jsonBundlePath = "compoundcaption/info.json";
        ArrayList<ParseJsonFile.FxJsonFileInfo.JsonFileInfo> infoLists = ParseJsonFile.readBundleFxJsonFile(AiCameraApplication.getContext(), jsonBundlePath);
        if (infoLists != null) {
            for (ParseJsonFile.FxJsonFileInfo.JsonFileInfo jsonFileInfo : infoLists) {
                for (NvAsset asset : usableAsset) {
                    if (asset == null || TextUtils.isEmpty(asset.uuid)) {
                        continue;
                    }
                    //assets路径下的字幕样式包
                    if (asset.isReserved && asset.uuid.equals(jsonFileInfo.getFxPackageId())) {
                        if (isZh(AiCameraApplication.getContext())) {
                            asset.name = jsonFileInfo.getName_Zh();
                        } else {
                            asset.name = jsonFileInfo.getName();
                        }
                        //asset.name = jsonFileInfo.getName( );
                        asset.aspectRatio = Integer.parseInt(jsonFileInfo.getFitRatio());
                        StringBuilder coverPath = new StringBuilder("file:///android_asset/compoundcaption/");
                        coverPath.append(jsonFileInfo.getImageName());
                        asset.coverUrl = coverPath.toString();
                        asset.fxFileName = jsonFileInfo.getFxFileName();
                    }
                }
            }
        }
        int ratio = TimelineData.instance().getMakeRatio();
        for (NvAsset asset : usableAsset) {
            if (asset == null || TextUtils.isEmpty(asset.uuid)) {//|| (ratio & asset.aspectRatio) == 0) {
                //制作比例不适配，不加载
                continue;
            }
            if (asset.fxFileName == null) {
                asset.fxFileName = PathNameUtil.getPathNameWithSuffix(asset.localDirPath);
            }
            String assetPackageFilePath = asset.localDirPath;
            if (TextUtils.isEmpty(asset.localDirPath)) {
                assetPackageFilePath = "assets:/compoundcaption/" + asset.fxFileName;
            }
            StringBuilder packageUuid = new StringBuilder();
            boolean installSuccess = installCaptionPackage(assetPackageFilePath, packageUuid);
            if (!installSuccess) {
                continue;
            }
            asset.uuid = packageUuid.toString();
            AssetItem assetItem = new AssetItem();
            assetItem.setAsset(asset);
            assetItem.setAssetMode(AssetItem.ASSET_LOCAL);
            comList.add(assetItem);
        }
        return comList;

    }

    //    /**
//     * 获取组合字幕列表
//     * @return
//     */
//    public ArrayList<AssetItem> getComCaptionList(){
//         ArrayList<AssetItem> comList = new ArrayList<>();
//        ArrayList<NvAsset> usableAsset = getAssetsDataList(mCaptionStyleType);
////        ArrayList<NvAsset> usableAsset = new ArrayList<>();
//        String jsonBundlePath = "compoundcaption/info.json";
//        ArrayList<ParseJsonFile.FxJsonFileInfo.JsonFileInfo> infoLists = ParseJsonFile.readBundleFxJsonFile(AiCameraApplication.getContext(), jsonBundlePath);
//        if (infoLists != null) {
//            for (ParseJsonFile.FxJsonFileInfo.JsonFileInfo jsonFileInfo : infoLists) {
////                for (NvAsset asset : usableAsset) {
////                    if (asset == null || TextUtils.isEmpty(asset.uuid)) {
////                        continue;
////                    }
////                    //assets路径下的字幕样式包
////                    if (asset.isReserved && asset.uuid.equals(jsonFileInfo.getFxPackageId())) {
////                        if (isZh(AiCameraApplication.getContext())) {
////                            asset.name = jsonFileInfo.getName_Zh();
////                        } else {
////                            asset.name = jsonFileInfo.getName();
////                        }
////                        //asset.name = jsonFileInfo.getName( );
////                        asset.aspectRatio = Integer.parseInt(jsonFileInfo.getFitRatio());
////                        StringBuilder coverPath = new StringBuilder("file:///android_asset/compoundcaption/");
////                        coverPath.append(jsonFileInfo.getImageName());
////                        asset.coverUrl = coverPath.toString();
////                        asset.fxFileName = jsonFileInfo.getFxFileName();
////                    }
////                }
//                NvAsset asset=new NvAsset();
//                asset.uuid=jsonFileInfo.getFxPackageId();
//                if (isZh(AiCameraApplication.getContext())) {
//                    asset.name = jsonFileInfo.getName_Zh();
//                } else {
//                    asset.name = jsonFileInfo.getName();
//                }
//                //asset.name = jsonFileInfo.getName( );
//                asset.aspectRatio = Integer.parseInt(jsonFileInfo.getFitRatio());
//                StringBuilder coverPath = new StringBuilder("file:///android_asset/compoundcaption/");
//                coverPath.append(jsonFileInfo.getImageName());
//                asset.coverUrl = coverPath.toString();
//                asset.fxFileName = jsonFileInfo.getFxFileName();
//                usableAsset.add(asset);
//            }
//        }
//        int ratio = TimelineData.instance().getMakeRatio();
//        for (NvAsset asset : usableAsset) {
//            if (asset == null || TextUtils.isEmpty(asset.uuid) || (ratio & asset.aspectRatio) == 0) {
//                //制作比例不适配，不加载
//                continue;
//            }
//            if (asset.fxFileName == null) {
//                asset.fxFileName = PathNameUtil.getPathNameWithSuffix(asset.localDirPath);
//            }
//            String assetPackageFilePath = asset.localDirPath;
//            if (TextUtils.isEmpty(asset.localDirPath)) {
//                assetPackageFilePath = "assets:/compoundcaption/" + asset.fxFileName;
//            }
//            StringBuilder packageUuid = new StringBuilder();
//            boolean installSuccess = installCaptionPackage(assetPackageFilePath, packageUuid);
//            if (!installSuccess) {
//                continue;
//            }
//            asset.uuid = packageUuid.toString();
//            AssetItem assetItem = new AssetItem();
//            assetItem.setAsset(asset);
//            assetItem.setAssetMode(AssetItem.ASSET_LOCAL);
//            comList.add(assetItem);
//        }
//         return comList;
//
//    }
    //获取下载到手机路径下的素材，包括assets路径下自带的素材
    private ArrayList<NvAsset> getAssetsDataList(int assetType) {
        return mAssetManager.getUsableAssets(assetType, NvAsset.AspectRatio_All, 0);
    }

    public static boolean isZh(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        return language.endsWith("zh");
    }

    private boolean installCaptionPackage(String assetPackageFilePath, StringBuilder packageUuid) {
        int retResult = NvsStreamingContext.getInstance().getAssetPackageManager().installAssetPackage(assetPackageFilePath, null,
                NvsAssetPackageManager.ASSET_PACKAGE_TYPE_COMPOUND_CAPTION, true, packageUuid);
        if (retResult != NvsAssetPackageManager.ASSET_PACKAGE_MANAGER_ERROR_NO_ERROR && retResult != NvsAssetPackageManager.ASSET_PACKAGE_MANAGER_ERROR_ALREADY_INSTALLED) {
            Log.e("", "failed to install package = " + assetPackageFilePath);
            return false;
        }
        return true;
    }

    public float getCurCaptionZVal(NvsTimeline timeline) {
        float zVal = 0.0f;
        NvsTimelineCompoundCaption caption = timeline.getFirstCompoundCaption();
        while (caption != null) {
            float tmpZVal = caption.getZValue();
            if (tmpZVal > zVal)
                zVal = tmpZVal;
            caption = timeline.getNextCaption(caption);
        }
        zVal += 1.0;
        return zVal;
    }

    //保存组合字幕数据
    public static CompoundCaptionInfo saveCompoundCaptionData(NvsTimelineCompoundCaption caption) {
        if (caption == null)
            return null;
        CompoundCaptionInfo captionInfo = new CompoundCaptionInfo();
        long inPoint = caption.getInPoint();
        captionInfo.setInPoint(inPoint);
        long outPoint = caption.getOutPoint();
        captionInfo.setOutPoint(outPoint);
        int captionCount = caption.getCaptionCount();
        for (int idx = 0; idx < captionCount; idx++) {
            captionInfo.addCaptionAttributeList(new CompoundCaptionInfo.CompoundCaptionAttr());
        }

        captionInfo.setCaptionZVal((int) caption.getZValue());
        captionInfo.setAnchor(caption.getAnchorPoint());
        PointF pointF = caption.getCaptionTranslation();
        captionInfo.setTranslation(pointF);
        return captionInfo;
    }

    /**
     * 删除时间区间内的组合字幕
     *
     * @param timeline
     * @param start
     * @param end
     */
    public void removeTimeCompoundCaption(NvsTimeline timeline, long start, long end) {
        List<CompoundCaptionInfo> mediaData = TimelineData.instance().cloneCompoundCaptionData();
        if (mediaData == null) {
            return;
        }
        for (CompoundCaptionInfo info : mediaData) {
            if ((start <= info.getInPoint() && info.getInPoint() <= end) || (start <= info.getOutPoint() && info.getOutPoint() <= end)) {
                NvsTimelineCompoundCaption compoundCaption = VideoEditManger.getNvsNvsTimelineCompoundCaption(timeline, info);
                removeCurCompoundCaption(timeline, compoundCaption);
            }

        }
        List<CompoundCaptionInfo> mediaData1 = TimelineData.instance().getCompoundCaptionArray();
        int size = mediaData1.size();

    }

    /**
     * 删除全部
     *
     * @param timeline
     */
    public void removeAllCompoundCaption(NvsTimeline timeline) {
        List<CompoundCaptionInfo> mediaData = TimelineData.instance().cloneCompoundCaptionData();
        if (mediaData == null) {
            return;
        }
        for (CompoundCaptionInfo info : mediaData) {
            NvsTimelineCompoundCaption compoundCaption = VideoEditManger.getNvsNvsTimelineCompoundCaption(timeline, info);
            removeCurCompoundCaption(timeline, compoundCaption);
        }
        TimelineData.instance().getCompoundCaptionArray().clear();
    }

    public void removeCurCompoundCaption(NvsTimeline timeline, NvsTimelineCompoundCaption compoundCaption) {

        if (compoundCaption == null) {
            return;
        }
        int index = getCaptionIndex((int) compoundCaption.getZValue());
        if (index >= 0) {
            if (compoundCaption != null) {
                VideoEditManger.delCompoundCaption(timeline, compoundCaption);
            }
            TimelineData.instance().getCompoundCaptionArray().remove(index);
        }
    }

    /**
     * 防止组合字幕同一时间点部分不能清除
     * @param timeline
     * @param time
     */
    public void removeTimeAllCompoundCaption(NvsTimeline timeline, long time) {
        List<NvsTimelineCompoundCaption> comList= timeline.getCompoundCaptionsByTimelinePosition(time);
        if(comList==null){
            return;
        }
        Iterator item = comList.iterator();
        while (item.hasNext()) {
            NvsTimelineCompoundCaption model = (NvsTimelineCompoundCaption) item.next();
            if(model!=null){
                timeline.removeCompoundCaption(model);
            }
        }

    }

    public int getCaptionIndex(int curZValue) {
        int index = -1;
        int count = TimelineData.instance().getCompoundCaptionArray().size();
        for (int i = 0; i < count; ++i) {
            int zVal = TimelineData.instance().getCompoundCaptionArray().get(i).getCaptionZVal();
            if (curZValue == zVal) {
                index = i;
                break;
            }
        }
        return index;
    }
}
