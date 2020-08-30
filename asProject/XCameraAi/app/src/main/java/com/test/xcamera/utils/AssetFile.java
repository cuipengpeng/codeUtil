package com.test.xcamera.utils;

import android.content.Context;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by smz on 2019/11/21.
 */

public class AssetFile {


    private String assetFileName;
    private Context context;

    public AssetFile(Context context){
        this.context = context;
    }

    public AssetFile fromAsset(String assetFileName){
        this.assetFileName = assetFileName;
        return this;
    }

    /**
     * @param
     */
    public void toSdcard(String filePath,String filechildren){
        try {
            InputStream inStream = context.getResources().getAssets().open(filechildren);
            OutputStream outStream = new FileOutputStream(filePath);
            byte[] buffer = new byte[1024*20];
            int length = inStream.read(buffer);
            while(length !=-1){
                outStream.write(buffer, 0, length);
                length=inStream.read(buffer);
            }
            outStream.flush();
            inStream.close();
            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
