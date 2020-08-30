package com.test.xcamera.managers;

import android.content.Context;

import com.test.xcamera.R;
import com.test.xcamera.bean.AWBType;
import com.test.xcamera.bean.MoFPVParameter;
import com.test.xcamera.bean.MoFPVSetting;
import com.test.xcamera.bean.MoRecordSetting;
import com.test.xcamera.bean.MoShotSetting;
import com.test.xcamera.bean.MoSnapShotSetting;
import com.test.xcamera.utils.MoUtil;

import java.util.ArrayList;
import java.util.Collections;

/**
 * 拍摄参数设置
 * Created by zll on 2019/10/16.
 */

public class MoFPVParametersManager {
    private static final String TAG = "WorkStateManager";
    private static MoFPVParametersManager singleton = null;
    private static Object lock = new Object();
    private ArrayList<MoFPVSetting> moFPVSettings;
    private Context mContext;
    private String[] mProfessionalNamesVideo;

    private int[] mProfessionalResIDVideo;
    private String[] mProfessionalNamesPhoto;
    private int[] mProfessionalResIDPhoto;

    private String[] mUnProfessionalNamesVideo;
    private int[] mUnProfessionalResIDVideo;

    private String[] mUnProfessionalNamesPhoto;
    private int[] mUnProfessionalResIDPhoto;

    private int[] mAWBTypes;

    private String[] mISOValue;
    private String[] mEVValue;
    private String[] mShutterValue;

    private ArrayList<ValueData> mISOValueList;
    private ArrayList<ValueData> mEVValueList;
    private ArrayList<ValueData> mShutterValueList;
    private ArrayList<ValueData> mAWBValueList;

    private int mSyncISOValue;
    private int mSyncEVValue;
    private int mSyncShutterValue;
    private int mSyncAWBValue;

    public static MoFPVParametersManager getInstance(Context context) {
        synchronized (lock) {
            if (singleton == null) {
                singleton = new MoFPVParametersManager(context);
            }
        }
        return singleton;
    }

    public MoFPVParametersManager(Context context) {
        mContext = context;
        moFPVSettings = new ArrayList<>();
        initData();
    }

    public void initData() {
        mProfessionalNamesVideo = new String[]{
                MoUtil.getString(mContext, R.string.fpv_setting_type_more)
                ,MoUtil.getString(mContext, R.string.fpv_setting_type_format)
                ,MoUtil.getString(mContext, R.string.fpv_setting_type_super_high_quality)
                ,MoUtil.getString(mContext, R.string.fpv_setting_type_wdr)
                ,MoUtil.getString(mContext, R.string.fpv_setting_type_dis)
                , MoUtil.getString(mContext, R.string.fpv_setting_type_track)
                , MoUtil.getString(mContext, R.string.fpv_setting_type_awb)
                , MoUtil.getString(mContext, R.string.fpv_setting_type_explore)};
        mProfessionalResIDVideo = new int[]{
                R.mipmap.icon_more_setting, R.drawable.fpv_setting_format_mp4_selector,
                R.drawable.fpv_setting_hd_selector, R.drawable.fpv_setting_wdr_selector,
                R.drawable.fpv_setting_fangdou_selector, R.drawable.fpv_setting_tracking_selector,
                R.drawable.fpv_setting_baipingheng_selector, R.drawable.fpv_setting_baoguang_selector};
        mProfessionalNamesPhoto = new String[]{
                MoUtil.getString(mContext, R.string.fpv_setting_type_more), MoUtil.getString(mContext, R.string.fpv_setting_type_format),
                MoUtil.getString(mContext, R.string.fpv_setting_type_super_high_quality), MoUtil.getString(mContext, R.string.fpv_setting_type_dis),
                MoUtil.getString(mContext, R.string.fpv_setting_type_track), MoUtil.getString(mContext, R.string.fpv_setting_type_awb),
                MoUtil.getString(mContext, R.string.fpv_setting_type_explore)};
        mProfessionalResIDPhoto = new int[]{
                R.mipmap.icon_more_setting, R.drawable.fpv_setting_format_jpg_selector,
                R.drawable.fpv_setting_hdr_selector, R.drawable.fpv_setting_fangdou_selector,
                R.drawable.fpv_setting_tracking_selector, R.drawable.fpv_setting_baipingheng_selector,
                R.drawable.fpv_setting_baoguang_selector};
        mUnProfessionalNamesVideo = new String[]{
                MoUtil.getString(mContext, R.string.fpv_setting_type_more), MoUtil.getString(mContext, R.string.fpv_setting_type_format),
                MoUtil.getString(mContext, R.string.fpv_setting_type_super_high_quality), MoUtil.getString(mContext, R.string.fpv_setting_type_wdr),
                MoUtil.getString(mContext, R.string.fpv_setting_type_dis),  MoUtil.getString(mContext, R.string.fpv_setting_type_track)};
        mUnProfessionalResIDVideo = new int[]{
                R.mipmap.icon_more_setting, R.drawable.fpv_setting_format_mp4_selector,
                R.drawable.fpv_setting_hd_selector, R.drawable.fpv_setting_wdr_selector,
                R.drawable.fpv_setting_fangdou_selector, R.drawable.fpv_setting_tracking_selector};
        mUnProfessionalNamesPhoto = new String[]{
                MoUtil.getString(mContext, R.string.fpv_setting_type_more), MoUtil.getString(mContext, R.string.fpv_setting_type_format),
                MoUtil.getString(mContext, R.string.fpv_setting_type_wdr), MoUtil.getString(mContext, R.string.fpv_setting_type_dis),
                MoUtil.getString(mContext, R.string.fpv_setting_type_track)};
        mUnProfessionalResIDPhoto = new int[]{
                R.mipmap.icon_more_setting, R.drawable.fpv_setting_format_jpg_selector,
                R.drawable.fpv_setting_hdr_selector, R.drawable.fpv_setting_fangdou_selector,
                R.drawable.fpv_setting_tracking_selector};
        mAWBTypes = new int[]{
                R.drawable.awb_7000_selector, R.drawable.awb_6000_selector, R.drawable.awb_5200_selector,
                R.drawable.awb_4000_selector, R.drawable.awb_3200_selector, R.drawable.awb_zidingyi_selector
        };
        mISOValue = new String[]{"1600", "1250", "1000", "800", "600", "500", "400"};
        mEVValue = new String[]{"-3", "-2", "-1", "0", "1", "2", "3"};
        mShutterValue = new String[]{"1/100", "1/80", "1/60", "1/50", "1/40", "1/30", "1/25"};
    }

    public ArrayList<MoFPVSetting> getMoFPVSettings(boolean isProfrssional, final boolean exploreAuto,
                                                    final boolean awbAuto) {
        if (moFPVSettings != null && moFPVSettings.size() > 0) {
            moFPVSettings.clear();
        }
        String names[];
        int resIDs[];
        if (isProfrssional) {
            if (ShotModeManager.getInstance().isVideo()) {
                names = mProfessionalNamesVideo;
                resIDs = mProfessionalResIDVideo;
            } else {
                names = mProfessionalNamesPhoto;
                resIDs = mProfessionalResIDPhoto;
            }
            for (int i = 0; i < names.length; i++) {
                MoFPVSetting setting = new MoFPVSetting();
                setting.setmTitle(names[i]);
                setting.setmResourceID(resIDs[i]);
                //TODO 添加分割线标记
                if (names[i].equals(MoUtil.getString(mContext, R.string.fpv_setting_type_explore))) {
                    setting.setmType(1);
                    MoFPVParameter parameter = new MoFPVParameter();
                    parameter.setmAuto(exploreAuto);
                    setting.setMoFPVParameter(parameter);
                    if (exploreAuto) {
                        setting.setmSelect(1);
                    } else {
                        setting.setmSelect(0);
                    }
                } else if (names[i].equals(MoUtil.getString(mContext, R.string.fpv_setting_type_awb))) {
                    setting.setmType(1);
                    MoFPVParameter parameter = new MoFPVParameter();
                    parameter.setmAuto(awbAuto);
                    setting.setMoFPVParameter(parameter);
                    if (awbAuto) {
                        setting.setmSelect(1);
                    } else {
                        setting.setmSelect(0);
                    }
                } else {
                    setting.setmType(0);
                }
                moFPVSettings.add(setting);
            }
        } else {
            if (ShotModeManager.getInstance().isVideo()) {
                names = mUnProfessionalNamesVideo;
                resIDs = mUnProfessionalResIDVideo;
            } else {
                names = mUnProfessionalNamesPhoto;
                resIDs = mUnProfessionalResIDPhoto;
            }
            for (int i = 0; i < names.length; i++) {
                MoFPVSetting setting = new MoFPVSetting();
                setting.setmTitle(names[i]);
                setting.setmResourceID(resIDs[i]);
                moFPVSettings.add(setting);
            }
        }
        return moFPVSettings;
    }

    public void setMoFPVSettings(ArrayList<MoFPVSetting> moFPVSettings) {
        this.moFPVSettings = moFPVSettings;
    }

    public ArrayList<AWBType> getAWBTypeValues() {
        ArrayList<AWBType> list = new ArrayList<>();
        for (int i = 0; i < mAWBTypes.length; i++) {
            AWBType type = new AWBType();
            if (i == 2) {
                type.setSelected(true);
            } else {
                type.setSelected(false);
            }
            type.setResID(mAWBTypes[i]);
            list.add(type);
        }
        return list;
    }

    public ArrayList<ValueData> getISOValue() {
        if (mISOValueList == null) {
            mISOValueList = new ArrayList<>();
            for (int i = 0; i < mISOValue.length; i++) {
                ValueData valueData = new ValueData();
                valueData.setValue(mISOValue[i]);
                valueData.setType(0);
                mISOValueList.add(valueData);
            }
            return mISOValueList;
        }
        return mISOValueList;
    }

    public ArrayList<MoFPVParametersManager.ValueData> getEVValue() {
        if (mEVValueList == null) {
            mEVValueList = new ArrayList<>();
            for (int i = 0; i < mEVValue.length; i++) {
                ValueData valueData = new ValueData();
                valueData.setValue(mEVValue[i]);
                valueData.setType(0);
                mEVValueList.add(valueData);
            }
            return mEVValueList;
        }
        return mEVValueList;
    }

    public ArrayList<ValueData> getShutterValue() {
        if (mShutterValueList == null) {
            mShutterValueList = new ArrayList<>();
            for (int i = 0; i < mShutterValue.length; i++) {
                ValueData valueData = new ValueData();
                valueData.setValue(mShutterValue[i]);
                valueData.setType(0);
                mShutterValueList.add(valueData);
            }
            return mShutterValueList;
        }
        return mShutterValueList;
    }

    public ArrayList<ValueData> getAWBValue() {
        if (mAWBValueList == null) {
            mAWBValueList = new ArrayList<>();
            for (int i = 30; i <= 70; i++) {
                ValueData data = new ValueData();
                String value = (i * 100) + "K";
                data.setValue(value);
                data.setType(0);
                mAWBValueList.add(data);
            }
            Collections.reverse(mAWBValueList);
            return mAWBValueList;
        }
        return mAWBValueList;
    }

    public class ValueData {
        String value;
        int type;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }

    public void syncFPVSettings(MoShotSetting shotSetting, boolean isProfrssional) {
        int iso = -1;
        int ev = -1;
        int shutter = -1;
        int awb = -1;
        int track = -1;
        int dis = -1;
        int wdr = -1;
        int hd = -1;
        int format = -1;
        if (ShotModeManager.getInstance().isVideo()) {
            MoRecordSetting recordSetting = shotSetting.getmMoRecordSetting();
            if (recordSetting != null) {
                iso = recordSetting.getmISO();
                ev = recordSetting.getmEV();
                awb = recordSetting.getmAwb();
                track = recordSetting.getmTrack();
                dis = recordSetting.getmDIS();
                wdr = recordSetting.getmWDR();
                hd = recordSetting.getmSuperHighQuality();
                format = recordSetting.getmType();
                mSyncISOValue = recordSetting.getmISO();
                mSyncEVValue = recordSetting.getmEV();
                mSyncAWBValue = recordSetting.getmAwb();
            }
        } else {
            MoSnapShotSetting snapShotSetting = shotSetting.getmMoSnapShotSetting();
            if (snapShotSetting != null) {
                iso = snapShotSetting.getmISO();
                ev = snapShotSetting.getmEV();
                shutter = snapShotSetting.getmShutter();
                awb = snapShotSetting.getmAwb();
                track = snapShotSetting.getmTrack();
                dis = snapShotSetting.getmDIS();
                hd = snapShotSetting.getmSuperHighQuality();
                format = snapShotSetting.getmType();
                mSyncISOValue = snapShotSetting.getmISO();
                mSyncEVValue = snapShotSetting.getmEV();
                mSyncShutterValue = snapShotSetting.getmShutter();
                mSyncAWBValue = snapShotSetting.getmAwb();
            }
        }

        if (moFPVSettings != null && moFPVSettings.size() > 0) {
            moFPVSettings.clear();
        }
        String names[];
        int resIDs[];
        if (isProfrssional) {
            if (ShotModeManager.getInstance().isVideo()) {
                names = mProfessionalNamesVideo;
                resIDs = mProfessionalResIDVideo;
            } else {
                names = mProfessionalNamesPhoto;
                resIDs = mProfessionalResIDPhoto;
            }
            for (int i = 0; i < names.length; i++) {
                MoFPVSetting setting = new MoFPVSetting();
                setting.setmTitle(names[i]);
                setting.setmResourceID(resIDs[i]);
                if (names[i].equals(MoUtil.getString(mContext, R.string.fpv_setting_type_explore))) {
                    setting.setmType(1);
                    MoFPVParameter parameter = new MoFPVParameter();
                    parameter.setmAuto(isExploreAuto());
                    setting.setMoFPVParameter(parameter);
                    if (isExploreAuto()) {
                        setting.setmSelect(1);
                    } else {
                        setting.setmSelect(0);
                    }
                } else if (names[i].equals(MoUtil.getString(mContext, R.string.fpv_setting_type_awb))) {
                    setting.setmType(1);
                    MoFPVParameter parameter = new MoFPVParameter();
                    parameter.setmAuto(isAWBAuto());
                    setting.setMoFPVParameter(parameter);
                    if (isAWBAuto()) {
                        setting.setmSelect(1);
                    } else {
                        setting.setmSelect(0);
                    }
                } else if (names[i].equals(MoUtil.getString(mContext, R.string.fpv_setting_type_track))) {
                    setting.setmSelect(track == 0 ? 0 : 1);
                } else if (names[i].equals(MoUtil.getString(mContext, R.string.fpv_setting_type_dis))) {
                    setting.setmSelect(dis == 0 ? 0 : 1);
                } else if (names[i].equals(MoUtil.getString(mContext, R.string.fpv_setting_type_wdr))) {
                    setting.setmSelect(wdr == 0 ? 0 : 1);
                } else if (names[i].equals(MoUtil.getString(mContext, R.string.fpv_setting_type_super_high_quality))) {
                    setting.setmSelect(hd == 0 ? 0 : 1);
                } else if (names[i].equals(MoUtil.getString(mContext, R.string.fpv_setting_type_format))) {
                    setting.setmSelect(format == 0 ? 0 : 1);
                } else {
                    setting.setmType(0);
                }
                moFPVSettings.add(setting);
            }
        } else {
            if (ShotModeManager.getInstance().isVideo()) {
                names = mUnProfessionalNamesVideo;
                resIDs = mUnProfessionalResIDVideo;
            } else {
                names = mUnProfessionalNamesPhoto;
                resIDs = mUnProfessionalResIDPhoto;
            }
            for (int i = 0; i < names.length; i++) {
                MoFPVSetting setting = new MoFPVSetting();
                setting.setmTitle(names[i]);
                setting.setmResourceID(resIDs[i]);
                if (names[i].equals(MoUtil.getString(mContext, R.string.fpv_setting_type_track))) {
                    setting.setmSelect(track == 0 ? 0 : 1);
                } else if (names[i].equals(MoUtil.getString(mContext, R.string.fpv_setting_type_dis))) {
                    setting.setmSelect(dis == 0 ? 0 : 1);
                } else if (names[i].equals(MoUtil.getString(mContext, R.string.fpv_setting_type_wdr))) {
                    setting.setmSelect(wdr == 0 ? 0 : 1);
                } else if (names[i].equals(MoUtil.getString(mContext, R.string.fpv_setting_type_super_high_quality))) {
                    setting.setmSelect(hd == 0 ? 0 : 1);
                } else if (names[i].equals(MoUtil.getString(mContext, R.string.fpv_setting_type_format))) {
                    setting.setmSelect(format == 0 ? 0 : 1);
                }
                moFPVSettings.add(setting);
            }
        }
    }

    public ArrayList<MoFPVSetting> getMoFPVSettings() {
        return moFPVSettings;
    }

    public int getSyncISOValue() {
        return mSyncISOValue;
    }

    public int getSyncEVValue() {
        return mSyncEVValue;
    }

    public int getSyncShutterValue() {
        return mSyncShutterValue;
    }

    public int getSyncAWBValue() {
        return mSyncAWBValue;
    }

    public boolean isExploreAuto() {
        return mSyncISOValue == 0 && mSyncEVValue == 0 && mSyncShutterValue == 0;
    }

    public boolean isAWBAuto() {
        return mSyncAWBValue == 0;
    }
}
