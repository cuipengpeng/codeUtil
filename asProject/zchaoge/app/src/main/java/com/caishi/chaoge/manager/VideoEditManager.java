package com.caishi.chaoge.manager;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.ProgressBar;

import com.caishi.chaoge.base.BaseActivity;
import com.caishi.chaoge.base.BaseRequestInterface;
import com.caishi.chaoge.bean.AEModuleBean.AETemplateInfo;
import com.caishi.chaoge.bean.AEModuleBean.AETextLayerInfo;
import com.caishi.chaoge.bean.EventBusBean.AudioVolumeBean;
import com.caishi.chaoge.bean.EventBusBean.IssueBean;
import com.caishi.chaoge.bean.LrcBean;
import com.caishi.chaoge.bean.ModuleMaterialBean;
import com.caishi.chaoge.http.RequestURL;
import com.caishi.chaoge.listener.OnUploadFileListener;
import com.caishi.chaoge.rd.overturn.CustomDrawTextHandler;
import com.caishi.chaoge.rd.overturn.MyDrawTextHandler;
import com.caishi.chaoge.request.PublishRequest;
import com.caishi.chaoge.utils.AEModuleUtils;
import com.caishi.chaoge.utils.FileUtils;
import com.caishi.chaoge.utils.LogUtil;
import com.caishi.chaoge.utils.SPUtils;
import com.caishi.chaoge.utils.ToastUtils;
import com.caishi.chaoge.utils.Utils;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import com.rd.lib.utils.ThreadPoolUtils;
import com.rd.vecore.Music;
import com.rd.vecore.VirtualVideo;
import com.rd.vecore.VirtualVideoView;
import com.rd.vecore.exception.InvalidArgumentException;
import com.rd.vecore.exception.InvalidStateException;
import com.rd.vecore.listener.ExportListener;
import com.rd.vecore.models.AECustomTextInfo;
import com.rd.vecore.models.AEFragmentInfo;
import com.rd.vecore.models.AspectRatioFitMode;
import com.rd.vecore.models.CanvasObject;
import com.rd.vecore.models.CustomDrawObject;
import com.rd.vecore.models.MediaObject;
import com.rd.vecore.models.Scene;
import com.rd.vecore.models.VideoConfig;
import com.rd.vecore.models.caption.CaptionObject;
import com.rd.vecore.utils.AEFragmentUtils;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static com.caishi.chaoge.http.RequestURL.VIDEO_BASE_URL;
import static com.rd.vecore.models.MusicFilterType.MUSIC_FILTER_NORMAL;

public class VideoEditManager {
    String TAG = "VideoEditManager";
    private static VideoEditManager videoEditManager;
    public float duration;
    private ModuleMaterialBean moduleMaterialBean;
    private int editFlag;//2.0版本编辑 和 3.0版本编辑    0 2.0   1 3.0
    private RxAppCompatActivity context;
    private VirtualVideoView virtualVideoView;
    //    private CustomCircleProgressBar cpb_progress;
    private MediaObject mediaObject;
    private Music bgMusic;
    private Music personMusic;
    private ArrayList<CaptionObject> captionObjectList = new ArrayList<>();
    //    private CaptionObject editingCaption;
    private RectF textRectF = new RectF(0.01f, 0.01f, 0.99f, 0.99f);
    private UploadManager uploadManager;
    private String fileOssPath;//上传阿里云的视频地址
    private String firstImgPath;//上传阿里云的图片地址
    private String centreImgPath;//上传阿里云的图片地址
    private String filePath;//保存视频的地址
    public RectF showRectF = null;
    public float scale = -1;
    public int captionPosition = -1;
    private AudioVolumeBean audioVolumeBean;
    public String firstSnapshot;//第一帧
    private String centreSnapshot;//中间帧
    public boolean isSave = false;
    private ProgressBar progressBar;
    private OnPublishListener onPublishListener;
    public MyDrawTextHandler myDrawTextHandler;

    public static VideoEditManager newInstance() {
        if (videoEditManager == null)
            videoEditManager = new VideoEditManager();
        return videoEditManager;
    }

    /**
     * @param context            上下文
     * @param virtualVideoView   预览的VideoView
     * @param moduleMaterialBean 素材的实体类
     * @param editFlag           2.0版本编辑 和 3.0版本编辑    0 2.0   1 3.0
     */
    public void init(BaseActivity context, final VirtualVideoView virtualVideoView, ModuleMaterialBean moduleMaterialBean, int editFlag) {
        this.moduleMaterialBean = moduleMaterialBean;
        this.editFlag = editFlag;
        this.context = context;
        this.virtualVideoView = virtualVideoView;
        if (audioVolumeBean == null)
            audioVolumeBean = new AudioVolumeBean();
        addVirtualVideo(true);
    }


    /**
     * @param isAdd 是否预览 区分预览与保存视频
     * @return VirtualVideo 虚拟视频
     */
    private VirtualVideo addVirtualVideo(boolean isAdd) {
        LogUtil.d("isAdd====" + isAdd);
        if (moduleMaterialBean == null)
            return null;
        //构造虚拟视频
        final VirtualVideo mVirtualVideo = new VirtualVideo();
        //构造一个场景
        Scene scene = VirtualVideo.createScene();
        duration = moduleMaterialBean.duration;
        try {
            if (!Utils.isEmpty(moduleMaterialBean.bgPath)) {
                //给场景添加媒体(传媒体路径)并返回媒体对象  添加背景图片
                mediaObject = scene.addMedia(moduleMaterialBean.bgPath);

                if (moduleMaterialBean.bgPath.contains(".mp4")) {
                    mediaObject.setTimelineRange(0, duration);
                    mediaObject.enableRepeat(true);
                    mediaObject.setMixFactor(audioVolumeBean.videoBgVolume);
                } else {
                    mediaObject.setMixFactor(0);
                    mediaObject.setIntrinsicDuration(duration);
                }
                //将场景添加到虚拟视频
                mVirtualVideo.addScene(scene);
            }
            mVirtualVideo.clearMusic();
            if (!Utils.isEmpty(moduleMaterialBean.bgMusicPath)) {
                bgMusic = VirtualVideo.createMusic(moduleMaterialBean.bgMusicPath);
                bgMusic.setFadeInOut(0, 0);//音量淡入淡出时间(单位秒)
                bgMusic.setMixFactor(audioVolumeBean.bgVolume); //音量占用比例，音量占用比例(正常范围 0-100,超过 100 代表音量增益，
                // 过大会破音,比如设置 20 代表原音量的 20%，500 则为原音量增益 5 倍)
                bgMusic.setMusicFilter(MUSIC_FILTER_NORMAL);//音效， MUSIC_FILTER_NORMAL 无效果
                bgMusic.setTimelineRange(0, duration);
                mVirtualVideo.addMusic(bgMusic);//添加背景音乐
            }
            if (!Utils.isEmpty(moduleMaterialBean.personMusicPath)) {
                personMusic = VirtualVideo.createMusic(moduleMaterialBean.personMusicPath);
                personMusic.setFadeInOut(0, 0);//音量淡入淡出时间(单位秒)
                personMusic.setMixFactor(audioVolumeBean.personVolume); //音量占用比例，音量占用比例(正常范围 0-100,超过 100 代表音量增益，
                // 过大会破音,比如设置 20 代表原音量的 20%，500 则为原音量增益 5 倍)
                personMusic.setMusicFilter(MUSIC_FILTER_NORMAL);//音效， MUSIC_FILTER_NORMAL 无效果
                personMusic.setTimelineRange(0, duration);
                mVirtualVideo.addMusic(personMusic);//添加背景音乐
            }
            List<LrcBean> lrcList = moduleMaterialBean.lrcList;
            if (null != lrcList && lrcList.size() > 0) {
                switch (moduleMaterialBean.specialFlag) {
                    case 0://自由翻转
                        aEFragment(lrcList, mVirtualVideo, 0);
//                        final CustomDrawTextHandler customDrawTextHandler = new CustomDrawTextHandler(moduleMaterialBean);
//                        mVirtualVideo.addCustomDraw(new CustomDrawObject(duration) {
//                            @Override
//                            public CustomDrawObject clone() {
//                                return null;
//                            }
//
//                            @Override
//                            public void draw(CanvasObject canvas, float progress) {
//                                float ps = progress * duration;
////                                Log.d("CHAOGELOG", "currentProgress=========" + ps);
//                                customDrawTextHandler.drawText(canvas, ps);
//                            }
//                        });
                        break;

                    case 1://口吐文字
                        List<LrcBean> lrcBeanList = new ArrayList<>();
                        lrcBeanList.clear();
                        lrcBeanList.addAll(moduleMaterialBean.lrcList);
                        List<LrcBean> newLrcBeanList = new ArrayList<>();
                        List<String> colorList = new ArrayList<>();
                        for(int i=0; i<lrcBeanList.size(); i++){
                            //每一行的开始时间
                            long startTimePerLine = lrcBeanList.get(i).getStart();
                            //每一行每个字的时间  毫秒
                            long timePerWord = (lrcBeanList.get(i).getEnd()-lrcBeanList.get(i).getStart())/lrcBeanList.get(i).getLrc().length(); //毫秒

                            List<Term> termList = HanLP.segment(lrcBeanList.get(i).getLrc());
                            List<String> wordList = new ArrayList<>();
                            for(int k=0; k<termList.size();k++){
                                wordList.add(termList.get(k).word);
                            }

                            int wordCount = 0;
                            for(int j=0; j<wordList.size(); j++){
                                if(j==0){
                                    newLrcBeanList.add(new LrcBean(wordList.get(j), startTimePerLine, wordList.get(j).length()*timePerWord+startTimePerLine));
                                    wordCount+=wordList.get(j).length();
                                }else {
                                    newLrcBeanList.add(new LrcBean(wordList.get(j), wordCount*timePerWord+startTimePerLine, (wordList.get(j).length()+wordCount)*timePerWord+startTimePerLine));
                                    wordCount+=wordList.get(j).length();
                                }
//                                colorList.add(moduleMaterialBean.colorList.get(i));
                            }
                        }

                        for(int i=0; i<newLrcBeanList.size();i++){
                            LogUtil.printLog(newLrcBeanList.get(i).getLrc()+"--"+newLrcBeanList.get(i).getStart()+"--"+newLrcBeanList.get(i).getEnd());
                        }
                        if(myDrawTextHandler == null){
                            myDrawTextHandler = new MyDrawTextHandler(context, newLrcBeanList, moduleMaterialBean.fontFilePath);
                        }else {
                            myDrawTextHandler.setLrcBeanList(newLrcBeanList, moduleMaterialBean.fontFilePath);
                        }
                        myDrawTextHandler.setColorList(colorList);
                        mVirtualVideo.addCustomDraw(new CustomDrawObject(duration) {
                            @Override
                            public CustomDrawObject clone() {
                                return null;
                            }

                            @Override
                            public void draw(CanvasObject canvas, float progress) {
                                float ps = progress * duration;
                                Log.d("CHAOGELOG", "currentProgress=========" + ps);
                                if (null != myDrawTextHandler) {

                                    ps = ps*1000; //毫秒
                                    myDrawTextHandler.drawText(canvas, ps);
                                }
                            }
                        });
                        break;
                    case 2://多行横排
//                        multiSpecial(lrcList, mVirtualVideo);
                        aEFragment(lrcList, mVirtualVideo, 1);
                        break;
                    case 3://多行竖排
                        aEFragment(lrcList, mVirtualVideo, 2);
                        break;
                    case 4://单行横排
                        singleSpecial(mVirtualVideo, 3);
                        break;
                    case 5://单行竖排
                        singleSpecial(mVirtualVideo, 4);
                        break;
                }
            }
            virtualVideoView.setAspectRatioFitMode(AspectRatioFitMode.KEEP_ASPECTRATIO_EXPANDING);
            if (isAdd) {
                virtualVideoView.getWordLayout().removeAllViews();
                mVirtualVideo.build(virtualVideoView);//将虚拟视频添加到播放器
            }
        } catch (InvalidStateException e) {
            e.printStackTrace();
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mVirtualVideo;
    }

    /**
     * 更新样式
     */
    public void updateVirtualVideo() {
        addVirtualVideo(true);
    }

    /**
     * 更新样式
     */
    public void updateVirtualVideo(VirtualVideoView virtualVideoView) {
        this.virtualVideoView = virtualVideoView;
        addVirtualVideo(true);
    }

    /**
     * 更新样式
     *
     * @param moduleMaterialBean 视频内容的实体
     */
    public void updateVirtualVideo(ModuleMaterialBean moduleMaterialBean) {
        this.moduleMaterialBean = moduleMaterialBean;
        addVirtualVideo(true);
    }

    /**
     * 获取音量
     *
     * @return 音量的对线
     */
    public AudioVolumeBean getVolume() {
        if (null == audioVolumeBean)
            audioVolumeBean = new AudioVolumeBean();
        audioVolumeBean.videoBgVolume = null != mediaObject ? mediaObject.getMixFactor() : editFlag == 0 ? 50 : 0;
        audioVolumeBean.bgVolume = null != bgMusic ? bgMusic.getMixFactor() : editFlag == 0 ? 50 : 0;
        audioVolumeBean.personVolume = null != personMusic ? personMusic.getMixFactor() : editFlag == 0 ? 150 : 0;
        return audioVolumeBean;
    }

    /**
     * 设置音量
     *
     * @param audioVolumeBean 音量对象
     */
    public void setVolume(AudioVolumeBean audioVolumeBean) {
        if (mediaObject != null) {
            mediaObject.setMixFactor(audioVolumeBean.videoBgVolume);
        }
        if (bgMusic != null)
            bgMusic.setMixFactor(audioVolumeBean.bgVolume);
        if (personMusic != null)
            personMusic.setMixFactor(audioVolumeBean.personVolume);
    }


    /**
     * 获取视频时长
     *
     * @return 时长
     */
    public float getDuration() {
        return duration;
    }

    /**
     * 生成封面图
     *
     * @param mVirtualVideo 虚拟视频
     * @param time          时间（S）
     * @return 图片保存的地址
     */
    public String getSnapshot(VirtualVideo mVirtualVideo, float time) {

        Bitmap bitmap = Bitmap.createBitmap(1088, 1920,
                Bitmap.Config.ARGB_8888);
        boolean snapshot = mVirtualVideo.getSnapshot(context, time, bitmap, true);
        if (snapshot) {
            return FileUtils.saveBitmap(bitmap);
        } else
            return "";
    }


    /**
     * 获取视频数据对象
     *
     * @return 数据对象
     */
    public ModuleMaterialBean getModuleMaterialBean() {
        return moduleMaterialBean;
    }

    /**
     * 获取字幕行数
     *
     * @return 行数
     */
    public int getCaptionNum() {
        if (moduleMaterialBean != null)
            return moduleMaterialBean.colorList.size();
        else
            return 0;
    }


    private void aEFragment(List<LrcBean> lrcList, VirtualVideo mVirtualVideo, int flag) throws InvalidArgumentException, JSONException {
        LogUtil.d("aEFragment=====" + flag);
        if (lrcList != null && lrcList.size() > 0) {
            List<AECustomTextInfo> aeCustomTextInfoList = new ArrayList<>();
            int size = lrcList.size();
            for (int i = 0; i < size; i++) {
                LrcBean lrcBean = lrcList.get(i);
                float start = ((float) lrcBean.getStart()) / 1000;
                float end = ((float) lrcBean.getEnd()) / 1000;
                if (end < duration) {
                    AECustomTextInfo aeCustomTextInfo = new AECustomTextInfo(lrcBean.getLrc(), start, end);//指定媒体的时间线
                    aeCustomTextInfoList.add(aeCustomTextInfo);
                }
            }
            AEFragmentInfo aeFragmentInfo = null;
            if (flag == 0) {
                aeFragmentInfo = AEFragmentUtils.loadSync(moduleMaterialBean.aeModulePath, aeCustomTextInfoList, duration);//添加AE模板
            } else
                aeFragmentInfo = AEFragmentUtils.loadSync(moduleMaterialBean.aeModulePath, null, duration);//添加AE模板
            int width = aeFragmentInfo.getWidth();
            int height = aeFragmentInfo.getHeight();
            float asp = width / (height + .0f);
            virtualVideoView.setPreviewAspectRatio(asp);
            List<AEFragmentInfo.LayerInfo> layers = aeFragmentInfo.getLayers();
            AETemplateInfo config = AEModuleUtils.getConfig(moduleMaterialBean.aeModulePath);
            LogUtil.d("lrcList.size()====" + lrcList.size());
            for (int i = 0; i < lrcList.size(); i++) {
                AEFragmentInfo.LayerInfo layerInfo = layers.get(i);
                String key = layerInfo.getName();
                AETextLayerInfo tmp = config.getTargetAETextLayer(key);
                String color = "#ffffff";
                if (moduleMaterialBean.colorList != null) {
                    color = moduleMaterialBean.colorList.get(i);
                }
                //文本转图片
                if (tmp != null) {
                    String init = lrcList.get(i).getLrc();
                    String file = AEModuleUtils.fixAEText(tmp, init, moduleMaterialBean.fontFilePath, color, layers.get(i));
                    layerInfo.setMediaObject(new MediaObject(file));//替换单个 layer 中的资源
                }
            }
            mVirtualVideo.addAEFragment(aeFragmentInfo);
        }
    }


    /**
     * 单排竖行 横排  4  竖排  3横排
     */
    public void singleSpecial(VirtualVideo virtualVideo, int flag) {
        List<LrcBean> lrcBeanList = moduleMaterialBean.lrcList;
        if (null != captionObjectList) {//移除字幕
            captionObjectList.clear();
        }
        int size = lrcBeanList.size();
        for (int i = 0; i < size; i++) {
            CaptionObject editingCaption = new CaptionObject();
            try {
                editingCaption.setVirtualVideo(virtualVideo, virtualVideoView);
                RectF showRectF = null;
                if (flag == 4) {
                    //更具字数计算字幕高度
                    float f = 0.3f * lrcBeanList.get(i).getLrc().length() / 6;
                    if (this.showRectF == null) {
                        showRectF = new RectF(moduleMaterialBean.leftLocation, moduleMaterialBean.topLocation,
                                moduleMaterialBean.leftLocation + 0.07f, moduleMaterialBean.topLocation + f);
                    } else {//移动之后的拿到记录的RectF重新计算位置
                        showRectF = new RectF(this.showRectF.left, this.showRectF.top,
                                this.showRectF.left + 0.07f, this.showRectF.top + f);
                    }
                } else {
                    //更具字数计算字幕长度
                    float f = 0.6f * lrcBeanList.get(i).getLrc().length() / 7;
                    float left = moduleMaterialBean.leftLocation - f / 2 < 0 ? 0 : moduleMaterialBean.leftLocation - f / 2;
                    float top = moduleMaterialBean.topLocation - 0.05f / 2;
                    float right = moduleMaterialBean.leftLocation + f / 2 > 1 ? 1 : moduleMaterialBean.leftLocation + f / 2;
                    float bottom = moduleMaterialBean.topLocation + 0.05f / 2;
                    showRectF = new RectF(left, top, right, bottom);
                }
                LogUtil.d((showRectF) + "========showRectF");
                editingCaption.setFrameArray(0, showRectF, textRectF, null, false, null, true, 1f);
            } catch (InvalidArgumentException e) {
                e.printStackTrace();
            }
            editingCaption.setText(lrcBeanList.get(i).getLrc());
            editingCaption.setTextStrokeWidth(10f);
            editingCaption.setTextStrokeAlpha(0);
            editingCaption.setShadow(false);
            if (moduleMaterialBean.fontFilePath != null)
                editingCaption.setFontByFilePath(moduleMaterialBean.fontFilePath);//使用自定义字体
            float start = ((float) lrcBeanList.get(i).getStart()) / 1000;
            float end = ((float) lrcBeanList.get(i).getEnd()) / 1000;
            editingCaption.setTimelineRange(start, end);//设置字幕时间线
            String color = "#ffffff";
            if (moduleMaterialBean.colorList != null && moduleMaterialBean.colorList.size() > 0) {
                color = moduleMaterialBean.colorList.get(i);
            }
            editingCaption.setTextColor(Color.parseColor(color));
            if (this.showRectF != null) {
                LogUtil.e(showRectF + "====showRectF");
                if (flag == 3)
                    editingCaption.setCenter(new PointF(showRectF.centerX(), showRectF.centerY()));
//                if (flag == 4)
//                    editingCaption.setShowRectF(showRectF);
            }
            if (this.scale != -1) {
                editingCaption.setScale(this.scale);
            }
            try {
                editingCaption.apply(false);
                virtualVideo.addCaption(editingCaption);
            } catch (InvalidArgumentException e) {
                e.printStackTrace();
            }
            captionObjectList.add(editingCaption);
        }

    }


    //更改位置（必须在暂停状态下才能更改）
    public void updateRectF(RectF showRectF, float scale) {
        this.showRectF = showRectF;
        this.scale = scale;
        int len = captionObjectList.size();
        if (null != showRectF) {
            int pW = virtualVideoView.getWordLayout().getWidth();
            int pH = virtualVideoView.getWordLayout().getHeight();
            Rect showRect = new Rect((int) (showRectF.left * pW), (int) (showRectF.top * pH), (int) (showRectF.right * pW), (int) (showRectF.bottom * pH));
            for (int i = 0; i < len; i++) {
                try {
                    CaptionObject captionObject = captionObjectList.get(i);
                    if (getCaptionObject() == captionObject) {
                        //修改时，当前正在编辑的caption不要修改，如果此时修改编辑，会出现两个caption  (等到播放时修改当前编辑即可)
                    } else {
                        //设置新的中心点
                        //如果要保证顶部对齐，那么需要移动的中心点就不应该是同一个了。因为：每个字幕的宽高是更加文字来计算出来的
                        // （文字数目不一致，字幕的宽高也会不一致，如果有疑问可以给字幕设置个背景图片查看每个的显示位置）
                        //1.得到要移动的字幕的显示位置
                        RectF rectF = captionObject.getCaptionDisplayRectF();
                        //根据当前的字幕宽高、得到真实的像素
                        RectF rect = new RectF((int) (rectF.left * pW), (int) (rectF.top * pH), (int) (rectF.right * pW), (int) (rectF.bottom * pH));

                        float backupScale = captionObject.getScale();
                        if (backupScale != scale) {
                            RectF dst = new RectF();
                            {
                                //通过之前的缩放系数与新的缩放系数，计算出新的目标坐标
                                Matrix matrix = new Matrix();
                                float fscale = scale / backupScale;
                                matrix.postScale(fscale, fscale, rect.centerX(), rect.centerY());
                                //得到在当前位置缩放后的位置
                                matrix.mapRect(dst, rect);
                            }
                            //因为captionObject.setScale(scale)只是记录了缩放系数，并没有正在缩放，此时的字幕的显示位置rectF依然没变 ，所以需要模拟出设置缩放后新的显示位置的坐标;
                            rect = dst;
                            captionObject.setScale(scale);
                        }

                        //偏移到顶部对齐的位置
                        int offsetX = (int) (showRect.centerX() - rect.centerX());
                        int offsetY = (int) (showRect.top - rect.top);
                        rect.offset(offsetX, offsetY);
                        PointF pointF = new PointF(rect.centerX() / (pW + .0f), rect.centerY() / (pH + .0f));
                        LogUtil.e("pointF====" + pointF);
                        captionObject.setCenter(pointF);
                        //根据新的位置从新生成简单字幕对象
                        captionObject.apply();
                    }
                } catch (InvalidArgumentException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 根据当前播放进度 获取对于的CaptionObject
     *
     * @return CaptionObject
     */
    public CaptionObject getCaptionObject() {
        if (null == virtualVideoView)
            return null;
        float currentPosition = virtualVideoView.getCurrentPosition();
        CaptionObject captionObject = null;
        if (captionObjectList != null && captionObjectList.size() > 0) {
            List<LrcBean> lrcBeans = moduleMaterialBean.lrcList;
            for (int i = 0; i < lrcBeans.size(); i++) {
                float start = ((float) lrcBeans.get(i).getStart()) / 1000;
                float end = ((float) lrcBeans.get(i).getEnd()) / 1000;
                if (currentPosition >= start && currentPosition < end) {
                    captionObject = captionObjectList.get(i);
                    captionPosition = i;
                    break;
                }
            }
        }
//        if (captionObjectList != null && captionObjectList.size() > 0) {
//            int index = 0;//最长的字幕
//            for (int i = 0; i < captionObjectList.size(); i++) {
//                if (captionObjectList.get(i).getText().length() > captionObjectList.get(index).getText().length()) {
//                    index = i;
//                }
//            }
//            captionObject = captionObjectList.get(index);
//        }
//        if (null != captionObject) {
//            virtualVideoView.seekTo(captionObject.getTimelineStart());
//            virtualVideoView.refresh();
//        }
        return captionObject;
    }


    /**
     * 保存视频
     */
    public void saveVideo(final IssueBean issueBean, final ProgressBar pb, RxAppCompatActivity mContext) {
        this.context = mContext;
        this.progressBar = pb;
        uploadManager = new UploadManager(context);
        final VirtualVideo virtualVideo = addVirtualVideo(false);
        if (virtualVideo == null) {
            onPublishListener.onFailed("virtualVideo  不能为空");
            ToastUtils.show(context, "保存错误");
            return;
        }
        VideoConfig videoConfig = new VideoConfig();
        videoConfig.setVideoSize(1080, 1920);
        String path = Environment.getExternalStorageDirectory() + "/ChaoGe";
        FileUtils.createFileMkdir(path);
        filePath = path + "/ChaoGe_" + System.currentTimeMillis() + ".mp4";
        virtualVideo.export(context, filePath, videoConfig, new ExportListener() {
            @Override
            public void onExportStart() {
                isSave = true;
                progressBar.setMax(100);
//                show = new SYDialog.Builder(context)
//                        .setDialogView(R.layout.dialog_progress)
////                                .setWindowBackgroundP(0.5f)
////                                .setScreenWidthP(1.0f)
//                        .setGravity(Gravity.CENTER)
//                        .setCancelable(false)
//                        .setCancelableOutSide(false)
//                        .setBuildChildListener(new IDialog.OnBuildListener() {
//                            @Override
//                            public void onBuildChildView(final IDialog dialog, View view, int layoutRes, FragmentManager fragmentManager) {
//                                cpb_progress = view.findViewById(R.id.cpb_progress);
//                                cpb_progress.setMaxProgress(100);
//                            }
//                        }).show();

            }

            @Override
            public boolean onExporting(int progress, int max) {
                int pro = progress * 50 / max;
                progressBar.setProgress(pro);
                LogUtil.d("导出中==" + pro);
                return true;
            }

            @Override
            public void onExportEnd(int i) {
                Utils.scanFile(context, filePath);
                ThreadPoolUtils.executeEx(new Runnable() {
                    @Override
                    public void run() {
                        VirtualVideo tmp = new VirtualVideo();
                        Scene scene = VirtualVideo.createScene();
                        try {
                            scene.addMedia(filePath);
                            tmp.addScene(scene);
                            firstSnapshot = getSnapshot(tmp, 1f);
                            centreSnapshot = getSnapshot(tmp, duration / 2);
                            upFirstPng(firstSnapshot, issueBean);
                        } catch (InvalidArgumentException e) {
                            e.printStackTrace();
                            isSave = false;
                        }

                    }
                });
            }
        });

    }


    private void upMP4(String path, final IssueBean issueBean) {
        fileOssPath = uploadManager.getObjKey(UploadManager.MP4);

        uploadManager.upload(fileOssPath, new UploadManager.Body(path), new OnUploadFileListener() {
            @Override
            public void success() {
                LogUtil.i("上传———成功——文件路径---" + fileOssPath);
                publish(issueBean);
            }

            @Override
            public void error(String msg) {
                LogUtil.i("上传———失败——原因---" + msg);
                onPublishListener.onFailed("上传阿里云 视频 失败  ：" + msg);
                saveFailed();
            }

            @Override
            public void progress(int progress, long currentSize, long totalSize) {
                progressBar.setProgress(50 + progress / 2);
                LogUtil.i("上传中———进度——" + progress);
                LogUtil.i("上传中———当前大小——" + currentSize);
                LogUtil.i("上传中———总大小——" + totalSize);

            }
        });

    }

    private void upCentrePng(String bitmapPath, final IssueBean issueBean) {
        if (bitmapPath == null)
            return;
//        image/201609/14/xxx.jpg
//       xxx = md5(uid + time + index)
//        // 按如上格式拼保存路径
        centreImgPath = uploadManager.getObjKey(UploadManager.PNG);

        uploadManager.upload(centreImgPath, new UploadManager.Body(bitmapPath), new OnUploadFileListener() {
            @Override
            public void success() {
                LogUtil.i("上传———成功——文件路径---" + centreImgPath);
                upMP4(filePath, issueBean);
            }

            @Override
            public void error(String msg) {
                LogUtil.i("线程——" + Looper.getMainLooper().getThread().getName());
                LogUtil.i("上传———失败——原因---" + msg);
                onPublishListener.onFailed("上传阿里云 视频中间帧Video封面图 失败  ：" + msg);
                saveFailed();
            }

            @Override
            public void progress(int progress, long currentSize, long totalSize) {
//                cpb_progress.setProgress(50 + progress / 4);
                LogUtil.i("上传中——中间帧—进度——" + progress);
                LogUtil.i("上传中———当前大小——" + currentSize);
                LogUtil.i("上传中———总大小——" + totalSize);

            }
        });
    }

    private void upFirstPng(String bitmapPath, final IssueBean issueBean) {
        if (bitmapPath == null)
            return;
//        image/201609/14/xxx.jpg
//       xxx = md5(uid + time + index)
//        // 按如上格式拼保存路径
        firstImgPath = uploadManager.getObjKey(UploadManager.PNG);

        uploadManager.upload(firstImgPath, new UploadManager.Body(bitmapPath), new OnUploadFileListener() {
            @Override
            public void success() {
                LogUtil.i("上传———成功——文件路径---" + firstImgPath);
                upCentrePng(centreSnapshot, issueBean);
            }

            @Override
            public void error(String msg) {
                LogUtil.i("线程——" + Looper.getMainLooper().getThread().getName());
                LogUtil.i("上传———失败——原因---" + msg);
                onPublishListener.onFailed("上传阿里云 视频第一帧Video封面图 失败  ：" + msg);
                saveFailed();
            }

            @Override
            public void progress(int progress, long currentSize, long totalSize) {
//                cpb_progress.setProgress(50 + progress / 4);
                LogUtil.i("上传中—第一帧——进度——" + progress);
                LogUtil.i("上传中———当前大小——" + currentSize);
                LogUtil.i("上传中———总大小——" + totalSize);

            }
        });
    }

    private void publish(final IssueBean issueBean) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", issueBean.userId);//用户ID  c595901cbb14672dc4a911f716556a8f
        map.put("musicId", issueBean.musicId);//背景音乐Id  493a4e87076898027890fc52224b16dd
        map.put("scriptId", issueBean.scriptId);//选用剧本Id  7d504774e7e9b72e47cc28774c43031c
        map.put("content", issueBean.content);//发布内容  7d504774e7e9b72e47cc28774c43031c
        map.put("modelId", issueBean.modelId);//模板Id  7d504774e7e9b72e47cc28774c43031c
        map.put("backGroundId", issueBean.backGroundId != null ? issueBean.backGroundId : "");//背景图Id
        map.put("videoUrl", fileOssPath);//视频 Video地址
        map.put("cover", firstImgPath);//视频第一帧Video封面图
        map.put("poster", centreImgPath);//视频中间帧Video封面图
        map.put("createTime", System.currentTimeMillis());//long createTime;
        PublishRequest.newInstance(context).publish(map, new BaseRequestInterface<String>() {
            @Override
            public void success(int state, String msg, String momentId) {
                if (!Utils.isEmpty(momentId)) {
                    String url = RequestURL.SHARE_VIDEO + "?im=" +
                            momentId + "&iu=" + issueBean.userId;
                    String title;
                    if (Utils.isEmpty(issueBean.content)) {
                        String nickname = SPUtils.readCurrentLoginUserInfo(context).nickname;
                        title = nickname + "的朝歌视频，快来看看吧!";
                    } else {
                        title = issueBean.content;
                    }


                    onPublishListener.onFinish(url, title, VIDEO_BASE_URL + centreImgPath);
                    isSave = false;
                    FileUtils.deleteFile(firstSnapshot);
                    FileUtils.deleteFile(centreSnapshot);
                }
            }

            @Override
            public void error(int state, String msg) {
                onPublishListener.onFailed("发布 失败  ：" + msg);
                saveFailed();
            }
        });
    }


    private void saveFailed() {
        isSave = false;
        FileUtils.deleteFile(filePath);
    }


    public void setOnPublishListener(OnPublishListener onPublishListener) {
        this.onPublishListener = onPublishListener;
    }

    public ArrayList<String> getStrList() {
        ArrayList<String> strList = new ArrayList<>();
        List<LrcBean> lrcBeans = moduleMaterialBean.lrcList;
        if (null != lrcBeans && lrcBeans.size() > 0)
            for (int i = 0; i < lrcBeans.size(); i++) {
                strList.add(lrcBeans.get(i).getLrc());
            }

        return strList;
    }


    public interface OnPublishListener {
        void onFinish(String fileLocalPath, String title, String coverPath);

        void onFailed(String msg);


    }


    public static void clearVideoEditManager() {
        if (null != videoEditManager)
            videoEditManager = null;
    }
}
