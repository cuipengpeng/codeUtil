package com.test.xcamera.phonealbum.widget.subtitle.bean;


import java.math.BigDecimal;

/**
 * Created by zhaoliangtai on 2018/1/5.
 */

public class ArithUtil {
    private static final int DEF_DIV_SCALE = 10;

    private ArithUtil() {
    }

    public static float add(float d1, float d2) {
        BigDecimal b1 = new BigDecimal(Float.toString(d1));
        BigDecimal b2 = new BigDecimal(Float.toString(d2));
        return b1.add(b2).floatValue();
    }




    public static float sub(float f1, float f2, float f3) {
        BigDecimal b1 = new BigDecimal(Float.toString(f1));
        BigDecimal b2 = new BigDecimal(Float.toString(f2));
        BigDecimal b3 = new BigDecimal(Float.toString(f3));
        return b1.subtract(b2).subtract(b3).floatValue();
    }

    public static float mul(float d1, float d2) {
        BigDecimal b1 = new BigDecimal(Float.toString(d1));
        BigDecimal b2 = new BigDecimal(Float.toString(d2));
        return b1.multiply(b2).floatValue();

    }

    public static float div(float d1, float d2) {

        return div(d1, d2, DEF_DIV_SCALE);

    }

    public static float div(float d1, float d2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Float.toString(d1));
        BigDecimal b2 = new BigDecimal(Float.toString(d2));
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    public static float add(float f1, float f2, float f3) {
//        BigDecimal b1 = new BigDecimal(Float.toString(f1));
//        BigDecimal maskview_cover = new BigDecimal(Float.toString(f2));
//        BigDecimal b3 = new BigDecimal(Float.toString(f3));
//        return b1.add(maskview_cover).add(b3).floatValue();
        float s1 = (float) Math.pow(10, getDecimalPlaces(f1));
        float s2 = (float) Math.pow(10, getDecimalPlaces(f2));
        float s3 = (float) Math.pow(10, getDecimalPlaces(f2));
        float scale = (s1 > s2 ? s1 : s2) > s3 ? (s1 > s2 ? s1 : s2) : s3;
        return (scale * f1 + scale * f2 + scale * f3) / scale;
    }

    public static float sub(float f1, float f2) {
        float s1 = (float) Math.pow(10, getDecimalPlaces(f1));
        float s2 = (float) Math.pow(10, getDecimalPlaces(f2));
        float scale = s1 > s2 ? s1 : s2;
        return (scale * f1 - scale * f2) / scale;
    }

    private static int getDecimalPlaces(float f) {
        String s = String.valueOf(f);
        return s.length() - s.indexOf(".") - 1;
    }

}
