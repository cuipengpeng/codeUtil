package com.test.xcamera.utils;

/**
 * Created by zhouxuecheng on
 * Create Time 2020/5/12
 * e-mail zhouxuecheng1991@163.com
 * secondMarkPath
 */

public class WaterMarkJson {
    public static String getFristMarkPath() {
        return "{" + "\"list\"" + ":" +
                "[" +
                "{" + "\"path\"" + ":" + "\"" + Constants.markPngAndVideo + "mark/dynamic_watermark_00000.png\"" + "}," +
                "{" + "\"path\"" + ":" + "\"" + Constants.markPngAndVideo + "mark/dynamic_watermark_00001.png\"" + "}," +
                "{" + "\"path\"" + ":" + "\"" + Constants.markPngAndVideo + "mark/dynamic_watermark_00002.png\"" + "}," +
                "{" + "\"path\"" + ":" + "\"" + Constants.markPngAndVideo + "mark/dynamic_watermark_00003.png\"" + "}," +
                "{" + "\"path\"" + ":" + "\"" + Constants.markPngAndVideo + "mark/dynamic_watermark_00004.png\"" + "}"
                + "]}";
    }

    public static String getSecondMarkPath() {
        return "{" + "\"list\"" + ":" +
                "[" +
                "{" + "\"path\"" + ":" + "\"" + Constants.markPngAndVideo + "mark/dynamic_watermark_00005.png\"" + "}," +
                "{" + "\"path\"" + ":" + "\"" + Constants.markPngAndVideo + "mark/dynamic_watermark_00006.png\"" + "}," +
                "{" + "\"path\"" + ":" + "\"" + Constants.markPngAndVideo + "mark/dynamic_watermark_00007.png\"" + "}," +
                "{" + "\"path\"" + ":" + "\"" + Constants.markPngAndVideo + "mark/dynamic_watermark_00008.png\"" + "}," +
                "{" + "\"path\"" + ":" + "\"" + Constants.markPngAndVideo + "mark/dynamic_watermark_00009.png\"" + "}"
                + "]}";
    }
}
