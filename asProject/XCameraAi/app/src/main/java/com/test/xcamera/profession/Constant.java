package com.test.xcamera.profession;

import com.test.xcamera.R;
import com.test.xcamera.enumbean.ScreenOrientationType;
import com.test.xcamera.managers.ShotModeManager;
import com.test.xcamera.utils.StringUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Created by smz on 2020/1/16.
 */

public class Constant {
    public static final int EXPOSURE = 0;
    public static final int AWB = 1;
    public static final int HDR = 2;
    public static final int FORMAT = 3;
    public static final int MORE = 4;
    private static final int FRAME_RATE[] = {0, 24, 30, 48, 60, 120, 240};
    /**
     * 设置页面 视频参数
     */
    public static final List<IconAdapter.SettingItem> settingsVideo = new ArrayList<>(Arrays.asList(
//            new IconAdapter.SettingItem(R.mipmap.icon_more, "更多", MORE, false)
            new IconAdapter.SettingItem(R.drawable.profession_format_video, StringUtil.getStr(R.string.fpv_set_format), FORMAT, false)
            , new IconAdapter.SettingItem(R.drawable.profession_awb, StringUtil.getStr(R.string.fpv_set_auto), AWB, false)
            , new IconAdapter.SettingItem(R.mipmap.icon_exposure, StringUtil.getStr(R.string.fpv_set_auto), EXPOSURE, false)
    ));

    /**
     * 设置页面 照片参数
     */
    public static final List<IconAdapter.SettingItem> settingsImage = new ArrayList<>(Arrays.asList(
//            new IconAdapter.SettingItem(R.mipmap.icon_more, "更多", MORE, false)
//            new IconAdapter.SettingItem(R.drawable.profession_format_image, "格式", FORMAT, false)
//            , new IconAdapter.SettingItem(R.mipmap.icon_hdr, "高动态", HDR, false)
            new IconAdapter.SettingItem(R.drawable.profession_awb, StringUtil.getStr(R.string.fpv_set_auto), AWB, false)
            , new IconAdapter.SettingItem(R.mipmap.icon_exposure, StringUtil.getStr(R.string.fpv_set_auto), EXPOSURE, false)
    ));

    /**
     * 设置页面 白平衡参数
     */
    public static final List<IconAdapter.SettingItem> settingsAwb = new ArrayList<>(Arrays.asList(
            new IconAdapter.SettingItem(R.drawable.profession_awb_7000, StringUtil.getStr(R.string.fpv_set_auto), 0, false)
            , new IconAdapter.SettingItem(R.drawable.profession_awb_6000, StringUtil.getStr(R.string.fpv_setting_awb_cloudy), 7000, false)
            , new IconAdapter.SettingItem(R.drawable.profession_awb_5200, StringUtil.getStr(R.string.fpv_setting_awb_fine), 5700, false)
            , new IconAdapter.SettingItem(R.drawable.profession_awb_4000, StringUtil.getStr(R.string.fpv_setting_awb_fluorescent), 4500, false)
            , new IconAdapter.SettingItem(R.drawable.profession_awb_3200, StringUtil.getStr(R.string.fpv_setting_awb_incandescent), 2600, false)
    ));

    public static final List<ParamMap> isoValues = new ArrayList<>();
    public static final List<ParamMap> isoValuesLapse = new ArrayList<>();
    public static final ArrayList<ParamMap> shutValues = new ArrayList<>();
    public static final ArrayList<ParamMap> evValues = new ArrayList<>();
    public static final List values[] = {isoValues, isoValuesLapse, shutValues, evValues};
    /**
     * 白平衡值 2000k-10000k 步进100
     */
    public static final ArrayList<ParamMap> awbValues = new ArrayList<>();
    public static List<ParamMap> awbValuesReve = new ArrayList<>();

    static {
        //iso值的映射
        for (int i = 3200; i >= 100; ) {
            isoValues.add(new ParamMap(i, i + ""));
            isoValuesLapse.add(new ParamMap(i, i + ""));
            i /= 2;
        }
        isoValuesLapse.add(new ParamMap(0, StringUtil.getStr(R.string.fpv_set_auto)));
        isoValues.add(new ParamMap(0, StringUtil.getStr(R.string.fpv_set_auto)));

        //ev值的映射
        int den = -9;
        for (int i = 0; i < 19; ) {
            float v = den++ / 3f;

            float tmp = (int) v - v;
            if (tmp > 0.6f && tmp < 0.7f)
                if (v < 0)
                    v = (int) v - 0.7f;
                else
                    v = (int) v + 0.7f;
            evValues.add(new ParamMap(i++, String.format(Locale.ENGLISH, (v < 0) ? "%+.1f" : "%.1f", v), true));
        }
        Collections.reverse(evValues);

        //快门速度的映射
//        shutValues.add(new ParamMap(0, "自动"));
        shutValues.add(new ParamMap(125, "1/8000"));
        shutValues.add(new ParamMap(156, "1/6400"));
        shutValues.add(new ParamMap(200, "1/5000"));
        shutValues.add(new ParamMap(250, "1/4000"));
        shutValues.add(new ParamMap(312, "1/3200"));
        shutValues.add(new ParamMap(400, "1/2500"));
        shutValues.add(new ParamMap(500, "1/2000"));
        shutValues.add(new ParamMap(625, "1/1600"));
        shutValues.add(new ParamMap(800, "1/1250"));
        shutValues.add(new ParamMap(1000, "1/1000"));
        shutValues.add(new ParamMap(1250, "1/800"));
        shutValues.add(new ParamMap(1562, "1/640"));
        shutValues.add(new ParamMap(2000, "1/500"));
        shutValues.add(new ParamMap(2500, "1/400"));
        shutValues.add(new ParamMap(3125, "1/320"));
        shutValues.add(new ParamMap(4166, "1/240"));
        shutValues.add(new ParamMap(5000, "1/200"));
        shutValues.add(new ParamMap(6250, "1/160"));
        shutValues.add(new ParamMap(8333, "1/120"));
        shutValues.add(new ParamMap(10000, "1/100"));
        shutValues.add(new ParamMap(12500, "1/80"));
        shutValues.add(new ParamMap(16666, "1/60"));
        shutValues.add(new ParamMap(33333, "1/30"));
        shutValues.add(new ParamMap(40000, "1/25"));
        shutValues.add(new ParamMap(50000, "1/20"));
        shutValues.add(new ParamMap(66666, "1/15"));
        shutValues.add(new ParamMap(80000, "1/12.5"));
        shutValues.add(new ParamMap(100000, "1/10"));
        shutValues.add(new ParamMap(125000, "1/8"));
        shutValues.add(new ParamMap(160000, "1/6.25"));
        shutValues.add(new ParamMap(200000, "1/5"));
        shutValues.add(new ParamMap(250000, "1/4"));
        shutValues.add(new ParamMap(333333, "1/3"));
        shutValues.add(new ParamMap(400000, "1/2.5"));
        shutValues.add(new ParamMap(500000, "1/2"));
        shutValues.add(new ParamMap(598802, "1/1.67"));
        shutValues.add(new ParamMap(800000, "1/1.25"));
        shutValues.add(new ParamMap(1000000, "1/1"));

        //白平衡
        awbValuesReve.add(new ParamMap(0, StringUtil.getStr(R.string.fpv_set_auto), ScreenOrientationType.LANDSCAPE));
        for (int i = 10000; i >= 2000; i -= 100) {
            awbValues.add(new ParamMap(i, i + "K"));
            int j = 12000 - i;
            awbValuesReve.add(new ParamMap(j, j + "K", ScreenOrientationType.LANDSCAPE));
        }
        awbValues.add(new ParamMap(0, StringUtil.getStr(R.string.fpv_set_auto)));
    }

    public static List<ParamMap> getIsoValues(int mode) {
        if (mode == 6)  //LAPSE_VIDEO
            return isoValuesLapse;
        return isoValues;
    }

    public static List<ParamMap> getShutValues(int value) {
        List<ParamMap> shuts = null;
        switch (ShotModeManager.getInstance().getmShootMode()) {
            case PHOTO:
                shuts = (List<ParamMap>) shutValues.clone();
                shuts.add(new ParamMap(0, StringUtil.getStr(R.string.fpv_set_auto)));
                break;
            case LAPSE_VIDEO:
                shuts = new ArrayList<>();
                int map = 1000000 / 30 + 1;
                for (ParamMap paramMap : shutValues)
                    if (paramMap.map <= map)
                        shuts.add(paramMap);

                shuts.add(new ParamMap(0, StringUtil.getStr(R.string.fpv_set_auto)));
                break;
            case SLOW_MOTION:
                shuts = new ArrayList<>();
                int map1 = 1000000 / 30 + 1;
                switch (value) {
                    case 4:
                        map1 = 8333;
                        break;
                    case 8:
                        map1 = 4166;
                        break;
                    default:
                        map1 = 16666;
                        break;
                }

                for (ParamMap paramMap : shutValues)
                    if (paramMap.map <= map1)
                        shuts.add(paramMap);

                shuts.add(new ParamMap(0, StringUtil.getStr(R.string.fpv_set_auto)));
                break;
            case VIDEO:
                shuts = new ArrayList<>();
                int map0 = 1000000 / 60 + 1;
                for (ParamMap paramMap : shutValues)
                    if (paramMap.map <= map0)
                        shuts.add(paramMap);
                if (value == 1) {
                    shuts.add(new ParamMap(33333, "1/30"));
                    shuts.add(new ParamMap(40000, "1/25"));
                } else if (value == 2)
                    shuts.add(new ParamMap(33333, "1/30"));
                else if (value == 3)
                    shuts.add(new ParamMap(20000, "1/50"));
                shuts.add(new ParamMap(0, StringUtil.getStr(R.string.fpv_set_auto)));
                break;
            default:
                shuts = new ArrayList<>();
                int mapd = 1000000 / FRAME_RATE[value] + 1;
                for (ParamMap paramMap : shutValues)
                    if (paramMap.map <= mapd)
                        shuts.add(paramMap);
                shuts.add(new ParamMap(0, StringUtil.getStr(R.string.fpv_set_auto)));
                break;
        }

        return shuts;
    }

    public static class ParamMap implements Serializable {
        public int map;
        public String value;
        public boolean selected;
        public boolean hideImg; //是否隐藏下方的小柱子
        public ScreenOrientationType rotation = ScreenOrientationType.PORTRAIT;
        public int extra;

        public ParamMap(int map, String value) {
            this(map, value, false);
        }

        public ParamMap(int map, String value, ScreenOrientationType rotation) {
            this(map, value, false);
            this.rotation = rotation;
        }

        public ParamMap(int map, String value, boolean hideImg) {
            this.map = map;
            this.value = value;
            this.hideImg = hideImg;
        }

        @Override
        public String toString() {
            return "ParamMap{" +
                    "map=" + map +
                    ", value='" + value + '\'' +
                    ", extra='" + extra + '\'' +
                    ", selected='" + selected + '\'' +
                    '}';
        }
    }
}
