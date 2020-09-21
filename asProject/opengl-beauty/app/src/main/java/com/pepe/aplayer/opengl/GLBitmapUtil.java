package com.pepe.aplayer.opengl;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class GLBitmapUtil {

    public static void saveFrame(int width, int height) {
        long startTime = System.currentTimeMillis();
        String filename = Environment.getExternalStorageDirectory().getAbsolutePath() + "/fbo_"+startTime+".png";
        try {
            new File(filename).createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //1.glReadPixels返回的是大端的RGBA Byte组数，我们使用小端Buffer接收得到ABGR Byte组数
        ByteBuffer buffer = ByteBuffer.allocateDirect(width * height * 4).order(ByteOrder.LITTLE_ENDIAN);
        GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, buffer);
        buffer.rewind();//reset position
        int pixelCount = width * height;
        int[] colors = new int[pixelCount];
        buffer.asIntBuffer().get(colors);
        Log.d("###############", "colors.length=:" +colors.length);
//        for (int i=0; i<colors.length;i++) {
//            int c = colors[i];   //2.每个int类型的c是接收到的ABGR，但bitmap需要ARGB格式，所以需要交换B和R的位置
////            colors[i] = (c & 0xff000000) | (c & 0x00ff0000 >> 16) |(c & 0x0000ff00) | (c & 0x000000ff << 16); //交换B和R，得到ARGB
//            colors[i] = (c >>> 8) | (c << (32 - 8));
//        }

        //上下翻转
        for (int y=0; y<height/2; y++) {
            for (int x=0; x<width; x++) {
                int temp = colors[(height - y - 1) * width + x];
                colors[(height - y - 1) * width + x] = colors[y * width + x];
                colors[y * width + x] = temp;
            }
        }

        //写入文件
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filename);
            Bitmap bmp = Bitmap.createBitmap(colors, width, height, Bitmap.Config.ARGB_8888);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, fos);
            bmp.recycle();
        } catch (Exception e0) {
        } finally {
            try {
                fos.close();
            } catch (Exception e) {
            }
        }
        long endTime = System.currentTimeMillis();
        Log.d("###############", "Saved duration:" + (endTime - startTime) + "ms -> " + width + "x" + height + " frame as '" + filename + "'");
    }
}
