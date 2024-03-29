/*
 *
 * ShaderUtils.java
 * 
 * Created by Wuwang on 2016/10/8
 */
package com.pepe.aplayer.opengl;

import android.content.Context;
import android.content.res.Resources;
import android.opengl.GLES20;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Description:
 */
public class ShaderUtils {

    private static final String TAG="ShaderUtils";

    private ShaderUtils(){
    }

    public static void checkGLError(String op){
        Log.e("wuwang",op);
    }

    public static int createProgramFromAssetsFile(Resources res, String vertexAssetsFileName, String fragmentAssetsFileName){
        int vertex= createShader(GLES20.GL_VERTEX_SHADER, getStringFromgetAssetsFile(vertexAssetsFileName,res));
        if(vertex==0)return 0;
        int fragment= createShader(GLES20.GL_FRAGMENT_SHADER, getStringFromgetAssetsFile(fragmentAssetsFileName,res));
        if(fragment==0)return 0;
        int program= GLES20.glCreateProgram();
        if(program!=0){
            GLES20.glAttachShader(program,vertex);
            checkGLError("Attach Vertex Shader");
            GLES20.glAttachShader(program,fragment);
            checkGLError("Attach Fragment Shader");
            GLES20.glLinkProgram(program);
            int[] linkStatus=new int[1];
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS,linkStatus,0);
            if(linkStatus[0]!= GLES20.GL_TRUE){
                Log.e(TAG,"Could not link program:"+ GLES20.glGetProgramInfoLog(program));
                GLES20.glDeleteProgram(program);
                program=0;
            }
        }
        return program;
    }

    //创建GL程序
    public static int createProgramFromString(String vertexSource, String fragmentSource){
        int vertex=createShader(GLES20.GL_VERTEX_SHADER,vertexSource);
        if(vertex==0)return 0;
        int fragment=createShader(GLES20.GL_FRAGMENT_SHADER,fragmentSource);
        if(fragment==0)return 0;
        int program= GLES20.glCreateProgram();
        if(program!=0){
            GLES20.glAttachShader(program,vertex);
            GLES20.glAttachShader(program,fragment);
            GLES20.glLinkProgram(program);
            int[] linkStatus=new int[1];
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS,linkStatus,0);
            if(linkStatus[0]!= GLES20.GL_TRUE){
                Log.e(TAG,"Could not link program:"+ GLES20.glGetProgramInfoLog(program));
                GLES20.glDeleteProgram(program);
                program=0;
            }
        }
        return program;
    }

    private static String getStringFromgetAssetsFile(String fname, Resources res){
        StringBuilder result=new StringBuilder();
        try{
            InputStream is=res.getAssets().open(fname);
            int ch;
            byte[] buffer=new byte[1024];
            while (-1!=(ch=is.read(buffer))){
                result.append(new String(buffer,0,ch));
            }
        }catch (Exception e){
            return null;
        }
        return result.toString().replaceAll("\\r\\n","\n");
    }

    private static int createShader(int shaderType, String source){
        int shader= GLES20.glCreateShader(shaderType);
        if(0!=shader){
            GLES20.glShaderSource(shader,source);
            GLES20.glCompileShader(shader);
            int[] compiled=new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS,compiled,0);
            if(compiled[0]==0){
                Log.e(TAG,"Could not compile shader:"+shaderType);
                Log.e(TAG,"GLES20 Error:"+ GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                shader=0;
            }
        }
        return shader;
    }

    public static String getStringFromRaw(Context context, int resId) {
        StringBuilder sb = new StringBuilder();
        InputStream inputStream = context.getResources().openRawResource(resId);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
