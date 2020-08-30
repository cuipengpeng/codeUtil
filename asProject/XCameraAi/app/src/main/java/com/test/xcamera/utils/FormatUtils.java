package com.test.xcamera.utils;

import java.text.DecimalFormat;

public class FormatUtils {

    public static String percentageToOther(float per){
        if(per==50){
            return "0.0";
        }else if(per<50){
           float valuer= 1-per/49f;
            DecimalFormat df = new DecimalFormat("#.0");
           return "-"+ String.format("%.1f",valuer);
        }else if(per>50){
            float valuer= (per-50)/49f;
            DecimalFormat df = new DecimalFormat("#.0");

            return "+"+String.format("%.1f",valuer);
        }
        return "0.0";
    }
}
