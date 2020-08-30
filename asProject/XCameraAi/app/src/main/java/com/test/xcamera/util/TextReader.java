package com.test.xcamera.util;

import android.support.annotation.NonNull;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TextReader {

    @NonNull
    public static String getFileContent(InputStream inputStream) {
        StringBuilder stringBuilder = new StringBuilder();
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        if(inputStream !=null){
            try {
                inputStreamReader = new InputStreamReader(inputStream);
                 bufferedReader = new BufferedReader(inputStreamReader);
                String jsonLine;
                while ((jsonLine = bufferedReader.readLine()) != null) {
                    stringBuilder.append(jsonLine);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                try {
                    if(bufferedReader!=null){
                        bufferedReader.close();
                        bufferedReader = null;
                    }
                    if(inputStreamReader!=null){
                        inputStreamReader.close();
                        inputStreamReader = null;
                    }
                    if(inputStream!=null){
                        inputStream.close();
                        inputStream  = null;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return stringBuilder.toString();
    }
}
