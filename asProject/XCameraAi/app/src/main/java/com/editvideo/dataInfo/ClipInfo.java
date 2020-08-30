package com.editvideo.dataInfo;

import android.graphics.RectF;
import android.text.TextUtils;

import com.meicam.sdk.NvsVideoStreamInfo;
import com.editvideo.Constants;

import static com.editvideo.Constants.VIDEOVOLUME_DEFAULTVALUE;


//开始制作时封装的数据
public class ClipInfo {
    public boolean isRecFile = false;
    public int rotation = NvsVideoStreamInfo.VIDEO_ROTATION_0;
    private String m_filePath;
    //视频处理前地址
    private String m_fileOldPath;
    //倒放后的视频视频地址
    private String m_fileConvertPath;
    //是否开启倒放
    private boolean mIsReverseConvert=false;
    //是否已经转码
    private boolean mIsConvert=false;
    private float m_speed;
    private boolean m_mute;
    private long m_trimIn;
    private long m_trimOut;

    private long duration;
    private boolean mIsConvertSuccess = false;//视频默认未转码成功
    private long mDurationBySpeed;

    //校色数据

    private float m_brightnessVal;//曝光
    private float m_contrastVal;//对比度
    private float m_saturationVal;//饱和度
    private float m_vignetteVal; // 暗角
    private float m_sharpenVal;  // 锐度
    //

    //美颜
    private float strength; // 磨皮
    private float red;  // 红润
    private float white;  // 美白
    private float bigeye; // 大眼
    private float thinface;  // 瘦脸
    private float xiaba; // 下巴
    private float nose;  // 鼻子
    private float head;  //额头
    private float mouthSize;  //嘴部
    private float smallFace;  //小脸
    private float eyeCorner;  //眼角
    private float faceWidth;  //窄脸
    private float noseLength;  //长鼻
    private float mouthCorner;  //嘴角

    //音量
    private float m_volume;
    //调整

    private int m_rotateAngle;//旋转角度
    private int m_scaleX;//
    private int m_scaleY;


    //图片展示模式

    private int m_imgDispalyMode = Constants.EDIT_MODE_PHOTO_AREA_DISPLAY;
    //是否开启图片运动
    private boolean isOpenPhotoMove = true;
    //图片起始ROI
    private RectF m_normalStartROI;
    //图片终止ROI
    private RectF m_normalEndROI;
    //视频横向裁剪，纵向平移

    private float m_pan;
    private float m_scan;
    //转场
    private TransitionInfo m_transitionInfo;
    private boolean remoteData = false;  //是否是相机数据。相机图片加载必须用picasso，本地图片加载用glide

    public boolean isRemoteData() {
        return remoteData;
    }

    public void setRemoteData(boolean remoteData) {
        this.remoteData = remoteData;
    }

    public TransitionInfo getTransitionInfo() {
        return m_transitionInfo;
    }

    public void setTransitionInfo(TransitionInfo m_transitionInfo) {
        this.m_transitionInfo = m_transitionInfo;
    }

    public boolean isIsConvert() {
        return mIsConvert;
    }

    public void setIsConvert(boolean mIsConvert) {
        this.mIsConvert = mIsConvert;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public float getPan() {
        return m_pan;
    }

    public void setPan(float pan) {
        this.m_pan = pan;
    }

    public float getScan() {
        return m_scan;
    }

    public void setScan(float scan) {
        this.m_scan = scan;
    }

    public RectF getNormalStartROI() {
        return m_normalStartROI;
    }

    public void setNormalStartROI(RectF normalStartROI) {
        this.m_normalStartROI = normalStartROI;
    }
    public RectF getNormalEndROI() {
        return m_normalEndROI;
    }

    public void setNormalEndROI(RectF normalEndROI) {
        this.m_normalEndROI = normalEndROI;
    }

    public boolean isOpenPhotoMove() {
        return isOpenPhotoMove;
    }

    public void setOpenPhotoMove(boolean openPhotoMove) {
        isOpenPhotoMove = openPhotoMove;
    }
    public int getImgDispalyMode() {
        return m_imgDispalyMode;
    }

    public void setImgDispalyMode(int imgDispalyMode) {
        m_imgDispalyMode = imgDispalyMode;
    }
    public int getScaleX() {
        return m_scaleX;
    }

    public void setScaleX(int scaleX) {
        this.m_scaleX = scaleX;
    }

    public int getScaleY() {
        return m_scaleY;
    }

    public void setScaleY(int scaleY) {
        this.m_scaleY = scaleY;
    }

    public int getRotateAngle() {
        return m_rotateAngle;
    }

    public void setRotateAngle(int rotateAngle) {
        this.m_rotateAngle = rotateAngle;
    }
    public float getVolume() {
        return m_volume;
    }

    public void setVolume(float volume) {
        this.m_volume = volume;
    }
    public float getBrightnessVal() {
        return m_brightnessVal;
    }

    public void setBrightnessVal(float brightnessVal) {
        this.m_brightnessVal = brightnessVal;
    }

    public float getContrastVal() {
        return m_contrastVal;
    }

    public void setContrastVal(float contrastVal) {
        this.m_contrastVal = contrastVal;
    }

    public float getSaturationVal() {
        return m_saturationVal;
    }

    public void setSaturationVal(float saturationVal) {
        this.m_saturationVal = saturationVal;
    }

    public float getVignetteVal() {
        return m_vignetteVal;
    }

    public void setVignetteVal(float vignetteVal) {
        this.m_vignetteVal = vignetteVal;
    }

    public float getSharpenVal() {
        return m_sharpenVal;
    }

    public void setSharpenVal(float sharpenVal) {
        this.m_sharpenVal = sharpenVal;
    }

    public String getM_fileOldPath() {
        return m_fileOldPath;
    }

    public void setM_fileOldPath(String m_fileOldPath) {
        this.m_fileOldPath = m_fileOldPath;
    }

    public ClipInfo() {
        m_filePath = null;
        m_fileConvertPath = "";
        m_fileOldPath = "";
        mIsReverseConvert=false;
        m_speed = 1.0f;
        m_mute = false;
        m_trimIn = -1;
        m_trimOut = -1;
        m_brightnessVal = -1.0f;
        m_contrastVal = -1.0f;
        m_saturationVal = -1.0f;
        m_sharpenVal = 0;
        m_vignetteVal = 0;
        m_volume = VIDEOVOLUME_DEFAULTVALUE;
        m_rotateAngle = 0;
        m_scaleX = -2;//
        m_scaleY = -2;
        m_pan = 0.0f;
        m_scan = 0.0f;
        m_transitionInfo=new TransitionInfo();
    }

    public String getFileConvertPath() {
        return m_fileConvertPath;
    }

    public void setFileConvertPath(String m_fileConvertPath) {
        this.m_fileConvertPath = m_fileConvertPath;
    }

    public boolean isIsReverseConvert() {
        return mIsReverseConvert;
    }

    public void setIsReverseConvert(boolean mIsReverseConvert) {
        this.mIsReverseConvert = mIsReverseConvert;
    }

    public void setFilePath(String filePath) {
        m_filePath = filePath;
    }

    public String getFilePath() {
        if(mIsReverseConvert&&!TextUtils.isEmpty(m_fileConvertPath)){
            return m_fileConvertPath;
        }
        return m_filePath;
    }

    public void setSpeed(float speed) {
        m_speed = speed;
    }

    public float getSpeed() {
        return m_speed;
    }

    public void setMute(boolean flag) {
        m_mute = flag;
    }

    public boolean getMute() {
        return m_mute;
    }

    public void changeTrimIn(long data) {
        m_trimIn = data;
    }

    public long getTrimIn() {
        return m_trimIn;
    }

    public void changeTrimOut(long data) {
        m_trimOut = data;
    }

    public long getTrimOut() {
        return m_trimOut;
    }

    public boolean ismIsConvertSuccess() {
        return mIsConvertSuccess;
    }

    public void setmIsConvertSuccess(boolean mIsConvertSuccess) {
        this.mIsConvertSuccess = mIsConvertSuccess;
    }

    public long getmDurationBySpeed() {
        return mDurationBySpeed;
    }

    public void setmDurationBySpeed(long mDurationBySpeed) {
        this.mDurationBySpeed = mDurationBySpeed;
    }
    public float getStrength() {
        return strength;
    }

    public void setStrength(float strength) {
        this.strength = strength;
    }

    public float getRed() {
        return red;
    }

    public void setRed(float red) {
        this.red = red;
    }
    public float getBigeye() {
        return bigeye;
    }

    public void setBigeye(float bigeye) {
        this.bigeye = bigeye;
    }

    public float getThinface() {
        return thinface;
    }

    public void setThinface(float thinface) {
        this.thinface = thinface;
    }

    public float getXiaba() {
        return xiaba;
    }

    public void setXiaba(float xiaba) {
        this.xiaba = xiaba;
    }

    public float getNose() {
        return nose;
    }

    public void setNose(float nose) {
        this.nose = nose;
    }
    public float getWhite() {
        return white;
    }

    public void setWhite(float white) {
        this.white = white;
    }

    public float getHead() {
        return head;
    }

    public void setHead(float head) {
        this.head = head;
    }

    public float getMouthSize() {
        return mouthSize;
    }

    public void setMouthSize(float mouthSize) {
        this.mouthSize = mouthSize;
    }

    public float getSmallFace() {
        return smallFace;
    }

    public void setSmallFace(float smallFace) {
        this.smallFace = smallFace;
    }

    public float getEyeCorner() {
        return eyeCorner;
    }

    public void setEyeCorner(float eyeCorner) {
        this.eyeCorner = eyeCorner;
    }

    public float getFaceWidth() {
        return faceWidth;
    }

    public void setFaceWidth(float faceWidth) {
        this.faceWidth = faceWidth;
    }

    public float getNoseLength() {
        return noseLength;
    }

    public void setNoseLength(float noseLength) {
        this.noseLength = noseLength;
    }

    public float getMouthCorner() {
        return mouthCorner;
    }

    public void setMouthCorner(float mouthCorner) {
        this.mouthCorner = mouthCorner;
    }


    public ClipInfo clone(){
        ClipInfo newClipInfo = new ClipInfo();
        newClipInfo.isRecFile = this.isRecFile;
        newClipInfo.rotation = this.rotation;
        newClipInfo.setFilePath(this.m_filePath);
        newClipInfo.setM_fileOldPath(this.getM_fileOldPath());
        newClipInfo.setFileConvertPath(this.getFileConvertPath());
        newClipInfo.setIsReverseConvert(this.isIsReverseConvert());
        newClipInfo.setMute(this.getMute());
        newClipInfo.setSpeed(this.getSpeed());
        newClipInfo.changeTrimIn(this.getTrimIn());
        newClipInfo.changeTrimOut(this.getTrimOut());
        newClipInfo.setDuration(this.getDuration());

        //copy data
        newClipInfo.setBrightnessVal(this.getBrightnessVal());
        newClipInfo.setSaturationVal(this.getSaturationVal());
        newClipInfo.setContrastVal(this.getContrastVal());
        newClipInfo.setVignetteVal(this.getVignetteVal());
        newClipInfo.setSharpenVal(this.getSharpenVal());
        newClipInfo.setVolume(this.getVolume());
        newClipInfo.setRotateAngle(this.getRotateAngle());
        newClipInfo.setScaleX(this.getScaleX());
        newClipInfo.setScaleY(this.getScaleY());

        //图片数据
        newClipInfo.setImgDispalyMode(this.getImgDispalyMode());
        newClipInfo.setOpenPhotoMove(this.isOpenPhotoMove());
        newClipInfo.setNormalStartROI(this.getNormalStartROI());
        newClipInfo.setNormalEndROI(this.getNormalEndROI());

        //视频横向裁剪，纵向平移
        newClipInfo.setPan(this.getPan());
        newClipInfo.setScan(this.getScan());
        newClipInfo.setmIsConvertSuccess(this.ismIsConvertSuccess());
        //美颜美型
        newClipInfo.setStrength(this.getStrength());
        newClipInfo.setRed(this.getRed());
        newClipInfo.setWhite(this.getWhite());
        newClipInfo.setBigeye(this.getBigeye());
        newClipInfo.setThinface(this.getThinface());
        newClipInfo.setXiaba(this.getXiaba());
        newClipInfo.setNose(this.getNose());
        newClipInfo.setHead(this.getHead());
        newClipInfo.setEyeCorner(this.getEyeCorner());
        newClipInfo.setFaceWidth(this.getFaceWidth());
        newClipInfo.setSmallFace(this.getSmallFace());
        newClipInfo.setNoseLength(this.getNoseLength());
        newClipInfo.setMouthSize(this.getMouthSize());
        newClipInfo.setMouthCorner(this.getMouthCorner());
        TransitionInfo info=new TransitionInfo();
        if(getTransitionInfo()!=null){
            info.setTransitionId(this.getTransitionInfo().getTransitionId());
            info.setTransitionMode(this.getTransitionInfo().getTransitionMode());
        }
        newClipInfo.setTransitionInfo(info);
        newClipInfo.setRemoteData(this.isRemoteData());
        return newClipInfo;
    }
}
