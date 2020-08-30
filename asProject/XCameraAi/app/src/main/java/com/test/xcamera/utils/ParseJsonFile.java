package com.test.xcamera.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import com.google.gson.Gson;
/**
 * Created by admin on 2018/11/28.
 */
public class ParseJsonFile {
    private static final String TAG = "ParseJsonFile";

    /**
     * Json转Java对象
     */
    public static <T> T fromJson(String json, Class<T> clz) {
        return new Gson().fromJson(json, clz);
    }

    public static <T> T fromJson(String json, Type typeOfT) {
        return new Gson().fromJson(json, typeOfT);
    }

    public static ArrayList<FxJsonFileInfo.JsonFileInfo> readBundleFxJsonFile(Context context, String jsonFilePath) {
        String retsult = readAssetJsonFile(context, jsonFilePath);
        if (TextUtils.isEmpty(retsult)) {
            return null;
        }
        FxJsonFileInfo resultInfo = fromJson(retsult, FxJsonFileInfo.class);
        ArrayList<FxJsonFileInfo.JsonFileInfo> infoLists = resultInfo.getFxInfoList();
        return infoLists;
    }

    public static String readAssetJsonFile(Context context, String jsonFilePath) {
        if (context == null) {
            return null;
        }
        if (TextUtils.isEmpty(jsonFilePath)) {
            return null;
        }
        BufferedReader bufferedReader = null;
        StringBuilder retsult = new StringBuilder();
        try {
            InputStream inputStream = context.getAssets().open(jsonFilePath);
            if (inputStream == null)
                return null;
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String infoStrLine;
            while ((infoStrLine = bufferedReader.readLine()) != null) {
                retsult.append(infoStrLine);
            }
        } catch (Exception e) {
            Log.e(TAG, "fail to read json" + jsonFilePath, e);
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (Exception e) {
                Log.e(TAG, "fail to close bufferedReader", e);
            }
        }
        return retsult.toString();
    }

    public static String readSDJsonFile(Context context, String jsonFilePath) {
        if (context == null) {
            return null;
        }
        if (TextUtils.isEmpty(jsonFilePath)) {
            return null;
        }
        if(!new File(jsonFilePath).exists()){
            return null;
        }
        BufferedReader bufferedReader = null;
        StringBuilder retsult = new StringBuilder();
        try {
            FileInputStream inputStream = new FileInputStream(jsonFilePath);
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String infoStrLine;
            while ((infoStrLine = bufferedReader.readLine()) != null) {
                retsult.append(infoStrLine);
            }
        } catch (Exception e) {
            Log.e(TAG, "fail to read json" + jsonFilePath, e);
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (Exception e) {
                Log.e(TAG, "fail to close bufferedReader", e);
            }
        }
        return retsult.toString();
    }

    public static class FxJsonFileInfo implements Serializable{

        public ArrayList<JsonFileInfo> getFxInfoList() {
            return fxInfoList;
        }

        private ArrayList<JsonFileInfo> fxInfoList;

        public static class JsonFileInfo implements Serializable{

            public String getName_Zh() {
                return name_Zh;
            }

            private String name_Zh;//素材中文名字

            public String getName() {
                return name;
            }

            private String name;//素材英文名字

            public String getFxPackageId() {
                return fxPackageId;
            }

            private String fxPackageId;//素材包Id

            public String getFxFileName() {
                return fxFileName;
            }

            private String fxFileName;//素材特效包文件名

            public String getFxLicFileName() {
                return fxLicFileName;
            }

            private String fxLicFileName;//素材特效包授权文件名

            public String getImageName() {
                return imageName;
            }

            private String imageName;//素材封面

            public String getFitRatio() {
                return fitRatio;
            }

            private String fitRatio;//适配比例，参考NvAsset的定义

            public JsonFileInfo(){

            }
            public JsonFileInfo(String imgPath, int imgRes, String name, String id){
                this.name=name;
                this.name_Zh=name;
                this.fxPackageId=id;
                this.imageName=imgPath+".png";
            }
        }
    }
}
