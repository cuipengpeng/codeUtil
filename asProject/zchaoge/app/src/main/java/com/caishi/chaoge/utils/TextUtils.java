package com.caishi.chaoge.utils;

import java.util.ArrayList;

public class TextUtils {
    public static ArrayList<String> textPartition(ArrayList<String> textList) {
        ArrayList<String> stringList = new ArrayList<>();
        for (int i = 0; i < textList.size(); i++) {
            String text = textList.get(i);
            text = text.replace(",", "");
            text = text.replace(".", "");
            text = text.replace("，", "");
            text = text.replace("。", "");
            text = text.replace("\\s", "");
            text = text.replace("\n", "");
            if (text.length() <= 9) {
                stringList.add(text);
            } else {
                while (text.length() > 9) {
//                    Random random = new Random();
//                    int num = random.nextInt(4) + 5;
//                    LogUtil.i("随机数====num====== " + num);
                    String substring = text.substring(0, 9);
                    stringList.add(substring);
                    text = text.substring(9, text.length());
                }
                stringList.add(text);
            }
        }
        return stringList;
    }

}
