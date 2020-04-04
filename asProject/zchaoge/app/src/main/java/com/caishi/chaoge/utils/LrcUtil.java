package com.caishi.chaoge.utils;

import android.content.Context;

import com.caishi.chaoge.bean.LrcBean;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.caishi.chaoge.utils.ConstantUtils.SINGLE_TEXT_DURATION;

public class LrcUtil {

    /**
     * 传入的参数为标准歌词字符串
     *
     * @param lrcStr
     * @return
     */
    public static List<LrcBean> parseStr2List(String lrcStr, long endTime) {
        List<LrcBean> list = new ArrayList<>();
        String lrcText = lrcStr.replaceAll("&#58;", ":")
                .replaceAll("&#10;", "\n")
                .replaceAll("&#46;", ".")
                .replaceAll("&#32;", " ")
                .replaceAll("&#45;", "-")
                .replaceAll("&#13;", "\r").replaceAll("&#39;", "'");
        String[] split = lrcText.split("\n");
        for (int i = 0; i < split.length; i++) {
            String lrc = split[i];
            if (lrc.contains(".")) {
                String min = lrc.substring(lrc.indexOf("[") + 1, lrc.indexOf("[") + 3);
                String seconds = lrc.substring(lrc.indexOf(":") + 1, lrc.indexOf(":") + 3);
                String mills = lrc.substring(lrc.indexOf(".") + 1, lrc.indexOf(".") + 3);
                long startTime = Long.valueOf(min) * 60 * 1000 + Long.valueOf(seconds) * 1000 + Long.valueOf(mills) * 10;
                String text = lrc.substring(lrc.indexOf("]") + 1);
                if (text == null || "".equals(text)) {
                    text = "";
                }
                LrcBean lrcBean = new LrcBean();
                lrcBean.setStart(startTime);
                lrcBean.setLrc(text);

                list.add(lrcBean);
                if (list.size() > 1) {
                    list.get(list.size() - 2).setEnd(startTime);
                }
                if (i == split.length - 1) {
                    list.get(list.size() - 1).setEnd(endTime);
                }

                lrcBean.setTime((int) (lrcBean.getEnd() - lrcBean.getStart()));
            }
        }
        return list;
    }

    /**
     * 传入的参数为标准歌词字符串
     *
     * @param lrcStr
     * @return
     */
    public static ArrayList<LrcBean> parseStr2ArrayList(String lrcStr, long endTime) {
        ArrayList<LrcBean> list = new ArrayList<>();
        String lrcText = lrcStr.replaceAll("&#58;", ":")
                .replaceAll("&#10;", "\n")
                .replaceAll("&#46;", ".")
                .replaceAll("&#32;", " ")
                .replaceAll("&#45;", "-")
                .replaceAll("&#13;", "\r").replaceAll("&#39;", "'");
        String[] split = lrcText.split("\n");
        for (int i = 0; i < split.length; i++) {
            String lrc = split[i];
            if (lrc.contains(".")) {
                String min = lrc.substring(lrc.indexOf("[") + 1, lrc.indexOf("[") + 3);
                String seconds = lrc.substring(lrc.indexOf(":") + 1, lrc.indexOf(":") + 3);
                String mills = lrc.substring(lrc.indexOf(".") + 1, lrc.indexOf(".") + 3);
                long startTime = Long.valueOf(min) * 60 * 1000 + Long.valueOf(seconds) * 1000 + Long.valueOf(mills) * 10;
                String text = lrc.substring(lrc.indexOf("]") + 1);
                if (text == null || "".equals(text)) {
                    text = "";
                }
                LrcBean lrcBean = new LrcBean();
                lrcBean.setStart(startTime);
                lrcBean.setLrc(text);

                list.add(lrcBean);
                if (list.size() > 1) {
                    list.get(list.size() - 2).setEnd(startTime);
                }
                if (i == split.length - 1) {
                    list.get(list.size() - 1).setEnd(endTime);
                }

                lrcBean.setTime((int) (lrcBean.getEnd() - lrcBean.getStart()));
            }
        }
        return list;
    }

    public static List<LrcBean> parseStr2List1(String lrcStr, long endTime) {
        List<LrcBean> list = new ArrayList<>();
        String lrcText = lrcStr.replaceAll("&#58;", ":")
                .replaceAll("&#10;", "\n")
                .replaceAll("&#46;", ".")
                .replaceAll("&#32;", " ")
                .replaceAll("&#45;", "-")
                .replaceAll("&#13;", "\r").replaceAll("&#39;", "'");
        String[] split = lrcText.split("_");
        for (int i = 0; i < split.length; i++) {
            String lrc = split[i];
            if (lrc.contains(".")) {
                String min = lrc.substring(lrc.indexOf("[") + 1, lrc.indexOf("[") + 3);
                String seconds = lrc.substring(lrc.indexOf(":") + 1, lrc.indexOf(":") + 3);
                String mills = lrc.substring(lrc.indexOf(".") + 1, lrc.indexOf(".") + 3);
                long startTime = Long.valueOf(min) * 60 * 1000 + Long.valueOf(seconds) * 1000 + Long.valueOf(mills) * 10;
                String text = lrc.substring(lrc.indexOf("]") + 1);
                if (text == null || "".equals(text)) {
                    text = "";
                }
                LrcBean lrcBean = new LrcBean();
                lrcBean.setStart(startTime * 1000);
                lrcBean.setLrc(text);

                list.add(lrcBean);
                if (list.size() > 1) {
                    list.get(list.size() - 2).setEnd(startTime * 1000);
                }
                if (i == split.length - 1) {
                    list.get(list.size() - 1).setEnd(endTime);
                }

                lrcBean.setTime((int) (lrcBean.getEnd() - lrcBean.getStart()));
            }
        }
        return list;
    }

    public static ArrayList<LrcBean> strLrc2LrcBean(List<String> lrcStrList) {
        ArrayList<LrcBean> lrcList = new ArrayList<>();
        for (int i = 0; i < lrcStrList.size(); i++) {
            LrcBean lrcBean = new LrcBean();
            lrcBean.setLrc(lrcStrList.get(i));
            if (i == 0) {
                lrcBean.setStart(0);
                lrcBean.setEnd((lrcStrList.get(i).length() * SINGLE_TEXT_DURATION));
            } else {
                lrcBean.setStart(lrcList.get(i - 1).getEnd());
                lrcBean.setEnd((lrcStrList.get(i).length() * SINGLE_TEXT_DURATION + lrcList.get(i - 1).getEnd()));
            }
            lrcList.add(lrcBean);
        }
        return lrcList;
    }


    public static String getLrcText(Context context, String fileName) {
        String lrcText = null;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            lrcText = new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lrcText;
    }

}
